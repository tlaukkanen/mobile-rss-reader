/*
 * Settings2Test.java
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
 * IB 2010-06-27 1.11.5Dev2 Have 2nd settings test class.
*/

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.utils;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.jmunit.utilities.BaseTestCase;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.Settings;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;

final public class Settings2Test extends BaseTestCase {

	static Settings m_settings = null;
	static RssReaderSettings m_appSettings = null;
	final static String TEST_INT = "test-int";
	final static String TEST_LONG = "test-long";

	public Settings2Test() {
		super(13, "Settings2Test");
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
			case 3:
				testSettings4();
				break;
			case 4:
				testSettings5();
				break;
			case 5:
				testSettings6();
				break;
			case 6:
				testSettings7();
				break;
			case 7:
				testSettings8();
				break;
			case 8:
				testSettings9();
				break;
			case 9:
				testSettings10();
				break;
			case 10:
				testSettings11();
				break;
			case 11:
				testSettings12();
				break;
			case 12:
				testSettings13();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	public void setUp() throws Throwable {
		super.setUp();
		if (m_appSettings == null) {
			//#ifdef DTEST
			Settings.deleteStore();
			Settings.deleteSettings();
			//#endif
			//#ifdef DLOGGING
			Settings.listRecordStores();
			//#endif
			m_appSettings = RssReaderSettings.getInstance(this);
		}
		if (m_settings == null) {
			m_settings = m_appSettings.getSettingsInstance();
		}
	}

	public void testSettings1() throws Throwable {
		String mname = "testSettings1";
		boolean itemsEncoding = m_settings.getBooleanProperty(
				Settings.ITEMS_ENCODED, false);
		settingsTestSub(mname, 0, Settings.ITEMS_ENCODED, new Boolean(true),
				new Boolean(itemsEncoding));
		m_settings.save(0, false);
	}

	public void testSettings2() throws Throwable {
		String mname = "testSettings2";
		long storeDate = m_settings.getLongProperty(Settings.STORE_DATE, 64L);
		settingsTestSub(mname, 0, Settings.STORE_DATE, new Long(storeDate),
				new Long(storeDate));
		m_settings.save(0, false);
	}

	public void testSettings3() throws Throwable {
		String mname = "testSettings3";
		boolean markUnreadItems = m_appSettings.getMarkUnreadItems();
		appSettingsTestSub(mname, "markUnreadItems", new Boolean(false), new Boolean(markUnreadItems));
		m_settings.save(0, false);
	}

	public void testSettings4() throws Throwable {
		String mname = "testSettings4";
		boolean getFeedListOpen = m_appSettings.getFeedListOpen();
		appSettingsTestSub(mname, "getFeedListOpen", new Boolean(false), new Boolean(getFeedListOpen));
		m_settings.save(0, false);
	}

	public void testSettings5() throws Throwable {
		String mname = "testSettings5";
		m_appSettings.setUseStandardExit(true);
		boolean useStandardExit = m_appSettings.getUseStandardExit();
		appSettingsTestSub(mname, "useStandardExit", new Boolean(true), new Boolean(useStandardExit));
		m_settings.save(0, false);
	}

	public void testSettings6() throws Throwable {
		String mname = "testSettings6";
		m_appSettings.setFeedListOpen(false);
		boolean getFeedListOpen = m_appSettings.getFeedListOpen();
		appSettingsTestSub(mname, "getFeedListOpen", new Boolean(false), new Boolean(getFeedListOpen));
		m_settings.save(0, false);
	}

	public void testSettings7() throws Throwable {
		String mname = "testSettings7";
		int intNo = m_settings.getIntProperty(TEST_INT, 45);
		settingsTestSub(mname, 0, TEST_INT, new Integer(45),
				new Integer(intNo));
		m_settings.save(0, false);
	}

	public void testSettings8() throws Throwable {
		String mname = "testSettings8";
		int intNo = m_settings.getIntProperty(TEST_INT, 88);
		settingsTestSub(mname, 0, TEST_INT, new Integer(88),
				new Integer(intNo));
		m_settings.save(0, false);
	}

	public void testSettings9() throws Throwable {
		String mname = "testSettings9";
		int maxCount = m_appSettings.getMaxWordCountInDesc();
		appSettingsTestSub(mname, "maxCount", new Integer(10),
				new Integer(maxCount));
		m_settings.save(0, false);
	}

	public void testSettings10() throws Throwable {
		String mname = "testSettings10";
		m_appSettings.setMaxWordCountInDesc(2);
		int maxCount = m_appSettings.getMaxWordCountInDesc();
		appSettingsTestSub(mname, "maxCount", new Integer(2),
				new Integer(maxCount));
		m_settings.save(0, false);
	}

	public void testSettings11() throws Throwable {
		String mname = "testSettings11";
		long testLong = m_settings.getLongProperty( TEST_LONG, 23L);
		settingsTestSub(mname, 0, TEST_LONG, new Long(23L), new Long(testLong));
		m_settings.save(0, false);
	}

	public void testSettings12() throws Throwable {
		String mname = "testSettings12";
		long testLong = m_settings.getLongProperty(TEST_LONG, 64L);
		settingsTestSub(mname, 0, TEST_LONG, new Long(64L), new Long(testLong));
		m_settings.save(0, false);
	}

	public void testSettings13() throws Throwable {
		String mname = "testSettings13";
		m_settings.setLongProperty(TEST_LONG, 423L);
		long testLong = m_settings.getLongProperty(TEST_LONG, 64L);
		settingsTestSub(mname, 0, TEST_LONG, new Long(423L), new Long(testLong));
		m_settings.save(0, false);
	}

	void settingsTestSub(String mname, int region, String name, Object oexpValue,
			Object ogetValue)
	throws Throwable {
		try {
			if (oexpValue instanceof Boolean) {
				boolean getValue = ((Boolean)ogetValue).booleanValue();
				boolean expValue = ((Boolean)oexpValue).booleanValue();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub mname,name,expValue,getValue=" + mname + "," + name + "," + expValue + "," + getValue);}
				//#endif
				assertEquals("Value must match expected value for 1 name=" + name, expValue, 
						getValue);
				boolean actValue = m_settings.getBooleanProperty(
					name, !expValue);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub mname,name,expValue,getValue=" + mname + "," + name + "," + expValue + "," + getValue);}
				//#endif
				assertEquals("Value must match expected value for 2 name=" + name, expValue, 
						actValue);
				actValue = m_settings.getBooleanProperty(
					name, expValue);
				assertEquals("Value must match expected value for 3 name=" + name, expValue, 
						actValue);
			} else if (oexpValue instanceof String) {
				String getValue = (String)ogetValue;
				String expValue = (String)oexpValue;
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub mname,name,expValue,getValue=" + mname + "," + name + "," + expValue + "," + getValue);}
				//#endif
				assertEquals("Value must match expected value for 1 name=" + name, expValue, 
						getValue);
				String actValue = m_settings.getStringProperty(0,
					name, "");
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub expValue,actValue=" + expValue + "," + actValue);}
				//#endif
				assertEquals("Value must match expected value for 2 name=" + name, expValue, 
						actValue);
				actValue = m_settings.getStringProperty(0,
					name, expValue);
				assertEquals("Value must match expected value for 3 name=" + name, expValue, 
						actValue);
			} else if (oexpValue instanceof Integer) {
				int getValue = ((Integer)ogetValue).intValue();
				int expValue = ((Integer)oexpValue).intValue();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub mname,name,expValue,getValue=" + mname + "," + name + "," + expValue + "," + getValue);}
				//#endif
				assertEquals("Value must match expected value for 1 name=" + name, expValue, 
						getValue);
				int actValue = m_settings.getIntProperty(
					name, 99);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub expValue,actValue=" + expValue + "," + actValue);}
				//#endif
				assertEquals("Value must match expected value for 2 name=" + name, expValue, 
						actValue);
				actValue = m_settings.getIntProperty(
					name, expValue);
				assertEquals("Value must match expected value for 3 name=" + name, expValue, 
						actValue);
			} else if (oexpValue instanceof Long) {
				long getValue = ((Long)ogetValue).longValue();
				long expValue = ((Long)oexpValue).longValue();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub mname,name,expValue,getValue=" + mname + "," + name + "," + expValue + "," + getValue);}
				//#endif
				long actValue = m_settings.getLongProperty(
					name, 99L);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("settingsTestSub expValue,actValue=" + expValue + "," + actValue);}
				//#endif
				assertEquals("Value must match expected value for 1 name=" + name, expValue, 
						actValue);
				actValue = m_settings.getLongProperty(
					name, expValue);
				assertEquals("Value must match expected value for 2 name=" + name, expValue, 
						actValue);
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("settingsTestSub " + mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	void appSettingsTestSub(String mname, String name, Object oexpValue,
			Object oactValue)
	throws Throwable {
		try {
			if (oexpValue instanceof Boolean) {
				boolean expValue = ((Boolean)oexpValue).booleanValue();
				boolean actValue = ((Boolean)oactValue).booleanValue();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("appSettingsTestSub mname,name,expValue,actValue=" + mname + "," + name + "," + expValue + "," + actValue);}
				//#endif
				assertEquals("Value must match expected value for name=" + name, expValue, 
						actValue);
			} else if (oexpValue instanceof String) {
				String expValue = (String)oexpValue;
				String actValue = (String)oactValue;
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("appSettingsTestSub mname,name,expValue,actValue=" + mname + "," + name + "," + expValue + "," + actValue);}
				//#endif
				assertEquals("Value must match expected value for name=" + name, expValue, 
						actValue);
			} else if (oexpValue instanceof Long) {
				long expValue = ((Long)oexpValue).longValue();
				long actValue = ((Long)oactValue).longValue();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("appSettingsTestSub mname,name,expValue,actValue=" + mname + "," + name + "," + expValue + "," + actValue);}
				//#endif
				assertEquals("Value must match expected value for name=" + name, expValue, 
						actValue);
			}
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
		//#ifdef DTEST
		Settings.deleteStore();
		if (Settings2Test.m_appSettings != null) {
			Settings2Test.m_appSettings.deleteSettings();
		}
		//#endif
		Settings2Test.m_settings = null;
		Settings2Test.m_appSettings = null;
	}
}
//#endif
