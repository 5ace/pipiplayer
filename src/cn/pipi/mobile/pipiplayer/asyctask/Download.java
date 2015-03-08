package cn.pipi.mobile.pipiplayer.asyctask;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.beans.DownloadAPK;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.service.UpdateService;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;


public class Download extends AsyncTask<String, Float, Boolean>{
   
	/**定义下载地址*/
	private String urlstr;
	/**定义下载地址*/
	private String version;
	/**定义下载信息的一个实体类*/
	private DownloadAPK downloadinfo;
	/**网络连接*/
	private HttpURLConnection hurlconn;
	/**定义下载的文件*/
	private File file;
	/**文件保存的路径*/
	private String filepath;
	/**定义变量判断下载状态时候停止*/
	private boolean isstop=false;
	
	private ProgressBar progressBar;
	private Handler handler;
	private int progress;
	
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public Download(Context context,DownloadAPK downloadAPK,Handler handler,boolean oldUrl){
		this.urlstr = downloadAPK.getUrl();
		this.filepath = downloadAPK.getPath();
		this.version = downloadAPK.getVersion();
		this.downloadinfo=downloadAPK;
		this.handler=handler;
		if(!oldUrl){
			//更换网址
			String channel=context.getString(R.string.BaiduMobAd_CHANNEL);
			if(urlstr!=null&&urlstr.endsWith(".apk")&&
					channel!=null&&channel.length()>0){
				StringBuffer sb=new StringBuffer();
				sb.append(urlstr).insert(urlstr.length()-4, "_"+channel);
				urlstr=sb.toString();
			}
		}
	}
	/**
	 * 判断是不是第一次下载这个资源
	 * @param url
	 * @return
	 */
	public boolean isFirstDownload(String url){
	/*	if(filepath!=null){
			file = new File(filepath);
			if(!file.exists()){//文件丢失，默认第一次下载
				return true;
			}
		}
		if(dao.isDownload(url)){//有下载记录
			return false;
		}*/
		return true;
	}
	/**
	 * 第一次下载的时候初始化文件
	 */
	public boolean init(){
		try {
			URL url = new URL(urlstr);
			hurlconn = (HttpURLConnection) url.openConnection();
		//	hurlconn.setRequestProperty("Accept-Encoding", "identity");
			hurlconn.setUseCaches(false);
			hurlconn.setRequestMethod("GET");
			hurlconn.setConnectTimeout(20000);
			int totle=hurlconn.getContentLength();
			if(totle==0){
				handler.sendEmptyMessage(UpdateService.DOWN_NOT_FOUND);
				return false;
			}
			file = new File(filepath);
			if(!file.exists()){
				file.createNewFile();
				RandomAccessFile raf = new RandomAccessFile(file, "rwd");
				raf.setLength(totle);
				raf.close();
			}
			downloadinfo.setEndPoint(totle);
		    downloadinfo.setLoadFileSize(totle);
		    return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			handler.sendEmptyMessage(UpdateService.DOWN_ERROR);
		}
		return false;
	}

