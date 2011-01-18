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
 */

// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities.compatibility2;

import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.utils.compatibility4.Base64;
import com.substanceofcode.utils.MiscUtil;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.substanceofcode.testutil.logging.TestLogUtil;

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
    
    private String m_title = "";   // The RSS item title
    private String m_link  = "";   // The RSS item link
    private String m_desc  = "";   // The RSS item description
    private Date m_date = null;
    private String m_enclosure  = "";   // The RSS item enclosure
    private boolean m_unreadItem = false;
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility2.RssItem");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif

    /** Creates a new instance of RssItem */
    public RssItem(String title, String link, String desc) {
        m_title = title;
        m_link = link;
        m_desc = desc;
        m_date = null;
        m_enclosure = "";
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
    
    /** Creates a new instance of RssItem */
    public RssItem(RssItemInfo item) {
        m_title = item.getTitle(); 
        m_link = item.getLink();
        m_desc = item.getDescription();
        m_date = item.getDate();
        m_enclosure = item.getEnclosure();
        m_unreadItem = isUnreadItem();
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

    /** Serialize the object */
    public String serialize() {
        String dateString;
        if(m_date==null){
            dateString = "";
        } else {
            dateString = String.valueOf( m_date.getTime() );
        }

		String title = MiscUtil.replace(m_title, "|", "\n");
        String preData = title + "|" + m_link + "|" + dateString + "|" +
			    m_enclosure + "|" + (m_unreadItem ? "1" : "0") + "|" + m_desc;
        Base64 b64 = new Base64();
        String encodedSerializedData = null;
		try {
			encodedSerializedData = b64.encode( preData.getBytes("UTF-8") );
		} catch (UnsupportedEncodingException e) {
			encodedSerializedData = b64.encode( preData.getBytes() );
		}
		return encodedSerializedData;
	}
		
    public String unencodedSerialize() {
		throw new RuntimeException("unencodedSerialize not supported.");
	}

	/** Deserialize the object */
	public static RssItem deserialize(String data) {
			
		String title = "";
		String link = "";
		String desc = "";
		Date date = null;
		String enclosure = "";
		boolean unreadItem = false;
		RssItem item = null;

		try {
			// Base64 decode
			Base64 b64 = new Base64();
			byte[] decodedData = b64.decode(data);
			try {
				data = new String( decodedData, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				data = new String( decodedData );
			}
			
			String[] nodes = MiscUtil.split( data, "|");
			
			/* Node count should be 6:
			 * title | link | date | enclosure | unreadItem | desc
			 */
			int TITLE = 0;
			title = nodes[TITLE];
			
			int LINK = 1;
			link = nodes[LINK];
			
			int DATE = 2;
			String dateString = nodes[DATE];
			if(dateString.length()>0) {
				date = new Date(Long.parseLong(dateString));
			}        
			
			int DESC = 5;
			if (DESC < nodes.length) {
				int ENCLOSURE = 3;
				enclosure = nodes[ENCLOSURE];
				int NEWITEM = 4;
				String cunreadItem = nodes[NEWITEM];
				if (cunreadItem.equals("1")) {
					unreadItem = true;
				} else if (cunreadItem.equals("0")) {
					unreadItem = false;
				} else {
					// If we get here, then description has '|' in it.
					DESC = 3;
				}
				if (DESC != 3) {
					title = MiscUtil.replace(title, "\n", "|");
				}
			} else {
				DESC = 3;
			}
					
			// If description has '|', we need to join.
			if (DESC < (nodes.length - 1)) {
				desc = MiscUtil.join(nodes, "|", DESC);
			} else {
				desc = nodes[DESC];
			}
					
			item = new RssItem(title, link, desc, date, enclosure, unreadItem);

        } catch(Exception e) {
            System.err.println("Error while RssItem deserialize : " + e.toString());
			e.printStackTrace();
        }
        return item;
    }

    public void setUnreadItem(boolean unreadItem) {
        this.m_unreadItem = unreadItem;
    }

    public boolean isUnreadItem() {
        return (m_unreadItem);
    }

    public void setEnclosure(String enclosure) {
        this.m_enclosure = enclosure;
    }

    public String getEnclosure() {
        return (m_enclosure);
    }

	//#ifdef DTEST
	/* Compare item. */
	public boolean equals(RssItemInfo item) {
		boolean result = true;
		if (!TestLogUtil.itemEquals(this, item,
			new boolean[] {true, true, true, true, true, true},
			"compatibility2.RssItem", logger, fineLoggable, traceLoggable)) {
			result = false;
		}
		return result;
	}
	//#endif

}
//#endif
