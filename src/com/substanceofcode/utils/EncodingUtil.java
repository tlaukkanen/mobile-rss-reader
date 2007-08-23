 /*
 * EncodingUtil.java
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
//#define DNOLOGGING
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Simple encoding handler to allow handling utf-16 and 1252.
 *
 * @author Irving Bunton Jr
 */
public class EncodingUtil {
    
	// Left single quote in cp-1252 (Windows) encoding.
    private static final byte [] WLEFT_SGL_QUOTE = {(byte)145};
	// Left single quote in Unicode (utf-16) encoding.
    private static final char ULEFT_SGL_QUOTE = (char)8216;
    private static final char [] CLEFT_DBL_QUOTE = {(char)8220};
    public static String LEFT_DBL_QUOTE = new String(CLEFT_DBL_QUOTE);
    private static final char [] CRIGHT_DBL_QUOTE = {(char)8221};
    public static String RIGHT_DBL_QUOTE = new String(CRIGHT_DBL_QUOTE);
    private static final char [] CLEFT_SGL_QUOTE = {(char)8217};
    public static String LEFT_SGL_QUOTE = new String(CLEFT_SGL_QUOTE);
    private static final char [] CRIGHT_SGL_QUOTE = {(char)8217};
    public static String RIGHT_SGL_QUOTE = new String(CRIGHT_SGL_QUOTE);
    private static final char [] CNON_BREAKING_SP = {(char)194};
    public static String NON_BREAKING_SP = new String(CNON_BREAKING_SP);
    private static final char [] CLONG_DASH = {(char)8211};
    public static String LONG_DASH = new String(CLONG_DASH);
    private static final char [] CA_UMLAUTE = {(char)228};
    public static String A_UMLAUTE = new String(CA_UMLAUTE);
    private static final char [] CO_UMLAUTE = {(char)246};
    public static String O_UMLAUTE = new String(CO_UMLAUTE);
    
    private InputStreamReader m_inputStream = null;
    private String fileEncoding = "ISO8859_1";  // See below
    private String docEncoding = "";  // Default for XML is UTF-8.
    private boolean modEncoding = true;  // First mod encoding to account for
	                                    // unexpected UTF-16.
    private boolean modUTF16 = false;  // First mod encoding to account for
    private static boolean vmBigEndian = false;  // J2ME VM big endian
    private static boolean docBigEndian = false;  // Doc big endian
    private boolean getPrologue = true;
    private boolean firstChar = true;
    private boolean secondChar = false;
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("EncodingUtil");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of EncodingUtil */
    public EncodingUtil(InputStream inputStream) {
		// Open with as this will allow us to read the bytes from any
		// encoding., then after we get the prologue, convert the strings
		// with new String(...,encoding);
		try {
			m_inputStream = new InputStreamReader(inputStream, "ISO8859_1");
		} catch (UnsupportedEncodingException e) {
//#ifdef DLOGGING
//@			logger.severe("init read Could not open stream with encoding " +
//@						  fileEncoding);
//#endif
			System.out.println("init read Could not open stream with " +
					           "encoding " + fileEncoding + e + " " +
					           e.getMessage());
			try {
				fileEncoding = "UTF-8";
				m_inputStream = new InputStreamReader(inputStream, "UTF-8");
			} catch (UnsupportedEncodingException e2) {
//#ifdef DLOGGING
//@				logger.severe("init read Could not open stream with " +
//@							  "encoding " + fileEncoding);
//#endif
				System.out.println("init read Could not open stream with " +
						           "encoding " + fileEncoding + e2 + " " +
						           e2.getMessage());
				m_inputStream = new InputStreamReader(inputStream);
				fileEncoding = "";
				modEncoding = false;
			}
		}
    }

