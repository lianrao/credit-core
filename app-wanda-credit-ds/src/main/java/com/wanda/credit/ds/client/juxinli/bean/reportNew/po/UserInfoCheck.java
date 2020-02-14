package com.wanda.credit.ds.client.juxinli.bean.reportNew.po;
/**
 * 用户信息检测
 * @author xiaobin.hou
 *
 */
public class UserInfoCheck {

	private CheckSearchInfo check_search_info;

	private CheckBlackInfo check_black_info;

	public CheckSearchInfo getCheck_search_info() {
		return check_search_info;
	}

	public void setCheck_search_info(CheckSearchInfo check_search_info) {
		this.check_search_info = check_search_info;
	}

	public CheckBlackInfo getCheck_black_info() {
		return check_black_info;
	}

	public void setCheck_black_info(CheckBlackInfo check_black_info) {
		this.check_black_info = check_black_info;
	}

	@Override
	public String toString() {
		return "userInfoCheck [check_search_info=" + check_search_info + ", check_black_info=" + check_black_info + "]";
	}

}
