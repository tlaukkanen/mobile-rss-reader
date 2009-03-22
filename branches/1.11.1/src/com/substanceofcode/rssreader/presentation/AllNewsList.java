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
//@import com.substanceofcode.testlcdui.LogActIntr;
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
public class AllNewsList extends FeatureList
	implements CommandListener, Runnable {

    protected Image       m_unreadImage;
    protected int       m_offset;             // Offset into bookmarkList
    protected int       m_len;                // length from offset above
	//#ifdef DLARGEMEM
//@    protected final static int AND_VAL = 15;
//@    protected final static int DIV_VAL = 16;
	//#endif
    protected RssReaderMIDlet m_midlet;
	// The loading form
	protected RssReaderMIDlet.LoadingForm m_loadForm;
    protected boolean     m_sort      = false; // Process sort
    protected boolean     m_sortDesc;          // Sort descending
    protected boolean     m_sortByDate = true; // Sort by date
    protected boolean     m_showAll = false;  // Show both read and unread.
    protected boolean     m_showUnread = false;  // Show unread.
	//#ifdef DTESTUI
//@    protected boolean     m_testNews = false;     // True if auto testing news.
//@	boolean m_newsNext = false; // Flag to control opening the next header
//@	boolean m_itemNext = false; // Flag to control opening the next item
	//#endif
	//#ifdef DTESTUI
//@	protected int         m_newsIndex = -1; // Index in headers to auto test
	//#endif
    protected FeatureList m_bookmarkList;
    protected Hashtable m_rssFeeds;
    public final static String TITLE = "River of News";
	protected Command     m_openHeaderCmd;    // The open header command
	protected Command     m_backHeaderCmd;    // The back to bookmark list command
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
//@	protected Command     m_testNewsCmd;       // Tet UI rss news command
	//#endif
    protected Vector      m_itemFeeds = new Vector();
    protected Vector      m_allItems = new Vector();
    protected Vector      m_unreadItems = new Vector();
    protected Vector      m_readItems = new Vector();
    protected RssItunesItem m_item;
    protected RssItunesFeed m_feed;

	//#ifdef DLOGGING
//@    private Logger m_logger = Logger.getLogger("AllNewsList");
//@    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
//@    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of AllNewsList
	    unreadImage - if non-null, put image for unread items for all list. */
	public AllNewsList(final RssReaderMIDlet midlet, String title,
			int listType, int offset, int len,
					   final FeatureList bookmarkList,
					   final Hashtable rssFeeds,
					   Image unreadImage,
			RssReaderMIDlet.LoadingForm loadForm, int priority) {
		super(midlet, title, listType);
		//#ifdef DLOGGING
//@		if (m_fineLoggable) {m_logger.fine("Starting AllNewsList");}
		//#endif

		m_midlet = midlet;
		m_offset = offset;
		m_len = len;
		m_loadForm = loadForm;
		m_bookmarkList = bookmarkList;
		m_rssFeeds = rssFeeds;
		m_unreadImage = unreadImage;
		final boolean open1st = midlet.getSettings().getFeedListOpen();
		//#ifdef DLOGGING
//@		if (m_fineLoggable) {m_logger.fine("AllNewsList open1st=" + open1st);}
		//#endif
		m_openHeaderCmd     = new Command("Open", Command.SCREEN,
				(open1st ? 1 : 2));
		m_backHeaderCmd     = new Command("Back", Command.BACK,
				(open1st ? 2 : 1));
		priority++;
		super.addCommand(m_openHeaderCmd);
		super.addCommand(m_backHeaderCmd);
		m_sortUnreadItemsCmd = new Command("Unread date sorted",
										   Command.SCREEN, priority++);
		m_sortReadItemsCmd = new Command("Read date sorted",
										   Command.SCREEN, priority++);
		m_sortUnreadFeedsCmd = new Command("Unread feed sorted",
										   Command.SCREEN, priority++);
		m_sortReadFeedsCmd = new Command("Read feed sorted",
										   Command.SCREEN, priority++);
		m_sortAllDateCmd = new Command("All date sorted",
										   Command.SCREEN, priority++);
		m_sortAllFeedsCmd = new Command("All feed sorted",
										   Command.SCREEN, priority++);
		m_markReadCmd = new Command("Mark read", Command.SCREEN, priority++);
		m_markUnReadCmd = new Command("Mark unread", Command.SCREEN, priority++);
		m_sortDesc  = !(this instanceof HeaderList);
		m_directionCmd = new Command(
				m_sortDesc ? "Sort ascending" : "Sort descending", Command.SCREEN, priority++);
        super.addCommand(m_sortUnreadItemsCmd);
        super.addCommand(m_sortReadItemsCmd);
        super.addCommand(m_sortUnreadFeedsCmd);
        super.addCommand(m_sortReadFeedsCmd);
        super.addCommand(m_sortAllDateCmd);
        super.addCommand(m_sortAllFeedsCmd);
        super.addCommand(m_markReadCmd);
        super.addCommand(m_markUnReadCmd);
        super.addCommand(m_directionCmd);
		//#ifdef DTESTUI
//@		m_testNewsCmd        = new Command("Test news/items", Command.SCREEN, 11);
		//#endif
		//#ifdef DTESTUI
//@        super.addCommand(m_testNewsCmd);
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
							 final Hashtable rssFeeds) {

		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("initNewsList showAll,showUnread,sortByDate=" + showAll + "," + showUnread + "," + sortByDate);}
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
				final Vector vitems = feed.getItems();
				if( vitems.size()>0 ) {
					/**
					 * Show currently selected RSS feed
					 * headers without updating them
					 */
					fillVectors( feed, vitems );
				}
			}
		}
	}

	/** Initialize item and feed name vectors. */
	private void initVectors() {
		m_unreadItems.removeAllElements();
		m_readItems.removeAllElements();
		m_allItems.removeAllElements();
		m_itemFeeds.removeAllElements();
	}

    /** Fill RSS item vectors */
    private void fillVectors( final RssItunesFeed feed, final Vector vitems ) {
        final int itemLen = vitems.size();
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("fillVectors itemLen=" + itemLen);}
		//#endif
		RssItunesItem[] aitems = new RssItunesItem[itemLen];
		vitems.copyInto(aitems);
        for(int i=0; i < itemLen; i++){
            final RssItunesItem r = aitems[i];
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
    
	/* Sort all items. */
	final public void sortAllItems(final boolean sortByDate,
							 final FeatureList bookmarkList,
							 final Hashtable rssFeeds) {
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
//@		int lc = super.size() - 1;
//@		while(lc-- >= 0) {
//@			super.delete(lc);
//@		}
		//#endif
		final int slen = sortedItems.size();
		RssItunesItem [] sitems = new RssItunesItem[slen];
		sortedItems.copyInto(sitems);
		//#ifdef DMIDP20
		final boolean addName = m_midlet.getSettings().getBookmarkNameNews();
		RssItunesFeed [] sfeeds = null;
		if (addName) {
			sfeeds = new RssItunesFeed[slen];
			m_itemFeeds.copyInto(sfeeds);
		}
		//#endif
		for( int ic = 0; ic < slen; ic++){
			String text = sitems[ic].getTitle();
			if (text.length() == 0) {
				text = m_midlet.getItemDescription(sitems[ic]);
			}
			//#ifdef DMIDP20
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
			m_item = (RssItunesItem)m_allItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
			if (updateIt && (m_unreadImage != null) && m_item.isUnreadItem()) {
				super.set(selIdx, super.getString(selIdx), null);
			}
		} else if (m_showUnread) {
			super.delete(selIdx);
			if (selIdx > 0) {
				super.setSelectedIndex(selIdx - 1, true);
			}
			m_item = (RssItunesItem)m_unreadItems.elementAt(selIdx);
			m_feed = (RssItunesFeed)m_itemFeeds.elementAt(selIdx);
			if (updateIt) {
				m_unreadItems.removeElementAt(selIdx);
				m_itemFeeds.removeElementAt(selIdx);
			}
			updTitle();
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

	/* Update the lists for mark or unmark. */
	private void updMarkSel(boolean markUnread) {
		int selIdx = super.getSelectedIndex();
		if (selIdx == -1) {
			super.setSelectedIndex(0, true);
			selIdx = 0;
		}
		if (m_showAll) {
			m_item = (RssItunesItem)m_allItems.elementAt(selIdx);
			if (m_unreadImage != null) {
				if (!markUnread && m_item.isUnreadItem()) {
					super.set(selIdx, super.getString(selIdx), null);
				} else if (markUnread && !m_item.isUnreadItem()) {
					super.set(selIdx, super.getString(selIdx), m_unreadImage);
				}
			}
			m_item.setUnreadItem(markUnread);
		} else {
			if ((m_showUnread && !markUnread) ||
					(!m_showUnread && markUnread)) {
				super.delete(selIdx);
				if (selIdx > 0) {
					super.setSelectedIndex(selIdx - 1, true);
				}
				if (m_showUnread) {
					m_item = (RssItunesItem)m_unreadItems.elementAt(selIdx);
					m_unreadItems.removeElementAt(selIdx);
				} else {
					m_item = (RssItunesItem)m_readItems.elementAt(selIdx);
					m_readItems.removeElementAt(selIdx);
				}
				m_item.setUnreadItem(markUnread);
				m_itemFeeds.removeElementAt(selIdx);
			}
			updTitle();
		}

	}

    /** Run method is used to sort RSS feed */
    public void run() {

		//#ifdef DLOGGING
//@		boolean newSort = false;
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

				//#ifdef DLOGGING
//@				newSort = true;
				//#endif
				m_midlet.initializeLoadingForm("Sorting items...", this);
				if (m_showAll) {
					sortAllItems( m_sortByDate, m_bookmarkList, m_rssFeeds );
				} else if (m_showUnread) {
					sortUnreadItems( m_sortByDate, m_bookmarkList, m_rssFeeds );
				} else {
					sortReadItems( m_sortByDate, m_bookmarkList, m_rssFeeds );
				}
				m_loadForm.setLoadingFinished("Sorting finished",
						"Sorting finished use back to return.");
				m_midlet.setCurrent(this);
			}

			//#ifdef DTESTUI
//@			// If there are headers, and the header index is >= 0,
//@			// open the header so that it's items can be listed
//@			// with test UI classes.
//@			// Need to change the selection to match the m_newsIndex.
//@			synchronized(this) {
//@				if (m_newsNext && (m_newsIndex >= 0) && m_testNews &&
//@					(m_newsIndex < super.size()) &&
//@					(m_midlet.getCurrent() == this)) {
//@					m_newsNext = false;
//@					if (super.getSelectedIndex() >= 0) {
//@						super.setSelectedIndex(
//@								super.getSelectedIndex(), false);
//@					}
//@					super.setSelectedIndex(m_newsIndex, true);
//@					commandAction(List.SELECT_COMMAND, this);
//@				}
//@				// After intializing the form (which was already logged by
//@				// testui classes), simulate the back command
//@				if (m_itemNext && (m_newsIndex >= 0) && m_testNews &&
//@					(m_newsIndex < super.size()) && m_midlet.isItemForm()) {
//@					m_itemNext = false;
//@					m_newsNext = true;
//@					m_midlet.backFrItemForm();
//@				}
//@			}
			//#endif

		}catch(OutOfMemoryError t) {
			m_loadForm.recordExcForm("\nOut Of Memory Error sorting items", t);
		}catch(Throwable t) {
			m_loadForm.recordExcForm("\nInternal error sorting items", t);
		}

		//#ifdef DLOGGING
//@		if (newSort) {
//@			newSort = false;
//@			if (m_finestLoggable) {m_logger.finest("run m_sortDesc,csort,m_showUnread,m_sortByDate,m_showAll=" + m_sortDesc + "," + csort + "," + m_showUnread + "," + m_sortByDate + "," + m_showAll);}
//@		}
		//#endif
	}

	//#ifdef DTESTUI
//@	private void checkTest() {
//@		if (m_newsIndex < 0) {
//@			m_testNews = false;
//@			m_newsNext = false;
//@			m_itemNext = false;
//@			System.out.println("Test UI Test Rss items last");
//@		}
//@	}
//@
//@	/** If auto testing news, set flag that says that we're going back to
//@	    the news menu from item. */
//@	final public void gotoNews() {
//@		if (m_testNews) {
//@			if (super.size() == 0) {
//@				m_testNews = false;
//@				m_newsIndex = -1;
//@				m_newsNext = false;
//@				m_itemNext = false;
//@			}
//@			m_newsNext = true;
//@		}
//@	}
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
								final Hashtable rssFeeds) {
		initNewsList(m_offset, m_len, false, true, sortByDate, bookmarkList, rssFeeds);
		if (sortByDate) {
			sortItems(true, m_unreadItems);
		} else if (m_sortDesc) {
			reverseItems(m_unreadItems);
		}
		fillItems( m_unreadItems);
	}

	/* Sort items read. */
	final public void sortReadItems(boolean sortByDate,
							  List bookmarkList, Hashtable rssFeeds) {
		initNewsList(m_offset, m_len, false, false, sortByDate, bookmarkList, rssFeeds);
		if (sortByDate) {
			sortItems(false, m_readItems);
		} else if (m_sortDesc) {
			reverseItems(m_readItems);
		}
		fillItems( m_readItems);
	}

	/* Sort items read or unread vector and put into GUI list. */
	private void sortItems(final boolean showUnread,
			final Vector unsortedItems) {

		if (unsortedItems.size() == 0) {
			return;
		}
		//#ifdef DLARGEMEM
//@		final int diffDelta = unsortedItems.size() / DIV_VAL;
//@		Gauge gauge = new Gauge("Preparing to sort...", false, diffDelta + 1, 0);
//@		int gcnt = 0;
//@		m_loadForm = m_midlet.getLoadForm();
//@		m_loadForm.append(gauge);
		//#endif
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
			Date sortDate = uitems[ic].getDate();
			//#ifdef DITUNES
//@			if (sortDate == null)
//@			{
//@				sortDate = ufeeds[ic].getDate();
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest("Using feed date for item=" + ic + "," + ufeeds[ic].getName() + "," + sortDate + "," + uitems[ic].getTitle());}
				//#endif
