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
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-05-30 1.11.5RC2 Alter alterning of log level for better debugging.
 * IB 2010-05-30 1.11.5RC2 Split up tests for better results.
 * IB 2010-05-30 1.11.5RC2 Check if the successful results match for both current and compatibility.
 * IB 2010-05-30 1.11.5RC2 Allow end of altering based on nextIx.
 * IB 2010-06-29 1.11.5RC2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-09-27 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
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
import net.eiroca.j2me.observable.compatibility4.Observer;
import net.eiroca.j2me.observable.compatibility4.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Level;
//#endif

final public class HtmlLinkParserTest extends BaseTestCase
//#ifdef DMIDP20
implements Observer, net.yinlight.j2me.observable.Observer
//#endif
{

	//#ifdef DMIDP20
	private boolean ready = false;
	//#endif
	private int nextIx = 0;
	//#ifdef DLOGGING
	private boolean alterLogLevel = false;
	private boolean endAlterLogLevel = false;
	private boolean levelAltered = false;
	private int alterix = 0;
	private int endAlterix = 2;
	private String newLogLevel = Level.FINEST.getName();
	private Level svLogLevel = null;
	//#endif

	public HtmlLinkParserTest() {
		super(1, "compatibility4.HtmlLinkParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testHtmlParse1();
				break;
			case 1:
				testHtmlParse2();
				break;
			case 2:
				testHtmlParse3();
				break;
			case 3:
				testHtmlParse4();
				break;
			case 4:
				testHtmlParse5();
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

	public void changed(net.yinlight.j2me.observable.Observable observable, Object arg) {
		ready = true;
	}

	public boolean isReady() {
		return ready;
	}
	//#endif

	/* Test parse HTML. */
	public void testHtmlParse1() throws Throwable {
		String mname = "testHtmlParse1";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				"jar:///links.html", "", "");
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser("jar:///links.html", "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse2() throws Throwable {
		String mname = "testHtmlParse2";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				"jar:///links.html", "", "");
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser("jar:///links.html", "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse3() throws Throwable {
		String mname = "testHtmlParse3";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				"jar:///links.html", "", "");
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser("jar:///links.html", "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse4() throws Throwable {
		String mname = "testHtmlParse4";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				"jar:///links.html", "", "");
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser("jar:///links.html", "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse5() throws Throwable {
		String mname = "testHtmlParse5";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				"jar:///links.html", "", "");
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser("jar:///links.html", "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

    public void compatibilityHtmlLinkParserTestSub(final String mname,
			final com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser,
			final HTMLLinkParser compatibilityHtmlParser, boolean endFeeds)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			if (finestLoggable) {logger.finest(mname + " compatibilityHtmlLinkParserTestSub htmlParser=" + htmlParser);}
			//#endif
			//#ifdef DMIDP20
			ready = false;
			htmlParser.getObservableHandler().addObserver(this);
			//#endif
			htmlParser.startParsing();
			//#ifdef DMIDP20
			while (!isReady()) {
				synchronized(this) {
					wait(1000L);
				}
			}
			//#else
			htmlParser.run();
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
			assertTrue(mname + " rssfeeds feed length should be > 0",
					rssfeeds.length > 0);
			assertTrue(mname + " cmpRssFeeds feed length should be > 0",
					cmpRssFeeds.length > 0);
			int endIx = endFeeds ? rssfeeds.length : (nextIx + (rssfeeds.length / 5));
			for (; (nextIx < endIx) && (nextIx < rssfeeds.length) && (nextIx < cmpRssFeeds.length);
					nextIx++) {
				//#ifdef DLOGGING
				if (alterLogLevel && (nextIx >= alterix) && (alterix >= 0)) {
					endAlterLogLevel = true;
					svLogLevel = logger.getParent().getLevel();
					logger.getParent().setLevel(Level.parse(newLogLevel));
					alterLogLevel = false;
					logger.info(mname + " altering level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
				} else if (endAlterLogLevel && (nextIx >= endAlterix)) {
					endAlterLogLevel = false;
					logger.getParent().setLevel(svLogLevel);
					logger.info(mname + " reverting level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
				}
				//#endif
				RssItunesFeedInfo feed = rssfeeds[nextIx];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,feed 1=" + nextIx + "," + feed.toString());}
				//#endif
				RssItunesFeed cmpfeed = cmpRssFeeds[nextIx];
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.toString());}
				//#endif
				String assertInfo = new String("nextIx,name,url=" + nextIx + "," + feed.getName() + "," +  feed.getUrl());
				assertTrue("Original feed must equal expected feed " + assertInfo, cmpfeed.equals(feed));
				if (((feed.getUrl().indexOf("http://") >= 0) &&
				   (feed.getName().indexOf("Russian") >= 0)) ||
				   (feed.getUrl().indexOf("rss-gnu-utf8.xml") >= 0)) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " skipping http or gnu XML URL nextIx,feed.getName(),feed.getUrl() 2=" + nextIx + "," + feed.getName() + "," + feed.getUrl());}
					//#endif
					continue;
				}
				com.substanceofcode.rssreader.businesslogic.RssFeedParser fparser =
					new com.substanceofcode.rssreader.businesslogic.RssFeedParser(
							(com.substanceofcode.rssreader.businessentities.RssItunesFeed)feed);
				//#ifdef DMIDP20
				fparser.makeObserable(true, 10);
				ready = false;
				fparser.getObservableHandler().addObserver(this);
				fparser.getParsingThread().start();
				while (!isReady()) {
					synchronized(this) {
						wait(1000L);
					}
				}
				boolean successful = fparser.isSuccessfull();
				Throwable fexc = fparser.getEx();
				//#else
				boolean successful = true;
				Throwable fexc = null;
				try {
					fparser.parseRssFeed( false, 10);
				} catch(Throwable e) {
					successful = false;
					fexc = e;
				}
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
				boolean cmpSuccessful = cmpFparser.isSuccessfull();
				Throwable cmpFexc = cmpFparser.getEx();
				//#else
				boolean cmpSuccessful = true;
				Throwable cmpFexc = null;
				try {
					cmpFparser.parseRssFeed( false, 10);
				} catch(Throwable e) {
					cmpSuccessful = false;
					cmpFexc = e;
				}
				//#endif
				assertEquals("compatibilityHtmlLinkParserTestSub isSuccessfull() must equal nextIx=" + nextIx, cmpSuccessful, successful);
				if (!cmpSuccessful) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " compatibilityHtmlLinkParserTestSub not successful nextIx,feed.getName(),fexc,cmpFexc=" + nextIx + "," + feed.getName() + "," + fexc + "," + cmpFexc);}
					//#endif
					continue;
				}
				RssItunesFeedInfo nfeed = fparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,feed 3=" + nextIx + "," + feed.toString());}
				//#endif
				RssItunesFeed ncmpfeed = (RssItunesFeed)cmpFparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,ncmpfeed 3=" + nextIx + "," + ncmpfeed.toString());}
				//#endif
				assertTrue("Feed must equal expected feed " + assertInfo, ncmpfeed.equals(nfeed));
				// Free up memory.
				rssfeeds[nextIx] = null;
				// Free up memory.
				cmpRssFeeds[nextIx] = null;

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
