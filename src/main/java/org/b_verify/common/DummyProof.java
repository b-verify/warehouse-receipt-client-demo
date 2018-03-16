package org.b_verify.common;

public class DummyProof implements Proof {

	private static final long serialVersionUID = 1L;
	
	private final String msg;
	
	public DummyProof(String msg) {
		this.msg = msg;
	}
	
	public String toString() {
		return this.msg;
	}
}
