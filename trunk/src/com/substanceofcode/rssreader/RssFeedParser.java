/*
 * RssFeedParser.java
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

package com.substanceofcode.rssreader;

import javax.microedition.io.*;
import java.util.*;
import java.io.*;
import org.xmlpull.v1.*;
import org.kxml2.io.*;

/**
 * RssFeedParser is an utility class for aquiring and parsing a RSS feed.
 * HttpConnection is used to fetch RSS feed and kXML is used on xml parsing.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class RssFeedParser {
    
    private RssFeed m_rssFeed;  // The RSS feed
    private XmlPullParser m_xmlParser = new KXmlParser(); // The Xml parser
    
    /** Create new instance of RssDocument */
    public RssFeedParser(RssFeed rssFeed) {
        m_rssFeed = rssFeed;
    }
    
    /** Return RSS feed */
    public RssFeed getRssFeed() {
        return m_rssFeed;
    }
    
    /**
     * Send a GET request to web server and parse feeds from response.
     *
     * @input maxItemCount Maximum item count for the feed.
     *
     */
    public void parseRssFeed(int maxItemCount)
    throws IOException, Exception {
        
        HttpConnection hc = null;
        DataInputStream dis = null;
        String response = "";
        try {
            /**
             * Open an HttpConnection with the Web server
             * The default request method is GET
             */
            hc = (HttpConnection) Connector.open( m_rssFeed.getUrl() );
            hc.setRequestMethod(HttpConnection.GET);
            /** Some web servers requires these properties */
            //hc.setRequestProperty("User-Agent",
            //        "Profile/MIDP-1.0 Configuration/CLDC-1.0");
            hc.setRequestProperty("Content-Length", "0");
            hc.setRequestProperty("Connection", "close");
            
            /** Add credentials if they are defined */
            if( m_rssFeed.getUsername().length()>0) {
                /**
                 * Add authentication header in HTTP request. Basic authentication
                 * should be formatted like this:
                 *     Authorization: Basic QWRtaW46Zm9vYmFy
                 */
                String username = m_rssFeed.getUsername();
                String password = m_rssFeed.getPassword();
                String userPass;
                Base64 b64 = new Base64();
                userPass = username + ":" + password;
                userPass = b64.encode(userPass.getBytes());
                hc.setRequestProperty("Authorization", "Basic " + userPass);
            }
            
            /**
             * Get a DataInputStream from the HttpConnection
             * and forward it to kXML parser
             */
            
            /*
            // DEBUG_START
            // Prepare buffer for input data
            InputStream is = hc.openInputStream();
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
            System.out.println("Input: " + text);
            
            // DEBUG_END
             **/
                        
            
            parseRssFeedXml( hc.openInputStream(), maxItemCount );
        } catch(Exception e) {
            throw new Exception("Error while parsing RSS data: "
                    + e.toString());
        } finally {
            if (hc != null) hc.close();
            if (dis != null) dis.close();
        }
    }
    
    /**
     * Nasty RSS feed XML parser.
     * Seems to work with all RSS 0.91, 0.92 and 2.0.
     */
    public void parseRssFeedXml(InputStream is, int maxItemCount)
    throws IOException, XmlPullParserException {
        /** Initialize item collection */
        m_rssFeed.getItems().removeAllElements();
        
        /** Initialize XML parser and parse RSS XML */
        KXmlParser  parser = new KXmlParser();
        InputStreamReader reader = new InputStreamReader(is);
        
        /*
        char[] buffer = new char[1024];
        reader.read(buffer, 0, 1024);
        String response = new String(buffer);
        System.out.println("Response: " + response);
         */
        parser.setInput( new InputStreamReader(is) );
        
        /** RSS item properties */
        String title = null;
        String description = null;
        String link = null;
        String date = null;
        
        /** <?xml...*/
        parser.nextTag();
        
        /** Various tags... Wait for the <item> tag */
        parser.require(parser.START_TAG, null, null);
        while(!"item".equals(parser.getName()) ){
            /** Check if document doesn't include any item tags */
            if( parser.next() == parser.END_DOCUMENT )
                throw new IOException("No items in RSS feed!");
        }
        
        /** Parse <item> tags */
        do {
            parser.require(parser.START_TAG, null, null);
            
            /** Initialize properties */
            title = "";
            description = "";
            link = "";
            date = "";
            
            /** One <item> tag handling*/
            while (parser.nextTag() != parser.END_TAG) {
                parser.require(parser.START_TAG, null, null);
                String name = parser.getName();
                String text = parser.nextText();
                
                /** Handle some entities and encoded characters */
                text = replace(text, "&nbsp;", " ");
                text = replace(text, "&amp;", "&");
                text = replace(text, "&auml;", "ä");
                text = replace(text, "&ouml;", "ö");
                text = replace(text, "Ã¤", "ä");
                text = replace(text, "Ã¶", "ö");
                text = replace(text, "&#8217;", "'");
                text = replace(text, "&#8220;", "\"");
                text = replace(text, "&#8221;", "\"");
                text = replace(text, "â‚¬", "€");
                
                /** Save item property values */
                if (name.equals("title")) {
                    title = text.trim();
                } else if (name.equals("description")) {
                    description = removeHtml( text );
                } else if (name.equals("link")) {
                    link = text;
                } else if (name.equals("pubDate")) {
                    date = text;
                }
                
            }
           
            /** Create new RSS item and add it do RSS document's item
             *  collection
             */
            RssItem rssItem = null;
            if(date.length()>0) {
                Date pubDate = parseRssDate(date);
                if(pubDate!=null) {
                    rssItem = new RssItem(title, link, description, pubDate);
                } else {
                    rssItem = new RssItem(title, link, description);
                }
            } else {
                rssItem = new RssItem(title, link, description);
            }
            Vector rssItems = m_rssFeed.getItems();
            rssItems.addElement( rssItem );
            if(rssItems.size()>= maxItemCount) {
                return;
            }
            parser.nextTag();
            
        } while("item".equals(parser.getName()));
    }
    
    /* Replace all instances of a String in a String.
     *   @param  s  String to alter.
     *   @param  f  String to look for.
     *   @param  r  String to replace it with, or null to just remove it.
     */
    public static String replace( String s, String f, String r ) {
        if (s == null)  return s;
        if (f == null)  return s;
        if (r == null)  r = "";
        
        int index01 = s.indexOf( f );
        while (index01 != -1) {
            s = s.substring(0,index01) + r + s.substring(index01+f.length());
            index01 += r.length();
            index01 = s.indexOf( f, index01 );
        }
        return s;
    }
    
    /**
     * Parse RSS date format to Date object.
     * Example of RSS date:
     * Sat, 23 Sep 2006 22:25:11 +0000
     */
    private Date parseRssDate(String dateString) {
        Date pubDate = null;
        try {
            // Split date string to values
            // 0 = week day
            // 1 = day of month
            // 2 = month
            // 3 = year (could be with either 4 or 2 digits)
            // 4 = time
            // 5 = GMT
            int weekDayIndex = 0;
            int dayOfMonthIndex = 1;
            int monthIndex = 2;
            int yearIndex = 3;
            int timeIndex = 4;
            int gmtIndex = 5;
            
            String[] values = StringUtil.split(dateString, " ");
            int columnCount = values.length;
            if( columnCount==5 ) {
                // Expected format:
                // 09 Nov 2006 23:18:49 EST
                dayOfMonthIndex = 0;
                monthIndex = 1;
                yearIndex = 2;
                timeIndex = 3;
                gmtIndex = 4;
            } else if( columnCount<5 || columnCount>6 ) {
                throw new Exception("Invalid date format: " + dateString);
            }
            
            // Day of month
            int dayOfMonth = Integer.parseInt( values[ dayOfMonthIndex ] );
            
            // Month
            String[] months =  {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String monthString = values[ monthIndex ];
            int month=0;
            for(int monthEnumIndex=0; monthEnumIndex<11; monthEnumIndex++) {
                if( monthString.equals( months[ monthEnumIndex ] )==true) {
                    month = monthEnumIndex;
                }
            }
            
            // Year
            int year = Integer.parseInt(values[ yearIndex ]);
            if(year<100) {
                year += 2000;
            }
            
            // Time
            String[] timeValues = StringUtil.split(values[ timeIndex ],":");
            int hours = Integer.parseInt( timeValues[0] );
            int minutes = Integer.parseInt( timeValues[1] );
            int seconds = Integer.parseInt( timeValues[2] );
            
            // Create calendar object from date values
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, seconds);
            
            pubDate = cal.getTime();
            
        } catch(Exception ex) {
            // TODO: Add exception handling code
            System.err.println("Error while convertin date string to object: " +
                    ex.toString());
        }
        return pubDate;
    }
    
    public static String removeHtml(String text) {
        try{
            int idx = text.indexOf("<");
            if (idx == -1) return text;
            
            String plainText = "";
            String htmlText = text;
            int htmlStartIndex = htmlText.indexOf("<", 0);
            if(htmlStartIndex == -1) {
                return text;
            }
            while (htmlStartIndex>=0) {
                plainText += htmlText.substring(0,htmlStartIndex);
                int htmlEndIndex = htmlText.indexOf(">", htmlStartIndex);
                htmlText = htmlText.substring(htmlEndIndex+1);
                htmlStartIndex = htmlText.indexOf("<", 0);
            }
            plainText = plainText.trim();
            return plainText;
        } catch(Exception e) {
            System.err.println("Error while removing HTML: " + e.toString());
            return text;
        }
    }
}
