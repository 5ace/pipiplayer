package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;

 public class Video_MovieTag_Adapter extends BaseAdapter{
    private Context mContext;
    List<String> list;
    String tag="";
    public Video_MovieTag_Adapter(List<String> list) {
        mContext = VLCApplication.getAppContext();
        this.list=list;
    }
    public int getCount() {
    	if(list==null)return 0;
        return list.size();
    }

    public String getItem(int i) {
        return list.get(i);
    }

    public long getItemId(int i) {
        return i;
    }
    public String getTag() {//排序后的集数
    return tag;
	}
	public void setTag(String tag) {//当前观看的集数
		this.tag=tag;
		if(tag==null){
			tag="";
		}
		notifyDataSetChanged();
	}
	
	
	public List<String> getList() {
		return list;
	}
	public View getView(int position, View convertView, ViewGroup vg) {
        // TODO Auto-generated method stub
		 ViewHolder holder;  

        if(convertView!=null){  
            holder=(ViewHolder)convertView.getTag();  
        }else{  
        	holder=new ViewHolder();
        	convertView=LayoutInflater.from(mContext).inflate(R.layout.item_tag_list, null);
        	holder.tag_text=(TextView) convertView.findViewById(R.id.tag_text);
        	holder.tag_img=(ImageView) convertView.findViewById(R.id.tag_img);
        	convertView.setTag(holder);  
        }  
        if (list==null|| position >= list.size()||position<0  ) return convertView;
        holder.tag_text.setText(list.get(position));
        try {
			holder.tag_img.setImageResource(PipiPlayerConstant.getInstance().sourcesList.get(list.get(position)));
		} catch (Exception e) {
			// TODO: handle exception
		}
        return convertView;
    }
    class ViewHolder{  
        TextView tag_text; 
        ImageView tag_img;  
    }  
}