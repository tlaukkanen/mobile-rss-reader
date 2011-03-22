//--Need to modify--#preprocess
/*
 * HtmlLinkParser2Test.java
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
 * IB 2010-04-26 1.11.5RC2 Set link to "" as it's not read by previous version.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser, HTMLLinkParser, and HtmlLinkParser2Test in small memory MIDP 1.0 to save space.
 * IB 2010-05-28 1.11.5RC2 Check for html, htm, shtml, and shtm suffixes.
 * IB 2010-05-28 1.11.5RC2 Fix last test case to go to the end of the feeds.
 * IB 2010-05-30 1.11.5RC2 Move RssCompFeeds to it's own class file.
 * IB 2010-05-30 1.11.5RC2 Allow endAlterix to specify end of altering logging.
 * IB 2010-05-30 1.11.5RC2 Skip result that shows fix of relative URLs.
 * IB 2010-06-29 1.11.5RC2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Allow test using jar or http links if oneTestOnly is true.
 * IB 2011-01-14 1.11.5Alpha15 Future calculate average time for http access/parsing for current and previous versions for performance testing.
 * IB 2011-01-14 1.11.5Alpha15 Allow retrying the test case with http access if retryModHttp is true.
 * IB 2011-01-14 1.11.5Alpha15 Increase number of tests to 10 to spread out the testing among more steps.  This also allows more tests to be run if one fails (not for errors).
 * IB 2011-01-14 1.11.5Alpha15 If failure is for equals test, increase nextIx to allow the next test to try the next feed instead of retrying the failed feed over and over again.
 * IB 2011-01-14 1.11.5Alpha15 Use convience methods updSvLogging and updPrevLogging from LoggingTestCase to alter/restore the logging level.
 * IB 2011-01-14 1.11.5Alpha15 Use convience method cmpModLog from LoggingTestCase to see if feeds are unequal and change the logging level to retry using logging to make debugging equals failures easier.  Also, retry with modified previous version for bug fixes/enhancements made in the current version.
 * IB 2011-01-14 1.11.5Alpha15 Start at second (index 1) feed to skip sourceforge home link.
 * IB 2011-01-14 1.11.5Alpha15 Allow tracing for parsing or reading chars once or ongoing.
 * IB 2011-01-14 1.11.5Alpha15 Better logging.
 * IB 2011-01-14 1.11.5Alpha15 Use procThrowable from LoggingTestCase.
 * IB 2011-01-17 1.11.5Alpha15 Handle search url with /feed.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
 * IB 2011-01-24 1.11.5Dev16 Fix conditional compile of DLOGGING code.
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

import com.substanceofcode.jmunit.rssreader.businesslogic.RssCompFeeds;
import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.utils.compatibility4.CauseException;
import com.substanceofcode.rssreader.businesslogic.compatibility4.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
import com.substanceofcode.utils.HTMLParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLLinkParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLAutoLinkParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.LineByLineParser;
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.Logger;
//#endif

  /**
   * Set link to "" for old version compare.  NEEDS TO BE CHANGED.
   *
   * @author Irv Bunton
   */
