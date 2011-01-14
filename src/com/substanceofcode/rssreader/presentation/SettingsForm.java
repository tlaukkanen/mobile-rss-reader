//--Need to modify--#preprocess
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
/*
 * IB 2010-03-07 1.11.5RC1 Remove unneeded import.
 * IB 2010-06-27 1.11.5Dev2 Change getSettingMemInfo to return int array to save memory and simplify.
 * IB 2010-08-21 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-08-15 1.11.5Dev8 Support loading form with FeatureMgr.
 * IB 2010-08-21 1.11.5Dev8 Use showMe for setCurrent.
 * IB 2010-08-21 1.11.5Dev8 Have loading form for updating settings.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for Settings.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for RssReaderSettings.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-10-30 1.11.5Dev12 Use getSysProperty to get system property and return error message.  This gets an error in microemulator if it causes a class to be loaded.
 * IB 2010-11-15 1.11.5Dev14 Use getSysPermission to get get permission for JSR-75 and system property and return error message.  This gets an error in microemulator if it causes a class to be loaded.
 * IB 2010-11-15 1.11.5Dev14 Do not create class variables for StringItem which do not change after initialization.
 * IB 2010-11-15 1.11.5Dev14 Cosmetic changes.
 * IB 2010-11-16 1.11.5Dev14 Add default value of null for getSysProperty and getSysPermission.
 * IB 2010-11-16 1.11.5Dev14 Don't have feed open property now that back will have consistent usage.
 * IB 2010-11-17 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
 * IB 2010-11-17 1.11.5Dev14 Cosmetic change.
 * IB 2010-11-18 1.11.5Dev14 Change jsr-75 exists to give true for allowed and not allowed.
 * IB 2010-11-19 1.11.5Dev14 Use getSysProperty instead of getProperty for working with emulator and possibly other devices.
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

import com.substanceofcode.utils.Settings;
import com.substanceofcode.rssreader.presentation.FeatureMgr;
import com.substanceofcode.rssreader.presentation.FeatureForm;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi Laukkanen
 */
public class SettingsForm extends FeatureForm
implements CommandListener {
    
    private Command m_okCommand;
    private Command m_cancelCommand;
    
    private TextField m_itemCountField;
    private ChoiceGroup m_markUnreadItems;
	//#ifdef DMIDP20
    private ChoiceGroup m_useTextBox;
	//#endif
    private ChoiceGroup m_useStandardExit;
    private ChoiceGroup m_itunesEnabled;
	//#ifdef DMIDP20
    private ChoiceGroup m_fontChoice;
    private ChoiceGroup m_fitPolicy;
    private ChoiceGroup m_nameNews;
	//#endif
    private TextField m_wordCountField;
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
	//#endif
    
    /** Creates a new instance of SettingsForm */
    public SettingsForm(LoadingForm loadForm) {
        super("Settings", loadForm);
        
        m_okCommand = new Command("OK", Command.OK, 4);
        super.addCommand( m_okCommand );
        
        m_cancelCommand = new Command("Cancel", Command.CANCEL, 2);
        super.addCommand( m_cancelCommand );
        
        RssReaderSettings settings = featureMgr.getMidlet().getSettings();
        int maxCount = settings.getMaximumItemCountInFeed();
        
        m_itemCountField = new TextField("Max item count in feed",
                String.valueOf(maxCount), 3, TextField.NUMERIC);
		//#ifdef DMIDP20
		m_itemCountField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_itemCountField );
		String [] choices = {"Mark", "No mark"};
        m_markUnreadItems = new ChoiceGroup("Mark unread items",
				                            Choice.EXCLUSIVE, choices, null);
		//#ifdef DMIDP20
		m_markUnreadItems.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_markUnreadItems );
		//#ifdef DMIDP20
		String [] txtChoices = {"Text (large) box", "Text (line) field"};
        m_useTextBox = new ChoiceGroup("Text entry items",
				                            Choice.EXCLUSIVE, txtChoices, null);
		m_useTextBox.setLayout(Item.LAYOUT_BOTTOM);
        super.append( m_useTextBox );
		//#endif

		String [] txtExit = {"Use standard exit key", "Use menu exit key"};
        m_useStandardExit = new ChoiceGroup("Exit key type",
				                            Choice.EXCLUSIVE, txtExit, null);
		//#ifdef DMIDP20
		m_useStandardExit.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_useStandardExit );
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
//@        super.append( m_itunesEnabled );
		//#endif
		//#ifdef DMIDP20
        m_fontChoice = FeatureMgr.getAddChoiceGroup(this,
				"Choose list font size",
				new String[] {"Default font size", "Small",
				"Medium", "Large"});
        m_fitPolicy = FeatureMgr.getAddChoiceGroup(this,
				"Choose list wraparound",
				new String[] {"Default wrap around", "Wraparound on",
				"Wrap around off"});
        m_nameNews = FeatureMgr.getAddChoiceGroup(this,
				"Put feed name in river of news", new String []
				{"Don't show name", "Show name"});
		//#endif
        int maxWordCount = settings.getMaxWordCountInDesc();
        m_wordCountField = new TextField("Max word count desc abbrev",
                String.valueOf(maxWordCount), 3, TextField.NUMERIC);
		//#ifdef DMIDP20
		m_wordCountField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_wordCountField );
        StringItem pgmMidpVers = new StringItem("Program MIDP version:",
		//#ifdef DMIDP20
				"MIDP-2.0");
		//#else
