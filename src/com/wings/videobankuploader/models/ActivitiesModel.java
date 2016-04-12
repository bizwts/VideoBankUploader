package com.wings.videobankuploader.models;

public class ActivitiesModel {

	private int activity_detail_id;
	private int activity_id;
	private String username;
	private String message;
	private int activity_type;
	private int status;
	private String createddate;
	private String filename;
	private String filepath;
	private String destpath;
	private int isdirectory;
	private int isvmr;

	public ActivitiesModel() {

	}

	public ActivitiesModel(int activity_id, String username, String message,
			int activity_type, int status, String createddate, String filename,
			String filepath, String destpath, int isdirectory, int isvmr) {
		this.activity_id = activity_id;
		this.username = username;
		this.message = message;
		this.activity_type = activity_type;
		this.status = status;
		this.createddate = createddate;
		this.filename = filename;
		this.filepath = filepath;
		this.destpath = destpath;
		this.isdirectory = isdirectory;
		this.isvmr = isvmr;
	}

	public int getActDetailID() {
		return this.activity_detail_id;
	}

	public void setActDetailID(int activity_detail_id) {
		this.activity_detail_id = activity_detail_id;
	}

	public int getActivityID() {
		return this.activity_id;
	}

	public void setActivityID(int activity_id) {
		this.activity_id = activity_id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getActivityType() {
		return this.activity_type;
	}

	public void setActivityType(int activity_type) {
		this.activity_type = activity_type;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreateddate() {
		return this.createddate;
	}

	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilePath() {
		return this.filepath;
	}

	public void setFilePath(String filepath) {
		this.filepath = filepath;
	}

	public String getDestPath() {
		return this.destpath;
	}

	public void setDestPath(String destpath) {
		this.destpath = destpath;
	}

	public int getIsdirectory() {
		return this.isdirectory;
	}

	public void setIsdirectory(int isdirectory) {
		this.isdirectory = isdirectory;
	}

	public int getIsVMR() {
		return this.isvmr;
	}

	public void setIsVMR(int isvmr) {
		this.isvmr = isvmr;
	}
}
