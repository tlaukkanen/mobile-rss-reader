//--Need to modify--#preprocess
/*
 * EncodingStreamReader.java
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
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define testing define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.substanceofcode.utils.EncodingUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Simple encoding handler to allow handling utf-16 and 1252.
 *
 * @author Irving Bunton Jr
 */
public class EncodingStreamReader extends InputStreamReader {
    
    private InputStreamReader m_inputStream = null;
    private String m_fileEncoding = "ISO8859_1";  // See below
    private boolean m_modEncoding = true;  // First mod encoding to account for
	                                    // unexpected big5 or UTF-16.
    private boolean m_modBit16 = false;  // First mod encoding to account for
    private boolean m_bit16Doc = false; // definately big5 or utf-16 doc
    private boolean m_utfDoc = false; // definately utf-8 or utf-16 doc
    private static boolean m_docBigEndian = false;  // Doc big endian
    private boolean m_getPrologue = true;
    private boolean m_getBom = true;    // Get the Byte Order Mark
    private boolean m_firstChar = true; // 1st of a possible 2 byte character
    private boolean m_secondChar = false; // 2nd of a possible 2 byte character
	//#ifdef DLOGGING
	//#ifdef DTEST
    private boolean m_logChar    = false; // Log characters
	//#endif
    private Logger m_logger = Logger.getLogger("EncodingStreamReader");
    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
    private boolean m_traceLoggable = m_logger.isLoggable(Level.TRACE);
	//#endif
    
    /** Creates a new instance of EncodingStreamReader */
    public EncodingStreamReader(InputStream inputStream) {
		super((InputStream)new ByteArrayInputStream(" ".getBytes()));
		// Open with as this will allow us to read the bytes from any
		// encoding., then after we get the prologue, convert the strings
		// with new String(...,encoding);
		try {
			m_inputStream = new InputStreamReader(inputStream,
					EncodingUtil.getIsoEncoding());
			m_fileEncoding = EncodingUtil.getIsoEncoding();
		} catch (UnsupportedEncodingException e) {
//#ifdef DLOGGING
			m_logger.severe("init read Could not open stream with encoding " +
						  m_fileEncoding);
//#endif
			System.out.println("init read Could not open stream with " +
					           "encoding " + m_fileEncoding + e + " " +
					           e.getMessage());
			try {
				m_fileEncoding = "UTF-8";
				m_inputStream = new InputStreamReader(inputStream, "UTF-8");
			} catch (UnsupportedEncodingException e2) {
//#ifdef DLOGGING
				m_logger.severe("init read Could not open stream with " +
							  "encoding " + m_fileEncoding);
//#endif
				System.out.println("init read Could not open stream with " +
						           "encoding " + m_fileEncoding + e2 + " " +
						           e2.getMessage());
				m_inputStream = new InputStreamReader(inputStream);
				m_fileEncoding = "";
				m_modEncoding = false;
			}
		}
    }

