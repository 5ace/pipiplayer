package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.HistroyListAdapter.ViewHolder;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.ui.Activity_MovieInfo;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;

public class MoiveLikeAdapter extends BaseAdapter{
    List<View> m_itemViews;
    private Context mContext;
    private BitmapManager bmpManager;
    List<MovieInfo> list;
    LayoutParams para;
    private LayoutInflater inflate;
    public MoiveLikeAdapter() {
        mContext = VLCApplication.getAppContext();
        this.bmpManager =  BitmapManager.getInstance();
        inflate=LayoutInflater.from(mContext);
    }

    public void reflash(List<MovieInfo> list)
    {   this.list=list;
        this.notifyDataSetChanged();
    }
    public void addAll(List<MovieInfo> mlist)
    {  
    	if(list==null)list=mlist;
    	else list.addAll(mlist);
        this.notifyDataSetChanged();
    }
    public int getCount() {
        if ( null == list ) return 0;
        return list.size();
    }

    public MovieInfo getItem(int i) {
    	if(list==null) return null;
        return list.get(i);
    }

    public long getItemId(int i) {
        return i;
    }
    public View getView(final int position, View convertView, ViewGroup vg) {
        // TODO Auto-generated method stub
   	 ViewHolder holder;  

    	if(convertView!=null){  
            holder=(ViewHolder)convertView.getTag(); 
            holder.moive_img.setImageResource(R.drawable.item_moive_list);//设置默认图片，避免�?成图片看起来混乱
        }else{  
        	holder=new ViewHolder();
        	convertView=inflate.inflate(R.layout.item_moive_like, null);  
            holder.moive_title=(TextView) convertView.findViewById(R.id.moive_title);  
            holder.moive_img=(ImageView)convertView.findViewById(R.id.moive_img);
            holder.moive_score=(TextView)convertView.findViewById(R.id.moive_score); 
            holder.moive_desc=(TextView)convertView.findViewById(R.id.moive_desc);
            convertView.setTag(holder);  
        } 
    	
   	if ( null == list  ||position >= list.size() ||list.size()<1) return convertView;
        MovieInfo beans = (MovieInfo) list.get(position);
          holder.moive_title.setText(beans.getName());
          holder.moive_desc.setText(beans.getDesc());
    	  bmpManager.loadBitmap(beans.getImg(),  holder.moive_img);
          try {
        	  String score=String.valueOf((int)(Float.parseFloat(beans.getDafen_num())*20)/10.0);
        	  holder.moive_score.setText(score);
		} catch (Exception e) {
			// TODO: handle exception
		}
    	 return convertView;
      
    }
    class ViewHolder{  
        TextView moive_title;  
        ImageView moive_img;  
        TextView moive_score; 
        TextView moive_desc; 
    }  
}
