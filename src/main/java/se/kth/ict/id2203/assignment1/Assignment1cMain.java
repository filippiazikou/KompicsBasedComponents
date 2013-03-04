/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment1;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import se.kth.ict.id2203.app1.Application1bInit;
import se.kth.ict.id2203.app1.Application1b;
import se.kth.ict.id2203.epfd.*;
import se.kth.ict.id2203.epfdport.EventuallyPerfectFailureDetector;
import se.kth.ict.id2203.flp2p.delay.*;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.java.JavaConsole;
import se.kth.ict.id2203.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.flp2p.delay.DelayDropLink;
import se.kth.ict.id2203.flp2p.delay.DelayDropLinkInit;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Fault;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

/**
 * The <code>Assignment1aMain</code> class.
 * 
 * @author Zikou Filippia
 */
public class Assignment1cMain extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static int selfId;
	private static String commandScript;
	Topology topology = Topology.load(System.getProperty("topology"), selfId);

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		selfId = Integer.parseInt(args[0]);
		commandScript = args[1];

		Kompics.createAndStart(Assignment1cMain.class);
	}

	/**
	 * Instantiates a new assignment1a.
	 */
	public Assignment1cMain() {
		
		// create components
		Component time = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);
		Component con = create(JavaConsole.class);
		Component flp2p = create(DelayDropLink.class);
		Component epfd = create(EPFDb.class);
		Component app = create(Application1b.class);

		// handle possible faults in the components
		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, flp2p.control());
		subscribe(handleFault, epfd.control());
		subscribe(handleFault, app.control());

		// initialize the components
		long timeDelay=1000;
		long delta=1000;
		
		Address self = topology.getSelfAddress();
		Set<Address> neighborSet = topology.getNeighbors(self);

		trigger(new MinaNetworkInit(self, 5), network.control());
		trigger(new DelayDropLinkInit(topology, 0), flp2p.control());
		trigger(new Application1bInit(commandScript, neighborSet, self, timeDelay, delta), app.control());
		trigger(new EpfdInit(topology, timeDelay, delta), epfd.control());

		// connect the components
		connect(app.required(Console.class),
				con.provided(Console.class));
		connect(app.required(EventuallyPerfectFailureDetector.class),
				epfd.provided(EventuallyPerfectFailureDetector.class));
		connect(epfd.required(FairLossPointToPointLink.class),
				flp2p.provided(FairLossPointToPointLink.class));
		connect(app.required(Timer.class), time.provided(Timer.class));
		
		connect(epfd.required(Timer.class), time.provided(Timer.class));
		connect(flp2p.required(Timer.class), time.provided(Timer.class));
		connect(flp2p.required(Network.class), network.provided(Network.class));
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
