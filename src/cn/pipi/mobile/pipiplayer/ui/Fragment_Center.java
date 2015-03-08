package cn.pipi.mobile.pipiplayer.ui;

import com.actionbarsherlock.app.SherlockFragment;

import cn.pipi.mobile.pipiplayer.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Fragment_Center extends SherlockFragment {
   private View view;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			view = inflater.inflate(R.layout.frame_center, null);
			
		}
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