    /** Determine big/little endian for character constants.  */
    public static void init() {
		vmBigEndian = ((ULEFT_SGL_QUOTE & 255) == 24);
		//#ifdef DLOGGING
//@		Logger log = Logger.getLogger("EncodingUtil");
//@		log.fine("vmBigEndian=" + vmBigEndian);
//@		log.fine("whole,halves=" + (int)ULEFT_SGL_QUOTE + "," +
//@				 (((int)ULEFT_SGL_QUOTE >> 8) & 255) + "," +
//@				  ((int)ULEFT_SGL_QUOTE & 255));
		//#endif
		if (!vmBigEndian) {
			//#ifdef DLOGGING
//@			log.fine("left double quote whole,halves=" +
//@					 (int)CLEFT_DBL_QUOTE[0] +
//@					 "," + (((int)CLEFT_DBL_QUOTE[0] >> 8) & 255) + "," +
//@					   ((int)CLEFT_DBL_QUOTE[0] & 255));
			//#endif
			LEFT_DBL_QUOTE = switchBytes(LEFT_DBL_QUOTE);
			//#ifdef DLOGGING
//@			log.fine("left double quote whole,halves=" +
//@					 (int)LEFT_DBL_QUOTE.charAt(0) +
//@					  "," + (((int)LEFT_DBL_QUOTE.charAt(0) >> 8) & 255) +
//@					  "," + ((int)LEFT_DBL_QUOTE.charAt(0) & 255));
			//#endif
			RIGHT_DBL_QUOTE = switchBytes(RIGHT_DBL_QUOTE);
			LEFT_SGL_QUOTE = switchBytes(LEFT_SGL_QUOTE);
			RIGHT_SGL_QUOTE = switchBytes(RIGHT_SGL_QUOTE);
			NON_BREAKING_SP = switchBytes(NON_BREAKING_SP);
			LONG_DASH = switchBytes(LONG_DASH);
		}
	}

    /** If little endian, need to switch bytes.  */
    private static String switchBytes(String str) {
		char [] nchars = new char[1];
		nchars[0] = (char)((((int)str.charAt(0) & 255) << 8) |
		            (((int)(str.charAt(0)) >> 8) & 255));
		//str.charAt(0) = (char)0;//undo
		return new String(nchars);
	}

