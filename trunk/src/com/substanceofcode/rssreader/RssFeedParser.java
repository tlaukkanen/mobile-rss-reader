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
    
    /** send a GET request to web server */
    public void parseRssFeed()
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
            hc.setRequestProperty("User-Agent", 
                    "Profile/MIDP-1.0 Configuration/CLDC-1.0");
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
                Base64 b64 = new Base64();
                String userPass;
                userPass = username + ":" + password;
                userPass = b64.encode(userPass.getBytes());
                hc.setRequestProperty("Authorization", "Basic " + userPass);
            }
            
            /** 
             * Get a DataInputStream from the HttpConnection 
             * and forward it to kXML parser
             */
            parseRssFeedXml( hc.openInputStream() );
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
    * Seams to work with both RSS 1.0 and 2.0.
    */
    public void parseRssFeedXml(InputStream is) 
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

            /** One <item> tag handling*/
            while (parser.nextTag() != parser.END_TAG) {
                parser.require(parser.START_TAG, null, null);
                String name = parser.getName();
                String text = parser.nextText();
                
                text = replace(text, "&auml;", "ä");
                text = replace(text, "&ouml;", "ö");
                text = replace(text, "Ã¤", "ä");
                text = replace(text, "Ã¶", "ö");
                
                /** Save item property values */
                if (name.equals("title")) {
                    title = text;
                }
                else if (name.equals("description")) {
                    description = text;
                }
                else if (name.equals("link")) {
                    link = text;
                }

                parser.require(parser.END_TAG, null, name);
            }
            
            /** Debugging information */
            System.out.println ("Title:       " + title);
            System.out.println ("Link:        " + link);
            System.out.println ("Description: " + description);

            /** Create new RSS item and add it do RSS document's item
             *  collection
             */
            RssItem rssItem = new RssItem(title, link, description);
            m_rssFeed.getItems().addElement( rssItem );
            parser.nextTag();
            
        } while("item".equals(parser.getName()));
    }   

    /* Replace all instances of a String in a String.
     *   @param  s  String to alter.
     *   @param  f  String to look for.
     *   @param  r  String to replace it with, or null to just remove it.
     */  
    public static String replace( String s, String f, String r )
    {
       if (s == null)  return s;
       if (f == null)  return s;
       if (r == null)  r = "";

       int index01 = s.indexOf( f );
       while (index01 != -1)
       {
          s = s.substring(0,index01) + r + s.substring(index01+f.length());
          index01 += r.length();
          index01 = s.indexOf( f, index01 );
       }
       return s;
    }    
}
