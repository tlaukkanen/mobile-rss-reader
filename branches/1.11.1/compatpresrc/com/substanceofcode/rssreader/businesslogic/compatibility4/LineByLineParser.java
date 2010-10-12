//--Need to modify--#preprocess
/*
 * LineByLineParser.java
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
 * IB 2010-05-24 1.11.5RC2 Test compatibility LineByLineParser.
 * IB 2010-05-24 1.11.5RC2 Put compatibility4 in getLogger.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.rssreader.businesslogic.compatibility4;

import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
import com.substanceofcode.utils.compatibility4.StringUtil;
import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * LineByLineParser class is used when we are parsing RSS feed list 
 * line-by-line.
 *
 * @author Tommi Laukkanen
 */
public class LineByLineParser extends FeedListParser {
    
	//#ifdef DLOGGING
    private Logger m_logger = Logger.getLogger("compatibility4.LineByLineParser");
	//#endif

    /** Creates a new instance of LineByLineParser */
    public LineByLineParser(String url, String username, String password) {
        super(url, username, password);
    }

    public RssItunesFeedInfo[] parseFeeds(InputStream is) {
        // Prepare buffer for input data
        StringBuffer inputBuffer = new StringBuffer();
        
        // Read all data to buffer
        int inputCharacter;
        try {
            while ((inputCharacter = is.read()) != -1) {
                inputBuffer.append((char)inputCharacter);
            }
        } catch (IOException ex) {
			//#ifdef DLOGGING
			m_logger.severe("parseFeeds Could not read string.", ex);
			//#endif
            ex.printStackTrace();
        }
        
        // Split buffer string by each new line
        String text = inputBuffer.toString();
        text = StringUtil.replace(text, "\r", "");
        String[] lines = StringUtil.split(text, "\n");
        
        RssItunesFeed[] feeds = new RssItunesFeed[ lines.length ];
        for(int lineIndex=0; lineIndex<lines.length; lineIndex++) {
            String line = lines[lineIndex];
			if (line.length() == 0) {
				continue;
			}
            String name;
            String url;
            int indexOfSpace = line.indexOf(' ');
            if(indexOfSpace>0) {
                name = line.substring(indexOfSpace+1);
                url = line.substring(0, indexOfSpace);
            } else {
                name = line;
                url = line;
            }
			if((( m_feedNameFilter != null) &&
			  (name.toLowerCase().indexOf(m_feedNameFilter) < 0)) ||
			  (( m_feedURLFilter != null) &&
			  (url.toLowerCase().indexOf(m_feedURLFilter) < 0))) {
				continue;
			}
            feeds[lineIndex] = new RssItunesFeed(name, url, "", "");
        }
        
        return feeds;        
    }
    
}
