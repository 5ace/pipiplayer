package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.MD5Util;
import cn.pipi.mobile.pipiplayer.adapter.HistroyListAdapter.ViewHolder;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;

public class SaveListAdapter extends BaseAdapter {
	List<DownLoadInfo> list;
	private Context mContext;
	private BitmapManager bmpManager;
	 private boolean deleteVisible=false;
	 private DBHelperDao dao;
	 private Handler handler;
	 private final int DELETElAYOUT=2015;//刷新影片删除界面
	 private LayoutInflater inflate;
	public SaveListAdapter(Context c,List<DownLoadInfo> list,Handler handler) {
		mContext = c;
		this.list = list;
		this.handler=handler;
		this.bmpManager =  BitmapManager.getInstance();
         dao=DBHelperDao.getDBHelperDaoInstace();
         inflate=LayoutInflater.from(mContext);
	}

	public void DeleteList() {//删除数据
	if(list==null||list.size()==0)return;
		for(int i=list.size()-1;i>-1;i--){//倒序删除，避免循环错误
			DownLoadInfo movie=list.get(i);
			if(movie.isDelete()){
				list.remove(movie);
				dao.delSingleSmovieSave(movie.getDownID());
			}
		}
		notifyDataSetChanged();
		setDeleteInVisible();
	}

	public void setDeleteAllList(Button deleteall) {//将所有数据标记为可删除
		boolean delete=getNotSelete();//含有未被选中的则全选  如果已经被全选则全部取消
		if(delete)deleteall.setText(mContext.getString(R.string.notselectall));
		else deleteall.setText(mContext.getString(R.string.selectall));
		for(DownLoadInfo downInfo :list)
			downInfo.setDelete(delete);
		notifyDataSetChanged();
		Message message=new Message();
		message.what=DELETElAYOUT;
		message.obj=getSeleteCount();
		handler.sendMessage(message);
		
	}

	public int getCount() {
		if (null == list)
			return 0;
		return list.size();
	}
	public boolean getNotSelete() {//是否含有未被选中的
		boolean delete=false;
		if(list==null||list.size()==0)
			return delete;
		for(DownLoadInfo downInfo:list){
			if(!downInfo.isDelete()){
				delete=true;
				break;
			}
		}
		return delete;
	}
	public int getSeleteCount() {//被选中即将删除的个数
		if(list==null||list.size()==0)return 0;
		int num=0;
		for(DownLoadInfo movieInfo:list){
			if(movieInfo.isDelete())num++;
		}
		return num;
	}
	public DownLoadInfo getItem(int i) {
		if(list==null)return null;
		return list.get(i);
	}

	public long getItemId(int i) {
		return i;
	}
    public DownLoadInfo getData(int i){
    	return list.get(i);
    }
    public boolean isDeleteVisible() {
		return deleteVisible;
	}

	public boolean changeDeleteVisible() {
		this.deleteVisible = !deleteVisible;
		if(!deleteVisible){////重置多选框状态
			for(DownLoadInfo downInfo :list){
				if(downInfo.isDelete())
					downInfo.setDelete(false);
			}
		}
		notifyDataSetChanged();
		return deleteVisible;
	}
    public void setDeleteInVisible() {//点击tab时，默认为不可见
		this.deleteVisible = false;
		notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup vg) {
		// TODO Auto-generated method stub
		 ViewHolder holder;  

		if(convertView!=null){  
            holder=(ViewHolder)convertView.getTag(); 
            holder.moive_img.setImageResource(R.drawable.item_moive_list);//设置默认图片，避免�?成图片看起来混乱
        }else{ 
        	holder=new ViewHolder();
            convertView=inflate.inflate(R.layout.item_play_history, null);  
            holder.title=(TextView) convertView.findViewById(R.id.moive_title);  
            holder.moive_img=(ImageView)convertView.findViewById(R.id.moive_img); 
            holder.delete=(CheckBox) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        }
		holder.moive_img.setImageResource(R.drawable.item_moive_list);//设置默认图片，避免造成图片看起来混乱
		if(list==null||list.size()==0||position<0|| position>=list.size())return convertView;
		final DownLoadInfo downInfo=list.get(position);
		holder.title.setText(downInfo.getDownName());
		 
		 
		 bmpManager.loadBitmap(downInfo.getDownImg(),  holder.moive_img);
		 
		//删除按钮
		 if(!deleteVisible) holder.delete.setVisibility(8);
         else {
        	 holder.delete.setVisibility(0);
        	 holder.delete.setChecked(downInfo.isDelete());//
        	 holder.delete.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					downInfo.setDelete(!downInfo.isDelete());
					
					Message message=new Message();
					message.what=DELETElAYOUT;
					message.obj=getSeleteCount();
					handler.sendMessage(message);
				}
			});
        	 
         }
			
		 
		return convertView;
	}

	 class ViewHolder{  
	        TextView title;  
	        ImageView moive_img;  
	        CheckBox delete;
	    } 
}