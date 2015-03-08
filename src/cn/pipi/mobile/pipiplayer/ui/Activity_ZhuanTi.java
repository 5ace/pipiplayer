package cn.pipi.mobile.pipiplayer.ui;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.MoiveListAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieByCatesAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.DataUtil;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_ZhuanTi extends SherlockFragmentActivity {
	private ProgressBar progerssbar;
	private ListView listview_shouye;
	private MoiveListAdapter adapter;
	private String title,typeId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_listview);
		try {
			typeId = getIntent().getStringExtra("id");
			title = getIntent().getStringExtra("name");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		progerssbar=(ProgressBar)findViewById(R.id.progerssBar);
		listview_shouye=(ListView)findViewById(R.id.listView_shouye);
		adapter=new MoiveListAdapter();
		listview_shouye.setAdapter(adapter);
		prepareActionBar();
		
		new GetMovieByCatesAsyncTask(handler,0).execute(typeId);
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(title);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
       	          finish();
        }
        return super.onOptionsItemSelected(item);
	}
	// 主线程中新建一个handler
			private	Handler  handler = new Handler() {
						public void handleMessage(android.os.Message msg) {
			            	switch (msg.what) {
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
			        		case PipiPlayerConstant.GETMOVIEBYCATE:
			        			progerssbar.setVisibility(View.GONE);
			        			adapter.reflash((List<MovieInfo>) msg.obj);
			    				break;
			        		default:
			        			break;
							}
			            }
			    };
}
