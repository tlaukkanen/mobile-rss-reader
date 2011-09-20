//--Need to modify--#preprocess
/*
 * RssItunesItemInfo.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2007 Tommi Laukkanen
 * Copyright (C) 2009 Irving Bunton Jr
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
 * IB 2010-03-14 1.11.5RC2 Add toString to interface.
 * IB 2010-03-14 1.11.5RC2 Code cleanup.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-02-01 1.11.5Dev17 Need clone method for RSS items.
*/

// Expand to define logging define
@DLOGDEF@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define test define
@DTESTDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities;

import java.util.Date;

/**
 * RssItunesItem class is a data store for a single item in RSS feed.
 * One item consist of title, link, description and optional date.
 *
 * @author  Tommi Laukkanen
 * @version 1.1
 */
public interface RssItunesItemInfo extends RssItemInfo {
    
    /** Serialize the object */
    String unencodedSerialize();

    String serialize();
		
    void setAuthor(String author);

    String getAuthor();

    void setSubtitle(String subtitle);

    String getSubtitle();

    void setSummary(String summary);

    String getSummary();

    void setExplicit(int explicit);

    String getExplicit();

    void setDuration(String duration);

    String getDuration();
    
	/* Compare item. */
	boolean equals(RssItemInfo item);

    void setItunes(boolean itunes);

    boolean isItunes();

    String toString();

	Object clone();

}
//#endif
