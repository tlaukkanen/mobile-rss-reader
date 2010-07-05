/*
 * CauseRecStoreException.java
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
/*
 * IB 2010-06-01 1.11.5RC2 Have record store exception with cause.
 * IB 2010-06-01 1.11.5RC2 Set serialVersionUID to 60L.
 */

package com.substanceofcode.utils;

/**
 * Allow J2SE style exceptions for out of memory.
 *
 * @author Irving Bunton
 */
public class CauseRecStoreException extends CauseException {
    
    private static final long serialVersionUID = 60L;

    public CauseRecStoreException() {
		super();
    }

    public CauseRecStoreException(String message) {
		super(message);
    }

    public CauseRecStoreException(String message, Throwable cause) {
		super(message, cause);
    }

}