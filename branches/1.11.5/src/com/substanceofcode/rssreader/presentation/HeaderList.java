//--Need to modify--#preprocess
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
/*
 * IB 2010-03-07 1.11.4RC1 Use observer pattern for feed parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-03-07 1.11.5Dev2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-03-07 1.11.5Dev2 Cosmetic but not execution code changes.
 * IB 2010-07-28 1.11.5Dev8 Show details if 0 items.
 * IB 2010-07-28 1.11.5Dev8 More logging.
 * IB 2010-07-29 1.11.5Dev8 Also, show details if date is not empty.
 * IB 2010-07-29 1.11.5Dev8 Fix order of commands for header list.
 * IB 2010-07-29 1.11.5Dev8 More logging.
 * IB 2010-09-26 1.11.5Dev8 Use midlet from FeatureMgr.
 * IB 2010-09-26 1.11.5Dev8 Have procBackPage create loading form.
 * IB 2010-09-27 1.11.5Dev8 Have "Loading detail form..." when going to the detail form.
 * IB 2010-09-27 1.11.5Dev8 Have update give updating feed loading form for MIDP 1.0.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-10-30 1.11.5Dev12 Need to set m_feed before we use it to log.
 * IB 2010-11-16 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
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
import com.substanceofcode.rssreader.presentation.LoadingForm;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form to add new/edit existing bookmark. */
final public class HeaderList extends AllNewsList
implements 
//#ifdef DMIDP20
			Observer,
//#endif
	CommandListener
{
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
	public HeaderList(final FeatureList bookmarkList, final int selectedIx,
			final Hashtable rssFeeds, Image unreadImage,
			boolean itunesEnabled,
			LoadingForm loadForm, final RssItunesFeed feed) {
		super(feed.getName(), List.IMPLICIT,
				selectedIx, 1, bookmarkList,
				rssFeeds, unreadImage, loadForm, 10);
		m_feed = feed;
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("Constructor m_feed.getName(),m_feed.getItems().size(),selectedIx,itunesEnabled=" + m_feed.getName() + "," + m_feed.getItems().size() + "," + selectedIx + "," + itunesEnabled);}
		//#ifdef DITUNES
//@		if (m_finestLoggable) {m_logger.finest("Constructor feed.getName(),feed.getLink(),feed.getItems().size()=" + feed.getName() + "," + feed.getLink() + "," + feed.getItems().size());}
		//#endif
		//#endif
		this.m_itunesEnabled = itunesEnabled;
		m_updateCmd         = new Command("Update feed", Command.SCREEN, 7);
		m_updateModCmd      = new Command("Update modified feed",
										  Command.SCREEN, 8);
		super.addCommand(m_updateCmd);
		super.addCommand(m_updateModCmd);
		//#ifdef DITUNES
//@		if (m_itunesEnabled && (feed.isItunes() ||
//@		   (feed.getLink().length() > 0) || (feed.getDate() != null))) { 
//@			m_bookmarkDetailsCmd    = new Command("Show bookmark details",
//@					Command.SCREEN, 9);
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
//@			featureMgr.getLoadForm().recordExcForm(
//@					"\ntestFeed Internal error", t);
//@		}
//@	}
	//#endif

	//#ifdef DMIDP20
	public void changed(Observable observable, Object arg) {

		RssFeedParser cbackGrRssParser = FeatureMgr.getMidlet().checkActive(observable);
		if (cbackGrRssParser == null) {
			return;
		}
		if (!cbackGrRssParser.getObservableHandler().isCanceled()) {
			m_feed = cbackGrRssParser.getRssFeed();
		}
	}
	//#endif

	public void commandAction(Command c, Displayable s) {

		super.commandAction(c, s);

		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("commandAction c=" + c.getLabel());}
		//#endif

		RssReaderMIDlet midlet = featureMgr.getMidlet();
		/** Update currently selected RSS feed's headers */
		if( (c == m_updateCmd) ||  (c == m_updateModCmd) ) {
			midlet.setPageInfo(false, (c == m_updateCmd),
					(c == m_updateModCmd), this);
			// Update existing bookmark.
			//#ifdef DMIDP20
			synchronized(this) {
				// Have procBackPage create loading form.
				midlet.procBackPage(m_feed, null, true, this, null);
			}
			//#else
//@			/* Updating feed... */
//@			LoadingForm loadForm = LoadingForm.getLoadingForm(
//@					"Updating feed...", this, null);
//@			featureMgr.setLoadForm(loadForm);
//@			try {
//@				RssFeedParser parser = new RssFeedParser( m_feed );
//@				parser.parseRssFeed( (c == m_updateModCmd),
//@					midlet.getSettings().getMaximumItemCountInFeed());
//@				midlet.procUpdHeader(parser, loadForm);
//@			} catch(Throwable e) {
//@				midlet.procPageExc(m_feed, false, e);
//@			}
			//#endif
		}
		
		//#ifdef DITUNES
//@		/** Display Itune's feed detail */
//@		if( c == m_bookmarkDetailsCmd ) {
//@			LoadingForm loadForm = LoadingForm.getLoadingForm(
//@					"Loading detail form...", this, null);
//@			featureMgr.setLoadForm(loadForm);
//@			midlet.initializeDetailForm(m_feed, this, loadForm);
//@		}
		//#endif

	}

	/** Keep the title */
	public void updTitle() {}
}
