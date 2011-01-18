//--Need to modify--#preprocess
/*
 * Settings1Test.java
 *
 * Copyright (C) 2009-2010 Irving Bunton, Jr
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
 * IB 2010-06-27 1.11.5Dev2 Have 1st settings test class.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for RssReaderSettings.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Optionally only compile this if the code is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
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
package com.substanceofcode.jmunit.utils;

import com.substanceofcode.jmunit.utilities.BaseTestCase;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.Settings;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;

final public class Settings1Test extends BaseTestCase {

	static Settings m_settings = null;
	static RssReaderSettings m_appSettings = null;

	//#ifdef DLOGGING
	boolean logListStores = false; // Possbily set to traceEnabled.
	//#endif

	public Settings1Test() {
		super(3, "Settings1Test");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testSettings1();
				break;
			case 1:
				testSettings2();
				break;
			case 2:
				testSettings3();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	public void setUp() throws Throwable {
		super.setUp();
		if (m_appSettings == null) {
			//#ifdef DLOGGING
			if (logListStores) {
				Settings.listRecordStores();
			}
			//#endif
			m_appSettings = RssReaderSettings.getInstance();
		}
		if (m_settings == null) {
			m_settings = m_appSettings.getSettingsInstance();
		}
	}

	public void testSettings1() throws Throwable {
		String mname = "testSettings1";
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("settingsTestSub " + mname + " m_settings.isInitialized(),m_settings.getInitRecs()=" + m_settings.isInitialized() + "," + m_settings.getInitRecs());}
		//#endif
		boolean markUnreadItems = m_appSettings.getMarkUnreadItems();
		//#ifdef DMIDP20
		appSettingsTestSub(mname, "markUnreadItems", m_settings.isInitialized() ? false : true, markUnreadItems);
		//#else
		appSettingsTestSub(mname, "markUnreadItems", m_settings.isInitialized() ? true : false, markUnreadItems);
		//#endif
		m_settings.save(0, false);
	}

	public void testSettings2() throws Throwable {
		String mname = "testSettings2";
		//#ifdef DMIDP20
		m_appSettings.setMarkUnreadItems(false);
		//#else
		m_appSettings.setMarkUnreadItems(true);
		//#endif
		boolean markUnreadItems = m_appSettings.getMarkUnreadItems();
		//#ifdef DMIDP20
		appSettingsTestSub(mname, "markUnreadItems", false, markUnreadItems);
		//#else
		appSettingsTestSub(mname, "markUnreadItems", true, markUnreadItems);
		//#endif
		m_settings.save(0, true);
		//#ifdef DTEST
		m_appSettings.deleteSettings();
		//#endif
		m_appSettings = null;
		m_settings = null;
	}

	public void testSettings3() throws Throwable {
		String mname = "testSettings3";
		boolean markUnreadItems = m_appSettings.getMarkUnreadItems();
		//#ifdef DMIDP20
		appSettingsTestSub(mname, "markUnreadItems", false, markUnreadItems);
		//#else
		appSettingsTestSub(mname, "markUnreadItems", true, markUnreadItems);
		//#endif
	}

	void appSettingsTestSub(String mname, String name, boolean expValue,
			boolean actValue)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("appSettingsTestSub " + mname + " expValue,actValue=" + expValue + "," + actValue);}
			//#endif
			assertEquals("Value must match expected value.", expValue, 
					actValue);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("appSettingsTestSub " + mname + " finished.");}
			//#endif
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("appSettingsTestSub " + mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void tearDown() {
		super.tearDown();
	}
}
//#endif
