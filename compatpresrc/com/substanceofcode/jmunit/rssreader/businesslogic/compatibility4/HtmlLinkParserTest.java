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
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Allow test using jar or http links if oneTestOnly is true.
 * IB 2011-01-14 1.11.5Alpha15 Calculate average time for http access/parsing for current and previous versions for performance testing.
 * IB 2011-01-14 1.11.5Alpha15 Allow retrying the test case with http access if retryModHttp is true.
 * IB 2011-01-14 1.11.5Alpha15 Increase number of tests to 10 to spread out the testing among more steps.  This also allows more tests to be run if one fails (not for errors).
 * IB 2011-01-14 1.11.5Alpha15 If failure is for equals test, increase nextIx to allow the next test to try the next feed instead of retrying the failed feed over and over again.
 * IB 2011-01-14 1.11.5Alpha15 Use convience methods updSvLogging and updPrevLogging from LoggingTestCase to alter/restore the logging level.
 * IB 2011-01-14 1.11.5Alpha15 Use convience method cmpModLog from LoggingTestCase to see if feeds are unequal and change the logging level to retry using logging to make debugging equals failures easier.  Also, retry with modified previous version for bug fixes/enhancements made in the current version.
 * IB 2011-01-14 1.11.5Alpha15 Start at second (index 1) feed to skip sourceforge home link.
 * IB 2011-01-14 1.11.5Alpha15 Allow tracing for parsing or reading chars once or ongoing.
 * IB 2011-01-14 1.11.5Alpha15 Better logging.
 * IB 2011-01-22 1.11.5Dev16 Only retry if we are doing oneTestOnly.
 * IB 2011-01-22 1.11.5Dev16 Start with first link for sourceforge which while invalid, will cause prompt for internet access.  When taking http stats, don't count the first (sourceforge) link so that time to answer prompt is not counted as access time.
 * IB 2011-01-22 1.11.5Dev16 Only take stats if both parsers are successful at parsing.  This prevents bad protocol (e.g. not http) links from giving bad stats.
 * IB 2011-01-22 1.11.5Dev16 If not logging, use stdout to list stats.
 * IB 2011-03-08 1.11.5Dev17 Increase number of tests to 15.
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

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.rssreader.businesslogic.compatibility4.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
//#ifndef DSMALLMEM
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLLinkParser;
//#endif
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Level;
//#endif

