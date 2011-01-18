//--Need to modify--#preprocess
/*
 * OpmlParser2Test.java
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
 * IB 2010-05-28 1.11.5RC2 Do comparison test using OpmlParser with links-opml.xml.
 * IB 2010-06-29 1.11.5RC2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-09-26 1.11.5Dev8 Use OpmlParser2Test only if not small memory.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Use procThrowable from LoggingTestCase.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Use linksearchurl to specify link search for a URL in the feed name.
 * IB 2011-01-14 1.11.5Alpha15 Use convience methods updSvLogging and updPrevLogging from LoggingTestCase to alter/restore the logging level.
 * IB 2011-01-14 1.11.5Alpha15 Use convience method cmpModLog from LoggingTestCase to see if feeds are unequal and change the logging level to retry using logging to make debugging equals failures easier.  Also, retry with modified previous version for bug fixes/enhancements made in the current version.
 * IB 2011-01-14 1.11.5Alpha15 Workaround problem where old name becomes null.
 * IB 2011-01-14 1.11.5Alpha15 If failure is for equals test, increase nextIx to allow the next test to try the next feed instead of retrying the failed feed over and over again.
 * IB 2011-01-14 1.11.5Alpha15 If old gives 0 feeds and new gives error (has no feeds assumed), treat as equal.
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

//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businesslogic.compatibility4;

import java.util.Date;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.utils.compatibility4.CauseException;
import com.substanceofcode.rssreader.businesslogic.compatibility4.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
//#ifndef DSMALLMEM
import com.substanceofcode.utils.HTMLParser;
//#endif
import com.substanceofcode.rssreader.businesslogic.compatibility4.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLLinkParser;
//#ifndef DSMALLMEM
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLAutoLinkParser;
//#endif
import com.substanceofcode.rssreader.businesslogic.compatibility4.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.LineByLineParser;
//#ifdef DMIDP20
import net.eiroca.j2me.observable.compatibility4.Observer;
import net.eiroca.j2me.observable.compatibility4.Observable;
//#endif

import com.substanceofcode.jmunit.utilities.BaseTestCase;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Level;
//#endif

  /**
   * Set link to "" for old version compare.  NEEDS TO BE CHANGED.
   *
   * @author Irv Bunton
   */
