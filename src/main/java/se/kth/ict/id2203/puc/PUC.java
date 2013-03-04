package se.kth.ict.id2203.puc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.rwac.RWACInit;
import se.kth.ict.id2203.rwacport.*;
import se.kth.ict.id2203.pucport.*;
import se.kth.ict.id2203.babcport.*;
import se.kth.ict.id2203.eldport.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.NoLinkException;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class PUC extends ComponentDefinition{
	Negative<UniformConsensus> uc = provides(UniformConsensus.class);
	
	Positive<AbortableConsensus> ac = requires(AbortableConsensus.class);
	Positive<BasicBroadcast> bab = requires(BasicBroadcast.class);
	Positive<EventualLeaderDetector> eld = requires(EventualLeaderDetector.class);

	private Address self;
	private Topology topology;
	private Set<Integer> seenIds;
	private boolean leader;
	private HashMap<Integer, Integer> proposal;
	private HashMap<Integer, Boolean> proposed;
	private HashMap<Integer, Boolean> decided;
	
	public PUC() {
		subscribe(handleInit, control);
		subscribe(handleTrust, eld);
		subscribe(handleUcPropose, uc);
		subscribe(handleAcDecide, ac);
		subscribe(handleDecidedDeliver, bab);
	}
	
	void initInstance(int id) {
		if (!seenIds.contains(id)) {
			proposal.put(id, -1);
			proposed.put(id, false);
			decided.put(id, false);
			seenIds.add(id);
		}
	}
	
	void tryPropose(int id) {
		if (leader== true && proposed.get(id) == false && proposal.get(id)!=-1) {
			//System.out.println("Leader tries to propose...");
			proposed.put(id, true);
			trigger(new AcPropose(id, proposal.get(id)), ac);
		}
	}
	
	
	Handler<PUCInit> handleInit = new Handler<PUCInit>() {
		public void handle(PUCInit event) {
			topology = event.getTopology();
			self = topology.getSelfAddress();
			seenIds = new HashSet<Integer>();
			leader = false;
			
			proposal = new HashMap<Integer, Integer>();
			proposed = new HashMap<Integer, Boolean>();
			decided = new HashMap<Integer, Boolean>();
		}
	};
	
	Handler<Trust> handleTrust = new Handler<Trust>() {
		public void handle(Trust event) {
			Address p = event.getLeader();
			//System.out.println("The leader now is: "+event.getLeader());
			if (p.equals(self)) {
				
				leader = true;
				for (int id : seenIds) {
					tryPropose(id);
				}		
			}
			else {
				leader = false;
			}
		}
	};
	
	Handler<UcPropose> handleUcPropose = new Handler<UcPropose>() {
		public void handle(UcPropose event) {
			int id = event.getId();
			int v = event.getValue();
			initInstance(id);
			proposal.put(id, v);
			tryPropose(id);
		}	
	};
	
	Handler<AcDecide> handleAcDecide = new Handler<AcDecide>() {
		public void handle(AcDecide event) {
			int id = event.getId();
			int result = event.getValue();
			if (result != -1) {
				trigger(new BabDecidedMessage(self, id, result), bab);
			}
			else {
				proposed.put(id, false);
				tryPropose(id);
			}
		}
	};
	
	Handler<BabDecidedMessage> handleDecidedDeliver = new Handler<BabDecidedMessage>() {
		public void handle(BabDecidedMessage event) {
			Address p =event.getSource();
			int id = event.getId();
			int v = event.getValue();
			initInstance(id);
			if (decided.get(id) == false) {
				decided.put(id,  true);
				trigger(new UcDecide(id, v), uc);
			}
		}
	};
}