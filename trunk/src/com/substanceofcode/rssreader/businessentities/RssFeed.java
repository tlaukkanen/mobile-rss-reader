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

import com.substanceofcode.utils.Base64;
import com.substanceofcode.utils.StringUtil;
import java.io.UnsupportedEncodingException;
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
    private Date m_upddate = null;
    
    protected Vector m_items = new Vector();  // The RSS item vector
    
    /** Creates a new instance of RSSBookmark */
    public RssFeed(String name, String url, String username, String password){
        m_name = name;
        m_url = url;
        m_username = username;
        m_password = password;
    }
    
	/** Create feed from an existing feed.  **/
	public RssFeed(RssFeed feed) {
		this.m_url = feed.m_url;
		this.m_name = feed.m_name;
		this.m_username = feed.m_username;
		this.m_password = feed.m_password;
		this.m_upddate = feed.m_upddate;
		this.m_items = feed.m_items;
		this.m_items = new Vector();
		for (int ic = 0; ic < feed.m_items.size(); ic++) {
			this.m_items.addElement(feed.m_items.elementAt(ic));
		}
	}
    
    /** Creates a new instance of RSSBookmark with record store string */
    public RssFeed(String storeString){

		try {
        
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
			
			int ITEMS = 5;
			if (ITEMS < nodes.length) {
				int UPDDATE = 4;
				String dateString = nodes[UPDDATE];
				if(dateString.length()>0) {
					m_upddate = new Date(Long.parseLong(dateString));
				}
				// Encode for better UTF-8 and to allow '|' in the name.
				Base64 b64 = new Base64();
				byte[] decodedName = b64.decode(m_name);
				try {
					m_name = new String( decodedName , "UTF-8" );
				} catch (UnsupportedEncodingException e) {
					m_name = new String( decodedName );
				}
			} else {
				ITEMS = 4;
			}
			String itemArrayData = nodes[ ITEMS ];
			
			// Deserialize itemss
			String[] serializedItems = StringUtil.split(itemArrayData, ".");
			
			m_items = new Vector();
			for(int itemIndex=0; itemIndex<serializedItems.length; itemIndex++) {
				String serializedItem = serializedItems[ itemIndex ];
				if(serializedItem.length()>0) {
					RssItem rssItem = RssItem.deserialize( serializedItem );
					if (rssItem != null) {
						m_items.addElement( rssItem );
					}
				}
			}
       
        } catch(Exception e) {
            System.err.println("Error while rssfeed initialization : " + e.toString());
			e.printStackTrace();
        }
    }
    
    /** Return bookmark's name */
    public String getName(){
        return m_name;
    }
    
    public void setName(String m_name) {
        this.m_name = m_name;
    }

    /** Return bookmark's URL */
    public String getUrl(){
        return m_url;
    }
    
    public void setUrl(String url) {
        this.m_url = url;
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
		String encodedName;
        Base64 b64 = new Base64();
		try {
			encodedName = b64.encode( m_name.getBytes("UTF-8") );
		} catch (UnsupportedEncodingException e) {
			encodedName = b64.encode( m_name.getBytes() );
		}
        String storeString = encodedName + "|" +
                              m_url + "|" + m_username + "|" +
                m_password + "|" +
                ((m_upddate == null) ? "" :
				 String.valueOf(m_upddate.getTime())) + "|" +
                serializedItems;
        return storeString;
        
    }

	/** Copy feed to an existing feed.  **/
	public void copyTo(RssFeed toFeed) {
		toFeed.m_url = this.m_url;
		toFeed.m_name = this.m_name;
		toFeed.m_username = this.m_username;
		toFeed.m_password = this.m_password;
		toFeed.m_upddate = this.m_upddate;
		toFeed.m_items = new Vector();
		for (int ic = 0; ic < this.m_items.size(); ic++) {
			toFeed.m_items.addElement(this.m_items.elementAt(ic));
		}
	}
    
    /** Return RSS feed items */
    public Vector getItems() {
        return m_items;
    }
    
    /** Set items */
    public void setItems(Vector items) {
        m_items = items;
    }
    
    public void setUpddate(Date upddate) {
        this.m_upddate = upddate;
    }

    public Date getUpddate() {
        return (m_upddate);
    }

}
