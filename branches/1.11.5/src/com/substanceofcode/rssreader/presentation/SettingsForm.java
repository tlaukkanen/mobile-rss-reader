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
 * IB 2011-01-14 1.11.5Alpha15 Only compile some portions if it is the full version.
 * IB 2011-01-11 1.11.5Alpha15 Use super.featureMgr instead of featureMgr.
 * IB 2011-01-14 1.11.5Dev15 Have optional backlight after update/refresh all.
 * IB 2011-01-14 1.11.5Dev15 Have optional vibrate after update/refresh all.
 * IB 2011-01-14 1.11.5Dev15 Have some public static keys for int options to allow shorter coding in SettingsForm.
 * IB 2011-01-14 1.11.5Dev15 Change static fields to instance vars for the RssReaderSettings singleton to reduce static memory used.
 * IB 2011-01-14 1.11.5Dev15 Have fields to show the preferred jar/jad based on current MIDP/JSR settings and the current jar/jad names for internet link/full code to display the text "current" if the preferred jar/jad name matches the current one.  In the future, the MIDP 2.0 version will download the preferred version on user request.
 * IB 2011-01-14 1.11.5Dev15 Have test update command to allow upating of the current jar/jad names to test internet link code to display the text "current" if the preferred jar/jad name matches the current one.
 * IB 2011-01-14 1.11.5Dev15 More logging.
 * IB 2011-01-12 1.11.5Alpha15 Use midlet in FeatureMgr with getRssMidlet to get the RssReaderMIDlet.
 * IB 2011-01-12 1.11.5Alpha15 Have getAddTextField to both create a text field which uses layout bottom (MIDP 2.0) and appends the field to the given form.
 * IB 2011-01-12 1.11.5Alpha15 Have getAddChoiceGroup to both create a choice field which uses layout bottom (MIDP 2.0) and appends the field to the given form.
 * IB 2011-01-12 1.11.5Alpha15 Have getAddStringItem to both create a string item which uses layout bottom (MIDP 2.0) and appends the item to the given form.
 * IB 2011-01-12 1.11.5Alpha15 Have novice as part of the program name if novice is built.
 * IB 2011-01-12 1.11.5Alpha15 If unable to get microedition.locale, get file.encoding for applet/standalone emulator.
 * IB 2011-01-12 1.11.5Alpha15 Use setSettingNbr to allow easy setting of int RssReaderSettings settings using key names.
 * IB 2011-01-12 1.11.5Alpha15 If logging level is changed, use initLogVars to reset the logging vars in RssReaderMIDlet.
 * IB 2011-01-12 1.11.5Alpha15 Use main display from FeatureMgr.
 * IB 2011-01-12 1.11.5Alpha15 If internet link version, have thread to initialize the settings form and then show the form.
 * IB 2011-01-12 1.11.5Alpha15 If internet link version, don't show optional items except standard exit.
 * IB 2011-01-18 1.11.5Alpha15 Give db avail./size in kb.
 * IB 2011-01-18 1.11.5Dev16 Use jsr75avail from FeatureMgr to determine availabilit of JSR-75.
 * IB 2011-01-18 1.11.5Dev16 Have about menu item to show the about/license info.
 * IB 2011-01-18 1.11.5Dev16 Give db used and available in Kbs.
 * IB 2011-01-18 1.11.5Dev16 Use initForm (singular) to initialize the settings form's vars/form.  
 * IB 2011-01-18 1.11.5Dev16 Use initForms (plural in RssReaderMIDlet) to initialize the settings form.  This will handle showing of about/license.
 * IB 2011-01-18 1.11.5Dev16 Use getCmdAdd to create and add a command.
 */

// Expand to define MIDP define
//#define DMIDP20
// Expand to define CLDC define
//#define DCLDCV10
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define smartphone define
//#define DNOSMARTPHONE
// Expand to define itunes define
//#define DFULLVERS
// Expand to define itunes define
//#define DNOINTLINK
// Expand to define novice define
//#define DNONOVICE
// Expand to define signed define
//#define DNOSIGNED
// Expand to define godaddy define
//#define DNOGODADDY
// Expand to define thawte define
//#define DNOTHAWTE
// Expand to define verisign define
//#define DNOVERISIGN
// Expand to define logging define
//#define DNOLOGGING
// Expand to define test define
//#define DNOTEST
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
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.rssreader.presentation.FeatureMgr;
import com.substanceofcode.rssreader.presentation.FeatureForm;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi Laukkanen
 */
