//--Need to modify--#preprocess
/*
 * RssFeedInfo.java
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
 * IB 2010-03-14 1.11.5RC2 Fixed problem with conditional get.
 * IB 2010-03-14 1.11.5RC2 Add toString to interface.
 * IB 2010-03-14 1.11.5RC2 Code cleanup.
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-31 1.11.5Dev17 Change items to array to save on memory and for simplicity.
 * IB 2011-01-31 1.11.5Dev17 Allow optional saving of only the feed header name, user/pass, and link.
 * IB 2011-02-01 1.11.5Dev17 Need clone method for RSS feeds.
 * IB 2011-03-13 1.11.5Dev17 Have adjustFields for compatibility to change fields that are time sensitive to get compatibility compares to match.
 * IB 2011-03-18 1.11.5Dev17 Remove unnecessarily public keyword.
*/

// Expand to define test define
@DTESTDEF@
// Expand to define compatibility
@DCOMPATDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities;

import java.util.Date;
import java.util.Vector;

/**
 * RssFeedInfo class contains one RSS feed's properties.
 * Properties include name and URL to RSS feed.
 *
 * @author Tommi Laukkanen
 */
public interface RssFeedInfo {
    
    /** Return bookmark's name */
    String getName();
    
    void setName(String name);

    /** Return bookmark's URL */
    String getUrl();
    
    void setUrl(String url);

    /** Return bookmark's username for basic authentication */
    String getUsername();
    
    /** Set bookmark's username for basic authentication */
    void setUsername(String username);

    /** Set bookmark's password for basic authentication */
    void setPassword(String password);

    /** Return bookmark's password for basic authentication */
    String getPassword();
    
    String getUpddate();

    /** Set bookmark's password for basic authentication */
    void setUpddate(String upddate);

    String getEtag();

    void setEtag(String etag);

    /** Return record store string for feed only.  This excludes items which
	    are put into store string by RssItunesFeed.  */
    String getStoreString(final boolean saveHdr,
			final boolean serializeItems, final boolean encoded);

	/** Compare feed to an existing feed.  **/
	boolean equals(RssFeedInfo feed);
    
    /** Return RSS feed items */
	//#ifdef DTEST
	RssItemInfo[] getItems();
	//#else
	public RssItem[] getItems();
	//#endif
    
    /** Set items */
	//#ifdef DTEST
	void setItems(RssItemInfo[] items);
	//#else
	void setItems(RssItem[] items);
	//#endif
    
	Vector getVecItems();

	void setVecItems(Vector vtems);

    String getLink();

    void setLink(String link);

    void setDate(Date date);

    Date getDate();

    String toString();

    Object clone();

	//#ifdef DCOMPATIBILITY
	boolean adjustFields();
	//#endif

}
//#endif
