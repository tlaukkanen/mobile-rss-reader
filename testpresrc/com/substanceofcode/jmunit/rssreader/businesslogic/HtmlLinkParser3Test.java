//--Need to modify--#preprocess
/*
 * HtmlLinkParser3Test.java
 *
 * Copyright (C) 2011 Irving Bunton
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
 * IB 2011-03-09 1.11.5Dev17 Test parsing feed list and parsing feeds at the same time.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define itunes define
@DSMARTPHONEDEF@
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
package com.substanceofcode.jmunit.rssreader.businesslogic;

import java.util.Date;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.utils.HTMLParser;
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
import com.substanceofcode.rssreader.businesslogic.HTMLAutoLinkParser;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
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
final public class HtmlLinkParser3Test extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	//#ifdef DMIDP20
	private boolean ready = false;
	//#endif

	private boolean oneTestOnly = true;
	private boolean retryModHttp = oneTestOnly && false;
	private String testUrl = (oneTestOnly ? "jar:///links2.html" :
				"http://mobilerssreader.sourceforge.net/testdata/links2.html");
	private long currTotalTime = 0L;
	private long currTotalCount = 0L;
	private long oldTotalTime = 0L;
	private long oldTotalCount = 0L;
	static private int nbrTests = 15;
	private int nextIx = 1; // Start at 1 to skip sourceforge home.
	private int prevNextIx = 1; // Start at 1 to skip sourceforge home.
	//#ifdef DLOGGING
	private boolean logParseChar = traceLoggable; // or traceLoggable
	private boolean logReadChar = traceLoggable; // or traceLoggable
	private boolean logRepeatChar = traceLoggable; // or traceLoggable
	private boolean alterLogLevel = false; // or traceLoggable
	private boolean endAlterLogLevel = false;
	private boolean levelAltered = false;
	private int alterix = 25;
	private int endAlterix = 26;
	private int alterj = -1;
	private int endAlterj = 2;
	private String newLogLevel = Level.FINEST.getName();
	//#endif
	private static final String CURRENT_CLASS = "HtmlLinkParser3Test";

	public HtmlLinkParser3Test() {
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
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse2() throws Throwable {
		String mname = "testHtmlParse2";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse3() throws Throwable {
		String mname = "testHtmlParse3";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse4() throws Throwable {
		String mname = "testHtmlParse4";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse5() throws Throwable {
		String mname = "testHtmlParse5";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse6() throws Throwable {
		String mname = "testHtmlParse6";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse7() throws Throwable {
		String mname = "testHtmlParse7";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse8() throws Throwable {
		String mname = "testHtmlParse8";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse9() throws Throwable {
		String mname = "testHtmlParse9";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse10() throws Throwable {
		String mname = "testHtmlParse10";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse11() throws Throwable {
		String mname = "testHtmlParse11";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse12() throws Throwable {
		String mname = "testHtmlParse12";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse13() throws Throwable {
		String mname = "testHtmlParse13";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse14() throws Throwable {
		String mname = "testHtmlParse14";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	/* Test parse HTML. */
	public void testHtmlParse15() throws Throwable {
		String mname = "testHtmlParse15";
		cmpHtmlLinkParserTestSub(mname, "links2.html", testUrl, true);
	}

	private RssItunesFeedInfo[] parseHtml(final String mname,
			String name, String url, boolean loadAll)
		throws CauseException, Throwable {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " entering parseHtml name,url=" + name + "," + url);}
			//#endif
			FeedListParser feedListParser;
			try {
				boolean hasAuto = (name.indexOf("option=htmlautolink") >= 0);
				if (url.endsWith(".txt")) {
					feedListParser =
						new LineByLineParser(
								url, "", "", new RssFeedStore());
				} else if (HTMLParser.isHtml(url) || hasAuto) {
					if (hasAuto) {
						feedListParser =
							new HTMLAutoLinkParser(
									url, "", "", new RssFeedStore());
					} else {
						feedListParser =
							new HTMLLinkParser(
									url, "", "", new RssFeedStore());
					}
				} else {
					feedListParser =
						new OpmlParser(
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
				if (loadAll) {
					feedListParser.setGetAllFeedList(true);
				}
				if (name.indexOf("option=missing title") >= 0) {
					if (loadAll) {
						feedListParser.setGetFeedTitleList(true);
					} else {
						feedListParser.setGetTitleOnly(true);
					}
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
				} else if (name.indexOf("linksearchurl=/?rss=") >= 0) {
					feedListParser.setFeedURLFilter("/?rss=");
				} else if (name.indexOf("linksearchurl=/rss") >= 0) {
					feedListParser.setFeedURLFilter("/rss");
				}
				//#ifdef DMIDP20
				ready = false;
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
				return feedListParser.getFeeds();
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
			} finally {
				feedListParser = null;
			}
		}

	/*
	private RssItunesFeedInfo[] cmpParseHtml(final String mname,
			String name, String url)
		throws CauseException, Throwable {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " entering cmpParseHtml name,url=" + name + "," + url);}
			//#endif
			try {
				FeedListParser cmpFeedListParser;
				boolean hasAuto = (name.indexOf("option=htmlautolink") >= 0);
				if (url.endsWith(".txt")) {
					cmpFeedListParser = new LineByLineParser(
							url, "", "", new RssFeedStore());
				} else if (HTMLParser.isHtml(url) || hasAuto) {
					if (name.indexOf("option=htmlautolink") >= 0) {
						cmpFeedListParser =
							new HTMLAutoLinkParser(
									url, "", "", new RssFeedStore());
					} else {
						cmpFeedListParser = new HTMLLinkParser(
								url, "", "", new RssFeedStore());
					}
				} else {
					cmpFeedListParser = new OpmlParser(
							url, "", "", new RssFeedStore());
				}
				if (name.indexOf("option=missing title") >= 0) {
					cmpFeedListParser.setGetTitleOnly(true);
				}
				if (name.indexOf("linksearchurl=rss.xml") >= 0) {
					cmpFeedListParser.setFeedURLFilter("rss.xml");
				} else if (name.indexOf("linksearchurl=xml") >= 0) {
					cmpFeedListParser.setFeedURLFilter("xml");
				} else if (name.indexOf("linksearchurl=/xml/rss/nyt/") >= 0) {
					cmpFeedListParser.setFeedURLFilter("/xml/rss/nyt/");
				} else if (name.indexOf("linksearchurl=/xml/rss/") >= 0) {
					cmpFeedListParser.setFeedURLFilter("/xml/rss/");
				} else if (name.indexOf("linksearchurl=index.rss") >= 0) {
					cmpFeedListParser.setFeedURLFilter("index.rss");
				} else if (name.indexOf("linksearchurl=?/rss=") >= 0) {
					cmpFeedListParser.setFeedURLFilter("?/rss=");
				} else if (name.indexOf("linksearchurl=/rss") >= 0) {
					cmpFeedListParser.setFeedURLFilter("/rss");
				}
				ready = false;
				cmpFeedListParser.startParsing();
				while (!cmpFeedListParser.isSuccessfull() &&
						(cmpFeedListParser.getEx() == null)) {
					synchronized(this) {
						Thread.yield();
						Thread.sleep(5000L);
					}
				}
				cmpFeedListParser.join();
				if (fineLoggable) {logger.fine(mname + " cmpFeedListParser.isSuccessfull()=" + cmpFeedListParser.isSuccessfull());}
				if (!cmpFeedListParser.isSuccessfull()) {
					throw cmpFeedListParser.getEx();
				}
				return cmpFeedListParser.getFeeds();
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
		*/

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
			CauseException exc = null;
			try {
				rssfeeds = parseHtml(mname, name, url, false);
			} catch (CauseException e) {
				exc = e;
			}
			CauseException cmpExc = null;
			RssItunesFeedInfo[] cmpRssFeeds = null;
			try {
				cmpRssFeeds = parseHtml(mname, name, url, false);
			} catch (CauseException e) {
				cmpExc = e;
			}
			if (cmpExc != null) {
				if (exc != null) {
					assertEquals(assertTextExcEquals, cmpExc.getMessage(),
							exc.getMessage());
				} else {
					assertEquals(assertTextExcEquals +
							" comparison not null exc is null",
							cmpExc.getMessage(), "");
				}
				return null;
			} else if (exc != null) {
				assertEquals(assertTextExcEquals +
						" comparison null exc is not null", "",
						exc.getMessage());
				return null;
			}
			assertNotNull("Original " + assertTextNotNull, rssfeeds);
			assertNotNull("Comparison " + assertTextNotNull, cmpRssFeeds);
			return new RssCompFeeds(rssfeeds, cmpRssFeeds);
		}

	public void cmpHtmlLinkParserTestSub(final String mname,
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
					" cmpHtmlLinkParserTestSub",
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
					if (alterLogLevel && (nextIx >= alterix) &&
							(alterix >= 0) && (alterj < 0)) {
						endAlterLogLevel = true;
						svLogLevel = super.updSvLogging(newLogLevel);
						logger.info(mname + " altering level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
						alterLogLevel = false;
					} else if (endAlterLogLevel && (nextIx > endAlterix) &&
							(alterj < 0)) {
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
					if (finestLoggable) {logger.finest(mname + " nextIx,feed 1=" + nextIx + "," + feed.toString());}
					//#endif
					RssItunesFeedInfo cmpfeed = cmpRssFeeds[nextIx];
					//#ifdef DLOGGING
					if (!finestLoggable) {logger.info(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());}
					//#endif
					//#ifdef DLOGGING
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
					if ((feed.getName().indexOf("28 HTML Der Spiegel search for ") >= 0) ||
							((feed.getUrl().indexOf("http://") >= 0) &&
							 ((feed.getName().indexOf("Russian") >= 0) ||
							  (feed.getName().indexOf("http://sourceforge.net") >= 0)))) {
						//#ifdef DLOGGING
						if (fineLoggable) {logger.fine(mname + " skipping http or gnu XML URL nextIx,feed.getUrl() 2=" + nextIx + "," + feed.getUrl());}
						//#endif
						continue;
					}
					CauseException currExc = null;
					RssItunesFeedInfo[] currRssfeeds = null;
					try {
						currRssfeeds = parseHtml(mname, feed.getName(),
								feed.getUrl(), true);
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " nextIx currRssfeeds.length=" + nextIx + "," + currRssfeeds.length);}
						//#endif
					} catch (CauseException e) {
						currExc = e;
					}
					CauseException currCmpExc = null;
					RssItunesFeedInfo[] currCmpRssfeeds = null;
					try {
						currCmpRssfeeds = parseHtml(
								mname, feed.getName(), feed.getUrl(), false);
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
						assertNotNull("Comparison sub feeds must not be null " + assertInfo, currCmpRssfeeds);
					}
					if ((currExc != null) || (currCmpExc != null)) {
						// Don't get hung up on the same error.
						nextIx++;
						assertTrue(mname +
								" feed currExceptions should be for both comparison and current " + assertInfo,
								(currCmpExc != null) && (currExc != null));
						assertTrue(mname +
								" feed currExceptions should be equal for both comparison and current " + assertInfo,
								currCmpExc.equals(currExc));
						continue;
					}
					for (int j = 0;
							(j < currRssfeeds.length) && (j < currCmpRssfeeds.length);
							j++) {
						try {
							//#ifdef DLOGGING
							if (alterLogLevel && (nextIx >= alterix) &&
									(alterix >= 0) && (alterj >= 0) &&
									(j >= alterj)) {
								endAlterLogLevel = true;
								svLogLevel = super.updSvLogging(newLogLevel);
								logger.info(mname + " altering level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
								alterLogLevel = false;
							} else if (endAlterLogLevel && (nextIx > endAlterix) &&
									(j > endAlterj)) {
								endAlterLogLevel = false;
								super.updPrevLogging(svLogLevel);
								logger.info(mname + " reverting level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
							}
							//#endif
							RssItunesFeedInfo subFeed = currRssfeeds[j];
							//#ifdef DLOGGING
							if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subFeed 1=" + j + "," + subFeed.getName() + "," + subFeed.getUrl());}
							//#endif
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest(mname + " j,subFeed 1=" + j + "," + subFeed.toString());}
							//#endif
							RssItunesFeedInfo subCmpfeed = currCmpRssfeeds[j];
							String assertInfoSub = new String("nextIx,j,name,url=" + nextIx + "," + j + "," + feed.getName() + "," +  feed.getUrl() + ",[" + subFeed.getName() + "," + subCmpfeed.getName() + "]," +  subFeed.getUrl());
							assertNotNull("Comparison sub feeds must not be null " + assertInfoSub, subCmpfeed);
							//#ifdef DLOGGING
							if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.getName() + "," + subCmpfeed.getUrl());}
							//#endif
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.toString());}
							//#endif
							RssFeedParser cmpFparser = new RssFeedParser(
									(RssItunesFeed)subCmpfeed, null, false);
							//#ifdef DMIDP20
							cmpFparser.makeObserable(true, 10);
							ready = false;
							cmpFparser.getObservableHandler().addObserver(this);
							cmpFparser.getParsingThread().start();
							while (!isReady()) {
								synchronized(this) {
									super.wait(500L);
								}
							}
							boolean cmpSuccessful = cmpFparser.isSuccessfull();
							Throwable subCmpFexc = cmpFparser.getEx();
							//#else
							boolean cmpSuccessful = true;
							Throwable subCmpFexc = null;
							try {
								cmpFparser.parseRssFeed( false, 10);
							} catch(Throwable e) {
								cmpSuccessful = false;
								subCmpFexc = e;
							}
							//#endif
							subCmpfeed = cmpFparser.getRssFeed();
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine(mname + " nextIx,j,subCmpfeed.getName(),subCmpfeed.getUrl(),subCmpFexc=" + nextIx + "," + j + "," + subCmpfeed.getName() + "," + subCmpfeed.getUrl() + "," + subCmpFexc);} ;
							if (fineLoggable) {logger.fine(mname + " nextIx,j,subCmpfeed.getName(),cmpSuccessful=" + nextIx + "," + j + "," + subCmpfeed.getName() + "," + cmpSuccessful);} ;
							//#endif
							if ((subFeed.getName().indexOf("Rotten Tomatoes:")
										>= 0) ||
									(subFeed.getUrl().indexOf(
															  "www.nytimes.com/services/xml/rss/nyt/BestSellers.xml")
									 >= 0) ||
									(subFeed.getUrl().indexOf(
															  "soccernet.espn.go.com/rss/news")
									 >= 0) ||
									(subFeed.getUrl().indexOf(
															  "rss.cnn.com/rss/cnn_topstories")
									 >= 0) ||
									(subFeed.getUrl().indexOf(
															  "money.cnn.com/rssclick/?section=money_topstories")
									 >= 0) ||
									(subFeed.getUrl().indexOf(
															  "feeds.chicagotribune.com/chicagotribune/news/")
									 >= 0) ||
									(subFeed.getUrl().indexOf(
															  "my.abcnews.go.com/rsspublic/fp_rss20.xml")
									 >= 0)) {
								//#ifdef DLOGGING
								if (fineLoggable) {logger.fine(mname + " skipping inconsistent nextIx,j,feed.getUrl(),subFeed.getName(),subFeed.getUrl()=" + nextIx + "," + j + "," + feed.getUrl() + "," + subFeed.getName() + "," + subFeed.getUrl());}
								//#endif
								continue;
							}
							goNext = true;
							if (subCmpFexc != null) {
								String subInfo = "Could not load subCmpfeed, ";
								assertEquals(subInfo +
										"name must equal expected name " +
										assertInfoSub,
										subCmpfeed.getName(), subFeed.getName());
								assertEquals(subInfo +
										"url must equal expected url " +
										assertInfoSub,
										subCmpfeed.getUrl(), subFeed.getUrl());
								//#ifdef DSMARTPHONE
								assertEquals(subInfo +
										"link must equal expected link " +
										assertInfoSub,
										subCmpfeed.getLink(), subFeed.getLink());
								//#endif
							} else {
								Object[] oret2 = super.cmpModLog(
										"Original sub feed must equal expected sub feed " + assertInfoSub,
										(RssItunesFeed)subCmpfeed, subFeed, null);
								if (oret2[1] != null) {
									throw (Throwable)oret2[1];
								}
							}
							goNext = false;
						} finally {
							currRssfeeds[j] = null;
							currCmpRssfeeds[j] = null;
						}
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
				cmpHtmlLinkParserTestSub(mname, name,
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
