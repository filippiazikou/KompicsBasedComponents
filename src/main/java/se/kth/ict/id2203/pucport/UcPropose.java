package se.kth.ict.id2203.pucport;

import se.sics.kompics.Event;

public class UcPropose extends Event{
	private int id;
	private int value;
	public UcPropose(int id, int value) {
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