public class SettingsForm extends FeatureForm
implements CommandListener
	//#ifdef DINTLINK
//@	,Runnable
	//#endif
{
    
    private Command m_okCommand;
	//#ifdef DFULLVERS
    private Command m_cancelCommand;
	//#endif
	//#ifdef DTEST
//@    private Command m_updateCommand;
	//#endif
    
	//#ifdef DFULLVERS
    private TextField m_itemCountField;
    private ChoiceGroup m_markUnreadItems;
	//#ifdef DMIDP20
    private ChoiceGroup m_useTextBox;
	//#endif
	//#ifdef DSMARTPHONE
//@    private ChoiceGroup m_itunesEnabled;
	//#else
    private StringItem m_itunesEnabled;
	//#endif
	//#ifdef DMIDP20
    private ChoiceGroup m_fontChoice;
    private ChoiceGroup m_fitPolicy;
    private ChoiceGroup m_nameNews;
    private TextField m_backlightFlash;
    private TextField m_vibrate;
	//#endif
	//#endif
    private ChoiceGroup m_useStandardExit;
    private TextField m_wordCountField;
    private StringItem m_pgmMemUsedItem;
    private StringItem m_pgmMemAvailItem;
    private StringItem m_memUsedItem;
    private StringItem m_memAvailItem;
    private StringItem m_threadsUsed;
    private boolean prevStdExit;
    private String prevLevel;
    private String m_preffnp;
    private String m_currfnp;
    private int m_preffJad;
    private StringItem m_preffJadTx;
    private StringItem m_preffJarTx;
	//#ifdef DTEST
//@    private TextField m_currJadTx;
//@    private TextField m_currJarTx;
	//#endif

	//#ifdef DLOGGING
//@    private TextField m_logLevelField;
//@    private Logger m_logger = Logger.getLogger("SettingsForm");
//@    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
//@    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif
    
    /** Creates a new instance of SettingsForm */
    public SettingsForm(LoadingForm loadForm) {
        super("Settings", loadForm);
	}
        
 	public void initForm() {
        m_okCommand = FeatureMgr.getCmdAdd(this, "OK", Command.OK, 4);

		//#ifdef DFULLVERS
        m_cancelCommand = FeatureMgr.getCmdAdd(this, "Cancel", Command.CANCEL, 2);
		//#else
//@		FeatureMgr.m_aboutCmd = FeatureMgr.getCmdAdd(this, "About", Command.SCREEN, 100);
//@		FeatureMgr.getRssMidlet().initExit(this);
		//#endif
        
		//#ifdef DTEST
//@        m_updateCommand = new Command("Update", Command.ITEM, 5);
//@        super.addCommand( m_updateCommand );
		//#endif
        
        RssReaderSettings settings = featureMgr.getRssMidlet().getSettings();
        Settings csettings = settings.getSettingsInstance();
		//#ifdef DFULLVERS
        int maxCount = settings.getMaximumItemCountInFeed();
        
        m_itemCountField = FeatureMgr.getAddTextField(this,
				"Max item count in feed",
                String.valueOf(settings.INIT_MAX_ITEM_COUNT), 3,
				TextField.NUMERIC);

        m_markUnreadItems = FeatureMgr.getAddChoiceGroup(this,
				"Mark unread items", new String [] {"Mark", "No mark"});
		//#ifdef DMIDP20
        m_useTextBox = FeatureMgr.getAddChoiceGroup(this,
				"Text entry items", new String [] {"Text (large) box",
				"Text (line) field"});
		//#endif

		//#ifdef DSMARTPHONE
//@        m_itunesEnabled = FeatureMgr.getAddChoiceGroup(this,
//@				"Choose to use podcast data", new String []
//@				{"Don't show podcast data", "Show Itunes data"});
		//#else
        m_itunesEnabled = FeatureMgr.getAddStringItem(this,
				"Choose to use podcast data",
				"This is only available on the smartphone version.");
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

        m_backlightFlash = FeatureMgr.getAddTextField(this,
				"Flash backlight for seconds after updating.",
                String.valueOf(settings.getBacklightFlashSecs()), 3,
				TextField.NUMERIC);

        m_vibrate = FeatureMgr.getAddTextField(this,
				"Vibrate phone for seconds after updating.",
                String.valueOf(settings.getVibrateSecs()), 3,
				TextField.NUMERIC);

		//#endif

        m_wordCountField = FeatureMgr.getAddTextField(this,
				"Max word count desc abbrev",
                String.valueOf(settings.getMaxWordCountInDesc()), 2,
				TextField.NUMERIC);

		//#endif
        m_useStandardExit = FeatureMgr.getAddChoiceGroup(this,
				"Exit key type", new String [] {"Use standard exit key",
				"Use menu exit key"});

        FeatureMgr.getAddStringItem(this, "Program MIDP version:",
		//#ifdef DMIDP20
				"MIDP-2.0"
		//#else
//@				"MIDP-1.0"
		//#endif
				);

        FeatureMgr.getAddStringItem(this, "Program CLDC version:",
				//#ifdef DCLDCV11
//@				"CLDC-1.1"
				//#else
				"CLDC-1.0"
				//#endif
				);

        FeatureMgr.getAddStringItem(this, "Program JSR 75 available:",
		//#ifdef DJSR75
//@				"true"
		//#else
				"false"
		//#endif
				);
        FeatureMgr.getAddStringItem(this, "Program smartphone:",
		//#ifdef DSMARTPHONE
//@				"true"
		//#else
				"false"
		//#endif
				);
        FeatureMgr.getAddStringItem(this, "Program signed:",
		//#ifdef DSIGNED
//@				"true"
		//#else
				"false"
		//#endif
				);
		Object[] omep = FeatureMgr.getSysProperty("microedition.profiles",
				"N/A", "Unable to get microedition.profiles",
				super.featureMgr.getLoadForm());
		String mep = (String)omep[0];
        FeatureMgr.getAddStringItem(this, "Phone MIDP version:", mep);
		Hashtable sysOptions = new Hashtable(5);
		String omidpv = MiscUtil.replace(MiscUtil.replace(mep, "-", ""),
					".", "").toLowerCase();
		sysOptions.put(omidpv, new Boolean(true));
		String cmidpv = MiscUtil.replace(omidpv, "21","20");
		sysOptions.put(cmidpv, new Boolean(true));
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("Constructor cmidpv=" + cmidpv);}
		//#endif
		Object[] omec = FeatureMgr.getSysProperty("microedition.configuration",
				"N/A", "Unable to get microedition.configuration",
				super.featureMgr.getLoadForm());
		String mec = (String)omec[0];
        FeatureMgr.getAddStringItem(this, "Phone CLDC version:", mec);

		sysOptions.put(MiscUtil.replace(MiscUtil.replace(mec, "-", ""),
					".", "").toLowerCase(), new Boolean(true));
		Object[] ojsr75Avail = super.featureMgr.jsr75Avail();
		boolean hasjsr75 = ((Boolean)ojsr75Avail[0]).booleanValue();
        FeatureMgr.getAddStringItem(this, "Phone JSR 75 available:",
				((Boolean)ojsr75Avail[0]).toString());
		if (ojsr75Avail[1] != null) {
			FeatureMgr.getAddStringItem(this, "Phone JSR 75 exists:",
					((Boolean)ojsr75Avail[1]).toString());
		}
		if (ojsr75Avail[2] != null) {
			FeatureMgr.getAddStringItem(this, "Phone JSR 75 version:",
					(String)ojsr75Avail[2]);
		}
		sysOptions.put("jsr75", new Boolean(hasjsr75));
		//#ifdef DLOGGING
//@		if (m_fineLoggable) {m_logger.fine("Constructor sysOptions=" + sysOptions);}
		//#endif
		Object[] omepl = FeatureMgr.getSysProperty("microedition.platform",
				"N/A", "Unable to get microedition.platform",
				super.featureMgr.getLoadForm());
		String mepl = (String)omepl[0];
        FeatureMgr.getAddStringItem(this, "Phone Microedition platform:", mepl);
		try {
			m_currfnp = csettings.getStringProperty(0, "build-file-root", "") +
				".";
			String[] sbuildf = MiscUtil.split(csettings.getStringProperty(0,
						"sbuild-file-root", ""), ",");
			boolean projectFound = false;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < sbuildf.length; i++) {
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest("Constructor sbuildf[i]=" + i + "," + sbuildf[i]);}
				//#endif
				String sval = ((sbuildf[i].charAt(0) == '-') ? "-_" :
						csettings.getStringProperty(0, sbuildf[i].replace(
								'.', '-'), ""));
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest("Constructor sval=" + sval);}
				//#endif
				int len;
				if ((len = sval.length()) > 0)  {
					String aval = (sval.charAt(len - 1) != '_') ? sval :
						sval.substring(0, len - 1);
					//#ifdef DLOGGING
//@					if (m_finestLoggable) {m_logger.finest("Constructor aval=" + aval);}
					//#endif
					if ((aval.charAt(0) == 'm') || (aval.charAt(0) == 'c') ||
							(aval.charAt(0) == 'j')) {
						Object oval;
						if (((oval = sysOptions.get(aval)) != null) &&
								((Boolean)oval).booleanValue()) {
							//#ifdef DLOGGING
//@							if (m_finestLoggable) {m_logger.finest("Constructor oval=" + oval);}
							//#endif
							if (aval.equals("cldc11")) {
								// For simplicity, if not smartphone version
								// use CLDC 1.0 for all versions.
								//#ifndef DSMARTPHONE
								aval = "cldc10";
								//#endif
							}
							sb.append(aval).append("_");
						}
					} else {
						if (!projectFound) {
							projectFound = sbuildf[i].startsWith("release");
						}
						// This is expanded by filter to "novice" or configured
						// value for it.
						if (aval.equals("novice")) {
							//#ifndef DNOVICE
							aval = "";
							//#endif
						} else if (aval.equals("smartphone")) {
							//#ifndef DSMARTPHONE
							aval = "";
							//#endif
							// intlink is never the preferred version.
						} else if (aval.equals("intlink")) {
							aval = "";
						} else if (aval.equals("signed")) {
							//#ifndef DSIGNED
							aval = "";
							//#endif
						} else if (aval.equals("verisign")) {
							//#ifndef DVERSIGN
							aval = "";
							//#endif
						} else if (aval.equals("thawte")) {
							//#ifndef DTHAWTE
							aval = "";
							//#endif
						} else if (aval.equals("godaddy")) {
							//#ifndef DGODADDY
							aval = "";
							//#endif
						}
						if (projectFound) {
							sb.append(aval);
						} else {
							if (aval.length() > 0) {
								sb.append(aval).append("_");
							}
						}
					}
				}
			}
			//#ifdef DLOGGING
