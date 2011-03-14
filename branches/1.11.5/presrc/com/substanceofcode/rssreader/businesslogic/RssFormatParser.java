//--Need to modify--#preprocess
/*
 * RssFormatParser.java
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
 * IB 2010-03-07 1.11.4RC1 Combine classes to save space.
 * IB 2010-07-05 1.11.5Dev6 Code cleanup.
 * IB 2010-07-05 1.11.5Dev6 Don't use toString for appending booleans and integers with StringBuffer as there is already an append method for this.
 * IB 2010-07-28 1.11.5Dev8 Don't remove HTML for link.
 * IB 2010-07-28 1.11.5Dev8 Don't remove entities for link with CDATA.
 * IB 2010-07-28 1.11.5Dev8 Don't convert entities for skipped/unused items.
 * IB 2010-07-28 1.11.5Dev8 Set feed header fields if no items.
 * IB 2010-07-28 1.11.5Dev8 More logging.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-16 1.11.5Dev14 Add columnCount to parseStdDate exceptions.
 * IB 2010-11-16 1.11.5Dev14 Remove stimeZones and GMT because this may change with phone config or upgrading phone.
 * IB 2010-11-16 1.11.5Dev14 Have parseStdDateTZ return string time zone instead of index of time zone.
 * IB 2010-11-26 1.11.5Dev14 To help with minor performance, start vector with max items.
 * IB 2010-11-26 1.11.5Dev14 To help with minor space, use trimToSize for items..
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-03-06 1.11.5Dev17 Change items to array to save on memory and for simplicity.
 * IB 2011-03-09 1.11.5Dev17 Recognize image with initial cap to prevent invalid links, etc inside the image tag contents.
 * IB 2011-03-11 1.11.5Dev17 Set the millisecond to 0 to allow dates to be more consistent.
 * IB 2011-03-11 1.11.5Dev17 Trim the date.
 * IB 2011-03-11 1.11.5Dev17 Use MiscUtil.getSgmlUrl to convert &amp; within links to handle parameters in links.
 * IB 2011-03-13 1.11.5Dev17 Have getFeedTitleList to use title for name when parsing the whole feed.
 * IB 2011-03-13 1.11.5Dev17 Use RssItem instead of RssItunesItem to allow future difference in the two.
 * IB 2011-03-13 1.11.5Dev17 Process dates of the form 11 Mar 2011 11:43 GMT.
 * IB 2011-03-13 1.11.5Dev17 Process dates of the form Wed, 6 September 14:00:00 GMT.
 * IB 2011-03-13 1.11.5Dev17 Process dates of the form March 11, 2011 3:11:26 PM PST
 * IB 2011-03-13 1.11.5Dev17 Combine statements.
 */

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businesslogic;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItem;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

/**
 *
 * @author Tommi
 */
public class RssFormatParser implements FeedFormatParser {
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("RssFormatParser");
	//#endif
	/** RSS item properties */
	private boolean m_hasExt = false;
	//#ifdef DLOGGING
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif
	private String m_title = "";
	private String m_author = "";
	private String m_description = "";
	private String m_link = "";
	private String m_language = "";
	private String m_date = "";
	private String m_lastBuildDate = "";
	private String m_enclosure = "";
	private ExtParser m_extParser = new ExtParser();

