package se.kth.ict.id2203.riwcm;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class AckMessage extends Pp2pDeliver{

	private static final long serialVersionUID = 2193713942080123560L;
	
	private int register;
	private int id;
	
	protected AckMessage(Address source, int register, int id) {
		super(source);
		this.register = register;
		this.id = id;
	}


	public int getReg() {
		return register;
	}

	public int getId() {
		return id;
	}

	
	

}
