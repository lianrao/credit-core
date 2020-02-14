/**
 * QueryValidatorServices.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.wanda.credit.ds.client.guoztCredit;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.wanda.credit.ds.client.guozt.ObjectFactory;

@XmlSeeAlso({ObjectFactory.class})
public interface QueryValidatorServices extends java.rmi.Remote {
    public java.lang.String querySingle(java.lang.String userName_, java.lang.String password_, java.lang.String type_, java.lang.String param_) throws java.rmi.RemoteException;
    public java.lang.String queryBatch(java.lang.String userName_, java.lang.String password_, java.lang.String type_, java.lang.String param_) throws java.rmi.RemoteException;
}
