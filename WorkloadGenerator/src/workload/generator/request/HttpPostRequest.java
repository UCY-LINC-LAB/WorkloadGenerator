package workload.generator.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;


public class HttpPostRequest implements Runnable{

	private String url;
	private String nodelist;
	
	public HttpPostRequest(String url, String nodelist){
		this.url = url;
		this.nodelist = nodelist;
	}
	
	@Override
	public void run() {
		try{
			String myurl = "";
			if (nodelist != null && nodelist.length()>0)
				myurl = "?nodelist="+nodelist;
			URL obj = new URL(this.url + myurl);
			
			long start = System.nanoTime();
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setRequestMethod("GET");	
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				System.out.println("HttpGetRequest >> success");
			}
			else
				System.err.println("HttpGetRequest >> fail");
			
			long elapsed = System.nanoTime() - start;
			in.close();
		} catch (SocketTimeoutException e) {
	        System.err.println("SocketTimeoutException");
	        e.printStackTrace();
		} catch (java.io.IOException e) {
			System.err.println("IOException");
	        e.printStackTrace();		
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{}
	}

}
