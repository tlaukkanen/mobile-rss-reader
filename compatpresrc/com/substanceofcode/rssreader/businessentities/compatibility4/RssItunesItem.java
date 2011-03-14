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
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-05-30 1.11.5RC2 Fix equals to use RssItemInfo interface.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-26 1.11.5Dev14 Need to add m_duration to equals.
 */

// Expand to define logging define
@DLOGDEF@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businessentities.compatibility4;

import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
import com.substanceofcode.rssreader.businessentities.RssItunesInfo;
import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.utils.compatibility4.Base64;
import com.substanceofcode.utils.MiscUtil;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Hashtable;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

//#ifdef DLOGGING
import com.substanceofcode.testutil.logging.TestLogUtil;
//#elif DTESTUI
import com.substanceofcode.testutil.console.TestLogUtil;
//#endif

/**
 * RssItunesItem class is a data store for a single item in RSS feed.
 * One item consist of title, link, description and optional date.
 *
 * @author  Tommi Laukkanen
 * @version 1.1
 */
public class RssItunesItem extends RssItem
implements RssItunesItemInfo, RssItunesInfo
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
    private Logger logger = Logger.getLogger("compatibility4.RssItunesItem");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#elif DTESTUI
    private Object logger = null;
    private boolean fineLoggable = true;
    private boolean finestLoggable = true;
	//#endif
    protected boolean m_itunes = false;
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
		super(title, link, desc, date, enclosure, unreadItem);
		//#ifdef DITUNES
		m_itunes = itunes;
		if (m_itunes) {
			m_author = author;
			m_subtitle = subtitle;
			m_summary = summary;
			m_explicit = explicit;
			m_duration = duration;
		}
		//#endif
    }
    
    /** Creates a new instance of RssItunesItem */
    public RssItunesItem(RssItemInfo pitem) {
		super(pitem);
		//#ifdef DITUNES
		if (pitem instanceof RssItunesItemInfo) {
			RssItunesItemInfo item = (RssItunesItemInfo)pitem;
			this.m_itunes = item.isItunes();
			this.m_author = item.getAuthor();
			this.m_subtitle = item.getSubtitle();
			this.m_summary = item.getSummary();
			this.m_explicit = convExplicit(item.getExplicit());
			this.m_duration = item.getDuration();
		}
		//#endif
    }
    
    /** Serialize the object */
    public String unencodedSerialize() {
		String author = "";
		String subtitle = "";
		String summary = "";
		//#ifdef DITUNES
		if (m_itunes) {
			author = m_author.replace('|', CONE);
			subtitle = m_subtitle.replace('|', CONE);
			summary = m_summary.replace('|', CONE);
		}
		//#endif
        String preData = (m_itunes ? "1" : "") + "|" +
			author + "|" + subtitle + "|" + summary + "|" +
                 ((m_explicit == BNO_EXPLICIT) ? "" :
						 Integer.toString((int)m_explicit)) + "|" +
				m_duration + "|" + super.unencodedSerialize();
		return preData;
	}

    public String serialize() {
        String preData = unencodedSerialize();
        Base64 b64 = new Base64();
        String encodedSerializedData = null;
		try {
			encodedSerializedData = b64.encode( preData.getBytes("UTF-8") );
		} catch (UnsupportedEncodingException e) {
			encodedSerializedData = b64.encode( preData.getBytes() );
		}
		return encodedSerializedData;
	}
		
	/** Deserialize the unencoded object */
	public static RssItem unencodedDeserialize(String data) {
			
		try {
			boolean hasPipe = (data.indexOf((char)1) >= 0);
			String[] nodes = MiscUtil.split( data, "|");
			RssItunesItem item = new RssItunesItem();
			item.init(hasPipe, nodes);
			return item;
        } catch(Exception e) {
            System.err.println("Error while RssItunesItem deserialize : " + e.toString());
			e.printStackTrace();
			return null;
        }
	}
			
	/** Deserialize the object */
	public static RssItem deserialize(String data) {
		try {
			// Base64 decode
			Base64 b64 = new Base64();
			byte[] decodedData = b64.decode(data);
			try {
				data = new String( decodedData, "UTF-8" );
			} catch (UnsupportedEncodingException e) {
				data = new String( decodedData );
			}
			return unencodedDeserialize(data);
        } catch(Exception e) {
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
			if (finestLoggable) {logger.finest("nodes.length=" + nodes.length);}
			//#endif
			//#ifdef DITUNES
			int ITUNES = 0;
			m_itunes = nodes[ITUNES].equals("1");
			
			if (m_itunes) {
				int AUTHOR = 1;
				m_author = nodes[AUTHOR];
				if (hasPipe) {
					m_author = m_author.replace(CONE, '|');
				}
				
				int SUBTITLE = 2;
				m_subtitle = nodes[SUBTITLE];
				if (hasPipe) {
					m_subtitle = m_subtitle.replace(CONE, '|');
				}
				
				int SUMMARY = 3;
				m_summary = nodes[SUMMARY];
				if (hasPipe) {
					m_summary = m_summary.replace(CONE, '|');
				}

				int EXPLICIT = 4;
				String explicit = nodes[EXPLICIT];
				if (explicit.length() > 0) {
					m_explicit = (byte)Integer.parseInt(explicit);
				}

				int DURATION = 5;
				m_duration = nodes[DURATION];
			}
			//#endif

			super.init(NBR_ITUNES_INFO, true, hasPipe, nodes);

        } catch(Exception e) {
            System.err.println("Error while RssItunesItem deserialize : " + e.toString());
			e.printStackTrace();
        }
    }

    /** Write record as a string */
    public String toString(){
        String storeString = m_itunes + "|" + m_author + "|" + m_subtitle + "|" +
			m_summary + "|" + + (int)m_explicit + super.toString();
        return storeString;
    }

    public void setAuthor(String m_author) {
        this.m_author = m_author;
    }

    public String getAuthor() {
        return (m_author);
    }

    public void setSubtitle(String m_subtitle) {
        this.m_subtitle = m_subtitle;
    }

    public String getSubtitle() {
        return (m_subtitle);
    }

    public void setSummary(String m_summary) {
        this.m_summary = m_summary;
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

    public void setDuration(String m_duration) {
        this.m_duration = m_duration;
    }

    public String getDuration() {
        return (m_duration);
    }
    
	/* Compare item. */
	public boolean equals(RssItemInfo pitem) {
		boolean result = true;
		if (!super.equals(pitem)) {
			result = false;
		}
		if (!(pitem instanceof RssItunesItemInfo)) {
			return result;
		}
		RssItunesItemInfo item = (RssItunesItemInfo)pitem;

		if ((item instanceof RssItunesInfo) &&
			!TestLogUtil.fieldEquals(((RssItunesInfo)item).isItunes(),
			m_itunes, "m_itunes", logger, fineLoggable)) {
			result = false;
		}

		if (!TestLogUtil.fieldEquals(item.getAuthor(), m_author,
			"m_author", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getSubtitle(), m_subtitle,
			"m_subtitle", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getSummary(), m_summary,
			"m_summary", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getExplicit().toLowerCase(),
					RssItunesItem.convExplicit(m_explicit).toLowerCase(),
			"m_explicit", logger, fineLoggable)) {
			result = false;
		}
		if (!TestLogUtil.fieldEquals(item.getDuration(), m_duration,
			"m_duration", logger, fineLoggable)) {
			result = false;
		}
		return result;
	}

    public void setItunes(boolean itunes) {
		//#ifdef DITUNES
        this.m_itunes = itunes;
		//#else
        this.m_itunes = false;
		//#endif
    }

    public boolean isItunes() {
		//#ifdef DITUNES
        return (m_itunes);
		//#else
        return (false);
		//#endif
    }

}
//#endif
