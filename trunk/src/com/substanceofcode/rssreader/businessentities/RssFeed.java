/*
 * RssFeed.java
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

package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.StringUtil;
import java.util.*;

/**
 * RssFeed class contains one RSS feed's properties.
 * Properties include name and URL to RSS feed.
 *
 * @author Tommi Laukkanen
 */
public class RssFeed{
    
    private String m_url  = "";
    private String m_name = "";
    private String m_username = "";
    private String m_password = "";
    
    protected Vector m_items = new Vector();  // The RSS item vector
    
    /** Creates a new instance of RSSBookmark */
    public RssFeed(String name, String url, String username, String password){
        m_name = name;
        m_url = url;
        m_username = username;
        m_password = password;
    }
    
    /** Creates a new instance of RSSBookmark with record store string */
    public RssFeed(String storeString){
        
        String[] nodes = StringUtil.split( storeString, "|" );
        
        /* Node count should be 5
         * name | url | username | password | items
         */
        int NAME = 0;        
        m_name = nodes[ NAME ];
        
        int URL = 1;
        m_url = nodes[ URL ];
        
        int USERNAME = 2;
        m_username = nodes[ USERNAME ];
        
        int PASSWORD = 3;
        m_password = nodes[ PASSWORD ];
        
        int ITEMS = 4;
        String itemArrayData = nodes[ ITEMS ];
        
        // Deserialize itemss
        String[] serializedItems = StringUtil.split(itemArrayData, ".");
        
        m_items = new Vector();
        for(int itemIndex=0; itemIndex<serializedItems.length; itemIndex++) {
            String serializedItem = serializedItems[ itemIndex ];
            if(serializedItem.length()>0) {
                RssItem rssItem = RssItem.deserialize( serializedItem );
                m_items.addElement( rssItem );
            }
        }
       
    }
    
    /** Return bookmark's name */
    public String getName(){
        return m_name;
    }
    
    /** Return bookmark's URL */
    public String getUrl(){
        return m_url;
    }
    
    /** Return bookmark's username for basic authentication */
    public String getUsername(){
        return m_username;
    }
    
    /** Return bookmark's password for basic authentication */
    public String getPassword(){
        return m_password;
    }
    
    /** Return record store string */
    public String getStoreString(boolean serializeItems){
        String serializedItems = "";
        if( serializeItems ) {
            for(int itemIndex=0; itemIndex<m_items.size();itemIndex++) {
                RssItem rssItem = (RssItem)m_items.elementAt(itemIndex);
                String serializedItem = rssItem.serialize();
                serializedItems += serializedItem + ".";
            }
        }
        String storeString = m_name + "|" +
                m_url + "|" +
                m_username + "|" +
                m_password + "|" +
                serializedItems;
        return storeString;
        
    }
    
    /** Return RSS feed items */
    public Vector getItems() {
        return m_items;
    }
    
    /** Set items */
    public void setItems(Vector items) {
        m_items = items;
    }
    
}
