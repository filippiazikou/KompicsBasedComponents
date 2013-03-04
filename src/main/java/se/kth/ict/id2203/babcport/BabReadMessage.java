package se.kth.ict.id2203.babcport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class BabReadMessage extends Event {

	private long timestamp;
	private int id;
	private Address source;
	
	public BabReadMessage(Address source, long timestamp,  int id) {
		super();
		this.source = source;
		this.timestamp = timestamp;
		this.id = id;
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
	
	
}
