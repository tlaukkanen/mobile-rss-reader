/*
 * RssFeedStoreCompatability2Test.java
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

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define compatibility
@DCOMPATDEF@
//#ifdef DCOMPATIBILITY1
  //#define DCOMPATIBILITY
//#endif
//#ifdef DCOMPATIBILITY2
  //#define DCOMPATIBILITY
//#endif
//#ifdef DCOMPATIBILITY3
  //#define DCOMPATIBILITY
//#endif

//#ifdef DJMTEST
//#ifdef DCOMPATIBILITY
package com.substanceofcode.jmunit.businessentities;

import java.util.Date;
import java.util.Vector;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.jmunit.logging.LoggingTestCase;
import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.jmunit.utilities.RssFeedStoreHolder;
import com.substanceofcode.rssreader.businessentities.CompatibilityRssFeed2;
import com.substanceofcode.rssreader.businessentities.CompatibilityRssItem2;

final public class RssFeedStoreCompatability2Test extends LoggingTestCase {

	public RssFeedStoreCompatability2Test() {
		super(10, "RssFeedStoreCompatability2Test");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 2:
				testFeedStoreStr2();
				break;
			case 4:
				testFeedStoreStr3();
				break;
			case 7:
				testFeedStoreStr4b();
				break;
			case 8:
				testFeedStoreStr5();
				break;
			default:
				break;
		}
	}

	public RssItunesFeed feedStoreStrFactory(String mname,
			String step, int nbr,
			String suffix,
			boolean isItunes, long ldate, String explicit,
			long litem1Date, String explicit1, long litem2Date,
			String explicit2) throws Throwable {
		try {
			RssItunesFeed feed = new RssItunesFeed(
				"test" + nbr + suffix, "url" + nbr + suffix, "username" + nbr + suffix, "password" + nbr + suffix);
			feed.setUpddate(new Date(ldate));
			if (isItunes) {
				feed.modifyItunes(
					true, "title" + nbr + suffix, "description" + nbr + suffix, "language" + nbr + suffix, "author" + nbr + suffix,
					"subtitle" + nbr + suffix, "summary" + nbr + suffix,
					RssItunesItem.convExplicit(explicit));
			}
			Vector vitems = new Vector();
			if (isItunes && (litem1Date > 0L)) {
				RssItunesItem item1 = new RssItunesItem("title" + nbr + suffix, "link" + nbr + suffix, "desc" + nbr + suffix,
						new Date(litem1Date),
							"enclosure" + nbr + suffix, true,
							true,
							"author" + nbr + suffix,
							"subtitle" + nbr + suffix,
							"summary" + nbr + suffix,
							RssItunesItem.convExplicit(explicit1),
							"50:00");
				vitems.addElement(item1);
			}

			if (isItunes && (litem2Date > 0L)) {
				RssItunesItem item2 = new RssItunesItem("title" + nbr + suffix + ".2", "link" + nbr + suffix + ".2", "desc" + nbr + suffix + ".2",
						new Date(litem2Date),
							"enclosure" + nbr + suffix + ".2", true,
							true,
							"author" + nbr + suffix + ".2",
							"subtitle" + nbr + suffix + ".2",
							"summary" + nbr + suffix + ".2",
							RssItunesItem.convExplicit(explicit2),
							"50:20");
				vitems.addElement(item2);
			}

			feed.setItems(vitems);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " step " + step +
					" feed=" + feed);}
			//#endif
			return feed;
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("feedStoreStrFactory  " + mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public CompatibilityRssFeed2 feed2StoreTestStrFactory(String mname,
			String step, int nbr,
			String suffix,
			long ldate,
			long litem1Date, long litem2Date) throws Throwable {
		try {
			CompatibilityRssFeed2 feed = new CompatibilityRssFeed2(
				"test" + nbr + suffix, "url" + nbr + suffix, "username" + nbr + suffix, "password" + nbr + suffix);
			feed.setUpddate(new Date(ldate));
			Vector vitems = new Vector();
			if (litem1Date > 0L) {
				CompatibilityRssItem2 item1 = new CompatibilityRssItem2(
						"title" + nbr + suffix, "link" + nbr + suffix, "desc" + nbr + suffix,
						new Date(litem1Date),
							"enclosure" + nbr + suffix, true);
				vitems.addElement(item1);
			}

			if (litem2Date > 0L) {
				CompatibilityRssItem2 item2 = new CompatibilityRssItem2("title" + nbr + suffix + ".2", "link" + nbr + suffix + ".2", "desc" + nbr + suffix + ".2",
						new Date(litem2Date),
							"enclosure" + nbr + suffix + ".2", true);
				vitems.addElement(item2);
			}

			feed.setItems(vitems);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " step " + step +
					" infoFeed=" + feed);}
			//#endif
			return feed;
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("feed2StoreTestStrFactory  " + mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public RssFeedStoreHolder feedStoreTestStrSub(String mname, int nbr, String suffix,
			boolean isItunes, long ldate, String explicit,
			long litem1Date, String explicit1, long litem2Date,
			String explicit2) throws Throwable {
		try {
			RssFeedStoreHolder fstore = new RssFeedStoreHolder();
			RssItunesFeed feed = feedStoreStrFactory(mname,
									"1", nbr, suffix + "z",
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);

			CompatibilityRssFeed2 infoFeed = feed2StoreTestStrFactory(mname,
									"2", nbr, suffix,
									ldate, litem1Date,
									litem2Date);
			assertFalse(mname +
					" should be feeds not equal with different attributes.",
					infoFeed.equals(feed));

			feed = feedStoreStrFactory(mname,
									"2", nbr, suffix,
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);

			assertTrue(mname + " feeds equal with the same attributes.",
					infoFeed.equals(feed));

			fstore.feed = feed;
			fstore.infoFeed = infoFeed;
			return fstore;

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("feedStoreTestStrSub  " + mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr2() throws Throwable {
		String mname = "testFeedStoreStr2";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 2, "",
			true, cdate, "yes",
			0L, "", 0L,
			"");

			storeStringTestSub(mname, true, true,
					fholder.feed, (CompatibilityRssFeed2)fholder.infoFeed, null);

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr3() throws Throwable {
		String mname = "testFeedStoreStr3";
		try {
			long cdate = System.currentTimeMillis();
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 2, "",
			true, cdate, "yes",
			cdate + 5500L, "no", 0L,
			"");

			storeStringTestSub(mname, true, true,
					fholder.feed, (CompatibilityRssFeed2)fholder.infoFeed, null);

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr4() throws Throwable {
		String mname = "testFeedStoreStr4";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 4, "",
			true, cdate, "yes",
			cdate + 5500L, "no", cdate + 6600L,
			"explicit");

			storeStringTestSub(mname, true, true,
					fholder.feed, (CompatibilityRssFeed2)fholder.infoFeed, null);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr4b() throws Throwable {
		String mname = "testFeedStoreStr4b";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 4, "b",
			true, cdate, "explicit",
			cdate + 5500L, "yes", cdate + 8800L,
			"no");

			storeStringTestSub(mname, true, true,
					fholder.feed, (CompatibilityRssFeed2)fholder.infoFeed, null);

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr5() throws Throwable {
		String mname = "testFeedStoreStr5";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 5, "",
			true, cdate, "yes",
			cdate + 5500L, "no", cdate + 7000L,
			"explicit");

			storeStringTestSub(mname, true, true,
					fholder.feed, (CompatibilityRssFeed2)fholder.infoFeed, null);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

    public void storeStringTestSub(final String mname,
			final boolean serializeItems, final boolean encoded,
			RssItunesFeed feed,
			CompatibilityRssFeed2 infoFeed,
			String storeString) throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("storeStringTestSub Started " + mname);
			boolean genFeed = storeString != null;
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " serializeItems,encoded,genFeed=" + serializeItems + "," + encoded + "," + genFeed);}
			//#endif
			if (!genFeed) {
				storeString = infoFeed.getStoreString(serializeItems, encoded);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " storeString=" + storeString);}
			//#endif
			if (genFeed) {
				infoFeed = new CompatibilityRssFeed2(storeString);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " infoFeed=" + infoFeed);}
			//#endif
			RssItunesFeed nfeed = new RssItunesFeed(new RssFeed(false,
						encoded, storeString));
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub " + mname + " nfeed=" + nfeed);}
			//#endif
			assertEquals(mname + " new feed items = 0", 0,
					nfeed.getItems().size());
			assertNull(mname + " new feed upddete = null", nfeed.getUpddate());
			infoFeed.setItems(new Vector());
			infoFeed.setUpddate(null);
			assertTrue(mname + " feeds equal without items.", infoFeed.equals(nfeed));
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("storeStringTestSub  " + mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

}
//#endif
//#endif
