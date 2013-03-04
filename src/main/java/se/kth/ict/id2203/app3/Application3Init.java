/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 2
 */

package se.kth.ict.id2203.app3;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

/**
 * The <code>PfdInit</code> class.
 * 
 * @author Zikou Filippia
 */
public final class Application3Init extends Init {

	private final String commandScript;

	private final Set<Address> neighborSet;
	
	private final Address self;
	
	
	/**
	 * Instantiates a new pfd init.
	 * 
	 * @param commandScript
	 *            the command script
	 * @param neighborSet
	 *            the neighbor set
	 * @param self
	 *            the self
	 */
	public Application3Init(String commandScript, Set<Address> neighborSet, Address self) {
		super();
		this.commandScript = commandScript;
		this.neighborSet = neighborSet;
		this.self = self;
	}
	

	/**
	 * Gets the command script.
	 * 
	 * @return the command script
	 */
	public final String getCommandScript() {
		return commandScript;
	}
	
	/**
	 * Gets the neighbor set.
	 * 
	 * @return the neighbor set
	 */
	public final Set<Address> getNeighborSet() {
		return neighborSet;
	}
	
	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public final Address getSelf() {
		return self;
	}
	

}
