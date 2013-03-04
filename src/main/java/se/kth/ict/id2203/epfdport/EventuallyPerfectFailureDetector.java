package se.kth.ict.id2203.epfdport;

import se.sics.kompics.PortType;

public final class EventuallyPerfectFailureDetector extends PortType {
	{
		indication(EpfdRestore.class);
		indication(EpfdSuspect.class);
	}
}