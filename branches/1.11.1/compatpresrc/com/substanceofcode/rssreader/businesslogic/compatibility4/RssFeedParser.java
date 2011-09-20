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
 * IB 2010-03-14 1.11.5RC2 Fixed problem with conditional get.  Don't set updated and etag if the updated and etag match since the values are not retrieved if it matches.  Use string for updated date (last-modified).
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-05-30 1.11.5RC2 Use compatibility URLHandler.
 * IB 2010-06-29 1.11.5RC2 Use compatibility observer pattern.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-29 1.11.5Dev9 Use compatibility4 version of EncodingUtil and EncodingStreamReader.
 * IB 2010-11-29 1.11.5Dev9 Use compatibility4 version of CauseException and EncodingStreamReader.
 * IB 2011-01-22 1.11.5Dev16 If m_maxItemCount has not been set, take it from the RssReaderSettings either via midlet or directly from getInstance.
 * IB 2011-03-08 1.11.5Dev17 Switch to yinlight Observable/ObservableHandler to prepare to change the license.
 * IB 2011-03-09 1.11.5Dev17 More logging.
 * IB 2011-03-28 1.11.5Dev18 Put errors for RssReaderSettings.getInstance into a vector.
*/

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define CLDC define
@DCLDCVERS@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businesslogic.compatibility4;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.compatibility4.XmlParser;
import com.substanceofcode.rssreader.businesslogic.compatibility4.URLHandler;
import com.substanceofcode.utils.SgmlParserIntr;
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.FeatureMgr;
import javax.microedition.io.*;
import java.util.*;
import java.io.*;

