//--Need to modify--#preprocess
/*
 * RssFeedStoreTest.java
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
 * IB 2010-03-28 1.11.5RC2 Updated date defaults to "".
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
 * IB 2011-01-24 1.11.5Dev16 Fix code placement for using JMUnit on a device.
 */

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define compatibility
@DCOMPATDEF@

//#ifdef DJMTEST
//#ifdef DCOMPATIBILITY
package com.substanceofcode.jmunit.rssreader.businessentities.compatibility1;

import java.util.Date;
import java.util.Vector;

import com.substanceofcode.jmunit.utilities.BaseTestCase;

import com.substanceofcode.jmunit.utilities.RssFeedStoreHolder;
import com.substanceofcode.rssreader.businessentities.compatibility1.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedInfo;
import com.substanceofcode.rssreader.businessentities.compatibility1.RssItem;

final public class RssFeedStoreTest extends BaseTestCase {

	public RssFeedStoreTest() {
		super(4, "compatibility1.RssFeedStoreTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testFeedStoreStr2();
				break;
			case 1:
				testFeedStoreStr3();
				break;
			case 2:
				testFeedStoreStr4b();
				break;
			case 3:
				testFeedStoreStr5();
				break;
			default:
				Exception e = new Exception(
						"No such test testNumber=" + testNumber);
				//#ifdef DLOGGING
				logger.severe("test no switch case failure #" +
						testNumber, e);
				//#endif
				throw e;
		}
	}

	public com.substanceofcode.rssreader.businessentities.RssItunesFeed feedStoreStrFactory(
			String mname,
			String step, int nbr,
			String suffix,
			boolean isItunes, long ldate, String explicit,
			long litem1Date, String explicit1, long litem2Date,
			String explicit2) throws Throwable {
		try {
			com.substanceofcode.rssreader.businessentities.RssItunesFeed feed =
				new com.substanceofcode.rssreader.businessentities.RssItunesFeed(
				"test" + nbr + suffix, "url" + nbr + suffix, "username" + nbr + suffix, "password" + nbr + suffix);
			feed.setUpddate(new Date(ldate).toString());
			if (isItunes) {
				feed.modifyItunes(
					true, "title" + nbr + suffix, "description" + nbr + suffix, "language" + nbr + suffix, "author" + nbr + suffix,
					"subtitle" + nbr + suffix, "summary" + nbr + suffix,
					com.substanceofcode.rssreader.businessentities.RssItunesItem.convExplicit(explicit));
			}
			Vector vitems = new Vector();
			if (isItunes && (litem1Date > 0L)) {
				com.substanceofcode.rssreader.businessentities.RssItunesItem item1 =
					new com.substanceofcode.rssreader.businessentities.RssItunesItem("title" + nbr + suffix, "link" + nbr + suffix, "desc" + nbr + suffix,
						new Date(litem1Date),
							"enclosure" + nbr + suffix, true,
							true,
							"author" + nbr + suffix,
							"subtitle" + nbr + suffix,
							"summary" + nbr + suffix,
							com.substanceofcode.rssreader.businessentities.RssItunesItem.convExplicit(explicit1),
							"50:00");
				vitems.addElement(item1);
			}

			if (isItunes && (litem2Date > 0L)) {
				com.substanceofcode.rssreader.businessentities.RssItunesItem item2 =
					new com.substanceofcode.rssreader.businessentities.RssItunesItem("title" + nbr + suffix + ".2", "link" + nbr + suffix + ".2", "desc" + nbr + suffix + ".2",
						new Date(litem2Date),
							"enclosure" + nbr + suffix + ".2", true,
							true,
							"author" + nbr + suffix + ".2",
							"subtitle" + nbr + suffix + ".2",
							"summary" + nbr + suffix + ".2",
							com.substanceofcode.rssreader.businessentities.RssItunesItem.convExplicit(explicit2),
							"50:20");
				vitems.addElement(item2);
			}

			feed.setItems(vitems);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " step " + step +
					" feed=" + feed.getClass().getName() + "," + feed);}
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

	public RssFeed feed1StoreTestStrFactory(String mname,
			String step, int nbr,
			String suffix,
			long ldate,
			long litem1Date, long litem2Date) throws Throwable {
		try {
			RssFeed feed = new RssFeed(
				"test" + nbr + suffix, "url" + nbr + suffix, "username" + nbr + suffix, "password" + nbr + suffix);
			Vector vitems = new Vector();
			if (litem1Date > 0L) {
				RssItem item1 = new RssItem(
						"title" + nbr + suffix, "link" + nbr + suffix, "desc" + nbr + suffix,
						new Date(litem1Date));
				vitems.addElement(item1);
			}

			if (litem2Date > 0L) {
				RssItem item2 = new RssItem("title" + nbr + suffix + ".2", "link" + nbr + suffix + ".2", "desc" + nbr + suffix + ".2",
						new Date(litem2Date));
				vitems.addElement(item2);
			}

			feed.setItems(vitems);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " step " + step +
					" infoFeed=" + feed.getClass().getName() + "," + feed);}
			//#endif
			return feed;
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("feed1StoreTestStrFactory  " + mname + " failure ",e);
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
			com.substanceofcode.rssreader.businessentities.RssItunesFeed feed =
				feedStoreStrFactory(mname,
									"1", nbr, suffix + "z",
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);

			RssFeed infoFeed = feed1StoreTestStrFactory(mname,
									"2", nbr, suffix,
									ldate, litem1Date,
									litem2Date);
			assertFalse(mname +
					" should be feeds not equal with different attributes.",
					infoFeed.equals((RssFeedInfo)feed));

			feed = feedStoreStrFactory(mname,
									"2", nbr, suffix,
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);

			assertTrue(mname + " feeds equal with the same attributes.",
					infoFeed.equals((RssFeedInfo)feed));

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
					fholder.feed, (RssFeed)fholder.infoFeed, null);

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
					fholder.feed, (RssFeed)fholder.infoFeed, null);

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
					fholder.feed, (RssFeed)fholder.infoFeed, null);
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
					fholder.feed, (RssFeed)fholder.infoFeed, null);

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
					fholder.feed, (RssFeed)fholder.infoFeed, null);
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
			com.substanceofcode.rssreader.businessentities.RssItunesFeed feed,
			RssFeed infoFeed,
			String storeString) throws Throwable {
		try {
			boolean genFeed = storeString != null;
			//#ifdef DLOGGING
			logger.info("storeStringTestSub Started " + mname);
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " serializeItems,encoded,genFeed=" + serializeItems + "," + encoded + "," + genFeed);}
			//#endif
			if (!genFeed) {
				storeString = infoFeed.getStoreString(serializeItems, encoded);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " storeString=" + storeString);}
			//#endif
			if (genFeed) {
				infoFeed = new RssFeed(storeString);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " infoFeed=" + infoFeed);}
			//#endif
			com.substanceofcode.rssreader.businessentities.RssItunesFeed nfeed =
				new com.substanceofcode.rssreader.businessentities.RssItunesFeed(
					new com.substanceofcode.rssreader.businessentities.RssFeed(true,
						encoded, storeString));
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub " + mname + " nfeed=" + nfeed);}
			//#endif
			assertEquals(mname + " new feed items = 0", 0,
					nfeed.getItems().size());
			assertEquals(mname + " new feed upddete = \"\"", "", nfeed.getUpddate());
			infoFeed.setItems(new Vector());
			infoFeed.setUpddate("");
			assertTrue(mname + " feeds equal without items.", infoFeed.equals(((RssFeedInfo)nfeed)));
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