//@				"MIDP-1.0");
		//#endif
		//#ifdef DMIDP20
		pgmMidpVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( pgmMidpVers );
        StringItem pgCldVers = new StringItem("Program CLDC version:",
				//#ifdef DCLDCV11
//@				"CLDC-1.1");
				//#else
				"CLDC-1.0");
				//#endif
		//#ifdef DMIDP20
		pgCldVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( pgCldVers );
        StringItem pgmJsr75 = new StringItem("Program JSR 75 available:",
		//#ifdef DJSR75
//@				"true");
		//#else
				"false");
		//#endif
		//#ifdef DMIDP20
		pgmJsr75.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( pgmJsr75 );
		Object[] omep = FeatureMgr.getSysProperty("microedition.profiles", null,
				"Unable to get microedition.profiles",
				featureMgr.getLoadForm());
		String mep = (String)omep[0];
		if (mep == null) {
			mep = "N/A";
		}
        StringItem midpVers = new StringItem("Phone MIDP version:", mep);
		//#ifdef DMIDP20
		midpVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( midpVers );
		Object[] omec = FeatureMgr.getSysProperty("microedition.configuration",
				null, "Unable to get microedition.configuration",
				featureMgr.getLoadForm());
		String mec = (String)omec[0];
		if (mec == null) {
			mec = "N/A";
		}
        StringItem cldcVers = new StringItem("Phone CLDC version:", mec);
		//#ifdef DMIDP20
		cldcVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( cldcVers );
		//#ifdef DMIDP10
//@        super.append(new StringItem("Phone JSR 75 available:",
//@				String.valueOf(FeatureMgr.getSysProperty(
//@				"microedition.io.file.FileConnection.version", null,
//@				"Unable to get JSR-75 FileConnection", featureMgr.getLoadForm())[0] != null)));
		//#else
		Object[] ojsr75 = 
				FeatureMgr.getSysPermission(
				"javax.microedition.io.Connector.file.read",
				"microedition.io.file.FileConnection.version", null,
				"Unable to get JSR-75 FileConnection", featureMgr.getLoadForm());
		int ijsr75 = ((Integer)ojsr75[0]).intValue();
		if (ijsr75 >= 0) {
			super.append(new StringItem("Phone JSR 75 available:",
					String.valueOf(ijsr75 == 1)));
			super.append(new StringItem("Phone JSR 75 exists:",
					String.valueOf(ijsr75 >= 0)));
		} else {
			super.append(new StringItem("Phone JSR 75 available:",
					String.valueOf(ojsr75[2] != null)));
		}
		if (ojsr75[2] != null) {
			super.append(new StringItem("Phone JSR 75 version:",
					(String)ojsr75[2]));
		}
		//#endif
		Object[] omepl = FeatureMgr.getSysProperty("microedition.platform",
				null, "Unable to get microedition.platform",
				featureMgr.getLoadForm());
		String mepl = (String)omepl[0];
		if (mepl == null) {
			mepl = "N/A";
		}
        StringItem platformVers = new StringItem("Phone Microedition platform:",
				mepl);
		//#ifdef DMIDP20
		platformVers.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( platformVers );
		Object[] omel = FeatureMgr.getSysProperty("microedition.locale",
				null, "Unable to get microedition.locale",
				featureMgr.getLoadForm());
		String mel = (String)omel[0];
		if (mel == null) {
			mel = "N/A";
		}
        StringItem silocale = new StringItem("Phone Microedition locale:", mel);
		//#ifdef DMIDP20
		silocale.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( silocale );
		//#ifdef DLOGGING
//@        m_logLevelField = new TextField("Logging level",
//@                logger.getParent().getLevel().getName(), 20, TextField.ANY);
		//#ifdef DMIDP20
