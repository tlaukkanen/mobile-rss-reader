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

package com.substanceofcode.rssreader;

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
        int i = storeString.indexOf("|");
        if(i>0) {
            m_name = storeString.substring(0,i);
            int usernameIndex = storeString.indexOf("|", i+1);
            if(usernameIndex>0) {
                m_url  = storeString.substring(i+1, usernameIndex);
                // Older versions didn't include username+password
                int passwordIndex = storeString.indexOf("|", usernameIndex+1);
                if(passwordIndex>0) {
                    m_username = storeString.substring(usernameIndex+1,passwordIndex);
                    m_password = storeString.substring(passwordIndex+1);
                }
            }
            else {
                m_url  = storeString.substring(i+1);
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
    public String getStoreString(){
        return m_name + "|" + m_url + "|" + m_username + "|" + m_password;
    }
    
    /** Return RSS feed items */
    public Vector getItems() {
        return m_items;
    }
    
}
