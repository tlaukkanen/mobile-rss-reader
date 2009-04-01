/*
 * HeaderList.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
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

// Expand to define MIDP define
//#define DMIDP20
// Expand to define itunes define
//#define DNOITUNES
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test ui define
//#define DNOTESTUI

package com.substanceofcode.rssreader.presentation;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
//#ifdef DLARGEMEM
//@import javax.microedition.lcdui.Gauge;
//#endif
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
// If not using the test UI define the J2ME UI's
//#ifndef DTESTUI
import javax.microedition.lcdui.List;
//#else
//@import com.substanceofcode.testlcdui.List;
//#endif
// If using the test UI define the Test UI's

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.utils.SortUtil;
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form to add new/edit existing bookmark. */
final public class HeaderList extends AllNewsList
	implements CommandListener {
	private boolean     m_itunesEnabled;    // True if Itunes is enabled
	private Command     m_updateCmd;        // The update headers command
	private Command     m_updateModCmd;     // The update modified headers command
	//#ifdef DITUNES
//@	private Command     m_bookmarkDetailsCmd;   // The show feed details
	//#endif

	//#ifdef DLOGGING
//@    private Logger m_logger = Logger.getLogger("HeaderList");
//@    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif
    
	/* Constructor */
	public HeaderList(final RssReaderMIDlet midlet,
			final FeatureList bookmarkList, final int selectedIx,
			final Hashtable rssFeeds, Image unreadImage,
			boolean itunesEnabled,
			RssReaderMIDlet.LoadingForm loadForm, final RssItunesFeed feed) {
		super(midlet, feed.getName(), List.IMPLICIT,
				selectedIx, 1, bookmarkList,
				rssFeeds, unreadImage, loadForm, 6);
		m_feed = feed;
		this.m_itunesEnabled = itunesEnabled;
		m_updateCmd         = new Command("Update feed", Command.SCREEN, 3);
		m_updateModCmd      = new Command("Update modified feed",
										  Command.SCREEN, 4);
		super.addCommand(m_updateCmd);
		super.addCommand(m_updateModCmd);
		//#ifdef DITUNES
//@		if (m_itunesEnabled && feed.isItunes()) { 
//@			m_bookmarkDetailsCmd    = new Command("Show bookmark details",
//@					Command.SCREEN, 4);
//@			super.addCommand(m_bookmarkDetailsCmd);
//@		}
		//#endif
	}
	
	//#ifdef DTEST
//@	/** Test that the feed is not ruined by being stored and restored. */
//@	public void testFeed() {
//@		try {
//@			RssItunesFeed feed = m_feed;
//@			String store = feed.getStoreString(true, true);
//@			RssItunesFeed feed2 = RssItunesFeed.deserialize( true,
//@					true, store );
//@			boolean feedEq = feed.equals(feed2);
			//#ifdef DLOGGING
//@			if (m_finestLoggable) {m_logger.finest("feed1,2 eq=" + feedEq);}
			//#endif
//@			if (!feedEq) {
				//#ifdef DLOGGING
//@				m_logger.severe("Itunes feed does not match name=" + feed.getName());
				//#endif
//@				System.out.println("feed=" + feed + "," + feed.toString());
//@				System.out.println("feed store=" + store);
//@			}
//@		} catch(Throwable t) {
//@			m_loadForm.recordExcForm(
//@					"\ntestFeed Internal error", t);
//@		}
//@	}
	//#endif

	public void commandAction(Command c, Displayable s) {

		super.commandAction(c, s);

		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("commandAction c=" + c.getLabel());}
		//#endif

		/** Update currently selected RSS feed's headers */
		if( (c == m_updateCmd) ||  (c == m_updateModCmd) ) {
			m_midlet.updateHeaders(c == m_updateModCmd, this);
			// Update existing bookmark.
			m_midlet.procPage(false);
		}
		
		//#ifdef DITUNES
//@		/** Display Itune's feed detail */
//@		if( c == m_bookmarkDetailsCmd ) {
//@			m_midlet.initializeDetailForm(m_feed, this);
//@		}
		//#endif

	}

	/** Keep the title */
	public void updTitle() {}
}
