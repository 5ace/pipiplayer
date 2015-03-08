package cn.pipi.mobile.pipiplayer.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import cn.pipi.mobile.pipiplayer.R;
import cn.pipi.mobile.pipiplayer.beans.MovieInfo;
import cn.pipi.mobile.pipiplayer.beans.TypeInfo;
import cn.pipi.mobile.pipiplayer.beans.ZhuanTiInfo;

public class JsonUtil {
	
	public static List<MovieInfo> getHomeData(Handler handler){
		List<MovieInfo> movieList=new ArrayList<MovieInfo>();
		String json = JsonGet.readJsonFromUrl("http://m.pipi.cn/indexdata.js",handler);
		try {
			JSONObject obj = new JSONObject(json);
			//电影
			JSONArray array = obj.optJSONArray("movie");
			for(int i=0;i<array.length();i++ ){
				MovieInfo info=new MovieInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setState(array.optJSONObject(i).optString("state"));
				info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
				info.setType(array.optJSONObject(i).optString("type"));
				info.setHeader("电影");
				info.setHeaderId(0);
				movieList.add(info);
			}
			//大陆剧
			array = obj.optJSONArray("dalu");
			for(int i=0;i<array.length();i++ ){
				MovieInfo info=new MovieInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setState(array.optJSONObject(i).optString("state"));
				info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
				info.setType(array.optJSONObject(i).optString("type"));
				info.setHeader("大陆剧");
				info.setHeaderId(1);
				movieList.add(info);
			}
			//港台剧
			array = obj.optJSONArray("gangtai");
			for(int i=0;i<array.length();i++ ){
				MovieInfo info=new MovieInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setState(array.optJSONObject(i).optString("state"));
				info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
				info.setType(array.optJSONObject(i).optString("type"));
				info.setHeader("港台剧");
				info.setHeaderId(2);
				movieList.add(info);
			}
			//欧美剧
			array = obj.optJSONArray("oumei");
			for(int i=0;i<array.length();i++ ){
				MovieInfo info=new MovieInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setState(array.optJSONObject(i).optString("state"));
				info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
				info.setType(array.optJSONObject(i).optString("type"));
				info.setHeader("欧美剧");
				info.setHeaderId(3);
				movieList.add(info);
			}
			//日韩剧
			array = obj.optJSONArray("rihan");
			for(int i=0;i<array.length();i++ ){
				MovieInfo info=new MovieInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setState(array.optJSONObject(i).optString("state"));
				info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
				info.setType(array.optJSONObject(i).optString("type"));
				info.setHeader("日韩剧");
				info.setHeaderId(4);
				movieList.add(info);
			}
			//卡通
			array = obj.optJSONArray("cartoon");
			for(int i=0;i<array.length();i++ ){
				MovieInfo info=new MovieInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setState(array.optJSONObject(i).optString("state"));
				info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
				info.setType(array.optJSONObject(i).optString("type"));
				info.setHeader("动漫");
				info.setHeaderId(5);
				movieList.add(info);
			}
			//综艺剧
			array = obj.optJSONArray("variety");
			for(int i=0;i<array.length();i++ ){
				MovieInfo info=new MovieInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setState(array.optJSONObject(i).optString("state"));
				info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
				info.setType(array.optJSONObject(i).optString("type"));
				info.setHeader("综艺");
				info.setHeaderId(6);
				movieList.add(info);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return movieList;
		
	}
	//专题
	public static List<ZhuanTiInfo> getZhuanTiData(Handler handler,String url){
		List<ZhuanTiInfo> list=new ArrayList<ZhuanTiInfo>();
		String json = JsonGet.readJsonFromUrl(url,handler);
		try {
			JSONObject obj = new JSONObject(json);
			//电影
			JSONArray array = obj.optJSONArray("data");
			for(int i=0;i<array.length();i++ ){
				ZhuanTiInfo info=new ZhuanTiInfo();
				info.setId(array.optJSONObject(i).optString("id"));
				info.setName(array.optJSONObject(i).optString("name"));
				info.setImg(array.optJSONObject(i).optString("img"));
				info.setShow_name(array.optJSONObject(i).optString("show_name"));
				list.add(info);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return list;
	}
	//获取选集
		public static Map<String ,List<String>> getMovieNum(Handler handler,int moiveID){
			Map<String ,List<String>> map=new LinkedHashMap<String ,List<String>>();
			String url="http://m.pipi.cn/" + String.valueOf(moiveID/1000) + "/"+ String.valueOf(moiveID) + "_hash.js";
			Log.i("TAG999", "url="+url);
			String json = JsonGet.readJsonFromUrl(url,handler);
			try {
				JSONObject obj = new JSONObject(json);
				//获取资源类型
				List<String> tagList=new ArrayList<String>();
				JSONArray array = obj.optJSONArray("source_name");
				for(int i=0;i<array.length();i++ ){
					tagList.add(array.optString(i));
				}
				JSONObject objs=obj.optJSONObject("data");
				//根据类型获取相关资源
				for(String tag:tagList){
					array = objs.optJSONArray(tag);
					List<String> list=new ArrayList<String>();
					for(int i=0;i<array.length();i++ ){
						list.add(array.optString(i));
					}
					map.put(tag, list);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			return map;
		}
		//获取详情
		public static MovieInfo getMovieDesc(Handler handler,MovieInfo info){
			int moiveID=0;
			try {
				 moiveID = Integer.parseInt(info.getId());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return info;
			}
			String url="http://m.pipi.cn/" + String.valueOf(moiveID/1000) + "/"+ String.valueOf(moiveID) + "_info.js";
			String json = JsonGet.readJsonFromUrl(url,handler);
			try {
						JSONObject obj = new JSONObject(json);
						info.setName(obj.getString("name"));
						info.setDafen_num(obj.getString("dafen_num"));
						info.setActor(obj.getString("actor"));
						info.setArea(obj.getString("area"));
						info.setDesc(obj.getString("desc"));
						info.setYear(obj.getString("year"));
						info.setDirector(obj.getString("director"));
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					return info;
				}
	//获取所有影片类型
	public static Map<String,TypeInfo> getMovieType(Handler handler){
		Map<String,TypeInfo> map = new HashMap<String, TypeInfo>();
		String url="http://m.pipi.cn/types.js";
		String json = JsonGet.readJsonFromUrl(url,handler);
		try {
				JSONObject o = new JSONObject(json);
				String[] names=new String[]{"电影","电视剧","动漫","综艺"};
				for(String s:names){
					
					//影片
					JSONObject obj=o.optJSONObject(s);
					TypeInfo types=new TypeInfo();
					JSONArray array=obj.optJSONArray("type");
					List<String> typeList=new ArrayList<String>();
					for(int i=0;i<array.length();i++){
						typeList.add(array.optString(i));
					}
					types.setTypeList(typeList);
					
					array=obj.optJSONArray("area");
					List<String> areaList=new ArrayList<String>();
					for(int i=0;i<array.length();i++){
						areaList.add(array.optString(i));
					}
					types.setAreaList(areaList);
					
					array=obj.optJSONArray("year");
					List<String> yearList=new ArrayList<String>();
					for(int i=0;i<array.length();i++){
						yearList.add(array.optString(i));
					}
					types.setYearList(yearList);
					
					array=obj.optJSONArray("cates");
					Map<String,String> cates=new LinkedHashMap<String, String>();
					for(int i=0;i<array.length();i++){
						cates.put(array.optJSONObject(i).getString("name"), array.optJSONObject(i).getString("id"));
					}
					types.setCates(cates);
					
					map.put(s, types);
				}
				
				
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
							
				return map;
						}
	
	//获取相关分类视频
		public static List<MovieInfo> getMovieByCate(String cateID,Handler handler){
			List<MovieInfo> list=new ArrayList<MovieInfo>();
			String json = JsonGet.readJsonFromUrl("http://m.pipi.cn/typedata/"+cateID+".js",handler);
			try {
				JSONObject obj = new JSONObject(json);
				//电影
				JSONArray array = obj.optJSONArray("movie_info");
				for(int i=0;i<array.length();i++ ){
					MovieInfo info=new MovieInfo();
					info.setId(array.optJSONObject(i).optString("id"));
					info.setName(array.optJSONObject(i).optString("name"));
					info.setImg(array.optJSONObject(i).optString("img"));
					info.setArea(array.optJSONObject(i).optString("area"));
					info.setYear(array.optJSONObject(i).optString("year"));
					info.setActor(array.optJSONObject(i).optString("actor"));
					info.setSubtitle(array.optJSONObject(i).optString("subtitle"));
					info.setDafen_num(array.optJSONObject(i).optString("dafen_num"));
					list.add(info);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			return list;
		}
		
}
