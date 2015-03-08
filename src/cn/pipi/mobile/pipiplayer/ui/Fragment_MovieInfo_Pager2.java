package cn.pipi.mobile.pipiplayer.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.app.SherlockFragment;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.DownButtonsAdapter;
import cn.pipi.mobile.pipiplayer.adapter.IconImageAdapter;
import cn.pipi.mobile.pipiplayer.adapter.PlayButtonsAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.GetMovieNumAsyncTask;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;
import cn.pipi.mobile.pipiplayer.util.MD5Util;
import cn.pipi.mobile.pipiplayer.view.MyGridView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_MovieInfo_Pager2 extends SherlockFragment implements OnClickListener{
   private View view;
   private ImageView tag_img;
   private TextView tag_text,tag_order;
   private MyGridView grid_num,grid_selection;
   private Activity_MovieInfo activity;
   private MovieInfo m_info;
   private DownButtonsAdapter adapterNum;
   private IconImageAdapter adapterIcon;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view == null){
			m_info=activity.getMovieInfo();
			view = inflater.inflate(R.layout.page_selectnum, null,false);
			view.setVisibility(4);
			tag_img=(ImageView) view.findViewById(R.id.tag_img);
			tag_text=(TextView) view.findViewById(R.id.tag_text);
			tag_order=(TextView) view.findViewById(R.id.tag_order);
			
			tag_img.setOnClickListener(this);
			tag_text.setOnClickListener(this);
			tag_order.setOnClickListener(this);
			
			grid_num=(MyGridView) view.findViewById(R.id.grid_num);
			adapterNum=new DownButtonsAdapter(activity, m_info);
			grid_num.setAdapter(adapterNum);
			
			grid_selection=(MyGridView) view.findViewById(R.id.grid_selection);
			adapterIcon=new IconImageAdapter(activity);
			grid_selection.setAdapter(adapterIcon);
			grid_selection.setOnItemClickListener(Selection_Click);
		}
		return view;
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (Activity_MovieInfo) activity;
	}
	@Override
	public void onResume() {
		super.onResume();
		
	}
	private void setTag(final String tag){
		view.setVisibility(0);
		tag_text.setText(tag);
		m_info.setTag(tag);
		//设置资源图标
		try {
			tag_img.setImageResource(PipiPlayerConstant.getInstance().sourcesList.get(tag));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		adapterNum.setOrdered(m_info.isOrdered());
		adapterNum.reflash();
		
	}
	
	// 主线程中新建一个handler
	private	Handler  handler = new Handler() {
				public void handleMessage(android.os.Message msg) {
	            	switch (msg.what) {
	            	case PipiPlayerConstant.EXEC_NORMOL:
	        			activity.setProgressBar(false);
	        			Map<String ,ArrayList<String>> map = (Map<String ,ArrayList<String>>) msg.obj;
	        			m_info.setPlayList(map);
	        			Set<String> keySet = map.keySet();
	        			//遍历key集合，获取value
	        			List<String> list=new ArrayList<String>();
	        			for(String key : keySet) {
	        				list.add(key);
	        			}
	        			m_info.setMovieTag(list);
	        			//设置默认资源
	        			String tag=m_info.getMovieTag().get(0);
	        			setTag(tag);
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
	        		case PipiPlayerConstant.DATA_REFLASH:
	        			if(m_info.isOrdered()){//正序
	        				tag_order.setText(getString(R.string.order_2));
	        			}else{
	        				tag_order.setText(getString(R.string.order_1));
	        			}
	        			if(!TextUtils.isEmpty(m_info.getTag())){//说明已经加载过数据
	        				setTag(m_info.getTag());
	        				return;
	        			}
	        			if(adapterNum.getCount()==0){//加载网络数据
	        				try {
	        					int movieID=Integer.parseInt(m_info.getId());
	        					new GetMovieNumAsyncTask(handler, movieID).execute("");
	        					activity.setProgressBar(true);
	        				} catch (Exception e) {
	        					// TODO: handle exception
	        					e.printStackTrace();
	        				}
	        			}
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
        	handler.sendEmptyMessageDelayed(PipiPlayerConstant.DATA_REFLASH, 300);
        } else {
            //相当于Fragment的onPause
        }
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.tag_img||v.getId()==R.id.tag_text){
			if(grid_selection.getVisibility()==View.GONE){
				grid_selection.setVisibility(View.VISIBLE);
				if(m_info.getMovieTag()!=null){
					adapterIcon.reflash(m_info.getMovieTag());
				}
				try {
					Drawable drawable= getResources().getDrawable(R.drawable.ic_bt_up);  
					/// 这一步必须要做,否则不会显示.  
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
					tag_text.setCompoundDrawables(null,null,drawable,null);  
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				grid_selection.setVisibility(View.GONE);
				try {
					Drawable drawable= getResources().getDrawable(R.drawable.ic_bt_down);  
					/// 这一步必须要做,否则不会显示.  
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
					tag_text.setCompoundDrawables(null,null,drawable,null);  
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(v.getId()==R.id.tag_order){
			//更改顺序
			//更改顺序
			if(m_info.isOrdered()){//正序
				m_info.setOrdered(false);
				tag_order.setText(getString(R.string.order_1));
				adapterNum.setOrdered(false);
			}else{//倒序
				m_info.setOrdered(true);
				tag_order.setText(getString(R.string.order_2));
				adapterNum.setOrdered(true);
			}
		}
	}	
	
	private OnItemClickListener Selection_Click =new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			setTag(adapterIcon.getItem(arg2));
			grid_selection.setVisibility(View.GONE);
			try {
				Drawable drawable= getResources().getDrawable(R.drawable.ic_bt_down);  
				/// 这一步必须要做,否则不会显示.  
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
				tag_text.setCompoundDrawables(null,null,drawable,null);  
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
}
