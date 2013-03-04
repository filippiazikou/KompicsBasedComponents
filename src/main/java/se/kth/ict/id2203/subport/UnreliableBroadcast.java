package se.kth.ict.id2203.subport;

import se.sics.kompics.PortType;

public final class UnreliableBroadcast extends PortType {
	{
		indication(UnDeliver.class);
		request(UnBroadcast.class);
	}
}
