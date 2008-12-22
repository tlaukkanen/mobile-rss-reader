/*
 * 
 * UiUtil.java
 *
 * Copyright (C) 2008 Irving Bunton, Jr
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

// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define logging define
@DLOGDEF@


package com.substanceofcode.rssreader.presentation;

import java.util.Vector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
// If not using the test UI define the J2ME UI's
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;
//#else
// If using the test UI define the Test UI's
import com.substanceofcode.testlcdui.ChoiceGroup;
import com.substanceofcode.testlcdui.Form;
import com.substanceofcode.testlcdui.List;
import com.substanceofcode.testlcdui.TextBox;
import com.substanceofcode.testlcdui.TextField;
import com.substanceofcode.testlcdui.StringItem;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * RSS feed reader MIDlet
 *
 * RssReaderMIDlet is an application that can read RSS feeds. User can store
 * multiple RSS feeds as bookmarks into application's record store.
 *
 * @author  Irving Bunton
 * @version 1.0
 */
final public class UiUtil implements CommandListener {
 
    private RssReaderMIDlet   m_midlet;  // The RssReaderMIDlet midlet
    private Form        m_urlRrnForm;    // The form to return to for URL box
    private TextField   m_urlRrnItem;    // The item to return to for URL box

  /**
   * Return the selected index of the choice.  If nothing selected (-1),
   * return 0 if size &gt; 0, or -1 if 0 size.
   *
   * Constructor
   * @param choice - Choice interface
   *
   * @author Irv Bunton
   */
	static public int getSelectedIndex(Choice choice) {
		final int selIdx = choice.getSelectedIndex();
		if (selIdx != -1) {
			return selIdx;
		} else {
			if (choice.size() > 0) {
				choice.setSelectedIndex(0, true);
				return 0;
			} else {
				return -1;
			}
		}
	}

  /**
   * Get the place (index) in a list to insert/append an element if using
   * an inert, add, or append command.
   *
   * @param c - command selected by user
   * @param cplace - current place selected in list (-1 if no selection)
   * @param insCmd
   * @param addCmd
   * @param appndCmd
   * @param plist
   * @return    final
   * @author Irv Bunton
   */
	static int getPlaceIndex(Command c, Command insCmd,
							Command addCmd,
							Command appndCmd,
							javax.microedition.lcdui.List plist) {
		if( (insCmd == null ) || (addCmd == null ) || (appndCmd == null )) {
			return -1;
		}

		if( (c == insCmd ) || (c == addCmd ) || (c == appndCmd )) {
			final int blen = plist.size();
			int cplace = getSelectedIndex(plist);
			int addElem = (cplace == -1) ? blen : cplace;
			if(( c == addCmd ) && (addElem < blen)) {
				addElem++;
			}
			if (c == appndCmd ) {
				addElem = blen;
			}
			if ((addElem < 0) || (addElem > blen)) {
				addElem = blen;
			}
			return addElem;
		} else {
			return -1;
		}
	}

  /**
   * Create a ChoiceGroup, set the layout and add it to the form.
   *
   * @param label
   * @param choices
   * @return    ChoiceGroup
   * @author Irv Bunton
   */
	static public ChoiceGroup getAddChoiceGroup(Form form, String label,
												String[] choices) {
        ChoiceGroup choiceGroup = new ChoiceGroup(label,
				                            Choice.EXCLUSIVE, choices, null);
		//#ifdef DMIDP20
		choiceGroup.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        form.append( choiceGroup );
		return choiceGroup;
	}

    /** Initialize URL text Box */
    public void initializeURLBox(RssReaderMIDlet midlet, final String url,
			Form prevForm, TextField prevItem) {
		m_midlet = midlet;
		TextBox boxURL = new TextBox("URL", url, 256, TextField.URL);
		m_urlRrnForm = prevForm;
		m_urlRrnItem = prevItem;
		boxURL.addCommand(new Command("OK", Command.OK, 1));
		boxURL.addCommand(new Command("Cancel", Command.CANCEL, 2));
        boxURL.setCommandListener(this);
		midlet.setCurrent( boxURL );
    }
    
    /** Respond to commands */
    public void commandAction(Command c, Displayable s) {

		/** Paste into URL field from previous form.  */
		if( (m_urlRrnForm != null) &&
			(s instanceof TextBox) && (c.getCommandType() == Command.OK) ){
			m_urlRrnItem.setString( ((TextBox)s).getString() );
			//#ifdef DMIDP20
			m_midlet.setCurrentItem( m_urlRrnItem );
			//#else
			m_midlet.setCurrent( m_urlRrnForm );
			//#endif
			// Free memory
			m_urlRrnForm = null;
			// Free memory
			m_urlRrnItem = null;
		}
		
		/** Cancel the box go back to the return form.  */
		if( (m_urlRrnForm != null) &&
			(s instanceof TextBox) &&
			(c.getCommandType() == Command.CANCEL) ){
			//#ifdef DMIDP20
			m_midlet.setCurrentItem( m_urlRrnItem );
			//#else
			m_midlet.setCurrent( m_urlRrnForm );
			//#endif
			// Free memory
			m_urlRrnForm = null;
			m_urlRrnItem = null;
		}
	}
	
}
