package cn.pipi.mobile.pipiplayer.asyctask;

import java.net.URLEncoder;
import java.util.ArrayList;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

	public		class Search_Key_AsyncTask extends AsyncTask<String, Void, ArrayList<String>> {

				private boolean isNetWork;
				private Handler handler;
				public Search_Key_AsyncTask(Handler handler) {
					// TODO Auto-generated constructor stub
					this.handler=handler;
					if(HandlerUtil.isConnect()){
						isNetWork=true;
					}
				}
				@Override
				protected void onPostExecute(ArrayList<String> result) {
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
				
				protected ArrayList<String> doInBackground(String... params){
					if (isNetWork) {
						ArrayList<String> arraylist=null;
						try {
							String urlPath="http://ms.pipi.cn/suggest?q="+URLEncoder.encode(params[0], "utf-8")+"&type=1&num=10";
							arraylist =XMLPullParseUtil.getEditSearchData(urlPath, handler);
						} catch (Exception e) {
							// TODO: handle exception
						}
						return arraylist;
					}
					return null;
				}
			}