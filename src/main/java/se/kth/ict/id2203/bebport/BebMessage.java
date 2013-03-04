package se.kth.ict.id2203.bebport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class BebMessage extends Event {


	private Address source;
	private int register;
	private int id;
	private long timestamp;
	private int rank;
	private int value;
	public BebMessage(Address source,int register,  int id, long timestamp, int rank, int value) {
		super();
		this.source = source;
		this.register = register;
		this.id = id;
		this.timestamp = timestamp;
		this.rank = rank;
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
	public int getRank() {
		return rank;
	}
	public int getValue() {
		return value;
	}
	public int getRegister() {
		return register;
	}
	
	
}
