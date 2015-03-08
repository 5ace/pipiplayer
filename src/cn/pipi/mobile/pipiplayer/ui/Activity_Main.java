package cn.pipi.mobile.pipiplayer.ui;

import java.util.Map;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.NetWorkStateReceiver;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.beans.TypeInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.slidingmenu.lib.SlidingMenu;
import cn.pipi.slidingmenu.lib.app.SlidingFragmentActivity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_Main extends SlidingFragmentActivity {
	private SlidingMenu mSlidingMenu;
	private Map<String, TypeInfo> map;
	private Handler handler = new Handler();
	NetWorkStateReceiver mBroadcast;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.center_frame);
		setBehindContentView(R.layout.left_frame);
		changeMenuOffset();
		prepareActionBar();
		 try {
				DownCenter.getInstance().setHandler(handler);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 registerAReceiver();//注册网络广播
	}

	private void registerAReceiver(){
		mBroadcast=new NetWorkStateReceiver();
        //为广播接收者设置IntentFilter
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mBroadcast, intentFilter);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
         case android.R.id.home:
        	 toggle();
        	 break;
         case R.id.menu_search:
     		 startActivity(new Intent(this,Activity_Search.class));
        	 break;
         case R.id.menu_histroy:
        	 MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "历史");
     		 startActivity(new Intent(this,Activity_Histroy.class));
        	 break;
         case R.id.menu_download:
        	 MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "下载");
        	 startActivity(new Intent(this,Activity_DownLoad.class));
        	 break;
        }
        return super.onOptionsItemSelected(item);
	}
    private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        PipiPlayerConstant.ActionBarHeight = mActionBar.getHeight();
    }
	
	private void changeMenuOffset() {
		if (mSlidingMenu == null) {
			// 根据屏幕大小 按比例计算 图片的高度
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			mSlidingMenu = getSlidingMenu();
			mSlidingMenu.setSecondaryMenu(R.layout.right_frame);
			mSlidingMenu.setMode(SlidingMenu.LEFT);
			// 设置要使菜单滑动，触碰屏幕的范围
			mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// 设置阴影宽度
			mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
			// 设置左菜单阴影图片
			mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
			//mSlidingMenu.setSecondaryShadowDrawable(R.drawable.slidingmenu_shadow);
			// mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			// 设置菜单占屏幕的比例
			mSlidingMenu.setBehindOffset(getWindowManager().getDefaultDisplay().getWidth() / 4);
			// 设置滑动时拖拽效果
			mSlidingMenu.setBehindScrollScale(0);
			mSlidingMenu.setFadeDegree(0.35f);
			
		}
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		
		t.replace(R.id.left_frame, new Fragment_Left());
		t.replace(R.id.center_frame, new Fragment_Shouye());
		//t.replace(R.id.right_frame, new Fragment_Right());
		t.commit();
		
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
    protected void onDestroy() {
            super.onDestroy();
            if (mBroadcast!=null) {
                unregisterReceiver(mBroadcast);
            }
    }
	
	public SlidingMenu getSlideMenu() {
		return mSlidingMenu;
	}
	
	public Map<String, TypeInfo> getMap() {
		return map;
	}

	public void setMap(Map<String, TypeInfo> map) {
		this.map = map;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK&&!mSlidingMenu.isMenuShowing()) {
			ExitApp();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    private long exitTime = 0;

    
    public void ExitApp()
    {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                    Toast.makeText(this, getString(R.string.exit_click), Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
            } else
            {
            	VLCApplication appState = (VLCApplication)this.getApplication();
            	if(appState.isServiceShowing){//如果正在更新版本，暂时隐藏后台
            		Intent home  =	new Intent(Intent.ACTION_MAIN);  
            	    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
            	    home.addCategory(Intent.CATEGORY_HOME);  
            	    startActivity(home);  
            	}else{
            		System.exit(0);//完美退出
            	}
            }

    }
	
}
