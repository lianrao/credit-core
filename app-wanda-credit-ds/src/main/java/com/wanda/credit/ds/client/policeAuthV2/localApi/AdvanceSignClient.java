package com.wanda.credit.ds.client.policeAuthV2.localApi;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.wanda.credit.common.template.iface.IPropertyEngine;
import com.wanda.credit.ds.client.policeAuthV2.localApi.signLocal.SignClient;

import cn.com.jit.assp.dsign.DSign;
import cn.com.jit.new_vstk.ConvertJson;
import cn.com.jit.new_vstk.IAdvanceSignClient;
import cn.com.jit.new_vstk.Bean.AsymmEncResult;
import cn.com.jit.new_vstk.Bean.AsymmdecResult;
import cn.com.jit.new_vstk.Bean.AttachVerifyResult;
import cn.com.jit.new_vstk.Bean.BodyContent;
import cn.com.jit.new_vstk.Bean.CertParams;
import cn.com.jit.new_vstk.Bean.CertResult;
import cn.com.jit.new_vstk.Bean.EnvelopResult;
import cn.com.jit.new_vstk.Bean.ExpandInfo;
import cn.com.jit.new_vstk.Bean.HttpRespResult;
import cn.com.jit.new_vstk.Bean.OnceSignResult;
import cn.com.jit.new_vstk.Bean.ParseTsaResult;
import cn.com.jit.new_vstk.Bean.SignResult;
import cn.com.jit.new_vstk.Bean.SymmKeyParams;
import cn.com.jit.new_vstk.Bean.TsaResult;
import cn.com.jit.new_vstk.Bean.VerifyResult;
import cn.com.jit.new_vstk.Bean.VerifyTsaResult;
import cn.com.jit.new_vstk.config.NewConfig;
import cn.com.jit.new_vstk.exception.NewCSSException;
import cn.com.jit.new_vstk.utils.Base64;
import cn.com.jit.new_vstk.utils.Base64Util;
import cn.com.jit.new_vstk.utils.FileUtil;
import cn.com.jit.new_vstk.utils.ParamValidater;
import cn.com.jit.new_vstk.utils.VstkConstants;
import jar.org.apache.commons.lang.StringUtils;


public class AdvanceSignClient extends SignClient implements IAdvanceSignClient{
	private static final long serialVersionUID = 8736517933822954205L;
	@Autowired
	public IPropertyEngine propertyEngine;
	  public AdvanceSignClient()
	    throws NewCSSException{
	  }

	  public AdvanceSignClient(NewConfig config)
	  {
	    super(config);
	  }

	  protected AdvanceSignClient(Properties properties)
	    throws NewCSSException
	  {
	    super(properties);
	  }

	  public AdvanceSignClient(String path) throws NewCSSException {
	    super(path);
	  }

