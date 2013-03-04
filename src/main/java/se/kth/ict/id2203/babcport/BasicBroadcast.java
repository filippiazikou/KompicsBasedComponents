package se.kth.ict.id2203.babcport;

import se.sics.kompics.PortType;

public final class BasicBroadcast extends PortType {
	{
		indication(BabWriteMessage.class);
		indication(BabReadMessage.class);
		indication(BabDecidedMessage.class);
		request(BabWriteMessage.class);
		request(BabReadMessage.class);
		request(BabDecidedMessage.class);
	}
}
