package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
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
public class SearchKeyAdapter extends BaseAdapter{
	Context mContext;
	List<String> list;
	ViewHolder holder;
	public SearchKeyAdapter(Context context){
		mContext = context;
	}
	//@Override
	public int getCount() {
		if(list==null)return 0;
		return list.size();
	}
	public void research(List<String> arraylist){
		this.list=arraylist;
		notifyDataSetChanged();
	}
	public void clear(){
		if(list!=null){
			list.clear();
			notifyDataSetChanged();
		}
	}
	//@Override
	public String getItem(int position) {
		return list.get(position);
	}
	//@Override
	public long getItemId(int position) {
		return position;
	}
	//@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView != null){
			holder=(ViewHolder)convertView.getTag();
		}else{
			holder=new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate( R.layout.item_search_key, null);
			holder.text=(TextView) convertView.findViewById(R.id.text); 
			 convertView.setTag(holder);
		}
		if(list==null||list.size()==0||list.size()<=position)return convertView;
		holder.text.setText(list.get(position));
		return convertView;
	}
	 class ViewHolder{  
            TextView text;  
        }  
}