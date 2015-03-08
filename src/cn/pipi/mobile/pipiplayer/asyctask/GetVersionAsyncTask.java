package cn.pipi.mobile.pipiplayer.asyctask;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

	public	class GetVersionAsyncTask extends AsyncTask<String, Void, String> {
			// 判断网络
			private boolean isNetworkNorol = false;
			private Context context;
			private Handler handler;
			public GetVersionAsyncTask(Context context,Handler handler) {
				this.context = context;
				this.handler=handler;
			}
			//@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				if (isNetworkNorol) {
					//DBHelperDao.getDBHelperDaoInstace(context).
					XMLPullParseUtil.getServiceVersionName(context,PipiPlayerConstant.getInstance().VERSIONINFO,handler);
				}
				return null;
			}
			//@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				if (HandlerUtil.isConnect()) {
					isNetworkNorol = true;
				} else {
					HandlerUtil.sendMsgToHandler(handler,
							PipiPlayerConstant.NONETWORK);
				}
				super.onPreExecute();
			}
			//@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				
				super.onPostExecute(result);
			}

			//@Override
			protected void onProgressUpdate(Void... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}
		}