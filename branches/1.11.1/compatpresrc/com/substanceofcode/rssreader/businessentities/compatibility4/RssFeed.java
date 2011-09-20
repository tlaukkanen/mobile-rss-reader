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
 * IB 2010-05-30 1.11.5RC2 Better logging.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-26 1.11.5Alpha15 Use RssItunesItemInfo for the item compares.
 * IB 2011-01-31 1.11.5Dev17 Change items to array to save on memory and for simplicity.
 * IB 2011-02-02 1.11.5Dev17 Allow optional saving of only the feed header name, user/pass, and link.
 * IB 2011-03-13 1.11.5Dev17 Have adjustFields for compatibility to change fields that are time sensitive to get compatibility compares to match.
 */

// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
//#ifdef DTESTUI
//#define HAS_EQUALS
//#endif
//#ifdef DLOGGING
//#define HAS_EQUALS
//#endif
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities.compatibility4;

import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
import com.substanceofcode.rssreader.businessentities.RssFeedInfo;
import com.substanceofcode.utils.compatibility4.Base64;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.rssreader.businesslogic.compatibility4.RssFormatParser;
import java.io.UnsupportedEncodingException;
import java.util.*;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif
//#ifdef DLOGGING
import com.substanceofcode.testutil.logging.TestLogUtil;
//#elif DTESTUI
import com.substanceofcode.testutil.console.TestLogUtil;
//#endif

/**
 * RssFeed class contains one RSS feed's properties.
 * Properties include name and URL to RSS feed.
 *
 * @author Tommi Laukkanen
 */
public class RssFeed implements RssFeedInfo {

