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

// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define compatibility
@DCOMPATDEF@
// Expand to define JMUnit test define @DJMTESTDEF@
//#ifdef DCOMPATIBILITY1
  //#define DCOMPATIBILITY
//#endif
//#ifdef DCOMPATIBILITY2
  //#define DCOMPATIBILITY
//#endif
//#ifdef DCOMPATIBILITY3
  //#define DCOMPATIBILITY
//#endif
//#ifdef DTEST
package com.substanceofcode.testutil.logging;

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

//#ifdef DJMTEST
import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
//#ifdef DCOMPATIBILITY
import com.substanceofcode.rssreader.businessentities.CompatibilityRssItem1;
import com.substanceofcode.rssreader.businessentities.CompatibilityRssItem2;
import com.substanceofcode.rssreader.businessentities.CompatibilityRssItunesItem3;
//#endif
//#endif
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.testutil.TestOutput;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Show characters with different encodings to help show the presentation.
 * Also, allow easy test of how it will look.
 *
 * @author Irving Bunton
 */
public class TestLogUtil {
    
	static public boolean fieldEquals(Object parmValue, Object thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (parmValue.equals(thisValue)) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return true;
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + ((parmValue == null) ? "null" : parmValue.toString()) + "," + ((thisValue == null) ? "null" : thisValue.toString()));}
			//#endif
			return false;
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
		//#endif
		return true;
	}

	static public boolean fieldEquals(RssItunesItem parmValue,
			RssItunesItem thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (parmValue.equals(thisValue)) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return true;
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + ((parmValue == null) ? "null" : parmValue.toString()) + "," + ((thisValue == null) ? "null" : thisValue.toString()));}
			//#endif
			return false;
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
		//#endif
		return true;
	}

	//#ifdef DCOMPATIBILITY
	static public boolean fieldEquals(CompatibilityRssItem1 parmValue,
			RssItemInfo thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (parmValue.equals(thisValue)) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return true;
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + ((parmValue == null) ? "null" : parmValue.toString()) + "," + ((thisValue == null) ? "null" : thisValue.toString()));}
			//#endif
			return false;
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
		//#endif
		return true;
	}

	static public boolean fieldEquals(CompatibilityRssItem2 parmValue,
			RssItemInfo thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (parmValue.equals(thisValue)) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return true;
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + ((parmValue == null) ? "null" : parmValue.toString()) + "," + ((thisValue == null) ? "null" : thisValue.toString()));}
			//#endif
			return false;
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
		//#endif
		return true;
	}

	static public boolean fieldEquals(CompatibilityRssItunesItem3 parmValue,
			RssItemInfo thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (parmValue.equals(thisValue)) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return true;
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + ((parmValue == null) ? "null" : parmValue.toString()) + "," + ((thisValue == null) ? "null" : thisValue.toString()));}
			//#endif
			return false;
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
		//#endif
		return true;
	}

	//#endif

	static public boolean fieldEquals(int parmValue, int thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if (parmValue == thisValue) {
			return true;
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + parmValue + "," + thisValue);}
		//#endif
		return false;
	}

	static public boolean fieldEquals(boolean parmValue, boolean thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if (parmValue == thisValue) {
			return true;
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + parmValue + "," + thisValue);}
		//#endif
		return false;
	}

}
//#endif
