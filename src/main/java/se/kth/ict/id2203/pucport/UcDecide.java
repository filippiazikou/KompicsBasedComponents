package se.kth.ict.id2203.pucport;

import se.sics.kompics.Event;

public class UcDecide extends Event{
	private int id;
	private int value;
	public UcDecide(int id, int value) {
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
