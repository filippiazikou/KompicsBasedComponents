package se.kth.ict.id2203.pp2p.delay;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;


public final class DelayLinkInit extends Init {

	private final Topology topology;
	
	public DelayLinkInit(Topology topology) {
		this.topology = topology;
	}
	
	public final Topology getTopology() {
		return topology;
	}
}