	  public SignResult p1Sign(String certId, byte[] plain)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validatePlain(plain);
	      Map<String,String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_SIGN_P1);

	      BodyContent bc = this.bodyContentFormer.formP1Sign(certId, plain, "");
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);
	      SignResult signData = this.responseFormer.formSignResult(result);
	      methodRunTime(startRunTime, "p1Sign");
	      return signData;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignResult p1Sign(String certId, String plainPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validatePlainPath(plainPath);
	      Map<String,String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_SIGN_P1);

	      BodyContent bc = this.bodyContentFormer.formP1SignByPath(certId, plainPath);

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);
	      SignResult signData = this.responseFormer.formSignResult(result);
	      methodRunTime(startRunTime, "p1Sign file");
	      return signData;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignResult p7AttachSign(String certId, byte[] plain)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validatePlain(plain);
	      Map<String,String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_SIGN_ATTACH);

	      BodyContent bc = this.bodyContentFormer.formP7Sign(certId, plain);
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);

	      Map<String,String> headers = result.getRespHeader();
	      String doTsa = (String)headers.get(VstkConstants.dosigntsa);
	      if ((doTsa != null) && ("yes".equals(doTsa))) {
	        byte[] digestData = result.getRespBody();

	        String digest = new String(Base64.encode(digestData));
	        httpHeader.put(VstkConstants.dosigntsa, "yes");
	        httpHeader.put("digestdata", digest);
	        result = this.sender.sendwithRetry(httpHeader, new BodyContent(plain, this.config.getSendSize()),signUrl);
	      }

	      SignResult signData = this.responseFormer.formSignResult(result);
	      methodRunTime(startRunTime, "attachSign");
	      return signData;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignResult p7AttachSign(String certId, String plainPath, String signPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validatePlainPath(plainPath);
	      Map<String,String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_SIGN_ATTACH);

	      BodyContent bc = this.bodyContentFormer.formP7AttachSign(plainPath, signPath);

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);
	      Map<String,String> headers = result.getRespHeader();
	      String doTsa = (String)headers.get(VstkConstants.dosigntsa);
	      if ((doTsa != null) && ("yes".equals(doTsa))) {
	        byte[] digestData = FileUtil.readByte(signPath);

	        String digest = new String(Base64.encode(digestData));
	        httpHeader.put(VstkConstants.dosigntsa, "yes");
	        httpHeader.put("digestdata", digest);
	        result = this.sender.sendwithRetry(httpHeader, new BodyContent(plainPath, signPath, this.config.getSendSize()),signUrl);
	      }

	      SignResult signData = this.responseFormer.formSignResult(result);
	      methodRunTime(startRunTime, "attachSign file");
	      return signData;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public VerifyResult p1Verify(byte[] signData, byte[] plain, CertParams certParams) throws NewCSSException
	  {
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateCertParams(certParams);
	      ParamValidater.validatePlain(plain);
	      ParamValidater.validateResult(signData);
	      BodyContent bodyContent = this.bodyContentFormer.formP1VerifyByPlain(certParams, signData, plain, "");

	      Map<String,String> httpHeader = this.requestFormer.formP1Verify(bodyContent, certParams, VstkConstants.BUSINESS_TYPE_VERIFY_P1);

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);

	      VerifyResult form = this.responseFormer.formVerifyResult(result);
	      methodRunTime(startRunTime, "p1Verify");
	      return form;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public VerifyResult p1Verify(byte[] signData, String plainPath, CertParams certParams) throws NewCSSException
	  {
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateCertParams(certParams);
	      ParamValidater.validatePlainPath(plainPath);
	      ParamValidater.validateResult(signData);

	      BodyContent bodyContent = this.bodyContentFormer.formP1VerifyByPlainPath(plainPath, signData);

	      Map<String,String> httpHeader = this.requestFormer.formP1Verify(bodyContent, certParams, VstkConstants.BUSINESS_TYPE_VERIFY_P1);

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);

	      VerifyResult form = this.responseFormer.formVerifyResult(result);
	      methodRunTime(startRunTime, "p1Verify file");
	      return form;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public AttachVerifyResult p7AttachVerify(byte[] signData)
	    throws NewCSSException
	  {
	    try
	    {
	      long startRunTime = getTime();
	      ParamValidater.validateResult(signData);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      BodyContent bodyContent = this.bodyContentFormer.formP7AttachVerify(signData);

	      Map<String,String> httpHeader = this.requestFormer.formP7Verify(bodyContent, VstkConstants.BUSINESS_TYPE_VERIFY_ATTACH);

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);

	      AttachVerifyResult form = this.responseFormer.formAttachVerifyResult(result);

	      methodRunTime(startRunTime, "attachVerify");
	      return form;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public AttachVerifyResult p7AttachVerify(String signPath, String plainPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validateResultPath(signPath);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      BodyContent bodyContent = this.bodyContentFormer.formP7AttachVerify(signPath, plainPath);

	      Map<String,String> httpHeader = this.requestFormer.formP7Verify(bodyContent, VstkConstants.BUSINESS_TYPE_VERIFY_ATTACH);

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);

	      AttachVerifyResult form = this.responseFormer.formAttachVerifyResult(result);

	      methodRunTime(startRunTime, "attachVerify file");
	      return form;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public byte[] digest(byte[] plain)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validatePlain(plain);
	      byte[] digest = new byte[0];
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      Map<String,String> httpHeader = this.requestFormer.formDigest(VstkConstants.BUSINESS_TYPE_DIGEST);

	      BodyContent bodyContent = this.bodyContentFormer.formDigestData(plain, this.config.getSendSize());

	      if (this.config.getDigestAlg().toUpperCase().equals(VstkConstants.DIGEST_ALG_MD2))
	      {
	        MessageDigest messageDigest = MessageDigest.getInstance(this.config.getDigestAlg().toUpperCase());
	        digest = messageDigest.digest(plain);
	      } else {
	        HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);

	        digest = this.responseFormer.formDigest(result);
	      }
	      methodRunTime(startRunTime, "digest");
	      return digest;
	    }
	    catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public boolean verifyDigest(byte[] plain, byte[] digestData)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validatePlain(plain);
	      ParamValidater.validateResult(digestData);
	      Map<String,String> map = this.requestFormer.formDigest(VstkConstants.BUSINESS_TYPE_DIGEST);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      HttpRespResult result = this.sender.sendwithRetry(map, new BodyContent(plain, this.config.getSendSize()),signUrl);

	      boolean bon = Arrays.equals(result.getRespBody(), digestData);
	      methodRunTime(startRunTime, "verifyDigest");
	      return bon;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public byte[] symmEncrypt(SymmKeyParams symmParams, byte[] plain)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validateSymmKeyParams(symmParams);
	      ParamValidater.validatePlain(plain);
	      Map<String,String> httpHeader = this.requestFormer.formSymm(symmParams, VstkConstants.BUSINESS_TYPE_SYMM_ENCRYPT);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      BodyContent bodyContent = this.bodyContentFormer.formSymmEncrypt(symmParams, plain, this.config.getSendSize());

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);

	      byte[] symmEncrypt = this.responseFormer.formSymmEncrypt(result);
	      methodRunTime(startRunTime, "symmEncrypt");
	      return symmEncrypt;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public void symmEncrypt(SymmKeyParams symmParams, String plainPath, String symmEncryptPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validateSymmKeyParams(symmParams);
	      ParamValidater.validatePlainPath(plainPath);
	      Map<String,String> httpHeader = this.requestFormer.formSymm(symmParams, VstkConstants.BUSINESS_TYPE_SYMM_ENCRYPT);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      this.sender.sendwithRetry(httpHeader, new BodyContent(plainPath, symmEncryptPath, this.config.getSendSize()),signUrl);

	      methodRunTime(startRunTime, "symmEncrypt file");
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public byte[] symmDecrypt(SymmKeyParams symmParams, byte[] encryptedData)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      ParamValidater.validateSymmKeyParams(symmParams);
	      ParamValidater.validateResult(encryptedData);
	      Map<String,String> httpHeader = this.requestFormer.formSymm(symmParams, VstkConstants.BUSINESS_TYPE_SYMM_DECRYPT);

	      BodyContent bodyContent = this.bodyContentFormer.formSymmDecrypt(symmParams, encryptedData, this.config.getSendSize());

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);

	      byte[] symmDecrypt = this.responseFormer.formSymmDecrypt(result);
	      methodRunTime(startRunTime, "symmDecrypt");
	      return symmDecrypt;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public void symmDecrypt(SymmKeyParams symmParams, String symmEncryptFilePath, String plainPath)
	    throws NewCSSException
	  {
	    try
	    {
	      long startRunTime = getTime();
	      ParamValidater.validateSymmKeyParams(symmParams);
	      ParamValidater.validatePlainPath(symmEncryptFilePath);
	      Map<String,String> httpHeader = this.requestFormer.formSymm(symmParams, VstkConstants.BUSINESS_TYPE_SYMM_DECRYPT);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      this.sender.sendwithRetry(httpHeader, new BodyContent(symmEncryptFilePath, plainPath, this.config.getSendSize()),signUrl);

	      methodRunTime(startRunTime, "symmDecrypt file");
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public SignResult grantSign(byte[] signedData)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      String result = null;
	      String certId = null;
	      SignResult rs = new SignResult();
	      DSign ds = new DSign();
	      String path = getConfig().getConfigFile();
	      DSign.init(path);
	      long res = ds.verifyAttachedSign(signedData);
	      if (res == 0L) {
	        String certBase64 = ds.getCertInfo("VS", 7, "");
	        byte[] plainData = ds.getPlainByteData();
	        certId = searchCertIdByCertBase(certBase64.getBytes());
	        if ((certId != null) && (!certId.equals(""))) {
	          DSign ds1 = new DSign();
	          result = ds1.attachSign(certId, plainData);
	          rs.setSignData(result.getBytes());
	        } else {
	          rs.setErrorMsg("没有找到授权证书！");
	          rs.setErrorCode(10700000L);
	        }
	      }
	      methodRunTime(startRunTime, "grantSign");
	      return rs;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e1) {
	      throw new NewCSSException("-10700000", "服务器错误", e1);
	    }
	  }

	  private String searchCertIdByCertBase(byte[] certBase)
	    throws NewCSSException{
	    Map<String,String> httpHeader = this.requestFormer.formSearchCertId(certBase.length, VstkConstants.BUSINESS_TYPE_SEARCH_CERTID);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, new BodyContent(certBase, this.config.getSendSize()),signUrl);

	    return new String(result.getRespBody());
	  }

	  public TsaResult tsaApplyByDigest(String certInfo, byte[] digest)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validatePlain(digest);
	      if (StringUtils.isBlank(certInfo)) {
	        certInfo = "";
	      }
	      Map<String,String> httpHeader = this.requestFormer.formDigestApplyTsa(certInfo, VstkConstants.BUSINESS_TYPE_APPLY_TSA);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, new BodyContent(digest, 2147483640),signUrl);

	      TsaResult tsaResult = this.responseFormer.formApplyTsa(result);
	      methodRunTime(startRunTime, "tsaApplyByDigest");
	      return tsaResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public TsaResult tsaApplyByPlain(String certInfo, byte[] plain)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validatePlain(plain);
	      if (StringUtils.isBlank(certInfo)) {
	        certInfo = "";
	      }
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      Map<String,String> httpHeader = this.requestFormer.formPlainApplyTsa(certInfo, VstkConstants.BUSINESS_TYPE_APPLY_TSA);

	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, new BodyContent(plain, 2147483640),signUrl);

	      TsaResult tsaResult = this.responseFormer.formApplyTsa(result);
	      methodRunTime(startRunTime, "tsaApplyByPlain");
	      return tsaResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public TsaResult tsaApplyByPlain(String certInfo, String plainPath)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validatePlainPath(plainPath);
	      if (StringUtils.isBlank(certInfo)) {
	        certInfo = "";
	      }
	      Map<String,String> httpHeader = this.requestFormer.formPlainApplyTsa(certInfo, VstkConstants.BUSINESS_TYPE_APPLY_TSA);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, new BodyContent(plainPath, 2147483640),signUrl);

	      TsaResult tsaResult = this.responseFormer.formApplyTsa(result);
	      methodRunTime(startRunTime, "tsaApplyByPlain file");
	      return tsaResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public VerifyTsaResult tsaVerify(byte[] tsa)
	    throws NewCSSException{
	    return tsaVerify(null, tsa);
	  }

	  public VerifyTsaResult tsaVerify(String certInfo, byte[] tsa)
	    throws NewCSSException{
	    try
	    {
	      long startRunTime = getTime();
	      ParamValidater.validateResult(tsa);
	      if (StringUtils.isBlank(certInfo)) {
	        certInfo = "";
	      }
	      Map<String,String> httpHeader = this.requestFormer.formVerifyTsa(certInfo, VstkConstants.BUSINESS_TYPE_VERIFY_TSA);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, new BodyContent(tsa, 2147483640),signUrl);

	      VerifyTsaResult verifyTsaResult = this.responseFormer.formVerifyTsa(result);

	      methodRunTime(startRunTime, "tsaVerify");
	      return verifyTsaResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public ParseTsaResult tsaParse(byte[] tsaData, int type)
	    throws NewCSSException{
	    try{
	      long startRunTime = getTime();
	      ParamValidater.validateResult(tsaData);
	      Map<String,String> httpHeader = this.requestFormer.formParseTsa(type, VstkConstants.BUSINESS_TYPE_PARSE_TSA);
	      String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	      HttpRespResult result = this.sender.sendwithRetry(httpHeader, new BodyContent(tsaData, 2147483640),signUrl);

	      ParseTsaResult parseTsaResult = this.responseFormer.formParseTsa(result);
	      methodRunTime(startRunTime, "tsaParse");
	      return parseTsaResult;
	    } catch (NewCSSException e) {
	      throw e;
	    } catch (Exception e) {
	      throw new NewCSSException("-10700000", "服务器错误", e);
	    }
	  }

	  public AsymmEncResult asymmEncrypt(CertParams certParams, byte[] plain)
	    throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validatePlain(plain);
	    Map<String,String> httpHeader = this.requestFormer.formAsymmEncrypt(certParams, VstkConstants.BUSINESS_TYPE_ASYMM_ENCRYPT);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bodyContent = this.bodyContentFormer.formAsymmEncrypt(certParams, plain, this.config.getSendSize());

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    AsymmEncResult signData = this.responseFormer.formasymmEncryptResult(result);
	    methodRunTime(startRunTime, "asymmEncrypt");
	    return signData;
	  }

	  public AsymmEncResult asymmEncrypt(CertParams[] certParams, byte[] plain)
	    throws NewCSSException
	  {
	    long startRunTime = getTime();
	    ParamValidater.validatePlain(plain);
	    Map<String,String> httpHeader = this.requestFormer.formAsymmEncrypt(certParams, VstkConstants.BUSINESS_TYPE_ASYMM_ENCRYPT);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bodyContent = this.bodyContentFormer.formAsymmEncrypt(certParams, plain, this.config.getSendSize());

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    AsymmEncResult signData = this.responseFormer.formasymmEncryptResult(result);
	    methodRunTime(startRunTime, "asymmEncrypt");
	    return signData;
	  }

	  public AsymmdecResult asymmDecrypt(CertParams certParams, byte[] encryptedData)
	    throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validateResult(encryptedData);
	    Map<String,String> httpHeader = this.requestFormer.formAsymmDecrypt(certParams, VstkConstants.BUSINESS_TYPE_ASYMM_DECRYPT);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bodyContent = this.bodyContentFormer.formAsymmDecrypt(certParams, encryptedData, this.config.getSendSize());

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    AsymmdecResult signData = this.responseFormer.formasymmDecryptResult(result);
	    methodRunTime(startRunTime, "asymmDecrypt");
	    return signData;
	  }

	  public SignResult unformatSign(String certId, byte[] plain)
	    throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validatePlain(plain);
	    Map<String,String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_UNFORMAT_SIGN);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bc = this.bodyContentFormer.formP1Sign(certId, plain, null);
	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);
	    SignResult signData = this.responseFormer.formSignResult(result);
	    methodRunTime(startRunTime, "unformatSign");
	    return signData;
	  }

	  public AsymmEncResult unformatAsymmEncrypt(CertParams certParams, byte[] plain) 
			  throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validatePlain(plain);
	    Map<String,String> httpHeader = this.requestFormer.formAsymmEncrypt(certParams, VstkConstants.BUSINESS_TYPE_UNFORMAT_ASYMM_ENCRYPT);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bodyContent = this.bodyContentFormer.formAsymmEncrypt(certParams, plain, this.config.getSendSize());

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    AsymmEncResult signData = this.responseFormer.formasymmEncryptResult(result);
	    methodRunTime(startRunTime, "unformatAsymmEncrypt");
	    return signData;
	  }

	  public VerifyResult unformatVerify(byte[] signData, byte[] plain, CertParams certParams)
	    throws NewCSSException
	  {
	    long startRunTime = getTime();
	    ParamValidater.validateCertParams(certParams);
	    ParamValidater.validatePlain(plain);
	    ParamValidater.validateResult(signData);
	    BodyContent bodyContent = this.bodyContentFormer.formP1VerifyByPlain(certParams, signData, plain, "");
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    Map<String,String> httpHeader = this.requestFormer.formP1Verify(bodyContent, certParams, VstkConstants.BUSINESS_TYPE_UNFORMAT_VERIFY);

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    VerifyResult form = this.responseFormer.formVerifyResult(result);
	    methodRunTime(startRunTime, "unformatVerify");
	    return form;
	  }

	  public AsymmdecResult unformatAsymmDecrypt(CertParams certParams, byte[] encryptedData) throws NewCSSException
	  {
	    long startRunTime = getTime();
	    ParamValidater.validateResult(encryptedData);
	    Map<String,String> httpHeader = this.requestFormer.formAsymmDecrypt(certParams, VstkConstants.BUSINESS_TYPE_UNFORMAT_ASYMM_DECRYPT);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bodyContent = this.bodyContentFormer.formAsymmDecrypt(certParams, encryptedData, this.config.getSendSize());

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    AsymmdecResult signData = this.responseFormer.formasymmDecryptResult(result);
	    methodRunTime(startRunTime, "unformatAsymmDecrypt");
	    return signData;
	  }

	  public CertResult queryCert(CertParams certParams, String certUse, String asymmAlg)
	    throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validateCertParams(certParams);
	    ParamValidater.validateCertUse(certUse);
	    ParamValidater.validateAsymmAlg(asymmAlg);
	    Map<String,String> httpHeader = this.requestFormer.formQueryCert(certParams, certUse, asymmAlg, VstkConstants.BUSINESS_TYPE_CERT_QUERY);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bodyContent = this.bodyContentFormer.queryCert(certParams, certUse, asymmAlg);

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    CertResult certResult = this.responseFormer.formCertResult(result);
	    methodRunTime(startRunTime, "queryCert");
	    return certResult;
	  }

	  public OnceSignResult onceSign(byte[] plain, Map<String, String> userInfo, List<ExpandInfo> list)
	    throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validatePlain(plain);
	    Map<String,String> httpHeader = this.requestFormer.onceSignPackage(userInfo, VstkConstants.ONCE_SIGN);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    for (ExpandInfo expand : list) {
	      expand.setoId(expand.getoId());
	      expand.setoName(Base64Util.getUTF8Base64(expand.getoName()));
	      expand.setValue(Base64Util.getUTF8Base64(expand.getValue()));
	    }
	    httpHeader.put("expand", ConvertJson.list2json(list));

	    BodyContent bc = this.bodyContentFormer.formOnceSign(plain, null);
	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);
	    OnceSignResult signData = this.responseFormer.formOnceSignResult(result);
	    methodRunTime(startRunTime, "onceSign");
	    return signData;
	  }

	  public SignResult p1DigestSign(byte[] hash, String certId)
	    throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validatePlain(hash);
	    Map<String,String> httpHeader = this.requestFormer.formSign(certId, VstkConstants.BUSINESS_TYPE_SIGN_P1_DIGEST);
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    BodyContent bc = this.bodyContentFormer.formP1Sign(certId, hash, "");
	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bc,signUrl);
	    SignResult sr = this.responseFormer.formSignResult(result);
	    methodRunTime(startRunTime, "p1DigestSign");
	    return sr;
	  }

	  public VerifyResult p1DigestVerify(byte[] signedData, byte[] hash, CertParams certParams)
	    throws NewCSSException{
	    long startRunTime = getTime();
	    ParamValidater.validatePlain(hash);
	    ParamValidater.validateResult(signedData);
	    ParamValidater.validateCertParams(certParams);
	    BodyContent bodyContent = this.bodyContentFormer.formP1VerifyByPlain(certParams, signedData, hash, "");
	    String signUrl = propertyEngine.readById("ds_polcieV2_signUrl");
	    Map<String,String> httpHeader = this.requestFormer.formP1Verify(bodyContent, certParams, VstkConstants.BUSINESS_TYPE_VERIFY_P1_DIGEST);

	    HttpRespResult result = this.sender.sendwithRetry(httpHeader, bodyContent,signUrl);
	    VerifyResult form = this.responseFormer.formVerifyResult(result);
	    methodRunTime(startRunTime, "p1digestverifysign");
	    return form;
	  }

	@Override
	public SignResult sign(String arg0, byte[] arg1) throws NewCSSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnvelopResult encryptEnvelop(CertParams[] arg0, byte[] arg1) throws NewCSSException {
		// TODO Auto-generated method stub
		return null;
	}

}
