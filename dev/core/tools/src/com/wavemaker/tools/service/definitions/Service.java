/*
 *  Copyright (C) 2009-2011 WaveMaker Software, Inc.
 *
 *  This file is part of the WaveMaker Server Runtime.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.4-10/02/2007 10:39 AM(ffu)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.23 at 10:17:33 AM PDT 
//


package com.wavemaker.tools.service.definitions;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="operation" type="{http://www.activegrid.com/namespaces/ServiceDefinitions/1.0}Operation" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="dataobjects" type="{http://www.activegrid.com/namespaces/ServiceDefinitions/1.0}dataobjects" minOccurs="0"/>
 *           &lt;element name="eventnotifier" type="{http://www.activegrid.com/namespaces/ServiceDefinitions/1.0}EventNotifier" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="class" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="CRUDService" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="springFile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "operation",
    "dataobjects",
    "eventnotifier"
})
@XmlRootElement(name = "service")
public class Service {

    protected List<Operation> operation;
    protected DataObjects dataobjects;
    protected List<EventNotifier> eventnotifier;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(name = "class", required = true)
    protected String clazz;
    @XmlAttribute(name = "CRUDService")
    protected Boolean crudService;
    @XmlAttribute(required = true)
    protected String type;
    @XmlAttribute
    protected String springFile;

    /**
     * Gets the value of the operation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the operation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Operation }
     * 
     * 
     */
    public List<Operation> getOperation() {
        if (operation == null) {
            operation = new ArrayList<Operation>();
        }
        return this.operation;
    }

    public List<Operation> addOperation(Operation operation) { //xxx
        if (this.operation == null) {
            this.operation = new ArrayList<Operation>();
        }
        this.operation.add(operation);

        return this.operation;
    }

    /**
     * Gets the value of the dataobjects property.
     * 
     * @return
     *     possible object is
     *     {@link DataObjects }
     *     
     */
    public DataObjects getDataobjects() {
        return dataobjects;
    }

    /**
     * Sets the value of the dataobjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataObjects }
     *     
     */
    public void setDataobjects(DataObjects value) {
        this.dataobjects = value;
    }

    /**
     * Gets the value of the eventnotifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventnotifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventnotifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventNotifier }
     * 
     * 
     */
    public List<EventNotifier> getEventnotifier() {
        if (eventnotifier == null) {
            eventnotifier = new ArrayList<EventNotifier>();
        }
        return this.eventnotifier;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the crudService property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getCRUDService() {
        return crudService;
    }

    /**
     * Sets the value of the crudService property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCRUDService(Boolean value) {
        this.crudService = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the springFile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpringFile() {
        return springFile;
    }

    /**
     * Sets the value of the springFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpringFile(String value) {
        this.springFile = value;
    }

}
