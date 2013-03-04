package se.kth.ict.id2203.bab;


import se.kth.ict.id2203.pp2p.*;
import se.kth.ict.id2203.babport.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

public class BAB extends ComponentDefinition {
	Negative<BasicBroadcast> bab = provides(BasicBroadcast.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
	
	private Topology topology;
	
	public BAB() {
		subscribe(handleInit, control);
		subscribe(handleBabReadBroadcast, bab);
		subscribe(handleBabWriteBroadcast, bab);
		subscribe(handleWriteDeliver, pp2p);
		subscribe(handleReadDeliver, pp2p);
	}
	
	Handler<BABInit> handleInit = new Handler<BABInit>() {
		public void handle(BABInit event) {
			topology = event.getTopology();
		}
	};
	
	Handler<BabWriteMessage> handleBabWriteBroadcast = new Handler<BabWriteMessage>() {
		public void handle(BabWriteMessage event) {
			Address s = event.getSource();
			int id = event.getId();
			long ts = event.getTimestamp();
			int rank = event.getRank();
			int val = event.getValue();
			int reg = event.getRegister();
			//for (Address neighbor : topology.getNeighbors(s)) {
			for (Address neighbor : topology.getAllAddresses()) {
				trigger(new Pp2pSend(neighbor, new DeliverWriteMessage(topology.getSelfAddress(), reg, id, ts, rank, val)), pp2p);
			}
		}
	};
	
	Handler<BabReadMessage> handleBabReadBroadcast = new Handler<BabReadMessage>() {
		public void handle(BabReadMessage event) {
			Address s = event.getSource();
			int id = event.getId();
			int reg = event.getRegister();
			//for (Address neighbor : topology.getNeighbors(s)) {
			for (Address neighbor : topology.getAllAddresses()) {
				trigger(new Pp2pSend(neighbor, new DeliverReadMessage(topology.getSelfAddress(), reg, id)), pp2p);
			}
		}
	};
	
	Handler<DeliverWriteMessage> handleWriteDeliver = new Handler<DeliverWriteMessage>() {
		public void handle(DeliverWriteMessage event) {
			Address source = event.getSource();
			int id = event.getId();
			long ts = event.getTimestamp();
			int rank = event.getRank();
			int val = event.getValue();
			int reg = event.getRegister();
			//BabWriteMessage deliverEvent = new BabWriteMessage(source, reg, id, ts, rank, val);
			BabWriteMessage deliverEvent = new BabWriteMessage(source, reg, id, ts, rank, val);
			trigger(deliverEvent, bab);
		}
	};
	
	Handler<DeliverReadMessage> handleReadDeliver = new Handler<DeliverReadMessage>() {
		public void handle(DeliverReadMessage event) {
			Address source = event.getSource();
			int id = event.getId();
			int reg = event.getRegister();
			//BabReadMessage deliverEvent = new BabReadMessage(source, reg, id);
			BabReadMessage deliverEvent = new BabReadMessage(source, reg, id);
			trigger(deliverEvent, bab);
		}
	};
}
