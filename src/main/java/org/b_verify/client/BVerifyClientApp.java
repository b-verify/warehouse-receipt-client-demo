package org.b_verify.client;

import java.io.File;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.RegTestParams;
import org.catena.client.CatenaClient;
import org.catena.common.CatenaStatement;

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

	
	public BVerifyClientApp(String serveraddress, String transactionid, String network, 
			BVerifyClientGui gui) 
			throws IOException, AlreadyBoundException, NotBoundException {
		
		
		bverifygui = gui;
		
		// bitcoin commitment reader (catena) setup
		params = RegTestParams.get();
		directory = "./client-data";
		addr = Address.fromBase58(params, serveraddress);
		txid = Sha256Hash.wrap(transactionid);
		commitmentReader = new CatenaClient(params, new File(directory), txid, addr, null);
		
		// client 
		clientKey = new ECKey();
		clientAddress = clientKey.toAddress(params);
		
		// rmi registry (null corresponds to localhost)
		// registry = LocateRegistry.getRegistry(null);

		// b_verify server
		// bverifyserver = (BVerifyProtocolServer) registry.lookup("Server");
		
		
		// b_verify client
		bverifyclient = new BVerifyClient(clientAddress.toBase58(), bverifyserver, gui);

		
		// BVerifyProtocolClient clientStub = (BVerifyProtocolClient) UnicastRemoteObject.exportObject(bverifyclient, 0);
		// registry.bind(clientAddress.toBase58(), clientStub);			

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
