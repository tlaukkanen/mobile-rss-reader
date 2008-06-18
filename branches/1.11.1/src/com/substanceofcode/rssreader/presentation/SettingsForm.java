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

// Expand to define MIDP define
//#define DMIDP20
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define itunes define
//#define DNOITUNES
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test ui define
//#define DNOTESTUI

package com.substanceofcode.rssreader.presentation;

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
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.StringItem;
//#else
//@// If using the test UI define the Test UI's
//@import com.substanceofcode.testlcdui.ChoiceGroup;
//@import com.substanceofcode.testlcdui.Form;
//@import com.substanceofcode.testlcdui.List;
//@import com.substanceofcode.testlcdui.TextBox;
//@import com.substanceofcode.testlcdui.TextField;
//@import com.substanceofcode.testlcdui.StringItem;
//#endif
import javax.microedition.lcdui.Item;

//#ifdef DJSR75
//@import org.kablog.kgui.KFileSelectorMgr;
//#endif
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
    private ChoiceGroup m_useTextBox;
    private ChoiceGroup m_useStandardExit;
    private ChoiceGroup m_feedListOpen;
    private ChoiceGroup m_itunesEnabled;
    private TextField m_wordCountField;
    private StringItem m_pgmMidpVers;
    private StringItem m_pgCldVers;
    private StringItem m_pgmJsr75;
    private StringItem m_midpVers;
    private StringItem m_cldcVers;
    private StringItem m_platformVers;
    private StringItem m_jsr75;
    private StringItem m_pgmMemUsedItem;
    private StringItem m_pgmMemAvailItem;
    private StringItem m_memUsedItem;
    private StringItem m_memAvailItem;
    private StringItem m_threadsUsed;
    private boolean prevStdExit;
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
        
        m_okCommand = new Command("OK", Command.OK, 1);
        this.addCommand( m_okCommand );
        
        m_cancelCommand = new Command("Cancel", Command.CANCEL, 2);
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
        m_useTextBox = new ChoiceGroup("Text entry items",
				                            Choice.EXCLUSIVE, txtChoices, null);
		//#ifdef DMIDP20
		m_useTextBox.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_useTextBox );

		String [] txtExit = {"Use standard exit key", "Use menu exit key"};
        m_useStandardExit = new ChoiceGroup("Exit key type",
				                            Choice.EXCLUSIVE, txtExit, null);
		//#ifdef DMIDP20
		m_useStandardExit.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_useStandardExit );
		String [] itunesEnabledChoices = {"Don't show Itunes data",
				"Show Itunes data"};
        m_itunesEnabled = new ChoiceGroup("Choose to use Itunes data",
				                            Choice.EXCLUSIVE,
											itunesEnabledChoices,
											null);
		//#ifdef DMIDP20
		m_itunesEnabled.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
		//#ifdef DITUNES
//@        this.append( m_itunesEnabled );
		//#endif
		String [] feedBackChoices = {"Open item first", "Back first"};
        m_feedListOpen = new ChoiceGroup("Choose feed list menu first item",
				                            Choice.EXCLUSIVE, feedBackChoices,
											null);
		//#ifdef DMIDP20
		m_feedListOpen.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_feedListOpen );
        int maxWordCount = settings.getMaxWordCountInDesc();
        m_wordCountField = new TextField("Max word count desc abbrev",
                String.valueOf(maxCount), 3, TextField.NUMERIC);
		//#ifdef DMIDP20
		m_wordCountField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_wordCountField );
        m_pgmMidpVers = new StringItem("Program MIDP version:",
		//#ifdef DMIDP20
				"MIDP-2.0");
		//#else
//@				"MIDP-1.0");
		//#endif
		//#ifdef DMIDP20
		m_pgmMidpVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_pgmMidpVers );
        m_pgCldVers = new StringItem("Program CLDC version:",
				//#ifdef DCLDCV11
//@				"CLDC-1.1");
				//#else
				"CLDC-1.0");
				//#endif
		//#ifdef DMIDP20
		m_pgCldVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_pgCldVers );
        m_pgmJsr75 = new StringItem("Program JSR 75 available:",
		//#ifdef DJSR75
//@				"true");
		//#else
				"false");
		//#endif
		//#ifdef DMIDP20
		m_pgmJsr75.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_pgmJsr75 );
        m_midpVers = new StringItem("Phone MIDP version:",
				System.getProperty("microedition.profiles"));
		//#ifdef DMIDP20
		m_midpVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_midpVers );
        m_cldcVers = new StringItem("Phone CLDC version:",
				System.getProperty("microedition.configuration"));
		//#ifdef DMIDP20
		m_cldcVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_cldcVers );
        m_jsr75 = new StringItem("Phone JSR 75 available:",
				new Boolean(System.getProperty(
				"microedition.io.file.FileConnection.version")
				!= null).toString());
		//#ifdef DMIDP20
		m_jsr75.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_jsr75 );
		String me = System.getProperty("microedition.platform");
		if (me == null) {
			me = "N/A";
		}
        m_platformVers = new StringItem("Phone Microedition platform:", me);
		//#ifdef DMIDP20
		m_platformVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_platformVers );
		//#ifdef DLOGGING
