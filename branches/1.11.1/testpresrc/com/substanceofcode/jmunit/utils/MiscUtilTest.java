//--Need to modify--#preprocess
/*
 * MiscUtilTest.java
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
 * IB 2011-02-07 1.11.5Dev17 Create test fo MiscUtil in MiscUtilTest.
*/

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
//#ifdef DFULLVERS
package com.substanceofcode.jmunit.utils;

import java.util.Date;
import java.util.Vector;

import com.substanceofcode.rssreader.businessentities.RssItem;
import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
import com.substanceofcode.utils.MiscUtil;

import com.substanceofcode.jmunit.utilities.BaseTestCase;

public class MiscUtilTest extends BaseTestCase
{

	public MiscUtilTest() {
		super(2, "MiscUtilTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				miscUtilTest3();
				break;
			case 1:
				miscUtilTest4();
				break;
				/*
			case 4:
				miscUtilTest5();
				break;
			case 5:
				miscUtilTest6();
				break;
			case 6:
				miscUtilTest7();
				break;
			case 7:
				miscUtilTest8();
				break;
			*/
			default:
				super.test(testNumber);
		}
	}

	public void miscUtilTest3() throws Throwable {
		String mname = "miscUtilTest3";
		RssItem[] expObjs = new RssItem[] {
			new RssItem("title1", "link1", "desc1", null, "", true),
			new RssItem("title2", "link2", "desc2", null, "", true)};
		Vector inpVec = new Vector();
		inpVec.addElement(new RssItem("title1","link1", "desc1", null, "",
					true));
		inpVec.addElement(new RssItem("title2", "link2", "desc2", null, "",
					true));
		RssItem[] actObjs = MiscUtil.getVecrItem(inpVec);
		miscUtilTestSub(mname,
				"Convert from object array to vector with two integers.",
				"import com.substanceofcode.rssreader.businessentities.RssItem",
				expObjs, null,
				actObjs, null);
	}

	public void miscUtilTest4() throws Throwable {
		String mname = "miscUtilTest4";
		RssItem[] expObjs = new RssItem[] {
			new RssItunesItem("title1", "link1", "desc1", (Date)null, "", true,
					true, "author1", "subtitle1", "summary1", (byte)0, "1:00"),
				new RssItunesItem("title2", "link2", "desc2", (Date)null, "",
						true, true, "author2", "subtitle2", "summary2", (byte)0,
						"1:10")};
		Vector inpVec = new Vector();
		inpVec.addElement(new RssItem("title1","link1", "desc1", null, "",
					true));
		inpVec.addElement(new RssItem("title2", "link2", "desc2", null, "",
					true));
		RssItem[] actObjs = MiscUtil.getVecrItem(inpVec);
		miscUtilTestSub(mname,
				"Convert from object array to vector with two integers.",
				"import com.substanceofcode.rssreader.businessentities.RssItem",
				expObjs, null,
				actObjs, null);
	}

    public void miscUtilTestSub(final String mname,
			String cmpMsg,
			String expClass,
			Object[] expObjs, Vector expVec,
			Object[] actObjs, Vector actVec)
	throws Throwable {
		int ix = 0;
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname + " miscUtilTestSub");
			//#endif
			boolean bexpObj;
			int elen = (bexpObj = (expObjs != null)) ?
				expObjs.length : expVec.size();
			boolean bactObj;
			int alen = (bactObj = (actObjs != null)) ?
				actObjs.length : actVec.size();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " miscUtilTestSub elen,expObjs,expVec,alen,actObjs,actVec=" + elen + "," + (expObjs != null) + "," + (expVec != null) + "," + alen + "," + (actObjs != null) + "," + (actVec != null));}
			//#endif
			assertEquals("Lengths not equal elen,alen=" + elen  + "," + alen,
					elen, alen);
			for (; (ix < elen) && (ix < alen); ix++) {
				Object eval = bexpObj ? expObjs[ix] : expVec.elementAt(ix);
				Object aval = bactObj ? actObjs[ix] : actVec.elementAt(ix);
				if (ix == 0) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(mname + " miscUtilTestSub aval.getClass().getName()=" + aval.getClass().getName());}
					//#endif
				}
				if (eval instanceof RssItem) {
					assertTrue(cmpMsg + " values must be equal RssItem.",
							((RssItem)eval).equals((RssItem)aval));
				} else if (eval instanceof RssItunesItem) {
					assertTrue(cmpMsg + " values must be equal RssItunesItem.",
							((RssItunesItem)eval).equals((RssItunesItem)aval));
				} else {
					assertEquals(cmpMsg + " values must be equal unknown Object.",
							eval, aval);
				}
			}
			//#ifdef DLOGGING
			logger.info("Test " + mname + " PASSED.");
			//#endif
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " miscUtilTestSub failure ix=" + ix,e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

}
//#endif
//#endif
