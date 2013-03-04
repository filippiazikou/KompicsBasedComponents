package se.kth.ict.id2203.pfd;

import java.util.HashSet;
import java.util.Set;

import se.kth.ict.id2203.application.ApplicationContinue;
import se.kth.ict.id2203.application.Pp2pMessage;
import se.kth.ict.id2203.pfdport.PerfectFailureDetector;
import se.kth.ict.id2203.pfdport.PfdCrash;
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

public class PFD extends ComponentDefinition{
	Negative<PerfectFailureDetector> pfd = provides(PerfectFailureDetector.class);

	Positive<Timer> timer = requires(Timer.class);
	Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

	private Address self;
	private Topology topology;
	private Set<Address> detected;
	private Set<Address> alive;
	
	private long gama;
	private long delta;
	
	private ScheduleTimeout stg; 
	private ScheduleTimeout std; 

	public PFD() {
		subscribe(handleInit, control);
		subscribe(handleHeartbeatMessage, pp2p);
		subscribe(handleCheckTimeout, timer);
		subscribe(handleHeartbeatTimeout, timer);
	}

	Handler<PfdInit> handleInit = new Handler<PfdInit>() {
		public void handle(PfdInit event) {
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
			
			detected = new HashSet<Address>();
			
			gama = event.getGama();
			delta = event.getDelta();
			
			stg = new ScheduleTimeout(gama);
			stg.setTimeoutEvent(new HeartbeatTimeout(stg));
			trigger(stg, timer);
			
			std = new ScheduleTimeout(delta+gama);
			std.setTimeoutEvent(new CheckTimeout(std));
			trigger(std, timer);
			
		}
	};

	Handler<CheckTimeout> handleCheckTimeout = new Handler<CheckTimeout>() {
		public void handle(CheckTimeout event) {
//			System.out.println("check timeout");
			self= topology.getSelfAddress();
			for (Address neighbor : topology.getNeighbors(self)) {
				if (!detected.contains(neighbor) && !alive.contains(neighbor)){
					detected.add(neighbor);
					PfdCrash crashEvent = new PfdCrash(neighbor);
					trigger(crashEvent, pfd);
				}
			}
			alive.clear();
			std = new ScheduleTimeout(delta+gama);
			std.setTimeoutEvent(new CheckTimeout(std));
			trigger(std, timer);
		}
	};

	Handler<HeartbeatTimeout> handleHeartbeatTimeout = new Handler<HeartbeatTimeout>() {
		public void handle(HeartbeatTimeout event) {
//			System.out.println("heart beat timeout");
			self= topology.getSelfAddress();
			for (Address neighbor : topology.getNeighbors(self)) {
				Pp2pDeliver deliverEvent = new HeartbeatMessage(self, "hb");
				Pp2pSend sendEvent= new Pp2pSend(neighbor, deliverEvent);
				trigger(sendEvent, pp2p);
			}
			stg = new ScheduleTimeout(gama);
			stg.setTimeoutEvent(new HeartbeatTimeout(stg));
			trigger(stg, timer);
		}
	};

	Handler<HeartbeatMessage> handleHeartbeatMessage = new Handler<HeartbeatMessage>() {
		
		public void handle(HeartbeatMessage event) {
//			System.out.println("heart beat msg from"+event.getSource());
			alive.add(event.getSource());
		}
	};
}
