package se.kth.ict.id2203.babcport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class BabWriteMessage extends Event {

	private Address source;
	private int id;
	private long timestamp;
	private int value;
	public BabWriteMessage(Address source, int id, long timestamp,int value) {
		super();
		this.source = source;
		this.id = id;
		this.timestamp = timestamp;
		this.value = value;
	}

	public Address getSource() {
		return source;
	}
	public int getId() {
		return id;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public int getValue() {
		return value;
	}	
}
