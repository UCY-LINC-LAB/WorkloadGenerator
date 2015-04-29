var dateFormat = function(timeStamp)
{
	var d = new Date(timeStamp);
	var curr_date = d.getDate();
	var curr_month = d.getMonth();
	curr_month++;
	var curr_year = d.getFullYear();
	var curr_hour = d.getHours();
	var curr_min = d.getMinutes();

	var curr_sec = d.getSeconds();
	var curr_msec = d.getMilliseconds();	
	
	var str = curr_date + "/" + curr_month+ "/" + curr_year + ", " + curr_hour + ":" + curr_min + ":"  + curr_sec + ":" + curr_msec;
	
	return str;
};

var resizeIndicator = function (parentItem)
{
	console.log(parentItem.height());
	parentItem.find('.indicator').height(parentItem.height());
};


var statusUpdate_parseResponse = function(jsonObjArray) {
	$.each(jsonObjArray, function(wrlId, dataObj)
	{
		var wellItem = $('.wellItem form > input[name="wrlId"][value="' + wrlId + '"]').closest('.wellItem');
		
		console.log(wellItem);
		
		wellItem.find('span[data-id="status"]').text(dataObj.status);
		
		console.log(wellItem.find('span[data-id="status"]'));
		
		wellItem.find('span[data-id="task"]').text(dataObj.task);
		wellItem.find('span[data-id="active"]').text(dataObj.active);
		wellItem.find('span[data-id="completed"]').text(dataObj.completed);
		
		var updateTime = dateFormat(dataObj.updateTime*1000);
		wellItem.find('span[data-id="updateTime"]').text(updateTime);
		
		if(dataObj.status == "finish")
		{
			duration = "-";
		}
		else
		{
			duration = parseInt(dataObj.updateTime) - parseInt(wellItem.find('span[data-id="startTime"]').text());
		}
		wellItem.find('span[data-id="duration"]').text(duration);
		
		wellItem.attr('class', 'wellItem'); // reset class
		wellItem.addClass(dataObj.status);
		
		//
		wellItem.find('span[data-id="task"]').closest('div.row').removeClass('noDisplay');
		
		//
		resizeIndicator(wellItem);
	});
};

$('document').ready(function ()
{
	$('button[name="newWorkload"]').on('click', function(){
		$('#newWorkload').submit();;
	});
	
	var form = $('#newWorkload');
	form.submit(function () {	 
		$.ajax({
			type: form.attr('method'),
			url: form.attr('action'),
			data: form.serialize(),
			success: function(jsonObj){
				var wellHolder = $('.well > .wellContentHolder');
				var wellItem = $('.well > .wellItemTemplate').clone();				
				// remove unwanted classes
				wellItem.removeClass('noDisplay');
				wellItem.removeClass('wellItemTemplate');				
				// Fill item properties
				wellItem.find('input[name="wrlId"]').val(jsonObj.name);
				wellItem.find('span[data-id="wrlId"]').text(jsonObj.name);
				wellItem.find('span[data-id="status"]').text(jsonObj.status);
				var startTime = dateFormat(jsonObj.startTime*1000);
				wellItem.find('span[data-id="startTime"]').text(startTime);
				wellItem.find('span[data-id="updateTime"]').text(startTime);
				wellItem.find('span[data-id="duration"]').text(0);
				wellItem.addClass(jsonObj.status);
				
				//
				resizeIndicator(wellItem);
				
				// Assign events
				// Check Status
				wellItem.find('form').submit(function () {
					var form = wellItem.find('form');
					$.ajax({
						type: form.attr('method'),
						url: form.attr('action'),
						data: form.serialize(),
						success: function(jsonResponse) {
							statusUpdate_parseResponse(jsonResponse); 
						}				
					});	 
					return false;
				});
				
				// Remove from view
				wellItem.find('button[name="delete"]').on('click', function(){
					$(this).closest('div.wellItem').remove();
				});				
				
				// Stop
				wellItem.find('button[name="stop"]').on('click', function(){
					var id = $(this).closest('div.wellItem').find('input[name="wrlId"]');
					
					var dt = new Object();
					dt['wrlId'] = id.val();
					
					$.ajax({
						type: 'GET',
						url: 'WorkloadGenerator/ajax/workload/stop',
						data: dt,
						success: function(jsonObj){
							wellItem.find('span[data-id="status"]').text(jsonObj.status);
							
							wellItem.find('span[data-id="task"]').text(jsonObj.task);
							wellItem.find('span[data-id="active"]').text(jsonObj.active);
							wellItem.find('span[data-id="completed"]').text(jsonObj.completed);
							
							var updateTime = dateFormat(jsonObj.updateTime*1000);
							wellItem.find('span[data-id="updateTime"]').text(updateTime);
							
							if(jsonObj.status == "finish")
							{
								duration = "-";
							}
							else
							{
								duration = parseInt(jsonObj.updateTime) - parseInt(wellItem.find('span[data-id="startTime"]').text());
							}
							wellItem.find('span[data-id="duration"]').text(duration);
							
							wellItem.attr('class', 'wellItem'); // reset class
							wellItem.addClass(jsonObj.status);
							
							//
							wellItem.find('span[data-id="task"]').closest('div.row').removeClass('noDisplay');
							
							//
							resizeIndicator(wellItem);
						}
					});	 
				});
					
				
				// Append item to well
				wellHolder.append(wellItem);
				
				//
				$('#result').attr("value",jsonObj.name);		 
			}
		});	 
		return false;
	});
		
	$('button[name="reset"]').on('click', function(){
		var rows = $(this).closest('div.formControls').siblings('.content').find('.formContent .formRow');
		rows.each(function(){
			$(this).find('input').val($(this).find('span.defaultInputValue').html());
		});
	});
	
	
	$('button#add').on('click', function(){
		$('.popupOverlay').removeClass('noDisplay');
		$('.popup').removeClass('noDisplay');
	});
	
	$('button#refreshAll').on('click', function(){
		var ids = "";
		$('.well > .wellContentHolder > .wellItem').each(function(){
			ids += $(this).find('input[name="wrlId"]').val() + ",";
		});
		
		var dt = new Object();
		dt['wrlId'] = ids;
		
		$.ajax({
			type: 'GET',
			url: '/WorkloadGenerator/ajax/workload/status',
			data: dt,
			success: function(jsonResponse){
				statusUpdate_parseResponse(jsonResponse);
			}
		});	 
		
	});
	
	$('button[name="close"]').on('click', function(){
		$('.popupOverlay').addClass('noDisplay');
		$('.popup').addClass('noDisplay');
	});
	
	$('select[name="HttpLoadDistro"]').on('change', function(){		
		console.log($(this).val());
		$('[data-section="distroSpecifics"] > div[data-group]').addClass('noDisplay');
		$('[data-section="distroSpecifics"] > div[data-group="'+$(this).val()+'"]').removeClass('noDisplay');		
	});
});