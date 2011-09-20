//--Need to modify--#preprocess
/*
 * RssFeedStoreStr3Test.java
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
package com.substanceofcode.jmunit.rssreader.businessentities;

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
final public class RssFeedStoreStr3Test extends BaseTestCase
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
	private static final String CURRENT_CLASS = "RssFeedStoreStr3Test";

	public RssFeedStoreStr3Test() {
		super(nbrTests, CURRENT_CLASS);
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testHtmlFeedStoreStr1();
				break;
			case 1:
				testHtmlFeedStoreStr2();
				break;
			case 2:
				testHtmlFeedStoreStr3();
				break;
			case 3:
				testHtmlFeedStoreStr4();
				break;
			case 4:
				testHtmlFeedStoreStr5();
				break;
			case 5:
				testHtmlFeedStoreStr6();
				break;
			case 6:
				testHtmlFeedStoreStr7();
				break;
			case 7:
				testHtmlFeedStoreStr8();
				break;
			case 8:
				testHtmlFeedStoreStr9();
				break;
			case 9:
				testHtmlFeedStoreStr10();
				break;
			case 10:
				testHtmlFeedStoreStr11();
				break;
			case 11:
				testHtmlFeedStoreStr12();
				break;
			case 12:
				testHtmlFeedStoreStr13();
				break;
			case 13:
				testHtmlFeedStoreStr14();
				break;
			case 14:
				testHtmlFeedStoreStr15();
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

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr1() throws Throwable {
		String mname = "testHtmlFeedStoreStr1";
		storeStringTestSub(mname, "links2.html", testUrl, false, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr2() throws Throwable {
		String mname = "testHtmlFeedStoreStr2";
		storeStringTestSub(mname, "links2.html", testUrl, false, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr3() throws Throwable {
		String mname = "testHtmlFeedStoreStr3";
		storeStringTestSub(mname, "links2.html", testUrl, false, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr4() throws Throwable {
		String mname = "testHtmlFeedStoreStr4";
		storeStringTestSub(mname, "links2.html", testUrl, false, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr5() throws Throwable {
		String mname = "testHtmlFeedStoreStr5";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr6() throws Throwable {
		String mname = "testHtmlFeedStoreStr6";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr7() throws Throwable {
		String mname = "testHtmlFeedStoreStr7";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr8() throws Throwable {
		String mname = "testHtmlFeedStoreStr8";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr9() throws Throwable {
		String mname = "testHtmlFeedStoreStr9";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr10() throws Throwable {
		String mname = "testHtmlFeedStoreStr10";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr11() throws Throwable {
		String mname = "testHtmlFeedStoreStr11";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr12() throws Throwable {
		String mname = "testHtmlFeedStoreStr12";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr13() throws Throwable {
		String mname = "testHtmlFeedStoreStr13";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr14() throws Throwable {
		String mname = "testHtmlFeedStoreStr14";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
	}

	/* Test parse HTML store str. */
	public void testHtmlFeedStoreStr15() throws Throwable {
		String mname = "testHtmlFeedStoreStr15";
		storeStringTestSub(mname, "links2.html", testUrl, true, true, true, true, true, true);
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
				//#ifdef DLOGGING
				String srchUrl;
				if (fineLoggable &&
						((srchUrl = feedListParser.getFeedURLFilter()).length() >
						 0)) {
					logger.fine(mname + " nextIx,name,feedListParser.getFeedURLFilter(=" + nextIx + "," + name + "," + srchUrl);
				}
				//#endif
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

	private RssItunesFeedInfo[] parseFeed(final String mname,
			final String logText,
			final String assertTextExcNull,
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
			assertNotNull("Feeds " + assertTextNotNull, rssfeeds);
			assertNull("Exception " + assertTextExcNull, exc);
			return rssfeeds;
		}

	public void storeStringTestSub(final String mname,
			String name, String url, boolean endFeeds,
			final boolean saveHdr,
			final boolean serializeItems, final boolean encoded,
			boolean itemsSaved, boolean modifyCapable)
	throws Throwable
	{
		boolean httpFile = url.startsWith("http:");
		boolean goNext = false;
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname + ",activeCount,freeMemory()=" + Thread.activeCount() + "," + Runtime.getRuntime().freeMemory());
			//#endif
			if (!httpFile) {
				prevNextIx = nextIx;
			}
			RssItunesFeedInfo[] rssfeeds = parseFeed(mname,
					" storeStringTestSub",
					"parse feeds exception must be null",
					"parse feeds must not be null", name, url);
			if (rssfeeds == null) {
				return;
			}
			assertTrue(mname + " rssfeeds feed length should be > 0",
					rssfeeds.length > 0);
			int endIx = endFeeds ? rssfeeds.length : (nextIx + (rssfeeds.length / nbrTests));

			for (; (nextIx < endIx) && (nextIx < rssfeeds.length); nextIx++) {
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
					String assertInfo = new String("nextIx,name,url=" + nextIx + "," + feed.getName() + "," +  feed.getUrl());
					// Workaround
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
								feed.getUrl(), false);
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " nextIx currRssfeeds.length=" + nextIx + "," + currRssfeeds.length);}
						//#endif
					} catch (CauseException e) {
						currExc = e;
					}
					//#ifdef DLOGGING
					if (fineLoggable && !finestLoggable) {logger.fine(mname + " nextIx,currExc" + nextIx + "," + currExc);} ;
					//#endif
					if (currExc == null)  {
						assertNotNull("Original sub feeds must not be null " + assertInfo, currRssfeeds);
					}
					if (currExc != null) {
						//#ifdef DLOGGING
						logger.info(mname + " skipping exception nextIx,feed.getName(),feed.getUrl(),currExc 3=" + nextIx + "," + feed.getUrl() + "," + feed.getName() + "," + currExc.getMessage());
						//#endif
						continue;
					}
					for (int j = 0; (j < currRssfeeds.length); j++) {
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
							String assertInfoSub = "nextIx,j,name,url=" + nextIx + "," + j + "," + feed.getName() + "," +  feed.getUrl() + ",[" + subFeed.getName() + "]," +  subFeed.getUrl();
							RssFeedParser fparser = new RssFeedParser(
									(RssItunesFeed)subFeed, null, false);
							//#ifdef DMIDP20
							fparser.makeObserable(true, 10);
							ready = false;
							fparser.getObservableHandler().addObserver(this);
							fparser.getParsingThread().start();
							while (!isReady()) {
								synchronized(this) {
									super.wait(500L);
								}
							}
							boolean successful = fparser.isSuccessfull();
							Throwable subFexc = fparser.getEx();
							//#else
							boolean successful = true;
							Throwable subFexc = null;
							try {
								fparser.parseRssFeed( false, 10);
							} catch(Throwable e) {
								successful = false;
								subFexc = e;
							}
							//#endif
							subFeed = fparser.getRssFeed();
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine(mname + " nextIx,j,subFeed.getName(),subFeed.getUrl(),subFexc=" + nextIx + "," + j + "," + subFeed.getName() + "," + subFeed.getUrl() + "," + subFexc);} ;
							if (fineLoggable) {logger.fine(mname + " nextIx,j,subFeed.getName(),successful=" + nextIx + "," + j + "," + subFeed.getName() + "," + successful);} ;
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
							if (subFexc != null) {
								//#ifdef DLOGGING
								if (fineLoggable) {logger.fine(mname + " skipping nextIx,j,subFeed.getName(),subFeed.getUrl(),subFexc=" + nextIx + "," + j + "," + subFeed.getName() + "," + subFeed.getUrl() + "," + subFexc.getMessage());} ;
								if (fineLoggable) {logger.fine(mname + " skipping 2 nextIx,j,subFeed.getName(),successful=" + nextIx + "," + j + "," + subFeed.getName() + "," + successful);} ;
								//#endif
								continue;
							}
							goNext = true;
							String storeString = subFeed.getStoreString(saveHdr,
									serializeItems, encoded);
							RssItunesFeed nsubFeed = RssItunesFeed.deserialize(
									true, encoded, storeString);
							assertTrue(mname + " feeds equal.", subFeed.equals(
										nsubFeed));
							if (!itemsSaved) {
								assertEquals(mname + " new feed items = 0", 0,
										nsubFeed.getItems().length);
							}
							goNext = false;
						} finally {
							currRssfeeds[j] = null;
						}
					}
				} finally {
					// Free up memory.
					rssfeeds[nextIx] = null;
					// Free up memory.
				}

			}
			if (!httpFile && oneTestOnly && retryModHttp) {
				storeStringTestSub(mname, name,
						"http://mobilerssreader.sourceforge.net/testdata/links2.html",
						endFeeds, saveHdr, serializeItems, encoded,
						itemsSaved, modifyCapable);
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
