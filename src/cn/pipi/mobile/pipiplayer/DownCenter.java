package cn.pipi.mobile.pipiplayer;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.htmlutils.HtmlUtils;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVLC;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcException;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.MD5Util;

import com.umeng.analytics.MobclickAgent;

public class DownCenter   {
	
	/** 定义最大同时下载的任务数量 **/
	public static final int MAX_THREAD_NUMS = 1;
	
	private static final String TAG = "DownCenter";

	private static DownCenter sInstance;
	
	
	private List<DownTask> mDownTaskList;
	
	

	/** DownCenter instance C pointer */
	private long mDownCenterInstance = 0; // Read-only, reserved for JNI
	/** Check in DownCenter already initialized otherwise crash */
	private boolean mIsInitialized = false;
	private Handler mHandler;
	private LibVLC mlibvlc = null;
	private SharedPreferences sharedPreferences;
	public List<DownTask> getDownTaskList() {
		return mDownTaskList;
	}
	
   public List<String> getDownTaskListHashID() {
	   //获取所有hashid  
	   List<String> HashIDList=new ArrayList<String>();//将
	   for(DownTask downTask:mDownTaskList){
		   if(!MD5Util.getFromHttpfilm(downTask.getMovieUrl())){
			   String ppfilmHashID=MD5Util.getMD5HashIDByUrl(downTask.getMovieUrl());
			   if(ppfilmHashID!=null)
				   HashIDList.add(ppfilmHashID);
		       else{
			        return null;//皮皮资源验证失败其中一个，则无法判断要删除的是哪个，中断操作
		        }
		   }
	   }
	//   最近播放影片hashid
   	String preMovie=sharedPreferences.getString("preMovie", null);//上次影片
	if(preMovie!=null&&!MD5Util.getFromHttpfilm(preMovie))	{
		 String ppfilmHashID=MD5Util.getMD5HashIDByUrl(preMovie);
		 if(ppfilmHashID!=null)
			   HashIDList.add(ppfilmHashID);
		   else 
			   return null;
	}
	   
	   return HashIDList;
	}

	/**
	 * Singleton constructor of libVLC Without surface and vout to create the
	 * thumbnail and get information e.g. on the MediaLibraryActivity
	 * 
	 * @return libVLC instance
	 * @throws LibVlcException
	 */
	public static DownCenter getInstance() throws LibVlcException {
		synchronized (DownCenter.class) {
			if (sInstance == null) {
				/* First call */
				sInstance = new DownCenter();
				sInstance.init();
			}
		}
		
		

		return sInstance;
	}

