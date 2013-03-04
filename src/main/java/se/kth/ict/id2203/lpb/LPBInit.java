package se.kth.ict.id2203.lpb;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

public final class LPBInit extends Init {
	
	private final Topology topology;
	private int fanout;
	private int rMax;
	private long delta;
	private double alpha;
	
	public LPBInit(Topology topology, int fanout, int rounds, long delta, double alpha) {
		this.topology = topology;
		this.fanout = fanout;
		this.rMax = rounds;
		this.delta=delta;
		this.alpha = alpha;
		
	}

	public Topology getTopology() {
		return topology;
	}
	
	public int getFanout() {
		return fanout;
	}

	public int getR() {
		return rMax;
	}

	public long getDelta() {
		return delta;
	}
	
	public double getAlpha() {
		return alpha;
	}
}
