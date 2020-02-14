package com.wanda.credit.ds.client.dsconfig.ext.expression;

import com.wanda.credit.dsconfig.model.expression.ConditionExpr;

/**
 * @description 参数条件表达式
 * @author wuchsh
 * @version 1.0
 * @createdate 2017年3月7日 下午8:41:58
 * 
 */
public class ParamCondExpr extends ConditionExpr {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
