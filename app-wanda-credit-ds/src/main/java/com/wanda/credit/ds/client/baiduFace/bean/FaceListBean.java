package com.wanda.credit.ds.client.baiduFace.bean;

public class FaceListBean {
	private String face_token;
	private int  face_probability;
	private FaceLocationBean location;
	private FaceAngleBean angle;
	public String getFace_token() {
		return face_token;
	}
	public void setFace_token(String face_token) {
		this.face_token = face_token;
	}
	public int getFace_probability() {
		return face_probability;
	}
	public void setFace_probability(int face_probability) {
		this.face_probability = face_probability;
	}
	public FaceLocationBean getLocation() {
		return location;
	}
	public void setLocation(FaceLocationBean location) {
		this.location = location;
	}
	public FaceAngleBean getAngle() {
		return angle;
	}
	public void setAngle(FaceAngleBean angle) {
		this.angle = angle;
	}
	
}
