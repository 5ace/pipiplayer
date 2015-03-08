package cn.pipi.mobile.pipiplayer.htmlutils;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;

import android.util.Log;

/**
 * 代理服务器类
 * @author hellogv
 *
 */
public class HttpGetProxy implements Serializable{
	private static final long serialVersionUID = 3L;
	final static public String TAG = "HttpGetProxy";
	/**下载线程*/
	private DownloadThread download = null;
	private DownLoadInfo downInfo =null;
	private long mDownloadSize;//用于两次比较得出下载速度
	private boolean isStoping=false;
	/**
	 * 初始化代理服务器
	 * @param localport 代理服务器监听的端口
	 */
	public HttpGetProxy(DownLoadInfo downInfo) {
		try {
			this.downInfo=downInfo;
			 start();
		} catch (Exception e) {
			//System.exit(0);
		}
	}
	
	public void start() {//开始下载
		try {
			timer.schedule(task, 1000,1000);
			prebuffer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//开始下载
	public void prebuffer() throws Exception{
		if(download!=null)
			download.stop();
		
		download=new DownloadThread(downInfo);
		download.start();
	}
	//重启
	public void ResumeDownload()
	{
		if(download!=null)
			download.resume();
		isStoping=false;
	}
	//暂停
	public void PauseDownload()
	{
		if(download!=null )
			download.pause();
			isStoping=true;
	}
	//删除任务
		public void DeleteDownload()
		{
			isStoping=true;
			if(download!=null)
				download.stop();
			task.cancel();
			timer.cancel();
		}
		
		Timer timer=new Timer();
		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!isStoping){
					try {
						downInfo.setDownCompleteSize(download.getDownloadedSize());
						   //数据显示
						   if(downInfo.getDownCompleteSize()>0
								   &&downInfo.getDownTotalSize()>0
								   &&mDownloadSize>0){
							   long speed=downInfo.getDownCompleteSize()-mDownloadSize;
							   downInfo.setDownSpeed(speed>0?speed:0);
							   downInfo.setDownProgress((int)(downInfo.getDownCompleteSize()*100/downInfo.getDownTotalSize()));
							 //  Log.i("TAG999", "name = "+downInfo.getDownName()+",progress"+downInfo.getDownProgress()+",index"+downInfo.getDownIndex());
							   //检测是否下个
							   if(downInfo.getDownCompleteSize()>=downInfo.getDownTotalSize()){
								   int index=downInfo.getDownIndex();
								   Log.i("DownTask", "下载完成  下一个 ="+(index+1));
								   //判断是否下载完毕
								   if(index+1<downInfo.getTaskList().size()){
									   downInfo.setDownIndex(index+1);
									   downInfo.setDownProgress(0);
									   downInfo.setDownCompleteSize(0);
									   downInfo.setDownTotalSize(0);
									   try {
											prebuffer();
										} catch (Exception e) {
											e.printStackTrace();
										}
								   }else{
									   isStoping=true;
									   download.stop();
									   downInfo.setDownSpeed(0);
									  // downInfo.setDownState(DownTask.TASK_FINISHED);
									   downInfo.setDownProgress(100);
									   Log.i("DownTask", "全部下载完成   ="+index);
								   }
							   }
						   }
						   mDownloadSize=downInfo.getDownCompleteSize();
				 } catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				 
			}
		};
	   
}