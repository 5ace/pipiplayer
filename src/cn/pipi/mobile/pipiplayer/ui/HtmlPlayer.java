package cn.pipi.mobile.pipiplayer.ui;

import java.lang.reflect.Method;
import java.util.TimerTask;

import com.umeng.analytics.MobclickAgent;


import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class HtmlPlayer extends Activity{
	PowerManager powerManager = null;
    WakeLock wakeLock = null;
    private String url=null;
    private String tag=null;
    		//"http://tv.sohu.com/20130614/n378869578.shtml";
    private View myView = null;
    private WebView mWebView;
    TimerTask mPreloadTask;
    Handler handler = new Handler(); 
    LinearLayout logoLayout;
    TextView t_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示标�?
            if(android.os.Build.VERSION.SDK_INT >= 14) {//浼橀叿瑙嗛蹇呴』鍔犲姞閫熷櫒锛屽惁鍒欐斁涓嶅嚭锟�
            	getWindow().setFlags(
        				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
        				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
            getWindow().setFlags(
    				WindowManager.LayoutParams.FLAG_FULLSCREEN,
    				WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(
    				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
    				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            
            setContentView(R.layout.htmlplayer);
            logoLayout=(LinearLayout) findViewById(R.id.logoLayout);
            t_progress=(TextView) findViewById(R.id.t_progress);
            url=getIntent().getStringExtra("url");
            tag=getIntent().getStringExtra("tag");
            Log.i(TAG, "url="+url);
            init1();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
     //   mWebView.saveState(outState);
    }
    
@SuppressWarnings("deprecation")
@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public void init1(){
	 this.powerManager = (PowerManager) this
             .getSystemService(Context.POWER_SERVICE);
this.wakeLock = this.powerManager.newWakeLock(
             PowerManager.FULL_WAKE_LOCK, "My Lock");
mWebView = (WebView) findViewById(R.id.webView);
mWebView.setWebChromeClient(mChromeClient);
mWebView.setWebViewClient(mWebViewClient);
WebSettings webSetting=mWebView.getSettings();
webSetting.setJavaScriptEnabled(true);
//閫傚簲灞忓箷
//webSetting.setLoadWithOverviewMode(true);
webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
//webSetting.setSupportZoom(true);
//webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
//mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);// 璁剧疆缂撳啿澶у皬锛屾垜璁剧殑锟�0M
//webSetting.setAllowFileAccess(true);
//webSetting.setPluginsEnabled(true);
//webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
if(android.os.Build.VERSION.SDK_INT>=11){
	 webSetting.setRenderPriority(RenderPriority.HIGH);
}
if(android.os.Build.VERSION.SDK_INT>=8){
	webSetting.setPluginState(WebSettings.PluginState.ON);
}
//webSetting.setBuiltInZoomControls(true);
webSetting.setUseWideViewPort(true);
//webSetting.setLoadWithOverviewMode(true);
//webSetting.setBlockNetworkImage( true );//绂佹鍔犺浇鍥剧墖锛屽姞蹇闂拷?锟�

   mWebView.loadUrl(url);
}

    private WebViewClient mWebViewClient = new WebViewClient() {
    	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
              mWebView.loadUrl(url);
             return true;   
        }
            @Override
            public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
            }
            @Override  
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            	Toast.makeText(HtmlPlayer.this, "Oh no! " + getString(R.string.NO_DATA_RETURN), Toast.LENGTH_SHORT).show();
            	finish();
            	}
    };

    // goBack()鍜実oForward()
    @Override
   	public boolean onKeyDown(int keyCode, KeyEvent event) {
   		if (keyCode == KeyEvent.KEYCODE_BACK) {
   			ExitApp();
   			return true;
   		}
   		return super.onKeyDown(keyCode, event);
   	}
       private long exitTime = 0;

       
       public void ExitApp()
       {
               if ((System.currentTimeMillis() - exitTime) > 2000)
               {
                       Toast.makeText(this, getString(R.string.exit_activity), Toast.LENGTH_SHORT).show();
                       exitTime = System.currentTimeMillis();
               } else
               {
              finish();
               }

       }

    private WebChromeClient mChromeClient = new WebChromeClient() {

            private CustomViewCallback myCallback = null;

            // 閰嶇疆鏉冮檺 锛堝湪WebChromeClinet涓疄鐜帮級
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                            GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            // 鎵╁厖鏁版嵁搴撶殑�?归噺锛堝湪WebChromeClinet涓疄鐜帮級
            @Override
            public void onExceededDatabaseQuota(String url,
                            String databaseIdentifier, long currentQuota,
                            long estimatedSize, long totalUsedQuota,
                            WebStorage.QuotaUpdater quotaUpdater) {

                    quotaUpdater.updateQuota(estimatedSize * 2);
            }

            // 鎵╁厖缂撳瓨鐨勫锟�?            @Override
            public void onReachedMaxAppCacheSize(long spaceNeeded,
                            long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

                    quotaUpdater.updateQuota(spaceNeeded * 2);
            }

            // Android 浣縒ebView�?��寔HTML5 Video锛堝叏灞忥級鎾斁鐨勬柟锟�
            @Override
            public synchronized  void  onShowCustomView(View view, CustomViewCallback callback) {
                    if (myCallback != null) {
                            myCallback.onCustomViewHidden();
                            myCallback = null;
                            return;
                    }

                    ViewGroup parent = (ViewGroup) mWebView.getParent();
                    parent.removeView(mWebView);
                    parent.addView(view);
                    myView = view;
               //     setWidthAndHeight(view);
                    myCallback = callback;
                    mChromeClient = this;
                    
                   // DataUtil.getToast(HtmlPlayer.this, getString(R.string.waitintformovie) );
                    logoLayout.setVisibility(8);
              	     t_progress.setText(getString(R.string.buffer_loading));
                    Toast.makeText(HtmlPlayer.this, getString(R.string.waitintformovie), 5000).show();
                    
            }
            
            @Override
            public synchronized  void onHideCustomView() {
                    if (myView != null) {
                            if (myCallback != null) {
                                    myCallback.onCustomViewHidden();
                                    myCallback = null;
                            }

                            ViewGroup parent = (ViewGroup) myView.getParent();
                            parent.removeView(myView);
                            parent.addView(mWebView);
                            myView = null;
                    }
                  //  super.onHideCustomView();
            }
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 0) {
                	 logoLayout.setVisibility(0);
                	 t_progress.setText(getString(R.string.buffer_loading));
                }else if(progress > 0&&progress<100){
                	StringBuffer string=new StringBuffer(getString(R.string.buffer_loading));
                	string.append(progress);
                	string.append("%");
                	 t_progress.setText(string.toString());
                	 logoLayout.setVisibility(0);
                }else if(progress==100){
                	 logoLayout.setVisibility(8);
               	     t_progress.setText(getString(R.string.buffer_loading));
                }
               }
    };

    private void callHiddenWebViewMethod(String name) {
		if (mWebView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView, (Object[]) null);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}   
    @Override
    protected void onResume() {
            // TODO Auto-generated method stub
            super.onResume();
            callHiddenWebViewMethod("onResume");
    }
    @Override
    protected void onPause() {
            // TODO Auto-generated method stub
            super.onPause();
            if(mChromeClient!=null){
            	if(!url.startsWith("http://v.qq.com/")){
            		 mChromeClient.onHideCustomView();
            		 mChromeClient=null;
            	}
     	   }
            callHiddenWebViewMethod("onPause");
    }
    @Override
    protected void onStop() {
            // TODO Auto-generated method stub
            super.onStop();
            if(mChromeClient!=null){
            	if(url.startsWith("http://v.qq.com/"))
     		   mChromeClient.onHideCustomView();
     	   }
    }
    private String TAG="HtmlPlayer";
    @Override
    protected void onDestroy() {
            // TODO Auto-generated method stub
    	super.onDestroy();
    	mWebView.setWebChromeClient(null);
    	mWebView.setWebViewClient(null);
    	mWebView.loadUrl("about:blank");
        mWebView.stopLoading();
        mWebView=null;
    }
 
}
