package se.kth.ict.id2203.sub;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class SUBInit extends Init{

	private final Topology topology;
	
	public SUBInit(Topology topology) {
		this.topology = topology;
	}
	
	public final Topology getTopology() {
		return topology;
	}
}
