<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Workload Generator</title>
	<link href="Library/resources/css/style.css" rel="stylesheet">
	<script src="http://code.jquery.com/jquery-1.9.0.js"></script>
	<script type="text/javascript" src="Library/resources/js/script.js"></script>
</head>
<body>
	<div class="popupOverlay noDisplay">
		<div class="popup dialog noDisplay">
			<div class="head">
				<div class="popupControl">
					<button name="close">Close</button>
				</div>
			</div>
			<div class="content">
				<form id="newWorkload"
					action="/WorkloadGenerator/ajax/workload/new" method="post">
					<div class="formContent">
						<div data-section="common">
							<h4> Workload Common Properties</h4>
							<div class="formRow">
								<label for="url">WebServer Url</label> <input name="url"><span
									class="defaultInputValue noDisplay">http://109.231.122.181:30000</span>
							</div>
							
							<div class="formRow">
								<label>I Want Generic Workload</label> <input name="wgType" type="checkbox" value="generic" />
							</div>
							
							<div class="formRow">
								<label>Threads</label> <input name="threads"><span
									class="defaultInputValue noDisplay">10</span>
							</div>	
							<div class="formRow">
									<label>Max Workload Time (in secs)</label> <input name="maxTime"><span
										class="defaultInputValue noDisplay">1000</span>
							</div>
							<div class="formRow">
								<label>Max Requests</label> <input name="maxReq"><span
									class="defaultInputValue noDisplay">10</span>
							</div>
						
							<div class="formRow">
								<label>Http Workload Distribution</label> <select
									name="HttpLoadDistro">
									<option value="Stable">Stable</option>
									<option value="Linear">Linear</option>
									<option value="Sinusoidal">Sinusoidal</option>
								</select>
							</div>
						</div>
						<div data-section="distroSpecifics">
						<h4> Workload Distribution Specific Properties</h4>
							<div data-group="Stable">
								<div class="formRow">
									<label>Request Rate (in secs)</label> <input name="Stable_rRate"><span
										class="defaultInputValue noDisplay">2</span>
								</div>
							</div>
							<div data-group="Linear" class="noDisplay">
							<div class="formRow">
									<label>Request Rate (in secs)</label> <input name="Linear_rRate"><span
										class="defaultInputValue noDisplay">2</span>
								</div>
							</div>
							<div data-group="Sinusoidal" class="noDisplay">
								<div class="formRow">
									<label>Number of Waves</label> <input name="Sinusoidal_period"><span
										class="defaultInputValue noDisplay">4</span>
								</div>
								<div class="formRow">
									<label>Wave peek value</label> <input name="Sinusoidal_max"><span
										class="defaultInputValue noDisplay">40</span>
								</div>						
							</div>
												
						</div>
						<div data-section="dbLoad">
						<h4> Database Workload Properties</h4>
							<div class="formRow">
								<label>Database Nodes</label> <input name="dbNodes"><span
									class="defaultInputValue noDisplay">172.31.39.10</span>
							</div>
							<div class="formRow">
								<label for="loadDistro">DB Workload Type</label> <select
									name="DBLoadDistro">
									<option>Write Heavy (upload)</option>
									<option>Read Heavy</option>
									<option>Combination</option>
								</select>
							</div>						
						</div>
					</div>
				</form>
			</div>
			<div class="controls formControls">
				<div>
					<button type="submit" name="newWorkload">Go</button>
					<button type="button" name="reset">Reset</button>
				</div>
			</div>
		</div>
	</div>
	<div class="page">
		<div class="workloadsHeader">
			<div class="title">
				<span>My Workloads</span>
			</div>
			<div class="control">
				<button id="refreshAll">Refresh All</button>
				<button id="add">[+] Add New</button>
			</div>
		</div>

		<div class="well">
			<div class="noDisplay wellItemTemplate wellItem">
				<div class="indicatorWrapper">
				<form action="/workload_webFrontend/ajax/workload/status" method="post">
						<input type="hidden" name="wrlId" />
						<div class="row header">
							<div class="left">
								<span>WorkLoad Id : </span><span data-id="wrlId"></span>
							</div>					
							<div class="right">
								<button type="submit">Check Status</button>
								<button type="button" name="delete">Clear</button>
								<button type="button" name="stop">Stop</button>
							</div>
						</div>					
						<div class="row">
							<div class="left">
								<span class="keyValue"><span>Start Time : </span><span data-id=startTime></span></span>
								<span class="keyValue"><span>Update Time : </span><span data-id=updateTime></span></span>
							</div>					
							<div class="right">
								<span class="keyValue"><span>Status : </span><span data-id="status"></span></span>
							</div>	
						</div>					
						<div class="row noDisplay">
							<span class="keyValue"><span>Total Sent : </span><span data-id="task"></span></span>
							<span class="keyValue"><span>Active : </span><span data-id="active"></span></span>
							<span class="keyValue"><span>Completed : </span><span data-id="completed"></span></span>
							<span class="keyValue"><span>Duration : </span><span data-id="duration"></span></span>
						</div>
					</form>
				</div>
			</div>
			<div class="wellContentHolder"></div>
		</div>
	</div>
</body>
</html>