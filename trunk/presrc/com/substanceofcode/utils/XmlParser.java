/*
 * XmlParser.java
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

// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif
/**
 * Simple and lightweight XML parser without complete error handling.
 *
 * @author Tommi Laukkanen
 */
public class XmlParser {
    
    /** Current XML element name (eg. <title> = title) */
    private String m_currentElementName = "";
    private String m_currentElementData = "";
    private boolean m_currentElementContainsText = false;
    private String fileEncoding = "ISO8859_1";  // See EncodingUtil
    private String docEncoding = "";  // See EncodingUtil
    private EncodingUtil encodingUtil = null;
    private String namespace = null;
    private boolean getPrologue = true;
    private int nextChar;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("XmlParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Enumerations for parse function */
    public static final int END_DOCUMENT = 0;
    public static final int ELEMENT = 1;
    public static final int PROLOGUE = 2;
    
    /** Creates a new instance of XmlParser */
    public XmlParser(InputStream inputStream) {
		encodingUtil = new EncodingUtil(inputStream);
		fileEncoding = encodingUtil.getFileEncoding();
    }

    /** Parse next element */
    public int parse() throws IOException {
		StringBuffer inputBuffer = new StringBuffer();
		
		boolean parsingElementName = false;
		boolean elementFound = false;
		boolean elementStart = false;
		boolean parsingElementData = false;
		boolean prologueFound = false;
				
        char c;
        int inputCharacter = encodingUtil.read();
		try {
			while ((inputCharacter != -1) && !elementFound) {
                c = (char)inputCharacter;
				
				if(c=='/' && elementStart) {
					parsingElementName = false;
				}
				else if(elementStart && (c=='?' || c=='!')) {
					if(m_currentElementData.charAt(m_currentElementData.length()-1)=='<') {
						parsingElementName = false;
						// If we find <? and we're looking for the prologue,
						// set flag.
						if (getPrologue) {
							prologueFound = true;
						}
					}
				}
				if(parsingElementName) {
					if(c==' ' || c=='/' || c==':') {
						// For specified namespace, put it into element name
						if ((c==':') && (namespace != null) &&
							namespace.equals(m_currentElementName)) {
							m_currentElementName += c;
						} else {
							parsingElementName = false;
							parsingElementData = true;
						}
					}
					else if(c!='>') {
						m_currentElementName += c;
					}
				}              
				if(c=='<') {
					elementStart = true;
					parsingElementName = true;
					parsingElementData = true;
					m_currentElementName = "";
					m_currentElementData = "";
				}            
				if(parsingElementData) {
					m_currentElementData += c;
				}
				if(c=='>') {
					if(m_currentElementName.length()>0) {
						elementFound = true;
						parsingElementName = false;
						// If we find XML without a prologue, need
						// to treat as default UTF-8 encoding for XML.
						if (getPrologue) {
							getPrologue = false;
							encodingUtil.getEncoding(null);
							docEncoding = encodingUtil.getDocEncoding();
						}
					} else if (getPrologue && prologueFound) {
						// If we are looking for the prolog, now
						// we have read the end of it, so we can
						// get the encoding specified (or null which
						// defaults to UTF-8).
						getPrologue = false;
						String cencoding = getAttributeValue("encoding");
						encodingUtil.getEncoding(cencoding);
						// Get doc encoding.  The encoding to translate
						// the bytes into.
						docEncoding = encodingUtil.getDocEncoding();
						return PROLOGUE;
					}
				}    

				if(!elementFound){
                    inputCharacter = encodingUtil.read();
				}
			}
			
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("m_currentElementData=" + m_currentElementData);}
			//#endif
			if( m_currentElementData.charAt( m_currentElementData.length()-2 )=='/' &&
				m_currentElementData.charAt( m_currentElementData.length()-1 )=='>' ) {
				m_currentElementContainsText = false;
			} else {
				m_currentElementContainsText = true;
			}
			
		} catch (IOException e) {
//#ifdef DLOGGING
			logger.severe("parse read error ");
//#endif
			System.out.println("parse read error " + e + " " + e.getMessage());
			throw e;
		}
		if( inputCharacter == -1 ) {
			return END_DOCUMENT;
		} else {
			return ELEMENT;
		}
    }
    
    /** Get element name */
    public String getName() {
        return m_currentElementName;
    }
    
