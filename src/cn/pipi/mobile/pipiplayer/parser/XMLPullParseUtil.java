package cn.pipi.mobile.pipiplayer.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.beans.DownloadAPK;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.beans.MovieSms;
import cn.pipi.mobile.pipiplayer.beans.SelectionInfo;
import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.HandlerUtil;

public class XMLPullParseUtil {

	/**
	 * 以XMLPullParse 解析xml 返回gallery 数据
	 */
	private final static int TIMEOUT=5*1000;
	
	

	/**
	 * 搜素数据
	 */
	public static ArrayList<MovieInfo> getSearhResult(String urlPath,Handler handler) {
		if(TextUtils.isEmpty(urlPath))return null;
		ArrayList<MovieInfo> tempList = new ArrayList<MovieInfo>();
		MovieInfo MovieInfo = null;
		HttpURLConnection conn=null;
		InputStream inputStream = null;
		try {
			URL url = new URL(PipiPlayerConstant.getInstance().NORMAL_SEARCH_URL+URLEncoder.encode(urlPath));
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_STATE_NOTOK);
				return null;
			} else {
				inputStream = conn.getInputStream();
			}
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();
			parser.setInput(inputStream, "UTF-8");
			int eventType = parser.getEventType();
			String name = null;
			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					
					tempList = new ArrayList<MovieInfo>();
					break;
				case XmlPullParser.START_TAG:
					
					name = parser.getName();// 获取解析器当前指向的元素的名�?
					if (name.equals("pp_data")) {
						MovieInfo = new MovieInfo();
					}
                    if(MovieInfo!=null){
                    	if (name.equals("id")) {
    						MovieInfo.setId(parser.nextText());
    					} else if (name.equals("img")) {
    						MovieInfo.setImg(parser.nextText());
    					} else if (name.equals("name")) {
    						MovieInfo.setName(parser.nextText());
    					} else if (name.equals("type")) {
    						MovieInfo.setType(parser.nextText());
    					} else if (name.equals("dafen_num")) {
    						MovieInfo.setDafen_num(parser.nextText());
    					} else if (name.equals("actor")) {
    						MovieInfo.setActor(parser.nextText());
    					} else if (name.equals("area")) {
    						MovieInfo.setArea(parser.nextText());
    					} else if (name.equals("year")) {
    						MovieInfo.setYear(parser.nextText());
    					}
                    }
					
