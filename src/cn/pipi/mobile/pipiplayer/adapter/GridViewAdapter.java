package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.beans.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcException;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoPlayerActivity;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.ui.Activity_DownLoad;
import cn.pipi.mobile.pipiplayer.ui.Activity_Video;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.MD5Util;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;

public class GridViewAdapter extends BaseAdapter {
	public  static String TAG = "GridViewAdapter";
	protected static final int ACTION_UPDATE_LOADBAR = 0x1024;
	private LayoutInflater mLayoutInflater;
	// 数据源
    private List<DownTask > mTaskList;
	private BitmapManager bmpManager;
	private Context context;
	 private Handler handler;
	 private boolean deleteVisible=false;
	 private final int DELETElAYOUT=2015;//刷新影片删除界面
	 
	public GridViewAdapter(Context mContext,Handler handler) {
		context=mContext;
		mLayoutInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.bmpManager = BitmapManager.getInstance();
		try {
			mTaskList = DownCenter.getInstance().getDownTaskList();
		} catch (LibVlcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         this.handler=handler;
         
	}
	public void DeleteList() {//删除数据
		if(mTaskList==null||mTaskList.size()==0)return;
			for(int i=mTaskList.size()-1;i>-1;i--){//倒序删除，避免循环错误
				DownLoadInfo downInfo=mTaskList.get(i).getDownLoadInfo();
				if(downInfo.isDelete()){
					remove(mTaskList.get(i));
				}
			}
			notifyDataSetChanged();
			setDeleteInVisible();
		}

		public void setDeleteAllList(Button deleteall) {//将所有数据标记为可删除
			boolean delete=getNotSelete();//含有未被选中的则全选  如果已经被全选则全部取消
			if(delete)deleteall.setText(context.getString(R.string.notselectall));
			else deleteall.setText(context.getString(R.string.selectall));
			for(DownTask task:mTaskList){
				DownLoadInfo downInfo=task.getDownLoadInfo();
				downInfo.setDelete(delete);
			}
			notifyDataSetChanged();
			Message message=new Message();
			message.what=DELETElAYOUT;
			message.obj=getSeleteCount();
			handler.sendMessage(message);
			
		}
		
	public boolean isDeleteVisible() {
		return deleteVisible;
	}
	public void setDeleteInVisible() {//点击tab时，默认为不可见
		this.deleteVisible = false;
		notifyDataSetChanged();
	}
	public boolean changeDeleteVisible() {
		this.deleteVisible = !deleteVisible;
		if(deleteVisible){//重置多选框状态
			for(DownTask task:mTaskList){
				DownLoadInfo downInfo=task.getDownLoadInfo();
				if(downInfo.isDelete())
					downInfo.setDelete(false);
			}}
		notifyDataSetChanged();
		return deleteVisible;
	}
	public boolean getNotSelete() {//是否含有未被选中的
		boolean delete=false;
		if(mTaskList==null||mTaskList.size()==0)
			return delete;
		for(DownTask task:mTaskList){
			DownLoadInfo downInfo=task.getDownLoadInfo();
			if(!downInfo.isDelete()){
				delete=true;
				break;
			}
		}
		return delete;
	}
	public int getSeleteCount() {//被选中即将删除的个数
		if(mTaskList==null||mTaskList.size()==0)return 0;
		int num=0;
		for(DownTask task:mTaskList){
			DownLoadInfo downInfo=task.getDownLoadInfo();
			if(downInfo.isDelete())num++;
		}
		return num;
	}
	//@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mTaskList==null)return 0;
		return mTaskList.size();// 用于循环滚动
	}

	//@Override
	public DownTask getItem(int position) {
		return  mTaskList.get(position);

	}

