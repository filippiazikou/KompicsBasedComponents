package se.kth.ict.id2203.pp2p;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public final class Pp2pSend extends Event {

	private final Pp2pDeliver deliverEvent;
	
	private final Address destination;
	
	public Pp2pSend(Address destination, Pp2pDeliver pp2pDeliver) {
		this.destination = destination;
		this.deliverEvent = pp2pDeliver;
	}
	
	public final Pp2pDeliver getDeliverEvent() {
		return deliverEvent;
	}
	
	public final Address getDestination() {
		return destination;
	}
}
