// Copyright 2006-2012, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.09.10 at 03:41:38 PM PDT
//


package gov.nasa.pds.harvest.policy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;all>
 *         &lt;element ref="{}registryPackage" minOccurs="0"/>
 *         &lt;element ref="{}collections" minOccurs="0"/>
 *         &lt;element ref="{}directories" minOccurs="0"/>
 *         &lt;element ref="{}pds3Directory" minOccurs="0"/>
 *         &lt;element ref="{}storageIngestion" minOccurs="0"/>
 *         &lt;element ref="{}accessUrls" minOccurs="0"/>
 *         &lt;element ref="{}checksums" minOccurs="0"/>
 *         &lt;element ref="{}candidates"/>
 *         &lt;element ref="{}references" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "policy")
public class Policy {

    protected RegistryPackage registryPackage;
    protected Collection collections;
    protected Directory directories;
    protected Pds3Directory pds3Directory;
    protected StorageIngestion storageIngestion;
    protected AccessUrls accessUrls;
    protected Checksums checksums;
    @XmlElement(required = true)
    protected Candidate candidates;
    protected References references;

    public Policy() {
      registryPackage = new RegistryPackage();
      directories = new Directory();
      pds3Directory = new Pds3Directory();
      collections = new Collection();
      storageIngestion = null;
      accessUrls = new AccessUrls();
      references = new References();
      checksums = new Checksums();
    }

    /**
     * Gets the value of the registryPackage property.
     *
     * @return
     *     possible object is
     *     {@link RegistryPackage }
     *
     */
    public RegistryPackage getRegistryPackage() {
        return registryPackage;
    }

    /**
     * Sets the value of the registryPackage property.
     *
     * @param value
     *     allowed object is
     *     {@link RegistryPackage }
     *
     */
    public void setRegistryPackage(RegistryPackage value) {
        this.registryPackage = value;
    }

    /**
     * Gets the value of the collections property.
     *
     * @return
     *     possible object is
     *     {@link Collection }
     *
     */
    public Collection getCollections() {
        return collections;
    }

    /**
     * Sets the value of the collections property.
     *
     * @param value
     *     allowed object is
     *     {@link Collection }
     *
     */
    public void setCollections(Collection value) {
        this.collections = value;
    }

    /**
     * Gets the value of the directories property.
     *
     * @return
     *     possible object is
     *     {@link Directory }
     *
     */
    public Directory getDirectories() {
        return directories;
    }

    /**
     * Sets the value of the directories property.
     *
     * @param value
     *     allowed object is
     *     {@link Directory }
     *
     */
    public void setDirectories(Directory value) {
        this.directories = value;
    }

    /**
     * Gets the value of the pds3Directory property.
     *
     * @return
     *     possible object is
     *     {@link Pds3Directory }
     *
     */
    public Pds3Directory getPds3Directory() {
        return pds3Directory;
    }

    /**
     * Sets the value of the pds3Directory property.
     *
     * @param value
     *     allowed object is
     *     {@link Pds3Directory }
     *
     */
    public void setPds3Directory(Pds3Directory value) {
        this.pds3Directory = value;
    }

    /**
     * Gets the value of the storageIngestion property.
     *
     * @return
     *     possible object is
     *     {@link StorageIngestion }
     *
     */
    public StorageIngestion getStorageIngestion() {
        return storageIngestion;
    }

    /**
     * Sets the value of the storageIngestion property.
     *
     * @param value
     *     allowed object is
     *     {@link StorageIngestion }
     *
     */
    public void setStorageIngestion(StorageIngestion value) {
        this.storageIngestion = value;
    }

    /**
     * Gets the value of the accessUrls property.
     *
     * @return
     *     possible object is
     *     {@link AccessUrls }
     *
     */
    public AccessUrls getAccessUrls() {
        return accessUrls;
    }

    /**
     * Sets the value of the accessUrls property.
     *
     * @param value
     *     allowed object is
     *     {@link AccessUrls }
     *
     */
    public void setAccessUrls(AccessUrls value) {
        this.accessUrls = value;
    }

    /**
     * Gets the value of the checksums property.
     *
     * @return
     *     possible object is
     *     {@link Checksums }
     *
     */
    public Checksums getChecksums() {
        return checksums;
    }

    /**
     * Sets the value of the checksums property.
     *
     * @param value
     *     allowed object is
     *     {@link Checksums }
     *
     */
    public void setChecksums(Checksums value) {
        this.checksums = value;
    }

    /**
     * Gets the value of the candidates property.
     *
     * @return
     *     possible object is
     *     {@link Candidate }
     *
     */
    public Candidate getCandidates() {
        return candidates;
    }

    /**
     * Sets the value of the candidates property.
     *
     * @param value
     *     allowed object is
     *     {@link Candidate }
     *
     */
    public void setCandidates(Candidate value) {
        this.candidates = value;
    }

    /**
     * Gets the value of the references property.
     *
     * @return
     *     possible object is
     *     {@link References }
     *
     */
    public References getReferences() {
        return references;
    }

    /**
     * Sets the value of the references property.
     *
     * @param value
     *     allowed object is
     *     {@link References }
     *
     */
    public void setReferences(References value) {
        this.references = value;
    }

}