final public class HtmlLinkParser2Test extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	//#ifdef DMIDP20
	private boolean ready = false;
	//#endif

	private boolean oneTestOnly = true;
	private boolean retryModHttp = false;
	private String testUrl = (oneTestOnly ? "jar:///links2.html" :
				"http://mobilerssreader.sourceforge.net/testdata/links2.html");
	private long currTotalTime = 0L;
	private long currTotalCount = 0L;
	private long oldTotalTime = 0L;
	private long oldTotalCount = 0L;
	static private int nbrTests = 10;
	private int nextIx = 1; // Start at 1 to skip sourceforge home.
	private int prevNextIx = nextIx; // Start at 1 to skip sourceforge home.
	//#ifdef DLOGGING
	private boolean logParseChar = traceLoggable; // or traceLoggable
	private boolean logReadChar = traceLoggable; // or traceLoggable
	private boolean logRepeatChar = traceLoggable; // or traceLoggable
	private boolean alterLogLevel = false; // undo or traceLoggable
	private boolean endAlterLogLevel = false;
	private boolean levelAltered = false;
	private int alterix = 23;
	private int endAlterix = 24;
	private String newLogLevel = Level.FINEST.getName();
	//#endif
	private static final String CURRENT_CLASS = "compatibility4.HtmlLinkParser2Test";

	public HtmlLinkParser2Test() {
		super(nbrTests, CURRENT_CLASS);
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

	public boolean isReady() {
		return ready;
	}
	//#endif

	/* Test parse HTML. */
	public void testHtmlParse1() throws Throwable {
		String mname = "testHtmlParse1";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse2() throws Throwable {
		String mname = "testHtmlParse2";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse3() throws Throwable {
		String mname = "testHtmlParse3";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse4() throws Throwable {
		String mname = "testHtmlParse4";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse5() throws Throwable {
		String mname = "testHtmlParse5";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse6() throws Throwable {
		String mname = "testHtmlParse6";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse7() throws Throwable {
		String mname = "testHtmlParse7";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse8() throws Throwable {
		String mname = "testHtmlParse8";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse9() throws Throwable {
		String mname = "testHtmlParse9";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse10() throws Throwable {
		String mname = "testHtmlParse10";
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	private Object[] parseHtml(final String mname,
			String name, String url)
		throws com.substanceofcode.utils.CauseException, Throwable {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " entering parseHtml name,url=" + name + "," + url);}
			//#endif
			try {
				com.substanceofcode.rssreader.businesslogic.FeedListParser feedListParser;
				boolean hasAuto = (name.indexOf("option=htmlautolink") >= 0);
				if (url.endsWith(".txt")) {
					feedListParser =
						new com.substanceofcode.rssreader.businesslogic.LineByLineParser(
								url, "", "", new RssFeedStore());
				} else if (HTMLParser.isHtml(url) || hasAuto) {
					if (hasAuto) {
						feedListParser =
							new com.substanceofcode.rssreader.businesslogic.HTMLAutoLinkParser(
									url, "", "", new RssFeedStore());
					} else {
						feedListParser =
							new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
									url, "", "", new RssFeedStore());
					}
				} else {
					feedListParser =
						new com.substanceofcode.rssreader.businesslogic.OpmlParser(
								url, "", "", new RssFeedStore());
				}
				//#ifdef DLOGGING
				if (logParseChar) {
					feedListParser.setLogChar(logParseChar);
				}
				if (logReadChar) {
					feedListParser.setLogReadChar(logReadChar);
					feedListParser.setLogRepeatChar(logRepeatChar);
				}
				//#endif
				if (name.indexOf("option=missing title") >= 0) {
					feedListParser.setGetTitleOnly(true);
				}
				if (name.indexOf("linksearchurl=rss.xml") >= 0) {
					feedListParser.setFeedURLFilter("rss.xml");
				} else if (name.indexOf("linksearchurl=xml") >= 0) {
					feedListParser.setFeedURLFilter("xml");
				} else if (name.indexOf("linksearchurl=/feed") >= 0) {
					feedListParser.setFeedURLFilter("/feed");
				} else if (name.indexOf("linksearchurl=/xml/rss/nyt/") >= 0) {
					feedListParser.setFeedURLFilter("/xml/rss/nyt/");
				} else if (name.indexOf("linksearchurl=/xml/rss/") >= 0) {
					feedListParser.setFeedURLFilter("/xml/rss/");
				} else if (name.indexOf("linksearchurl=index.rss") >= 0) {
					feedListParser.setFeedURLFilter("index.rss");
				} else if (name.indexOf("linksearchurl=?/rss=") >= 0) {
					feedListParser.setFeedURLFilter("?/rss=");
				} else if (name.indexOf("linksearchurl=/rss") >= 0) {
					feedListParser.setFeedURLFilter("/rss");
				}
				ready = false;
				//#ifdef DMIDP20
				feedListParser.getObservableHandler().addObserver(this);
				//#endif
				feedListParser.startParsing();
				//#ifdef DMIDP20
				while (!isReady()) {
					synchronized(this) {
						wait(500L);
					}
				}
				//#else
				feedListParser.join();
				//#endif
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine(mname + " feedListParser.isSuccessfull()=" + feedListParser.isSuccessfull());}
				//#endif
				if (!feedListParser.isSuccessfull()) {
					throw feedListParser.getEx();
				}
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine(mname + " parseHtml feedListParser=" + feedListParser);}
				//#endif
				return new Object[] {feedListParser.getFeeds(), feedListParser};
			} catch (com.substanceofcode.utils.CauseException e) {
				//#ifdef DLOGGING
				logger.severe(mname + " CauseException failure ",e);
				//#endif
				e.printStackTrace();
				throw e;
			} catch (Throwable e) {
				//#ifdef DLOGGING
				logger.severe(mname + " failure ",e);
				//#endif
				e.printStackTrace();
				throw e;
			}
		}

	private Object[] compatibilityParseHtml(final String mname,
			String name, String url)
	throws CauseException, Throwable {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine(mname + " entering compatibilityParseHtml name,url=" + name + "," + url);}
		//#endif
		try {
			FeedListParser compatibilityFeedListParser;
			boolean hasAuto = (name.indexOf("option=htmlautolink") >= 0);
			if (url.endsWith(".txt")) {
				compatibilityFeedListParser = new LineByLineParser( url, "", "");
			} else if (HTMLParser.isHtml(url) || hasAuto) {
				if (name.indexOf("option=htmlautolink") >= 0) {
					compatibilityFeedListParser =
						new HTMLAutoLinkParser( url, "", "");
				} else {
					compatibilityFeedListParser = new HTMLLinkParser(
							url, "", "");
				}
			} else {
				compatibilityFeedListParser = new OpmlParser( url, "", "");
			}
			if (name.indexOf("option=missing title") >= 0) {
				compatibilityFeedListParser.setGetFeedTitleList(true);
			}
			if (name.indexOf("linksearchurl=rss.xml") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("rss.xml");
			} else if (name.indexOf("linksearchurl=xml") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("xml");
			} else if (name.indexOf("linksearchurl=index.rss") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("index.rss");
			} else if (name.indexOf("linksearchurl=/xml/rss/nyt/") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("/xml/rss/nyt/");
			} else if (name.indexOf("linksearchurl=/xml/rss/") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("/xml/rss/");
			} else if (name.indexOf("linksearchurl=?/rss=") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("?/rss=");
			} else if (name.indexOf("linksearchurl=/rss") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("/rss");
			}
			//#ifdef DMIDP20
			ready = false;
			compatibilityFeedListParser.getObservableHandler().addObserver(this);
			//#endif
			compatibilityFeedListParser.startParsing();
			//#ifdef DMIDP20
			while (!isReady()) {
				synchronized(this) {
					wait(500L);
				}
			}
			//#else
			compatibilityFeedListParser.join();
			//#endif
			if (fineLoggable) {logger.fine(mname + " compatibilityFeedListParser.isSuccessfull()=" + compatibilityFeedListParser.isSuccessfull());}
			if (!compatibilityFeedListParser.isSuccessfull()) {
				throw compatibilityFeedListParser.getEx();
			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " entering compatibilityParseHtml compatibilityFeedListParser=" + compatibilityFeedListParser);}
			//#endif
			return new Object[] {compatibilityFeedListParser.getFeeds(),
				compatibilityFeedListParser};
		} catch (CauseException e) {
			//#ifdef DLOGGING
			logger.severe(mname + " CauseException failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	private RssCompFeeds parseCompFeeds(final String mname,
			final String logText,
			final String assertTextExcEquals,
			final String assertTextNotNull, String name,
			String url)
		throws Throwable {
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer(mname + " " + logText + " name,url=" + name + "," + url);}
			//#endif
			RssItunesFeedInfo[] rssfeeds = null;
			com.substanceofcode.utils.CauseException exc = null;
			Object[] ret = null;
			try {
				ret = parseHtml(mname, name, url);
				rssfeeds = (RssItunesFeedInfo[])ret[0];
			} catch (com.substanceofcode.utils.CauseException e) {
				exc = e;
			}
			CauseException compatibilityExc = null;
			RssItunesFeedInfo[] cmpRssFeeds = null;
			Object[] cmpRet = null;
			try {
				cmpRet = compatibilityParseHtml(mname,
						name, url);
				cmpRssFeeds = (RssItunesFeedInfo[])cmpRet[0];
			} catch (CauseException e) {
				compatibilityExc = e;
			}
			if (compatibilityExc != null) {
				if (exc != null) {
					assertEquals(assertTextExcEquals, compatibilityExc.getMessage(),
							exc.getMessage());
				} else {
					assertEquals(assertTextExcEquals +
							" compatibility not null exc is null",
							compatibilityExc.getMessage(), "");
				}
				return null;
			} else if (exc != null) {
				assertEquals(assertTextExcEquals +
						" compatibility null exc is not null", "",
						exc.getMessage());
				return null;
			}
			assertNotNull("Original " + assertTextNotNull, rssfeeds);
			assertNotNull("Compatibility " + assertTextNotNull, cmpRssFeeds);
			return new RssCompFeeds(rssfeeds, cmpRssFeeds);
		}

	public void compatibilityHtmlLinkParserTestSub(final String mname,
			String name, String url, boolean endFeeds)
	throws Throwable {
		boolean httpFile = url.startsWith("http:");
		boolean goNext = false;
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname + ",activeCount,freeMemory()=" + Thread.activeCount() + "," + Runtime.getRuntime().freeMemory());
			//#endif
			if (!httpFile) {
				prevNextIx = nextIx;
			}
			RssCompFeeds feeds = parseCompFeeds(mname,
					" compatibilityHtmlLinkParserTestSub",
					"parse feeds exception must be equal",
					"parse feeds must not be null", name, url);
			if (feeds == null) {
				return;
			}
			RssItunesFeedInfo[] rssfeeds = feeds.getRssfeeds();
			RssItunesFeedInfo[] cmpRssFeeds = feeds.getCmpRssFeeds();
			assertTrue(mname + " rssfeeds feed length should be > 0",
					rssfeeds.length > 0);
			assertTrue(mname + " cmpRssFeeds feed length should be > 0",
					cmpRssFeeds.length > 0);
			int endIx = endFeeds ? rssfeeds.length : (nextIx + (rssfeeds.length / nbrTests));

			for (; (nextIx < endIx) && (nextIx < rssfeeds.length) && (nextIx < cmpRssFeeds.length);
					nextIx++) {
				try {
					//#ifdef DLOGGING
					if (alterLogLevel && (nextIx >= alterix) && (alterix >= 0)) {
						endAlterLogLevel = true;
						svLogLevel = super.updSvLogging(newLogLevel);
						logger.info(mname + " altering level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
						alterLogLevel = false;
					} else if (endAlterLogLevel && (nextIx > endAlterix)) {
						endAlterLogLevel = false;
						super.updPrevLogging(svLogLevel);
						logger.info(mname + " reverting level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
					}
					//#endif
					RssItunesFeedInfo feed = rssfeeds[nextIx];
					//#ifdef DLOGGING
					if (!finestLoggable) {logger.info(mname + " nextIx,feed 1=" + nextIx + "," + feed.getName() + "," + feed.getUrl());}
					//#endif
					//#ifdef DLOGGING
					if (!finestLoggable && fineLoggable) {logger.fine(mname + " nextIx,feed 1=" + nextIx + "," + feed.getName() + "," + feed.getItems().length);}
					if (finestLoggable) {logger.finest(mname + " nextIx,feed 1=" + nextIx + "," + feed.toString());}
					//#endif
					RssItunesFeedInfo cmpfeed = cmpRssFeeds[nextIx];
					//#ifdef DLOGGING
					if (!finestLoggable) {logger.info(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());}
					//#endif
					//#ifdef DLOGGING
					if (!finestLoggable && fineLoggable) {logger.fine(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getVecItems().size());}
					if (finestLoggable) {logger.finest(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.toString());}
					//#endif
					String assertInfo = new String("nextIx,name,url=" + nextIx + "," + feed.getName() + "," +  feed.getUrl());
					// Workaround
					goNext = true;
					Object[] oret = super.cmpModLog(
							"Original feed must equal expected feed " + assertInfo,
							(RssItunesFeed)cmpfeed, feed, null);
					if (oret[1] != null) {
						throw (Throwable)oret[1];
					}
					goNext = false;
					// Don't do bom 7.
					if ((feed.getName().indexOf("BOM encoding7-utf8-txt.txt") >= 0) ||
							(feed.getName().indexOf("28 HTML Der Spiegel search for ") >= 0) ||
							(feed.getName().indexOf("feature=relative-url") >= 0) ||
							((feed.getUrl().indexOf("http://") >= 0) &&
							 ((feed.getName().indexOf("Russian") >= 0) ||
							  (feed.getName().indexOf("Russian") >= 0) ||
							  (feed.getName().indexOf("http://sourceforge.net") >= 0)))) {
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " skipping http or gnu XML URL nextIx,feed.getUrl() 2=" + nextIx + "," + feed.getUrl());}
						//#endif
						continue;
					}
					com.substanceofcode.utils.CauseException currExc = null;
					RssItunesFeedInfo[] currRssfeeds = null;
					Object[] currRet = null;
					try {
						currRet = parseHtml(mname, feed.getName(),
								feed.getUrl());
						currRssfeeds = (RssItunesFeedInfo[])currRet[0];
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " nextIx currRssfeeds.length=" + nextIx + "," + currRssfeeds.length);}
						//#endif
					} catch (com.substanceofcode.utils.CauseException e) {
						currExc = e;
					}
					CauseException currCmpExc = null;
					RssItunesFeedInfo[] currCmpRssfeeds = null;
					Object[] currCmpRet = null;
					try {
						currCmpRet = compatibilityParseHtml(
								mname, feed.getName(), feed.getUrl());
						currCmpRssfeeds = (RssItunesFeedInfo[])currCmpRet[0];
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " nextIx currCmpRssfeeds.length=" + nextIx + "," + currCmpRssfeeds.length);}
						//#endif
					} catch (CauseException e) {
						currCmpExc = e;
					}
					//#ifdef DLOGGING
					if (fineLoggable && !finestLoggable) {logger.fine(mname + " nextIx,currCmpExc,currExc" + nextIx + "," + currCmpExc + "," + currExc);} ;
					//#endif
					if (currExc == null)  {
						assertNotNull("Original sub feeds must not be null " + assertInfo, currRssfeeds);
					}
					if (currCmpExc == null) { 
						assertNotNull("Compatibility sub feeds must not be null " + assertInfo, currCmpRssfeeds);
					}
					if ((currExc != null) && (currCmpExc == null) ||
							((currExc == null) && (currCmpExc != null))) {
						if ((currExc != null) && (currCmpExc == null) &&
								(currCmpRssfeeds.length == 0)) {
							// Workaround
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine(mname + " skipping 0 feeds nextIx,feed.getName()=" + nextIx + "," + feed.getName());} ;
							//#endif
							continue;
						}
						// Don't get hung up on the same error.
						nextIx++;
						assertTrue(mname +
								" feed currExceptions should be for both compatibility and current " + assertInfo,
								(currCmpExc != null) && (currExc != null));
					}
					for (int j = 0;
							(j < currRssfeeds.length) && (j < currCmpRssfeeds.length);
							j++) {
						RssItunesFeedInfo subFeed = currRssfeeds[j];
						//#ifdef DLOGGING
						if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subFeed 1=" + j + "," + subFeed.getName() + "," + subFeed.getUrl() + "," + subFeed.getItems().length);}
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " j,subFeed 1=" + j + "," + subFeed.toString());}
						//#endif
						RssItunesFeedInfo subCmpfeed = currCmpRssfeeds[j];
						String assertInfoSub = new String("nextIx,j,name,url=" + nextIx + "," + j + "," + feed.getName() + "," +  feed.getUrl() + ",[" + subFeed.getName() + "," + subCmpfeed.getName() + "]," +  subFeed.getUrl());
						assertNotNull("Compatibility sub feeds must not be null " + assertInfoSub, subCmpfeed);
						//#ifdef DLOGGING
						if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.getName() + "," + subCmpfeed.getUrl() + "," + subCmpfeed.getVecItems().size());}
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.toString());}
						//#endif
						subFeed.setLink("");
						if ((nextIx == 26) &&
								feed.getUrl().equals("http://www.nytimes.com/services/xml/rss/index.html")) {
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine(mname + " skipping fix for relative URLs for nextIx,subFeed.getUrl()=" + nextIx + "," + subFeed.getUrl());}
							//#endif
							continue;
						}
						// Fixed problem where name was null.  We NEVER want null.
						if ((subFeed.getName().length() == 0) &&
								(subCmpfeed.getName() == null)) {
							// workaround
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine(mname + " fixing null name j,subCmpfeed.getName(),subFeed.getName()=" + j + "," + subCmpfeed.getName() + "," + subFeed.getName());} ;
							//#endif
							subCmpfeed.setName("");
						}
						goNext = true;
						Object[] oret2 = super.cmpModLog(
								"Original sub feed must equal expected sub feed " + assertInfoSub,
								(RssItunesFeed)subCmpfeed, subFeed,
							new Object[] {currCmpRet, currRet});
						if (oret2[1] != null) {
							throw (Throwable)oret2[1];
						}
						goNext = false;
					}
				} finally {
					// Free up memory.
					rssfeeds[nextIx] = null;
					// Free up memory.
					cmpRssFeeds[nextIx] = null;
				}

			}
			if (endFeeds) {
				assertEquals(mname + " feed lengths should be equal",
						cmpRssFeeds.length, rssfeeds.length);
			}
			if (!httpFile && oneTestOnly && retryModHttp) {
				compatibilityHtmlLinkParserTestSub(mname, name,
					"http://mobilerssreader.sourceforge.net/testdata/links2.html",
						endFeeds);
				nextIx = prevNextIx;
			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " finished.");}
			//#endif
		} catch (Throwable e) {
			if (goNext) {
				nextIx++;
			}
			super.procThrowable(mname, e);
		}
	}

}
//#endif
//#endif