//@			}
			//#endif
			if (sortDate == null)
			{
				vsorted.addElement(uitems[ic]);
				vfeedSorted.addElement(ufeeds[ic]);
			} else {
				indexes[kc] = kc;
				ldates[kc++] = m_sortDesc ?
					sortDate.getTime() : -sortDate.getTime();
				vunsorted.addElement(uitems[ic]);
				vfeedUnsorted.addElement(ufeeds[ic]);
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {
//@					if (m_sortDesc) {
//@						m_logger.finest("kc,date=" + ic + "," + new Date(ldates[kc - 1]));
//@					} else {
//@						m_logger.finest("kc,date=" + ic + "," + new Date(-ldates[kc - 1]));
//@					}
//@				}
				//#endif
			}
			//#ifdef DLARGEMEM
//@			if ((ic & AND_VAL) == 0) {
//@				gauge.setValue(gcnt++);
//@			}
			//#endif
		}
		// Save memory by making null
		uitems = null;
		ufeeds = null;
		//#ifdef DLARGEMEM
		//#ifdef DMIDP20
//@		gauge.setValue(diffDelta + 1);
//@		gauge = new Gauge("Sorting...", false, Gauge.INDEFINITE,
//@				Gauge.CONTINUOUS_RUNNING);
//@		int gitem = m_loadForm.append(gauge);
		//#else
