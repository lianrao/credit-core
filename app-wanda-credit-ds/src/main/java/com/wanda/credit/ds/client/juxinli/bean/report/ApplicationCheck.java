package com.wanda.credit.ds.client.juxinli.bean.report;


/**
 * 申请表单检查 .
 */
public class ApplicationCheck {
	
	/**
	 * 标记
	 */
	private String score;
	
	/**
	 * 检查点名称
	 */
	private String check_point;
	
	/**
	 * 检查结果
	 */
	private String result;
	
	/**
	 * 判断依据
	 */
	private String evidence;
	
	public String getScore(){
	
		return score;
	}
	
	public void setScore(String score){
	
		this.score = score;
	}
	
	public String getCheck_point(){
	
		return check_point;
	}
	
	public void setCheck_point(String check_point){
	
		this.check_point = check_point;
	}
	
	public String getEvidence(){
	
		return evidence;
	}
	
	public void setEvidence(String evidence){
	
		this.evidence = evidence;
	}
	
	public String getResult(){
	
		return result;
	}
	
	public void setResult(String result){
	
		this.result = result;
	}
	
	@Override
	public String toString(){
	
		return "ApplicationCheck [score=" + score + ", check_point=" + check_point + ", result=" + result
		        + ", evidence=" + evidence + "]";
	}
	
}
