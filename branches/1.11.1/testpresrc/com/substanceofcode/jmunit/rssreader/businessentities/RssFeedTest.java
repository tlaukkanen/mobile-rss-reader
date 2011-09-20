//--Need to modify--#preprocess
/*
 * RssFeedTest.java
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
 * IB 2011-02-02 1.11.5Dev17 Test creating store string and creating from the store string to make sure that it works.
 */

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.rssreader.businessentities;

import java.util.Date;
import java.util.Vector;

import com.substanceofcode.utils.MiscUtil;

import com.substanceofcode.jmunit.utilities.BaseTestCase;

import com.substanceofcode.jmunit.utilities.RssFeedStoreHolder;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businessentities.RssItemInfo;

final public class RssFeedTest extends BaseTestCase {

	public RssFeedTest() {
		super(5, "RssFeedTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testFeed2();
				break;
				/*
			case 1:
				testFeed3();
				break;
			case 2:
				testFeed4();
				break;
			case 3:
				testFeed4b();
				break;
			case 4:
				testFeed5();
				break;
				*/
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

	public RssItunesFeed FeedFactory(String mname,
			String step, int nbr,
			String suffix,
			boolean isItunes, long ldate, String explicit,
			long litem1Date, String explicit1, long litem2Date,
			String explicit2) throws Throwable {
		try {
			RssItunesFeed feed =
				new RssItunesFeed("test" + nbr + suffix, "url" + nbr + suffix, "username" + nbr + suffix, "password" + nbr + suffix);
			feed.setUpddate(new Date(ldate).toString());
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

			RssItemInfo[] ritems = (RssItemInfo[])MiscUtil.getVecrItemf(vitems);
			feed.setItems(ritems);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " step " + step +
					" feed=" + feed);}
			//#endif
			return feed;
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("FeedFactory  " + mname + " failure ",e);
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
			RssItunesFeed feed = FeedFactory(mname,
									"1", nbr, suffix + "z",
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);

			RssItunesFeed infoFeed = FeedFactory(mname,
									"2", nbr, suffix,
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);
			assertFalse(mname +
					" should be feeds not equal with different attributes.",
					infoFeed.equals(feed));

			feed = FeedFactory(mname,
									"2", nbr, suffix,
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);

			assertTrue(mname + " feeds equal with the same attributes.",
					infoFeed.equals(feed));

			//#ifdef DITUNES
			infoFeed.setDate(null);
			infoFeed.setLink("");
			//#endif
			infoFeed.setItems(new RssItemInfo[0]);
			infoFeed.setEtag("");
			infoFeed.setUpddate("");
			infoFeed.setTitle("");
			infoFeed.setDescription("");
			infoFeed.setLanguage("");
			infoFeed.setAuthor("");
			infoFeed.setSubtitle("");
			infoFeed.setSummary("");
			infoFeed.setExplicit("");

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

	public void testFeed2() throws Throwable {
		String mname = "testFeed2";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 2, "",
			true, cdate, "yes",
			0L, "", 0L,
			"");

			storeStringTestSub(mname, true, true,
					false,
					fholder.feed, (RssItunesFeed)fholder.infoFeed, null);

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	/*
	public void testFeed3() throws Throwable {
		String mname = "testFeed3";
		try {
			long cdate = System.currentTimeMillis();
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 2, "",
			true, cdate, "yes",
			cdate + 5500L, "no", 0L,
			"");

			storeStringTestSub(mname, true, true,
					false,
					fholder.feed, (RssItunesFeed)fholder.infoFeed, null);

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeed4() throws Throwable {
		String mname = "testFeed4";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 4, "",
			true, cdate, "yes",
			cdate + 5500L, "no", cdate + 6600L,
			"explicit");

			storeStringTestSub(mname, true, true,
					false,
					fholder.feed, (RssItunesFeed)fholder.infoFeed, null);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeed4b() throws Throwable {
		String mname = "testFeed4b";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 4, "b",
			true, cdate, "explicit",
			cdate + 5500L, "yes", cdate + 8800L,
			"no");

			storeStringTestSub(mname, true, true,
					false,
					fholder.feed, (RssItunesFeed)fholder.infoFeed, null);

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeed5() throws Throwable {
		String mname = "testFeed5";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssFeedStoreHolder fholder = feedStoreTestStrSub(mname, 5, "",
			true, cdate, "yes",
			cdate + 5500L, "no", cdate + 7000L,
			"explicit");

			storeStringTestSub(mname, true, true,
					false,
					fholder.feed, (RssItunesFeed)fholder.infoFeed, null);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}
	*/

    public void storeStringTestSub(final String mname,
			final boolean serializeItems, final boolean encoded,
			final boolean modifyCapable,
			RssItunesFeed feed,
			RssItunesFeed infoFeed,
			String storeString) throws Throwable {
		try {
			boolean genFeed = storeString != null;
			//#ifdef DLOGGING
			logger.info("storeStringTestSub Started " + mname);
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " serializeItems,encoded,modifyCapable,genFeed=" + serializeItems + "," + encoded + "," + modifyCapable + "," + genFeed);}
			//#endif
			if (!genFeed) {
				storeString = feed.getStoreString(true, serializeItems,
						encoded);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " storeString=" + storeString);}
			//#endif
			if (genFeed) {
				infoFeed = RssItunesFeed.deserialize(true, encoded,
								storeString);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " infoFeed=" + infoFeed);}
			//#endif
			RssItunesFeed nfeed = RssItunesFeed.deserialize(
								modifyCapable, encoded, storeString);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub " + mname + " nfeed=" + MiscUtil.toString(nfeed, false, 80));}
			//#endif
			assertEquals(mname + " new feed upddete = \"\"", "", nfeed.getUpddate());
			assertTrue(mname + " feeds equal items.", feed.equals(nfeed));
			String storeString2 = feed.getStoreString(false, false, encoded);
			nfeed = RssItunesFeed.deserialize(modifyCapable, encoded,
					storeString2);
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
