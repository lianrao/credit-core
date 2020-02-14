package com.wanda.credit.ds.client.policeAuthV2.localApi.signLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.base.util.ExceptionUtil;
import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.client.policeAuthV2.localApi.sender.BaseHttpClient;

import cn.com.jit.new_vstk.Bean.BodyContent;
import cn.com.jit.new_vstk.Bean.CertParams;
import cn.com.jit.new_vstk.Bean.DecryptResult;
import cn.com.jit.new_vstk.Bean.EnvelopResult;
import cn.com.jit.new_vstk.Bean.HttpRespResult;
import cn.com.jit.new_vstk.Bean.SignDecryptResult;
import cn.com.jit.new_vstk.Bean.SignEnvelopResult;
import cn.com.jit.new_vstk.Bean.SignResult;
import cn.com.jit.new_vstk.Bean.VerifyResult;
import cn.com.jit.new_vstk.config.NewConfig;
import cn.com.jit.new_vstk.exception.NewCSSException;
import cn.com.jit.new_vstk.utils.Base64;
import cn.com.jit.new_vstk.utils.ParamValidater;
import cn.com.jit.new_vstk.utils.SystemProperties;
import cn.com.jit.new_vstk.utils.VstkConstants;


public class SignClient extends BaseHttpClient implements ISignClient{
	private final static Logger logger = LoggerFactory
			.getLogger(SignClient.class);
	@Autowired
	public IPropertyEngine propertyEngine;
	 private static final long serialVersionUID = -34595385006662931L;
	  public static final String CLASS_NAME = SignClient.class.getName();

	  public SignClient() throws NewCSSException{
	  }

	  public SignClient(String path) throws NewCSSException {
	    super(path);
	  }

	  public SignClient(NewConfig config) {
	    super(config);
	  }

	  protected SignClient(Properties properties) throws NewCSSException {
	    super(properties);
	  }

