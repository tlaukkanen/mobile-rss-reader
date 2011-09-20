//--Need to modify--#preprocess
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
/*
 * IB 2010-03-07 1.11.4RC1 Fix testing code of explicit.
 * IB 2010-03-07 1.11.4RC1 Use convenience method for encoding/decoding.
 * IB 2010-03-07 1.11.4RC1 Combine classes to save space.
 * IB 2010-05-30 1.11.4RC2 Use RssItemInfo for equals.
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-26 1.11.5Dev14 Need to add m_duration to equals.
 * IB 2010-11-26 1.11.5Dev15 Use checkRead to set the m_unreadItem to the parameter RssItem's m_unreadItem if the other fields are equal.
 * IB 2010-11-26 1.11.5Dev15 Fix toString used by testing to separate explicit from the first field in the item.
 * IB 2011-01-24 1.11.5Dev16 Don't compile unneeded code for internet link version.
 * IB 2011-01-31 1.11.5Dev17 Have checkRead return true if items are the same.  Use this to reduce cross check of items being the same.
 * IB 2011-02-01 1.11.5Dev17 Need clone method for RSS items.
 * IB 2011-03-06 1.11.5Dev17 Combine statements.
 * IB 2011-03-12 1.11.5Dev17 Have m_state keep track of unread items and itunes flag.
 * IB 2011-03-12 1.11.5Dev17 Have m_state keep track of unread items and itunes flag.
 * IB 2011-03-15 1.11.5Dev17 More logging.
 * IB 2011-03-15 1.11.5Dev17 Catch Throwable instead of Exception and return null or partially initialized item.
 * IB 2011-03-15 1.11.5Dev17 If 0/1 fields while deserialization, return null
 * IB 2011-03-06 1.11.5Dev17 Use RssItem instead of RssItunesItem to allow future difference in the two.
*/

// Expand to define itunes define
//#define DFULLVERS
// Expand to define logging define
//#define DNOLOGGING
// Expand to define itunes define
//#define DNOITUNES
// Expand to define test define
//#define DNOTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define JMUnit test define
//#define DNOJMTEST
//#ifdef DTESTUI
//#define HAS_EQUALS
//#endif
//#ifdef DLOGGING
//#define HAS_EQUALS
//#endif
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.MiscUtil;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Hashtable;
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

//#ifdef DLOGGING
//@import com.substanceofcode.testutil.logging.TestLogUtil;
//#elif DTESTUI
//@import com.substanceofcode.testutil.console.TestLogUtil;
//#endif

/**
 * RssItunesItem class is a data store for a single item in RSS feed.
 * One item consist of title, link, description and optional date.
 *
 * @author  Tommi Laukkanen
 * @version 1.1
 */