    /** Get element text including inner xml */
    public String getText() throws IOException {
        
		if(!m_currentElementContainsText) {
			return "";
		}
		boolean endParsing = false;
		
		String endElementName = "";
		String text = "";
		try {
			StringBuffer textBuffer = new StringBuffer();
			int inputCharacter;
			char c;
			char lastChars[] = new char[3];
			lastChars[0] = ' ';
			lastChars[1] = ' ';
			lastChars[2] = ' ';
			
			char elementNameChars[] = new char[3];
			elementNameChars[0] = m_currentElementName.charAt( m_currentElementName.length()-3 );
			elementNameChars[1] = m_currentElementName.charAt( m_currentElementName.length()-2 );
			elementNameChars[2] = m_currentElementName.charAt( m_currentElementName.length()-1 );
			while (((inputCharacter = encodingUtil.read()) != -1) &&
					!endParsing) {
				c = (char)inputCharacter;
				lastChars[0] = lastChars[1];
				lastChars[1] = lastChars[2];
				lastChars[2] = c;
				//System.out.print(c);

				textBuffer.append(c);
				if( lastChars[0] == elementNameChars[0] &&
					lastChars[1] == elementNameChars[1] &&
					lastChars[2] == elementNameChars[2]) {
					if( textBuffer.toString().endsWith("</" + m_currentElementName)) {
						endParsing = true;
					}
				}
			}

			if (docEncoding.equals("")) {
				text = textBuffer.toString();
			} else {
				// We read the bytes in as ISO8859_1, so we must get them
				// out as that and then encode as they should be.
				if (fileEncoding.equals("")) {
					text = new String(textBuffer.toString().getBytes(),
									  docEncoding);
				} else {
					text = new String(textBuffer.toString().getBytes(
								fileEncoding), docEncoding);
				}
			}
			text = StringUtil.replace(text, "</" + m_currentElementName, "");
			
			/** Handle some entities and encoded characters */
			text = StringUtil.replace(text, "<![CDATA[", "");
			text = StringUtil.replace(text, "]]>", "");
			text = replaceAlphaEntities(text);
			// No need to convert from UTF-8 to Unicode using replace
			// umlauts now because it is done with new String...,encoding.

			// Replace numeric entities including &#8217;, &#8216;
			// &#8220;, and &#8221;
			text = encodingUtil.replaceNumEntity(text);

			// Replace special chars like left quote, etc.
			text = encodingUtil.replaceSpChars(text);
			
		} catch (Throwable t) {
//#ifdef DLOGGING
			logger.severe("getText Could not read a char run time.", t);
//#endif
			System.out.println("getText Could not read a char run time." + t +
					           " " + t.getMessage());
		}
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("text=" + text);}
		//#endif
		return text;
    }

	/**
	  Replace alphabetic entities.
	  */
	public static String replaceAlphaEntities(String text) {
		text = StringUtil.replace(text, "&lt;", "<");
		text = StringUtil.replace(text, "&gt;", ">");
		text = StringUtil.replace(text, "&nbsp;", " ");
		text = StringUtil.replace(text, "&amp;", "&");
		text = StringUtil.replace(text, "&apos;", "'");
		text = StringUtil.replace(text, "&quot;", "\"");
		// Avoid problems with different editors by using
		// static variables.
		text = StringUtil.replace(text, "&auml;", EncodingUtil.A_UMLAUTE);
		text = StringUtil.replace(text, "&ouml;", EncodingUtil.O_UMLAUTE);
		return text;
	}

    /** 
     * Get attribute value from current element 
     */
    public String getAttributeValue(String attributeName) {
        
		try {
			/** Check whatever the element contains given attribute */
			int attributeStartIndex = m_currentElementData.indexOf(attributeName);
			if( attributeStartIndex<0 ) {
				return null;
			}
			
			/** Calculate actual value start index */
			int valueStartIndex = attributeStartIndex + attributeName.length() + 2;
			
			/** Check the attribute value end index */
			int valueEndIndex = m_currentElementData.indexOf("\"", valueStartIndex);
			if( valueEndIndex<0 ) {
				return null;
			}
			
			/** Parse value */
			String value = m_currentElementData.substring(valueStartIndex, valueEndIndex);
					
			return value;
		} catch (Throwable t) {
//#ifdef DLOGGING
			logger.severe("getAttributeValue error.", t);
//#endif
			System.out.println("getAttributeValue error." + t + " " +
					           t.getMessage());
			return null;
		}
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return (namespace);
    }

    public void setDocEncoding(String docEncoding) {
        this.docEncoding = docEncoding;
    }

    public String getDocEncoding() {
        return (docEncoding);
    }

}
