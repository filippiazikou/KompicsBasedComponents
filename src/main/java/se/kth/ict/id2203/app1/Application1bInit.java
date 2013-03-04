/**
 * Advanced Distributed Systems
 * Zikou Filippia
 * Assignment 1
 */

package se.kth.ict.id2203.app1;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;
import se.sics.kompics.launch.Topology;

/**
 * The <code>PfdInit</code> class.
 * 
 * @author Zikou Filippia
 */
public final class Application1bInit extends Init {

	private final String commandScript;

	private final Set<Address> neighborSet;
	
	private final Address self;
	
	private long timeDelay;
	
	private long delta;
	
	
	/**
	 * Instantiates a new epfd init.
	 * 
	 * @param commandScript
	 *            the command script
	 * @param neighborSet
	 *            the neighbor set
	 * @param self
	 *            the self
	 */
	public Application1bInit(String commandScript, Set<Address> neighborSet, Address self, long timeDelay, long delta) {
		super();
		this.commandScript = commandScript;
		this.neighborSet = neighborSet;
		this.self = self;
		this.timeDelay = timeDelay;
		this.delta = delta;
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
	
	/**
	 * Gets the gama.
	 * 
	 * @return the gama
	 */
	public final long getTimeDelay() {
		return timeDelay;
	}
	
	/**
	 * Gets the delta.
	 * 
	 * @return the delta
	 */
	public final long getDelta() {
		return delta;
	}
	
}
