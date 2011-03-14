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
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-03-17 1.11.5Dev17 If item date has an error, save the string value ass errDate.
 */

// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businesslogic.compatibility4;
//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssFeed;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesItem;
import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItem;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.SgmlParserIntr;
import com.substanceofcode.rssreader.businesslogic.SgmlFormatParser;
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
public class RssFormatParser implements SgmlFormatParser {
    
	final static public String stimeZones;
	final static public byte GMT;
	static {
		String[] timeZones = TimeZone.getAvailableIDs();
		StringBuffer sb = new StringBuffer();
		sb.append(MiscUtil.join(timeZones, ",", 0));
		sb.insert(0, "UTC,");
		stimeZones = sb.toString();
		GMT = (byte)stimeZones.indexOf(",GMT,");
	}

	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.RssFormatParser");
	//#endif
	/** RSS item properties */
	private boolean m_hasExt = false;
	//#ifdef DLOGGING
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
	private String m_title = "";
	private String m_author = "";
	private String m_description = "";
	private String m_firstLink = "";
	private String m_link = "";
	private String m_language = "";
	private String m_date = "";
	private String m_enclosure = "";
	private ExtParser m_extParser = new ExtParser();

    public RssItunesFeedInfo parse(SgmlParserIntr parser, RssItunesFeedInfo cfeed,
			            int maxItemCount, boolean getTitleOnly)
	throws IOException {
        
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parse cfeed.getName(),cfeed.getUrl(),maxItemCount,getTitleOnly=" + cfeed.getName() + "," + cfeed.getUrl() + "," + + maxItemCount + "," + getTitleOnly);}
		//#endif
        Vector items = new Vector();
		m_extParser.parseNamespaces(parser);
		m_hasExt = m_extParser.isHasExt();
		RssItunesFeedInfo feed = cfeed;
        feed.setVecItems(items);
        
        /** Parse to first entry element */
        while(!parser.getName().equals("item")) {
            switch (parser.parse()) {
				case SgmlParserIntr.END_DOCUMENT:
					System.out.println("No entries found.");
					return feed;
				case SgmlParserIntr.ELEMENT:
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
							 if (elementName.equals("language")) {
								 m_language = parser.getText();
								 //#ifdef DLOGGING
								 if (finestLoggable) {logger.finest("m_language=" + m_language);}
								 //#endif
								 continue;
							 }
							 break;
						case 'i':
							 if (elementName.equals("image")) {
								 // Skip image text as it includes link
								 // and title.
								 parser.getText();
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
		feed.setLink(m_link);
		if (m_firstLink.length() > 0) {
			((RssFeed)feed).setFirstLink(m_firstLink);
		}
		if (m_date.length() > 0) {
			Date pubDate = parseRssDate(m_date);
			if (pubDate == null) {
				((RssFeed)feed).setErrDate(m_date);
			} else {
				feed.setDate(pubDate);
			}
		} else {
			feed.setDate(null);
		}
		if (m_extParser.isItunes()) {
			feed = m_extParser.getFeedInstance(feed, m_language, m_title,
					m_description);
		}
        
		reset();

		/** Parse next element */            
        int parsingResult;
        while( (parsingResult = parser.parse()) !=SgmlParserIntr.END_DOCUMENT ) {
			if (parsingResult != SgmlParserIntr.ELEMENT) {
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
						RssItunesItemInfo item = createItem();
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
						parser.getText();
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
		RssItunesItemInfo item = createItem();
		if ( item != null) {
            items.addElement( item );
        }        
                        
		return feed;
    }
    
	/** Save previous entry */
	final private RssItunesItemInfo createItem() {
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
			RssItunesItemInfo item;
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
				item = new RssItunesItem(m_title, m_link,
						m_description, pubDate,
						m_enclosure, true);
			}
			if ((pubDate == null) && (m_date.length() > 0)) {
				((RssItem)item).setErrDate(m_date);
			}
			return item;
		}
		return null;
	}

	private void reset() {
		m_title = "";
		m_author = "";
		m_description = "";
		m_firstLink = "";
		m_link = "";
		m_language = "";
		m_date = "";
		m_enclosure = "";
		if (m_hasExt) {
			m_extParser.reset();
		}
	}

	/* Parse the fields common to feed and item. */
	private boolean parseCommon(SgmlParserIntr parser, char elemChar,
			String elementName)
	throws IOException {
		switch (elemChar) {
			case 'p':
				if( elementName.equals("pubDate")) {
					m_date = parser.getText();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_date=" + m_date);}
					//#endif
					return true;
				}
				break;
			case 't':
				if( elementName.equals("title") ) {
					m_title = parser.getText();
					m_title = MiscUtil.removeHtml( m_title );
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_title=" + m_title);}
					//#endif
					return true;
				}
				break;
			case 'd':
				if( elementName.equals("description")) {
					m_description = parser.getText();
					m_description = MiscUtil.removeHtml( m_description );
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_description=" + m_description);}
					//#endif
					return true;
				}
				break;
			case 'l':
				if( elementName.equals("link") ) {
					m_link = parser.getText();
					m_link = MiscUtil.removeHtml( m_link );
					if ((m_link.length() > 0) && (m_firstLink.length() == 0)) {
						m_firstLink = m_link;
					}
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
	void parseItem(SgmlParserIntr parser, char elemChar, String elementName)
	throws IOException {
		switch (elemChar) {
			case 'a':
				if( elementName.equals("author")) {
					m_author = parser.getText();
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
		Logger logger = Logger.getLogger("compatibility4.RssFormatParser");
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
		Logger logger = Logger.getLogger("compatibility4.RssFormatParser");
		//#endif
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
            int tzIndex = 5;
            
			int kc = 0;
            while ((dateString.indexOf("  ") >= 0) &&
					(kc++ < dateString.length())) {
				dateString = MiscUtil.replace(dateString, "  ", " ");
			}

            String[] values = MiscUtil.split(dateString, " ");
            int columnCount = values.length;
            if( columnCount==5 ) {
                // Expected format:
                // 09 Nov 2006 23:18:49 EST
                dayOfMonthIndex = 0;
                monthIndex = 1;
                yearIndex = 2;
                timeIndex = 3;
                tzIndex = 4;
			} else if( columnCount==7 ) {
                // Expected format:
                // Thu, 19 Jul  2007 00:00:00 N
                yearIndex = 4;
                timeIndex = 5;
                tzIndex = -1;
            } else if( columnCount<5 || columnCount>6 ) {
				//#ifdef DLOGGING
				logger.warning("Invalid date format: " + dateString);
				//#endif
				//#ifdef DTEST
				for (int ic = 0; ic < dateString.length(); ic++) {
					System.out.println("date=" + ic + "," + dateString.charAt(ic) + "," + (int)dateString.charAt(ic));
				}
				//#endif
                throw new Exception("Invalid date format: " + dateString);
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
                if( monthString.equals( months[ monthEnumIndex ] )) {
                    month = monthEnumIndex;
                }
            }
            
            // Year
            int year = Integer.parseInt(values[ yearIndex ]);
            if(year<100) {
                year += 2000;
            }
            
            // Time
            String[] timeValues = MiscUtil.split(values[ timeIndex ],":");
            int hours = Integer.parseInt( timeValues[0] );
            int minutes = Integer.parseInt( timeValues[1] );
            int seconds = Integer.parseInt( timeValues[2] );
            
			Date pubDate = getCal(
					dayOfMonth, month, year, hours, minutes, seconds);
			objs = new Object[2];
			objs[0] = pubDate;
			if ((tzIndex == -1) || (tzIndex >= values.length)) {
				objs[1] = null;
			} else {
				final String stz = values[tzIndex];
				//#ifdef DLOGGING
				logger.finest("parseStdDateTZ values.length,tzIndex,stz=" + values.length + "," + tzIndex + "," + stz);
				//#endif
				objs[1] = new Byte(
						(byte)RssFormatParser.stimeZones.indexOf("," + stz + ","));
			}
            
        } catch(Exception ex) {
            // TODO: Add exception handling code
            System.err.println("parseStdDate error while converting date string to object: " + 
                    dateString + "," + ex.toString());
			//#ifdef DLOGGING
			logger.severe("parseStdDateTZ   error while converting date " +
						   "string to object: " +
                    dateString, ex);
			//#endif
        } catch(Throwable t) {
            // TODO: Add exception handling code
            System.err.println("parseStdDate error while converting date string to object: " + 
                    dateString + "," + t.toString());
			//#ifdef DLOGGING
			logger.severe("parseStdDateTZ   error while converting date " +
						   "string to object: " +
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
            int yearIndex = 0;
            int monthIndex = 1;
            int dayOfMonthTimeIndex = 2;
			if (dateString.charAt(0) == '-') {
				dateString = dateString.substring(1);
			}
            
            String[] values = MiscUtil.split(dateString, "-");

            if( values.length<3 ) {
                throw new Exception("Invalid date format: " + dateString);
            }
            
            int year = Integer.parseInt(values[ yearIndex ]);
            
            // Month
            int month = Integer.parseInt( values[ monthIndex ] );
            
            // Day of month
            String sdayOfMonth = values[ dayOfMonthTimeIndex ].substring(0, 2);

            int dayOfMonth = Integer.parseInt( sdayOfMonth );
            
            String time = values[ dayOfMonthTimeIndex ].substring(3);
            String [] timeValues = MiscUtil.split(time, ":");

            int hours = Integer.parseInt( timeValues[0] );
            int minutes = Integer.parseInt( timeValues[1] );
            timeValues[2] = timeValues[2].substring( 0, 2 );
            int seconds = Integer.parseInt( timeValues[2] );
            
            pubDate = getCal(dayOfMonth, month - 1 + Calendar.JANUARY, year,
					hours, minutes, seconds);
            
        } catch(Exception ex) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("compatibility4.RssFormatParser");
			logger.warning("parseDcDate error while converting date " +
						   "string to object: " +
                    dateString, ex);
			//#endif
            // TODO: Add exception handling code
            System.err.println("parseDcDate error while converting date string to object: " +
                    dateString + "," + ex.toString());
        } catch(Throwable t) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("compatibility4.RssFormatParser");
			logger.severe("parseStdDateTZ  error while converting date " +
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
		sdate.append(Integer.toString(dm));
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
		sdate.append(Integer.toString(year));
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
		Logger logger = Logger.getLogger("compatibility4.RssFormatParser");
		logger.finest("stdDate doff,dm,moff,syear,year,hour,min,sec,tz=" + doff + "," + dm + "," + moff + "," + syear + "," + year + "," + hour + "," + min + "," + sec + "," + tz);
		logger.finest("stdDate sdate=" + sdate.toString());
		//#endif
		return sdate.toString();
	}

}
//#endif
