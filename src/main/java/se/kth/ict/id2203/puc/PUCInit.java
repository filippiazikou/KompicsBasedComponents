package se.kth.ict.id2203.puc;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class PUCInit extends Init{

	private final Topology topology;
	
	public PUCInit(Topology topology) {
		this.topology = topology;
	}
	
	public final Topology getTopology() {
		return topology;
	}

}
