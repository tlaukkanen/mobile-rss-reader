/*
 * CompatibilityRssFeed2.java
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

// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.Base64;
import com.substanceofcode.utils.StringUtil;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.substanceofcode.testutil.logging.TestLogUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * CompatibilityRssFeed2 class contains one RSS feed's properties.
 * Properties include name and URL to RSS feed.
 *
 * @author Tommi Laukkanen
 */
public class CompatibilityRssFeed2 implements RssFeedInfo {
    
    private String m_url  = "";
    private String m_name = "";
    private String m_username = "";
    private String m_password = "";
    private Date m_upddate = null;
    
    protected Vector m_items = new Vector();  // The RSS item vector
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("CompatibilityRssFeed2");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif

    /** Creates a new instance of RSSBookmark */
    public CompatibilityRssFeed2(String name, String url, String username, String password){
        m_name = name;
        m_url = url;
        m_username = username;
        m_password = password;
    }
    
	/** Create feed from an existing feed.  **/
	public CompatibilityRssFeed2(RssFeedInfo feed) {
		this.m_url = feed.getUrl();
		this.m_name = feed.getName();
		this.m_username = feed.getUsername();
		this.m_password = feed.getPassword();
		this.m_upddate = feed.getUpddate();
		this.m_items = new Vector();
		for (int ic = 0; ic < feed.getItems().size(); ic++) {
			this.m_items.addElement(new CompatibilityRssItem2(
						(RssItemInfo)feed.getItems().elementAt(ic)));
		}
	}
    
    /** Creates a new instance of RSSBookmark with record store string */
    public CompatibilityRssFeed2(String storeString){

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
					CompatibilityRssItem2 item = CompatibilityRssItem2.deserialize( serializedItem );
					if (item != null) {
						m_items.addElement( item );
					}
				}
			}
       
        } catch(Exception e) {
            System.err.println("Error while CompatibilityRssFeed2 initialization : " + e.toString());
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
    
    /** Set bookmark's username for basic authentication */
    public void setUsername(String username){
        m_username = username;;
    }
    
    /** Return bookmark's password for basic authentication */
    public String getPassword(){
        return m_password;
    }
    
    /** Return bookmark's password for basic authentication */
    public void setPassword(String password) {
        m_password = password;
    }
    
    /** Return record store string */
    public String getStoreString(boolean serializeItems, boolean encoded){
        String serializedItems = "";
        if( serializeItems ) {
            for(int itemIndex=0; itemIndex<m_items.size();itemIndex++) {
                CompatibilityRssItem2 item = (CompatibilityRssItem2)m_items.elementAt(itemIndex);
                String serializedItem = item.serialize();
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

    public void setEtag(String etag) { }

    public String getEtag() { return null; }

	/** Copy feed to an existing feed.  **/
	/* UNDO?
	public void copyTo(CompatibilityRssFeed2 toFeed) {
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
	*/
    
	//#ifdef DTEST
	/** Compare feed to an existing feed.  **/
	public boolean equals(RssFeedInfo feed) {
		boolean result = true;
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
			if (!TestLogUtil.fieldEquals(feed.getUpddate(), m_upddate,
				"m_upddate", logger, fineLoggable)) {
				result = false;
			}

			int flen = feed.getItems().size();
			int ilen = m_items.size();
			if (!TestLogUtil.fieldEquals(flen, ilen,
				"m_items.size() ilen", logger, fineLoggable)) {
				result = false;
			}
			CompatibilityRssItem2 [] ritems = new CompatibilityRssItem2[ilen];
			m_items.copyInto(ritems);
			RssItemInfo [] fitems = new RssItemInfo[flen];
			feed.getItems().copyInto(fitems);
			for (int ic = 0; ic < ilen; ic++) {
				if (!TestLogUtil.fieldEquals(ritems[ic], fitems[ic],
							ic + ",ritems[ic]", logger, fineLoggable)) {
					result = false;
				}
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
			int flen = feed.getItems().size();
			int ilen = m_items.size();
			logger.severe("equals error feed.m_items,m_items=" + flen + "," + ((flen == 0) ? "n/a" : feed.getItems().elementAt(0)) + "," + ilen + "," + ((ilen == 0) ? "n/a" : m_items.elementAt(0)) , e);
			//#endif
		}
		return result;
	}
	//#endif
    
    /** Return RSS feed items */
    public Vector getItems() {
        return m_items;
    }
    
    /** Set items */
    public void setItems(Vector items) {
        m_items = items;
    }
    
    public void setLink(String link) { }

    public String getLink() {
        return "";
    }

    public void setUpddateTz(String supddate) { }

    public void setUpddate(Date upddate) {
        this.m_upddate = upddate;
    }

    public Date getUpddate() {
		return m_upddate;
	}

    public String getUpddateTz() { return null;}

    public Date getDate() {
        return (null);
    }

    public void setDate(Date date) { }

}
//#endif
