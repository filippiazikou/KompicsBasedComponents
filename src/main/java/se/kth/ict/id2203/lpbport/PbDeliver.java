package se.kth.ict.id2203.lpbport;


import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class PbDeliver extends Event {
	
	private Address source;
	private String msg;
	
	public PbDeliver(Address source, String msg) {
		this.source = source;
		this.msg = msg;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public Address getSource() {
		return source;
	}
	
	public String getMsg() {
		return msg;
	}
}
