package se.kth.ict.id2203.pucport;

import se.sics.kompics.PortType;

public final class UniformConsensus extends PortType {
	{
		indication(UcDecide.class);
		request(UcPropose.class);
	}
}
