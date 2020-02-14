package com.wanda.credit.ds.client.policeAuthV2.data;

import com.fri.ctid.security.jit.Api;

/**
 * Created by on 2018/3/29.
 * 保留数据区
 * 加密保留数据与照片工具类
 */
public class EncryptedReservedData {
    private static String certID = "rzfw01";
    static Api api = new Api();

    //加密保留数据(吉大正元)
    public static byte[] encryptEnvelope(byte[] msg) throws Exception {
        api.initConnection(DataSignature.filePath);
        byte[] result = api.encryptEnvelope(msg,certID);
        return result;
    }
}