//@		m_loadForm.append("Sorting...\n");
		//#endif
		//#endif
		SortUtil.sortLongs(indexes, ldates, 0, kc - 1);
		//#ifdef DLARGEMEM
		//#ifdef DMIDP20
//@		m_loadForm.set(gitem, new StringItem(null, "Sorting finished."));
		//#endif
//@		gauge = new Gauge("After sorting 1...", false, diffDelta + 1, 0);
//@		m_loadForm.append(gauge);
//@		gcnt = 0;
		//#endif
		uitems = new RssItunesItem[kc];
		vunsorted.copyInto(uitems);
		// Save memory by making null
		vunsorted = null;
		ufeeds = new RssItunesFeed[kc];
		vfeedUnsorted.copyInto(ufeeds);
		// Save memory by making null
		vfeedUnsorted = null;
		for (int ic = 0; ic < kc ; ic++) {
			//#ifdef DLOGGING
//@			if (m_finestLoggable) {m_logger.finest("ic,index,date=" + ic + "," + indexes[ic] + "," + new Date(ldates[indexes[ic]]) + "," + uitems[indexes[ic]].getDate());}
			//#endif
			vsorted.insertElementAt(uitems[indexes[ic]], 0);
			vfeedSorted.insertElementAt(ufeeds[indexes[ic]], 0);
			//#ifdef DLARGEMEM
//@			if ((ic & AND_VAL) == 0) {
//@				gauge.setValue(gcnt++);
//@			}
			//#endif
		}
		// Save memory by making null
		uitems = null;
		//#ifdef DLARGEMEM
