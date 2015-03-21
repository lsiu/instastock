//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.21 at 12:20:47 PM CDT 
//


package com.mastercard.mcwallet.sdk.xml.commontypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CheckoutSelection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CheckoutSelection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShippingDestinationId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="PaymentCardId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="LoyaltyCardId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="PrecheckoutTransactionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DigitalGoods" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="MerchantCheckoutId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="TimeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ExtensionPoint" type="{}ExtensionPoint" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckoutSelection", propOrder = {
    "shippingDestinationId",
    "paymentCardId",
    "loyaltyCardId",
    "precheckoutTransactionId",
    "digitalGoods",
    "merchantCheckoutId",
    "timeStamp",
    "extensionPoint"
})
public class CheckoutSelection {

    @XmlElement(name = "ShippingDestinationId")
    protected Long shippingDestinationId;
    @XmlElement(name = "PaymentCardId")
    protected Long paymentCardId;
    @XmlElement(name = "LoyaltyCardId")
    protected Long loyaltyCardId;
    @XmlElement(name = "PrecheckoutTransactionId", required = true)
    protected String precheckoutTransactionId;
    @XmlElement(name = "DigitalGoods")
    protected boolean digitalGoods;
    @XmlElement(name = "MerchantCheckoutId", required = true)
    protected String merchantCheckoutId;
    @XmlElement(name = "TimeStamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;
    @XmlElement(name = "ExtensionPoint")
    protected ExtensionPoint extensionPoint;

    /**
     * Gets the value of the shippingDestinationId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getShippingDestinationId() {
        return shippingDestinationId;
    }

    /**
     * Sets the value of the shippingDestinationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setShippingDestinationId(Long value) {
        this.shippingDestinationId = value;
    }

    /**
     * Gets the value of the paymentCardId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPaymentCardId() {
        return paymentCardId;
    }

    /**
     * Sets the value of the paymentCardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPaymentCardId(Long value) {
        this.paymentCardId = value;
    }

    /**
     * Gets the value of the loyaltyCardId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getLoyaltyCardId() {
        return loyaltyCardId;
    }

    /**
     * Sets the value of the loyaltyCardId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setLoyaltyCardId(Long value) {
        this.loyaltyCardId = value;
    }

    /**
     * Gets the value of the precheckoutTransactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrecheckoutTransactionId() {
        return precheckoutTransactionId;
    }

    /**
     * Sets the value of the precheckoutTransactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrecheckoutTransactionId(String value) {
        this.precheckoutTransactionId = value;
    }

    /**
     * Gets the value of the digitalGoods property.
     * 
     */
    public boolean isDigitalGoods() {
        return digitalGoods;
    }

    /**
     * Sets the value of the digitalGoods property.
     * 
     */
    public void setDigitalGoods(boolean value) {
        this.digitalGoods = value;
    }

    /**
     * Gets the value of the merchantCheckoutId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantCheckoutId() {
        return merchantCheckoutId;
    }

    /**
     * Sets the value of the merchantCheckoutId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantCheckoutId(String value) {
        this.merchantCheckoutId = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the extensionPoint property.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionPoint }
     *     
     */
    public ExtensionPoint getExtensionPoint() {
        return extensionPoint;
    }

    /**
     * Sets the value of the extensionPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionPoint }
     *     
     */
    public void setExtensionPoint(ExtensionPoint value) {
        this.extensionPoint = value;
    }

}