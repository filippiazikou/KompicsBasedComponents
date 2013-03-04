/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.assignment3;


import se.sics.kompics.launch.Scenario;
import se.sics.kompics.launch.Topology;

@SuppressWarnings("serial")
public final class Assignment3bExecution {

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
				
				/*Exercise 1+2*/
//				defaultLinks(1000, 0);
				
				/*Exercise 3*/
				link(1, 2, 1000, 0).bidirectional();
				link(1, 3, 2500, 0).bidirectional();
				link(2, 3, 2300, 0).bidirectional();

			}
		};

		Scenario scenario1 = new Scenario(Assignement3bMain.class) {
			{
				/*Exercise 1*/
//				command(1, "S30000");
//				command(2, "S500:W4:S25000");
//				command(3, "S10000:R");
				
				/*Exercise 2*/
				//command(1, "S500:W5:R:S5000:R:S30000");
				//command(2, "S500:W6:R:S5000:R:S30000");
				//command(3, "");
				//Execute on 3 after sleep: S500:R:S500:R:S10000
				
				/*Exercise 3*/
				command(1, "S500:W1:R:S500:R:S8000");
				command(2, "S500:W2:R:S500:R:S8000",100);
				command(3, "S500:W3:R:S500:R:S8000", 200);

			}
		};

		scenario1.executeOn(topology1);

		System.exit(0);
	}
}
