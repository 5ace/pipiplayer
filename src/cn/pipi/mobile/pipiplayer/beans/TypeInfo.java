package cn.pipi.mobile.pipiplayer.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TypeInfo {//电影
private List<String> orderList;//排序
private List<String> typeList;//动作 科幻
private List<String> areaList;//大陆 欧美
private List<String> yearList;//年份
private Map<String,String> cates;
public List<String> getTypeList() {
	return typeList;
}
public void setTypeList(List<String> typeList) {
	this.typeList = typeList;
}
public List<String> getAreaList() {
	return areaList;
}
public void setAreaList(List<String> areaList) {
	this.areaList = areaList;
}
public List<String> getYearList() {
	return yearList;
}
public void setYearList(List<String> yearList) {
	this.yearList = yearList;
}
public Map<String, String> getCates() {
	return cates;
}
public void setCates(Map<String, String> cates) {
	this.cates = cates;
}
public List<String> getOrderList() {
	if(orderList==null){
		orderList=new ArrayList<String>();
		orderList.add("最新");
		orderList.add("最热");
		orderList.add("评分");
	}
	return orderList;
}
public void setOrderList(List<String> orderList) {
	this.orderList = orderList;
}

}
