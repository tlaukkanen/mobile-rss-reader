/*
 * CompatibilityRssItem2.java
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
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.Base64;
import com.substanceofcode.utils.StringUtil;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.substanceofcode.testutil.logging.TestLogUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * CompatibilityRssItem2 class is a data store for a single item in RSS feed.
 * One item consist of title, link, description and optional date.
 *
 * @author  Tommi Laukkanen
 * @version 1.1
 */
public class CompatibilityRssItem2 implements RssItemInfo {
    
    private String m_title = "";   // The RSS item title
    private String m_link  = "";   // The RSS item link
    private String m_desc  = "";   // The RSS item description
    private Date m_date = null;
    private String m_enclosure  = "";   // The RSS item enclosure
    private boolean m_unreadItem = false;
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("CompatibilityRssItem2");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
	//#endif

    /** Creates a new instance of CompatibilityRssItem2 */
    public CompatibilityRssItem2(String title, String link, String desc) {
        m_title = title;
        m_link = link;
        m_desc = desc;
        m_date = null;
        m_enclosure = "";
    }
    
    /** Creates a new instance of CompatibilityRssItem2 */
    public CompatibilityRssItem2(String title, String link, String desc, Date pubDate,
			       String enclosure, boolean unreadItem) {
        m_title = title;
        m_link = link;
        m_desc = desc;
        m_date = pubDate;
        m_enclosure = enclosure;
        m_unreadItem = unreadItem;
    }
    
    /** Creates a new instance of CompatibilityRssItem2 */
    public CompatibilityRssItem2(RssItemInfo item) {
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

		String title = StringUtil.replace(m_title, "|", "\n");
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
	public static CompatibilityRssItem2 deserialize(String data) {
			
		String title = "";
		String link = "";
		String desc = "";
		Date date = null;
		String enclosure = "";
		boolean unreadItem = false;
		CompatibilityRssItem2 item = null;

		try {
			// Base64 decode
			Base64 b64 = new Base64();
			byte[] decodedData = b64.decode(data);
			try {
				data = new String( decodedData, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				data = new String( decodedData );
			}
			
			String[] nodes = StringUtil.split( data, "|");
			
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
					title = StringUtil.replace(title, "\n", "|");
				}
			} else {
				DESC = 3;
			}
					
			// If description has '|', we need to join.
			if (DESC < (nodes.length - 1)) {
				desc = StringUtil.join(nodes, "|", DESC);
			} else {
				desc = nodes[DESC];
			}
					
			item = new CompatibilityRssItem2(title, link, desc, date, enclosure, unreadItem);

        } catch(Exception e) {
            System.err.println("Error while CompatibilityRssItem2 deserialize : " + e.toString());
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
		if (!TestLogUtil.fieldEquals(item.getTitle(), m_title,
			"m_title", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getLink(), m_link,
			"m_link", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getDescription(), m_desc,
			"m_desc", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getDate(), m_date,
			"m_date", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getEnclosure(), m_enclosure,
			"m_enclosure", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.isUnreadItem(), m_unreadItem,
			"m_unreadItem", logger, fineLoggable)) {
			result = false;
		}
		return result;
	}
	//#endif

}
//#endif
