
package com.wavemaker.tools.deployment.cloudfoundry;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudService;
import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.archive.ZipApplicationArchive;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.wavemaker.common.WMRuntimeException;
import com.wavemaker.runtime.data.util.DataServiceConstants;
import com.wavemaker.tools.data.BaseDataModelSetup;
import com.wavemaker.tools.data.DataModelConfiguration;
import com.wavemaker.tools.data.DataModelManager;
import com.wavemaker.tools.deployment.AppInfo;
import com.wavemaker.tools.deployment.ArchiveType;
import com.wavemaker.tools.deployment.DeploymentDB;
import com.wavemaker.tools.deployment.DeploymentInfo;
import com.wavemaker.tools.deployment.DeploymentTarget;
import com.wavemaker.tools.deployment.cloudfoundry.LoggingStatusCallback.Timer;
import com.wavemaker.tools.project.Project;

public class VmcDeploymentTarget implements DeploymentTarget {

    public static final String SUCCESS_RESULT = "SUCCESS";

    public static final String TOKEN_EXPIRED_RESULT = "ERROR: CloudFoundry login token expired";

    public static final String VMC_USERNAME_PROPERTY = "username";

    public static final String VMC_PASSWORD_PROPERTY = "password";

    public static final String VMC_URL_PROPERTY = "url";

    public static final Map<String, String> CONFIGURABLE_PROPERTIES;

    private static final String DEFAULT_URL = "https://api.cloudfoundry.com";

    private static final String HREF_TEMPLATE = "<a href=\"url\" target=\"_blank\">url</a>";

    private static final String SERVICE_TYPE = "database";

    private static final String MYSQL_SERVICE_VENDOR = "mysql";

    private static final String MYSQL_SERVICE_VERSION = "5.1";

    private static final String POSTGRES_SERVICE_VENDOR = "postgresql";

    private static final String POSTGRES_SERVICE_VERSION = "9.0";

    private static final String SERVICE_TIER = "free";

    private static final Log log = LogFactory.getLog(VmcDeploymentTarget.class);

    private DataModelManager dataModelManager;

    private WebAppAssembler webAppAssembler;

    static {
        Map<String, String> props = new LinkedHashMap<String, String>();
        props.put(VMC_USERNAME_PROPERTY, "username@mydomain.com");
        props.put(VMC_PASSWORD_PROPERTY, "password");
        props.put(VMC_URL_PROPERTY, DEFAULT_URL);
        CONFIGURABLE_PROPERTIES = Collections.unmodifiableMap(props);
    }

    public void setDataModelManager(DataModelManager dataModelManager) {
        this.dataModelManager = dataModelManager;
    }

    public void setWebAppAssembler(WebAppAssembler webAppAssembler) {
        this.webAppAssembler = webAppAssembler;
    }

