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
 * IB 2010-03-12 1.11.5RC2 Use string for last modified date (m_upddate) to prevent problems from time zone differences from causing problems with deterining if a feed was updated.
 * IB 2010-03-12 1.11.5RC2 If xml url is the same as html url, use xml url for html url.
 * IB 2010-03-12 1.11.5RC2 Combine classes to save space.
 * IB 2010-03-12 1.11.5RC2 Use convenience method for encoding/decoding.
 * IB 2010-03-12 1.11.5RC2 Use fieldEquals to compare items.
 * IB 2010-03-12 1.11.5RC2 Better logging.
 * IB 2010-05-25 1.11.5RC2 Don't deserialize items if not iTunesCapable.
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-07-29 1.11.5Dev8 Don't allocate space for m_date and m_link if not smartphone.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-26 1.11.5Dev15 Use checkPresRead to set the m_unreadItem to the parameter RssItem's m_unreadItem if the other fields are equal.
 * IB 2010-11-26 1.11.5Dev15 Use checkPresRead to set the username/password to the parameter's RssFeed.
 * IB 2010-11-26 1.11.5Dev15 Use setItemDatesNull in compatibility testing to null the feed's date if the comparison feed's date is null.  This worksaround a fix where the new version takes the lastBuildDate if pubdate is null.
 * IB 2011-01-24 1.11.5Dev16 Don't compile unneeded code for internet link version.
 * IB 2011-01-31 1.11.5Dev17 RssFeed clone/constructor needs to clone/instantiate new items instead of re-using the parameter's items.
 * IB 2011-01-31 1.11.5Dev17 More logging.
 * IB 2011-01-31 1.11.5Dev17 Have checkRead return true if items are the same.  Use this to reduce cross check of items being the same.
 * IB 2011-01-31 1.11.5Dev17 Change items to array to save on memory and for simplicity.
 * IB 2011-01-31 1.11.5Dev17 Allow optional saving of only the feed header name, user/pass, and link.
 * IB 2011-02-01 1.11.5Dev17 Need clone method for RSS feeds.
 * IB 2011-03-06 1.11.5Dev17 Combine statements.
 * IB 2011-03-06 1.11.5Dev17 Use a StringBuffer to serialize for minor performance improvement.
 * IB 2011-03-06 1.11.5Dev17 Specify imports without '*'.
 * IB 2011-03-06 1.11.5Dev17 Use RssItem instead of RssItunesItem to allow future difference in the two.
*/

// Expand to define itunes define
//#define DFULLVERS
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test define
//#define DNOCOMPAT
// Expand to define test define
//#define DNOTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define MIDP define
//#define DMIDP20
// Expand to define itunes define
//#define DNOITUNES
// Expand to define logging define
//#define DNOLOGGING
// Expand to define JMUnit test define
//#define DNOJMTEST
//#ifdef DTESTUI
//#define HAS_EQUALS
//#endif
//#ifdef DLOGGING
//#define HAS_EQUALS
//#endif
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.MiscUtil;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Vector;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif
//#ifdef DLOGGING
//@import com.substanceofcode.testutil.logging.TestLogUtil;
//#elif DTESTUI
//@import com.substanceofcode.testutil.console.TestLogUtil;
//#endif

/**
 * RssFeed class contains one RSS feed's properties.
 * Properties include name and URL to RSS feed.
 *
 * @author Tommi Laukkanen
 */
public class RssFeed
	//#ifdef DTEST
	//#ifdef DJMTEST
