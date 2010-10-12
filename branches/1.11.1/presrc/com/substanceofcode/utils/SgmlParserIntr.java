//--Need to modify--#preprocess
/*
 * SgmlParserIntr.java
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
 * IB 2010-03-14 1.11.5RC2 Use interface to make compatibility testing easier.
 * IB 2010-07-28 1.11.5Dev8 Convert entities if CDATA used.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
*/

// Expand to define testing define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DTEST
package com.substanceofcode.utils;

import java.io.IOException;

/**
 * Simple and lightweight XML parser without complete error handling.
 *
 * @author Tommi Laukkanen
 * @author Irving Bunton, Jr
 */
public interface SgmlParserIntr {
    
    /** Enumerations for parse function */
    
    public static final int PARTIAL_TEXT = 0;
    public static final int UNKNOWN_ELEMENT = 1;
    public static final int END_DOCUMENT = 2;
    public static final int ELEMENT = 3;
    public static final int PROLOGUE = 4;
    public static final int STYLESHEET = 5;
    public static final int DOCTYPE = 6;
    public static final int CDATA = 7;
    public static final int COMMENT = 8;
    public static final int CLOSE_TAG = 9;
    public static final int LAST_TOKEN = CLOSE_TAG;
    public static final int REDIRECT_URL = LAST_TOKEN + 1;

    public static final String[] PARSING_RESULTS = {"PARTIAL_TEXT",
    "UNKNOWN_ELEMENT",
    "END_DOCUMENT",
    "ELEMENT",
    "PROLOGUE",
    "STYLESHEET",
    "DOCTYPE",
    "CDATA",
    "COMMENT",
    "CLOSE_TAG",
	"REDIRECT_URL"};
    
    /** Parse next element */
    int parse() throws IOException;

    /** Get element name */
    String getName();
	
    String getText() throws IOException;

    String getText(boolean convEnts) throws IOException;

    /** 
     * Get attribute value from current element 
     */
    String getAttributeValue(String attributeName);

    /** 
     * Get namesapces.  Return two dimension array with the first column
	 * the namespace and the second on the URL for the namespace.
     */
    String [][] parseNamespaces();
    
    void setNamespaces(String [] namespaces);

    String [] getNamespaces();

    void setDocEncoding(String docEncoding);

    String getDocEncoding();

    boolean isWindows();
    boolean isUtf();

    EncodingUtil getEncodingUtil();

	//#ifdef DTEST
	//#ifdef DLOGGING
	void setLogChar(boolean logChar);
	//#endif
	//#endif
}
//#endif
