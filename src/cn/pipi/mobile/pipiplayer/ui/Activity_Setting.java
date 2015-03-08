package cn.pipi.mobile.pipiplayer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.asyctask.GetVersionAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.DownloadAPK;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;
import cn.pipi.mobile.pipiplayer.service.UpdateService;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_Setting extends SherlockFragmentActivity implements OnClickListener{
	private Context mContext;
    SharedPreferences sharedPreferences=null;
    private final int CLEARCACHE=0;
    private final int SING_CHOICE_DIALOG = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		mContext=this;
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
		prepareActionBar();
		
		((RelativeLayout) findViewById(R.id.layout1)).setOnClickListener(this);
	       ((RelativeLayout) findViewById(R.id.layout2)).setOnClickListener(this);
	       ((RelativeLayout) findViewById(R.id.layout3)).setOnClickListener(this);
	       ((RelativeLayout) findViewById(R.id.layout8)).setOnClickListener(this);
	       ((RelativeLayout) findViewById(R.id.layout9)).setOnClickListener(this);
	       
	       ImageView imageview1=(ImageView) findViewById(R.id.ImageView4);
	       imageview1.setOnClickListener(this);
	       ImageView imageview2=(ImageView) findViewById(R.id.ImageView5);
	       imageview2.setOnClickListener(this);
	       ImageView imageview3=(ImageView) findViewById(R.id.ImageView6);
	       imageview3.setOnClickListener(this);
	       
	       if(!PipiPlayerConstant.getInstance().playnext)imageview1.setBackgroundResource(R.drawable.bt_setting_off);
	       if(!PipiPlayerConstant.getInstance().playfromhistroy)imageview2.setBackgroundResource(R.drawable.bt_setting_off);
	       if(!PipiPlayerConstant.getInstance().allowdown)imageview3.setBackgroundResource(R.drawable.bt_setting_off);
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(R.string.set);
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
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
       	          finish();
       	     break;
        }
        return super.onOptionsItemSelected(item);
	}
	
	private Handler handler = new Handler() {
		//@Override
		public void handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case CLEARCACHE:
				DataUtil.getToast(getString(R.string.cleanCachefinish));
				break;
			case PipiPlayerConstant.EXEC_NORMOL:
				break;
			case PipiPlayerConstant.NO_DATA_RETURN:
				DataUtil.getToast(getString(R.string.NO_DATA_RETURN));
				break;
			case PipiPlayerConstant.NONETWORK:
				DataUtil.getToast(getString(R.string.NONETWORK));
				break;
			case PipiPlayerConstant.HTTP_IOEXCEPTION:
				DataUtil.getToast(getString(R.string.HTTP_IOEXCEPTION));
				break;
			case PipiPlayerConstant.UPDATE_MINVER://强制升级
				DownLoadAPK((DownloadAPK)message.obj);
				break;
			case PipiPlayerConstant.UPDATE_NEWVER://提示升级
				DownLoadAPKDialog((DownloadAPK)message.obj);
				break;
			case PipiPlayerConstant.UPDATE_NO://不用升级时,删除源文件
				DataUtil.getToast(getString(R.string.OLDVERSIOM));
				break;
			default:
				break;
			}
			super.handleMessage(message);
		}

	};
	
	 private void DownLoadAPK(DownloadAPK down){
	    	   if(down==null||down.getUrl()==null)return;
	    		//开始更新版本
	    		Intent updateIntent = new Intent(this,UpdateService.class);
	    		updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK ) ;
	    		updateIntent.putExtra("DownloadAPK", down);
	    		startService(updateIntent);
	    	
	    }
	    private void DownLoadAPKDialog(final DownloadAPK down){
			// 发现新版本，提示用户更新
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("软件升级")
					.setMessage("发现新版本,建议立即更新使用")
					.setPositiveButton("更新",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									DataUtil.getToast("正在后台升级新版本...");
									DownLoadAPK(down);
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			alert.create().show();
			
	    }
	    
	    
	    @Override  
	    protected Dialog onCreateDialog(int id) {  
	        Dialog dialog = null;  
	        switch(id) {  
	            case SING_CHOICE_DIALOG: 
	            	//获取路径
	            	final String[] list=Util.getStorageDirectories();
	            	String[] listName=new String[list.length];
	            	//检索本身选择的第几个
	            	int which=0;
	            	for(int i=0;i<list.length;i++){
	            		if(list[i].equals(sharedPreferences.getString("pipicache", "")))
	            			which=i;
	            		StringBuffer name=new StringBuffer();
	            		if(list[i].contains(Environment.getExternalStorageDirectory().getPath())){//内置卡
	            			name.append("内置卡(剩");
	            		}else{
	            			name.append("外置卡(剩");
	            		}
	            		name.append(SdcardUtil.formatSize(mContext, SdcardUtil.getAvailableStore(list[i])));
	            		name.append("/共");
	            		name.append(SdcardUtil.formatSize(mContext, SdcardUtil.getSdCardTotalStore(list[i])));
	            		name.append(")");
	            		listName[i]=name.toString();
	            	}
	                Builder builder = new AlertDialog.Builder(this); 
	                builder.setTitle("请选择存储空间");
	                builder.setSingleChoiceItems(listName, which, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							sharedPreferences.edit().putString("pipicache", list[which]).commit();//设置路径，创建目录
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									//防止
									try {
										FileUtils.makeAppCacheDir();
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							}).start();
	                         dialog.dismiss();
						}
					});  
	                builder.setNegativeButton(getString(R.string.cancel), null);
	                dialog = builder.create();  
	                break;  
	        }  
	        return dialog;  
	    }
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId()==R.id.layout1){
				//getNextPage(1);
				Intent screenParam = new Intent();
				screenParam.setClass( this, Activity_Advice.class );
				startActivity(screenParam);
			}else if(v.getId()==R.id.layout2){
				Intent screenParam= new Intent();
				screenParam.setClass(this, Activity_About.class);
				startActivity(screenParam);
			}else if(v.getId()==R.id.layout3){//查看新版本
				 new GetVersionAsyncTask(mContext,handler).execute("");
			}else if(v.getId()==R.id.layout8){//清除缓存
				new Thread(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						try {
							FileUtils.clearCache(true);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						handler.sendEmptyMessage(CLEARCACHE);
					}
				}).start();
			}else if(v.getId()==R.id.layout9){//设置路径
				if(DownCenter.getExistingInstance().isAllDownTaskFinish()){
					showDialog(SING_CHOICE_DIALOG);
				}else{
					DataUtil.getToast(getString(R.string.nottaskallfinish));
				}
			}else if(v.getId()==R.id.ImageView4){//自动播放下一集
				PipiPlayerConstant.getInstance().playnext=!PipiPlayerConstant.getInstance().playnext;
				v.setBackgroundResource(PipiPlayerConstant.getInstance().playnext?R.drawable.bt_setting_on:R.drawable.bt_setting_off);
				sharedPreferences.edit().putBoolean("playnext", PipiPlayerConstant.getInstance().playnext).commit();
			}else if(v.getId()==R.id.ImageView5){//从历史播放
				PipiPlayerConstant.getInstance().playfromhistroy=!PipiPlayerConstant.getInstance().playfromhistroy;
				v.setBackgroundResource(PipiPlayerConstant.getInstance().playfromhistroy?R.drawable.bt_setting_on:R.drawable.bt_setting_off);
				sharedPreferences.edit().putBoolean("playfromhistroy", PipiPlayerConstant.getInstance().playfromhistroy).commit();
			}else if(v.getId()==R.id.ImageView6){//允许gprs下载
				PipiPlayerConstant.getInstance().allowdown=!PipiPlayerConstant.getInstance().allowdown;
				v.setBackgroundResource(PipiPlayerConstant.getInstance().allowdown?R.drawable.bt_setting_on:R.drawable.bt_setting_off);
				sharedPreferences.edit().putBoolean("allowdown", PipiPlayerConstant.getInstance().allowdown).commit();
				if(PipiPlayerConstant.getInstance().allowdown){//开启3G下载任务
            		try {
						DownCenter.getInstance().ResumeAllTask();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}else{//关闭3G下载任务
            		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState(); 
                    if(state == State.CONNECTED||state == State.CONNECTING) {
                    		try {
        						DownCenter.getInstance().pauseAllTaskByError();
        					} catch (Exception e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
                    	}
                    	return ;
                    }
            	}
			}
			
		}  
	      
	
}
