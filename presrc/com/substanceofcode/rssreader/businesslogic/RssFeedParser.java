//--Need to modify--#preprocess
/*
 * RssFeedParser.java
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
 * IB 2010-03-07 1.11.4RC1 Combine classes to save space.
 * IB 2010-03-07 1.11.4RC1 Recognize style sheet, and DOCTYPE and treat properly.
 * IB 2010-03-14 1.11.5RC2 Fixed problem with conditional get.  Don't set updated and etag if the updated and etag match since the values are not retrieved if it matches.  Use string for updated date (last-modified).
 * IB 2010-04-30 1.11.5RC2 Track threads used.
 * IB 2010-05-26 1.11.5RC2 Give error if run out of data before finding anything.
 * IB 2010-05-28 1.11.5RC2 Use threads and CmdReceiver for MIDP 2.0 only.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser and HTMLLinkParser in small memory MIDP 1.0 to save space.
 * IB 2010-05-28 1.11.5RC2 Check for html, htm, shtml, and shtm suffixes.
 * IB 2010-05-29 1.11.5RC2 Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
 * IB 2010-05-29 1.11.5RC2 Better logging.
 * IB 2010-05-29 1.11.5RC2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern using nullPtr.
 * IB 2010-09-26 1.1.5Dev8 Don't use midlet directly.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-26 1.11.5Dev15 If we are modifying the feed, save the unread settings.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Save the pointer to the old feed and make a copy to modify so that it will allow future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Use procIoExc from URLHandler process exception handling for IO and other exceptions including out of memory.
 * IB 2011-01-14 1.11.5Alpha15 Use getRssMidlet from FeatureMgr to get the Rss Midlet.
 * IB 2011-01-22 1.11.5Dev16 If m_maxItemCount has not been set, take it from the RssReaderSettings either via midlet or directly from getInstance.
*/

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define CLDC define
@DCLDCVERS@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.XmlParser;
//#ifndef DSMALLMEM
import com.substanceofcode.utils.HTMLParser;
//#endif
import com.substanceofcode.rssreader.presentation.FeatureMgr;
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import javax.microedition.io.*;
import java.util.*;
import java.io.*;

import com.substanceofcode.utils.CauseException;
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observable;
import net.yinlight.j2me.observable.ObservableHandler;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * RssFeedParser is an utility class for aquiring and parsing a RSS feed.
 * HttpConnection is used to fetch RSS feed and kXML is used on xml parsing.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class RssFeedParser extends URLHandler
implements 
//#ifdef DMIDP20
			Observable,
