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

package com.substanceofcode.rssreader.presentation;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author Tommi Laukkanen
 */
public class SettingsForm extends Form implements CommandListener {
    
    private RssReaderMIDlet m_midlet;
    private Command m_okCommand;
    private Command m_cancelCommand;
    
    private TextField m_itemCountField;
    
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
        this.append( m_itemCountField );
    }
    
    public void commandAction(Command command, Displayable displayable) {
        if(command==m_okCommand) {
            // Save settings
            RssReaderSettings settings = m_midlet.getSettings();
            try {
                int maxCount = Integer.parseInt( m_itemCountField.getString() );
                settings.setMaximumItemCountInFeed( maxCount );
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
