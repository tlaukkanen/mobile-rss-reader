//--Need to modify--#preprocess
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
/*
 * IB 2010-03-07 1.11.4RC1 Have next item command on item display.
 * IB 2010-05-24 1.11.5RC2 Replace SortUtil with LGPL code to allow adding of LGPL license.
 * IB 2010-05-27 1.11.5RC2 More logging.
 * IB 2010-06-01 1.11.5RC2 Use back from RssReaderMIDlet if priority matches.
 * IB 2010-06-01 1.11.5RC2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern for nulls to initialize and save memory.
 * IB 2010-08-14 1.11.5Dev8 Support loading form with FeatureMgr.
 * IB 2010-08-15 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-08-15 1.11.5Dev8 Use featureMgr for super.getFeatureMgr().
 * IB 2010-08-15 1.11.5Dev8 Support loading form with FeatureMgr.
 * IB 2010-08-15 1.11.5Dev8 Use showMe for setCurrent(this).
 * IB 2010-09-27 1.11.5Dev8 Fix river of news/headers for MIDP 1.0 error.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-16 1.11.5Dev14 Have back be 1, cancel be 2, ok be 3, open be 4, and select be 5.
 * IB 2010-11-16 1.11.5Dev14 Don't have feed open property now that back will have consistent usage.
 * IB 2010-11-16 1.11.5Dev14 Use m_backCommand from RssReaderMIDlet all the time.
 * IB 2010-11-17 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
 * IB 2010-11-19 1.11.5Dev14 Move static var m_backCommand out of midlet class to FeatureMgr.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-12 1.11.5Alpha15 Use midlet in FeatureMgr with getRssMidlet to get the RssReaderMIDlet.
 * IB 2011-01-12 1.11.5Alpha15 Save old feed pointer to use to update feed for future background processing only for MIDP 2.0.
 * IB 2011-01-12 1.11.5Alpha15 After modifying the read field, use old feed pointer with new feed pointer to update the feed.  If the old pointer does not match the current pointer, do not update as it means that the future background processing has updated the feed already. 
 * IB 2011-01-11 1.11.5Dev15 Use super.featureMgr instead of featureMgr.
 * IB 2011-01-11 1.11.5Dev15 Have special reverseItems to change sort order.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-12 1.11.5Alpha15 Maintain only one vector for sorted items.
 * IB 2011-01-31 1.11.5Dev17 Change items to array to save on memory and for simplicity.
 * IB 2011-02-05 1.11.5Dev17 Allow regular and up memory devices to use bookmark name in title.  
 * IB 2011-03-06 1.11.5Dev17 Use RssItem instead of RssItunesItem to allow future difference in the two.
 * IB 2011-02-07 1.11.5Dev17 More logging.
 * IB 2011-03-07 1.11.5Dev17 Use getCmdAdd to have long labels for the commands.
 * IB 2011-03-06 1.11.5Dev17 Combine statements.
 * IB 2011-03-06 1.11.5Dev17 Use utility methods to convert from Vector to arrays.
 * IB 2011-03-06 1.11.5Dev17 Make sure midlet is not null for tests and future combination with other programs.
*/

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define full vers define
@DFULLVERSDEF@

