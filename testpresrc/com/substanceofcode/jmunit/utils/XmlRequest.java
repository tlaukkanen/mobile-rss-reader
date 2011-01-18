//--Need to modify--#preprocess
/*
 * XmlRequest.java
 *
 * Copyright (C) 2009 Irving Bunton
 * http://code.google.com/p/mobile-rss-reader/
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
 * IB 2010-03-14 1.11.5RC2 Test results from XmlParser.
 * IB 2010-05-29 1.11.5RC2 Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
*/

// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@

//#ifdef DJMTEST
//#ifdef DFULLVERS
package com.substanceofcode.jmunit.utils;

public interface XmlRequest
{
	static int GET_PARSE = 0;
	static int GET_PARSE_XML_ELEMENT = 1;
	static int GET_IS_UTF =	2;
	static int GET_IS_WIN = 3;
	static int GET_IS_DOCENCODING = 4;
	static int GET_NAMESPACES = 5;
	static int GET_ATTRIBUTE = 6;
	static int GET_TEXT = 7;
	static int GET_NAME = 8;
	static Integer IGET_PARSE = new Integer(GET_PARSE);
	static Integer IGET_PARSE_XML_ELEMENT = new Integer(GET_PARSE_XML_ELEMENT);
	static Integer IGET_IS_UTF =	new Integer(GET_IS_UTF);
	static Integer IGET_IS_WIN = new Integer(GET_IS_WIN);
	static Integer IGET_IS_DOCENCODING = new Integer(GET_IS_DOCENCODING);
	static Integer IGET_NAMESPACES = new Integer(GET_NAMESPACES);
	static Integer IGET_ATTRIBUTE = new Integer(GET_ATTRIBUTE);
	static Integer IGET_TEXT = new Integer(GET_TEXT);
	static Integer IGET_NAME = new Integer(GET_NAME);
}
//#endif
//#endif
