/*
 * AllNewsList.java
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
import javax.microedition.lcdui.Image;
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

/**
 *
 * @author Tommi Laukkanen
 */
final public class AllNewsList extends List implements CommandListener {
    
    private RssReaderMIDlet m_midlet;
    private boolean     m_sort      = false; // Process sort
    private boolean     m_sortByDate = false; // Sort by date
    private boolean     m_showAll = false;  // Show both read and unread.
    private boolean     m_showUnread = false;  // Show unread.
    private boolean     m_dateSort = false; // Sort by date, else by feed
    private boolean     m_sortUnread = false; // Sort unread items
    private boolean     m_needCleanup = false; // True if we're finished.
    private boolean     m_markUnreadItems;     // Mark unread items.
    private Image       m_unreadImage;
    private final static String TITLE = "River of News";
    private Command     m_openUnreadHdrCmd;    // The open header command
    private Command     m_sortUnreadItemsCmd;  // The sort unread items by date command
    private Command     m_sortReadItemsCmd;  // The sort read items by date command
    private Command     m_sortUnreadFeedsCmd;  // The sort unread items by feed command
    private Command     m_sortReadFeedsCmd;  // The sort read items by feed command
    private Command     m_sortAllDateCmd;  // The sort all items by date command
    private Command     m_sortAllFeedsCmd;  // The sort read items by feed command
    private Command     m_markReadCmd;      // Mark the item as read
    private Command     m_markUnReadCmd;    // Mark the item as unread
    private Command     m_backUnreadHdrCmd;    // The back to bookmark list command
    private Vector      m_itemFeeds = new Vector();
    private Vector      m_allItems = new Vector();
    private Vector      m_unreadItems = new Vector();
    private Vector      m_readItems = new Vector();
    private RssItunesItem m_item;
    private RssItunesFeed m_feed;
	//#ifdef DLOGGING
//@    private Logger m_logger = Logger.getLogger("AllNewsList");
//@    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
//@    private boolean m_finerLoggable = m_logger.isLoggable(Level.FINER);
//@    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of AllNewsList
	    unreadImage - if non-null, put image for unread items for all list. */
    public AllNewsList(final RssReaderMIDlet midlet, Image unreadImage) {
        //super(TITLE, List.IMPLICIT);
        super("Unread Headers", List.IMPLICIT);
		m_midlet = midlet;
		m_unreadImage = unreadImage;
    }

    /** Initialize new RSS headers list */
    final public void initializeUnreadHhdrsList() {
		m_openUnreadHdrCmd  = new Command("Open item", Command.SCREEN, 1);
		m_sortUnreadItemsCmd = new Command("Unread date sorted",
										   Command.SCREEN, 1);
		m_sortReadItemsCmd = new Command("Read date sorted",
										   Command.SCREEN, 2);
		m_sortUnreadFeedsCmd = new Command("Unread feed sorted",
										   Command.SCREEN, 3);
		m_sortReadFeedsCmd = new Command("Read feed sorted",
										   Command.SCREEN, 4);
		m_sortAllDateCmd = new Command("All date sorted",
										   Command.SCREEN, 5);
		m_sortAllFeedsCmd = new Command("All feed sorted",
										   Command.SCREEN, 6);
		m_markReadCmd = new Command("Mark read", Command.SCREEN, 7);
		m_markUnReadCmd = new Command("Mark unread", Command.SCREEN, 8);
		m_backUnreadHdrCmd  = new Command("Back", Command.BACK, 9);
        super.addCommand(m_openUnreadHdrCmd);
        super.addCommand(m_sortUnreadItemsCmd);
        super.addCommand(m_sortReadItemsCmd);
        super.addCommand(m_sortUnreadFeedsCmd);
        super.addCommand(m_sortReadFeedsCmd);
        super.addCommand(m_sortAllDateCmd);
        super.addCommand(m_sortAllFeedsCmd);
        super.addCommand(m_markReadCmd);
        super.addCommand(m_markUnReadCmd);
        super.addCommand(m_backUnreadHdrCmd);
        super.setCommandListener(this);
    }
    