    public RssItunesFeed parse(XmlParser parser, RssItunesFeed cfeed,
			int maxItemCount, boolean getFeedTitleList, boolean getTitleOnly)
	throws IOException {
        
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parse parser,cfeed.getName(),cfeed.getUrl(),maxItemCount,getFeedTitleList,getTitleOnly=" + parser + "," + cfeed.getName() + "," + cfeed.getUrl() + "," + + maxItemCount + "," + getFeedTitleList + "," + getTitleOnly);}
		//#endif
        Vector items = new Vector(maxItemCount);
		m_extParser.parseNamespaces(parser);
		m_hasExt = m_extParser.isHasExt();
		RssItunesFeed feed = cfeed;
        
        /** Parse to first entry element */
		try {
			while(!parser.getName().equals("item")) {
				switch (parser.parse()) {
					case XmlParser.END_DOCUMENT:
						System.out.println("No entries found.");
						return feed;
					case XmlParser.ELEMENT:
						String elementName = parser.getName();
						if (elementName.length() == 0) {
							continue;
						}
						char elemChar = elementName.charAt(0);
						if (parseCommon(parser, elemChar, elementName)) {
							if ((elemChar == 't') && 
									getTitleOnly && elementName.equals("title") ) {
								feed.setName(m_title);
								return feed;
							}
							continue;
						}
						switch (elemChar) {
							case 'l':
								 if (elementName.equals("lastBuildDate")) {
									 m_lastBuildDate = parser.getText(true).trim();
									 //#ifdef DLOGGING
									 if (finestLoggable) {logger.finest("m_lastBuildDate=" + m_lastBuildDate);}
									 //#endif
									 continue;
								 } else if (elementName.equals("language")) {
									 m_language = parser.getText(true);
									 //#ifdef DLOGGING
									 if (finestLoggable) {logger.finest("m_language=" + m_language);}
									 //#endif
									 continue;
								 }
								 break;
							case 'i':
							case 'I':
								 if (elementName.substring(1).equals("mage")) {
									 // Skip image text as it includes link
									 // and title.
									 parser.getText(false);
									 //#ifdef DLOGGING
									 if (finestLoggable) {logger.finest("skipping image");}
									 //#endif
									 continue;
								 }
								 break;
							default:
								 break;
						}
						if (m_hasExt) {
							m_extParser.parseExtItem(parser, elemChar, elementName);
						}
						break;
					default:
						break;
				}
			}
		} finally {
			if ((feed.getName().length() == 0) && getFeedTitleList) {
				feed.setName(m_title);
			}
			//#ifdef DITUNES
			feed.setLink(m_link);
			if (m_date.length() > 0) {
				Date pubDate = parseRssDate(m_date);
				feed.setDate(pubDate);
			} else if (m_lastBuildDate.length() > 0) {
				Date pubDate = parseRssDate(m_lastBuildDate);
				feed.setDate(pubDate);
			} else {
				feed.setDate(null);
			}
			if (m_extParser.isItunes()) {
				feed = m_extParser.getFeedInstance(feed, m_language, m_title,
						m_description);
			}
			//#endif
		}
        
		reset();

		try {
			/** Parse next element */            
			int parsingResult;
			while( (parsingResult = parser.parse()) !=XmlParser.END_DOCUMENT ) {
				if (parsingResult != XmlParser.ELEMENT) {
					continue;
				}
				String elementName = parser.getName();
				if (elementName.length() == 0) {
					continue;
				}
				
				char elemChar = elementName.charAt(0);
				switch (elemChar) {
					case 'i':
						if (elementName.equals("item") ) {
							/** Save previous entry */
							RssItem item = createItem();
							if ( item != null) {
								items.addElement( item );
								if(items.size()==maxItemCount) {
									return feed;
								}
							}                

							/** New entry */
							/** reset */
							reset();
							continue;
						}
						break;
					case 't':
						// Textinput has required sub element description.
						// We don't want the overriding description.
						if (elementName.equals("textinput") ) {
							parser.getText(false);
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("skipping textinput data");}
							//#endif
							continue;
						}
						break;
					default:
				}
				parseItem(parser, elemChar, elementName);
				
			}

			/** Save previous entry */
			RssItem item = createItem();
			if ( item != null) {
				items.addElement( item );
			}        
		} finally {
			feed.setVecItems(items);
		}
                        
