package se.kth.ict.id2203.rwac;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class NackMessage extends Pp2pDeliver{
	private static final long serialVersionUID =  2193713942080123560L;
		
	private int id;
	protected NackMessage(Address source, int id) {
		super(source);
		this.id = id;
	}

	public int getId(){
		return id;
	}
	

}
