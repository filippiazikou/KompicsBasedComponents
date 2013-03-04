package se.kth.ict.id2203.epfd;

import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.epfdport.*;
import se.kth.ict.id2203.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.kth.ict.id2203.pp2p.Pp2pSend;
import se.kth.ict.id2203.pp2p.delay.DelayLinkInit;
import se.kth.ict.id2203.pp2p.delay.DelayLinkMessage;
import se.kth.ict.id2203.pp2p.delay.DelayedMessage;
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

public class EPFD extends ComponentDefinition{
	Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);

	Positive<Timer> timer = requires(Timer.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private Address self;
	private Topology topology;
	private Set<Address> suspected;
	private Set<Address> alive;
	
	private long timeDelay;
	private long delta;
	
	private long period;
	
	private ScheduleTimeout stt; 
	private ScheduleTimeout stp; 

	public EPFD() {
		subscribe(handleInit, control);
		subscribe(handleHeartbeatMessage, pp2p);
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
				Pp2pDeliver deliverEvent = new HeartbeatMessage(self, "hb");
				Pp2pSend sendEvent= new Pp2pSend(neighbor, deliverEvent);
				trigger(sendEvent, pp2p);
			}
			stt = new ScheduleTimeout(timeDelay);
			stt.setTimeoutEvent(new HeartbeatTimeout(stt));
			trigger(stt, timer);
		}
	};

	Handler<HeartbeatMessage> handleHeartbeatMessage = new Handler<HeartbeatMessage>() {
		
		public void handle(HeartbeatMessage event) {
//			System.out.println("heart beat msg from"+event.getSource());
			alive.add(event.getSource());
		}
	};
}
