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
   IB 2010-03-07 1.11.4RC1 Use feed interface only for testing.
*/

// Expand to define logging define
//#define DNOLOGGING
// Expand to define test define
//#define DNOTEST
//#ifdef DTEST
//@package com.substanceofcode.rssreader.businessentities;
//@
//@import java.util.Date;
//@import java.util.Vector;
//@
//@/**
//@ * RssFeedInfo class contains one RSS feed's properties.
//@ * Properties include name and URL to RSS feed.
//@ *
//@ * @author Tommi Laukkanen
//@ */
//@public interface RssFeedInfo {
//@    
//@    /** Return bookmark's name */
//@    String getName();
//@    
//@    void setName(String m_name);
//@
//@    /** Return bookmark's URL */
//@    String getUrl();
//@    
//@    void setUrl(String url);
//@
//@    /** Return bookmark's username for basic authentication */
//@    String getUsername();
//@    
//@    /** Set bookmark's username for basic authentication */
//@    void setUsername(String username);
//@
//@    /** Set bookmark's password for basic authentication */
//@    void setPassword(String password);
//@
//@    /** Return bookmark's password for basic authentication */
//@    String getPassword();
//@    
//@    Date getUpddate();
//@
//@    /** Set bookmark's password for basic authentication */
//@    void setUpddate(Date upddate);
//@
//@    void setUpddateTz(String supddate);
//@
//@    String getUpddateTz();
//@
//@    String getEtag();
//@
//@    void setEtag(String etag);
//@
//@    /** Return record store string for feed only.  This excludes items which
//@	    are put into store string by RssItunesFeed.  */
//@    String getStoreString(boolean serializeItems, boolean encoded);
//@
	//#ifdef DTEST
//@	/** Compare feed to an existing feed.  **/
//@	boolean equals(RssFeedInfo feed);
	//#endif
//@    
//@    /** Return RSS feed items */
//@    Vector getItems();
//@    
//@    /** Set items */
//@    void setItems(Vector items);
//@    
//@    String getLink();
//@
//@    void setLink(String link);
//@
//@    void setDate(Date date);
//@
//@    Date getDate();
//@
//@}
//#endif
