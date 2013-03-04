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

import se.kth.ict.id2203.pp2p.Pp2pDeliver;
import se.sics.kompics.address.Address;

/**
 * The <code>Pp2pMessage</code> class.
 * 
 * @author Cosmin Arad <cosmin@sics.se>
 * @version $Id: Pp2pMessage.java 516 2009-01-28 04:00:47Z cosmin $
 */
public class Pp2pMessage extends Pp2pDeliver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2193713942080123560L;
	
	private final String message;

	/**
	 * Instantiates a new pp2p message.
	 * 
	 * @param source
	 *            the source
	 * @param message
	 *            the message
	 */
	protected Pp2pMessage(Address source, String message) {
		super(source);
		this.message = message;
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public final String getMessage() {
		return message;
	}
}
