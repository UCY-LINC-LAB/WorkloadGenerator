package workload.generator.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class NullRequest implements Runnable{

	private String url;
	private String nodelist;
	private final Thread thread;
	
	public NullRequest(String url, String nodelist){
		(thread = new Thread(this)).start();
		this.url = url;
		this.nodelist = nodelist;
	}
	
	@Override
	public void run() {
		try{
			Thread.sleep(5000);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
    public String toString()
    {
        return (this.url);
    }
	
	 public Thread.State getState() {
	        return thread.getState();
	    }

}
