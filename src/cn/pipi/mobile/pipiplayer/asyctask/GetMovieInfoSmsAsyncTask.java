package cn.pipi.mobile.pipiplayer.asyctask;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import cn.pipi.mobile.pipiplayer.beans.MovieSms;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;


public class GetMovieInfoSmsAsyncTask extends AsyncTask<String, Void, ArrayList<MovieSms>>{
    
     private int  currrentIndex=1;
	 private boolean isNetWork;
	 private Handler handler;
	public GetMovieInfoSmsAsyncTask(int currrentIndex,Handler handler) {
		this.currrentIndex=currrentIndex;
		this.handler=handler;
		if(HandlerUtil.isConnect()){
			isNetWork=true;
		}
	}
	@Override
	protected void onPostExecute(ArrayList<MovieSms> result) {
		// TODO Auto-generated method stub
		if(!isNetWork){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NONETWORK);
			return;
		}
		if(result == null){
			//HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NO_DATA_RETURN);
		}else if(result.size()!=0){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.EXEC_NORMOL,result);
		}else if(result.size()==0){
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.DATA_RETURN_ZERO);
		}
	}
	
	@Override
	protected ArrayList<MovieSms> doInBackground(String... params){
		if (isNetWork) {
			return XMLPullParseUtil.getMovieInfoSms(handler, params[0],currrentIndex);
		}
		return null;
	}
}
