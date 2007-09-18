/*
 * AtomParser.java
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

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssItem;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.XmlParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Tommi
 */
public class AtomFormatParser implements FeedFormatParser{
    
    /** Creates a new instance of AtomParser */
    public AtomFormatParser() {
    }
    
    public Vector parse(InputStream inputStream, int maxItemCount) {
        
        Vector items = new Vector();

            // Prepare buffer for input data
            StringBuffer inputBuffer = new StringBuffer();
            
            
             
            // Read all data to buffer
            int inputCharacter;
            try {
                inputStream.reset();
                while ((inputCharacter = inputStream.read()) != -1 &&
                        inputBuffer.length()<1000) {
                    inputBuffer.append((char)inputCharacter);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
             
            // Split buffer string by each new line
            String text = inputBuffer.toString();
            System.out.println("Input: " + text);        
                
        return items;
    }
    
    /** Parse Atom feed */
    public Vector parse(XmlParser parser, RssFeed feed,
			            int maxItemCount, boolean getTitleOnly)
	throws IOException {
        /** Atom item properties */
        String title = "";
        String description = "";
        String summary = "";
        String link = "";
        String relLink = "";
        String selfLink = "";
        String altLink = "";
        String enclosure = "";
        String date = "";
        
        Vector items = new Vector();
        
        /** Parse to first entry element */
        while(!parser.getName().equals("entry")) {
            System.out.println("Parsing to first entry");
            switch (parser.parse()) {
				case XmlParser.END_DOCUMENT:
					System.out.println("No entries found.");
					return items;
				case XmlParser.ELEMENT:
					if (getTitleOnly && parser.getName().equals("title") ) {
						feed.setName(parser.getText());
						return items;
					}
					break;
				default:
					break;
            }
        }
        
        int parsingResult = parser.parse();
        while( parsingResult!=XmlParser.END_DOCUMENT ) {
            String elementName = parser.getName();
            
            if( elementName.equals("entry") ) {
                /** Save previous entry */
                if(title.length()>0) {
					if (description.equals("")) {
						if (!summary.equals("")) {
							description = summary;
						}
					}
					if (link.equals("")) {
						if (!selfLink.equals("")) {
							link = selfLink;
						} else if (!relLink.equals("")) {
							link = relLink;
						} else if (!altLink.equals("")) {
							link = altLink;
						}
					}
                    Date pubDate = null;
					// Check date in case we cannot find it.
					if (!date.equals("")) {
						pubDate = RssFormatParser.parseDcDate(date);
					}
                    RssItem item;
                    if(pubDate!=null) {
                        item = new RssItem(title, link, description, pubDate,
								           enclosure, true);
					} else {
						item = new RssItem(title, link, description);
						if (!enclosure.equals("")) {
							item.setEnclosure(enclosure);
						}
						item.setUnreadItem(true);
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
                enclosure = "";
				summary = "";
				relLink = "";
				selfLink = "";
				altLink = "";
            }
            else if( elementName.equals("title") ) {
                title = parser.getText();
                title = StringUtil.replace(title, "\n", " ");
                title = StringUtil.removeHtml( title );
            }
            else if( elementName.equals("link") ) {
                String clink = parser.getText();
				// Some atoms have href= attribute.
                if (clink.equals("")) {
					String hlink = parser.getAttributeValue("href");
					if (hlink != null) {
						clink = hlink;
					}
				}
				String rel = parser.getAttributeValue("rel");
				if (rel == null) {
					link = clink;
				} else {
					if (rel.equals("enclosure")) {
						 enclosure = clink;
					} else if (rel.equals("related")) {
						 relLink = clink;
					} else if (rel.equals("self")) {
						 selfLink = clink;
					} else if (rel.equals("alternate")) {
						 altLink = clink;
					}
				}
            }
            else if( elementName.equals("content")) {
                description = parser.getText();
                description = StringUtil.removeHtml( description );
			}
            else if( elementName.equals("summary")) {
                summary = parser.getText();
                summary = StringUtil.removeHtml( summary );
			}
            else if( elementName.equals("published")) {
                date = parser.getText();
            }
            
            /** Parse next element */            
            parsingResult = parser.parse();
        }

        /** Save previous entry */
        if(title.length()>0) {
			if (description.equals("")) {
				description = summary;
			}
			if (link.equals("")) {
				if (!selfLink.equals("")) {
					link = selfLink;
				} else if (!relLink.equals("")) {
					link = relLink;
				} else if (!altLink.equals("")) {
					link = altLink;
				}
			}
		    Date pubDate = null;
			// Check date in case we cannot find it.
			if (!date.equals("")) {
				pubDate = RssFormatParser.parseDcDate(date);
			}
			RssItem item;
			if(pubDate!=null) {
				item = new RssItem(title, link, description, pubDate,
								   enclosure, true);
			} else {
				item = new RssItem(title, link, description);
				item.setUnreadItem(true);
				if (!enclosure.equals("")) {
					item.setEnclosure(enclosure);
				}
			}
            items.addElement( item );
        }        
                        
        return items;
    }
    
}
