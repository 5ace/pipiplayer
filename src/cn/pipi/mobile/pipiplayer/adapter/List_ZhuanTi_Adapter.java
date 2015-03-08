package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.HistroyListAdapter.ViewHolder;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.beans.ZhuanTiInfo;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.view.MyGridView;

public class List_ZhuanTi_Adapter extends BaseAdapter{
    private Context mContext;
    List<ZhuanTiInfo>  list;
    private BitmapManager bmpManager;
	public List_ZhuanTi_Adapter(Context c, List<ZhuanTiInfo> list ) {
		this.bmpManager = BitmapManager.getInstance();
        mContext = c;
        this.list=list;
    }

	
    public int getCount() {
        if ( null == list ) return 0;
        return list.size();
    }

    public Object getItem(int i) {
        return i;
    }

    public long getItemId(int i) {
        return i;
    }
    public View getView(final int position, View convertView, ViewGroup vg) {
        // TODO Auto-generated method stub
   	 ViewHolder holder;  

    	if(convertView!=null){  
            holder=(ViewHolder)convertView.getTag();
            holder.moive_img.setImageResource(R.drawable.item_moive_list_small);//设置默认图片，避免�?成图片看起来混乱
        }else{  
        	holder=new ViewHolder();
        	convertView=LayoutInflater.from(mContext).inflate(R.layout.item_zhuanti_list, null);  
            holder.zhuanti_name=(TextView) convertView.findViewById(R.id.zhuanti_name);  
            holder.zhuanti_show_name=(TextView)convertView.findViewById(R.id.zhuanti_show_name);
            holder.moive_img=(ImageView)convertView.findViewById(R.id.moive_img); 
            convertView.setTag(holder);  
        } 
    	
   	if ( null == list  ||position >= list.size() ||list.size()<1) return convertView;
        ZhuanTiInfo info= list.get(position);
        holder.zhuanti_name.setText(info.getName());
        holder.zhuanti_show_name.setText(info.getShow_name());
        bmpManager.loadBitmap(info.getImg(), holder.moive_img);
    	return convertView;
      
    }
    class ViewHolder{  
        TextView zhuanti_name;  
        TextView zhuanti_show_name; 
        ImageView moive_img;  
    }  
}
