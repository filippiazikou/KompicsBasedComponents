package se.kth.ict.id2203.lpb;

import se.kth.ict.id2203.flp2p.Flp2pDeliver;
import se.sics.kompics.address.Address;

public class DataMessage extends Flp2pDeliver{

	private static final long serialVersionUID = 2193713942080123560L;
	
	private Address source;
	private String msg;
	private int sn;
	
	protected DataMessage(Address source, String msg, int sn) {
		super(source);
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

	
	

}
