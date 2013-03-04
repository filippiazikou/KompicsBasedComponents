package se.kth.ict.id2203.babport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;



public class BabReadMessage extends Event {


	private Address source;
	private int register;
	private int id;
	
	public BabReadMessage(Address source,int register,  int id) {
		super();
		this.source = source;
		this.register = register;
		this.id = id;
	}
	public Address getSource() {
		return source;
	}
	public int getId() {
		return id;
	}
	
	public int getRegister() {
		return register;
	}
	
	
}
