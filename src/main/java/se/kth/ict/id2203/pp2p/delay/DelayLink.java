package se.kth.ict.id2203.pp2p.delay;

import se.kth.ict.id2203.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.kth.ict.id2203.pp2p.Pp2pSend;
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


public final class DelayLink extends ComponentDefinition {

	Negative<PerfectPointToPointLink> pp2p = provides(PerfectPointToPointLink.class);

	Positive<Network> network = requires(Network.class);
	Positive<Timer> timer = requires(Timer.class);

	private Address self;
	private Topology topology;

	public DelayLink() {
		subscribe(handleInit, control);
		subscribe(handlePp2pSend, pp2p);
		subscribe(handleMessage, network);
		subscribe(handleDelayedMessage, timer);
	}

	Handler<DelayLinkInit> handleInit = new Handler<DelayLinkInit>() {
		public void handle(DelayLinkInit event) {
			topology = event.getTopology();
			self = topology.getSelfAddress();
		}
	};

	Handler<Pp2pSend> handlePp2pSend = new Handler<Pp2pSend>() {
		public void handle(Pp2pSend event) {
			Address destination = event.getDestination();

			if (destination.equals(self)) {
				// deliver locally
				Pp2pDeliver deliverEvent = event.getDeliverEvent();
				trigger(deliverEvent, pp2p);
				return;
			}

			long latency;
			try {
				latency = topology.getLatencyMs(self, destination);
			} catch (NoLinkException e) {
				// there is no link to the destination, we drop the message
				return;
			}

			// make a DelayLinkMessage to be delivered at the destination
			DelayLinkMessage message = new DelayLinkMessage(self, destination,
					event.getDeliverEvent());

			if (latency > 0) {
				// delay the sending according to the latency
				ScheduleTimeout st = new ScheduleTimeout(latency);
				st.setTimeoutEvent(new DelayedMessage(st, message));
				trigger(st, timer);
			} else {
				// send immediately
				trigger(message, network);
			}
		}
	};

	Handler<DelayedMessage> handleDelayedMessage = new Handler<DelayedMessage>() {
		public void handle(DelayedMessage event) {
			trigger(event.getMessage(), network);
		}
	};

	Handler<DelayLinkMessage> handleMessage = new Handler<DelayLinkMessage>() {
		public void handle(DelayLinkMessage event) {
			trigger(event.getDeliverEvent(), pp2p);
		}
	};
}
