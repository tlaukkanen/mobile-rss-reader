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
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifndef DSMALLMEM
//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businesslogic.compatibility4;

import java.util.Date;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businesslogic.compatibility4.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
//#ifndef DSMALLMEM
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLLinkParser;
//#endif
//#ifdef DMIDP20
import net.eiroca.j2me.observable.Observer;
import net.eiroca.j2me.observable.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

final public class HtmlLinkParserTest extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	private boolean ready = false;

	public HtmlLinkParserTest() {
		super(1, "compatibility4.HtmlLinkParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testHtmlParse1();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	//#ifdef DMIDP20
	public void changed(Observable observable) {
		ready = true;
	}
	//#endif

	public boolean isReady() {
		return ready;
	}

	/* Test parse HTML. */
	public void testHtmlParse1() throws Throwable {
		String mname = "testHtmlParse1";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				"jar:///links.html", "", "");
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser("jar:///links.html", "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser);
	}

    public void compatibilityHtmlLinkParserTestSub(final String mname,
			final com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser,
			final HTMLLinkParser compatibilityHtmlParser)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			if (finestLoggable) {logger.finest(mname + " compatibilityHtmlLinkParserTestSub htmlParser=" + htmlParser);}
			//#endif
			ready = false;
			//#ifdef DMIDP20
			htmlParser.getObserverManager().addObserver(this);
			//#endif
			htmlParser.startParsing();
			//#ifdef DMIDP20
			while (!isReady()) {
				synchronized(this) {
					wait(1000L);
				}
			}
			//#else
			htmlParser.join();
			//#endif
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " htmlParser.isSuccessfull()=" + htmlParser.isSuccessfull());}
			//#endif
			RssItunesFeedInfo[] rssfeeds = htmlParser.getFeeds();
			compatibilityHtmlParser.startParsing();
			compatibilityHtmlParser.join();
			if (fineLoggable) {logger.fine(mname + " compatibilityHtmlParser.isSuccessfull()=" + compatibilityHtmlParser.isSuccessfull());}
			RssItunesFeed[] cmpRssFeeds =
				(RssItunesFeed[])compatibilityHtmlParser.getFeeds();
			for (int i = 0; (i < rssfeeds.length) && (i < cmpRssFeeds.length);
					i++) {
				RssItunesFeedInfo feed = rssfeeds[i];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " i,feed 1=" + i + "," + feed.toString());}
				//#endif
				RssItunesFeed cmpfeed = cmpRssFeeds[i];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " i,cmpfeed 1=" + i + "," + cmpfeed.toString());}
				//#endif
				String assertInfo = new String("i,name,url=" + i + "," + feed.getName() + "," +  feed.getUrl());
				assertTrue("Original feed must equal expected feed " + assertInfo, cmpfeed.equals(feed));
				if (((feed.getUrl().indexOf("http://") >= 0) &&
				   (feed.getName().indexOf("Russian") >= 0)) ||
				   (feed.getUrl().indexOf("rss-gnu-utf8.xml") >= 0)) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " skipping http or gnu XML URL i,feed.getName(),feed.getUrl() 2=" + i + "," + feed.getName() + "," + feed.getUrl());}
					//#endif
					continue;
				}
				com.substanceofcode.rssreader.businesslogic.RssFeedParser fparser =
					new com.substanceofcode.rssreader.businesslogic.RssFeedParser(
							(com.substanceofcode.rssreader.businessentities.RssItunesFeed)feed);
				//#ifdef DMIDP20
				fparser.makeObserable(null, true, 10);
				ready = false;
				fparser.getObserverManager().addObserver(this);
				fparser.getParsingThread().start();
				while (!isReady()) {
					synchronized(this) {
						wait(1000L);
					}
				}
				//#else
				fparser.parseRssFeed( false, 10);
				//#endif
				RssItunesFeedInfo nfeed = fparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " i,feed 3=" + i + "," + feed.toString());}
				//#endif
				RssFeedParser cmpFparser = new RssFeedParser(cmpfeed);
				//#ifdef DMIDP20
				cmpFparser.makeObserable(null, true, 10);
				ready = false;
				cmpFparser.getObserverManager().addObserver(this);
				cmpFparser.getParsingThread().start();
				while (!isReady()) {
					synchronized(this) {
						wait(1000L);
					}
				}
				//#else
				cmpFparser.parseRssFeed( false, 10);
				//#endif
				RssItunesFeed ncmpfeed = (RssItunesFeed)cmpFparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " i,ncmpfeed 3=" + i + "," + ncmpfeed.toString());}
				//#endif
				assertTrue("Feed must equal expected feed " + assertInfo, ncmpfeed.equals(nfeed));
				// Free up memory.
				rssfeeds[i] = null;
				// Free up memory.
				cmpRssFeeds[i] = null;

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
