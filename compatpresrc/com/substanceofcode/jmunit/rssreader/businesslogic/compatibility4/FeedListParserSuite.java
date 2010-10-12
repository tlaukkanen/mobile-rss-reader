//--Need to modify--#preprocess
/*
 * FeedListParserSuite.java
 *
 * Copyright (C) 2009 Irving Bunton
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
 * IB 2010-05-24 1.11.5RC2 Test compatibility FeedListParser.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser, HTMLLinkParser, HtmlLinkParserTest, and HtmlLinkParser2Test in small memory MIDP 1.0 to save space.
 * IB 2010-05-28 1.11.5RC2 Test compatibility OpmlParser so that we can do comparision tests.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businesslogic.compatibility4;

import jmunit.framework.cldc10.TestSuite;

final public class FeedListParserSuite extends TestSuite {

	public FeedListParserSuite() {
		super("compatibility4.FeedListParserSuite");
		//#ifndef DSMALLMEM
		add(new HtmlLinkParserTest());
		add(new HtmlLinkParser2Test());
		//#endif
		add(new OpmlParserTest());
		add(new OpmlParser2Test());
	}
}
//#endif
