package cn.pipi.mobile.pipiplayer.asyctask;


import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import cn.pipi.mobile.pipiplayer.beans.TypeInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

public	class GetMovieTypeAsyncTask extends AsyncTask<String, Void, Map<String,TypeInfo>> {
	private boolean isNetWork;
	private Handler handler;
	public GetMovieTypeAsyncTask(Handler handler) {
		this.handler=handler;
		if(HandlerUtil.isConnect()){
			isNetWork=true;
		}
	}
	@Override
	protected void onPostExecute(Map<String,TypeInfo> result) {
		if(!isNetWork){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NONETWORK);
			return;
		}
		if (result != null&&result.size()!=0) {
			//position
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.GETMOVIETYPE,result);
		} else {
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NO_DATA_RETURN);
		}
	}

    @Override
    protected void onCancelled() {}
	
	protected Map<String,TypeInfo> doInBackground(String... params){
		if (isNetWork) { 
			return JsonUtil.getMovieType(handler);
		}
		return null;
	}
}