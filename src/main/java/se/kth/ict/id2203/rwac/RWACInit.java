package se.kth.ict.id2203.rwac;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class RWACInit extends Init{

	private final Topology topology;
	
	public RWACInit(Topology topology) {
		this.topology = topology;
	}
	
	public final Topology getTopology() {
		return topology;
	}

}
