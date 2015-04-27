package workload.generator.old;


public class Old_SinusoidalRequestGenerator extends RequestGenerator implements IReqGenerator {
	protected static String distribution = "sinusoidal";
	private long step = (long) 0.1;
	private long last = (long) 0.0;
	
	public Old_SinusoidalRequestGenerator(String lb_url, String nodelist, int req_rate, long max_time, long max_req, int thread_num) {
		super(lb_url, nodelist,  req_rate,  max_time,  max_req,  thread_num);		
	}

	@Override
	public long getRate() {
		this.req_rate = (long) (this.req_rate + Math.sin(this.last));
		this.last -= this.step;
		
		return this.req_rate;
	}	
}
