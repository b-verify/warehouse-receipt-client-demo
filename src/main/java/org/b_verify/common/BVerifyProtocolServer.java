package org.b_verify.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * b_verify API provided by the server. Using Java RMI, a client can connect
 * to a server using the b_verify protocol by looking up "Server" on the 
 * RMI registry. The client can then invoke these methods remotely on the server.
 * Each method returns a Proof which the client should check before accepting the result
 * 
 * @author henryaspegren
 *
 */
public interface BVerifyProtocolServer extends Remote {
	
	/**
	 * Initiate transfer of ownership permissions from one user to another. 
	 * This triggers the server to update the balances of the required users
	 * and use the client RPC to request signatures from all parties involved
	 * 
	 * @param userTo - the user receiving the amount 
	 * @param userFrom - the user sending the amount 
	 * @param amount - the amount to send 
	 * @return true is initiated and false otherwise
	 * @throws InsufficientFundsException - if the client's balance is too low 
	 * @throws RemoteException - if problem with RMI
	 */
	public boolean transfer(String userTo, String userFrom, int amount) throws InsufficientFundsException, 
		RemoteException;
	
	/**
	 * Request a proof of the user's balance at a specific time 
	 * @param user - the user
	 * @param time - the commitment number 
	 * @return a proof, should be evaluated by the client to confirm the result
	 * @throws RemoteException
	 */
	public Proof getBalance(String user, int time) throws RemoteException;
	
	/**
	 * Request a proof of all user's balances at a specific time
	 * @param time - the commitment number 
	 * @param changedOnly - if true returns only the changed balances 
	 * from the last commitment 
	 * @return a proof, should be evaulated by the client to confirm the result
	 * @throws RemoteException
	 */
	public Proof getBalances(int time, boolean changedOnly) throws RemoteException;
	
}
