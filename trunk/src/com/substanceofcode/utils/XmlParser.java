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

package com.substanceofcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Simple and lightweight XML parser without complete error handling.
 *
 * @author Tommi Laukkanen
 */
public class XmlParser {
    
    private InputStream m_inputStream = null;
    
    /** Current XML element name (eg. <title> = title) */
    private String m_currentElementName = "";
    private String m_currentElementData = "";
    private boolean m_currentElementContainsText = false;
    private static String encoding = "UTF-8";
    private String namespace = null;
    
    /** Enumerations for parse function */
    public static final int END_DOCUMENT = 0;
    public static final int ELEMENT = 1;
    
    /** Creates a new instance of XmlParser */
    public XmlParser(InputStream inputStream) {
        m_inputStream = inputStream;
    }
    
    /** Parse next element */
    public int parse() throws IOException {
        StringBuffer inputBuffer = new StringBuffer();
        
        boolean parsingElementName = false;
        boolean elementFound = false;
        boolean elementStart = false;
        boolean parsingElementData = false;
                
        int inputCharacter;
        char c;
        inputCharacter = m_inputStream.read();
        while (inputCharacter != -1 && elementFound==false) {
            c = (char)inputCharacter;
            
            if(c=='/' && elementStart==true) {
                parsingElementName = false;
            }
            else if(elementStart==true && (c=='?' || c=='!')) {
                if(m_currentElementData.charAt(m_currentElementData.length()-1)=='<') {
                    parsingElementName = false;
                }
            }
            if(parsingElementName==true) {
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
            if(parsingElementData==true) {
                m_currentElementData += c;
            }
            if(c=='>') {
                if(m_currentElementName.length()>0) {
                    elementFound = true;
                    parsingElementName = false;
                }
            }    

            if(!elementFound){
                inputCharacter = m_inputStream.read();
            }
        }
        
        if( m_currentElementData.charAt( m_currentElementData.length()-2 )=='/' &&
            m_currentElementData.charAt( m_currentElementData.length()-1 )=='>' ) {
            m_currentElementContainsText = false;
        } else {
            m_currentElementContainsText = true;
        }
        
        if( inputCharacter==-1 ) {
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
        
        if(m_currentElementContainsText==false) {
            return "";
        }
        boolean endParsing = false;
        
        String endElementName = "";
        String text;
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
        while ((inputCharacter = m_inputStream.read()) != -1 && endParsing==false) {
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
		if (encoding.equals("")) {
			text = textBuffer.toString();
		} else {
			try {
				text = new String(textBuffer.toString().getBytes(), encoding);
			} catch (UnsupportedEncodingException e) {
				try {
					text = new String(textBuffer.toString().getBytes(), "UTF8");
					encoding = "UTF8";
				} catch (UnsupportedEncodingException e2) {
					text = textBuffer.toString();
					encoding = "";
				}
			}
		}
        text = StringUtil.replace(text, "</" + m_currentElementName, "");
        
        /** Handle some entities and encoded characters */
        text = StringUtil.replace(text, "<![CDATA[", "");
        text = StringUtil.replace(text, "]]>", "");
        text = StringUtil.replace(text, "&lt;", "<");
        text = StringUtil.replace(text, "&gt;", ">");
        text = StringUtil.replace(text, "&nbsp;", " ");
        text = StringUtil.replace(text, "&amp;", "&");
        text = StringUtil.replace(text, "&apos;", "'");
        text = StringUtil.replace(text, "&auml;", "ä");
        text = StringUtil.replace(text, "&ouml;", "ö");
        text = StringUtil.replace(text, "Ã¤", "ä");
        text = StringUtil.replace(text, "Ã¶", "ö");
        text = StringUtil.replace(text, "&#8217;", "'");
        text = StringUtil.replace(text, "&#8216;", "'");
        text = StringUtil.replace(text, "&#8220;", "\"");
        text = StringUtil.replace(text, "&#8221;", "\"");
        text = StringUtil.replace(text, "â‚¬", "€");
        
        return text;
    }

    /** 
     * Get attribute value from current element 
     */
    public String getAttributeValue(String attributeName) {
        
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
    }
    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return (namespace);
    }

}
