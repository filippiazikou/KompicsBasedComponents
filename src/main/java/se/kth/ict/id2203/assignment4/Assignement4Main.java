
package se.kth.ict.id2203.assignment4;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import se.kth.ict.id2203.puc.*;
import se.kth.ict.id2203.pucport.*;
import se.kth.ict.id2203.rwac.*;
import se.kth.ict.id2203.rwacport.*;
import se.kth.ict.id2203.eld.*;
import se.kth.ict.id2203.eldport.*;
import se.kth.ict.id2203.babc.*;
import se.kth.ict.id2203.babcport.*;
import se.kth.ict.id2203.pp2p.delay.*;
import se.kth.ict.id2203.pp2p.*;
import se.kth.ict.id2203.app4.Application4;
import se.kth.ict.id2203.app4.Application4Init;

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
public class Assignement4Main extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static int selfId;
	private static String commandScript;
	Topology topology = Topology.load(System.getProperty("topology"), selfId);
	
	public static void main(String[] args) {
		selfId = Integer.parseInt(args[0]);
		commandScript = args[1];

		Kompics.createAndStart(Assignement4Main.class);
	}

	/**
	 * Instantiates a new assignment0 group0.
	 */
	public Assignement4Main() {
		
		// create components
		Component time = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);
		Component con = create(JavaConsole.class);
		Component dl = create(DelayLink.class);
		Component bab = create(BAB.class);
		Component puc = create(PUC.class);
		Component eld = create(ELD.class);
		Component ac = create(RWAC.class);
		Component app = create(Application4.class);

		// handle possible faults in the components
		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, dl.control());
		subscribe(handleFault, bab.control());
		subscribe(handleFault, puc.control());
		subscribe(handleFault, eld.control());
		subscribe(handleFault, ac.control());
		subscribe(handleFault, app.control());

		// initialize the components
		
		
		Address self = topology.getSelfAddress();
		Set<Address> neighborSet = topology.getNeighbors(self);
		
		trigger(new MinaNetworkInit(self, 5), network.control());
		trigger(new DelayLinkInit(topology), dl.control());
		trigger(new BABInit(topology), bab.control());
		trigger(new ELDInit(topology, 100, 500), eld.control());
		trigger(new PUCInit(topology), puc.control());
		trigger(new RWACInit(topology), ac.control());
		trigger(new Application4Init(commandScript, neighborSet, self), app.control());
		

		// connect the components
		connect(app.required(Console.class), con.provided(Console.class));
		connect(app.required(UniformConsensus.class), puc.provided(UniformConsensus.class));
		connect(app.required(Timer.class), time.provided(Timer.class));
		
		connect(puc.required(BasicBroadcast.class), bab.provided(BasicBroadcast.class));
		connect(puc.required(AbortableConsensus.class), ac.provided(AbortableConsensus.class));
		connect(puc.required(EventualLeaderDetector.class), eld.provided(EventualLeaderDetector.class));
		
		connect(ac.required(BasicBroadcast.class), bab.provided(BasicBroadcast.class));
		connect(ac.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		
		connect(bab.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		
		connect(eld.required(PerfectPointToPointLink.class), dl.provided(PerfectPointToPointLink.class));
		connect(eld.required(Timer.class), time.provided(Timer.class));
		
		connect(dl.required(Timer.class), time.provided(Timer.class));
		connect(dl.required(Network.class), network.provided(Network.class));
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
