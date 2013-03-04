package se.kth.ict.id2203.rwac;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class ReadAck extends Pp2pDeliver{
	private static final long serialVersionUID =  2193713942080123560L;
		
	private int id;
	private long wts;
	private int val;
	private long ts;
	
	protected ReadAck(Address source, int id, long wts, int val, long ts) {
		super(source);
		this.id = id;
		this.wts = wts;
		this.val = val;
		this.ts = ts;
		
	}

	public int getId(){
		return id;
	}


	public long getWts() {
		return wts;
	}

	public int getVal() {
		return val;
	}

	public long getTs() {
		return ts;
	}
	

}
