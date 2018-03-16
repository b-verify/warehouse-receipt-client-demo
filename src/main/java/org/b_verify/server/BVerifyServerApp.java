package org.b_verify.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.b_verify.common.BVerifyProtocolServer;

public class BVerifyServerApp {
	public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry();
        	
        	BVerifyServer server = new BVerifyServer(registry);
            BVerifyProtocolServer stub = (BVerifyProtocolServer) UnicastRemoteObject.exportObject(server, 0);

            registry.bind("Server", stub);
            System.err.println("BVerfiyProtocol Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
	}
}
