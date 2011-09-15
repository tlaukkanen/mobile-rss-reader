//--Need to modify--#preprocess
/*
 * HTMLLinkParser.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2007-2011 Irving Bunton, Jr
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
 * IB 2010-03-07 1.11.4RC1 Use absolute address for redirects.
 * IB 2010-03-07 1.11.4RC1 Recognize style sheet, and DOCTYPE and treat properly.
 * IB 2010-05-24 1.11.5RC2 Log instead of out.println.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser andd HTMLLinkParser in small memory MIDP 1.0 to save space.
 * IB 2010-05-29 1.11.5RC2 Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
 * IB 2010-07-04 1.11.5Dev6 Use "" when feedNameFilter and feedURLFilter are not used.
 * IB 2010-07-04 1.11.5Dev6 Do not have empty catch block.
 * IB 2010-07-19 1.11.5Dev8 Convert entities for text if CDATA used.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Pass in rss feed store.
 * IB 2011-01-14 1.11.5Alpha15 Have character and parse logging for testing.
 * IB 2011-01-14 1.11.5Alpha15 Fix getting the absolute URL from the relative URL.
 * IB 2011-01-14 1.11.5Alpha15 Make sure we parse an element.  Not getting one is only a future possibilty.
 * IB 2011-01-14 1.11.5Alpha15 Make sure the tag name length is > 0.
 * IB 2011-01-14 1.11.5Alpha15 Make sure the anchor is not another tag starting with 'a'.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-04-05 1.11.5Dev18 Trim the title in case it has spaces for HTML and OPML parsers.
*/

// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define logging define
@DLOGDEF@

//#ifndef DSMALLMEM
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.utils.EncodingStreamReader;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.HTMLParser;
import com.substanceofcode.utils.XmlParser;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.substanceofcode.utils.EncodingUtil;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * HTMLLinkParser class is used when we are parsing RSS feed list 
 * using HTML hyperlinks &lt;a href="link"&gt;Name&lt;/a&gt;.
 * For example, the BBC page has such links with URL rss.xml, so one
 * would use URL http://news.bbc.co.uk/2/hi/help/3223484.stm with
 * URL search string as rss.xml to weed out the unrelated links.
 *
 * @author Irving Bunton
 */
public class HTMLLinkParser extends FeedListParser {
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("HTMLLinkParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Creates a new instance of HTMLLinkParser */
    public HTMLLinkParser(String url, String username, String password,
			RssFeedStore rssFeeds) {
        super(url, username, password, rssFeeds);
    }

    public RssItunesFeed[] parseFeeds(InputStream is) {
		// Init in case we get a severe error.
		try {
			return HTMLLinkParser.parseFeeds(EncodingUtil.getEncodingUtil(is),
											m_url,
											m_feedNameFilter,
											m_feedURLFilter
											//#ifdef DLOGGING
											,logger
											,fineLoggable
											,finerLoggable
											,finestLoggable
											,m_logParseChar
											,m_logRepeatChar
											,m_logReadChar
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
        
    static public RssItunesFeed[] parseFeeds(EncodingUtil encodingUtil,
										String url,
										String feedNameFilter,
										String feedURLFilter
										//#ifdef DLOGGING
										,Logger logger
										,boolean fineLoggable
										,boolean finerLoggable
										,boolean finestLoggable
										,boolean logParseChar
										,boolean logRepeatChar
										,boolean logReadChar
										//#endif
			                           ) {
        /** Initialize item collection */
        Vector rssFeeds = new Vector();
        
        /** Initialize XML parser and parse OPML XML */
        HTMLParser parser = new HTMLParser(url, encodingUtil);
		//#ifdef DTEST
		//#ifdef DLOGGING
		if (logReadChar) {
			parser.setLogReadChar(logReadChar);
		}
		if (logParseChar) {
			parser.setLogChar(logParseChar);
		}
		if (logRepeatChar) {
			parser.setLogRepeatChar(logRepeatChar);
		}
		//#endif
		//#endif
        try {
            
			// The first element is the main tag.
			// If we found the prologue, doctype, or STYLESHEET, get the next entry.
            int elementType = parser.parseXmlElement();
			if (elementType == XmlParser.END_DOCUMENT ) {
				return null;
			}
            
			boolean bodyFound = false;
            do {
				if (elementType == HTMLParser.REDIRECT_URL) {
					RssItunesFeed [] feeds = new RssItunesFeed[1];
					feeds[0] = new RssItunesFeed("", parser.getRedirectUrl(),
							"", "");
					return feeds;
				} else if (elementType != XmlParser.ELEMENT) {
					continue;
				}
				/** RSS item properties */
				String title = "";
				String link = "";
												
				String tagName = parser.getName();
				//#ifdef DLOGGING
				if (finerLoggable) {logger.finer("tagname: " + tagName);} ;
				//#endif
				int tagLen;
				if ((tagLen = tagName.length()) == 0) {
					continue;
				}
				switch (tagName.charAt(0)) {
					case 'm':
					case 'M':
						if (bodyFound) {
							break;
						}
						break;
					case 'b':
					case 'B':
						if (!bodyFound) {
							bodyFound = parser.isBodyFound();
						}
						break;
					case 'a':
					case 'A':
						if (tagLen != 1) {
							break;
						}
						//#ifdef DLOGGING
						if (finerLoggable) {logger.finer("Parsing <a> tag");} ;
						//#endif
						
						title = parser.getText(true);
						// Title can be 0 as this is used also for
						// getting 
						title = title.trim();
						title = MiscUtil.removeHtml( title ).trim();

						if ((link = parser.getAttributeValue( "href" ))
									== null) {
							continue;
						}
						link = link.trim();
						if ( link.length() == 0 ) {
							continue;
						}
						try {
							link = HTMLParser.getAbsoluteUrl(url, link);
						} catch (IllegalArgumentException e) {
							//#ifdef DLOGGING
							if (finerLoggable) {logger.finer("Not support for protocol or no protocol=" + link);}
							//#endif
							e.printStackTrace();
						}
			
						/** Debugging information */
						//#ifdef DLOGGING
						if (finerLoggable) {logger.finer("Title:       " + title);} ;
						if (finerLoggable) {logger.finer("Link:        " + link);} ;
						//#endif

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
						if (title.length() == 0) {logger.warning("parseFeeds warning empty title for link=" + link);}
						//#endif
						RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
						rssFeeds.addElement( feed );
						break;
					default:
						break;
				}
            }
            while( (elementType = parser.parse()) != XmlParser.END_DOCUMENT );
            
        } catch (Exception ex) {
            System.err.println("HTMLLinkParser.parseFeeds(): Exception " + ex.toString());
			ex.printStackTrace();
            return null;
        } catch (Throwable t) {
            System.err.println("HTMLLinkParser.parseFeeds(): Exception " + t.toString());
			t.printStackTrace();
            return null;
        }
        
        /** Create array */
        RssItunesFeed[] feeds = new RssItunesFeed[ rssFeeds.size() ];
        rssFeeds.copyInto(feeds);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parseFeeds return url,feeds.length=" + url + "," + feeds.length);}
		//#endif
        return feeds;
    }
    
}
//#endif
//#endif
