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
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.utils.Base64;
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
public class RssFeedParser {
    
    private RssFeed m_rssFeed;  // The RSS feed
    private boolean m_redirect = false;  // The RSS feed
	//#ifdef DTEST
    private long m_lastMod;  // Last modification used for testing.
	//#endif
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
        
        HttpConnection hc = null;
        InputStream is = null;
        String response = "";
		long lastMod = 0L;
        try {
			String contentType = null;
			String url = m_rssFeed.getUrl();
			//#ifdef DTEST
			if (url.indexOf("file://") == 0) {
				is = this.getClass().getResourceAsStream( url.substring(7));
				if (is == null) {
					throw new IOException("Cannot read jar file " + url);
				}
				int dotPos = url.lastIndexOf('.');
				if (dotPos >= 0) {
					contentType = url.substring(dotPos + 1);
				}
				lastMod = m_lastMod;
			} else {
			//#endif
				/**
				 * Open an HttpConnection with the Web server
				 * The default request method is GET
				 */
				hc = (HttpConnection) Connector.open( url );
				hc.setRequestMethod(HttpConnection.GET);
				/** Some web servers requires these properties */
				//hc.setRequestProperty("User-Agent",
				//        "Profile/MIDP-1.0 Configuration/CLDC-1.0");
				hc.setRequestProperty("Content-Length", "0");
				hc.setRequestProperty("Connection", "close");
				
				/** Add credentials if they are defined */
				if( m_rssFeed.getUsername().length()>0) {
					/**
					 * Add authentication header in HTTP request. Basic authentication
					 * should be formatted like this:
					 *     Authorization: Basic QWRtaW46Zm9vYmFy
					 */
					String username = m_rssFeed.getUsername();
					String password = m_rssFeed.getPassword();
					String userPass;
					Base64 b64 = new Base64();
					userPass = username + ":" + password;
					userPass = b64.encode(userPass.getBytes());
					hc.setRequestProperty("Authorization", "Basic " + userPass);
				}
				
				/**
				 * Get a DataInputStream from the HttpConnection
				 * and forward it to XML parser
				 */
				
				/*
				// DEBUG_START
				// Prepare buffer for input data
				InputStream is = hc.openInputStream();
				StringBuffer inputBuffer = new StringBuffer();
				 
				// Read all data to buffer
				int inputCharacter;
				try {
					while ((inputCharacter = is.read()) != -1) {
						inputBuffer.append((char)inputCharacter);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				 
				// Split buffer string by each new line
				String text = inputBuffer.toString();
				System.out.println("Input: " + text);
				 
				// DEBUG_END
				*/
							
				is = hc.openInputStream();
				lastMod = hc.getLastModified();
				contentType = hc.getHeaderField("content-type");
			//#ifdef DTEST
			}
			//#endif

			// If we find HTML, ussume it is redirection
			if ((contentType != null) && (contentType.indexOf("html") >= 0)) {
				if (m_redirect) {
					//#ifdef DLOGGING
					logger.severe("Error 2nd redirect url:  " + url);
					//#endif
					System.out.println("Error 2nd redirecturl:  " + url);
					throw new IOException("Error 2nd redirect.");
				}
				m_redirect = true;
				RssFeed[] feeds =
						HTMLLinkParser.parseFeeds(new EncodingUtil(is),
											null,
											null
											//#ifdef DLOGGING
											,logger,
											fineLoggable,
											finerLoggable,
											finestLoggable
											//#endif
											);
				if (feeds.length == 0) {
					//#ifdef DLOGGING
					logger.severe("Parsing HTML redirect cannot be " +
								  "processed.");
					//#endif
					System.out.println(
							"Parsing HTML redirect cannot be " +
							"processed.");
					throw new IOException("Parsing HTML redirect cannot be " +
										  "processed.");
				}
				RssFeed svFeed = new RssFeed(m_rssFeed);
				m_rssFeed.setUrl(feeds[0].getUrl());
				try {
					parseRssFeed(updFeed, maxItemCount);
				} catch (Exception e) {
					svFeed.copyTo(m_rssFeed);
					throw e;
				}
				return;

			}

			if (lastMod == 0L) {
				m_rssFeed.setUpddate(null);
			} else if (updFeed && (m_rssFeed.getUpddate() != null)) {
				if (m_rssFeed.getUpddate().equals(new Date(lastMod))) {
					return;
				}
			}
            parseRssFeedXml( is, maxItemCount );
			m_rssFeed.setUpddate(new Date(lastMod));
        } catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("Error ", e);
			//#endif
			System.out.println("error " + e.getMessage());
			e.printStackTrace();
            throw new Exception("Error while parsing feed: "
                    + e.toString());
        } finally {
            if (hc != null) hc.close();
			//#ifdef DTEST
            try {
				if (is != null) is.close();
			} catch (IOException e) { }

			//#endif
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
					maxItemCount );
            m_rssFeed.setItems( items );
            
        } else if(entryElementName.equals("feed")) {
            /** Feed is in Atom format */
            formatParser = new AtomFormatParser();
            Vector items = formatParser.parse( parser, m_rssFeed,
					maxItemCount );
            m_rssFeed.setItems( items );
            
        } else {
            /** Unknown feed */
            throw new IOException("Unable to parse feed. Feed format is not supported.");
            
        }
        
    }
    
	//#ifdef DTEST
    public void setLastMod(long m_lastMod) {
        this.m_lastMod = m_lastMod;
    }

    public long getLastMod() {
        return (m_lastMod);
    }
	//#endif

}
