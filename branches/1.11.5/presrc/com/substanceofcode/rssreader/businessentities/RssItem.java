//--Need to modify--#preprocess
/*
 * RssItem.java
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
 * IB 2010-04-30 1.11.5RC2 Combine classes.
 * IB 2010-04-30 1.11.5RC2 Use method to encode/decode.
 * IB 2010-07-04 1.11.5Dev6 Code cleanup.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-26 1.11.5Dev15 Use checkRead to set the m_unreadItem to the parameter RssItem's m_unreadItem if the other fields are equal.
 * IB 2010-11-26 1.11.5Dev15 Use itemEquals to compare each item for testing and logging.
 * IB 2011-01-24 1.11.5Dev16 Don't compile unneeded code for internet link version.
 * IB 2011-02-01 1.11.5Dev17 Need clone method for RSS items.
 * IB 2011-03-06 1.11.5Dev17 Combine statements.
 * IB 2011-03-06 1.11.5Dev17 Have checkRead return true if the item's unread was saved.
 * IB 2011-03-07 1.11.5Dev17 Have m_state keep track of unread items and itunes flag.
 */

// Expand to define itunes define
@DFULLVERSDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
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

//#ifdef DLOGGING
import com.substanceofcode.testutil.logging.TestLogUtil;
//#elif DTESTUI
import com.substanceofcode.testutil.console.TestLogUtil;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * RssItem class is a data store for a single item in RSS feed.
 * One item consist of title, link, description and optional date.
 *
 * @author  Tommi Laukkanen
 * @version 1.1
 */
