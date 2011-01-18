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

// Expand to define MIDP define
//#define DMIDP20
// Expand to define logging define
//#define DNOLOGGING
// Expand to define itunes define
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
//@			if (m_traceLoggable) {m_logger.trace("put name,feed.getItems().size=" + name + "," + ((feed == null) ? "null" : (feed.getItems().size() + "")));}
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
   * Get update version of RssItunesFeed from feed name.  If this returned
   * feed is modified, it will change what is stored.
   *
   * @param name - feed name
   * @return    RssItunesFeed - feed for feed name
   * @author Irv Bunton
   */
	public RssItunesFeed get(final String name) {
		return (RssItunesFeed)super.get(name);
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
	public RssFeedStore[] copyFeeds() {
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("copyFeeds super.size=" + super.size());}
		//#endif
		RssFeedStore oldRssFeeds;
		RssFeedStore oldRssNames;
		synchronized(this) {
			oldRssFeeds = new RssFeedStore();
			oldRssNames = new RssFeedStore();
			Enumeration keyEnum = super.keys();
			while(keyEnum.hasMoreElements()) {
				final String fname = (String)keyEnum.nextElement();
				RssItunesFeed oldfeed = get(fname);
				oldRssFeeds.put(fname, oldfeed, null);
				oldRssNames.put(oldfeed.getUrl(), fname, null);
			}
		}
		//#ifdef DLOGGING
//@		if (m_traceLoggable) {m_logger.trace("copyFeeds super.size=" + super.size());}
		//#endif
		return new RssFeedStore[] {oldRssFeeds, oldRssNames};
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
				oldfeed.setItems((Vector)null);
				oldfeed.setItems(new Vector());
				put(fname, newfeed, oldfeed);
			}
		}
		//#ifdef DLOGGING
//@		m_logger.severe("freeFeedItems finished");
		//#endif
	}

}
