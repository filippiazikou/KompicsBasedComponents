/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.epfd;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * The <code>CheckTimeout</code> class.
 * 
 * @author Zikou Filippia
 */
public final class CheckTimeout extends Timeout {

	/**
	 * Instantiates a new application continue.
	 * 
	 * @param request
	 *            the request
	 */
	public CheckTimeout(ScheduleTimeout request) {
		super(request);
	}
}
