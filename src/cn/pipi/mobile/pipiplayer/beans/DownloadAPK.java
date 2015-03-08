package cn.pipi.mobile.pipiplayer.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DownloadAPK implements Serializable{
	  private int _id;
	  /**下载的起始点*/
      private int startPoint;
      /**下载的结束点*/
      private int endPoint;
      /**下载文件的大小*/
      private int loadFileSize;
      /**已下载文件的大小*/
      private int completeload;
      private int state=-1;//下载状态  -1没有下载  0 下载中  1 下载完成
      /**下载的地址*/
      private  String  url;
      private String version;//APK版本
      private String path;//保存路径
      private String md5;
      public DownloadAPK() {
    		super();
    	}
      public DownloadAPK(String version,String url,String md5) {
  		super();
  		this.version = version;
  		this.url = url;
  		this.md5 = md5;
  	}
      
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public DownloadAPK(int startPoint, int endPoint,int completeload,int loadFileSize,String url,String md5) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.loadFileSize = loadFileSize;
		this.completeload = completeload;
		this.url = url;
		this.md5 = md5;
	}
	
	public DownloadAPK(int startPoint, int endPoint,
			int loadFileSize, int completeload, String url, String version, String path) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.loadFileSize = loadFileSize;
		this.completeload = completeload;
		this.url = url;
		this.version = version;
		this.path = path;
	}
	
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(int startPoint) {
		this.startPoint = startPoint;
	}
	public int getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(int endPoint) {
		this.endPoint = endPoint;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getLoadFileSize() {
		return loadFileSize;
	}
	public void setLoadFileSize(int loadFileSize) {
		this.loadFileSize = loadFileSize;
	}
	public int getCompleteload() {
		return completeload;
	}
	public void setCompleteload(int completeload) {
		this.completeload = completeload;
	}
	
	
      
      
}
