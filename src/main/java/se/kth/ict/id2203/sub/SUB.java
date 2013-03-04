package se.kth.ict.id2203.sub;

import java.util.HashSet;

import se.kth.ict.id2203.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.flp2p.Flp2pDeliver;
import se.kth.ict.id2203.flp2p.Flp2pSend;
import se.kth.ict.id2203.subport.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

public class SUB extends ComponentDefinition {
	Negative<UnreliableBroadcast> sub = provides(UnreliableBroadcast.class);
	Positive<FairLossPointToPointLink> flp2p = requires(FairLossPointToPointLink.class);
	
	private Address self;
	private Topology topology;
	
	public SUB() {
		subscribe(handleInit, control);
		subscribe(handleUnBroadcast, sub);
		subscribe(handleDeliverMessage, flp2p);
	}
	
	Handler<SUBInit> handleInit = new Handler<SUBInit>() {
		public void handle(SUBInit event) {
			topology = event.getTopology();
		}
	};
	
	Handler<UnBroadcast> handleUnBroadcast = new Handler<UnBroadcast>() {
		public void handle(UnBroadcast event) {
			//System.out.println("At UnBroadcast");
			Address s= topology.getSelfAddress();
			String m=event.getMsg();
			int lsn = event.getSn();

			for (Address neighbor : topology.getNeighbors(s)) {
				//System.out.println("triggering FlSend at"+neighbor);
				trigger(new Flp2pSend(neighbor, new DataMessage(s, m, lsn)), flp2p);
			}
		}
	};
	
	Handler<DataMessage> handleDeliverMessage = new Handler<DataMessage>() {
		public void handle(DataMessage event) {
			Address source = event.getSource();
			String msg = event.getMsg();
			int sn = event.getSn();
			//System.out.println("DataMessage triggering UnDeliver");
			UnDeliver deliverEvent = new UnDeliver(source, msg, sn);
			trigger(deliverEvent, sub);
		}
	};
}
