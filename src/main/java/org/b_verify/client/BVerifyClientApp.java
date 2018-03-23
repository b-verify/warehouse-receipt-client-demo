package org.b_verify.client;

import java.io.File;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;
import org.b_verify.common.InsufficientFundsException;
import org.b_verify.server.BVerifyServer;
import org.b_verify.server.BVerifyServerApp;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.RegTestParams;
import org.catena.client.CatenaClient;
import org.catena.common.CatenaStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main app is responsible for setting up and starting the client as well as
 * managing the GUI
 * 
 * @author henryaspegren
 *
 */
public class BVerifyClientApp implements Runnable {

	/** Configuration **/

	// client parameters
	private final ECKey clientKey;
	private final Address clientAddress;
	private final String clientName;

	// parameters for configuration commitment
	private final String directory;
	private final NetworkParameters params;
	private final Sha256Hash txid;
	private final Address addr;

	// components
	private Registry registry;
	private BVerifyClient bverifyclient;
	private BVerifyProtocolServer bverifyserver;
	private BVerifyClientGui bverifygui;
	private CatenaClient commitmentReader;
	
	/** Debugging - use this instead of printing to Standard out **/
    private static final Logger log = LoggerFactory.getLogger(BVerifyClientApp.class);

	
	public BVerifyClientApp(String serveraddress, String transactionid, String network, 
			BVerifyClientGui gui) 
			throws IOException, AlreadyBoundException, NotBoundException {
		
		// client info
		params = RegTestParams.get();
		clientKey = new ECKey();
		clientAddress = clientKey.toAddress(params);
		clientName = clientAddress.toBase58();
		directory = "./client-data-"+clientName;
		
		log.info("Setting up b_verify client with address: "+clientAddress);
		
		// server info
		addr = Address.fromBase58(params, serveraddress);
		txid = Sha256Hash.wrap(transactionid);
		
		// conmponents 
		bverifygui = gui;
		commitmentReader = new CatenaClient(params, new File(directory), txid, addr, null);
		// for now the reigstry is just on local host 
		// we will need to change this down the road
		registry = LocateRegistry.getRegistry(null, BVerifyServerApp.RMI_REGISTRY_PORT);
		bverifyserver = (BVerifyProtocolServer) registry.lookup("Server");
		bverifyclient = new BVerifyClient(clientAddress.toBase58(), bverifyserver, bverifygui);

		
		BVerifyProtocolClient clientStub = (BVerifyProtocolClient) UnicastRemoteObject.exportObject(bverifyclient, 0);
		// clients are registered by their pubkeyhash
		registry.bind(clientName, clientStub);
		
		log.info("b_verify client ready");

	}
	
	public boolean startTransfer(String transferTo, int amount) throws RemoteException, InsufficientFundsException {
		return bverifyserver.transfer(clientName, transferTo, amount);
	}
	
	public String getClientName() {
		return clientName;
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
