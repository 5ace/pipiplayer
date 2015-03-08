package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.HistroyListAdapter.ViewHolder;
import cn.pipi.mobile.pipiplayer.beans.MovieSms;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
public class MovieSmsAdapter extends BaseAdapter{
	private List<MovieSms> list;
	private LayoutInflater inflater;
	public MovieSmsAdapter(){
		inflater = LayoutInflater.from(VLCApplication.getAppContext());
	}
	//@Override
	public int getCount() {
		if(list==null)return 0;
		return list.size();
	}
	public void reflash(List<MovieSms> arraylist){
		if(list!=null){
			   list.addAll(arraylist);
			}else{
			   this.list=arraylist;
			}
		notifyDataSetChanged();
	}
	//@Override
	public Object getItem(int position) {
		return position;
	}
	//@Override
	public long getItemId(int position) {
		return position;
	}
	//@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder;  

		if(convertView != null){
			holder=(ViewHolder)convertView.getTag();
		}else{
			holder=new ViewHolder();
			convertView = inflater.inflate( R.layout.item_movie_sms, null);
			holder.userName=(TextView) convertView.findViewById(R.id.userName); 
			holder.time=(TextView) convertView.findViewById(R.id.time); 
			holder.content=(TextView) convertView.findViewById(R.id.content); 
			
			convertView.setTag(holder);
		}
		if(list==null||list.size()<1)return convertView;
		MovieSms movieSms=list.get(position);
		
		holder.userName.setText(movieSms.getUserName());
		holder.time.setText(movieSms.getTime());
		holder.content.setText(movieSms.getContent());
		return convertView;
	}
	 class ViewHolder{  
            TextView userName;  
            TextView content;  
            TextView time;  
        }  
}