//--Need to Modify--#preprocess
/*
 * Observable.java
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
 * IB 2010-10-12 1.11.5Dev8 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define MIDP define
@DMIDPVERS@
//#ifdef DMIDP20
package net.yinlight.j2me.observable;

public interface Observable {

	ObservableHandler getObservableHandler();

}
//#endif
