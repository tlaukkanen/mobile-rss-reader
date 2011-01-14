//--Need to modify--#preprocess
/*
 * XmlParserTest.java
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
 * IB 2010-04-05 1.11.4RC1 Allow logging of characters for different expected tokens.
 * IB 2010-04-30 1.11.5RC2 Test line feeds in code.
 * IB 2010-05-29 1.11.5RC2 Return first non PROLOGUE, DOCTYPE, STYLESHEET, or ELEMENT which is not link followed by meta.
 * IB 2010-07-19 1.11.5Dev8 Convert entities for text if CDATA used.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
*/

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.utils;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Vector;

import com.substanceofcode.utils.XmlParser;
import com.substanceofcode.utils.SgmlParserIntr;
import com.substanceofcode.rssreader.businesslogic.ExtParser;

import com.substanceofcode.jmunit.utils.XmlRequest;
import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.jmunit.utilities.BaseTestCase;

final public class XmlParserTest extends BaseTestCase
{

	//#ifdef DTEST
	//#ifdef DLOGGING
	private boolean logParseChar = traceLoggable; // or traceLoggable
	private boolean logParseXmlElemChar = traceLoggable; // or traceLoggable
	private boolean logNameChar = false;
	private boolean logTextChar = false;
	private boolean logAttrChar = false;
	//#endif
	//#endif

	public XmlParserTest() {
		super(3, "XmlParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testXmlParse1();
				break;
			case 1:
				testXmlParse2();
				break;
			case 2:
				testXmlParse3();
				break;
			default:
				Exception e = new Exception(
						"No such test testNumber=" + testNumber);
				//#ifdef DLOGGING
				logger.severe("test no switch case XmlRequest.failure #" +
						testNumber, e);
				//#endif
				throw e;
		}
	}

