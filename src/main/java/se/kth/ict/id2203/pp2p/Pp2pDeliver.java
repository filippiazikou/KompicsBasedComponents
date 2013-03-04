package se.kth.ict.id2203.pp2p;


import java.io.Serializable;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public abstract class Pp2pDeliver extends Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1565611742901069512L;

	private Address source;
	
	protected Pp2pDeliver(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
