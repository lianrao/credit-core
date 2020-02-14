package com.wanda.credit.ds.client.policeAuthV2.localApi.signLocal;
import cn.com.jit.new_vstk.Bean.CertParams;
import cn.com.jit.new_vstk.Bean.DecryptResult;
import cn.com.jit.new_vstk.Bean.EnvelopResult;
import cn.com.jit.new_vstk.Bean.SignDecryptResult;
import cn.com.jit.new_vstk.Bean.SignEnvelopResult;
import cn.com.jit.new_vstk.Bean.SignResult;
import cn.com.jit.new_vstk.Bean.VerifyResult;
import cn.com.jit.new_vstk.exception.NewCSSException;
import java.io.Serializable;
public abstract interface ISignClient extends Serializable {
	 public abstract SignResult sign(String paramString, byte[] paramArrayOfByte,String trade_id,String signUrl)
			    throws NewCSSException;

			  public abstract SignResult sign(String paramString1, String paramString2)
			    throws NewCSSException;

			  public abstract VerifyResult verify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
			    throws NewCSSException;

			  public abstract VerifyResult verify(byte[] paramArrayOfByte, String paramString)
			    throws NewCSSException;

			  public abstract EnvelopResult encryptEnvelop(CertParams[] paramArrayOfCertParams, byte[] paramArrayOfByte,String trade_id,String signUrl)
			    throws NewCSSException;

			  public abstract EnvelopResult encryptEnvelop(CertParams[] paramArrayOfCertParams, String paramString1, String paramString2)
			    throws NewCSSException;

			  public abstract DecryptResult decryptEnvelop(byte[] paramArrayOfByte)
			    throws NewCSSException;

			  public abstract DecryptResult decryptEnvelop(String paramString1, String paramString2)
			    throws NewCSSException;

			  public abstract SignEnvelopResult encryptSignEnvelop(CertParams[] paramArrayOfCertParams, String paramString, byte[] paramArrayOfByte)
			    throws NewCSSException;

			  public abstract SignEnvelopResult encryptSignEnvelop(CertParams[] paramArrayOfCertParams, String paramString1, String paramString2)
			    throws NewCSSException;

			  public abstract SignEnvelopResult encryptSignEnvelop(CertParams[] paramArrayOfCertParams, String paramString1, String paramString2, String paramString3)
			    throws NewCSSException;

			  public abstract SignDecryptResult decryptSignEnvelop(byte[] paramArrayOfByte)
			    throws NewCSSException;

			  public abstract SignDecryptResult decryptSignEnvelop(String paramString1, String paramString2)
			    throws NewCSSException;

			  public abstract void setBase64(boolean paramBoolean1, boolean paramBoolean2);
}
