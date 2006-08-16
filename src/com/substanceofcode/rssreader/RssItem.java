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

/**
 * RssItem class is a data store for a single item in RSS feed.
 * One item consist of title, link and description.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class RssItem {
    
    private String  m_title = "";   // The RSS item title
    private String  m_link  = "";   // The RSS item link
    private String  m_desc  = "";   // The RSS item description
    
    /** Creates a new instance of RssItem */
    public RssItem(String title, String link, String desc) {
        m_title = title;
        m_link  = link;
        m_desc  = desc;
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
}
