package se.kth.ict.id2203.babcport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class BabDecidedMessage extends Event {

	private Address source;
	private int id;
	private int value;
	public BabDecidedMessage(Address source, int id,int value) {
		super();
		this.source = source;
		this.id = id;
		this.value = value;
	}

	public Address getSource() {
		return source;
	}
	public int getId() {
		return id;
	}
	public int getValue() {
		return value;
	}	
}
