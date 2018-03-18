package org.b_verify.client;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.params.RegTestParams;
import org.catena.client.CatenaClient;

/**
 * The main app is responsible for setting up and starting the client as well as
 * managing the GUI
 * 
 * @author henryaspegren
 *
 */
public class BVerifyClientApp {
	

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
}
