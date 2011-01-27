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
 * IB 2010-05-24 1.11.5RC2 Code cleanup.
 * IB 2010-05-24 1.11.5RC2 Better logging.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-18 1.11.5Dev14 Have fieldEquals with RssItemInfo only for JM Unit tests.
 * IB 2011-01-06 1.11.5Dev15 Change equals message for both null so that it is done as an else vs due to the other conditions returning null.  This is a slight performance improvement.
 * IB 2011-01-06 1.11.5Dev15 Use MiscUtil.toString to log null or the value of the equals parameter vs conditional logic.
 * IB 2011-01-24 1.11.5Dev16 Don't compile some code for internet link version.
 * IB 2011-01-24 1.11.5Dev16 Fix println statement.
 */

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define compatibility
@DCOMPATDEF@
//#ifdef DTEST
package com.substanceofcode.testutil.logging;

import java.lang.IllegalArgumentException;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.utils.MiscUtil;
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

//#ifdef DFULLVERS
//#ifdef DJMTEST
import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
//#endif
//#endif
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
				if (fineLoggable) {logger.fine("equals object equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return true;
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals object unequal " + thisLog + ",this=" + parmValue.getClass().getName() + "," + parmValue.toString() + "," + thisValue.getClass().getName() + "," + thisValue.toString());}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals object unequal one is null " + thisLog + ",this=" + MiscUtil.toString(parmValue, true) + "," + MiscUtil.toString(thisValue, true));}
			//#endif
			return false;
			// else both are null and equal
		} else {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
			//#endif
		}
		return true;
	}

	static public boolean fieldEquals(String parmValue, String thisValue,
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
				if (fineLoggable) {logger.fine("equals String equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
				//#endif
				return true;
			} else {
				char[] parmChars = parmValue.toCharArray();
				char[] thisChars = thisValue.toCharArray();
				int parmLen = parmChars.length;
				int thisLen = thisChars.length;
				int i = 0;
				for (; (i < parmLen) && (i < thisLen); i++) {
					if (parmChars[i] != thisChars[i]) {
						break;
					}
				}
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals String unequal " + thisLog + ",this,pos=" + parmValue + "," + thisValue + "," + i);}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals String unequal one is null " + thisLog + ",this=" + MiscUtil.toString(parmValue) + "," + MiscUtil.toString(thisValue));}
			//#endif
			return false;
			// else both are null and equal
		} else {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
			//#endif
		}
		return true;
	}

	static public boolean fieldEquals(Date parmValue, Date thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (parmValue.equals(thisValue) ||
					(parmValue.getTime() == thisValue.getTime()) ||
			(parmValue.toString().equals(thisValue.toString()))) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue.toString() + "," + thisValue.toString());}
				//#endif
				return true;
			} else {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals date unequal " + thisLog + ",this=" + parmValue.getTime() + "," + parmValue.toString() + "," + thisValue.getTime() +  "," + thisValue.toString());}
				//#endif
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + MiscUtil.toString(parmValue) + "," + MiscUtil.toString(thisValue));}
			//#endif
			return false;
			// else both are null and equal
		} else {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
			//#endif
		}
		return true;
	}

	//#ifdef DFULLVERS
	static public boolean itemEquals(RssItemInfo parmValue,
			RssItemInfo thisValue,
			boolean[] fldPres,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable, boolean traceLoggable) {
		boolean result = true;
		if (fldPres[0] && !TestLogUtil.fieldEquals(parmValue.getTitle(),
					thisValue.getTitle(),
			"m_title", logger, fineLoggable)) {
			result = false;
		}
		if (fldPres[1] && !TestLogUtil.fieldEquals(parmValue.getLink(),
					thisValue.getLink(),
			"m_link", logger, fineLoggable)) {
			result = false;
		}
		if (fldPres[2] && !TestLogUtil.fieldEquals(parmValue.getDescription(),
					thisValue.getDescription(),
			"m_desc", logger, fineLoggable)) {
			result = false;
		}
		if (fldPres[3] && !TestLogUtil.fieldEquals(parmValue.getDate(),
					thisValue.getDate(),
			"m_date", logger, fineLoggable)) {
			result = false;
		}
		if (fldPres[4] && !TestLogUtil.fieldEquals(parmValue.getEnclosure(),
					thisValue.getEnclosure(),
			"m_enclosure", logger, fineLoggable)) {
			result = false;
		}
		if (fldPres[5] && !TestLogUtil.fieldEquals(parmValue.isUnreadItem(),
					thisValue.isUnreadItem(),
			"m_unreadItem", logger, fineLoggable)) {
			result = false;
		}
		return true;
	}

	//#ifdef DJMTEST
	static public boolean fieldEquals(RssItemInfo parmValue,
			RssItemInfo thisValue,
			String thisLog,
			//#ifdef DLOGGING
			Logger logger,
			//#else
			Object logger,
			//#endif
			boolean fineLoggable) {
		if ((parmValue != null) && (thisValue != null)) {
			if (thisValue instanceof RssItunesItemInfo) {
				RssItunesItemInfo thistItem = (RssItunesItemInfo)thisValue;

				if (thistItem.equals(parmValue)) {
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("equals RssItunesItemInfo equal " + thisLog + ",this=" + MiscUtil.toString(parmValue, true) + "," + thistItem.toString());}
					//#endif
					return true;
				} else {
					if (fineLoggable) {logger.fine("equals RssItunesItemInfo unequal " + thisLog + ",this=" + MiscUtil.toString(parmValue, true) + "," + MiscUtil.toString(thisValue, true));}
					return false;
				}
			} else if (thisValue.equals(parmValue)) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals RssItemInfo equal " + thisLog + ",this=" + MiscUtil.toString(parmValue, true) + "," + MiscUtil.toString(thisValue, true));}
				//#endif
				return true;
			} else {
				if (fineLoggable) {logger.fine("equals RssItemInfo unequal " + thisLog + ",this=" + MiscUtil.toString(parmValue, true) + "," + MiscUtil.toString(thisValue, true));}
				return false;
			}
		} else if ((parmValue != null) || (thisValue != null)) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals unequal " + thisLog + ",this=" + MiscUtil.toString(parmValue, true) + "," + MiscUtil.toString(thisValue, true));}
			//#endif
			return false;
			// else both are null and equal
		} else {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals equal " + thisLog + ",this=" + parmValue + "," + thisValue);}
			//#endif
		}
		return true;
	}
	//#endif
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
