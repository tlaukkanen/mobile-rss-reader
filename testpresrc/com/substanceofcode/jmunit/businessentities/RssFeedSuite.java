/*
 * RssFeedSuite.java
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

// Expand to define test define
@DTESTDEF@
// Expand to define compatibility
@DCOMPATDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DCOMPATIBILITY1
  //#define DCOMPATIBILITY
//#endif
//#ifdef DCOMPATIBILITY2
  //#define DCOMPATIBILITY
//#endif
//#ifdef DCOMPATIBILITY3
  //#define DCOMPATIBILITY
//#endif

//#ifdef DJMTEST
package com.substanceofcode.jmunit.businessentities;

import jmunit.framework.cldc10.TestSuite;

import com.substanceofcode.rssreader.businessentities.RssItunesItem;

final public class RssFeedSuite extends TestSuite {

	public RssFeedSuite() {
		super("RssFeedSuite");
		add(new RssFeedStoreStrTest());
		//#ifdef DCOMPATIBILITY
		add(new RssFeedStoreCompatability1Test());
		add(new RssFeedStoreCompatability2Test());
		add(new RssFeedStoreCompatability3Test());
		//#endif
	}
}
//#endif
