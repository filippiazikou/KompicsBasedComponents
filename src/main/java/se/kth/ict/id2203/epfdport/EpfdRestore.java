package se.kth.ict.id2203.epfdport;



import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class EpfdRestore extends Event {

	

	private Address source;
	
	public EpfdRestore(Address source) {
		this.source = source;
	}
	
	public final Address getSource() {
		return source;
	}
}
