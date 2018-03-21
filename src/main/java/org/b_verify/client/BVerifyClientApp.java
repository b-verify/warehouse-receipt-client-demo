package org.b_verify.client;

import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.RegTestParams;
import org.catena.client.CatenaClient;
import org.catena.client.CatenaStatementListener;
import org.catena.common.CatenaStatement;


/**
 * The main app is responsible for setting up and starting the client as well as
 * managing the GUI
 * 
 * @author henryaspegren
 *
 */
public class BVerifyClientApp implements Runnable {
	
	private final CatenaClient commitmentReader;
	
	public BVerifyClientApp(String address, String transactionid, String network) throws IOException {
		NetworkParameters params = RegTestParams.get();
		String directory = "./client-data";
		Address addr = Address.fromBase58(params, address);
		Sha256Hash txid = Sha256Hash.wrap(transactionid);
		commitmentReader = 
				new CatenaClient(params, new File(directory), txid, addr, null);
		System.out.println("setup b_verify client");
		
	}
	

	public static void main(String[] args) {
		try {

			String name = args[0];
			boolean sendTransfer = Boolean.parseBoolean(args[1]);
			
			// these config parameters should moved into a GUI || config file
			String directory = "./"+name;
			String txidHex = "";
			String addr = "";
			NetworkParameters params = RegTestParams.get();
			Sha256Hash txid;
			Address chainAddr = Address.fromBase58(params, addr);
			txid = Sha256Hash.wrap(txidHex);
			
			// rmi registry (null corresponds to localhost)
			Registry registry = LocateRegistry.getRegistry(null);

			// b_verify server
			BVerifyProtocolServer server = (BVerifyProtocolServer) registry.lookup("Server");
			
			// b_verify client
			BVerifyClient client = new BVerifyClient(name, server);

			// catena client 
			CatenaClient commitmentReader = new CatenaClient(params, new File(directory), txid, chainAddr, null);
			
			// link them together
			commitmentReader.getCatenaWallet().addStatementListener(client);

			BVerifyProtocolClient clientStub = (BVerifyProtocolClient) UnicastRemoteObject.exportObject(client, 0);
			registry.bind(name, clientStub);
			
			// start the commitment reader 
			commitmentReader.startAsync();
			commitmentReader.awaitRunning();
			
			
			if (sendTransfer) {
				String[] serverandclients = registry.list();
				String nameOtherClient = "";
				for(String entryname : serverandclients) {
					if (!entryname.equals(name) && !entryname.equals("Server")){
						nameOtherClient = entryname;
					}
				}
				System.out.println("requesting transfer to: "+nameOtherClient);
				System.out.println("response: " + server.transfer(name, nameOtherClient, 100));
			}

		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		System.out.println("--------Starting client-----------");
		
		commitmentReader.startAsync();
		commitmentReader.awaitRunning();
		System.out.println("--------Adding Statement Listener-----------");

		Iterator<CatenaStatement> itr = commitmentReader.getCatenaWallet().statementIterator(true);
		while(itr.hasNext()) {
			System.out.println("stmt: "+itr.next().toString());
		}
		
		commitmentReader.getCatenaWallet().addStatementListener(new CatenaStatementListener() {

			@Override
			public void onStatementAppended(CatenaStatement s) {
				System.out.println("---------NEW STATEMENT: --------");
				System.out.println(s);
			}

			@Override
			public void onStatementWithdrawn(CatenaStatement s) {
			}
			
		});
		System.out.println("--------Shutting Down-----------");

		commitmentReader.stopAsync();
	
		commitmentReader.awaitTerminated();
		System.out.println("--------Terminated-----------");

		
	}
}
