package org.b_verify.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.b_verify.common.BVerifyCommitment;
import org.catena.client.CatenaClient;
import org.catena.client.CatenaStatementListener;
import org.catena.common.CatenaStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.bverify.Receipt;
import pki.Account;
import pki.PKIDirectory;

import org.json.*;

/**
 * The main app is responsible for setting up and starting the client as well as
 * managing the desktop client GUI.
 * 
 * @author binhle
 */
public class BVerifyClientApp implements Runnable, CatenaStatementListener {

	private final String clientIdString;
	
	// components
	private BVerifyClientDemo bverifyclientdemo;
	private PKIDirectory pki;
	private CatenaClient commitmentReader;
	private final List<BVerifyCommitment> commitments;
	
	/** Debugging - use this instead of printing to Standard out **/
    private static final Logger log = LoggerFactory.getLogger(BVerifyClientApp.class);

	
	public BVerifyClientApp(BVerifyClientDemo bverifyclientdemo, PKIDirectory pki, Account client) {
		this.bverifyclientdemo = bverifyclientdemo;
		this.pki = pki;
		this.commitments = new ArrayList<BVerifyCommitment>();
		clientIdString = client.getIdAsString();
		log.info("Setting up b_verify client with id: "+ clientIdString);
	}
	
	public void initIssueReceipt(JSONObject receiptJSON) {
		// DepositorId set to Alice pubkey for demo purposes
		String depositorId = "df3b507b-31c7-4b07-bea2-4256144c2c41";
		if (receiptJSON.get("depositor").equals("Alice")) {
			depositorId = "df3b507b-31c7-4b07-bea2-4256144c2c41";
		}
		if (receiptJSON.get("depositor").equals("Bob")) {
			depositorId = "e5985074-99c1-4fa6-80bc-dca299b5b12f";
		} 
		Receipt receipt =
				Receipt.newBuilder()
				.setWarehouseId(clientIdString) // Warehouse id set to warehouse pubkey for demo purposes
				.setDepositorId(depositorId)
				.setAccountant(receiptJSON.get("accountant").toString())
				.setCategory(receiptJSON.get("category").toString())
				.setDate(receiptJSON.get("date").toString())
				.setInsurance(receiptJSON.get("insurance").toString())
				.setWeight(Double.parseDouble(receiptJSON.get("weight").toString()))
				.setVolume(Double.parseDouble(receiptJSON.get("volume").toString()))
				.setHumidity(Double.parseDouble(receiptJSON.get("humidity").toString()))
				.setPrice(Double.parseDouble(receiptJSON.get("price").toString()))
				.setDetails(receiptJSON.get("details").toString())
				.build();
		Account recipient = pki.getAccount(depositorId);
		bverifyclientdemo.deposit(receipt, recipient);
	}
	
	public String getClientIdString() {
		return clientIdString;
	}

	@Override
	public void run() {
		commitmentReader.startAsync();
		commitmentReader.awaitRunning();
		Iterator<CatenaStatement> itr = commitmentReader.getCatenaWallet().statementIterator(true);
		// push the existing statements to the client handler
		while (itr.hasNext()) {
			this.onStatementAppended(itr.next());
		}
		// add listener for future statements
		commitmentReader.getCatenaWallet().addStatementListener(this);
	}

	@Override
	public void onStatementAppended(CatenaStatement s) {
		// TODO Auto-generated method stub
		log.info("Commitment Added: "+ s);
		// add BVerify commitment 
		int commitmentNumber = this.commitments.size()+1;
		BVerifyCommitment commitment = new BVerifyCommitment(commitmentNumber, s.getData(), 
				s.getTxHash());
		this.commitments.add(commitment);
		
		// TODO ask server for a proof
		// assume it succeeds for now
		log.debug("Proof succeeded - commitment verified - updating ux");
		//BVerifyClientGui.updateCurrentCommitment(commitment.getCommitmentNumber(), new String(commitment.getCommitmentData()),commitment.getCommitmentTxnHash().toString());		
	}

	@Override
	public void onStatementWithdrawn(CatenaStatement s) {
		// later need to implement this to deal with Reorgs, 
		// for now - ignore
		log.warn("REORG - feature not implemented yet - crashing program");
		System.exit(1);		
	}
}
