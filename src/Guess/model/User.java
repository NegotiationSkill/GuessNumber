package Guess.model;

import java.io.Serializable;

public class User implements Serializable {
	private String username;
	private String userpwd;
	private int grade;
	 
	public User(String name,String pwd) {
		this.username = name;
		this.userpwd = pwd;
	}
	public User(String name,int grade){
		this.username = name;
		this.grade = grade;
		
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpwd() {
		return userpwd;
	}

	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
	
}
