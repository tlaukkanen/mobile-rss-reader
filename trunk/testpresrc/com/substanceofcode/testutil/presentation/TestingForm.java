/*
 * TestingForm.java
 *
 * Copyright (C) 2008 Irving Bunton
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
// Expand to define test ui define
@DTESTUIDEF@

//#ifdef DTESTUI

package com.substanceofcode.testutil.presentation;

import java.lang.IllegalArgumentException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.lang.StringBuffer;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.utils.HTMLParser;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import com.substanceofcode.testlcdui.ChoiceGroup;
import com.substanceofcode.testlcdui.Form;
import com.substanceofcode.testlcdui.List;
import com.substanceofcode.testlcdui.TextBox;
import com.substanceofcode.testlcdui.TextField;
import com.substanceofcode.testlcdui.StringItem;
import javax.microedition.lcdui.Item;

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.utils.EncodingUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Show characters with different encodings to help show the presentation.
 * Also, allow easy test of how it will look.
 *
 * @author Irving Bunton
 */
public class TestingForm extends Form implements CommandListener {
    
    private RssReaderMIDlet m_midlet;
    private Command m_tstEncCommand;
    private Command m_backCommand;
    
	//#ifdef DLOGGING
    private Logger m_logger = Logger.getLogger("TestingForm");
    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
    private boolean m_finerLoggable = m_logger.isLoggable(Level.FINER);
    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of TestingForm */
    public TestingForm(RssReaderMIDlet midlet) {
        super("Testing Form");
        this.m_midlet = midlet;
        
        this.m_tstEncCommand = new Command("Encoding Test", Command.SCREEN, 1);
        super.addCommand( m_tstEncCommand );
        this.m_backCommand = new Command("Back", Command.BACK, 5);
        super.addCommand( m_backCommand );
        this.setCommandListener( this );
    }
    
	/* Perform a test. */
	private void performTest() {
		while (super.size() > 0) {super.delete(0);}

		// TODO put in expected results and test descr
		super.append(new StringItem("isConvWinUni()=",
					new Boolean(EncodingUtil.isConvWinUni()).toString()));
		super.append(new StringItem("JavaME encoding=",
				System.getProperty("microedition.encoding")));
		appendEntityAlphaTest("Test with no data.", "", "");
		appendEntityAlphaTest("Test with one entity.", "&lt;", "<");
		appendEntityAlphaTest("Test with one entity.", "&lt;&gt;&amp;&quot;&nbsp;&apos;",
						 "<>&\" '");
		appendEntityAlphaTest("Test with one entity.", " &lt;&gt; &amp;&quot; &nbsp;",
						 " <> &\"  ");
		appendEntityAlphaTest("Test with one entity.", " &lt &gt; &amp;&quot; &nbsp;",
						 " &lt > &\"  ");
		appendEntityAlphaTest("Test with one amp replace.",
				" &amp;quot;backpack&amp;quot;", "\"backpack\"");
		appendEntityAlphaTest("Test with one entity.",
				"  &lt;  &gt;  &auml;  &ouml;  ",
						"");
		String html = "&auml; &Auml; &ouml; &Ouml; &lt; &amp; &gt; " +
			"&apos; &nbsp; &quot;";
		appendEntityAlphaTest("Test with one entity.", html,
						"");
		StringBuffer sbControl = new StringBuffer();
		for (int ic = 0x80; ic <= 0x9f; ic++) {
			sbControl.append((char)ic);
		}
		super.append("control(0)=\n" + Integer.toHexString(sbControl.charAt(0)));
		String control = sbControl.toString();
		char[] cc = new char[sbControl.length()];
		sbControl.getChars(0, cc.length, cc, 0);
		String control2 = new String(cc);
		super.append("control(0)=\n" + control.charAt(0) + "," + Integer.toHexString(control.charAt(0)));
		super.append("control2(0)=\n" + control2.charAt(0) + "," + Integer.toHexString(control2.charAt(0)));
		StringBuffer sbTst = new StringBuffer();
		StringBuffer sbRes = new StringBuffer();
		appendHashTest("Iso entities", EncodingUtil.getConvIso88591(), sbTst, sbRes);
		// Convert from ISO to iso
		String cvRes = EncodingUtil.replaceSpChars(sbRes.toString(),
				false, false, false, false);
		StringBuffer sbwTst = new StringBuffer();
		StringBuffer sbwRes = new StringBuffer();
		appendHashTest("cp1252 entities", EncodingUtil.getConvCp1252(), sbwTst, sbwRes);
		// Convert from windows to windows
		String cvwRes = EncodingUtil.replaceSpChars(sbwRes.toString(),
				true, false, true, false);
		appendEntitySpTest("Test with iso conv to iso midp phone.",
				sbRes.toString(), false, false, false, cvRes);
		// Convert from windows to windows .  Given that windows uses
		// ISO control characters, it should not match the other encoding.
		appendEntitySpTest("Test with win conv to iso midp phone.",
				sbwRes.toString(), true, false, false, cvRes);
		appendEntitySpTest("Test with win conv to win midp phone.",
				sbwRes.toString(), true, true, false, cvwRes);
		// Convert from ISO to windows.  Given that windows supports
		// the ISO characters, it should match other decoding too.
		appendEntitySpTest("Test with iso conv to windows midp phone.",
				sbRes.toString(), false, true, false, cvwRes);
		appendEntitySpTest("Test with iso sponly conv to iso midp phone.",
				control, false, false, false, control);
		// Convert from windows to windows .  Given that windows uses
		// ISO control characters, it should not match the other encoding.
		appendEntitySpTest("Test with win sponly conv to iso midp phone.",
				control, true, false, false, control);
		appendEntitySpTest("Test with win sponly conv to win midp phone.",
				control, true, true, false, control);
		// Convert from ISO to windows.  Given that windows supports
		// the ISO characters, it should match other decoding too.
		appendEntitySpTest("Test with iso sponly conv to windows midp phone.",
				control, false, true, false, control);
		String[] isoSp = EncodingUtil.getIsoSpecialEntities();
		char[] isoSpVals =
			{'-', // en dash 
			'-', // em dash 
			'\'', // left single quotation mark 
			'\'', // right single quotation mark 
			'\'', // single low-9 quotation mark 
			'\"', // left double quotation mark 
			'\"', // right double quotation mark 
			'\"'}; // double low-9 quotation mark 
		Hashtable isoConv = new Hashtable();
		EncodingUtil.initEntVals(isoConv, isoSp, isoSpVals);
		StringBuffer sbSp = new StringBuffer();
		StringBuffer sbSpRes = new StringBuffer();
		appendHashTest("Iso special entities", isoConv, sbSp, sbSpRes);
	}

