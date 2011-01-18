//--Need to modify--#preprocess
/*
 * SortTest.java
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
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-04-26 1.11.5RC2 Set link to "" as it's not read by previous version.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.utils.compatibility4;

import java.util.Date;

import com.substanceofcode.utils.compatibility4.SortUtil;
import com.substanceofcode.utils.compatibility4.CauseException;
import com.substanceofcode.utils.MiscUtil;

import com.substanceofcode.jmunit.utilities.BaseTestCase;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
  *
  @author Irv Bunton
 */
final public class SortTest extends BaseTestCase {

	private int[] indexes = null;

	public SortTest() {
		super(8, "compatibility4.SortTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testSort1();
				break;
			case 1:
				testSort2();
				break;
			case 2:
				testSort3();
				break;
			case 3:
				testSort4();
				break;
			case 4:
				testSort5();
				break;
			case 5:
				testSort6();
				break;
			case 6:
				testSort7();
				break;
			case 7:
				testSort8();
				break;
			default:
				fail("Bad number for switch testNumber=" + testNumber);
				break;
		}
	}

	/* Test sort. */
	public void testSort1() throws Throwable {
		String mname = "testSort1";
		compatibilitySortTestSub(mname,
				new long[] {5L},
				new long[] {5L});
	}

	/* Test sort. */
	public void testSort2() throws Throwable {
		String mname = "testSort2";
		compatibilitySortTestSub(mname,
				new long[] {5L, 0L},
				new long[] {0L, 5L});
	}

	/* Test sort. */
	public void testSort3() throws Throwable {
		String mname = "testSort3";
		compatibilitySortTestSub(mname,
				new long[] {0L, 5L, 8L},
				new long[] {0L, 5L, 8L});
	}

	/* Test sort. */
	public void testSort4() throws Throwable {
		String mname = "testSort4";
		compatibilitySortTestSub(mname,
				new long[] {8L, 5L, 0L},
				new long[] {0L, 5L, 8L});
	}

	/* Test sort. */
	public void testSort5() throws Throwable {
		String mname = "testSort5";
		compatibilitySortTestSub(mname,
				new long[] {8L, 0L, 5L},
				new long[] {0L, 5L, 8L});
	}

	/* Test sort. */
	public void testSort6() throws Throwable {
		String mname = "testSort6";
		compatibilitySortTestSub(mname,
				new long[] {8L, 0L, 5L, 6L},
				new long[] {0L, 5L, 6L, 8L});
	}

	/* Test sort. */
	public void testSort7() throws Throwable {
		String mname = "testSort7";
		compatibilitySortTestSub(mname,
				new long[] {6L, 0L, 5L, 8L},
				new long[] {0L, 5L, 6L, 8L});
	}

	/* Test sort. */
	public void testSort8() throws Throwable {
		String mname = "testSort8";
		compatibilitySortTestSub(mname,
				new long[] {8L, 6L, 0L, 5L},
				new long[] {0L, 5L, 6L, 8L});
	}

    public void compatibilityCompSortTestSub(final String mname,
			String subTestLog,
			String subTestEqLog,
			int[] actIndexes, long[] actual,
			int[] expIndexes, long[] expected)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started compatibilityCompSortTestSub mname,subTestLog=" + mname + "," + subTestLog);
			//#endif
			if (expIndexes == null) {
				expIndexes = new int[indexes.length];
				System.arraycopy(indexes, 0, expIndexes, 0, indexes.length);
			}
			for (int i = 0; i < expected.length; i++) {
				assertEquals(mname + " " + subTestEqLog +
						" i,expIndexes[i],actIndexes[i]=" + i +
						"," + expIndexes[i] + "," + actIndexes[i],
						expected[expIndexes[i]],
						actual[actIndexes[i]]);
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

    public void compatibilitySortTestSub(final String mname,
			long[] initial, long[] expected)
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started compatibilitySortTestSub " + mname);
			//#endif
			indexes = new int[initial.length];
			for (int i = 0; i < initial.length; i++) {
				indexes[i] = i;
			}
			int[] compatIndexes = new int[initial.length];
			System.arraycopy(indexes, 0, compatIndexes, 0, initial.length);
			long[] compatActual = new long[initial.length];
			System.arraycopy(initial, 0, compatActual, 0, initial.length);
			SortUtil.sortLongs(compatIndexes, compatActual, 0,
					initial.length - 1);

			int[] currIndexes = new int[initial.length];
			System.arraycopy(indexes, 0, currIndexes, 0, initial.length);
			long[] currActual = new long[initial.length];
			System.arraycopy(initial, 0, currActual, 0, initial.length);
			MiscUtil.indexedSort(currActual, currIndexes, initial.length - 1);

			compatibilityCompSortTestSub(mname,
					"Compare compatibility to current",
					"Compare compatibility with current should " +
					"be equal", compatIndexes, compatActual, currIndexes,
					currActual);

			compatibilityCompSortTestSub(mname,
					"Compare compatibility to expected",
					"Compare compatibilityParseHtml with expected should " +
					"be equal", compatIndexes, compatActual,
					null, expected);

			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine(mname + " finished.");}
			//#endif
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
