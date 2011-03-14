//--Need to modify--#preprocess
/*
 * RssItemInfo.java
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
 * IB 2010-03-07 1.11.4RC1 Use feed interface only for testing.
 * IB 2010-03-07 1.11.4RC1 Code cleanup.
 * IB 2010-03-07 1.11.4RC1 Need toString.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-02-01 1.11.5Dev17 Need clone method for RSS items.
*/

// Expand to define logging define
@DLOGDEF@
// Expand to define test define
@DTESTDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities;

import java.util.Date;

/**
 * RssItemInfo class is a data store for a single item in RSS feed.
 * One item consist of title, link, description and optional date.
 *
 * @author  Tommi Laukkanen
 * @version 1.1
 */
public interface RssItemInfo {
    
    /** Get RSS item title */
    String getTitle();
    
    /** Get RSS item link address */
    String getLink();
    
    void setLink(String link);

    /** Get RSS item description */
    String getDescription();
    
    /** Get RSS item description */
    void setDescription(String desc);
    
    /** Get RSS item publication date */
    Date getDate();
    
    void setDate(Date date);

    /** Serialize the object
	  When we serialize we don't do anything special for itunes as the
	  store to memory will be deserialized only by the iTunes capable
	  version.
	  */
    String unencodedSerialize();

    /** Serialize the object
	  this serialize does not need to know if Itunes is capable/enabled given
	  that no fields were added to make it capable/enabled
	  */
    String serialize();
		
	/* Compare item. */
	boolean equals(RssItemInfo item);

    void setUnreadItem(boolean unreadItem);

    boolean isUnreadItem();

    void setEnclosure(String enclosure);

    String getEnclosure();

    String toString();

	Object clone();

}
//#endif
