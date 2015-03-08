package cn.pipi.mobile.pipiplayer.htmlutils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import cn.pipi.mobile.pipiplayer.BuildConfig;

import android.text.TextUtils;
import android.util.Log;

public  class Ku6 implements HtmlInterface{
	//http://v.youku.com/v_show/id_XNjU2MTczMjQw.html
	  private  final String TAG = "TAG999 ku6";
	 //大于2-->hd2  大于1->mp4  其他 -->flv
	 
	  public  ArrayList<String> getDownloadInfo(String url,int parseMode) {
	        Pattern pattern = Pattern.compile(".*/([A-Za-z0-9]+)\\.*html.*");
	        Matcher matcher = pattern.matcher(url);
	        if (matcher.matches()) {
	        	try {
	        		  return parseVideoItem(matcher.group(1), parseMode);
				} catch (Exception e) {
					// TODO: handle exception
				}
	        } 
	        return null;
	    }

	  private  ArrayList<String> parseVideoItem(String vid, int parseMode){
		  ArrayList<String> downloadInfo = new ArrayList<String>();
	        String url ="http://v.ku6.com/fetchVideo4Player/" + vid+"...html";
	        try {
	        	JSONObject jsonObject = HtmlUtils.readJsonFromUrl(url);
	            if(jsonObject == null) return downloadInfo;
	            JSONObject data=jsonObject.getJSONObject("data");
	            String su=data.getString("f");
	            if(!TextUtils.isEmpty(su)){
	            	downloadInfo.add(su);
	            }
	        } catch (Exception e) {
	            if(BuildConfig.DEBUG)
	                Log.w(TAG, "failed to parse ku6 video =" +url , e);
	        }
	        return downloadInfo;
	    }

	  
}
