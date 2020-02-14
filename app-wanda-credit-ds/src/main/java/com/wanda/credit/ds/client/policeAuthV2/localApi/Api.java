package com.wanda.credit.ds.client.policeAuthV2.localApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.jit.new_vstk.Bean.CertIdParams;
import cn.com.jit.new_vstk.Bean.CertParams;
import cn.com.jit.new_vstk.Bean.DecryptResult;
import cn.com.jit.new_vstk.Bean.EnvelopResult;
import cn.com.jit.new_vstk.Bean.SignResult;
import cn.com.jit.new_vstk.Bean.VerifyResult;
import cn.com.jit.new_vstk.exception.NewCSSException;
import cn.com.jit.new_vstk.utils.Base64;

public class Api {
	private final static Logger logger = LoggerFactory
			.getLogger(Api.class);
	private AdvanceSignClient client;

	  public void initConnection(String configUrl)
	    throws NewCSSException
	  {
	    try
	    {
	      this.client = new AdvanceSignClient(configUrl);
	    } catch (NewCSSException e) {
	      throw e;
	    }
	  }

	  public byte[] p1Sign(byte[] msg, String certID)
	    throws NewCSSException
	  {
	    try
	    {
	      SignResult res = this.client.p1Sign(certID, msg);
	      return this.client.getConfig().getCommunicationProtocol().equals("1") ? Base64.encode(res.getSignData()) : res
	        .getSignData();
	    } catch (NewCSSException e) {
	      String errorMsg = "p1sign failed,errorCode:" + e.getCode() + ". errorMsg:" + e.getDescription();
	      throw new NewCSSException(errorMsg, e);
	    } catch (Exception e) {
	      String errorMsg = "p1sign failed,errorCode:-11000007. errorMsg:服务器未知错误";

	      throw new NewCSSException(errorMsg, e);
	    }
	  }

	  public boolean p1SignVerify(byte[] msg, byte[] sign, String certID)
	    throws NewCSSException{
	    long errorCode = 0L;
	    try {
	      CertParams certParams = new CertIdParams(certID);
	      sign = this.client.getConfig().getCommunicationProtocol().equals("1") ? Base64.decode(sign) : sign;
	      VerifyResult res = this.client.p1Verify(sign, msg, certParams);
	      errorCode = res.getErrorCode();
	      if (errorCode != 0L)
	        throw new NewCSSException(Long.toString(errorCode), "", res.getErrorMsg());
	    }
	    catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      String errorMsg = "p1vsign failed,errorCode:-11000007. errorMsg:服务器未知错误";

	      throw new NewCSSException(errorMsg, e);
	    }

	    return errorCode == 0L;
	  }

	  public byte[] p7Sign(byte[] msg, String certID,String trade_id,String signUrl)
	    throws NewCSSException{
	    try{
	      SignResult res = this.client.sign(certID, msg,trade_id,signUrl);
	      return this.client.getConfig().getCommunicationProtocol().equals("1") ? Base64.encode(res.getSignData()) : res
	        .getSignData();
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      String errorMsg = "p7sign failed,errorCode:-11000007. errorMsg:服务器未知错误";

	      throw new NewCSSException(errorMsg, e);
	    }
	  }

	  public boolean p7SignVerify(byte[] msg, byte[] sign)
	    throws NewCSSException
	  {
	    long errorCode = 0L;
	    try {
	      sign = this.client.getConfig().getCommunicationProtocol().equals("1") ? Base64.decode(sign) : sign;
	      VerifyResult res = this.client.verify(sign, msg);
	      errorCode = res.getErrorCode();
	      if (errorCode != 0L)
	        throw new NewCSSException(Long.toString(errorCode), "", res.getErrorMsg());
	    }
	    catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      String errorMsg = "p7vsign failed,errorCode:-11000007. errorMsg:服务器未知错误";

	      throw new NewCSSException(errorMsg, e);
	    }
	    return errorCode == 0L;
	  }

	  public byte[] encryptEnvelope(byte[] msg, String certID,String trade_id,String signUrl)
	    throws NewCSSException
	  {
	    try
	    {
	      CertParams[] certParams = { new CertIdParams(certID) };
	      EnvelopResult res = this.client.encryptEnvelop(certParams, msg,trade_id,signUrl);
	      return this.client.getConfig().getCommunicationProtocol().equals("1") ? Base64.encode(res.getEnvelopData()) : res
	        .getEnvelopData();
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      String errorMsg = "encryptEnvelopeBytes failed,errorCode:-11000007. errorMsg:服务器未知错误";

	      throw new NewCSSException(errorMsg, e);
	    }
	  }

	  public byte[] decryptEnvelope(byte[] msg)
	    throws NewCSSException
	  {
	    byte[] result = null;
	    try {
	      msg = this.client.getConfig().getCommunicationProtocol().equals("1") ? Base64.decode(msg) : msg;
	      DecryptResult res = this.client.decryptEnvelop(msg);
	      result = res.getPlainData();
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      String errorMsg = "decryptEnvelopeBytes failed,errorCode:-11000007. errorMsg:服务器未知错误";

	      throw new NewCSSException(errorMsg, e);
	    }

	    return result;
	  }
}
