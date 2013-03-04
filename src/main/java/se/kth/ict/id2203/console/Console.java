package se.kth.ict.id2203.console;

import se.sics.kompics.PortType;

public class Console extends PortType {
	{
		indication(ConsoleLine.class);
		request(ConsoleLine.class);
	}
}
