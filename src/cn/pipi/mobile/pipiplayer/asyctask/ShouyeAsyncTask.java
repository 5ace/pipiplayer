package cn.pipi.mobile.pipiplayer.asyctask;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

public	class ShouyeAsyncTask extends AsyncTask<String, Void, List<MovieInfo>> {
	private boolean isNetWork;
	private Handler handler;
	public ShouyeAsyncTask(Handler handler) {
		this.handler=handler;
		if(HandlerUtil.isConnect()){
			isNetWork=true;
		}
	}
	@Override
	protected void onPostExecute(List<MovieInfo> result) {
		if(!isNetWork){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NONETWORK);
			return;
		}
		if(result == null){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NO_DATA_RETURN);
		}else if(result.size()!=0){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.EXEC_NORMOL,result);
		}else if(result.size()==0){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.DATA_RETURN_ZERO);
		}
	}

    @Override
    protected void onCancelled() {}
	
	protected List<MovieInfo> doInBackground(String... params){
		if (isNetWork) { 
			List<MovieInfo> result=JsonUtil.getHomeData(handler);
			return result;
		}
		return null;
	}
}