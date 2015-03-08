package cn.pipi.mobile.pipiplayer.ui;


import com.actionbarsherlock.app.SherlockFragment;
import com.umeng.analytics.MobclickAgent;
import com.umeng.newxp.common.ExchangeConstants;
import com.umeng.newxp.controller.ExchangeDataService;
import com.umeng.newxp.view.ExchangeViewManager;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.MainLocalActivity;
import cn.pipi.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class Fragment_Left extends SherlockFragment implements OnClickListener{
	private View view ,bt_default ;
	private FragmentManager fm;
	private int[] res = new int[]{
			R.drawable.bt_home_1,R.drawable.bt_dianying_1,R.drawable.bt_dianshiju_1,
			R.drawable.bt_zongyi_1,R.drawable.bt_dongman_1,R.drawable.bt_zhuanti_1,
			R.drawable.bt_dapian_1,R.drawable.bt_histroy_1,R.drawable.bt_save_1,R.drawable.bt_local_1,
			 
		    R.drawable.bt_home_2,R.drawable.bt_dianying_2,R.drawable.bt_dianshiju_2,
		    R.drawable.bt_zongyi_2,R.drawable.bt_dongman_2,R.drawable.bt_zhuanti_2,
		    R.drawable.bt_dapian_2,R.drawable.bt_histroy_2,R.drawable.bt_save_2,R.drawable.bt_local_2,
	};
	private int oldPosition=0;//上次点击的按钮位置 0-9
	private SlidingMenu mSlidingMenu;
	private Activity_Main activity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fm = getActivity().getSupportFragmentManager();
		mSlidingMenu=activity.getSlideMenu();
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.frame_left, null);
			bt_default=(Button) view.findViewById(R.id.bt_shouye);
			bt_default.setOnClickListener(this);
			view.findViewById(R.id.bt_dianying).setOnClickListener(this);
			view.findViewById(R.id.bt_dianshiju).setOnClickListener(this);
			view.findViewById(R.id.bt_zongyi).setOnClickListener(this);
			view.findViewById(R.id.bt_dongman).setOnClickListener(this);
			view.findViewById(R.id.bt_zhuanti).setOnClickListener(this);
			view.findViewById(R.id.bt_dapian).setOnClickListener(this);
			view.findViewById(R.id.bt_download).setOnClickListener(this);
			view.findViewById(R.id.bt_shoucang).setOnClickListener(this);
			view.findViewById(R.id.bt_local).setOnClickListener(this);
			view.findViewById(R.id.bt_set).setOnClickListener(this);
			view.findViewById(R.id.bt_histroy).setOnClickListener(this);
			View entranceView = view.findViewById(R.id.entrance);
		      ExchangeDataService mExService = new ExchangeDataService();
		      ExchangeConstants.APPKEY="5330f612be190254e6b562f4";
		      Drawable drawable = getResources().getDrawable(R.drawable.bt_app_1);
		      ExchangeViewManager mMgr = new ExchangeViewManager(activity,mExService);
		      mMgr.addView(ExchangeConstants.type_list_curtain,entranceView,drawable);
		}
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (Activity_Main) activity;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.bt_shouye){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "首页");
			startFragment(v,0,new Fragment_Shouye());
			activity.getSupportActionBar().setTitle(R.string.app_name);
		}else if(v.getId() == R.id.bt_dianying){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "电影");
			Fragment_Dianying dianying=new Fragment_Dianying();
			Bundle b=new Bundle();
			b.putString("type", getString(R.string.dianying));
			dianying.setArguments(b);
			startFragment(v,1,dianying);
		}else if(v.getId() == R.id.bt_dianshiju){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "电视剧");
			Fragment_Dianying dianying=new Fragment_Dianying();
			Bundle b=new Bundle();
			b.putString("type", getString(R.string.dianshiju));
			dianying.setArguments(b);
			startFragment(v,2,dianying);
		}else if(v.getId() == R.id.bt_zongyi){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "综艺");
			Fragment_Dianying dianying=new Fragment_Dianying();
			Bundle b=new Bundle();
			b.putString("type", getString(R.string.zongyi));
			dianying.setArguments(b);
			startFragment(v,3,dianying);
		}else if(v.getId() == R.id.bt_dongman){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "动漫");
			Fragment_Dianying dianying=new Fragment_Dianying();
			Bundle b=new Bundle();
			b.putString("type", getString(R.string.dongman));
			dianying.setArguments(b);
			startFragment(v,4,dianying);
		}else if(v.getId() == R.id.bt_zhuanti){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "专题");
			startFragment(v,5,new Fragment_Zhuanti());
			activity.getSupportActionBar().setTitle(R.string.zhuanti);
		}else if(v.getId() == R.id.bt_dapian){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "大片");
			startFragment(v,6,new Fragment_Dapian());
			activity.getSupportActionBar().setTitle(R.string.dapian);
		}else if(v.getId() == R.id.bt_histroy){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "历史");
			//changeButtonBg(v, 7);
			startActivity(new Intent(activity, Activity_Histroy.class));
		}else if(v.getId() == R.id.bt_shoucang){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "收藏");
			//changeButtonBg(v, 8);
			startActivity(new Intent(activity, Activity_Save.class));
		}else if(v.getId() == R.id.bt_local){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "本地");
			//changeButtonBg(v, 9);
			startActivity(new Intent(activity, MainLocalActivity.class));
		}else if(v.getId() == R.id.bt_set){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "设置");
			startActivity(new Intent(activity, Activity_Setting.class));
		}else if(v.getId() == R.id.bt_download){
			MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Page", "下载");
			startActivity(new Intent(activity, Activity_DownLoad.class));
		}
	}
	
	private void startFragment(View bt,int position, Fragment fragment){
		changeButtonBg(bt, position);
		FragmentTransaction t = fm.beginTransaction();
		t.replace(R.id.center_frame, fragment);
		t.commit();
		mSlidingMenu.toggle();
	}
	private void changeButtonBg(View bt,int position){
		try {
			bt.setBackgroundResource(res[position+10]);//更换点击后图片
			bt_default.setBackgroundResource(res[oldPosition]);//上次点击的按钮 还原图片
			bt_default = bt;
			oldPosition=position;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
