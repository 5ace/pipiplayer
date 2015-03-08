package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.HistroyListAdapter.ViewHolder;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;

 public class IconImageAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> list;
    public IconImageAdapter(Context c) {
        mContext = c;
    }
    public int getCount() {
    	if(list==null)return 0;
        return list.size();
    }

    public String getItem(int i) {
        return list.get(i);
    }
    
    public void reflash(List<String> list) {
    	 this.list=list;
    	 if(list==null)return;
    	 notifyDataSetChanged();
    }

    public long getItemId(int i) {
        return i;
    }
	public View getView(int position, View convertView, ViewGroup vg) {
        // TODO Auto-generated method stub
		 ViewHolder holder;  

        if(convertView!=null){  
            holder=(ViewHolder)convertView.getTag();  
        }else{  
        	holder=new ViewHolder();
        	convertView=LayoutInflater.from(mContext).inflate(R.layout.item_selection_source, null);
        	holder.text=(TextView) convertView.findViewById(R.id.tag_text); 
        	holder.icon=(ImageView) convertView.findViewById(R.id.tag_img); 
            convertView.setTag(holder);  
        }  
        if (list==null|| position >= list.size()||position<0  ) return convertView;
        
        String key=list.get(position);
        holder.text.setText(key);
        try {
        holder.icon.setImageResource(PipiPlayerConstant.getInstance().sourcesList.get(key));
		} catch (Exception e) {
			// TODO: handle exception
		}
        return convertView;
    }
    class ViewHolder{  
    	ImageView icon;
        TextView text;  
    }  
}