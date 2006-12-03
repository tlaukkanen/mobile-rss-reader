/*
 * OpmlParser.java
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

import javax.microedition.io.*;
import java.util.*;
import java.io.*;
import org.xmlpull.v1.*;
import org.kxml2.io.*;

/**
 * OpmlParser is an utility class for aquiring and parsing a OPML lists.
 * HttpConnection is used to fetch OPML list and kXML is used on xml parsing.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class OpmlParser extends FeedListParser {
    
    private XmlPullParser m_xmlParser = new KXmlParser(); // The Xml parser
    
    /** Constructor with url, username and password parameters. */
    public OpmlParser(String url, String username, String password) {
        super(url, username, password);
    }
    
    /** Parse OPML list */
    public RssFeed[] parseFeeds(InputStream is) {
        /** Initialize item collection */
        Vector rssFeeds = new Vector();
        
        /** Initialize XML parser and parse OPML XML */
        KXmlParser  parser = new KXmlParser();
        InputStreamReader reader = new InputStreamReader(is);
        try {
            
            parser.setInput( new InputStreamReader(is) );
            
            do {
                /** RSS item properties */
                String title = "";
                String link = "";
                
                // Get next event from parser
                parser.next();
                
                System.out.println("Parser.next()");
                
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.equals("outline")) {
                        System.out.println("Parsing <outline> tag");
                        
                        title = parser.getAttributeValue(null, "text");
                        link = parser.getAttributeValue(null, "xmlUrl");
                        
                        /** Debugging information */
                        System.out.println("Title:       " + title);
                        System.out.println("Link:        " + link);
                        
                        /** Create new RSS item and add it do RSS document's item
                         *  collection
                         */
                        RssFeed feed = new RssFeed(title, link, "", "");
                        rssFeeds.addElement( feed );
                    }
                }
            }while (parser.getEventType() != XmlPullParser.END_DOCUMENT);
            
        } catch (XmlPullParserException ex) {
            System.err.println("OpmlParser.parseFeeds(): XmlPullParserException " + ex.toString());
            return null;
        } catch (Exception ex) {
            System.err.println("OpmlParser.parseFeeds(): Exception " + ex.toString());
            return null;
        }
        
        /** Create array */
        RssFeed[] feeds = new RssFeed[ rssFeeds.size() ];
        for(int feedIndex=0; feedIndex<rssFeeds.size(); feedIndex++) {
            feeds[ feedIndex ] = (RssFeed)rssFeeds.elementAt(feedIndex);
        }
        return feeds;
    }
    
}

