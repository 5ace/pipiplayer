package cn.pipi.mobile.pipiplayer.ui;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnHeaderClickListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.MoiveListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.Shouye_Adapter;
import cn.pipi.mobile.pipiplayer.asyctask.ShouyeAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnHeaderRefreshListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class Fragment_Shouye extends SherlockFragment implements OnHeaderRefreshListener{
	private View view;
	private StickyGridHeadersGridView mGridView;
	private ProgressBar progerssbar;
	private FragmentManager fm;
	private PullToRefreshView pull_refresh_view;
	private ArrayList<MovieInfo> list;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.fragment_shouye, null);
			mGridView=(StickyGridHeadersGridView) view.findViewById(R.id.asset_grid);
			progerssbar=(ProgressBar) view.findViewById(R.id.progerssBar);
			pull_refresh_view = (PullToRefreshView) view.findViewById(R.id.pull_refresh_view);
			pull_refresh_view.setOnHeaderRefreshListener(this);
			pull_refresh_view.setFooterRefresh(false);;
			new ShouyeAsyncTask(handler).execute("");
		}
		return view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fm = getActivity().getSupportFragmentManager();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	}
	
	// 主线程中新建一个handler
		private	Handler  handler = new Handler() {
					public void handleMessage(android.os.Message msg) {
		            	switch (msg.what) {
		            	case PipiPlayerConstant.EXEC_NORMOL:
		        			progerssbar.setVisibility(View.GONE);
		        			list = (ArrayList<MovieInfo>) msg.obj;
		        			mGridView.setAdapter(new Shouye_Adapter(getActivity(), list,
		        	                R.layout.item_shouye_header, R.layout.item_shouye_grid,fm));
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
		public void onHeaderRefresh(final PullToRefreshView view) {
			// TODO Auto-generated method stub
			view.post(new Runnable() {
				//@Override
				public void run() {
					progerssbar.setVisibility(View.VISIBLE);
					new ShouyeAsyncTask(handler).execute("");
					view.onHeaderRefreshComplete("");
				}
			});
		}
		
}