import com.substanceofcode.utils.compatibility4.EncodingUtil;
import com.substanceofcode.rssreader.businesslogic.SgmlFormatParser;
import com.substanceofcode.utils.compatibility4.CauseException;
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
    
    private RssReaderMIDlet m_midlet = null;
    private Thread m_parsingThread = null;
    private RssItunesFeedInfo m_rssFeed;  // The RSS feed
    private int m_maxItemCount;  // Max count of itetms to get for a feed.
    private boolean m_getTitleOnly = false;  // The RSS feed
    private boolean m_updFeed = false;  // Do updated feeds only.
    private boolean m_successfull = false;
    private CauseException m_ex = null;
	//#ifdef DMIDP20
    private ObservableHandler observableHandler = null;
	//#endif
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.RssFeedParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Create new instance of RssFeedParser */
    public RssFeedParser(RssItunesFeedInfo rssFeed) {
        m_rssFeed = rssFeed;
		m_updFeed = true;
		m_maxItemCount = -2;
    }
    
	//#ifdef DMIDP20
    /** Make this observable. */
    public void makeObserable(RssReaderMIDlet midlet,
			boolean updFeed, int maxItemCount) {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("makeObserable midlet,updFeed,maxItemCount=" + midlet + "," + updFeed + "," + maxItemCount);}
		//#endif
		m_midlet = midlet;
		m_updFeed = updFeed;
		m_maxItemCount = maxItemCount;
		observableHandler = new ObservableHandler();
		//#ifdef DCLDCV11
        m_parsingThread = new Thread(this, "RssFeedParser");
		//#else
        m_parsingThread = new Thread(this);
		//#endif
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Thread created=" + m_parsingThread);}
		//#endif
    }
	//#endif
    
    /** Return RSS feed */
    public RssItunesFeedInfo getRssFeed() {
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
     *
     * @input url to parse
     * @input updFeed Do updated feeds only.
     * @input maxItemCount Maximum item count for the feed.
     *
     */
    public void parseRssFeedUrl(String url, boolean updFeed, int maxItemCount)
    throws IOException, Exception {
        
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parseRssFeedUrl url,maxItemCount=" + url + "," + maxItemCount);}
		//#endif
		try {
			super.handleOpen(url, m_rssFeed.getUsername(),
					  m_rssFeed.getPassword(), false, updFeed,
					  m_rssFeed.getUpddate(), m_rssFeed.getEtag());
			if (m_needRedirect) {
				m_needRedirect = false;
				parseHeaderRedirect(updFeed, m_location, maxItemCount);
				return;
			}
			// If we find HTML, usually it is redirection
			if ((m_contentType != null) && (m_contentType.indexOf("html") >= 0)) {
				parseHTMLRedirect(updFeed, url, m_inputStream,
								  maxItemCount);
			} else {
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
			}
        } catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("parseRssFeedUrl error with " + url, e);
			//#endif
			if ((url != null) && url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
            throw new Exception("Error while parsing RSS data: " 
                    + e.toString());
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
			//#ifdef DLOGGING
			logger.severe("Error redirect url:  " + url);
			//#endif
			System.out.println("Error redirect url:  " + url);
			throw new IOException("Error url " + m_redirectUrl +
					" to redirect url:  " + url);
		}
		m_redirectUrl = url;
		parseRssFeedUrl(url, updFeed, maxItemCount);
	}

	/** Read HTML and if it has links, redirect and parse the XML. */
	private void parseHTMLRedirect(boolean updFeed, String url,
								   InputStream is, int maxItemCount)
    throws IOException, Exception {
		String newUrl = super.parseHTMLRedirect(url, is);
		parseRssFeedUrl(newUrl, updFeed, maxItemCount);
	}

    /**
     * Nasty RSS feed XML parser.
     * Seems to work with all RSS 0.91, 0.92 and 2.0.
     */
    public void parseRssFeedXml(InputStream is, int maxItemCount)
    throws IOException {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parseRssFeedXml is,m_rssFeed.getName(),m_rssFeed.getUrl(),m_maxItemCount,m_getTitleOnly=" + is + "," + m_rssFeed.getName() + "," + m_rssFeed.getUrl() + "," + + m_maxItemCount + "," + m_getTitleOnly);}
		//#endif
        /** Initialize item collection */
        m_rssFeed.getVecItems().removeAllElements();
        
        /** Initialize XML parser and parse feed */
        SgmlParserIntr parser = new XmlParser(is);
        
        /** <?xml...*/
        int parsingResult = parser.parse();
		/** if prologue was found, parse after prologue.  **/
		if (parsingResult == SgmlParserIntr.PROLOGUE) {
			parser.parse();
		}
        
        SgmlFormatParser formatParser = null;
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
			//#ifdef DLOGGING
			logger.severe("Unable to parse feed type:  " + entryElementName);
			//#endif
            /** Unknown feed */
            throw new IOException("Unable to parse feed. Feed format is not supported.");
            
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
			if (fineLoggable) {logger.fine("Thread running=" + this);}
			//#endif
			if (m_maxItemCount == -2) {
				RssReaderMIDlet midlet = FeatureMgr.getRssMidlet();
				Object[] parms = new Object[] {null};
				m_maxItemCount = (midlet != null) ? midlet.getSettings().INIT_MAX_ITEM_COUNT : RssReaderSettings.getInstance(parms).INIT_MAX_ITEM_COUNT;
			}
			parseRssFeed(m_updFeed, m_maxItemCount);
        } catch( IOException ex ) {
			//#ifdef DLOGGING
			logger.severe("RssFeedParser.run(): Error while parsing " +
					      "feeds: " + m_rssFeed.getUrl(), ex);
			//#endif
            // TODO: Add exception handling
            System.err.println("RssFeedParser.run(): Error while parsing feeds: " + ex.toString());
			m_ex = new CauseException("Error while parsing feed " + m_rssFeed.getUrl(), ex);
        } catch( Exception ex ) {
			//#ifdef DLOGGING
			logger.severe("RssFeedParser.run(): Error while parsing " +
					      "feeds: " + m_rssFeed.getUrl(), ex);
			//#endif
            // TODO: Add exception handling
            System.err.println("RssFeedParser.run(): Error while parsing feeds: " + ex.toString());
			m_ex = new CauseException("Error while parsing feed " + m_rssFeed.getUrl(), ex);
        } catch( OutOfMemoryError t ) {
			System.gc();
			// Save memory by releasing it.
			m_rssFeed = null;
			//#ifdef DLOGGING
			logger.severe("RssFeedParser.run(): Out Of Memory Error while " +
					"parsing feeds: " + m_rssFeed.getUrl(), t);
			//#endif
            // TODO: Add exception handling
            System.err.println("RssFeedParser.run(): " +
					"Out Of Memory Error while parsing feeds: " + t.toString());
			m_ex = new CauseException("Out Of Memory Error while parsing " +
					"feed " + m_rssFeed.getUrl(), t);
        } catch( Throwable t ) {
			//#ifdef DLOGGING
			logger.severe("RssFeedParser.run(): Error while parsing " +
					      "feeds: " + m_rssFeed.getUrl(), t);
			//#endif
            // TODO: Add exception handling
            System.err.println("RssFeedParser.run(): Error while parsing feeds: " + t.toString());
			m_ex = new CauseException("Internal error while parsing feed " +
									  m_rssFeed.getUrl(), t);
        } finally {
			if (m_midlet != null) {
				m_midlet.wakeup(2);
			}
			//#ifdef DMIDP20
			if (observableHandler != null) {
				observableHandler.notifyObservers(this);
			}
			//#endif
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("run m_successfull=" + m_successfull);}
			//#endif
        }        
    }
    
    public CauseException getEx() {
        return (m_ex);
    }

    public boolean isSuccessfull() {
        return (m_successfull);
    }

    public void setGetTitleOnly(boolean m_getTitleOnly) {
        this.m_getTitleOnly = m_getTitleOnly;
    }

    public boolean isGetTitleOnly() {
        return (m_getTitleOnly);
    }

    public Thread getParsingThread() {
        return (m_parsingThread);
    }

}
//#endif
