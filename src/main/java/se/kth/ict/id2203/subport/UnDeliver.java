package se.kth.ict.id2203.subport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class UnDeliver extends Event{

	private Address source;
	private String msg;
	private int sn;
	
	public UnDeliver(Address source, String msg, int sn) {
		this.source = source;
		this.msg = msg;
		this.sn = sn;
	}


	public String getMsg() {
		return msg;
	}

	public int getSn() {
		return sn;
	}

	public Address getSource(){
		return source;
	}
	

}
