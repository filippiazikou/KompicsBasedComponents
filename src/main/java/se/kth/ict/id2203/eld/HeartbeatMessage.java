/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.eld;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

public class HeartbeatMessage extends Pp2pDeliver {

	private static final long serialVersionUID = 2193713942080123560L;
	
	private final String message;

	protected HeartbeatMessage(Address source, String message) {
		super(source);
		this.message = message;
	}
	public final String getMessage() {
		return message;
	}
}
