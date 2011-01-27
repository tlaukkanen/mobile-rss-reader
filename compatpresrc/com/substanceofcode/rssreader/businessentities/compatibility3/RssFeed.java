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

// Expand to define logging define
@DLOGDEF@
// Expand to define itunes define
@DITUNESDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities.compatibility3;

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
    
    protected static final char CONE = (char)1;
    protected static final char [] CBONE = {CONE};
    public static String STR_ONE = new String(CBONE);
    protected static final char [] CBTWO = {(char)2};
    public static String STR_TWO = new String(CBTWO);
    public static int ITUNES_ITEMS = 8;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility3.RssFeed");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#else
    private Object logger = null;
    private boolean fineLoggable = true;
	//#endif
    protected String m_url  = "";
    protected String m_name = "";
    protected String m_username = "";
    protected String m_password = "";
    protected Date m_upddate = null;
    protected Date m_date = null;
    protected String m_link = "";   // The RSS feed link
    protected int m_category = -1; // The RSS feed category
    
    protected Vector m_items = new Vector();  // The RSS item vector
    
    /** Creates a new instance of RSSBookmark */
    public RssFeed(){
	}

    /** Creates a new instance of RSSBookmark */
    public RssFeed(String name, String url, String username, String password){
        m_name = name;
        m_url = url;
        m_username = username;
        m_password = password;
    }
    
    /** Creates a new instance of RSSBookmark */
    public RssFeed(String name, String url, String username, String password,
				   Date upddate,
				   String link,
				   Date date,
				   int category) {
        m_name = name;
        m_url = url;
        m_username = username;
        m_password = password;
        m_upddate = upddate;
		//#ifdef DITUNES
        m_link = link;
        m_date = date;
		//#endif
        m_category = category;
    }
    
	/** Create feed from an existing feed.  **/
	public RssFeed(RssFeedInfo feed) {
		this.m_url = feed.getUrl();
		this.m_name = feed.getName();
		this.m_username = feed.getUsername();
		this.m_password = feed.getPassword();
		//#ifdef DITUNES
		this.m_link = feed.getLink();
		this.m_date = feed.getDate();
		//#endif
		this.m_items = new Vector();
		int ilen = feed.getItems().size();
		RssItem [] rItems = new RssItem[ilen];
		feed.getItems().copyInto(rItems);
		for (int ic = 0; ic < ilen; ic++) {
			m_items.addElement(rItems[ic]);
		}
	}
    
    /** Creates a new instance of RSSBookmark with record store string 
	  firstSettings  - True if the data was from the settings which were
	  				   compatible with the first version and several after.
	  **/
    public RssFeed(boolean firstSettings, boolean encoded, String storeString){

		try {
        
			String[] nodes = MiscUtil.split( storeString, "|" );
			init(firstSettings, 0, false, false, encoded, nodes);
        } catch(Exception e) {
            System.err.println("Error while RssFeed initialization : " + e.toString());
			e.printStackTrace();
        }
	}
			
	/**
	  Initialize fields in the class from data.
	  startIndex - Starting index in nodes of RssItem
	  iTunesCapable - True if the data can support Itunes (but may not
	  				  actually have Itunes data) or may not be turned
					  on by the user.  So, the serializaion/deserialization
					  will account for iTunes fields except if not
					  enabled, the will have empty values.
	  hasPipe - True if the data has a pipe in at least one item
	  nodes - (elements in an array).
	  **/
	protected void init(boolean firstSettings,
						int startIndex, boolean iTunesCapable,
					    boolean hasPipe, boolean encoded,
					    String [ ] nodes) {

		try {
        
			/* Node count should be 8
			 * name | url | username | password | upddate | link | date |
			 * category | items 
			 */
			int NAME = 0;
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("startIndex,nodes.length,first nodes=" + startIndex + "," + nodes.length + "|" + nodes[ startIndex + NAME ]);}
			//#endif
			m_name = nodes[ startIndex + NAME ];
			
			int URL = 1;
			m_url = nodes[ startIndex + URL ];
			
			int USERNAME = 2;
			m_username = nodes[ startIndex + USERNAME ];
			if (iTunesCapable) {
				m_username = m_username.replace(CONE, '|');
			}
			
			int PASSWORD = 3;
			m_password = nodes[ startIndex + PASSWORD ];
			if (iTunesCapable) {
				m_password = m_password.replace(CONE, '|');
				// Dencode so that password is not in regular lettters.
				Base64 b64 = new Base64();
				byte[] decodedPassword = b64.decode(m_password);
				try {
					m_password = new String( decodedPassword , "UTF-8" );
				} catch (UnsupportedEncodingException e) {
					m_password = new String( decodedPassword );
				}
				if (hasPipe) {
					m_password = m_password.replace(CONE, '|');
				}
			}
			
			m_items = new Vector();
			if (firstSettings) {
				// Given the bugs with the first settings, we do not
				// retrieve the items so that we can restore them
				// without the bugs.
				return;
			}

			int ITEMS = (iTunesCapable ? ITUNES_ITEMS : 5);
			int UPDDATE = 4;
			String dateString = nodes[startIndex + UPDDATE];
			if(dateString.length()>0) {
				if (iTunesCapable) {
					m_upddate = new Date(Long.parseLong(dateString, 16));
				} else {
					m_upddate = new Date(Long.parseLong(dateString));
				}
			}
			if (iTunesCapable && hasPipe) {
				if (hasPipe) {
					m_name = m_name.replace(CONE, '|');
				}
			} else {
				if (!iTunesCapable) {
					// Dencode for better UTF-8 and to allow '|' in the name.
					// For iTunesCapable, replace | with (char)1
					Base64 b64 = new Base64();
					byte[] decodedName = b64.decode(m_name);
					try {
						m_name = new String( decodedName , "UTF-8" );
					} catch (UnsupportedEncodingException e) {
						m_name = new String( decodedName );
					}
				}
			}
			if (iTunesCapable) {
				//#ifdef DITUNES
				int LINK = 5;
				if (nodes[startIndex + LINK].length() > 0) {
					m_link = nodes[startIndex + LINK];
				}
				int DATE = 6;
				String fdateString = nodes[startIndex + DATE];
				if (fdateString.length() > 0) {
					m_date = new Date(Long.parseLong(fdateString, 16));
				}
				//#endif
				int CATEGORY = 7;
				if (nodes[startIndex + CATEGORY].length() > 0) {
					m_category = Integer.parseInt(nodes[startIndex + CATEGORY]);
				}
			}
			if (firstSettings) {
				// Given the bugs with the first settings, we do not
				// retrieve the items so that we can restore them
				// without the bugs.
				return;
			}
			String itemArrayData = nodes[ startIndex + ITEMS ];
			
			// Deserialize itemss
			String[] serializedItems = MiscUtil.split(itemArrayData, ".");
			
			for(int itemIndex=0; itemIndex<serializedItems.length; itemIndex++) {
				String serializedItem = serializedItems[ itemIndex ];
				if(serializedItem.length()>0) {
					RssItem item3;
					if (iTunesCapable) {
						if (encoded) {
							item3 = RssItunesItem.deserialize( 
									serializedItem );
						} else {
							item3 = RssItunesItem.unencodedDeserialize(
									serializedItem );
						}
					} else {
						item3 = RssItem.deserialize( serializedItem );
					}
					if (item3 != null) {
						m_items.addElement( item3 );
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
    
    /** Return bookmark's username for basic authentication */
    public void setUsername(String username){
        m_username = username;
    }
    
    /** Return bookmark's password for basic authentication */
    public String getPassword(){
        return m_password;
    }
    
    /** Return bookmark's password for basic authentication */
    public void setPassword(String password) {
        m_password = password;
    }
    
    /** Return record store string for feed only.  This excludes items which
	    are put into store string by RssItunesFeed.  */
    public String getStoreString(boolean serializeItems, boolean encoded){
        StringBuffer serializedItems = new StringBuffer();
        if( serializeItems ) {
			int ilen = m_items.size();
			RssItunesItem [] ritems = new RssItunesItem[ilen];
			m_items.copyInto(ritems);
            for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
                RssItunesItem RssItem = (RssItunesItem)ritems[itemIndex];
				if (encoded) {
					serializedItems.append(RssItem.serialize());
					serializedItems.append(".");
				} else {
					serializedItems.append(RssItem.unencodedSerialize());
					serializedItems.append(CBTWO);
				}
            }
        }
		String name = m_name.replace('|', CONE);
		String username = m_username.replace('|' , CONE);
		String password = m_password.replace('|' , CONE);
		String encodedPassword;
		// Encode password to make reading password difficult
        Base64 b64 = new Base64();
		try {
			encodedPassword = b64.encode( m_password.getBytes("UTF-8") );
		} catch (UnsupportedEncodingException e) {
			encodedPassword = b64.encode( m_password.getBytes() );
		}
	    String dateString;
        if(m_date==null){
            dateString = "";
        } else {
		    // We use base 16 (hex) for the date so that we can save some
			// space for toString.
            dateString = Long.toString( m_date.getTime(), 16 );
        }
        String updString;
        if(m_upddate==null){
            updString = "";
        } else {
		    // We use base 16 (hex) for the update date so that we can save some
			// space for toString.
            updString = Long.toString( m_upddate.getTime(), 16 );
        }
        String storeString = m_name + "|" +
                              m_url + "|" + username + "|" +
                encodedPassword + "|" + updString + "|" +
				m_link + "|" + dateString + "|" +
				((m_category == -1) ? "" : Integer.toString(m_category)) +
				"|" + serializedItems;
        return storeString;
        
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
			if (!TestLogUtil.fieldEquals(feed.getDate(), m_date,
				"m_date", logger, fineLoggable)) {
				result = false;
			}
			if (!TestLogUtil.fieldEquals(feed.getLink(), m_link,
				"m_link", logger, fineLoggable)) {
				result = false;
			}
			if (feed instanceof RssFeed) {
				RssFeed feed3 = (RssFeed)feed;
				if (feed3.m_category != this.m_category) {
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("equals unequal feed3.m_category,this=" + feed3.m_category + "," + m_category);}
					//#endif
					result = false;
				}
			}
			if (!TestLogUtil.fieldEquals(flen, ilen,
				"m_items.size() ilen", logger, fineLoggable)) {
				result = false;
			}
			RssItem [] ritems = new RssItem[ilen];
			m_items.copyInto(ritems);
			RssItemInfo [] fitems = new RssItemInfo[flen];
			feed.getItems().copyInto(fitems);
			for (int ic = 0; (ic < ilen) && (ic < flen); ic++) {
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
    
    /** Return RSS feed items */
    public Vector getItems() {
        return m_items;
    }
    
    /** Set items */
    public void setItems(Vector items) {
        m_items = items;
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

    public void setEtag(String etag) {}

    public String getEtag() {
        return null;
    }

    public void setCategory(int category) {
        this.m_category = category;
    }

    public int getCategory() {
        return (m_category);
    }

    /** Write record as a string */
    public String toString() {
        StringBuffer serializedItems = new StringBuffer();
		int ilen = m_items.size();
		RssItunesItem [] ritems = new RssItunesItem[ilen];
		m_items.copyInto(ritems);
		for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
			RssItunesItem RssItem = (RssItunesItem)ritems[itemIndex];
			serializedItems.append(RssItem.toString());
			serializedItems.append(".");
		}
        String dateString;
        if(m_date==null){
            dateString = "";
        } else {
		    // We use base 16 (hex) for the date so that we can save some
			// space for toString.
            dateString = Long.toString( m_date.getTime(), 16 );
        }
        String updString;
        if(m_upddate==null){
            updString = "";
        } else {
		    // We use base 16 (hex) for the update date so that we can save some
			// space for toString.
            updString = Long.toString( m_upddate.getTime(), 16 );
        }
        String storeString = m_name + "|" + m_url + "|" + m_username + "|" +
                m_password + "|" +
                updString + "|" + m_link + "|" + m_category + "|" +
                dateString + "|" + serializedItems.toString();
        return storeString;
        
    }

    public void setLink(String link) {
		//#ifdef DITUNES
		if (!link.equals(m_url)) {
			this.m_link = link;
		}
		//#endif
    }

    public String getLink() {
        return (m_link);
    }

    public void setDate(Date date) {
		//#ifdef DITUNES
        this.m_date = date;
		//#endif
    }

    public Date getDate() {
        return (m_date);
    }

}
//#endif
