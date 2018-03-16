package org.b_verify.client;

import java.rmi.RemoteException;

import org.b_verify.common.BVerifyProtocolClient;
import org.b_verify.common.DummyProof;
import org.b_verify.common.Proof;

public class BVerifyClient implements BVerifyProtocolClient {

	private final String clientName;
	
	public BVerifyClient(String name) {
		this.clientName = name;
	}
	
	public Proof proposeTransfer(String userTo, String userFrom, Proof proofOfUpdate) throws RemoteException {
		System.out.println("Transfer Request Recieved:");
		System.out.println(proofOfUpdate.toString());
		return new DummyProof(this.clientName+" approves!");
	}

}
