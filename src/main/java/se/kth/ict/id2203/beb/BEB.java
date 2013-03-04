package se.kth.ict.id2203.beb;


import se.kth.ict.id2203.pp2p.*;
import se.kth.ict.id2203.bebport.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

public class BEB extends ComponentDefinition {
	Negative<BestEffortBroadcast> beb = provides(BestEffortBroadcast.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	
	private Topology topology;
	
	public BEB() {
		subscribe(handleInit, control);
		subscribe(handleBebBroadcast, beb);
		subscribe(handleDeliver, pp2p);
	}
	
	Handler<BEBInit> handleInit = new Handler<BEBInit>() {
		public void handle(BEBInit event) {
			topology = event.getTopology();
		}
	};
	
	Handler<BebMessage> handleBebBroadcast = new Handler<BebMessage>() {
		public void handle(BebMessage event) {
			Address s = event.getSource();
			int id = event.getId();
			long ts = event.getTimestamp();
			int rank = event.getRank();
			int val = event.getValue();
			int reg = event.getRegister();
			//for (Address neighbor : topology.getNeighbors(s)) {
			for (Address neighbor : topology.getAllAddresses()) {
				//System.out.println("triggering FlSend at"+neighbor);
				trigger(new Pp2pSend(neighbor, new DeliverMessage(s, reg, id, ts, rank, val)), pp2p);
			}
		}
	};
	
	Handler<DeliverMessage> handleDeliver = new Handler<DeliverMessage>() {
		public void handle(DeliverMessage event) {
			Address source = event.getSource();
			int id = event.getId();
			long ts = event.getTimestamp();
			int rank = event.getRank();
			int val = event.getValue();
			int reg = event.getRegister();
			//System.out.println("DataMessage triggering UnDeliver");
			BebMessage deliverEvent = new BebMessage(source, reg, id, ts, rank, val);
			trigger(deliverEvent, beb);
		}
	};
}
