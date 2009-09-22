/*
 * RssItunesItem.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2007 Tommi Laukkanen
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
// Expand to define itunes define
@DITUNESDEF@
// Expand to define test define
@DTESTDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.Base64;
import com.substanceofcode.utils.StringUtil;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Hashtable;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

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
    
	//#ifdef DTEST
	/* Compare item. */
	boolean equals(RssItemInfo item);
	//#endif

    void setItunes(boolean itunes);

    boolean isItunes();

}
//#endif
