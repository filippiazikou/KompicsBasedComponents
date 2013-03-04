package se.kth.ict.id2203.pfdport;



import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class PfdCrash extends Event {

	

	private Address source;
	
	public PfdCrash(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