					break;
				case XmlPullParser.END_TAG:
					if ("pp_data".equals(parser.getName())) {
						tempList.add(MovieInfo);
						MovieInfo = null;
					}
					break;
				}
				eventType = parser.next();
			}

			return tempList;
		} catch (Exception e) {
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_IOEXCEPTION);
			e.printStackTrace();
		} finally {
				   if(conn!=null)
						 conn.disconnect();
		}
		return tempList;
	}

	/**
	 * 热门搜索词汇
	 */
	public static ArrayList<String> getHotSearchData(String urlPath,Handler handler) {
		ArrayList<String> tempList = null;
		HttpURLConnection conn=null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_STATE_NOTOK);
				return null;
			} else {
				inputStream = conn.getInputStream();
			}
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实�?
			XmlPullParser parser = pullParserFactory.newPullParser();

			// 设置输入�?xml文件
			parser.setInput(inputStream, "UTF-8");

			// �?��
			int eventType = parser.getEventType();

			// scrollviewdisplay videoitemsdisplay
			String name = null;
			tempList = new ArrayList<String>();
			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();// 获取解析器当前指向的元素的名�?
					if (name.equals("video_name")) {
						tempList.add(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					name=null;
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			tempList = null;
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_IOEXCEPTION);
			e.printStackTrace();
		} finally {
			// 扫尾操作
				   if(conn!=null)
						 conn.disconnect();
		}
		return tempList;
	}
	
	/**
	 * 输入框搜索词�?
	 */
	public static ArrayList<String> getEditSearchData(String urlPath,Handler handler) {
		ArrayList<String> tempList = new ArrayList<String>();
		HttpURLConnection conn=null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			//判断是否有sessionid�?
			if (PipiPlayerConstant.getInstance().SESSION != null) {
				conn.setRequestProperty("Cookie", PipiPlayerConstant.getInstance().SESSION);
			}	
			conn.connect();
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_STATE_NOTOK);
				return null;
			} else {
				inputStream = conn.getInputStream();
				String cookieVal = conn.getHeaderField("Set-Cookie");
				if (cookieVal != null) {
					//存储sessionid�?
					String sid = cookieVal.substring(0, cookieVal.indexOf(";"));
					if (sid != null && !sid.isEmpty()) {
						PipiPlayerConstant.getInstance().SESSION=sid;
					}
					}
			}
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实�?
			XmlPullParser parser = pullParserFactory.newPullParser();

			// 设置输入�?xml文件
			parser.setInput(inputStream, "UTF-8");
			// �?��
			int eventType = parser.getEventType();
			String name = null;
			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();// 获取解析器当前指向的元素的名�?
					if (name.equals("Rs")) {
						if(parser.getAttributeValue(1)!=null){
							tempList.add(parser.getAttributeValue(1));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name=null;
					break;
				}
				eventType = parser.next();
			}

			return tempList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_IOEXCEPTION);
			e.printStackTrace();
		} finally {
			// 扫尾操作
				   if(conn!=null)
						 conn.disconnect();
		}
		return tempList;
	}

	/**
	 * 得到分类影片数据 电影 电视�?综艺 科教 其他
	 */
	public static ArrayList<MovieInfo> getClassifyData(SelectionInfo info,Handler handler) {
		ArrayList<MovieInfo> tempList = new ArrayList<MovieInfo>();;
		StringBuffer buffer=new StringBuffer("http://124.95.152.254/search?cl=4&ie=UTF-8");
		try {
		if(!TextUtils.isEmpty(info.getTp()))buffer.append("&tp="+URLEncoder.encode(info.getTp(), "utf-8"));
		if(!TextUtils.isEmpty(info.getAr()))buffer.append("&ar="+URLEncoder.encode(info.getAr(), "utf-8"));
		if(!TextUtils.isEmpty(info.getSb()))buffer.append("&sb="+URLEncoder.encode(info.getSb(), "utf-8"));
		if(!TextUtils.isEmpty(info.getStp()))buffer.append("&stp="+URLEncoder.encode(info.getStp(), "utf-8"));
		buffer.append("&np="+info.getNp());
		buffer.append("&ps="+info.getPs());
		if(!TextUtils.isEmpty(info.getFt()))buffer.append("&ft="+URLEncoder.encode(info.getFt(), "utf-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		MovieInfo MovieInfo = null;
		HttpURLConnection conn=null;
		InputStream inputStream = null;
		try {
			Log.i("TAG999", "url=="+buffer.toString());
			URL url = new URL(buffer.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			if (conn.getResponseCode() != 200) {
				
				conn.disconnect();
				HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_STATE_NOTOK);
				return null;
			} else {
				inputStream = conn.getInputStream();
			}
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();
			parser.setInput(inputStream, "UTF-8");
			int eventType = parser.getEventType();
			String name = null;

			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					
					name = parser.getName();// 获取解析器当前指向的元素的名�?
					if (name.equals("pp_data")) {
						MovieInfo = new MovieInfo();
					}else if(name.equals("id")){
						MovieInfo.setId(parser.nextText());
					}else if(name.equals("img")){
						MovieInfo.setImg(parser.nextText());
					}else if (name.equals("name")) {
						MovieInfo.setName(parser.nextText());
					} else if (name.equals("type")) {
						MovieInfo.setType(parser.nextText());
					} else if (name.equals("dafen_num")) {
						MovieInfo.setDafen_num(parser.nextText());
					}else if(name.equals("year")){
						MovieInfo.setYear(parser.nextText());
					}else if(name.equals("area")){
						MovieInfo.setArea(parser.nextText());
					}else if(name.equals("actor")){
						MovieInfo.setActor(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("pp_data".equals(parser.getName())) {
						tempList.add(MovieInfo);
						MovieInfo = null;
					}
					break;
				}
				eventType = parser.next();
			}
			return tempList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_IOEXCEPTION);
		} finally {
			// 扫尾操作
				   if(conn!=null)
						 conn.disconnect();
		}
		return tempList;
	}




		/**
		 * 
		 * @param context   上下�?
		 * @param requestUrl  服务器端 版本�?请求地址
		 * @param handler    句柄
		 * @return    服务器端versionName
		 */
		public static  void getServiceVersionName(Context context,String requestUrl,Handler handler){
			HttpURLConnection conn=null;
			InputStream inputStream = null;
			try {
				URL url = new URL(requestUrl);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(TIMEOUT);
				conn.setReadTimeout(TIMEOUT);//避免时间过长造成体验不好
				if (conn.getResponseCode() != 200) {
					conn.disconnect();
					HandlerUtil.sendMsgToHandler(handler,
							PipiPlayerConstant.HTTP_STATE_NOTOK);
					return ;
				} else {
					inputStream = conn.getInputStream();
				}
				XmlPullParserFactory pullParserFactory = XmlPullParserFactory
						.newInstance();
				// 获取XmlPullParser的实�?
				XmlPullParser parser = pullParserFactory.newPullParser();

				// 设置输入�?xml文件
				parser.setInput(inputStream, "UTF-8");

				// �?��
				int eventType = parser.getEventType();

				String name = null;
				
				boolean androidPhone=false;
                String minver = null,newver= null,datasource= null,md5= null;
				while ((eventType != XmlPullParser.END_DOCUMENT)) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						name = parser.getName();
						if (name.equals("android-phone")) {
							androidPhone=true;
						}
						if(androidPhone){
							if(name.equals("minver")){
								minver=parser.nextText();
							}else if(name.equals("newver")){
								newver=parser.nextText();
							}else if(name.equals("datasource")){
			                    datasource=parser.nextText();
							}else if(name.equals("md5")){
								md5=parser.nextText();
							}
						}
						
						break;
					case XmlPullParser.END_TAG:
						if (parser.getName().equals("android-phone")) {
							androidPhone=false;
						}
						
						break;
					}
					eventType = parser.next();
				}
				//测试数据
                if(minver!=null&&minver.compareTo(PipiPlayerConstant.getInstance().dver)>0){
					//强制升级
                	DownloadAPK	 down=new DownloadAPK(newver,datasource,md5);
					Message message=new Message();
					message.what=PipiPlayerConstant.UPDATE_MINVER;
					message.obj=down;
					handler.sendMessage(message);
			   }else if(newver!=null&&newver.compareTo(PipiPlayerConstant.getInstance().dver)>0){
				   //提示升级
				   DownloadAPK    down=new DownloadAPK(newver,datasource,md5);
					Message message=new Message();
					message.what=PipiPlayerConstant.UPDATE_NEWVER;
					message.obj=down;
					handler.sendMessage(message);
			   }else if(minver!=null&&newver!=null){
				   //不用升级，应该删除APK文件
				   handler.sendEmptyMessage(PipiPlayerConstant.UPDATE_NO);
			   }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// 扫尾操作
					   if(conn!=null)
							 conn.disconnect();
			}
		}
		
		public static List<String> getPipilPlayList(String str) {
			List<String> pipiPlayAddressList = new ArrayList<String>();
			JSONObject jsonObject = null;
			JSONArray jsonArray = null;
			try {
				jsonObject = new JSONObject(str);
				jsonArray = jsonObject.getJSONArray("pipi");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject temp = (JSONObject) jsonArray.get(i);
					String url = temp.getString("playurl");
					System.out.println("getPipilPlayList  "+url);
					pipiPlayAddressList.add(url);
				}

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return pipiPlayAddressList;
		}

		public InputStream getInputStream(String urlPath,Handler handler ){
			InputStream inputStream = null;
			HttpURLConnection conn=null;
			try {
				URL url = new URL(urlPath);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(TIMEOUT);
				conn.setReadTimeout(TIMEOUT);
				if (conn.getResponseCode() != 200) {
					conn.disconnect();
					HandlerUtil.sendMsgToHandler(handler,
							PipiPlayerConstant.HTTP_STATE_NOTOK);
					return null;
				} else {
					inputStream = conn.getInputStream();
				}
		}catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
				   if(conn!=null)
						 conn.disconnect();
		}
			return inputStream;
}
		
		//写评论信�?
				public static Integer WriteMovieInfoSms(Handler handler,String session,String address  ){
					HttpURLConnection conn=null;
					try {
						URL url = new URL(address);
						conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(TIMEOUT);
						conn.setReadTimeout(TIMEOUT);
						//判断是否有sessionid�?
						if (!TextUtils.isEmpty(session)) {
							conn.setRequestProperty("Cookie", session);
						}	
						if (conn.getResponseCode() != 200) {
							conn.disconnect();
							HandlerUtil.sendMsgToHandler(handler,
									PipiPlayerConstant.HTTP_STATE_NOTOK);
							return -1;
						}
						}catch (Exception e) {
							// TODO: handle exception
						}
					return 1;
				}
				
				//获取 评论信息
				public static ArrayList<MovieSms> getMovieInfoSms(Handler handler,String m_moiveID ,int page ){
					HttpPost post=null;
					try {
						//String m_moiveID=m_info.getMovieID();
						String uri="http://user.pipi.cn/common/minirev/"+m_moiveID.substring(0,m_moiveID.length()-3) + "/"+ m_moiveID+"_"+page+".js";
						post = new HttpPost(uri);
						HttpClient http = new DefaultHttpClient();
						HttpResponse response = http.execute(post);
						int code = response.getStatusLine().getStatusCode();
						if(code == 404){
							HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.DATA_RETURN_ZERO);
						}else if(code != 200){
							HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.NO_DATA_RETURN);
						}else if(code == 200){
							 HttpEntity entity = response.getEntity();
							    InputStream in = entity.getContent();
							  return  readSmsResponse(in);
						}
						
					}catch (Exception e) {
						// TODO: handle exception
					}finally{
						if(post!=null){
							post.abort();
						}
					}
					return null;
				}
				public static ArrayList<MovieSms> readSmsResponse(InputStream in) throws Exception{
				    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				    ArrayList<MovieSms> list=new ArrayList<MovieSms>();
				    MovieSms movieSms = null;
				    String line = null;
				    while ((line = reader.readLine()) != null) {
				    	/*if(line.startsWith("mini.review.contents.push")){
				    		movieSms=new MovieSms();
				    	}else */
				    	int index=0;
				    		if((index=line.indexOf("content:"))!=-1){
				    			movieSms=new MovieSms();
				    			String content=line.substring(index+9, line.length()-2);
				    			movieSms.setContent(content);
				    			
				    	}else if((index=line.indexOf("userNickName:"))!=-1){
				    		String userNickName=line.substring(index+14, line.length()-2);
				    		    movieSms.setUserName(URLDecoder.decode(userNickName, "UTF-8"));
				    		    
				    	}else if((index=line.indexOf("revDate:"))!=-1){
				    		String revDate=line.substring(index+9, line.length()-1);
				    		    movieSms.setTime(revDate);
				    		    list.add(movieSms);
				    		    
				    	}else if(line.startsWith("mini.review.navi.callback")){
				    		//m_info.setMovieSms(list);
				    		return list;
				    	}
				    }
				    if(in!=null)
				    	in.close();
				        in=null;
				    return list;
				}
				
				//获取 评论信息
				public static List<MovieInfo> getMovieLike(Handler handler,String m_moiveID){
					HttpPost post=null;
					try {
						//String m_moiveID=m_info.getMovieID();
						String uri="http://dm.pipi.cn/re?mid="+m_moiveID;
						Log.i("TAG999", "uri=="+uri);
						post = new HttpPost(uri);
						HttpClient http = new DefaultHttpClient();
						HttpResponse response = http.execute(post);
						if(response.getStatusLine().getStatusCode() != 200){
							HandlerUtil.sendMsgToHandler(handler, PipiPlayerConstant.HTTP_STATE_NOTOK);
						}else{
							 HttpEntity entity = response.getEntity();
							    InputStream in = entity.getContent();
							  return  readLikeResponse(in);
						}
						
					}catch (Exception e) {
						// TODO: handle exception
					}finally{
						if(post!=null){
							post.abort();
						}
					}
					return null;
				}
				public static List<MovieInfo> readLikeResponse(InputStream in) throws Exception{
				    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				    ArrayList<MovieInfo> list=new ArrayList<MovieInfo>();
				    MovieInfo movie = null;
				    String line = null;
				    while ((line = reader.readLine()) != null) {
				    	/*if(line.startsWith("mini.review.contents.push")){
				    		movieSms=new MovieSms();
				    	}else */
				    	int index=0;
				    	if((index=line.indexOf("mov_id:"))!=-1){
				    			movie=new MovieInfo();
				    			String content=line.substring(index+8, line.length()-2);
				    			movie.setId(content);
				    			
				    	}else if((index=line.indexOf("mov_name:"))!=-1){
				    		String userNickName=line.substring(index+10, line.length()-2);
				    		    movie.setName(URLDecoder.decode(userNickName, "UTF-8"));
				    		    
				    	}else if((index=line.indexOf("mark:"))!=-1){
				    		String revDate=line.substring(index+6, line.length()-2);
			    		    movie.setDafen_num(revDate);
			    		    
			    	    }else if((index=line.indexOf("desc:"))!=-1){
				    		String revDate=line.substring(index+6, line.length()-2);
			    		    movie.setDesc(revDate);
			    		    
			    	    }else if((index=line.indexOf("mov_pic:"))!=-1){
				    		String revDate=line.substring(index+9, line.length()-2);
			    		    movie.setImg(revDate);
			    		    list.add(movie);
			    		    
			    	    }else if(line.startsWith("jse.movList.showSearchData")){
				    		//m_info.setMovieSms(list);
				    		return list;
				    	}
				    }
				    if(in!=null)
				    	in.close();
				        in=null;
				    return list;
				}
}