//--Need to Modify--#preprocess
/*
 * ObservableHandler.java
 *
 * Copyright (C) 2010 Irving Bunton, Jr
 * http://www.substanceofcode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * Alternately, at your option, you can redistribute the software and/or modify
 * it under the terms the GNU Lessor General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License or
 * GNU Lesser Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
/*
 * IB 2010-06-15 1.11.5Dev2 Use observer pattern for feed parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-06-15 1.11.5Dev2 Use version that can be distributed as GPL or LGPL.
 * IB 2010-07-05 1.11.5Dev7 Update source text to make distribution/redistribution license options clearer.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev8 Change --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-03-13 1.11.5Dev17 Have notifyObservers without parameter send boolean true to observers.
 */

// Expand to define MIDP define
@DMIDPVERS@
//#ifdef DMIDP20
package net.yinlight.j2me.observable;

import java.util.Vector;

public class ObservableHandler {

	private final Vector observers;
	private boolean canceled = false;
	private boolean changed = false;

	public ObservableHandler() {
		observers = new Vector();
	}

	public void addObserver(final Observer o) {
		synchronized(this) {
			if (!observers.contains(o)) {
				observers.addElement(o);
			}
		}
	}

	public void deleteObserver(final Observer o) {
		synchronized(this) {
			if (observers.contains(o)) {
				observers.removeElement(o);
			}
		}
	}

	public void deleteObservers() {
		synchronized(this) {
			observers.removeAllElements();
		}
	}

	public int countObservers() {
		synchronized(this) {
			return observers.size();
		}
	}

	public void notifyObservers(Observable observable) {
		notifyObservers(observable, new Boolean(true));
	}

	public void notifyObservers(final Observable observable, final Object arg) {
		synchronized(this) {
			for (int i = 0; i < observers.size(); i++) {
				((Observer)observers.elementAt(i)).changed(observable, arg);
			}
			clearChanged();
		}
	}

	public Observable checkActive(boolean cparseBackground,
			Observable cbackGrParser, Observable observable) {
		if (cparseBackground &&
			(cbackGrParser != null) &&
			observable.getClass().isInstance(cbackGrParser) &&
			(cbackGrParser == observable)) {
				return observable;
		} else {
			return null;
		}
	}

    public void setCanceled(Observable observable, boolean canceled) {
		synchronized(this) {
			this.canceled = canceled;
			notifyObservers(observable);
		}
    }

    public boolean isCanceled() {
		synchronized(this) {
			return canceled;
		}
    }

    public void clearChanged() {
        this.changed = false;
    }

    public boolean hasChanged() {
        return (changed);
    }

}
//#endif
