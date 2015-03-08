package cn.pipi.mobile.pipiplayer.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Activity_Center extends SherlockFragmentActivity {
	private MovieInfo m_info;
	private ProgressBar progerssbar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movieinfo);
		progerssbar=(ProgressBar)findViewById(R.id.progerssBar);
		prepareActionBar();
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.movieinfo, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
       	          finish();
       	     break;
        }
        return super.onOptionsItemSelected(item);
	}
	
	
}
