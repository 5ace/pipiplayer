package cn.pipi.mobile.pipiplayer.beans;

import java.io.Serializable;

public class ZhuanTiInfo implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String id;
private String name;
private String img;
private String show_name;
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getImg() {
	return img;
}
public void setImg(String img) {
	this.img = img;
}
public String getShow_name() {
	return show_name;
}
public void setShow_name(String show_name) {
	this.show_name = show_name;
}

}
