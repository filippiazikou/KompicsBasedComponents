package se.kth.ict.id2203.eldport;

import se.sics.kompics.Event;
import se.sics.kompics.address.Address;

public class Trust extends Event {

	private Address leader;
	
	public Trust(Address leader) {
		this.leader = leader;
	}

	public Address getLeader() {
		return leader;
	}
	
	
	
}