//@	implements RssFeedInfo
	//#endif
	//#endif
{

	final protected static char CONE = (char)1;
	final private static char [] CBONE = {CONE};
	final public static String STR_ONE = new String(CBONE);
    final protected static char CTWO = (char)2;
    final private static char [] CATWO = {CTWO};
	final public static String STR_TWO = new String(CATWO);
	final public static int ITUNES_ITEMS = 8;
	final public static int MODIFY_ITEMS = 9;
	final public static int MAX_NAME_LEN = 100;
	//#ifdef DLOGGING
//@	private Logger logger = Logger.getLogger("RssFeed");
//@	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@	private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#elif DTESTUI
//@    private Object logger = null;
//@    private boolean fineLoggable = true;
//@    private boolean finestLoggable = true;
	//#endif
	protected String m_url  = "";
	protected String m_name = "";
	protected String m_username = "";
	protected String m_password = "";
	protected String m_upddate = "";
	//#ifdef DITUNES
//@	protected Date m_date = null;
//@	protected String m_link = "";   // The RSS feed link
	//#endif
	protected String m_etag = ""; // The RSS feed etag

	protected RssItem[] m_items = new RssItem[0];  // The RSS item array

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
		m_upddate = upddate;
		//#ifdef DITUNES
//@		m_link = link;
//@		m_date = date;
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
//@		this.m_link = feed.m_link;
//@		this.m_date = feed.m_date;
		//#endif
		this.m_etag = feed.m_etag;
		int ilen = feed.m_items.length;
		RssItem[] cItems = new RssItem[ilen];
		this.m_items = cItems;
		RssItem[] rItems = feed.m_items;
		for (int ic = 0; ic < ilen; ic++) {
			RssItem rssItem = rItems[ic];
			if (rssItem instanceof RssItunesItem) {
				cItems[ic] = (RssItem)((RssItunesItem)rssItem).clone();
			} else {
				cItems[ic] = (RssItem)rssItem.clone();
			}
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
			boolean modifyCapable,
			boolean hasPipe, boolean encoded,
			String [ ] nodes) {

		int gindex = 0;
		int itemIndex=0;
		RssItem[] gitems = null;
		try {

			/* Node count should be 9
			 * name | url | username | password | upddate | link | date |
			 * etag | place holder for time zone. | items 
			 */
			{
				int NAME;
				m_name = nodes[ startIndex + (NAME = 0) ];
			}
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("init firstSettings,startIndex,iTunesCapable,modifyCapable,nodes.length,m_name=" + firstSettings + "," + startIndex + "," + iTunesCapable + "," + modifyCapable + "," + nodes.length + "|" + m_name);}
			//#endif

			{
				int URL;
				m_url = nodes[ startIndex + (URL = 1) ];
			}

			{
				int USERNAME;
				m_username = nodes[ startIndex + (USERNAME = 2) ];
			}
			if (iTunesCapable && hasPipe) {
				m_username = m_username.replace(CONE, '|');
			}

			{
				int PASSWORD;
				m_password = nodes[ startIndex + (PASSWORD = 3) ];
			}
			if (iTunesCapable) {
				if (m_password.length() > 0) {
					m_password = MiscUtil.decodeStr(m_password);
				}
				if (hasPipe) {
					m_password = m_password.replace(CONE, '|');
				}
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("m_password=" + m_password);}
				//#endif
			}

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
			{
				int UPDDATE;
				m_upddate = nodes[startIndex + (UPDDATE = 4)];
			}
			// In this version, we are adding ETag, so we cannot use
			// m_upddate from before because they must be used together.
			if (hasPipe && (m_upddate.length()>0) && iTunesCapable &&
					modifyCapable) {
				m_upddate = m_upddate.replace(CONE, '|');
			}
			if(modifyCapable) {
				{
					int ETAG;
					m_etag = nodes[startIndex + (ETAG = 7)];
				}
				if (hasPipe) {
					m_etag = m_etag.replace(CONE, '|');
				}
			}
			if (iTunesCapable && hasPipe) {
				m_name = m_name.replace(CONE, '|');
			} else {
				if (!iTunesCapable) {
					// Dencode for better UTF-8 and to allow '|' in the name.
					// For iTunesCapable, replace | with (char)1
					m_name = MiscUtil.decodeStr(m_name);
				}
			}
			if (iTunesCapable) {
				//#ifdef DITUNES
//@				{
//@					int LINK = 5;
//@					m_link = nodes[startIndex + (LINK = 5)];
//@				}
//@				{
//@					int DATE;
//@					String fdateString = nodes[startIndex + (DATE = 6)];
//@					if (fdateString.length() > 0) {
//@						m_date = new Date(Long.parseLong(fdateString, 16));
//@					}
//@				}
				//#endif
			}
			if (firstSettings || !modifyCapable) {
				// Given the bugs with the first settings, we do not
				// retrieve the items so that we can restore them
				// without the bugs.
				// Also, do not try to modify previous items as it
				// unnecessarily complicates the code for a corner case
				// of upgrading.
				// Don't need to check for iTunesCapable as it's superceeded
				// by modifyCapable.
				//#ifdef DLOGGING
				//#ifdef DITUNES
//@				if (traceLoggable) {logger.trace("init m_upddate,m_date,m_link,m_etag=" + m_upddate + "," + m_date + "," + m_link + "," + m_etag);}
				//#else
//@				if (traceLoggable) {logger.trace("init m_upddate,m_date,m_link,m_etag=" + m_upddate + ",,," + m_etag);}
				//#endif
				//#endif
				return;
			}
			String itemArrayData = nodes[ startIndex + ITEMS ];
			//#ifdef DLOGGING
			//#ifdef DITUNES
//@			if (traceLoggable) {logger.trace("init m_url,m_name,m_username,m_upddate,m_date,m_link,m_etag,m_password,first item=" + m_url + "," + m_name + "," + m_username + "," + m_password + "," + m_upddate + "," + m_date + "," + m_link + "," + m_etag + "," + nodes[ startIndex + ITEMS ]);}
			//#else
//@			if (traceLoggable) {logger.trace("init m_url,m_name,m_username,m_upddate,m_date,m_link,m_etag,m_password,first item=" + m_url + "," + m_name + "," + m_username + "," + m_password + "," + m_upddate + ",,," + m_etag + "," + nodes[ startIndex + ITEMS ]);}
			//#endif
			//#endif

			// Deserialize itemss
			String[] serializedItems = MiscUtil.split(itemArrayData,
						(encoded ? '.' : CTWO));
			//#ifdef DLOGGING
//@			if (traceLoggable) {logger.trace("init serializedItems.length=" + serializedItems.length);}
			//#endif
			gitems = new RssItem[serializedItems.length];
			final int silen = serializedItems.length;
			for(; itemIndex<silen; itemIndex++) {
				String serializedItem = serializedItems[ itemIndex ];
				if(serializedItem.length()>0) {
					RssItem rssItem;
					if (encoded) {
						rssItem = RssItunesItem.deserialize( 
								serializedItem );
					} else {
						rssItem = RssItunesItem.unencodedDeserialize(
								serializedItem );
					}
					if (rssItem != null) {
						gitems[gindex++] = rssItem;
					}
				}
			}

		} catch(Exception e) {
			//#ifdef DLOGGING
//@			logger.severe("init error", e);
			//#endif
			System.err.println("Error while rssfeed initialization : " + e.toString());
			e.printStackTrace();
		} finally {
			if (gindex == itemIndex) {
				if (gitems == null) {
					m_items = new RssItem[0];
				} else {
					m_items = gitems;
				}
			} else {
				m_items = new RssItem[gindex];
				System.arraycopy(gitems, 0, m_items, 0, gindex);
			}
		}
	}

	/** Return bookmark's name */
	public String getName(){
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
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
    public String getStoreString(final boolean saveHdr,
			final boolean serializeItems, final boolean encoded) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("getStoreString saveHdr,serializeItems,encoded,m_items.length=" + saveHdr + "," + serializeItems + "," + encoded + "," + m_items.length);}
		//#endif
		StringBuffer serializedItems = new StringBuffer();
		if( serializeItems ) {
			int ilen = m_items.length;
			RssItem [] ritems = m_items;
			for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
				RssItem rssItem = ritems[itemIndex];
				if (encoded) {
					if (rssItem instanceof RssItunesItem) {
						serializedItems.append(
								((RssItunesItem)rssItem).serialize());
					} else {
						serializedItems.append(rssItem.serialize());
					}
					serializedItems.append(".");
				} else {
					if (rssItem instanceof RssItunesItem) {
						serializedItems.append(
								((RssItunesItem)rssItem).unencodedSerialize());
					} else {
						serializedItems.append(rssItem.unencodedSerialize());
					}
					serializedItems.append(CTWO);
				}
			}
		}
		String url = m_url.replace('|', CONE);
		String name = m_name.replace('|', CONE);
		String username = m_username.replace('|' , CONE);
		String password = m_password.replace('|' , CONE);
		//#ifdef DITUNES
