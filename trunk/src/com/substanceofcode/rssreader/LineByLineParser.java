/*
 * LineByLineParser.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * http://www.substanceofcode.com
 *
 * Created on 9. syyskuuta 2006
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

package com.substanceofcode.rssreader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 *
 * @author Tommi
 */
public class LineByLineParser extends FeedListParser {
    
    /** Creates a new instance of LineByLineParser */
    public LineByLineParser(String url) {
        super(url);
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
        String[] lines = StringUtil.split(text, "\n");
        
        RssFeed[] feeds = new RssFeed[ lines.length ];
        for(int lineIndex=0; lineIndex<lines.length; lineIndex++) {
            String line = lines[lineIndex];
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
            feeds[lineIndex] = new RssFeed(name, url, "", "");
        }
        
        return feeds;        
    }
    
}
