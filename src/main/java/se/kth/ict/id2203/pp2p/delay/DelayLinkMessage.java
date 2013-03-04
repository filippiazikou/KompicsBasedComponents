package se.kth.ict.id2203.pp2p.delay;
import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;
import se.sics.kompics.network.Transport;


public final class DelayLinkMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8044668011408046391L;
	
	private final Pp2pDeliver deliverEvent;

	public DelayLinkMessage(Address source, Address destination,
			Pp2pDeliver deliverEvent) {
		super(source, destination, Transport.TCP);
		this.deliverEvent = deliverEvent;
	}

	public final Pp2pDeliver getDeliverEvent() {
		return deliverEvent;
	}
}
