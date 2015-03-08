package cn.pipi.mobile.pipiplayer.beans;

public class MovieSms {
private String userName;
private String content;
private String time;
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
public MovieSms(String userName, String content, String time) {
	super();
	this.userName = userName;
	this.content = content;
	this.time = time;
}
public MovieSms() {
	super();
}

}
