package cn.pipi.mobile.pipiplayer.htmlutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;
import android.util.Log;

public  class Ifeng implements HtmlInterface{
	//http://v.youku.com/v_show/id_XNjU2MTczMjQw.html
	  private  final String TAG = "TAG999 凤凰";
	 //大于2-->hd2  大于1->mp4  其他 -->flv
	 
	  public  List<String> getDownloadInfo(String url,int parseMode) {
	        Pattern pattern = Pattern.compile(".*/([A-Za-z0-9]{8})\\-([A-Za-z0-9]{4})\\-([A-Za-z0-9]{4})\\-([A-Za-z0-9]{4})\\-([A-Za-z0-9]{12})\\.shtml");
	        Matcher matcher = pattern.matcher(url);
	        if (matcher.matches()) {
	            return parseVideoItem(matcher.group(1)+"-"
	                                  +matcher.group(2)+"-"
	            		              +matcher.group(3)+"-"
	                                  +matcher.group(4)+"-"
	                                  +matcher.group(5), parseMode);
	        } else {
	            return null;
	        }
	    }
// 350 1000 1300 720p 1080p
	  private  List<String> parseVideoItem(String vid, int parseMode){
	        List<String> downloadInfo = new ArrayList<String>();
	        Log.i(TAG, "vid="+vid);
	        StringBuffer buffer=new StringBuffer("http://v.ifeng.com/video_info_new/");
	        int len=vid.length();
	        buffer.append(vid.charAt(len-2));
	        buffer.append("/");
	        buffer.append(vid.substring(len-2));
	        buffer.append("/");
	        buffer.append(vid);
	        buffer.append(".xml");
	        Log.i(TAG, "xml="+buffer.toString());
	        String xml=getXmlData(buffer.toString());
	        Log.i(TAG, "xml="+xml);
	        if(!TextUtils.isEmpty(xml)){
	        	downloadInfo.add(xml);
	        }
	        return downloadInfo;
	    }

	  private  String getXmlData(String url) {
			// 泛型暂时不写
			boolean isContinue = true;
			HttpURLConnection conn=null;
			InputStream inputStream = null;
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.setRequestMethod("GET");//以get方式发起请求
				conn.setUseCaches(false);//不进行缓存
				conn.connect();
				if (conn.getResponseCode() != 200) {
					conn.disconnect();
					return null;
				} else {
					inputStream = conn.getInputStream();
				}
				XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				parser.setInput(inputStream, "UTF-8");
				int eventType = parser.getEventType();
				while ((eventType != XmlPullParser.END_DOCUMENT)&&isContinue) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if (parser.getName().equals("item")) {
							isContinue=false;
							return parser.getAttributeValue(12);
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					eventType = parser.next();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			//	//Log.i(PipiPlayerConstant.TAG, "解析异常");
				e.printStackTrace();
			} finally {
				// 扫尾操作
				try {
					   if(inputStream!=null)
							inputStream.close();
							 inputStream=null;
					   if(conn!=null)
							 conn.disconnect();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}
			return null;
		}
	  
}