public class RssItem
	//#ifdef DTEST
	//#ifdef DJMTEST
	implements RssItemInfo, RssItunesInfo
	//#endif
	//#endif
{
    
    protected static final char CONE = (char)1;
	// Use protected so that sub classes can access these including the
	// backward store compatibility classes.
    protected String m_title = "";   // The RSS item title
    protected String m_link  = "";   // The RSS item link
    protected String m_desc  = "";   // The RSS item description
    protected Date m_date = null;
    protected String m_enclosure  = "";   // The RSS item enclosure
    protected byte m_state = (byte)0x00;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("RssItem");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#elif DTESTUI
    private Object logger = null;
    private boolean fineLoggable = true;
    private boolean finestLoggable = true;
    private boolean traceLoggable = true;
	//#endif
    
    /** Creates a new instance of RssItem.  Used by this class and
	    RssItem and later the fields are initalized. */
    protected RssItem() {
	}

    /** Creates a new instance of RssItem */
    protected RssItem(String title, String link, String desc, Date pubDate,
			       String enclosure, byte state) {
        m_title = title;
        m_link = link;
        m_desc = desc;
        m_date = pubDate;
        m_enclosure = enclosure;
        m_state = state;
    }
    
    public RssItem(String title, String link, String desc, Date pubDate,
			       String enclosure, boolean unreadItem) {
		this(title, link, desc, pubDate, enclosure,
				(byte)(unreadItem ? 0x01 : 0x00));
    }
    
    public RssItem(RssItem item) {
		this(item.m_title, item.m_link, item.m_desc, item.m_date,
			 item.m_enclosure, item.m_state);
	}

    /** Get RSS item title */
    public String getTitle(){
        return m_title;
    }
    
    /** Get RSS item title */
    public void setTitle(String title){
        m_title = title;
    }
    
    /** Get RSS item link address */
    public String getLink(){
        return m_link;
    }
    
    /** Get RSS item link address */
    public void setLink(String link){
        m_link = link;
    }
    
    /** Get RSS item description */
    public String getDescription(){
        return m_desc;
    }
    
    /** Get RSS item description */
    public void setDescription(String description){
        m_desc = description;
    }
    
    /** Get RSS item publication date */
    public Date getDate() {
        return m_date;
    }
    
    public void setDate(Date date) {
        this.m_date = date;
    }

    /** Serialize the object
	  When we serialize we don't do anything special for itunes as the
	  store to memory will be deserialized only by the iTunes capable
	  version.
	  */
    public String unencodedSerialize() {
        String dateString;
        if(m_date==null){
            dateString = "";
        } else {
		    // We use base 16 (hex) for the date so that we can save some
			// space for toString.
            dateString = Long.toString( m_date.getTime(), 16 );
        }

		String title = m_title.replace('|', CONE);
        String preData = title + "|" + m_link + "|" + dateString + "|" +
			    m_enclosure + "|" + (int)m_state + "|" + m_desc;
		return (preData);
	}
    
    /** Serialize the object
	  this serialize does not need to know if Itunes is capable/enabled given
	  that no fields were added to make it capable/enabled
	  */
    public String serialize() {
        String encodedSerializedData = MiscUtil.encodeStr(unencodedSerialize());
		return encodedSerializedData;
	}
		
	/**
	  Initialize fields in the class from data.
	  startIndex - Starting index in nodes of RssItem
	  iTunesCapable - True if the data can support Itunes (but may not
	  				  actually have Itunes data) or may not be turned
					  on by the user.  So, the serializaion/deserialization
					  will account for iTunes fields except if not
					  enabled, the will have empty values.
					  If itunes capable we use base 16 (hex) for
					  the date so that we can save some space for
					  toString.
	  hasPipe - True if the data has a pipe in at least one item
	  nodes - (elements in an array).
	  **/
	protected void init(int startIndex, boolean iTunesCapable,
					    boolean hasPipe, String [ ] nodes) {

		try {
			/* Node count should be 6:
			 * title | link | date | enclosure | unreadItem | desc
			 */
			{
				int TITLE;
				m_title = nodes[startIndex + (TITLE = 0)];
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("currCmpRssfeeds startIndex,nodes.length,first nodes=" + startIndex + "," + nodes.length + "|" + m_title);} ;
			//#endif
			if (hasPipe) {
				if (iTunesCapable) {
					m_title = m_title.replace(CONE, '|');
				} else {
					m_title = m_title.replace('\n', '|');
				}
			}
			
			{
				int LINK;
				m_link = nodes[startIndex + (LINK = 1)];
			}
			
			{
				int DATE;
				String dateString = nodes[startIndex + (DATE = 2)];
				if(dateString.length()>0) {
					if (iTunesCapable) {
						m_date = new Date(Long.parseLong(dateString, 16));
					} else {
						m_date = new Date(Long.parseLong(dateString));
					}
				}        
			}
			
			{
				int ENCLOSURE;
				m_enclosure = nodes[startIndex + (ENCLOSURE = 3)];
			}

			{
				int STATE;
				String cunreadState = nodes[startIndex + (STATE = 4)];
				m_state = (byte)Integer.parseInt(cunreadState);
			}
					
			// If description has '|', we need to join.
			{
				int DESC;
				if ((DESC = 5) + startIndex < (nodes.length - 1)) {
					m_desc = MiscUtil.join(nodes, "|", startIndex + DESC);
				} else {
					m_desc = nodes[startIndex + DESC];
				}
			}
					
        } catch(Exception e) {
            System.err.println("Error while rssitem init : " + e.toString());
			e.printStackTrace();
        }
    }

	/** Deserialize the object **/
	public static RssItem deserialize(String encodedData) {
		try {
			// Base 64 decode
			String data = MiscUtil.decodeStr(encodedData);
			return unencodedDeserialize(data);
        } catch(Exception e) {
            System.err.println("Error while rssitem deserialize : " + e.toString());
			e.printStackTrace();
			return new RssItem();
        }
	}

	/** This is only used to deserialize data from a previous version
	  but not the initial version as that version has a bug in getting
	  the items.
	  */
	public static RssItem unencodedDeserialize(String data) {
		RssItem item = new RssItem();
		try {
			boolean hasPipe = (data.indexOf('\n') >= 0);
			String[] nodes = MiscUtil.split( data, "|");
			item.init(0, false, hasPipe, nodes);
			return item;
			
        } catch(Exception e) {
            System.err.println("Error while rssitem deserialize : " + e.toString());
			e.printStackTrace();
        }
        return item;
	}

	public boolean checkRead(RssItem item) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("checkRead item=" + item);}
		if (finestLoggable) {logger.finest("checkRead this=" + this);}
		//#endif
		if (m_title.equals(item.m_title) &&
			m_link.equals(item.m_link) &&
			m_desc.equals(item.m_desc) &&
			MiscUtil.cmpDateStr(m_date, item.m_date) ||
			m_enclosure.equals(item.m_enclosure)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("checkRead m_title,m_state,item.m_state=" + m_title + "," + m_state + "," + item.m_state);}
			//#endif
			m_state = item.m_state;
			return true;
		} else {
			return false;
		}
	}

	/* Compare item. */
	//#ifdef HAS_EQUALS
	//#ifdef DJMTEST
	public boolean equals(RssItemInfo item)
	//#else
	public boolean equals(RssItem item)
	//#endif
	{
		boolean result = true;
		if (!TestLogUtil.itemEquals(this, item,
			new boolean[] {true, true, true, true, true, true},
			"RssItem", logger, fineLoggable, traceLoggable)) {
			result = false;
		}
		return result;
	}
	//#endif

    public void setUnreadItem(boolean unreadItem) {
        this.m_state = (byte)((m_state & 0x0e) | (unreadItem ? 0x01 : 0x00));
    }

    public boolean isUnreadItem() {
        return ((m_state & 0x01) == 1);
    }

    public void setItunes(boolean itunes) {
		//#ifdef DITUNES
        this.m_state = (byte)((m_state & 0x0d) | (itunes ? 0x02 : 0x00));
		//#else
        this.m_state &= 0x0d;
		//#endif
    }

    public boolean isItunes() {
		//#ifdef DITUNES
        return ((m_state & 0x02) == 0x02);
		//#else
        return (false);
		//#endif
    }

    public String getEnclosure() {
        return (m_enclosure);
    }

    public void setEnclosure(String enclosure) {
        m_enclosure = enclosure;
    }

    /** convert the object to string */
	//#ifdef DTEST
    public String toString() {
        String preData = m_title + "|" + m_link + "|" + m_date + "|" +
			    m_enclosure + "|" + m_state + "|" + m_desc;
		return (preData);
	}
	//#endif
    
    public Object clone() {
		return new RssItem(this);
	}

}
//#endif
