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

package com.substanceofcode.rssreader;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

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
    private boolean m_ready;
    private RssFeed[] m_feeds;
    
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
        } catch( Exception ex ) {
            // TODO: Add exception handling
            System.err.println("FeedListParser.run(): Error while parsing feeds: " + ex.toString());
        } finally {
            m_ready = true;
        }        
    }  
    
    
    /** Get feeds from selected url */
    public RssFeed[] parseFeeds() throws IOException, Exception {
        
        HttpConnection hc = null;
        DataInputStream dis = null;
        String response = "";
        try {
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
            throw new Exception("Error while parsing RSS data: " 
                    + e.toString());
        } finally {
            if (hc != null) hc.close();
            if (dis != null) dis.close();
        }
    }    
    
    abstract RssFeed[] parseFeeds(InputStream is);
    
}
