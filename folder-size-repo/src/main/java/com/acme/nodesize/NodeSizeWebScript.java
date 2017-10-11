package com.acme.nodesize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.alfresco.model.ContentModel;
import org.alfresco.model.ForumModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Calculate node content size recursively. Modified from
 * http://www.dedunu.info/2015/03/alfresco-calculate-folder-size-using.html
 * 
 * @author Zhihai Liu
 * 
 */
public class NodeSizeWebScript extends DeclarativeWebScript {
	long count;
	static Logger log = Logger.getLogger(NodeSizeWebScript.class.getName());
	private NodeService nodeService;
	private DictionaryService dictionaryService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public final void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req,
			Status status, Cache cache) {
		Map<String, Object> model = new HashMap<String, Object>();
		String nodeRefId = req.getParameter("nodeRef");
		NodeRef nodeRef = new NodeRef(nodeRefId);

		String nodeName = (String) nodeService.getProperty(nodeRef,
				ContentModel.PROP_NAME);
		long size = getNodeSize(nodeRef);
		long[] nodeInfo = getNodeInfo(nodeRef);
		long noOfFiles = nodeInfo[0];
		long noOfFolders = nodeInfo[1];
		model.put("nodeName", nodeName);
		model.put("size", Long.toString(size));
		model.put("noOfFiles", noOfFiles);
		model.put("noOfFolders", noOfFolders);
		return model;
	}

	private long[] getNodeInfo(NodeRef nodeRef) {
		long filesCount = 0;
		long foldersCount = 0;

		long[] nodeInfo = new long[2];

		// Collecting child nodes' sizes
		// even a document (cm:content) can have child nodes, such as thumbnail
		List<ChildAssociationRef> chilAssocsList = nodeService
				.getChildAssocs(nodeRef);

		for (ChildAssociationRef childAssociationRef : chilAssocsList) {
			NodeRef childNodeRef = childAssociationRef.getChildRef();

			QName targetNodeQName = nodeService.getType(childNodeRef);
			
			// check whether 
			if (ContentModel.TYPE_CONTENT.toString().equals(
					nodeService.getType(childNodeRef).toString())) {
				filesCount += 1;
			}else if (ContentModel.TYPE_FOLDER.toString().equals(
					nodeService.getType(childNodeRef).toString())) {
				foldersCount += 1;
			}else if(isSubTypeOf(ContentModel.TYPE_CONTENT, targetNodeQName)){
				filesCount += 1;
			}else if(isSubTypeOf(ContentModel.TYPE_FOLDER, targetNodeQName)) {
				foldersCount += 1;
			}
		}

		nodeInfo[0] = filesCount;
		nodeInfo[1] = foldersCount;

		return nodeInfo;
	}

	private long getNodeSize(NodeRef nodeRef) {
		long size = 0;
		// Collecting current node size
		ContentData contentData = (ContentData) nodeService.getProperty(
				nodeRef, ContentModel.PROP_CONTENT);
		try {
			size = contentData.getSize();
		} catch (Exception e) {
			size = 0;
		}

		// Collecting child nodes' sizes
		// even a document (cm:content) can have child nodes, such as thumbnail
		List<ChildAssociationRef> chilAssocsList = nodeService
				.getChildAssocs(nodeRef);

		for (ChildAssociationRef childAssociationRef : chilAssocsList) {
			NodeRef childNodeRef = childAssociationRef.getChildRef();
			QName targetNodeQName = nodeService.getType(childNodeRef);
			
			if (ContentModel.TYPE_CONTENT.toString().equals(
					nodeService.getType(childNodeRef).toString())
					|| ContentModel.TYPE_FOLDER.toString().equals(
							nodeService.getType(childNodeRef).toString()) || isSubTypeOf(ContentModel.TYPE_CONTENT, targetNodeQName) 
					|| isSubTypeOf(ContentModel.TYPE_FOLDER, targetNodeQName))
				size = size + getNodeSize(childNodeRef);

		}
		return size;
	}
	
	/**
     * Determines whether one class is a sub type of an other.  Returns true if it is, false otherwise.
     * 
     * @param childNodeQName         the class to test
     * @param parentNodeQName     test whether the class is a sub-type of this class
     * @return boolean      true if it is a sub-class, false otherwise
     */
    public boolean isSubTypeOf(final QName parentNodeQName, final QName childNodeQName)
    {
    	return dictionaryService.isSubClass(childNodeQName, parentNodeQName);
    	
    }
}