	/**
	 * 下载文件根据传入的地址来判读是不是第一次下载
	 * 如果是 则直接下载这样从新下载，并且删除以前数据和文件
	 * 不是第一次下载则根据上传的断点下载
	 */
	public boolean DownloadStart(String version){
		if(isFirstDownload(version)){//第一次下载时
			//下载
			 if(init()&&hurlconn!=null){
			     firstDownload(hurlconn);
			 }
		
		}else{
			downloadinfo = DBHelperDao.getDBHelperDaoInstace().getInfos(version);
			if(downloadinfo.getCompleteload()<downloadinfo.getLoadFileSize()){//已经下载的情况，但是没下完
				if(init()&&hurlconn!=null)
				unfirstDownload(hurlconn);
			}else{//已经下载过并且下载完了
				//提示安装
				handler.sendEmptyMessage(UpdateService.DOWN_OK);
			}
		}
		hurlconn.disconnect();
		return true;
	}
	@Override
	protected void onPreExecute(){
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
	
	@Override
	protected void onProgressUpdate(Float... values) {
		
		if(getProgress()<values[0].intValue()){//只有进度上升才更新，减少通知栏更新压力
			setProgress(values[0].intValue());
			 Message msg = new Message();
			 msg.obj=values[0].intValue();
			 msg.what=UpdateService.UPDATE_MESSAGE;
	         handler.sendMessage(msg);
		}
		
	}
	@Override
	protected Boolean doInBackground(String... params) {
		try {
			return DownloadStart(version);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	
	
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
		this.progressBar.setProgress(0);
	}
	
	public void setIsstop(boolean isstop) {
		this.isstop = isstop;
		DBHelperDao.getDBHelperDaoInstace().updateInfos(downloadinfo);
	}
	/**
	 * 判断是否为第一次下载该文件
	 * @param hurlconn 
	 * @param isstop  传入一个boolean参数判断是否要暂停下载
	 * @return  如果下载完成则返回true，如果暂停或出现异常则返回false
	 */
	public boolean firstDownload(HttpURLConnection hurlconn){
		try {
			handler.sendEmptyMessage(UpdateService.DOWN_START);
			// 设置范围，格式为Range：bytes x-y;
			if(hurlconn.getResponseCode()== HttpURLConnection.HTTP_OK){
				 hurlconn.connect();
				 InputStream instream = hurlconn.getInputStream();
				 RandomAccessFile rasf = new RandomAccessFile(file, "rwd");
				 //这种方法有弊端适合小文件传输
				 byte[] b = new byte[1024];
				 int length =-1;
				 int completeload = 0;
				 DBHelperDao.getDBHelperDaoInstace().saveInfos(downloadinfo);//插入数据
				 while((length=instream.read(b))!=-1){
					 rasf.seek(completeload);
					 completeload+= length;
					 rasf.write(b,0,length);
					 float completeloadfloat = (float)completeload;
					 publishProgress(completeloadfloat/downloadinfo.getLoadFileSize()*100);
					 downloadinfo.setCompleteload(completeload);
					// dao.updateInfos(downloadinfo);
					 if(completeload==downloadinfo.getLoadFileSize()){
						 DBHelperDao.getDBHelperDaoInstace().updateInfos(downloadinfo);
						 handler.sendEmptyMessage(UpdateService.DOWN_OK);
						 return true;
					 }
					 if(isstop){
						 DBHelperDao.getDBHelperDaoInstace().updateInfos(downloadinfo);
						 return false;
					 }
				 }
			}else if(hurlconn.getResponseCode()== HttpURLConnection.HTTP_NOT_FOUND){//资源未找到，尝试新连接
				handler.sendEmptyMessage(UpdateService.DOWN_NOT_FOUND);
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			handler.sendEmptyMessage(UpdateService.DOWN_ERROR);
			e.printStackTrace();
		}finally{
			hurlconn.disconnect();
		}
		return false;
	}
	/**
	 * 判断是否为非第一次下载该文件
	 * @return  如果下载完成则返回true，如果暂停或出现异常则返回false，默返回为false
	 */
	public boolean unfirstDownload(HttpURLConnection hurlconn){
		try {
			// 设置范围，格式为Range：bytes x-y;
			if(downloadinfo.getCompleteload()>0&&downloadinfo.getLoadFileSize()>0)
			hurlconn.setRequestProperty("Range", "bytes="+downloadinfo.getCompleteload()+"-"+downloadinfo.getLoadFileSize());
		//	if(hurlconn.getResponseCode()== HttpURLConnection.HTTP_OK){
				hurlconn.connect();
				 InputStream instream = hurlconn.getInputStream();
				 RandomAccessFile rasf = new RandomAccessFile(file, "rwd");
				 rasf.seek(downloadinfo.getCompleteload());
				 byte[] b = new byte[1024];
				 int length =-1;
				 int completeload = downloadinfo.getCompleteload();
				 
				 handler.sendEmptyMessage(UpdateService.DOWN_START);
				 
				 while((length=instream.read(b))!=-1){
					 
					 completeload+= length;
					 rasf.write(b,0,length);
					 float completeloadfloat = (float)completeload;
					 publishProgress(completeloadfloat/downloadinfo.getLoadFileSize()*100);
					 downloadinfo.setCompleteload(completeload);
					 DBHelperDao.getDBHelperDaoInstace().updateInfos(downloadinfo);
					 if(completeload==downloadinfo.getLoadFileSize()){
					//	 dao.updateInfos(downloadinfo);
						 handler.sendEmptyMessage(UpdateService.DOWN_OK);
						 return true;
					 }
					 if(isstop){
						// dao.updateInfos(downloadinfo);
						 return false;
					 }
				 }
			
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			handler.sendEmptyMessage(UpdateService.DOWN_ERROR);
			e.printStackTrace();
		}
		return false;
	}
	 
	
	
	
}












