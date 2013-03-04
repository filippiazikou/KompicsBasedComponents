package se.kth.ict.id2203.eld;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class ELDInit extends Init{

	private final Topology topology;
	private long timeDelay;
	private long delta;
	
	public ELDInit(Topology topology, long timeDelay, long delta) {
		this.topology = topology;
		this.timeDelay = timeDelay;
		this.delta = delta;
	}
	
	public final Topology getTopology() {
		return topology;
	}

	public long getTimeDelay() {
		return timeDelay;
	}

	public long getDelta() {
		return delta;
	}

}
