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

import org.b_verify.common.BVerifyProtocolClientAPI;
import org.b_verify.common.BVerifyProtocolServerAPI;
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
 * managing the warehouse GUI
 * 
 * @author binhle
 */
public class BVerifyWarehouseApp implements Runnable {

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
	private BVerifyWarehouse bverifywarehouse;
	private BVerifyProtocolServerAPI bverifyserver;
	private BVerifyWarehouseGui bverifygui;
	private CatenaClient commitmentReader;
	
	/** Debugging - use this instead of printing to Standard out **/
    private static final Logger log = LoggerFactory.getLogger(BVerifyClientApp.class);

	
	public BVerifyWarehouseApp(String serveraddress, String transactionid, String network, 
			BVerifyWarehouseGui gui) 
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
		// for now the registry is just on local host 
		// we will need to change this down the road
		registry = LocateRegistry.getRegistry(null, BVerifyServerApp.RMI_REGISTRY_PORT);
		bverifyserver = (BVerifyProtocolServerAPI) registry.lookup("Server");
		bverifywarehouse = new BVerifyWarehouse(clientAddress.toBase58(), bverifyserver, bverifygui);

		
		BVerifyProtocolClientAPI clientStub = (BVerifyProtocolClientAPI) UnicastRemoteObject.exportObject(bverifywarehouse, 0);
		// clients are registered by their pubkeyhash
		registry.bind(clientName, clientStub);
		
		log.info("b_verify client ready");
	}
	
	public void initIssueReceipt(byte[] requestIssueMessage) throws RemoteException {
		bverifyserver.startIssueReceipt(requestIssueMessage);
	}
	
	public void initRedeemReceipt(byte[] requestIssueMessage) throws RemoteException {
		bverifyserver.startRedeemReceipt(requestIssueMessage);
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
			bverifywarehouse.onStatementAppended(itr.next());
		}
		// add listener for future statements
		commitmentReader.getCatenaWallet().addStatementListener(bverifywarehouse);

	}
}
