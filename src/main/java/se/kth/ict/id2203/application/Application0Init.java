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
package se.kth.ict.id2203.application;

import java.util.Set;

import se.sics.kompics.Init;
import se.sics.kompics.address.Address;

/**
 * The <code>Application0Init</code> class.
 * 
 * @author Cosmin Arad <cosmin@sics.se>
 * @version $Id: Application0Init.java 516 2009-01-28 04:00:47Z cosmin $
 */
public final class Application0Init extends Init {

	private final String commandScript;

	private final Set<Address> neighborSet;
	
	private final Address self;
	
	/**
	 * Instantiates a new application0 init.
	 * 
	 * @param commandScript
	 *            the command script
	 * @param neighborSet
	 *            the neighbor set
	 * @param self
	 *            the self
	 */
	public Application0Init(String commandScript, Set<Address> neighborSet, Address self) {
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
