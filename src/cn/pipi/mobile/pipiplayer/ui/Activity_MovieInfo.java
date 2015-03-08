package cn.pipi.mobile.pipiplayer.ui;


import java.net.URLDecoder;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.PageFragmentAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.Download;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.MainLocalActivity;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;
import cn.pipi.mobile.pipiplayer.util.MD5Util;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;

public class Activity_MovieInfo extends SherlockFragmentActivity implements OnPageChangeListener,OnCheckedChangeListener{
	private ViewPager vPager;
	private RadioGroup bar_radio;
	private MovieInfo m_info;
	private ProgressBar progerssbar;
	private PageFragmentAdapter adapter;
	private UMSocialService mController ;
	private ImageView bt_play_start;
	private Fragment_VideoPlayer f;
	private boolean fromOther;//来自浏览器等其他程序打开的页面，则返回到主页
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movieinfo);
		progerssbar=(ProgressBar)findViewById(R.id.progerssBar);
		bt_play_start=(ImageView) findViewById(R.id.bt_play_start);
		bt_play_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(m_info.getPlayList()==null)return;
				try {
					int position = DBHelperDao.getDBHelperDaoInstace().getHistroyPositionByID(m_info.getId());
					ArrayList<String> list = m_info.getPlayList().get(m_info.getTag());
					final DownLoadInfo  downInfo = new DownLoadInfo(m_info.getId(),m_info.getName(),
							m_info.getImg(),list.get(position),position,list);
					downInfo.setDownTag(m_info.getTag());
					if(MD5Util.getCheckPiPiDownLoad(m_info.getTag())){//该资源来自皮皮网
					//	插入数据库，播放历史
						if(DataUtil.Check3GNet(Activity_MovieInfo.this)){
							Log.i("TAG999", "Check3GNet  true");
							AlertDialog.Builder alert = new AlertDialog.Builder(Activity_MovieInfo.this);
					   		alert.setTitle(R.string.ISGPRS);
					   		alert.setMessage(R.string.allowGPRSplay);
					   		alert.setPositiveButton(R.string.ok,
					   						new DialogInterface.OnClickListener() {
					   							public void onClick(DialogInterface dialog,
					   									int which) {
					   								playVideo(downInfo);
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
						}else{
							Log.i("TAG999", "Check3GNet  false");
							playVideo(downInfo);
						}
						
					}else{
						if(downInfo.getTaskList()!=null){
							playVideo(downInfo);
						}else{
							 Intent screenParam = new Intent( Activity_MovieInfo.this, HtmlPlayer.class );
						        screenParam.putExtra("url", downInfo.getDownUrl());
						        screenParam.putExtra("tag", downInfo.getDownTag());
						        startActivity( screenParam );  //�?��新的Activity
						        DBHelperDao.getDBHelperDaoInstace().insertMovieHistroy(downInfo);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				if(bt_play_start.getVisibility()==0)bt_play_start.setVisibility(8);
			}
		});
		try {
			fromOther = getIntent().getAction() != null
		            && getIntent().getAction().equals(Intent.ACTION_VIEW);
			if (fromOther) {
				m_info = new MovieInfo();
				m_info.setId(getIntent().getData().getQueryParameter("id"));
				m_info.setName(URLDecoder.decode(getIntent().getData().getQueryParameter("name")));
		        }else{
		        m_info = (MovieInfo) getIntent().getSerializableExtra("movieInfo");
		        }
			prepareActionBar();
			bar_radio=(RadioGroup)findViewById(R.id.bar_radio);
			bar_radio.setOnCheckedChangeListener(this);
			initViewPager();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
			if(preference.getBoolean("first_movieinfo",true)){
			preference.edit().putBoolean("first_movieinfo", false).commit();
			findViewById(R.id.control_dianying_layout).setVisibility(0);
			findViewById(R.id.control_dianying_layout).setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					v.setVisibility(8);
					return true;
				}
			});
					 };
		
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(m_info.getName());
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
            // TODO Auto-generated method stub
            super.onDestroy();
            if(fromOther){
            	startActivity(new Intent(this,Activity_Main.class));
            }
    }  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.movieinfo, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
       	          finish();
       	     break;
         case R.id.menu_save:
        	 if(DBHelperDao.getDBHelperDaoInstace().isMovieSaveByID(m_info.getId())){//已经收藏了则取消收藏
					DataUtil.getToast(R.string.removesave);
					DBHelperDao.getDBHelperDaoInstace().delSingleSmovieSave(m_info.getId());
				}else{
        	 DownLoadInfo info=new DownLoadInfo(m_info.getId(), m_info.getName(), m_info.getImg(), null);
        	 DBHelperDao.getDBHelperDaoInstace().insertMovieSave(info);
        	 DataUtil.getToast(R.string.addsave);
				}
        	 break;
         case R.id.menu_share:
        	 if(mController==null){
					mController = UMServiceFactory.getUMSocialService("com.umeng.share",RequestType.SOCIAL);
				    String url="http://www.pipi.cn/mvshare.html?id="+m_info.getId();
					mController.getConfig().setSsoHandler(new SinaSsoHandler());
					mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
					mController.getConfig().supportQQPlatform(Activity_MovieInfo.this, url);
					mController.getConfig().supportWXPlatform(Activity_MovieInfo.this, "wx0756f6331a55cdff",url);
					mController.getConfig().supportWXCirclePlatform(Activity_MovieInfo.this, "wx0756f6331a55cdff",url);
					mController.setAppWebSite(SHARE_MEDIA.RENREN, url);
					mController.setAppWebSite(SHARE_MEDIA.QZONE, url);
					mController.getConfig().removePlatform(SHARE_MEDIA.SMS,SHARE_MEDIA.DOUBAN);
					// 设置分享内容
					// 设置分享视频
					UMVideo umVedio = new UMVideo(url);
					// 设置视频缩略图
					umVedio.setThumb(m_info.getImg());
					umVedio.setTitle(m_info.getName());
					mController.setShareMedia(umVedio);
				}
			mController.setShareContent(m_info.getDesc());
			mController.openShare(Activity_MovieInfo.this, false);
        	 break;
         //case R.id.menu_download:
        //	 startActivity(new Intent(this,Activity_DownLoad.class));
        //	 break;
        }
        return super.onOptionsItemSelected(item);
	}
	private void initViewPager(){
		vPager = (ViewPager)findViewById(R.id.vPager);
		adapter = new PageFragmentAdapter(getSupportFragmentManager());
        vPager.setAdapter(adapter);
        vPager.setCurrentItem(0);  
        vPager.setOnPageChangeListener(this); 
        vPager.setOffscreenPageLimit(5);  
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		//vPager.getAdapter().get
		RadioButton bt = (RadioButton) bar_radio.getChildAt(arg0);
		bt.setChecked(true);
	}
	
	public MovieInfo getMovieInfo(){
		return m_info;
	}
	public void setProgressBar(boolean visible){
		if(visible)
		progerssbar.setVisibility(0);
		else
		progerssbar.setVisibility(8);
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		group.check(checkedId);
		switch (checkedId) {
		case R.id.tv_guid1:
			vPager.setCurrentItem(0);
			break;
        case R.id.tv_guid2:
			vPager.setCurrentItem(1);
			break;
        case R.id.tv_guid3:
			vPager.setCurrentItem(2);
			break;
        case R.id.tv_guid4:
			vPager.setCurrentItem(3);
	        break;
        case R.id.tv_guid5:
			vPager.setCurrentItem(4);
	        break;

		default:
			break;
		}
	}
	
      public void playVideo(DownLoadInfo  downInfo){
    	Log.i("TAG999", "playVideo");
    	if(downInfo == null)return;
    	FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
    	f=new Fragment_VideoPlayer();
    	Bundle b=new Bundle();
    	b.putSerializable("downInfo", downInfo);
    	b.putSerializable("movieInfo", m_info);//用来切换清晰度
    	f.setArguments(b);
  		t.replace(R.id.frame_player, f);
  		t.commit();
  		if(bt_play_start.getVisibility()==0)bt_play_start.setVisibility(8);
	  }

      public void setFullScreen(boolean full){
    	  //强制横竖屏
    	  boolean isLandsCape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    	  if(full&&!isLandsCape){
    		  //全屏
    		  getSupportActionBar().hide();
    		  setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
    		  getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
    	              WindowManager.LayoutParams. FLAG_FULLSCREEN);//全屏
    		  findViewById(R.id.ll_viewPager).setVisibility(8);
    	  }else if(!full&&isLandsCape){
    		  getSupportActionBar().show();
    		  setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
    		  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    		  findViewById(R.id.ll_viewPager).setVisibility(0);
    	  }
      }
      
      @Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  		if (keyCode == KeyEvent.KEYCODE_BACK) {
  			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
  				if(f!=null&&f.isAdded()){
  					f.setViewHide(false);
  				}
  				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						setFullScreen(false);
					}
				},200);
  				return true;
  			}
  		}
  		return super.onKeyDown(keyCode, event);
  	}
      
      @Override 
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        if(mController==null){
	        	 /**使用SSO授权必须添加如下代码 */
		        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
		        if(ssoHandler != null){
		           ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		        }
	        }
	    }
      
}
