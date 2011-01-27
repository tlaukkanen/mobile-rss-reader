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

// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities.compatibility2;

import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedInfo;
import com.substanceofcode.utils.compatibility4.Base64;
import com.substanceofcode.utils.MiscUtil;
import java.io.UnsupportedEncodingException;
import java.util.*;

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
    private Date m_upddate = null;
    
    protected Vector m_items = new Vector();  // The RSS item vector
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility2.RssFeed");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
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
    
	/** Create feed from an existing feed.  **/
	public RssFeed(RssFeedInfo feed) {
		this.m_url = feed.getUrl();
		this.m_name = feed.getName();
		this.m_username = feed.getUsername();
		this.m_password = feed.getPassword();
		this.m_items = new Vector();
		for (int ic = 0; ic < feed.getItems().size(); ic++) {
			this.m_items.addElement(new RssItem(
						(RssItemInfo)feed.getItems().elementAt(ic)));
		}
	}
    
    /** Creates a new instance of RSSBookmark with record store string */
    public RssFeed(String storeString){

		try {
        
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
			String[] serializedItems = MiscUtil.split(itemArrayData, ".");
			
			m_items = new Vector();
			for(int itemIndex=0; itemIndex<serializedItems.length; itemIndex++) {
				String serializedItem = serializedItems[ itemIndex ];
				if(serializedItem.length()>0) {
					RssItem item = RssItem.deserialize( serializedItem );
					if (item != null) {
						m_items.addElement( item );
					}
				}
			}
       
        } catch(Exception e) {
            System.err.println("Error while RssFeed initialization : " + e.toString());
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
                RssItem item = (RssItem)m_items.elementAt(itemIndex);
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

	//#ifdef DTEST
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

    public void setUpddate(String upddate) {
        this.m_upddate = null;
    }

    public String getUpddate() {
		if (m_upddate == null) {
			return "";
		} else {
			return m_upddate.toString();
		}
	}

    public Date getDate() {
        return (null);
    }

    public void setDate(Date date) { }

}
//#endif
