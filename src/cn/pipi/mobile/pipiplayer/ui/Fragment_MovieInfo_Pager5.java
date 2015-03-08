package cn.pipi.mobile.pipiplayer.ui;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.MoiveLikeAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MoiveListAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieDescAsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieLikeAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class Fragment_MovieInfo_Pager5 extends SherlockFragment implements OnItemClickListener{
    private View view;
	private ListView listview_shouye;
	private MoiveLikeAdapter adapter;
	private Activity_MovieInfo activity;
	private MovieInfo m_info;
	private boolean hasData;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.listview_shouye, null,false);
			listview_shouye=(ListView)view.findViewById(R.id.listView_shouye);
			adapter=new MoiveLikeAdapter();
			listview_shouye.setAdapter(adapter);
			m_info = activity.getMovieInfo();
			listview_shouye.setOnItemClickListener(this);
		}
		return view;
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (Activity_MovieInfo) activity;
	}

	// 主线程中新建一个handler
		private	Handler  handler = new Handler() {
					public void handleMessage(android.os.Message msg) {
		            	switch (msg.what) {
		            	case PipiPlayerConstant.EXEC_NORMOL:
		            		hasData=true;
		        			activity.setProgressBar(false);
		        			adapter.reflash((List<MovieInfo>) msg.obj);
		        			break;
		        		case PipiPlayerConstant.NONETWORK:
		        			activity.setProgressBar(false);
		        			DataUtil.getToast(R.string.NONETWORK);
		        			break;
		        		case PipiPlayerConstant.NO_DATA_RETURN:
		        			activity.setProgressBar(false);
		        			DataUtil.getToast(R.string.NO_DATA_RETURN);
		        			break;
		        		case PipiPlayerConstant.DATA_RETURN_ZERO:
		        			activity.setProgressBar(false);
		        			DataUtil.getToast(R.string.nosoucefound);
		        			break;
		        		default:
		        			break;
						}
		            }
		    }; 
		    

		    @Override
		    public void setUserVisibleHint(boolean isVisibleToUser) {
		        super.setUserVisibleHint(isVisibleToUser);
		        if (isVisibleToUser) {
		            //相当于Fragment的onResume
		        	if(!hasData&&m_info!=null){
		        		new GetMovieLikeAsyncTask(handler).execute(m_info.getId());
				    	activity.setProgressBar(true);
		        	}
		        } else {
		            //相当于Fragment的onPause
		        }
		    }
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 MovieInfo beans = adapter.getItem(arg2);
				 Intent screenParam = new Intent();
				 screenParam.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				 screenParam.putExtra("movieInfo", beans);
				 screenParam.setClass( activity, Activity_MovieInfo.class );
				 activity.startActivity(screenParam);
				 activity.finish();
			}
}
