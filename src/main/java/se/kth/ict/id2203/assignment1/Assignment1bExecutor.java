/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment1;


import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

/**
 * The <code>Assignment1aMain</code> class.
 * 
 * @author Zikou Filippia
 */

@SuppressWarnings("serial")
public final class Assignment1bExecutor {

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
				link(1, 2, 3000, 0).bidirectional();
				link(2,3, 3000, 0);
				defaultLinks(1000, 0);
			}
		};

		Scenario scenario1 = new Scenario(Assignment1bMain.class) {
			{
				command(1, "S500");
				command(2, "S500");
				command(3, "S500");
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
