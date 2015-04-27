package workload.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import workload.generator.request.HttpGetRequest;
import workload.generator.request.NullRequest;

public class SinusoidalRequestGenerator extends AGenerator implements IGenerator {
	private final String distribution = "sinusoidal";
	
	protected String lb_url;
	protected long req_rate;
	protected long max_time;
	protected long max_req;
	private int period;
	private int frameCount;
	private int sinStaringPoint;
	private long interval;
	private int max;
	
	protected String nodelist;
	
	public SinusoidalRequestGenerator(String lb_url, String nodelist, long max_time, long max_req, int thread_num) {
		super(thread_num);
		
		String test = "Starting " + this.distribution + " HttpGetRequest Generator>> load_balancer: "+lb_url+" request_rate: "+req_rate+" nodelist: "+nodelist
		         + " seconds"+" max_exec_time: "+max_time+" seconds "+"max_requests: "+max_req+" thread_num: "+ thread_num;		
		System.err.println(test);
				
		
		this.max_time = max_time * 1000;
		this.max_req = max_req;
		
		this.lb_url = lb_url;
		this.nodelist = nodelist;
		
	}
	
	public SinusoidalRequestGenerator(String lb_url, String nodelist, long max_time, long max_req, int thread_num, int period, int max) {		
		this(lb_url, nodelist, max_time, max_req, thread_num);
		
		//this.req_rate = req_rate * 1000;
		this.period = period;
		this.max = max;
	}
	
	@Override
	protected void initialiazeGenerator() {
		this.calculateMissingParam();
		
	}
	
	public boolean runGenerator()  {
		
		FileWriter writer = this.initLog();
		
		double elapsed_time = 0;
		long req_count = 0;	
		
		//ROUND(((($G$1*F4) + $G$1)/2),0)
		//=SIN(E4/360*2*PI())
		
		int regPerFrame = 0;
		regPerFrame = 1;
		int currRadian = this.sinStaringPoint;
		long inter = this.interval;
		//while(elapsed_time < this.max_time && req_count < this.max_req)
		while(elapsed_time < this.max_time )
		{
			if(this.stopThread)
					break;
			try
			{
				double a = Math.sin((currRadian/(double)360)*2*Math.PI);
				regPerFrame = (int) Math.round(((a*this.max) + this.max)/2);				
				
				if(regPerFrame != 0)
					inter = this.interval / regPerFrame;				
				
				long seqStart = System.nanoTime();
				for(int i=0; i < regPerFrame; i++)
				{
					this.executor.execute(new HttpGetRequest(this.lb_url, this.nodelist));
					//this.executor.execute(new NullRequest(this.lb_url, this.nodelist));
					req_count++;					
					Thread.sleep(inter);
				}
				
				writer.append(String.valueOf(System.currentTimeMillis() / 1000L));
			    writer.append(',');
			    writer.append(String.valueOf(regPerFrame));
			    writer.append('\n');
				
				elapsed_time += ((System.nanoTime()) - seqStart) / 1000000.0;
								
				currRadian = currRadian + (360 / this.frameCount);
				if(currRadian > 360)
					currRadian = currRadian % 360;
				
				
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
		this.frameCount = 9; //360 / 9 = 40
		this.sinStaringPoint = 280;
		// TODO Auto-generated method stub
		//this.period = 4;		
		this.interval = this.max_time / (this.period * this.frameCount);
		
		//this.max = 40;
		
	}

	
	public void setSpecifics() {
		// TODO Auto-generated method stub
		
	}
}
