package se.kth.ict.id2203.bab;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class DeliverReadMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private int register;
	private int id;

	public DeliverReadMessage(Address source, int register, int id) {
		super(source);
		this.register = register;
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public int getRegister() {
		return register;
	}
	
}
