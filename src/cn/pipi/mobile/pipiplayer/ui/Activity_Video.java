package cn.pipi.mobile.pipiplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ProgressBar;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoPlayerActivity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_Video extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		DownLoadInfo downInfo=(DownLoadInfo) getIntent().getSerializableExtra("downInfo");
		if(downInfo == null)finish();
		playVideo(downInfo);
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
	public void playVideo(DownLoadInfo  downInfo){
   	  Log.i("TAG999", "playVideo");
   	FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
   	Fragment_VideoPlayer f=new Fragment_VideoPlayer();
   	Bundle b=new Bundle();
   	b.putSerializable("downInfo", downInfo);
   	f.setArguments(b);
 	t.replace(R.id.frame_player, f);
 	t.commit();
	  }
	
	public static void start(Context context, DownLoadInfo downInfo) {
    	Intent intent = new Intent(context, Activity_Video.class);
        intent.putExtra("downInfo", downInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }
	
}
