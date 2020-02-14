package com.wanda.credit.ds.client.juxinli.bean.report;
/**
 * 行为检查
 * @author xiaobin.hou
 *
 */
public class BehaviorCheck {
	
	/**
	 * 检查点名称
	 */
	private String check_point;
	
	/**
	 * 检查结果
	 */
	private String result;
	
	/**
	 * 标记
	 */
	private String score;
	
	/**
	 * 凭证
	 */
	private String evidence;
	
	public String getCheck_point(){
	
		return check_point;
	}
	
	public void setCheck_point(String check_point){
	
		this.check_point = check_point;
	}
	
	public String getResult(){
	
		return result;
	}
	
	public void setResult(String result){
	
		this.result = result;
	}
	
	public String getScore(){
	
		return score;
	}
	
	public void setScore(String score){
	
		this.score = score;
	}
	
	public String getEvidence(){
	
		return evidence;
	}
	
	public void setEvidence(String evidence){
	
		this.evidence = evidence;
	}
	
	@Override
	public String toString(){	
		return "BehaviorCheck [check_point=" + check_point + ", result=" + result + ", score=" + score + ", evidence="
		        + evidence + "]";
	}
	
}
