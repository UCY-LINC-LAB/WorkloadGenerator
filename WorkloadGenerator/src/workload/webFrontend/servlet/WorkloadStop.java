package workload.webFrontend.servlet;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import workload.generator.AGenerator;

/**
 * Servlet implementation class WorkloadStop
 */
public class WorkloadStop extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WorkloadStop() {
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
String wrlId = request.getParameter("wrlId");
		
		Thread[] threadArray = null;
		
		boolean method = true;
		if(method)
		{
			//Method 1
			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
			threadArray = threadSet.toArray(new Thread[threadSet.size()]);			
		}
		else
		{
			//Method 2
			ThreadGroup rootGroup = Thread.currentThread( ).getThreadGroup( );
			ThreadGroup parentGroup;
			while ( ( parentGroup = rootGroup.getParent() ) != null ) {
			    rootGroup = parentGroup;
			}
			
			threadArray = new Thread[ rootGroup.activeCount() ];
			while ( rootGroup.enumerate( threadArray, true ) == threadArray.length ) {
				threadArray = new Thread[ threadArray.length * 2 ];
			}
			
		}
		
		boolean found = false;
		AGenerator generatorThread = null;
		for (Thread t : threadArray) {
			//System.out.println(t.getName());
		    if (t.getName().equals(wrlId.trim()))
		    {
		    	generatorThread = (AGenerator) t;
		    	found = true;
		    	//System.out.println(out);
		    	break;
		    }
		}
		
		String status = "", progress = "";
		int activeCount = 0;
		long completedCount = 0, taskCount = 0;
		boolean waiting = false;
		if(found)
		{
			generatorThread.kill();
			
			ThreadPoolExecutor executor = (ThreadPoolExecutor) generatorThread.executor;
			
			activeCount = executor.getActiveCount();		
		    completedCount = executor.getCompletedTaskCount();
		    taskCount = executor.getTaskCount();
		    waiting = executor.isShutdown();
			
		    progress = String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
					executor.getPoolSize(),
					executor.getCorePoolSize(),
					executor.getActiveCount(),
					executor.getCompletedTaskCount(),
	                executor.getTaskCount(),
	                executor.isShutdown(),
	                executor.isTerminated());
		    
		    status = "running";
		
		}
		else
		{
			progress = "finish";
			status = "finish";
		}		
		System.out.println("<" + wrlId + ">" + progress);
		
		
		String updateTime = String.valueOf(System.currentTimeMillis() / 1000L);
		// Build report
	    JSONObject json = new JSONObject();
	    try 
	    {
			json.put("active", activeCount);
			json.put("updateTime", updateTime);
		    json.put("completed", completedCount);
		    json.put("task", taskCount);
		    if(waiting)
		    	status = "waiting";	
		    json.put("status", status);
	    } 
	    catch (JSONException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    response.setContentType("application/json");
	    response.getWriter().write(json.toString());
    }

}