//@		String link = m_link.replace('|' , CONE);
		//#else
		String link = "";
		//#endif
		String encodedPassword;
		// Encode password to make reading password difficult
		encodedPassword = MiscUtil.encodeStr( password );
		String updString = saveHdr ? m_upddate.replace('|' , CONE) : "";
		// Leave space for former time zone. 
		// We'll change to image? in next release.
	    StringBuffer storeString = new StringBuffer(name).append('|').append(
			url).append('|').append(username).append('|').append(
			encodedPassword).append('|').append(updString).append('|').append(
			link);
		if (saveHdr) {
			//#ifdef DITUNES
//@			String dateString;
//@			if(m_date==null){
//@				dateString = "";
//@			} else {
//@				// We use base 16 (hex) for the date so that we can save some
//@				// space for toString.
//@				dateString = Long.toString( m_date.getTime(), 16 );
//@			}
			//#else
			String dateString = "";
			//#endif
			String etag = m_etag.replace('|' , CONE);
			storeString.append('|').append(dateString).append('|').append(
				etag).append("||");
		} else {
			storeString.append("||||");
		}
		storeString.append(serializedItems);
		return storeString.toString();

	}

	//#ifdef HAS_EQUALS
	//#ifdef DJMTEST
//@	/** Compare feed to an existing feed.  **/
//@	public boolean equals(RssFeedInfo feed)
	//#else
