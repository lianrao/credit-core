/**
 * WebServiceSingleQueryOfUnzipServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.wanda.credit.ds.client.pengyuan;

import com.wanda.credit.common.props.DynamicConfigLoader;

public class WebServiceSingleQueryOfUnzipServiceOldLocator extends org.apache.axis.client.Service implements WebServiceSingleQueryOfUnzipService {

    public WebServiceSingleQueryOfUnzipServiceOldLocator() {
    }


    public WebServiceSingleQueryOfUnzipServiceOldLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WebServiceSingleQueryOfUnzipServiceOldLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    
    // Use to get a proxy class for WebServiceSingleQueryOfUnzip
    private java.lang.String WebServiceSingleQueryOfUnzip_address = DynamicConfigLoader.get("sys.credit.client.pengyuan.old.url");

    public java.lang.String getWebServiceSingleQueryOfUnzipAddress() {
        return WebServiceSingleQueryOfUnzip_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String WebServiceSingleQueryOfUnzipWSDDServiceName = "WebServiceSingleQueryOfUnzip";

    public java.lang.String getWebServiceSingleQueryOfUnzipWSDDServiceName() {
        return WebServiceSingleQueryOfUnzipWSDDServiceName;
    }

    public void setWebServiceSingleQueryOfUnzipWSDDServiceName(java.lang.String name) {
        WebServiceSingleQueryOfUnzipWSDDServiceName = name;
    }

    public WebServiceSingleQueryOfUnzip_PortType getWebServiceSingleQueryOfUnzip() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WebServiceSingleQueryOfUnzip_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWebServiceSingleQueryOfUnzip(endpoint);
    }

    public WebServiceSingleQueryOfUnzip_PortType getWebServiceSingleQueryOfUnzip(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            WebServiceSingleQueryOfUnzipSoapBindingStub _stub = new WebServiceSingleQueryOfUnzipSoapBindingStub(portAddress, this);
            _stub.setPortName(getWebServiceSingleQueryOfUnzipWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWebServiceSingleQueryOfUnzipEndpointAddress(java.lang.String address) {
        WebServiceSingleQueryOfUnzip_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (WebServiceSingleQueryOfUnzip_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                WebServiceSingleQueryOfUnzipSoapBindingStub _stub = new WebServiceSingleQueryOfUnzipSoapBindingStub(new java.net.URL(WebServiceSingleQueryOfUnzip_address), this);
                _stub.setPortName(getWebServiceSingleQueryOfUnzipWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("WebServiceSingleQueryOfUnzip".equals(inputPortName)) {
            return getWebServiceSingleQueryOfUnzip();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.pycredit.com:9001/services/WebServiceSingleQueryOfUnzip", "WebServiceSingleQueryOfUnzipService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.pycredit.com:9001/services/WebServiceSingleQueryOfUnzip", "WebServiceSingleQueryOfUnzip"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("WebServiceSingleQueryOfUnzip".equals(portName)) {
            setWebServiceSingleQueryOfUnzipEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
