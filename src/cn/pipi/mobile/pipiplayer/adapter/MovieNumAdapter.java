package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;

 public class MovieNumAdapter extends BaseAdapter{
    ViewHolder holder;  
    List<String> list;
    int positinID=0;
    public MovieNumAdapter(List<String> list) {
        this.list=list;
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
    
    public int getPositinID() {
		return positinID;
	}
	public void setPositinID(int positinID) {
		this.positinID = positinID;
		notifyDataSetChanged();
	}
	public View getView(int position, View convertView, ViewGroup vg) {
        // TODO Auto-generated method stub
        if(convertView==null){  
        	holder=new ViewHolder();
        	convertView=LayoutInflater.from(VLCApplication.getAppContext()).inflate(R.layout.item_num_grid, null);
        	holder.title=(TextView) convertView.findViewById(R.id.text);  
        	convertView.setTag(holder); 
        }else{  
        	holder=(ViewHolder)convertView.getTag();  
        }  
        if (list==null|| position >= list.size()||position<0  ) return convertView;
        
        if(position==getPositinID())
            holder.title.setBackgroundResource(R.drawable.player_num_1);
        else
            holder.title.setBackgroundResource(R.drawable.player_num_0);
        holder.title.setTextColor(Color.WHITE);
        holder.title.setText(String.valueOf(position+1));
        return convertView;
    }
    class ViewHolder{  
        TextView title;  
    }  
   
}