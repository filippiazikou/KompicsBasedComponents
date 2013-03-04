/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.pfd;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * The <code>CheckTimeout</code> class.
 * 
 * @author Zikou Filippia
 */
public final class HeartbeatTimeout extends Timeout {

	/**
	 * Instantiates a new application continue.
	 * 
	 * @param request
	 *            the request
	 */
	public HeartbeatTimeout(ScheduleTimeout request) {
		super(request);
	}
}