//@	public boolean equals(RssFeed feed)
	//#endif
//@	{
//@		if (feed == null) { return false;}
//@		boolean result = true;
//@		int flen = feed.getItems().length;
//@		int ilen = m_items.length;
//@		try {
//@			if (!TestLogUtil.fieldEquals(feed.getUrl(), m_url,
//@						"m_url", logger, fineLoggable)) {
//@				result = false;
//@			}
//@			if (!TestLogUtil.fieldEquals(feed.getName(), m_name,
//@						"m_name", logger, fineLoggable)) {
//@				result = false;
//@			}
//@			if (!TestLogUtil.fieldEquals(feed.getUsername(), m_username,
//@						"m_username", logger, fineLoggable)) {
//@				result = false;
//@			}
//@			if (!TestLogUtil.fieldEquals(feed.getPassword(), m_password,
//@						"m_password", logger, fineLoggable)) {
//@				result = false;
//@			}
//@			if (!TestLogUtil.fieldEquals(feed.getUpddate(), m_upddate,
//@						"m_upddate", logger, fineLoggable)) {
//@				result = false;
//@			}
			//#ifdef DITUNES
//@			if (!TestLogUtil.fieldEquals(feed.getDate(), m_date,
//@						"m_date", logger, fineLoggable)) {
//@				result = false;
//@			}
//@			if (!TestLogUtil.fieldEquals(feed.getLink(), m_link,
//@						"m_link", logger, fineLoggable)) {
//@				result = false;
//@			}
			//#endif
//@			if (!TestLogUtil.fieldEquals(feed.getEtag(), m_etag,
//@						"m_etag", logger, fineLoggable)) {
//@				result = false;
//@			}
//@			if (!TestLogUtil.fieldEquals(flen, ilen,
//@				"m_items.length ilen", logger, fineLoggable)) {
//@				result = false;
//@			}
//@			RssItemInfo[] ritems = m_items;
//@			RssItemInfo[] fitems = feed.getItems();
//@			for (int ic = 0; (ic < ilen) && (ic < flen); ic++) {
//@				if (!TestLogUtil.fieldEquals(fitems[ic], ritems[ic],
//@							"ritems[" + ic + "]", logger, fineLoggable)) {
//@					result = false;
//@				}
//@			}
//@		} catch (Throwable e) {
//@			result = false;
			//#ifdef DLOGGING
//@			logger.severe("equals error feed.m_items,m_items=" + flen + "," + ((flen == 0) ? "n/a" : (feed.getItems()[0]).toString()) + "," + ilen + "," + ((ilen == 0) ? "n/a" : m_items[0].toString()) , e);
			//#endif
//@		}
//@		return result;
//@	}
	//#endif

	//#ifdef DCOMPATIBILITY
//@	/** Compare feed to an existing feed.  **/
//@	public boolean setItemDatesNull(RssItunesFeedInfo feed) {
//@		boolean result = false;
		//#ifdef DLOGGING
//@		if (traceLoggable) {logger.trace("setItemDatesNull feed.getDate(),m_date=" + feed.getDate() + "," + m_date);}
		//#endif
//@		if ((feed.getDate() == null) && (m_date != null)) {
//@			m_date = null;
//@			result = true;
//@		}
//@		int ilen = m_items.length;
//@		RssItem[] ritems = m_items;
//@		int flen = feed.getItems().length;
//@		RssItemInfo [] fitems = feed.getItems();
//@		for (int ic = 0; (ic < ilen) && (ic < flen); ic++) {
			//#ifdef DLOGGING
//@			if (traceLoggable) {logger.trace("setItemDatesNull ic,fitems[ic].getDate(),ritems[ic].m_date=" + ic + "," + fitems[ic].getDate() + "," + ritems[ic].m_date);}
			//#endif
//@			if ((fitems[ic].getDate() == null) &&
//@				(ritems[ic].m_date != null)) {
//@				ritems[ic].m_date = null;
//@				result = true;
//@			}
//@		}
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("result=" + result);}
		//#endif
//@		return result;
//@	}
//@
//@	public boolean adjustFields() { return false; }
//@
	//#endif

	/** Return RSS feed items */
	//#ifdef DTEST
