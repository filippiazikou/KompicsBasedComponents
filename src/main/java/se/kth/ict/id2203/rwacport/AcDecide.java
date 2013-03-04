package se.kth.ict.id2203.rwacport;

import se.sics.kompics.Event;

public class AcDecide extends Event{
	private int id;
	private int value;
	public AcDecide(int id, int value) {
		super();
		this.id = id;
		this.value = value;
	}
	public int getId() {
		return id;
	}
	public int getValue() {
		return value;
	}
	
}
