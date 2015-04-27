package workload.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import workload.generator.request.HttpGetRequest;
import workload.generator.request.NullRequest;
import workload.generator.util.MyRejectedExecutionHandeler;

public class StableRequestGenerator extends AGenerator implements IGenerator {	
	protected static String distribution = "stable";
	
	private String lb_url;
	private long req_rate;
	private long max_time;
	private long max_req;
	private String nodelist;
	
	public StableRequestGenerator(String lb_url, String nodelist, long max_time, long max_req, int thread_num) {
		super(thread_num);
		
		String test = "Starting " + this.distribution + " HttpGetRequest Generator>> load_balancer: "+lb_url+" request_rate: "+req_rate+" nodelist: "+nodelist
		         + " seconds"+" max_exec_time: "+max_time+" seconds "+"max_requests: "+max_req+" thread_num: "+ thread_num;	
		System.err.println(test);
		
		this.max_time = max_time * 1000;
		this.max_req = max_req;
		
		this.thread_num = thread_num;
		
		this.nodelist = nodelist;
		this.lb_url = lb_url;
	}
	
	public StableRequestGenerator(String lb_url, String nodelist, long max_time, long max_req, int thread_num, int req_rate) { 
		this(lb_url, nodelist, max_time, max_req, thread_num);
		
		this.req_rate = req_rate * 1000;
	}

	
	@Override
	protected void initialiazeGenerator() {
		this.calculateMissingParam();		
	}
	
	public boolean runGenerator()  {
		
		
		FileWriter writer = this.initLog();
		
		
		double elapsed_time = 0;
		long req_count = 0;
		
		//while(elapsed_time < this.max_time && req_count < this.max_req)
		while(elapsed_time < this.max_time)
		{
			if(this.stopThread)
					break;
			try
			{
				long seqStart = System.nanoTime();
				
				this.executor.execute(new HttpGetRequest(this.lb_url, this.nodelist));
				//this.executor.execute(new NullRequest(this.lb_url, this.nodelist));
				req_count++;					
				Thread.sleep(this.req_rate);
				
				writer.append(String.valueOf(System.currentTimeMillis() / 1000L));
			    writer.append(',');
			    writer.append(String.valueOf(1));
			    writer.append('\n');
				
			    elapsed_time += ((System.nanoTime()) - seqStart) / 1000000.0;				
				//elapsed_time += this.req_rate;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			try {
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        return true;
	}

	@Override
	public void calculateMissingParam() {
		// TODO Auto-generated method stub
		
	}


		
}
