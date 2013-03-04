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
package se.kth.ict.id2203.flp2p.delay;

import se.sics.kompics.Init;
import se.sics.kompics.launch.Topology;

/**
 * The <code>DelayDropLinkInit</code> class.
 * 
 * @author Cosmin Arad <cosmin@sics.se>
 * @version $Id: DelayDropLinkInit.java 516 2009-01-28 04:00:47Z cosmin $
 */
public final class DelayDropLinkInit extends Init {

	private final Topology topology;
	
	private final long randomSeed;
	
	/**
	 * Instantiates a new delay drop link init.
	 * 
	 * @param topology
	 *            the topology
	 * @param randomSeed
	 *            the random seed
	 */
	public DelayDropLinkInit(Topology topology, long randomSeed) {
		this.topology = topology;
		this.randomSeed = randomSeed;
	}
	
	/**
	 * Gets the topology.
	 * 
	 * @return the topology
	 */
	public final Topology getTopology() {
		return topology;
	}

	/**
	 * Gets the random seed.
	 * 
	 * @return the random seed
	 */
	public final long getRandomSeed() {
		return randomSeed;
	}
}