	//@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position ;

	}
	//@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 ViewHolder holder = new ViewHolder();  
	//	if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_download_list, null);
			  holder.movieImgView= (ImageView) convertView.findViewById(R.id.moive_img);
			  holder.text_icon=(TextView)convertView.findViewById(R.id.text_icon); 
	             holder.name=(TextView) convertView.findViewById(R.id.gvitem_name);  
	             holder.speed=(TextView) convertView.findViewById(R.id.gvitem_speed);
	             holder.size=(TextView) convertView.findViewById(R.id.gvitem_size);
	             holder.bar=(TextView) convertView.findViewById(R.id.gvitem_bar);
	             holder.delete=(CheckBox) convertView.findViewById(R.id.delete);
	             holder.gvitem_icon=(ImageView) convertView.findViewById(R.id.gvitem_icon);
	     /*        convertView.setTag(holder);  
		} else {  
			            holder = (ViewHolder) convertView.getTag();  
			        }  */ 	
		if(mTaskList==null||mTaskList.size()==0||position<0|| position>=mTaskList.size())return convertView;
	    final DownLoadInfo downInfo=mTaskList.get(position).getDownLoadInfo();
	    if(downInfo==null)                     return convertView;
	    
	    StringBuffer name=new StringBuffer(downInfo.getDownName());
	    name.append(".");
	    name.append(downInfo.getDownPosition()+1);
		holder.name.setText(name.toString());
		bmpManager.loadBitmap(downInfo.getDownImg(), holder.movieImgView);
		try {
			holder.text_icon.setText(downInfo.getDownTag());
			holder.gvitem_icon.setImageResource(PipiPlayerConstant.getInstance().sourcesList.get(downInfo.getDownTag()));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		holder.movieImgView.setOnClickListener(new OnClickListener() {//点击图片开始播放
			public void onClick(View v) {
				if(MD5Util.getFromHttpfilm(downInfo.getDownUrl())&&
						downInfo.getDownState()!=DownTask.TASK_FINISHED){
					DataUtil.getToast("聚合资源下载完才可观看！");
					return;
				}
				if(downInfo.getDownState()!=DownTask.TASK_FINISHED
						&&DataUtil.Check3GNet(context)){//未下载完成的才需要检测网络，避免用流量下载
					Message message=new Message();
					message.what=PipiPlayerConstant.playmovieTip;
					message.obj=downInfo;
					handler.sendMessage(message);
				}else
					
					Activity_Video.start((Activity_DownLoad)context, downInfo);
			}
		});
		
		updatebarProgress(holder,downInfo);
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
	
	  public class ViewHolder {   
	         public TextView name;  
	         public TextView speed;  
	         public TextView size;  
	         public TextView bar;  
	         public ImageView  del;
	         public ImageView movieImgView ;
	         public TextView text_icon;
	         public CheckBox delete;
	         public ImageView gvitem_icon;
		     }  
	 
	    
	public  void updatebarProgress(ViewHolder m_holder,DownLoadInfo downInfo) {//下载中更新
	//	ViewHolder holder = ViewHolders.get(uri);
		 if(m_holder==null||downInfo==null){
		   return;
		 }
		 if(MD5Util.getFromHttpfilm(downInfo.getDownUrl())){//聚合名字
			 if(downInfo.getTaskList()!=null&&downInfo.getTaskList().size()>1){
				 StringBuffer string=new StringBuffer();
				 string.append("(");
				 string.append(downInfo.getDownIndex()+1);
				 string.append("/");
				 string.append(downInfo.getTaskList().size());
				 string.append(")");
				 string.append(downInfo.getDownName());
				 string.append(".");
				 string.append(downInfo.getDownPosition()+1);
				 m_holder.name.setText(string.toString());
			 }
		 }
		 long progress=downInfo.getDownProgress();
		 long speed=downInfo.getDownSpeed();
		 long size=downInfo.getDownTotalSize();
		 m_holder.bar.setText((int)progress+"%");
		 if(downInfo.getDownState()==DownTask.TASK_FileMerge){
        	 m_holder.speed.setText(context.getString(R.string.task_merge));
        	 m_holder.name.setTextColor(Color.BLACK);
			 try {
				Thread.sleep(500);//检测下一个任务
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }else if(downInfo.getDownState()==DownTask.TASK_FINISHED){
			 m_holder.bar.setVisibility(View.GONE);
			 m_holder.size.setText(SdcardUtil.formatSize(context,downInfo.getDownTotalSize()));
        	 m_holder.speed.setText(context.getString(R.string.task_finish));
        	 m_holder.name.setTextColor(Color.BLACK);
			 m_holder=null;
		 }else if(downInfo.getDownState()==DownTask.TASK_DOWNLOADING){
			 m_holder.speed.setText(SdcardUtil.formatSize(context,speed) +"/s");
			 StringBuffer string=new StringBuffer();
			 if(progress!=100.0){
				 string.append(SdcardUtil.formatSize(context,(long)(size*progress/1E2)));
				 string.append("/");
			 }
			 string.append(SdcardUtil.formatSize(context,size));
			 m_holder.name.setTextColor(Color.RED);
			 m_holder.size.setText(string);
		 }else if(downInfo.getDownState()==DownTask.TASK_WIFI_ERROR){
        	 m_holder.speed.setText(context.getString(R.string.task_error));
        	 m_holder.name.setTextColor(Color.BLACK);
        	 StringBuffer string=new StringBuffer();
			 if(progress!=100.0){
				 string.append(SdcardUtil.formatSize(context,(long)(size*progress/1E2)));
				 string.append("/");
			 }
			 string.append(SdcardUtil.formatSize(context,size));
			 m_holder.size.setText(string);
		 }else if(downInfo.getDownState()==DownTask.TASK_PAUSE_DOWNLOAD){
			 m_holder.bar.setVisibility(View.VISIBLE);
			 m_holder.speed.setText(context.getString(R.string.task_pause));
			 m_holder.bar.setText((int)progress+"%");
			 StringBuffer string=new StringBuffer();
			 if(progress!=100.0){
				 string.append(SdcardUtil.formatSize(context,(long)(size*progress/1E2)));
				 string.append("/");
			 }
			 string.append(SdcardUtil.formatSize(context,size));
			 m_holder.size.setText(string);
		 }else if(downInfo.getDownState()==DownTask.TASK_WAITING_DOWNLOAD
				 ||downInfo.getDownState()==DownTask.TASK_RESUME_DOWNLOAD){
			 m_holder.bar.setVisibility(View.VISIBLE);
			 m_holder.speed.setText(context.getString(R.string.task_waitting));
			 m_holder.bar.setText((int)progress+"%");
			 StringBuffer string=new StringBuffer();
			 if(progress!=100.0){
				 string.append(SdcardUtil.formatSize(context,(long)(size*progress/1E2)));
				 string.append("/");
			 }
			 string.append(SdcardUtil.formatSize(context,size));
			 m_holder.size.setText(string);
		 }
		 
		 notifyDataSetChanged();
	}

	// 删除选中项
			public void remove(final DownTask downTask) {
				mTaskList.remove(downTask);
				 notifyDataSetChanged();//立即刷新，避免造成删除文件卡顿现象，文件删除交给其他线程
				 new Thread(new Runnable() {
						public void run() {
							try {
								DownCenter.getExistingInstance().delDownTask(downTask);//取消下载任务
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}).start();
			}
			
	
	
}
