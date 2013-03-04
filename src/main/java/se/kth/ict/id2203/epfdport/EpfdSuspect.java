package se.kth.ict.id2203.epfdport;



import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class EpfdSuspect extends Event {

	

	private Address source;
	
	public EpfdSuspect(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