final public class HtmlLinkParserTest extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	//#ifdef DMIDP20
	private boolean ready = false;
	//#endif

	private boolean oneTestOnly = true;
	private boolean retryModHttp = oneTestOnly && false; // UNDO
	private String testUrl = (oneTestOnly ? "jar:///links.html" :
				"http://mobilerssreader.sourceforge.net/testdata/links.html");
	private long currTotalTime = 0L;
	private long currTotalCount = 0L;
	private long oldTotalTime = 0L;
	private long oldTotalCount = 0L;
	private int nextIx = 0; // Start at 0 to get interenet prompt for sourceforge home to skip it's stats.
	private int prevNextIx = nextIx; // undo 0; // Start at 0 to get interenet prompt for sourceforge home to skip it's stats.
	static private int nbrTests = 15;
	//#ifdef DLOGGING
	private boolean alterLogLevel = traceLoggable;
	private boolean endAlterLogLevel = false;
	private boolean levelAltered = false;
	private int alterix = 49;
	private int endAlterix = 50;
	private String newLogLevel = Level.FINEST.getName(); // UNDO
	//#endif

	public HtmlLinkParserTest() {
		super(nbrTests, "compatibility4.HtmlLinkParserTest");
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
			case 5:
				testHtmlParse6();
				break;
			case 6:
				testHtmlParse7();
				break;
			case 7:
				testHtmlParse8();
				break;
			case 8:
				testHtmlParse9();
				break;
			case 9:
				testHtmlParse10();
				break;
			case 10:
				testHtmlParse11();
				break;
			case 11:
				testHtmlParse12();
				break;
			case 12:
				testHtmlParse13();
				break;
			case 13:
				testHtmlParse14();
				break;
			case 14:
				testHtmlParse15();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	//#ifdef DMIDP20
	public void changed(Observable observable, Object arg) {
		synchronized(this) {
			ready = true;
			super.notifyAll();
		}
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
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse2() throws Throwable {
		String mname = "testHtmlParse2";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse3() throws Throwable {
		String mname = "testHtmlParse3";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse4() throws Throwable {
		String mname = "testHtmlParse4";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse5() throws Throwable {
		String mname = "testHtmlParse5";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse6() throws Throwable {
		String mname = "testHtmlParse6";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse7() throws Throwable {
		String mname = "testHtmlParse7";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse8() throws Throwable {
		String mname = "testHtmlParse8";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse9() throws Throwable {
		String mname = "testHtmlParse9";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse10() throws Throwable {
		String mname = "testHtmlParse10";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse11() throws Throwable {
		String mname = "testHtmlParse11";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse12() throws Throwable {
		String mname = "testHtmlParse12";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse13() throws Throwable {
		String mname = "testHtmlParse13";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse14() throws Throwable {
		String mname = "testHtmlParse14";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse15() throws Throwable {
		String mname = "testHtmlParse15";
		com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser =
			new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser =
			new HTMLLinkParser(testUrl, "", "");

		compatibilityHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

    public void compatibilityHtmlLinkParserTestSub(final String pmname,
			com.substanceofcode.rssreader.businesslogic.HTMLLinkParser htmlParser,
			HTMLLinkParser compatibilityHtmlParser, boolean endFeeds)
	throws Throwable {
		boolean httpFile = htmlParser.getUrl().startsWith("http:");
		String mname = pmname + "," + nextIx + "," + htmlParser.getUrl() + "," + httpFile;
		boolean goNext = false;
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname + ",activeCount,freeMemory()=" + Thread.activeCount() + "," + Runtime.getRuntime().freeMemory());
			if (finestLoggable) {logger.finest(mname + " compatibilityHtmlLinkParserTestSub htmlParser=" + htmlParser);} ;
			if (httpFile && (nextIx >= 1)) {
				//#ifdef DLOGGING
				logger.info(mname + " nextIx,currTotalTime,currTotalCount(prev)=" + nextIx + "," + currTotalTime + "," + currTotalCount);
				if (currTotalCount > 0) {
					logger.info(mname + " currTotalTime/currTotalCount(prev)=" + currTotalTime/currTotalCount);
				}
				logger.info(mname + " nextIx,oldTotalTime,oldTotalCount=" + nextIx + "," + oldTotalTime + "," + oldTotalCount);
				if (oldTotalCount > 0) {
					logger.info(mname + " oldTotalTime/oldTotalCount=" + oldTotalTime/oldTotalCount);
				}
				//#endif
			} else {
				prevNextIx = nextIx;
			}
			//#endif
			long startTime = 0L;
			if (httpFile && (nextIx >= 1)) {
				System.gc();
				startTime = System.currentTimeMillis();
			}
			ready = false;
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
			htmlParser.run();
			//#endif
			if (httpFile && (nextIx >= 1) && htmlParser.isSuccessfull()) {
				currTotalTime += (System.currentTimeMillis() - startTime);
				currTotalCount++;
				System.gc();
			}
			//#ifdef DMIDP20
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " htmlParser.isSuccessfull()=" + htmlParser.isSuccessfull());} ;
			//#endif
			RssItunesFeedInfo[] rssfeeds = htmlParser.getFeeds();
			if (httpFile && (nextIx >= 1)) {
				System.gc();
				startTime = System.currentTimeMillis();
			}
			compatibilityHtmlParser.startParsing();
			compatibilityHtmlParser.join();
			if (httpFile && (nextIx >= 1) && htmlParser.isSuccessfull() &&
					compatibilityHtmlParser.isSuccessfull()) {
				oldTotalTime += (System.currentTimeMillis() - startTime);
				oldTotalCount++;
				System.gc();
			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " compatibilityHtmlParser.isSuccessfull()=" + compatibilityHtmlParser.isSuccessfull());} ;
			//#endif
			RssItunesFeed[] cmpRssFeeds =
				(RssItunesFeed[])compatibilityHtmlParser.getFeeds();
			if (htmlParser.isSuccessfull()) {
				assertTrue(mname + " rssfeeds feed length should be > 0",
						rssfeeds.length > 0);
			} else {
				throw htmlParser.getEx();
			}
			if (compatibilityHtmlParser.isSuccessfull()) {
				assertTrue(mname + " cmpRssFeeds feed length should be > 0",
						cmpRssFeeds.length > 0);
			} else {
				throw compatibilityHtmlParser.getEx();
			}
			int endIx = endFeeds ? rssfeeds.length : (nextIx + (rssfeeds.length / nbrTests));
			for (; (nextIx < endIx) && (nextIx < rssfeeds.length) && (nextIx < cmpRssFeeds.length);
					nextIx++) {
				//#ifdef DLOGGING
				if (alterLogLevel && (nextIx >= alterix) && (alterix >= 0)) {
					endAlterLogLevel = true;
					svLogLevel = super.updSvLogging(newLogLevel);
					alterLogLevel = false;
					logger.info(mname + " altering level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
				} else if (endAlterLogLevel && (nextIx > endAlterix)) {
					endAlterLogLevel = false;
					super.updPrevLogging(svLogLevel);
					logger.info(mname + " reverting level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
				}
				//#endif
				RssItunesFeedInfo feed = rssfeeds[nextIx];
				//#ifdef DLOGGING
				if (!finestLoggable) {logger.info(mname + " nextIx,feed 1=" + nextIx + "," + feed.getName() + "," + feed.getUrl());} ;
				if (finestLoggable) {logger.finest(mname + " nextIx,feed 1=" + nextIx + "," +  feed.getName() + "," + feed.getUrl());} ;
				//#endif
				RssItunesFeed cmpfeed = cmpRssFeeds[nextIx];
				//#ifdef DLOGGING
				if (!finestLoggable) {logger.info(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.getName());} ;
				if (finestLoggable) {logger.finest(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());} ;
				//#endif
				String assertInfo = new String("nextIx,name,url=" + nextIx + "," + feed.getName() + "," +  feed.getUrl());
				goNext = true;
				assertTrue("Original feed must equal expected feed " + assertInfo, cmpfeed.equals(feed));
				goNext = false;
				if (((feed.getUrl().indexOf("http://") >= 0) &&
				   (feed.getName().indexOf("Russian") >= 0)) ||
				   (feed.getUrl().indexOf("rss-gnu-utf8.xml") >= 0)) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " skipping http or gnu XML URL nextIx,feed.getName(),feed.getUrl() 2=" + nextIx + "," + feed.getName() + "," + feed.getUrl());} ;
					//#endif
					continue;
				}
				startTime = System.currentTimeMillis();
				com.substanceofcode.rssreader.businesslogic.RssFeedParser fparser =
					new com.substanceofcode.rssreader.businesslogic.RssFeedParser(
							(com.substanceofcode.rssreader.businessentities.RssItunesFeed)feed,
							null, false);
				boolean subHttpFile = htmlParser.getUrl().startsWith("http:");
				if (subHttpFile) {
					System.gc();
					startTime = System.currentTimeMillis();
				}
				//#ifdef DMIDP20
				ready = false;
				fparser.makeObserable(true, 10);
				fparser.getObservableHandler().addObserver(this);
				fparser.getParsingThread().start();
				while (!isReady()) {
					synchronized(this) {
						super.wait(500L);
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
				if (subHttpFile && successful) {
					currTotalTime += (System.currentTimeMillis() - startTime);
					currTotalCount++;
				}
				RssFeedParser cmpFparser = new RssFeedParser(cmpfeed);
				startTime = System.currentTimeMillis();
				//#ifdef DMIDP20
				cmpFparser.makeObserable(null, true, 10);
				ready = false;
				cmpFparser.getObservableHandler().addObserver(this);
				cmpFparser.getParsingThread().start();
				while (!isReady()) {
					synchronized(this) {
						super.wait(500L);
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
				if (subHttpFile && cmpSuccessful) {
					oldTotalTime += (System.currentTimeMillis() - startTime);
					oldTotalCount++;
				}
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine(mname + " nextIxa,feed.getName(),cmpFexc,fexc=" + nextIx + "," + feed.getName() + "," + cmpFexc + "," + fexc);} ;
				if (fineLoggable) {logger.fine(mname + " nextIx,feed.getName(),cmpSuccessful,successful=" + nextIx + "," + feed.getName() + "," + cmpSuccessful + "," + successful);} ;
				//#endif
				if ((cmpfeed.getUrl().indexOf(
							"http://www.internet-nexus.com/rss.xml") >= 0) ||
				   (cmpfeed.getUrl().indexOf(
					"http://rss.immowelt.de/getfeed.aspx?GesuchGuid=5929B08E8B6C479E87C79CC93D4F80A4") >= 0)) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " skipping starts with link or meta or if no items still need to get feed link. nextIx,cmpfeed.getName(),cmpfeed.getUrl() =" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());} ;
					//#endif
					continue;
				}
				assertEquals("compatibilityHtmlLinkParserTestSub isSuccessfull() must equal nextIx=" + nextIx, cmpSuccessful, successful);
				if (!cmpSuccessful) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " compatibilityHtmlLinkParserTestSub not successful nextIx,feed.getName(),fexc,cmpFexc=" + nextIx + "," + feed.getName() + "," + fexc + "," + cmpFexc);} ;
					//#endif
					continue;
				}
				RssItunesFeedInfo nfeed = fparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,feed 3=" + nextIx + "," + feed.getName() + "," + feed.getUrl());} ;
				//#endif
				RssItunesFeed ncmpfeed = (RssItunesFeed)cmpFparser.getRssFeed();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " nextIx,ncmpfeed 3=" + nextIx + "," + ncmpfeed.getName() + "," + ncmpfeed.getUrl());} ;
				//#endif
				// Workaround
				if ((feed.getUrl().indexOf("rss-bom-utf8.xml") >= 0) ||
				    (feed.getUrl().indexOf("rss-no-link-bbc-iso8859-1.xml") >= 0) ||
				    (feed.getUrl().indexOf("rss-bbc-iso8859-1.xml") >= 0)) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " skipping date has value other does not nextIx,feed.getName(),feed.getUrl() 2=" + nextIx + "," + feed.getName() + "," + feed.getUrl());} ;
					//#endif
					continue;
				}
				goNext = true;
				// Workaround
				Object[] oret = super.cmpModLog(
						"Feed must equal expected feed " + assertInfo,
						ncmpfeed, nfeed, null);
				if (oret[1] != null) {
					throw (Throwable)oret[1];
				}
				goNext = false;
				// Free up memory.
				rssfeeds[nextIx] = null;
				// Free up memory.
				cmpRssFeeds[nextIx] = null;

			}
			if (!httpFile && oneTestOnly && retryModHttp) {
				htmlParser =
					new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
						"http://mobilerssreader.sourceforge.net/testdata/links.html",
						"", "", new RssFeedStore());
				nextIx = prevNextIx;
				compatibilityHtmlParser = new HTMLLinkParser(
						htmlParser.getUrl(), "", "");
				compatibilityHtmlLinkParserTestSub(pmname, htmlParser,
						compatibilityHtmlParser, endFeeds);
			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " finished.");} ;
			//#endif
		} catch (Throwable e) {
			if (goNext) {
				nextIx++;
			}
			procThrowable(mname, e);
		} finally {
			if (httpFile) {
				//#ifdef DLOGGING
				logger.info(mname + " nextIx,currTotalTime,currTotalCount=" + nextIx + "," + currTotalTime + "," + currTotalCount);
				//#endif
				if (currTotalCount > 0) {
					//#ifdef DLOGGING
					logger.info
					//#else
					System.out.println
					//#endif
						(mname + " currTotalTime/currTotalCount=" + currTotalTime/currTotalCount);
				}
				//#ifdef DLOGGING
				logger.info(mname + " nextIx,oldTotalTime,oldTotalCount=" + nextIx + "," + oldTotalTime + "," + oldTotalCount);
				//#endif
				if (oldTotalCount > 0) {
					//#ifdef DLOGGING
					logger.info
					//#else
					System.out.println
					//#endif
						(mname + " oldTotalTime/oldTotalCount=" + oldTotalTime/oldTotalCount);
				}
			}
		}
	}

}
//#endif
//#endif
