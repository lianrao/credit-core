package com.wanda.credit.ds.client.shangtang;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

public class GenerateString {
	public static final String id = "xxxx";
    public static final String secret = "xxxx";

    private static final String HASH_ALGORITHM = "HmacSHA256";
    static String timestamp = Long.toString(System.currentTimeMillis());
    static String nonce = RandomStringUtils.randomAlphanumeric(16);

    public static String genOriString(String api_key){

        ArrayList<String> beforesort = new ArrayList<String>();
        beforesort.add(api_key);
        beforesort.add(timestamp);
        beforesort.add(nonce);

        Collections.sort(beforesort, new SpellComparator());  
        StringBuffer aftersort = new StringBuffer();
        for (int i = 0; i < beforesort.size(); i++) {  
            aftersort.append(beforesort.get(i));
        }  

        String OriString = aftersort.toString();
        return OriString;
    }

    public static String genEncryptString(String genOriString, String api_secret)throws SignatureException {
        try{
            Key sk = new SecretKeySpec(api_secret.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(genOriString.getBytes());
            StringBuilder sb = new StringBuilder(hmac.length * 2);  

                @SuppressWarnings("resource")
                Formatter formatter = new Formatter(sb);  
                for (byte b : hmac) {  
                    formatter.format("%02x", b);  
                }  
            String EncryptedString = sb.toString();
            return EncryptedString;
        }catch (NoSuchAlgorithmException e1){
            throw new SignatureException("error building signature, no such algorithm in device "+ HASH_ALGORITHM);
        }catch (InvalidKeyException e){
            throw new SignatureException("error building signature, invalid key " + HASH_ALGORITHM);
        }
    }

    public static String genHeaderParam(String api_key, String api_secret) throws SignatureException{

        String GenOriString = genOriString(api_key);
        String EncryptedString = genEncryptString(GenOriString, api_secret);

        String HeaderParam = "key=" + api_key 
                     +",timestamp=" + timestamp 
                         +",nonce=" + nonce 
                     +",signature=" + EncryptedString;
        System.out.println(HeaderParam);
        return HeaderParam;
    }

    public static void main(String[] args) throws ClientProtocolException, IOException, SignatureException{
        genHeaderParam(id, secret);
    }
}
