package se.kth.ict.id2203.rwac;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class WriteAck extends Pp2pDeliver {
	private static final long serialVersionUID =  2193713942080123560L;
	
	private int id;
	private long ts;
	public WriteAck(Address source, int id, long ts) {
		super(source);
		this.id = id;
		this.ts = ts;
	}
	public int getId() {
		return id;
	}
	public long getTs() {
		return ts;
	}
	
}
