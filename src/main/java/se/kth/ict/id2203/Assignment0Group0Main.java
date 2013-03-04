/**
 * This file is part of the ID2203 course assignments kit.
 * 
 * Copyright (C) 2009 Royal Institute of Technology (KTH)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.kth.ict.id2203;

import java.util.Set;

import org.apache.log4j.PropertyConfigurator;

import se.kth.ict.id2203.application.Application0;
import se.kth.ict.id2203.application.Application0Init;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.java.JavaConsole;
import se.kth.ict.id2203.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.flp2p.delay.DelayDropLink;
import se.kth.ict.id2203.flp2p.delay.DelayDropLinkInit;
import se.kth.ict.id2203.pp2p.PerfectPointToPointLink;
import se.kth.ict.id2203.pp2p.delay.DelayLink;
import se.kth.ict.id2203.pp2p.delay.DelayLinkInit;
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
 * The <code>Assignment0Group0</code> class.
 * 
 * @author Cosmin Arad <cosmin@sics.se>
 * @version $Id: Assignment0Group0.java 516 2009-01-28 04:00:47Z cosmin $
 */
public class Assignment0Group0Main extends ComponentDefinition {
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

		Kompics.createAndStart(Assignment0Group0Main.class);
	}

	/**
	 * Instantiates a new assignment0 group0.
	 */
	public Assignment0Group0Main() {
		// create components
		Component time = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);
		Component con = create(JavaConsole.class);
		Component pp2p = create(DelayLink.class);
		Component flp2p = create(DelayDropLink.class);
		Component app = create(Application0.class);

		// handle possible faults in the components
		subscribe(handleFault, time.control());
		subscribe(handleFault, network.control());
		subscribe(handleFault, con.control());
		subscribe(handleFault, pp2p.control());
		subscribe(handleFault, flp2p.control());
		subscribe(handleFault, app.control());

		// initialize the components
		Address self = topology.getSelfAddress();
		Set<Address> neighborSet = topology.getNeighbors(self);

		trigger(new MinaNetworkInit(self, 5), network.control());
		trigger(new DelayLinkInit(topology), pp2p.control());
		trigger(new DelayDropLinkInit(topology, 0), flp2p.control());
		trigger(new Application0Init(commandScript, neighborSet, self), app
				.control());

		// connect the components
		connect(app.required(Console.class),
				con.provided(Console.class));
		connect(app.required(PerfectPointToPointLink.class),
				pp2p.provided(PerfectPointToPointLink.class));
		connect(app.required(FairLossPointToPointLink.class),
				flp2p.provided(FairLossPointToPointLink.class));
		connect(app.required(Timer.class), time.provided(Timer.class));

		connect(pp2p.required(Timer.class), time.provided(Timer.class));
		connect(pp2p.required(Network.class), network.provided(Network.class));

		connect(flp2p.required(Timer.class), time.provided(Timer.class));
		connect(flp2p.required(Network.class), network.provided(Network.class));
	}

	Handler<Fault> handleFault = new Handler<Fault>() {
		public void handle(Fault fault) {
			fault.getFault().printStackTrace(System.err);
		}
	};
}