final public class OpmlParser2Test extends BaseTestCase
//#ifdef DMIDP20
implements Observer, net.yinlight.j2me.observable.Observer
//#endif
{

	final public String LINKSEARCHURL = "linksearchurl=";
	//#ifdef DMIDP20
	private boolean ready = false;
	//#endif
	private int nextIx = 0;
	//#ifdef DLOGGING
	private boolean alterLogLevel = false;
	private boolean endAlterLogLevel = false;
	private boolean levelAltered = false;
	private int alterix = 28;
	private int endAlterix = 29;
	private String newLogLevel = Level.FINEST.getName();
	//#endif

	public OpmlParser2Test() {
		super(5, "compatibility4.OpmlParser2Test");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testOpmlParse1();
				break;
			case 1:
				testOpmlParse2();
				break;
			case 2:
				testOpmlParse3();
				break;
			case 3:
				testOpmlParse4();
				break;
			case 4:
				testOpmlParse5();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	//#ifdef DMIDP20
	public void changed(Observable observable) {
		ready = true;
		synchronized(this) {
			super.notifyAll();
		}
	}

	public void changed(net.yinlight.j2me.observable.Observable observable, Object arg) {
		ready = true;
	}

	public boolean isReady() {
		return ready;
	}
	//#endif

	/* Test parse OPML XML. */
	public void testOpmlParse1() throws Throwable {
		String mname = "testOpmlParse1";
		compatibilityOpmlParserTestSub(mname, "links2-opml.xml", "jar:///links2-opml.xml", false);
	}

	/* Test parse OPML. */
	public void testOpmlParse2() throws Throwable {
		String mname = "testOpmlParse2";
		compatibilityOpmlParserTestSub(mname, "links2-opml.xml", "jar:///links2-opml.xml", false);
	}

	/* Test parse OPML. */
	public void testOpmlParse3() throws Throwable {
		String mname = "testOpmlParse3";
		compatibilityOpmlParserTestSub(mname, "links2-opml.xml", "jar:///links2-opml.xml", false);
	}

	/* Test parse OPML. */
	public void testOpmlParse4() throws Throwable {
		String mname = "testOpmlParse4";
		compatibilityOpmlParserTestSub(mname, "links2-opml.xml", "jar:///links2-opml.xml", false);
	}

	/* Test parse OPML. */
	public void testOpmlParse5() throws Throwable {
		String mname = "testOpmlParse5";
		compatibilityOpmlParserTestSub(mname, "links2-opml.xml", "jar:///links2-opml.xml", true);
	}

	private RssItunesFeedInfo[] parseOpml(final String mname,
			String name, String url)
	throws com.substanceofcode.utils.CauseException, Throwable {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine(mname + " entering parseOpml name,url=" + name + "," + url);} ;
		//#endif
		try {
			com.substanceofcode.rssreader.businesslogic.FeedListParser feedListParser;
			if (url.endsWith(".txt")) {
				feedListParser =
				new com.substanceofcode.rssreader.businesslogic.LineByLineParser(
					url, "", "", new RssFeedStore());
			//#ifndef DSMALLMEM
			} else if (HTMLParser.isHtml(url)) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(mname + " skipping HTML url=" + url);} ;
				//#endif
				return null;
			//#endif
			} else if (url.endsWith(".xml") || url.endsWith(".sh")) {
				feedListParser =
				new com.substanceofcode.rssreader.businesslogic.OpmlParser(
					url, "", "", new RssFeedStore());
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine(mname + " skipping unknown url=" + url);} ;
				//#endif
				return null;
			}
			//#ifdef DMIDP20
			ready = false;
			//#endif
			if (name.indexOf("option=missing title") >= 0) {
				feedListParser.setGetFeedTitleList(true);
			}
			int pos;
			if ((pos = name.indexOf(LINKSEARCHURL)) >= 0) {
				String srchurl = name.substring(pos +
						LINKSEARCHURL.length());
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine(mname + " search filter srchurl=" + srchurl);}
				//#endif
				feedListParser.setFeedURLFilter(srchurl);
			}
			if (name.indexOf("linksearchurl=rss.xml") >= 0) {
				feedListParser.setFeedURLFilter("rss.xml");
			} else if (name.indexOf("linksearchurl=xml") >= 0) {
				feedListParser.setFeedURLFilter("xml");
			} else if (name.indexOf("linksearchurl=/rss") >= 0) {
				feedListParser.setFeedURLFilter("/rss");
			}
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
			if (fineLoggable) {logger.fine(mname + " feedListParser.isSuccessfull()=" + feedListParser.isSuccessfull());} ;
			//#endif
			if (!feedListParser.isSuccessfull()) {
				throw feedListParser.getEx();
			}
			return feedListParser.getFeeds();
		} catch (com.substanceofcode.utils.CauseException e) {
			//#ifdef DLOGGING
			logger.warning(mname + " CauseException failure ",e);
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

	private RssItunesFeedInfo[] compatibilityParseOpml(final String mname,
			String name, String url)
	throws CauseException, Throwable {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine(mname + " entering compatibilityParseOpml name,url=" + name + "," + url);} ;
		//#endif
		try {
			FeedListParser compatibilityFeedListParser;
			if (url.endsWith(".txt")) {
				compatibilityFeedListParser = new LineByLineParser( url, "", "");
			//#ifndef DSMALLMEM
			} else if (HTMLParser.isHtml(url)) {
				if (name.indexOf("option=htmlautolink") >= 0) {
					compatibilityFeedListParser =
					new HTMLAutoLinkParser( url, "", "");
				} else {
					compatibilityFeedListParser = new HTMLLinkParser(
						url, "", "");
				}
			//#endif
			} else if (url.endsWith(".xml") || url.endsWith(".sh")) {
				compatibilityFeedListParser = new OpmlParser( url, "", "");
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine(mname + " skipping unknown url=" + url);} ;
				//#endif
				return null;
			}
			if (name.indexOf("option=missing title") >= 0) {
				compatibilityFeedListParser.setGetFeedTitleList(true);
			}
			int pos;
			if ((pos = name.indexOf(LINKSEARCHURL)) >= 0) {
				String srchurl = name.substring(pos +
						LINKSEARCHURL.length());
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine(mname + " search filter srchurl=" + srchurl);}
				//#endif
				compatibilityFeedListParser.setFeedURLFilter(srchurl);
			}
			if (name.indexOf("linksearchurl=rss.xml") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("rss.xml");
			} else if (name.indexOf("linksearchurl=xml") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("xml");
			} else if (name.indexOf("linksearchurl=/rss") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("/rss");
			}
			compatibilityFeedListParser.startParsing();
			compatibilityFeedListParser.join();
			if (fineLoggable) {logger.fine(mname + " compatibilityFeedListParser.isSuccessfull()=" + compatibilityFeedListParser.isSuccessfull());} ;
			if (!compatibilityFeedListParser.isSuccessfull()) {
				throw compatibilityFeedListParser.getEx();
			}
			return compatibilityFeedListParser.getFeeds();
		} catch (CauseException e) {
			//#ifdef DLOGGING
			logger.warning(mname + " CauseException failure ",e);
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
		if (finerLoggable) {logger.finer(mname + " " + logText + " name,url=" + name + "," + url);} ;
		//#endif
		RssItunesFeedInfo[] rssfeeds = null;
		com.substanceofcode.utils.CauseException exc = null;
		try {
			if ((rssfeeds = parseOpml(mname, name, url)) == null) {
				return null;
			}
		} catch (com.substanceofcode.utils.CauseException e) {
			exc = e;
		}
		CauseException compatibilityExc = null;
		RssItunesFeedInfo[] cmpRssFeeds = null;
		try {
			cmpRssFeeds = compatibilityParseOpml(mname,
					name, url);
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

    public void compatibilityOpmlParserTestSub(final String mname,
			String name, String url, boolean endFeeds)
	throws Throwable {
		boolean goNext = false;
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			//#endif
			RssCompFeeds feeds = parseCompFeeds(mname,
					" compatibilityOpmlParserTestSub",
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
			int endIx = endFeeds ? rssfeeds.length : (nextIx + (rssfeeds.length / 5));

			for (; (nextIx < endIx) && (nextIx < rssfeeds.length) && (nextIx < cmpRssFeeds.length);
					nextIx++) {
				try {
					//#ifdef DLOGGING
					if (alterLogLevel && (nextIx >= alterix) && (alterix >= 0)) {
						endAlterLogLevel = true;
						svLogLevel = super.updSvLogging(newLogLevel);
						alterLogLevel = false;
						logger.info(mname + " altering level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
					} else if (endAlterLogLevel && (nextIx >= endAlterix)) {
						endAlterLogLevel = false;
						updPrevLogging(svLogLevel);
						logger.info(mname + " reverting level nextIx,svLogLevel,newLevel=" + nextIx + "," + svLogLevel + "," + logger.getParent().getLevel());
					}
					//#endif
					RssItunesFeedInfo feed = rssfeeds[nextIx];
					//#ifdef DLOGGING
					if (fineLoggable && !finestLoggable) {logger.fine(mname + " nextIx,feed 1=" + nextIx + "," + feed.getName() + "," + feed.getUrl());} ;
					//#endif
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " nextIx,feed 1=" + nextIx + "," + feed.toString());} ;
					//#endif
					RssItunesFeedInfo cmpfeed = cmpRssFeeds[nextIx];
					//#ifdef DLOGGING
					if (fineLoggable && !finestLoggable) {logger.fine(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());} ;
					//#endif
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " nextIx,cmpfeed 1=" + nextIx + "," + cmpfeed.toString());} ;
					//#endif
					String assertInfo = new String("nextIx,name,url=" + nextIx + "," + feed.getName() + "," +  feed.getUrl());
					// Workaround
					Object[] oret = super.cmpModLog(
							"Original feed must equal expected feed " + assertInfo,
							(RssItunesFeed)cmpfeed, feed);
					if (oret[1] != null) {
						throw (Throwable)oret[1];
					}
					// Don't do bom 7.
					if ((feed.getName().indexOf("BOM encoding7-utf8-txt.txt") >= 0) ||
						(feed.getName().indexOf("29 HTML Der Spiegel search for ") >= 0) ||
						(feed.getName().indexOf("feature=relative-url") >= 0) ||
						//#ifndef DSMALLMEM
						HTMLParser.isHtml(feed.getUrl()) ||
						//#endif
					   ((feed.getUrl().indexOf("http://") >= 0) &&
					   ((feed.getName().indexOf("Russian") >= 0) ||
					   (feed.getName().indexOf("Russian") >= 0) ||
					   (feed.getName().indexOf("http://sourceforge.net") >= 0)))) {
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " skipping http or gnu XML URL nextIx,feed.getUrl() 2=" + nextIx + "," + feed.getUrl());} ;
						//#endif
						continue;
					} else if (!feed.getUrl().endsWith(".xml") &&
							!feed.getUrl().endsWith(".sh") &&
						!feed.getUrl().endsWith(".txt")) {
						//#ifdef DLOGGING
						if (fineLoggable) {logger.fine(mname + " skipping unknown url nextIx,feed.getUrl()=" + nextIx + "," + feed.getUrl());} ;
						//#endif
						continue;
					}
					com.substanceofcode.utils.CauseException currExc = null;
					RssItunesFeedInfo[] currRssfeeds = null;
					try {
						currRssfeeds = parseOpml(mname,
								feed.getName(), feed.getUrl());
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " nextIx currRssfeeds.length=" + nextIx + "," + currRssfeeds.length);}
						//#endif
					} catch (com.substanceofcode.utils.CauseException e) {
						currExc = e;
					}
					CauseException currCmpExc = null;
					RssItunesFeedInfo[] currCmpRssfeeds = null;
					try {
						currCmpRssfeeds = compatibilityParseOpml(
								mname, feed.getName(), feed.getUrl());
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
						String assertInfoSub = new String("nextIx,j,name,url=" + nextIx + "," + j + "," + feed.getName() + "," +  feed.getUrl() + "," + subFeed.getName() + "," +  subFeed.getUrl());
						//#ifdef DLOGGING
						if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subFeed 1=" + j + "," + subFeed.getName() + "," + subFeed.getUrl());} ;
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " j,subFeed 1=" + j + "," + subFeed.toString());} ;
						//#endif
						RssItunesFeedInfo subCmpfeed = currCmpRssfeeds[j];
						assertNotNull("Compatibility sub feeds must not be null " + assertInfoSub, subCmpfeed);
						//#ifdef DLOGGING
						if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.getName() + "," + subCmpfeed.getUrl());} ;
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.toString());} ;
						//#endif
						// Fixed problem where name was null.  We NEVER want null.
						if ((subFeed.getName().length() == 0) &&
						    (subCmpfeed.getName() == null)) {
							// workaround
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine(mname + " fixing null name j,subCmpfeed.getName(),subFeed.getName()=" + j + "," + subCmpfeed.getName() + "," + subFeed.getName());} ;
							//#endif
							subCmpfeed.setName("");
						}
						// Workaround new feature/bug fix.
						subFeed.setLink("");
						goNext = true;
						Object[] oret2 = super.cmpModLog(
								"Original sub feed must equal expected sub " +
								"feed " + assertInfoSub,
								subCmpfeed, subFeed);
						if (oret[1] != null) {
							throw (Throwable)oret[1];
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
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " finished.");} ;
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
