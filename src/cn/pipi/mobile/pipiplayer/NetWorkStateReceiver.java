package cn.pipi.mobile.pipiplayer;


import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetWorkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager)      
            		context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
            	 State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState(); 
                 if(state == State.CONNECTED||state == State.CONNECTING) {
                 	DataUtil.getToast("切换为GPRS手机网络");
                 	if(!PipiPlayerConstant.getInstance().allowdown){
                 		try {
     						DownCenter.getInstance().pauseAllTaskByError();
     					} catch (Exception e) {
     						// TODO Auto-generated catch block
     						e.printStackTrace();
     					}
                 	}
                 	return ;
                 }
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

            NetworkInfo info = connectivityManager.getActiveNetworkInfo(); 
            if(info!=null&&info.isConnected()){
            	DataUtil.getToast("切换为WIFI网络");
            	try {
            		DownCenter.getInstance().ResumeAllTask();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }else{
            	DataUtil.getToast("没有可用网络");
            	try {
            		DownCenter.getInstance().pauseAllTaskByError();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
        }

}
