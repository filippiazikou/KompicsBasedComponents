package se.kth.ict.id2203.riwc;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class RIWCInit extends Init{

	private final Topology topology;
	private int regNum;
	public RIWCInit(Topology topology, int regNum) {
		this.topology = topology;
		this.regNum = regNum;
	}
	
	public final Topology getTopology() {
		return topology;
	}
	
	public int getRegNum() {
		return regNum;
	}
}