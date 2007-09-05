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

// Expand to define logging define
@DLOGDEF@

package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.XmlParser;
import java.io.IOException;
import java.io.InputStream;
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

    public RssFeed[] parseFeeds(InputStream is) {
		// Init in case we get a severe error.
		try {
			return HTMLLinkParser.parseFeeds(new EncodingUtil(is),
											feedNameFilter,
											feedURLFilter
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
        
    static public RssFeed[] parseFeeds(EncodingUtil encodingUtil,
										String feedNameFilter,
										String feedURLFilter
										//#ifdef DLOGGING
										,Logger logger,
										 boolean fineLoggable,
										 boolean finerLoggable,
										 boolean finestLoggable
										//#endif
			                           ) {
		// Init in case we get a severe error.
        RssFeed[] feeds = new RssFeed[0];
		String docEncoding = "";  // Default for HTML is ISO8859_1?
		try {
			// Prepare buffer for input data
			StringBuffer inputBuffer = new StringBuffer();
			String fileEncoding = encodingUtil.getFileEncoding();
			
			// Read all data to buffer
			int inputCharacter;
			try {
				while ((inputCharacter = encodingUtil.read()) != -1) {
					inputBuffer.append((char)inputCharacter);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			// Split buffer string by each new line
			String text = inputBuffer.toString();
			int pcharset;
			if ((pcharset = text.indexOf("charset=")) > 0) {
				int plt;
				if (((plt = text.lastIndexOf('<', pcharset)) != -1) &&
					(plt < pcharset)) {
					int pgt = text.indexOf(">", plt);
					String meta = text.substring(plt, pgt);
					pcharset = meta.indexOf("charset=");
					int pquot = meta.indexOf("\"", pcharset);
					String encoding = meta.substring(pcharset + 8, pquot);
					encodingUtil.getEncoding(encoding);
					docEncoding = encodingUtil.getDocEncoding();
				}
			}

			if (!docEncoding.equals("")) {
				try {
					if (fileEncoding.equals("")) {
						text = new String(text.getBytes(), docEncoding);
					} else {
						text = new String(text.getBytes(fileEncoding), docEncoding);
					}
				} catch (UnsupportedEncodingException e) {
					// We should not get here as EncodingUtil checks if
					// this is supported.
					System.out.println("UnsupportedEncodingException " + e +
									   e.getMessage());
				}
			}

			text = StringUtil.replace(text, "\r", "");
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finer("text=" + text);}
			//#endif
			String[] alinks = StringUtil.split(text, "<a ");
			
			Vector vfeeds = new Vector();
			for(int alinkIndex=0; alinkIndex<alinks.length; alinkIndex++) {
				String alink = alinks[alinkIndex];
				String[] calinks = StringUtil.split(alink, "<A ");
				for(int calinkIndex = 0; calinkIndex < calinks.length;
						calinkIndex++) {
					String calink = calinks[calinkIndex];
					//#ifdef DLOGGING
					if (finerLoggable) {logger.finer("calink=" + calink);}
					//#endif
					String name;
					String url;
					int indexOfHref = calink.indexOf("href");
					if (indexOfHref < 0) {
						indexOfHref = calink.indexOf("HREF");
						if (indexOfHref < 0) {
							continue;
						}
					}
					int indexOfBQuote = calink.indexOf("\"", indexOfHref);
					if (indexOfBQuote < 0) {
						continue;
					}
					int indexOfEQuote = calink.indexOf("\"", indexOfBQuote + 1);
					if (indexOfEQuote < 0) {
						continue;
					}
					url = calink.substring(indexOfBQuote + 1, indexOfEQuote);
					//#ifdef DLOGGING
					if (finerLoggable) {logger.finer("url=" + url);}
					//#endif
					// If filtering is requesting, continue if it does not match.
					if ((feedURLFilter != null) &&
						(url.toLowerCase().indexOf(feedURLFilter) < 0)) {
						continue;
					}
					int indexOfGt = calink.indexOf('>', indexOfEQuote);
					if (indexOfGt < 0) {
						continue;
					}
					int aindexOfELink = calink.indexOf("</a>", indexOfGt);
					int caindexOfELink = calink.indexOf("</A>", indexOfGt);
					int indexOfELink = -1;
					if ((aindexOfELink > 0) && ((caindexOfELink < 0) ||
						(aindexOfELink < caindexOfELink))) {
						indexOfELink = aindexOfELink;
					} else if ((caindexOfELink > 0) && ((aindexOfELink < 0) ||
						(caindexOfELink < aindexOfELink))) {
						indexOfELink = caindexOfELink;
					}
					if (indexOfELink < 0) {
						continue;
					}
					name = calink.substring(indexOfGt + 1, indexOfELink);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("name 1=" + name);}
					//#endif
					if ((feedNameFilter != null) &&
						(name.toLowerCase().indexOf(feedNameFilter) < 0)) {
						continue;
					}
					name = name.trim();
					// Replace special chars like left quote, etc.
					name = XmlParser.replaceAlphaEntities(name);
					name = encodingUtil.replaceNumEntity(name);
					name = encodingUtil.replaceSpChars(name);
					name = StringUtil.removeHtml(name);
					vfeeds.addElement(new RssFeed(name, url, "", ""));
				}
			}

			feeds = new RssFeed[ vfeeds.size() ];
			for(int linkIndex=0; linkIndex<feeds.length; linkIndex++) {
				feeds[linkIndex] = (RssFeed)vfeeds.elementAt(linkIndex);
			}
		} catch (Throwable t) {
//#ifdef DLOGGING
			logger.severe("parseFeeds error.", t);
//#endif
			System.out.println("parseFeeds error." + t + " " + t.getMessage());
		}
        
        return feeds;        
    }
    
}
