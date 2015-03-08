package cn.pipi.mobile.pipiplayer.beans;

import android.text.TextUtils;

public class SelectionInfo {

String tp = "电影";//影片类型(电影，动漫，电视剧)
String order ="最新";//排序
String stp ;//电影类型(全部，动作，科幻)
String ar ;//电影地区
String ft ;//电影年份
String sb = "1";//电影排序(最新(1),最热(2),评分(3))
int ps = 20;//每页显示多少
int np =1;//当前页数
String dev = "android";//
String ever ;//版本号

public String getTp() {
	return tp;
}
public void setTp(String tp) {
	this.tp = tp;
}
public String getStp() {
	return stp;
}
public void setStp(String stp) {
	this.stp = stp;
}
public String getAr() {
	return ar;
}
public void setAr(String ar) {
	this.ar = ar;
}
public String getFt() {
	return ft;
}
public void setFt(String ft) {
	this.ft = ft;
}
public String getSb() {
    if(!TextUtils.isEmpty(order)&&order.equals("最热")){
    	sb = "2";
	}else if(!TextUtils.isEmpty(order)&&order.equals("评分")){
		sb = "3";
	}else{
		sb = "1";
	}
	return sb;
}
public void setSb(String sb) {
	this.sb = sb;
}
public int getNp() {
	return np;
}
public void setNp(int np) {
	this.np = np;
}
public int getPs() {
	return ps;
}
public void setPs(int ps) {
	this.ps = ps;
}
public String getOrder() {
	return order;
}
public void setOrder(String order) {
	this.order = order;
}


}
