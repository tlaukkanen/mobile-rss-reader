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
 * IB 2010-03-07 1.11.4RC1 Use observer pattern for feed/OPML/list parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-03-14 1.11.5RC2 Remove unnecessary keywords.
*/
// Expand to define MIDP define
//#define DMIDP20
//#ifdef DMIDP20
package net.eiroca.j2me.observable;

/**
	* Observe interface
	*/
public interface Observer {

	void changed(Observable observable);

}
//#endif
