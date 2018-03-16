package org.b_verify.common;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * b_verify API provided by the client. The main purpose of the client API is 
 * to allow other clients or the server to coordinate transfers (or more 
 * generally updates to data stored on the server). 
 * 
 * @author henryaspegren
 *
 */
public interface BVerifyProtocolClient extends Remote {
	
	
	/**
	 * This method is called remotely whenever the server or 
	 * another client wishes to propose a transfer. The client 
	 * should check the proof and, if acceptable, counter sign 
	 * 
	 * @param userTo - the user receiving amount
	 * @param userFrom - the user sending the amount 
	 * @param proofOfUpdate - proof of the updated balances
	 * @return a proof containing the required signatures for the client's
	 * approval
	 * @throws RemoteException
	 */
	public Proof proposeTransfer(String userTo, String userFrom, 
			Proof proofOfUpdate) throws RemoteException;
	
}
