package cn.pipi.mobile.pipiplayer.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.MoiveListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.PageAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.Search_Result_AsyncTask;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Activity_SearchResult extends SherlockFragmentActivity implements OnScrollListener,
       OnCheckedChangeListener,OnPageChangeListener{
	private HorizontalScrollView scrollView;
	private int[] location; //保存频道button的位置
	private RadioGroup radioGroup;  //放置上面的各个频道按钮
	private int width;//屏幕宽度
	private ViewPager vPager;
	private LayoutInflater inflater;
	private String key;
	private ProgressBar progerssbar;
	private Context mContext;
	private List<String> tagList = new ArrayList<String>();//标签
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_dianying);
		mContext=this;
		key=getIntent().getStringExtra("key");
		inflater = LayoutInflater.from(this);
		prepareActionBar();
		progerssbar=(ProgressBar)findViewById(R.id.progerssBar);
		scrollView = (HorizontalScrollView) findViewById(R.id.hscroll);
		location = new int[2];
		width = getWindowManager().getDefaultDisplay().getWidth();
		findViewById(R.id.ll_select).setVisibility(8);
		//initViewPage();
		//initRadioGroup();
		radioGroup = (RadioGroup) findViewById(R.id.bar_radio);
		radioGroup.setOnCheckedChangeListener(this);
		vPager = (ViewPager) findViewById(R.id.vPager);
		new Search_Result_AsyncTask(handler).execute(key);
	}
	
    private void initRadioGroup(){
		for(int i=0;i<tagList.size();i++){
			RadioButton radioButton=(RadioButton) inflater.inflate(R.layout.radio_button_main, null);
			radioButton.setText(tagList.get(i));
			radioGroup.addView(radioButton);
			if(i==0)radioButton.setChecked(true);
		}
    }
	
    private void initViewPage(Map<String, List<MovieInfo>> map){
    	List<View> ViewList = new ArrayList<View>();
    	for(String key:tagList){
    		ListView listview=(ListView) inflater.inflate(R.layout.listview_shouye, null);
    		MoiveListAdapter adapter = new MoiveListAdapter(this, map.get(key));
    		listview.setAdapter(adapter);
    		ViewList.add(listview);
    	}
    	vPager.setAdapter(new PageAdapter(ViewList));
    	vPager.setCurrentItem(0);
    	vPager.setOnPageChangeListener(this);
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		for(int i=0;i<group.getChildCount();i++){
			if(checkedId == group.getChildAt(i).getId()){
				setPostion(group.getChildAt(i));
				vPager.setCurrentItem(i);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	private void setPostion(View view) {
		((RadioButton)view).setChecked(true);
		view.getLocationInWindow(location);
		int postion = location[0] - width / 2;
		if (postion != 0) {
			postion += 50;
			scrollView.smoothScrollBy(postion, 0);
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		//0未处理 1滑动中 2滑动完毕
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		vPager.setCurrentItem(arg0);
		setPostion(radioGroup.getChildAt(arg0));
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle("搜索+"+key);
    }
	
	// 主线程中新建一个handler
				Handler  handler = new Handler() {
						public void handleMessage(android.os.Message msg) {
			            	switch (msg.what) {
			            	case PipiPlayerConstant.EXEC_NORMOL:
			            		progerssbar.setVisibility(View.GONE);
			            		//对数据进行分类
			            		tagList.add("全部");
			            		List<MovieInfo> list = (List<MovieInfo>)msg.obj;
			            		Map<String, List<MovieInfo>> map = new HashMap<String, List<MovieInfo>>();
			            		map.put("全部", list);
			            		for(MovieInfo info:list){
			            			if(map.containsKey(info.getType())){
			            				//已经含有标签
			            				map.get(info.getType()).add(info);
			            			}else{
			            				tagList.add(info.getType());
			            				//创建新标签
			            				List<MovieInfo> l = new ArrayList<MovieInfo>();
			            				l.add(info);
			            				map.put(info.getType(), l);
			            			}
			            		}
			            		initRadioGroup();
			            		initViewPage(map);
			    				break;
			    			case PipiPlayerConstant.NONETWORK:
			    				progerssbar.setVisibility(View.GONE);
			    				DataUtil.getToast(R.string.NONETWORK);
			    			        break;
			    			case PipiPlayerConstant.NO_DATA_RETURN:
			    				progerssbar.setVisibility(View.GONE);
			    				DataUtil.getToast(R.string.NO_DATA_RETURN);
			    				break;
			    			case PipiPlayerConstant.DATA_RETURN_ZERO:
			    				progerssbar.setVisibility(View.GONE);
			    				DataUtil.getToast(R.string.nosoucefound);
			    				break;
			    			default:
			    				break;
							}
			            }
			    }; 
			    @Override
			    public boolean onOptionsItemSelected(MenuItem item) {
			        switch (item.getItemId()) {
			         case android.R.id.home:
			        	 finish();
			        	 break;
			        }
			        return super.onOptionsItemSelected(item);
				}
			    
}
