package cn.pipi.mobile.pipiplayer.ui;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.MovieSmsAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieInfoSmsAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieSms;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class Fragment_MovieInfo_Pager4 extends SherlockFragment implements OnScrollListener{
	private View view;
	private ListView listview_shouye;
	private MovieSmsAdapter adapter;
	private Activity_MovieInfo activity;
	private boolean reflash = true;
	private int currrentIndex=1;//默认为第一页数据
	String movieID;
	EditText et_write_comment;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.page_comment, null,false);
			listview_shouye=(ListView)view.findViewById(R.id.listView_shouye);
			adapter=new MovieSmsAdapter();
			listview_shouye.setAdapter(adapter);
			listview_shouye.setOnScrollListener(this);
			try {
				movieID = activity.getMovieInfo().getId();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			view.findViewById(R.id.text_comment).setOnClickListener(new OnClickListener() {
				@Override
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent =new Intent(getActivity(), Activity_Comment.class);
					intent.putExtra("MovieID", movieID);
					try {
						intent.putExtra("MovieName", activity.getMovieInfo().getName());
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					startActivity(intent);
				}
			});
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
			            		currrentIndex+=1;
			            		reflash=true;
			        			activity.setProgressBar(false);
			        			adapter.reflash((ArrayList<MovieSms>) msg.obj);
			        			break;
			        		case PipiPlayerConstant.NONETWORK:
			        			reflash=true;
			        			activity.setProgressBar(false);
			        			DataUtil.getToast(R.string.NONETWORK);
			        			break;
			        		case PipiPlayerConstant.NO_DATA_RETURN:
			        			reflash=true;
			        			activity.setProgressBar(false);
			        			DataUtil.getToast(R.string.NO_DATA_RETURN);
			        			break;
			        		case PipiPlayerConstant.DATA_RETURN_ZERO:
			        			reflash=true;
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
			        	if(reflash&&adapter.getCount()==0){
			        			new GetMovieInfoSmsAsyncTask(currrentIndex,handler).execute(movieID);
						    	activity.setProgressBar(true);
						    	reflash=false;
			        	}
			        } else {
			            //相当于Fragment的onPause
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
						if(firstVisibleItem + visibleItemCount == totalItemCount
								&&reflash
								&&totalItemCount>0){
							
							if(adapter.getCount()%50==0){
								new GetMovieInfoSmsAsyncTask(currrentIndex,handler).execute(movieID);
						    	activity.setProgressBar(true);
						    	reflash=false;
			        		}else{
			        			DataUtil.getToast("已经最后一页");
			        		}
						}
					}
				}
}
