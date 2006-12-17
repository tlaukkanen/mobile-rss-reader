/*
 * AtomParser.java
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

import com.substanceofcode.rssreader.businessentities.RssItem;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Tommi
 */
public class AtomFormatParser implements FeedFormatParser{
    
    /** Creates a new instance of AtomParser */
    public AtomFormatParser() {
    }
    
    public Vector parse(InputStream inputStream, int maxItemCount) {
        
        Vector items = new Vector();

            // Prepare buffer for input data
            StringBuffer inputBuffer = new StringBuffer();
            
            
             
            // Read all data to buffer
            int inputCharacter;
            try {
                inputStream.reset();
                while ((inputCharacter = inputStream.read()) != -1 &&
                        inputBuffer.length()<1000) {
                    inputBuffer.append((char)inputCharacter);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
             
            // Split buffer string by each new line
            String text = inputBuffer.toString();
            System.out.println("Input: " + text);        
                
        return items;
    }
    
    /** Parse Atom feed */
    public Vector parse(XmlParser parser, int maxItemCount) throws IOException {
        /** Atom item properties */
        String title = null;
        String description = null;
        String link = null;
        String date = null;
        
        Vector items = new Vector();
        
        /** Parse to first entry element */
        while(!parser.getName().equals("entry")) {
            System.out.println("Parsing to first entry");
            if(parser.parse()==XmlParser.END_DOCUMENT) {
                System.out.println("No entries found.");
                return items;
            }
        }
        
        int parsingResult = parser.parse();
        while( parsingResult!=XmlParser.END_DOCUMENT ) {
            String elementName = parser.getName();
            
            if( elementName.equals("entry") ) {
                /** Save previous entry */
                if(title.length()>0) {
                    RssItem item = new RssItem(title, link, description);
                    items.addElement( item );
                    if(items.size()==maxItemCount) {
                        return items;
                    }
                }                
                
                /** New entry */
                title = "";
                description = "";
                link = "";
                date = "";
            }
            else if( elementName.equals("title") ) {
                title = parser.getText();
                title = StringUtil.removeHtml( title );
            }
            else if( elementName.equals("link") ) {
                link = parser.getText();
            }
            else if( elementName.equals("content")) {
                description = parser.getText();
                description = StringUtil.removeHtml( description );
            }
            
            /** Parse next element */            
            parsingResult = parser.parse();
        }

        /** Save previous entry */
        if(title.length()>0) {
            RssItem item = new RssItem(title, link, description);
            items.addElement( item );
        }        
                        
        return items;
    }
    
    /**
     * Method converts string representation of xsd:dateTime to Date class object.
     *
     * @input dateString    String should be in the following format
     *                      [-]CCYY-MM-DDThh:mm:ss[Z|(+|-)hh:mm]
     * @return              Converted date object.
     */
    private Date parseXsdDate(String dateString) {
        Date result = null;
        return result;
    }
    
}
