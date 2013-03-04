/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.epfd;

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

/**
 * The <code>HeartbeatMessage</code> class.
 * 
 * @author Zikou Filippia
 */

public class HeartbeatMessage extends Pp2pDeliver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2193713942080123560L;
	
	private final String message;

	/**
	 * Instantiates a new pp2p message.
	 * 
	 * @param source
	 *            the source
	 * @param message
	 *            the message
	 */
	protected HeartbeatMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}
}