public class RssItunesItem extends RssItem
//#ifdef DTEST
//#ifdef DJMTEST
//@implements RssItunesItemInfo, RssItunesInfo
//#endif
//#endif
{
    
	// Make max summary same as max description (actual max is 50K)
    public static int MAX_SUMMARY = 500;
	// Beginning of data that has 0 itunes info.
	// Number of Itunes info
    final protected static int NBR_ITUNES_INFO = 6;
    final protected static byte BNO_EXPLICIT = (byte)-1;
    final public static String UNSPECIFIED = "unspecified";
    // Value that shows that the first item (and those following may
	// contain ITunes items (or all may not contain any, but they
	// can later be modified to contain them).
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("RssItunesItem");
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#elif DTESTUI
//@    private Object logger = null;
//@    private boolean fineLoggable = true;
//@    private boolean finestLoggable = true;
	//#endif
    protected String m_author = "";   // The RSS item description
    protected String m_subtitle = "";   // The RSS item description
    protected String m_summary = "";   // The RSS item description
	//TODO 4 values for m_explicit.  Fix duration descriptions above.
	//TODO duration use 60 radius.  Max summary of 4K
    protected byte m_explicit = BNO_EXPLICIT;   // The RSS item explicit
    protected String m_duration  = "";   // The RSS item duration

    /** Creates a new instance of RssItunesItem.  Used by this class.  */
    public RssItunesItem() {
		super();
	}

    /** Creates a new instance of RssItunesItem
		title - title of item
		link - link of item
		desc - description of item
		pubDate - If no pubDate use null
		enclosure - enclosure of item or "" if no enclosure
		unreadItem - True if item unread, false if read
		**/
    public RssItunesItem(String title, String link, String desc, Date pubDate,
			        String enclosure, boolean unreadItem) {
		super(title, link, desc, pubDate, enclosure, unreadItem);
    }
    
    public RssItunesItem(String title, String link, String desc, Date date,
			        String enclosure, boolean unreadItem,
					boolean itunes,
					String author,
					String subtitle,
					String summary,
					byte explicit,
					String duration) {
		super(title, link, desc, date, enclosure,
				(byte)((unreadItem ? 0x01 : 0x00)
		//#ifdef DITUNES
//@				 | (itunes ? 0x02 : 0x00)
		//#endif
			 ));
		//#ifdef DITUNES
//@		if (itunes) {
//@			m_author = author;
//@			m_subtitle = subtitle;
//@			m_summary = summary;
//@			m_explicit = explicit;
//@			m_duration = duration;
//@		}
		//#endif
    }
    
    /** Creates a new instance of RssItunesItem */
    public RssItunesItem(RssItem item) {
		super(item);
		//#ifdef DITUNES
//@		if (item instanceof RssItunesItem) {
//@			RssItunesItem ititem = (RssItunesItem)item;
//@			this.m_author = ititem.m_author;
//@			this.m_subtitle = ititem.m_subtitle;
//@			this.m_summary = ititem.m_summary;
//@			this.m_explicit = ititem.m_explicit;
//@			this.m_duration = ititem.m_duration;
//@		}
		//#endif
    }
    
    /** Serialize the object */
    public String unencodedSerialize() {
		String author = "";
		String subtitle = "";
		String summary = "";
		//#ifdef DITUNES
//@		author = m_author.replace('|', CONE);
//@		subtitle = m_subtitle.replace('|', CONE);
//@		summary = m_summary.replace('|', CONE);
		//#endif
        String preData = "|" +
			author + "|" + subtitle + "|" + summary + "|" +
                 ((m_explicit == BNO_EXPLICIT) ? "" :
						 Integer.toString((int)m_explicit)) + "|" +
				m_duration + "|" + super.unencodedSerialize();
		return preData;
	}

    public String serialize() {
        String encodedSerializedData = MiscUtil.encodeStr(unencodedSerialize());
		return encodedSerializedData;
	}
		
	/** Deserialize the unencoded object */
	public static RssItem unencodedDeserialize(String data) {
			
		try {
			boolean hasPipe = (data.indexOf((char)1) >= 0);
			String[] nodes = MiscUtil.split( data, "|");
			RssItunesItem item = new RssItunesItem();
			if (nodes.length > 1) {
				item.init(hasPipe, nodes);
			} else {
				//#ifdef DLOGGING
//@				Logger.getLogger("RssItem").severe(
//@						"unencodedDeserialize error invalid item data=" + data,
//@						new Exception(
//@							"unencodedDeserialize error invalid item data"));
				//#endif
				return null;
			}
			if (item.isItunes()) {
				return item;
			} else {
				return new RssItem(item);
			}
        } catch(Throwable e) {
			//#ifdef DLOGGING
//@			Logger.getLogger("RssItem").severe("unencodedDeserialize error data=" + data, e);
			//#endif
            System.err.println("unencodedDeserialize Error while RssItunesItem deserialize : " + e.toString());
			e.printStackTrace();
			return null;
        }
	}
			
	/** Deserialize the object */
	public static RssItem deserialize(String data) {
		try {
			return unencodedDeserialize(MiscUtil.decodeStr(data));
        } catch(Throwable e) {
			//#ifdef DLOGGING
//@			Logger.getLogger("RssItem").severe("deserialize error data=" + data, e);
			//#endif
            System.err.println("Error while RssItunesItem deserialize : " + e.toString());
			e.printStackTrace();
			return null;
		}
			
	}
			
	/**
	  Initialize fields in the class from data.
	  hasPipe - True if the data has a pipe in at least one item
	  nodes - (elements in an array).
	  **/
	protected void init(boolean hasPipe, String [] nodes) {
		try {
			/* Node count should be 12:
			 * author | subtitle | category | enclosure | summary | explicit
			 * | duration | followed by RssItem fields
			 * title | link | date | enclosure | unreadItem | desc
			 */
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("init nodes.length=" + nodes.length);}
			//#endif
			//#ifdef DITUNES
//@			{
//@				int AUTHOR;
//@				m_author = nodes[AUTHOR = 1];
//@			}
//@			if (hasPipe) {
//@				m_author = m_author.replace(CONE, '|');
//@			}
//@			
//@			{
//@				int SUBTITLE;
//@				m_subtitle = nodes[SUBTITLE = 2];
//@			}
//@			if (hasPipe) {
//@				m_subtitle = m_subtitle.replace(CONE, '|');
//@			}
//@			
//@			{
//@				int SUMMARY;
//@				m_summary = nodes[SUMMARY = 3];
//@			}
//@			if (hasPipe) {
//@				m_summary = m_summary.replace(CONE, '|');
//@			}
//@
//@			{
//@				int EXPLICIT;
//@				String explicit = nodes[EXPLICIT = 4];
//@				if (explicit.length() > 0) {
//@					m_explicit = (byte)Integer.parseInt(explicit);
//@				}
//@			}
//@
//@			{
//@				int DURATION;
//@				m_duration = nodes[DURATION = 5];
//@			}
			//#endif

			super.init(NBR_ITUNES_INFO, true, hasPipe, nodes);

        } catch(Throwable e) {
			//#ifdef DLOGGING
//@			logger.severe("init error m_title,m_link=" + m_title + "," + m_link, e);
			//#endif
            System.err.println("Error while RssItunesItem deserialize : " + e.toString());
			e.printStackTrace();
        }
    }

    /** Write record as a string */
	//#ifdef DTEST
