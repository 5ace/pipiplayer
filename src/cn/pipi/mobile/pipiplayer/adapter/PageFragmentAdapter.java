package cn.pipi.mobile.pipiplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

import cn.pipi.mobile.pipiplayer.ui.Fragment_MovieInfo_Pager1;
import cn.pipi.mobile.pipiplayer.ui.Fragment_MovieInfo_Pager2;
import cn.pipi.mobile.pipiplayer.ui.Fragment_MovieInfo_Pager3;
import cn.pipi.mobile.pipiplayer.ui.Fragment_MovieInfo_Pager4;
import cn.pipi.mobile.pipiplayer.ui.Fragment_MovieInfo_Pager5;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageFragmentAdapter extends FragmentPagerAdapter{
	List<SherlockFragment> list=new ArrayList<SherlockFragment>();
 	public PageFragmentAdapter(FragmentManager fm) {
		super(fm);
		list.add(new Fragment_MovieInfo_Pager1());
		list.add(new Fragment_MovieInfo_Pager2());
		list.add(new Fragment_MovieInfo_Pager3());
		list.add(new Fragment_MovieInfo_Pager4());
		list.add(new Fragment_MovieInfo_Pager5());
	}
	@Override
	public SherlockFragment getItem(int arg0) {
		 return list.get(arg0);
	}
	@Override
	public int getCount() {
		return list.size();
	}
}