	/**
	  Read the next character.  For UTF-16, read two bytes when
	  we discover this.
	  */
	public int read() throws IOException {
    
		try {
			int inputCharacter = m_inputStream.read();
			//#ifdef DLOGGING
			//#ifdef DTEST
			if (m_logChar && m_traceLoggable) {m_logger.trace("read inputCharacter,m_modEncoding,m_getPrologue,m_modBit16,m_firstChar,m_secondChar=" + inputCharacter  + "," + ((inputCharacter == -1) ? "-1" : new Character((char)inputCharacter).toString()) + "," + Integer.toString(inputCharacter, 16) + "," + m_modEncoding + "," + m_getPrologue + "," + m_modBit16 + "," + m_firstChar + "," + m_secondChar);}
			//#endif
			//#endif
			if (m_modEncoding && (m_getPrologue || m_modBit16)) {
				// If we get 0 character during prologue, it must be
				// first or second byte of UTF-16.  Throw it out.
				// If little endian, we need to read the next character.
				if (m_firstChar) {
					if (m_getBom) {
						//#ifdef DLOGGING
						if (m_finestLoggable) {m_logger.finest("read m_getBom,inputCharacter=" + "," + m_getBom + "," + inputCharacter + "," + Integer.toString(inputCharacter, 16));}
						//#endif
						m_getBom = false;
						if (inputCharacter == 0x0ef) {
							//#ifdef DLOGGING
							int prevInputChar = inputCharacter;
							//#endif
							inputCharacter = m_inputStream.read();
							//#ifdef DLOGGING
							if (m_finestLoggable) {m_logger.finest("read next byte of bom inputCharacter=" + "," + inputCharacter + "," + Integer.toString(inputCharacter, 16));}
							//#endif
							switch (inputCharacter) {
								case -1:
									return inputCharacter;
								case 0xff:
									m_modBit16 = true;
									m_bit16Doc = true;
									m_utfDoc = true;
									m_docBigEndian = true;
									break;
								case 0xfe:
									m_modBit16 = true;
									m_bit16Doc = true;
									m_utfDoc = true;
									m_docBigEndian = false;
									break;
								case 0xbb:
									if ((inputCharacter = m_inputStream.read())
											== -1) {
										return inputCharacter;
									}
									if (inputCharacter != 0xbf) {
										//#ifdef DLOGGING
										m_logger.severe("Invalid BOM for UTf-8 " +
											"has last character " +
											inputCharacter,
											new Exception());
										//#endif
										return inputCharacter;
									}
									m_modBit16 = false;
									m_bit16Doc = false;
									m_utfDoc = true;
									m_modEncoding = false;
									break;
								default:
									return inputCharacter;
							}
							//#ifdef DLOGGING
							if (m_fineLoggable) {m_logger.fine("read() read bom prevInputChar,inputCharacter,m_modBit16,m_bit16Doc,m_utfDoc,m_modEncoding=" + prevInputChar + "," + inputCharacter + "," + m_modBit16 + "," + m_bit16Doc + "," + m_utfDoc + "," + m_modEncoding);}
							//#endif
							/* Read 1st char after BOM. */
							inputCharacter = m_inputStream.read();
							if (m_modEncoding) {
								return inputCharacter;
							}
						}
					}
					if (inputCharacter == -1) {
						return inputCharacter;
					}
					if (m_modBit16) {
						// If we know that this is UTF-16 from
						// encoding, put the bytes together correctly.
						// In this case, we are not in prolog, so
						// both bytes may be meaningful.
						int secondCharacter = m_inputStream.read();
						if (m_docBigEndian) {
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
							m_docBigEndian = true;
							m_bit16Doc = true;
						} else {
							m_firstChar = false;
							m_secondChar = true;
							if (m_bit16Doc) {
								m_bit16Doc = false;
							}
						}
					}
				} else if (m_secondChar) {
					// If 0 character, it is UTF-16 and little endian.
					if (inputCharacter == 0) {
						inputCharacter = m_inputStream.read();
						m_docBigEndian = false;
						m_bit16Doc = true;
					} else if (inputCharacter == -1) {
						return inputCharacter;
					}
					m_firstChar = true;
					m_secondChar = false;
				}
			}
			return inputCharacter;
		} catch (IOException e) {
//#ifdef DLOGGING
			m_logger.severe("read Could not read a char io error." + e + " " +
					           e.getMessage());
//#endif
			System.out.println("read Could not read a char io error." + e + " " +
					           e.getMessage());
			throw e;
		} catch (Throwable t) {
//#ifdef DLOGGING
			m_logger.severe("read Could not read a char run time." + t + " " +
					           t.getMessage());
//#endif
			System.out.println("read Could not read a char run time." + t + " " +
					           t.getMessage());
			return -1;
		}
	}

	public int read(char [] cbuf, int off, int len) throws IOException {
		throw new IOException("Not implemented.");
	}

    public void setModEncoding(boolean modEncoding) {
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("setModEncoding modEncoding=" + modEncoding);}
		//#endif
        this.m_modEncoding = modEncoding;
    }

    public boolean isModEncoding() {
        return (m_modEncoding);
    }

    public void setFileEncoding(String fileEncoding) {
        this.m_fileEncoding = fileEncoding;
    }

    public String getFileEncoding() {
        return (m_fileEncoding);
    }

    public void setGetPrologue(boolean getPrologue) {
        this.m_getPrologue = getPrologue;
    }

    public boolean isGetPrologue() {
        return (m_getPrologue);
    }

    public void setInputStream(InputStreamReader inputStream) {
        this.m_inputStream = inputStream;
    }

    public InputStreamReader getInputStream() {
        return (m_inputStream);
    }

    public void setModBit16(boolean modBit16) {
		//#ifdef DLOGGING
		if (m_finestLoggable) {m_logger.finest("setModBit16 modBit16=" + modBit16);}
		//#endif
        this.m_modBit16 = modBit16;
    }

    public boolean isModBit16() {
        return (m_modBit16);
    }

    public void setBit16Doc(boolean bit16Doc) {
        this.m_bit16Doc = bit16Doc;
    }

    public boolean isBit16Doc() {
        return (m_bit16Doc);
    }

    public void setUtfDoc(boolean utfDoc) {
        this.m_utfDoc = utfDoc;
    }

    public boolean isUtfDoc() {
        return (m_utfDoc);
    }

	//#ifdef DLOGGING
	//#ifdef DTEST
    public void setLogChar(boolean logChar) {
        this.m_logChar = logChar;
    }

    public boolean isLogChar() {
        return (m_logChar);
    }
	//#endif
	//#endif

}
