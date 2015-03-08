/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.pipi.mobile.pipiplayer.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pipi.mobile.pipiplayer.htmlutils.HttpGetProxy;
/**
 * 
 * @author wangbo DownLoadInfo 电影集数信息
 */
public class DownLoadInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	// 来自电影id
	private String downID;
	// 集数名字
	private String downName;
	// 集数图片地址
	private String downImg;
	// 集数地址
	private String downUrl;
	// 集数本地地址
	private String downPath;
	//集数标签
	private String downTag;
	//该集有几段
	private int downCount;
	//当前为第几集
	private int downPosition;
	//当前状态   1下载完成；0正在下载；-1等待下载
	private int downState=-1;
	//清晰度
	private int parseMode;
	//文件大小
	private long downTotalSize;
	//已下载  皮皮资源只保存了百分比   聚合资源保存了下载多少
	private long downCompleteSize;
	//下载速度
	private long downSpeed;
	//当前正在下载的那段
	private int downIndex;
	//下载进度
	private long downProgress;

	private String downTime;//总共播放时间
	//集数资源 用于播放时选集
	private ArrayList<String> playList;
	//正在下载的该集片段
	private ArrayList<String> taskList;
	private boolean delete;
	private HttpGetProxy proxy;//下载任务
	private String downLocal;//文件夹存放路径
	
	private Map<String, ArrayList<String>> mapList;//影片地址 用于切换标清高清资源
	public String getDownID() {
		return downID;
	}

	public void setDownID(String downID) {
		this.downID = downID;
	}

	public String getDownImg() {
		return downImg;
	}

	public void setDownImg(String downImg) {
		this.downImg = "http://img.pipi.cn/movies/126X168/" +downImg;
	}

	public String getDownName() {
		return downName;
	}

	public void setDownName(String downName) {
		this.downName = downName;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public String getDownPath() {
		return downPath;
	}

	public void setDownPath(String downPath) {
		this.downPath = downPath;
	}

	public String getDownTag() {
		return downTag;
	}

	public void setDownTag(String downTag) {
		this.downTag = downTag;
	}

	public int getDownCount() {
		return downCount;
	}

	public void setDownCount(int downCount) {
		this.downCount = downCount;
	}

	public int getDownPosition() {
		return downPosition;
	}

	public void setDownPosition(int downPosition) {
		this.downPosition = downPosition;
	}

	public int getDownState() {
		return downState;
	}

	public void setDownState(int downState) {
		this.downState = downState;
	}

	public int getParseMode() {
		return parseMode;
	}

	public void setParseMode(int parseMode) {
		this.parseMode = parseMode;
	}

	public long getDownTotalSize() {
		return downTotalSize;
	}

	public void setDownTotalSize(long downTotalSize) {
		this.downTotalSize = downTotalSize;
	}

	public long getDownCompleteSize() {
		return downCompleteSize;
	}

	public void setDownCompleteSize(long downCompleteSize) {
		this.downCompleteSize = downCompleteSize;
	}

	public long getDownSpeed() {
		return downSpeed;
	}

	public void setDownSpeed(long downSpeed) {
		this.downSpeed = downSpeed;
	}

	public int getDownIndex() {
		return downIndex;
	}

	public void setDownIndex(int downIndex) {
		this.downIndex = downIndex;
	}

	public String getDownTime() {
		return downTime;
	}

	public void setDownTime(String downTime) {
		this.downTime = downTime;
	}

	public long getDownProgress() {
		return downProgress;
	}

	public void setDownProgress(long downProgress) {
		this.downProgress = downProgress;
	}

	public ArrayList<String> getPlayList() {
		return playList;
	}

	public void setPlayList(ArrayList<String> playList) {
		this.playList = playList;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public ArrayList<String> getTaskList() {
		return taskList;
	}

	public void setTaskList(ArrayList<String> taskList) {
		this.taskList = taskList;
	}

	public HttpGetProxy getProxy() {
		return proxy;
	}

	public void setProxy(HttpGetProxy proxy) {
		this.proxy = proxy;
	}


	public String getDownLocal() {
		return downLocal;
	}

	public void setDownLocal(String downLocal) {
		this.downLocal = downLocal;
	}

	public DownLoadInfo() {
		super();
	}

	public DownLoadInfo(String downID, String downName, String downImg,
			String downUrl) {
		super();
		this.downID = downID;
		this.downImg = downImg;
		this.downName = downName;
		this.downUrl = downUrl;
	}

	public DownLoadInfo(String downID, String downName, String downImg,
			String downUrl, int downPosition, int downState) {
		super();
		this.downID = downID;
		this.downImg = downImg;
		this.downName = downName;
		this.downUrl = downUrl;
		this.downPosition = downPosition;
		this.downState = downState;
	}

	public DownLoadInfo(String downID, String downName, String downImg,
			String downUrl, int downPosition, ArrayList<String> playList) {
		super();
		this.downID = downID;
		this.downImg = downImg;
		this.downName = downName;
		this.downUrl = downUrl;
		this.downPosition = downPosition;
		this.playList = playList;
	}

	public Map<String, ArrayList<String>> getMapList() {
		return mapList;
	}

	public void setMapList(Map<String, ArrayList<String>> mapList) {
		this.mapList = mapList;
	}



	
	
}
