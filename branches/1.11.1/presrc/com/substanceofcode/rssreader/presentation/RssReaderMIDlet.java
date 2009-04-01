/*
   if setCommandListener again, just change user, restart thread, join
   TODO fix KFileSelectorMgr setCommandListener, deprecated?
   TODO handle OutOfMemoryError
   TODO storeDate
 * RssReaderMIDlet.java
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

// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define compatibility
@DCOMPATDEF@

//#ifdef DCOMPATIBILITY1
//#define DCOMPATIBILITY
//#elifdef DCOMPATIBILITY2
//#define DCOMPATIBILITY
//#elifdef DCOMPATIBILITY3
//#define DCOMPATIBILITY
//#endif

package com.substanceofcode.rssreader.presentation;

import java.lang.SecurityException;
//#ifdef DJSR75
import org.kablog.kgui.KFileSelectorMgr;
//#endif
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
//#ifdef DCOMPATIBILITY1
import com.substanceofcode.rssreader.businessentities.CompatibilityRssFeed1;
import com.substanceofcode.rssreader.businessentities.CompatibilityRssItem1;
//#elifdef DCOMPATIBILITY2
import com.substanceofcode.rssreader.businessentities.CompatibilityRssFeed2;
import com.substanceofcode.rssreader.businessentities.CompatibilityRssItem2;
//#endif
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.rssreader.businesslogic.Controller;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.RssFormatParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.presentation.AllNewsList;
import com.substanceofcode.utils.Settings;
import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.CauseException;
import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.midlet.*;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.rms.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
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
//#ifdef DTESTUI
import com.substanceofcode.testutil.presentation.TestingForm;
import com.substanceofcode.testutil.TestOutput;
//#endif

//#ifdef DJSR238
import javax.microedition.global.ResourceManager;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.presentation.LoggerRptForm;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
//#endif

/**
 * RSS feed reader MIDlet
 *
 * RssReaderMIDlet is an application that can read RSS feeds. User can store
 * multiple RSS feeds as bookmarks into application's record store.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class RssReaderMIDlet extends MIDlet
        implements CommandListener,
        Runnable {
    
    final static public char CFEED_SEPARATOR = (char)4;
    final static public char OLD_FEED_SEPARATOR = '^';
    // Attributes
    private Display     m_display;          // The display for this MIDlet
    private Displayable m_prevDisp;         // The displayable to return to
    private Settings    m_settings;         // The settings
    private RssReaderSettings m_appSettings;// The application settings
    private Hashtable   m_rssFeeds;         // The bookmark URLs
    private RssReaderMIDlet m_midlet;       // The display for this MIDlet
    final static public boolean JSR75_ENABLED =
	          (System.getProperty(
			"microedition.io.file.FileConnection.version") != null);
	//#ifdef DTEST
    private boolean     m_debugOutput = true; // Flag to write to output for test
	//#endif
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_openPage;         // Open the headers
    private boolean     m_saveBookmarks;    // The save bookmarks flag
    private boolean     m_exit;             // The exit application flag
    private boolean     m_saving;           // The saving settings flag
    private boolean     m_stored;           // The data stored flag
    private boolean     m_about;            // The about flag
    private boolean     m_getModPage;       // The noticy flag for modified HTTP
    private boolean     m_getSettingsForm;  // Flag to get settings form
    private boolean     m_getAddBMForm;     // Flag to get add bookmark form
    private boolean     m_getEditBMForm;    // Flag to get edit bookmark form
    private boolean     m_refreshAllFeeds;  // The notify flag for all feeds
    private boolean     m_refreshUpdFeeds;  // The notify flag for updated feeds
    private boolean     m_getImportForm;    // The noticy flag for going to Import Feed list
    private boolean     m_getExportForm;    // The noticy flag for going to Export Feed list
    private boolean     m_getFile;          // The noticy flag for getting find files form
    private boolean     m_selectDir;          // The noticy flag for selecting directories
    private boolean     m_runNews = false;  // Run AllNewsList form.
	//#ifdef DTEST
    // Get import form using URL from current bookmark
    private boolean     m_getTestImportForm = false; // Get import form 
	//#endif
	//#ifdef DTESTUI
	boolean m_headerNext = false; // Flag to control opening the next header
	boolean m_itemNext = false; // Flag to control opening the next item
	//#endif
	//#ifdef DTESTUI
	private int         m_headerIndex = -1; // Index in headers to auto test
    // Index in bookmarks to auto test by opening in edit
	// This counts up until the bookmark size is reached.
    private int         m_bookmarkIndex = -1;
    private int         m_bookmarkLastIndex = -1; // Last place when import current was selected
	//#endif
	// Tells us if this is the first time program was used.  This is
	// done by seeing if max item count is set.  We also set it after
	// showing the about.
    private boolean     m_firstTime = false;
    private boolean     m_novice = false;
    private boolean     m_itunesEnabled = false;
    private boolean     m_parseBackground = false;
	//#ifdef DLOGGING
    private boolean fineLoggable;
    private boolean finestLoggable;
	//#endif
	// This is a mark (icon) next to unread items (except on unread items
	// screen).  Given that many screens are small, it is optional as 
	// we don't want to reduce space for text.
    private Image           m_unreadImage = null;
    
    // Currently selected bookmark
    private int             m_curBookmark;  // The currently selected item
    private int             m_backGrBookmark;  // The background selected item
    private RssFeedParser   m_curRssParser; // The currently selected RSS
    private RssFeedParser   m_backGrRssParser = null; // The currently selected RSS in background
    
    // GUI items
    private FeatureList  m_bookmarkList;     // The bookmark list
	//#ifdef DTESTUI
    private HeaderList  m_headerTestList;       // The header list
    private AllNewsList m_allNewsTestList; // The test header list for unread items
	//#endif
    private List        m_itemRrnForm;      // The list to return from for item
    private ItemForm    m_itemForm;         // The item form
    private LoadingForm m_loadForm;         // The "loading..." form
    private TextField   m_fileURL;          // The file URL field from a form
    private Form        m_fileRtnForm;      // The form to return to for file
	//#ifdef DTESTUI
    private TestingForm m_testingForm;    // The testing form
	//#endif
    
    // Commands
	//#ifdef DTESTUI
	private Command     m_testRssCmd;       // Tet UI rss headers command
	private Command     m_testBMCmd;        // Tet UI bookmarks list command
	private Command     m_testRtnCmd;       // Tet UI return to prev command
	//#endif
    private Command     m_exitCommand = null;// The exit command
    private Command     m_saveCommand;      // The save without exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_readUnreadItems;  // The read unread items command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
    private Command     m_backCommand;      // The back to header list command
    private Command     m_importFeedListCmd;// The import feed list command
    private Command     m_exportFeedListCmd;// The export feed list command
	//#ifdef DTEST
    private Command     m_importCurrFeedListCmd;// The import feed list command and default current seleected feed
	//#endif
	//#ifdef DTESTUI
    private Command     m_testEncCmd;     // The test encoding
	//#endif
	//#ifdef DLOGGING
    private Command     m_debugCmd; // The back to bookmark list command
	                                      // from debug form
    private Command     m_backFrDebugCmd; // The back to bookmark list command
    private Command     m_clearDebugCmd; // The back to bookmark list command
	                                      // from debug form
	//#endif
    private Command     m_settingsCmd;      // The show settings command
    private Command     m_aboutCmd;      // The show About
    private Command     m_updateAllCmd;     // The update all command
    private Command     m_updateAllModCmd;  // The update all modified command
    
    // The controller of the application
    private Controller m_controller;
    private int citemLnkNbr = -1;
    private int citemEnclNbr = -1;
    private RssItunesItem citem = null;
	//#ifdef DLOGGING
    private LoggerRptForm m_debug;
    private Logger logger;
	//#endif
    
    public RssReaderMIDlet()
	throws SecurityException {
        m_display = Display.getDisplay(this);
		m_midlet = this;
        
		//#ifdef DTESTUI
		TestOutput.init(System.out, "UTF-8");
		//#endif

		//#ifdef DLOGGING
		initializeLoadingForm("Loading items...", null);
		try {
			LogManager logManager = LogManager.getLogManager();
			logManager.readConfiguration(this);
			logger = Logger.getLogger("RssReaderMIDlet");
			for (Enumeration eHandlers = logger.getParent().getHandlers().elements();
					eHandlers.hasMoreElements();) {
				Object ohandler = eHandlers.nextElement();
				if (ohandler instanceof FormHandler) {
					Form oform = (Form)((FormHandler)ohandler).getView();
					logger.finest("form=" + oform);
				}
			}
			logger = Logger.getLogger("RssReaderMIDlet");
			logger.info("RssReaderMIDlet started.");
			logger.info("RssReaderMIDlet has form handler=" + (m_debug != null));
			m_debug = new LoggerRptForm(logManager, this,
						this, "net.sf.jlogmicro.util.logging.FormHandler");
		} catch (Throwable t) {
			m_loadForm.appendMsg("Error initiating logging " +
					t.getClass().getName() + "," + t.getMessage());
			String [] msgs = LogManager.getLogManager().getStartMsgs();
			m_loadForm.addExc("msgs.length" + msgs.length, t);
			for (int ic = 0; ic < msgs.length; ic++) {
				m_loadForm.appendMsg(msgs[ic]);
			}
			System.out.println("Error initiating logging" + t);
			t.printStackTrace();
			return;
		}
		//#endif

		try {

			/** Initialize controller */
			m_controller = new Controller( this );
			
			m_appSettings = RssReaderSettings.getInstance(this);
			m_itunesEnabled = m_appSettings.getItunesEnabled();

			/** Initialize commands */
			//#ifdef DTESTUI
			m_testRssCmd        = new Command("Test headers/items", Command.SCREEN, 9);
			m_testBMCmd         = new Command("Test bookmarks shown", Command.SCREEN, 9);
			m_testRtnCmd        = new Command("Test go back to last", Command.SCREEN, 10);
			//#endif
			m_backCommand       = new Command("Back", Command.BACK, 1);
			initExit();
			m_saveCommand       = new Command("Save without exit", Command.SCREEN, 10);
			m_addNewBookmark    = new Command("Add new feed", Command.SCREEN, 2);
			m_openBookmark      = new Command("Open feed", Command.SCREEN, 1);
			m_readUnreadItems   = new Command("River of news", Command.SCREEN, 3);
			m_editBookmark      = new Command("Edit feed", Command.SCREEN, 4);
			m_delBookmark       = new Command("Delete feed", Command.SCREEN, 5);
			m_importFeedListCmd = new Command("Import feeds", Command.SCREEN, 6);
			m_exportFeedListCmd = new Command("Export feeds", Command.SCREEN, 7);
			//#ifdef DTEST
			m_importCurrFeedListCmd = new Command("Import current feeds", Command.SCREEN, 8);
			//#endif
			m_settingsCmd       = new Command("Settings", Command.SCREEN, 11);
			m_aboutCmd          = new Command("About", Command.SCREEN, 12);
			m_updateAllCmd      = new Command("Update all", Command.SCREEN, 9);
			m_updateAllModCmd   = new Command("Update modified all",
											  Command.SCREEN, 10);
			//#ifdef DTESTUI
			m_testEncCmd        = new Command("Testing Form", Command.SCREEN, 4);
			//#endif

		//#ifdef DLOGGING
			m_debugCmd          = new Command("Debug Log", Command.SCREEN, 4);
			m_clearDebugCmd     = new Command("Clear", Command.SCREEN, 1);
			m_backFrDebugCmd    = new Command("Back", Command.BACK, 2);
		//#endif
			
			m_getPage = false;
			m_exit = false;
			m_stored = false;
			m_saving = false;
			m_about = false;
			m_saveBookmarks = false;
			m_openPage = false;
			m_getModPage = false;
			m_getSettingsForm = false;
			m_getAddBMForm = false;
			m_getEditBMForm = false;
			m_refreshAllFeeds = false;
			m_refreshUpdFeeds = false;
			m_getImportForm = false;
			m_getExportForm = false;
			m_getFile = false;
			m_selectDir = false;
			m_curBookmark = -1;
			CauseException ce = null;
			
			// To get proper initialization, need to 
			try {
				m_settings = Settings.getInstance(this);
				m_firstTime = !m_settings.isInitialized();
			} catch(Exception e) {
				ce = new CauseException("Error while getting settings/stored bookmarks", e);
				//#ifdef DLOGGING
				logger.severe(e.getMessage(), ce);
				//#endif
				System.err.println(ce.getMessage());
				e.printStackTrace();
			}

			//#ifdef DLOGGING
			if (m_appSettings.getLogLevel().length() == 0) {
				m_appSettings.setLogLevel(
						logger.getParent().getLevel().getName());
			} else {
				logger.getParent().setLevel(
				Level.parse(m_appSettings.getLogLevel()));
			}
			fineLoggable = logger.isLoggable(Level.FINE);
			logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
			finestLoggable = logger.isLoggable(Level.FINEST);
			logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
			//#endif

			initializeLoadingForm("Loading items...", null);
			if (m_appSettings.getMarkUnreadItems()) {
				try {
					try {
						// createImage("/icons/unread.png") does not always work
						// with the emulator.  so, I do an alternate which is
						// effectively the same thing.
						m_unreadImage = Image.createImage("/icons/unread.png");
					} catch(IOException e) {
						//#ifdef DMIDP20
						InputStream is =
								this.getClass().getResourceAsStream("/icons/unread.png");
						m_unreadImage = Image.createImage(is);
						is.close();
						//#endif
					}
				} catch(Exception e) {
					System.err.println("Error while getting mark image: " + e.toString());
				}
			}
			
			if (ce != null) {
				m_loadForm.addExc(ce.getMessage(), ce);
			}

		}catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("RssReaderMIDlet constructor ", t);
			//#endif
			/** Error while executing constructor */
			System.out.println("RssReaderMIDlet constructor " + t.getMessage());
			t.printStackTrace();
			if (m_loadForm == null) {
				initializeLoadingForm("Loading items...", null);
			}
            m_loadForm.addExc("Internal error starting applicaiton.", t);
		}
    }
    
	/* Create exit command based on if it's a standard exit. */
	final public void initExit() {
		boolean prevExit = (m_exitCommand != null);
		if (prevExit) {
			m_bookmarkList.removeCommand(m_exitCommand);
		}
		m_exitCommand       = new Command("Exit",
				(m_appSettings.getUseStandardExit() ? Command.EXIT
				 : Command.SCREEN), 14);
		if (prevExit) {
			m_bookmarkList.addPromptCommand(m_exitCommand,
					"Are you sure you want to exit?");
		}
	}

	/* Initialize the forms that are not dynamic. */
	final private void initForms() {
		try {
			// Get here so that bookmarklist knows to not use some commands
			m_novice = m_appSettings.getNovice();
			/** Initialize GUI items */
			initializeBookmarkList();
			//initializeLoadingForm();
			//#ifdef DLOGGING
			if (m_debug != null) {
				initializeDebugForm();
			}
			//#endif
			//#ifdef DTEST
			System.gc();
			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			//#ifdef DTESTUI
			m_testingForm = new TestingForm(this);
			//#endif
			//#ifdef DTEST
			System.gc();
			System.out.println("TestingForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("m_novice=" + m_novice);}
			//#endif
			if( m_firstTime ) {
				try {
					// Set Max item count to default so that it is initialized.
					m_appSettings.setMaximumItemCountInFeed(
							m_appSettings.getMaximumItemCountInFeed());
					saveBkMrkSettings("Initializing database...",
							System.currentTimeMillis(), false);
					// If novice, show about later.
					if (!m_novice) {
						m_firstTime = false;
						Alert m_about = getAbout();
						setCurrentNotes( m_about, m_bookmarkList );
					}
				} catch(Exception e) {
					System.err.println("Error while getting/updating settings: " + e.toString());
					m_loadForm.replaceRef(null, m_bookmarkList);
					m_loadForm.recordExcForm("Internal error.  Unable to initialize forms",
							e);
				}
			} else {
				// If not novice, show bookmark.  If we are novice,
				// we only show novice if we have already loaded the
				// novice bookmarks.
				if (!m_novice) {
					setCurrentNotes( m_bookmarkList );
				}
			}

		}catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("initForms ", t);
			//#endif
			/** Error while initializing forms */
			System.out.println("initForms " + t.getMessage());
			t.printStackTrace();
			m_loadForm.replaceRef(null, m_bookmarkList);
			m_loadForm.recordExcForm("Internal error.  Unable to initialize forms",
					t);
		}
		//#ifdef DTEST
		System.gc();
		System.out.println("Initial memory size=" + (Runtime.getRuntime().freeMemory() / 1024L) + "kb");
		//#endif
    }
    
    /** Get application settings */
    final public RssReaderSettings getSettings() {
        return m_appSettings;
    }
    
    /** Show bookmark list */
    final public void showBookmarkList() {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("before m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		m_itunesEnabled = m_appSettings.getItunesEnabled();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("after m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		setCurrent( m_bookmarkList );
    }
    
    /** Load bookmarks from record store */
    final private void initializeBookmarkList() {
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		Gauge gauge = new Gauge("Initializing bookmarks...",
				false, m_settings.MAX_REGIONS +
				((m_novice && m_firstTime) ?  1 : 0), 0);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("m_settings.MAX_REGIONS,gauge.getMaxValue()=" + m_settings.MAX_REGIONS + "," + gauge.getMaxValue());}
		//#endif
		int pl = m_loadForm.append(gauge);
        try {
            m_bookmarkList = new FeatureList(this, "Bookmarks", List.IMPLICIT);
			//#ifdef DMIDP20
			// If font is wrong, it can cause an exception for some
			// devices.  This leaves some of the data not loaded.
			// So, we'll update this later
            m_bookmarkList.setFont(null);
			//#endif
			if (!m_novice) {
				m_bookmarkList.addCommand( m_addNewBookmark );
			}
            m_bookmarkList.addCommand( m_openBookmark );
            m_bookmarkList.addCommand( m_readUnreadItems );
			if (!m_novice) {
				m_bookmarkList.addCommand( m_editBookmark );
				m_bookmarkList.addPromptCommand( m_delBookmark,
											 "Are you sure you want to delete?" );
			}
			if (!m_novice) {
				m_bookmarkList.addCommand( m_importFeedListCmd );
				//#ifdef DITUNES
				m_bookmarkList.addCommand( m_exportFeedListCmd );
				//#endif
				//#ifdef DTEST
				m_bookmarkList.addCommand( m_importCurrFeedListCmd );
				//#endif
			}
            m_bookmarkList.addCommand( m_updateAllCmd );
            m_bookmarkList.addCommand( m_updateAllModCmd );
            m_bookmarkList.addCommand( m_saveCommand );
            m_bookmarkList.addCommand( m_settingsCmd );
            m_bookmarkList.addPromptCommand( m_exitCommand,
					                         "Are you sure you want to exit?" );
            m_bookmarkList.addCommand( m_aboutCmd );
			//#ifdef DTESTUI
            m_bookmarkList.addCommand( m_testBMCmd );
            m_bookmarkList.addCommand( m_testRtnCmd );
			//#endif
			//#ifdef DTESTUI
			m_bookmarkList.addCommand( m_testEncCmd );
			//#endif
	//#ifdef DLOGGING
			if (m_debug != null) {
				m_bookmarkList.addCommand( m_debugCmd );
			}
	//#endif
            m_bookmarkList.setCommandListener( this, true );
			//#ifdef DTEST
			System.gc();
			System.out.println("empty bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
            
            m_rssFeeds = new Hashtable();
			for (int ic = 1; ic < m_settings.MAX_REGIONS; ic++) {
				boolean stop = false;
				final String vers = m_settings.getStringProperty(ic,
						m_settings.SETTINGS_NAME, "");
				final boolean firstSettings =
					 vers.equals(m_settings.FIRST_SETTINGS_VERS);
				final boolean itunesCapable = ((vers.length() > 0) &&
					 (vers.compareTo(m_settings.ITUNES_CAPABLE_VERS) >= 0));
				final boolean encodingSettings = ((vers.length() > 0) &&
					 (vers.compareTo(m_settings.ENCODING_VERS) >= 0));
				final boolean modifiedSettings = vers.equals(
						m_settings.MODIFIED_VERS);
				m_settings.getBooleanProperty(m_settings.ITEMS_ENCODED,
							true);
				/* FUTURE
				final long storeDate = m_settings.getLongProperty(
						m_settings.STORE_DATE, 0L);
					*/
				final char feedSeparator =
					encodingSettings ? CFEED_SEPARATOR : OLD_FEED_SEPARATOR;
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("Settings region,vers,firstSettings,itunesCapable,encodingSettings,modifiedSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + encodingSettings + "," + modifiedSettings);}
				//#endif
				//#ifdef DTEST
				if (m_debugOutput) System.out.println("Settings region,vers,firstSettings,itunesCapable,encodingSettings,modifiedSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + encodingSettings + "," + modifiedSettings);
				//#endif
				String bms = m_settings.getStringProperty(ic, "bookmarks", "");
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("bms.length()=" + bms.length());}
				//#endif
				// Save memory by setting bookmarks to "" now that
				// we will convert them to objects.
				m_settings.setStringProperty("bookmarks", "");
				
				if(bms.length()>0) {
					do{
						
						String part = "";
						int pos = bms.indexOf(feedSeparator);
						if(pos > 0) {
							part = bms.substring(0, pos);
						}
						bms = bms.substring(pos+1);
						if(part.length()>0) {
							//#ifdef DCOMPATIBILITY1
							RssFeed bm1 = new CompatibilityRssFeed1( part );
							RssItunesFeed bm = new RssItunesFeed( bm1 );
							//#elifdef DCOMPATIBILITY2
							RssFeed bm2 = new CompatibilityRssFeed2( part );
							RssItunesFeed bm = new RssItunesFeed( bm2 );
							//#elifdef DCOMPATIBILITY3
							RssFeed bm3 = new CompatibilityRssFeed3( part );
							RssItunesFeed bm = new RssItunesFeed( bm3 );
							//#else
							RssItunesFeed bm;
							if (itunesCapable) {
								bm = RssItunesFeed.deserialize(modifiedSettings,
										true, part );
							} else {
								bm = new RssItunesFeed(new RssFeed(
											firstSettings, true, part ));
							}

							//#endif
							if(bm.getName().length()>0){
								m_bookmarkList.append(bm.getName(),null);
								m_rssFeeds.put(bm.getName(), bm);
							}
						}
						if( part.length()==0)
							stop = true;
					}while(!stop);
				}
				gauge.setValue(ic);
            }
			pl = -1;
			gauge.setValue(m_settings.MAX_REGIONS);
			// Reset internal region to 0.
			m_settings.getStringProperty(0, "bookmarks", "");
			//#ifdef DMIDP20
			if (m_appSettings.getFontChoice() !=
					RssReaderSettings.DEFAULT_FONT_CHOICE) {
				final int len = m_bookmarkList.size();
				m_bookmarkList.initFont(this);
				final Font font = m_bookmarkList.getFont();
				for (int ic = 0; ic < len; ic++) {
					m_bookmarkList.setFont(ic, font);
				}
			}
			//#endif
			//#ifdef DTEST
			System.gc();
			System.out.println("full bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
        } catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("Error while initializing bookmark list: ", e);
			//#endif
            System.err.println("Error while initializing bookmark list: " + e.toString());
        } catch(OutOfMemoryError e) {
			//#ifdef DLOGGING
			logger.severe("Error while initializing bookmark list: ", e);
			//#endif
            System.err.println("Error while initializing bookmark list: " + e.toString());
            final Alert memoryAlert = new Alert(
                    "Out of memory", 
                    "Loading bookmarks without all news items.",
                    null,
                    AlertType.WARNING);
			memoryAlert.setTimeout(Alert.FOREVER);
            setCurrent( memoryAlert, m_loadForm );
		}catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("Error while initializing bookmark list: ", t);
			//#endif
			/** Error while parsing RSS feed */
			System.out.println("Error while initializing bookmark list: " + t.getMessage());
		} finally {
			if (pl >= 0) {
				m_loadForm.delete(pl);
			}
		}
    }
    
    /** Show loading form */
    final public void showLoadingForm() {
        setCurrent( m_loadForm );
    }
    
    /** Initialize loading form */
    final public void initializeLoadingForm(final String desc,
									   Displayable disp) {
		m_loadForm = new LoadingForm("Loading", disp);
		m_loadForm.appendMsg( desc + "\n" );
		m_loadForm.setCommandListener( m_loadForm, false );
		setCurrent( m_loadForm );
    }

	//#ifdef DLOGGING
    final public void initializeDebugForm() {
        m_debug.addCommand( m_backFrDebugCmd );
        m_debug.addCommand( m_clearDebugCmd );
        m_debug.setCommandListener(this);
	}
	//#endif

    /** Run method is used to get RSS feed with HttpConnection, etc */
    public void run(){
		try {

			//#ifdef DTESTUI
			// If there are headers, and the header index is >= 0,
			// open the header so that it's items can be listed
			// with test UI classes.
			// Need to change the selection to match the m_headerIndex.
			if (m_headerNext && (m_headerIndex >= 0) &&
					(m_headerTestList != null) &&
				(m_headerIndex < m_headerTestList.size()) &&
				(m_display.getCurrent() == m_headerTestList)) {
				m_headerNext = false;
				if (m_headerTestList.getSelectedIndex() >= 0) {
					m_headerTestList.setSelectedIndex(
							m_headerTestList.getSelectedIndex(), false);
				}
				m_headerTestList.setSelectedIndex(m_headerIndex, true);
				m_headerTestList.commandAction(List.SELECT_COMMAND,
						m_headerTestList);
			}
			// After intializing the form (which was already logged by
			// testui classes), simulate the back command
			if (m_itemNext && (m_headerIndex >= 0) &&
					(m_headerTestList != null) &&
				(m_headerIndex < m_headerTestList.size()) &&
				(m_display.getCurrent() == m_itemForm )) {
				m_itemNext = false;
				m_itemForm.commandAction( m_backCommand, m_itemForm );
				m_headerIndex++;
				if (m_headerIndex >= m_headerTestList.size()) {
					System.out.println("Test UI Test Rss items last");
					m_headerIndex = -1;
				}
			}
			//#endif

			/* Handle going to settings form. */
			if( m_getSettingsForm ) {
				m_getSettingsForm = false;
				initializeLoadingForm("Loading settings...", m_bookmarkList);
				try{
					//#ifdef DTEST
					System.gc();
					long beginMem = Runtime.getRuntime().freeMemory();
					//#endif
					final SettingsForm settingsForm = new SettingsForm(this);
					settingsForm.setCommandListener( settingsForm, false );
        
					settingsForm.updateForm();
					//#ifdef DTEST
					System.gc();
					System.out.println("SettingsForm size=" +
							(beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( settingsForm );
				} catch(OutOfMemoryError t) {
					m_loadForm.recordExcForm("\nOut Of Memory Error " +
							"loading settings form", t);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("\nInternal error loading settings " +
							"form", t);
				}
			}

			/* Handle going to bookmark form. */
			if( m_getAddBMForm || m_getEditBMForm ) {
				try{
					if( m_getAddBMForm ) {
						initializeLoadingForm("Loading add bookmark...",
								m_bookmarkList);
					} else {
						initializeLoadingForm("Loading edit bookmark...",
								m_bookmarkList);
					}
					//#ifdef DTEST
					System.gc();
					long beginMem = Runtime.getRuntime().freeMemory();
					//#endif
					BMForm bmForm = null;
					if (m_getEditBMForm) {
						final RssItunesFeed bm = (RssItunesFeed)m_rssFeeds.get(
								m_bookmarkList.getString(m_curBookmark));
						bmForm = new BMForm(this, m_rssFeeds, m_appSettings,
								m_bookmarkList, m_loadForm, bm);
					} else {
						bmForm = new BMForm(this, m_rssFeeds, m_appSettings,
								m_bookmarkList, m_loadForm);
					}
					bmForm.setCommandListener( bmForm, false );
					//#ifdef DTEST
					System.gc();
					System.out.println("BMForm size=" +
							(beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( bmForm );
				} catch(OutOfMemoryError t) {
					m_loadForm.recordExcForm("\nOut Of Memory Error loading " +
							"bookmark form", t);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("\nInternal error loading bookmark " +
							"form", t);
				} finally {
					m_getAddBMForm = false;
					m_getEditBMForm = false;
				}
			}

			if( m_refreshAllFeeds || m_refreshUpdFeeds ) {
				initializeLoadingForm("Updating all " +
						(m_refreshUpdFeeds ? "modified " : "") +
						"feeds...", m_bookmarkList);
				try{
					boolean errFound = false;
					final int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
					Enumeration feedEnum = m_rssFeeds.elements();
					while(feedEnum.hasMoreElements()) {
						RssItunesFeed feed = (RssItunesFeed)feedEnum.nextElement();
						try{
							m_loadForm.appendMsg(feed.getName() + "...");
							RssFeedParser parser = new RssFeedParser( feed );
							parser.parseRssFeed( m_refreshUpdFeeds,
									maxItemCount);
							m_loadForm.appendMsg("ok\n");
						} catch(Exception ex) {
							CauseException ce = new CauseException(
									"Error parsing feed " + feed.getName(),
									ex);
							//#ifdef DLOGGING
							logger.severe(ce.getMessage(), ex);
							//#endif
							m_loadForm.addExc("Error\n", ce);
							System.out.println(ce.getMessage());
							errFound = true;
						}
					}
					if (errFound) {
						m_loadForm.setLoadingFinished(
								"Finished with one or more exceptions " +
								"or errors.",
								"Updating finished with one or more " +
								"exceptions or errors..");
						setCurrent( m_loadForm );
					} else {
						m_loadForm.setLoadingFinished("Updating finished",
								"Updating finished use back to return.");
						showBookmarkList();
					}
				} catch(Exception ex) {
					m_loadForm.recordExcForm("Error parsing feeds\n", ex);
				} catch(OutOfMemoryError ex) {
					m_loadForm.recordExcForm("Out Of Memory Error parsing feeds \n",
							ex);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("Internal error parsing feeds from:\n",
							t);
				} finally {
					m_refreshAllFeeds = false;
					m_refreshUpdFeeds = false;
				}
			}

			// Go to import feed form
			if( m_getImportForm || m_getExportForm ) {
				try {
					initializeLoadingForm("Loading " +
							(m_getImportForm ? "import" : "export") +
							" form...",
							m_bookmarkList);
					//#ifdef DTEST
					System.gc();
					long beginMem = Runtime.getRuntime().freeMemory();
					//#endif
					ImportFeedsForm importFeedsForm;
					//#ifdef DTEST
					if (m_getTestImportForm) {
						RssItunesFeed bm = (RssItunesFeed)m_rssFeeds.get(
								m_bookmarkList.getString(m_curBookmark));
						importFeedsForm = new ImportFeedsForm(this,
								m_bookmarkList, m_getImportForm, m_rssFeeds,
								m_appSettings, m_loadForm, bm.getUrl());
					} else
					//#endif
					{
						importFeedsForm = new ImportFeedsForm(this,
								m_bookmarkList, m_getImportForm, m_rssFeeds,
								m_appSettings,
								m_loadForm, m_appSettings.getImportUrl());
					}
					importFeedsForm.setCommandListener(importFeedsForm, true);
					//#ifdef DTEST
					System.gc();
					System.out.println("ImportForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( importFeedsForm );
				} catch(Exception ex) {
					m_loadForm.recordExcForm("Error parsing feeds from:\n",
							ex);
				} catch(OutOfMemoryError ex) {
					m_loadForm.recordExcForm("Out Of Memory Error parsing feeds \n",
							ex);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("Internal error parsing feeds from:\n",
							t);
				} finally {
					m_getImportForm = false;
					m_getExportForm = false;
					//#ifdef DTEST
					m_getTestImportForm = false;
					//#endif
				}
			}

			if (m_parseBackground) {
				procHeader(true, false);
			}

			//#ifdef DTESTUI
			if ((m_bookmarkIndex < m_bookmarkList.size()) &&
				(m_bookmarkIndex >= 0)) {
				if (m_bookmarkList.getSelectedIndex() >= 0) {
					m_bookmarkList.setSelectedIndex(
							m_bookmarkList.getSelectedIndex(), false);
				}
				m_bookmarkList.setSelectedIndex(m_bookmarkIndex, true);
				commandAction(m_editBookmark, m_bookmarkList);
				m_bookmarkIndex++;
				if (m_bookmarkIndex >= m_bookmarkList.size()) {
					m_bookmarkIndex = -1;
					System.out.println("Test UI Test Rss feeds last");
				}
			}

			//#endif


			/* Sort the read or unread items. */
			if ( m_runNews ) {
				try {
					initializeLoadingForm("Sorting items...",
							m_bookmarkList);
					AllNewsList allNewsList = new AllNewsList(this,
							AllNewsList.TITLE, List.IMPLICIT, 0,
						m_bookmarkList.size(), m_bookmarkList, m_rssFeeds,
								m_unreadImage, m_loadForm, 3);
					// Need to do this before the tread starts to avoid
					// race conditions
					allNewsList.sortUnreadItems( true,
							m_bookmarkList, m_rssFeeds );
					allNewsList.setCommandListener(allNewsList, true);
					//#ifdef DTESTUI
					m_allNewsTestList = allNewsList;
					//#endif
					setCurrentNotes( allNewsList );
				}catch(OutOfMemoryError t) {
					m_loadForm.recordExcForm("\nOut Of Memory Error sorting items", t);
				}catch(Throwable t) {
					m_loadForm.recordExcForm("\nInternal error sorting items", t);
				} finally {
					m_runNews = false;
				}
			}

			if ( m_about ) {
				m_about = false;
				final Alert aboutAlert = getAbout();
				// Because of problems with alerts on T637, need to
				// show a form before we show the alert, or it never
				// appears.
				initializeLoadingForm(aboutAlert.getString(),
						m_bookmarkList);
				setCurrent( aboutAlert, m_bookmarkList );
			}

			synchronized(this) {
				if ( m_exit || m_saveBookmarks ) {
					exitApp();
				}
			}

		} catch (Throwable t) {
			try {
				if (m_loadForm == null) {
					synchronized(this) {
						if (m_loadForm == null) {
							initializeLoadingForm("Processing...",
									m_bookmarkList);
						}
					}
				}
				CauseException ce = new CauseException(
						"\nInternal error while processing", t);
				//#ifdef DLOGGING
				logger.severe(ce.getMessage(), t);
				//#endif
				/** Error while parsing RSS feed */
				System.out.println("Throwable Error: " + t.getMessage());
				t.printStackTrace();
				m_loadForm.addExc(ce.getMessage(), ce);
				setCurrent( m_loadForm );
			} catch (Throwable e) {
				t.printStackTrace();
				final Alert internalAlert = new Alert(
						"Internal error", 
						"Internal error while processing",
						null,
						AlertType.WARNING);
				internalAlert.setTimeout(Alert.FOREVER);
				setCurrent( internalAlert );
			}
		}
    }
	
	// Open existing bookmark and show headers (items).
	final public void procPage(boolean cbackground) {

		Displayable cprevDisp = null;
		boolean     copenPage = false;
		boolean     cgetPage = false;
		boolean     cgetModPage = false;
		RssFeedParser ccurRssParser = null;
		synchronized(this) {
			cprevDisp = m_prevDisp;
			copenPage = m_openPage;
			cgetPage = m_getPage;
			cgetModPage = m_getModPage;
			ccurRssParser = m_curRssParser;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procPage cbackground,copenPage,cgetPage,cgetModPage,cprevDisp,ccurRssParser=" + cbackground + "," + copenPage + "," + cgetPage + "," + cgetModPage + "," + cprevDisp + "," + ccurRssParser);}
		//#endif
		// Open existing bookmark and show headers (items).
		if( copenPage || cgetPage || cgetModPage ) {
			try {
				if( copenPage ) {
					initializeLoadingForm("Loading feed...", m_bookmarkList);
				} else {
					initializeLoadingForm("updating feed...", cprevDisp);
				}
				final RssItunesFeed feed = ccurRssParser.getRssFeed();
				if(feed.getUrl().length() == 0) {
					m_loadForm.recordExcForm("Unable to open feed.  No URL.",
							new Exception(
							"Feed has no URL cannot load."));
					return;
				}
				if (!copenPage) {
					/** Get RSS feed */
					final int maxItemCount =
						m_appSettings.getMaximumItemCountInFeed();
					if (cbackground) {
						synchronized(this) {
							m_backGrRssParser = new RssFeedParser(this, feed,
									cgetModPage, maxItemCount);
							m_parseBackground = true;
							m_backGrBookmark = m_curBookmark;
							m_loadForm.addPromptCommand(m_backCommand,
									"Are you sure that you want to go back? " +
									"Reading the feed has not finished.");
							return;
						}
					} else {
						ccurRssParser.parseRssFeed( cgetModPage,
								maxItemCount );
					}
				}
				procHeader(cbackground, copenPage);
			}catch(Exception e) {
				m_loadForm.recordExcForm(
						"\nError " + (copenPage ? "loading" :
							"parsing") + " feed on:\n" +
						ccurRssParser.getRssFeed().getUrl(), e);
			}catch(OutOfMemoryError e) {
				m_loadForm.recordExcForm(
						"\nOut of memory " + (copenPage ? "loading" :
							"parsing") + " feed on:\n" +
						ccurRssParser.getRssFeed().getUrl(), e);
			}catch(Throwable t) {
				m_loadForm.recordExcForm(
						"\nInternal error " + (copenPage ? "loading" :
							"parsing") + " feed on:\n" +
						ccurRssParser.getRssFeed().getUrl(), t);
			} finally {
				synchronized(this) {
					m_getPage = false;
					m_openPage = false;
					m_getModPage = false;
				}
			}
		}
	}

	// Open existing bookmark and show headers (items).
	final public void procHeader(boolean cbackground, boolean copenPage) {

		Displayable cprevDisp = null;
		boolean cparseBackground = false;
		RssFeedParser ccurRssParser = null;
		RssFeedParser cbackGrRssParser = null;
		int ccurBookmark = -1;
		int cbackGrBookmark = -1;
		synchronized(this) {
			cprevDisp = m_prevDisp;
			cbackGrRssParser = m_backGrRssParser;
			ccurRssParser = m_curRssParser;
			cparseBackground = m_parseBackground;
			cbackGrBookmark = m_backGrBookmark;
			ccurBookmark =    m_curBookmark;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procHeader cbackground,copenPage,m_getPage,m_getModPage,cparseBackground,ccurRssParser,cbackGrRssParser=" + cbackground + "," + copenPage + "," + m_getPage + "," + m_getModPage + "," + cparseBackground + "," + ccurRssParser + "," + cbackGrRssParser);}
		//#endif
		// Open existing bookmark and show headers (items).
		boolean rssBackground = cbackground && cparseBackground &&
				(cbackGrRssParser != null) &&
				(cbackGrRssParser != ccurRssParser) && (cbackGrBookmark >= 0);
		if( (cprevDisp != null) && (!cbackground || (rssBackground &&
				cbackGrRssParser.isReady()) )) {
			try {

				if(rssBackground) {
					if(cbackGrRssParser.isSuccessfull()) {
						m_rssFeeds.put(cbackGrRssParser.getRssFeed().getName(),
								cbackGrRssParser.getRssFeed());
					} else {
						throw cbackGrRssParser.getEx();
					}
				}
				HeaderList hdrList = null;
				if(rssBackground || !(cprevDisp instanceof HeaderList)) {
					//#ifdef DTEST
					System.gc();
					long beginMem = Runtime.getRuntime().freeMemory();
					//#endif
					RssFeedParser cactRssParser;
					int cactbookindex;
					if (cbackground) {
						cactRssParser = cbackGrRssParser;
						cactbookindex = cbackGrBookmark;
					} else {
						cactRssParser = ccurRssParser;
						cactbookindex = ccurBookmark;
					}
					hdrList = new HeaderList(this, m_bookmarkList,
						cactbookindex, m_rssFeeds,
						m_unreadImage, m_itunesEnabled, m_loadForm,
						cactRssParser.getRssFeed());
					hdrList.setCommandListener(hdrList, true);
					//#ifdef DTEST
					System.out.println("headerList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
				} else {
					hdrList = (HeaderList)cprevDisp;
				}
				hdrList.sortAllItems( false, m_bookmarkList, m_rssFeeds );
				setCurrent( hdrList );
				//#ifdef DTEST
				hdrList.testFeed();
				//#endif
			}catch(Exception e) {
				m_loadForm.recordExcForm(
						"\nError " + (copenPage ? "loading" :
							"parsing") + " feed on:\n" +
						cbackGrRssParser.getRssFeed().getUrl(), e);

			}catch(OutOfMemoryError e) {
				m_loadForm.recordExcForm(
						"\nOut of memory " + (copenPage ? "loading" :
							"parsing") + " feed on:\n" +
						cbackGrRssParser.getRssFeed().getUrl(), e);
			}catch(Throwable t) {
				m_loadForm.recordExcForm(
						"\nInternal error " + (copenPage ? "loading" :
							"parsing") + " feed on:\n" +
						cbackGrRssParser.getRssFeed().getUrl(), t);
			} finally {
				synchronized(this) {
					if ((cbackGrRssParser != null) &&
						(cbackGrRssParser == m_backGrRssParser)) {
						m_loadForm.removeCommandPrompt(m_backCommand);
						m_curRssParser = m_backGrRssParser;
						m_parseBackground = false;
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("procHeader m_parseBackground=" + m_parseBackground);}
						//#endif
					}
				}
			}
		}
	}

  /**
   * Get file in file system
   * Constructor
   * @author Irv Bunton
   */
	final public void getFile() {
		//#ifdef DJSR75
		/* Find files in the file system to get for bookmark or
		   import from. */
		boolean cgetFile = false;
		boolean cselectDir = false;
		Form cfileRtnForm = null;
		TextField   cfileURL;
		synchronized(this) {
			cselectDir = m_selectDir;
			cfileRtnForm = m_fileRtnForm;
			cfileURL = m_fileURL;
			cgetFile = m_getFile;
		}
		if( cgetFile ) {
			try {
				if (cfileRtnForm instanceof ImportFeedsForm) {
					initializeLoadingForm(
							"Loading files to " + (cselectDir ? "import" :
								"export") + " from...",
							cfileRtnForm);
				} else {
					initializeLoadingForm(
							"Loading files to bookmark from...",
							cfileRtnForm);
				}
				final KFileSelectorMgr fileSelectorMgr =
					new KFileSelectorMgr();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("cselectDir,cfileRtnForm,cfileURL=" + cselectDir + "," + cfileRtnForm + "," + cfileURL);}
				//#endif
				fileSelectorMgr.doLaunchSelector(this,
							cselectDir, cfileRtnForm, cfileURL);
			} catch(OutOfMemoryError ex) {
				m_loadForm.recordExcForm("Out Of Memory Error getting " +
						"file form.", ex);
			} catch (Throwable t) {
				m_loadForm.recordExcForm("Internal error getting file " +
						"form.", t);
			} finally {
				synchronized(this) {
					m_getFile = false;
				}
			}
		}
		//#endif
	}

	/** Save data and exit the application. This accesses the database,
	    so it must not be called by commandAction as it may hang.  It must
	    be called by a separate thread.  */
	final private void exitApp() {
		synchronized(this) {
			if ( (m_exit || m_saveBookmarks) && !m_saving ) {
				if (m_exit && m_stored) {
					return;
				}
				try {
					m_saving = true;
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("m_exit,m_saveBookmarks=" + m_exit + "," + m_saveBookmarks);}
					//#endif
					if ( m_exit ) {
						initializeLoadingForm("Exiting saving data...",
								m_bookmarkList);
					} else if ( m_saveBookmarks ) {
						initializeLoadingForm("Saving data...",
								m_bookmarkList);
					} else {
						return;
					}
					saveBkMrkSettings("Saving items to database...",
							System.currentTimeMillis(), m_exit);
					if (m_exit) {
						try {
							destroyApp(true);
						} catch (MIDletStateChangeException e) {
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine("MIDletStateChangeException=" + e.getMessage());}
							//#else
							e.printStackTrace();
							//#endif
						}
						super.notifyDestroyed();
						m_exit = false;
					} else {
						m_loadForm.appendMsg(
								"Finished saving.  Use back to return.");
						showBookmarkList();
					}
				} finally {
					m_stored = m_exit;
					m_exit = false;
					m_saveBookmarks = false;
					m_saving = false;
				}
			}
		}
	}

	/* Notify us that we are finished. */
	final public void wakeup(int loop) {
    
		if (m_bookmarkList != null) {
			m_bookmarkList.getFeatureMgr().wakeup(loop);
		}
	}

	//#ifdef DMIDP20
	final public void setCurrentItem(Item item) {
		// To prevent loading form from being displayed instead of the
		// next form when that form has no items, show the load form
		// again as a workaround.
		if ((m_loadForm != null) &&
				(m_display.getCurrent() == m_loadForm)) {
			m_display.setCurrent(m_loadForm);
		}
		m_display.setCurrentItem(item);
		// Prevents loading screen Display.getDisplay(this).setCurrentItem(item);
		wakeup(2);
	}
	//#endif

	/* Set current displayable and wake up the thread. */
	final public void setCurrent(Displayable disp) {

		//#ifdef DTESTUI
		String title = "";
		if (disp instanceof Form) {
			title = ((Form)disp).getTitle();
		} else if (disp instanceof List) {
			title = ((List)disp).getTitle();
		}
		System.out.println("Test UI setCurrent " + disp.getClass().getName() + "," + title);
		//#endif
		m_display.setCurrent( disp );
		// Prevents loading screen Display.getDisplay(this).setCurrent( disp );
		wakeup(2);
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Displayable disp) {

		//#ifdef DTESTUI
		String title = "";
		if (disp instanceof Form) {
			title = ((Form)disp).getTitle();
		} else if (disp instanceof List) {
			title = ((List)disp).getTitle();
		}
		System.out.println("Test UI setCurrentNotes " + disp.getClass().getName() + "," + title);
		//#endif
		if (m_loadForm.hasNotes() || m_loadForm.hasExc()) {
			m_loadForm.replaceRef(null, disp);
			setCurrent( m_loadForm );
		} else {
			setCurrent( disp );
		}
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Alert alert, Displayable disp) {

		//#ifdef DTESTUI
		String title = "";
		if (disp instanceof Form) {
			title = ((Form)disp).getTitle();
		} else if (disp instanceof List) {
			title = ((List)disp).getTitle();
		}
		System.out.println("Test UI setCurrentNotes " + disp.getClass().getName() + "," + title);
		//#endif
		if (m_loadForm.hasNotes()) {
			m_loadForm.replaceRef(null, disp);
			setCurrent( alert, m_loadForm );
		} else {
			setCurrent( alert, disp );
		}
	}

	//#ifdef DTESTUI
	/* Get current displayable. */
	final public Displayable getCurrent() {
		return m_display.getCurrent();
	}
	//#endif

	/* Set current displayable and wake up the thread. */
	final public void setCurrent(Alert alert, Displayable disp) {
		m_display.setCurrent( alert, disp );
		// Prevents loading screen Display.getDisplay(this).setCurrent( alert, disp );
		wakeup(2);
	}

    /** Show item form */
    final public void showItemForm() {
        setCurrent( m_itemForm );
    }
    
	//#ifdef DTESTUI
	/** Cause item form to go back to the prev form. */
    final public void backFrItemForm() {
		m_itemForm.commandAction( m_backCommand, m_itemForm );
    }
    
    /** Show item form */
    final public boolean isItemForm() {
        return (m_display.getCurrent() == m_itemForm);
    }
	//#endif
    
    /** Initialize RSS item form */
    final public void initializeItemForm(final RssItunesFeed feed,
								   final RssItunesItem item,
								   List prevList) {
        System.out.println("Create new item form");
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		final String title = item.getTitle();
		m_itemRrnForm = prevList;
		if (title.length() > 0) {
			m_itemForm = new ItemForm( title, title, feed, item);
		} else {
			m_itemForm = new ItemForm( getItemDescription(item), title,
					feed, item);
		}
		m_itemForm.setCommandListener(m_itemForm, false);
		//#ifdef DTEST
		System.out.println("itemForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
		setCurrent( m_itemForm );
    }

	//#ifdef DITUNES
    /** Initialize RSS item form */
    final public void initializeDetailForm(final RssItunesFeed feed,
								   List prevList) {
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		DetailForm displayDtlForm = new DetailForm( feed, prevList );
		displayDtlForm.setCommandListener(displayDtlForm, false);
		//#ifdef DTEST
		System.out.println("displayDtlForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
		setCurrent( displayDtlForm );
    }
	//#endif

	/** Get the max words configured from the descritption. */
	final public String getItemDescription( final RssItunesItem item ) {
		final String [] parts = StringUtil.split(item.getDescription(), " ");
		StringBuffer sb = new StringBuffer();
        final int wordCount = Math.min(parts.length,
				m_appSettings.getMaxWordCountInDesc());
		for (int ic = 0; ic < wordCount; ic++) {
			if (ic > 0) {
				sb.append(" ");
			}
			sb.append(parts[ic]);
		}
		return sb.toString();
	}
    
    /**
     * Start up the Hello MIDlet by creating the TextBox and associating
     * the exit command and listener.
     */
    public void startApp()
	throws MIDletStateChangeException {
		// Initialize bookmarks here since it does some work.
		if (m_bookmarkList == null) {
			synchronized (this) {
				if (m_bookmarkList == null) {
					initForms();
				}
				if (m_novice) {
					if (m_bookmarkList.size() == 0) {
						initializeLoadingForm("Loading items...",
								m_bookmarkList);
						try {
							FeedListParser listParser =
								new LineByLineParser(
									"jar:///data/novice.txt", "", "");
							listParser.setFeedNameFilter(null);
							listParser.setFeedURLFilter(null);
							listParser.setRedirectHtml(false);
							listParser.startParsing();
							listParser.join();
							ImportFeedsForm.addFeedLists(listParser,
									false, 0,
									m_appSettings.getMaximumItemCountInFeed(),
									true, m_rssFeeds, m_bookmarkList,
									m_loadForm);
						}catch(Throwable e) {
							m_loadForm.recordExcForm(
									"\nError loading intial bookmarkd\n", e);
						}
						if (m_loadForm.size() > 0) {
							Item item = m_loadForm.get(
									m_loadForm.size() - 1);
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("item=" + item.getClass().getName());}
							//#endif
							if (item instanceof Gauge) {
								((Gauge)item).setValue(
									m_settings.MAX_REGIONS + 1);
							}
						}
						if (!m_firstTime && !m_loadForm.hasExc()) {
							showBookmarkList();
						}
					}
					if (m_firstTime) {
						m_firstTime = false;
						final Alert aboutAlert = getAbout();
						setCurrent( aboutAlert, 
								(m_loadForm.hasExc() ?
								 (Displayable)m_loadForm :
								 (Displayable)m_bookmarkList) );
					}
				}

			}
		}
    }
    
    /**
	 * Create about alert.
	 * @author  Irving Bunton
	 * @version 1.0
	 */
	final private Alert getAbout() {
		final Alert about = new Alert("About RssReader",
 "RssReader v" + super.getAppProperty("MIDlet-Version") + "-" +
 super.getAppProperty("Program-Version") +
 " Copyright (C) 2005-2006 Tommi Laukkanen, " +
 " Copyright (c) 2001-2005 Todd C. Stellanova, rawthought, " +
 " (C)1999 Romain Guy, Osvaldo Pinali Doederlein, " +
 "http://code.google.com/p/mobile-rss-reader/.  " +
 "This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version." +
 "" +
 "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR " +
 "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, " +
 "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE " +
 "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER " +
 "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING " +
 "FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS " +
 "IN THE SOFTWARE.  " +
 "See the GNU General Public License for more details." +
 "" +
 "You should have received a copy of the GNU General Public License along with this program; if not, write to the:" +
 "Free Software Foundation, Inc." +
 "51 Franklin Street, Fifth Floor" +
 "Boston, MA" +
 "02110-1301 USA" +
 "Using this software means that you accept this license and agree to" +
 "not use this program to break any laws.",
  null, AlertType.INFO);
		about.setTimeout(Alert.FOREVER);
 
		return about;
	}

    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() {
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case we need to save the bookmarks/feeds:w
     */
    public void destroyApp(boolean unconditional)
		throws MIDletStateChangeException {
    	if (unconditional && (m_bookmarkList != null)) {
			m_bookmarkList.getFeatureMgr().setBackground(false);
		}
    }
    
    /** Save bookmarks to record store
        releaseMemory use true if exiting as we do not need
		the rss feeds anymore, so we can save memory and avoid
		having extra memory around.  */
    final public void saveBookmarks(final long storeDate,
			int region, boolean releaseMemory) {
		System.gc();
		StringBuffer bookmarks = new StringBuffer();
		m_settings.setStringProperty("bookmarks", bookmarks.toString());
		final int bsize = m_bookmarkList.size();
		if (bsize == 0) {
			return;
		}
		//#ifdef DTEST
		int storeTime = 0;
		//#endif
		final int bookRegion = region - 1;
		final int iparts = m_settings.MAX_REGIONS - 1;
		final int firstIx = bookRegion * bsize / iparts;
		final int endIx = (bookRegion + 1) * bsize / iparts - 1;
        try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("firstIx,endIx=" + firstIx + "," + endIx);}
			//#endif
			Vector vstored = new Vector();
			try {
				/** Try to save feeds including items */
				for( int i=firstIx; i<=endIx; i++) {
					final String name = m_bookmarkList.getString(i);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("i,name=" + i + "," + name);}
					//#endif
					if (!m_rssFeeds.containsKey( name )) {
						continue;
					}
					if( name.length()>0) {
						final RssItunesFeed rss =
							(RssItunesFeed)m_rssFeeds.get( name );
						//#ifdef DCOMPATIBILITY1
						CompatibilityRssFeed1 rss1 = new CompatibilityRssFeed1(rss);
						//#ifdef DTEST
						String prevStore = rss1.getStoreString(true);
						RssItunesFeed nrss = new RssItunesFeed( false, true, true, prevStore );
						if (!rss1.equals(nrss)) {
							//#ifdef DLOGGING
							logger.severe("itunes store stings not backwards compatible i=" + i);
							//#endif
						}
						long beginStore = System.currentTimeMillis();
						//#endif
						bookmarks.append(rss1.getStoreString(true));
						//#elifdef DCOMPATIBILITY2
						CompatibilityRssFeed2 rss2 = new CompatibilityRssFeed2(rss);
						final String prevStore = rss2.getStoreString(true);
						bookmarks.append(prevStore);
						//#ifdef DTEST
						RssItunesFeed nrss = new RssItunesFeed(new RssFeed(
											false, true, prevStore ));
						if (!rss2.equals(nrss)) {
							//#ifdef DLOGGING
							logger.severe("itunes store stings not backwards compatible i=" + i);
							//#endif
						}
						long beginStore = System.currentTimeMillis();
						//#endif
						//#else
						//#ifdef DTEST
						long beginStore = System.currentTimeMillis();
						//#endif
						bookmarks.append(rss.getStoreString(true, true));
						//#endif
						//#ifdef DTEST
						storeTime += System.currentTimeMillis() - beginStore;
						//#endif
						//#ifdef DCOMPATIBILITY
						bookmarks.append(OLD_FEED_SEPARATOR);
						//#else
						bookmarks.append(CFEED_SEPARATOR);
						//#endif
						if (releaseMemory) {
							vstored.addElement( name );
						}
					}
				}
			} catch(OutOfMemoryError error) {
	//#ifdef DLOGGING
				logger.severe("saveBookmarks could not save.", error);
	//#endif
				System.out.println("Error saveBookmarks could not save.  " +
						error + " " + error.getMessage());
				final Alert memoryAlert = new Alert(
						"Out of memory", 
						"Saving bookmarks without updated news items.",
						null,
						AlertType.WARNING);
				memoryAlert.setTimeout(Alert.FOREVER);
				setCurrent( memoryAlert, m_loadForm );
				
				/** Save feeds without items */
				bookmarks.setLength(0);
				for( int i=firstIx; i<=endIx; i++) {
					final String name = m_bookmarkList.getString(i);
					if( name.length() == 0) {
						continue;
					}
					final RssItunesFeed rss = (RssItunesFeed)m_rssFeeds.get( name );
					bookmarks.append(rss.getStoreString(false, true));
					bookmarks.append(CFEED_SEPARATOR);
					if (releaseMemory) {
						vstored.addElement( name );
					}
				}
			} finally {
				if (releaseMemory) {
					final int vslen = vstored.size();
					for (int ic = 0; ic < vslen; ic++) {
						m_rssFeeds.remove( (String)vstored.elementAt( ic ));
					}
				}
			}
			//#ifdef DTEST
			System.out.println("storeTime=" + storeTime);
			//#endif
            m_settings.setStringProperty("bookmarks",bookmarks.toString());
			//#ifndef DCOMPATIBILITY
			m_settings.setBooleanProperty(m_settings.ITEMS_ENCODED, true);
			m_settings.setLongProperty(m_settings.STORE_DATE, storeDate);
			//#endif
		} catch (Throwable t) {
            m_settings.setStringProperty("bookmarks", bookmarks.toString());
			//#ifdef DTEST
			System.out.println("storeTime=" + storeTime);
			//#endif
//#ifdef DLOGGING
			logger.severe("saveBookmarks could not save.", t);
//#endif
			System.out.println("saveBookmarks could not save." + t + " " +
					           t.getMessage());
			t.printStackTrace();
        }
    }

    /** Update RSS feed's headers */
    final public void updateHeaders(final boolean updMod, Displayable dispBack) {
		try {
			synchronized(this) {
				m_prevDisp = dispBack;
				if (updMod) {
					m_getModPage = true;
				} else {
					m_getPage = true;
				}
				m_openPage = false;
			}
		} catch (Throwable t) {
//#ifdef DLOGGING
			logger.severe("updateHeaders could not update." + t + " " +
					           t.getMessage());
//#endif
			System.out.println("updateHeaders could not update." + t + " " +
					           t.getMessage());
		}
    }
    
    /** Update all RSS feeds */
    final private void updateAllHeaders(final boolean updModHdr) {
		if (updModHdr) {
			m_refreshUpdFeeds = true;
		} else {
			m_refreshAllFeeds = true;
		}
    }
    
	//#ifdef DJSR75
	/* Set flag to show find files list.
	   fileRtnForm - Form to return to after file finished.
	   fileURL - Text field that has URL to put file URL into as well
	   			 as field to go back to if 2.0 is valid.
	*/
	final public void reqFindFiles( final boolean selectDir,
			final Form fileRtnForm, final TextField fileURL) {
		synchronized(this) {
			m_fileRtnForm = fileRtnForm;
			m_fileURL = fileURL;
			m_getFile = true;
			m_selectDir = selectDir;
		}
	}
	//#endif

	/* Save the current bookmarks and other properties.
	   releaseMemory - true if memory used is to be released as the
	   				   bookmarks are saved.  Used when exitiing as true.
	*/
	final private synchronized void saveBkMrkSettings(String guageTxt,
			final long storeDate,
			final boolean releaseMemory) {
		Gauge gauge = new Gauge(guageTxt, false,
				m_settings.MAX_REGIONS + 1, 0);
		int pl = m_loadForm.append(gauge);
		showLoadingForm();
		try {
			//#ifndef DCOMPATIBILITY
			m_settings.setBooleanProperty(m_settings.ITEMS_ENCODED, true);
			m_settings.setLongProperty(m_settings.STORE_DATE, storeDate);
			//#endif
			m_settings.save(0, false);
			gauge.setValue(1);
			for (int ic = 1; ic < m_settings.MAX_REGIONS; ic++) {
				saveBookmarks(storeDate, ic, releaseMemory);
				m_settings.save(ic, false);
				gauge.setValue(ic + 1);
			}
			// Set internal region back to 0.
			m_settings.setStringProperty("bookmarks","");
			//#ifndef DCOMPATIBILITY
			m_settings.setBooleanProperty(m_settings.ITEMS_ENCODED, true);
			m_settings.setLongProperty(m_settings.STORE_DATE, storeDate);
			//#endif
			m_settings.save(0, false);
			gauge.setValue(m_settings.MAX_REGIONS + 1);
			pl = -1;
		} catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("Saving feeds.", e);
			//#endif
			/** Error while parsing RSS feed */
			System.out.println("Error saving: " + e + e.getMessage());
		} catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("Saving feeds.", t);
			//#endif
			/** Error while parsing RSS feed */
			System.out.println("Error saving: " + t + t.getMessage());
		} finally {
			if (pl >= 0) {
				m_loadForm.delete(pl);
			}
		}
	}

	/** Remove the ref to this displayable so that the memory can be freed. */
	final public void replaceRef(final Displayable disp,
			final Displayable newDisp) {
		m_loadForm.replaceRef(disp, newDisp);
	}

    /** Respond to commands */
    public void commandAction(Command c, Displayable s) {
		//#ifdef DLOGGING
		//#ifdef DMIDP20
		if (finestLoggable) {logger.finest("command,displayable=" + c.getLabel() + "," + s.getTitle());}
		//#else
		if (finestLoggable) {logger.finest("command,displayable=" + c.getLabel());}
		//#endif
		//#endif
        /** Add new RSS feed bookmark */
        if( c == m_addNewBookmark ){
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getAddBMForm = true;
        }
        
        /** Exit from MIDlet and save bookmarks */
        if( c == m_exitCommand ){
			synchronized(this) {
				m_exit = true;
			}
        }
        
        /** Save bookmarks without exit (don't free up bookmarks)  */
        if( c == m_saveCommand ){
			m_saveBookmarks = true;
        }
        
        /** Edit currently selected RSS feed bookmark */
        if( c == m_editBookmark ){
			try {
				if( m_bookmarkList.size()>0 ){
					m_curBookmark = m_bookmarkList.getSelectedIndex();
					if (m_curBookmark < 0) {
						m_curBookmark = 0;
						m_bookmarkList.setSelectedIndex(0, true);
					}
					m_getEditBMForm = true;
				}
			}catch(Throwable t) {
				//#ifdef DLOGGING
				logger.severe("Editing feeds.", t);
				//#endif
				/** Error while parsing RSS feed */
				System.out.println("Error editing feeds: " + t.getMessage());
			}
        }
        
        /** Delete currently selected RSS feed bookmark */
        if(( c == m_delBookmark ) && ( m_bookmarkList.size()>0 )){
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			String name = m_bookmarkList.getString(m_curBookmark);
			m_bookmarkList.delete( m_curBookmark );
			if (m_rssFeeds.containsKey( name )) {
				m_rssFeeds.remove( name );
            }
        }
        
        /** Open RSS feed bookmark */
        if( ((c == m_openBookmark) || (c == List.SELECT_COMMAND &&
                (s==m_bookmarkList)))){
			try {
				m_curBookmark = FeatureMgr.getSelectedIndex(m_bookmarkList);
				if (m_curBookmark >= 0) {
					RssItunesFeed feed = (RssItunesFeed)m_rssFeeds.get(
							m_bookmarkList.getString(m_curBookmark));
					m_curRssParser = new RssFeedParser( feed );
					if( feed.getItems().size()==0 ) {
						/** Update RSS feed headers only if this is a first time */
						updateHeaders(false, m_bookmarkList);
					} else {
						/**
						 * Show currently selected RSS feed
						 * headers without updating them
						 */
						synchronized(this) {
							m_openPage = true;
							m_prevDisp = m_bookmarkList;
						}
					}

					// Open existing bookmark and show headers (items).
					procPage(!m_openPage);
				}
			}catch(Throwable t) {
				//#ifdef DLOGGING
				logger.severe("commandAction openPage.", t);
				//#endif
				/** Error while parsing RSS feed */
				System.out.println("Error editing feeds: " + t.getMessage());
			}
		}
        
        /** Read unread items date sorted */
        if(( c == m_readUnreadItems ) && (m_bookmarkList.size() > 0)) {
			m_runNews = true;
        }
        
        /** Open RSS feed's selected topic */
        /** Get back to RSS feed headers */
        if( c == m_backCommand ){
            setCurrent( m_itemRrnForm );
			//#ifdef DTESTUI
			if (m_headerIndex >= 0) {
				m_headerNext = true;
			} else if (m_allNewsTestList != null) {
				m_allNewsTestList.gotoNews();
			}
			//#endif
        }
        
        /** Update all RSS feeds */
        if( (c == m_updateAllCmd) || (c == m_updateAllModCmd) ) {
            updateAllHeaders(c == m_updateAllModCmd);
        }
        
        /** Show import feed list form */
        if( c == m_importFeedListCmd ) {
			// Set current bookmark so that the added feeds go after
			// the current boolmark.
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getImportForm = true;
        }
        
        /** Show export feed list form */
        if( c == m_exportFeedListCmd ) {
			// Set current bookmark so that the added feeds go after
			// the current boolmark.
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getExportForm = true;
        }
        
		//#ifdef DTEST
		/** Show import feed list form and default file */
		if( c == m_importCurrFeedListCmd ) {
			m_curBookmark = FeatureMgr.getSelectedIndex(m_bookmarkList);
			if( m_curBookmark >= 0 ) {
				//#ifdef DTESTUI
				m_bookmarkLastIndex = m_curBookmark;
				//#endif
				m_getTestImportForm = true;
				m_getImportForm = true;
			}
        }
		//#endif

		//#ifdef DTESTUI
        /** Auto edit feeds/bookmarks to */
        if( c == m_testBMCmd ) {
			m_bookmarkIndex = 0;
			System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" + m_bookmarkIndex);
		}
		//#endif

		//#ifdef DTESTUI
        /** Go back to last position */
        if( c == m_testRtnCmd ) {
			if (m_bookmarkLastIndex != 1) {
				if (m_bookmarkList.getSelectedIndex() >= 0) {
					m_bookmarkList.setSelectedIndex(
							m_bookmarkList.getSelectedIndex(), false);
				}
				m_bookmarkList.setSelectedIndex( m_bookmarkLastIndex, true );
			}
		}
		//#endif

        /** Settings form */
        if( c == m_settingsCmd ) {
			m_getSettingsForm = true;
        }
        
        /** Show about */
		if( c == m_aboutCmd ) {
			m_about = true;
		}

		//#ifdef DTESTUI
        /** Show encodings list */
		if( c == m_testEncCmd ) {
			try {
				initializeLoadingForm("Loading test form...", m_bookmarkList);
				setCurrent( m_testingForm );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//#endif

	//#ifdef DLOGGING
        /** Show about */
		if( c == m_debugCmd ) {
			setCurrent( m_debug );
		}

        /** Clear form */
		if( c == m_clearDebugCmd ) {
			while(m_debug.size()>0) {
				m_debug.delete(0);
			}
		}

        /** Back to bookmarks */
		if( c == m_backFrDebugCmd ) {
			showBookmarkList();
		}

		//#endif

    }
    
	public void stopRssBackground() {
		synchronized(this) {
			if (m_parseBackground) {
				m_parseBackground = false;
				m_curRssParser = new RssFeedParser(
						m_backGrRssParser.getRssFeed());
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("m_parseBackground,m_curRssParser,m_backGrRssParser=" + m_parseBackground + "," + m_curRssParser + "," + m_backGrRssParser);}
			//#endif
		}
	}

	//#ifdef DTESTUI
    public void setBookmarkIndex(int bookmarkIndex) {
        this.m_bookmarkIndex = bookmarkIndex;
    }

    public int getBookmarkIndex() {
        return (m_bookmarkIndex);
    }
	//#endif

	//#ifdef DITUNES
	/* Form to look at item. */
	final public class DetailForm extends FeatureForm
		implements CommandListener {
		private Displayable        m_rtn;

		private DetailForm (final RssItunesFeed feed, Displayable rtn ) {
			super(m_midlet, feed.getName());
			this.m_rtn = rtn;
			super.addCommand( m_backCommand );
			if (m_itunesEnabled && feed.isItunes()) {
				final String language = feed.getLanguage();
				if (language.length() > 0) {
					super.append(new StringItem("Language:", language));
				}
				final String author = feed.getAuthor();
				if (author.length() > 0) {
					super.append(new StringItem("Author:", author));
				}
				final String subtitle = feed.getSubtitle();
				if (subtitle.length() > 0) {
					super.append(new StringItem("Subtitle:", subtitle));
				}
				final String summary = feed.getSummary();
				if (summary.length() > 0) {
					super.append(new StringItem("Summary:", summary));
				}
				super.append(new StringItem("Explicit:", feed.getExplicit()));
				final String title = feed.getTitle();
				if (title.length() > 0) {
					super.append(new StringItem("title:", title));
				}
				final String description = feed.getDescription();
				if (description.length() > 0) {
					super.append(new StringItem("Description:", description));
				}
			}
			final String link = feed.getLink();
			if (link.length() > 0) {
				//#ifdef DMIDP20
				StringItem slink = new StringItem("Link:", link,
												  Item.HYPERLINK);
				//#else
				StringItem slink = new StringItem("Link:", link);
				//#endif
				super.append(slink);
			}
			final Date feedDate = feed.getDate();
			if (feedDate != null) {
				super.append(new StringItem("Date:",
							feedDate.toString()));
			}
		}

		public void commandAction(Command c, Displayable s) {
			/* Back from details form. */
			if( c == m_backCommand ){
				setCurrent( m_rtn );
			}
		}
			
	}
	//#endif

	/* Form to look at item. */
	final private class ItemForm extends FeatureForm
		implements CommandListener {
		private boolean     m_platformReq;    // Flag to get platform req open link
		private String m_platformURL;         // Platform request URL
		//#ifdef DMIDP20
		private Command     m_openLinkCmd;      // The open link command
		private Command     m_openEnclosureCmd; // The open enclosure command
		//#endif
		private Command     m_copyEnclosureCmd; // The copy enclosure command
		private Command     m_copyLinkCmd;    // The copy link command

		private ItemForm(final String title, final String actTitle,
								final RssItunesFeed feed,
								   final RssItunesItem item) {
			super(m_midlet, title);
			m_platformReq = false;
			//#ifdef DMIDP20
			m_openLinkCmd       = new Command("Open link", Command.SCREEN, 2);
			m_openEnclosureCmd  = new Command("Open enclosure", Command.SCREEN, 2);
			//#endif
			m_copyLinkCmd       = new Command("Copy link", Command.SCREEN, 1);
			m_copyEnclosureCmd  = new Command("Copy enclosure", Command.SCREEN, 1);
			super.addCommand( m_backCommand );
			final String sienclosure = item.getEnclosure();
			final String desc = item.getDescription();
			if ((actTitle.length()>0) && (desc.length()>0)) {
				super.append(new StringItem(actTitle + "\n", desc));
			} else if (actTitle.length()>0) {
				super.append(new StringItem("Title\n", actTitle));
			} else {
				super.append(new StringItem("Description\n", desc));
			}
			citem = item;
			if (m_itunesEnabled && (item.isItunes() || feed.isItunes())) {
				final String author = item.getAuthor();
				if (author.length() > 0) {
					super.append(new StringItem("Author:", author));
				}
				final String subtitle = item.getSubtitle();
				if (subtitle.length() > 0) {
					super.append(new StringItem("Subtitle:", subtitle));
				}
				final String summary = item.getSummary();
				if (summary.length() > 0) {
					super.append(new StringItem("Summary:", summary));
				}
				final String duration = item.getDuration();
				if (duration.length() > 0) {
					super.append(new StringItem("Duration:", duration));
				}
				String expLabel = "Explicit:";
				String explicit = item.getExplicit();
				if (explicit.equals(RssItunesItem.UNSPECIFIED)) {
					expLabel = "Feed explicit:";
					explicit = feed.getExplicit();
				}
				super.append(new StringItem(expLabel, explicit));
			}
			String linkLabel = "Link:";
			String link = item.getLink();
			//#ifdef DITUNES
			if (link.length() == 0) {
				link = feed.getLink();
				linkLabel = "Feed link:";
			}
			//#endif
			if (link.length() > 0) {
				//#ifdef DMIDP20
				StringItem slink = new StringItem(linkLabel, link, Item.HYPERLINK);
				//#else
				StringItem slink = new StringItem(linkLabel, link);
				//#endif
				citemLnkNbr  = super.append(slink);
			} else {
				citemLnkNbr  = -1;
			}
			if (sienclosure.length() > 0) {
				//#ifdef DMIDP20
				StringItem senclosure = new StringItem("Enclosure:", sienclosure,
														  Item.HYPERLINK);
				//#else
				StringItem senclosure = new StringItem("Enclosure:", sienclosure);
				//#endif
				citemEnclNbr = super.append(senclosure);
			} else {
				citemEnclNbr  = -1;
			}
			
			// Add item's date if it is available
			String dateLabel = "Date:";
			Date itemDate = item.getDate();
			//#ifdef DITUNES
			if(itemDate==null) {
				itemDate = feed.getDate();
				dateLabel = "Feed date:";
			}
			//#endif
			if(itemDate!=null) {
				//#ifdef DMIDP10
				// MIDP 1.0 does not require toString to produce a date.
				// It may give the hash of the string (e.g. Sony Ericsson T637).
				final String sdate = RssFormatParser.stdDate(itemDate, "GMT");
				//#else
				final String sdate = itemDate.toString();
				//#endif
				super.append(new StringItem(dateLabel, sdate));
			}

			if (link.length() > 0) {
				super.addCommand( m_copyLinkCmd );
			}
			if (sienclosure.length() > 0) {
				super.addCommand( m_copyEnclosureCmd );
			}
			//#ifdef DMIDP20
			if (link.length() > 0) {
				super.addCommand( m_openLinkCmd );
			}
			if (sienclosure.length() > 0) {
				super.addCommand( m_openEnclosureCmd );
			}
			//#endif
		}

		public void commandAction(Command c, Displayable s) {
			/** Get back to RSS feed headers */
			if( c == m_backCommand ){
				setCurrent( m_itemRrnForm );
				//#ifdef DTESTUI
				if (m_headerIndex >= 0) {
					m_headerNext = true;
				}
				//#endif
			}
			
			/** Copy link to clipboard.  */
			if( c == m_copyLinkCmd ){
				String link = citem.getLink();
				super.set(citemLnkNbr, new TextField("Link:", link,
						link.length(), TextField.URL));
				//#ifdef DMIDP10
				setCurrent(m_itemForm);
				//#else
				setCurrentItem(m_itemForm.get(citemLnkNbr));
				//#endif
			}
			
			/** Copy enclosure to clipboard.  */
			if( c == m_copyEnclosureCmd ){
				final String link = citem.getEnclosure();
				super.set(citemEnclNbr, new TextField("Enclosure:",
					link, link.length(), TextField.URL));
				//#ifdef DMIDP10
				setCurrent(m_itemForm);
				//#else
				setCurrentItem(m_itemForm.get(citemEnclNbr));
				//#endif
			}
			
			//#ifdef DMIDP20
			/** Go to link and get back to RSS feed headers */
			if( c == m_openLinkCmd ){
				synchronized(this) {
					final String link = citem.getLink();
					m_platformURL = link;
					m_platformReq = true;
				}
			}
			//#endif

			//#ifdef DMIDP20
			/** Go to link and get back to RSS feed headers */
			if( c == m_openEnclosureCmd ){
				m_platformURL = citem.getEnclosure();
				m_platformReq = true;
			}
			//#endif
			
			execute();

		}

		public void execute() {

			/* Handle going to link (platform request.). */
			//#ifdef DMIDP20
			if ( m_platformReq ) {
				try {

					initializeLoadingForm("Loading web page...",
							m_itemForm);
					if( m_midlet.platformRequest(m_platformURL) ) {
						initializeLoadingForm("Exiting saving data...",
								m_itemRrnForm);
						synchronized(this) {
							m_exit = true;
							exitApp();
						}
					} else {
						setCurrent( m_itemRrnForm );
					}
				} catch (ConnectionNotFoundException e) {
					//#ifdef DLOGGING
					logger.severe("Error opening link " + m_platformURL, e);
					//#endif
					final Alert badLink = new Alert("Could not connect to link",
							"Bad link:  " + m_platformURL,
							null, AlertType.ERROR);
					badLink.setTimeout(Alert.FOREVER);
					setCurrent( badLink, m_itemRrnForm );
				} finally {
					m_platformReq = false;
				}
			}
		//#endif

		}

	}

	/* Form to show data being loaded.  Save messages and exceptions to
	   allow them to be viewed separately as well as diagnostics for
	   reporting errors. */
	final public class LoadingForm extends FeatureForm
		implements CommandListener {
		//#ifdef DMIDP10
		private String      m_title;         // Store title.
		//#endif
		private Command     m_loadMsgsCmd;   // The load form messages command
		private Command     m_loadDiagCmd;   // The load form diagnostic command
		private Command     m_loadErrCmd;    // The load form error command
		private Vector m_msgs = new Vector(); // Original messages
		private Vector m_notes = new Vector(); // Notes
		private Vector m_excs = new Vector(); // Only errors
		private Displayable m_disp;

		/* Constructor */
		LoadingForm(final String title,
				final Displayable disp) {
			super(m_midlet, title);
			//#ifdef DMIDP10
			this.m_title = title;
			//#endif
			m_loadMsgsCmd       = new Command("Messages", Command.SCREEN, 2);
			m_loadErrCmd        = new Command("Errors", Command.SCREEN, 3);
			m_loadDiagCmd       = new Command("Diagnostics", Command.SCREEN, 4);
			if (m_backCommand == null) {
				m_backCommand   = new Command("Back", Command.BACK, 1);
			}
			super.addCommand( m_loadMsgsCmd );
			super.addCommand( m_loadErrCmd );
			super.addCommand( m_loadDiagCmd );
			m_disp = disp;
			if (disp != null) {
				super.addCommand( m_backCommand );
			}
		}

		/** Respond to commands */
		public void commandAction(Command c, Displayable s) {

			if( c == m_backCommand ){
				Displayable cdisp = null;
				synchronized(this) {
					cdisp = m_disp;
				}
				stopRssBackground();
				setCurrent( cdisp );
			}

			/** Give messages for loading */
			if( c == m_loadMsgsCmd ) {
				showMsgs();
			}

			/** Give errors for loading */
			if( c == m_loadErrCmd ) {
				showErrMsgs(true);
			}

			/** Give diagnostics for loading */
			if( c == m_loadDiagCmd ) {
				showErrMsgs(false);
			}

		}

		/** Set title and addmessage for loading form */
		public void setLoadingFinished(final String title, String msg) {
			if (title != null) {
				super.setTitle(title);
			}
			if (msg != null) {
				appendMsg(msg);
			}
		}
		
		/* Record the exception in the loading form, log it and give std error. */
		public void recordExcForm(final String causeMsg, final Throwable e) {
			final CauseException ce = new CauseException(causeMsg, e);
			//#ifdef DLOGGING
			logger.severe(ce.getMessage(), e);
			//#endif
			/** Error while parsing RSS feed */
			System.out.println(e.getClass().getName() + " " + ce.getMessage());
			e.printStackTrace();
			setTitle("Finished with errors below");
			addExc(ce.getMessage(), ce);
			setCurrent( this );
		}

		/* Show errors and diagnostics. */
		private void showMsgs() {
			try {
				while(super.size()>0) {
					super.delete(0);
				}
				final int elen = m_msgs.size();
				for (int ic = 0; ic < elen; ic++) {
					super.append((String)m_msgs.elementAt(ic));
				}
			}catch(Throwable t) {
				//#ifdef DLOGGING
				logger.severe("showMsgs", t);
				//#endif
				/** Error while executing constructor */
				System.out.println("showMsgs " + t.getMessage());
				t.printStackTrace();
			}
		}

		/* Show errors and diagnostics. */
		private void showErrMsgs(final boolean showErrsOnly) {
			try {
				while(super.size()>0) {
					super.delete(0);
				}
				final int elen = m_excs.size();
				for (int ic = 0; ic < elen; ic++) {
					Throwable nexc = (Throwable)m_excs.elementAt(ic);
					while (nexc != null) {
						String msg = nexc.getMessage();
						if (msg != null) {
							super.append(nexc.getMessage());
							// If showing errs only, only show the first error found
							if (showErrsOnly) {
								break;
							}
						} else if (!showErrsOnly) {
							super.append("Error " + nexc.getClass().getName());
						}
						if (nexc instanceof CauseException) {
							nexc = ((CauseException)nexc).getCause();
						} else {
							break;
						}
					}
				}
				if (!showErrsOnly) {
					super.append(new StringItem("Active Threads:",
								Integer.toString(Thread.activeCount())));
				}
			}catch(Throwable t) {
				//#ifdef DLOGGING
				logger.severe("showErrMsgs", t);
				//#endif
				/** Error while executing constructor */
				System.out.println("showErrMsgs " + t.getMessage());
				t.printStackTrace();
			}
		}

		/* Append message to form and save in messages. */
		public void appendMsg(final String msg) {
			if (msg != null) {
				super.append(msg);
				m_msgs.addElement(msg);
			}
		}

		/* Append note to form and save in messages and notes. */
		public void appendNote(final String note) {
			if (note != null) {
				super.append(note);
				m_notes.addElement(note);
			}
		}

		/* Add exception. */
		public void addExc(final String msg, final Throwable exc) {
			appendMsg(msg);
			m_excs.addElement(exc);
		}

		/* Replace reference to displayable to free memory or
		   define where to return to.  Use null to go to m_bookmarkList. */
		public void replaceRef(final Displayable disp,
				final Displayable newDisp) {
			//#ifdef DLOGGING
			boolean removed = false;
			//#endif
			synchronized (this) {
				Displayable odisp = m_disp;
				if (m_disp == disp) {
					m_disp = null;
				}
				m_disp = (newDisp == null) ? m_bookmarkList : newDisp;
				if ((odisp == null) && (m_disp != null)) {
					super.addCommand( m_backCommand);
				}
			}
			//#ifdef DLOGGING
			if (removed) {
				removed = true;
				if (finestLoggable) {logger.finest("Ref removed " + disp);}
			}
			//#endif
		}

		/* Check for exceptions. */
		public boolean hasExc() {
			return (m_excs.size() > 0);
		}

		/* Check for notes. */
		public boolean hasNotes() {
			return (m_notes.size() > 0);
		}

		/* Check for messages. */
		public boolean hasMsgs() {
			return (m_msgs.size() > 0);
		}

		//#ifdef DMIDP10
		public String getTitle() {
			return m_title;
		}
		//#endif

	}

    public LoadingForm getLoadForm() {
        return (m_loadForm);
    }

}