//@		gauge.setValue(diffDelta + 1);
//@		gauge = new Gauge("After sorting 2...", false, diffDelta + 1, 0);
//@		m_loadForm.append(gauge);
//@		gcnt = 0;
		//#endif
		RssItunesItem[] sitems = new RssItunesItem[vsorted.size()];
		vsorted.copyInto(sitems);
		// Save memory by making null
		vsorted = null;
		unsortedItems.removeAllElements();
		for( int ic = 0; ic < sitems.length; ic++){
			unsortedItems.addElement(sitems[ic]);
			//#ifdef DLARGEMEM
//@			if ((ic & AND_VAL) == 0) {
//@				gauge.setValue(gcnt++);
//@			}
			//#endif
		}
		// Save memory by making null
		ufeeds = null;
		//#ifdef DLARGEMEM
//@		gauge.setValue(diffDelta + 1);
//@		gauge = new Gauge("After sorting 3...", false, diffDelta + 1, 0);
//@		m_loadForm.append(gauge);
//@		gcnt = 0;
		//#endif
		RssItunesFeed[] sfeeds = new RssItunesFeed[vfeedSorted.size()];
		vfeedSorted.copyInto(sfeeds);
		// Save memory by making null
		vfeedSorted = null;
		m_itemFeeds.removeAllElements();
		for( int ic = 0; ic < sitems.length; ic++){
			m_itemFeeds.addElement(sfeeds[ic]);
			//#ifdef DLARGEMEM
//@			if ((ic & AND_VAL) == 0) {
//@				gauge.setValue(gcnt++);
//@			}
			//#endif
		}
		//#ifdef DLARGEMEM
