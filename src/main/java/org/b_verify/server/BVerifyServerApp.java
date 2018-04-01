package org.b_verify.server;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.b_verify.common.BVerifyProtocolServer;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.RegTestParams;
import org.catena.server.CatenaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for setting up the server and managing the server daemon
 * @author henryaspegren
 *
 */
public class BVerifyServerApp {
	
	public static final int RMI_REGISTRY_PORT = 1099;

    private static final Logger log = LoggerFactory.getLogger(BVerifyServerApp.class);

	public static void main(String[] args) {
        try {
        	// configuration -- should be moved to a GUI || config file
        	log.info("Creating a b_verify server with the deault configuration");
        	
			NetworkParameters params = RegTestParams.get();
			String directory = "./server";
			String dumpedPrivKey = "cRSsPikCuEFJv2FySNYGq78oJHJcD3TZDWj1U13wezMBdNAhGZu8";
			ECKey chainkey = DumpedPrivateKey.fromBase58(params, dumpedPrivKey).getKey();
			Address serverAddress = chainkey.toAddress(params);

						
        	CatenaServer catenaServer = new CatenaServer(params, new File(directory), chainkey, null);
        	
        	// start up catena server 
        	catenaServer.connectToLocalHost();
        	catenaServer.startAsync();
        	catenaServer.awaitRunning();
        	
        	// issue root of trust txn and print txid (do this before starting clients)
        	Transaction rot = catenaServer.appendStatement("ROOT OF TRUST TXN".getBytes());        	
        	log.info("network=regtest | "+"addr="+serverAddress.toBase58()+" | txid="+rot.getHashAsString());

        	
        	// create the RMI Registry
        	Registry registry = LocateRegistry.createRegistry(1099);
        	
        	BVerifyServer server = new BVerifyServer(registry, catenaServer);
            BVerifyProtocolServer stub = (BVerifyProtocolServer) UnicastRemoteObject.exportObject(server, 0);
            registry.bind("Server", stub);
            
            log.info("b_verify server READY!");
            catenaServer.awaitTerminated();
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
	}
	
}
