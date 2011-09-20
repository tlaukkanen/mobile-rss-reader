//--Need to modify--#preprocess
/*
 * QuickSortRecursive.java
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
 * IB 2010-05-25 1.11.5RC2 Use recursive quicksort to compare with non-recursive.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
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

public class QuickSortRecursive {
/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
/*
 * This was modified 2010-05-22.
 */
	/** This is a generic version of C.A.R Hoare's Quick Sort 
	 * algorithm.  This will handle arrays that are already
	 * Sorted, and arrays with duplicate keys.
	 * If you think of a one dimensional array as going from
	 * the lowest index on the left to the highest index on the right
	 * then the parameters to this function are lowest index or
	 * left and highest index or right.  The first time you call
	 * this function it will be with the parameters 0, a.length - 1.
	 *
	 * @param a	   an long array
	 * @param lo0	 left boundary of array partition
	 * @param hi0	 right boundary of array partition */
	static private void longQuickSort(long[] a, int[] indexes, int lo0, int hi0)
	{

		long mid;
		int swap;
		int lo = lo0;
		int hi = hi0;

		if (hi0 > lo0) {

			/* Arbitrarily establishing partition element as the midpoint of
			 * the array. */
			mid = a[indexes[(lo0 + hi0) >> 1]];

			// loop through the array until indices cross
			while (lo <= hi) {
				/* find the first element that is greater than or equal to 
				 * the partition element starting from the left Index. */
				while ((lo < hi0) && (a[indexes[lo]] < mid)) {
					++lo;
				}

				/* find an element that is smaller than or equal to 
				 * the partition element starting from the right Index. */
				while ((hi > lo0) && (a[indexes[hi]] > mid)) {
					--hi;
				}

				// if the indexes have not crossed, Swap
				if (lo <= hi) {
					swap = indexes[lo];
					indexes[lo] = indexes[hi];
					indexes[hi] = swap;
					++lo;
					--hi;
				}
			}

			/* If the right index has not reached the left side of array
			 * must now Sort the left partition. */
			if (lo0 < hi) {
				longQuickSort(a, indexes, lo0, hi);
			}

			/* If the left index has not reached the right side of array
			 * must now Sort the right partition. */
			if (lo < hi0) {
				longQuickSort(a, indexes, lo, hi0);
			}
		}

	}

  /**
   * Call quick sort if &gt; 1 element.
   * @param a
   */
	static public void indexedSort(long[] a, int[] indexes, int aend) {
		if (aend <= 0) {
			return;
		}
		longQuickSort(a, indexes, 0, aend);
	}

}
//#endif
//#endif
