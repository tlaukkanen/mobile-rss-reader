//--Need to modify--#preprocess
/*
 * UtilitySuite.java
 *
 * Copyright (C) 2009-2010 Irving Bunton
 * http://code.google.com/p/mobile-rss-reader/
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
 * IB 2010-05-24 1.11.5RC2 Unit test utility classes.
 * IB 2010-06-27 1.11.5Dev2 Test 1st and 2nd settings test classes.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only doing EncodingUtilTest if this is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use conditional preprocessed cldc11 code (with modifications) instead of cldc10 code.
 */

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define compatibility
@DCOMPATDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.utils;

import jmunit.framework.cldc11.TestSuite;

final public class UtilitySuite extends TestSuite {

	public UtilitySuite() {
		super("UtilitySuite");
		add(new Settings1Test());
		add(new Settings2Test());
		//#ifdef DFULLVERS
		add(new EncodingUtilTest());
		add(new XmlParserTest());
		//#ifndef DSMALLMEM
		add(new HtmlParserTest());
		//#endif
		//#ifdef DCOMPATIBILITY
		add(new com.substanceofcode.jmunit.utils.compatibility4.SortTest());
		//#endif
		//#endif
	}

}
//#endif
