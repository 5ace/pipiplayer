package cn.pipi.mobile.pipiplayer.service;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.asyctask.Download;
import cn.pipi.mobile.pipiplayer.beans.DownloadAPK;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.FileUtils;

/***
 * 更新版本
 * 
 * @author zhangjia
 * 
 */
public class UpdateService extends Service {
	public static final int DOWN_ERROR = -1;
	public static final int DOWN_START = 1;
	public static final int UPDATE_MESSAGE = 2;
	public static final int DOWN_OK = 3;
	public static final int DOWN_NOT_FOUND = 4;//网址无法识别
	private File downloadFile=null;
	MediaPlayer media;
	 Download down;
	 DownloadAPK downloadAPK;
	 NotificationManager  manager;
	 Notification notification;
	 PendingIntent pendingIntent;
	 VLCApplication appState;
	 boolean oldUrl;//默认不是老网址
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override
	public void onCreate() {
		manager = (NotificationManager) this
		        .getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification();
		
	    appState = (VLCApplication)this.getApplication(); 
		
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
			  // 创建一个Notification
	         if(intent!=null&&intent.getSerializableExtra("DownloadAPK")!=null)
			 downloadAPK= (DownloadAPK) intent.getSerializableExtra("DownloadAPK");
			if(downloadAPK!=null){
				StringBuffer app_name=new StringBuffer();
				app_name.append(getString(R.string.app_name));
				app_name.append(downloadAPK.getVersion().toString());
				app_name.append(".apk");
				
				downloadFile=FileUtils.createFile(app_name.toString());
				
				downloadAPK.setPath(downloadFile.getAbsolutePath());
				down= new Download(this,downloadAPK,handler,oldUrl);
				down.execute("");
		}
		//down.setProgressBar(progressBar);
		return super.onStartCommand(intent, flags, startId);
	}

	/***
	 * 更新UI
	 */
	 @SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_ERROR:
				//createNotification(DOWN_ERROR);
				if(notification!=null){
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					
					pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, notification.flags |= Notification.FLAG_AUTO_CANCEL);
					
					notification.setLatestEventInfo(UpdateService.this, "皮皮影视更新失败","请检查您的网络状况" , pendingIntent);
					manager.notify(0, notification);
				}
				stopSelf();
				break;
			case DOWN_START:
				appState.setServiceShowing(true);
				createNotification();
				break;
			case UPDATE_MESSAGE:
				int progress=(Integer) msg.obj;
				notification.setLatestEventInfo(UpdateService.this, "皮皮影视正在更新", progress+"%", pendingIntent);
				manager.notify(0, notification);
				break;
			case DOWN_OK:
				endNotification();
				stopSelf();
				break;
			case DOWN_NOT_FOUND://更换官方资源
				oldUrl=true;
			DataUtil.getToast(getString(R.string.DOWN_URL_ERR));
				if(downloadAPK!=null){
					StringBuffer app_name=new StringBuffer();
					app_name.append(getString(R.string.app_name));
					app_name.append(downloadAPK.getVersion().toString());
					app_name.append(".apk");
					
					downloadFile=FileUtils.createFile(app_name.toString());
					
					downloadAPK.setPath(downloadFile.getAbsolutePath());
					down= new Download(UpdateService.this,downloadAPK,handler,oldUrl);
					down.execute("");
			}
				break;
			default:
				stopSelf();
				break;
			}

		}

	};
	/***
	 * 创建通知栏,用户更新
	 */

	@SuppressWarnings("deprecation")
	public void createNotification() {
		        // 设置显示在手机最上边的状态栏的图标
		        notification.icon = R.drawable.logo;
		        // 当当前的notification被放到状态栏上的时候，提示内容
		        notification.tickerText = "皮皮影视版本升级";
		        //Intent.ACTION_VIEW
		        Intent intent = new Intent();
		//		intent.setDataAndType(uri,
		//				"application/vnd.android.package-archive");
		        notification.flags=Notification.FLAG_ONGOING_EVENT |Notification.FLAG_NO_CLEAR;
		         pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		        // 点击状态栏的图标出现的提示信息设置
		        notification.setLatestEventInfo(this, "皮皮影视新版本", "正在下载..", pendingIntent);
		    //    manager.notify(0, notification);
	}
	/***
	 * 安装提示
	 */

	@SuppressWarnings("deprecation")
	public void endNotification() {
		// 下载完成，点击安装
		    Uri uri = Uri.fromFile(downloadFile);
		        // 创建一个Notification
		        // 设置显示在手机最上边的状态栏的图标
		        notification.icon = R.drawable.logo;
		        // 当当前的notification被放到状态栏上的时候，提示内容
		        notification.tickerText = "皮皮影视新版本安装提示";
		        // 添加声音提示
		        notification.defaults=Notification.DEFAULT_SOUND;
		        // audioStreamType的值必须AudioManager中的值，代表着响铃的模式
		        notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;
		        
		        //Intent.ACTION_VIEW
		        Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");
		        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, notification.flags |= Notification.FLAG_AUTO_CANCEL);
		        // 点击状态栏的图标出现的提示信息设置
		        notification.setLatestEventInfo(this, "皮皮影视新版本", "下载完成,点击直接安装", pendingIntent);
		        manager.notify(0, notification);
		        
		        //跳转安装界面
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);
		        
	}
	
	
	public void onDestroy(){
		down.setIsstop(true);
		appState.setServiceShowing(false);
		super.onDestroy();
	}
}
