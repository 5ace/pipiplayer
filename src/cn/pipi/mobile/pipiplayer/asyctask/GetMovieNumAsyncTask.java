package cn.pipi.mobile.pipiplayer.asyctask;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ProgressBar;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.beans.ZhuanTiInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

public	class GetMovieNumAsyncTask extends AsyncTask<String, Void, Map<String ,List<String>>> {
	private boolean isNetWork;
	private Handler handler;
	private int id;
	public GetMovieNumAsyncTask(Handler handler,int id) {
		this.handler=handler;
		this.id=id;
		if(HandlerUtil.isConnect()){
			isNetWork=true;
		}
	}
	@Override
	protected void onPostExecute(Map<String ,List<String>> result) {
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
	
	protected Map<String ,List<String>> doInBackground(String... params){
		if (isNetWork) { 
			Map<String ,List<String>> result=JsonUtil.getMovieNum(handler,id);
			return result;
		}
		return null;
	}
}