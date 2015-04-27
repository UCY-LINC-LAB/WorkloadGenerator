package workload.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import workload.generator.request.HttpGetRequest;
import workload.generator.request.NullRequest;

public class LinearRequestGenerator extends AGenerator implements IGenerator {
	private final String distribution = "linear";
	
	private String lb_url;
	private long req_rate;
	private long max_time;
	private long max_req;
	private int startValue;
	private int steep;
	private String nodelist;
	
	public LinearRequestGenerator(String lb_url, String nodelist, long max_time, long max_req, int thread_num) {
		super(thread_num);
		
		String test = "Starting " + this.distribution + " HttpGetRequest Generator>> load_balancer: "+lb_url+" request_rate: "+req_rate+" nodelist: "+nodelist
		         + " seconds"+" max_exec_time: "+max_time+" seconds "+"max_requests: "+max_req+" thread_num: "+ thread_num;	
		System.err.println(test);
		
		this.max_time = max_time * 1000;
		this.max_req = max_req;
		//this.startValue = 1;
		//double steep = 1.5;
		
		this.thread_num = thread_num;
		
		this.nodelist = nodelist;
		this.lb_url = lb_url;
	}
	
	public LinearRequestGenerator(String lb_url, String nodelist, long max_time, long max_req, int thread_num, int req_rate) { 
		this(lb_url, nodelist, max_time, max_req, thread_num);
		
		this.req_rate = req_rate * 1000;
	}
	
	@Override
	protected void initialiazeGenerator() {
		this.calculateMissingParam();
		
	}
	
	public boolean runGenerator() {
		
		FileWriter writer = this.initLog();
		
				
		double elapsed_time = 0;
		long req_count = 0;
		boolean exitFlag = false;
		
		int regPerFrame = this.startValue;		
		//while(elapsed_time < this.max_time && req_count < this.max_req)
		while(elapsed_time < this.max_time && !exitFlag)
		{
			if(this.stopThread)
					break;
			
			// Stop the load if you issuing more than 1req per second
			if(this.req_rate / regPerFrame < 1)
				exitFlag = true;
			try
			{
				long seqStart = System.nanoTime();
				for(int i=0; i < regPerFrame; i++)
				{
					this.executor.execute(new HttpGetRequest(this.lb_url, this.nodelist));
					//this.executor.execute(new NullRequest(this.lb_url, this.nodelist));
					req_count++;					
					Thread.sleep(this.req_rate / regPerFrame);
				}
				
				writer.append(String.valueOf(System.currentTimeMillis() / 1000L));
			    writer.append(',');
			    writer.append(String.valueOf(regPerFrame));
			    writer.append('\n');
				
			    elapsed_time += ((System.nanoTime()) - seqStart) / 1000000.0;
				regPerFrame += Math.round(this.startValue * this.steep);				
				
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
	public void calculateMissingParam() 
	{
		this.startValue = 1;
		this.steep = 2;
	}

	
	
}
