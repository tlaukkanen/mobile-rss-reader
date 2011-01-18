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
 * IB 2010-03-07 1.11.4RC1 Combine classes to save space.
 * IB 2010-03-07 1.11.4RC1 Recognize style sheet, and DOCTYPE and treat properly.
 * IB 2010-05-26 1.11.5RC2 Use absolute address for redirects.
 * IB 2010-05-26 1.11.5RC2 More logging.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser or HTMLAutoLinkParser in small memory MIDP 1.0 to save space.
 * IB 2010-05-29 1.11.5RC2 Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
 * IB 2010-07-04 1.11.5Dev6 Do not have empty catch block.
 * IB 2010-07-04 1.11.5Dev6 Do not have feedNameFilter and feedUrlFilter null.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-12 1.11.5Alpha15 Pass rssFeeds to FeedListParser.
 * IB 2010-01-12 1.11.5Alpha15 Add ability to log for character, 
 * IB 2011-01-14 1.11.5Alpha15 Use getEncodingUtil and getEncodingStreamReader to create EncodingUtil and EncodingStreamReader respectively to eliminate cross referencing in constructors.
 * IB 2011-01-12 1.11.5Alpha15 Have replace... methods not be static.
 * IB 2011-01-14 1.11.5Alpha15 Make sure we parse an element.  Not getting one is only a future possibilty.
 * IB 2011-01-14 1.11.5Alpha15 Have "" as empty title instead of null.
 * IB 2011-01-14 1.11.5Alpha15 Only process non empty tag names.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
*/

// Expand to define memory size define
//#define DREGULARMEM
// Expand to define logging define
//#define DNOLOGGING

//#ifndef DSMALLMEM
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.utils.HTMLParser;
import com.substanceofcode.utils.XmlParser;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.MiscUtil;
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
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
//@    private Logger logger = Logger.getLogger("HTMLAutoLinkParser");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Creates a new instance of HTMLAutoLinkParser */
    public HTMLAutoLinkParser(String url, String username, String password,
			RssFeedStore rssFeeds) {
        super(url, username, password, rssFeeds);
    }

    public RssItunesFeed[] parseFeeds(InputStream is) {
		// Init in case we get a severe error.
		try {
			return HTMLAutoLinkParser.parseFeeds(EncodingUtil.getEncodingUtil(
						is),
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
//@											,m_logParseChar
//@											,m_logRepeatChar
//@											,m_logReadChar
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
//@										,Logger logger
//@										,boolean fineLoggabl
//@										,boolean finerLoggable
//@										,boolean finestLoggable
//@										,boolean logParseChar
//@										,boolean logRepeatChar
//@										,boolean logReadChar
										//#endif
			                           ) {
        /** Initialize item collection */
        Vector rssFeeds = new Vector();
        
        /** Initialize XML parser and parse OPML XML */
        HTMLParser parser = new HTMLParser(url, encodingUtil);
		//#ifdef DTEST
		//#ifdef DLOGGING
//@		if (logReadChar) {
//@			parser.setLogReadChar(logReadChar);
//@		}
//@		if (logParseChar) {
//@			parser.setLogChar(logParseChar);
//@		}
//@		if (logRepeatChar) {
//@			parser.setLogRepeatChar(logRepeatChar);
//@		}
		//#endif
		//#endif
        try {
            
			// The first element is the main tag.
			// If we found the PROLOGUE, DOCTYPE, or STYLESHEET, get the next entry.
			// If link followed by meta found, go to following XML.

            int elementType = parser.parseXmlElement();

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
												
				if (elementType != XmlParser.ELEMENT) {
					continue;
				}
				String tagName = parser.getName();
				//#ifdef DLOGGING
//@				if (finerLoggable) {logger.finer("tagname: " + tagName);}
				//#endif
				int tagLen;
				if ((tagLen = tagName.length()) == 0) {
					continue;
				}
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
							title = encodingUtil.replaceAlphaEntities(title);
							title = encodingUtil.replaceNumEntity(title);
							// Replace special chars like left quote, etc.
							// Since we have already converted to unicode, we want
							// to replace with uni chars.
							title = encodingUtil.replaceSpChars(title);

							title = MiscUtil.removeHtml(title);
						} else {
							title = "";
						}
						if ((link = parser.getAttributeValue( "href" ))
									== null) {
							continue;
						}
						link = link.trim();
						if ( link.length() == 0 ) {
							continue;
						}
						
						try {
							HTMLParser.getAbsoluteUrl(url, link);
						} catch (IllegalArgumentException e) {
							//#ifdef DLOGGING
//@							if (finerLoggable) {logger.finer("Not support for protocol or no protocol=" + link);}
							//#endif
							e.printStackTrace();
						}
			
						/** Debugging information */
						//#ifdef DLOGGING
//@						if (finerLoggable) {logger.finer("Title:       " + title);}
//@						if (finerLoggable) {logger.finer("Link:        " + link);}
						//#endif

						/** 
						 * Create new RSS item and add it do RSS document's item
						 * collection.  Account for wrong OPML which is an
						 * OPML composed of other OPML.  These have url attribute
						 * instead of link attribute.
						 */
						if (!needRss || needFirstRss) {
							//#ifdef DLOGGING
//@							if (title.length() == 0) {logger.warning("parseFeeds warning null title for link=" + link);}
							//#endif
							RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
							rssFeeds.addElement( feed );
							process = false;
							break;
						}
						if ((feedURLFilter.length() > 0) &&
							( link.toLowerCase().indexOf(feedURLFilter) < 0)) {
							continue;
						}
						if ((feedNameFilter.length() > 0) &&
							(title.length() != 0) &&
							(title.toLowerCase().indexOf(feedNameFilter) < 0)) {
							continue;
						}
						//#ifdef DLOGGING
//@						if (title.length() == 0) {logger.warning("parseFeeds warning empty title for link=" + link);}
						//#endif
						RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
						rssFeeds.addElement( feed );
						break;
					default:
						break;
				}
			}
            while( process && (elementType = parser.parse()) !=
					XmlParser.END_DOCUMENT );
            
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
