package com.wings.videobankuploader.models;

public class UserModel {

	// private variables
	int userid;
	private String username;
	private String phonenumber;
	private String userpass;
	private String fname;
	private String lname;
	private String email;

	// Empty constructor
	public UserModel() {

	}

	// constructor
	public UserModel(int id, String username, String userpass, String fname,
			String lname, String email, String phonenumber) {
		this.userid = id;
		this.username = username;
		this.userpass = userpass;
		this.fname = fname;
		this.lname = lname;
		this.email = email;
		this.phonenumber = phonenumber;
	}

	public UserModel(String username, String userpass, String fname,
			String lname, String email, String phonenumber) {
		this.username = username;
		this.userpass = userpass;
		this.fname = fname;
		this.lname = lname;
		this.email = email;
		this.phonenumber = phonenumber;
	}

	// getting ID
	public int getID() {
		return this.userid;
	}

	// setting id
	public void setID(int userid) {
		this.userid = userid;
	}

	// getting name
	public String getName() {
		return this.username;
	}

	// setting name
	public void setName(String username) {
		this.username = username;
	}

	public String getUPass() {
		return this.userpass;
	}

	// setting name
	public void setUPass(String userpass) {
		this.userpass = userpass;
	}

	public String getUFName() {
		return this.fname;
	}

	// setting name
	public void setUFName(String fname) {
		this.fname = fname;
	}

	public String getULName() {
		return this.lname;
	}

	// setting name
	public void setULName(String lname) {
		this.lname = lname;
	}

	public String getEmail() {
		return this.email;
	}

	// setting name
	public void setEmail(String email) {
		this.email = email;
	}

	// getting phone number
	public String getPhoneNumber() {
		return this.phonenumber;
	}

	// setting phone number
	public void setPhoneNumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
}