package com.alabus.cordapp.template.client.webserver.bean;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.alabus.cordapp.template.client.webserver.NodeRPCConnection;
import com.alabus.cordapp.template.state.IOUState;

import net.corda.core.contracts.StateAndRef;


/**
 * This class implements.........
 * 
 * @author zh10082
 */

@Scope (value = "session")
@Component (value = "iouList")
@ELBeanName(value = "iouList")
@Join(path = "/iouList", to = "/node/iou-list.xhtml")
public class IOUStateListController {
	
	@Autowired
	private NodeRPCConnection connection;
	
	private List<IOUState> ious;

    @Deferred
    @RequestAction
    @IgnorePostback
    public void loadData() {
    	ious = new ArrayList<IOUState>();
    	SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    	connection.initialiseNodeRPCConnection();
    	List<StateAndRef<IOUState>> states = connection.getProxy().vaultQuery(IOUState.class).getStates();
    	for(StateAndRef<IOUState> state : states) {
    		ious.add(state.getState().getData());
    	}
    }

    public List<IOUState> getIous() {
    	loadData();
        return ious;
    }
}