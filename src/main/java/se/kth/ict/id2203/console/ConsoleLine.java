package se.kth.ict.id2203.console;

import se.sics.kompics.Event;

public class ConsoleLine extends Event {
	private final String line;

	public ConsoleLine(String line) {
		this.line = line;
	}
	
	public final String getLine() {
		return line;
	}
}
