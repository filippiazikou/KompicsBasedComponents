/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment2;


import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

@SuppressWarnings("serial")
public final class Assignment2Execution {

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
				node(4, "127.0.0.1", 22034);
				node(5, "127.0.0.1", 22035);
				node(6, "127.0.0.1", 22036);
				link(1, 2, 3000, 0).bidirectional();
				link(2,3, 3000, 0);
				//link(2,3, 3000, 0).bidirectional();
				//link(3, 6, 3000, 0);
				//link(4, 6, 3000, 0).bidirectional();
				//link(6, 5, 3000, 0);
				
				defaultLinks(1000, 0);
			}
		};

		Scenario scenario1 = new Scenario(Assignment2Main.class) {
			{
				command(1, "S500:Baaa");
				command(2, "S500:Bccc");
				command(3, "S500:Beee");
				command(4, "S1000:Bbbb");
				command(5, "S1000:Bddd");
				command(6, "S1000:Bfff");
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
