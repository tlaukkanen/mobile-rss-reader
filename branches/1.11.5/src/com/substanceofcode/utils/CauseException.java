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
 * IB 2010-04-30 1.11.5RC2 Track thread info.
 * IB 2010-04-30 1.11.5RC2 Method to get the first cause.
 */

package com.substanceofcode.utils;

/**
 * Allow J2SE style exceptions
 *
 * @author Irving Bunton
 */
public class CauseException extends Exception {
    
    private static final long serialVersionUID = 50L;
    final static protected int MAX_CAUSES = 50;
    private Throwable cause = null;
    final private String threadInfo;
    private boolean causeSet = false;

    public CauseException() {
		super();
		threadInfo = MiscUtil.getThreadInfo(Thread.currentThread());
    }

    public CauseException(String message) {
		super(message);
		threadInfo = MiscUtil.getThreadInfo(Thread.currentThread());
		causeSet = true;
    }

    public CauseException(String message, Throwable cause) {
		super(message);
		threadInfo = MiscUtil.getThreadInfo(Thread.currentThread());
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

    public String getThreadInfo() {
        return (threadInfo);
    }

    public Throwable getFirstCause() {
		Throwable e = getCause();
		if (e == null) {
			return null;
		}
		for (int ic = 0; ic < MAX_CAUSES; ic++) {
			if (e instanceof CauseException) {
				CauseException ce = (CauseException)e;
				Throwable nce;
				if ((nce = ce.getCause()) == null) {
					return ce;
				} else {
					e = nce;
				}
			} else if (e instanceof CauseRuntimeException) {
				CauseRuntimeException ce = (CauseRuntimeException)e;
				Throwable nce = ce.getCause();
				if (nce == null) {
					return ce;
				} else {
					e = nce;
				}
			} else {
				return e;
			}
		}

        return null;
    }

}
