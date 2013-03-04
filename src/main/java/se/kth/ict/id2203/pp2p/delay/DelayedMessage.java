package se.kth.ict.id2203.pp2p.delay;

import se.sics.kompics.network.Message;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;



public final class DelayedMessage extends Timeout {

	private final Message message;

	public DelayedMessage(ScheduleTimeout request, Message message) {
		super(request);
		this.message = message;
	}

	public final Message getMessage() {
		return message;
	}
}
