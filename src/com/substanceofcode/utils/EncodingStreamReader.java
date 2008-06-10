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

// Expand to define logging define
//#define DNOLOGGING
package com.substanceofcode.utils;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.substanceofcode.utils.EncodingUtil;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Simple encoding handler to allow handling utf-16 and 1252.
 *
 * @author Irving Bunton Jr
 */
public class EncodingStreamReader extends InputStreamReader {
    
    private InputStreamReader m_inputStream = null;
    private String m_fileEncoding = "ISO8859_1";  // See below
    private boolean modEncoding = true;  // First mod encoding to account for
	                                    // unexpected UTF-16.
    private boolean modUTF16 = false;  // First mod encoding to account for

    private boolean m_utf16Doc = false; // utf-16 doc

    private static boolean docBigEndian = false;  // Doc big endian
    private boolean m_getPrologue = true;
    private boolean firstChar = true;
    private boolean secondChar = false;
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("EncodingStreamReader");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
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
//@			logger.severe("init read Could not open stream with encoding " +
//@						  m_fileEncoding);
//#endif
			System.out.println("init read Could not open stream with " +
					           "encoding " + m_fileEncoding + e + " " +
					           e.getMessage());
			try {
				m_fileEncoding = "UTF-8";
				m_inputStream = new InputStreamReader(inputStream, "UTF-8");
			} catch (UnsupportedEncodingException e2) {
//#ifdef DLOGGING
//@				logger.severe("init read Could not open stream with " +
//@							  "encoding " + m_fileEncoding);
//#endif
				System.out.println("init read Could not open stream with " +
						           "encoding " + m_fileEncoding + e2 + " " +
						           e2.getMessage());
				m_inputStream = new InputStreamReader(inputStream);
				m_fileEncoding = "";
				modEncoding = false;
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
			if (modEncoding) {
				if (m_getPrologue || modUTF16) {
					// If we get 0 character during prologue, it must be
					// first or second byte of UTF-16.  Throw it out.
					// If little endian, we need to read the next character.
					if (firstChar) {
						if (inputCharacter == -1) {
							return inputCharacter;
						}
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
								m_utf16Doc = true;
							} else {
								firstChar = false;
								secondChar = true;
								if (m_utf16Doc) {
									m_utf16Doc = false;
								}
							}
						}
					} else if (secondChar) {
						// If 0 character, it is UTF-16 and little endian.
						if (inputCharacter == 0) {
							inputCharacter = m_inputStream.read();
							docBigEndian = false;
							m_utf16Doc = true;
						} else if (inputCharacter == -1) {
							return inputCharacter;
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

	public int read(char [] cbuf, int off, int len) throws IOException {
		throw new IOException("Not implemented.");
	}

    public void setModEncoding(boolean modEncoding) {
        this.modEncoding = modEncoding;
    }

    public boolean isModEncoding() {
        return (modEncoding);
    }

    public void setFileEncoding(String fileEncoding) {
        this.m_fileEncoding = fileEncoding;
    }

    public String getFileEncoding() {
        return (m_fileEncoding);
    }

    public void setGetPrologue(boolean m_getPrologue) {
        this.m_getPrologue = m_getPrologue;
    }

    public boolean isGetPrologue() {
        return (m_getPrologue);
    }

    public void setInputStream(InputStreamReader m_inputStream) {
        this.m_inputStream = m_inputStream;
    }

    public InputStreamReader getInputStream() {
        return (m_inputStream);
    }

    public void setModUTF16(boolean modUTF16) {
        this.modUTF16 = modUTF16;
    }

    public boolean isModUTF16() {
        return (modUTF16);
    }

    public void setUtf16Doc(boolean m_utf16Doc) {
        this.m_utf16Doc = m_utf16Doc;
    }

    public boolean isUtf16Doc() {
        return (m_utf16Doc);
    }

}
