package org.b_verify.common;

import org.bitcoinj.core.Sha256Hash;

/**
 * Minimal interface that b_verify commitments must provide. 
 * @author henryaspegren
 *
 */
public class BVerifyCommitment {
	
	private final int commitmentNubmer;
	
	private final byte[] data;
	
	private final Sha256Hash txnHash;
	
	public BVerifyCommitment(int num, byte[] cmtData, Sha256Hash txnHash) {
		this.commitmentNubmer = num;
		this.data = cmtData;
		this.txnHash = txnHash;
	}
	
	/**
	 * Returns the commitment number. Commitment 0 is the 
	 * root-of-trust. 
	 * @return
	 */
	public int getCommitmentNumber() {
		return this.commitmentNubmer;
	}
	
	
	/**
	 * The commitment data - C_t
	 * @return
	 */
	public byte[] getCommitmentData() {
		return this.data;
	}
	
	
	/**
	 * The commitment txn hash - B_t
	 * @return
	 */
	public Sha256Hash getCommitmentTxnHash() {
		return this.txnHash;
	}
	
	@Override
	public String toString() {
		return "BVerifyCommitment number: "+this.commitmentNubmer+" | data: "+this.data.toString()+
				" | hash "+this.txnHash.toString();
	}
	
}
