package com.wanda.credit.ds.client.policeAuthV2.localApi;

import com.wanda.credit.ds.client.policeAuthV2.localApi.signLocal.ISignClient;

import cn.com.jit.new_vstk.Bean.AsymmEncResult;
import cn.com.jit.new_vstk.Bean.AsymmdecResult;
import cn.com.jit.new_vstk.Bean.AttachVerifyResult;
import cn.com.jit.new_vstk.Bean.CertParams;
import cn.com.jit.new_vstk.Bean.SignResult;
import cn.com.jit.new_vstk.Bean.SymmKeyParams;
import cn.com.jit.new_vstk.Bean.VerifyResult;
import cn.com.jit.new_vstk.exception.NewCSSException;
public abstract interface IAdvanceSignClient extends ISignClient {
	public abstract SignResult p1Sign(String paramString, byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract SignResult p1Sign(String paramString1, String paramString2)
		    throws NewCSSException;

		  public abstract SignResult p7AttachSign(String paramString, byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract SignResult p7AttachSign(String paramString1, String paramString2, String paramString3)
		    throws NewCSSException;

		  public abstract VerifyResult p1Verify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, CertParams paramCertParams)
		    throws NewCSSException;

		  public abstract VerifyResult p1Verify(byte[] paramArrayOfByte, String paramString, CertParams paramCertParams)
		    throws NewCSSException;

		  public abstract AttachVerifyResult p7AttachVerify(byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract AttachVerifyResult p7AttachVerify(String paramString1, String paramString2)
		    throws NewCSSException;

		  public abstract byte[] digest(byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract boolean verifyDigest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
		    throws NewCSSException;

		  public abstract byte[] symmEncrypt(SymmKeyParams paramSymmKeyParams, byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract void symmEncrypt(SymmKeyParams paramSymmKeyParams, String paramString1, String paramString2)
		    throws NewCSSException;

		  public abstract byte[] symmDecrypt(SymmKeyParams paramSymmKeyParams, byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract void symmDecrypt(SymmKeyParams paramSymmKeyParams, String paramString1, String paramString2)
		    throws NewCSSException;

		  public abstract SignResult grantSign(byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract AsymmEncResult asymmEncrypt(CertParams paramCertParams, byte[] paramArrayOfByte)
		    throws NewCSSException;

		  public abstract AsymmdecResult asymmDecrypt(CertParams paramCertParams, byte[] paramArrayOfByte)
		    throws NewCSSException;
}
