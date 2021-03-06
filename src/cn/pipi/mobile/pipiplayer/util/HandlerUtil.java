package cn.pipi.mobile.pipiplayer.util;

import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HandlerUtil {
	/**
	 * 发送 消息到主界面
	 * 
	 * @param handler
	 * @param type
	 */
	public static void sendMsgToHandler(Handler handler, int type) {
		if (handler != null) {
			Message message = handler.obtainMessage();
			message.what = type;
			handler.sendMessage(message);
		} else {
			System.out.println("handler--->等于null");
		}
	}
	/**
	 * 发送 消息到主界面
	 * 
	 * @param handler
	 * @param type
	 * @param object
	 */
	public static void sendMsgToHandler(Handler handler, int type, Object object) {
		if (handler != null) {
			Message message = handler.obtainMessage();
			message.what = type;
			message.obj = object;
			handler.sendMessage(message);
		} else {
			System.out.println("handler--->等于null");
		}
	}
	
	/**
	 * 发送 消息到主界面
	 * 
	 * @param handler
	 * @param type
	 * @param object
	 */
	public static void sendMsgToHandler(Handler handler, int type,int arg1, Object object) {
		if (handler != null) {
			Message message = handler.obtainMessage();
			message.what = type;
			message.arg1 = arg1;
			message.obj = object;
			handler.sendMessage(message);
		} else {
			System.out.println("handler--->等于null");
		}
	}
	
	/**
	 * 延迟发送 消息到主界面
	 * 
	 * @param handler
	 * @param type
	 */
	public static void sendMsgToHandlerDelay(Handler handler, int type,
			Object object,int time) {
		if (handler != null) {
			// handler.sendEmptyMessage(type);
			Message message = handler.obtainMessage();
			message.what = type;
			message.obj = object;
			handler.sendMessageDelayed(message, time);
		} else {
			System.out.println("handler--->等于null");
		}
	}
	
	public static boolean isConnect() {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) VLCApplication.getAppContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
}
