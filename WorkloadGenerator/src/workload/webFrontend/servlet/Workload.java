package workload.webFrontend.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import workload.generator.IGenerator;
import workload.generator.LinearRequestGenerator;
import workload.generator.SinusoidalRequestGenerator;
import workload.generator.StableRequestGenerator;
import workload.generator.old.IReqGenerator;
import workload.generator.old.Old_LinearRequestGenerator;
import workload.generator.old.Old_SinusoidalRequestGenerator;
import workload.generator.old.RequestGenerator;

/**
 * Servlet implementation class Workload
 */
public class Workload extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Workload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doRequest(request, response);
	}
	
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String[] args = new String[6];
    	//args[0] = "http://109.231.122.181:30000/AppServer/restAPI/sync"; //<load_balancer_url>
    	//args[1] = "109.231.122.187"; // <database_nodes_comma_seperated>
    	//args[2] = "2"; // <request_rate_in_seconds>
    	//args[3] = "1000"; // <request_rate_in_seconds> 
    	//args[4] = "10"; //<request_rate_in_seconds> 
    	//args[5] = "10"; //<request_rate_in_seconds>
    	
    	// Common Workload properties
    	String t = request.getParameter("wgType");
    	String defaultSubURL = "";
    	if (t != null && t.equals("generic")){
    		System.out.println("wg>> not going to use defaults, user requested generic workload");
    	}
    	else{
    		defaultSubURL = "/AppServer/restAPI/sync"; // TODO WARNING hardcoded url part
    	}
    	
    	args[0] = request.getParameter("url") + defaultSubURL;
    	args[1] = request.getParameter("dbNodes");
    	args[3] = request.getParameter("maxTime");
    	args[4] = request.getParameter("maxReq");
    	args[5] = request.getParameter("threads");
    	
    	
    	String distribution = request.getParameter("HttpLoadDistro");		
		try
		{
			//IReqGenerator generator;
			IGenerator generator = null;
			if(distribution.equals("Sinusoidal"))
			{
				// Type specific properties				
				int period = Integer.parseInt(request.getParameter(distribution + "_" + "period"));
				int max = Integer.parseInt(request.getParameter(distribution + "_" + "max"));
				
				
				generator = new SinusoidalRequestGenerator(args[0], args[1], Long.parseLong(args[3]), Long.parseLong(args[4]), Integer.parseInt(args[5]), period, max);				
			}
			else if(distribution.equals("Linear"))
			{		
				// Type specific properties	
				String rRate = request.getParameter(distribution + "_" + "rRate");
				
				generator = new LinearRequestGenerator(args[0], args[1], Long.parseLong(args[3]), Long.parseLong(args[4]), Integer.parseInt(args[5]), Integer.parseInt(rRate));
			}
			else
			{
				// Type specific properties	
				String rRate = request.getParameter(distribution + "_" + "rRate");
				
				generator = new StableRequestGenerator(args[0], args[1], Long.parseLong(args[3]), Long.parseLong(args[4]), Integer.parseInt(args[5]), Integer.parseInt(rRate));
			}
			System.out.println(distribution);			
			
			String name = String.valueOf(System.nanoTime());
			String startTime = String.valueOf(System.currentTimeMillis() / 1000L);
			// Spawn a new thread to handle the generator and close the servlet
		    ((Thread) generator).setName(name);
		    ((Thread) generator).start();
		    System.out.println(((Thread) generator).getName());
		    
		    // Build report
		    JSONObject json = new JSONObject();
		    json.put("status", "send");
		    json.put("name", name);
		    json.put("startTime", startTime);
		    
		    response.setContentType("application/json");
		    response.getWriter().write(json.toString());
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//System.err.println(usage);			
		}
    }

}
