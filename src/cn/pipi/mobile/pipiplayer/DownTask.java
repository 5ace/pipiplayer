package cn.pipi.mobile.pipiplayer;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.htmlutils.Cntv;
import cn.pipi.mobile.pipiplayer.htmlutils.HtmlUtils;
import cn.pipi.mobile.pipiplayer.htmlutils.HttpGetProxy;
import cn.pipi.mobile.pipiplayer.htmlutils.Ku6;
import cn.pipi.mobile.pipiplayer.htmlutils.LeTv;
import cn.pipi.mobile.pipiplayer.htmlutils.QQVideo;
import cn.pipi.mobile.pipiplayer.htmlutils.SoHu;
import cn.pipi.mobile.pipiplayer.htmlutils.TuDou;
import cn.pipi.mobile.pipiplayer.htmlutils.W56;
import cn.pipi.mobile.pipiplayer.htmlutils.YouKu;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.MD5Util;

public class DownTask implements Runnable {

	private String urlstr;
	private DownLoadInfo downInfo;
	private Thread mThread = null;
	private boolean isDelete = false;//是否被删除
	public static final int TASK_WIFI_ERROR = 0;//网络异常
	public static final int TASK_FINISHED = 1;//下载完成
	    // 任务下载状态(加更新和运行两种)
	public static final int TASK_DOWNLOADING = 2;//下载中(显示进度)
	public static final int TASK_PAUSE_DOWNLOAD = 3;//已暂停
	public static final int TASK_RESUME_DOWNLOAD = 4;//已恢复
	public static final int TASK_QUIT_DOWNLOAD = 5;//中途退出
	public static final int TASK_WAITING_DOWNLOAD = 6;//等待下载
	public static final int TASK_DELETE_DOWNLOAD = 7;//删除下载
	public static final int TASK_FileMerge = 8;//合并视频
	    
	public DownTask(DownLoadInfo downInfo, Handler handler)
	{

		this.downInfo = downInfo;
		this.urlstr = downInfo.getDownUrl();
	//	this.mHandler = handler;
	//	start();
	}
	
	public DownTask(DownLoadInfo downInfo) {
		this.downInfo = downInfo;
		this.urlstr = downInfo.getDownUrl();
	}

	public void start() {//开始下载
		downInfo.setDownState(TASK_DOWNLOADING);
		if (mThread == null || mThread.getState() == State.TERMINATED) {
			mThread = new Thread(this);
			mThread.start();
		}
	}

	
    public void waiting() {//播放某影片时，让当前下载中影片处于等待
     DownCenter.getExistingInstance().PauseDownload(downInfo.getDownUrl());
     downInfo.setDownState(TASK_RESUME_DOWNLOAD);
	}
	public void stop() {
		DownCenter.getExistingInstance().autoDown();
		downInfo.setDownState(TASK_QUIT_DOWNLOAD);
	}
	
	public void finish() {
		//开始合并文件
		if(!MD5Util.getFromHttpfilm(urlstr)){
			DBHelperDao.getDBHelperDaoInstace().updataMovieStoreState(urlstr,  TASK_FINISHED);
			downInfo.setDownState(TASK_FINISHED);
			String PlayTaskUrl=DownCenter.getExistingInstance().getPlayTaskUrl();
			if(PlayTaskUrl==null||!urlstr.equals(PlayTaskUrl))//下载视频处于播放状态，则没必要结束线程
				DownCenter.getExistingInstance().DeleteDownload(urlstr, false);
			DownCenter.getExistingInstance().autoDown();
		}else{
			downInfo.setDownState(TASK_FileMerge);
			DBHelperDao.getDBHelperDaoInstace().updataMovieStoreState(urlstr,  TASK_FileMerge);
			//合并视频
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(DownCenter.getExistingInstance().GetFileMerge(downInfo)){//合并完成或者出现文件丢失
						DBHelperDao.getDBHelperDaoInstace().updataMovieStoreState(downInfo.getDownUrl(),  DownTask.TASK_FINISHED);
						downInfo.setDownState(DownTask.TASK_FINISHED);
					}
					//延迟一秒，等待完成状态显示到界面
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//合并完成在开启下个任务，避免卡
					DownCenter.getExistingInstance().autoDown();
				}
			}).start();
		}
	}
	
	 public void pause() {//暂停
		 if(downInfo.getDownState()==TASK_FileMerge)return;
		 //合并文件状态不可暂停
		 downInfo.setDownState(TASK_PAUSE_DOWNLOAD);
		 if(MD5Util.getFromHttpfilm(urlstr)){//聚合影片准备工作
			 if(downInfo.getProxy()!=null){
				 downInfo.getProxy().PauseDownload();
			 }
	      }else{
	    	  DownCenter.getExistingInstance().PauseDownload(urlstr);
	      }
		 DownCenter.getExistingInstance().autoDown();
	    }
	 
	 public void pauseErr() {//网络引起暂停
		 if(downInfo.getDownState()==TASK_FileMerge)return;
		 //合并文件状态不可暂停
		 downInfo.setDownState(TASK_WIFI_ERROR);
		 if(MD5Util.getFromHttpfilm(urlstr)){//聚合影片准备工作
			 if(downInfo.getProxy()!=null){
				 downInfo.getProxy().PauseDownload();
			 }
	      }else{
	    	  DownCenter.getExistingInstance().PauseDownload(urlstr);
	      }
	    }
	 public void delete() {//删除下载任务以及文件并清理数据库
		 isDelete=true;
		 DBHelperDao.getDBHelperDaoInstace().delSingleSmovieStore(urlstr);
		 if (mThread != null && mThread.getState() != State.TERMINATED) {
				mThread.interrupt();
			}
		 if(MD5Util.getFromHttpfilm(urlstr)){//聚合影片准备工作
			 FileUtils.deleteDirectory(HtmlUtils.getFileDir(downInfo));
			 if(downInfo.getProxy()!=null)
			 downInfo.getProxy().DeleteDownload();
	      }else{
	    	  DownCenter.getExistingInstance().DeleteDownload(urlstr,true);
	    	  FileUtils.deleteFinishedFile(urlstr); //应用层也删除一次
	      }
		  if(downInfo.getDownState()==DownTask.TASK_DOWNLOADING)
			  DownCenter.getExistingInstance().autoDown();//检测是否删除的正在下载的任务
				 
		downInfo=null;
	    }
	 
	 public void resume() {//继续下载，也是加入下载队列
		 downInfo.setDownState(TASK_DOWNLOADING);
		 if (mThread == null || mThread.getState() == State.TERMINATED) {//可能是上次未正常退出的任务
				mThread = new Thread(this);
				mThread.start();
			}else{//刚才用户主动暂停过的任务
				 if(MD5Util.getFromHttpfilm(urlstr)){//聚合影片准备工作
					 if(downInfo.getProxy()!=null)
					 downInfo.getProxy().ResumeDownload();
			      }else{
			    	  DownCenter.getExistingInstance().ResumeDownload(urlstr);
			      }
			}
	    }

	public DownLoadInfo getDownLoadInfo() {
		return downInfo;
	}
	
	public void setHandler(Handler handler) {
		
	//	mHandler = handler;
	}

