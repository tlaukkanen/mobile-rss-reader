//--Need to modify--#preprocess
/*
 * HtmlLinkParserTest.java
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
 * IB 2010-05-24 1.11.5RC2 Unit test HTMLLinkParser class.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser HtmlLinkParserTest in small memory MIDP 1.0 to save space.
 * IB 2010-05-28 1.11.5RC2 Code cleanup.
 * IB 2010-06-29 1.11.5RC2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
 * IB 2011-03-07 1.11.5Dev17 Need () after Thread.yield.
 */

// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DFULLVERS
//#ifndef DSMALLMEM
//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businesslogic;

import java.util.Date;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
//#ifndef DSMALLMEM
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
//#endif
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

final public class HtmlLinkParserTest extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	private boolean ready = false;

	public HtmlLinkParserTest() {
		super(1, "HtmlLinkParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testHtmlLinkParse1();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	//#ifdef DMIDP20
	public void changed(Observable observable, Object arg) {
		ready = true;
		synchronized(this) {
			super.notifyAll();
		}
	}
	//#endif

	public boolean isReady() {
		return ready;
	}

	/* Test parse HTML. */
	public void testHtmlLinkParse1() throws Throwable {
		String mname = "testHtmlLinkParse1";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				"jar:///test-a-href-html.html", "", "", new RssFeedStore());
		RssItunesFeed[] cmpRssFeeds = new RssItunesFeed[] {
			new RssItunesFeed("Test 1 href link with img (closed) tag",
					"jar:///link1.xml", "", ""),
			new RssItunesFeed("Test 2 href link with img (unclosed)",
					"jar:///link2.xml", "", ""),
			new RssItunesFeed("0 rss.asp?ad=essa (modify header).",
					"jar:///link3.xml",
					"", ""),
			new RssItunesFeed("1 rss-1252.xml",
					"jar:///rss-1252.xml", "", ""),
			new RssItunesFeed("2 rss-i-1252.xml",
					"jar:///rss-i-1252.xml", "", ""),
			new RssItunesFeed("2 rss-i-1252.xml",
					"jar:///rss-i-1252.xml", "", "")};

		htmlLinkParserTestSub(mname, htmlParser, cmpRssFeeds);
	}

    public void htmlLinkParserTestSub(final String mname,
			final HTMLLinkParser htmlParser,
			final RssItunesFeed[] cmpRssFeeds)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			if (finestLoggable) {logger.finest(mname + " htmlParser=" + htmlParser);}
			//#endif
			ready = false;
			//#ifdef DMIDP20
			htmlParser.getObservableHandler().addObserver(this);
			//#endif
			htmlParser.startParsing();
			//#ifdef DMIDP20
			while (!isReady()) {
				synchronized(this) {
					wait(500L);
				}
			}
			//#else
			synchronized(this) {
				Thread.yield();
				Thread.sleep(5000L);
			}
			htmlParser.join();
			//#endif
			RssItunesFeed[] rssfeeds = htmlParser.getFeeds();
			for (int i = 0; (i < rssfeeds.length) && (i < cmpRssFeeds.length);
					i++) {
				RssItunesFeed feed = rssfeeds[i];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " feed=" + feed);}
				//#endif
				RssItunesFeed cmpfeed = cmpRssFeeds[i];
				assertTrue("Feed must equal expected feed.", cmpfeed.equals(feed));
			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " finished.");}
			//#endif
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

}
//#endif
//#endif
//#endif
