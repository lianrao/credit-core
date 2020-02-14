package com.wanda.credit.ds.client.bill99lBankCard.util;


import java.security.cert.CertificateException; 
import java.security.cert.X509Certificate; 

import javax.net.ssl.X509TrustManager; 

public class MyX509TrustManager implements X509TrustManager { 



    public MyX509TrustManager() throws Exception { 
	
    } 

    /* 
     * Delegate to the default trust manager. 
     */ 
    public void checkClientTrusted(X509Certificate[] chain, String authType) 
                throws CertificateException { 

    } 

    /* 
     * Delegate to the default trust manager. 
     */ 
    public void checkServerTrusted(X509Certificate[] chain, String authType) 
                throws CertificateException { 

    } 

    /* 
     * Merely pass this through. 
     */ 
    public X509Certificate[] getAcceptedIssuers() { 	
        return new java.security.cert.X509Certificate[0];	
    } 
}
