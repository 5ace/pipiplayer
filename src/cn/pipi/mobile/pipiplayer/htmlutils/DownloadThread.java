package cn.pipi.mobile.pipiplayer.htmlutils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.Thread.State;
import java.net.URL;
import java.net.URLConnection;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;


import android.text.TextUtils;
import android.util.Log;
/**
 * 下载模块
 * @author hellogv
 * 
 */
public class DownloadThread implements Runnable {
	static private final String TAG="TAG999";
	private String mUrl;
	private String mPath;

	private long mDownloadSize=0;
	private long mTotalSize;
	private boolean mStop;
	private boolean mStarted;
	private boolean mError;
	private DownLoadInfo downInfo;
	private Thread mThread = null;
	public DownloadThread(DownLoadInfo downinfo) {
		this.downInfo=downinfo;
		mStop = false;
		mStarted = false;
		mError=false;
	}

	@Override
	public void run() {
		if(TextUtils.isEmpty(mUrl)){
			mUrl = HtmlUtils.getRedirectUrl(downInfo.getTaskList().get(downInfo.getDownIndex()));
		}
		if(TextUtils.isEmpty(mPath)){
			mPath=HtmlUtils.urlToFileName(downInfo,mUrl);
		}
		if(mDownloadSize==0){
			File fis=null;
			try {
				fis = new File(mPath);
				if(fis!=null&&fis.exists()){
					mDownloadSize=fis.length();
				}
				downInfo.setDownCompleteSize(mDownloadSize);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mTotalSize=downInfo.getDownTotalSize();
		if(mDownloadSize==0||mDownloadSize<mTotalSize){//未开始下或者未下完
			download();
		}
	}
	
	/** 启动下载线程 */
	public void start() {
		if (!mStarted) {
			if (mThread == null || mThread.getState() == State.TERMINATED) {
				mThread = new Thread(this);
				mThread.start();
			}
			// 只能启动一次
			mStarted = true;
		}
	}
	 public void resume() {
		 try {
			 mStop = false;
			 if (mThread == null || mThread.getState() == State.TERMINATED) {
					mThread = new Thread(this);
					mThread.start();
			            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	 }
	 public void pause() {//暂停
		 mStop = true;
		 if (mThread != null && mThread.getState() != State.TERMINATED) {
				mThread.interrupt();
			}
	 }
	/** 停止下载线程, deleteFile是否要删除临时文件 */
	public void stop() {
		mStop = true;
		if (mThread != null && mThread.getState() != State.TERMINATED) {
			mThread.interrupt();
		}
	}

	/**
	 * 是否下载异常
	 * @return
	 */
	public boolean isError(){
		return mError;
	}
	
	public long getDownloadedSize() {
		return mDownloadSize;
	}

	/** 是否下载成功 */
	public synchronized boolean isDownloadSuccessed() {
		return (mDownloadSize != 0 && mDownloadSize == mTotalSize);
	}

	private synchronized void download() {
		InputStream is = null;
		URLConnection con=null;
		if (mStop) {
			return;
		}
		try {
			URL url = new URL(mUrl);
			con = url.openConnection();
			con.setRequestProperty("Connection", "Keep-Alive");
			if(mDownloadSize>0&&mDownloadSize<mTotalSize){
				con.setRequestProperty("Range","bytes="+ mDownloadSize + "-"+ mTotalSize);
			}else{
				mTotalSize = con.getContentLength();
				DBHelperDao.getDBHelperDaoInstace().updataMovieStoreSize(downInfo.getDownUrl(),mTotalSize);
				downInfo.setDownTotalSize(mTotalSize);
			}
			is = con.getInputStream();
			int len = 0;
			byte[] bs = new byte[1024];
			if (mStop) {
				downInfo.setDownState(DownTask.TASK_PAUSE_DOWNLOAD);
				return;
			}
			RandomAccessFile rasf = new RandomAccessFile(mPath, "rwd");
			while (!mStop //未强制停止
					&& ((len = is.read(bs)) != -1)) {//未全部读取
					rasf.seek(mDownloadSize);
					rasf.write(bs, 0, len);
					mDownloadSize += len;
			}
			stop();
		} catch (Exception e) {
			mError=true;
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e){e.printStackTrace();}
			}
		}
	}
}