	public void testXmlParse1() throws Throwable {
		String mname = "testXmlParse1";
	String[] xmlStrings = {"<!DOCTYPE chapter PUBLIC \"-//OASIS//DTD DocBook XML//EN\"",
          "\"../dtds/chapter.dtd\">",
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>",
			"<?xml-stylesheet href=\"http://feeds.feedburner.com/~d/styles/rss2full.xsl\" type=\"text/xsl\" media=\"screen\"?>",
			"<!-- comment1 -->",
			"<rss version=\"0.91\">",
			"<!-- comment2 -->",
			"<channel>",
			"<!-- comment3 -->",
			"<title>Andere | Lander - Anderes Deutsch</title>",
			"<!-- comment4 -->",
			"<link>http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/</link>",
			"<image></image>",
			"<!-- comment5 -->",
			"<item>",
			"<!-- comment6 -->",
			"<title>Episode| 1</title>",
			"<!-- comment7 -->",
			"<enclosure url=\"http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/episodes/Episode1.mp3\" length=\"9681361\" type=\"audio/mpeg\" />",
			"<!-- comment8 -->",
			"</item>",
			"<item>",
			"<!-- comment9 -->",
			"<title>Episode| 2</title>",
			"<! comment10 >",
			"<description>desc2 &mdash;",
			"</description>",
			"<!-- comment11 -->",
			"</item>",
			"</channel>",
			"</rss>"};

		Object [] xmlRequests = new Object[] {XmlRequest.IGET_PARSE, // doctype
		XmlRequest.IGET_PARSE, // prologue
		XmlRequest.IGET_PARSE, // stylesheet
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // rss
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // channel
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // link
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // image
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // item
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_ATTRIBUTE, "url", XmlRequest.IGET_ATTRIBUTE, "length",
		   XmlRequest.IGET_ATTRIBUTE, "type", // feed enclosure
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // item
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed description
		XmlRequest.IGET_PARSE // end document
		};
		Vector expXmlResults = new Vector();
		expXmlResults.addElement(new Integer(SgmlParserIntr.DOCTYPE));
		expXmlResults.addElement(new Integer(SgmlParserIntr.PROLOGUE));
		expXmlResults.addElement(new Integer(SgmlParserIntr.STYLESHEET));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("rss"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("channel"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Andere | Lander - Anderes Deutsch"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("link"));
		expXmlResults.addElement(new String("http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("image"));
		expXmlResults.addElement(new String(""));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("item"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Episode| 1"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("enclosure"));
		expXmlResults.addElement(new String("http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/episodes/Episode1.mp3"));
		expXmlResults.addElement(new String("9681361"));
		expXmlResults.addElement(new String("audio/mpeg"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("item"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Episode| 2"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("description"));
		expXmlResults.addElement(new String("desc2 -"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.END_DOCUMENT));
		Vector xmlResults = new Vector();
		xmlParserTestSub(mname, xmlStrings, "utf-8", xmlRequests, expXmlResults,
				xmlResults);
	}

	public void testXmlParse2() throws Throwable {
		String mname = "testXmlParse2";
	String[] xmlStrings = {"<!DOCTYPE\nchapter PUBLIC \"-//OASIS//DTD DocBook XML//EN\"",
          "\"../dtds/chapter.dtd\">",
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>",
			"<?xml-stylesheet\nhref=\"http://feeds.feedburner.com/~d/styles/rss2full.xsl\" type=\"text/xsl\" media=\"screen\"?>",
			"<!-- comment1 -->",
			"<rss\nversion=\"0.91\">",
			"<!-- comment2 -->",
			"<channel>",
			"<!-- comment3 -->",
			"<title>Andere | Lander - Anderes Deutsch</title>",
			"<!-- comment4 -->",
			"<link>http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/</link>",
			"<image></image>",
			"<!-- comment5 -->",
			"<item>",
			"<!-- comment6 -->",
			"<title>Episode| 1</title>",
			"<!-- comment7 -->",
			"<enclosure\nurl=\"http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/episodes/Episode1.mp3\" length=\"9681361\" type=\"audio/mpeg\" />",
			"<!-- comment8 -->",
			"</item>",
			"<item>",
			"<!-- comment9 -->",
			"<title>Episode| 2</title>",
			"<! comment10 >",
			"<description>desc2",
			"</description>",
			"<!-- comment11 -->",
			"</item>",
			"</channel>",
			"</rss>"};

		Object [] xmlRequests = new Object[] {XmlRequest.IGET_PARSE, // doctype
		XmlRequest.IGET_PARSE, // prologue
		XmlRequest.IGET_PARSE, // stylesheet
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // rss
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // channel
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // link
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // image
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // item
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_ATTRIBUTE, "url", XmlRequest.IGET_ATTRIBUTE, "length",
		   XmlRequest.IGET_ATTRIBUTE, "type", // feed enclosure
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // item
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed description
		XmlRequest.IGET_PARSE // end document
		};
		Vector expXmlResults = new Vector();
		expXmlResults.addElement(new Integer(SgmlParserIntr.DOCTYPE));
		expXmlResults.addElement(new Integer(SgmlParserIntr.PROLOGUE));
		expXmlResults.addElement(new Integer(SgmlParserIntr.STYLESHEET));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("rss"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("channel"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Andere | Lander - Anderes Deutsch"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("link"));
		expXmlResults.addElement(new String("http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("image"));
		expXmlResults.addElement(new String(""));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("item"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Episode| 1"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("enclosure"));
		expXmlResults.addElement(new String("http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/episodes/Episode1.mp3"));
		expXmlResults.addElement(new String("9681361"));
		expXmlResults.addElement(new String("audio/mpeg"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("item"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Episode| 2"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("description"));
		expXmlResults.addElement(new String("desc2"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.END_DOCUMENT));
		Vector xmlResults = new Vector();
		xmlParserTestSub(mname, xmlStrings, "utf-8", xmlRequests, expXmlResults,
				xmlResults);
	}

	public void testXmlParse3() throws Throwable {
		String mname = "testXmlParse3";
	String[] xmlStrings = {"<link rel=\"canonical\" href=\"http://internet-nexus.blogspot.com/\" />",
		"<meta http-equiv=\"refresh\" content=\"30;url=http://internet-nexus.blogspot.com/\" />",
		"<!DOCTYPE\nchapter PUBLIC \"-//OASIS//DTD DocBook XML//EN\"",
          "\"../dtds/chapter.dtd\">",
		"<?xml version=\"1.0\" encoding=\"utf-8\"?>",
			"<?xml-stylesheet\nhref=\"http://feeds.feedburner.com/~d/styles/rss2full.xsl\" type=\"text/xsl\" media=\"screen\"?>",
			"<!-- comment1 -->",
			"<rss\nversion=\"0.91\">",
			"<!-- comment2 -->",
			"<channel>",
			"<!-- comment3 -->",
			"<title>Andere | Lander - Anderes Deutsch</title>",
			"<!-- comment4 -->",
			"<link>http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/</link>",
			"<image></image>",
			"<!-- comment5 -->",
			"<item>",
			"<!-- comment6 -->",
			"<title>Episode| 1</title>",
			"<!-- comment7 -->",
			"<enclosure\nurl=\"http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/episodes/Episode1.mp3\" length=\"9681361\" type=\"audio/mpeg\" />",
			"<!-- comment8 -->",
			"</item>",
			"<item>",
			"<!-- comment9 -->",
			"<title>Episode| 2</title>",
			"<! comment10 >",
			"<description>desc2",
			"</description>",
			"<!-- comment11 -->",
			"</item>",
			"</channel>",
			"</rss>"};

		Object [] xmlRequests = new Object[] {
		XmlRequest.IGET_PARSE_XML_ELEMENT, XmlRequest.IGET_NAME, // rss
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // channel
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // link
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // image
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // item
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_ATTRIBUTE, "url", XmlRequest.IGET_ATTRIBUTE, "length",
		   XmlRequest.IGET_ATTRIBUTE, "type", // feed enclosure
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // item
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // feed description
		XmlRequest.IGET_PARSE // end document
		};

		Vector expXmlResults = new Vector();
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("rss"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("channel"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Andere | Lander - Anderes Deutsch"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("link"));
		expXmlResults.addElement(new String("http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("image"));
		expXmlResults.addElement(new String(""));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("item"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Episode| 1"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("enclosure"));
		expXmlResults.addElement(new String("http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/episodes/Episode1.mp3"));
		expXmlResults.addElement(new String("9681361"));
		expXmlResults.addElement(new String("audio/mpeg"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("item"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("title"));
		expXmlResults.addElement(new String("Episode| 2"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expXmlResults.addElement(new String("description"));
		expXmlResults.addElement(new String("desc2"));
		expXmlResults.addElement(new Integer(SgmlParserIntr.END_DOCUMENT));
		Vector xmlResults = new Vector();
		xmlParserTestSub(mname, xmlStrings, "utf-8", xmlRequests, expXmlResults,
				xmlResults);
	}

    public void xmlParserTestSub(final String mname, String[] xmlStrings,
			String enc,
			Object [] xmlRequests, Vector expXmlResults, Vector xmlResults )
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			//#endif
			StringBuffer xmlsb = new StringBuffer();
			for (int i = 0; i < xmlStrings.length; i++) {
				xmlsb.append(xmlStrings[i]);
			}
			XmlParser parser = new XmlParser(
						new ByteArrayInputStream(
							xmlsb.toString().getBytes(enc)));
			int i = 0;
			int parseReq = ((Integer)xmlRequests[i]).intValue();
			//#ifdef DTEST
			//#ifdef DLOGGING
			if (parseReq == XmlRequest.GET_PARSE) {
				if (logParseChar) {
					parser.setLogChar(logParseChar);
				}
			} else {
				if (logParseXmlElemChar) {
					parser.setLogChar(logParseXmlElemChar);
				}
			}
			//#endif
			//#endif
			int parsingResult = (parseReq == XmlRequest.GET_PARSE) ? parser.parse() : parser.parseXmlElement();
			//#ifdef DTEST
			//#ifdef DLOGGING
			if (parseReq == XmlRequest.GET_PARSE) {
				if (logParseChar) {
					parser.setLogChar(false);
				}
			} else {
				if (logParseXmlElemChar) {
					parser.setLogChar(false);
				}
			}
			//#endif
			//#endif
			i++;
			int j = 0;
			int pexpValue = 
					((Integer)expXmlResults.elementAt(j)).intValue();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " parse j,pexpValue=" + j + "," + pexpValue + "," + SgmlParserIntr.PARSING_RESULTS[pexpValue] + "," + parsingResult + "," + SgmlParserIntr.PARSING_RESULTS[parsingResult]);}
			//#endif
			assertEquals(mname + " 0 parser result not expected.", pexpValue,
					parsingResult);
			j++;
			for (; i < xmlRequests.length; i++) {
				Object expObj = expXmlResults.elementAt(j++);
				//#ifdef DLOGGING
				if (traceLoggable) {logger.trace(mname + " xmlParserTestSub i,j,expObj.getClass().getName(),expObj.hashCode(),expObj.toString()=" + i + "," + (j - 1) + "," + ((expObj == null) ? "null" : expObj.getClass().getName()) + "," + ((expObj == null) ? "null" : Integer.toString(expObj.hashCode())) + "," + ((expObj == null) ? "null" : expObj.toString()) );}
				//#endif
				switch (((Integer)xmlRequests[i]).intValue()) {
					case XmlRequest.GET_PARSE:
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logParseChar) {
							parser.setLogChar(logParseChar);
						}
						//#endif
						//#endif
						parsingResult = parser.parse();
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logParseChar) {
							parser.setLogChar(false);
						}
						//#endif
						//#endif
						int expValue = 
								((Integer)expObj).intValue();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " parse i,j,expValue,parsingResult=" + i + "," + (j - 1) + "," + expValue + "," + SgmlParserIntr.PARSING_RESULTS[expValue] + "," + parsingResult + "," + SgmlParserIntr.PARSING_RESULTS[parsingResult]);}
						//#endif
						assertEquals(mname + "parser result " + parsingResult + " not expected " + expValue,
								expValue, parsingResult);
						break;
					case XmlRequest.GET_PARSE_XML_ELEMENT:
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logParseChar) {
							parser.setLogChar(logParseChar);
						}
						//#endif
						//#endif
						parsingResult = parser.parse();
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logParseChar) {
							parser.setLogChar(false);
						}
						//#endif
						//#endif
						int expElemValue = 
								((Integer)expObj).intValue();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " parse i,j,expElemValue,parsingResult=" + i + "," + (j - 1) + "," + expElemValue + "," + SgmlParserIntr.PARSING_RESULTS[expElemValue] + "," + parsingResult + "," + SgmlParserIntr.PARSING_RESULTS[parsingResult]);}
						//#endif
						assertEquals(mname + "parser result " + parsingResult + " not expected " + expElemValue,
								expElemValue, parsingResult);
						break;
					case XmlRequest.GET_IS_UTF:
						boolean bexpValue = 
								((Boolean)expObj).booleanValue();
						boolean bactValue = parser.isUtf();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " isUtf i,j,bexpValue,bactValue=" + i + "," + (j - 1) + "," + bexpValue + "," + bactValue);}
						//#endif
						assertEquals(mname + " isUtf result not expected.", bexpValue,
								bactValue);
						break;
					case XmlRequest.GET_IS_WIN:
						boolean bexpValue2 = 
								((Boolean)expObj).booleanValue();
						boolean bactValue2 = parser.isWindows();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " isWindows i,j,bexpValue2,bactValue2=" + i + "," + (j - 1) + "," + bexpValue2 + "," + bactValue2);}
						//#endif
						assertEquals(mname + " isWindows result not expected.", bexpValue2,
								bactValue2);
						break;
					case XmlRequest.GET_IS_DOCENCODING:
						String sexpValue = ((String)expObj).toString();
						String sactValue = parser.getDocEncoding();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " getDocEncoding i,j,sexpValue,sactValue=" + i + "," + (j - 1) + "," + sexpValue + "," + sactValue);}
						//#endif
						assertEquals(mname + " getDocEncoding result not expected.",
								sexpValue, sactValue);
						break;
					case XmlRequest.GET_NAMESPACES:
						new ExtParser().parseNamespaces(parser);
						String[] snamespaces = parser.getNamespaces();
						Vector vnamespaces = (Vector)expObj;
						final int vnamespaces_len = vnamespaces.size();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " getNamespaces i,j,vnamespaces_len,snamespaces.length=" + i + "," + j + "," + vnamespaces_len + "," + snamespaces.length);}
						//#endif
						final String[] expNamespaces = new String[vnamespaces_len];
						vnamespaces.copyInto(expNamespaces);
						for (int k = 0; k < vnamespaces_len; k++) {
							String expValuek = expNamespaces[k];
							String actValuek = snamespaces[k];
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest(mname + " getNamespaces i,j,k,expValuek,actValuek=" + i + "," + j + "," + k + "," + expValuek + "," + actValuek);}
							//#endif
							assertEquals(mname + 
									" getNamespaces result not expected:  i,j,k="+ i + "," + j + "," + k,
									expValuek, actValuek);
						}
						break;
					case XmlRequest.GET_ATTRIBUTE:
						String sexpValue2 = ((String)expObj).toString();
						String sreqAttr = (String)xmlRequests[++i];
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logAttrChar) {
							parser.setLogChar(logAttrChar);
						}
						//#endif
						//#endif
						String sactValue2 = parser.getAttributeValue(sreqAttr);
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logAttrChar) {
							parser.setLogChar(false);
						}
						//#endif
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " getAttributeValue i,j,sreqAttr,sexpValue2,sreqAttr,sactValue=" + i + "," + (j - 1) + "," + sexpValue2 + "," + sreqAttr + "," + sactValue2);}
						//#endif
						assertEquals(mname + " getAttributeValue result not expected:  i,j="+ i + "," + j,
								sexpValue2, sactValue2);
						break;
					case XmlRequest.GET_TEXT:
						String sexpValue3 = ((String)expObj).toString();
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logTextChar) {
							parser.setLogChar(logTextChar);
						}
						//#endif
						//#endif
						String sactValue3 = parser.getText(true);
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logTextChar) {
							parser.setLogChar(false);
						}
						//#endif
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " getText i,j,sexpValue3,sactValue3=" + i + "," + (j - 1) + "," + sexpValue3 + "," + sactValue3);}
						//#endif
						assertEquals(mname + " getText result not expected:  i,j="+ i + "," + j,
								sexpValue3, sactValue3);
						break;
					case XmlRequest.GET_NAME:
						String sexpValue4 = ((String)expObj).toString();
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logNameChar) {
							parser.setLogChar(logNameChar);
						}
						//#endif
						//#endif
						String sactValue4 = parser.getName();
						//#ifdef DTEST
						//#ifdef DLOGGING
						if (logNameChar) {
							parser.setLogChar(false);
						}
						//#endif
						//#endif
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest(mname + " getName i,j,sexpValue4,sactValue4=" + i + "," + (j - 1) + "," + sexpValue4 + "," + sactValue4);}
						//#endif
						assertEquals(mname + " getName result not expected:  i,j="+ i + "," + j,
								sexpValue4, sactValue4);
						break;
					default:
						break;
				}
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

}
//#endif