	  public SignResult sign(String certId, byte[] plain,String trade_id,String signUrl)throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      logger.info("{} 加签请求拼装开始...",trade_id);
	      ParamValidater.validatePlain(plain);
	      Map<String, String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_SIGN_DETACH);
	      httpHeader.put("url", "/signMessage");
	      BodyContent bc = this.bodyContentFormer.formP7Detach(certId, plain);
	      logger.info("{} 加签http请求开始...",trade_id);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);
	      logger.info("{} 加签http请求结束",trade_id);
	      Map<String, String> headers = result.getRespHeader();
	      String doTsa = (String)headers.get(VstkConstants.dosigntsa);
	      if ((doTsa != null) && ("yes".equals(doTsa))) {
	        byte[] digestData = result.getRespBody();

	        String digest = new String(Base64.encode(digestData));
	        httpHeader.put(VstkConstants.dosigntsa, "yes");
	        httpHeader.put("digestdata", digest);
	        result = this.sender.sendwithRetry(httpHeader, new BodyContent(plain, this.config.getSendSize()),signUrl);
	      }

	      SignResult signResult = this.responseFormer.formSignResult(result);
	      methodRunTime(startRunTime, "detachSign");
	      return signResult;
	    } catch (NewCSSException e) {
	    	logger.info("{} 加签http请求00异常:{}",trade_id,ExceptionUtil.getTrace(e));
	      throw e;
	    } catch (Exception e) {
	    	logger.info("{} 加签http请求01异常:{}",trade_id,ExceptionUtil.getTrace(e));
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignResult sign(String certId, String plainPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validatePlainPath(plainPath);
	      Map<String, String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_SIGN_DETACH);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, new BodyContent(plainPath, this.config.getSendSize()),signUrl);

	      Map<String, String> headers = result.getRespHeader();
	      String doTsa = (String)headers.get(VstkConstants.dosigntsa);
	      if ((doTsa != null) && ("yes".equals(doTsa))) {
	        byte[] digestData = result.getRespBody();

	        String digest = new String(Base64.encode(digestData));
	        httpHeader.put(VstkConstants.dosigntsa, "yes");
	        httpHeader.put("digestdata", digest);
	        result = this.sender.sendwithRetry(httpHeader, new BodyContent(plainPath, this.config.getSendSize()),signUrl);
	      }

	      SignResult signResult = this.responseFormer.formSignResult(result);
	      methodRunTime(startRunTime, "detachSign file");
	      return signResult;
	    }
	    catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public VerifyResult verify(byte[] signData, byte[] plain) throws NewCSSException{
	    try {
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validatePlain(plain);
	      ParamValidater.validateResult(signData);
	      int sendSize = this.config.getSendSize();
	      int signDataLen = signData.length;
	      if (signDataLen > this.config.getSendSize()) {
	        while (signDataLen % 12 != 0) {
	          signDataLen++;
	        }
	        sendSize = signDataLen;
	      }

	      BodyContent bodyContent = this.bodyContentFormer.formP7DetachVerify(plain, signData, sendSize);
	      Map<String, String> httpHeader = this.requestFormer.formP7Verify(bodyContent, VstkConstants.BUSINESS_TYPE_VERIFY_DETACH);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      VerifyResult form = this.responseFormer.formVerifyResult(result);
	      methodRunTime(startRunTime, "detachVerify");
	      return form;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public VerifyResult verify(byte[] signData, String plainPath) throws NewCSSException {
	    try {
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateResult(signData);
	      ParamValidater.validatePlainPath(plainPath);
	      BodyContent bodyContent = new BodyContent(plainPath, signData, this.config.getSendSize());
	      Map<String, String> httpHeader = this.requestFormer.formP7Verify(bodyContent, VstkConstants.BUSINESS_TYPE_VERIFY_DETACH);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      VerifyResult form = this.responseFormer.formVerifyResult(result);
	      methodRunTime(startRunTime, "detachVerify file");
	      return form;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public EnvelopResult encryptEnvelop(CertParams[] certParams, byte[] plain,String trade_id,String signUrl)
	    throws NewCSSException{
	    try
	    {
	      long startRunTime = getTime();
	      ParamValidater.validateCertParamsArr(certParams);
	      ParamValidater.validatePlain(plain);
	      Map<String, String> httpHeader = this.requestFormer.formEncrypt(certParams, VstkConstants.BUSINESS_TYPE_ENVELOP);
	      BodyContent bodyContent = this.bodyContentFormer.formEncryptEnvelop(certParams, plain, this.config.getSendSize());
	      HttpRespResult send = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      EnvelopResult envelopResult = this.responseFormer.formEnvelopResult(send);
	      methodRunTime(startRunTime, "encryptEnvelop");
	      return envelopResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public EnvelopResult encryptEnvelop(CertParams[] certInfos, String plainPath, String envelopPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateCertParamsArr(certInfos);
	      ParamValidater.validatePlainPath(plainPath);
	      Map<String, String> httpHeader = this.requestFormer.formEncrypt(certInfos, VstkConstants.BUSINESS_TYPE_ENVELOP);
	      HttpRespResult send = this.sender.sendwithRetry(httpHeader, new BodyContent(plainPath, envelopPath, this.config.getSendSize()),signUrl);
	      EnvelopResult envelopResult = this.responseFormer.formEnvelopResult(send);
	      methodRunTime(startRunTime, "encryptEnvelop file");
	      return envelopResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public DecryptResult decryptEnvelop(byte[] envelopData)
	    throws NewCSSException{
	    try
	    {
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateResult(envelopData);
	      Map<String, String> httpHeader = this.requestFormer.formDecrypt(VstkConstants.BUSINESS_TYPE_DECRYPT_ENVELOP);
	      BodyContent bodyContent = this.bodyContentFormer.formDecryptEnvelop(envelopData, this.config.getSendSize());
	      HttpRespResult send = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      DecryptResult decryptResult = this.responseFormer.formDecryptResult(send);
	      methodRunTime(startRunTime, "decryptEnvelop");
	      return decryptResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public DecryptResult decryptEnvelop(String envelopPath, String plainPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateResultPath(envelopPath);
	      Map<String, String> httpHeader = this.requestFormer.formDecrypt(VstkConstants.BUSINESS_TYPE_DECRYPT_ENVELOP);
	      HttpRespResult send = this.sender.sendwithRetry(httpHeader, new BodyContent(envelopPath, plainPath, this.config.getSendSize()),signUrl);
	      DecryptResult decryptResult = this.responseFormer.formDecryptResult(send);
	      methodRunTime(startRunTime, "decryptEnvelop file");
	      return decryptResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignEnvelopResult encryptSignEnvelop(CertParams[] certInfos, String signCertId, byte[] plain)
	    throws NewCSSException{
	    try
	    {
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateCertParamsArr(certInfos);
	      ParamValidater.validatePlain(plain);
	      ParamValidater.validateCertId(signCertId);
	      Map<String, String> httpHeader = this.requestFormer.formSignEnvelope(certInfos, signCertId, VstkConstants.BUSINESS_TYPE_SIGN_ENVELOP);
	      BodyContent bodyContent = this.bodyContentFormer.formEncryptSignEnvelop(certInfos, plain, signCertId);
	      HttpRespResult send = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      SignEnvelopResult signEnvelopResult = this.responseFormer.formSignEnvelopeResult(send);
	      methodRunTime(startRunTime, "encryptSignEnvelop");
	      return signEnvelopResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignEnvelopResult encryptSignEnvelop(CertParams[] certInfos, String signCertId, String plainPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateCertParamsArr(certInfos);
	      ParamValidater.validatePlainPath(plainPath);
	      ParamValidater.validateCertId(signCertId);
	      Map<String, String> httpHeader = this.requestFormer.formSignEnvelope(certInfos, signCertId, VstkConstants.BUSINESS_TYPE_SIGN_ENVELOP);
	      BodyContent bodyContent = this.bodyContentFormer.formEncryptSignEnvelopByPlainPath(plainPath);
	      HttpRespResult send = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      SignEnvelopResult signEnvelopResult = this.responseFormer.formSignEnvelopeResult(send);
	      methodRunTime(startRunTime, "encryptSignEnvelop file");
	      return signEnvelopResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignEnvelopResult encryptSignEnvelop(CertParams[] certInfos, String signCertId, String plainPath, String encryptPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateCertParamsArr(certInfos);
	      ParamValidater.validatePlainPath(plainPath);
	      ParamValidater.validateCertId(signCertId);
	      ParamValidater.validateResultPath(encryptPath);
	      Map<String, String> httpHeader = this.requestFormer.formSignEnvelope(certInfos, signCertId, VstkConstants.BUSINESS_TYPE_SIGN_ENVELOP);
	      BodyContent bodyContent = this.bodyContentFormer.formEncryptSignEnvelopByPlainPath(plainPath, encryptPath);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      SignEnvelopResult signEnvelopResult = this.responseFormer.formSignEnvelopeResult(result);
	      methodRunTime(startRunTime, "encryptSignEnvelop file encryptPath");
	      return signEnvelopResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignDecryptResult decryptSignEnvelop(byte[] envelopData)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validateResult(envelopData);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      BodyContent bodyContent = this.bodyContentFormer.formDecryptSignEnvelop(envelopData);
	      Map<String, String> httpHeader = this.requestFormer.formDecryptSignEnvelope(bodyContent, VstkConstants.BUSINESS_TYPE_DECRYPT_SIGN_ENVELOP);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      SignDecryptResult signDecryptResult = this.responseFormer.formDecryptSignEnvelopeResult(result);
	      methodRunTime(startRunTime, "decryptSignEnvelop");
	      return signDecryptResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignDecryptResult decryptSignEnvelop(String envelopDataPath, String plainPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validatePlainPath(envelopDataPath);
	      ParamValidater.validateResultPath(plainPath);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      BodyContent bodyContent = this.bodyContentFormer.formDecryptSignEnvelop(envelopDataPath, plainPath);
	      Map<String, String> httpHeader = this.requestFormer.formDecryptSignEnvelope(bodyContent, VstkConstants.BUSINESS_TYPE_DECRYPT_SIGN_ENVELOP);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	      SignDecryptResult signDecryptResult = this.responseFormer.formDecryptSignEnvelopeResult(result);
	      methodRunTime(startRunTime, "decryptSignEnvelop file");
	      return signDecryptResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public void setBase64(boolean inputSigned, boolean outputSigned)
	  {
	  }

	  public String getServerVersion()throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      Map<String, String> headerMap = new HashMap<String, String>();
	      headerMap.put(VstkConstants.CONNECTION, this.config.getConnectionType());
	      headerMap.put(VstkConstants.VERSION, SystemProperties.vstkversion);
	      headerMap.put(VstkConstants.BUSINESS_TYPE, "server_version");
	      headerMap.put(VstkConstants.MESSAGE_TYPE, "system");
	      HttpRespResult sendwithRetry = this.sender.sendwithRetry(headerMap, new BodyContent(new byte[0], this.config.getSendSize()),signUrl);
	      String version = (String)sendwithRetry.getRespHeader().get("version");
	      methodRunTime(startRunTime, "serverVersion");
	      return version;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }
}
