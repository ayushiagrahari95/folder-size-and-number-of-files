(function()
{
/**
 * Alfresco Slingshot aliases
 */
var $html = Alfresco.util.encodeHTML,
 $isValueSet = Alfresco.util.isValueSet;


if (Alfresco.DocumentList)
{     
	YAHOO.Bubbling.fire("registerRenderer",
			{
				propertyName: "showFolderInfo",
		 
			   renderer: function type_renderer(record)
			   {
					var jsNode = record.jsNode;  
					var nodeRef = jsNode.nodeRef;
					var nodeid = nodeRef.id;			
					var size_in_bytes, noOfFiles, noOfFolders;
					var formatted_size;
					console.log(Alfresco.constants.PROXY_URI + "com/acme/nodesize/node-size.json?nodeRef=" + nodeRef);
					
					Alfresco.util.Ajax.jsonGet({
						url: encodeURI(Alfresco.constants.PROXY_URI + 'com/acme/nodesize/node-size.json?nodeRef='+nodeRef),
						successCallback:
					    {
					       fn: function loadWebscript_successCallback(response, config)
					       {
					           var obj = JSON.parse(response.serverResponse.responseText);
					           if (obj)
					           {
					        	 console.log("AJAX calls works. Printing the folder size " + obj.size + " & no of files " + obj.noOfFiles);
					        	 size_in_bytes = obj.size;
					        	 noOfFiles = obj.noOfFiles;
					        	 noOfFolders = obj.noOfFolders;
					        	 formatted_size=formatBytes(size_in_bytes);
					        	 
					        	   document.getElementsByClassName('size_' + nodeid)[0].innerHTML = '<b>' + formatted_size+ '</b>' + ", No of Files: " + '<b>' + noOfFiles + '</b>' + ", No of Folders: " + '<b>' + noOfFolders + '</b>';
					           }
					       }
					    }
					 });
					
					function formatBytes(bytes) {
						if(bytes == 0) return '0 Byte';
						var k = 1024; // or 1024 for binary
						var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
						var i = Math.floor(Math.log(bytes) / Math.log(k));
						return parseFloat((bytes / Math.pow(k, i)).toFixed()) + ' ' + sizes[i];
					}
					
					html = '<span class="item">' + "Size: " + '<span class="size_'+ nodeid +'">' +  + '</span>' + '</span>';
					
					return html;
			   }
		  });
}

})();