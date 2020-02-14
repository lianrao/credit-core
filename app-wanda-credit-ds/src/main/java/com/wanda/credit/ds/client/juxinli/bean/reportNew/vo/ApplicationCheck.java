package com.wanda.credit.ds.client.juxinli.bean.reportNew.vo;

/**
 * 申请表单检查
 * @author xiaobin.hou
 *
 */
public class ApplicationCheck {

	/**
	 * 标记
	 */
	private String app_point;

	/**
	 * 检查点名称
	 */
	private CheckPoints check_points;

	public String getApp_point() {
		return app_point;
	}

	public void setApp_point(String app_point) {
		this.app_point = app_point;
	}

	public CheckPoints getCheck_points() {
		return check_points;
	}

	public void setCheck_points(CheckPoints check_points) {
		this.check_points = check_points;
	}

	@Override
	public String toString() {
		return "ApplicationCheck [app_point=" + app_point + ", check_points=" + check_points + "]";
	}

}
