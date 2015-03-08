package cn.pipi.mobile.pipiplayer.ui;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.umeng.analytics.MobclickAgent;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.List_ZhuanTi_Adapter;
import cn.pipi.mobile.pipiplayer.asyctask.ShouyeAsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.ZhuanTiAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.ZhuanTiInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnHeaderRefreshListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_Dapian extends SherlockFragment implements OnItemClickListener,OnHeaderRefreshListener{
	private View view;
	private ListView listview_shouye;
	private List<ZhuanTiInfo> list;
	private ProgressBar progerssbar;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.frame_listview_reflash, null);
			listview_shouye=(ListView) view.findViewById(R.id.listView_shouye);
			listview_shouye.setOnItemClickListener(this);
			progerssbar=(ProgressBar) view.findViewById(R.id.progerssBar);
			PullToRefreshView pull_refresh_view = (PullToRefreshView) view.findViewById(R.id.pull_refresh_view);
			pull_refresh_view.setOnHeaderRefreshListener(this);
			pull_refresh_view.setFooterRefresh(false);;
		}
		new ZhuanTiAsyncTask( handler).execute("http://m.pipi.cn/dapian.js");
		return view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	// 主线程中新建一个handler
		private	Handler  handler = new Handler() {
					public void handleMessage(android.os.Message msg) {
		            	switch (msg.what) {
		            	case PipiPlayerConstant.EXEC_NORMOL:
		        			progerssbar.setVisibility(View.GONE);
		        			list = (List<ZhuanTiInfo>) msg.obj;
		        			listview_shouye.setAdapter(new List_ZhuanTi_Adapter(getActivity(), list));
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent =new Intent(getActivity(), Activity_ZhuanTi.class);
		intent.putExtra("name", list.get(arg2).getName());
		intent.putExtra("id", list.get(arg2).getId());
		startActivity(intent);
		MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_tuijian", "大片");
	}
	@Override
	public void onHeaderRefresh(final PullToRefreshView view) {
		// TODO Auto-generated method stub
		view.post(new Runnable() {
			//@Override
			public void run() {
				progerssbar.setVisibility(View.VISIBLE);
				new ZhuanTiAsyncTask( handler).execute("http://m.pipi.cn/dapian.js");
				view.onHeaderRefreshComplete("");
			}
		});
	} 
		    
}