//@		gauge.setValue(diffDelta + 1);
		//#endif
	}

	/* Reverse the order of the items to allow reverse (descending) feed sort. */
	private void reverseItems(final Vector unsortedItems) {
		final int ulen = unsortedItems.size();
		final int flen = m_itemFeeds.size();
		RssItunesItem[] items = new RssItunesItem[ulen];
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
//@		if (m_testNews && (m_newsIndex >= 0)) {
//@			((LogActIntr)this).outputCmdAct(c, s);
//@		}
		//#endif
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("commandAction c=" + c.getLabel());}
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
				if( super.size()>0){
					m_midlet.initializeLoadingForm("Loading item...", this);
					getUpdSel(true);
					m_midlet.initializeItemForm( m_feed, m_item, this );
					//#ifdef DTESTUI
//@					synchronized(this) {
//@						m_itemNext = true;
//@						m_newsIndex--;
//@						checkTest();
//@					}
					//#endif
				}
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
							m_directionCmd = new Command("Sort descending", Command.SCREEN, 9);
						} else {
							m_sortDesc = true;
							m_directionCmd = new Command("Sort ascending", Command.SCREEN, 9);
						}
						m_sort = true;
						super.addCommand(m_directionCmd);
					}
				}
			/** Get back to RSS feed bookmarks */
			} else if( c == m_backHeaderCmd ){
				//#ifdef DTESTUI
//@				synchronized(this) {
//@					m_testNews = false;
//@					m_newsIndex = -1;
//@					m_newsNext = false;
//@					m_itemNext = false;
//@				}
				//#endif
				super.getFeatureMgr().setBackground(false);
				m_midlet.replaceRef(this, null);
				m_midlet.showBookmarkList();

				//#ifdef DTESTUI
//@				/** Indicate that we want to test the headers/items.  */
//@			} else if( c == m_testNewsCmd) {
//@				synchronized(this) {
//@					if( super.size()>0 ) {
//@						m_itemNext = false;
//@						m_newsIndex = super.size() - 1;
//@						System.out.println("Test UI Test News items start m_newsIndex=" + m_newsIndex);
//@						m_newsNext = true;
//@						m_testNews = true;
//@					}
//@				}
				//#endif

			}

			//#ifdef DLOGGING
//@			if (m_finestLoggable) {m_logger.finest("commandAction m_sortDesc,m_sort,m_showUnread,m_sortByDate,m_showAll=" + m_sortDesc + "," + m_sort + "," + m_showUnread + "," + m_sortByDate + "," + m_showAll);}
			//#endif
			
		}catch(Throwable t) {
			m_loadForm.recordExcForm("Internal error", t);
		}
	}

}