	/* Initialize the read an unread news lists.  This is done when we
	   switch to the list. */
	final public void initNewsList(final boolean showAll,
						     final boolean showUnread,
						     final boolean sortByDate,
							 final List bookmarkList,
							 final Hashtable rssFeeds) {

		this.m_showAll = showAll;
		this.m_showUnread = showUnread;
		this.m_sortByDate = sortByDate;
		final int bsize = bookmarkList.size();
		if( bsize > 0 ){
			boolean firstItem = true;
			for( int ic = 0; ic < bsize; ic++ ){
			
				final RssItunesFeed feed = (RssItunesFeed)rssFeeds.get(
						bookmarkList.getString(ic));
				if( feed.getItems().size()>0 ) {
					/**
					 * Show currently selected RSS feed
					 * headers without updating them
					 */
					fillVectors( firstItem, feed );
					if ( firstItem ) {
						firstItem = false;
					}
				}
			}
		}
	}

	/* Sort all items. */
	final public void sortAllItems(final boolean sortByDate,
							 final List bookmarkList,
							 final Hashtable rssFeeds) {
		initNewsList(true, false, sortByDate, bookmarkList, rssFeeds);
		if (sortByDate) {
			sortItems(false, m_allItems);
		} else {
			fillItems( m_allItems);
		}
		super.setTitle("All items " + (sortByDate ? "date" : "feed") +
				" sorted:  " + super.size());
	}

	/* Sort items unread. */
	final public void sortUnreadItems(final boolean sortByDate,
								final List bookmarkList,
								final Hashtable rssFeeds) {
		initNewsList(false, true, sortByDate, bookmarkList, rssFeeds);
		if (sortByDate) {
			sortItems(true, m_unreadItems);
		} else {
			fillItems( m_unreadItems);
		}
		super.setTitle("Unread items " + (sortByDate ? "date" : "feed") +
				" sorted:  " + super.size());
	}

	/* Sort items read. */
	final public void sortReadItems(boolean sortByDate,
							  List bookmarkList, Hashtable rssFeeds) {
		initNewsList(false, false, sortByDate, bookmarkList, rssFeeds);
		if (sortByDate) {
			sortItems(false, m_readItems);
		} else {
			fillItems( m_readItems);
		}
		super.setTitle("Read items " + (sortByDate ? "date" : "feed") +
				" sorted:  " + super.size());
	}

