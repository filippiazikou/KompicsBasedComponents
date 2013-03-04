package se.kth.ict.id2203.babc;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class BABInit extends Init{

	private final Topology topology;
	
	public BABInit(Topology topology) {
		this.topology = topology;
	}
	
	public final Topology getTopology() {
		return topology;
	}
}
