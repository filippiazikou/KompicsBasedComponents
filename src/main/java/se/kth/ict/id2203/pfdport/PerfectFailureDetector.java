package se.kth.ict.id2203.pfdport;

import se.sics.kompics.PortType;

public final class PerfectFailureDetector extends PortType {
	{
		indication(PfdCrash.class);
	}
}
