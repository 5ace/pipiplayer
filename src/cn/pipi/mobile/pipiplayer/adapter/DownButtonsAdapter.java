package cn.pipi.mobile.pipiplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.DataUtil;

 public class DownButtonsAdapter extends BaseAdapter{
    private Context mContext;
    private ViewHolder holder=new ViewHolder();  
    private ArrayList<String> list;
    private DBHelperDao dao;
    private MovieInfo m_info;
    private boolean ordered;//默认反序
    public DownButtonsAdapter(Context c ,MovieInfo m_info) {
        mContext = c;
        this.m_info=m_info;
        dao = DBHelperDao.getDBHelperDaoInstace();
        reflash();
    }
    public int getCount() {
    	if(list==null)return 0;
        return list.size();
    }

    public Object getItem(int i) {
        return i;
    }

    public long getItemId(int i) {
        return i;
    }
    public void reflash(){
    	 try {
         	this.list=m_info.getPlayList().get(m_info.getTag());
         	notifyDataSetChanged();
 		} catch (Exception e) {
 			// TODO: handle exception
 		}
    }
    public void setOrdered(boolean ordered) {
		this.ordered = ordered;
		notifyDataSetChanged();
	}
	public View getView( final int position, View convertView, ViewGroup vg) {
        // TODO Auto-generated method stub
        if(convertView!=null){  
            holder=(ViewHolder)convertView.getTag();  
        }else{  
        	holder=new ViewHolder();
        	convertView=LayoutInflater.from(mContext).inflate(R.layout.item_num_grid, null);
        	holder.title=(TextView) convertView.findViewById(R.id.text);  
        	convertView.setTag(holder);  
        }  
        if (list==null|| position >= list.size()||position<0 ) return convertView;
        if(ordered)holder.title.setText(String.valueOf(position+1));//从1-最后
        else holder.title.setText(String.valueOf(list.size()-position));//从最后到1
        
        
        final String moviepath = list.get(ordered?position:list.size()-1-position);
        
        final  boolean isChecked=DownCenter.getExistingInstance().isExsitJob(moviepath);      
        if(isChecked){
        		holder.title.setTextColor(Color.WHITE);
                holder.title.setBackgroundResource(R.drawable.item_num_grid_3);
       }else{
        		holder.title.setTextColor(Color.BLACK);
                holder.title.setBackgroundResource(R.drawable.item_num_grid_1);
            }
        convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Movie_Down", m_info.getType());
				  if(isChecked){
					  dao.delSingleSmovieStore(moviepath);
					  //删除数据，为了刷新界面
					  final DownTask downTask =DownCenter.getExistingInstance().getTaskByUrl(moviepath);
						if (downTask==null) {
							return;
						}
					  DownCenter.getExistingInstance().getDownTaskList().remove(downTask);
					  notifyDataSetChanged();
					  DataUtil.getToast(R.string.removedowntask);
					  //删除源文件
					  new Thread(new Runnable() {
							public void run() {
								try {
									DownCenter.getExistingInstance().delDownTask(downTask);//取消下载任务
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
								}
							}
						}).start();
					  
				  }else {  //执行下载任务
				  DownLoadInfo  down=new DownLoadInfo(m_info.getId(),m_info.getName(),
							  m_info.getImg(),moviepath,ordered?position:list.size()-1-position,list);
				  Log.i("TAG999", "insertMovieStore positionID==="+(ordered?position:list.size()-1-position));
				  down.setDownTag(m_info.getTag());
				if(DataUtil.Check3GNet(mContext)){
              		DataUtil.show3GTipsToDown(mContext,down);
              	}else{
              		DownCenter.getExistingInstance().addJob(down);
   				     dao.insertMovieStore(down);
   				     DataUtil.getToast(R.string.adddowntask);
              	}
				  
				  }
				  
				  notifyDataSetChanged();
			}
		});
        return convertView;
    }
    class ViewHolder{  
        TextView title;  
    }  
}