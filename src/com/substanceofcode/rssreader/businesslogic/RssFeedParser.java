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

// Expand to define test define
//#define DTEST
// Expand to define logging define
//#define DLOGGING
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.XmlParser;
import javax.microedition.io.*;
import java.util.*;
import java.io.*;

import com.substanceofcode.utils.EncodingUtil;
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
public class RssFeedParser extends URLHandler {
    
    private RssFeed m_rssFeed;  // The RSS feed
    private boolean m_getTitleOnly = false;  // The RSS feed
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("RssFeedParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Create new instance of RssDocument */
    public RssFeedParser(RssFeed rssFeed) {
        m_rssFeed = rssFeed;
    }
    
    /** Return RSS feed */
    public RssFeed getRssFeed() {
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
        
		String url = m_rssFeed.getUrl();
		try {
			super.handleOpen(url, m_rssFeed.getUsername(),
					  m_rssFeed.getPassword());
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
				if (m_lastMod == 0L) {
					m_rssFeed.setUpddate(null);
				} else if (updFeed && (m_rssFeed.getUpddate() != null)) {
					if (m_rssFeed.getUpddate().equals(new Date(m_lastMod))) {
						return;
					}
 				}
				parseRssFeedXml( m_inputStream, maxItemCount );
				m_rssFeed.setUpddate(new Date(m_lastMod));
			}
        } catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("parseRssFeed error with " + url, e);
			//#endif
			if ((url != null) && (url.indexOf("file://") == 0)) {
				System.err.println("Cannot process file.");
			}
            throw new Exception("Error while parsing RSS data: " 
                    + e.toString());
        } catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("parseRssFeed error with " + url, t);
			//#endif
			if ((url != null) && (url.indexOf("file://") == 0)) {
				System.err.println("Cannot process file.");
			}
            throw new Exception("Error while parsing RSS data: " 
								+ t.toString());
        } finally {
			super.handleClose();
		}
    }
    
    protected void handleHeaderRedirect(String url)
	throws IOException, Exception {
	}

	/** Read HTML and if it has links, redirect and parse the XML. */
	private void parseHeaderRedirect(boolean updFeed, String url,
								     int maxItemCount)
    throws IOException, Exception {
		if (m_redirect) {
			//#ifdef DLOGGING
			logger.severe("Error 2nd header redirect url:  " + url);
			//#endif
			System.out.println("Error 2nd header redirecturl:  " + url);
			throw new IOException("Error 2nd header redirect.");
		}
		m_redirect = true;
		RssFeed svFeed = new RssFeed(m_rssFeed);
		m_rssFeed.setUrl(url);
		try {
			parseRssFeed(updFeed, maxItemCount);
		} finally {
			m_rssFeed.setUrl(svFeed.getUrl());
		}
		return;

	}

	/** Read HTML and if it has links, redirect and parse the XML. */
	private void parseHTMLRedirect(boolean updFeed, String url,
								   InputStream is, int maxItemCount)
    throws IOException, Exception {
		String newUrl = super.parseHTMLRedirect(url, is);
		RssFeed svFeed = new RssFeed(m_rssFeed);
		m_rssFeed.setUrl(newUrl);
		try {
			parseRssFeed(updFeed, maxItemCount);
		} finally {
			m_rssFeed.setUrl(svFeed.getUrl());
		}
	}

    /**
     * Nasty RSS feed XML parser.
     * Seems to work with all RSS 0.91, 0.92 and 2.0.
     */
    public void parseRssFeedXml(InputStream is, int maxItemCount)
    throws IOException {
        /** Initialize item collection */
        m_rssFeed.getItems().removeAllElements();
        
        /** Initialize XML parser and parse feed */
        XmlParser parser = new XmlParser(is);
		// Account for some Chinese (and other) rss.
        parser.setNamespace("dc");
        
        /** <?xml...*/
        int parsingResult = parser.parse();
		/** if prologue was found, parse after prologue.  **/
		if (parsingResult == XmlParser.PROLOGUE) {
			parser.parse();
		}
        
        FeedFormatParser formatParser = null;
        String entryElementName = parser.getName();
        if(entryElementName.equals("rss") || 
           entryElementName.equals("rdf")) {
            /** Feed is in RSS format */
            formatParser = new RssFormatParser();
            Vector items = formatParser.parse( parser, m_rssFeed,
					maxItemCount, m_getTitleOnly );
            m_rssFeed.setItems( items );
            
        } else if(entryElementName.equals("feed")) {
            /** Feed is in Atom format */
            formatParser = new AtomFormatParser();
            Vector items = formatParser.parse( parser, m_rssFeed,
					maxItemCount, m_getTitleOnly );
            m_rssFeed.setItems( items );
            
			// TODO handle HTML redirect
        } else {
			//#ifdef DLOGGING
			logger.severe("Unable to parse feed type:  " + entryElementName);
			//#endif
            /** Unknown feed */
            throw new IOException("Unable to parse feed. Feed format is not supported.");
            
        }
        
    }
    
    public void setGetTitleOnly(boolean m_getTitleOnly) {
        this.m_getTitleOnly = m_getTitleOnly;
    }

    public boolean isGetTitleOnly() {
        return (m_getTitleOnly);
    }

}