//	@Override
	public void run() {

		try {
			if(downInfo.getDownState()==TASK_RESUME_DOWNLOAD){
				//恢复下载
				//resume();
			}else {
				//开始新的下载
         if(MD5Util.getFromHttpfilm(urlstr)){//聚合影片准备工作
        	getHtmlList(urlstr);
         }else{//皮皮资源准备工作
        	 DownCenter.getExistingInstance().GeneralPlayerTask(urlstr, FileUtils.getFileCaches());
             //存储影片大致信息
			String movieLocalPath = DownCenter.getExistingInstance().GetLocalFileName(urlstr);
			downInfo.setDownPath(movieLocalPath);
			downInfo.setDownTotalSize(DownCenter.getExistingInstance().GetCurFileSize(urlstr));
			DBHelperDao.getDBHelperDaoInstace().updataMovieStoreSize(urlstr,downInfo.getDownTotalSize());
			DBHelperDao.getDBHelperDaoInstace().updataMovieStoreLocal(urlstr,downInfo.getDownPath());
         }
         downInfo.setDownState(TASK_DOWNLOADING);
				}
			
			
				while (!isDelete) {
					try {
						if (downInfo.getDownProgress() == 100.0) {
							finish();
								break;
							}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(downInfo.getDownState()==TASK_DOWNLOADING){//得到下载速度和进度
						if(!MD5Util.getFromHttpfilm(urlstr)){
							DownCenter.getExistingInstance().getDownInfo(downInfo);
						}
						DBHelperDao.getDBHelperDaoInstace().updataMovieStoreProgress(urlstr, downInfo.getDownProgress());
					}
					else if(downInfo.getDownState()==TASK_PAUSE_DOWNLOAD){//暂停时保存一下数据
					}
					else if(downInfo.getDownState()==TASK_RESUME_DOWNLOAD){//继续下载得到下载速度和进度
					}
					else if (downInfo.getDownState() ==TASK_FINISHED)//下载完成 跳出循环
					{
						//finish();
						break;
					}
					else if (downInfo.getDownState()== TASK_DELETE_DOWNLOAD )//用户主动删除任务  跳出循环
					{
						DownCenter.getExistingInstance().DeleteDownload(urlstr, true);
						DBHelperDao.getDBHelperDaoInstace().delSingleSmovieStore(urlstr) ;
						break;
					}
					
				try {
					mThread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	public String getMovieUrl() {
		// TODO Auto-generated method stub
		return urlstr;
	}
	
	private void getHtmlList(String path){
		if(downInfo.getTaskList()==null){
			ArrayList<String> urls=null;
			if(path.startsWith("http://www.letv.com")){
				LeTv leTv=new LeTv();
				urls=leTv.getDownloadInfo(path, 0);
			}else if(path.startsWith("http://tv.sohu.com")){
				SoHu sohu=new SoHu();
				urls=sohu.getDownloadInfo(path, 0);
			}else if(path.startsWith("http://v.qq.com")){
				QQVideo qq=new QQVideo();
				urls=qq.getDownloadInfo(path, 0);
			}else if(path.startsWith("http://v.youku.com")){
				YouKu youku=new YouKu();
				urls=youku.getDownloadInfo(path, 0);
			}else if(path.startsWith("http://www.tudou.com")){
				TuDou tudou=new TuDou();
				urls=tudou.getDownloadInfo(path, 0);
			}else if(path.startsWith("http://tv.cntv.cn")){
				Cntv cntv=new Cntv();
				urls=cntv.getDownloadInfo(path, 0);
			}else if(path.startsWith("http://v.ku6.com")){
				Ku6 ku6=new Ku6();
				urls=ku6.getDownloadInfo(path, 0);
			}else if(path.startsWith("http://www.56.com")){
				W56 w56=new W56();
				urls=w56.getDownloadInfo(path, 0);
			}
			downInfo.setTaskList(urls);
		}
		if(downInfo.getTaskList()!=null&&downInfo.getTaskList().size()>0){
			HttpGetProxy proxy = new HttpGetProxy(downInfo);
			downInfo.setProxy(proxy);
		}
	}
	
	
}
