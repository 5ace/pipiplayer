package cn.pipi.mobile.pipiplayer.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoPlayerActivity;
import cn.pipi.mobile.pipiplayer.ui.Activity_Video;
import cn.pipi.mobile.pipiplayer.R;

public class DataUtil {
	//时间转换
public static  String DatetoString(){
	  Date currentTime = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String dateString = formatter.format(currentTime);
	   return dateString;
}
	//时间转换
public static  int DatetoInt(){
	   Date currentTime = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
	   String dateString = formatter.format(currentTime);
	   return Integer.parseInt(dateString);
}
//文件创建时间转换
public static  int DatetoInt(File file){
	   Date currentTime = new Date(file.lastModified());
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
	   String dateString = formatter.format(currentTime);
	   return Integer.parseInt(dateString);
}
//时间转换
public static  String IntoData(int data,String defaultstring){
	String datastring=String.valueOf(data);
	if(datastring==null||datastring.length()<8){
		return defaultstring;
	}
	StringBuffer string=new StringBuffer();
	string.append(datastring.substring(0, 4));
	string.append("-");
	string.append(datastring.substring(4, 6));
	string.append("-");
	string.append(datastring.substring(6, 8));
	datastring=null;
   return string.toString();
}


//生成toast
public static void getToast(String string){
	Toast.makeText(VLCApplication.getAppContext(), string, Toast.LENGTH_SHORT).show();
}

//生成toast
public static void getToast(int id){
	Toast.makeText(VLCApplication.getAppContext(), VLCApplication.getAppResources().getString(id), Toast.LENGTH_SHORT).show();
}

public static String getDevicesUUID(Context context) {

	TelephonyManager telManager = (TelephonyManager) context
			.getSystemService(Context.TELEPHONY_SERVICE);
	
	final String tmDevice, tmSerial, tmPhone, androidId;

	tmDevice = "" + telManager.getDeviceId();

	tmSerial = "" + telManager.getSimSerialNumber();

	androidId = ""
			+ android.provider.Settings.Secure.getString(
					context.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID);

	UUID deviceUuid = new UUID(androidId.hashCode(),
			((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

	String uniqueId = deviceUuid.toString();

	return uniqueId;
}
//检测是否为3G网络
public static boolean  Check3GNet(Context context)
{
	try {
		 ConnectivityManager manager = (ConnectivityManager)VLCApplication.getAppContext().getSystemService(
		            Context.CONNECTIVITY_SERVICE);
		    State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		    if(mobile == State.CONNECTED||mobile==State.CONNECTING){
		  	 return true;
		    }
	} catch (Exception e) {
		// TODO: handle exception
	}
    return false;
}

//检测是否为wifi网络
public static boolean  CheckWIFINet(Context context)
{
	try {
		 ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
		          Context.CONNECTIVITY_SERVICE);
		  State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		  if(wifi == State.CONNECTED||wifi==State.CONNECTING){
			 return true;
		  }
	} catch (Exception e) {
		// TODO: handle exception
	}
  return false;
}
  
  private static void showTips(final Context context)//无网提示
  {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setIcon(android.R.drawable.ic_dialog_alert);
      builder.setTitle(R.string.NONETWORK);
      builder.setPositiveButton(R.string.setnetwork, new DialogInterface.OnClickListener() {
       //   @Override
          public void onClick(DialogInterface dialog, int which) {
              // 如果没有网络连接，则进入网络设置界面
        	  context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
          }
      });
      builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
          
        //  @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
          }
      });
      builder.create();
      builder.show();
  }
  
  public static  void show3GTipsToPlay(final Activity context,final DownLoadInfo  downInfo)//GPRS播放提示
  {
   		AlertDialog.Builder alert = new AlertDialog.Builder(context);
   		alert.setTitle(R.string.ISGPRS);
   		alert.setMessage(R.string.allowGPRSplay);
   		alert.setPositiveButton(R.string.ok,
   						new DialogInterface.OnClickListener() {
   							public void onClick(DialogInterface dialog,
   									int which) {
   								Activity_Video.start(context, downInfo);
   							}
   						})
   				.setNegativeButton(R.string.cancel,
   						new DialogInterface.OnClickListener() {
   							public void onClick(DialogInterface dialog,
   									int which) {
   								dialog.dismiss();
   							}
   						});
   		alert.create().show();
  }
  public static  void show3GTipsToDown(final Context context,final DownLoadInfo  downInfo)//GPRS下载提示
  {
   		AlertDialog.Builder alert = new AlertDialog.Builder(context);
   		alert.setTitle(R.string.ISGPRS);
   		alert.setMessage(R.string.allowGPRSdown);
   		alert.setPositiveButton(R.string.ok,
   						new DialogInterface.OnClickListener() {
   							public void onClick(DialogInterface dialog,
   									int which) {
   								DBHelperDao.getDBHelperDaoInstace().insertMovieStore( downInfo);
		                		DownCenter.getExistingInstance().addJob(downInfo);
		                		getToast(R.string.adddowntask);
   							}
   						})
   				.setNegativeButton(R.string.cancel,
   						new DialogInterface.OnClickListener() {
   							public void onClick(DialogInterface dialog,
   									int which) {
   								dialog.dismiss();
   							}
   						});
   		alert.create().show();
  }
  
  /**
	 * 检测邮箱地址是否合法
	 * 
	 * @param email
	 * @return true合法 false不合法
	 */
	public static boolean isAvailableEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		// Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
		Pattern p = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	/**
	 * 是否是合法的手机号码格式
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isTelePhone(String mobiles) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);

		System.out.println(m.matches() + "---");
		return m.matches();
	}
}
