//--Need to modify--#preprocess
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
/*
 * IB 2010-11-29 1.11.5Dev15 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-29 1.11.5Dev14 Change package and getlogging for compatibility4.
 * IB 2010-11-29 1.11.5Dev9 Add setLogChar and isLogChar for SgmlParserIntr implementation to compatibility4 XmlParser.
 * IB 2010-11-29 1.11.5Dev9 Use compatibility4 version of EncodingUtil and EncodingStreamReader.
 * IB 2010-11-29 1.11.5Dev9 Use compatibility4 version of StringUtil.
 * IB 2010-11-29 1.11.5Dev9 Allow test to get the EncodingUtilIntr interface.
 */

// Expand to define testing define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.utils.compatibility4;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.substanceofcode.utils.SgmlParserIntr;
import com.substanceofcode.utils.compatibility4.EncodingUtil;
//#ifdef DTEST
import com.substanceofcode.utils.EncodingUtilIntr;
//#endif
import com.substanceofcode.utils.compatibility4.EncodingStreamReader;
import com.substanceofcode.utils.MiscUtil;

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
public class XmlParser implements SgmlParserIntr {
    
    /** Current XML element name (eg. <title> = title) */
    final protected StringBuffer m_currentElementName = new StringBuffer();
    final protected StringBuffer m_currentElementData = new StringBuffer();
    protected boolean m_currentElementContainsText = false;
	//#ifdef DTEST
    boolean m_debugTrace = false;  // True to add extra trace
	//#endif
    protected String m_fileEncoding = "ISO8859_1";  // See EncodingUtil
    protected String m_docEncoding = "";  // See EncodingUtil
    protected String m_defEncoding = "UTF-8";  // Default doc encoding
    protected EncodingUtil m_encodingUtil = null;
    protected EncodingStreamReader m_encodingStreamReader;
	protected InputStreamReader m_inputStream;
    private String [] m_namespaces = null;
    private boolean m_getPrologue = true;
	//#ifdef DLOGGING
	//#ifdef DTEST
    private boolean m_logChar    = false; // Log characters
	//#endif
    private Logger logger = Logger.getLogger("compatibility4.XmlParser");
    final private boolean fineLoggable = logger.isLoggable(Level.FINE);
    final private boolean finerLoggable = logger.isLoggable(Level.FINER);
    final private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    final private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif
    
    /** Enumerations for parse function taken from SgmlParserIntr.  */
    
    /** Creates a new instance of XmlParser */
    public XmlParser(InputStream inputStream) {
		this(new EncodingUtil(inputStream));
    }

    /** Creates a new instance of XmlParser */
    public XmlParser(EncodingUtil encodingUtil) {
		this.m_encodingUtil = encodingUtil;
		m_encodingStreamReader =
			m_encodingUtil.getEncodingStreamReader();
		m_fileEncoding = m_encodingStreamReader.getFileEncoding();
		m_inputStream = m_encodingStreamReader.getInputStream();
    }

