package se.kth.ict.id2203.babc;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class DeliverDecidedMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private int id;
	private int value;
	
	public DeliverDecidedMessage(Address source, int id, int value) {
		super(source);
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

	
}
