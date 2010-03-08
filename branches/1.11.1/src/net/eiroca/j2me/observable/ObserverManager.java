/** GPL >= 2.0
 *
 * Copyright (C) M. Serhat Cinar, http://graviton.de
 * Copyright (C) 2006-2008 eIrOcA (eNrIcO Croce & sImOnA Burzio)
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
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
/**
 * This was modified no later than 2009-01-29
 */
/*
   IB 2010-03-07 1.11.4RC1 Use observer pattern for feed/OPML/list parsing to prevent hangs from spotty networks and bad URLs.
*/
// Expand to define MIDP define
//#define DMIDP20
//#ifdef DMIDP20
package net.eiroca.j2me.observable;

import java.util.Vector;

/**
	* Observer manager.  This allows objects to listen for changes.
	*/
public class ObserverManager {

	private final Vector observers;
	private boolean m_canceled = false;
	private Observable observable = null;

	public ObserverManager(Observable observable) {
		observers = new Vector();
		this.observable = observable;
	}

	public void addObserver(final Observer observer) {
		synchronized(this) {
			if (!observers.contains(observer)) {
				observers.addElement(observer);
			}
		}
	}

	public void removeObserver(final Observer observer) {
		synchronized(this) {
			if (observers.contains(observer)) {
				observers.removeElement(observer);
			}
		}
	}

	public void notifyObservers(final Observable observable) {
		synchronized(this) {
			for (int i = 0; i < observers.size(); i++) {
				((Observer) observers.elementAt(i)).changed(observable);
			}
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

    public void setCanceled(boolean canceled) {
		synchronized(this) {
			this.m_canceled = canceled;
			notifyObservers(observable);
		}
    }

    public boolean isCanceled() {
		synchronized(this) {
			return m_canceled;
		}
    }

}
//#endif
