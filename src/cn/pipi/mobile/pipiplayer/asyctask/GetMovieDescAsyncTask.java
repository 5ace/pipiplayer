package cn.pipi.mobile.pipiplayer.asyctask;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

public	class GetMovieDescAsyncTask extends AsyncTask<String, Void, Void> {
	private boolean isNetWork;
	private Handler handler;
	private MovieInfo info;
	public GetMovieDescAsyncTask(Handler handler,MovieInfo info) {
		this.handler=handler;
		this.info=info;
		if(HandlerUtil.isConnect()){
			isNetWork=true;
		}
	}
	@Override
	protected void onPostExecute(Void i) {
		if(!isNetWork){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NONETWORK);
			return;
		}
		if (info != null&&info.getDesc()!=null) {
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.EXEC_NORMOL);
		} else {
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NO_DATA_RETURN);
		}
	}

    @Override
    protected void onCancelled() {}
	
	protected Void doInBackground(String... params){
		if (isNetWork) { 
			JsonUtil.getMovieDesc(handler,info);
		}
		return null;
	}
}