//@			if (m_finestLoggable) {m_logger.finest("Constructor sb=" + sb);}
			//#endif
			m_preffnp = sb.append(".").toString();
			m_preffJadTx = FeatureMgr.getAddStringItem(this, "Preferred jad:",
					m_preffnp + "jad");
			m_preffJarTx = FeatureMgr.getAddStringItem(this, "Preferred jar:",
					m_preffnp + "jar");
			//#ifdef DTEST
//@			m_currJadTx = FeatureMgr.getAddTextField(this, "Current jad:",
//@					m_currfnp + "jad", Math.max(m_currfnp.length() + 3, 526),
//@					TextField.ANY);
			//#else
			FeatureMgr.getAddStringItem(this, "Current jad:", m_currfnp + "jad");
			//#endif
			//#ifdef DTEST
//@			m_currJarTx = FeatureMgr.getAddTextField(this, "Current jar:",
//@					m_currfnp + "jar", Math.max(m_currfnp.length() + 3, 526),
//@					TextField.ANY);
			//#else
			FeatureMgr.getAddStringItem(this, "Current jar:",
					m_currfnp +
					"jar");
			//#endif
			if (m_preffnp.equals(m_currfnp)) {
				m_preffJadTx.setLabel("Preferred jad (current):");
				m_preffJarTx.setLabel("Preferred jar (current):");
			}
		} catch (Throwable e) {
			super.featureMgr.getLoadForm().recordExcForm(
					"Internal error unable to get preferred jad/jar.", e);
		}

		Object[] omel = FeatureMgr.getSysProperty("microedition.locale",
				null, "Unable to get microedition.locale",
				super.featureMgr.getLoadForm());
		if (omel == null) {
			omel = FeatureMgr.getSysProperty("file.encoding",
					"N/A", "Unable to get file.encoding.",
					super.featureMgr.getLoadForm());
		}
		String mel = (String)omel[0];
        FeatureMgr.getAddStringItem(this, "Phone Microedition locale:", mel);
		//#ifdef DLOGGING
