(function()
{
/**
 * Alfresco Slingshot aliases
 */
var $html = Alfresco.util.encodeHTML,
 $isValueSet = Alfresco.util.isValueSet;

var $html5 = Alfresco.util.encodeHTML,
$isValueSet = Alfresco.util.isValueSet;


if (Alfresco.DocumentList)
{     
	YAHOO.Bubbling.fire("registerRenderer",
	{
		propertyName: "hasCustomType",
 
	   renderer: function type_renderer(record)
	   {
			var jsNode = record.jsNode;  
			var typestr = jsNode.type.replace(':', '_') + '.title';
			
			
			html = '<span class="item">' + "Type: " + '<b>' + this.msg(typestr) + '</b>' + '</span>'; 
			
			return html;
	  }
  });
	YAHOO.Bubbling.fire("registerRenderer",
			{
				propertyName: "FolderInfo",
		 
			   renderer: function type_renderer(record)
			   {
					var jsNode = record.jsNode;  
					var nodeRef = jsNode.nodeRef;
					var size=0, noOfFiles;
					
					console.log(size);
					console.log(Alfresco.constants.PROXY_URI + "com/acme/nodesize/node-size.json?nodeRef=" + nodeRef);
					
					Alfresco.util.Ajax.jsonGet({
					/*	$.ajax({*/
						url: encodeURI(Alfresco.constants.PROXY_URI + 'com/acme/nodesize/node-size.json?nodeRef='+nodeRef),
						/*async: false,*/
						dataType:'json',
						successCallback:
					    {
					       fn: function loadWebscript_successCallback(response, config)
					       {
					           var obj = JSON.parse(response.serverResponse.responseText);
					           if (obj)
					           {
					        	 console.log("AJAX calls works. Printing the folder size " + obj.size + " & no of files " + obj.noOfFiles);
					        	   size = obj.size;
					        	   console.log(size);
					        	   
					        	   noOfFiles = obj.noOfFiles;
					        	  
					        	 
					        	  
					           }
					       },
						   scope: this
					    },
					    scope: this
					 });
						 
					console.log(size);
			   }
		  });
}



})();