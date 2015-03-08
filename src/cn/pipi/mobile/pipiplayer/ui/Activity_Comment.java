package cn.pipi.mobile.pipiplayer.ui;

import java.net.URLEncoder;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.parser.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;

public class Activity_Comment extends SherlockFragmentActivity implements OnClickListener{
	private final int FLASHCODE=1;
	BitmapManager bitmapManager;
	 EditText vCode,comment_txt;
	 String movieID,movieName;
	 ImageView imageView;
	 public static String SessionString;
	 private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		mContext=this;
        bitmapManager=BitmapManager.getInstance();
        movieID=getIntent().getStringExtra("MovieID");
        movieName=getIntent().getStringExtra("MovieName");
        comment_txt=(EditText) findViewById(R.id.comment_txt);
        comment_txt.requestFocus();
		vCode=(EditText) findViewById(R.id.vCode);
		imageView=(ImageView) findViewById(R.id.ImageView);
		vCode.setOnClickListener(this);
		imageView.setOnClickListener(this);
		findViewById(R.id.flashCode).setOnClickListener(this);
		findViewById(R.id.bt_submit).setOnClickListener(this);
		prepareActionBar();
		getCodeImageView();
	}
	private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(getString(R.string.comment)+"  "+movieName);
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
		// TODO Auto-generated method stub
		if(v.getId()==R.id.bt_submit){
			String vCodeText=vCode.getText().toString();
			String comment_txtText=comment_txt.getText().toString();
			if(comment_txtText==null||comment_txtText.length()==0){
				 showErrText(comment_txt, "评论不能为空！");
				 
			}else if(vCodeText==null||vCodeText.length()==0){
				comment_txt.setError("请输入验证码！");
				 showErrText(comment_txt, "请输入验证码！");
				 
			}else if(vCodeText==null||vCodeText.length()!=4){
				vCode.setError("验证码不正确！");
				 showErrText(vCode, "验证码不正确！");
				 
			}else{
				try {
					String url="http://user.pipi.cn/action/reviewCommit.jsp?"
				               +"comment_txt="+URLEncoder.encode(comment_txtText, "utf-8")
				               +"&movId="+movieID
				               +"&userName="+URLEncoder.encode(getString(R.string.smsfromandroid), "utf-8")
				               +"&vCode="+vCodeText;
					Log.i("TAG999", "url===="+url);
					 new ExecMovie(mContext).execute(url);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}else if(v.getId()==R.id.flashCode||v.getId()==R.id.ImageView){
			//bitmapManager.loadBitmap(PipiPlayerConstant.VERIFY_URL, imageView);
			getCodeImageView();
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
	
	/**
	 * 异步加载 xml
	 */
	class ExecMovie extends AsyncTask<String, Void, Integer> {

		// 判断网络
		private boolean isNetworkNorol = false;

		private Context context;
		public ExecMovie(Context context) {
			this.context = context;
		}
		//@Override
		protected Integer doInBackground(String... params) {
			int result=-1;
			if(isNetworkNorol){
				Log.i("TAG999", "session1="+SessionString);
				result=XMLPullParseUtil.WriteMovieInfoSms(handler,SessionString, params[0]);
			}
			return result;
		}

		//@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (HandlerUtil.isConnect()) {
				isNetworkNorol = true;
			} else {
				HandlerUtil.sendMsgToHandler(handler,
						PipiPlayerConstant.NONETWORK);
			}

		}

		//@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
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
			case FLASHCODE:
				imageView.setImageBitmap((Bitmap)message.obj);
				break;
			case PipiPlayerConstant.EXEC_NORMOL:
				DataUtil.getToast(R.string.submit_comment);
				finish();
				break;
			case PipiPlayerConstant.NONETWORK:
				DataUtil.getToast(R.string.NONETWORK);
				break;
			case PipiPlayerConstant.NO_DATA_RETURN:
				DataUtil.getToast(R.string.submit_comment_fail);
				break;
			default:
				break;
			}
			super.handleMessage(message);
		}

	};
	
	  public void getCodeImageView(){
		   new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bitmap=BitmapManager.getVcodeBitmap(PipiPlayerConstant.getInstance().VERIFY_URL);
				Log.i("TAG999", "session2="+SessionString);
				if(bitmap!=null){
					Message message=new Message();
					message.obj=bitmap;
					message.what=FLASHCODE;
					handler.sendMessage(message);
				}
			}
		}).start();
	   }
	  
}
