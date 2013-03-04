package se.kth.ict.id2203.epfd;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class EpfdInit extends Init{

	private final Topology topology;
	private long timeDelay;
	private long delta;
	
	public EpfdInit(Topology topology, long timeDelay, long delta) {
		this.topology = topology;
		this.timeDelay = timeDelay;
		this.delta = delta;
	}
	
	public final Topology getTopology() {
		return topology;
	}
	
	public final long getTimeDelay() {
		return timeDelay;
	}
	
	public final long getDelta() {
		return delta;
	}

}
