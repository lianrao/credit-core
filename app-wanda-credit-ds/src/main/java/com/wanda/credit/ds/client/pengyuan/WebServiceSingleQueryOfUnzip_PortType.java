/**
 * WebServiceSingleQueryOfUnzip_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.wanda.credit.ds.client.pengyuan;

public interface WebServiceSingleQueryOfUnzip_PortType extends java.rmi.Remote {
    public java.lang.String test() throws java.rmi.RemoteException;
    public java.lang.String testConnection() throws java.rmi.RemoteException;
    public java.lang.String queryReport(java.lang.String userID, java.lang.String password, java.lang.String queryCondition) throws java.rmi.RemoteException;
    public java.lang.String queryReport(java.lang.String userID, java.lang.String password, java.lang.String queryCondition, java.lang.String outputStyle) throws java.rmi.RemoteException;
    public java.lang.String batOfflineDownload(java.lang.String userID, java.lang.String password, java.lang.String batNo) throws java.rmi.RemoteException;
    public java.lang.String batOfflineDownload(java.lang.String userID, java.lang.String password, java.lang.String batNo, java.lang.String outputStyle) throws java.rmi.RemoteException;
    public java.lang.String download(java.lang.String userID, java.lang.String password, java.lang.String batNo) throws java.rmi.RemoteException;
    public java.lang.String download(java.lang.String userID, java.lang.String password, java.lang.String batNo, java.lang.String outputStyle) throws java.rmi.RemoteException;
    public java.lang.String changePassword(java.lang.String userID, java.lang.String oldPasswowd, java.lang.String newPassword) throws java.rmi.RemoteException;
    public java.lang.String telAndNameCheck(java.lang.String userID, java.lang.String password, java.lang.String queryCondition) throws java.rmi.RemoteException;
    public java.lang.String demurralMark(java.lang.String userID, java.lang.String password, java.lang.String queryCondition) throws java.rmi.RemoteException;
    public java.lang.String getMormalCorpNameTest() throws java.rmi.RemoteException;
}
