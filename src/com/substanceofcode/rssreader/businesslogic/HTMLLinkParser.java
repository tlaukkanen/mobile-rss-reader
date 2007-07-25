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

package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.utils.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

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
    

    /** Creates a new instance of HTMLLinkParser */
    public HTMLLinkParser(String url, String username, String password) {
        super(url, username, password);
    }

    public RssFeed[] parseFeeds(InputStream is) {
        // Prepare buffer for input data
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
        text = StringUtil.replace(text, "\r", "");
        String[] links = StringUtil.split(text, "<a");
        
        Vector vfeeds = new Vector( links.length );
        for(int linkIndex=0; linkIndex<links.length; linkIndex++) {
            String link = links[linkIndex];
            String name;
            String url;
            int indexOfHref = link.indexOf("href");
            if (indexOfHref < 0) {
				continue;
			}
            int indexOfBQuote = link.indexOf("\"", indexOfHref);
            if (indexOfBQuote < 0) {
				continue;
			}
            int indexOfEQuote = link.indexOf("\"", indexOfBQuote + 1);
            if (indexOfEQuote < 0) {
				continue;
			}
			url = link.substring(indexOfBQuote + 1, indexOfEQuote);
			if ((feedURLFilter != null) &&
				(url.toLowerCase().indexOf(feedURLFilter) < 0)) {
				continue;
			}
            int indexOfGt = link.indexOf('>', indexOfEQuote);
            if (indexOfGt < 0) {
				continue;
			}
            int indexOfELink = link.indexOf("</a>", indexOfGt);
            if (indexOfELink < 0) {
				continue;
			}
			name = link.substring(indexOfGt + 1, indexOfELink);
			if ((feedNameFilter != null) &&
				(name.toLowerCase().indexOf(feedNameFilter) < 0)) {
				continue;
			}
			name = name.trim();
			// Must be a valid name.
			if (name.indexOf('<') >= 0) {
				continue;
			}
            vfeeds.addElement(new RssFeed(name, url, "", ""));
        }
        RssFeed[] feeds = new RssFeed[ vfeeds.size() ];
        for(int linkIndex=0; linkIndex<feeds.length; linkIndex++) {
            feeds[linkIndex] = (RssFeed)vfeeds.elementAt(linkIndex);
		}
        
        return feeds;        
    }
    
}
