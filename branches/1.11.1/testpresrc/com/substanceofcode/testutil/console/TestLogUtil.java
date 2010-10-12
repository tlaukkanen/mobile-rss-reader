//--Need to modify--#preprocess
/*
 *
 * Copyright (C) 2008 Irving Bunton
 * http://www.substanceofcode.com
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
 * IB 2010-05-24 1.11.5RC2 Add logging.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define test ui define
@DTESTUIDEF@

//#ifdef DTESTUI

package com.substanceofcode.testutil.console;

import java.lang.IllegalArgumentException;
import java.io.IOException;
import java.util.Hashtable;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import com.substanceofcode.testlcdui.ChoiceGroup;
import com.substanceofcode.testlcdui.Form;
import com.substanceofcode.testlcdui.List;
import com.substanceofcode.testlcdui.TextBox;
import com.substanceofcode.testlcdui.TextField;
import com.substanceofcode.testlcdui.StringItem;
import javax.microedition.lcdui.Item;

import com.substanceofcode.testutil.TestOutput;

/**
 * Show characters with different encodings to help show the presentation.
 * Also, allow easy test of how it will look.
 *
 * @author Irving Bunton
 */
public class TestLogUtil {
    
    
	static public boolean fieldEquals(Object parmValue, Object thisValue,
			String thisLog,
			Object dummyLogger,
			boolean dummyFineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (parmValue.equals(thisValue)) {
				//#ifdef DLOGGING
				TestOutput.println("equals equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());
				//#endif
				return true;
			} else {
				TestOutput.println("equals unequal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			TestOutput.println("equals unequal " + thisLog + ",this=" + ((parmValue == null) ? "null" : parmValue.toString()) + "," + ((thisValue == null) ? "null" : thisValue.toString()));
			return false;
		}
		return true;
	}

	static public boolean fieldEquals(int parmValue, int thisValue,
			String thisLog,
			Object dummyLogger,
			boolean dummyFineLoggable) {
		if (parmValue == thisValue) {
			return true;
		}
		TestOutput.println("equals unequal " + thisLog + ",this=" + parmValue + "," + thisValue);
		return false;
	}

	static public boolean fieldEquals(boolean parmValue, boolean thisValue,
			String thisLog,
			Object dummyLogger,
			boolean dummyFineLoggable) {
		if (parmValue == thisValue) {
			return true;
		}
		TestOutput.println("equals unequal " + thisLog + ",this=" + parmValue + "," + thisValue);
		return false;
	}

}
//#endif
