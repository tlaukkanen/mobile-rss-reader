//--Need to modify--#preprocess
/*
 * EncodingList.java
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
/*
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-10-30 1.11.5Dev12 Use getSysProperty to get system property and return error message.  This gets an error in microemulator if it causes a class to be loaded.
 * IB 2010-11-16 1.11.5Dev14 Add default value of null for getSysProperty.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define logging define
@DLOGDEF@
// Expand to define test ui define
@DTESTUIDEF@

//#ifdef DFULLVERS
//#ifdef DTESTUIUNDO

package com.substanceofcode.testutil.presentation;

import java.lang.IllegalArgumentException;
import java.io.IOException;
import java.util.Hashtable;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
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
import com.substanceofcode.rssreader.presentation.FeatureMgr;
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
public class EncodingList extends List implements CommandListener {
    
    private RssReaderMIDlet m_midlet;
    private Command m_backCommand;
    private Command m_winNoneCommand;
    private Command m_winUniCommand;
    private Command m_noneWinCommand;
    private Command m_uniWinCommand;
    
	//#ifdef DLOGGING
    private Logger m_logger = Logger.getLogger("EncodingList");
    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
    private boolean m_finerLoggable = m_logger.isLoggable(Level.FINER);
    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of EncodingList */
    public EncodingList(RssReaderMIDlet midlet) {
        super("Encoding List", List.IMPLICIT);
        this.m_midlet = midlet;
		//#ifdef DMIDP20
		super.setFitPolicy(List.TEXT_WRAP_ON);
		//#endif
        
		showEncodings("", "Cp1252");
        this.m_backCommand = new Command("Back", Command.BACK, 1);
        super.addCommand( m_backCommand );
        this.m_winNoneCommand = new Command("CP1252, no encoding", Command.SCREEN, 2);
        super.addCommand( m_winNoneCommand );
        this.m_winUniCommand = new Command("CP1252, utf8", Command.SCREEN, 2);
        super.addCommand( m_winUniCommand );
        this.m_noneWinCommand = new Command("No, CP1252", Command.SCREEN, 2);
        super.addCommand( m_noneWinCommand );
        this.m_uniWinCommand = new Command("Uni, CP1252", Command.SCREEN, 2);
        super.addCommand( m_uniWinCommand );
        this.setCommandListener( this );
        
        
    }
    
	/* Put encoding strings into a list and try to convert. */
	private void showEncodings(final String bytesEnc, final String strEnc) {
		while (super.size() > 0) {super.delete(0);}

		super.append("JavaME encoding=" +
				FeatureMgr.getSysProperty("microedition.encoding", null,
					"Unable to get encoding.", null), null);
		super.append("Get bytes encoding: " + bytesEnc, null);
		super.append("String encoding: " + strEnc, null);
		for (int ic = 0; ic < EncodingUtil.CONV_CHARS.length; ic++) {
			String wconv = "";
			try {
				byte [] bval = EncodingUtil.WCONV_CHARS[ic].getBytes();
				if (bytesEnc.length() != 0) {
					bval = EncodingUtil.WCONV_CHARS[ic].getBytes(bytesEnc);
				}
				if (strEnc.length() != 0) {
					wconv = new String(bval, strEnc);
				} else {
					wconv = new String(bval);
				}
			} catch(Exception e) {
				System.err.println("Error testing conversion: " + e.toString());
				super.append("ic=" + ic + "Error testing conversion: " + e.toString(), null);
				e.printStackTrace();
				continue;
			} catch(Throwable e) {
				System.err.println("Error testing conversion: " + e.toString());
				super.append("ic=" + ic + "Error testing conversion: " + e.toString(), null);
				e.printStackTrace();
				continue;
			}
			super.append("ic=" + ic, null);
			for (int jc = 0; jc < wconv.length(); jc++) {
				final char uchar = EncodingUtil.CONV_CHARS[ic].charAt(jc);
				final char uwchar = wconv.charAt(jc);
				final char wchar = EncodingUtil.WCONV_CHARS[ic].charAt(jc);
				super.append("wc=" + jc + "," + uchar + "," + uwchar + "," + wchar, null);
				super.append("wc=" + jc + "," + (int)uchar + "," + (int)uwchar + "," + (int)wchar, null);
				if (uchar != uwchar) {
					super.append("!= wc=" + jc + "," + uchar + "," + uwchar + "," + wchar, null);
				}
			}
		}
		super.setSelectedIndex(0, true);
	}

    public void commandAction(Command command, Displayable displayable) {
		super.outputCmdAct(command, displayable,
				javax.microedition.lcdui.List.SELECT_COMMAND);
        if(command==m_backCommand) {
            m_midlet.showBookmarkList();
		} else if(command==m_winNoneCommand) {
			showEncodings("Cp1252", "");
		} else if(command==m_winUniCommand) {
			showEncodings("Cp1252", "UTF-8");
		} else if(command==m_noneWinCommand) {
			showEncodings("", "Cp1252");
		} else if(command==m_uniWinCommand) {
			showEncodings("UTF-8", "Cp1252");
        }
        
    }
    
}
//#endif
//#endif
