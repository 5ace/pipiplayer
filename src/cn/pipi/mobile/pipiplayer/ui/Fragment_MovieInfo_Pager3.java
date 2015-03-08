package cn.pipi.mobile.pipiplayer.ui;

import com.actionbarsherlock.app.SherlockFragment;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieDescAsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieNumAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_MovieInfo_Pager3 extends SherlockFragment {
   private View view;
   private TextView moive_actor,moive_area,moive_desc;
   private Activity_MovieInfo activity;
   private MovieInfo m_info;
   private boolean hasData;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.page_desc, null,false);
			moive_actor=(TextView) view.findViewById(R.id.moive_actor);
			moive_area=(TextView) view.findViewById(R.id.moive_area);
			moive_desc=(TextView) view.findViewById(R.id.moive_desc);
			m_info = activity.getMovieInfo();
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
	        			moive_actor.setText("导演："+m_info.getDirector()+"\n"+"主演："+m_info.getActor());
	        			String score = null;
	        			try {
	        	        	  score=String.valueOf((int)(Float.parseFloat(m_info.getDafen_num())*20)/10.0);
	        			} catch (Exception e) {
	        				// TODO: handle exception
	        			}
	        			moive_area.setText(m_info.getYear()+"/"+m_info.getArea()+"/"+score);
	        			moive_desc.setText("\r\r\r\r\r\r"+m_info.getDesc());
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
	        	if(hasData)return;
		    	new GetMovieDescAsyncTask(handler, m_info).execute("");
		    	activity.setProgressBar(true);
	        } else {
	            //相当于Fragment的onPause
	        }
	    }
}
