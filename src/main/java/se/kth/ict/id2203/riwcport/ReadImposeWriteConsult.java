package se.kth.ict.id2203.riwcport;

import se.sics.kompics.PortType;

public final class ReadImposeWriteConsult extends PortType {
	{
		request(ReadMessage.class);
		request(WriteMessage.class);
		indication(ReadMessageReturn.class);
		indication(WriteMessageReturn.class);
	}
}
