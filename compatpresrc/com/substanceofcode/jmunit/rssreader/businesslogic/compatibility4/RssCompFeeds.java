//--Need to modify--#preprocess
/*
 * RssCompFeeds.java
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
 * IB 2010-05-28 1.11.5RC2 Do comparison test using FeedListParsers.xml.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
 */

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businesslogic.compatibility4;

import java.util.Date;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;

public class RssCompFeeds {

	final private RssItunesFeedInfo[] rssfeeds;
	final private RssItunesFeedInfo[] cmpRssFeeds;

	public RssCompFeeds(final RssItunesFeedInfo[] rssfeeds,
		final RssItunesFeedInfo[] cmpRssFeeds) {
		this.rssfeeds = rssfeeds;
		this.cmpRssFeeds = cmpRssFeeds;
	}

    public RssItunesFeedInfo[] getRssfeeds() {
        return (rssfeeds);
    }

    public RssItunesFeedInfo[] getCmpRssFeeds() {
        return (cmpRssFeeds);
    }

}
//#endif
