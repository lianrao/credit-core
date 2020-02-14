/**
 * 
 */
package com.wanda.credit.ds.client.qianhai;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 我们信任任何SERVER 证书
 * 
 * @author 
 * 
 */
public class TrustAnyTrustManager implements X509TrustManager
{

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[] {};
    }

}