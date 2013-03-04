/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment3;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.riwcm.*;
import se.kth.ict.id2203.riwcport.*;
import se.kth.ict.id2203.bab.*;
import se.kth.ict.id2203.babport.*;
import se.kth.ict.id2203.pp2p.delay.*;
import se.kth.ict.id2203.pp2p.*;
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
public class Assignement3bMain extends ComponentDefinition {
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

		Kompics.createAndStart(Assignement3bMain.class);
	}

	/**
	 * Instantiates a new assignment0 group0.
	 */
	public Assignement3bMain() {
		
		// create components
		Component time = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);
		Component con = create(JavaConsole.class);
		Component dl = create(DelayLink.class);
		Component bab = create(BAB.class);
		Component riwcm = create(RIWCM.class);
		Component app = create(Application3.class);

		// handle possible faults in the components
		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, dl.control());
		subscribe(handleFault, bab.control());
		subscribe(handleFault, riwcm.control());
		subscribe(handleFault, app.control());

		// initialize the components
		
		
		Address self = topology.getSelfAddress();
		Set<Address> neighborSet = topology.getNeighbors(self);

		int regNum = 1;
		
		trigger(new MinaNetworkInit(self, 5), network.control());
		trigger(new DelayLinkInit(topology), dl.control());
		trigger(new BABInit(topology), bab.control());
		trigger(new RIWCMInit(topology, regNum), riwcm.control());
		trigger(new Application3Init(commandScript, neighborSet, self), app.control());
		

		// connect the components
		connect(app.required(Console.class), con.provided(Console.class));
		connect(app.required(ReadImposeWriteConsult.class), riwcm.provided(ReadImposeWriteConsult.class));
		connect(app.required(Timer.class), time.provided(Timer.class));
		
		connect(riwcm.required(BasicBroadcast.class), bab.provided(BasicBroadcast.class));
		connect(riwcm.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		
		connect(bab.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		
		connect(dl.required(Timer.class), time.provided(Timer.class));
		connect(dl.required(Network.class), network.provided(Network.class));
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
