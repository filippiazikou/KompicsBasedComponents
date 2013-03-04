package se.kth.ict.id2203.epfd;

import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.epfdport.*;
import se.kth.ict.id2203.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.flp2p.Flp2pDeliver;
import se.kth.ict.id2203.flp2p.Flp2pSend;
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

public class EPFDb extends ComponentDefinition{
	Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);

	Positive<Timer> timer = requires(Timer.class);
	Positive<FairLossPointToPointLink> flp2p = requires(FairLossPointToPointLink.class);

	private Address self;
	private Topology topology;
	private Set<Address> suspected;
	private Set<Address> alive;
	
	private long timeDelay;
	private long delta;
	
	private long period;
	
	private ScheduleTimeout stt; 
	private ScheduleTimeout stp; 

	public EPFDb() {
		subscribe(handleInit, control);
		subscribe(handleHeartbeatMessageb, flp2p);
		subscribe(handleCheckTimeout, timer);
		subscribe(handleHeartbeatTimeout, timer);
	}

	Handler<EpfdInit> handleInit = new Handler<EpfdInit>() {
		public void handle(EpfdInit event) {
			topology = event.getTopology();
			self= topology.getSelfAddress();
//			System.out.println("My topology: ");
//			for (Address neighbor : topology.getNeighbors(self)) {
//				System.out.println("-"+neighbor.getId()+" ");
//			}
//			System.out.println("~~~~~");
			
			alive = new HashSet<Address>();
			alive = topology.getNeighbors(self);
			alive.add(self);
			
			suspected = new HashSet<Address>();
			
			timeDelay = event.getTimeDelay();
			delta = event.getDelta();
			
			period = timeDelay;
			
			stt = new ScheduleTimeout(timeDelay);
			stt.setTimeoutEvent(new HeartbeatTimeout(stt));
			trigger(stt, timer);
			
			stp = new ScheduleTimeout(period);
			stp.setTimeoutEvent(new CheckTimeout(stp));
			trigger(stp, timer);
		}
	};

	Handler<CheckTimeout> handleCheckTimeout = new Handler<CheckTimeout>() {
		public void handle(CheckTimeout event) {
//			System.out.println("check event");
			self= topology.getSelfAddress();
			
			if (commonAliveSuspect() == true) {
				period = period + delta;
			}
			
			for (Address neighbor : topology.getNeighbors(self)) {
				if (!suspected.contains(neighbor) && !alive.contains(neighbor)){
					suspected.add(neighbor);
					EpfdSuspect suspectEvent = new EpfdSuspect(neighbor);
					trigger(suspectEvent, epfd);
				}
				else if (suspected.contains(neighbor) && alive.contains(neighbor)){
					suspected.remove(neighbor);
					EpfdRestore restoreEvent = new EpfdRestore(neighbor);
					trigger(restoreEvent, epfd);
				}
			}
			alive.clear();
			stp = new ScheduleTimeout(period);
			stp.setTimeoutEvent(new CheckTimeout(stp));
			trigger(stp, timer);
		}

		private boolean commonAliveSuspect() {
			for (Address element : suspected) {
				if (alive.contains(element))
					return false;
			}
			return true;
		}
		
	};

	Handler<HeartbeatTimeout> handleHeartbeatTimeout = new Handler<HeartbeatTimeout>() {
		public void handle(HeartbeatTimeout event) {
//			System.out.println("heartbeat event");
			self= topology.getSelfAddress();
			
			for (Address neighbor : topology.getNeighbors(self)) {
				Flp2pDeliver deliverEvent = new HeartbeatMessageb(self, "hb");
				Flp2pSend sendEvent= new Flp2pSend(neighbor, deliverEvent);
				trigger(sendEvent, flp2p);
			}
			stt = new ScheduleTimeout(timeDelay);
			stt.setTimeoutEvent(new HeartbeatTimeout(stt));
			trigger(stt, timer);
		}
	};

	Handler<HeartbeatMessageb> handleHeartbeatMessageb = new Handler<HeartbeatMessageb>() {
		
		public void handle(HeartbeatMessageb event) {
//			System.out.println("heart beat msg from"+event.getSource());
			alive.add(event.getSource());
		}
	};
}
