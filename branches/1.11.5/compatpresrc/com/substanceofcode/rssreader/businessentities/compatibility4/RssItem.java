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
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-26 1.11.5Dev15 Use itemEquals to compare each item for testing and logging.
 * IB 2011-02-01 1.11.5Dev17 Need clone method for RSS items.
 */

// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities.compatibility4;

import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.utils.compatibility4.Base64;
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
public class RssItem implements RssItemInfo {
    
    protected static final char CONE = (char)1;
	// Use protected so that sub classes can access these including the
	// backward store compatibility classes.
    protected String m_title = "";   // The RSS item title
    protected String m_link  = "";   // The RSS item link
    protected String m_desc  = "";   // The RSS item description
    protected Date m_date = null;
	protected String m_errDate = "";
    protected String m_enclosure  = "";   // The RSS item enclosure
    protected boolean m_unreadItem = false;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.RssItem");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#elif DTESTUI
    private Object logger = null;
    private boolean fineLoggable = true;
    private boolean finestLoggable = true;
    private boolean traceLoggable = false;
	//#endif
    
    /** Creates a new instance of RssItem.  Used by this class and
	    RssItem and later the fields are initalized. */
    protected RssItem() {
	}

    /** Creates a new instance of RssItem */
    public RssItem(String title, String link, String desc, Date pubDate,
			       String enclosure, boolean unreadItem) {
        m_title = title;
        m_link = link;
        m_desc = desc;
        m_date = pubDate;
        m_enclosure = enclosure;
        m_unreadItem = unreadItem;
    }
    
    public RssItem(RssItemInfo item) {
		this(item.getTitle(), item.getLink(), item.getDescription(),
				item.getDate(), item.getEnclosure(), item.isUnreadItem());
		if (item instanceof RssItem) {
			m_errDate = ((RssItem)item).m_errDate;
		}
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
			    m_enclosure + "|" + (m_unreadItem ? "1" : "0") + "|" + m_desc;
		return (preData);
	}
    
    /** Serialize the object
	  this serialize does not need to know if Itunes is capable/enabled given
	  that no fields were added to make it capable/enabled
	  */
    public String serialize() {
        String preData = unencodedSerialize();
        Base64 b64 = new Base64();
        String encodedSerializedData = null;
		try {
			encodedSerializedData = b64.encode( preData.getBytes("UTF-8") );
		} catch (UnsupportedEncodingException e) {
			encodedSerializedData = b64.encode( preData.getBytes() );
		}
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
			int TITLE = 0;
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("startIndex,nodes.length,first nodes=" + startIndex + "," + nodes.length + "|" + nodes[ startIndex + TITLE ]);} ;
			//#endif
			m_title = nodes[startIndex + TITLE];
			if (hasPipe) {
				if (iTunesCapable) {
					m_title = m_title.replace(CONE, '|');
				} else {
					m_title = m_title.replace('\n', '|');
				}
			}
			
			int LINK = 1;
			m_link = nodes[startIndex + LINK];
			
			int DATE = 2;
			String dateString = nodes[startIndex + DATE];
			if(dateString.length()>0) {
				if (iTunesCapable) {
					m_date = new Date(Long.parseLong(dateString, 16));
				} else {
					m_date = new Date(Long.parseLong(dateString));
				}
			} else {
				m_date = null;
			}        
			
			int ENCLOSURE = 3;
			m_enclosure = nodes[startIndex + ENCLOSURE];

			int NEWITEM = 4;
			String cunreadItem = nodes[startIndex + NEWITEM];
			m_unreadItem = (cunreadItem.equals("1"));
					
			// If description has '|', we need to join.
			int DESC = 5;
			if (DESC + startIndex < (nodes.length - 1)) {
				m_desc = MiscUtil.join(nodes, "|", startIndex + DESC);
			} else {
				m_desc = nodes[startIndex + DESC];
			}
					
        } catch(Exception e) {
            System.err.println("Error while RssItem init : " + e.toString());
			e.printStackTrace();
        }
    }

	/** Deserialize the object **/
	public static RssItem deserialize(String encodedData) {
		try {
			// Base64 decode
			Base64 b64 = new Base64();
			byte[] decodedData = b64.decode(encodedData);
			String data;
			try {
				data = new String( decodedData, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				data = new String( decodedData );
			}
			return unencodedDeserialize(data);
        } catch(Exception e) {
            System.err.println("Error while RssItem deserialize : " + e.toString());
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
            System.err.println("Error while RssItem deserialize : " + e.toString());
			e.printStackTrace();
        }
        return item;
	}

	/* Compare item. */
	public boolean equals(RssItemInfo item) {
		boolean result = true;
		if (!TestLogUtil.itemEquals(this, item,
			new boolean[] {true, true, true, true, true, true},
			"compatibility4.RssItem", logger, fineLoggable, traceLoggable)) {
			result = false;
		}
		return result;
	}

    public void setUnreadItem(boolean unreadItem) {
        this.m_unreadItem = unreadItem;
    }

    public boolean isUnreadItem() {
        return (m_unreadItem);
    }

    public String getEnclosure() {
        return (m_enclosure);
    }

    public void setEnclosure(String enclosure) {
        m_enclosure = enclosure;
    }

    /** convert the object to string */
    public String toString() {
        String preData = m_title + "|" + m_link + "|" + m_date + "|" +
			    m_enclosure + "|" + m_unreadItem + "|" + m_desc;
		return (preData);
	}
    
    public void setErrDate(String errDate) {
        this.m_errDate = errDate;
    }

    public String getErrDate() {
        return (m_errDate);
    }

    public Object clone() {
		return new RssItem(this);
	}

}
//#endif
