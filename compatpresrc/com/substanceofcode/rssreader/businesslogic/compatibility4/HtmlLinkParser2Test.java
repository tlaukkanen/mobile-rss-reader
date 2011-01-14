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
package com.substanceofcode.jmunit.businesslogic.compatibility4;

import java.util.Date;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.utils.compatibility4.CauseException;
import com.substanceofcode.rssreader.businesslogic.compatibility4.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
//#ifndef DSMALLMEM
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLLinkParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.HTMLAutoLinkParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.LineByLineParser;
//#endif
//#ifdef DMIDP20
import net.eiroca.j2me.observable.Observer;
import net.eiroca.j2me.observable.Observable;
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
final public class HtmlLinkParser2Test extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	private boolean ready = false;
	//#ifdef DLOGGING
	private boolean alterLogLevel = false;
	private boolean levelAltered = false;
	private int alterix = 28;
	private String newLogLevel = Level.FINEST.getName();
	private Level svLogLevel = null;
	//#endif

	public HtmlLinkParser2Test() {
		super(1, "compatibility4.HtmlLinkParser2Test");
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
		compatibilityHtmlLinkParserTestSub(mname, "links2.html", "jar:///links2.html");
	}

	private RssItunesFeedInfo[] parseHtml(final String mname,
			String name, String url)
	throws com.substanceofcode.utils.CauseException, Throwable {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine(mname + " entering parseHtml name,url=" + name + "," + url);}
		//#endif
		try {
			com.substanceofcode.rssreader.businesslogic.FeedListParser feedListParser;
			if (url.endsWith(".txt")) {
				feedListParser =
				new com.substanceofcode.rssreader.businesslogic.LineByLineParser(
					url, "", "");
			} else if (url.endsWith(".html")) {
				if (name.indexOf("option=htmlautolink") >= 0) {
					feedListParser =
					new com.substanceofcode.rssreader.businesslogic.HTMLAutoLinkParser(
					url, "", "");
				} else {
					feedListParser =
					new com.substanceofcode.rssreader.businesslogic.HTMLLinkParser(
					url, "", "");
				}
			} else {
				feedListParser =
				new com.substanceofcode.rssreader.businesslogic.OpmlParser(
					url, "", "");
			}
			ready = false;
			if (name.indexOf("option=missing title") >= 0) {
				feedListParser.setGetFeedTitleList(true);
			}
			if (name.indexOf("linksearch=rss.xml") >= 0) {
				feedListParser.setFeedURLFilter("rss.xml");
			} else if (name.indexOf("linksearch=xml") >= 0) {
				feedListParser.setFeedURLFilter("xml");
			} else if (name.indexOf("linksearch=/rss") >= 0) {
				feedListParser.setFeedURLFilter("/rss");
			}
			//#ifdef DMIDP20
			feedListParser.getObserverManager().addObserver(this);
			//#endif
			feedListParser.startParsing();
			//#ifdef DMIDP20
			while (!isReady()) {
				synchronized(this) {
					wait(1000L);
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

	private RssItunesFeedInfo[] compatibilityParseHtml(final String mname,
			String name, String url)
	throws CauseException, Throwable {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine(mname + " entering compatibilityParseHtml name,url=" + name + "," + url);}
		//#endif
		try {
			FeedListParser compatibilityFeedListParser;
			if (url.endsWith(".txt")) {
				compatibilityFeedListParser = new LineByLineParser( url, "", "");
			} else if (url.endsWith(".html")) {
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
			if (name.indexOf("linksearch=rss.xml") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("rss.xml");
			} else if (name.indexOf("linksearch=xml") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("xml");
			} else if (name.indexOf("linksearch=/rss") >= 0) {
				compatibilityFeedListParser.setFeedURLFilter("/rss");
			}
			compatibilityFeedListParser.startParsing();
			compatibilityFeedListParser.join();
			if (fineLoggable) {logger.fine(mname + " compatibilityFeedListParser.isSuccessfull()=" + compatibilityFeedListParser.isSuccessfull());}
			if (!compatibilityFeedListParser.isSuccessfull()) {
				throw compatibilityFeedListParser.getEx();
			}
			return compatibilityFeedListParser.getFeeds();
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
		try {
			rssfeeds = parseHtml(mname, name, url);
		} catch (com.substanceofcode.utils.CauseException e) {
			exc = e;
		}
		CauseException compatibilityExc = null;
		RssItunesFeedInfo[] cmpRssFeeds = null;
		try {
			cmpRssFeeds = compatibilityParseHtml(mname,
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

    public void compatibilityHtmlLinkParserTestSub(final String mname,
			String name, String url)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			//#endif
			RssCompFeeds feeds = parseCompFeeds(mname,
					" compatibilityHtmlLinkParserTestSub",
					"parse feeds exception must be equal",
					"parse feeds must not be null", name, url);
			if (feeds == null) {
				return;
			}
			RssItunesFeedInfo[] rssfeeds = feeds.getRssfeeds();
			RssItunesFeedInfo[] cmpRssFeeds = feeds.getCmpRssFeeds();

			for (int i = 0; (i < rssfeeds.length) && (i < cmpRssFeeds.length);
					i++) {
				try {
					//#ifdef DLOGGING
					if (alterLogLevel && (i >= alterix)) {
						svLogLevel = logger.getParent().getLevel();
						logger.getParent().setLevel(Level.parse(newLogLevel));
						alterLogLevel = false;
						logger.info(mname + " altering level i,svLogLevel,newLevel=" + i + "," + svLogLevel + "," + logger.getParent().getLevel());
					}
					//#endif
					RssItunesFeedInfo feed = rssfeeds[i];
					//#ifdef DLOGGING
					if (fineLoggable && !finestLoggable) {logger.fine(mname + " i,feed 1=" + i + "," + feed.getName() + "," + feed.getUrl());}
					//#endif
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " i,feed 1=" + i + "," + feed.toString());}
					//#endif
					RssItunesFeedInfo cmpfeed = cmpRssFeeds[i];
					//#ifdef DLOGGING
					if (fineLoggable && !finestLoggable) {logger.fine(mname + " i,cmpfeed 1=" + i + "," + cmpfeed.getName() + "," + cmpfeed.getUrl());}
					//#endif
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " i,cmpfeed 1=" + i + "," + cmpfeed.toString());}
					//#endif
					String assertInfo = new String("i,name,url=" + i + "," + feed.getName() + "," +  feed.getUrl());
					assertTrue("Original feed must equal expected feed " + assertInfo, ((RssItunesFeed)cmpfeed).equals(feed));
					// Don't do bom 7.
					if ((feed.getName().indexOf("BOM encoding7-utf8-txt.txt") >= 0) ||
						(feed.getName().indexOf("29 HTML Der Spiegel search for ") >= 0) ||
						(feed.getName().indexOf("feature=relative-url") >= 0) ||
					   ((feed.getUrl().indexOf("http://") >= 0) &&
					   ((feed.getName().indexOf("Russian") >= 0) ||
					   (feed.getName().indexOf("Russian") >= 0) ||
					   (feed.getName().indexOf("http://sourceforge.net") >= 0)))) {
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " skipping http or gnu XML URL i,feed.getUrl() 2=" + i + "," + feed.getUrl());}
						//#endif
						continue;
					}
					RssItunesFeedInfo[] currRssfeeds = parseHtml(mname,
							feed.getName(), feed.getUrl());
					RssItunesFeedInfo[] currCmpRssfeeds = compatibilityParseHtml(
							mname, feed.getName(), feed.getUrl());
					assertNotNull("Original sub feeds must not be null " + assertInfo, currRssfeeds);
					assertNotNull("Compatibility sub feeds must not be null " + assertInfo, currCmpRssfeeds);
					for (int j = 0;
							(j < currRssfeeds.length) && (j < currCmpRssfeeds.length);
							j++) {
						RssItunesFeedInfo subFeed = currRssfeeds[j];
						String assertInfoSub = new String("i,j,name,url=" + i + "," + j + "," + feed.getName() + "," +  feed.getUrl() + "," + subFeed.getName() + "," +  subFeed.getUrl());
						//#ifdef DLOGGING
						if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subFeed 1=" + j + "," + subFeed.getName() + "," + subFeed.getUrl());}
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " j,subFeed 1=" + j + "," + subFeed.toString());}
						//#endif
						RssItunesFeedInfo subCmpfeed = currCmpRssfeeds[j];
						assertNotNull("Compatibility sub feeds must not be null " + assertInfoSub, subCmpfeed);
						//#ifdef DLOGGING
						if (fineLoggable && !finestLoggable) {logger.fine(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.getName() + "," + subCmpfeed.getUrl());}
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " j,subCmpfeed 1=" + j + "," + subCmpfeed.toString());}
						//#endif
						subFeed.setLink("");
						assertTrue("Original sub feed must equal expected sub feed " + assertInfo, ((RssItunesFeed)subCmpfeed).equals(subFeed));
					}
				} finally {
					// Free up memory.
					rssfeeds[i] = null;
					// Free up memory.
					cmpRssFeeds[i] = null;
				}

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

class RssCompFeeds {

	final RssItunesFeedInfo[] rssfeeds;
	final RssItunesFeedInfo[] cmpRssFeeds;

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
//#endif