		return feed;
    }
    
	/** Save previous entry */
	final private RssItem createItem() {
		boolean hasTitle = (m_title.length()>0);
		boolean hasDesc = (m_description.length()>0);
		if (hasTitle || hasDesc) {
			if (hasDesc) {
				m_description = m_description.trim();
			}
			if (hasTitle && hasDesc) {
				m_title = m_title.replace('\n', ' ');
				m_title = m_title.trim();
			}
			RssItem item;
			Date pubDate = null;
			// Check date in case we cannot find it.
			if (m_date.equals("") && m_extParser.isHasExt()) {
				m_date = m_extParser.getDate();
			}
			if (m_date.length() > 0) {
				pubDate = parseRssDate(m_date);
			}
			m_link = m_link.trim();
			if (m_hasExt) {
				item = m_extParser.createItem(m_title, m_link,
						m_description, pubDate, m_enclosure, true,
						m_author);
			} else {
				item = new RssItem(m_title, m_link,
						m_description, pubDate,
						m_enclosure, true);
			}
			return item;
		}
		return null;
	}

	private void reset() {
		m_title = "";
		m_author = "";
		m_description = "";
		m_link = "";
		m_language = "";
		m_date = "";
		m_enclosure = "";
		if (m_hasExt) {
			m_extParser.reset();
		}
	}

	/* Parse the fields common to feed and item. */
	private boolean parseCommon(XmlParser parser, char elemChar,
			String elementName)
	throws IOException {
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("parseCommon elemChar,elementName=" + elemChar + "," + elementName);}
		//#endif
		switch (elemChar) {
			case 'p':
				if( elementName.equals("pubDate")) {
					m_date = parser.getText(true).trim();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_date=" + m_date);}
					//#endif
					return true;
				}
				break;
			case 't':
				if( elementName.equals("title") ) {
					m_title = parser.getText(true);
					m_title = MiscUtil.removeHtml( m_title );
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_title=" + m_title);}
					//#endif
					return true;
				}
				break;
			case 'd':
				if( elementName.equals("description")) {
					m_description = parser.getText(true);
					m_description = MiscUtil.removeHtml( m_description );
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_description=" + m_description);}
					//#endif
					return true;
				}
				break;
			case 'l':
				if( elementName.equals("link") ) {
					m_link = MiscUtil.getSgmlUrl(parser.getText(false));
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_link=" + m_link);}
					//#endif
					return true;
				}
				break;
			default:
		}
		return false;
	}

	/* Parse the item to get it's fields */
	void parseItem(XmlParser parser, char elemChar, String elementName)
	throws IOException {
		switch (elemChar) {
			case 'a':
				if( elementName.equals("author")) {
					m_author = parser.getText(true);
					return;
				}
				break;
			case 'e':
				if( elementName.equals("enclosure") ) {
					String cenclosure = parser.getAttributeValue("url");
					if (cenclosure != null) {
						m_enclosure = cenclosure;
						return;
					}
					return;
				}
				break;
			default:
		}
		if (parseCommon(parser, elemChar, elementName)) {
			return;
		}
		if (m_hasExt) {
			m_extParser.parseExtItem(parser, elemChar, elementName);
		}
	}

	/** Get calendar date. **/
	public static Date getCal(int dayOfMonth, int month, int year, int hours,
			           int minutes, int seconds) throws Exception {
		// Create calendar object from date values
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}

	/**  Parse the standard RSS date and Dublin Core (dc) date. */
	static Date parseRssDate(String date) {
		Date pubDate = null;
		int dpos = date.indexOf('-', 2);
		if ((dpos > 0) && (date.indexOf('-', dpos + 1) > 0)) {
			pubDate = parseDcDate(date);
		} else {
			pubDate = parseStdDate(date);
		}
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("RssFormatParser");
		logger.finest("date,pubDate=" + date + "," + pubDate);
		//#endif
		return pubDate;
	}

    /**
     * Parse RSS date format to Date object.
     * Example of RSS date:
     * Sat, 23 Sep 2006 22:25:11 +0000
     */
    public static Object[] parseStdDateTZ(String dateString) {
        Object[] objs = null;
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("RssFormatParser");
		boolean finestLoggable = logger.isLoggable(Level.FINEST);
		//#endif
		int columnCount = -1;
        try {
            // Split date string to values
            // 0 = week day
            // 1 = day of month
            // 2 = month
            // 3 = year (could be with either 4 or 2 digits)
            // 4 = time
            // 5 = GMT
            int dayOfMonthIndex = 1;
            int monthIndex = 2;
            int yearIndex = 3;
            int timeIndex = 4;
            int ampmAdj = 0;
            int tzIndex = 5;
            
			int kc = 0;
            while ((dateString.indexOf("  ") >= 0) &&
					(kc++ < dateString.length())) {
				dateString = MiscUtil.replace(dateString, "  ", " ");
			}

            String[] values;
            columnCount = (values = MiscUtil.split(dateString, " ")).length;
            switch (columnCount) {
				case 5:
					// Expected format:
					// 09 Nov 2006 23:18:49 EST
					// or
					// 11 Mar 2011 11:43 GMT
					// or
					// Wed, 6 September 14:00:00 GMT
					if (Character.isDigit(
								values[dayOfMonthIndex = 0].charAt(0))) {
						monthIndex = 1;
						yearIndex = 2;
						timeIndex = 3;
					} else {
						dayOfMonthIndex = 1;
						monthIndex = 2;
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date()); 
						cal.set(Calendar.MILLISECOND, 0);
						yearIndex = -cal.get(Calendar.YEAR);
						timeIndex = 3;
					}
					tzIndex = 4;
					break;
				case 6:
					// Thr, 09 November 2006 23:18:49 EST
					// or
					// March 11, 2011 3:11:26 PM PST
					if (values[dayOfMonthIndex].length() > 2) {
						values[dayOfMonthIndex] =
								values[dayOfMonthIndex].substring(0, 2);
					}
					if (Character.isDigit(values[monthIndex].charAt(0))) {
						monthIndex = 0;
						yearIndex = 2;
						timeIndex = 3;
						{
							int ampmIndex;
							int ampmpos;
							if (!Character.isDigit(
									values[tzIndex = 5].charAt(0)) &&
									!Character.isDigit(
									values[ampmIndex = 4].charAt(0))
									&& ((ampmpos =
									",AM,PM,".indexOf(values[ampmIndex]))
										> 0) &&
								(ampmpos == 4)) {
								ampmAdj = 12;
							}
						}
					}
					break;
				case 7:
					// Expected format:
					// Thu, 19 Jul  2007 00:00:00 N
					yearIndex = 4;
					timeIndex = 5;
					tzIndex = -1;
					break;
				default:
				//#ifdef DLOGGING
				logger.warning("Invalid columnCount,date format: " + columnCount + "," + dateString);
				//#endif
				//#ifdef DTEST
				for (int ic = 0; ic < dateString.length(); ic++) {
					System.out.println("date=" + ic + "," + dateString.charAt(ic) + "," + (int)dateString.charAt(ic));
				}
				//#endif
                throw new Exception("Invalid columnCount,date format: " + columnCount + "," + dateString);
            }
            
            // Day of month
            int dayOfMonth = Integer.parseInt( values[ dayOfMonthIndex ] );
            
            // Month
            String[] months =  {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String monthString = values[ monthIndex ];
            int month=0;
            for(int monthEnumIndex=0; monthEnumIndex<11; monthEnumIndex++) {
                if( monthString.startsWith( months[ monthEnumIndex ] )) {
                    month = monthEnumIndex;
                }
            }
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("parse month,monthString=" + month + "," + monthString);}
			//#endif
            
            // Year
            int year;
			if (yearIndex >= 0) {
				year = Integer.parseInt(values[ yearIndex ]);
				if(year<100) {
					year += 2000;
				}
			} else {
				year = -yearIndex;
			}
            
            // Time
            String[] timeValues = MiscUtil.split(values[ timeIndex ],":");
            int hours = Integer.parseInt( timeValues[0] );
			if (ampmAdj > 0) {
				hours += ampmAdj;
			}
            int minutes = Integer.parseInt( timeValues[1] );
            int seconds;
			{
				int secIndex;
				if (timeValues.length > (secIndex = 2)) {
					seconds = Integer.parseInt( timeValues[secIndex] );
				} else {
					seconds = 0;
				}
			}
            
			Date pubDate = getCal(
					dayOfMonth, month, year, hours, minutes, seconds);
			if ((tzIndex == -1) || (tzIndex >= values.length)) {
				objs = new Object[] {pubDate, null};
			} else {
				final String stz;
				objs = new Object[] {pubDate, (stz = values[tzIndex])};
				//#ifdef DLOGGING
				logger.finest("parseStdDateTZ values.length,tzIndex,stz=" + values.length + "," + tzIndex + "," + stz);
				//#endif
			}
            
        } catch(Exception ex) {
            // TODO: Add exception handling code
            System.err.println("parseStdDate error while converting date string to object " + 
				"columnCount,date format: " +

                    columnCount + "," + dateString + "," + ex.toString());
			//#ifdef DLOGGING
			logger.severe("parseStdDateTZ   error while converting date " +
						   "string to object columnCount,date format: " +
						   columnCount + "," + dateString, ex);
			//#endif
        } catch(Throwable t) {
            // TODO: Add exception handling code
            System.err.println("parseStdDate error while converting date string to object " + 
					"columnCount,date format,throwable: " + columnCount + "," +
                    dateString + "," + t.toString());
			//#ifdef DLOGGING
			logger.severe("parseStdDateTZ   error while converting date " +
						   "string to object " +
                    "columnCount,date format: " + columnCount + "," +
					dateString, t);
			//#endif
        }
        return objs;
    }
    
    /**
     * Parse RSS date format to Date object.
     * Example of RSS date:
     * Sat, 23 Sep 2006 22:25:11 +0000
     */
    public static Date parseStdDate(String dateString) {
		Object[] objs = parseStdDateTZ(dateString);
		if (objs == null) {
			return null;
		} else {
			return (Date)objs[0];
		}
	}

    /**
     * Parse RSS date dc:date or atom format to Date object.
     * Example of RSS dc:date:
     * 2007-07-31T02:02:00+00:00
	 * atom date
     * [-]CCYY-MM-DDThh:mm:ss[Z|(+|-)hh:mm]
     */
    public static Date parseDcDate(String dateString) {
        Date pubDate = null;
        try {
            // Split date string to values
            // 0 = year (could be with either 4 or 2 digits)
            // 1 = month
            // 2 = day of month/time
            int dayOfMonthTimeIndex = 2;
			if (dateString.charAt(0) == '-') {
				dateString = dateString.substring(1);
			}
            
            String[] values = MiscUtil.split(dateString, "-");

            if( values.length<3 ) {
                throw new Exception("Invalid date format: " + dateString);
            }
            
            int year;
			{
				int yearIndex;
				year = Integer.parseInt(values[ yearIndex = 0 ]);
			}
            
            // Month
            int month;
			{
				int monthIndex;
				month = Integer.parseInt( values[ monthIndex = 1 ] );
			}
            
            // Day of month
			String sdayTime;
            String sdayOfMonth = (sdayTime =
					values[ dayOfMonthTimeIndex ]).substring(0,
					Math.min(2, sdayTime.length()));

            int dayOfMonth = Integer.parseInt( sdayOfMonth );
            
            String [] timeValues;
            int hours;
            int minutes;
            int seconds;
            String time;
            if ((sdayTime.length() > sdayOfMonth.length()) &&
				((time = sdayTime.substring(sdayOfMonth.length() + 1)).length()
				 > 1) && (timeValues = MiscUtil.split(time, ":")).length >= 3) {
				hours = Integer.parseInt( timeValues[0] );
				String sminutes;
				if ((sminutes = timeValues[1]).length() > 2) {
					minutes = Integer.parseInt( sminutes.substring(0, 2) );
					timeValues[2] = sminutes.substring(3, 5);
				} else {
					minutes = Integer.parseInt( sminutes );
					timeValues[2] = timeValues[2].substring( 0, 2 );
				}
				seconds = Integer.parseInt( timeValues[2] );
			} else {
				hours = 0;
				minutes = 0;
				seconds = 0;
			}
            
            pubDate = getCal(dayOfMonth, month - 1 + Calendar.JANUARY, year,
					hours, minutes, seconds);
            
        } catch(Exception ex) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("RssFormatParser");
			logger.warning("parseDcDate error while converting date " +
						   "string to object: " +
                    dateString, ex);
			//#endif
            // TODO: Add exception handling code
            System.err.println("parseDcDate error while converting date string to object: " +
                    dateString + "," + ex.toString());
        } catch(Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("RssFormatParser");
			logger.severe("parseDcDate error while converting date " +
						   "string to object: " +
                    dateString, t);
			//#endif
            // TODO: Add exception handling code
            System.err.println("parseDcDate error while converting date string to object: " +
                    dateString + "," + t.toString());
        }
        return pubDate;
    }
    
	public static String stdDate(Date cdate, String tz) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(cdate); 
		cal.setTimeZone(TimeZone.getTimeZone(tz)); 
		StringBuffer sdate = new StringBuffer();
		final int doff = cal.get(Calendar.DAY_OF_WEEK) * 3 - 3;
		sdate.append("SunMonTueWedThuFriSat".substring(doff, doff + 3));
		sdate.append(", ");
		final int dm = cal.get(Calendar.DAY_OF_MONTH);
		sdate.append(dm);
		sdate.append(" ");
		final int moff = cal.get(Calendar.MONTH) * 3;
		sdate.append("JanFebMarAprMayJunJulAugSepNovOctDec".substring(moff,
					moff + 3));
		sdate.append(" ");
		final int syear = cal.get(Calendar.YEAR);
		int year;
		if (syear < 100) {
			year = syear + 1900;
		} else {
			year = syear;
		}
		sdate.append(year);
		sdate.append(" ");
		final int hour = cal.get(Calendar.HOUR_OF_DAY);
		String shour = Integer.toString(hour);
		sdate.append("00".substring(0, 2 - shour.length()));
		sdate.append(shour);
		sdate.append(":");
		final int min = cal.get(Calendar.MINUTE);
		final String smin = Integer.toString(min);
		sdate.append("00".substring(0, 2 - smin.length()));
		sdate.append(smin);
		sdate.append(":");
		final int sec = cal.get(Calendar.SECOND);
		final String ssec = Integer.toString(sec);
		sdate.append("00".substring(0, 2 - ssec.length()));
		sdate.append(ssec);
		sdate.append(" ");
		sdate.append(tz);
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("RssFormatParser");
		logger.finest("stdDate doff,dm,moff,syear,year,hour,min,sec,tz=" + doff + "," + dm + "," + moff + "," + syear + "," + year + "," + hour + "," + min + "," + sec + "," + tz);
		logger.finest("stdDate sdate=" + sdate.toString());
		//#endif
		return sdate.toString();
	}

}
//#endif