//@	public RssItemInfo[] getItems()
	//#else
	public RssItem[] getItems()
	//#endif
	{
		return m_items;
	}

	/** Set items */
	//#ifdef DTEST
//@	public void setItems(RssItemInfo[] items) 
	//#else
	public void setItems(RssItem[] items) 
	//#endif
	{
		//#ifdef DTEST
//@		m_items = (RssItem[])items;
		//#else
		m_items = items;
		//#endif
	}

	//#ifdef DTEST
//@	/** Return RSS feed items */
//@	public Vector getVecItems() {
//@		return MiscUtil.convVec(m_items);
//@	}
	//#endif

	/** Set items */
	public void setVecItems(Vector vtems) {
		m_items = new RssItem[vtems.size()];
		vtems.copyInto(m_items);
	}

	public void setUpddate(String upddate) {
		//#ifdef DLOGGING
//@		if (traceLoggable) {logger.trace("setUpddate upddate=" + upddate);}
		//#endif
		this.m_upddate = upddate;
	}

	public String getUpddate() {
		return (m_upddate);
	}

	public void setEtag(String etag) {
		//#ifdef DLOGGING
//@		if (traceLoggable) {logger.trace("setEtag etag=" + etag);}
		//#endif
		this.m_etag = etag;
	}

	public String getEtag() {
		return (m_etag);
	}

	/** Write record as a string */
	//#ifdef DTEST
//@	public String toString() {
//@		StringBuffer serializedItems = new StringBuffer();
//@		int ilen = m_items.length;
//@		RssItem [] ritems = m_items;
//@		for(int itemIndex=0; itemIndex<ilen;itemIndex++) {
//@			RssItem rssItem = ritems[itemIndex];
//@			if (rssItem instanceof RssItunesItem) {
//@				serializedItems.append(((RssItunesItem)rssItem).toString());
//@			} else {
//@				serializedItems.append(rssItem.toString());
//@			}
//@			serializedItems.append(".");
//@		}
		//#ifdef DITUNES
//@		String dateString;
//@		if(m_date==null){
//@			dateString = "";
//@		} else {
//@			// We use base 16 (hex) for the date so that we can save some
//@			// space for toString.
//@			dateString = Long.toString( m_date.getTime(), 16 );
//@		}
		//#else
//@		String dateString = "";
		//#endif
//@		String storeString = m_name + "|" + m_url + "|" + m_username + "|" +
//@			m_password + "|" +
//@			m_upddate + "|" +
			//#ifdef DITUNES
//@			m_link +
			//#endif
//@			"|" +
//@			m_etag + "|" +
//@			dateString + "|" + serializedItems.toString();
//@		return storeString;
//@
//@	}
	//#endif

	public void setLink(String link) {
		//#ifdef DITUNES
//@		this.m_link = link.equals(m_url) ? m_url : link;
		//#endif
	}

	public String getLink() {
		//#ifdef DITUNES
//@		return (m_link);
		//#else
		return "";
		//#endif
	}

	public void setDate(Date date) {
		//#ifdef DITUNES
//@		this.m_date = date;
		//#endif
	}

	public Date getDate() {
		//#ifdef DITUNES
//@		return (m_date);
		//#else
		return null;
		//#endif
	}

	public void checkPresRead(boolean modFeed, RssFeed feed) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("checkPresRead modFeed,feed=" + modFeed + "," + feed);}
//@		if (finestLoggable) {logger.finest("checkPresRead this=" + this);}
		//#endif
		if (feed.getUsername().length() > 0) {
			if (m_username.length() == 0) {
				m_username = feed.getUsername();
				m_password = feed.getPassword();
			}
		}
		if (modFeed) {
			RssItem[] ritems = feed.m_items;
			final int olen = feed.getItems().length;
			if (olen > 0) {
				final int clen = m_items.length;
				boolean[] checked = new boolean[clen];
				RssItem[] citems = m_items;
				for (int ic = 0; ic < clen; ic++) {
					RssItem citem = citems[ic];
					for (int jc = 0; jc < olen; jc++) {
						if (citem instanceof RssItunesItem) {
							if (!checked[jc] &&
									((RssItunesItem)citem).checkRead(
										ritems[jc])) {
								checked[jc] = true;
							}
						} else {
							if (!checked[jc] && citem.checkRead(ritems[jc])) {
								checked[jc] = true;
							}
						}
					}
				}
			}
		}
	}

	public Object clone() {
		return new RssFeed(this);
	}

}
//#endif