//#endif
	Runnable {
    
	final       Object nullPtr = null;
    private Thread m_parsingThread = null;
    private RssItunesFeed m_rssFeed;  // The RSS feed
    private RssItunesFeed m_oldRssFeed;  // The original RSS feed
    private int m_maxItemCount;  // Max count of itetms to get for a feed.
    private boolean m_getTitleOnly = false;  // The RSS feed
    private boolean m_updFeed = false;  // Do updated feeds only.
    private boolean m_successfull = false;
    private Exception m_ex = null;
	//#ifdef DMIDP20
    private ObservableHandler observableHandler = null;
	//#endif
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("RssFeedParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Create new instance of RssFeedParser */
    public RssFeedParser(RssItunesFeed rssFeed, RssItunesFeed oldRssFeed,
						 boolean updFeed) {
		m_updFeed = updFeed;
		if (oldRssFeed == null) {
			m_oldRssFeed = rssFeed;
			m_rssFeed = (RssItunesFeed)rssFeed.clone();
		} else {
			m_oldRssFeed = oldRssFeed;
			m_rssFeed = (updFeed && (oldRssFeed != null)) ?
					(RssItunesFeed)oldRssFeed.clone() : rssFeed;
		}
		m_maxItemCount = -2;
    }
    
	//#ifdef DMIDP20
    /** Make this observable. */
    public void makeObserable(boolean updFeed, int maxItemCount) {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("makeObserable updFeed,maxItemCount=" + updFeed + "," + maxItemCount);}
		//#endif
		m_maxItemCount = maxItemCount;
		observableHandler = new ObservableHandler();
        m_parsingThread = MiscUtil.getThread(this, "RssFeedParser", this,
				"makeObserable");
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Thread created=" + MiscUtil.getThreadInfo(m_parsingThread));}
		//#endif
    }
	//#endif
    
    /** Return RSS feed */
    public RssItunesFeed getRssFeed() {
        return m_rssFeed;
    }
    
    /**
     * Send a GET request to web server and parse feeds from response.
     *
     * @input updFeed Do updated feeds only.
     * @input maxItemCount Maximum item count for the feed.
     *
     */
    public void parseRssFeed(boolean updFeed, int maxItemCount)
    throws IOException, Exception {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parseRssFeed updFeed,maxItemCount=" + updFeed + "," + maxItemCount);}
		//#endif
		// Set this here as the instance of this class is reused
		// for update of the current feed.
		m_redirects = 0;
		parseRssFeedUrl(m_rssFeed.getUrl(), updFeed, maxItemCount);
		m_successfull = true;
	}
        
    /**
     * Send a GET request to web server and parse feeds from response.
     * Preserve the read field if title/date and description are the same.
     *
     * @input updFeed Do updated feeds only.
     * @input maxItemCount Maximum item count for the feed.
     *
     */
    public void parseModRssFeed(boolean updFeed, int maxItemCount)
    throws IOException, Exception {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parseModRssFeed updFeed,maxItemCount=" + updFeed + "," + maxItemCount);}
		//#endif
		if (!m_updFeed && updFeed && (m_oldRssFeed != null)) {
			m_rssFeed = (RssItunesFeed)m_oldRssFeed.clone();
		}
		m_updFeed = updFeed;
		// Set this here as the instance of this class is reused
		// for update of the current feed.
		m_redirects = 0;
		parseRssFeedUrl(m_rssFeed.getUrl(), updFeed, maxItemCount);
		m_rssFeed.checkPresRead(updFeed, m_oldRssFeed);
		m_successfull = true;
	}

    /**
     * Send a GET request to web server and parse feeds from response.
     *
     * @input url to parse
     * @input updFeed Do updated feeds only.
     * @input maxItemCount Maximum item count for the feed.
     *
     */
    public void parseRssFeedUrl(String url, boolean updFeed, int maxItemCount)
    throws IOException, CauseException, Exception {
        
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parseRssFeedUrl url,maxItemCount=" + url + "," + maxItemCount);}
		//#endif
		try {
			super.handleOpen(url, m_rssFeed.getUsername(),
					  m_rssFeed.getPassword(), false, updFeed,
					  m_rssFeed.getUpddate(), m_rssFeed.getEtag(),
					  "Error while parsing RSS data ",
					  "Out of memory error while parsing RSS data ",
					  "Internal error while parsing RSS data ");
			if (m_needRedirect) {
				m_needRedirect = false;
				parseHeaderRedirect(updFeed, m_location, maxItemCount);
				return;
			}
			//#ifndef DSMALLMEM
			// If we find HTML, usually it is redirection
			if (HTMLParser.isHtml(m_contentType)) {
				parseHTMLRedirect(updFeed, url, m_inputStream,
								  maxItemCount);
			} else {
			//#endif
				// If we're only processing if the feed is updated,
				// check if we previously had a update value.
				// If so and it does equals the new one, return
				if (updFeed && m_same) {
					return;
				}
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("run m_rssFeed.getUpddate()=" + m_rssFeed.getUpddate());}
				//#endif
				m_rssFeed.setUpddate(m_lastMod);
				//#ifdef DLOGGING
				String etag = m_rssFeed.getEtag();
				//#endif
				m_rssFeed.setEtag(m_etag);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("run m_lastMod,etag,m_etag=" + m_lastMod + "," + etag + "," + m_etag);}
				//#endif
				parseRssFeedXml( m_inputStream, maxItemCount);
			//#ifndef DSMALLMEM
			}
			//#endif
        } catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("parseRssFeedUrl error with " + url, e);
			//#endif
			if ((url != null) && url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
            throw new CauseException("Error while parsing RSS data url: " + url,
					e);
        } catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("parseRssFeedUrl error with " + url, t);
			//#endif
			if ((url != null) && url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
            throw new Exception("Error while parsing RSS data: " 
								+ t.toString());
        } finally {
			super.handleClose();
		}
    }
    
	/** Read HTML and if it has links, redirect and parse the XML. */
	private void parseHeaderRedirect(boolean updFeed, String url,
								     int maxItemCount)
    throws IOException, Exception {
		if (++m_redirects >= MAX_REDIRECTS) {
            IOException ioe = new IOException("Error url " + m_redirectUrl +
					" to redirect url:  " + url);
			//#ifdef DLOGGING
			logger.severe(ioe.getMessage(), ioe);
			//#endif
			System.out.println(ioe.getMessage());
			throw ioe;
		}
		m_redirectUrl = url;
		parseRssFeedUrl(url, updFeed, maxItemCount);
	}

	//#ifndef DSMALLMEM
	/** Read HTML and if it has links, redirect and parse the XML. */
	private void parseHTMLRedirect(boolean updFeed, String url,
								   InputStream is, int maxItemCount)
    throws IOException, Exception {
		String newUrl = super.parseHTMLRedirect(url, is);
		parseRssFeedUrl(newUrl, updFeed, maxItemCount);
	}
	//#endif

    /**
     * Nasty RSS feed XML parser.
     * Seems to work with all RSS 0.91, 0.92 and 2.0.
     */
    public void parseRssFeedXml(InputStream is, int maxItemCount)
    throws IOException, CauseException {
        /** Initialize item collection */
        m_rssFeed.getItems().removeAllElements();
        
        /** Initialize XML parser and parse feed */
        XmlParser parser = new XmlParser(is);
        
        /** <?xml...*/
		/** If prologue, DOCTYPE, or STYLESHEET was found, parse after them.  **/
		/** If link followed by meta found, go to following XML.  **/

        int parsingResult = parser.parseXmlElement();
        
		if (parsingResult == XmlParser.END_DOCUMENT) {
            CauseException ce = new CauseException(
					"Unable to parse feed. Feed has no data:" + m_rssFeed.getName() + "," + m_rssFeed.getUrl());
			//#ifdef DLOGGING
			logger.severe(ce.getMessage(), ce);
			//#endif
            /** Unknown feed */
            throw ce;
            
        }
        FeedFormatParser formatParser = null;
        String entryElementName = parser.getName();
        if(entryElementName.equals("rss") || 
           entryElementName.equals("rdf")) {
            /** Feed is in RSS format */
            formatParser = new RssFormatParser();
            m_rssFeed = formatParser.parse( parser, m_rssFeed,
					maxItemCount, m_getTitleOnly );
            
        } else if(entryElementName.equals("feed")) {
            /** Feed is in Atom format */
            formatParser = new AtomFormatParser();
            m_rssFeed = formatParser.parse( parser, m_rssFeed,
					maxItemCount, m_getTitleOnly );
            
        } else {
            CauseException ce = new CauseException(
					"Unable to parse feed type:" + m_rssFeed.getName() + "," + m_rssFeed.getUrl() + "," + entryElementName);
			//#ifdef DLOGGING
			logger.severe(ce.getMessage(), ce);
			//#endif
            /** Unknown feed */
            throw ce;
        }
        
    }
    
	//#ifdef DMIDP20
	public ObservableHandler getObservableHandler() {
		return observableHandler;
	}
	//#endif

    /** Parsing thread */
    public void run() {
        try {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("Thread running=" + MiscUtil.getThreadInfo(m_parsingThread));}
			//#endif
			if (m_maxItemCount == -2) {
				RssReaderMIDlet midlet = FeatureMgr.getRssMidlet();
				m_maxItemCount = (midlet != null) ? midlet.getSettings().INIT_MAX_ITEM_COUNT : RssReaderSettings.getInstance().INIT_MAX_ITEM_COUNT;
			}
			parseModRssFeed(m_updFeed, m_maxItemCount);
        } catch( Throwable e ) {
			m_ex = URLHandler.procIoExc("Error while parsing feed ", e,
					false, m_rssFeed.getUrl(),
					"Out of memory error while parsing feed ",
					"Internal error while parsing feed ",
					"run()", null
					//#ifdef DLOGGING
					,logger
					//#endif
					);
        } finally {
			//#ifndef DSMALLMEM
			if (m_parsingThread != null) {
				MiscUtil.removeThread(m_parsingThread);
			}
			//#endif
			//#ifdef DMIDP20
			if (observableHandler != null) {
				observableHandler.notifyObservers(this);
			}
			//#endif
			if (FeatureMgr.getRssMidlet() != null) {
				FeatureMgr.getRssMidlet().wakeup(2);
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("run m_successfull=" + m_successfull);}
			//#endif
        }        
    }
    
    public Exception getEx() {
        return (m_ex);
    }

    public boolean isSuccessfull() {
        return (m_successfull);
    }

    public void setGetTitleOnly(boolean getTitleOnly) {
        this.m_getTitleOnly = getTitleOnly;
    }

    public boolean isGetTitleOnly() {
        return (m_getTitleOnly);
    }

    public Thread getParsingThread() {
        return (m_parsingThread);
    }

    public RssItunesFeed getOldRssFeed() {
        return (m_oldRssFeed);
    }

}
//#endif
