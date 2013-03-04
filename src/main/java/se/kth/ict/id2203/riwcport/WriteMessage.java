package se.kth.ict.id2203.riwcport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class WriteMessage extends Event{

	private Address source;
	private int value;
	private int register;
	public WriteMessage(Address source,int register, int value) {
		super();
		this.source = source;
		this.register = register;
		this.value = value;
	}
	public Address getSource() {
		return source;
	}
	public int getValue() {
		return value;
	}
	public int getRegister() {
		return register;
	}
}
