package workload.generator.old;

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

public class RequestGenerator extends Thread implements IReqGenerator {	
	protected static String distribution = "stable";
	
	protected String lb_url;
	protected long req_rate;
	protected long max_time;
	protected long max_req;
	protected int thread_num;
	protected String nodelist;
	protected long sampling_rate;
	
	public ExecutorService executor; 
	
	public RequestGenerator(String lb_url, String nodelist, int req_rate, long max_time, long max_req, int thread_num) {
		String test = "Starting " + this.distribution + " HttpGetRequest Generator>> load_balancer: "+lb_url+" request_rate: "+req_rate+" nodelist: "+nodelist
		         + " seconds"+" max_exec_time: "+max_time+" seconds "+"max_requests: "+max_req+" thread_num: "+ thread_num;		
		System.err.println(test);
		
		this.lb_url = lb_url;
		this.req_rate = req_rate * 1000;
		this.sampling_rate = req_rate * 1000;
		this.max_time = max_time * 1000;
		this.max_req = max_req;
		this.thread_num = thread_num;
		this.nodelist = nodelist;
		
		this.executor = new ThreadPoolExecutor(this.thread_num, this.thread_num, 60, TimeUnit.SECONDS,
            			                        new ArrayBlockingQueue<Runnable>(10000,true),
            			                        new MyRejectedExecutionHandeler()) {
	        @Override
	        protected void afterExecute(Runnable r, Throwable t)
	        {
	            // Do some logging here
	        	System.err.println("go");
	            super.afterExecute(r, t);
	        }
		};
		
	}

	@Override
	public void run() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(this.getName() + ".csv");		
			writer.append("regTime");
		    writer.append(',');
		    writer.append("regCount");
		    writer.append('\n');
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		long workLoad_start = System.currentTimeMillis() / 1000L;
		
		long elapsed_time = 0;
		long elapsed_sampling = 0;
		long req_count = 0;	
		long sample_count = 0;	
		while(elapsed_time < max_time && req_count < max_req)
		{
			try
			{
				Future<?> job;
				//List<Runnable> runnables = new ArrayList<> ();
				//NullRequest r = new NullRequest(String.valueOf(req_count),this.nodelist);
				//runnables.add(r);
				//job = this.executor.submit(r);
				
				
				//this.executor.execute(new NullRequest(this.lb_url,this.nodelist));
				//this.executor.execute(new HttpGetRequest(this.lb_url,this.nodelist));	
				//job = this.executor.submit(new NullRequest(String.valueOf(req_count),this.nodelist));				
				req_count++;
				sample_count++;
				if(elapsed_sampling + req_rate == sampling_rate)
				{
					Thread.sleep(req_rate);
					//Take sample
					try
					{
						writer.append(String.valueOf(elapsed_time + req_rate));
					    writer.append(',');
					    writer.append(String.valueOf(sample_count));
					    writer.append('\n');
					    
					}
					catch(IOException e)
					{
					     e.printStackTrace();
					}
					elapsed_sampling = 0;
					sample_count = 0;
				}
				else if(elapsed_sampling + req_rate > sampling_rate)
				{
					long sleepTime = sampling_rate - elapsed_sampling;
					Thread.sleep(sleepTime);
					// Take Sample
					try
					{
						writer.append(String.valueOf(elapsed_time + sleepTime));
					    writer.append(',');
					    writer.append(String.valueOf(sample_count));
					    writer.append('\n');
					    
					}
					catch(IOException e)
					{
					     e.printStackTrace();
					}
					elapsed_sampling = 0;
					sample_count = 0;
					//					
					Thread.sleep(req_rate - sleepTime);					
				}
				else if(elapsed_sampling + req_rate < sampling_rate)
				{
					Thread.sleep(req_rate);
					elapsed_sampling += req_rate;
					System.out.println(elapsed_sampling);
				}
				writer.flush();
				
				
				
				
				
				elapsed_time += req_rate;
				System.out.println(String.valueOf(elapsed_time) + ":" + String.valueOf(req_count));
				
				this.req_rate = this.getRate();
								
				//System.out.println("job" + job.toString() + ": " + job.isDone());
				
				
				 
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		try {
			
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	   
		this.executor.shutdown();
		while (!executor.isTerminated())
		{
			try 
			{
				this.executor.awaitTermination(1, TimeUnit.MINUTES);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		long workLoad_end = System.currentTimeMillis() / 1000L;
		long duration = workLoad_end -workLoad_start;
        System.out.println("Finished all threads (started at: " + workLoad_start + ", ended at: " + workLoad_end + ", duration: " + duration + ")");
	}


	@Override
	public long getRate() {
		System.err.println("fuck");
		return req_rate;
	}	
}
