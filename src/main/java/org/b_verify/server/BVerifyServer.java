package org.b_verify.server;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.List;

import org.b_verify.common.BVerifyProtocolClientAPI;
import org.b_verify.common.BVerifyProtocolServerAPI;
import org.b_verify.common.DummyProof;
import org.b_verify.common.InsufficientFundsException;
import org.b_verify.common.Proof;
import org.catena.common.CatenaUtils;
import org.catena.server.CatenaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for managing server data structures, broadcasting commitments 
 * and constructing proofs for servers. Must be threadsafe.
 * 
 * @author henryaspegren
 */
public class BVerifyServer implements BVerifyProtocolServerAPI {
	
	private Registry registry;
	private CatenaServer commitmentPublisher;

	/** Debugging - use this instead of printing to Standard out **/
    private static final Logger log = LoggerFactory.getLogger(BVerifyServer.class);
	
	public BVerifyServer(Registry registry, CatenaServer publisher) {
		this.commitmentPublisher = publisher;
		this.registry = registry;
	}

	public void startIssueReceipt(byte[] requestIssueMessage) {
		// TODO Auto-generated method stub
		
	}

	public void startRedeemReceipt(byte[] requestRedeemMessage) {
		// TODO Auto-generated method stub
		
	}

	public void startTransferReceipt(byte[] requestTransferMessage) {
		// TODO Auto-generated method stub
		
	}

	public byte[] getUpdates(byte[] updateRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
