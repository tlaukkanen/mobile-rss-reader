//--Need to modify--#preprocess
/*
 * CauseException.java
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
 * IB 2010-05-24 1.11.5RC2 Use CauseException in compatibility package for testing.
 * IB 2010-07-03 1.11.5RC2 Have different serialVersionUID.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

package com.substanceofcode.utils.compatibility4;

/**
 * Allow J2SE style exceptions
 *
 * @author Irving Bunton
 */
public class CauseException extends Exception {
    
    private static final long serialVersionUID = 50L;
    private Throwable cause = null;
    private boolean causeSet = false;

    public CauseException() {
		super();
    }

    public CauseException(String message) {
		super(message);
		causeSet = true;
    }

    public CauseException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
		causeSet = true;
    }

    public void initCause(Throwable cause) {
		if (!causeSet) {
			this.cause = cause;
		}
    }

    public Throwable getCause() {
        return (cause);
    }

}
