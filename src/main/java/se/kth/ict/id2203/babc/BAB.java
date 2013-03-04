package se.kth.ict.id2203.babc;


import se.kth.ict.id2203.pp2p.*;
import se.kth.ict.id2203.babcport.*;
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
		subscribe(handleBabDecidedBroadcast, bab);
		subscribe(handleDecidedDeliver, pp2p);
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
			int id = event.getId();
			long ts = event.getTimestamp();
			int val = event.getValue();
			//for (Address neighbor : topology.getNeighbors(s)) {
			for (Address neighbor : topology.getAllAddresses()) {
				trigger(new Pp2pSend(neighbor, new DeliverWriteMessage(topology.getSelfAddress(), id, ts, val)), pp2p);
			}
		}
	};
	
	Handler<BabReadMessage> handleBabReadBroadcast = new Handler<BabReadMessage>() {
		public void handle(BabReadMessage event) {
			int id = event.getId();
			long ts = event.getTimestamp();
			for (Address neighbor : topology.getAllAddresses()) {
				trigger(new Pp2pSend(neighbor, new DeliverReadMessage(topology.getSelfAddress(), id, ts)), pp2p);
			}
		}
	};
	
	Handler<BabDecidedMessage> handleBabDecidedBroadcast = new Handler<BabDecidedMessage>() {
		public void handle(BabDecidedMessage event) {
			int id = event.getId();
			int val = event.getValue();
			//for (Address neighbor : topology.getNeighbors(s)) {
			for (Address neighbor : topology.getAllAddresses()) {
				trigger(new Pp2pSend(neighbor, new DeliverDecidedMessage(topology.getSelfAddress(), id, val)), pp2p);
			}
		}
	};
	
	Handler<DeliverWriteMessage> handleWriteDeliver = new Handler<DeliverWriteMessage>() {
		public void handle(DeliverWriteMessage event) {
			int id = event.getId();
			long ts = event.getTimestamp();
			int val = event.getValue();
			BabWriteMessage deliverEvent = new BabWriteMessage(event.getSource(), id, ts, val);
			trigger(deliverEvent, bab);
		}
	};
	
	Handler<DeliverReadMessage> handleReadDeliver = new Handler<DeliverReadMessage>() {
		public void handle(DeliverReadMessage event) {
			int id = event.getId();
			long ts = event.getTimestamp();
			//BabReadMessage deliverEvent = new BabReadMessage(source, reg, id);
			BabReadMessage deliverEvent = new BabReadMessage(event.getSource(), ts, id);
			trigger(deliverEvent, bab);
		}
	};
	
	Handler<DeliverDecidedMessage> handleDecidedDeliver = new Handler<DeliverDecidedMessage>() {
		public void handle(DeliverDecidedMessage event) {
			int id = event.getId();
			int val = event.getValue();
			BabDecidedMessage deliverEvent = new BabDecidedMessage(event.getSource(), id, val);
			trigger(deliverEvent, bab);
		}
	};
	
}