	private void appendHashTest(String tstName, Hashtable tst,
							    StringBuffer sbTst, StringBuffer sbRes) {
		Enumeration etst = tst.keys();
		for (;etst.hasMoreElements();) {
			String ckey = (String)etst.nextElement();
			sbTst.append("&" + ckey + "; ");
			sbRes.append((String)tst.get(ckey) + " ");
		}
		appendEntityAlphaTest(tstName, sbTst.toString(), sbRes.toString());

	}

	private void buildCharEnts(String tstName, char[] tst,
							    StringBuffer sbTst) {
		for (int ic = 0;ic < tst.length;ic++) {
			char[] carr = new char[1];
			String cval = new String(carr);
			sbTst.append("&" + cval + "; ");
		}
	}

	private void appendEntityAlphaTest(String tstName, String tst, String res) {
		super.append(new StringItem(tstName, tst));
		super.append(new StringItem("(" + tstName + ") Result:", "\n" + res));
		String actres = EncodingUtil.replaceAlphaEntities(tst);
		super.append(new StringItem("(" + tstName + ") Act Result: ", "\n" + actres));
		super.append("\n" + new Boolean(actres.equals(res)).toString());
		super.append("-------");
	}

	private void appendEntitySpTest(String tstName, String tst,
							        boolean isWindows, boolean midpWin,
									boolean midpUni, String res) {
		super.append(new StringItem(tstName, tst));
		super.append(new StringItem("(" + tstName + ") Result:", "\n" + res));
		String actres = EncodingUtil.replaceSpChars(tst,
				isWindows, false, midpWin, midpUni);
		super.append(new StringItem("(" + tstName + ") Act Result: ", "\n" + actres));
		super.append("\n" + new Boolean(actres.equals(res)).toString());
		super.append("-------");
	}

    public void commandAction(Command command, Displayable displayable) {
		super.outputCmdAct(command, displayable);
        if(command==m_backCommand) {
            m_midlet.showBookmarkList();
			while (super.size() > 0) {super.delete(0);}
		} else if(command==m_tstEncCommand) {
			performTest();
		}
    }
    
}
//#endif
