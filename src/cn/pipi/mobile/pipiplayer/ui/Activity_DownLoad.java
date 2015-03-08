package cn.pipi.mobile.pipiplayer.ui;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.GridViewAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MoiveListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.PageFragmentAdapter;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoPlayerActivity;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;
import cn.pipi.mobile.pipiplayer.util.UITimer;
import cn.pipi.mobile.pipiplayer.util.UITimer.OnUITimer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_DownLoad extends SherlockFragmentActivity implements OnItemClickListener,OnClickListener{
	private GridViewAdapter adapter;
	private final int SDCARDSIZE=2012;
	private final int UPDATELOAD=2014;//刷新影片下载整体界面
	private final int DELETElAYOUT=2015;//刷新影片删除界面
	private Context mContext;
	private LinearLayout deletelayout;//内存剩余是否显示   删除按钮布局
	private TextView mSDCardSizeTv,mLocalSizeTv,mRemainTv;
	private Button deletesingle,deleteall;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=this;
		setContentView(R.layout.activity_download);
		prepareActionBar();
		ListView listview=(ListView) findViewById(R.id.listView_shouye);
		adapter = new GridViewAdapter(this,handler);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		deletelayout=(LinearLayout) findViewById(R.id.deletelayout);//删除按钮布局
        deleteall=(Button) findViewById(R.id.deleteall);
        deletesingle=(Button) findViewById(R.id.deletesingle);
        deleteall.setOnClickListener(this);
        deletesingle.setOnClickListener(this);
		mSDCardSizeTv = (TextView)findViewById(R.id.offline_sdsizetv);
		mLocalSizeTv = (TextView)findViewById(R.id.offline_localsize);
		mRemainTv = (TextView)findViewById(R.id.offline_remainsize);
		
		initDeleteLayout();
		timer.start();
		new Thread(){
			public void run() {
				computeSdCardSize();
			};
		}.start();
		if(adapter.getCount()==0){
        	DataUtil.getToast("暂无下载记录");
        }
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(R.string.download);
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
       	     break;
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
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//点击暂停或�?加入下载队列
		final DownTask downTask=adapter.getItem(arg2);
		if(downTask==null)return;
		//在异步中处理这些操作
				// TODO Auto-generated method stub
				DownLoadInfo  downInfo = downTask.getDownLoadInfo();
				if(downInfo.getDownState()==DownTask.TASK_WAITING_DOWNLOAD
						||downInfo.getDownState()==DownTask.TASK_DOWNLOADING
						||downInfo.getDownState()==DownTask.TASK_RESUME_DOWNLOAD)//暂停掉即将或者正在下载的任务
					{
					downTask.pause();
					handler.sendEmptyMessage(UPDATELOAD);
					}
				else if(downInfo.getDownState()==DownTask.TASK_PAUSE_DOWNLOAD
						||downInfo.getDownState()==DownTask.TASK_WIFI_ERROR)//加入队列,而不是让他立即下�?
					{
					if(DataUtil.CheckWIFINet(mContext)||(DataUtil.Check3GNet(mContext)&&PipiPlayerConstant.getInstance().allowdown)){
						//允许下载
						downInfo.setDownState(DownTask.TASK_RESUME_DOWNLOAD);
						DownCenter.getExistingInstance().autoDown();//
						handler.sendEmptyMessage(UPDATELOAD);
					}else if(DataUtil.Check3GNet(mContext)&&!PipiPlayerConstant.getInstance().allowdown){
						//提示是否允许3G下载
						AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				   		alert.setTitle(R.string.ISGPRS);
				   		alert.setMessage(R.string.allowGPRSdown);
				   		alert.setPositiveButton(R.string.ok,
				   						new DialogInterface.OnClickListener() {
				   							public void onClick(DialogInterface dialog,
				   									int which) {
				   								downTask.resume();
				   							}
				   						})
				   				.setNegativeButton(R.string.cancel,
				   						new DialogInterface.OnClickListener() {
				   							public void onClick(DialogInterface dialog,
				   									int which) {
				   								dialog.dismiss();
				   							}
				   						});
				   		alert.create().show();
						handler.sendEmptyMessage(UPDATELOAD);
					}else{
						DataUtil.getToast("没有可用网络");
						downTask.pauseErr();
					}
					}
				else if(downInfo.getDownState()==DownTask.TASK_FINISHED)//下载完成了直接点击播�?
				{
						//重新生成�?��对象，避免下载和播放造成冲突
						DownLoadInfo Info=new DownLoadInfo(downInfo.getDownID(), downInfo.getDownName(),
								downInfo.getDownImg(), downInfo.getDownUrl(),
								downInfo.getDownPosition(),downInfo.getPlayList());
						
						Activity_Video.start(mContext, Info);
				}
				
	
	}
	 private Handler handler = new Handler() {
			//@Override
			public void handleMessage(Message message) {
				// TODO Auto-generated method stub
				switch (message.what) {
				case PipiPlayerConstant.UPDATEPROGRESS_VIEW:
					if(adapter!=null&&adapter.getCount()>0)adapter.notifyDataSetChanged();
					break;
				case SDCARDSIZE:
					updataWidget( message);
					break;
				case PipiPlayerConstant.playmovieTip:
					DataUtil.show3GTipsToPlay(Activity_DownLoad.this,(DownLoadInfo)message.obj);
					break;
				case UPDATELOAD:
					adapter.notifyDataSetChanged();
					break;
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
		
		UITimer timer = new UITimer(2000,new OnUITimer()
	    {
	        public void onTimer() {
	        	 Message message = new Message();
	    	        message.what = PipiPlayerConstant.UPDATEPROGRESS_VIEW;
	    	        handler.sendMessage(message);
	        }
	    });
		
		public void updataWidget(Message message) {
			if (message == null)
				return;
			Bundle bundle = message.getData();
			if (bundle == null)
				return;
			mSDCardSizeTv.setText("其他"
					+ SdcardUtil.formatSize(mContext,
							bundle.getLong("sdCardTotalSize")));
			mLocalSizeTv.setText("本地"
					+ SdcardUtil.formatSize(mContext,
							bundle.getLong("pipiLocalFileSize")));
			mRemainTv.setText("剩余"
					+ SdcardUtil.formatSize(mContext,
							bundle.getLong("avaiableSize")));
		}
		private void computeSdCardSize() {
			if (SdcardUtil.existSDcard()) {
				long sdCardTotalSize = SdcardUtil.computeSDSize1(this);
				long pipiLocalFileSize=0;
				try {
					File file=	new File(FileUtils.getFileCaches());
				    pipiLocalFileSize = SdcardUtil.getFileSize(file);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				long avaiableSize = SdcardUtil.getAvaliableSDSize(this);
				long otherSize = sdCardTotalSize - pipiLocalFileSize - avaiableSize;
				long usedSize = sdCardTotalSize - avaiableSize;
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putLong("sdCardTotalSize", sdCardTotalSize);
				bundle.putLong("pipiLocalFileSize", pipiLocalFileSize);
				bundle.putLong("avaiableSize", avaiableSize);
				bundle.putLong("otherSize", otherSize);
				bundle.putLong("usedSize", usedSize);
				message.setData(bundle);
				message.what = SDCARDSIZE; // 标示 更新界面ui 信息
				handler.sendMessage(message);
			}
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
	
}
