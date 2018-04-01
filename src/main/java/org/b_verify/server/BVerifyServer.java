package org.b_verify.server;

import java.rmi.registry.Registry;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;
import org.b_verify.common.DummyProof;
import org.b_verify.common.InsufficientFundsException;
import org.b_verify.common.Proof;
import org.catena.common.CatenaUtils;
import org.catena.server.CatenaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for managing server data structures, broadcasting commitments 
 * and constructing proofs for servers. Must be threadsafe
 * @author henryaspegren
 *
 */
public class BVerifyServer implements BVerifyProtocolServer {
	
	private Registry registry;
	private CatenaServer commitmentPublisher;

	/** Debugging - use this instead of printing to Standard out **/
    private static final Logger log = LoggerFactory.getLogger(BVerifyServer.class);
	
	public BVerifyServer(Registry registry, CatenaServer publisher) {
		this.commitmentPublisher = publisher;
		this.registry = registry;
	}

	public boolean transfer(String userTo, String userFrom, int amount) throws InsufficientFundsException {
		log.info("Transfer request recieved - userTo:"+userTo+" userFrom:"+userFrom+" amount: "+amount);
		
		try {
			String[] usersToContact = new String[] {userTo, userFrom};
			// contact all users
			for(String user : usersToContact) {
				log.debug("Looking up "+user+" in java RMI registry");
	            BVerifyProtocolClient client = (BVerifyProtocolClient) this.registry.lookup(user);
				log.debug("Calling RMI method on "+user);
	            Proof respTo = client.proposeTransfer(userTo, userFrom, 
	            		new DummyProof(userFrom+" -- "+amount+" -- >"+userTo));
	            log.info("Response from "+user+": "+respTo.toString());
			}    
			// for now assume all clients approve and publish commitment 
            log.info("Transfer success");
            String stmt = "NEW COMMITMENT! " +System.currentTimeMillis();
            log.info("Creating new commitment: "+stmt);
            commitmentPublisher.appendStatement(stmt.getBytes());
            // TODO - figure out how to best incorporate this.
            CatenaUtils.generateBlockRegtest();
            return true;
            
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public Proof getBalance(String user, int time) {
		log.info("getBalance request recieved - user: "+user+" time: "+ time);
		return new DummyProof(user+" balance at time: "+time);
	}

	public Proof getBalances(int time, boolean changedOnly) {
		log.info("getBalances request recieved - time: "+ time+ " changedOnly: "+changedOnly);
		return new DummyProof("user balances at time: "+time+" changed only: "+changedOnly);
	}

}
