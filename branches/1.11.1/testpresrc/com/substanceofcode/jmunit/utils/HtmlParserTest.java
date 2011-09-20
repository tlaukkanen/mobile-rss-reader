//--Need to modify--#preprocess
/*
 * HtmlParserTest.java
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
 * IB 2010-03-07 1.11.4RC1 Don't use observer pattern for MIDP 1.0 as it increases size.
 * IB 2010-04-05 1.11.4RC1 Allow logging of characters for different expected tokens.
 * IB 2010-05-29 1.11.5RC2 Don't use HTML in small memory MIDP 1.0 to save space.
 * IB 2010-07-19 1.11.5Dev8 Convert entities for text if CDATA used.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-15 1.11.5Dev14 Remove redundant new String of literal string.
 * IB 2011-01-14 1.11.5Alpha15 Remove unused and now obsolete cldc10.TestCase
*/

// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifndef DSMALLMEM
//#ifdef DJMTEST
package com.substanceofcode.jmunit.utils;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Vector;

import com.substanceofcode.utils.HTMLParser;
import com.substanceofcode.utils.SgmlParserIntr;
import com.substanceofcode.rssreader.businesslogic.ExtParser;

import com.substanceofcode.jmunit.utils.XmlRequest;

import com.substanceofcode.jmunit.utilities.BaseTestCase;

final public class HtmlParserTest extends BaseTestCase
{

	//#ifdef DTEST
	//#ifdef DLOGGING
	private boolean logParseChar = false; // or traceLoggable
	private boolean logNameChar = false; // or traceLoggable
	private boolean logTextChar = false; // or traceLoggable
	private boolean logAttrChar = false; // or traceLoggable
	//#endif
	//#endif

	public HtmlParserTest() {
		super(4, "HtmlParserTest");
	}

