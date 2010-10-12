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
 * IB 2010-05-24 1.11.5RC2 Fix line by line parser to recognise Byte Order Mark (BOM).
 * IB 2010-05-24 1.11.5RC2 Combine classes to save space.
 * IB 2010-05-24 1.11.5RC2 Remove control characters beginning and ending of text to prevent blank lines at beginning or end from being processed.
 * IB 2010-05-24 1.11.5RC2 More logging.
 * IB 2010-05-24 1.11.5RC2 Optionally use feed title for feed name.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern for nulls to initialize and save memory.
 * IB 2010-07-05 1.11.5Dev6 Do not have feedNameFilter and feedUrlFilter null.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
*/

// Expand to define logging define
//#define DNOLOGGING
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.EncodingStreamReader;
import com.substanceofcode.utils.MiscUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * LineByLineParser class is used when we are parsing RSS feed list 
 * line-by-line.
 *
 * @author Tommi Laukkanen
 */
public class LineByLineParser extends FeedListParser {
    
	final       Object nullPtr = null;
	//#ifdef DLOGGING
//@    private Logger m_logger = Logger.getLogger("LineByLineParser");
//@    private boolean warningLoggable = m_logger.isLoggable(Level.WARNING);
//@    private boolean fineLoggable = m_logger.isLoggable(Level.FINE);
	//#endif

    /** Creates a new instance of LineByLineParser */
    public LineByLineParser(String url, String username, String password) {
        super(url, username, password);
    }

    public RssItunesFeed[] parseFeeds(InputStream is) {
		return parseFeeds(new EncodingUtil(is));
	}

    public RssItunesFeed[] parseFeeds(EncodingUtil encodingUtil) {
        // Prepare buffer for input data
        StringBuffer inputBuffer = new StringBuffer();
		EncodingStreamReader isr = encodingUtil.getEncodingStreamReader();
        
        // Read all data to buffer
        int inputCharacter;
        try {
			if ((inputCharacter = isr.read()) == -1) {
				return new RssItunesFeed[0];
			}
			if (isr.isUtfDoc()) {
				if (isr.isModBit16()) {
					encodingUtil.getEncoding(isr.getFileEncoding(), "UTF-8");
				} else {
					encodingUtil.getEncoding(isr.getFileEncoding(), "UTF-16");
				}
			}
            do {
                inputBuffer.append((char)inputCharacter);
            }
            while ((inputCharacter = isr.read()) != -1);
        } catch (IOException ex) {
			//#ifdef DLOGGING
//@			m_logger.severe("parseFeeds Could not read string.", ex);
			//#endif
            ex.printStackTrace();
        }
        
        // Split buffer string by each new line
        String text = inputBuffer.toString();
		if (isr.isUtfDoc()) {
			try {
				// We read the bytes in as ISO8859_1, so we must get them
				// out as that and then encode as they should be.
				if (isr.getFileEncoding().length() == 0) {
					text = new String(inputBuffer.toString().getBytes(),
									  encodingUtil.getDocEncoding());
				} else {
					text = new String(inputBuffer.toString().getBytes(
								isr.getFileEncoding()),
							encodingUtil.getDocEncoding());
				}
			} catch (IOException e) {
				//#ifdef DLOGGING
//@				m_logger.severe("parseFeeds Could not convert string from,to" + isr.getFileEncoding() + "," + encodingUtil.getDocEncoding(), e);
				//#endif
				System.out.println("parseFeeds Could not convert string " +
						"from,to=" + isr.getFileEncoding() + "," +
						encodingUtil.getDocEncoding() +
						" " + e + " " + e.getMessage());
				e.printStackTrace();
				text = inputBuffer.toString();
			}
		} else {
			text = inputBuffer.toString();
		}
		// Save memory
		inputBuffer = (StringBuffer)nullPtr;
        text = MiscUtil.replace(text.trim(), "\r", "");
        String[] lines = MiscUtil.split(text, "\n");
		// Save memory
		text = (String)nullPtr;
        
        RssItunesFeed[] feeds = new RssItunesFeed[ lines.length ];
        for(int lineIndex=0; lineIndex<lines.length; lineIndex++) {
            String line = lines[lineIndex];
            String name;
            String url;
            int indexOfSpace = line.indexOf(' ');
            if(indexOfSpace>0) {
                name = line.substring(indexOfSpace+1);
                url = line.substring(0, indexOfSpace);
            } else if (line.length() == 0) {
				//#ifdef DLOGGING
//@				if (warningLoggable) {m_logger.warning("parseFeeds warning skipping blank line for url=" + m_url);}
				//#endif
				continue;
            } else {
                name = (String)nullPtr;
                url = line;
				//#ifdef DLOGGING
//@				if (warningLoggable) {m_logger.warning("parseFeeds warning null title for url=" + url);}
				//#endif
            }
			//#ifdef DLOGGING
//@			if (fineLoggable) {m_logger.fine("parseFeeds name,url=" + name + "," + url);}
			//#endif
			if((( m_feedNameFilter.length() > 0) &&
			  (name.toLowerCase().indexOf(m_feedNameFilter) < 0)) ||
			  (( m_feedURLFilter.length() > 0) &&
			  (url.toLowerCase().indexOf(m_feedURLFilter) < 0))) {
				continue;
			}
            feeds[lineIndex] = new RssItunesFeed(name, url, "", "");
        }
        
        return feeds;        
    }
    
}
