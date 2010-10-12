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
 */

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.utilities;

import java.util.Date;

import jmunit.framework.cldc10.TestCase;

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

	public void setUp() throws Throwable {
		super.setUp();
		//#ifdef DLOGGING
		if (!started) {
			started = true;
			logger.info("Starting test case " + name);
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
