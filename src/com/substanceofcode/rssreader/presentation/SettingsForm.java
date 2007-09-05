/*
 * SettingsForm.java
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

package com.substanceofcode.rssreader.presentation;

import java.lang.IllegalArgumentException;
import java.io.IOException;
import java.util.Hashtable;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Item;

import com.substanceofcode.utils.Settings;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi Laukkanen
 */
public class SettingsForm extends Form implements CommandListener {
    
    private RssReaderMIDlet m_midlet;
    private Command m_okCommand;
    private Command m_cancelCommand;
    
    private TextField m_itemCountField;
    private ChoiceGroup m_markUnreadItems;
	//#ifdef DMIDP20
    private ChoiceGroup m_useTextBox;
	//#endif
    private StringItem m_pgmMemUsedItem;
    private StringItem m_pgmMemAvailItem;
    private StringItem m_memUsedItem;
    private StringItem m_memAvailItem;
	//#ifdef DLOGGING
//@    private TextField m_logLevelField;
//@    private Logger logger = Logger.getLogger("SettingsForm");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of SettingsForm */
    public SettingsForm(RssReaderMIDlet midlet) {
        super("Settings");
        m_midlet = midlet;
        
        m_okCommand = new Command("OK", Command.SCREEN, 1);
        this.addCommand( m_okCommand );
        
        m_cancelCommand = new Command("Cancel", Command.SCREEN, 2);
        this.addCommand( m_cancelCommand );
        
        this.setCommandListener( this );
        
        RssReaderSettings settings = m_midlet.getSettings();
        int maxCount = settings.getMaximumItemCountInFeed();
        
        m_itemCountField = new TextField("Max item count in feed",
                String.valueOf(maxCount), 3, TextField.NUMERIC);
		//#ifdef DMIDP20
		m_itemCountField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_itemCountField );
		String [] choices = {"Mark", "No mark"};
        m_markUnreadItems = new ChoiceGroup("Mark unread items",
				                            Choice.EXCLUSIVE, choices, null);
		//#ifdef DMIDP20
		m_markUnreadItems.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_markUnreadItems );
		String [] txtChoices = {"Text (large) box", "Text (line) field"};
		//#ifdef DMIDP20
        m_useTextBox = new ChoiceGroup("Text entry items",
				                            Choice.EXCLUSIVE, txtChoices, null);
		m_useTextBox.setLayout(Item.LAYOUT_BOTTOM);
        this.append( m_useTextBox );
		//#endif
		//#ifdef DLOGGING
//@        m_logLevelField = new TextField("Logging level",
//@                logger.getLevel().getName(), 20, TextField.ANY);
		//#ifdef DMIDP20
//@		m_logLevelField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
//@        this.append( m_logLevelField );
		//#endif
        m_pgmMemUsedItem = new StringItem("Application memory used", "");
		//#ifdef DMIDP20
		m_pgmMemUsedItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_pgmMemUsedItem );
        m_pgmMemAvailItem = new StringItem("Application memory available", "");
		//#ifdef DMIDP20
		m_pgmMemAvailItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_pgmMemAvailItem );
        m_memUsedItem = new StringItem("DB memory used", "");
		//#ifdef DMIDP20
		m_memUsedItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_memUsedItem );
        m_memAvailItem = new StringItem("DB memory available", "");
		//#ifdef DMIDP20
		m_memAvailItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_memAvailItem );
		updateForm();
    }
    
	public void updateForm() {
		Hashtable memInfo = null;
        RssReaderSettings settings = m_midlet.getSettings();
        boolean markUnreadItems = settings.getMarkUnreadItems();
		boolean [] selectedItems = {markUnreadItems, !markUnreadItems};
		m_markUnreadItems.setSelectedFlags( selectedItems );
        boolean useTextBox = settings.getUseTextBox();
		//#ifdef DMIDP20
		boolean [] txtSelectedItems = {useTextBox, !useTextBox};
		m_useTextBox.setSelectedFlags( txtSelectedItems );
		//#endif
		try {
			Settings m_settings = Settings.getInstance(m_midlet);
			memInfo = m_settings.getSettingMemInfo();
		} catch (Exception e) {
			memInfo = new Hashtable(0);
		}

		m_pgmMemUsedItem.setText(
				(Runtime.getRuntime().totalMemory() -
				Runtime.getRuntime().freeMemory())/1024 + "kb");
		m_pgmMemAvailItem.setText(
				Runtime.getRuntime().freeMemory()/1024 + "kb");
        if (memInfo.size() == 0) {
			m_memUsedItem.setText("0");
			m_memAvailItem.setText("0");
		} else {
			m_memUsedItem.setText((String)memInfo.get("used"));
			m_memAvailItem.setText((String)memInfo.get("available"));
		}
	}

    public void commandAction(Command command, Displayable displayable) {
        if(command==m_okCommand) {
            // Save settings
            RssReaderSettings settings = m_midlet.getSettings();
            try {
                int maxCount = Integer.parseInt( m_itemCountField.getString() );
                settings.setMaximumItemCountInFeed( maxCount );
				boolean markUnreadItems = m_markUnreadItems.isSelected(0);
                settings.setMarkUnreadItems( markUnreadItems );
				//#ifdef DMIDP20
				boolean useTextBox = m_useTextBox.isSelected(0);
				settings.setUseTextBox(useTextBox);
				//#endif
				//#ifdef DLOGGING
//@				try {
//@					String logLevel =
//@						m_logLevelField.getString().toUpperCase();
//@					logger.setLevel(Level.parse(logLevel));
//@					settings.setLogLevel( logLevel );
//@				} catch (IllegalArgumentException e) {
//@					Alert invalidData = new Alert("Invalid Log Level",
//@									"Invalid Log Level " +
//@									m_logLevelField.getString(),
//@									null,
//@									AlertType.ERROR);
//@					invalidData.setTimeout(Alert.FOREVER);
//@					Display.getDisplay(m_midlet).setCurrent(invalidData, this);
//@					return;
//@				}
				//#endif
            } catch(Exception e) {
                System.err.println("Error: " + e.toString());
            }
            
            m_midlet.showBookmarkList();
        }
        
        if(command==m_cancelCommand) {
            m_midlet.showBookmarkList();
        }
    }
    
}