	/**
	  Read the next character.  For UTF-16, read two bytes when
	  we discover this.
	  */
	public int read() throws IOException {
    
		try {
			int inputCharacter = m_inputStream.read();
			if (modEncoding) {
				if (getPrologue || modUTF16) {
					// If we get 0 character during prologue, it must be
					// first or second byte of UTF-16.  Throw it out.
					// If little endian, we need to read the next character.
					if (firstChar) {
						if (modUTF16) {
							// If we know that this is UTF-16 from
							// encoding, put the bytes together correctly.
							// In this case, we are not in prolog, so
							// both bytes may be meaningful.
							int secondCharacter = m_inputStream.read();
							if (docBigEndian) {
								inputCharacter <<= 8;
								inputCharacter |= secondCharacter;
							} else {
								secondCharacter <<= 8;
								secondCharacter |= inputCharacter;
							}
						} else {
							if (inputCharacter == 0) {
								// This is UTF-16, read second character
								inputCharacter = m_inputStream.read();
								docBigEndian = true;
							} else {
								firstChar = false;
								secondChar = true;
							}
						}
					} else if (secondChar) {
						// If 0 character, it is UTF-16 and little endian.
						if (inputCharacter == 0) {
							inputCharacter = m_inputStream.read();
							docBigEndian = false;
						}
						firstChar = true;
						secondChar = false;
					}
				}
			}
			return inputCharacter;
		} catch (IOException e) {
//#ifdef DLOGGING
//@			logger.severe("read Could not read a char io error." + e + " " +
//@					           e.getMessage());
//#endif
			System.out.println("read Could not read a char io error." + e + " " +
					           e.getMessage());
			throw e;
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("read Could not read a char run time." + t + " " +
//@					           t.getMessage());
//#endif
			System.out.println("read Could not read a char run time." + t + " " +
					           t.getMessage());
			return -1;
		}
	}

	/**  Determine the encoding based on what is passed in as well
	  as if/when strings are to be further encoded.  Also decide to
	  modify bytes read.  
	 **/

    public void getEncoding(String cencoding) {
        // If there is a second char, don't stop splitting until we
        // return that char as input.
        if (cencoding == null) {
           cencoding = "UTF-8";
        }
        cencoding = cencoding.toUpperCase();
        if ((cencoding.equals("UTF-8") || cencoding.equals("UTF8"))) {
           docEncoding = "UTF-8";
           modEncoding = false;
        } else if ((cencoding.equals("UTF-16") || cencoding.equals("UTF16"))) {
           modUTF16 = true;
        } else if (cencoding.indexOf("ISO-8859") == 0) {
           docEncoding = StringUtil.replace(cencoding, "ISO-",
                                          "ISO");
            
           docEncoding = StringUtil.replace(docEncoding, "-", "_");
           if (docEncoding.equals("ISO8859_1")) {
               docEncoding = "";
           }
           modEncoding = false;
        } else if (cencoding.indexOf("WINDOWS-12") == 0) {
            docEncoding = StringUtil.replace(cencoding, "WINDOW-",
                                             "Cp");
           modEncoding = false;
        }
		if (!docEncoding.equals("")) {
			try {
				String a = new String("a".getBytes(), docEncoding);
			} catch (UnsupportedEncodingException e) {
				//#ifdef DLOGGING
//@				logger.severe("UnsupportedEncodingException error for " +
//@							   "encoding: " + docEncoding);
				//#endif
				System.out.println("UnsupportedEncodingException error for " +
							   "encoding: " + docEncoding + " " + e + " " +
							   e.getMessage());
				// If encoding problem, use the main encoding as it is
				// close enough.
				if (docEncoding.indexOf("ISO8859") >= 0) {
					docEncoding = "ISO8859_1";
				} else if (docEncoding.indexOf("Cp12") >= 0) {
					docEncoding = "Cp1252";
				}
				try {
					String a = new String("a".getBytes(), docEncoding);
				} catch (UnsupportedEncodingException e2) {
					//#ifdef DLOGGING
//@					logger.severe("UnsupportedEncodingException error for " +
//@								   "encoding: " + docEncoding);
					//#endif
					System.out.println("UnsupportedEncodingException error " +
							           "for encoding: " + docEncoding + " "
									   + e + " " + e.getMessage());
					docEncoding = "";
				}
			}
		}

		//#ifdef DLOGGING
//@        logger.fine("docEncoding=" + docEncoding);
		//#endif
    }

	public static String replaceSpChars(String text) {
		try {
			// No need to convert i diaeresis anymore as we do encoding
			// change.
			text = StringUtil.replace(text, LEFT_DBL_QUOTE, "\"");
			text = StringUtil.replace(text, RIGHT_DBL_QUOTE, "\"");
			text = StringUtil.replace(text, LEFT_SGL_QUOTE, "'");
			text = StringUtil.replace(text, RIGHT_SGL_QUOTE, "'");
			text = StringUtil.replace(text, NON_BREAKING_SP, " ");
			text = StringUtil.replace(text, LONG_DASH, "-");
		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("EncodingUtil");
//@            logger.severe("replaceNumEntity error ", t);
			//#endif
            System.out.println("replaceNumEntity error " + t + "," +
					           t.getMessage());
		}
		return text;
	}

    /* Replace all numeric entites e.g. &#228;
     *   @param  s  String to alter.
     */
    public static String replaceNumEntity( String s) {
        if (s == null)  return s;
		try {
			
			int index01 = s.indexOf( "&#" );
			char [] achar = new char[1];
			while (index01 != -1) {
				int index02 = s.indexOf( ";" , index01 );
				if (index02 == -1) {
					return s;
				}
				try {
					String snum = s.substring(index01 + 2, index02);
					achar[0] = (char)Integer.parseInt(snum);
					s = s.substring(0, index01) + new String(achar) +
							  s.substring(index02 + 1);
				} catch (NumberFormatException e) {
					return s;
				}
				index01 = s.indexOf( "&#" );
			}
		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("EncodingUtil");
//@            logger.severe("replaceNumEntity error ", t);
			//#endif
            System.out.println("replaceNumEntity error " + t + "," +
					           t.getMessage());
		}
        return s;
    }
    
    public void setDocEncoding(String docEncoding) {
        this.docEncoding = docEncoding;
    }

    public String getDocEncoding() {
        return (docEncoding);
    }

    public void setModEncoding(boolean modEncoding) {
        this.modEncoding = modEncoding;
    }

    public boolean isModEncoding() {
        return (modEncoding);
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public String getFileEncoding() {
        return (fileEncoding);
    }

}
