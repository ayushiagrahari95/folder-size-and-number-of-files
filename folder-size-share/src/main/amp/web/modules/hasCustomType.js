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
		propertyName: "hasCustomType",
 
	   renderer: function type_renderer(record)
	   {
			var jsNode = record.jsNode;  
			var typestr = jsNode.type.replace(':', '_') + '.title';
			
			
			html = '<span class="item">' + "Type: " + '<b>' + this.msg(typestr) + '</b>' + '</span>'; 
			
			return html;
	  }
  });
}

})();