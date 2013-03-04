/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment2;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.lpb.*;
import se.kth.ict.id2203.lpbport.*;
import se.kth.ict.id2203.sub.*;
import se.kth.ict.id2203.subport.*;
import se.kth.ict.id2203.flp2p.delay.*;
import se.kth.ict.id2203.flp2p.*;
import se.kth.ict.id2203.app2.*;

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
public class Assignment2Main extends ComponentDefinition {
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

		Kompics.createAndStart(Assignment2Main.class);
	}

	/**
	 * Instantiates a new assignment0 group0.
	 */
	public Assignment2Main() {
		
		// create components
		Component time = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);
		Component con = create(JavaConsole.class);
		Component ddl = create(DelayDropLink.class);
		Component sub = create(SUB.class);
		Component lpb = create(LPB.class);
		Component app = create(Application2.class);

		// handle possible faults in the components
		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, ddl.control());
		subscribe(handleFault, lpb.control());
		subscribe(handleFault, app.control());

		// initialize the components
		
		
		Address self = topology.getSelfAddress();
		Set<Address> neighborSet = topology.getNeighbors(self);

		int fanout = 3;
		int rounds = 10;
		long delta = 2000;
		double alpha = 0.5;
		
		trigger(new MinaNetworkInit(self, 5), network.control());
		trigger(new DelayDropLinkInit(topology, 0), ddl.control());
		trigger(new LPBInit(topology, fanout, rounds, delta, alpha), lpb.control());
		trigger(new SUBInit(topology), sub.control());
		trigger(new Application2Init(commandScript, neighborSet, self), app.control());
		

		// connect the components
		connect(app.required(Console.class), con.provided(Console.class));
		connect(app.required(ProbabilisticBroadcast.class), lpb.provided(ProbabilisticBroadcast.class));
		connect(app.required(Timer.class), time.provided(Timer.class));
		
		connect(lpb.required(UnreliableBroadcast.class), sub.provided(UnreliableBroadcast.class));
		connect(lpb.required(FairLossPointToPointLink.class), ddl.provided(FairLossPointToPointLink.class));
		connect(lpb.required(Timer.class), time.provided(Timer.class));
		
		connect(sub.required(FairLossPointToPointLink.class), ddl.provided(FairLossPointToPointLink.class));
		
		connect(ddl.required(Timer.class), time.provided(Timer.class));
		connect(ddl.required(Network.class), network.provided(Network.class));
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
