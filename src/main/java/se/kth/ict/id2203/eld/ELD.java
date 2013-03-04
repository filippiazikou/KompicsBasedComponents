package se.kth.ict.id2203.eld;

import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.eldport.*;
import se.kth.ict.id2203.pp2p.*;
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

public class ELD extends ComponentDefinition{
	Negative<EventualLeaderDetector> eld = provides(EventualLeaderDetector.class);

	Positive<Timer> timer = requires(Timer.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private Address self;
	private Topology topology;
	private Set<Address> candidates;
	private Address leader;
	private long period;
	private long delta;
	
	private ScheduleTimeout st;

	public ELD() {
		subscribe(handleInit, control);
		subscribe(handleHeartbeatMessage, pp2p);
		subscribe(handleHeartbeatTimeout, timer);
	}
	
	Handler<ELDInit> handleInit = new Handler<ELDInit>() {
		public void handle(ELDInit event) {
			topology = event.getTopology();
			self= topology.getSelfAddress();
			delta = event.getDelta();
			/*Select Leader*/
			int min = topology.getSelfAddress().getId();
			for (Address a : topology.getAllAddresses()) {
				if (a.getId() < min)
					min = a.getId();
			}
			leader = topology.getAddress(min);
			
			/*Trigger the selection*/
			trigger(new Trust(leader), eld);
			
			/*Send Heartbeats*/
			for (Address a : topology.getAllAddresses()) {
				trigger(new Pp2pSend(a, new HeartbeatMessage(self, "heartbeat")), pp2p);
			}
			
			/*Initiate candidate Set*/
			candidates = new HashSet<Address>();
			
			/*Start timer*/
			period = event.getTimeDelay();
			st = new ScheduleTimeout(period);
			st.setTimeoutEvent(new HeartbeatTimeout(st));
			trigger(st, timer);

		}
	};


	Handler<HeartbeatTimeout> handleHeartbeatTimeout = new Handler<HeartbeatTimeout>() {
		public void handle(HeartbeatTimeout event) {
			/*Select new leader*/
			int min = -1;
			for (Address a : candidates) {
				if (min == -1) 
					min = a.getId();
				if (a.getId() < min)
					min = a.getId();
			}
			
			/*If new leader selected - trigger*/
			if (min != -1){
				Address newleader = topology.getAddress(min);
				if (!newleader.equals(leader)) {
					period = period + delta;
					leader = newleader;
					//System.out.println(newleader);
					trigger(new Trust(leader), eld);
				}
			}
			
			/*Send Heartbeats*/
			for (Address a : topology.getAllAddresses()) {
				trigger(new Pp2pSend(a, new HeartbeatMessage(self, "heartbeat")), pp2p);
			}
			
			/*Clear candidate Set*/
			candidates.clear();
			
			/*Start timer*/
			st = new ScheduleTimeout(period);
			st.setTimeoutEvent(new HeartbeatTimeout(st));
			trigger(st, timer);
		}
	};

	Handler<HeartbeatMessage> handleHeartbeatMessage = new Handler<HeartbeatMessage>() {
		
		public void handle(HeartbeatMessage event) {
			candidates.add(event.getSource());
		}
	};
}