//@        m_logLevelField = new TextField("Logging level",
//@                m_logger.getParent().getLevel().getName(), 20, TextField.ANY);
		//#ifdef DMIDP20
//@		m_logLevelField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
//@        super.append( m_logLevelField );
		//#endif
        m_pgmMemUsedItem = FeatureMgr.getAddStringItem(this,
				"Application memory used:", "");
        m_pgmMemAvailItem = FeatureMgr.getAddStringItem(this,
				"Application memory available:", "");
        m_memUsedItem = FeatureMgr.getAddStringItem(this,
				"DB memory used:", "");
        m_memAvailItem = FeatureMgr.getAddStringItem(this,
				"DB memory available:", "");
        m_threadsUsed = FeatureMgr.getAddStringItem(this,
				"Active Threads:", "");
		updateForm();
    }
    
	/* Update form items that change per run. */
	public void updateForm() {
        RssReaderMIDlet midlet = featureMgr.getRssMidlet();
        RssReaderSettings settings = midlet.getSettings();
		//#ifdef DFULLVERS
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
		// end DMIDP20
        boolean useStdExit = settings.getUseStandardExit();
        prevStdExit = useStdExit;
		boolean [] boolExitItems = {useStdExit, !useStdExit};
		m_useStandardExit.setSelectedFlags( boolExitItems );
        boolean itunesEnabled = settings.getItunesEnabled();
		//#ifdef DSMARTPHONE
//@		boolean [] boolItunesEnabled = {!itunesEnabled, itunesEnabled};
//@		m_itunesEnabled.setSelectedFlags( boolItunesEnabled );
		//#endif
		// end DSMARTPHONE
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
		// end DMIDP20
		//#endif
		// end DFULLVERS
		int[] memInfo = null;
		try {
			Settings csettings = settings.getSettingsInstance();
			memInfo = csettings.getSettingMemInfo();
		} catch (Exception e) {
			memInfo = new int[0];
		}

		System.gc();
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		m_pgmMemUsedItem.setText(((totalMem - freeMem)/1024L) + "kb");
		m_pgmMemAvailItem.setText((freeMem/1024L) + "kb");
        if (memInfo.length == 0) {
			m_memUsedItem.setText("0kb");
			m_memAvailItem.setText("0kb");
		} else {
			m_memUsedItem.setText((memInfo[0]/1024L) + "kb");
			m_memAvailItem.setText((memInfo[1]/1024L) + "kb");
		}
		m_threadsUsed.setText(Integer.toString(Thread.activeCount()));
	}

    public void commandAction(Command command, Displayable displayable) {
        RssReaderMIDlet midlet = featureMgr.getRssMidlet();
        if 
			//#ifdef DTEST
//@			((command==m_updateCommand) ||
			//#endif
			(command==m_okCommand)
			//#ifdef DTEST
//@			)
			//#endif
		{
            // Save settings
            RssReaderSettings settings = midlet.getSettings();
			Settings csettings = settings.getSettingsInstance();
			LoadingForm loadForm = LoadingForm.getLoadingForm(
						"Updating settings...", this, null);
			super.featureMgr.setLoadForm(loadForm);
            try {
				//#ifdef DFULLVERS
                int maxCount = Integer.parseInt( m_itemCountField.getString() );
                settings.setMaximumItemCountInFeed( maxCount );
				boolean markUnreadItems = m_markUnreadItems.isSelected(0);
                settings.setMarkUnreadItems( markUnreadItems );
				//#ifdef DMIDP20
				boolean useTextBox = m_useTextBox.isSelected(0);
				settings.setUseTextBox(useTextBox);
				//#endif
				//#ifdef DSMARTPHONE
//@				boolean itunesEnabled = !m_itunesEnabled.isSelected(0);
//@				settings.setItunesEnabled( itunesEnabled );
				//#else
				settings.setItunesEnabled( false );
				//#endif
				//#ifdef DMIDP20
				int fontChoice = m_fontChoice.getSelectedIndex();
				settings.setFontChoice( fontChoice );
				//#ifdef DLOGGING
//@				if (m_fineLoggable) {m_logger.fine("fontChoice=" + fontChoice);}
				//#endif
				int fitPolicy = m_fitPolicy.getSelectedIndex();
				settings.setFitPolicy( fitPolicy );
				//#ifdef DLOGGING
//@				if (m_fineLoggable) {m_logger.fine("fitPolicy=" + fitPolicy);}
				//#endif
				boolean nameNews = !m_nameNews.isSelected(0);
				settings.setBookmarkNameNews( nameNews );

				super.featureMgr.setSettingNbr(m_backlightFlash,
						settings.BACKLIGHT_FLASH_SECS, csettings);

				super.featureMgr.setSettingNbr(m_vibrate,
						settings.VIBRATE_SECS, csettings);

				//#endif

				super.featureMgr.setSettingNbr(m_wordCountField,
						settings.MAX_WORD_COUNT, csettings);
				//#endif
				// end DFULLVERS

				boolean useStdExit = m_useStandardExit.isSelected(0);
				settings.setUseStandardExit(useStdExit);
				if (useStdExit != prevStdExit) {
					midlet.initExit(FeatureMgr.getMainDisp());
				}
				//#ifdef DLOGGING
//@				prevLevel = settings.getLogLevel();
//@				try {
//@					String logLevel =
//@						m_logLevelField.getString().toUpperCase();
//@					if (!logLevel.equals(prevLevel)) {
//@						m_logger.getParent().setLevel(Level.parse(logLevel));
//@						settings.setLogLevel(logLevel);
//@						FeatureMgr.getRssMidlet().initLogVars();
//@					}
//@				} catch (IllegalArgumentException e) {
//@					Alert invalidData = new Alert("Invalid Log Level",
//@									"Invalid Log Level " +
//@									m_logLevelField.getString(),
//@									null,
//@									AlertType.ERROR);
//@					invalidData.setTimeout(Alert.FOREVER);
//@					super.featureMgr.showMe(invalidData);
//@					return;
//@				}
				//#endif
			} catch(Throwable e) {
				/* Internal error.:\n */
				loadForm.recordExcFormFin("Internal error.", e);
            }
            
			//#ifdef DTEST
//@			if (command==m_updateCommand) {
//@				String sjad = m_currJadTx.getString();
//@				m_currfnp = sjad.substring(0, sjad.length() - 3);
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest("commandAction m_currfnp=" + m_currfnp);}
				//#endif
//@				if (m_preffnp.equals(m_currfnp)) {
//@					m_preffJadTx.setLabel("Preferred jad (current):");
//@					m_preffJarTx.setLabel("Preferred jar (current):");
//@				} else {
//@					m_preffJadTx.setLabel("Preferred jad:");
//@					m_preffJarTx.setLabel("Preferred jar:");
//@				}
//@				super.getFeatureMgr().getLoadForm().showMeNotes(this);
//@			} else {
			//#endif
				super.getFeatureMgr().getLoadForm().showMeNotes(
						FeatureMgr.getMainDisp());
			//#ifdef DTEST
//@			}
			//#endif
        }
        
        /** Show about */
		if(command == FeatureMgr.m_aboutCmd) {
			midlet.initializeAboutForm();
		}

		//#ifdef DFULLVERS
        if(command==m_cancelCommand) {
            midlet.showBookmarkList();
        }
		//#else
//@        if(command==FeatureMgr.m_exitCommand) {
//@			LoadingForm loadForm = LoadingForm.getLoadingForm(
//@					"Exiting...", this, null);
//@			super.featureMgr.setLoadForm(loadForm);
//@			super.featureMgr.getRssMidlet().exitApp( true, loadForm );
//@		}
		//#endif
    }

	//#ifdef DINTLINK
//@    /** Run method is used to initialize the form for internet link version. */
//@    public void run() {
		//#ifdef DLOGGING
//@		if (m_finestLoggable) {m_logger.finest("run super.size()=" + super.size());}
		//#endif
//@		try {
//@			Thread.sleep(1L);
//@			Thread.yield();
//@		} catch (Throwable e) {
//@			e.printStackTrace();
//@		}
//@		super.featureMgr.getRssMidlet().initForms();
//@		super.featureMgr.setBackground(false);
//@	}
	//#endif
}
