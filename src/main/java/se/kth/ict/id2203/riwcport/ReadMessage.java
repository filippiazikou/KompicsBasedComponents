package se.kth.ict.id2203.riwcport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class ReadMessage extends Event{

	private Address source;
	private int register;
	
	public ReadMessage(Address source, int register) {
		super();
		this.source = source;
		this.register = register;
	}
	public Address getSource() {
		return source;
	}
	public int getRegister() {
		return register;
	}
}
