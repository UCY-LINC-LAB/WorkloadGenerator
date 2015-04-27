package workload.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import workload.generator.util.MyRejectedExecutionHandeler;
import workload.generator.util.MyThreadPoolExecutor;

public abstract class AGenerator extends Thread {
	protected final String distribution = "";	
	protected boolean stopThread = false;
	protected int thread_num;
	public ExecutorService executor; 
	
	//protected String outputPath = "." + File.separator;
	//protected String outputPath = System.getProperty("user.dir") + File.separator;
	///usr/share/tomcat7
	protected String outputPath = System.getProperty("user.home") + File.separator;
	
	// Abstract methods
	protected abstract boolean runGenerator();
	protected abstract void initialiazeGenerator();
	
	// Implemented Methods
	protected void terminateGenerator()
	{
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
	}
	
	public AGenerator (int thread_num)
	{
		this.thread_num = thread_num;		
		
		this.executor = new ThreadPoolExecutor(this.thread_num, this.thread_num, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10000, true),
                new MyRejectedExecutionHandeler()) {
					@Override
					protected void afterExecute(Runnable r, Throwable t)
					{
						// Do some logging here
						System.err.println("go");
						super.afterExecute(r, t);
					}
				};
		
				/*
		this.executor = new MyThreadPoolExecutor(this.thread_num, this.thread_num, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10000, true),
                new MyRejectedExecutionHandeler());
                
                */
	}
	
	public void run() 
	{
		this.initialiazeGenerator();
		
		long workLoad_start = System.currentTimeMillis() / 1000L;
		
		// TODO
		// make kill condition this.stopThread = true;
		// abstruct, to need to add a code block on each runGenerator method
		this.runGenerator();
		
		this.terminateGenerator();
		
		long workLoad_end = System.currentTimeMillis() / 1000L;
		long duration = workLoad_end -workLoad_start;
        System.out.println("Finished all threads (started at: " + workLoad_start + ", ended at: " + workLoad_end + ", duration: " + duration + ")");
	}
	
	 public void kill() {
		 this.stopThread = true;
	 }
	
	
	protected FileWriter initLog()
	{
		FileWriter writer = null;
		try {
			writer = new FileWriter(this.outputPath + this.getName() + ".csv");		
			writer.append("regTime");
		    writer.append(',');
		    writer.append("regCount");
		    writer.append('\n');
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("Saving at : " + this.outputPath + this.getName() + ".csv");
		return writer;
	}
}
