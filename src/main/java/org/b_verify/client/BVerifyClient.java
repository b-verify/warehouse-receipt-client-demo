package org.b_verify.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.b_verify.common.BVerifyCommitment;
import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;
import org.b_verify.common.DummyProof;
import org.b_verify.common.Proof;
import org.catena.client.CatenaStatementListener;
import org.catena.common.CatenaStatement;

/**
 * The client is responsible for keeping track of the balances of users by 
 * using the b_verify protocol to ask the server for the requisite proofs. This 
 * class manages the core data structures as well as requests and validates the proofs. 
 * It must be thread-safe since some of these methods can be invoked via Java RMI and 
 * BitcoinJ
 * @author henryaspegren
 *
 */
public class BVerifyClient implements BVerifyProtocolClient, CatenaStatementListener {

	private final List<BVerifyCommitment> commitments;
	private final String clientName;
	private final BVerifyProtocolServer server;
	private final BVerifyClientGui appgui;
	
	public BVerifyClient(String name, BVerifyProtocolServer srvr, BVerifyClientGui gui) {
		appgui = gui;
		commitments = new ArrayList<BVerifyCommitment>();
        clientName = name;
        server = srvr;
	}
	
	public synchronized Proof proposeTransfer(String userTo, String userFrom, Proof proofOfUpdate) throws RemoteException {
		System.out.println("Transfer Request Recieved:");
		System.out.println(proofOfUpdate.toString());
		return new DummyProof(this.clientName+" approves!");
	}

	@Override
	public synchronized void onStatementAppended(CatenaStatement s) {
		// add BVerify commitment 
		int commitmentNumber = this.commitments.size()+1;
		BVerifyCommitment commitment = new BVerifyCommitment(commitmentNumber, s.getData(), 
				s.getTxHash());
		this.commitments.add(commitment);
		
		
		// ask server for a proof
//		try {
//			System.out.println("Asking server to prove commitment: "+commitment.toString());
//			System.out.println(
//					this.server.getBalance(this.clientName, commitmentNumber));
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
		
		// assume it succeeds (for now)
		
		// update the gui
		appgui.updateCurrentCommitment(commitment.getCommitmentNumber(), new String(commitment.getCommitmentData()), 
				commitment.getCommitmentTxnHash().toString());
	}

	@Override
	public synchronized void onStatementWithdrawn(CatenaStatement s) {
		// later need to implement this to deal with Reorgs, 
		// for now - ignore
		System.err.println("REORG - feature not implemented yet");
		System.exit(1);		
	}

}
