package com.wanda.credit.ds.client.policeAuthV2.data;

import com.wanda.credit.ds.client.policeAuthV2.localApi.Api;

/**
 * Created by on 2018/3/29.
 * 保留数据区
 * 加密保留数据与照片工具类
 */
public class EncryptedReservedLocal {
    private static String certID = "rzfw01";
    static Api api = new Api();

    //加密保留数据(吉大正元)
    public static byte[] encryptEnvelope(byte[] msg,String trade_id,String signUrl) throws Exception {
        api.initConnection(DataSignatureLocal.filePath);
        byte[] result = api.encryptEnvelope(msg,certID,trade_id,signUrl);
        return result;
    }
}
