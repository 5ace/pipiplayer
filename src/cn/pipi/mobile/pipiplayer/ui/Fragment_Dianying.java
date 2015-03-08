package cn.pipi.mobile.pipiplayer.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragment;
import com.umeng.analytics.MobclickAgent;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.MoiveListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.PageAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieByCatesAsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieBySelectionAsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieTypeAsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.ShouyeAsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.ZhuanTiAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.beans.SelectionInfo;
import cn.pipi.mobile.pipiplayer.beans.TypeInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoPlayerActivity;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.view.FlowLayout;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnHeaderRefreshListener;
import cn.pipi.slidingmenu.lib.SlidingMenu;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Fragment_Dianying extends SherlockFragment implements OnScrollListener,
       OnCheckedChangeListener,OnPageChangeListener,OnHeaderRefreshListener{
	private View view;
	private HorizontalScrollView scrollView;
	private ScrollView scrlll;
	private int[] location; //保存频道button的位置
	private RadioGroup radioGroup;  //放置上面的各个频道按钮
	private int width;//屏幕宽度
	private TextView selectInfo,select;//筛选后信息,筛选按钮,加载更多
	private ViewPager vPager;
	private Activity_Main activity;
	private TypeInfo types;
	private List<String> groupTitleList=new ArrayList<String>();
	private List<MoiveListAdapter> adapterList ;
	private ProgressBar progerssbar;
	private SelectionInfo selection=new SelectionInfo();//选择条件
	private boolean reflash = true ;//是否允许加载数据  避免滑动到底部连续加载多次
	private LinearLayout layout_selection;
	private LayoutInflater inflater;
	private FlowLayout flowLayout;
	private Animation downTranslateAnimation,upTranslateAnimation;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapterList = new ArrayList<MoiveListAdapter>();
		try {
			String type = getArguments().getString("type");
			activity.getSupportActionBar().setTitle(type);
			selection.setTp(type);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (Activity_Main) activity;
	}
	@Override
	public View onCreateView( final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			this.inflater=inflater;
			view = inflater.inflate(R.layout.frame_dianying, null);
			
			select = (TextView) view.findViewById(R.id.select);
			select.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getSelection();
				}
			});
			downTranslateAnimation=AnimationUtils.loadAnimation(VLCApplication.getAppContext(), R.anim.my_translate_down);
	        upTranslateAnimation=AnimationUtils.loadAnimation(VLCApplication.getAppContext(), R.anim.my_translate_up);
			selectInfo = (TextView) view.findViewById(R.id.selectInfo);
			layout_selection=(LinearLayout) view.findViewById(R.id.layout_selection);
			progerssbar = (ProgressBar) view.findViewById(R.id.progerssBar);
			scrollView = (HorizontalScrollView) view.findViewById(R.id.hscroll);
			scrlll = (ScrollView) view.findViewById(R.id.scrlll);
			vPager = (ViewPager) view.findViewById(R.id.vPager);
			radioGroup = (RadioGroup) view.findViewById(R.id.bar_radio);
			flowLayout = (FlowLayout) view.findViewById(R.id.flowlayout);
			if(isAdded()){
				SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(activity);
				if(preference.getBoolean("first_dianying",true)){
				preference.edit().putBoolean("first_dianying", false).commit();
				view.findViewById(R.id.control_dianying_layout).setVisibility(0);
				view.findViewById(R.id.control_dianying_layout).setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						v.setVisibility(8);
						return true;
					}
				});
						 };
			}
		}
		return view;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		location = new int[2];
		width = activity.getWindowManager().getDefaultDisplay().getWidth();
		try {
			if(activity.getMap()==null||
					activity.getMap().size()==0){
				
				new GetMovieTypeAsyncTask(handler).execute("");
			}else{
				getMovieType();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
    private void initRadioGroup(){
    	view.findViewById(R.id.bt_select_down).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(scrlll.getVisibility()==0){
					scrlll.startAnimation(upTranslateAnimation);
					scrlll.setVisibility(8);
				}else{
					scrlll.setVisibility(0);
					setFlowLayout(flowLayout, groupTitleList, groupTitleList.get(vPager.getCurrentItem()));
					scrlll.startAnimation(downTranslateAnimation);
				}
				
			}
		});
		radioGroup.setOnCheckedChangeListener(this);
		for(int i=0;i<groupTitleList.size();i++){
			RadioButton radioButton=(RadioButton) inflater.inflate(R.layout.radio_button_main, null);
			radioButton.setText(groupTitleList.get(i));
			radioGroup.addView(radioButton);
			if(i==0){
				radioButton.setChecked(true);
			}
		}
		
		view.findViewById(R.id.rl_hscroll).setVisibility(0);
    }
	
    private void initViewPage(){
    	List<View> ViewList = new ArrayList<View>();
    	for(String name:groupTitleList){
    		View mView = inflater.inflate(R.layout.listview_reflash, null);
    		
    		ListView listview=(ListView) mView.findViewById(R.id.listView_shouye);
    		MoiveListAdapter adapter = new MoiveListAdapter();
    		listview.setAdapter(adapter);
    		if(name.endsWith(groupTitleList.get(0))){//默认只有全部才可以滑动加载更多
    		listview.setOnScrollListener(this);
    		}
    		ViewList.add(mView);
    		adapterList.add(adapter);
    		
    		PullToRefreshView pull_refresh_view = (PullToRefreshView) mView.findViewById(R.id.pull_refresh_view);
    		pull_refresh_view.setOnHeaderRefreshListener(this);
    		pull_refresh_view.setFooterRefresh(false);;
    	}
    	vPager.setAdapter(new PageAdapter(ViewList));
    	vPager.setOnPageChangeListener(this);
    	vPager.setCurrentItem(0);
    }

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<group.getChildCount();i++){
			if(checkedId == group.getChildAt(i).getId()){
				if(scrlll.getVisibility()==0){
					scrlll.startAnimation(upTranslateAnimation);
					scrlll.setVisibility(8);
				}
				setPostion(group.getChildAt(i));
				vPager.setCurrentItem(i);
			}
		}
	}

	private int firstVisibleItem,visibleItemCount,totalItemCount;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		this.firstVisibleItem = firstVisibleItem;
		this.visibleItemCount = visibleItemCount;
		this.totalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(scrollState==SCROLL_STATE_IDLE&&firstVisibleItem>0){
			if(firstVisibleItem + visibleItemCount == totalItemCount&&reflash&&totalItemCount>0){
				getDatas(0);
			}
		}
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
		MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Movie_Page", selection.getTp());
		setPostion(radioGroup.getChildAt(arg0));
		//没有数据则重新加载
		if(adapterList.get(arg0).getCount()==0){
			getDatas(arg0);
		}
		if(arg0==0){
			activity.getSlideMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			view.findViewById(R.id.ll_select).setVisibility(0);
			
		}else{
			activity.getSlideMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			view.findViewById(R.id.ll_select).setVisibility(8);
			if(layout_selection.getVisibility()==0){
				layout_selection.setVisibility(8);
				layout_selection.startAnimation(upTranslateAnimation);
			}
		}
	}
	
	private void getDatas(int position){
		Log.i("TAG999", "position=="+position);
		if(position==0){//加载筛选内容
			if(reflash&&adapterList.get(0).getCount()%selection.getPs()==0){//可以加载数据
			reflash=false;
			progerssbar.setVisibility(View.VISIBLE);
			new GetMovieBySelectionAsyncTask(handler,selection).execute("");
			}
		}else{
			progerssbar.setVisibility(View.VISIBLE);
			new GetMovieByCatesAsyncTask(handler,position).execute(types.getCates().get(groupTitleList.get(position)));
		}
	}
	
	// 主线程中新建一个handler
				Handler  handler = new Handler() {
						public void handleMessage(android.os.Message msg) {
			            	switch (msg.what) {
			            	case PipiPlayerConstant.EXEC_NORMOL:
			            		progerssbar.setVisibility(View.GONE);
			            		adapterList.get(0).addAll((ArrayList<MovieInfo>) msg.obj);
			            		selection.setNp(adapterList.get(0).getCount()/selection.getPs()+1);//可以加载下一页数据
			            		reflash=true;
			    				break;
			            	case PipiPlayerConstant.GETMOVIEBYCATE:
			            		progerssbar.setVisibility(View.GONE);
			            		List<MovieInfo> list=(ArrayList<MovieInfo>) msg.obj;
			            		adapterList.get(msg.arg1).reflash(list);
			    				break;
			    			case PipiPlayerConstant.NONETWORK:
			    				reflash=true;
			    				progerssbar.setVisibility(View.GONE);
			    				DataUtil.getToast(R.string.NONETWORK);
			    			    break;
			    			case PipiPlayerConstant.NO_DATA_RETURN:
			    				reflash=true;
			    				progerssbar.setVisibility(View.GONE);
			    				DataUtil.getToast(R.string.NO_DATA_RETURN);
			    				break;
			    			case PipiPlayerConstant.DATA_RETURN_ZERO:
			    				reflash=true;
			    				progerssbar.setVisibility(View.GONE);
			    				DataUtil.getToast(R.string.nosoucefound);
			    				break;
			    			case PipiPlayerConstant.GETMOVIETYPE:
			    				progerssbar.setVisibility(View.GONE);
			    				activity.setMap((Map<String,TypeInfo>) msg.obj);
			    				getMovieType();
			    				break;
			    			default:
			    				break;
							}
			            }
			    }; 
			    
	private void getSelection(){
		if(layout_selection.getVisibility()==8){
			layout_selection.setVisibility(0);
			final FlowLayout flowlayout1 = (FlowLayout) view.findViewById(R.id.flowlayout1);
			setFlowLayout(flowlayout1, types.getOrderList(),selection.getOrder());
			final FlowLayout flowlayout2 = (FlowLayout) view.findViewById(R.id.flowlayout2);
			setFlowLayout(flowlayout2, types.getTypeList(),selection.getStp());
			final FlowLayout flowlayout3 = (FlowLayout) view.findViewById(R.id.flowlayout3);
			setFlowLayout(flowlayout3, types.getAreaList(),selection.getAr());
			final FlowLayout flowlayout4 = (FlowLayout) view.findViewById(R.id.flowlayout4);
			setFlowLayout(flowlayout4, types.getYearList(),selection.getFt());
			
			Button ok=(Button)view.findViewById(R.id.ok);
			Button cancle=(Button)view.findViewById(R.id.cancle);
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(flowlayout1.getTag()!=null){
						selection.setOrder(flowlayout1.getTag().toString());
					}
					if(flowlayout2.getTag()!=null
							&&!TextUtils.isEmpty(flowlayout2.getTag().toString())){
						selection.setStp(flowlayout2.getTag().toString().equals("全部")?"":flowlayout2.getTag().toString());
					}
					if(flowlayout3.getTag()!=null
							&&!TextUtils.isEmpty(flowlayout3.getTag().toString())){
						selection.setAr(flowlayout3.getTag().toString().equals("全部")?"":flowlayout3.getTag().toString());
					}
					if(flowlayout4.getTag()!=null
							&&!TextUtils.isEmpty(flowlayout4.getTag().toString())){
						selection.setFt(flowlayout4.getTag().toString().equals("全部")?"":flowlayout4.getTag().toString());
					}
					StringBuffer buffer = new StringBuffer(selection.getOrder()); 
					if(!TextUtils.isEmpty(selection.getStp())){
						buffer.append("/"+selection.getStp());
					}else{
						buffer.append("/类型");
					}
					if(!TextUtils.isEmpty(selection.getAr())){
						buffer.append("/"+selection.getAr());
					}else{
						buffer.append("/地区");
					}
					if(!TextUtils.isEmpty(selection.getFt())){
						buffer.append("/"+selection.getFt());
					}else{
						buffer.append("/年份");
					}

					selectInfo.setText(buffer.toString());
					selection.setNp(1);
					layout_selection.setVisibility(8);
					adapterList.get(0).reflash(null);
					getDatas(0);
					MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Movie_Select", selection.getTp());
				}
			});
			cancle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					layout_selection.setVisibility(8);
				}
			});
			 
			layout_selection.startAnimation(downTranslateAnimation);
		}else{
			layout_selection.startAnimation(upTranslateAnimation);
			layout_selection.setVisibility(8);
		}
	}
	
	private void setFlowLayout(final FlowLayout flowlayout,final List<String> list,final String defaultString){
		flowlayout.removeAllViews();//清空记录
		flowlayout.setTag(defaultString);//默认选中项
		final List<TextView> textList =new ArrayList<TextView>();
		 for(int i=0;i<list.size();i++){
			 View v=inflater.inflate(R.layout.item_selection_grid, null);
			 final TextView text = (TextView) v.findViewById(R.id.text);
			 text.setText(list.get(i));
			 textList.add(text);
			 if(!TextUtils.isEmpty(list.get(i))&&
					 !TextUtils.isEmpty(defaultString)&&
					 list.get(i).equals(defaultString)){
				 text.setTextColor(Color.WHITE);
				 text.setBackgroundResource(R.drawable.rounded_select_red);
			 }
			 
			 flowlayout.addView(v);
			 final int position =i;
			 text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for(TextView textView:textList){
						textView.setTextColor(Color.BLACK);
						textView.setBackgroundColor(Color.TRANSPARENT);
					}
					flowlayout.setTag(list.get(position));//当前选中项
					text.setTextColor(Color.WHITE);
					text.setBackgroundResource(R.drawable.rounded_select_red);
					
					if(flowlayout.getId() == flowLayout.getId()){//当前为大分类
						scrlll.startAnimation(upTranslateAnimation);
						scrlll.setVisibility(8);
						vPager.setCurrentItem(position);
					}
				}
			});
		 }
	}
	
	
	private void getMovieType(){
		types=activity.getMap().get(selection.getTp());
		if(types==null){
			return;
		}
		for(String s:types.getCates().keySet()){
			groupTitleList.add(s);
		}
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				initRadioGroup();
				view.findViewById(R.id.ll_select).setVisibility(0);
				initViewPage();
				getDatas(0);
			}
		},300);
	}
	@Override
	public void onHeaderRefresh(final PullToRefreshView view) {
		// TODO Auto-generated method stub
		view.post(new Runnable() {
			//@Override
			public void run() {
				if(groupTitleList.size()!=0){
					getDatas(vPager.getCurrentItem());
				}
				view.onHeaderRefreshComplete("");
			}
		});
	}
			    
}
