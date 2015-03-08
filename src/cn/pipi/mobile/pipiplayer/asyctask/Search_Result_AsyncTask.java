package cn.pipi.mobile.pipiplayer.asyctask;

import java.util.ArrayList;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

	public		class Search_Result_AsyncTask extends AsyncTask<String, Void, ArrayList<MovieInfo>> {

				private boolean isNetWork;
				private Handler handler;
				public Search_Result_AsyncTask(Handler handler) {
					// TODO Auto-generated constructor stub
					this.handler=handler;
					if(HandlerUtil.isConnect()){
						isNetWork=true;
					}
				}
				@Override
				protected void onPostExecute(ArrayList<MovieInfo> result) {
					// TODO Auto-generated method stub
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
				
				protected ArrayList<MovieInfo> doInBackground(String... params){
					if (isNetWork) {
						ArrayList<MovieInfo> arraylist=null;
						try {
							arraylist =XMLPullParseUtil.getSearhResult(params[0], handler);;
						} catch (Exception e) {
							// TODO: handle exception
						}
						return arraylist;
					}
					return null;
				}
			}