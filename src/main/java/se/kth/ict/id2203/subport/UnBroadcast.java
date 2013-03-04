package se.kth.ict.id2203.subport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class UnBroadcast extends Event {


	private String msg;
	private Address source;
	int sn;
	
	public UnBroadcast(Address source, String msg, int sn) {
		this.source = source;
		this.msg = msg;
		this.sn = sn;
	}

	public int getSn() {
		return sn;
	}
	
	public String getMsg() {
		return msg;
	}

	public Address getSource() {
		return source;
	}
	
}
