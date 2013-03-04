package se.kth.ict.id2203.bab;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class DeliverWriteMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private int register;
	private int id;
	private long timestamp;
	private int rank;
	private int value;
	public DeliverWriteMessage(Address source, int register, int id, long timestamp, int rank, int value) {
		super(source);
		this.register = register;
		this.id = id;
		this.timestamp = timestamp;
		this.rank = rank;
		this.value = value;
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
