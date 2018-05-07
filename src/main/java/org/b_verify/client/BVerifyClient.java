package org.b_verify.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.b_verify.common.BVerifyCommitment;
import org.b_verify.common.BVerifyProtocolClientAPI;
import org.b_verify.common.BVerifyProtocolServerAPI;
import org.catena.client.CatenaStatementListener;
import org.catena.common.CatenaStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client is responsible for keeping track of user receipts by using the b_verify 
 * protocol to ask the server for the requisite proofs. This class manages the core data 
 * structures as well as requests and validates the proofs. It must be thread-safe since 
 * some of these methods can be invoked via Java RMI and BitcoinJ.
 * 
 * @author binhle
 */
public class BVerifyClient implements BVerifyProtocolClientAPI, CatenaStatementListener {

	private final List<BVerifyCommitment> commitments;
	private final String clientName;
	private final BVerifyProtocolServerAPI server;
	private final BVerifyClientGui appgui;
	
	/** Debugging - use this instead of printing to Standard out **/
    private static final Logger log = LoggerFactory.getLogger(BVerifyClient.class);

	public BVerifyClient(String name, BVerifyProtocolServerAPI srvr, BVerifyClientGui gui) {
		appgui = gui;
		commitments = new ArrayList<BVerifyCommitment>();
        clientName = name;
        server = srvr;
	}

	@Override
	public synchronized void onStatementAppended(CatenaStatement s) {
		log.info("Commitment Added: "+s);
		// add BVerify commitment 
		int commitmentNumber = this.commitments.size()+1;
		BVerifyCommitment commitment = new BVerifyCommitment(commitmentNumber, s.getData(), 
				s.getTxHash());
		this.commitments.add(commitment);
		
		
		// ask server for a proof
//		try {
//			log.debug("Asking server to prove commitment by making a getBalanceRequest");
//			Proof response = this.server.getUpdates(updateRequest);
//			log.debug("Response: "+response);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
		
		// assume it succeeds (for now)
		log.debug("Proof succeeded - commitment verified - updating ux");
		// update the gui
		appgui.updateCurrentCommitment(commitment.getCommitmentNumber(), new String(commitment.getCommitmentData()),commitment.getCommitmentTxnHash().toString());		
	}

	@Override
	public synchronized void onStatementWithdrawn(CatenaStatement s) {
		// later need to implement this to deal with Reorgs, 
		// for now - ignore
		log.warn("REORG - feature not implemented yet - crashing program");
		System.exit(1);		
	}

	@Override
	public byte[] approveReceiptIssue(byte[] approveIssueMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] approveReceiptRedeem(byte[] approveRedeemMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] approveReceiptTransfer(byte[] approveTransferMessage) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public byte[] approveDeposit(byte[] request) throws RemoteException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void addNewCommitment(byte[] commitment) throws RemoteException {
//		// TODO Auto-generated method stub
//		
//	}
}
