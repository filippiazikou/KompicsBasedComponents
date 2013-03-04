package se.kth.ict.id2203.pfd;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class PfdInit extends Init{

	private final Topology topology;
	private long gama;
	private long delta;
	
	public PfdInit(Topology topology, long gama, long delta) {
		this.topology = topology;
		this.gama = gama;
		this.delta = delta;
	}
	
	public final Topology getTopology() {
		return topology;
	}
	
	public final long getGama() {
		return gama;
	}
	
	public final long getDelta() {
		return delta;
	}

}
