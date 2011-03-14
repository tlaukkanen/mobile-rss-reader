/*
 * RssFeedStore.java
 *
 * Copyright (C) 2009-2010 Irving Bunton Jr
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
 * IB 2011-01-24 1.11.5Dev16 Don't compile unneeded code for internet link version.
 * IB 2011-02-02 1.11.5Dev17 Change items to array to save on memory and for simplicity.
 * IB 2011-03-06 1.11.5Dev17 More logging.
 * IB 2011-03-13 1.11.5Dev17 Save old feeds and feed names (from URL) from RssFeedStore to allow future background updates.
 * IB 2011-03-13 1.11.5Dev17 Allow access to current field names from RssFeedStore to allow future background updates.
 * IB 2011-03-13 1.11.5Dev17 Allow clearing of RssFeedStore background info.
 */

// Expand to define MIDP define
//#define DMIDP20
// Expand to define itunes define
//#define DFULLVERS
// Expand to define test define
//#define DNOTEST
// Expand to define logging define
//#define DNOLOGGING
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businessentities;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * RssFeedStore class stores feeds and manages different ways to store to save
 * memory.
 *
 * @author Irving Bunton
 */
final public class RssFeedStore extends Hashtable {
    
	RssFeedStore m_oldRssFeeds = null;
	RssFeedStore m_oldRssNames = null;
	Vector m_feedNames = null;

	//#ifdef DLOGGING
//@    private Logger m_logger = Logger.getLogger("RssFeedStore");
//@    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
//@    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
//@    private boolean m_traceLoggable = m_logger.isLoggable(Level.TRACE);
	//#endif
    
    /** Creates a new instance of RssFeedStore */
    public RssFeedStore() {
		super();
	}

    /** Creates a new instance of RssFeedStore */
    public RssFeedStore(int initialCapacity){
		super(initialCapacity);
    }

