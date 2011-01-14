//--Need to modify--#preprocess
/*
 * HTMLLinkParser.java
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
*/

// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define logging define
@DLOGDEF@

//#ifndef DSMALLMEM
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.utils.EncodingStreamReader;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.HTMLParser;
import com.substanceofcode.utils.XmlParser;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public HTMLLinkParser(String url, String username, String password) {
        super(url, username, password);
    }

    public RssItunesFeed[] parseFeeds(InputStream is) {
		// Init in case we get a severe error.
		try {
			return HTMLLinkParser.parseFeeds(new EncodingUtil(is),
											m_url,
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
        
    static public RssItunesFeed[] parseFeeds(EncodingUtil encodingUtil,
										String url,
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
        HTMLParser parser = new HTMLParser(url, encodingUtil);
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
				}
				/** RSS item properties */
				String title = "";
				String link = "";
												
				String tagName = parser.getName();
				//#ifdef DLOGGING
				if (finerLoggable) {logger.finer("tagname: " + tagName);}
				//#endif
				if (tagName.length() == 0) {
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
						//#ifdef DLOGGING
						if (finerLoggable) {logger.finer("Parsing <a> tag");}
						//#endif
						
						title = parser.getText(true);
						// Title can be 0 as this is used also for
						// getting 
						title = title.trim();
						title = MiscUtil.removeHtml( title );

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
							if (finerLoggable) {logger.finer("Not support for protocol or no protocol=" + link);}
							//#endif
							e.printStackTrace();
						}
			
						/** Debugging information */
						//#ifdef DLOGGING
						if (finerLoggable) {logger.finer("Title:       " + title);}
						if (finerLoggable) {logger.finer("Link:        " + link);}
						//#endif

						if ((feedURLFilter.length() > 0) &&
							( link.toLowerCase().indexOf(feedURLFilter) < 0)) {
							continue;
						}
						
						if ((feedNameFilter.length() > 0) &&
							((title != null) &&
							(title.toLowerCase().indexOf(feedNameFilter) < 0))) {
							continue;
						}
						//#ifdef DLOGGING
						if (title == null) {logger.warning("parseFeeds warning null title for link=" + link);}
						//#endif
						RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
						rssFeeds.addElement( feed );
						break;
					default:
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
        return feeds;
    }
    
}
//#endif
