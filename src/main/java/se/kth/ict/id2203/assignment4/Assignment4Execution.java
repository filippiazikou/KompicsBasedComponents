/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment4;


import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

@SuppressWarnings("serial")
public final class Assignment4Execution {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static final void main(String[] args) {
		Topology topology1 = new Topology() {
			{
				node(1, "127.0.0.1", 22031);
				node(2, "127.0.0.1", 22032);
				node(3, "127.0.0.1", 22033);
				
				defaultLinks(1000, 0);
			}
		};

		Scenario scenario1 = new Scenario(Assignement4Main.class) {
			{
				command(1, "D2200:P1-1");
				command(2, "D2000:P1-2");
				command(3, "D2000:P1-3");
			}
		};

		scenario1.executeOn(topology1);

		System.exit(0);
		// move one of the below scenario executions above the exit for
		// execution

		/*scenario1.executeOn(topology1);
		scenario1.executeOn(topology2);
		scenario2.executeOn(topology1);
		scenario2.executeOn(topology2);
		scenario1.executeOnFullyConnected(topology1);
		scenario1.executeOnFullyConnected(topology2);
		scenario2.executeOnFullyConnected(topology1);
		scenario2.executeOnFullyConnected(topology2);*/
	}
}