//@		m_logLevelField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
//@        super.append( m_logLevelField );
		//#endif
        m_pgmMemUsedItem = new StringItem("Application memory used:", "");
		//#ifdef DMIDP20
		m_pgmMemUsedItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_pgmMemUsedItem );
        m_pgmMemAvailItem = new StringItem("Application memory available:", "");
		//#ifdef DMIDP20
		m_pgmMemAvailItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_pgmMemAvailItem );
        m_memUsedItem = new StringItem("DB memory used:", "");
		//#ifdef DMIDP20
		m_memUsedItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_memUsedItem );
        m_memAvailItem = new StringItem("DB memory available:", "");
		//#ifdef DMIDP20
		m_memAvailItem.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_memAvailItem );
        m_threadsUsed = new StringItem("Active Threads:", "");
		//#ifdef DMIDP20
		m_threadsUsed.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        super.append( m_threadsUsed );
		updateForm();
    }
    
	/* Update form items that change per run. */
	public void updateForm() {
        RssReaderMIDlet midlet = featureMgr.getMidlet();
        RssReaderSettings settings = midlet.getSettings();
        int maxCount = settings.getMaximumItemCountInFeed();
        m_itemCountField.setString(String.valueOf(maxCount));
        boolean markUnreadItems = settings.getMarkUnreadItems();
		boolean [] selectedItems = {markUnreadItems, !markUnreadItems};
		m_markUnreadItems.setSelectedFlags( selectedItems );
		//#ifdef DMIDP20
        boolean useTextBox = settings.getUseTextBox();
		boolean [] boolSelectedItems = {useTextBox, !useTextBox};
		m_useTextBox.setSelectedFlags( boolSelectedItems );
		//#endif
        boolean useStdExit = settings.getUseStandardExit();
        prevStdExit = useStdExit;
		boolean [] boolExitItems = {useStdExit, !useStdExit};
		m_useStandardExit.setSelectedFlags( boolExitItems );
        boolean itunesEnabled = settings.getItunesEnabled();
		boolean [] boolItunesEnabled = {!itunesEnabled, itunesEnabled};
		m_itunesEnabled.setSelectedFlags( boolItunesEnabled );
		//#ifdef DMIDP20
        int fontChoice = settings.getFontChoice();
		m_fontChoice.setSelectedFlags( new boolean[] {false, false, false,
				false} );
		m_fontChoice.setSelectedIndex( fontChoice, true );
        int fitPolicy = settings.getFitPolicy();
		m_fitPolicy.setSelectedFlags( new boolean[] {false, false, false} );
		m_fitPolicy.setSelectedIndex( fitPolicy, true );
        boolean nameNews = settings.getBookmarkNameNews();
		m_nameNews.setSelectedFlags( new boolean[] {!nameNews, nameNews});
		//#endif
		int[] memInfo = null;
		try {
			Settings m_settings = Settings.getInstance();
			memInfo = m_settings.getSettingMemInfo();
		} catch (Exception e) {
			memInfo = new int[0];
		}

		System.gc();
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		m_pgmMemUsedItem.setText(((totalMem - freeMem)/1024L) + "kb");
		m_pgmMemAvailItem.setText((freeMem/1024L) + "kb");
        if (memInfo.length == 0) {
			m_memUsedItem.setText("0");
			m_memAvailItem.setText("0");
		} else {
			m_memUsedItem.setText(Integer.toString(memInfo[0]));
			m_memAvailItem.setText(Integer.toString(memInfo[1]));
		}
		m_threadsUsed.setText(Integer.toString(Thread.activeCount()));
	}

    public void commandAction(Command command, Displayable displayable) {
        RssReaderMIDlet midlet = featureMgr.getMidlet();
        if(command==m_okCommand) {
            // Save settings
            RssReaderSettings settings = midlet.getSettings();
			LoadingForm loadForm = LoadingForm.getLoadingForm(
						"Updating settings...", this, null);
			featureMgr.setLoadForm(loadForm);
            try {
                int maxCount = Integer.parseInt( m_itemCountField.getString() );
                settings.setMaximumItemCountInFeed( maxCount );
				boolean markUnreadItems = m_markUnreadItems.isSelected(0);
                settings.setMarkUnreadItems( markUnreadItems );
				//#ifdef DMIDP20
				boolean useTextBox = m_useTextBox.isSelected(0);
				settings.setUseTextBox(useTextBox);
				//#endif
				boolean useStdExit = m_useStandardExit.isSelected(0);
				settings.setUseStandardExit(useStdExit);
				if (useStdExit != prevStdExit) {
					midlet.initExit();
				}
				boolean itunesEnabled = !m_itunesEnabled.isSelected(0);
				//#ifdef DITUNES
//@				settings.setItunesEnabled( itunesEnabled );
				//#else
				settings.setItunesEnabled( false );
				//#endif
				//#ifdef DMIDP20
				int fontChoice = m_fontChoice.getSelectedIndex();
				settings.setFontChoice( fontChoice );
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("fontChoice=" + fontChoice);}
				//#endif
				int fitPolicy = m_fitPolicy.getSelectedIndex();
				settings.setFitPolicy( fitPolicy );
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("fitPolicy=" + fitPolicy);}
				//#endif
				boolean nameNews = !m_nameNews.isSelected(0);
				settings.setBookmarkNameNews( nameNews );
				//#endif
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
//@					featureMgr.showMe(invalidData);
//@					return;
//@				}
				//#endif
			} catch(Throwable e) {
				/* Internal error.:\n */
				loadForm.recordExcFormFin("Internal error.", e);
            }
            
            midlet.showBookmarkList();
        }
        
        if(command==m_cancelCommand) {
            midlet.showBookmarkList();
        }
    }

}
