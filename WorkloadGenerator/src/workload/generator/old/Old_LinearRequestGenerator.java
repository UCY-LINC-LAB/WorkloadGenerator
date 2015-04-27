package workload.generator.old;


public class Old_LinearRequestGenerator extends RequestGenerator implements IReqGenerator {	 
	protected static String distribution = "linear";
	private long a = 0;
	
	public Old_LinearRequestGenerator(String lb_url, String nodelist, int req_rate, long max_time, long max_req, int thread_num) {
		super(lb_url, nodelist,  req_rate,  max_time,  max_req,  thread_num);
		this.a = (long) (this.req_rate * 0.1);
	}

	@Override
	public long getRate() {		
		if(req_rate - this.a > 0)
			req_rate = (long) (req_rate - this.a);
		
		return req_rate;
	}	
}
