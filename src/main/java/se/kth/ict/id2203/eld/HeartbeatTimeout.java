/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.eld;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public final class HeartbeatTimeout extends Timeout {

	public HeartbeatTimeout(ScheduleTimeout request) {
		super(request);
	}
}
