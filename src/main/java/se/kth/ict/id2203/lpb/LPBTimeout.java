package se.kth.ict.id2203.lpb;

import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public final class LPBTimeout extends Timeout{

	private Address source;
	private int sn;
	
	public LPBTimeout(ScheduleTimeout request, Address source, int sn) {
		super(request);
		this.source = source;
		this.sn = sn;
	}

	public Address getSource() {
		return source;
	}

	public int getSn() {
		return sn;
	}

}
