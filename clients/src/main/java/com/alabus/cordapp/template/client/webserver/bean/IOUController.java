package com.alabus.cordapp.template.client.webserver.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.alabus.cordapp.template.client.webserver.NodeRPCConnection;
import com.alabus.cordapp.template.flows.ExampleFlow.Initiator;
import com.google.common.collect.ImmutableList;

import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;

/**
 * This class implements the controller for the iou bean
 * 
 * @author zh10082
 */
@Scope(value = "session")
@Component(value = "iouController")
@ELBeanName(value = "iouController")
@Join(path = "/iou", to = "/node/iou-form.xhtml")
public class IOUController {
	
	@Autowired
	private NodeRPCConnection connection;
	
    private IOUBean iou = new IOUBean();
    private final static Logger logger = LoggerFactory.getLogger(IOUController.class);

    private final List<String> serviceNames = ImmutableList.of("Notary");
	
    public String createIOU() throws Exception {
    	
    	int iouValue = iou.getValue();
        String partyName = iou.getOtherPartyName();
        if(partyName == null){
            throw new Exception("Query parameter 'partyName' must not be null.");
        }
        if (iouValue <= 0 ) {
        	throw new Exception("Query parameter 'iouValue' must be non-negative.");
        }
        
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        CordaRPCOps proxy = connection.getProxy();
        CordaX500Name partyX500Name = CordaX500Name.parse(partyName);
        
        Party otherParty;
        Set<Party> matchingParties = proxy.partiesFromName(partyX500Name.getOrganisation(), false);
        if(matchingParties.size() != 1) {
        	throw new Exception("Party named "+partyName+" not found or not unique.");
        } else {
        	otherParty = matchingParties.iterator().next();
        }
        
        try {
        	SignedTransaction signedTx = proxy.startTrackedFlowDynamic(Initiator.class, iouValue, otherParty).getReturnValue().get();
        	FacesContext facesContext = FacesContext.getCurrentInstance();
        	FacesMessage facesMessage = new FacesMessage("Transaction with id "+signedTx.getId()+" commited to ledger.");
        	facesContext.addMessage(null, facesMessage);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    	
        return "/faces/iou-list.xhtml?faces-redirect=true";
    }
    
    public List<SelectItem> getOtherPartySelectItems() {
    	
    	SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        CordaRPCOps proxy = connection.getProxy();
        
        CordaX500Name myLegalName = proxy.nodeInfo().getLegalIdentities().get(0).getName();
        List<NodeInfo> nodeInfo = proxy.networkMapSnapshot();
    	
    	List<SelectItem> selectItems = new ArrayList<SelectItem>();
    	for(NodeInfo nodeInfotmp : nodeInfo) {
    		if(!nodeInfotmp.getLegalIdentities().get(0).getName().equals(myLegalName) && !serviceNames.contains(nodeInfotmp.getLegalIdentities().get(0).getName().getOrganisation())) {
    			selectItems.add(new SelectItem(nodeInfotmp.getLegalIdentities().get(0).getName(), nodeInfotmp.getLegalIdentities().get(0).getName().getOrganisation()));
    		}
    	}
    	
    	return selectItems;
    	
    }

	public IOUBean getIou() {
        return iou;
    }
}