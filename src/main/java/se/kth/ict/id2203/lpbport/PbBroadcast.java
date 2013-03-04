package se.kth.ict.id2203.lpbport;

import se.sics.kompics.Event;



public class PbBroadcast extends Event {


	private String msg;
	
	public PbBroadcast( String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
	
}