  /**
   * Put feed into store and use compact to save memory or full class
   *
   * @param name - Name of feed to put into store
   * @param feed - Feed to put into store
   * @author Irv Bunton
   */
	public RssFeed put(String name, RssFeed feed, RssFeed oldfeed) {
		synchronized(this) {
			//#ifdef DLOGGING
//@			if (m_traceLoggable) {m_logger.trace("put name,feed.getItems().length=" + name + "," + ((feed == null) ? "null" : (feed.getItems().length + "")));}
			//#endif
			RssFeed prevFeed = (RssFeed)super.get(name);
			if ((oldfeed == null) || (prevFeed == (Object)oldfeed)) {
				super.put(name, feed);
				if ((oldfeed != null) && !oldfeed.getName().equals(name)) {
					remove(oldfeed);
				}
				return prevFeed;
			} else {
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest(
//@						"put old item does not match prevFeed,oldfeed=" +
//@						prevFeed.hashCode() + "," + oldfeed.hashCode());}
				//#endif
				return null;
			}
		}
	}

  /**
   * Put feed into store.
   *
   * @param feed - Feed to put into store
   * @author Irv Bunton
   */
	public RssItunesFeed put(RssItunesFeed feed) {
		synchronized(this) {
			//#ifdef DLOGGING
//@			if (m_traceLoggable) {m_logger.trace("put feed.getName(),feed.getItems().length=" + feed.getName() + "," + feed.getItems().length);}
			//#endif
			return (RssItunesFeed)super.put(feed.getName(), feed);
		}
	}

  /**
   * Put feed into store and use compact to save memory or full class
   *
   * @param name - Name of feed to put into store
   * @param feed - Feed to put into store
   * @author Irv Bunton
   */
	public String put(String link, String name, String oname) {
		synchronized(this) {
			//#ifdef DLOGGING
//@			if (m_traceLoggable) {m_logger.trace("put link,name,oname=" + link + "," + name + "," + oname);}
			//#endif
			String prevName = (String)super.get(link);
			if ((oname == null) || (prevName == oname)) {
				super.put(link, name);
				return prevName;
			} else {
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest(
//@						"put old name does not match prevName,oname=" +
//@						prevName + "," + oname);}
				//#endif
				return null;
			}
		}
	}

  /**
   * Put feed into store and use compact to save memory or full class
   *
   * @param feed - Feed to put into store
   * @author Irv Bunton
   */
	public Object put(RssFeed feed, RssFeed oldfeed) {
		return put(feed.getName(), feed, oldfeed);
	}

	public Object put(Object key) {
		throw new Error("Invalid method for this store.");
	}

  /**
   * Get update version of feed name from url.  If this returned
   * feed is modified, it will change what is stored.
   *
   * @param url - URL link
   * @return    String - feed name for url
   * @author Irv Bunton
   */
	public String getFeedName(final String url) {
		synchronized(this) {
			return (String)super.get(url);
		}
	}

  /**
   * Get update version of RssItunesFeed from feed name.  If this returned
   * feed is modified, it will change what is stored.
   *
   * @param name - feed name
   * @return    RssItunesFeed - feed for feed name
   * @author Irv Bunton
   */
	public RssItunesFeed get(final String name) {
		synchronized(this) {
			return (RssItunesFeed)super.get(name);
		}
	}

  /**
   * Get update version of RssItunesFeed from feed name.  If this returned
   * feed is modified, it will change what is stored.
   *
   * @param name - feed name
   * @return    RssItunesFeed - feed for feed name
   * @author Irv Bunton
   */
	public RssItunesFeed getOld(final String name) {
		synchronized(this) {
			return (RssItunesFeed)super.get(name);
		}
	}

  /**
   * Get update version of RssItunesFeed from feed name in feed.  If this
   * returned feed is modified, it will change what is stored.
   *
   * @param name - feed name
   * @return    RssItunesFeed - feed for feed name
   * @author Irv Bunton
   */
	public RssItunesFeed get(final RssItunesFeed feed) {
		return (RssItunesFeed)super.get(feed.getName());
	}

	/* Get copy of the RssFeedStore and Hashtable linking feed links to field */
	/* names. */
	public Object[] copyFeeds(String[] feedNames) {
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("copyFeeds super.size=" + super.size());}
		//#endif
		synchronized(this) {
			RssFeedStore oldRssFeeds;
			RssFeedStore oldRssNames;
			oldRssFeeds = new RssFeedStore();
			oldRssNames = new RssFeedStore();
			boolean useEnum;
			int len;
			Vector vfeedNames = new Vector();
			Enumeration keyEnum;
			if (useEnum = (feedNames == null)) {
				len = super.size();
				keyEnum = super.keys();
			} else {
				len = feedNames.length;
				keyEnum = null;
			}
			for (int i = 0; (i < len) || (useEnum && keyEnum.hasMoreElements()); i++) {
				String fname;
				if (useEnum) {
					vfeedNames.addElement(fname =
							(String)keyEnum.nextElement());
				} else {
					vfeedNames.addElement(fname = feedNames[i]); 
				}
				RssItunesFeed oldfeed = get(fname);
				oldRssFeeds.put(fname, oldfeed, null);
				oldRssNames.put(oldfeed.getUrl(), fname, null);
			}
			//#ifdef DLOGGING
//@			if (m_traceLoggable) {m_logger.trace("copyFeeds super.size=" + super.size());}
			//#endif
			m_oldRssFeeds = oldRssFeeds;
			m_oldRssNames = oldRssNames;
			m_feedNames = vfeedNames;
			return new Object[] {oldRssFeeds, oldRssNames, vfeedNames};
		}
	}

	/* Free memory by getting rid of items. */
	public void freeFeedItems() {
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("freeFeedItems super.size=" + super.size());}
		//#endif
		synchronized(this) {
			Enumeration keyEnum = super.keys();
			while(keyEnum.hasMoreElements()) {
				final String fname = (String)keyEnum.nextElement();
				RssItunesFeed oldfeed = (RssItunesFeed)get(fname);
				RssItunesFeed newfeed = (RssItunesFeed)oldfeed.clone();
				//#ifdef DTEST
//@				oldfeed.setItems((RssItemInfo[])null);
//@				oldfeed.setItems(new RssItemInfo[0]);
				//#else
				oldfeed.setItems((RssItem[])null);
				oldfeed.setItems(new RssItem[0]);
				//#endif
				put(fname, newfeed, oldfeed);
			}
		}
		//#ifdef DLOGGING
//@		m_logger.severe("freeFeedItems finished");
		//#endif
	}

	public void clearFeedInfo() {
		m_oldRssFeeds = null;
		m_oldRssNames = null;
		m_feedNames = null;
	}

}
//#endif
