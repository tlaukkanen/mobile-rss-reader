//--Need to modify--#preprocess
/*
 * EncodingUtilTest.java
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
 * IB 2010-10-12 1.11.5Dev9 New class to test EncodingUtil.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-15 1.11.5Dev14 Remove redundant new String of literal string.
*/

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
package com.substanceofcode.jmunit.utils;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Vector;

import com.substanceofcode.jmunit.utils.XmlRequest;

import com.substanceofcode.jmunit.utilities.BaseTestCase;

import com.substanceofcode.utils.EncodingUtil;

final public class EncodingUtilTest extends BaseTestCase
{

	public EncodingUtilTest() {
		super(1, "EncodingUtilTest");
	}

	EncodingUtil m_encodingInstance = null;

	public void setUp() throws Throwable {
		super.setUp();
		if (m_encodingInstance == null) {
			m_encodingInstance = EncodingUtil.getInstance();
		}
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testEncodingUtil1();
				break;
			default:
				Exception e = new Exception(
						"No such test testNumber=" + testNumber);
				//#ifdef DLOGGING
				logger.severe("test no switch case XmlRequest.failure #" +
						testNumber, e);
				//#endif
				throw e;
		}
	}

	public void testEncodingUtil1() throws Throwable {
		String mname = "testEncodingUtil1";
		try {
			// This may change if errors are encountered.
			assertNotNull("m_sglStatExcs must not be null",
					m_encodingInstance.getStatExcs());
			assertNotNull("m_sglIsoEncoding must not be null",
					m_encodingInstance.m_sglIsoEncoding);
			assertNotNull("m_sglWinEncoding must not be null",
					m_encodingInstance.getWinEncoding());
			assertNotNull("m_sglIsoCommonEntities must not be null",
					m_encodingInstance.getIsoCommonEntities());
			assertNotNull("m_sglIsoSpecialEntities must not be null",
					m_encodingInstance.getIsoSpecialEntities());
			assertNotNull("m_sglIsoSpecialValues must not be null",
					m_encodingInstance.getIsoSpecialValues());
			assertNotNull("m_sglIsoCommValues must not be null",
					m_encodingInstance.getIsoCommValues());
			assertNotNull("m_sglIsoLatin1Entities must not be null",
					m_encodingInstance.getIsoLatin1Entities());
			assertNotNull("m_sglWinIsoConvx80 must not be null",
					m_encodingInstance.m_sglWinIsoConvx80);
			assertNotNull("m_sglConvIso88591 must not be null",
					m_encodingInstance.m_sglConvIso88591);
			assertNotNull("m_sglConvCp1252 must not be null",
					m_encodingInstance.m_sglConvCp1252);
			assertNotNull("SGL_WRIGHT_SGLE_QUOTE not be null",
					m_encodingInstance.SGL_WRIGHT_SGLE_QUOTE);
			assertNotNull("SGL_RIGHT_SGLE_QUOTE not be null",
					m_encodingInstance.SGL_RIGHT_SGLE_QUOTE);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

}
//#endif
//#endif
