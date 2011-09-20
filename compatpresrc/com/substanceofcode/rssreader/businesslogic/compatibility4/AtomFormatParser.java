//--Need to modify--#preprocess
/*
 * AtomParser.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2010 Irving Bunton, Jr
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
 * IB 2011-03-09 1.11.5Dev17 More logging.
 * IB 2011-03-17 1.11.5Dev17 If item date has an error, save the string value ass errDate.
 */

// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DTEST
package com.substanceofcode.rssreader.businesslogic.compatibility4;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesItem;
import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
import com.substanceofcode.rssreader.businessentities.compatibility4.RssItem;
import com.substanceofcode.rssreader.businesslogic.SgmlFormatParser;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.SgmlParserIntr;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author Tommi
 */
public class AtomFormatParser implements SgmlFormatParser {
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.AtomFormatParser");
	//#endif
	private boolean m_hasExt = false;
	//#ifdef DLOGGING
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
	/** Atom item properties */
	private String m_title = "";
	private String m_language = "";
	private String m_description = "";
	private String m_summary = "";
	private String m_author = "";
	private String m_link = "";
	private String m_relLink = "";
	private String m_selfLink = "";
	private String m_altLink = "";
	private String m_enclosure = "";
	private String m_date = "";
	private String m_modified = "";
	private String m_updated = "";
	private ExtParser m_extParser = new ExtParser();

