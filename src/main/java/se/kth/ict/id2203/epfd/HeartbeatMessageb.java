/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.epfd;

import se.kth.ict.id2203.flp2p.Flp2pDeliver;
import se.sics.kompics.address.Address;

/**
 * The <code>HeartbeatMessage</code> class.
 * 
 * @author Zikou Filippia
 */

public class HeartbeatMessageb extends Flp2pDeliver {

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
	protected HeartbeatMessageb(Address source, String message) {
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
