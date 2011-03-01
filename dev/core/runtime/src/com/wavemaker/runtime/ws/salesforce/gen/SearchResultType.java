/*
 * Copyright (C) 2011 WaveMaker Software, Inc.
 *
 * This file is part of WaveMaker Enterprise.
 *  You may not use this file in any manner except through written agreement with WaveMaker Software, Inc.
 *
 */ 


package com.wavemaker.runtime.ws.salesforce.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="searchRecords" type="{urn:partner.soap.sforce.com}SearchRecord" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sforceReserved" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResult", namespace = "urn:partner.soap.sforce.com", propOrder = {
    "searchRecords",
    "sforceReserved"
})
public class SearchResultType {

    @XmlElement(namespace = "urn:partner.soap.sforce.com")
    protected List<SearchRecordType> searchRecords;
    @XmlElement(namespace = "urn:partner.soap.sforce.com")
    protected String sforceReserved;

    /**
     * Gets the value of the searchRecords property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchRecords property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchRecords().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchRecordType }
     * 
     * 
     */
    public List<SearchRecordType> getSearchRecords() {
        if (searchRecords == null) {
            searchRecords = new ArrayList<SearchRecordType>();
        }
        return this.searchRecords;
    }

    /**
     * Gets the value of the sforceReserved property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSforceReserved() {
        return sforceReserved;
    }

    /**
     * Sets the value of the sforceReserved property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSforceReserved(String value) {
        this.sforceReserved = value;
    }

    /**
     * Sets the value of the searchRecords property.
     * 
     * @param searchRecords
     *     allowed object is
     *     {@link SearchRecordType }
     *     
     */
    public void setSearchRecords(List<SearchRecordType> searchRecords) {
        this.searchRecords = searchRecords;
    }

}