    /** Parse next element */
    protected int parseStream(InputStreamReader is) throws IOException {
		
		boolean parsingElementName = false;
		boolean elementFound = false;
		boolean elementStart = false;
		boolean parsingElementData = false;
		boolean prologueFound = false;
				
        char c;
        int inputCharacter = is.read();
		try {
			while ((inputCharacter != -1) && !elementFound) {
                c = (char)inputCharacter;
				
				if (elementStart) {
					switch (c) {
						case '/':
							parsingElementName = false;
							break;
						// If we get ? or ! after '<' this is not an
						// element, it's a comment or prologe.
						case '?':
						case '!':
							if(m_currentElementData.charAt(m_currentElementData.length()-1)=='<') {
								parsingElementName = false;
								// If we find <? and we're looking for the prologue,
								// set flag.
								if (m_getPrologue && (c == '?')) {
									prologueFound = true;
								}
							}
							break;
						default:
							break;
					}
				}
				if(parsingElementName) {
					// Determine if we have found the end of the element
					// name and thus started element data.
					switch (c) {
						case ':':
							// For specified namespace, put it into element name
							if ((m_namespaces != null) &&
								(((m_namespaces.length >= 1) &&
								 m_namespaces[0].equals(
									m_currentElementName.toString())) ||
								((m_namespaces.length >= 2) &&
								m_namespaces[1].equals(
									m_currentElementName.toString())) ||
								((m_namespaces.length >= 3) &&
								m_namespaces[2].equals(
									m_currentElementName.toString())))) {
								m_currentElementName.append(c);
								break;
							}
							// Don't break after ':' (above) if not a part of
							// namespace as it is the end of the element
							// name.
						case ' ':
						case '/':
						case '\n':
						case '\r':
							parsingElementName = false;
							parsingElementData = true;
							break;
						// Finding '>' is the end of an element name,
						// but we process it below.
						case '>':
							break;
						default:
							m_currentElementName.append(c);
							break;
					}
				}              
				// We found the beginning of a tag, so we start an element
				// name.
				if(c=='<') {
					elementStart = true;
					parsingElementName = true;
					parsingElementData = true;
					m_currentElementName.setLength(0);
					m_currentElementData.setLength(0);
				}            
				// If parsing element data, add to it.
				if(parsingElementData) {
					m_currentElementData.append(c);
				}
				// If we find end tag '>' can also be the
				// end of the prologe so we check.
				if(c=='>') {
					if(m_currentElementName.length()>0) {
						elementFound = true;
						parsingElementName = false;
						//#ifdef DLOGGING
						if (m_logChar) {
							m_logChar = false;
							//#ifdef DLOGGING
							if (traceLoggable) {logger.trace("parseStream m_currentElementName=" + m_currentElementName);}
							//#endif
							m_encodingStreamReader.setLogChar(false);
						}
						//#endif
						// If we find XML without a prologue, need
						// to treat as default UTF-8 encoding for XML.
						if (m_getPrologue) {
							m_getPrologue = false;
							m_encodingUtil.getEncoding(m_fileEncoding,
									m_defEncoding);
							m_docEncoding = m_encodingUtil.getDocEncoding();
						}
					} else if (m_getPrologue && prologueFound &&
						// If we are looking for the prolog, now
						// we have read the end of it, so we can
						// get the encoding specified (or null which
						// defaults to UTF-8).
						// Only process actual prologes.  <?xmlstylesheet
						// is not what we want.
						m_currentElementData.toString().
							startsWith("<?xml ")) {
							m_getPrologue = false;
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("parseStream m_currentElementData.length()=" + m_currentElementData.length());}
						//#endif
						String cencoding = getAttributeValue("encoding");
						if (cencoding == null) {
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("parseStream Prologue cencoding,m_defEncoding=" + cencoding + "," + m_defEncoding);}
							//#endif
							cencoding = m_defEncoding;
						}
						m_encodingUtil.getEncoding(m_fileEncoding,
								cencoding);
						// Get doc encoding.  The encoding to translate
						// the bytes into.
						m_docEncoding = m_encodingUtil.getDocEncoding();
						return PROLOGUE;
					}
				}    

				// If we have not found an element, keep parsing.
				// Otherwise, we get out of the loop.
				if(!elementFound){
                    inputCharacter = is.read();
				}
				//#ifdef DTEST
				//#ifdef DLOGGING
				if (m_debugTrace) {
					logger.finest("parseStream c=" + c);
					logger.finest("parseStream m_currentElementName=" + m_currentElementName);
					logger.finest("parseStream m_currentElementData=" + m_currentElementData);
					logger.finest("parseStream m_currentElementContainsText=" + m_currentElementContainsText);
					logger.finest("parseStream parsingElementName=" + parsingElementName);
					logger.finest("parseStream parsingElementData=" + parsingElementData);
					logger.finest("parseStream prologueFound=" + prologueFound);
					logger.finest("parseStream parsingElementData=" + parsingElementData);
					logger.finest("parseStream parsingElementData=" + parsingElementData);
					logger.finest("parseStream elementFound=" + elementFound);
					logger.finest("parseStream elementStart=" + elementStart);
				}
				//#endif
				//#endif
			}
			
