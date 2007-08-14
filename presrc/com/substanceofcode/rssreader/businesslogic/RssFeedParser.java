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
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.utils.Base64;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.XmlParser;
import javax.microedition.io.*;
import java.util.*;
import java.io.*;

/**
 * RssFeedParser is an utility class for aquiring and parsing a RSS feed.
 * HttpConnection is used to fetch RSS feed and kXML is used on xml parsing.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class RssFeedParser {
    
    private RssFeed m_rssFeed;  // The RSS feed
    
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
     * @input maxItemCount Maximum item count for the feed.
     *
     */
    public void parseRssFeed(int maxItemCount)
    throws IOException, Exception {
        
        HttpConnection hc = null;
		//#ifdef DTEST
        InputStream ris = null;
		//#endif
        DataInputStream dis = null;
        String response = "";
        try {
			//#ifdef DTEST
			if (m_rssFeed.getUrl().indexOf("file://") == 0) {
				parseRssFeedXml(
						this.getClass().getResourceAsStream(
						 m_rssFeed.getUrl().substring(7)), maxItemCount );
				return;
			}
			//#endif
            /**
             * Open an HttpConnection with the Web server
             * The default request method is GET
             */
            hc = (HttpConnection) Connector.open( m_rssFeed.getUrl() );
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
                        
            parseRssFeedXml( hc.openInputStream(), maxItemCount );
        } catch(Exception e) {
			System.out.println("error " + e.getMessage());
            throw new Exception("Error while parsing feed: "
                    + e.toString());
        } finally {
            if (hc != null) hc.close();
			//#ifdef DTEST
            if (ris != null) ris.close();
			//#endif
            if (dis != null) dis.close();
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
            Vector items = formatParser.parse( parser, maxItemCount );
            m_rssFeed.setItems( items );
            
        } else if(entryElementName.equals("feed")) {
            /** Feed is in Atom format */
            formatParser = new AtomFormatParser();
            Vector items = formatParser.parse( parser, maxItemCount );
            m_rssFeed.setItems( items );
            
        } else {
            /** Unknown feed */
            throw new IOException("Unable to parse feed. Feed format is not supported.");
            
        }
        
    }
    
}
