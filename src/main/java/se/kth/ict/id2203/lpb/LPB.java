package se.kth.ict.id2203.lpb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import se.kth.ict.id2203.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.flp2p.Flp2pDeliver;
import se.kth.ict.id2203.flp2p.Flp2pSend;
import se.kth.ict.id2203.lpbport.PbBroadcast;
import se.kth.ict.id2203.lpbport.PbDeliver;
import se.kth.ict.id2203.lpbport.ProbabilisticBroadcast;
import se.kth.ict.id2203.pfd.CheckTimeout;
import se.kth.ict.id2203.subport.UnBroadcast;
import se.kth.ict.id2203.subport.UnDeliver;
import se.kth.ict.id2203.subport.UnreliableBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class LPB extends ComponentDefinition{
	Negative<ProbabilisticBroadcast> pb = provides(ProbabilisticBroadcast.class);

	Positive<Timer> timer = requires(Timer.class);
	Positive<FairLossPointToPointLink> flp2p = requires(FairLossPointToPointLink.class);
	Positive<UnreliableBroadcast> sub = requires(UnreliableBroadcast.class);

	private Address self;
	private Topology topology;
	private HashMap<Address, Integer> next;
	private int lsn;
	private int fanout;
	private int rMax;
	private long delta;
	private double alpha;
	
	private Set<UnDeliver> stored;
	private Set<UnDeliver> pending;
	
	public LPB() {
		subscribe(handleInit, control);
		subscribe(handleDataMessage, flp2p);
		subscribe(handleRequestMessage, flp2p);
		subscribe(handlePbBroadcast, pb);
		subscribe(handleUnDeliver, sub);
		subscribe(handleLPBTimeout, timer);
	}
	
	
	private void existEvent(UnDeliver msg) {
		Address s = msg.getSource();
		String x = msg.getMsg();
		int sn = msg.getSn();
		int value = next.get(s);
		next.remove(s);
		next.put(s, value);
		pending.remove(msg);
		//System.out.println("upon pending exist... triggering PbDeliver");
		trigger(new PbDeliver(s, x), pb);
	}
	
	private void gossip(RequestMessage msg)
	{	
		/*picktargets*/
		//System.out.println("Picking targets...");
		self = topology.getSelfAddress();
		Set<Address> targets = new HashSet<Address>();
		while (targets.size()<fanout) {
			/*Select one random*/
			Set<Address> Pi = new HashSet<Address>();
			Pi = topology.getNeighbors(self);
			Pi.remove(self);
			int size = Pi.size();
			int item = new Random().nextInt(size); 
			int i = 0;
			Address candidate = null;
			for(Address add : Pi)
			{
			    if (i == item) {
			        candidate = add;
			        break;
			    }
			    i = i + 1;
			}
			if (targets.contains(candidate) == false)
				targets.add(candidate);
		}
		//System.out.println("Gossiping: triggering Flp2pSend (upon unDeliver event)");
		for (Address a : targets) {
			trigger(new Flp2pSend(a, msg), flp2p);
		}
	}
	
	Handler<LPBInit> handleInit = new Handler<LPBInit>() {
		public void handle(LPBInit event) {
			topology = event.getTopology();
			fanout = event.getFanout();
			rMax = event.getR();
			delta = event.getDelta();
			alpha = event.getAlpha();
			self= topology.getSelfAddress();
			next = new HashMap<Address, Integer>();
			for (Address neighbor : topology.getNeighbors(self)) {
				next.put(neighbor, 1);
			}
			lsn = 0;
			pending = new HashSet<UnDeliver>();
			stored = new HashSet<UnDeliver>();
		}
	};
	
	
	
	
	Handler<PbBroadcast> handlePbBroadcast = new Handler<PbBroadcast>() {
		public void handle(PbBroadcast event) {
			self= topology.getSelfAddress();
			lsn = lsn +1;
			UnBroadcast msg = new UnBroadcast(self, event.getMsg(), lsn);
			//System.out.println("PbBroadcast: triggering UnBroadcast");
			trigger(msg, sub);
		}
	};
	
	Handler<DataMessage> handleDataMessage = new Handler<DataMessage>() {
		public void handle(DataMessage event) {
			//System.out.println("DataMessage: add to pending");
			UnDeliver msgDel = new UnDeliver(event.getSource(), event.getMsg(), event.getSn());
			pending.add(msgDel);
			existEvent(msgDel);
		}
	};
	
	
	Handler<RequestMessage> handleRequestMessage = new Handler<RequestMessage>() {
		public void handle(RequestMessage event) {
			Address q = event.getSource();
			Address s = event.getDatasource();
			int sn = event.getMissing();
			int r = event.getR();
			boolean found = false;
			for (UnDeliver dm : stored) {
				if (dm.getSource().equals(s) && dm.getSn()==sn ) {
					//System.out.println("RequestMessage: triggering DataMessage");
					trigger(new DataMessage(s, dm.getMsg(), sn), flp2p);
					found = true;
				}
				if (found == false && r>0) {
					RequestMessage rm = new RequestMessage(q, s, sn, r-1);
					gossip(rm);
				}
					
			}
		}
	};
	
	
	
	Handler<UnDeliver> handleUnDeliver = new Handler<UnDeliver>() {
		public void handle(UnDeliver event) {
			self= topology.getSelfAddress();
			Address s = event.getSource();
			String m = event.getMsg();
			int sn = event.getSn();
			if (Math.random()  > alpha) {
				stored.add(event);
			}
			if (sn == next.get(s)) {
				int value = next.get(s);
				next.remove(s);
				next.put(s, value+1);
				//System.out.println("UnDeliver: triggering PbDeliver");
				PbDeliver deliverEvent = new PbDeliver(s, m);
				trigger(deliverEvent, pb);
			}
			else if (sn > next.get(s)) {
				pending.add(event);
				existEvent(event);
				/*for all missing that belong to [next(s), sn-1] */
				for (Address neighbor : topology.getNeighbors(self)) {
					if (next.get(neighbor) >= next.get(s) && next.get(neighbor) < sn-1) {
						/*if there is no message with the value missing at pending array*/
						boolean found = false;
						for (UnDeliver dm : pending) {
							if (dm.getSource().equals(s) && dm.getSn() == next.get(neighbor)) {
								found = true;
								break;
							}
						}
						/*if not found - gossip*/
						if (found == false) {
							System.out.println("UnDeliver: gossiping...");
							RequestMessage rm = new RequestMessage(self, s, next.get(neighbor), rMax-1);
							gossip(rm);
						}
					}
				}
				ScheduleTimeout st = new ScheduleTimeout(delta);
				st.setTimeoutEvent(new LPBTimeout(st, s, sn));
				//System.out.println("UnDeliver: triggering timeout...");
				trigger(st, timer);
			}
		}
	};
	
	Handler<LPBTimeout> handleLPBTimeout = new Handler<LPBTimeout>() {
		public void handle(LPBTimeout event) {
			//System.out.println("At timeout...");
			Address s = event.getSource();
			int sn = event.getSn();
			if (sn>next.get(s)) {
				int value = next.get(s);
				next.remove(s);
				next.put(s, value+1);
			}
		}
	};
}
