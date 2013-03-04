package se.kth.ict.id2203.babport;

import se.sics.kompics.PortType;

public final class BasicBroadcast extends PortType {
	{
		indication(BabWriteMessage.class);
		indication(BabReadMessage.class);
		request(BabWriteMessage.class);
		request(BabReadMessage.class);
	}
}
