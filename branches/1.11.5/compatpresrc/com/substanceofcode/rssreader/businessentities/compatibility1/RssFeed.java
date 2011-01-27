//--Need to modify--#preprocess
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
/*
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-24 1.11.5Dev16 Have logger object, finestLoggable, and traceLoggable for using JMUnit on a device.
 */

package com.substanceofcode.rssreader.businessentities.compatibility1;

import com.substanceofcode.utils.MiscUtil;
import java.util.*;

import com.substanceofcode.rssreader.businessentities.RssFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.testutil.logging.TestLogUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * RssFeed class contains one RSS feed's properties.
 * Properties include name and URL to RSS feed.
 *
 * @author Tommi Laukkanen
 */
public class RssFeed implements RssFeedInfo {
    
    private String m_url  = "";
    private String m_name = "";
    private String m_username = "";
    private String m_password = "";
    
    protected Vector m_items = new Vector();  // The RSS item vector
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility1.RssFeed");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#else
    private Object logger = null;
    private boolean fineLoggable = true;
	//#endif

    /** Creates a new instance of RSSBookmark */
    public RssFeed(String name, String url, String username, String password){
        m_name = name;
        m_url = url;
        m_username = username;
        m_password = password;
    }
    
    public RssFeed(String name, String url, String username, String password,
				   Date upddate,
				   String link,
				   Date date,
				   String etag) {
    	this(name, url, username, password);
    }
    
	/** Create feed from an existing feed.  **/
	public RssFeed(RssFeedInfo feed) {
        m_name = feed.getName();
        m_url = feed.getUrl();
        m_username = feed.getUsername();
        m_password = feed.getPassword();
		for (int ic = 0; ic < feed.getItems().size(); ic++) {
			this.m_items.addElement(
					new RssItem((RssItem)feed.getItems().elementAt(ic)));
		}
	}
    
	/** Create feed from an existing feed.  **/
	public RssFeed(RssFeed feed) {
		this((RssFeedInfo)feed);
	}

    /** Creates a new instance of RSSBookmark with record store string */
    public RssFeed(String storeString){
        
        String[] nodes = MiscUtil.split( storeString, "|" );
        
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
        String[] serializedItems = MiscUtil.split(itemArrayData, ".");
        
        m_items = new Vector();
        for(int itemIndex=0; itemIndex<serializedItems.length; itemIndex++) {
            String serializedItem = serializedItems[ itemIndex ];
            if(serializedItem.length()>0) {
                RssItem item1 = RssItem.deserialize( serializedItem );
                m_items.addElement( item1 );
            }
        }
       
    }

    /** Creates a new instance of RSSBookmark with record store string */
    RssFeed(boolean firstSettings, boolean encoded,
			String storeString) {
		this(storeString);
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
    
    /** Set bookmark's username for basic authentication */
    public void setUsername(String username){
        m_username = username;;
    }
    
    /** Return bookmark's password for basic authentication */
    public String getPassword(){
        return m_password;
    }
    
    /** Set bookmark's password for basic authentication */
    public void setPassword(String password){
        m_password = password;
    }
    
    /** Set bookmark's password for basic authentication */
    public void setUpddate(String upddate) {}

    /** Return bookmark's update date for basic authentication */
    public String getUpddate() { return "";};
    
    public void setUpddate(Date upddate) {}

    public String getEtag() { return null;}

    public void setEtag(String etag) { }

    /** Return record store string */
    public String getStoreString(boolean serializeItems){
        String serializedItems = "";
        if( serializeItems ) {
            for(int itemIndex=0; itemIndex<m_items.size();itemIndex++) {
                RssItem RssItem = (RssItem)m_items.elementAt(itemIndex);
                String serializedItem = RssItem.serialize();
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
    
    /** Return record store string for feed only.  This excludes items which
	    are put into store string by RssItunesFeed.  */
    public String getStoreString(boolean serializeItems, boolean encoded) {
		return getStoreString(serializeItems);
	}

    /** Return RSS feed items */
    public Vector getItems() {
        return m_items;
    }
    
    /** Set items */
    public void setItems(Vector items) {
        m_items = items;
    }
    
    public String getLink() {
        return null;
    }

    public void setLink(String link) {
    }

    public void setDate(Date date) {
    }

    public Date getDate() {
        return null;
    }

	/** Compare feed to an existing feed.  **/
	public boolean equals(RssFeedInfo feed) {
		if (feed == null) { return false;}
		boolean result = true;
		int flen = feed.getItems().size();
		int ilen = m_items.size();
		try {
			if (!TestLogUtil.fieldEquals(feed.getUrl(), m_url,
				"m_url", logger, fineLoggable)) {
				result = false;
			}
			if (!TestLogUtil.fieldEquals(feed.getName(), m_name,
				"m_name", logger, fineLoggable)) {
				result = false;
			}
			if (!TestLogUtil.fieldEquals(feed.getUsername(), m_username,
				"m_username", logger, fineLoggable)) {
				result = false;
			}
			if (!TestLogUtil.fieldEquals(feed.getPassword(), m_password,
				"m_password", logger, fineLoggable)) {
				result = false;
			}
			if (!TestLogUtil.fieldEquals(flen, ilen,
				"m_items.size() ilen", logger, fineLoggable)) {
				result = false;
			}
			RssItem [] ritems = new RssItem[ilen];
			m_items.copyInto(ritems);
			RssItemInfo [] fitems = new RssItemInfo[flen];
			feed.getItems().copyInto(fitems);
			for (int ic = 0; ic < ilen; ic++) {
				if (!TestLogUtil.fieldEquals(fitems[ic], ritems[ic],
							"ritems[" + ic + "]", logger, fineLoggable)) {
					result = false;
				}
			}
		} catch (Throwable e) {
			result = false;
			//#ifdef DLOGGING
			logger.severe("equals error feed.m_items,m_items=" + flen + "," + ((flen == 0) ? "n/a" : feed.getItems().elementAt(0)) + "," + ilen + "," + ((ilen == 0) ? "n/a" : m_items.elementAt(0)) , e);
			//#endif
		}
		return result;
	}
    
}
