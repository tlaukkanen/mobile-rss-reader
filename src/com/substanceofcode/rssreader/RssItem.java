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

package com.substanceofcode.rssreader;

import java.util.Date;

/**
 * RssItem class is a data store for a single item in RSS feed.
 * One item consist of title, link, description and optional date.
 *
 * @author  Tommi Laukkanen
 * @version 1.1
 */
public class RssItem {
    
    private String m_title = "";   // The RSS item title
    private String m_link  = "";   // The RSS item link
    private String m_desc  = "";   // The RSS item description
    private Date m_date = null;
    
    /** Creates a new instance of RssItem */
    public RssItem(String title, String link, String desc) {
        m_title = title;
        m_link = link;
        m_desc = desc;
        m_date = null;
    }
    
    /** Creates a new instance of RssItem */
    public RssItem(String title, String link, String desc, Date pubDate) {
        m_title = title;
        m_link = link;
        m_desc = desc;
        m_date = pubDate;
    }
    
    /** Get RSS item title */
    public String getTitle(){
        return m_title;
    }
    
    /** Get RSS item link address */
    public String getLink(){
        return m_link;
    }
    
    /** Get RSS item description */
    public String getDescription(){
        return m_desc;
    }
    
    /** Get RSS item publication date */
    public Date getDate() {
        return m_date;
    }
    
    /** Serialize the object */
    public String serialize() {
        String dateString;
        if(m_date==null){
            dateString = "";
        } else {
            dateString = String.valueOf( m_date.getTime() );
        }
        String preData = m_title + "|" + m_link + "|" + dateString + "|" + m_desc;
        Base64 b64 = new Base64();
        String encodedSerializedData = b64.encode( preData.getBytes() );
        return encodedSerializedData;
    }
    
    /** Deserialize the object */
    public static RssItem deserialize(String data) {
        
        String title = "";
        String link = "";
        String desc = "";
        Date date = null;

        // Base64 decode
        Base64 b64 = new Base64();
        byte[] decodedData = b64.decode(data);
        data = new String( decodedData );
        
        String[] nodes = StringUtil.split( data, "|");
        
        /* Node count should be 4:
         * title | link | date | desc
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
        
        int DESC = 3;
        desc = nodes[DESC];
                
        RssItem item = new RssItem(title, link, desc, date);
        return item;
    }
}
