package cn.pipi.mobile.pipiplayer.ui;

import java.net.URLEncoder;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_Advice extends SherlockFragmentActivity implements OnClickListener{
	private EditText et_common,et_QQ,et_email,et_phoneNum;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advice);
		prepareActionBar();
		
		et_common=(EditText) findViewById(R.id.et_common);
		et_QQ=(EditText) findViewById(R.id.et_QQ);
		et_email=(EditText) findViewById(R.id.et_email);
		et_phoneNum=(EditText) findViewById(R.id.et_phoneNum);
		findViewById(R.id.bt_clear).setOnClickListener(this);
		findViewById(R.id.bt_submit).setOnClickListener(this);
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(R.string.advice);
    }
	@Override
    protected void onResume() {
            // TODO Auto-generated method stub
            super.onResume();
            MobclickAgent.onResume(this); 
    }
    @Override
    protected void onPause() {
            // TODO Auto-generated method stub
            super.onPause();
            MobclickAgent.onPause(this);
    }  
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
       	          finish();
       	     break;
        }
        return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.bt_submit){
			String commontext=et_common.getText().toString();
			String phoneNumtext=et_phoneNum.getText().toString();
			String QQtext=et_QQ.getText().toString();
			String emailtext=et_email.getText().toString();
			//验证信息
			if(TextUtils.isEmpty(commontext)){
				showErrText(et_common, "内容不能为空");
			}else if(!TextUtils.isEmpty(phoneNumtext)
					&&!DataUtil.isTelePhone(phoneNumtext)){
				showErrText(et_phoneNum, "手机号码格式错误");
				
			}else if(!TextUtils.isEmpty(emailtext)&&!DataUtil.isAvailableEmail(emailtext)){
                showErrText(et_email, "邮箱格式错误");
                
			}else{
				//开始提交
				try {
					String url="http://mu.pipi.cn/suggestCommit.action?"
							+"adviceContent="+URLEncoder.encode(commontext, "utf-8")
							+"&tp="+phoneNumtext
							+"&qq="+QQtext
							+"&em="+emailtext
							+"&deviceVersion="+android.os.Build.BRAND+android.os.Build.MODEL
							+"&iOSVersion="+android.os.Build.VERSION.SDK_INT
							+"&UUID="+PipiPlayerConstant.getInstance().UUID;
					Log.i("TAG999", "url = "+ url);
					new ExecMovie().execute(url);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}else if(v.getId()==R.id.bt_clear){
			et_common.setText("");
			et_QQ.setText("");
			et_email.setText("");
			et_phoneNum.setText("");
		}
		
	}
	public void showErrText(EditText edit,String string){
		CharSequence html = Html.fromHtml("<font color='red'>"+string+"</font>"); 
		edit.setError(html);
		edit.requestFocus();
		//SpannableStringBuilder style=new SpannableStringBuilder(string); 
		//style.setSpan(new ForegroundColorSpan(Color.RED), 0, string.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE ); 
		//edit.setError(style);
	}
	
	class ExecMovie extends AsyncTask<String, Void, Integer> {

		// 判断网络
		private boolean isNetworkNorol = false;
		//@Override
		protected Integer doInBackground(String... params) {
			return XMLPullParseUtil.WriteMovieInfoSms(handler,"", params[0]);
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
		protected void onPostExecute(Integer result) {
			if(isNetworkNorol){
				if (result == -1) {
					HandlerUtil.sendMsgToHandler(handler,
							PipiPlayerConstant.NO_DATA_RETURN);
				} else if(result == 1){
					HandlerUtil.sendMsgToHandler(handler,
							PipiPlayerConstant.EXEC_NORMOL);
				}
			}
			super.onPostExecute(result);
		}

		//@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

	}
	private Handler handler = new Handler() {
		//@Override
		public void handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case PipiPlayerConstant.EXEC_NORMOL:
				DataUtil.getToast(getString(R.string.submit_comment));
				finish();
				break;
			case PipiPlayerConstant.NO_DATA_RETURN:
				DataUtil.getToast(getString(R.string.NO_DATA_RETURN));
				break;
			case PipiPlayerConstant.NONETWORK:
				DataUtil.getToast(getString(R.string.NONETWORK));
				break;
			default:
				break;
			}
			super.handleMessage(message);
		}

	};
	
}