	public void test(int testNumber) throws Throwable {
		switch (testNumber) {
			case 0:
				testHtmlParse1();
				break;
			case 1:
				testHtmlParse2();
				break;
			case 2:
				testHtmlParse3();
				break;
			case 3:
				testHtmlParse5();
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

	public void testHtmlParse1() throws Throwable {
		String mname = "testHtmlParse1";
		try {
			String url = "http://www.baseurl.com";
			String origLink = "http://www.link.com";
			String newLink = HTMLParser.getAbsoluteUrl(url, origLink);
			assertEquals(mname + " link not changed if absolute.", origLink,
					newLink);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testHtmlParse2() throws Throwable {
		String mname = "testHtmlParse2";
		try {
			String url = "http://www.baseurl.com";
			String origLink = "subdir";
			String newLink = HTMLParser.getAbsoluteUrl(url, origLink);
			assertEquals(mname + " link appended changed if not absolute.",
					url + "/" + origLink, newLink);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testHtmlParse3() throws Throwable {
		String mname = "testHtmlParse3";
		try {
			String url = "http://www.baseurl.com/usubdir";
			String origLink = "/lsubdir";
			String newLink = HTMLParser.getAbsoluteUrl(url, origLink);
			assertEquals(mname + " link appended changed if not absolute.",
					"http://www.baseurl.com/lsubdir", newLink);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(mname + " failure ",e);
			//#endif
			e.printStackTrace();
			throw e;
		}
	}

	public void testHtmlParse5() throws Throwable {
		String mname = "testHtmlParse1";
	String[] xmlStrings = {"<html>",
			"<!-- comment1 -->",
			"<head>",
			"<!-- comment2 -->",
			"<title>Andere | Lander - Anderes Deutsch</title>",
			"<!-- comment4 -->",
			"</head>",
			"<body>",
			"<a href=\"http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/\">a link</a>",
			"<image></image>",
			"<!-- comment5 -->",
			"<h1>First Header</h1>",
			"<!-- comment6 -->",
			"<big>Some Big text</big>",
			"<!-- comment7 -->",
			"<cite>Some cited text</cite>",
			"<!-- comment7 -->",
			"<a href=\"http://www.testurl2.com/\">second link< /a  >",
			"</body>",
			"</html>"};

		Object [] htmlRequests = new Object[] {XmlRequest.IGET_NAME, // html
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // head
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // html title
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, // body
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, XmlRequest.IGET_ATTRIBUTE, "href", // a
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // image
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // h1
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // big
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, // cite
		XmlRequest.IGET_PARSE, XmlRequest.IGET_NAME, XmlRequest.IGET_TEXT, XmlRequest.IGET_ATTRIBUTE, "href", // a
		};
		Vector expHtmlResults = new Vector();
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("html");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("head");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("title");
		expHtmlResults.addElement("Andere | Lander - Anderes Deutsch");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("body");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("a");
		expHtmlResults.addElement("a link");
		expHtmlResults.addElement("http://german.lss.wisc.edu/gdgsa/podcast/Andere_Lander/");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("image");
		expHtmlResults.addElement("");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("h1");
		expHtmlResults.addElement("First Header");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("big");
		expHtmlResults.addElement("Some Big text");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("cite");
		expHtmlResults.addElement("Some cited text");
		expHtmlResults.addElement(new Integer(SgmlParserIntr.ELEMENT));
		expHtmlResults.addElement("a");
		expHtmlResults.addElement("second link");
		expHtmlResults.addElement("http://www.testurl2.com/");
		Vector xmlResults = new Vector();
		htmlParserTestSub(mname, "http://www.baseurl.com", xmlStrings, "utf-8", htmlRequests, expHtmlResults,
				xmlResults);
	}

    public void htmlParserTestSub(final String mname, final String url,
			String[] xmlStrings,
			String enc,
			Object [] htmlRequests, Vector expHtmlResults, Vector xmlResults )
	throws Throwable {
		try {
			//#ifdef DLOGGING
			logger.info("Started " + mname);
			//#endif
			StringBuffer htmlsb = new StringBuffer();
			for (int i = 0; i < xmlStrings.length; i++) {
				htmlsb.append(xmlStrings[i]);
			}
			HTMLParser parser = new HTMLParser(url, 
						new ByteArrayInputStream(
							htmlsb.toString().getBytes(enc)));
			//#ifdef DTEST
			//#ifdef DLOGGING
			if (logParseChar) {
				parser.setLogChar(logParseChar);
			}
			//#endif
			//#endif
			int parsingResult = parser.parse();
			//#ifdef DTEST
			//#ifdef DLOGGING
			if (logParseChar) {
				parser.setLogChar(false);
			}
			//#endif
			//#endif
			int j = 0;
			int pexpValue = 
					((Integer)expHtmlResults.elementAt(j)).intValue();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(mname + " parse j,pexpValue=" + j + "," + pexpValue + "," + SgmlParserIntr.PARSING_RESULTS[pexpValue] + "," + parsingResult + "," + SgmlParserIntr.PARSING_RESULTS[parsingResult]);}
			//#endif
			assertEquals(mname + " 0 parser result not expected.", pexpValue,
					parsingResult);
			j++;
			for (int i = 0; i < htmlRequests.length; i++) {
				Object expObj = expHtmlResults.elementAt(j++);
				//#ifdef DLOGGING
				if (traceLoggable) {logger.trace(mname + " htmlParserTestSub i,j,expObj.getClass().getName(),expObj.hashCode(),expObj.toString()=" + i + "," + (j - 1) + "," + ((expObj == null) ? "null" : expObj.getClass().getName()) + "," + ((expObj == null) ? "null" : Integer.toString(expObj.hashCode())) + "," + ((expObj == null) ? "null" : expObj.toString()) );}
				//#endif
				switch (((Integer)htmlRequests[i]).intValue()) {
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
						String sreqAttr = (String)htmlRequests[++i];
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
//#endif
