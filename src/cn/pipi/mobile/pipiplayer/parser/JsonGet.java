package cn.pipi.mobile.pipiplayer.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import cn.pipi.mobile.pipiplayer.constant.PipiPlayerConstant;

import android.os.Handler;
import android.util.Log;

public class JsonGet {
	//***************json获取*******************************
	  public static String readJsonFromUrl(String url,Handler handler) { 
		  Log.i("TAG999", "url=="+url);
		  try {
			  InputStream is = new URL(url).openStream();  
		        try {  
		          BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));  
		          return readAll(rd);  
		        } finally {  
		          is.close();  
		        }  
		} catch (Exception e) {
			// TODO: handle exception
			handler.sendEmptyMessage(PipiPlayerConstant.HTTP_IOEXCEPTION);
			e.printStackTrace();
		}
		  return null;
	      }  
	  public static  String readAll(Reader rd) throws IOException {  
	        StringBuilder sb = new StringBuilder();  
	        int cp;  
	        while ((cp = rd.read()) != -1) {  
	          sb.append((char) cp);  
	        }  
	        return sb.toString();  
	      }  
}
