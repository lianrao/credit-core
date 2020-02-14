package com.wanda.credit.ds.client.baiduFace.bean;

import java.util.List;

public class ResultBean {
	private double score;
	private int face_num;
	private List<FaceListBean> face_list;
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public List<FaceListBean> getFace_list() {
		return face_list;
	}
	public void setFace_list(List<FaceListBean> face_list) {
		this.face_list = face_list;
	}
	public int getFace_num() {
		return face_num;
	}
	public void setFace_num(int face_num) {
		this.face_num = face_num;
	}

}