    /** Parse Atom feed */
    public RssItunesFeedInfo parse(SgmlParserIntr parser, RssItunesFeedInfo cfeed,
			            int maxItemCount, boolean getTitleOnly)
	throws IOException {
        
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("parse parser,cfeed.getName(),cfeed.getUrl(),maxItemCount,getTitleOnly=" + parser + "," + cfeed.getName() + "," + cfeed.getUrl() + "," + + maxItemCount + "," + getTitleOnly);}
		//#endif
        Vector items = new Vector();
		m_extParser.parseNamespaces(parser);
		m_language = parser.getAttributeValue("xml:lang");
		if (m_language == null) {
			m_language = "";
		}
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("m_language=" + m_language);}
		//#endif
		m_hasExt = m_extParser.isHasExt();
		RssItunesFeedInfo feed = cfeed;
        feed.setVecItems(items);
        
        /** Parse to first entry element */
        while(!parser.getName().equals("entry")) {
			//#ifdef DTEST
			//#ifndef DTESTUI
            System.out.println("Parsing to first entry");
			//#endif
			//#endif
            switch (parser.parse()) {
				case SgmlParserIntr.END_DOCUMENT:
					System.out.println("No entries found.");
					return feed;
				case SgmlParserIntr.ELEMENT:
					String elementName = parser.getName();
					char elemChar = elementName.charAt(0);
					if (parseCommon(parser, elemChar, elementName) &&
						(elemChar == 't') && 
								getTitleOnly && elementName.equals("title") ) {
						feed.setName(m_title);
						return feed;
					}
					if ((elemChar == 's') &&
						elementName.equals("subtitle") ) {
						m_description = parser.getText();
						m_description = MiscUtil.removeHtml(
								m_description ).trim();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("m_description=" + m_description);}
						//#endif
						continue;
					}
					if (m_hasExt) {
						m_extParser.parseExtItem(parser, elemChar,
												 elementName);
					}
					break;
				default:
					break;
            }
        }

		feed.setLink(m_link);
		// Atom has not feed level date.
		feed.setDate(null);
		if (m_extParser.isItunes()) {
			feed = m_extParser.getFeedInstance(feed, m_language,
					m_title, m_description);
		}
        
		reset();

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
            if( (elemChar == 'e') &&
				 elementName.equals("entry") ) {
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
            } else {
				if (parseCommon(parser, elemChar, elementName)) {
					continue;
				}
				parseItem(parser, elemChar, elementName);
			}
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
		if(hasTitle || hasDesc || (m_summary.length()>0)) {
			if (!hasDesc) {
				m_description = m_summary;
				hasDesc = true;
			}
			if (hasTitle && hasDesc) {
				m_title = m_title.replace('\n', ' ');
				m_title = m_title.trim();
			}
			if (m_link.length() == 0) {
				if (m_selfLink.length() != 0) {
					m_link = m_selfLink;
				} else if (m_relLink.length() != 0) {
					m_link = m_relLink;
				} else if (m_altLink.length() != 0) {
					m_link = m_altLink;
				}
			}
			m_link = MiscUtil.removeHtml( m_link ).trim();
			Date pubDate = null;
			// Check date in case we cannot find it.
			if (m_date.length() == 0) {
				if (m_updated.length() > 0) {
					m_date = m_updated;
				} else {
					m_date = m_modified;
				}
			}
			if ((m_date.length() == 0) && m_extParser.isHasExt()) {
				m_date = m_extParser.getDate();
			}
			if (m_date.length() > 0) {
				pubDate = RssFormatParser.parseRssDate(m_date);
			}
			RssItunesItemInfo item;
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
		m_language = "";
		m_description = "";
		m_author = "";
		m_link = "";
		m_date = "";
		m_updated = "";
		m_modified = "";
		m_enclosure = "";
		m_summary = "";
		m_relLink = "";
		m_selfLink = "";
		m_altLink = "";
		if (m_hasExt) {
			m_extParser.reset();
		}
	}

	/* Parse the fields common to feed and item. */
	private boolean parseCommon(SgmlParserIntr parser, char elemChar,
			String elementName)
	throws IOException {
		switch (elemChar) {
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
			case 'l':
				if( elementName.equals("link") ) {
					String clink = parser.getText().trim();
					// Some atoms have href= attribute.
					if (clink.length() == 0) {
						String hlink = parser.getAttributeValue("href");
						if (hlink != null) {
							clink = hlink;
						}
					}
					String rel = parser.getAttributeValue("rel");
					if ((rel == null) || (rel.length() == 0)) {
						m_link = clink;
						return true;
					} else {
						switch (rel.charAt(0)) {
							case 'e':
								// Only get the first m_enclosure.  Atom's can have
								// multiple enclosures for the same item.
								if (rel.equals("enclosure") && m_enclosure.equals("")) {
									 m_enclosure = clink;
									 return true;
								}
								break;
							case 'r':
								if (rel.equals("related")) {
									m_relLink = clink;
									return true;
								}
								break;
							case 's':
								if (rel.equals("self")) {
									m_selfLink = clink;
									return true;
								}
								break;
							case 'a':
								if (rel.equals("alternate")) {
									m_altLink = clink;
									return true;
								}
								break;
							default:
						}
					}
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
				if( m_hasExt && elementName.equals("author") ) {
					m_author = parser.getText();
					m_author = MiscUtil.removeHtml( m_author );
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_author=" + m_author);}
					//#endif
				}
				break;
			case 'c':
				if( elementName.equals("content")) {
					m_description = parser.getText();
					m_description = MiscUtil.removeHtml( m_description );
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("content=m_description=" + m_description);}
					//#endif
				}
				break;
			case 's':
				if( elementName.equals("summary")) {
					m_summary = parser.getText();
					m_summary = MiscUtil.removeHtml( m_summary );
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_summary=" + m_summary);}
					//#endif
				}
				break;
			case 'u': // Updated for Atom 1.0
				if( elementName.equals("updated")) {
					m_updated = parser.getText();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("published=m_updated=" + m_updated);}
					//#endif
				}
				break;
			case 'm': // Modified for Atom 0.3
				if( elementName.equals("modified")) {
					m_modified = parser.getText();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("published=m_modified=" + m_modified);}
					//#endif
				}
				break;
			case 'p': // Published
				if( elementName.equals("published")) {
					m_date = parser.getText();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("published=m_date=" + m_date);}
					//#endif
				}
				break;
			default:
		}
	}

}
//#endif
