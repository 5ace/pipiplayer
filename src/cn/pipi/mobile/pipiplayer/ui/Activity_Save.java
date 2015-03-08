package cn.pipi.mobile.pipiplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.HistroyListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MoiveListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.SaveListAdapter;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.util.DataUtil;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_Save extends SherlockFragmentActivity implements OnItemClickListener,OnClickListener{
	private SaveListAdapter adapter;
	private LinearLayout deletelayout;//内存剩余是否显示   删除按钮布局
	private Button deletesingle,deleteall;
	private final int DELETElAYOUT=2015;//刷新影片删除界面
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		prepareActionBar();
		findViewById(R.id.ll_sdsize).setVisibility(8);
		ListView listview=(ListView) findViewById(R.id.listView_shouye);
		adapter = new SaveListAdapter(this,DBHelperDao.getDBHelperDaoInstace().getSmovieSave(),handler);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		deletelayout=(LinearLayout) findViewById(R.id.deletelayout);//删除按钮布局
		deleteall=(Button) findViewById(R.id.deleteall);
	    deletesingle=(Button) findViewById(R.id.deletesingle);
	    deleteall.setOnClickListener(this);
	    deletesingle.setOnClickListener(this);
	    if(adapter.getCount()==0){
        	DataUtil.getToast("暂无收藏记录");
        }
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(R.string.shoucang);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.save, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
       	          finish();
        case R.id.menu_edit:
        	if(adapter.changeDeleteVisible()){
				//v.setBackgroundResource(R.drawable.button_my_moive_clear2);
				deletelayout.setVisibility(0);
			}else {
				initDeleteLayout();
			}
      	     break;
        }
        return super.onOptionsItemSelected(item);
	}
	private void initDeleteLayout(){//初始化删除功能布�?
		deleteall.setText(getString(R.string.selectall));
		deletesingle.setText(getString(R.string.delete));
		deletesingle.setBackgroundColor(getResources().getColor(R.color.gray2 ));
		deletelayout.setVisibility(View.GONE);
		//clear.setBackgroundResource(R.drawable.button_my_moive_clear);
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.deleteall){
			adapter.setDeleteAllList(deleteall);
		}else if(v.getId()==R.id.deletesingle){
			adapter.DeleteList();
			initDeleteLayout();
		}
	}
	
	private Handler handler = new Handler() {
		//@Override
		public void handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case DELETElAYOUT:
				int count=(Integer) message.obj;
				if(count>0){
					deletesingle.setBackgroundColor(getResources().getColor(R.color.red));
					StringBuffer string=new StringBuffer(getString(R.string.delete));
					string.append("(");
					string.append(count);
					string.append(")");
					deletesingle.setText(string.toString());
				}else{
					deletesingle.setText(getString(R.string.delete));
					deletesingle.setBackgroundColor(getResources().getColor(R.color.gray2));
				}
				break;
			default:
				break;
			}
			super.handleMessage(message);
		}

	};
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(this,Activity_MovieInfo.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		DownLoadInfo down = adapter.getItem(arg2);
		MovieInfo info=new MovieInfo();
		info.setId(down.getDownID());
		info.setName(down.getDownName());
		intent.putExtra("movieInfo", info);
		startActivity(intent);
	}
	
}
