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
 * IB 2010-03-12 1.11.5RC1 Use htmlUrl which is link tag in feed for OPML.
 * IB 2010-03-07 1.11.4RC1 Better logging.
 * IB 2010-05-25 1.11.5RC2 Log instead of out.println.
 * IB 2010-05-25 1.11.4RC1 Recognize style sheet, and DOCTYPE and treat properly.
 * IB 2010-05-25 1.11.4RC1 Only save htmlURL if it's not 0 length .
 * IB 2010-05-27 1.11.5RC2 Put in code to write OPML file.
 * IB 2010-05-27 1.11.5RC2 Have OpmlParser code to write to OPML file only if signed.
 * IB 2010-05-29 1.11.5RC2 Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
 * IB 2010-05-30 1.11.5RC2 Do export only for signed, Itunes and JSR-75.
 * IB 2010-07-04 1.11.5Dev6 Use "" when feedNameFilter and feedURLFilter are not used.
 * IB 2010-09-26 1.11.5Dev8 Add quote after title=.
 * IB 2010-09-26 1.11.5Dev8 Remove apostrophe after htmlUrl=.
 * IB 2010-09-26 1.11.5Dev8 Remove </outline> because the outline is already closed with />.
 * IB 2010-09-26 1.11.5Dev8 Put title= on a separate line.
 * IB 2010-09-26 1.11.5Dev8 Upshift utf-8.
 * IB 2010-09-26 1.11.5Dev8 Allow export of feeds with non-smartphones.
 * IB 2010-09-27 1.11.5Dev9 Convert &,<,> in attributes to entities.
 * IB 2010-09-28 1.11.5Dev9 Have second body tag be an end tag.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Pass in rssFeeds.
 * IB 2010-01-12 1.11.5Alpha15 Add ability to log for character, 
 * IB 2010-01-12 1.11.5Alpha15 Have a constructor for existing feeds to handle future background reading of feeds.
 * IB 2011-01-12 1.11.5Alpha15 Have replace... methods not be static.
 * IB 2011-01-14 1.11.5Alpha15 Use getEncodingUtil and getEncodingStreamReader to create EncodingUtil and EncodingStreamReader respectively to eliminate cross referencing in constructors.
 * IB 2011-01-14 1.11.5Alpha15 Use only EncodingStreamReader to read data instead of having a separate InputStreamReader to read with.
 * IB 2011-01-14 1.11.5Alpha15 Have "" as empty title instead of null.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-03-06 1.11.5Dev17 Specify imports without '*'.
 * IB 2011-03-11 1.11.5Dev17 If text attribute does not have the name, try the title attribute.
 * IB 2011-03-13 1.11.5Dev17 Have constructor for future background loading of feeds.
 * IB 2011-04-05 1.11.5Dev18 Trim the title in case it has spaces for HTML and OPML parsers.
*/

// Expand to define full vers define
//#define DFULLVERS
// Expand to define full vers define
//#define DNOINTLINK
// Expand to define MIDP define
//#define DMIDP20
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define itunes define
//#define DNOITUNES
// Expand to define signed define
//#define DNOSIGNED
// Expand to define logging define
//#define DNOLOGGING
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.utils.XmlParser;
import java.util.Vector;
import java.io.InputStream;
import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.XmlParser;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

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

	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("OpmlParser");
