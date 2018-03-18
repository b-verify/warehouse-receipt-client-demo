package org.b_verify.server;

import java.rmi.registry.Registry;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;
import org.b_verify.common.DummyProof;
import org.b_verify.common.InsufficientFundsException;
import org.b_verify.common.Proof;
import org.catena.server.CatenaServer;

/**
 * Responsible for managing server data structures, broadcasting commitments 
 * and constructing proofs for servers. Must be threadsafe
 * @author henryaspegren
 *
 */
public class BVerifyServer implements BVerifyProtocolServer {
	
	private Registry registry;
	private CatenaServer commitmentPublisher;
	
	public BVerifyServer(Registry registry, CatenaServer publisher) {
		this.commitmentPublisher = publisher;
		this.registry = registry;
	}

	public boolean transfer(String userTo, String userFrom, int amount) throws InsufficientFundsException {
		System.out.println(userFrom+" -- "+amount+" -- >"+userTo);
		
		try {
            BVerifyProtocolClient clientTo = (BVerifyProtocolClient) this.registry.lookup(userTo);
            Proof respTo = clientTo.proposeTransfer(userTo, userFrom, new DummyProof(userFrom+" -- "+amount+" -- >"+userTo));
            System.out.println("Response from clientTo: ");
            System.out.println(respTo);
            BVerifyProtocolClient clientFrom = (BVerifyProtocolClient) this.registry.lookup(userFrom);
            Proof respFrom = clientFrom.proposeTransfer(userTo, userFrom, new DummyProof(userFrom+" -- "+amount+" -- >"+userTo));
            System.out.println("Response from clientFrom: ");
            System.out.println(respFrom);
            
            // publish commitment 
            String stmt = "NEW COMMITMENT! " +System.currentTimeMillis();
            commitmentPublisher.appendStatement(stmt.getBytes());
            return true;
            
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public Proof getBalance(String user, int time) {
		return new DummyProof(user+" balance at time: "+time);
	}

	public Proof getBalances(int time, boolean changedOnly) {
		return new DummyProof("user balances at time: "+time+" changed only: "+changedOnly);
	}

}
