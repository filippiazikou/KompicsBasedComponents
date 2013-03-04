package se.kth.ict.id2203.eldport;

import se.sics.kompics.PortType;

public final class EventualLeaderDetector extends PortType {
	{
		indication(Trust.class);
	}
}