//@    private boolean warningLoggable = logger.isLoggable(Level.WARNING);
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
	//#endif
    /** Constructor with url, username and password parameters. */
    public OpmlParser(String url, String username, String password,
			RssFeedStore rssFeeds) {
        super(url, username, password, rssFeeds);
    }
    
    /** Constructor with current feeds. */
    public OpmlParser(String[] feedNames, RssFeedStore rssFeeds) {
        super(feedNames, rssFeeds);
    }
    
    /** Parse OPML list */
    public RssItunesFeed[] parseFeeds(InputStream is) {
        /** Initialize item collection */
        Vector rssFeeds = new Vector();
        
        /** Initialize XML parser and parse OPML XML */
        XmlParser  parser = new XmlParser(is);
		//#ifdef DTEST
		//#ifdef DLOGGING
//@		if (m_logReadChar) {
//@			parser.setLogReadChar(m_logReadChar);
//@		}
//@		if (m_logParseChar) {
//@			parser.setLogChar(m_logParseChar);
//@		}
//@		if (m_logRepeatChar) {
//@			parser.setLogRepeatChar(m_logRepeatChar);
//@		}
		//#endif
		//#endif
        try {
            
			// The first element is the main tag.
			// If we found the PROLOGUE, DOCTYPE, or STYLESHEET get the next entry.
			// If link followed by meta found, go to following XML.

            int elementType = parser.parseXmlElement();

			if (elementType == XmlParser.END_DOCUMENT ) {
				return null;
			}
            
			EncodingUtil encodingUtil = (EncodingUtil)parser.getEncodingUtil();
            do {
				/** RSS item properties */
				String title = "";
				String link = "";
												
				String tagName = parser.getName();
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("tagname: " + tagName);}
				//#endif
				if (tagName.equals("outline")) {
					//#ifdef DLOGGING
//@					if (fineLoggable) {logger.fine("Parsing <outline> tag");}
					//#endif
					
					if ((title = parser.getAttributeValue( "text" )) == null) {
						title = parser.getAttributeValue( "title" );
					}
					if (title != null) {
						title = encodingUtil.replaceAlphaEntities(title);
						// No need to convert from UTF-8 to Unicode using replace
						// umlauts now because it is done with new String...,encoding.

						// Replace numeric entities including &#8217;, &#8216;
						// &#8220;, and &#8221;
						title = encodingUtil.replaceNumEntity(title);

						// Replace special chars like left quote, etc.
						// Since we have already converted to unicode, we want
						// to replace with uni chars.
						title = encodingUtil.replaceSpChars(title).trim();
					} else {
						title = "";
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
					
					//#ifdef DITUNES
//@					/** 
//@					 * For Google htmlURL is the link in the feed.
//@					 */
//@
//@					String htmlUrl = parser.getAttributeValue( "htmlUrl" );
					//#endif
					
					/** Debugging information */
					//#ifdef DLOGGING
//@					if (fineLoggable) {logger.fine("parseFeeds title,link=" + title + "," + link);}
					//#endif
					
					if(( link == null ) || ( link.length() == 0 )) {
						continue;
					}
					// Allow null title so that it can be retrieved from
					// the feed title
					if (( m_feedNameFilter.length() > 0) &&
						(title.length() != 0) &&
						(title.toLowerCase().indexOf(m_feedNameFilter) < 0)) {
						continue;
					}
					if (( m_feedURLFilter.length() > 0) &&
						( link.toLowerCase().indexOf(m_feedURLFilter) < 0)) {
						continue;
					}
					//#ifdef DLOGGING
//@					if (warningLoggable && (title.length() == 0)) {logger.warning("parseFeeds warning null title for link=" + link);}
					//#endif
					RssItunesFeed feed = new RssItunesFeed(title, link, "", "");
					//#ifdef DITUNES
//@					if ((htmlUrl != null) && (htmlUrl.length() > 0)) {
//@						feed.setLink(htmlUrl);
//@					}
					//#endif

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

		//#ifdef DSIGNED
		//#ifdef DJSR75
//@		static public String getOpmlBegin() {
//@					return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<opml version=\"1.0\">\n<head>\n<title>Rss Reader subscriptions</title>\n</head>\n<body>\n";
//@		}
//@
//@		static public String getOpmlLine(final RssItunesFeed feed) {
//@			StringBuffer sb = new StringBuffer("    <outline text=\"").append(
//@					XmlParser.convAttrData(feed.getName())).append("\"\ntitle=\"").append(XmlParser.convAttrData(feed.getName())).append(
//@					"\" type=\"rss\"\n").append("xmlUrl=\"").append(
//@						XmlParser.convAttrUrlData(feed.getUrl())).append("\" ");
			//#ifdef DITUNES
//@			sb.append("htmlUrl=\"").append(XmlParser.convAttrUrlData(feed.getLink())).append("\"");
			//#endif
//@			sb.append("/>\n");
//@			return sb.toString();
//@		}
//@
//@		static public String getOpmlEnd() {
//@			return "</body>\n</opml>\n";
//@		}
		//#endif
		//#endif
    
}
//#endif