//@        m_logLevelField = new TextField("Logging level",
//@                logger.getParent().getLevel().getName(), 20, TextField.ANY);
		//#ifdef DMIDP20
//@		m_logLevelField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
//@        this.append( m_logLevelField );
		//#endif
        m_pgmMemUsedItem = new StringItem("Application memory used:", "");
		//#ifdef DMIDP20
		m_pgmMemUsedItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_pgmMemUsedItem );
        m_pgmMemAvailItem = new StringItem("Application memory available:", "");
		//#ifdef DMIDP20
		m_pgmMemAvailItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_pgmMemAvailItem );
        m_memUsedItem = new StringItem("DB memory used:", "");
		//#ifdef DMIDP20
		m_memUsedItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_memUsedItem );
        m_memAvailItem = new StringItem("DB memory available:", "");
		//#ifdef DMIDP20
		m_memAvailItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_memAvailItem );
        m_threadsUsed = new StringItem("Active Threads:", "");
		//#ifdef DMIDP20
		m_threadsUsed.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        this.append( m_threadsUsed );
		updateForm();
    }
    
	/* Update form items that change per run. */
	public void updateForm() {
		Hashtable memInfo = null;
        RssReaderSettings settings = m_midlet.getSettings();
        int maxCount = settings.getMaximumItemCountInFeed();
        m_itemCountField.setString(String.valueOf(maxCount));
        boolean markUnreadItems = settings.getMarkUnreadItems();
		boolean [] selectedItems = {markUnreadItems, !markUnreadItems};
		m_markUnreadItems.setSelectedFlags( selectedItems );
        boolean useTextBox = settings.getUseTextBox();
		boolean [] boolSelectedItems = {useTextBox, !useTextBox};
		m_useTextBox.setSelectedFlags( boolSelectedItems );
        boolean useStdExit = settings.getUseStandardExit();
        prevStdExit = useStdExit;
		boolean [] boolExitItems = {useStdExit, !useStdExit};
		m_useStandardExit.setSelectedFlags( boolExitItems );
        boolean itunesEnabled = settings.getItunesEnabled();
		boolean [] boolItunesEnabled = {!itunesEnabled, itunesEnabled};
		m_itunesEnabled.setSelectedFlags( boolItunesEnabled );
        boolean feedListOpen = settings.getFeedListOpen();
		boolean [] boolFeedListOpen = {feedListOpen, !feedListOpen};
		m_feedListOpen.setSelectedFlags( boolFeedListOpen );
		try {
			Settings m_settings = Settings.getInstance(m_midlet);
			memInfo = m_settings.getSettingMemInfo();
		} catch (Exception e) {
			memInfo = new Hashtable(0);
		}

		System.gc();
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		m_pgmMemUsedItem.setText(((totalMem - freeMem)/1024L) + "kb");
		m_pgmMemAvailItem.setText((freeMem/1024L) + "kb");
        if (memInfo.size() == 0) {
			m_memUsedItem.setText("0");
			m_memAvailItem.setText("0");
		} else {
			m_memUsedItem.setText((String)memInfo.get("used"));
			m_memAvailItem.setText((String)memInfo.get("available"));
		}
		m_threadsUsed.setText(Integer.toString(Thread.activeCount()));
	}

    public void commandAction(Command command, Displayable displayable) {
		//#ifdef DTESTUI
//@		super.outputCmdAct(command, displayable);
		//#endif
        if(command==m_okCommand) {
            // Save settings
            RssReaderSettings settings = m_midlet.getSettings();
            try {
                int maxCount = Integer.parseInt( m_itemCountField.getString() );
                settings.setMaximumItemCountInFeed( maxCount );
				boolean markUnreadItems = m_markUnreadItems.isSelected(0);
                settings.setMarkUnreadItems( markUnreadItems );
				boolean useTextBox = m_useTextBox.isSelected(0);
				settings.setUseTextBox(useTextBox);
				boolean useStdExit = m_useStandardExit.isSelected(0);
				settings.setUseStandardExit(useStdExit);
				if (useStdExit != prevStdExit) {
					m_midlet.initExit();
				}
				boolean itunesEnabled = !m_itunesEnabled.isSelected(0);
				//#ifdef DITUNES
//@				settings.setItunesEnabled( itunesEnabled );
				//#else
				settings.setItunesEnabled( false );
				//#endif
				boolean feedListOpen = m_feedListOpen.isSelected(0);
				settings.setFeedListOpen( feedListOpen);
                int maxWordCount = Integer.parseInt( m_wordCountField.getString() );
                settings.setMaxWordCountInDesc( maxWordCount );
				//#ifdef DLOGGING
//@				try {
//@					String logLevel =
//@						m_logLevelField.getString().toUpperCase();
//@					logger.getParent().setLevel(Level.parse(logLevel));
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
