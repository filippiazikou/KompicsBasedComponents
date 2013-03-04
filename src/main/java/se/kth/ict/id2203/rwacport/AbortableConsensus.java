package se.kth.ict.id2203.rwacport;

import se.sics.kompics.PortType;

public final class AbortableConsensus extends PortType {
	{
		indication(AcDecide.class);
		request(AcPropose.class);
	}
}
