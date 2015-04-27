package workload.webFrontend.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import workload.generator.AGenerator;
import workload.generator.IGenerator;
import workload.generator.old.RequestGenerator;

/**
 * Servlet implementation class WorkLoadStatus
 */
public class WorkLoadStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WorkLoadStatus() {
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
		JSONObject jsonResponse;
		
		// Get all running threads
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
		
		//List<String> wrlIdArray;
		Map<String, String> wrlIdArray = new HashMap<String, String>();
		if(wrlId.contains(","))
		{	
			for(String key : wrlId.split(",")) {
				wrlIdArray.put(key, "");
			}
		}
		else
		{
			wrlIdArray.put(wrlId, "");
		}			
			
		boolean found = false;
		AGenerator generatorThread = null;
		for (Thread t : threadArray) {
			//System.out.println(t.getName());
			found = false;
			for (String id : wrlIdArray.keySet()) {
				if(wrlIdArray.get(id).isEmpty()) 
				{
				    if (t.getName().equals(id.trim()))
				    {
				    	generatorThread = (AGenerator) t;
				    	
				    	String status = "", progress = "";
						int activeCount = 0;
						long completedCount = 0, taskCount = 0;
						boolean waiting = false;
				    	
						ThreadPoolExecutor executor = (ThreadPoolExecutor) generatorThread.executor;
					    progress = String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
								executor.getPoolSize(),
								executor.getCorePoolSize(),
								executor.getActiveCount(),
								executor.getCompletedTaskCount(),
				                executor.getTaskCount(),
				                executor.isShutdown(),
				                executor.isTerminated());
					    System.out.println("<" + wrlId + ">" + progress);
					    
					    status = "running";
					    activeCount = executor.getActiveCount();		
					    completedCount = executor.getCompletedTaskCount();
					    taskCount = executor.getTaskCount();
					    waiting = executor.isShutdown();				    	
					    String updateTime = String.valueOf(System.currentTimeMillis() / 1000L);
					    
					    try 
					    {
					    	JSONObject wrldItem = new JSONObject();
					    	wrldItem.put("active", activeCount);
					    	wrldItem.put("updateTime", updateTime);
					    	wrldItem.put("completed", completedCount);
					    	wrldItem.put("task", taskCount);
						    if(waiting)
						    	status = "waiting";	
						    wrldItem.put("status", status);
						    
						    wrlIdArray.put(id, wrldItem.toString());
						    
					    } 
					    catch (JSONException e) 
					    {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    	break; //for (String id : wrlIdArray.keySet())
				    }
				}
			}
		}
		
		// Build report
	    jsonResponse = new JSONObject();
		for (String id : wrlIdArray.keySet()) {
			if(wrlIdArray.get(id).isEmpty()) 
			{
				String status = "", progress = "";
				int activeCount = 0;
				long completedCount = 0, taskCount = 0;
				boolean waiting = false;
				
				progress = "finish";
				status = "finish";
				String updateTime = String.valueOf(System.currentTimeMillis() / 1000L);
				
				try 
			    {
			    	JSONObject wrldItem = new JSONObject();
			    	wrldItem.put("active", activeCount);
			    	wrldItem.put("updateTime", updateTime);
			    	wrldItem.put("completed", completedCount);
			    	wrldItem.put("task", taskCount);
				    if(waiting)
				    	status = "waiting";	
				    wrldItem.put("status", status);
				    
				    wrlIdArray.put(id, wrldItem.toString());
				    
			    } 
			    catch (JSONException e) 
			    {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			JSONObject item;
			try {
				item = new JSONObject(wrlIdArray.get(id));
				jsonResponse.put(id, item);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	    response.setContentType("application/json");
	    response.getWriter().write(jsonResponse.toString());
    }

}
