package org.b_verify.client;

import java.util.Iterator;

import org.catena.client.CatenaClient;
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
public class BVerifyClientApp implements Runnable {

	private final String clientIdString;
	
	// components
	private BVerifyClient bverifyclient;
	private BVerifyClientDemo bverifyclientdemo;
	private PKIDirectory pki;
	private CatenaClient commitmentReader;
	
	/** Debugging - use this instead of printing to Standard out **/
    private static final Logger log = LoggerFactory.getLogger(BVerifyClientApp.class);

	
	public BVerifyClientApp(BVerifyClientDemo bverifyclientdemo, PKIDirectory pki, Account client) {
		this.bverifyclientdemo = bverifyclientdemo;
		clientIdString = client.getIdAsString();
		log.info("Setting up b_verify client with id: "+ clientIdString);
	}
	
	public void initIssueReceipt(JSONObject receiptJSON) {
		Receipt receipt =
				Receipt.newBuilder()
				.setAccountant(receiptJSON.get("accountant").toString())
				.setDepositorId(receiptJSON.get("depositor").toString())
				.setCategory(receiptJSON.get("category").toString())
				.setDate(receiptJSON.get("date").toString())
				.setInsurance(receiptJSON.get("insurance").toString())
				.setWeight((double)receiptJSON.get("weight"))
				.setVolume((double)receiptJSON.get("volume"))
				.setHumidity((double)receiptJSON.get("humidity"))
				.setPrice((double)receiptJSON.get("price"))
				.setDetails(receiptJSON.get("details").toString())
				.build();
		Account depositor = pki.getAccount(receiptJSON.get("depositor").toString());
		bverifyclientdemo.deposit(receipt, depositor);
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
			bverifyclient.onStatementAppended(itr.next());
		}
		// add listener for future statements
		commitmentReader.getCatenaWallet().addStatementListener(bverifyclient);

	}
}
