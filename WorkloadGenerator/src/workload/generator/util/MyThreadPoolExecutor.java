package workload.generator.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class MyThreadPoolExecutor extends ThreadPoolExecutor {
	
	private final ThreadLocal<Long> startTime  = new ThreadLocal<Long>();	
	Handler lh;
	private final Logger log;
	private final AtomicLong numTasks = new AtomicLong();
	private final AtomicLong totalTime = new AtomicLong();
	
	public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) 
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		// TODO Auto-generated constructor stub
		
		//lh = new FileHandler("%t/wombat.log");
		lh = new ConsoleHandler();
		
		this.log = Logger.getLogger("TimingThreadPool");
		this.log.addHandler(lh);
		//this.log.setLevel(Level.INFO);
	}
	protected void beforeExecute(Thread t, Runnable r) 
	{
		super.beforeExecute(t, r);
		//log.fine(String.format("Thread %s: start %s", t, r));
		//startTime.set(System.nanoTime());
	}
	
	protected void afterExecute(Runnable r, Throwable t) 
	{
		try {
		    long endTime = System.nanoTime();
		    long taskTime = endTime - startTime.get();
		    numTasks.incrementAndGet();
		    totalTime.addAndGet(taskTime);
		    //log.fine(String.format("Thread %s: end %s, time=%dns", t, r, taskTime));
		    System.out.println(String.format("Thread %s, completed in %dns", t, taskTime));
		} finally {
		    super.afterExecute(r, t);
		}
	}
	
	protected void terminated() 
	{
		try {
		    log.info(String.format("Terminated: avg time=%dns", totalTime.get() / numTasks.get()));
		    System.out.println(String.format("Terminated: avg time=%dns", totalTime.get() / numTasks.get()));
		} finally {
		    super.terminated();
		}
	}

}
