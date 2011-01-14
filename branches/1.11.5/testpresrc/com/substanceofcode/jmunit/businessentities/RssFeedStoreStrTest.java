/*
 * RssFeedStoreStrTest.java
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
 * IB 2010-03-07 1.11.4RC1 Don't use observer pattern for MIDP 1.0 as it increases size.
 * IB 2010-03-14 1.11.5RC2 Fixed problem with conditional get.
*/

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.businessentities;

import java.util.Date;
import java.util.Vector;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.jmunit.logging.LoggingTestCase;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
//#ifdef DMIDP20
import net.eiroca.j2me.observable.Observer;
import net.eiroca.j2me.observable.Observable;
//#endif

final public class RssFeedStoreStrTest extends LoggingTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	private boolean ready = false;

	public RssFeedStoreStrTest() {
		super(7, "RssFeedStoreStrTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testFeedStoreStr1();
				break;
			case 1:
				testFeedStoreStr2();
				break;
			case 2:
				testFeedStoreStr3();
				break;
			case 3:
				testFeedStoreStr4();
				break;
			case 4:
				testFeedStoreStr4b();
				break;
			case 5:
				testFeedStoreStr5();
				break;
			case 6:
				testFeedStoreStr6();
				break;
			default:
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

	public RssItunesFeed feedStoreStrFactory(String mname,
			String step, int nbr,
			String suffix,
			boolean isItunes, long ldate, byte explicit,
			long litem1Date, byte explicit1, long litem2Date,
			byte explicit2) throws Throwable {
		try {
			RssItunesFeed feed = new RssItunesFeed(
				"test" + nbr + suffix, "url" + nbr + suffix, "username" + nbr + suffix, "password" + nbr + suffix);
			feed.setUpddate(new Date(ldate).toString());
			if (isItunes) {
				feed.modifyItunes(
					true, "title" + nbr + suffix, "description" + nbr + suffix, "language" + nbr + suffix, "author" + nbr + suffix,
					"subtitle" + nbr + suffix, "summary" + nbr + suffix,
					explicit);
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
							explicit1,
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
							explicit2,
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

	public RssItunesFeed feedStoreStrTestSub(String mname, int nbr, String suffix,
			boolean isItunes, long ldate, byte explicit,
			long litem1Date, byte explicit1, long litem2Date,
			byte explicit2) throws Throwable {
		try {
			RssItunesFeed feed = feedStoreStrFactory(mname,
									"1", nbr, suffix + "z",
									isItunes, ldate, explicit, litem1Date,
									explicit1, litem2Date, explicit2);

			return feed;

		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("feedStoreStrTestSub  " + mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr1() throws Throwable {
		String mname = "testFeedStoreStr1";
		try {
			long cdate = System.currentTimeMillis();
			RssItunesFeed feed = feedStoreStrTestSub(mname, 1, "",
			false, cdate, RssItunesItem.convExplicit(""),
			0L, RssItunesItem.convExplicit(""), 0L,
			RssItunesItem.convExplicit(""));
			storeStringTestSub(mname, true, true, true, true, true,
					feed, null);
			
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr2() throws Throwable {
		String mname = "testFeedStoreStr2";
		try {
			long cdate = System.currentTimeMillis() + 3000L;
			RssItunesFeed feed = feedStoreStrTestSub(mname, 2,
					"",
			true, cdate, RssItunesItem.convExplicit("yes"),
			0L, RssItunesItem.convExplicit(""), 0L,
			RssItunesItem.convExplicit(""));

			storeStringTestSub(mname, true, true, true, true, true,
					feed, null);

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
			RssItunesFeed feed = feedStoreStrTestSub(mname, 2, "",
			true, cdate, RssItunesItem.convExplicit("yes"),
			cdate + 5500L, RssItunesItem.convExplicit("no"), 0L,
			RssItunesItem.convExplicit(""));

			storeStringTestSub(mname, true, true, true, true, true,
					feed, null);

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
			RssItunesFeed feed = feedStoreStrTestSub(mname, 4, "",
			true, cdate, RssItunesItem.convExplicit("yes"),
			cdate + 5500L, RssItunesItem.convExplicit("no"), cdate + 6600L,
			(byte)0);

			storeStringTestSub(mname, true, true, true, true, true,
					feed, null);
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
			RssItunesFeed feed = feedStoreStrTestSub(mname, 4, "b",
			true, cdate, (byte)1,
			cdate + 5500L, RssItunesItem.convExplicit("yes"), cdate + 8800L,
			RssItunesItem.convExplicit("no"));

			storeStringTestSub(mname, true, true, true, true, true,
					feed, null);

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
			RssItunesFeed feed = feedStoreStrTestSub(mname, 5, "",
			true, cdate, RssItunesItem.convExplicit("yes"),
			cdate + 5500L, RssItunesItem.convExplicit("no"), cdate + 7000L,
			(byte)2);

			storeStringTestSub(mname, true, true, true, true, true,
					feed, null);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testFeedStoreStr6() throws Throwable {
		String mname = "testFeedStoreStr6";
		try {
			RssItunesFeed feed = new RssItunesFeed(
				"test3", "jar:///rss-1252.xml", "", "");

			RssFeedParser fparser = new RssFeedParser(feed);
			//#ifdef DMIDP20
			fparser.makeObserable(null, true, 10);
			ready = false;
			fparser.getObserverManager().addObserver(this);
			fparser.getParsingThread().start();
			while (!isReady()) {
				synchronized(this) {
					wait(1000L);
				}
			}
			//#else
			fparser.parseRssFeed( false, 10);
			//#endif
			feed = fparser.getRssFeed();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " exc,successfull=" + fparser.getEx() + "," + fparser.isSuccessfull());}
			if (finestLoggable) {logger.finest(mname + " feed read feed=" +
					feed);}
			//#endif
			assertNull("Should be no exception.", fparser.getEx());

			storeStringTestSub(mname, true, true, true, true, true,
					feed, null);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

    public void storeStringTestSub(final String mname, final boolean saveHdr,
			final boolean serializeItems, final boolean encoded,
			boolean itemsSaved, boolean modifyCapable,
			RssItunesFeed feed,
			String storeString) throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("storeStringTestSub Started " + mname);
			boolean genFeed = storeString != null;
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " saveHdr,serializeItems,encoded,modifyCapable,genFeed=" + saveHdr + "," + serializeItems + "," + encoded + "," + modifyCapable + "," + genFeed);}
			//#endif
			if (!genFeed) {
				storeString = feed.getStoreString(serializeItems, encoded);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " storeString=" + storeString);}
			//#endif
			if (genFeed) {
				feed = RssItunesFeed.deserialize(true, encoded,
								storeString);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " feed=" + feed);}
			//#endif
			RssItunesFeed nfeed = RssItunesFeed.deserialize(true, encoded,
								storeString);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("storeStringTestSub " + mname + " nfeed=" + nfeed);}
			//#endif
			assertTrue(mname + " feeds equal.", feed.equals(nfeed));
			if (!itemsSaved) {
				assertEquals(mname + " new feed items = 0", 0,
						nfeed.getItems().size());
			}
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