    @Override
    public String validateDeployment(DeploymentInfo deploymentInfo) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        CloudFoundryClient client = getClient(deploymentInfo);
        try {
            uploadProject(client, deploymentInfo);
            return "SUCCESS";
        } catch (StatusException e) {
            return e.getMessage();
        }
    }

    @Override
    public String deploy(File webapp, DeploymentInfo deploymentInfo) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        try {
            validateWar(webapp);
            ZipFile zipFile = new ZipFile(webapp);
            ApplicationArchive applicationArchive = new ZipApplicationArchive(zipFile);
            return doDeploy(applicationArchive, deploymentInfo);
        } catch (IOException e) {
            throw new WMRuntimeException(e);
        }
    }

    @Override
    public String deploy(Project project, DeploymentInfo deploymentInfo) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        ApplicationArchive applicationArchive = this.webAppAssembler.assemble(project);
        return doDeploy(applicationArchive, deploymentInfo);
    }

    private String doDeploy(ApplicationArchive applicationArchive, DeploymentInfo deploymentInfo) {
        CloudFoundryClient client = getClient(deploymentInfo);
        try {
            uploadProject(client, deploymentInfo);
            setupServices(client, deploymentInfo);
            Timer timer = new Timer();
            try {
                client.uploadApplication(deploymentInfo.getApplicationName(), applicationArchive, new LoggingStatusCallback(timer));
            } catch (IOException ex) {
                throw new WMRuntimeException("Error ocurred while trying to upload WAR file.", ex);
            }

            log.info("Application upload completed in " + timer.stop() + "ms");

            try {
                CloudApplication application = client.getApplication(deploymentInfo.getApplicationName());
                if (application.getState().equals(CloudApplication.AppState.STARTED)) {
                    doRestart(deploymentInfo, client);
                } else {
                    doStart(deploymentInfo, client);
                }
            } catch (CloudFoundryException ex) {
                return "ERROR: Could not start application. " + ex.getDescription();
            }
            return SUCCESS_RESULT;
        } catch (StatusException e) {
            return e.getMessage();
        }
    }

    private void uploadProject(CloudFoundryClient client, DeploymentInfo deploymentInfo) throws StatusException {
        List<String> uris = getUris(deploymentInfo);
        try {
            client.createApplication(deploymentInfo.getApplicationName(), CloudApplication.SPRING,
                client.getDefaultApplicationMemory(CloudApplication.SPRING), uris, null, true);
        } catch (CloudFoundryException e) {
            if (HttpStatus.FORBIDDEN == e.getStatusCode()) {
                throw new StatusException(TOKEN_EXPIRED_RESULT, e);
            } else if (HttpStatus.BAD_REQUEST == e.getStatusCode()) {
                throw new StatusException("ERROR: " + e.getDescription(), e);
            } else {
                throw e;
            }
        }
    }

    private List<String> getUris(DeploymentInfo deploymentInfo) {
        List<String> uris = new ArrayList<String>();
        String url = deploymentInfo.getTarget();
        if (!StringUtils.hasText(url)) {
            url = DEFAULT_URL;
        }
        uris.add(url.replace("api", deploymentInfo.getApplicationName()));
        return uris;
    }

    /**
     * @param deploymentInfo
     */
    private void setupServices(CloudFoundryClient client, DeploymentInfo deploymentInfo) {
        if (CollectionUtils.isEmpty(deploymentInfo.getDatabases())) {
            return;
        }

        CloudApplication app = client.getApplication(deploymentInfo.getApplicationName());

        for (DeploymentDB db : deploymentInfo.getDatabases()) {
            if (app.getServices().contains(db.getDbName())) {
                // service binding already exists
                continue;
            }

            String dbType = "NONE";
            DataModelConfiguration config = this.dataModelManager.getDataModel(db.getDataModelId());
            String url = config.readConnectionProperties().getProperty(DataServiceConstants.DB_URL_KEY, "");
            if (StringUtils.hasText(url)) {
                dbType = BaseDataModelSetup.getDBTypeFromURL(url);
            }
            boolean serviceToBind = false;
            try {
                CloudService service = client.getService(db.getDbName());
                if (dbType.equals(MYSQL_SERVICE_VENDOR)) {
                    Assert.state(MYSQL_SERVICE_VENDOR.equals(service.getVendor()),
                        "There is already a service provisioned with the name '" + db.getDbName() + "' but it is not a MySQL service.");
                } else if (dbType.equals(POSTGRES_SERVICE_VENDOR)) {
                    Assert.state(POSTGRES_SERVICE_VENDOR.equals(service.getVendor()),
                        "There is already a service provisioned with the name '" + db.getDbName() + "' but it is not a PostgreSQL service.");
                }
                serviceToBind = true;
            } catch (CloudFoundryException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                    throw ex;
                }

                if (dbType.equals(MYSQL_SERVICE_VENDOR)) {
                    client.createService(createMySqlService(db));
                    serviceToBind = true;
                } else if (dbType.equals(POSTGRES_SERVICE_VENDOR)) {
                    client.createService(createPostgresqlService(db));
                    serviceToBind = true;
                }
            }
            if (serviceToBind) {
                client.bindService(deploymentInfo.getApplicationName(), db.getDbName());
            }
        }
    }

    /**
     * @param db
     * @return
     */
    private CloudService createMySqlService(DeploymentDB db) {
        CloudService mysql = new CloudService();
        mysql.setType(SERVICE_TYPE);
        mysql.setVendor(MYSQL_SERVICE_VENDOR);
        mysql.setTier(SERVICE_TIER);
        mysql.setVersion(MYSQL_SERVICE_VERSION);
        mysql.setName(db.getDbName());
        return mysql;
    }

    /**
     * @param db
     * @return
     */
    public static CloudService createPostgresqlService(DeploymentDB db) {
        CloudService postgresql = new CloudService();
        postgresql.setType(SERVICE_TYPE);
        postgresql.setVendor(POSTGRES_SERVICE_VENDOR);
        postgresql.setTier(SERVICE_TIER);
        postgresql.setVersion(POSTGRES_SERVICE_VERSION);
        postgresql.setName(db.getDbName());
        return postgresql;
    }

    @Override
    public String undeploy(DeploymentInfo deploymentInfo, boolean deleteServices) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        CloudFoundryClient client = getClient(deploymentInfo);
        log.info("Deleting application " + deploymentInfo.getApplicationName());
        Timer timer = new Timer();
        timer.start();
        try {
            if (deleteServices) {
                CloudApplication app = client.getApplication(deploymentInfo.getApplicationName());
                for (String service : app.getServices()) {
                    client.deleteService(service);
                }
            }
            client.deleteApplication(deploymentInfo.getApplicationName());
            log.info("Application " + deploymentInfo.getApplicationName() + " deleted successfully in " + timer.stop() + "ms");
        } catch (CloudFoundryException ex) {
            if (HttpStatus.FORBIDDEN == ex.getStatusCode()) {
                return TOKEN_EXPIRED_RESULT;
            } else {
                throw ex;
            }
        }
        return SUCCESS_RESULT;
    }

    @Override
    public String redeploy(DeploymentInfo deploymentInfo) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        CloudFoundryClient client = getClient(deploymentInfo);
        doRestart(deploymentInfo, client);
        return SUCCESS_RESULT;
    }

    @Override
    public String start(DeploymentInfo deploymentInfo) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        CloudFoundryClient client = getClient(deploymentInfo);
        doStart(deploymentInfo, client);
        return SUCCESS_RESULT;
    }

    @Override
    public String stop(DeploymentInfo deploymentInfo) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        CloudFoundryClient client = getClient(deploymentInfo);
        log.info("Stopping application " + deploymentInfo.getApplicationName());
        Timer timer = new Timer();
        timer.start();
        client.stopApplication(deploymentInfo.getApplicationName());
        log.info("Application " + deploymentInfo.getApplicationName() + " stopped successfully in " + timer.stop() + "ms");
        return SUCCESS_RESULT;
    }

    @Override
    public List<AppInfo> listDeploymentNames(DeploymentInfo deploymentInfo) {
        deploymentInfo = hackSetupDeploymentInfo(deploymentInfo);
        CloudFoundryClient client = getClient(deploymentInfo);
        List<AppInfo> infoList = new ArrayList<AppInfo>();
        List<CloudApplication> cloudApps = client.getApplications();
        for (CloudApplication app : cloudApps) {
            String href = HREF_TEMPLATE.replaceAll("url", "http://" + app.getUris().get(0));
            infoList.add(new AppInfo(app.getName(), href, app.getState().toString()));
        }
        return infoList;
    }

    @Override
    public Map<String, String> getConfigurableProperties() {
        return CONFIGURABLE_PROPERTIES;
    }

    private void doRestart(DeploymentInfo deploymentInfo, CloudFoundryClient client) {
        log.info("Restarting application " + deploymentInfo.getApplicationName());
        Timer timer = new Timer();
        timer.start();
        client.restartApplication(deploymentInfo.getApplicationName());
        log.info("Application " + deploymentInfo.getApplicationName() + " restarted successfully in " + timer.stop() + "ms");
    }

    private void doStart(DeploymentInfo deploymentInfo, CloudFoundryClient client) {
        log.info("Starting application " + deploymentInfo.getApplicationName());
        Timer timer = new Timer();
        timer.start();
        client.startApplication(deploymentInfo.getApplicationName());
        log.info("Application " + deploymentInfo.getApplicationName() + " started successfully in " + timer.stop() + "ms");
    }

    private CloudFoundryClient getClient(DeploymentInfo deploymentInfo) {
        if (true) {
            return hackGetClient(deploymentInfo);
        }
        Assert.hasText(deploymentInfo.getToken(), "CloudFoundry login token not supplied.");
        String url = deploymentInfo.getTarget();
        if (!StringUtils.hasText(url)) {
            url = DEFAULT_URL;
        }
        try {
            CloudFoundryClient client = new CloudFoundryClient(deploymentInfo.getToken(), url);
            return client;
        } catch (MalformedURLException e) {
            throw new WMRuntimeException("CloudFoundry target URL is invalid", e);
        }
    }

    private DeploymentInfo hackSetupDeploymentInfo(DeploymentInfo deploymentInfo) {
        // FIXME PW HACK
        if (deploymentInfo == null) {
            deploymentInfo = new DeploymentInfo();
            deploymentInfo.setApplicationName("project1");
            deploymentInfo.setArchiveType(ArchiveType.WAR);
            deploymentInfo.setTarget("http://deployedproject.pwebb.cloudfoundry.me");
        }
        return deploymentInfo;
    }

    private CloudFoundryClient hackGetClient(DeploymentInfo deploymentInfo) {
        // FIXME PW HACK
        try {
            CloudFoundryClient client = new CloudFoundryClient("username", "password", "http://api.xxxx.cloudfoundry.me");
            client.login();
            return client;
        } catch (MalformedURLException e) {
            throw new WMRuntimeException("CloudFoundry target URL is invalid", e);
        }
    }

    private void validateWar(File war) {
        Assert.notNull(war, "war cannot be null");
        Assert.isTrue(war.exists(), "war does not exist");
        Assert.isTrue(!war.isDirectory(), "war cannot be a directory");
    }

    private static class StatusException extends Exception {

        private static final long serialVersionUID = 1L;

        public StatusException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}