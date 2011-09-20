//--Need to modify--#preprocess
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
/*
 * IB 2010-05-24 1.11.5RC2 Test compatibility OpmlParser.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-29 1.11.5Dev9 Use compatibility4 version of EncodingUtil and EncodingStreamReader.
 * IB 2010-11-29 1.11.5Dev9 Allow test to get the EncodingUtilIntr interface.
 */

package com.substanceofcode.rssreader.businesslogic.compatibility4;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
import com.substanceofcode.utils.compatibility4.XmlParser;
import javax.microedition.io.*;
import java.util.*;
import java.io.*;
import com.substanceofcode.utils.compatibility4.EncodingUtil;

/**
 * OpmlParser is an utility class for aquiring and parsing a OPML lists.
 * HttpConnection is used to fetch OPML list and kXML is used on xml parsing.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class OpmlParser extends FeedListParser {
    
	// Future allow reading in OMPL which contain OMPL.

	private boolean opmlDirectory = false;

    /** Constructor with url, username and password parameters. */
    public OpmlParser(String url, String username, String password) {
        super(url, username, password);
    }
    
    /** Parse OPML list */
    public RssItunesFeedInfo[] parseFeeds(InputStream is) {
        /** Initialize item collection */
        Vector rssFeeds = new Vector();
        
        /** Initialize XML parser and parse OPML XML */
        XmlParser  parser = new XmlParser(is);
        try {
            
			// The first element is the main tag.
            int elementType = parser.parse();
			// If we found the prologue, get the next entry.
			if( elementType == XmlParser.PROLOGUE ) {
				elementType = parser.parse();
			}
			if (elementType == XmlParser.END_DOCUMENT ) {
				return null;
			}
            
			EncodingUtil encodingUtil = (EncodingUtil)parser.getEncodingUtil();
            do {
				/** RSS item properties */
				String title = "";
				String link = "";
												
				String tagName = parser.getName();
				System.out.println("tagname: " + tagName);
				if (tagName.equals("outline")) {
					System.out.println("Parsing <outline> tag");
					
					title = parser.getAttributeValue( "text" );
					if (title != null) {
						title = EncodingUtil.replaceAlphaEntities(title);
						// No need to convert from UTF-8 to Unicode using replace
						// umlauts now because it is done with new String...,encoding.

						// Replace numeric entities including &#8217;, &#8216;
						// &#8220;, and &#8221;
						title = EncodingUtil.replaceNumEntity(title);

						// Replace special chars like left quote, etc.
						// Since we have already converted to unicode, we want
						// to replace with uni chars.
						title = encodingUtil.replaceSpChars(title);
					}
					/** 
					 * Create new RSS item and add it do RSS document's item
					 * collection.  Account for wrong OPML which is an
					 * OPML composed of other OPML.  These have url attribute
					 * instead of link attribute.
					 */

					if (((link = parser.getAttributeValue( "xmlUrl" )) == null) &&
						opmlDirectory) {
						link = parser.getAttributeValue( "url" );
					}
					
					/** Debugging information */
					System.out.println("Title:       " + title);
					System.out.println("Link:        " + link);
					
					if(( link == null ) || ( link.length() == 0 )) {
						continue;
					}
					if (( m_feedNameFilter != null) &&
						((title != null) &&
						(title.toLowerCase().indexOf(m_feedNameFilter) < 0))) {
						continue;
					}
					if (( m_feedURLFilter != null) &&
						( link.toLowerCase().indexOf(m_feedURLFilter) < 0)) {
						continue;
					}
					RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
					rssFeeds.addElement( feed );
				}
				
			}
            while( parser.parse() != XmlParser.END_DOCUMENT );
            
        } catch (Exception ex) {
            System.err.println("OpmlParser.parseFeeds(): Exception " + ex.toString());
			ex.printStackTrace();
            return null;
        } catch (Throwable t) {
            System.err.println("OpmlParser.parseFeeds(): Exception " + t.toString());
			t.printStackTrace();
            return null;
        }
        
        /** Create array */
        RssItunesFeed[] feeds = new RssItunesFeed[ rssFeeds.size() ];
        rssFeeds.copyInto(feeds);
        return feeds;
    }
    
}

