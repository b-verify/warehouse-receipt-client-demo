package org.b_verify.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.BVerifyProtocolServer;


public class BVerifyClientApp {
	
	
	public static void main(String[] args) {

        try {
        	
            String name1 = args[0];
           
            String name2 = args[1];
            
            int sendInt = Integer.parseInt(args[2]);
               	
        	BVerifyClient client = new BVerifyClient(name1);
        	BVerifyProtocolClient clientStub = (BVerifyProtocolClient) UnicastRemoteObject.exportObject(client, 0);
            Registry registry = LocateRegistry.getRegistry(null);
            registry.bind(name1, clientStub);
            
            BVerifyProtocolServer server = (BVerifyProtocolServer) registry.lookup("Server");
            
            System.out.println("response: " + server.getBalance(name1, 10));
           
            if(sendInt == 1){
            	System.out.println("response: "+
            			server.transfer(name1, name2, 100));
            }
            
        }  
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
