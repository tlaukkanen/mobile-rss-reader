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

// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.Base64;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.rssreader.businesslogic.RssFormatParser;
import java.io.UnsupportedEncodingException;
import java.util.*;

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
public class RssFeed{
    
    final protected static char CONE = (char)1;
    final private static char [] CBONE = {CONE};
    final public static String STR_ONE = new String(CBONE);
    final private static char [] CBTWO = {(char)2};
    final public static String STR_TWO = new String(CBTWO);
    final public static int ITUNES_ITEMS = 8;
    final public static int MODIFY_ITEMS = 9;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("RssFeed");
	//#endif
	//#ifdef DLOGGING
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif
    protected String m_url  = "";
    protected String m_name = "";
    protected String m_username = "";
    protected String m_password = "";
    protected Date m_upddate = null;
    protected byte m_upddateTz = (byte)-1;
    protected Date m_date = null;
    protected String m_link = "";   // The RSS feed link
    protected String m_etag = ""; // The RSS feed etag
    
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
				   String etag) {
        m_name = name;
        m_url = url;
        m_username = username;
        m_password = password;
        m_upddate = upddate;
        m_upddateTz = RssFormatParser.GMT;
		//#ifdef DITUNES
        m_link = link;
        m_date = date;
		//#endif
        m_etag = etag;
    }
    
	/** Create feed from an existing feed.  **/
	public RssFeed(RssFeed feed) {
		this.m_url = feed.m_url;
		this.m_name = feed.m_name;
		this.m_username = feed.m_username;
		this.m_password = feed.m_password;
		this.m_upddate = feed.m_upddate;
		//#ifdef DITUNES
		this.m_link = feed.m_link;
		this.m_date = feed.m_date;
		//#endif
		this.m_etag = feed.m_etag;
		this.m_items = new Vector();
		int ilen = feed.m_items.size();
		RssItem [] rItems = new RssItem[ilen];
		feed.m_items.copyInto(rItems);
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
        
			String[] nodes = StringUtil.split( storeString, "|" );
			init(firstSettings, 0, false, false, false, encoded, nodes);
        } catch(Exception e) {
            System.err.println("Error while rssfeed initialization : " + e.toString());
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
					    boolean modifyCapable, boolean hasPipe, boolean encoded,
					    String [ ] nodes) {

		try {
        
			/* Node count should be 9
			 * name | url | username | password | upddate | link | date |
			 * etag | items 
			 */
			int NAME = 0;
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("init firstSettings,startIndex,iTunesCapable,modifyCapable,nodes.length,first nodes=" + firstSettings + "," + startIndex + "," + iTunesCapable + "," + modifyCapable + "," + nodes.length + "|" + nodes[ startIndex + NAME ]);}
			//#endif
			m_name = nodes[ startIndex + NAME ];
			
			int URL = 1;
			m_url = nodes[ startIndex + URL ];
			
			int USERNAME = 2;
			m_username = nodes[ startIndex + USERNAME ];
			if (iTunesCapable && hasPipe) {
				m_username = m_username.replace(CONE, '|');
			}
			
			int PASSWORD = 3;
			m_password = nodes[ startIndex + PASSWORD ];
			if (iTunesCapable) {
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
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("m_password=" + m_password);}
				//#endif
			}
			
			m_items = new Vector();
			if (firstSettings) {
				// Given the bugs with the first settings, we do not
				// retrieve the items so that we can restore them
				// without the bugs.
				return;
			}

			int ITEMS;
			if (iTunesCapable) {
				if (modifyCapable) {
					ITEMS = MODIFY_ITEMS;
				} else {
					ITEMS = ITUNES_ITEMS;
				}
			} else {
				ITEMS = 5;
			}
			int UPDDATE = 4;
			String dateString = nodes[startIndex + UPDDATE];
			// In this version, we are adding ETag, so we cannot use
			// m_upddate from before because they must be used together.
			if((dateString.length()>0) && iTunesCapable && modifyCapable) {
				m_upddate = new Date(Long.parseLong(dateString, 16));
				int UPDDATETZ = 8;
				String stz = nodes[startIndex + UPDDATETZ];
				m_upddateTz = (stz.length() == 0) ? (byte)-1 : (byte)Integer.parseInt(stz);
			}
			int ETAG = 7;
			String etagString = nodes[startIndex + ETAG];
			if (etagString.length() > 0) {
				m_etag = etagString;
			}
			if (iTunesCapable && hasPipe) {
				m_name = m_name.replace(CONE, '|');
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
			}
			if (firstSettings) {
				// Given the bugs with the first settings, we do not
				// retrieve the items so that we can restore them
				// without the bugs.
				return;
			}
			String itemArrayData = nodes[ startIndex + ITEMS ];
			
			// Deserialize itemss
			String[] serializedItems = StringUtil.split(itemArrayData, ".");
			
			for(int itemIndex=0; itemIndex<serializedItems.length; itemIndex++) {
				String serializedItem = serializedItems[ itemIndex ];
				if(serializedItem.length()>0) {
					RssItem rssItem;
					if (iTunesCapable) {
						if (encoded) {
							rssItem = RssItunesItem.deserialize( 
									serializedItem );
						} else {
							rssItem = RssItunesItem.unencodedDeserialize(
									serializedItem );
						}
					} else {
						rssItem = RssItem.deserialize( serializedItem );
					}
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
    
    /** Return record store string for feed only.  This excludes items which
	    are put into store string by RssItunesFeed.  */
    public String getStoreString(boolean serializeItems, boolean encoded){
        StringBuffer serializedItems = new StringBuffer();
        if( serializeItems ) {
			int ilen = m_items.size();
			RssItunesItem [] ritems = new RssItunesItem[ilen];
			m_items.copyInto(ritems);
            for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
                RssItunesItem rssItem = (RssItunesItem)ritems[itemIndex];
				if (encoded) {
					serializedItems.append(rssItem.serialize());
					serializedItems.append(".");
				} else {
					serializedItems.append(rssItem.unencodedSerialize());
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
			encodedPassword = b64.encode( password.getBytes("UTF-8") );
		} catch (UnsupportedEncodingException e) {
			encodedPassword = b64.encode( password.getBytes() );
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
        String storeString = name + "|" +
                              m_url + "|" + username + "|" +
                encodedPassword + "|" + updString + "|" +
				m_link + "|" + dateString + "|" +
				m_etag + "|" +
				((m_upddateTz == (byte)-1) ? "" : Integer.toString((int)m_upddateTz)) +
				"|" + serializedItems;
        return storeString;
        
    }

	/** Copy feed to an existing feed.  **/
	public void copyTo(RssFeed toFeed) {
		toFeed.m_url = this.m_url;
		toFeed.m_name = this.m_name;
		toFeed.m_username = this.m_username;
		toFeed.m_password = this.m_password;
		toFeed.m_upddate = this.m_upddate;
		//#ifdef DITUNES
		toFeed.m_link = this.m_link;
		toFeed.m_date = this.m_date;
		//#endif
		toFeed.m_etag = this.m_etag;
		toFeed.m_items = new Vector();
		int ilen = m_items.size();
		RssItem [] ritems = new RssItem[ilen];
		m_items.copyInto(ritems);
		for (int ic = 0; ic < ilen; ic++) {
			toFeed.m_items.addElement(ritems[ic]);
		}
	}
    
	//#ifdef DTEST
	/** Compare feed to an existing feed.  **/
	public boolean equals(RssFeed feed) {
		boolean result = true;
		if (!feed.m_url.equals(this.m_url)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_url,this=" + feed.m_url + "," + m_url);}
			//#endif
			result = false;
		}
		if (!feed.m_name.equals(this.m_name)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_name,this=" + feed.m_name + "," + m_name);}
			//#endif
			result = false;
		}
		if (!feed.m_username.equals(this.m_username)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_password,this=" + feed.m_password + "," + m_password);}
			//#endif
			result = false;
		}
		if (!feed.m_password.equals(this.m_password)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_password,this=" + feed.m_password + "," + m_password);}
			//#endif
			result = false;
		}
		if ((feed.m_date != null) && (this.m_date != null)) {
			if (!feed.m_date.equals(this.m_date)) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("unequal dates=" + feed.m_date + "," + m_date);}
				//#endif
				result = false;
			}
		} else if ((feed.m_date != null) || (this.m_date != null)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal dates=" + feed.m_date + "," + m_date);}
			//#endif
			result = false;
		}
		if (!feed.m_link.equals(m_link)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_link,this=" + feed.m_link + "," + m_link);}
			//#endif
			result = false;
		}
		if ((feed.m_date != null) && (this.m_date != null)) {
			if (!feed.m_date.equals(this.m_date)) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("unequal dates=" + feed.m_date + "," + m_date);}
				//#endif
				result = false;
			}
		} else if ((feed.m_date != null) || (this.m_date != null)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal dates=" + feed.m_date + "," + m_date);}
			//#endif
			result = false;
		}
		if (!feed.m_etag.equals(this.m_etag)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal feed.m_etag,this=" + feed.m_etag + "," + m_etag);}
			//#endif
			result = false;
		}
		int flen = feed.m_items.size();
		int ilen = m_items.size();
		if (flen != ilen) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("unequal size feed,this=" + flen + "," + ilen);}
			//#endif
			result = false;
		}
		RssItem [] ritems = new RssItem[ilen];
		m_items.copyInto(ritems);
		RssItem [] fitems = new RssItem[flen];
		feed.m_items.copyInto(fitems);
		for (int ic = 0; ic < ilen; ic++) {
			if (!fitems[ic].equals(ritems[ic])) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("unequal ic,fitems[ic],ritems[ic]" + ic + "," + fitems[ic] + "," + ritems[ic]);}
				//#endif
				result = false;
			}
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
    
    public void setUpddate(Date upddate) {
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("setUpddate upddate=" + ((upddate == null) ? "null" : upddate.toString()));}
		//#endif
        this.m_upddate = upddate;
        if (upddate == null) {
			this.m_upddateTz = (byte)-1;
		} else {
			this.m_upddateTz = RssFormatParser.GMT;
		}
    }

    public void setUpddateTz(String supddate) {
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("setUpddateTz supddate.length(),supddate=" + ((supddate == null) ? "null" : Integer.toString(supddate.length())) + "," + supddate);}
		//#endif
		Object[] objs = null;
		if ((supddate != null) && (supddate.length() > 0)) {
			objs = RssFormatParser.parseStdDateTZ(supddate);
		}
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("setUpddateTz objs,objs[0],objs[1]=" + ((objs == null) ? "null" : objs[0]) + "," + ((objs == null) ? "null" : objs[1]));}
		//#endif
		if (objs == null) {
			this.m_upddate = null;
			this.m_upddateTz = (byte)-1;
		} else {
			this.m_upddate = (Date)objs[0];
			this.m_upddateTz = ((Byte)objs[1]).byteValue();
		}
    }

    public Date getUpddate() {
        return (m_upddate);
    }

    public String getUpddateTz() {
		if (m_upddate == null) {
			return null;
		}
        final String stz = (m_upddateTz == (byte)-1) ? "GMT" : RssFormatParser.stimeZones.substring((int)m_upddateTz + 1, (int)m_upddateTz + 4);
        return RssFormatParser.stdDate(m_upddate, stz);
    }

    public void setEtag(String etag) {
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("setEtag etag=" + etag);}
		//#endif
        this.m_etag = etag;
    }

    public String getEtag() {
        return (m_etag);
    }

    /** Write record as a string */
    public String toString(){
        StringBuffer serializedItems = new StringBuffer();
		int ilen = m_items.size();
		RssItunesItem [] ritems = new RssItunesItem[ilen];
		m_items.copyInto(ritems);
		for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
			RssItunesItem rssItem = (RssItunesItem)ritems[itemIndex];
			serializedItems.append(rssItem.toString());
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
                updString + "|" + m_link + "|" + m_etag + "|" +
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
