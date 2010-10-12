//--Need to modify--#preprocess
/*
 * HTMLAutoLinkParser.java
 *
 * Copyright (C) 2007-2008 Tommi Laukkanen
 * Copyright (C) 2007-2008 Irving Bunton
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
 * IB 2010-05-24 1.11.5RC2 Test compatibility HTMLAutoLinkParser.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser and HTMLLinkParser in small memory MIDP 1.0 to save space.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define logging define
@DLOGDEF@

//#ifndef DSMALLMEM
package com.substanceofcode.rssreader.businesslogic.compatibility4;

import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.utils.compatibility4.HTMLParser;
import com.substanceofcode.utils.compatibility4.XmlParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.compatibility4.StringUtil;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * HTMLAutoLinkParser class is used when we are parsing RSS feed list 
 * using HTML autolinks &lt;link href="link" title="Name"/&gt;.
 * These have type with application/atom or rss.
 *
 * @author Irving Bunton
 */
public class HTMLAutoLinkParser extends FeedListParser {
    
	boolean m_needRss = true;
	boolean m_needFirstRss = false;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.HTMLAutoLinkParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Creates a new instance of HTMLAutoLinkParser */
    public HTMLAutoLinkParser(String url, String username, String password) {
        super(url, username, password);
    }

    public RssItunesFeedInfo[] parseFeeds(InputStream is) {
		// Init in case we get a severe error.
		try {
			return HTMLAutoLinkParser.parseFeeds(new EncodingUtil(is),
											m_url,
											m_needRss,
											m_needFirstRss,
											m_feedNameFilter,
											m_feedURLFilter
											//#ifdef DLOGGING
											,logger
											,fineLoggable
											,finerLoggable
											,finestLoggable
											//#endif
											);
		} catch (Throwable t) {
//#ifdef DLOGGING
			logger.severe("parseFeeds error.", t);
//#endif
			System.out.println("parseFeeds error." + t + " " + t.getMessage());
			return null;
		}
	}
        
	// Parse feeds.  Allow null title.
    static public RssItunesFeedInfo[] parseFeeds(EncodingUtil encodingUtil,
										String url,
										boolean needRss,
										boolean needFirstRss,
										String feedNameFilter,
										String feedURLFilter
										//#ifdef DLOGGING
										,Logger logger,
										 boolean fineLoggable,
										 boolean finerLoggable,
										 boolean finestLoggable
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
            
			/* FUTURE?
			boolean windows = parser.isWindows();
			boolean utf = parser.isUtf();
			*/
			boolean process = true;
			boolean bodyFound = false;
            do {
				/** RSS item properties */
				String title = "";
				String link = "";
												
				String tagName = parser.getName();
				//#ifdef DLOGGING
				if (finerLoggable) {logger.finer("tagname: " + tagName);}
				//#endif
				switch (tagName.charAt(0)) {
					case 'b':
					case 'B':
						if (bodyFound) {
							continue;
						}
						bodyFound = parser.isBodyFound();
						/* FUTURE?
						if (bodyFound) {
							   windows = parser.isWindows();
							   utf = parser.isUtf();
						}
						 */
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
						if (finerLoggable) {logger.finer("Parsing <link> tag");}
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
            System.err.println("HTMLAutoLinkParser.parseFeeds(): Exception " + ex.toString());
			ex.printStackTrace();
            return null;
        } catch (Throwable t) {
            System.err.println("HTMLAutoLinkParser.parseFeeds(): Exception " + t.toString());
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
//#endif
