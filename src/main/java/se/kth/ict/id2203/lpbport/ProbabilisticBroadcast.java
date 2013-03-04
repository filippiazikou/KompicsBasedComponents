package se.kth.ict.id2203.lpbport;

import se.kth.ict.id2203.epfdport.EpfdRestore;
import se.sics.kompics.PortType;

public final class ProbabilisticBroadcast extends PortType{
	{
		indication(PbDeliver.class);
		request(PbBroadcast.class);
	}
}
