package se.kth.ict.id2203.pp2p;

import se.sics.kompics.PortType;

public final class PerfectPointToPointLink extends PortType {
	{
		indication(Pp2pDeliver.class);
		request(Pp2pSend.class);
	}
}