	/**
	 * Return an existing instance of libVLC Call it when it is NOT important
	 * that this fails
	 * 
	 * @return libVLC instance OR null
	 */
	public static DownCenter getExistingInstance() {
		synchronized (DownCenter.class) {
			if (sInstance == null) {
				/* First call */
				sInstance = new DownCenter();
				try {
					sInstance.init();
				} catch (LibVlcException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return sInstance;
		}
	}

	/**
	 * Constructor It is private because this class is a singleton.
	 */
	private DownCenter() {
		mDownTaskList = DBHelperDao.getDBHelperDaoInstace().getActiveDownLoad();
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(VLCApplication.getAppContext());
	}
	
	//查询MovieURL 为inppfilmUrl，moveinfo
	public String getDownLoadInfoFromresultsInfos(String inppfilmUrl)
	{
		try {
			if(inppfilmUrl.startsWith("ppfilm")){
				return mlibvlc.nativeGeneralHashID(inppfilmUrl);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Destructor: It is bad practice to rely on them, so please don't forget to
	 * call destroy() before exiting.
	 */
	@Override
	public void finalize() {
		if (mDownCenterInstance != 0) {
			Log.d(TAG, "DownCenter is was destroyed yet before finalize()");
			destroy();
		}
	}

	/**
	 * Initialize the DownCenter class
	 */
	private void init() throws LibVlcException {
		Log.i(TAG, "Initializing DownCenter");
		if (!mIsInitialized) {
			mlibvlc = Util.getLibVlcInstance();
			mIsInitialized = true;
		}
	}


	public void setHandler(Handler handler) {
		// TODO Auto-generated method stub
		mHandler = handler;
		updateDownTaskNum(true);
	}
	
	public boolean isExsitJob(String uri) {
		for(DownTask downTask:mDownTaskList)
			if(downTask.getMovieUrl().equals(uri))
				return true;
		return false;
	}
	//是否所有文件已完成
	public boolean isAllDownTaskFinish() {
		for(DownTask downTask:mDownTaskList)
			if(downTask.getDownLoadInfo().getDownState()!=DownTask.TASK_FINISHED)
				return false;
		return true;
	}
	
	public void addJob(DownLoadInfo downInfo) {
		for(DownTask downTask : mDownTaskList)
			if(downTask.getMovieUrl().equals(downInfo.getDownUrl()))
			    return;
		DownTask downTask = new DownTask(downInfo);
        mDownTaskList.add(downTask);
        startDown(downTask);
	}
	/**
	 * 更新未下载数量标识
	 * */
	private void updateDownTaskNum(boolean isFirst) {
		if(mHandler==null||mDownTaskList==null)return;
			int num = 0;
			for (DownTask task : mDownTaskList) {
				if (task.getDownLoadInfo().getDownState() != DownTask.TASK_FINISHED) {
					num++;
				}
			}
			if(isFirst&&num==0)return;
			Message message=new Message();
        	message.what=PipiPlayerConstant.BadgeView;
        	message.obj=num;
        	mHandler.sendMessage(message);
	}
	
	/**
	 * 判断当前下载数是否超过下载限制数
	 * */
	private boolean canActivite() {
		updateDownTaskNum(false);
		if (mDownTaskList.size() < MAX_THREAD_NUMS+1) {
			return true;
		} else {
			int num = 0;
			for (DownTask task : mDownTaskList) {
				if (task.getDownLoadInfo().getDownState() == DownTask.TASK_DOWNLOADING
						||task.getDownLoadInfo().getDownState()==DownTask.TASK_FileMerge) {//合并完在开启下个下载
					
					num++;
					if (num >= MAX_THREAD_NUMS) {
						break;
					}
				}
			}
			if (num < MAX_THREAD_NUMS) {
				return true;
			} else {
				return false;
			}
		}
	}
	/**
	 * 暂停当前正在下载的任务
	 * */
	public void pauseLoadingTask(String urlstr) {
		updateDownTaskNum(false);
			for (DownTask task : mDownTaskList) {
				if (task.getDownLoadInfo().getDownState() == DownTask.TASK_DOWNLOADING) {
					if(!task.getDownLoadInfo().getDownUrl().equals(urlstr))//排除当前播放电影下载任务
						task.waiting();
				}
			}
	}
	
	
	/**
	 * 开启下个等待下载任务
	 * */
	private void startDown(DownTask task) {
		
		if (canActivite()) {
			task.getDownLoadInfo().setDownState(DownTask.TASK_DOWNLOADING);
			task.start();
		} else {
			task.getDownLoadInfo().setDownState(DownTask.TASK_WAITING_DOWNLOAD);
			//task.waiting();
		}
		
	}
	/**
	 * 自动开启任务判定的方法
	 * */
	public void autoDown() {
		for (DownTask temp : mDownTaskList) 
		{
			Boolean  canActive = canActivite();
			
			if(!canActive)return;//不存在将要下载的
			if (temp.getDownLoadInfo().getDownState() == DownTask.TASK_WAITING_DOWNLOAD )
	       {
				temp.getDownLoadInfo().setDownState(DownTask.TASK_DOWNLOADING);
				temp.start();
				break;
			}
			
		   if (canActive && temp.getDownLoadInfo().getDownState() == DownTask.TASK_RESUME_DOWNLOAD	)
		   {
			   temp.getDownLoadInfo().setDownState(DownTask.TASK_RESUME_DOWNLOAD);
				temp.resume();
				break;
		  }
	  }
	}
	
	/**
	 * 自动全部暂停
	 * */
	public void pauseAllTask() {
		for (DownTask temp : mDownTaskList) 
		{
			if (temp.getDownLoadInfo().getDownState() == DownTask.TASK_WAITING_DOWNLOAD 
					|| temp.getDownLoadInfo().getDownState() == DownTask.TASK_DOWNLOADING
					|| temp.getDownLoadInfo().getDownState() == DownTask.TASK_RESUME_DOWNLOAD)
	       {
				temp.pause();
			}
			
	  }
	}
	
	/**
	 * 网络引起全部暂停
	 * */
	public void pauseAllTaskByError() {
		for (DownTask temp : mDownTaskList) 
		{
			if (temp.getDownLoadInfo().getDownState() == DownTask.TASK_WAITING_DOWNLOAD 
					|| temp.getDownLoadInfo().getDownState() == DownTask.TASK_DOWNLOADING
					|| temp.getDownLoadInfo().getDownState() == DownTask.TASK_RESUME_DOWNLOAD)
	       {
				temp.pauseErr();
			}
			
	  }
	}
	
	/**
	 * 自动恢复下载状态
	 * */
	public void ResumeAllTask() {
		for (DownTask temp : mDownTaskList) 
		{
			if (temp.getDownLoadInfo().getDownState() == DownTask.TASK_WIFI_ERROR )
	       {
				temp.getDownLoadInfo().setDownState(DownTask.TASK_RESUME_DOWNLOAD);
				temp.waiting();
			}
	  }
		autoDown();
	}
	
	
	
	public void setPause(String url) {
		getTaskByUrl(url).getDownLoadInfo().setDownState(DownTask.TASK_PAUSE_DOWNLOAD);
		autoDown();
	}
	/**
	 * 根据url 在缓存中List<DownLoadTask> 获取想对应的DownTask
	 * */
	public DownTask getTaskByUrl(String url) {
		int count = mDownTaskList.size();
		DownTask task = null;
		for (int i = 0; i < count; i++) {
			String stempUrl = mDownTaskList.get(i).getMovieUrl();
			if (url.equals(stempUrl)) {
				task = mDownTaskList.get(i);
				break;
			}
		}
		return task;
	}
	
	public void stopUpdate() {
		for (DownTask task : mDownTaskList) {
			task.setHandler(null);
		}
	}
	
	public  void delDownTask(String uri) {
		// TODO Auto-generated method stub
		DownTask downTask =getTaskByUrl(uri);
		if (downTask==null) {
			return;
		}
		updateDownTaskNum(false);
		downTask.delete();
		mDownTaskList.remove(downTask);
	}
	
	public  void delDownTask(DownTask downTask) {
		// TODO Auto-generated method stub
		if(getPlayTaskUrl()!=null&&downTask.getMovieUrl().equals(getPlayTaskUrl()))return;//正在播放不能删
		updateDownTaskNum(false);
		downTask.delete();
	}
	
	public  void stopDownTasks(){
		
		for (DownTask downTask : mDownTaskList) {
		downTask.stop();
		}
	}

	public void pauseDownTask(String uri) {
		// TODO Auto-generated method stub
		DownTask downTask =getTaskByUrl(uri);
		downTask.getDownLoadInfo().setDownState(DownTask.TASK_PAUSE_DOWNLOAD);
		
	}
	public void pauseDownTasks() {
		for (DownTask downTask : mDownTaskList) {
			downTask.getDownLoadInfo().setDownState(DownTask.TASK_PAUSE_DOWNLOAD);
		}
	}
	
	public String GeneralPlayerTask(String filmHashID,String SavePath)
	{
			String ppfilmurl = mlibvlc.nativeGeneralPlayerTask(filmHashID, SavePath);
			return ppfilmurl;
	}
	
	public String GetLocalFileName(String filmHashID)
	{
		   String LocalFileName = mlibvlc.nativeGetLocalFileName(filmHashID);
		   return LocalFileName;
	}
	
	public String GetFileLoadPath(String filmHashID)
	{
		String Loadpath = mlibvlc.nativeGetFileLoadPath(filmHashID);
		return Loadpath;
	}
	
	public int GetCurFileSize(String filmuri)
	{
			int CurFileSize = mlibvlc.nativeGetCurFileSize(filmuri);
			return CurFileSize;
	}
	
	public String GetCurDownloadInfo(String pcsz_filmuri)
	{
		  String DownloadInfo = mlibvlc.nativeGetDownloadInfo(pcsz_filmuri);
		  return DownloadInfo;
	}
	
	public int PauseDownload(String filmuri)
	{
			int pausestatus = mlibvlc.nativePause(filmuri);
			return pausestatus;
	}
	
	public int ResumeDownload(String filmuri)
	{
		int ResumeStatus = mlibvlc.nativeResume(filmuri);
		return ResumeStatus;
	}
	
	public int DeleteDownload(String filmuri,boolean bComplete)
	{
		int DeleteStatus = mlibvlc.nativeDelete(filmuri, bComplete);
		return DeleteStatus;
	}
	public int DeleteFileCacheFromPPfilme(String filmHashID,String filecacheDir){
		return mlibvlc.nativeDeleteFileCacheFromPPfilme(filmHashID, filecacheDir);
	};


	
	protected  PlayTask playTask=null;

    public String   startPlayTask2(final String ppfilmURL)
    {
    	new Thread(new Runnable() {//删除缓存
			public void run() {
				// TODO Auto-generated method stub
				FileUtils.deleteMovieFile(ppfilmURL);
			}
		}).start();
    	
    	for (DownTask downTask : mDownTaskList) 
		{
			if (downTask.getMovieUrl().equals(ppfilmURL)) 
			{
				if (downTask.getDownLoadInfo().getDownState()==DownTask.TASK_FINISHED)
				{
					//下载完成 播放本地文件
					String localfilename = downTask.getDownLoadInfo().getDownPath();
					try {
						File file=new File(localfilename);
						if(file!=null&&file.exists()){
							//数据库存在，在检查文件是否被意外删除掉
								return "file://"+localfilename;
						}
						else 
							break;
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		}

		//销毁上一次播放下载任务
		if (playTask!=null)
			 playTask = null;
		
		//开始播放下载任务
		 playTask = new PlayTask(ppfilmURL);
		 playTask.start();
		 return playTask.getppfilmstr();
	 
    }
    
	  public PlayTask getPlayTask()
	  {
		return playTask;
	  }
	  	public int getPlayTaskSpeed()
	  	{
	  		if(playTask != null)
	  		{	
	  			return playTask.getspeed();
	  		}
	  		
	  		return 0;
	  	}
	  	public int getPlayTaskPercent()
	  	{
	  		if(playTask != null)
	  		{	
	  			return playTask.getPercent();
	  		}
	  		
	  		return 0;
	  	}
	  public String getPlayTaskUrl(){//判断当前下载的电影是否处于播放状态
		  if(playTask != null&&playTask.isPlaying())
	  		{	
	  			return playTask.getppUrl();
	  		}
		  return null;
	  }
	/**
	 * Destroy this libVLC instance
	 * 
	 * @note You must call it before exiting
	 */
	  	
	public void destroy() {
		try {
//			task.cancel();
			stopDownTasks();
			mlibvlc.nativeExitp2pSystem();
			mIsInitialized = false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void StopPlaytaskRead()
  	{
  		if(playTask != null)
  		{
  			mlibvlc.nativeSetStopCurReadTask(playTask.getppUrl(),1);
  			try {
  				Thread.sleep(500);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
  		}
  	}
	
	public void DestroyPlaytak()
  	{
  		if(playTask != null)
  		{
  			mlibvlc.nativeSetStopCurReadTask(playTask.getppUrl(),1);
  			try {
  				Thread.sleep(500);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
 
  			playTask.stop();
  			playTask = null;
  		}
  	}
	synchronized public void getDownInfo(DownLoadInfo downInfo) {
		try {
			String mediainfo = GetCurDownloadInfo(downInfo.getDownUrl());
			//int outspeed;
			// float outPercent;
			// long  outFileSize;		
			String tmp[]=mediainfo.split("&");
		    String speed=tmp[0];
		    String percent = tmp[1];
		    
		    downInfo.setDownSpeed(Integer.parseInt(speed));
		    downInfo.setDownProgress((int)Float.parseFloat(percent));
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
synchronized public void setDownInfo(DownLoadInfo downInfo) {
		try {
			String mediainfo=null;
			mediainfo = GetCurDownloadInfo(downInfo.getDownUrl());
			//int outspeed;
			// float outPercent;
			// long  outFileSize;		
			String tmp[]=mediainfo.split("&");
		    String speed=tmp[0];
		    String percent = tmp[1];
		    
		    downInfo.setDownSpeed(Integer.parseInt(speed));
		    downInfo.setDownProgress((int)Float.parseFloat(percent));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

//文件合并
@SuppressWarnings("resource")
synchronized public boolean GetFileMerge(final DownLoadInfo downInfo){
		String parent=HtmlUtils.getFileDir(downInfo);
		final List<String> list=new ArrayList<String>();
		File file=new File(parent);
		if(!file.exists())return true;
		//检索要合并的文件名，不合格的剔除
		 File[] childFiles = file.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().startsWith(downInfo.getDownName())&&!pathname.getName().endsWith(".ts");
				}
			});
		 if(childFiles!=null){
			//文件片段多于一个才需要合并
			 String filePath=null;
			 for (File file1 : childFiles) {
				 filePath=file1.getAbsolutePath();
				 list.add(filePath);
				}
			 if(list.size()==1){
				 DBHelperDao.getDBHelperDaoInstace().updataMovieStoreLocal(downInfo.getDownUrl(), filePath);
			 }else{
				 DBHelperDao.getDBHelperDaoInstace().updataMovieStoreState(downInfo.getDownUrl(),  DownTask.TASK_FileMerge);
				 downInfo.setDownState(DownTask.TASK_FileMerge);
				 String mp4filepath=null;
				 if(filePath.endsWith(".flv")){
					 mp4filepath=parent+"/"+downInfo.getDownName()+String.format("%03d", downInfo.getDownPosition())+".flv";
			  		}else{
			  		 mp4filepath=parent+"/"+downInfo.getDownName()+String.format("%03d", downInfo.getDownPosition())+".mp4";
			  		}
				 final String[] filearray = list.toArray(new String[list.size()]); 
				 Arrays.sort(filearray);//排序
				 try {
					 mlibvlc.nativeFileMerge(mp4filepath, filearray, false);
					 //GetFileMergeStart(list, mp4filepath);
					 //合并完文件在更新数据库信息
					 FileInputStream stream=new FileInputStream(mp4filepath);
					 if(stream!=null){
						long size= stream.available();
						downInfo.setDownTotalSize(size);
						downInfo.setDownLocal(downInfo.getDownUrl());
						DBHelperDao.getDBHelperDaoInstace().updataMovieStoreSize(downInfo.getDownUrl(), size);
						DBHelperDao.getDBHelperDaoInstace().updataMovieStoreLocal(downInfo.getDownUrl(), mp4filepath);
						downInfo.setDownState(DownTask.TASK_FINISHED);
						DBHelperDao.getDBHelperDaoInstace().updataMovieStoreState(downInfo.getDownUrl(),  DownTask.TASK_FINISHED);
						//合并完成 删除源文件
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								for(String fileName:list){
									FileUtils.deleteFile(fileName);
								}
							}
						}).start();
					 }
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					return false;
				}
			 }
		 }
		 return true;
	}


}