	/* Sort items read or unread vector and put into GUI list. */
	final private void sortItems(final boolean showUnread,
			final Vector unsortedItems) {

		this.m_showUnread = showUnread;
		int [] indexes = new int[unsortedItems.size()];
		long [] ldates = new long[unsortedItems.size()];
		Vector vsorted = new Vector(unsortedItems.size());
		Vector vfeedSorted = new Vector(m_itemFeeds.size());
		Vector vunsorted = new Vector(unsortedItems.size());
		Vector vfeedUnsorted = new Vector(m_itemFeeds.size());
		RssItunesItem [] uitems = new RssItunesItem[unsortedItems.size()];
		unsortedItems.copyInto(uitems);
		RssItunesFeed [] ufeeds = new RssItunesFeed[m_itemFeeds.size()];
		m_itemFeeds.copyInto(ufeeds);
		int kc = 0;
		for (int ic = 0; ic < uitems.length; ic++) {
			if (uitems[ic].getDate() == null) {
				vsorted.addElement(uitems[ic]);
				vfeedSorted.addElement(ufeeds[ic]);
			} else {
				indexes[kc] = kc;
				ldates[kc++] = uitems[ic].getDate().getTime();
				vunsorted.addElement(uitems[ic]);
				vfeedUnsorted.addElement(ufeeds[ic]);
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest("kc,date=" + ic + "," + new Date(ldates[kc - 1]));}
				//#endif
			}
		}
		uitems = null;
		ufeeds = null;
		SortUtil.sortLongs( indexes, ldates, 0, kc - 1);
		uitems = new RssItunesItem[kc];
		vunsorted.copyInto(uitems);
		vunsorted = null;
		ufeeds = new RssItunesFeed[kc];
		vfeedUnsorted.copyInto(ufeeds);
		vfeedUnsorted = null;
		for (int ic = 0; ic < kc ; ic++) {
			//#ifdef DLOGGING
//@			if (m_finestLoggable) {m_logger.finest("ic,index,date=" + ic + "," + indexes[ic] + "," + new Date(ldates[indexes[ic]]) + "," + uitems[indexes[ic]].getDate());}
			//#endif
			vsorted.insertElementAt(uitems[indexes[ic]], 0);
			vfeedSorted.insertElementAt(ufeeds[indexes[ic]], 0);
		}
		uitems = null;
		RssItunesItem[] sitems = new RssItunesItem[vsorted.size()];
		vsorted.copyInto(sitems);
		unsortedItems.removeAllElements();
		for( int ic = 0; ic < sitems.length; ic++){
			unsortedItems.addElement(sitems[ic]);
		}
		ufeeds = null;
		RssItunesFeed[] sfeeds = new RssItunesFeed[vfeedUnsorted.size()];
		vfeedUnsorted.copyInto(sfeeds);
		m_itemFeeds.removeAllElements();
		for( int ic = 0; ic < sitems.length; ic++){
			m_itemFeeds.addElement(sfeeds[ic]);
		}
		fillItems( unsortedItems );
	}

    /** Fill list from items */
    final private void fillItems(final Vector sortedItems) {
		//#ifdef DMIDP20
		super.deleteAll();
		//#else
//@		while(super.size()>0) {
//@			super.delete(0);
//@		}
		//#endif
		final int slen = sortedItems.size();
		RssItunesItem [] sitems = new RssItunesItem[slen];
		sortedItems.copyInto(sitems);
		for( int ic = 0; ic < slen; ic++){
			String text = sitems[ic].getTitle();
			if (text.length() == 0) {
				text = m_midlet.getItemDescription(sitems[ic]);
			}
			if (m_showAll && (m_unreadImage != null) &&
					sitems[ic].isUnreadItem()) {
				super.append( text, m_unreadImage );
			} else {
				super.append( text, null );
			}
		}
	}

    /** Fill RSS item vectors */
    final private void fillVectors( final boolean firstItem,
			final RssItunesFeed feed ) {
        if(firstItem) {
			m_unreadItems.removeAllElements();
			m_readItems.removeAllElements();
			m_allItems.removeAllElements();
			m_itemFeeds.removeAllElements();
        }
        final Vector vitems = feed.getItems();
        final int itemLen = vitems.size();
        for(int i=0; i < itemLen; i++){
            final RssItunesItem r = (RssItunesItem)vitems.elementAt(i);
			if (m_showAll) {
				m_allItems.addElement(r);
				m_itemFeeds.addElement(feed);
			} else {
				if (r.isUnreadItem()) {
					if (m_showUnread) {
						m_unreadItems.addElement(r);
						m_itemFeeds.addElement(feed);
					}
				} else {
					if (!m_showUnread) {
						m_readItems.addElement(r);
						m_itemFeeds.addElement(feed);
					}
				}
			}
        }
    }
    
	/* Get the current selected index (or 0 index) and update the vectors. */
	final private void getUpdSel(boolean updateIt) {
		int selIdx = super.getSelectedIndex();
		if (selIdx == -1) {
			super.setSelectedIndex(0, true);
			selIdx = 0;
		}
		if (m_showAll) {
			m_item = (RssItunesItem)m_allItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
			final int unreadIdx = m_unreadItems.indexOf(m_item);
			if (updateIt && (m_unreadImage != null)) {
				super.set(selIdx, super.getString(selIdx), null);
			}
		} else if (m_showUnread) {
			super.delete(selIdx);
			m_item = (RssItunesItem)m_unreadItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
			if (updateIt) {
				m_unreadItems.removeElementAt(selIdx);
				m_itemFeeds.removeElementAt(selIdx);
			}
			super.setTitle("Unread items " + (m_sortByDate ? "date" : "feed") +
					" sorted:  " + super.size());
		} else {
			m_item = (RssItunesItem)m_readItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
		}
		/**
		 * Show currently selected RSS item
		 * without updating it
		 */
		if (updateIt) {
			m_item.setUnreadItem(false);
		}
	}

    /** Run method is used to get RSS feed with HttpConnection */
    final public void run(final List bookmarkList, final Hashtable rssFeeds) {

		/* Sort the read or unread items. */
		if ( m_sort ) {
			if (m_showAll) {
				sortAllItems( m_dateSort, bookmarkList, rssFeeds );
			} else if (m_sortUnread) {
				sortUnreadItems( m_dateSort, bookmarkList, rssFeeds );
			} else {
				sortReadItems( m_dateSort, bookmarkList, rssFeeds );
			}
			if (super.size() == 0) {
				alertNoQualfiy(false);
			}
			m_midlet.showNewsList();
			m_sort = false;
		}

	}

	/** Tell the user that nothing qualified. */
	final public void alertNoQualfiy(final boolean initSort) {
		String msg;
		if (initSort) {
			msg = "Choose an item to sort by.";
		} else if (m_sortUnread) {
			msg = "No unread items to sort.  Choose " +
				"another item to sort by.";
		} else if (m_showAll) {
			msg = "No items to sort.  Choose " +
				"another item to sort by.";
		} else {
			msg = "No read items to sort.  Choose " +
				"another item to sort by.";
		}
		super.append(msg, null);
	}

    /** Respond to commands */
    final public void commandAction(final Command c, final Displayable s) {
		//#ifdef DTESTUI
//@		super.outputCmdAct(c, s, javax.microedition.lcdui.List.SELECT_COMMAND);
		//#endif
        if( c == m_sortUnreadItemsCmd ) {
			m_midlet.initializeLoadingForm("Sorting items...", this);
			m_midlet.showLoadingForm();
			m_sortUnread = true;
			m_dateSort = true;
			m_showAll = false;
			m_sort = true;
        }
        
        /** Read read items date sorted */
        if( c == m_sortReadItemsCmd ) {
			m_midlet.initializeLoadingForm("Sorting items...", this);
			m_midlet.showLoadingForm();
			m_sortUnread = false;
			m_dateSort = true;
			m_showAll = false;
			m_sort = true;
        }
        
        /** Read unread items feed sorted */
        if( c == m_sortUnreadFeedsCmd ) {
			m_midlet.initializeLoadingForm("Sorting items...", this);
			m_midlet.showLoadingForm();
			m_sortUnread = true;
			m_dateSort = false;
			m_showAll = false;
			m_sort = true;
        }
        
        /** Read read items feed sorted */
        if( c == m_sortReadFeedsCmd ) {
			m_midlet.initializeLoadingForm("Sorting items...", this);
			m_midlet.showLoadingForm();
			m_sortUnread = false;
			m_dateSort = false;
			m_showAll = false;
			m_sort = true;
        }
        
        if( (c == m_openUnreadHdrCmd) || (c == List.SELECT_COMMAND) ) {
            if( super.size()>0){
				getUpdSel(true);
				m_midlet.initializeItemForm( m_feed, m_item, this );
				m_midlet.showItemForm();
            }
		} else if( c == m_sortAllDateCmd ) {
			m_midlet.initializeLoadingForm("Sorting items...", this);
			m_midlet.showLoadingForm();
			m_sortUnread = false;
			m_dateSort = true;
			m_showAll = true;
			m_sort = true;
		} else if( c == m_sortAllFeedsCmd ) {
			m_midlet.initializeLoadingForm("Sorting items...", this);
			m_midlet.showLoadingForm();
			m_sortUnread = false;
			m_dateSort = false;
			m_showAll = true;
			m_sort = true;
		} else if( c == m_markReadCmd ) {
            if( super.size()>0){
				getUpdSel(false);
				m_item.setUnreadItem(false);
				m_midlet.initializeLoadingForm("Sorting items...", this);
				m_sort = true;
            }
		} else if( c == m_markUnReadCmd ) {
            if( super.size()>0){
				getUpdSel(false);
				m_item.setUnreadItem(true);
				m_midlet.initializeLoadingForm("Sorting items...", this);
				m_sort = true;
            }
        /** Get back to RSS feed bookmarks */
		} else if( c == m_backUnreadHdrCmd ){
			m_midlet.showBookmarkList();
			m_needCleanup = true;
		}
        
	}

    final public boolean isNeedCleanup() {
        return (m_needCleanup);
    }

}
