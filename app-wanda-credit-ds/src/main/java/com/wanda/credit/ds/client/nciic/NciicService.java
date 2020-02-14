package com.wanda.credit.ds.client.nciic;

public interface NciicService{
	// 简项核查方法
	public String nciicCheck(String inLicense, String inConditions);
	// 取得条件文件模板
	public String nciicGetCondition(String inLicense) throws Exception;
}