//#ifdef DFULLVERS
package com.substanceofcode.rssreader.presentation;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
//#ifdef DLARGEMEM
import javax.microedition.lcdui.Gauge;
//#endif
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
// If not using the test UI define the J2ME UI's
//#ifndef DTESTUI
import javax.microedition.lcdui.List;
//#else
import com.substanceofcode.testlcdui.List;
import com.substanceofcode.testlcdui.LogActIntr;
//#endif
// If using the test UI define the Test UI's

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.LoadingForm;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItem;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.utils.MiscUtil;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form to add new/edit existing bookmark. */
public class AllNewsList extends FeatureList
	implements CommandListener, Runnable {

	final       Object nullPtr = null;
    protected Image       m_unreadImage;
    protected int       m_offset;             // Offset into bookmarkList
    protected int       m_len;                // length from offset above
    private boolean m_openItem = false;
    private boolean m_nextItem = false;
	//#ifdef DLARGEMEM
    protected final static int AND_VAL = 15;
    protected final static int DIV_VAL = 16;
	//#endif
	// The loading form
    protected boolean     m_sort      = false; // Process sort
    protected boolean     m_sortDesc;          // Sort descending
    protected boolean     m_sortByDate = true; // Sort by date
    protected boolean     m_showAll = false;  // Show both read and unread.
    protected boolean     m_showUnread = false;  // Show unread.
	//#ifdef DTESTUI
    protected boolean     m_testNews = false;     // True if auto testing news.
	boolean m_newsNext = false; // Flag to control opening the next header
	boolean m_itemNext = false; // Flag to control opening the next item
	//#endif
	//#ifdef DTESTUI
	protected int         m_newsIndex = -1; // Index in headers to auto test
	//#endif
    protected FeatureList m_bookmarkList;
    protected RssFeedStore m_rssFeeds;
    public final static String TITLE = "River of News";
	protected Command     m_openHeaderCmd;    // The open header command
    protected Command     m_sortUnreadItemsCmd;  // The sort unread items by date command
    protected Command     m_sortReadItemsCmd;  // The sort read items by date command
    protected Command     m_sortUnreadFeedsCmd;  // The sort unread items by feed command
    protected Command     m_sortReadFeedsCmd;  // The sort read items by feed command
    protected Command     m_sortAllDateCmd;  // The sort all items by date command
    protected Command     m_sortAllFeedsCmd;  // The sort read items by feed command
    protected Command     m_markReadCmd;      // Mark the item as read
    protected Command     m_markUnReadCmd;    // Mark the item as unread
    protected Command     m_directionCmd;    // Direction of sort asc/desc
	//#ifdef DTESTUI
	protected Command     m_testNewsCmd;       // Tet UI rss news command
	//#endif
    protected Vector      m_itemFeeds = new Vector();
	//#ifndef DMIDP10
    protected Vector      m_itemOldFeeds = new Vector();
	//#endif
    protected Vector      m_allItems = new Vector();
    protected RssItem m_item;
    volatile protected RssItunesFeed m_feed;
	volatile protected RssItunesFeed m_oldFeed;          // True if Itunes is enabled

	//#ifdef DLOGGING
    private Logger m_logger = Logger.getLogger("AllNewsList");
    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of AllNewsList
	    unreadImage - if non-null, put image for unread items for all list. */
	public AllNewsList(String title,
			int listType, int offset, int len,
					   final FeatureList bookmarkList,
					   final RssFeedStore rssFeeds,
					   Image unreadImage,
					   LoadingForm loadForm, int priority) {
		super(title, listType, loadForm);
		//#ifdef DLOGGING
		if (m_fineLoggable) {m_logger.fine("Starting AllNewsList");}
		//#endif

		m_offset = offset;
		m_len = len;
		m_bookmarkList = bookmarkList;
		m_rssFeeds = rssFeeds;
		m_unreadImage = unreadImage;
		//#ifdef DMIDP10
		m_oldFeed = null;
		//#endif
		m_openHeaderCmd     = FeatureMgr.getCmdAdd(this, "Open", null,
				Command.SCREEN, 5);
		super.addCommand(FeatureMgr.m_backCommand);
		m_sortUnreadItemsCmd = FeatureMgr.getCmdAdd(this, "Unread date",
				"Unread date sorted", Command.SCREEN, priority++);
		m_sortReadItemsCmd = FeatureMgr.getCmdAdd(this, "Read date",
				"Read date sorted", Command.SCREEN, priority++);
		m_sortUnreadFeedsCmd = FeatureMgr.getCmdAdd(this, "Unread feed",
				"Unread feed sorted", Command.SCREEN, priority++);
		m_sortReadFeedsCmd = FeatureMgr.getCmdAdd(this, "Read feed",
				"Read feed sorted", Command.SCREEN, priority++);
		m_sortAllDateCmd = FeatureMgr.getCmdAdd(this, "All date",
				"All date sorted", Command.SCREEN, priority++);
		m_sortAllFeedsCmd = FeatureMgr.getCmdAdd(this, "All feed",
				"All feed sorted", Command.SCREEN, priority++);
		m_markReadCmd = FeatureMgr.getCmdAdd(this, "Mark read",
				null, Command.SCREEN, priority++);
		m_markUnReadCmd = FeatureMgr.getCmdAdd(this, "Mark unread",
				null, Command.SCREEN, priority++);
		m_sortDesc  = !(this instanceof HeaderList);
		m_directionCmd = FeatureMgr.getCmdAdd(this, 
				m_sortDesc ? "Ascending" : "Descending",
				m_sortDesc ? "Sort ascending" : "Sort descending", Command.SCREEN, 100);
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("Constructor m_sortDesc,m_directionCmd=" + m_sortDesc + "," + FeatureMgr.logCmd(m_directionCmd));}
		//#endif
		//#ifdef DTESTUI
		m_testNewsCmd        = FeatureMgr.getCmdAdd(this, "Test news/items", null,
				Command.SCREEN, 11);
		//#endif
	}
	
	/* Initialize the read an unread news lists.  This is done when we
	   switch to the list. */
	final public void initNewsList(final int offset,
	                         final int len,
	                         final boolean showAll,
						     final boolean showUnread,
						     final boolean sortByDate,
							 final List bookmarkList,
							 final RssFeedStore rssFeeds) {

		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("initNewsList showAll,showUnread,sortByDate=" + showAll + "," + showUnread + "," + sortByDate);}
		//#endif
		this.m_showAll = showAll;
		this.m_showUnread = showUnread;
		this.m_sortByDate = sortByDate;
		final int bsize = bookmarkList.size();
		if( bsize > 0 ){
			initVectors();
			int last = offset + len;
			if (last > bsize) {
				last = bsize;
			}
			for( int ic = offset; ic < last; ic++ ){
			
				final RssItunesFeed feed = (RssItunesFeed)rssFeeds.get(
						bookmarkList.getString(ic));
				//#ifdef DTEST
				final RssItem[] gitems = (RssItem[])feed.getItems();
				//#else
				final RssItem[] gitems = feed.getItems();
				//#endif
				final int ilen = gitems.length;
				//#ifdef DLOGGING
				if (m_finestLoggable) {m_logger.finest("ic,feed.getName(),ilen=" + ic + "," + feed.getName() + "," + ilen);}
				//#endif
				if (ilen > 0) {
					/**
					 * Show currently selected RSS feed
					 * headers without updating them
					 */
					fillVectors( feed, gitems );
				}
			}
		}
	}

	/** Initialize item and feed name vectors. */
	private void initVectors() {
		m_allItems.removeAllElements();
		m_itemFeeds.removeAllElements();
		//#ifndef DMIDP10
		m_itemOldFeeds.removeAllElements();
		//#endif
	}

    /** Fill RSS item vectors */
    private void fillVectors( final RssItunesFeed feed, RssItem[] aitems) {
        final int itemLen = aitems.length;
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("fillVectors itemLen=" + itemLen);}
		//#endif

		for(int i = 0; i < itemLen; i++){
			final RssItem r = aitems[i];
			m_allItems.addElement(r);
			m_itemFeeds.addElement(feed);
			//#ifndef DMIDP10
			m_itemOldFeeds.addElement(feed);
			//#endif
		}
    }
    
	/* Sort all items. */
	final public void sortAllItems(final boolean sortByDate,
							 final FeatureList bookmarkList,
							 final RssFeedStore rssFeeds) {
		initNewsList(m_offset, m_len, true, false, sortByDate, bookmarkList,
				rssFeeds);
		if (sortByDate) {
			sortItems(false, m_allItems);
		} else if (m_sortDesc) {
			reverseItems(m_allItems);
		}
		fillItems( m_allItems);

	}

    /** Fill list from items */
    private void fillItems(final Vector sortedItems) {
		//#ifdef DMIDP20
		super.deleteAll();
		//#else
		int lc = super.size();
		while(--lc >= 0) {
			super.delete(lc);
		}
		//#endif
		final int slen = sortedItems.size();
		RssItem[] sitems = new RssItem[slen];
		sortedItems.copyInto(sitems);
		RssReaderMIDlet midlet = FeatureMgr.getRssMidlet();
		//#ifndef DSMALLMEM
		final boolean addName = (midlet != null) &&
			midlet.getSettings().getBookmarkNameNews();
		RssItunesFeed[] sfeeds = null;
		if (addName) {
			sfeeds = MiscUtil.getVecrFeed(m_itemFeeds);
		}
		//#endif
		for( int ic = 0; ic < slen; ic++){
			String text = sitems[ic].getTitle();
			if ((text.length() == 0) && (midlet != null)) {
				text = midlet.getItemDescription(sitems[ic]);
			}
			//#ifndef DSMALLMEM
			if (addName) {
				text = "[" + sfeeds[ic].getName() + "] " + text;
			}
			//#endif
			if (m_showAll && (m_unreadImage != null) &&
					sitems[ic].isUnreadItem()) {
				super.append( text, m_unreadImage );
			} else {
				super.append( text, null );
			}
		}
		if (slen > 0) {
			// Workaround for some phones leaving the cursor in the middle
			// of the screen.
			super.setSelectedIndex(0, true);
		}
		updTitle();
	}

	/* Get the current selected index (or 0 index) and update the vectors. */
	private void getUpdSel(boolean updateIt) {
		int selIdx = super.getSelectedIndex();
		if (selIdx == -1) {
			super.setSelectedIndex(0, true);
			selIdx = 0;
		}
		if (m_showAll) {
			m_item = (RssItem)m_allItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
			//#ifndef DMIDP10
			m_oldFeed = (RssItunesFeed)m_itemOldFeeds.elementAt(selIdx);
			//#endif
			if (updateIt && (m_unreadImage != null) && m_item.isUnreadItem()) {
				super.set(selIdx, super.getString(selIdx), null);
			}
		} else if (m_showUnread) {
			super.delete(selIdx);
			if (selIdx > 0) {
				super.setSelectedIndex(selIdx - 1, true);
			} else {
				super.setSelectedIndex(0, true);
			}
			m_item = (RssItem)m_allItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
			//#ifndef DMIDP10
			m_oldFeed = (RssItunesFeed)m_itemOldFeeds.elementAt(selIdx);
			//#endif
			if (updateIt) {
				m_allItems.removeElementAt(selIdx);
				m_itemFeeds.removeElementAt(selIdx);
				//#ifndef DMIDP10
				m_itemOldFeeds.removeElementAt(selIdx);
				//#endif
			}
			updTitle();
		} else {
			m_item = (RssItem)m_allItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
			//#ifndef DMIDP10
			m_oldFeed = (RssItunesFeed)m_itemOldFeeds.elementAt(selIdx);
			//#endif
		}
		/**
		 * Show currently selected RSS item
		 * without updating it
		 */
		if (updateIt) {
			m_item.setUnreadItem(false);
			m_rssFeeds.put(m_feed, m_oldFeed);
			//#ifndef DMIDP10
			m_oldFeed = m_rssFeeds.get(m_feed);
			m_itemOldFeeds.setElementAt(m_oldFeed, selIdx);
			//#endif
		}
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("getUpdSel updateIt,selIdx=" + updateIt + "," + selIdx);}
		//#endif
	}

	/* Update the lists for mark or unmark. */
	private void updMarkSel(boolean markUnread) {
		int selIdx = super.getSelectedIndex();
		if (selIdx == -1) {
			super.setSelectedIndex(0, true);
			selIdx = 0;
		}
		if (m_showAll) {
			m_item = (RssItem)m_allItems.elementAt(selIdx);
			if (m_unreadImage != null) {
				if (!markUnread && m_item.isUnreadItem()) {
					super.set(selIdx, super.getString(selIdx), null);
				} else if (markUnread && !m_item.isUnreadItem()) {
					super.set(selIdx, super.getString(selIdx), m_unreadImage);
				}
			}
			m_item.setUnreadItem(markUnread);
			m_rssFeeds.put(m_feed, m_oldFeed);
			//#ifndef DMIDP10
			m_oldFeed = m_rssFeeds.get(m_feed);
			m_itemOldFeeds.setElementAt(m_oldFeed, selIdx);
			//#endif
		} else {
			if ((m_showUnread && !markUnread) ||
					(!m_showUnread && markUnread)) {
				super.delete(selIdx);
				if (selIdx > 0) {
					super.setSelectedIndex(selIdx - 1, true);
				}
				if (m_showUnread) {
					m_item = (RssItem)m_allItems.elementAt(selIdx);
					m_allItems.removeElementAt(selIdx);
				} else {
					m_item = (RssItem)m_allItems.elementAt(selIdx);
					m_allItems.removeElementAt(selIdx);
				}
				m_item.setUnreadItem(markUnread);
				m_itemFeeds.removeElementAt(selIdx);
				//#ifndef DMIDP10
				m_itemOldFeeds.removeElementAt(selIdx);
				//#endif
				m_rssFeeds.put(m_feed, m_oldFeed);
				// Since we removed an element, we cannot update it.
				m_oldFeed = m_rssFeeds.get(m_feed);
			}
			updTitle();
		}

	}

    /** Run method is used to sort RSS feed */
    public void run() {

		//#ifdef DLOGGING
		boolean newSort = false;
		//#endif
		boolean csort = false;
		try {
			csort = false;
			synchronized(this) {
				if ( m_sort ) {
					csort = true;
					m_sort = false;
				}
			}
			/* Sort the read or unread items. */
			if ( csort ) {
				csort = false;
				nextItem(false);

				//#ifdef DLOGGING
				newSort = true;
				//#endif
				LoadingForm loadForm = LoadingForm.getLoadingForm(
							"Sorting items...", this, null);
				super.featureMgr.setLoadForm(loadForm);
				if (m_showAll) {
					sortAllItems( m_sortByDate, m_bookmarkList, m_rssFeeds );
				} else if (m_showUnread) {
					sortUnreadItems( m_sortByDate, m_bookmarkList, m_rssFeeds );
				} else {
					sortReadItems( m_sortByDate, m_bookmarkList, m_rssFeeds );
				}
				loadForm.setLoadingFinished("Sorting finished",
						"Sorting finished use back to return.");
				super.featureMgr.showMe();
			}

			if ( m_openItem || m_nextItem ) {
				int selIdx = FeatureMgr.getSelectedIndex(this);
				//#ifdef DLOGGING
				if (m_finestLoggable) {m_logger.finest("run selIdx,m_openItem,m_nextItem=" + selIdx + "," + m_openItem + "," + m_nextItem);}
				//#endif
				synchronized(this) {
					m_nextItem = false;
					m_openItem = false;
				}
				if( selIdx >= 0){
					LoadingForm loadForm = LoadingForm.getLoadingForm(
							"Loading item...", this, null);
					super.featureMgr.setLoadForm(loadForm);
					try {
						getUpdSel(true);
						FeatureMgr.getRssMidlet().initializeItemForm( m_feed,
								m_item, this,
								loadForm);
						//#ifdef DTESTUI
							checkTest();
						//#endif
					}catch(OutOfMemoryError t) {
						loadForm.recordExcForm(
								"Out Of Memory Error selecting item", t);
					}
				}
			}

			//#ifdef DTESTUI
			// If there are headers, and the header index is >= 0,
			// open the header so that it's items can be listed
			// with test UI classes.
			// Need to change the selection to match the m_newsIndex.
			synchronized(this) {
				if (m_newsNext && (m_newsIndex >= 0) && m_testNews &&
					(m_newsIndex < super.size()) &&
					(FeatureMgr.getCurrent() == this)) {
					m_newsNext = false;
					if (super.getSelectedIndex() >= 0) {
						super.setSelectedIndex(
								super.getSelectedIndex(), false);
					}
					super.setSelectedIndex(m_newsIndex, true);
					commandAction(List.SELECT_COMMAND, this);
				}
				// After intializing the form (which was already logged by
				// testui classes), simulate the back command
				if (m_itemNext && (m_newsIndex >= 0) && m_testNews &&
					(m_newsIndex < super.size())) {
					RssReaderMIDlet midlet = FeatureMgr.getRssMidlet();
					if ((midlet != null) && midlet.isItemForm()) {
						m_itemNext = false;
						m_newsNext = true;
						midlet.backFrItemForm();
					}
				}
			}
			//#endif

		}catch(OutOfMemoryError t) {
			super.featureMgr.getLoadForm().recordExcForm("\nOut Of Memory Error sorting items", t);
		}catch(Throwable t) {
			super.featureMgr.getLoadForm().recordExcForm("\nInternal error sorting items", t);
		}

		//#ifdef DLOGGING
		if (newSort) {
			newSort = false;
			if (m_finestLoggable) {m_logger.finest("run m_sortDesc,csort,m_showUnread,m_sortByDate,m_showAll=" + m_sortDesc + "," + csort + "," + m_showUnread + "," + m_sortByDate + "," + m_showAll);}
		}
		//#endif
	}

	//#ifdef DTESTUI
	private void checkTest() {
		m_itemNext = true;
		m_newsIndex--;
		if (m_newsIndex < 0) {
			m_testNews = false;
			m_newsNext = false;
			m_itemNext = false;
			System.out.println("Test UI Test Rss items last");
		}
	}

	/** If auto testing news, set flag that says that we're going back to
	    the news menu from item. */
	final public void gotoNews() {
		if (m_testNews) {
			if (super.size() == 0) {
				m_testNews = false;
				m_newsIndex = -1;
				m_newsNext = false;
				m_itemNext = false;
			}
			m_newsNext = true;
		}
	}
	//#endif

	/** Update the title.  Override to get another title.*/
	public void updTitle() {
		if (m_showAll) {
			super.setTitle("All items " + (m_sortByDate ? "date" : "feed") +
					" sorted:  " + super.size());
		} else if (m_showUnread) {
			super.setTitle("Unread items " + (m_sortByDate ? "date" : "feed") +
					" sorted:  " + super.size());
		} else {
			super.setTitle("Read items " + (m_sortByDate ? "date" : "feed") +
					" sorted:  " + super.size());
		}
	}

	/** Sort items unread. */
	final public void sortUnreadItems(final boolean sortByDate,
								final List bookmarkList,
								final RssFeedStore rssFeeds) {
		initNewsList(m_offset, m_len, false, true, sortByDate, bookmarkList, rssFeeds);
		if (sortByDate) {
			sortItems(true, m_allItems);
		} else if (m_sortDesc) {
			reverseItems(m_allItems);
		}
		fillItems( m_allItems);
	}

	/* Sort items read. */
	final public void sortReadItems(boolean sortByDate,
							  List bookmarkList, RssFeedStore rssFeeds) {
		initNewsList(m_offset, m_len, false, false, sortByDate, bookmarkList, rssFeeds);
		if (sortByDate) {
			sortItems(false, m_allItems);
		} else if (m_sortDesc) {
			reverseItems(m_allItems);
		}
		fillItems( m_allItems);
	}

	/* Sort items read or unread vector and put into GUI list. */
	private void sortItems(final boolean showUnread,
			final Vector unsortedItems) {

		if (unsortedItems.size() == 0) {
			return;
		}
		//#ifdef DLARGEMEM
		final int diffDelta = unsortedItems.size() / DIV_VAL;
		Gauge gauge = new Gauge("Preparing to sort...", false, diffDelta + 1, 0);
		int gcnt = 0;
		LoadingForm loadForm = featureMgr.getLoadForm();
		loadForm.append(gauge);
		//#endif
		this.m_showUnread = showUnread;
		int[] indexes = new int[unsortedItems.size()];
		long[] ldates = new long[unsortedItems.size()];
		Vector vsorted = new Vector(unsortedItems.size());
		Vector vfeedSorted = new Vector(m_itemFeeds.size());
		//#ifndef DMIDP10
		Vector voldFeedSorted = new Vector(m_itemOldFeeds.size());
		//#endif
		Vector vunsorted = new Vector(unsortedItems.size());
		Vector vfeedUnsorted = new Vector(m_itemFeeds.size());
		//#ifndef DMIDP10
		Vector voldFeedUnsorted = new Vector(m_itemOldFeeds.size());
		//#endif
		RssItem[] uitems = MiscUtil.getVecrItem(unsortedItems);
		RssItunesFeed[] ufeeds = MiscUtil.getVecrFeed(m_itemFeeds);
		//#ifndef DMIDP10
		RssItunesFeed[] uoldFeeds = new RssItunesFeed[m_itemOldFeeds.size()];
		m_itemOldFeeds.copyInto(uoldFeeds);
		//#endif
		int kc = 0;
		for (int ic = 0; ic < uitems.length; ic++) {
			Date sortDate = uitems[ic].getDate();
			//#ifdef DITUNES
			if (sortDate == null)
			{
				sortDate = ufeeds[ic].getDate();
				//#ifdef DLOGGING
				if (m_finestLoggable) {m_logger.finest("Using feed date for item=" + ic + "," + ufeeds[ic].getName() + "," + sortDate + "," + uitems[ic].getTitle());}
				//#endif
			}
			//#endif
			if (sortDate == null) {
				vsorted.addElement(uitems[ic]);
				vfeedSorted.addElement(ufeeds[ic]);
				//#ifndef DMIDP10
				voldFeedSorted.addElement(uoldFeeds[ic]);
				//#endif
			} else {
				indexes[kc] = kc;
				ldates[kc++] = m_sortDesc ?
					sortDate.getTime() : -sortDate.getTime();
				vunsorted.addElement(uitems[ic]);
				vfeedUnsorted.addElement(ufeeds[ic]);
				//#ifndef DMIDP10
				voldFeedUnsorted.addElement(uoldFeeds[ic]);
				//#endif
				//#ifdef DLOGGING
				if (m_finestLoggable) {
					if (m_sortDesc) {
						m_logger.finest("kc,date=" + ic + "," + new Date(ldates[kc - 1]));
					} else {
						m_logger.finest("kc,date=" + ic + "," + new Date(-ldates[kc - 1]));
					}
				}
				//#endif
			}
			//#ifdef DLARGEMEM
			if ((ic & AND_VAL) == 0) {
				gauge.setValue(gcnt++);
			}
			//#endif
		}
		// Save memory by making null
		uitems = (RssItem[])nullPtr;
		ufeeds = (RssItunesFeed[])nullPtr;
		//#ifndef DMIDP10
		uoldFeeds = (RssItunesFeed[])nullPtr;
		//#endif
		//#ifdef DLARGEMEM
		//#ifdef DMIDP20
		gauge.setValue(diffDelta + 1);
		gauge = new Gauge("Sorting...", false, Gauge.INDEFINITE,
				Gauge.CONTINUOUS_RUNNING);
		int gitem = loadForm.append(gauge);
		//#else
		loadForm.append("Sorting...\n");
		//#endif
		//#endif
		MiscUtil.indexedSort(ldates, indexes, kc - 1);
		//#ifdef DLARGEMEM
		//#ifdef DMIDP20
		loadForm.set(gitem, new StringItem(null, "Sorting finished."));
		//#endif
		gauge = new Gauge("After sorting 1...", false, diffDelta + 1, 0);
		loadForm.append(gauge);
		gcnt = 0;
		//#endif
		uitems = new RssItem[kc];
		vunsorted.copyInto(uitems);
		// Save memory by making null
		vunsorted = (Vector)nullPtr;
		ufeeds = new RssItunesFeed[kc];
		vfeedUnsorted.copyInto(ufeeds);
		// Save memory by making null
		vfeedUnsorted = (Vector)nullPtr;
		//#ifndef DMIDP10
		uoldFeeds = new RssItunesFeed[kc];
		voldFeedUnsorted.copyInto(uoldFeeds);
		// Save memory by making null
		voldFeedUnsorted = (Vector)nullPtr;
		//#endif
		for (int ic = 0; ic < kc ; ic++) {
			//#ifdef DLOGGING
			if (m_finestLoggable) {m_logger.finest("ic,index,date=" + ic + "," + indexes[ic] + "," + new Date(ldates[indexes[ic]]) + "," + uitems[indexes[ic]].getDate());}
			//#endif
			vsorted.insertElementAt(uitems[indexes[ic]], 0);
			vfeedSorted.insertElementAt(ufeeds[indexes[ic]], 0);
			//#ifndef DMIDP10
			voldFeedSorted.insertElementAt(uoldFeeds[indexes[ic]], 0);
			//#endif
			//#ifdef DLARGEMEM
			if ((ic & AND_VAL) == 0) {
				gauge.setValue(gcnt++);
			}
			//#endif
		}
		// Save memory by making null
		uitems = (RssItem[])nullPtr;
		//#ifdef DLARGEMEM
		gauge.setValue(diffDelta + 1);
		gauge = new Gauge("After sorting 2...", false, diffDelta + 1, 0);
		loadForm.append(gauge);
		gcnt = 0;
		//#endif
		RssItem[] sitems = MiscUtil.getVecrItem(vsorted);
		// Save memory by making null
		vsorted = (Vector)nullPtr;
		unsortedItems.removeAllElements();
		for( int ic = 0; ic < sitems.length; ic++){
			unsortedItems.addElement(sitems[ic]);
			//#ifdef DLARGEMEM
			if ((ic & AND_VAL) == 0) {
				gauge.setValue(gcnt++);
			}
			//#endif
		}
		// Save memory by making null
		ufeeds = (RssItunesFeed[])nullPtr;
		//#ifndef DMIDP10
		uoldFeeds = (RssItunesFeed[])nullPtr;
		//#endif
		//#ifdef DLARGEMEM
		gauge.setValue(diffDelta + 1);
		gauge = new Gauge("After sorting 3...", false, diffDelta + 1, 0);
		loadForm.append(gauge);
		gcnt = 0;
		//#endif
		RssItunesFeed[] sfeeds = MiscUtil.getVecrFeed(vfeedSorted);
		// Save memory by making null
		vfeedSorted = (Vector)nullPtr;
		//#ifndef DMIDP10
		RssItunesFeed[] soldFeeds = MiscUtil.getVecrFeed(voldFeedSorted);
		// Save memory by making null
		voldFeedSorted = (Vector)nullPtr;
		//#endif
		m_itemFeeds.removeAllElements();
		//#ifndef DMIDP10
		m_itemOldFeeds.removeAllElements();
		//#endif
		for( int ic = 0; ic < sitems.length; ic++){
			m_itemFeeds.addElement(sfeeds[ic]);
		//#ifndef DMIDP10
			m_itemOldFeeds.addElement(sfeeds[ic]);
		//#endif
			//#ifdef DLARGEMEM
			if ((ic & AND_VAL) == 0) {
				gauge.setValue(gcnt++);
			}
			//#endif
		}
		//#ifdef DLARGEMEM
		gauge.setValue(diffDelta + 1);
		//#endif
	}

	/* Reverse the order of the items to allow reverse (descending) feed sort. */
	private void reverseItems(final Vector unsortedItems) {
		final int ulen = unsortedItems.size();
		final int flen = m_itemFeeds.size();
		RssItem[] items = new RssItem[ulen];
		unsortedItems.copyInto(items);
		RssItunesFeed[] feeds = new RssItunesFeed[flen];
		m_itemFeeds.copyInto(feeds);
		int l = ulen - 1;
		for (int i = 0; i < ulen; i++) {
			unsortedItems.insertElementAt(items[l], i);
			unsortedItems.removeElementAt(ulen);
			m_itemFeeds.insertElementAt(feeds[l--], i);
			m_itemFeeds.removeElementAt(ulen);
		}
	}

	public void commandAction(Command c, Displayable s) {

		//#ifdef DTESTUI
		if (m_testNews && (m_newsIndex >= 0)) {
			((LogActIntr)this).outputCmdAct(c, s);
		}
		//#endif
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("commandAction c=" + c.getLabel());}
		//#endif

		synchronized(this) {
			if( c == m_sortUnreadItemsCmd ) {
				m_showUnread = true;
				m_sortByDate = true;
				m_showAll = false;
				m_sort = true;
			}
			
			/** Read read items date sorted */
			if( c == m_sortReadItemsCmd ) {
				m_showUnread = false;
				m_sortByDate = true;
				m_showAll = false;
				m_sort = true;
			}
			
			/** Read unread items feed sorted */
			if( c == m_sortUnreadFeedsCmd ) {
				m_showUnread = true;
				m_sortByDate = false;
				m_showAll = false;
				m_sort = true;
			}
			
			/** Read read items feed sorted */
			if( c == m_sortReadFeedsCmd ) {
				m_showUnread = false;
				m_sortByDate = false;
				m_showAll = false;
				m_sort = true;
			}
		}
        
		try {
			if( (c == m_openHeaderCmd) || (c == List.SELECT_COMMAND) ) {
				m_openItem = true;
			} else if( c == m_sortAllDateCmd ) {
				synchronized(this) {
					m_showUnread = false;
					m_sortByDate = true;
					m_showAll = true;
					m_sort = true;
				}
			} else if( c == m_sortAllFeedsCmd ) {
				synchronized(this) {
					m_showUnread = false;
					m_sortByDate = false;
					m_showAll = true;
					m_sort = true;
				}
			} else if( c == m_markReadCmd ) {
				if( super.size()>0){
					updMarkSel(false);

				}
			} else if( c == m_markUnReadCmd ) {
				if( super.size()>0){
					updMarkSel(true);
				}
			} else if( c == m_directionCmd ) {
				if( super.size()>0){
					synchronized(this) {
						super.removeCommand(m_directionCmd);
						if (m_sortDesc) {
							m_sortDesc = false;
							m_directionCmd = FeatureMgr.getCmdAdd(this,
									"Descending", "Sort descending",
									Command.SCREEN, 100);
						} else {
							m_sortDesc = true;
							m_directionCmd = FeatureMgr.getCmdAdd(this,
									"Ascending", "Sort ascending", Command.SCREEN, 100);
						}
						m_sort = true;
						//#ifdef DLOGGING
						if (m_finestLoggable) {m_logger.finest("Constructor m_sort,m_sortDesc,m_directionCmd=" + m_sort + "," + m_sortDesc + "," + FeatureMgr.logCmd(m_directionCmd));}
						//#endif
					}
				}
			/** Get back to RSS feed bookmarks */
			} else if( c == FeatureMgr.m_backCommand ){
				//#ifdef DTESTUI
				synchronized(this) {
					m_testNews = false;
					m_newsIndex = -1;
					m_newsNext = false;
					m_itemNext = false;
				}
				//#endif
				super.featureMgr.setBackground(false);
				super.featureMgr.getLoadForm().replaceRef(this, null);
				RssReaderMIDlet midlet;
				if ((midlet = FeatureMgr.getRssMidlet()) != null) {
					midlet.showBookmarkList();
				}

				//#ifdef DTESTUI
				/** Indicate that we want to test the headers/items.  */
			} else if( c == m_testNewsCmd) {
				synchronized(this) {
					if( super.size()>0 ) {
						m_itemNext = false;
						m_newsIndex = super.size() - 1;
						System.out.println("Test UI Test News items start m_newsIndex=" + m_newsIndex);
						m_newsNext = true;
						m_testNews = true;
						super.removeCommand(m_testNewsCmd);
					}
				}
				//#endif

			}

			//#ifdef DLOGGING
			if (m_finestLoggable) {m_logger.finest("commandAction m_sortDesc,m_sort,m_showUnread,m_sortByDate,m_showAll=" + m_sortDesc + "," + m_sort + "," + m_showUnread + "," + m_sortByDate + "," + m_showAll);}
			//#endif
			
		}catch(Throwable t) {
			super.featureMgr.getLoadForm().recordExcForm("Internal error", t);
		}
	}

	public void nextItem(boolean next) {
		if (!next) {
			return;
		}
		int selIdx = FeatureMgr.getSelectedIndex(this);
		if (selIdx < 0) {
			return;
		}
		int asize = super.size();
		int nselected;
		if (m_showUnread) {
			nselected = selIdx;
		} else {
			nselected = selIdx + 1;
		}
		synchronized(this) {
			if (nselected < asize) {
				super.setSelectedIndex(nselected, true);
				synchronized(this) {
					m_nextItem = true;
				}
				super.featureMgr.wakeup(2);
			}
		}
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("nextItem nselected,asize,m_nextItem=" + nselected + "," + asize + "," + m_nextItem);}
		//#endif
		super.featureMgr.wakeup(2);
	}

}
//#endif
