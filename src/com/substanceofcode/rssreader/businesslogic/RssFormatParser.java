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

package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItem;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Tommi
 */
public class RssFormatParser implements FeedFormatParser {
    
    /** Creates a new instance of RssFormatParser */
    public RssFormatParser() {
    }
    
    public Vector parse(XmlParser parser, int maxItemCount) throws IOException {
        /** RSS item properties */
        String title = null;
        String description = null;
        String link = null;
        String date = null;
        
        Vector items = new Vector();
        
        /** Parse to first entry element */
        while(!parser.getName().equals("item")) {
            if(parser.parse()==XmlParser.END_DOCUMENT) {
                System.out.println("No entries found.");
                return items;
            }
        }
        
        int parsingResult = parser.parse();
        while( parsingResult!=XmlParser.END_DOCUMENT ) {
            String elementName = parser.getName();
            
            if( elementName.equals("item") ) {
                /** Save previous entry */
                if(title.length()>0) {
                    RssItem item;
                    Date pubDate = null;
					// Check date in case we cannot find it.
					if (date != null) {
						if (date.indexOf("-") >= 0) {
							pubDate = parseDcDate(date);
						} else {
							pubDate = parseRssDate(date);
						}
					}
                    if(pubDate!=null) {
                        item = new RssItem(title, link, description, pubDate);
                    } else {
                        item = new RssItem(title, link, description);
                    }
                    items.addElement( item );
                    if(items.size()==maxItemCount) {
                        return items;
                    }
                }                
                
                /** New entry */
                title = "";
                description = "";
                link = "";
                date = "";
            }
            else if( elementName.equals("title") ) {
                title = parser.getText();
                title = StringUtil.removeHtml( title );
            }
            else if( elementName.equals("link") ) {
                link = parser.getText();
            }
            else if( elementName.equals("description")) {
                description = parser.getText();
                description = StringUtil.removeHtml( description );
            }
            else if( elementName.equals("pubDate") ||
                     elementName.equals("dc:date")) {
                date = parser.getText();
            }
            
            /** Parse next element */            
            parsingResult = parser.parse();
        }

        /** Save previous entry */
        if(title.length()>0) {
            RssItem item;
            Date pubDate = parseRssDate(date);
            if(pubDate!=null) {
                item = new RssItem(title, link, description, pubDate);
            } else {
                item = new RssItem(title, link, description);
            }
            items.addElement( item );
        }        
                        
        return items;
    }
    
	/** Get calendar date. **/
	private Date getCal(int dayOfMonth, int month, int year, int hours,
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

    /**
     * Parse RSS date format to Date object.
     * Example of RSS date:
     * Sat, 23 Sep 2006 22:25:11 +0000
     */
    private Date parseRssDate(String dateString) {
        Date pubDate = null;
        try {
            // Split date string to values
            // 0 = week day
            // 1 = day of month
            // 2 = month
            // 3 = year (could be with either 4 or 2 digits)
            // 4 = time
            // 5 = GMT
            int weekDayIndex = 0;
            int dayOfMonthIndex = 1;
            int monthIndex = 2;
            int yearIndex = 3;
            int timeIndex = 4;
            int gmtIndex = 5;
            
            String[] values = StringUtil.split(dateString, " ");
            int columnCount = values.length;
            if( columnCount==5 ) {
                // Expected format:
                // 09 Nov 2006 23:18:49 EST
                dayOfMonthIndex = 0;
                monthIndex = 1;
                yearIndex = 2;
                timeIndex = 3;
                gmtIndex = 4;
            } else if( columnCount<5 || columnCount>6 ) {
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
                if( monthString.equals( months[ monthEnumIndex ] )==true) {
                    month = monthEnumIndex;
                }
            }
            
            // Year
            int year = Integer.parseInt(values[ yearIndex ]);
            if(year<100) {
                year += 2000;
            }
            
            // Time
            String[] timeValues = StringUtil.split(values[ timeIndex ],":");
            int hours = Integer.parseInt( timeValues[0] );
            int minutes = Integer.parseInt( timeValues[1] );
            int seconds = Integer.parseInt( timeValues[2] );
            
            pubDate = getCal(dayOfMonth, month, year, hours, minutes, seconds);
            
        } catch(Exception ex) {
            // TODO: Add exception handling code
            System.err.println("Error while convertin date string to object: " +
                    ex.toString());
        }
        return pubDate;
    }
    
    /**
     * Parse RSS date dc:date format to Date object.
     * Example of RSS dc:date:
     * 2007-07-31T02:02:00+00:00
     */
    private Date parseDcDate(String dateString) {
        Date pubDate = null;
        try {
            // Split date string to values
            // 0 = year (could be with either 4 or 2 digits)
            // 1 = month
            // 2 = day of month/time
            int yearIndex = 0;
            int monthIndex = 1;
            int dayOfMonthTimeIndex = 2;
            
            String[] values = StringUtil.split(dateString, "-");

            if( values.length!=3 ) {
                throw new Exception("Invalid date format: " + dateString);
            }
            
            int year = Integer.parseInt(values[ yearIndex ]);
            
            // Month
            int month = Integer.parseInt( values[ monthIndex ] );
            
            int dayOfMonthIndex = 0;
            // Time
            String[] dayTimeValues = StringUtil.split(values[ dayOfMonthTimeIndex ],":");
            // Day of month
            String sdayOfMonth = values[ dayOfMonthTimeIndex ].substring(0, 2);

            int dayOfMonth = Integer.parseInt( sdayOfMonth );
            
            String time = values[ dayOfMonthTimeIndex ].substring(3);
            String [] timeValues = StringUtil.split(time, ":");

            int hours = Integer.parseInt( timeValues[0] );
            int minutes = Integer.parseInt( timeValues[1] );
            timeValues[2] = timeValues[2].substring( 0, 2 );
            int seconds = Integer.parseInt( timeValues[2] );
            
            pubDate = getCal(dayOfMonth, month, year, hours, minutes, seconds);
            
        } catch(Exception ex) {
            // TODO: Add exception handling code
            System.err.println("Error while convertin date string to object: " +
                    ex.toString());
        }
        return pubDate;
    }
    
}