			// Determine if we actually have element data or a tag
			// that ends without data/text (e.g. <br/> has no text)
			if( m_currentElementData.charAt( m_currentElementData.length()-2 )=='/' &&
				m_currentElementData.charAt( m_currentElementData.length()-1 )=='>' ) {
				m_currentElementContainsText = false;
			} else {
				m_currentElementContainsText = true;
			}
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("parseStream m_currentElementContainsText,m_currentElementData=" + m_currentElementContainsText + "," + m_currentElementData);}
			//#endif
			
		} catch (IOException e) {
//#ifdef DLOGGING
			logger.severe("parse read error ");
//#endif
			System.out.println("parse read error " + e + " " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		if( inputCharacter == -1 ) {
			return END_DOCUMENT;
		} else {
			return ELEMENT;
		}
    }
    
    /** Parse next element */
    public int parse() throws IOException {
		if (m_encodingStreamReader.isModEncoding()) {
			return parseStream(m_encodingStreamReader);
		} else {
			return parseStream(m_inputStream);
		}
	}
		
    /** Get element name */
    public String getName() {
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("m_currentElementName=" + m_currentElementName);}
		//#endif
        return m_currentElementName.toString();
    }
    
    /** Get element text including inner xml
	  * If no text, return empty string "" */
    private String getTextStream(InputStreamReader is) throws IOException {
        
		if(!m_currentElementContainsText) {
			return "";
		}
		boolean endParsing = false;
		
		String text = "";
		try {
			StringBuffer textBuffer = new StringBuffer();
			int inputCharacter;
			char c;
			char lastChars[] = {' ', ' ', ' '};
			
			char elementNameChars[] = new char[3];
			int elen = m_currentElementName.length();
			switch (elen) {
				case 0:
					return "";
				case 1:
					elementNameChars[0] = m_currentElementName.charAt(0);
					break;
				case 2:
					elementNameChars[0] = m_currentElementName.charAt(0);
					elementNameChars[1] = m_currentElementName.charAt(1);
					break;
				default:
					// Copy the last 3 characters indexes begin at elen -3
					// to before elen to the char array.
					m_currentElementName.toString().getChars(elen - 3, elen,
							elementNameChars, 0);
					break;
			}
			final String endCurrentElement = m_currentElementName.insert(
					0, "</").toString();
			while (((inputCharacter = is.read()) != -1) &&
					!endParsing) {
				c = (char)inputCharacter;
				lastChars[0] = lastChars[1];
				lastChars[1] = lastChars[2];
				lastChars[2] = c;
				//System.out.print(c);

				textBuffer.append(c);
				if( lastChars[0] == elementNameChars[0] &&
					lastChars[1] == elementNameChars[1] &&
					lastChars[2] == elementNameChars[2] &&
					textBuffer.toString().endsWith(endCurrentElement)) {
					endParsing = true;
				}
			}

			if (m_docEncoding.length() == 0) {
				text = textBuffer.toString();
			} else {
				try {
					// We read the bytes in as ISO8859_1, so we must get them
					// out as that and then encode as they should be.
					if (m_fileEncoding.length() == 0) {
						text = new String(textBuffer.toString().getBytes(),
										  m_docEncoding);
					} else {
						text = new String(textBuffer.toString().getBytes(
									m_fileEncoding), m_docEncoding);
					}
				} catch (IOException e) {
					//#ifdef DLOGGING
					logger.severe("getTextStream Could not convert string from,to" + m_fileEncoding + "," + m_docEncoding, e);
					//#endif
					System.out.println("getTextStream Could not convert string " +
							"from,to=" + m_fileEncoding + "," + m_docEncoding +
							" " + e + " " + e.getMessage());
					e.printStackTrace();
					text = textBuffer.toString();
				}
			}
			text = StringUtil.replace(text, endCurrentElement, "");
			
			/** Handle some entities and encoded characters */
			text = StringUtil.replace(text, "<![CDATA[", "");
			text = StringUtil.replace(text, "]]>", "");
			text = m_encodingUtil.replaceAlphaEntities(text);
			// No need to convert from UTF-8 to Unicode using replace
			// umlauts now because it is done with new String...,encoding.

			// Replace numeric entities including &#8217;, &#8216;
			// &#8220;, and &#8221;
			text = EncodingUtil.replaceNumEntity(text);

			// Replace special chars like left quote, etc.
			text = m_encodingUtil.replaceSpChars(text);
			
		} catch (Throwable t) {
//#ifdef DLOGGING
			logger.severe("getTextStream Could not read a char run time.", t);
//#endif
			System.out.println("getTextStream Could not read a char run time." + t +
					           " " + t.getMessage());
			t.printStackTrace();
		}
		//#ifdef DLOGGING
		if (finerLoggable) {logger.finer("text=" + text);}
		//#endif
		return text;
    }

    /** Get element text including inner xml
	  * save some time by using the normal m_inputStream when we
	  * know that we are not reading UTF-8/16. */
    public String getText() throws IOException {
		if (m_encodingStreamReader.isModEncoding()) {
			return getTextStream(m_encodingStreamReader);
		} else {
			return getTextStream(m_inputStream);
		}
	}

    public String getText(boolean convEnts) throws IOException {
		return getText();
	}

    /** 
     * Get attribute value from current element 
     */
    public String getAttributeValue(String attributeName) {
        
		try {
			/** Check whatever the element contains given attribute */
			String ccurrentElementData = m_currentElementData.toString();
			int attributeStartIndex = ccurrentElementData.indexOf(attributeName);
			if( attributeStartIndex<0 ) {
				return null;
			}
			
			/** Calculate actual value start index */
			int valueStartIndex = attributeStartIndex + attributeName.length() + 2;
			
			/** Check the attribute value end index */
			int valueEndIndex = ccurrentElementData.indexOf('\"', valueStartIndex);
			if( valueEndIndex<0 ) {
				/** Check using windows quote account for other unexplained
				    quotes */
				if ((valueStartIndex + 1) < ccurrentElementData.length()) {
					String beginQuote = ccurrentElementData.substring(
							valueStartIndex - 1, valueStartIndex);
					valueEndIndex = ccurrentElementData.indexOf(beginQuote,
							valueStartIndex);
				}
				if( valueEndIndex<0 ) {
					return null;
				}
			}
			
			/** Parse value */
			String value = ccurrentElementData.substring(valueStartIndex, valueEndIndex);
			if (m_docEncoding.length() != 0) {
				// We read the bytes in as ISO8859_1, so we must get them
				// out as that and then encode as they should be.
				if (m_fileEncoding.length() == 0) {
					value = new String(value.getBytes(),
									  m_docEncoding);
				} else {
					value = new String(value.getBytes(
								m_fileEncoding), m_docEncoding);
				}
			}
			//#ifdef DLOGGING
			if (finerLoggable) {logger.finer("attribute value=" + value);}
			//#endif
					
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
    
    /** 
     * Get namesapces.  Return two dimension array with the first column
	 * the namespace and the second on the URL for the namespace.
     */
    public String [][] parseNamespaces() {
        
		try {
			/** Check whatever the element contains given attribute */
			String ccurrentElementData = m_currentElementData.toString();
			Vector vnamespaces = new Vector();
			Vector vnamesurls = new Vector();
			int nspos = 0;
			while ((nspos = ccurrentElementData.indexOf("xmlns:", nspos)) >= 0) {
				nspos+= 6;
				int eqpos = ccurrentElementData.indexOf('=', nspos);
				if (eqpos < 0) {
					continue;
				}
				String xmlns = ccurrentElementData.substring(nspos, eqpos);
				int qpos = ccurrentElementData.indexOf('\"', eqpos + 2);
				if (qpos < 0) {
					continue;
				}
				String url = ccurrentElementData.substring(eqpos + 2, qpos);
				//#ifdef DLOGGING
				if (finerLoggable) {logger.finer("xmlns,url=" + xmlns + "," + url);}
				//#endif
				vnamespaces.addElement(xmlns);
				vnamesurls.addElement(url);
			}
			if (vnamespaces.size() == 0) {
				return new String[0][0];
			}
			int vlen = vnamespaces.size();
			String [][] ns = new String[2][vlen];
			for (int ic = 0; ic < vlen; ic++) {
				ns[0][ic] = (String)vnamespaces.elementAt(ic);
				ns[1][ic] = (String)vnamesurls.elementAt(ic);
			}
			return ns;
		} catch (Throwable t) {
//#ifdef DLOGGING
			logger.severe("parseNamespaces error.", t);
//#endif
			System.out.println("parseNamespaces error." + t + " " +
					           t.getMessage());
			return new String[0][0];
		}
    }
    
    public void setNamespaces(String [] namespaces) {
        this.m_namespaces = namespaces;
    }

    public String [] getNamespaces() {
        return (m_namespaces);
    }

    public void setDocEncoding(String docEncoding) {
        this.m_docEncoding = docEncoding;
    }

    public String getDocEncoding() {
        return (m_docEncoding);
    }

    public boolean isWindows() {
        return (m_encodingUtil.isWindows());
    }

    public boolean isUtf() {
        return (m_encodingUtil.isUtf());
    }

	//#ifdef DTEST
    public EncodingUtilIntr getEncodingUtil()
	//#else
    public EncodingUtil getEncodingUtil()
	//#endif
	{
        return (m_encodingUtil);
    }

	//#ifdef DLOGGING
    public void setLogChar(boolean logChar) {
        this.m_logChar = logChar;
    }

    public boolean isLogChar() {
        return (m_logChar);
    }
	//#endif

}
