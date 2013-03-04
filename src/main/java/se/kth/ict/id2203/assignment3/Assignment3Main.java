/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment3;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.riwc.*;
import se.kth.ict.id2203.riwcport.*;
import se.kth.ict.id2203.beb.*;
import se.kth.ict.id2203.bebport.*;
import se.kth.ict.id2203.pfd.PFD;
import se.kth.ict.id2203.pfd.PfdInit;
import se.kth.ict.id2203.pfdport.PerfectFailureDetector;
import se.kth.ict.id2203.pp2p.delay.*;
import se.kth.ict.id2203.pp2p.*;
import se.kth.ict.id2203.app2.*;
import se.kth.ict.id2203.app3.Application3;
import se.kth.ict.id2203.app3.Application3Init;

import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.java.JavaConsole;
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
 * The <code>Assignment2Main</code> class.
 * 
 * @author Zikou Filippia
 */
public class Assignment3Main extends ComponentDefinition {
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

		Kompics.createAndStart(Assignment3Main.class);
	}

	/**
	 * Instantiates a new assignment0 group0.
	 */
	public Assignment3Main() {
		
		// create components
		Component time = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);
		Component con = create(JavaConsole.class);
		Component dl = create(DelayLink.class);
		Component beb = create(BEB.class);
		Component riwc = create(RIWC.class);
		Component pfd = create(PFD.class);
		Component app = create(Application3.class);

		// handle possible faults in the components
		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, dl.control());
		subscribe(handleFault, beb.control());
		subscribe(handleFault, riwc.control());
		subscribe(handleFault, pfd.control());
		subscribe(handleFault, app.control());

		// initialize the components
		
		
		Address self = topology.getSelfAddress();
		Set<Address> neighborSet = topology.getNeighbors(self);

		int regNum = 1;
		long gama = 1000;
		long delta = 4000;
		
		trigger(new MinaNetworkInit(self, 5), network.control());
		trigger(new DelayLinkInit(topology), dl.control());
		trigger(new BEBInit(topology), beb.control());
		trigger(new RIWCInit(topology, regNum), riwc.control());
		trigger(new PfdInit(topology, gama, delta), pfd.control());
		trigger(new Application3Init(commandScript, neighborSet, self), app.control());
		

		// connect the components
		connect(app.required(Console.class), con.provided(Console.class));
		connect(app.required(ReadImposeWriteConsult.class), riwc.provided(ReadImposeWriteConsult.class));
		connect(app.required(Timer.class), time.provided(Timer.class));
		
		connect(riwc.required(BestEffortBroadcast.class), beb.provided(BestEffortBroadcast.class));
		connect(riwc.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		connect(riwc.required(PerfectFailureDetector.class), pfd.provided(PerfectFailureDetector.class));
		
		connect(beb.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		
		connect(pfd.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		connect(pfd.required(Timer.class), time.provided(Timer.class));
		
		connect(dl.required(Timer.class), time.provided(Timer.class));
		connect(dl.required(Network.class), network.provided(Network.class));
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
