package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.ui.Activity_MovieInfo;
import cn.pipi.mobile.pipiplayer.ui.Fragment_Dianying;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleArrayAdapter;
import com.umeng.analytics.MobclickAgent;

public class Shouye_Adapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter{

	private int mHeaderResId;

    private LayoutInflater mInflater;

    private int mItemResId;

    private List<MovieInfo> mItems;
    
    private Context mContext ;
    private FragmentManager fm;
    private BitmapManager bmpManager;
	public Shouye_Adapter(Activity context, List<MovieInfo> items, int headerResId,
			int itemResId,FragmentManager fm) {
		// TODO Auto-generated constructor stub
		this.bmpManager = BitmapManager.getInstance();
		if(context==null){
			mContext=VLCApplication.getAppContext();
		}else{
			mContext=context;
		}
		this.fm = fm;
		init(mContext, items, headerResId, itemResId);
	}
	
	@Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public long getHeaderId(int position) {
    	MovieInfo item = getItem(position);
        return item.getHeaderId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mHeaderResId, parent, false);
            holder = new HeaderViewHolder();
            holder.img = (ImageView)convertView.findViewById(R.id.img);
            holder.text = (TextView)convertView.findViewById(R.id.text);
            holder.text_more = (TextView)convertView.findViewById(R.id.text_more);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder)convertView.getTag();
        }
        if(mItems==null||position<0||position>=mItems.size())return convertView;
        final MovieInfo item = getItem(position);
        holder.text.setText(item.getHeader());
        if(item.getType().equals("电影")){
        	holder.img.setImageResource(R.drawable.bt_ic_dianying);
        }else if(item.getType().equals("电视剧")){
        	holder.img.setImageResource(R.drawable.bt_ic_dianshiju);
        }else if(item.getType().equals("动漫")){
        	holder.img.setImageResource(R.drawable.bt_ic_dongman);
        }else if(item.getType().equals("综艺")){
        	holder.img.setImageResource(R.drawable.bt_ic_zongyi);
        }
        convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Fragment_Dianying dianying=new Fragment_Dianying();
				Bundle b=new Bundle();
				b.putString("type", item.getType());
				dianying.setArguments(b);
				
				FragmentTransaction t = fm.beginTransaction();
				t.replace(R.id.center_frame, dianying);
				t.commit();
				MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Shouye_More", item.getHeader());
			}
		});
        
        return convertView;
    }

    @Override
    public MovieInfo getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mItemResId, parent, false);
            holder = new ViewHolder();
            holder.name=(TextView) convertView.findViewById(R.id.name);  
            holder.img=(ImageView)convertView.findViewById(R.id.img);
            holder.state=(TextView)convertView.findViewById(R.id.state);
            holder.dafen_num=(TextView)convertView.findViewById(R.id.dafen_num); 
            holder.subtitle=(TextView)convertView.findViewById(R.id.subtitle); 
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
            holder.img.setImageResource(R.drawable.item_moive_list);//设置默认图片，避免�?成图片看起来混乱
        }

        final MovieInfo item = getItem(position);
        holder.name.setText(item.getName());
        holder.state.setText(item.getState());
        holder.subtitle.setText(item.getSubtitle());
    	holder.dafen_num.setText(item.getDafen_num());
    	bmpManager.loadBitmap(item.getImg(), holder.img);
    	convertView.setOnClickListener(new OnClickListener() {
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				 Intent screenParam = new Intent();
 				 screenParam.putExtra("movieInfo", item);
 				 screenParam.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
 				 screenParam.setClass( mContext, Activity_MovieInfo.class );
 				 mContext.startActivity(screenParam);
 				MobclickAgent.onEvent(VLCApplication.getAppContext(), "Click_Shouye", item.getHeader());
 			}
 		});
        return convertView;
    }

    private void init(Context context, List<MovieInfo> items, int headerResId, int itemResId) {
        this.mItems = items;
        this.mHeaderResId = headerResId;
        this.mItemResId = itemResId;
        mInflater = LayoutInflater.from(context);
    }

    protected class HeaderViewHolder {
        public ImageView img;
        public TextView text;
        public TextView text_more;
    }

    protected class ViewHolder {
    	TextView name;  
        ImageView img;  
        TextView state; 
        TextView dafen_num; 
        TextView subtitle; 
    }
    

}
