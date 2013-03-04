package se.kth.ict.id2203.babc;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class DeliverWriteMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private int id;
	private long timestamp;
	private int value;
	
	public DeliverWriteMessage(Address source, int id, long timestamp, int value) {
		super(source);
		this.id = id;
		this.timestamp = timestamp;
		this.value = value;
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
