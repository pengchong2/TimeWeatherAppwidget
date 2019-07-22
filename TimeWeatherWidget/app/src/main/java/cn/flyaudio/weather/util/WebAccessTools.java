package cn.flyaudio.weather.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;


public class WebAccessTools {
	
//	private Context context;
	public WebAccessTools(/*Context context*/) {
//		this.context = context;
	}
/**	public  InputStream getWebContent(String url) {
		HttpGet request = new HttpGet(url);
		HttpParams params=new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 10000);
		HttpClient httpClient = new DefaultHttpClient(params);
		try{
			HttpResponse response = httpClient.execute(request);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return response.getEntity().getContent();//content
			} else {
	//			Toast.makeText(context, "Data loading``", Toast.LENGTH_LONG).show();
			}
			
		}catch(Exception e) {
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return null;
	} **/
	
	public InputStream getWebContent(String urlString){
		HttpURLConnection urlConnection = null;
		URL url = null;
		try {
			url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();// 打开http连接
			urlConnection.setConnectTimeout(5000);// 连接的超时时间
			urlConnection.setUseCaches(false);// 不使用缓存
			urlConnection.setReadTimeout(10000);// 响应的超时时间
			urlConnection.setDoInput(true);// 设置这个连接是否可以写入数据
			urlConnection.setDoOutput(true);// 设置这个连接是否可以输出数据
			urlConnection.setRequestMethod("GET");// 设置请求的方式
			urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
			if (urlConnection.getResponseCode() == 200) {
				return urlConnection.getInputStream();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			urlConnection.disconnect();
		}
		return null;
	}
}