//--Need to modify--#preprocess
/*
 * RssFeedStoreHolder.java
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
 * IB 2010-05-24 1.11.5RC2 Code cleanup.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define compatibility
@DCOMPATDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.utilities;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedInfo;

final public class RssFeedStoreHolder {
	public RssItunesFeed feed; 
	public RssFeedInfo infoFeed; 
}
//#endif
