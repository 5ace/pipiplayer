package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;

 public class SelectionAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> list;
    private String string;
    public SelectionAdapter(Context c ,List<String> list,String string) {
        mContext = c;
        this.list=list;
        this.string=string;
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
	public View getView(final int position, View convertView, ViewGroup vg) {
        // TODO Auto-generated method stub
		ViewHolder holder;
        if(convertView!=null){  
            holder=(ViewHolder)convertView.getTag();  
        }else{  
        	holder=new ViewHolder();
        	convertView=LayoutInflater.from(mContext).inflate(R.layout.item_selection_grid, null);
        	holder.title=(TextView) convertView.findViewById(R.id.text);  
        	convertView.setTag(holder);  
        }  
        if (list==null|| position >= list.size()||position<0  ) return convertView;
            holder.title.setText(list.get(position));
        	if(list.get(position).equals(string)){
        		holder.title.setTextColor(Color.WHITE);
                holder.title.setBackgroundColor(Color.RED);
        	}else{
        		holder.title.setTextColor(Color.BLACK);
                holder.title.setBackgroundColor(Color.TRANSPARENT);
                }
        convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				string=list.get(position);
		    	notifyDataSetChanged();
			}
		});
        return convertView;
    }
    class ViewHolder{  
        TextView title;  
    }
}