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
 * IB 2011-03-08 1.11.5Dev17 Test to make sure that unexpected exceptions are not thrown for all test feeds and compare.
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
package com.substanceofcode.jmunit.rssreader.businesslogic;

import java.util.Date;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
//#ifndef DSMALLMEM
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
//#endif
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Level;
//#endif

final public class HtmlLinkParser2Test extends BaseTestCase
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
	private int prevNextIx = 0; // Start at 0 to get interenet prompt for sourceforge home to skip it's stats.
	static private int nbrTests = 15;
	//#ifdef DLOGGING
	private boolean alterLogLevel = traceLoggable;

	private boolean endAlterLogLevel = false;
	private boolean levelAltered = false;
	private int alterix = 54;
	private int endAlterix = 55;
	private String newLogLevel = Level.FINEST.getName(); // UNDO
	//#endif

	public HtmlLinkParser2Test() {
		super(nbrTests, "HtmlLinkParser2Test");
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
	public void changed(Observable observable) {
		synchronized(this) {
			super.notifyAll();
			ready = true;
		}
	}

	public void changed(net.yinlight.j2me.observable.Observable observable, Object arg) {
		synchronized(this) {
			super.notifyAll();
			ready = true;
		}
	}

	public boolean isReady() {
		return ready;
	}
	//#endif

	/* Test parse HTML. */
	public void testHtmlParse1() throws Throwable {
		String mname = "testHtmlParse1";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse2() throws Throwable {
		String mname = "testHtmlParse2";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse3() throws Throwable {
		String mname = "testHtmlParse3";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse4() throws Throwable {
		String mname = "testHtmlParse4";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, false);
	}

	/* Test parse HTML. */
	public void testHtmlParse5() throws Throwable {
		String mname = "testHtmlParse5";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse6() throws Throwable {
		String mname = "testHtmlParse6";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse7() throws Throwable {
		String mname = "testHtmlParse7";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse8() throws Throwable {
		String mname = "testHtmlParse8";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse9() throws Throwable {
		String mname = "testHtmlParse9";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse10() throws Throwable {
		String mname = "testHtmlParse10";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse11() throws Throwable {
		String mname = "testHtmlParse11";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse12() throws Throwable {
		String mname = "testHtmlParse12";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse13() throws Throwable {
		String mname = "testHtmlParse13";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse14() throws Throwable {
		String mname = "testHtmlParse14";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

	public void testHtmlParse15() throws Throwable {
		String mname = "testHtmlParse15";
		HTMLLinkParser htmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());
		HTMLLinkParser cmpHtmlParser = new HTMLLinkParser(
				testUrl, "", "", new RssFeedStore());

		cmpHtmlLinkParserTestSub(mname, htmlParser, cmpHtmlParser, true);
	}

    public void cmpHtmlLinkParserTestSub(final String pmname,
			HTMLLinkParser htmlParser,
			HTMLLinkParser cmpHtmlParser, boolean endFeeds)
	throws Throwable {
		boolean httpFile = htmlParser.getUrl().startsWith("http:");
		String mname = pmname + "," + nextIx + "," + htmlParser.getUrl() + "," + httpFile;
		boolean goNext = false;
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname + ",activeCount,freeMemory()=" + Thread.activeCount() + "," + Runtime.getRuntime().freeMemory());
			if (finestLoggable) {logger.finest(mname + " cmpHtmlLinkParserTestSub htmlParser=" + htmlParser);} ;
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
			cmpHtmlParser.startParsing();
			cmpHtmlParser.join();
			if (httpFile && (nextIx >= 1) && htmlParser.isSuccessfull() &&
					cmpHtmlParser.isSuccessfull()) {
				oldTotalTime += (System.currentTimeMillis() - startTime);
				oldTotalCount++;
				System.gc();
			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " cmpHtmlParser.isSuccessfull()=" + cmpHtmlParser.isSuccessfull());} ;
			//#endif
			RssItunesFeed[] cmpRssFeeds =
				(RssItunesFeed[])cmpHtmlParser.getFeeds();
			if (htmlParser.isSuccessfull()) {
				assertTrue(mname + " rssfeeds feed length should be > 0",
						rssfeeds.length > 0);
			} else {
				throw htmlParser.getEx();
			}
			if (cmpHtmlParser.isSuccessfull()) {
				assertTrue(mname + " cmpRssFeeds feed length should be > 0",
						cmpRssFeeds.length > 0);
			} else {
				throw cmpHtmlParser.getEx();
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
				startTime = System.currentTimeMillis();
				feed.setName("");
				RssFeedParser fparser = new RssFeedParser((RssItunesFeed)feed,
							null, false);
				fparser.setGetTitleOnly(true);
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
				// Skipping link 0 because the title changes to the current date/time.
				if (cmpfeed.getName().indexOf("0 rss.asp?ad=essa ") >= 0) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " skipping rss.asp XML URL nextIx,cmpfeed.getName(),cmpfeed.getUrl()=" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());}
					//#endif
					continue;
				} else {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " processing  nextIx,cmpfeed.getName(),cmpfeed.getUrl()=" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());}
					//#endif
				}
				cmpfeed.setName("");
				RssFeedParser cmpFparser = new RssFeedParser(cmpfeed, null,
						false);
				cmpFparser.setGetTitleOnly(true);
				startTime = System.currentTimeMillis();
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
				assertEquals("cmpHtmlLinkParserTestSub isSuccessfull() must equal nextIx=" + nextIx, cmpSuccessful, successful);
				if (!cmpSuccessful) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " cmpHtmlLinkParserTestSub not successful nextIx,feed.getName(),fexc,cmpFexc=" + nextIx + "," + feed.getName() + "," + fexc + "," + cmpFexc);} ;
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
				goNext = true;
				//#ifdef DLOGGING
				if (finerLoggable) {logger.finer(mname + " cmpHtmlLinkParserTestSub ncmpfeed.getName(),nfeed.getName()=" + ncmpfeed.getName() + "," + nfeed.getName());}
				//#endif
				// Workaround
				assertEquals("Feed must equal expected feed title " +
						assertInfo + "," + ncmpfeed.getName() + "," + nfeed.getName(), ncmpfeed.getName(), nfeed.getName());
				goNext = false;
				// Free up memory.
				rssfeeds[nextIx] = null;
				// Free up memory.
				cmpRssFeeds[nextIx] = null;

			}
			if (!httpFile && oneTestOnly && retryModHttp) {
				htmlParser = new HTMLLinkParser(
						"http://mobilerssreader.sourceforge.net/testdata/links.html",
						"", "", new RssFeedStore());
				nextIx = prevNextIx;
				cmpHtmlParser = new HTMLLinkParser(
						htmlParser.getUrl(), "", "", new RssFeedStore());
				cmpHtmlLinkParserTestSub(pmname, htmlParser,
						cmpHtmlParser, endFeeds);
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
