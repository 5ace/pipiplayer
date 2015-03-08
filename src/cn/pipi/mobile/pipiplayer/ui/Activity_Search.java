package cn.pipi.mobile.pipiplayer.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.adapter.SearchKeyAdapter;
import cn.pipi.mobile.pipiplayer.asyctask.Search_HotKey_AsyncTask;
import cn.pipi.mobile.pipiplayer.asyctask.Search_Key_AsyncTask;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.view.FlowLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;

public class Activity_Search extends SherlockFragmentActivity implements OnClickListener{
	private ProgressBar progerssbar;
	private EditText searchKey;
	private ListView listView;
	private View view;
	private DBHelperDao dao;
	private SearchKeyAdapter adapter;
	private Context mContext;
	private ImageView img_clear;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view=getLayoutInflater().inflate( R.layout.activity_search, null );
		setContentView(view);
		mContext=this;
		dao=DBHelperDao.getDBHelperDaoInstace();
		progerssbar=(ProgressBar)findViewById(R.id.progerssBar);
		LinearLayout rl_bar =(LinearLayout) findViewById(R.id.rl_bar);
        ImageView img_back = (ImageView) findViewById(R.id.img_back);  
        ImageView img_search = (ImageView) findViewById(R.id.img_search);
        img_clear = (ImageView) findViewById(R.id.img_clear);  
        img_search.setOnClickListener(this);  
        img_back.setOnClickListener(this); 
        img_clear.setOnClickListener(this); 
        listView=(ListView) findViewById(R.id.listView_shouye);  
        listView.setBackgroundColor(getResources().getColor(R.color.background_common));
        adapter=new SearchKeyAdapter(this);
        listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				search(adapter.getItem(arg2));
			}
		});
		initHistroyKey(dao.getKeys(6));
		initEditText();
        new Search_HotKey_AsyncTask(handler).execute("");
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
	private void initEditText(){
		searchKey=(EditText)findViewById(R.id.searchKey);
		searchKey.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchKey.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void afterTextChanged(Editable s) {
				try {
					String key=searchKey.getText().toString();
					if(key.length()<1){
						hideSearchListView(true);
					}else {
						hideSearchListView(false);
						new Search_Key_AsyncTask(handler).execute(key.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        searchKey.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode==KeyEvent.KEYCODE_ENTER||keyCode==KeyEvent.KEYCODE_SEARCH){
					String key=searchKey.getText().toString();
					if(key.length()>0){
						search(key);
					}
				//	hideSearchListView(true);//停止联想
				}
				return false;
			}

		});
	}
	
	private void initHistroyKey(List<String> list){
		 FlowLayout flowlayout = (FlowLayout) findViewById(R.id.flowlayout1);
		 flowlayout.removeAllViews();//清空记录
		 for(final String string:list){
			 View v=LayoutInflater.from(this).inflate(R.layout.item_search_hotkey, null);
			 final Button bt = (Button) v.findViewById(R.id.bt);
			 bt.setText(string);
			 flowlayout.addView(v);
			 bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					search(bt.getText().toString());
				}
			});
		 }
	        
	    }
	 private void initHotKey(List<String> list){
		 FlowLayout flowlayout = (FlowLayout) findViewById(R.id.flowlayout2);
		 for(final String string:list){
			 View v=LayoutInflater.from(this).inflate(R.layout.item_search_hotkey, null);
			 final Button bt = (Button) v.findViewById(R.id.bt);
			 bt.setText(string);
			 flowlayout.addView(v);
			 bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					search(bt.getText().toString());
				}
			});
		 }
	        
	    }
	private void hideSearchListView(boolean hide){
		if(hide){
			adapter.clear();
			listView.setVisibility(8);
		}else{
			listView.setVisibility(0);
		}
	}
	private void search( String key ){
    	//把键盘隐藏起来
    	hideKeyBoard();
    	if (0 >= key.length()) return;
    	dao.insertKeys(key);
    	 initHistroyKey(dao.getKeys(6));
    	 Intent intent =new Intent(this,Activity_SearchResult.class);
    	 intent.putExtra("key", key);
    	 startActivity(intent);
    }
	//隐藏软件盘
    protected void hideKeyBoard()
    {
        try
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch( Exception e )
        {
        	e.printStackTrace();
        }
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.img_back){
			finish();
		}else if(v.getId() == R.id.img_search){
			String key=searchKey.getText().toString();
			if(key.length()>0){
				search(key);
			}
		}else if(v.getId() == R.id.img_clear){
			dao.delAllKeys();
	    	initHistroyKey(dao.getKeys(6));
		}
	}
	// 主线程中新建一个handler
			Handler  handler = new Handler() {
					public void handleMessage(android.os.Message msg) {
		            	switch (msg.what) {
		            	case PipiPlayerConstant.EXEC_NORMOL:
		    				adapter.research((ArrayList<String>)msg.obj);
		    				break;
		    			case PipiPlayerConstant.NONETWORK:
		    				progerssbar.setVisibility(View.GONE);
		    				DataUtil.getToast(R.string.NONETWORK);
		    			        break;
		    			case PipiPlayerConstant.NO_DATA_RETURN:
		    				progerssbar.setVisibility(View.GONE);
		    				DataUtil.getToast(R.string.NO_DATA_RETURN);
		    				break;
		    			case PipiPlayerConstant.DATA_RETURN_ZERO:
		    				progerssbar.setVisibility(View.GONE);
		    				adapter.research((ArrayList<String>)msg.obj);
		    				DataUtil.getToast(R.string.nosoucefound);
		    				break;
		    			case PipiPlayerConstant.EXEC_NORMOL_HOTKEY://热门搜索词汇获取成功
		    				progerssbar.setVisibility(View.GONE);
		    				initHotKey((ArrayList<String>)msg.obj);
		    		        break;
		    			default:
		    				break;
						}
		            }
		    }; 
			
	
}