	final protected static char CONE = (char)1;
	final private static char [] CBONE = {CONE};
	final public static String STR_ONE = new String(CBONE);
	final protected static char [] CBTWO = {(char)2};
	final public static String STR_TWO = new String(CBTWO);
	final public static int ITUNES_ITEMS = 8;
	final public static int MODIFY_ITEMS = 9;
	final public static int MAX_NAME_LEN = 100;
	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("compatibility4.RssFeed");
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#elif DTESTUI
    private Object logger = null;
    private boolean fineLoggable = true;
    private boolean finestLoggable = true;
	//#endif
	protected String m_url  = "";
	protected String m_name = "";
	protected String m_username = "";
	protected String m_password = "";
	protected Date m_upddate = null;
	protected byte m_upddateTz = (byte)-1;
	protected Date m_date = null;
	protected String m_errDate = "";
	protected String m_link = "";   // The RSS feed link
	private String m_firstLink = "";
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
			String upddate,
			String link,
			Date date,
			String etag) {
		m_name = name;
		m_url = url;
		m_username = username;
		m_password = password;
		m_upddateTz = RssFormatParser.GMT;
		//#ifdef DITUNES
		m_link = link;
		m_date = date;
		//#endif
		m_etag = etag;
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
		this.m_etag = feed.getEtag();
		this.m_items = new Vector();
  		int ilen = feed.getVecItems().size();
  		RssItem [] rItems = new RssItem[ilen];
  		if (ilen > 0) {
  			feed.getVecItems().copyInto(rItems);
  		}
  		for (int ic = 0; ic < ilen; ic++) {
  			m_items.addElement(new RssItunesItem(rItems[ic]));
		}
		if (feed instanceof RssFeed) {
			m_firstLink = ((RssFeed)feed).m_firstLink;
			m_errDate = ((RssFeed)feed).m_errDate;
		}
	}

	/** Creates a new instance of RSSBookmark with record store string 
	  firstSettings  - True if the data was from the settings which were
	  compatible with the first version and several after.
	 **/
	public RssFeed(boolean firstSettings, boolean encoded, String storeString){

		try {

			String[] nodes = MiscUtil.split( storeString, "|" );
			init(firstSettings, 0, false, false, false, encoded, nodes);
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
			if(modifyCapable) {
				int ETAG = 7;
				String etagString = nodes[startIndex + ETAG];
				if (etagString.length() > 0) {
					m_etag = etagString;
				}
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
			if (firstSettings || !modifyCapable) {
				// Given the bugs with the first settings, we do not
				// retrieve the items so that we can restore them
				// without the bugs.
				// Also, do not try to modify previous items as it
				// unnecessarily complicates the code for a corner case
				// of upgrading.
				return;
			}
			String itemArrayData = nodes[ startIndex + ITEMS ];
			//#ifdef DLOGGING
			if (traceLoggable) {logger.trace("init m_upddate,m_date,m_etag,first item=" + m_upddate + "," + m_date + "," + m_etag + "," + nodes[ startIndex + ITEMS ]);}
			//#endif

			// Deserialize itemss
			String[] serializedItems = MiscUtil.split(itemArrayData, ".");

			for(int itemIndex=0; itemIndex<serializedItems.length; itemIndex++) {
				String serializedItem = serializedItems[ itemIndex ];
				if(serializedItem.length()>0) {
					RssItemInfo rssItem;
					if (iTunesCapable) {
						if (encoded) {
							rssItem = (RssItemInfo)RssItunesItem.deserialize( 
									serializedItem );
						} else {
							rssItem = (RssItemInfo)RssItunesItem.unencodedDeserialize(
									serializedItem );
						}
					} else {
						rssItem = (RssItemInfo)RssItem.deserialize( serializedItem );
					}
					if (rssItem != null) {
						m_items.addElement( rssItem );
					}
				}
			}

		} catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("init error", e);
			//#endif
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

    public String getStoreString(final boolean saveHdr,
			final boolean serializeItems, final boolean encoded) {
		return getStoreString(serializeItems, encoded);
	}

	/** Return record store string for feed only.  This excludes items which
	  are put into store string by RssItunesFeed.  */
	public String getStoreString(boolean serializeItems, boolean encoded){
		StringBuffer serializedItems = new StringBuffer();
		if( serializeItems ) {
			int ilen = m_items.size();
			RssItunesItem [] ritems = new RssItunesItem[ilen];
			if (ilen > 0) {
				m_items.copyInto(ritems);
			}
			for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
				RssItunesItemInfo rssItem = (RssItunesItemInfo)ritems[itemIndex];
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

	/** Compare feed to an existing feed.  **/
	public boolean equals(RssFeedInfo feed)
	{
		if (feed == null) { return false;}
		boolean result = true;
		int flen = feed.getItems().length;
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
			if (!TestLogUtil.fieldEquals(feed.getEtag(), m_etag,
						"m_etag", logger, fineLoggable)) {
				result = false;
			}
			if (!TestLogUtil.fieldEquals(flen, ilen,
				"m_items.size() ilen", logger, fineLoggable)) {
				result = false;
			}
			RssItunesItemInfo [] ritems = new RssItunesItemInfo[ilen];
			if (ilen > 0) {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("equals ritems[0]=" + m_items.elementAt(0).getClass().getName());}  
				//#endif
				m_items.copyInto(ritems);
			}
			RssItemInfo [] fitems = feed.getItems();
			for (int ic = 0; (ic < ilen) && (ic < flen); ic++) {
				if (!TestLogUtil.fieldEquals(fitems[ic], ritems[ic],
							"ritems[" + ic + "]", logger, fineLoggable)) {
					result = false;
				}
			}
		} catch (Throwable e) {
			result = false;
			//#ifdef DLOGGING
			logger.severe("equals unequal error flen,ilen=" + flen + "," + ilen, e);
			logger.severe("equals error feed.m_items,m_items=" + flen + "," + ((flen == 0) ? "n/a" : feed.getItems()[0].toString()) + "," + ilen + "," + ((ilen == 0) ? "n/a" : m_items.elementAt(0)) , e);
			//#endif
		}
		return result;
	}

    /** Return RSS feed items */
	public RssItemInfo[] getItems() {
		RssItemInfo[] ritems = MiscUtil.getVecrItemf(m_items);
        return ritems;
    }
    
    /** Set items */
	public void setItems(RssItemInfo[] items) {
        m_items = MiscUtil.convVec(items);
    }
    
	/** Return RSS feed items */
	public Vector getVecItems() {
		return m_items;
	}

	/** Set items */
	public void setVecItems(Vector vitems) {
		m_items = vitems;
	}

	public boolean adjustFields() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("adjustFields m_firstLink,m_link=" + m_firstLink + "," + m_link);}
		//#endif
		if (m_firstLink.length() > 0) {
			m_link = m_firstLink;
			return true;
		} else {
			return false;
		}
	}

	public void setUpddate(String upddate) {
		m_date = null;
	}

    public String getUpddate() {
		if (m_upddate == null) {
			return "";
		} else {
			return m_upddate.toString();
		}
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
		if (ilen > 0) {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("equals ritems[0]=" + m_items.elementAt(0).getClass().getName());}  
			//#endif
			m_items.copyInto(ritems);
		}
		for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
			RssItunesItemInfo rssItem = (RssItunesItemInfo)ritems[itemIndex];
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

    public void setErrDate(String m_errDate) {
        this.m_errDate = m_errDate;
    }

    public String getErrDate() {
        return (m_errDate);
    }

    public void setFirstLink(String firstLink) {
        this.m_firstLink = firstLink;
    }

    public String getFirstLink() {
        return (m_firstLink);
    }

	public Object clone() {
		return new RssFeed(this);
	}

}
//#endif
