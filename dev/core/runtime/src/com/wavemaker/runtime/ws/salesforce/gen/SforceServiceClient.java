/*
 * Copyright (C) 2011 WaveMaker Software, Inc.
 *
 * This file is part of WaveMaker Enterprise.
 *  You may not use this file in any manner except through written agreement with WaveMaker Software, Inc.
 *
 */ 


package com.wavemaker.runtime.ws.salesforce.gen;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * Sforce SOAP API
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.5-b03-
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "SforceService", targetNamespace = "urn:partner.soap.sforce.com", wsdlLocation = "partner.wsdl")
public class SforceServiceClient
    extends Service
{

    private final static URL SFORCESERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.wavemaker.runtime.ws.salesforce.gen.SforceServiceClient.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.wavemaker.runtime.ws.salesforce.gen.SforceServiceClient.class.getResource(".");
            url = new URL(baseUrl, "partner.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'partner.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SFORCESERVICE_WSDL_LOCATION = url;
    }

    public SforceServiceClient(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SforceServiceClient() {
        super(SFORCESERVICE_WSDL_LOCATION, new QName("urn:partner.soap.sforce.com", "SforceService"));
    }

    /**
     * 
     * @return
     *     returns Soap
     */
    @WebEndpoint(name = "Soap")
    public Soap getSoap() {
        return super.getPort(new QName("urn:partner.soap.sforce.com", "Soap"), Soap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Soap
     */
    @WebEndpoint(name = "Soap")
    public Soap getSoap(WebServiceFeature... features) {
        return super.getPort(new QName("urn:partner.soap.sforce.com", "Soap"), Soap.class, features);
    }

}
