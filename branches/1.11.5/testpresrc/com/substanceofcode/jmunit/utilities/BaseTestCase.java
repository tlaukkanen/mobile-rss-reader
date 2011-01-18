//--Need to modify--#preprocess
/*
 * BaseTestCase.java
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
 * IB 2010-05-24 1.11.5RC2 Use BaseTestCase to log start of test.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Use conditional preprocessed cldc11 code with modifications instead of cldc10 code.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
 */

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define JMTESTPLUS define
@DJMTESTPLUSDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.utilities;

import java.util.Date;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;

import jmunit.framework.cldc11.Test;

import com.substanceofcode.rssreader.presentation.FeatureMgr;

import com.substanceofcode.jmunit.logging.LoggingTestCase;

abstract public class BaseTestCase extends LoggingTestCase {

	//#ifdef DLOGGING
	final protected String name;
	protected boolean started = false;
	protected int stopNbr = 1;
	//#endif

	public BaseTestCase(int nbr, String name) {
		super(nbr, name);
		//#ifdef DLOGGING
		this.name = name;
		//#endif
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			default:
				Exception e = new Exception(
						"No such test testNumber=" + testNumber);
				//#ifdef DLOGGING
				logger.severe("test no switch case test case #" +
						testNumber, e);
				//#endif
				throw e;
		}
	}

	public void setUp() throws Throwable {
		super.setUp();
		//#ifdef DLOGGING
		if (!started) {
			started = true;
			logger.info("Starting test case " + name);
			if (FeatureMgr.getDisplay() == null) {
				//#ifdef DJMTESTPLUS
				FeatureMgr.setDisplay(Display.getDisplay(Test.getTestMidlet()));
				//#else
				FeatureMgr.setDisplay(Display.getDisplay(this));
				//#endif
			}
			if (FeatureMgr.m_backCommand == null) {
				FeatureMgr.m_backCommand = new Command("Back", Command.BACK, 1);
			}
			if (FeatureMgr.m_exitCommand == null) {
				FeatureMgr.m_exitCommand = new Command("Exit",
					 Command.EXIT, 30);
			}
		}
		//#endif
	}

	public void tearDown() {
		super.tearDown();
		//#ifdef DLOGGING
		logger.info("Tear down test case name,stopNbr=" + name + "," + stopNbr);
		//#endif
	}

}
//#endif
