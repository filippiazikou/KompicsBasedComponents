package se.kth.ict.id2203.bebport;

import se.sics.kompics.PortType;

public final class BestEffortBroadcast extends PortType {
	{
		indication(BebMessage.class);
		request(BebMessage.class);
	}
}