//@    public String toString() {
//@        String storeString = "|" + m_author + "|" + m_subtitle + "|" +
//@			m_summary + "|" + (int)m_explicit + "|" + super.toString();
//@        return storeString;
//@    }
	//#endif

    public void setAuthor(String author) {
        this.m_author = author;
    }

    public String getAuthor() {
        return (m_author);
    }

    public void setSubtitle(String subtitle) {
        this.m_subtitle = subtitle;
    }

    public String getSubtitle() {
        return (m_subtitle);
    }

    public void setSummary(String summary) {
        this.m_summary = summary;
    }

    public String getSummary() {
        return (m_summary);
    }

    public void setExplicit(int explicit) {
        this.m_explicit = (byte)explicit;
    }

    static public String convExplicit(byte explicit) {
		switch (explicit) {
			case 0:
				return "No";
			case 1:
				return "Clean";
			case 2:
				return "Yes";
			default:
				return UNSPECIFIED;
		}
    }

    public String getExplicit() {
		return convExplicit(m_explicit);
	}

    static public byte convExplicit(String pexplicit) {
		if ((pexplicit == null) || (pexplicit.length() == 0)) {
			return BNO_EXPLICIT;
		}
		String explicit = pexplicit.toLowerCase();
		switch (explicit.charAt(0)) {
			case 'n':
				if (explicit.equals("no")) {
					return (byte)0;
				} else {
					return BNO_EXPLICIT;
				}
			case 'c':
				if (explicit.equals("clean")) {
					return (byte)1;
				} else {
					return BNO_EXPLICIT;
				}
			case 'y':
				if (explicit.equals("yes")) {
					return (byte)2;
				} else {
					return BNO_EXPLICIT;
				}
			default:
				return BNO_EXPLICIT;
		}
    }

    public void setExplicit(String explicit) {
        this.m_explicit = convExplicit(explicit);
    }

    public void setDuration(String duration) {
        this.m_duration = duration;
    }

    public String getDuration() {
        return (m_duration);
    }
    
	public boolean checkRead(RssItem pitem) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("checkRead pitem=" + pitem);}
//@		if (finestLoggable) {logger.finest("checkRead this=" + this);}
		//#endif
		if (((super.m_state & pitem.m_state) == 0x02) &&
			(pitem instanceof RssItunesItem)) {
			RssItunesItem pititem = (RssItunesItem)pitem;
			if (m_author.equals(pititem.m_author) &&
			 m_subtitle.equals(pititem.m_subtitle) &&
			 m_summary.equals(pititem.m_subtitle) &&
			 (m_explicit == pititem.m_explicit) &&
			 m_duration.equals(pititem.m_duration)) {
				return super.checkRead(pitem);
			} else {
				return false;
			}
		} else {
			return super.checkRead(pitem);
		}
	}

	/* Compare item. */
	//#ifdef HAS_EQUALS
	//#ifdef DJMTEST
//@	public boolean equals(RssItemInfo pitem)
	//#else
//@	public boolean equals(RssItem pitem)
	//#endif
//@	{
//@		boolean result = true;
//@		if (!super.equals(pitem)) {
//@			result = false;
//@		}
		//#ifdef DJMTEST
//@		if (!(pitem instanceof RssItunesItemInfo)) {
//@			return result;
//@		}
//@		RssItunesItemInfo item = (RssItunesItemInfo)pitem;
		//#else
//@		if (!(pitem instanceof RssItunesItem)) {
//@			return result;
//@		}
//@		RssItunesItem item = (RssItunesItem)pitem;
		//#endif
//@
//@		if ((item instanceof RssItunesInfo) &&
//@			!TestLogUtil.fieldEquals(((RssItunesInfo)item).isItunes(),
//@			super.isItunes(), "itunes", logger, fineLoggable)) {
//@			result = false;
//@		}
//@
//@		if (!TestLogUtil.fieldEquals(item.getAuthor(), m_author,
//@			"m_author", logger, fineLoggable)) {
//@			result = false;
//@		}
//@
//@		if (!TestLogUtil.fieldEquals(item.getSubtitle(), m_subtitle,
//@			"m_subtitle", logger, fineLoggable)) {
//@			result = false;
//@		}
//@		if (!TestLogUtil.fieldEquals(item.getSummary(), m_summary,
//@			"m_summary", logger, fineLoggable)) {
//@			result = false;
//@		}
//@		if (!TestLogUtil.fieldEquals(item.getExplicit().toLowerCase(),
//@					getExplicit().toLowerCase(),
//@			"m_explicit", logger, fineLoggable)) {
//@			result = false;
//@		}
//@		if (!TestLogUtil.fieldEquals(item.getDuration(), m_duration,
//@			"m_duration", logger, fineLoggable)) {
//@			result = false;
//@		}
//@		return result;
//@	}
	//#endif

    public Object clone() {
		return new RssItunesItem(this);
	}

}
//#endif
