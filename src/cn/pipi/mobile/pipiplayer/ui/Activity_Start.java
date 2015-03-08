package cn.pipi.mobile.pipiplayer.ui;

import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcUtil;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.CompatErrorActivity;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.FileUtils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class Activity_Start extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示标�?
        super.setContentView( R.layout.activity_start );
		try {
			if(!FileUtils.makeAppCacheDir()){
				new AlertDialog.Builder(this).setTitle("找不到存储空间")
				.setMessage("").setPositiveButton(getString(R.string.sdRemoved), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						System.exit(0);
					}
				}).show();
			}else{
				//初始化数�?
				PipiPlayerConstant.getInstance();
				loadJni();
				DownCenter.getInstance();
				FileUtils.clearCache(false);//清除下载目录中垃圾文�?
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						newScreen();
					}
				}, 1000);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			//System.exit(1);
		}
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
    }

    
    //创建并显示新屏幕
    //调用方式newScreen( 类名.Class )，不返回任何结果
    public void newScreen()
    {
       // m_timer.stop();
        Intent screenParam = new Intent();
        screenParam.setClass( this, Activity_Main.class ); //设置�?��转换的Activity
        screenParam.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ); //  避免闪屏现象
        startActivity( screenParam );  //�?��新的Activity
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
		
		 @Override
		    protected void onResume() {
		            // TODO Auto-generated method stub
		            super.onResume();
		            MobclickAgent.onResume(this); 
		    }
		    @Override
		    protected void onPause() {
		            // TODO Auto-generated method stub
		            super.onPause();
		            MobclickAgent.onPause(this);
		    }  
		    
		   
		    private void loadJni() {
				try {
					System.loadLibrary("TransmitLayer");
				} catch (UnsatisfiedLinkError ule) {
					// / FIXME Alert user
					System.exit(1);
				} catch (SecurityException se) {
					// / FIXME Alert user
					System.exit(1);
				}
              try {
					 System.loadLibrary("FileSession");
			  } catch (UnsatisfiedLinkError ule) {     
			         /// FIXME Alert user
			         System.exit(1);
			      } catch (SecurityException se) {
			            /// FIXME Alert user
			         System.exit(1);
			        }
              if (!LibVlcUtil.hasCompatibleCPU(this)) {
                  Intent i = new Intent(this, CompatErrorActivity.class);
                  startActivity(i);
                  finish();
              }
			}
		    
			
}
