package com.acme.nodesize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Calculate node content size recursively.
 * Modified from http://www.dedunu.info/2015/03/alfresco-calculate-folder-size-using.html
 * 
 * @author Zhihai Liu
 *
 */
public class NodeSizeWebScript extends DeclarativeWebScript {
	private static final Logger log = Logger.getLogger(NodeSizeWebScript.class.getName());
	private NodeService nodeService;

	public final void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		Map<String, Object> model = new HashMap<String, Object>();
		String nodeRefId = req.getParameter("nodeRef");
		NodeRef nodeRef = new NodeRef(nodeRefId);

		String nodeName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		long size = getNodeSize(nodeRef);
		long noOfFiles = getNoOfFiles(nodeRef);
		log.info("no of files"+ noOfFiles);
		model.put("nodeName", nodeName);
		model.put("size", Long.toString(size));
		model.put("noOfFiles",noOfFiles);
		return model;
	}

	private long getNodeSize(NodeRef nodeRef) {
		long size = 0;
		// Collecting current node size
		ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
		try {
			size = contentData.getSize();
		} catch (Exception e) {
			size = 0;
		}

		// Collecting child nodes' sizes
		// even a document (cm:content) can have child nodes, such as thumbnail
		List<ChildAssociationRef> chilAssocsList = nodeService.getChildAssocs(nodeRef);

		for (ChildAssociationRef childAssociationRef : chilAssocsList) {
			NodeRef childNodeRef = childAssociationRef.getChildRef();
			size = size + getNodeSize(childNodeRef);
		}
		return size;
	}
	
	private long getNoOfFiles(NodeRef nodeRef) {
		long count = 0;
		// Collecting current node size
		log.info("getting the number of files");
		// Collecting child nodes' sizes
		// even a document (cm:content) can have child nodes, such as thumbnail
		List<ChildAssociationRef> chilAssocsList = nodeService.getChildAssocs(nodeRef);
		
		log.info("getting the list of child files");
		for (ChildAssociationRef childAssociationRef : chilAssocsList) {
			log.info("entering for loop");
			NodeRef childNodeRef = childAssociationRef.getChildRef();
			count += 1;
			log.info("counter"+count);
		}
		return count;
	}

}