//--Need to modify--#preprocess
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
 * IB 2010-05-24 1.11.5RC2 Use BaseTestCase to log start of test.
 * IB 2010-05-29 1.11.5RC2 Use ready only for MIDP 2.0 with observer pattern.
 * IB 2010-06-29 1.11.5RC2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-06-29 1.11.5RC2 Don't use midlet in makeObserable.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile code/features present only in the full version if this compile is for the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use notifyAll to avoid waiting with wait.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
 * IB 2011-01-24 1.11.5Dev16 Fix code placement for using JMUnit on a device.
 * IB 2011-01-24 1.11.5Dev16 Don't compile unneeded code for internet link version.
 * IB 2011-02-02 1.11.5Dev17 Allow optional saving of only the feed header name, user/pass, and link.
 * IB 2011-02-02 1.11.5Dev17 Change items to array to save on memory and for simplicity.
*/

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
//#ifdef DFULLVERS
package com.substanceofcode.jmunit.rssreader.businessentities;

import java.util.Date;
import java.util.Vector;

import com.substanceofcode.jmunit.utilities.BaseTestCase;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

final public class RssFeedStoreStrTest extends BaseTestCase
//#ifdef DMIDP20
implements Observer
//#endif
{

	//#ifdef DMIDP20
	private boolean ready = false;
	//#endif

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
				Exception e = new Exception(
						"No such test testNumber=" + testNumber);
				//#ifdef DLOGGING
				logger.severe("test no switch case failure #" +
						testNumber, e);
				//#endif
				throw e;
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

	private void waitReady() throws Throwable {
		while (!isReady()) {
			synchronized(this) {
				wait(500L);
			}
		}
	}
	//#endif

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

			feed.setVecItems(vitems);
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

			RssFeedParser fparser = new RssFeedParser(feed, null, false);
			//#ifdef DMIDP20
			fparser.makeObserable(true, 10);
			ready = false;
			fparser.getObservableHandler().addObserver(this);
			fparser.getParsingThread().start();
			waitReady();
			//#else
			try {
				fparser.parseRssFeed( false, 10);
			} catch(Throwable e) {
				//#ifdef DLOGGING
				logger.severe(mname + " failure parseRssFeed feed=" + feed.getName() + "," + feed.getUrl(),e);
				//#endif
				throw e;
			}
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
			boolean genFeed = storeString != null;
			//#ifdef DLOGGING
			logger.info("storeStringTestSub Started " + mname);
			if (finestLoggable) {logger.finest("storeStringTestSub  " + mname + " saveHdr,serializeItems,encoded,modifyCapable,genFeed=" + saveHdr + "," + serializeItems + "," + encoded + "," + modifyCapable + "," + genFeed);}
			//#endif
			if (!genFeed) {
				storeString = feed.getStoreString(saveHdr, serializeItems, encoded);
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
						nfeed.getItems().length);
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
//#endif
