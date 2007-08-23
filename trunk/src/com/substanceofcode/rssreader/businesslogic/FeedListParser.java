/*
 * FeedListParser.java
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
//#define DNOTEST
// Expand to define logging define
//#define DNOLOGGING
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.utils.Base64;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Base class for feed list parsers.
 *
 * @author Tommi Laukkanen
 */
public abstract class FeedListParser implements Runnable{
    
    private Thread m_parsingThread;
    private String m_url;
    private String m_username;
    private String m_password;
	protected String feedNameFilter;
	protected String feedURLFilter;
    private boolean m_ready;
    private boolean m_successfull = false;
    private RssFeed[] m_feeds;
    
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("FeedListParser");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Creates a new instance of FeedListParser */
    public FeedListParser(String url, String username, String password) {
        m_parsingThread = new Thread(this);
        m_url = url;
        m_username = username;
        m_password = password;
    }
    
    /** Start parsing the feed list */
    public void startParsing() {
        m_ready = false;
        m_parsingThread.start();
    }
    
    /** Check whatever parsing is ready or not */
    public boolean isReady() {
        return m_ready;
    }
    
    /** Get feed list */
    public RssFeed[] getFeeds() {
        return m_feeds;
    }
    
    /** Parsing thread */
    public void run() {
        try {
            m_feeds = parseFeeds();
			m_successfull = true;
        } catch( Exception ex ) {
			//#ifdef DLOGGING
//@			logger.severe("FeedListParser.run(): Error while parsing " +
//@					      "feeds: " + m_url, ex);
			//#endif
            // TODO: Add exception handling
            System.err.println("FeedListParser.run(): Error while parsing feeds: " + ex.toString());
        } finally {
            m_ready = true;
        }        
    }  
    
    
    /** Get feeds from selected url */
    public RssFeed[] parseFeeds() throws IOException, Exception {
        
        HttpConnection hc = null;
		//#ifdef DTEST
//@        InputStream ris = null;
		//#endif
        DataInputStream dis = null;
        String response = "";
        try {
			//#ifdef DTEST
//@			// If testing, allow opening of files in the jar.
//@			if (m_url.indexOf("file://") == 0) {
//@				ris = this.getClass().getResourceAsStream( m_url.substring(7));
//@				if (ris == null) {
//@					new IOException("No file found:  " + m_url);
//@				}
//@				return parseFeeds(ris);
//@			}
			//#endif
            /**
             * Open an HttpConnection with the Web server
             * The default request method is GET
             */
            hc = (HttpConnection) Connector.open( m_url );
            hc.setRequestMethod(HttpConnection.GET);
            /** Some web servers requires these properties */
            hc.setRequestProperty("User-Agent", 
                    "Profile/MIDP-1.0 Configuration/CLDC-1.0");
            hc.setRequestProperty("Content-Length", "0");
            hc.setRequestProperty("Connection", "close");

            /** Add credentials if they are defined */
            if( m_username.length()>0) {
                /** 
                 * Add authentication header in HTTP request. Basic authentication
                 * should be formatted like this:
                 *     Authorization: Basic QWRtaW46Zm9vYmFy
                 */
                String userPass;
                Base64 b64 = new Base64();
                userPass = m_username + ":" + m_password;
                userPass = b64.encode(userPass.getBytes());
                hc.setRequestProperty("Authorization", "Basic " + userPass);
            }            
            
            /** 
             * Get a DataInputStream from the HttpConnection 
             * and return it to the caller
             */
            return parseFeeds(hc.openInputStream());
        } catch(Exception e) {
			//#ifdef DLOGGING
//@			logger.severe("parseFeeds error with " + m_url, e);
			//#endif
			if ((m_url != null) && (m_url.indexOf("file://") == 0)) {
				System.err.println("Cannot process file.");
			}
            throw new Exception("Error while parsing RSS data: " 
                    + e.toString());
        } catch(Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("parseFeeds error with " + m_url, t);
			//#endif
			if ((m_url != null) && (m_url.indexOf("file://") == 0)) {
				System.err.println("Cannot process file.");
			}
            throw new Exception("Error while parsing RSS data: " 
								+ t.toString());
        } finally {
            if (hc != null) hc.close();
			//#ifdef DTEST
//@            if (ris != null) ris.close();
			//#endif
            if (dis != null) dis.close();
        }
    }    
    
    abstract RssFeed[] parseFeeds(InputStream is);
    
    public void setFeedNameFilter(String feedNameFilter) {
        this.feedNameFilter = feedNameFilter.toLowerCase();
    }

    public String getFeedNameFilter() {
        return (feedNameFilter);
    }

    public void setFeedURLFilter(String feedURLFilter) {
        this.feedURLFilter = feedURLFilter.toLowerCase();
    }

    public String getFeedURLFilter() {
        return (feedURLFilter);
    }

    public boolean isSuccessfull() {
        return (m_successfull);
    }

}
