package se.kth.ict.id2203.beb;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class BEBInit extends Init{

	private final Topology topology;
	
	public BEBInit(Topology topology) {
		this.topology = topology;
	}
	
	public final Topology getTopology() {
		return topology;
	}
}
