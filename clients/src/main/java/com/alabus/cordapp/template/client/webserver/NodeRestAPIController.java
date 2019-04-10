package com.alabus.cordapp.template.client.webserver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alabus.cordapp.template.flows.ExampleFlow.Initiator;
import com.alabus.cordapp.template.state.IOUState;
import com.google.common.collect.ImmutableList;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/api/") // The paths for HTTP requests are relative to this base path.
public class NodeRestAPIController {
    private final CordaRPCOps proxy;
    private final static Logger logger = LoggerFactory.getLogger(NodeRestAPIController.class);

    private final List<String> serviceNames = ImmutableList.of("Notary");
	private CordaX500Name myLegalName;
    
    public NodeRestAPIController(NodeRPCConnection rpc) {
        this.proxy = rpc.proxy;
        this.myLegalName = proxy.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here.";
    }
    
    @GetMapping(value = "/ious", produces = "application/json")
    private ResponseEntity<List<StateAndRef<IOUState>>> getIOUs() {
    	return ResponseEntity.ok(proxy.vaultQuery(IOUState.class).getStates());
    }
    
    @PostMapping(value = "/create-iou", produces = "text/plain", headers = "Content-Type=application/x-www-form-urlencoded")
    private ResponseEntity<String> createIOU(HttpServletRequest request)  {
        int iouValue = request.getParameter("iouValue") != null ? Integer.valueOf(request.getParameter("iouValue")).intValue() : 0;
        String partyName = request.getParameter("partyName");
        if(partyName == null){
            return ResponseEntity.badRequest().body("Query parameter 'partyName' must not be null.\n");
        }
        if (iouValue <= 0 ) {
            return ResponseEntity.badRequest().body("Query parameter 'iouValue' must be non-negative.\n");
        }
        CordaX500Name partyX500Name = CordaX500Name.parse(partyName);
        Party otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name);
        if(otherParty != null) {
        	return ResponseEntity.badRequest().body("Party named $partyName cannot be found.\n");
        }
        try {
        	SignedTransaction signedTx = proxy.startTrackedFlowDynamic(Initiator.class, iouValue, otherParty).getReturnValue().get();
        	return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id "+signedTx.getId()+" committed to ledger.\n");
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    
}