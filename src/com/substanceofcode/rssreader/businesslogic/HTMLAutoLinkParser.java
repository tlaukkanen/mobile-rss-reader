/*
 * HTMLAutoLinkParser.java
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

// Expand to define logging define
//#define DNOLOGGING

package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.utils.HTMLParser;
import com.substanceofcode.utils.XmlParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.StringUtil;
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * HTMLAutoLinkParser class is used when we are parsing RSS feed list 
 * using HTML hyperlinks &lt;a href="link"&gt;Name&lt;/a&gt;.
 * For example, the BBC page has such links with URL rss.xml, so one
 * would use URL http://news.bbc.co.uk/2/hi/help/3223484.stm with
 * URL search string as rss.xml to weed out the unrelated links.
 *
 * @author Irving Bunton
 */
public class HTMLAutoLinkParser extends FeedListParser {
    
	boolean m_needRss = true;
	boolean m_needFirstRss = false;
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("HTMLAutoLinkParser");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Creates a new instance of HTMLAutoLinkParser */
    public HTMLAutoLinkParser(String url, String username, String password) {
        super(url, username, password);
    }

    public RssItunesFeed[] parseFeeds(InputStream is) {
		// Init in case we get a severe error.
		try {
			return HTMLAutoLinkParser.parseFeeds(new EncodingUtil(is),
											m_url,
											m_needRss,
											m_needFirstRss,
											m_feedNameFilter,
											m_feedURLFilter
											//#ifdef DLOGGING
//@											,logger
//@											,fineLoggable
//@											,finerLoggable
//@											,finestLoggable
											//#endif
											);
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("parseFeeds error.", t);
//#endif
			System.out.println("parseFeeds error." + t + " " + t.getMessage());
			return null;
		}
	}
        
	// Parse feeds.  Allow null title.
    static public RssItunesFeed[] parseFeeds(EncodingUtil encodingUtil,
										String url,
										boolean needRss,
										boolean needFirstRss,
										String feedNameFilter,
										String feedURLFilter
										//#ifdef DLOGGING
//@										,Logger logger,
//@										 boolean fineLoggable,
//@										 boolean finerLoggable,
//@										 boolean finestLoggable
										//#endif
			                           ) {
        /** Initialize item collection */
        Vector rssFeeds = new Vector();
        
        /** Initialize XML parser and parse OPML XML */
        HTMLParser parser = new HTMLParser(encodingUtil);
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
            
			boolean windows = parser.isWindows();
			boolean utf = parser.isUtf();
			boolean process = true;
			boolean bodyFound = false;
            do {
				/** RSS item properties */
				String title = "";
				String link = "";
												
				String tagName = parser.getName();
				//#ifdef DLOGGING
//@				if (finerLoggable) {logger.finer("tagname: " + tagName);}
				//#endif
				switch (tagName.charAt(0)) {
					case 'b':
					case 'B':
						if (bodyFound) {
							continue;
						}
						bodyFound = parser.isBodyFound();
						if (bodyFound) {
							windows = parser.isWindows();
							utf = parser.isUtf();
						}
						// If looking for OPML link, it is in header.
						if ((!needRss || needFirstRss) && bodyFound) {
							process = false;
							break;
						}
						break;
					case 'l':
					case 'L':
						if (!tagName.toLowerCase().equals("link")) {
							break;
						}
						//#ifdef DLOGGING
//@						if (finerLoggable) {logger.finer("Parsing <link> tag");}
						//#endif
						
						// TODO base
						String type = parser.getAttributeValue( "type" );
						if (type == null) {
							continue;
						}
						if (!needRss && (type.toLowerCase().indexOf("opml") < 0)) {
							continue;
						}
						if (needRss &&
								((type.toLowerCase().indexOf("rss") < 0) &&
								(type.toLowerCase().indexOf("atom") < 0))) {
							continue;
						}
						title = parser.getAttributeValue( "title" );
						// Allow null title so that the caller can
						// check if it needs to get the title another way.
						if (title != null) {
							title = EncodingUtil.replaceAlphaEntities(title);
							title = EncodingUtil.replaceNumEntity(title);
							// Replace special chars like left quote, etc.
							// Since we have already converted to unicode, we want
							// to replace with uni chars.
							title = encodingUtil.replaceSpChars(title);

							title = StringUtil.removeHtml(title);
						}
						if (((link = parser.getAttributeValue( "href" ))
									== null) || ( link.length() == 0 )) {
							continue;
						}
						if (link.charAt(0) == '/') {
							link = url + link;
						}
						
						/** Debugging information */
						System.out.println("Title:       " + title);
						System.out.println("Link:        " + link);
						
						/** 
						 * Create new RSS item and add it do RSS document's item
						 * collection.  Account for wrong OPML which is an
						 * OPML composed of other OPML.  These have url attribute
						 * instead of link attribute.
						 */
						if (!needRss || needFirstRss) {
							RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
							rssFeeds.addElement( feed );
							process = false;
							break;
						}
						if (( feedURLFilter != null) &&
							( link.toLowerCase().indexOf(feedURLFilter) < 0)) {
							continue;
						}
						if (( feedNameFilter != null) &&
							((title != null) &&
							(title.toLowerCase().indexOf(feedNameFilter) < 0))) {
							continue;
						}
						RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
						rssFeeds.addElement( feed );
						break;
					default:
				}
			}
            while( process && (parser.parse() != XmlParser.END_DOCUMENT) );
            
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
        if (feeds.length > 0) {
			rssFeeds.copyInto(feeds);
		}
        return feeds;
    }
    
    public void setNeedRss(boolean needRss) {
        this.m_needRss = needRss;
    }

    public boolean isNeedRss() {
        return (m_needRss);
    }

}
