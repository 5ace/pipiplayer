package cn.pipi.mobile.pipiplayer.ui;

import com.actionbarsherlock.app.SherlockFragment;

import cn.pipi.mobile.pipiplayer.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * �ұ�
 * @author yongchao1210
 *
 */
public class Fragment_Right extends SherlockFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frame_right, null);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
