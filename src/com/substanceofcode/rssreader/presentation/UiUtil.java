/*
 * 
 * UiUtil.java
 *
 * Copyright (C) 2008 Irving Bunton, Jr
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

// Expand to define test define
//#define DNOTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define MIDP define
//#define DMIDP20
// Expand to define logging define
//#define DNOLOGGING


package com.substanceofcode.rssreader.presentation;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Gauge;
// If not using the test UI define the J2ME UI's
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;
//#else
//@// If using the test UI define the Test UI's
//@import com.substanceofcode.testlcdui.ChoiceGroup;
//@import com.substanceofcode.testlcdui.Form;
//@import com.substanceofcode.testlcdui.List;
//@import com.substanceofcode.testlcdui.TextBox;
//@import com.substanceofcode.testlcdui.TextField;
//@import com.substanceofcode.testlcdui.StringItem;
//#endif

import cz.cacek.ebook.util.ResourceProviderME;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * RSS feed reader MIDlet
 *
 * RssReaderMIDlet is an application that can read RSS feeds. User can store
 * multiple RSS feeds as bookmarks into application's record store.
 *
 * @author  Irving Bunton
 * @version 1.0
 */
public class UiUtil {
 
	//#ifdef DLOGGING
//@    private javax.microedition.lcdui.Form m_debug;
//@    private Logger logger;
	//#endif
    
  /**
   * Create a new command using the resource key and standard parms
   *
   * @param key - Key for resource command label
   * @param commandType - Command type
   * @param priority - Command priority
   * @return    Command
   * @author Irv Bunton
   */
    public static Command getCmdRsc(String key, int commandType, int priority) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("UiUtil");
//@		logger.finest("key,commandType,priority=" + key + "," + commandType + "," + priority);
		//#endif
		return new Command(ResourceProviderME.get(key), commandType, priority);
	}

  /**
   * Create a new command using the resource key and standard parms
   *
   * @param key - Key for resource command label
   * @param lngkey - Key for resource command long label
   * @param commandType - Command type
   * @param priority - Command priority
   * @return    Command
   * @author Irv Bunton
   */
    public static Command getCmdRsc(String key, String lngkey,
			int commandType, int priority) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("UiUtil");
//@		logger.finest("key,lngkey,commandType,priority=" + key + "," + lngkey + "," + commandType + "," + priority);
		//#endif
		//#ifdef DMIDP20
		if (lngkey != null) {
			return new Command(ResourceProviderME.get(key),
					ResourceProviderME.get(lngkey), commandType, priority);
		} else {
			return new Command(ResourceProviderME.get(key), commandType,
					priority);
		}
		//#else
//@		return new Command(ResourceProviderME.get(key), commandType, priority);
		//#endif
	}

  /**
   * Remove all commands from the displayable
   *
   * @param cmds
   * @author Irv Bunton
   */
	public static void delCmds(Displayable disp, Command[] cmds) {

		for (int ic = 0; ic < cmds.length; ic++) {
			disp.removeCommand( cmds[ic] );
		}
	}

}
