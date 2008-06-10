/*
   TODO handle OutOfMemoryError
   TODO handle Exceptions
 * 
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
//#define DNOTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define MIDP define
//#define DMIDP20
// Expand to define CLDC define
//#define DCLDCV10
// Expand to define itunes define
//#define DNOITUNES
// Expand to define logging define
//#define DNOLOGGING
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define compatibility
//#define DNOCOMPAT


package com.substanceofcode.rssreader.presentation;

//#ifdef DJSR75
//@import org.kablog.kgui.KFileSelectorMgr;
//#endif
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
//#ifdef DCOMPATIBILITY1
//@import com.substanceofcode.rssreader.businessentities.CompatibilityRssFeed1;
//#elifdef DCOMPATIBILITY2
//@import com.substanceofcode.rssreader.businessentities.CompatibilityRssFeed2;
//#elifdef DCOMPATIBILITY3
//@import com.substanceofcode.rssreader.businessentities.CompatibilityRssItunesFeed3;
//#endif
import com.substanceofcode.rssreader.presentation.PromptList;
import com.substanceofcode.rssreader.presentation.PromptForm;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.rssreader.businesslogic.Controller;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.HTMLAutoLinkParser;
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.presentation.AllNewsList;
import com.substanceofcode.utils.Settings;
import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.StringUtil;
import com.substanceofcode.utils.CauseException;
import com.substanceofcode.utils.CauseMemoryException;
import com.substanceofcode.utils.CauseRecStoreException;
import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Gauge;
// If not using the test UI define the J2ME UI's
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
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
//#ifdef DTESTUI
//@import com.substanceofcode.testutil.presentation.TestingForm;
//@import com.substanceofcode.testutil.TestOutput;
//#endif

//#ifdef DJSR238
//@import javax.microedition.global.ResourceManager;
//#endif

//#ifdef DMIDP20
import cz.cacek.ebook.PageCustomItem;
//#endif
import cz.cacek.ebook.util.ResourceProviderME;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//@import net.sf.jlogmicro.util.logging.FormHandler;
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
    
    final static public char CFEED_SEPARATOR = (char)2;
    final static public char OLD_FEED_SEPARATOR = '^';
    // Attributes
    private Display     m_display;          // The display for this MIDlet
    private Settings    m_settings;         // The settings
    private RssReaderSettings m_appSettings;// The application settings
    private Hashtable   m_rssFeeds;         // The bookmark URLs
    private Thread      m_netThread;        // The thread for networking
    final static private boolean     JSR75_ENABLED =
	          (System.getProperty(
			"microedition.io.file.FileConnection.version") != null);
    private boolean     m_debugOutput = false; // Flag to write to output for test
    private boolean     m_process = true;   // Flag to continue looping
    private boolean     m_needWakeup = false;   // Flag to show need to wakeup
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_openPage;         // Open the headers
    private boolean     m_saveBookmarks;    // The save bookmarks flag
    private boolean     m_exit;             // The exit application flag
    private boolean     m_saving;           // The saving settings flag
    private boolean     m_stored;           // The data stored flag
    private boolean     m_getModPage;       // The noticy flag for modified HTTP
    private boolean     m_getSettingsForm;  // Flag to get settings form
    private boolean     m_getHelpForm;      // Flag to get help form
    private boolean     m_getAddBMForm;     // Flag to get add bookmark form
    private boolean     m_getEditBMForm;    // Flag to get edit bookmark form
    private boolean     m_mainBmk;          // Flag to show main bookmarks
    private boolean     m_platformReq;      // Flag to get platform req open link
    private boolean     m_refreshAllFeeds;  // The notify flag for all feeds
    private boolean     m_refreshUpdFeeds;  // The notify flag for updated feeds
    private boolean     m_getImportForm;    // The noticy flag for going to Import Feed list
    private boolean     m_getFile;          // The noticy flag for getting find files form
    private boolean     m_runNews = false;  // Run AllNewsList form.
	//#ifdef DTEST
//@    // Get import form using URL from current bookmark
//@    private boolean     m_getTestImportForm = false; // Get import form 
	//#endif
	//#ifdef DTESTUI
//@	boolean m_headerNext = false; // Flag to control opening the next header
//@	boolean m_itemNext = false; // Flag to control opening the next item
	//#endif
    private byte[]      m_importSave = null; // Import form save
    private byte[]      m_addBMSave = null; // Edit bookmark form save
	//#ifdef DTESTUI
//@	private int         m_headerIndex = -1; // Index in headers to auto test
//@    // Index in bookmarks to auto test by opening in edit
//@	// This counts up until the bookmark size is reached.
//@    private int         m_bookmarkIndex = -1;
//@    private int         m_bookmarkLastIndex = -1; // Last place when import current was selected
	//#endif
	// Tells us if this is the first time program was used.  This is
	// done by seeing if max item count is set.  We also set it after
	// showing the about.
    private boolean     m_firstTime = false;
    private boolean     m_itunesEnabled = false;
	//#ifdef DLOGGING
//@    private boolean fineLoggable;
//@    private boolean finestLoggable;
	//#endif
	// This is a mark (icon) next to unread items (except on unread items
	// screen).  Given that many screens are small, it is optional as 
	// we don't want to reduce space for text.
    private Image           m_unreadImage;
    
	private int             m_addBkmrk; // Place to add (insert) imported bookmarks
    // Currently selected bookmark
    private int             m_curBookmark;  // The currently selected item
    private RssFeedParser   m_curRssParser; // The currently selected RSS
	//#ifdef DLOGGING
//@	private RecordStore     m_recStore = null; // Rec store
	//#endif
    
    // GUI items
    private PromptList  m_bookmarkList;     // The bookmark list
	//#ifdef DTESTUI
//@    private HeaderList  m_headerTestList;       // The header list
//@    private AllNewsList m_unreadHeaderTestList; // The test header list for unread items
	//#endif
    private RssReaderMIDlet m_midlet;       // The RssReaderMIDlet midlet
    private List        m_itemRrnForm;      // The list to return from for item
	//#ifdef DTEST
//@    private PromptForm  m_itemForm;         // The item form
	//#else
    private Form        m_itemForm;         // The item form
	//#endif
    private LoadingForm m_loadForm = null;  // The "loading..." form
    private TextField   m_boxRtnItem;       // The item to return to
    private TextField   m_fileURL;          // The file URL field from a form
    private Form        m_boxRtnForm;       // The form to return to
    private Form        m_fileRtnForm;      // The form to return to for file
    private TextBox     m_boxURL;           // The feed list URL box
	//#ifdef DTESTUI
//@    private TestingForm m_testingForm;    // The testing form
	//#endif
    
    // Commands
	//#ifdef DTESTUI
//@	private Command     m_testRssCmd;       // Test UI rss headers command
//@	private Command     m_testBMCmd;        // Test UI bookmarks list command
//@	private Command     m_testRtnCmd;       // Test UI return to prev command
	//#endif
    private Command     m_exitCommand;      // The exit command
    private Command     m_saveCommand;      // The save without exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_readUnreadItems;  // The read unread items command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
    private Command     m_backBookmark;     // The back to bookmark list command
    private Command     m_backCommand;      // The back to header list command
	//#ifdef DMIDP20
    private Command     m_openLinkCmd;      // The open link command
    private Command     m_openEnclosureCmd; // The open enclosure command
	//#endif
    private Command     m_copyLinkCmd;    // The copy link command
    private Command     m_copyEnclosureCmd; // The copy enclosure command
    private Command     m_rssItemHelpCmd; // The RSS item command
    private Command     m_importFeedListCmd;// The import feed list command
	//#ifdef DTEST
//@    private Command     m_importCurrFeedListCmd;// The import feed list command and default current seleected feed
//@	private Command     m_testClearCmd;    // Test quit and clear database
	//#endif
	//#ifdef DTESTUI
//@    private Command     m_testEncCmd;     // The test encoding
	//#endif
	//#ifdef DLOGGING
//@    private Command     m_debugCmd; // The back to bookmark list command
//@	                                      // from debug form
//@    private Command     m_backFrDebugCmd; // The back to bookmark list command
//@    private Command     m_clearDebugCmd; // The back to bookmark list command
//@	                                      // from debug form
	//#endif
    private Command     m_boxOkCmd;         // The OK command for import box URL
    private Command     m_boxCancelCmd;     // The Cancel command for import box URL
    private Command     m_settingsCmd;      // The show settings command
    private Command     m_helpCmd;          // The show help
    private Command     m_manageBkmrk;      // The manage bookmarks command
    private Command     m_updateAllCmd;     // The update all command
    private Command     m_updateAllModCmd;  // The update all modified command
    
    // The controller of the application
    private Controller m_controller;
    private int citemLnkNbr = -1;
    private int citemEnclNbr = -1;
    private RssItunesItem citem = null;
    private String m_platformURL;           // Platform request URL
	//#ifdef DLOGGING
//@    private javax.microedition.lcdui.Form m_debug;
//@    private Logger logger;
	//#endif
    
    public RssReaderMIDlet() {
        m_display = Display.getDisplay(this);
        
		//#ifdef DTESTUI
//@		TestOutput.init(System.out, "UTF-8");
		//#endif

		//#ifdef DLOGGING
//@		try {
//@			LogManager.getLogManager().readConfiguration(this);
//@			/* Must be here as ResourceProviderME uses logging. */
//@			/* Loading items... */
//@			showLoadingFormRsc("text.l.items", null);
//@			logger = Logger.getLogger("RssReaderMIDlet");
//@			for (Enumeration eHandlers = logger.getParent().getHandlers().elements();
//@					eHandlers.hasMoreElements();) {
//@				Object ohandler = eHandlers.nextElement();
//@				if (ohandler instanceof FormHandler) {
//@					m_debug = ((FormHandler)ohandler).getForm();
//@					logger.finest("form=" + m_debug);
//@				}
//@			}
//@			logger = Logger.getLogger("RssReaderMIDlet");
//@			logger.info("RssReaderMIDlet started.");
//@			logger.info("RssReaderMIDlet has form handler=" + (m_debug != null));
//@		} catch (Throwable t) {
//@			/* Must be here as ResourceProviderME uses logging. */
//@			/* Loading items... */
//@			showLoadingFormRsc("text.l.items", null);
//@			m_loadForm.appendMsg("Error initiating logging " +
//@					t.getClass().getName() + "," + t.getMessage());
//@			m_loadForm.addExc(t);
//@			String [] msgs = LogManager.getLogManager().getStartMsgs();
//@			m_loadForm.appendMsg("msgs.length" + msgs.length);
//@			for (int ic = 0; ic < msgs.length; ic++) {
//@				m_loadForm.appendMsg(msgs[ic]);
//@			}
//@			System.out.println("Error initiating logging" + t);
//@			t.printStackTrace();
//@			return;
//@		}
		//#endif

		try {

			/** Initialize controller */
			m_controller = new Controller( this );
			
			/* Loading items... */
			if (m_loadForm == null) {
				showLoadingFormRsc("text.l.items", null);
			}

			m_appSettings = RssReaderSettings.getInstance(this);
			if (m_appSettings.getLoadExc() != null) {
				recordExcForm("Error while loading settings.",
							m_appSettings.getLoadExc());
			} else {
				m_itunesEnabled = m_appSettings.getItunesEnabled();
			}

			/** Initialize commands */
			//#ifdef DTESTUI
//@			/* Test headers/items */
//@			m_testRssCmd        = UiUtil.getCmdRsc("cmd.t.hdr", Command.SCREEN,
//@					9);
//@			/* Test bookmarks shown */
//@			m_testBMCmd         = UiUtil.getCmdRsc("cmd.t.bmk", Command.SCREEN,
//@					9);
//@			/* Test go back to last */
//@			m_testRtnCmd        = UiUtil.getCmdRsc("cmd.t.gb", Command.SCREEN, 10);
			//#endif
			m_backCommand       = UiUtil.getCmdRsc("cmd.back", Command.BACK, 1);
			m_backBookmark       = UiUtil.getCmdRsc("cmd.back", Command.BACK, 1);
			m_exitCommand       = UiUtil.getCmdRsc("cmd.exit", Command.EXIT, 14);
			/* Save without exit */
			m_saveCommand       = UiUtil.getCmdRsc("cmd.sve", Command.SCREEN, 10);
			//#ifdef DTEST
//@			/* Quit and delete RMS */
//@			m_testClearCmd = UiUtil.getCmdRsc("cmd.q.del", Command.SCREEN, 15);
			//#endif
			/* Add new feed */
			m_addNewBookmark    = UiUtil.getCmdRsc("cmd.a.fd", Command.SCREEN, 2);
			/* Open feed */
			m_openBookmark      = UiUtil.getCmdRsc("cmd.o.fd", Command.SCREEN, 1);
			/* River of news */
			m_readUnreadItems   = UiUtil.getCmdRsc("cmd.rvr", Command.SCREEN, 3);
			/* Edit feed */
			m_editBookmark      = UiUtil.getCmdRsc("cmd.e.fd", Command.SCREEN, 4);
			/* Delete feed */
			m_delBookmark       = UiUtil.getCmdRsc("cmd.d.fd", Command.SCREEN, 5);
			//#ifdef DMIDP20
			/* Open link */
			m_openLinkCmd       = UiUtil.getCmdRsc("cmd.o.lk", Command.SCREEN, 2);
			/* Open enclosure */
			m_openEnclosureCmd  = UiUtil.getCmdRsc("cmd.o.en", Command.SCREEN, 3);
			//#endif
			/* Copy link */
			m_copyLinkCmd       = UiUtil.getCmdRsc("cmd.c.lk", Command.SCREEN, 4);
			/* Copy enclosure */
			m_copyEnclosureCmd  = UiUtil.getCmdRsc("cmd.c.en", Command.SCREEN, 5);
			/* Item help */
			m_rssItemHelpCmd  = UiUtil.getCmdRsc("cmd.help", Command.HELP, 6);
			/* Import feeds */
			m_importFeedListCmd = UiUtil.getCmdRsc("cmd.im.fd", Command.SCREEN, 6);
			//#ifdef DTEST
//@			/* Import current feeds */
//@			m_importCurrFeedListCmd = UiUtil.getCmdRsc("cmd.im.cfd",
//@					Command.SCREEN, 6);
			//#endif
			m_boxOkCmd          = UiUtil.getCmdRsc("cmd.ok", Command.OK, 1);
			m_boxCancelCmd      = UiUtil.getCmdRsc("cmd.cancel", Command.CANCEL, 2);
			/* Settings */
			m_settingsCmd       = UiUtil.getCmdRsc("cmd.set", Command.SCREEN, 11);
			m_helpCmd           = UiUtil.getCmdRsc("cmd.help", Command.HELP, 13);
			/* Manage bookmarks */
			m_manageBkmrk       = UiUtil.getCmdRsc("cmd.m.bk", Command.SCREEN, 2);
			/* Update all */
			m_updateAllCmd      = UiUtil.getCmdRsc("cmd.ua", Command.SCREEN, 8);
			/* Update modified all */
			m_updateAllModCmd   = UiUtil.getCmdRsc("cmd.uma", Command.SCREEN, 9);
			//#ifdef DTESTUI
//@			/* Testing Form */
//@			m_testEncCmd        = UiUtil.getCmdRsc("cmd.tf", Command.SCREEN, 4);
			//#endif

		//#ifdef DLOGGING
//@			/* Debug Log */
//@			m_debugCmd          = UiUtil.getCmdRsc("cmd.dbl", Command.SCREEN, 4);
//@			/* Clear */
//@			m_clearDebugCmd     = UiUtil.getCmdRsc("cmd.clr", Command.SCREEN, 1);
//@			m_backFrDebugCmd    = UiUtil.getCmdRsc("cmd.back", Command.BACK, 2);
		//#endif
			
			m_midlet = this;
			m_mainBmk = true;
			m_getPage = false;
			m_exit = false;
			m_stored = false;
			m_saving = false;
			m_openPage = false;
			m_getModPage = false;
			m_getSettingsForm = false;
			m_getHelpForm = false;
			m_getAddBMForm = false;
			m_getEditBMForm = false;
			m_platformReq = false;
			m_refreshAllFeeds = false;
			m_refreshUpdFeeds = false;
			m_getImportForm = false;
			m_getFile = false;
			m_curBookmark = -1;
			
			// To get proper initialization, need to 
			try {
				m_settings = Settings.getInstance(this);
				m_firstTime = !m_settings.isInitialized();
			} catch(Exception e) {
				System.err.println("Error while getting settings: " + e.toString());
			}

			//#ifdef DLOGGING
//@			if (m_appSettings.getLogLevel().length() == 0) {
//@				m_appSettings.setLogLevel(
//@						logger.getParent().getLevel().getName());
//@			} else {
//@				logger.getParent().setLevel(
//@				Level.parse(m_appSettings.getLogLevel()));
//@			}
//@			fineLoggable = logger.isLoggable(Level.FINE);
//@			logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
//@			finestLoggable = logger.isLoggable(Level.FINEST);
//@			logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
			//#endif

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
			
			/** Initialize thread for http connection operations */
			m_process = true;
			//#ifdef DCLDCV11
//@			m_netThread = new Thread(this, "RssReaderMIDlet");
			//#else
			m_netThread = new Thread(this);
			//#endif
			m_netThread.start();

		}catch(Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("RssReaderMIDlet constructor ", t);
			//#endif
			/** Error while executing constructor */
			System.out.println("RssReaderMIDlet constructor " + t.getMessage());
			t.printStackTrace();
			m_loadForm.appendMsg("Internal error starting applicaiton.");
            m_loadForm.addExc(t);
		}
    }
    
	/* Initialize the forms that are not dynamic. */
	final private void initForms() {
		try {
			/** Initialize GUI items */
			initializeBookmarkList();
			//initializeLoadingForm();
			//#ifdef DLOGGING
//@			if (m_debug != null) {
//@				initializeDebugForm();
//@			}
			//#endif
			//#ifdef DTEST
//@			System.gc();
//@			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			//#ifdef DTESTUI
//@			m_testingForm = new TestingForm(this);
			//#endif
			//#ifdef DTEST
//@			System.gc();
//@			System.out.println("TestingForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			
			if( m_firstTime ) {
				try {
					m_firstTime = false;
					// Set Max item count to default so that it is initialized.
					m_appSettings.setMaximumItemCountInFeed(
							m_appSettings.getMaximumItemCountInFeed());
					saveBkMrkSettings(true, true, "label.init.d", false);
					Alert m_about = HelpForm.getAbout(this);
					if (m_loadForm.hasExc()) {
						setCurrent( m_about, m_loadForm );
					} else {
						setCurrent( m_about, m_bookmarkList );
					}
				} catch(RecordStoreFullException e) {
					/* Error while storing settings. */
					recordExcFormFinRsc("exc.sv.set", e);
				} catch(Exception e) {
					/* Internal error while storing settings. */
					recordExcFormFin("exc.int.set", e);
				}
			} else {
				if (m_loadForm.hasExc()) {
					recordFin();
					setCurrent( m_loadForm );
				} else {
					setCurrent( m_bookmarkList );
				}
			}

		}catch(Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("initForms ", t);
			//#endif
			/** Error while initializing forms */
			System.out.println("initForms " + t.getMessage());
			t.printStackTrace();
		}
		//#ifdef DTEST
//@		System.gc();
//@		System.out.println("Initial used memory size=" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L) + "kb");
		//#endif
    }
    
    /** Get application settings */
    final public RssReaderSettings getSettings() {
        return m_appSettings;
    }
    
    /** Show bookmark list */
    final public void showBookmarkList() {
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("before m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		m_itunesEnabled = m_appSettings.getItunesEnabled();
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("after m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		setCurrent( m_bookmarkList );
    }
    
    /** Load bookmarks from record store */
    final private void initializeBookmarkList() {
		//#ifdef DTEST
//@		System.gc();
//@		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		Gauge gauge = new Gauge(ResourceProviderME.get("label.init.b"),
				false, m_settings.MAX_REGIONS, 0);
		int pl = m_loadForm.append(gauge);
        try {
            m_bookmarkList = new PromptList(this, "Bookmarks", List.IMPLICIT);
			updBookmarkList();
            m_bookmarkList.setCommandListener( this );
			//#ifdef DTEST
//@			System.gc();
//@			System.out.println("empty bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
            
            int i = 1;
            
            m_rssFeeds = new Hashtable();
			for (int ic = 1; ic < m_settings.MAX_REGIONS; ic++) {
				boolean stop = false;
				final String vers = m_settings.getStringProperty(ic,
						m_settings.SETTINGS_NAME, "");
				final boolean firstSettings =
					 vers.equals(m_settings.FIRST_SETTINGS_VERS);
				final boolean itunesCapable = ((vers.length() > 0) &&
					 (vers.compareTo(m_settings.ITUNES_CAPABLE_VERS) >= 0));
				final boolean latestSettings =
					 vers.equals(m_settings.LATEST_VERS);
				final char feedSeparator =
					latestSettings ? CFEED_SEPARATOR : OLD_FEED_SEPARATOR;
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("Settings region,vers,firstSettings,itunescapable,latestSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + latestSettings);}
				//#endif
				//#ifdef DTEST
//@				if (m_debugOutput) System.out.println("Settings region,vers,firstSettings,itunescapable,latestSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + latestSettings);
				//#endif
				String bms = m_settings.getStringProperty(ic, "bookmarks", "");
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("bms.length()=" + bms.length());}
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
//@							RssFeed bm1 = new CompatibilityRssFeed1( part );
//@							RssItunesFeed bm = new RssItunesFeed( bm1 );
							//#elifdef DCOMPATIBILITY2
//@							RssFeed bm2 = new CompatibilityRssFeed2( part );
//@							RssItunesFeed bm = new RssItunesFeed( bm2 );
							//#elifdef DCOMPATIBILITY3
//@							RssItunesFeed bm2 =
//@								CompatibilityRssItunesFeed3.deserialize3(
//@								true, part );
//@							RssItunesFeed bm = new RssItunesFeed( bm2 );
							//#else
							RssItunesFeed bm;
							if (itunesCapable) {
								bm = RssItunesFeed.deserialize( true, part );
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
			//#ifdef DTEST
//@			System.gc();
//@			System.out.println("full bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
		} catch(CauseRecStoreException e) {
			/* Database error while loading saved feeds. */
			recordExcFormFin("exc.dbe.sv", e);
        } catch(CauseMemoryException e) {
			System.gc();
			/* Free memory by getting rid of items. */
			freeFeedItems();
			recordExcFormFin("Out of memory while loading saved feeds.", e);
        } catch(Exception e) {
			recordExcFormFin("Internal error while loading saved feeds.", e);
        } catch(OutOfMemoryError e) {
			System.gc();
			/* Free memory by getting rid of items. */
			freeFeedItems();
			recordExcFormFin("Out of memory while loading saved feeds.", e);
		} catch(Throwable t) {
			recordExcFormFin("Internal error while loading saved feeds.", t);
		} finally {
			m_loadForm.addStartCmd( m_bookmarkList );
			if (pl >= 0) {
				m_loadForm.delete(pl);
			}
			// Do this here in case load of settings failed.
			// Reset internal region to 0.
			m_settings.getStringProperty("bookmarks", "");
		}
    }
    
  /**
   * Update/initialize the bookmarkList title and commands
   *
   */
	final private void updBookmarkList() {
		Command[] allCmds = {m_addNewBookmark, m_openBookmark, m_backBookmark
			,m_readUnreadItems, m_editBookmark, m_delBookmark, m_manageBkmrk
			,m_importFeedListCmd
		//#ifdef DTEST
//@			,m_importCurrFeedListCmd
		//#endif
			,m_updateAllCmd, m_updateAllModCmd, m_saveCommand, m_settingsCmd
			,m_helpCmd ,m_exitCommand
		//#ifdef DTEST
//@			,m_testClearCmd
		//#endif
		//#ifdef DTESTUI
//@			,m_testBMCmd, m_testRtnCmd, m_testEncCmd
		//#endif
		//#ifdef DLOGGING
//@			,m_debugCmd
		//#endif
		};
		Command[] mainCmds = {m_manageBkmrk, m_openBookmark
			,m_manageBkmrk, m_readUnreadItems
			,m_updateAllCmd, m_updateAllModCmd, m_saveCommand, m_settingsCmd
			,m_helpCmd ,m_exitCommand
		//#ifdef DTEST
//@			,m_testClearCmd
		//#endif
		//#ifdef DTESTUI
//@			,m_testBMCmd, m_testRtnCmd, m_testEncCmd
		//#endif
		//#ifdef DLOGGING
//@			,m_debugCmd
		//#endif
		};
		Command[] manageCmds = {m_addNewBookmark, m_backBookmark
			,m_openBookmark, m_readUnreadItems, m_editBookmark, m_delBookmark
			,m_importFeedListCmd
		//#ifdef DTEST
//@			,m_importCurrFeedListCmd
		//#endif
			,m_saveCommand, m_helpCmd
		//#ifdef DTESTUI
//@			,m_testBMCmd, m_testRtnCmd, m_testEncCmd
		//#endif
			,m_exitCommand
		};
		String[] mainPrompts = {null, null
			,null, null
			,null, null, null, null
			,null ,"text.w.exit"
		//#ifdef DTEST
//@			,"text.w.clear"
		//#endif
		//#ifdef DTESTUI
//@			,null, null, null
		//#endif
		//#ifdef DLOGGING
//@			,null
		//#endif
		};
		String[] managePrompts = {null, null
			,null ,null, null, "text.w.del"
			,null 
		//#ifdef DTEST
//@			,null
		//#endif
			,null, null
		//#ifdef DTESTUI
//@			,null, null, null
		//#endif
			,"text.w.exit"
		};
		Command[] cmds = (m_mainBmk ? mainCmds : manageCmds);
		String[] prompts = (m_mainBmk ? mainPrompts : managePrompts);
		if (cmds.length != prompts.length) {
			Exception e = new Exception("Error cmds and prompts settings wrong " +
					cmds.length + "!=" + prompts.length);
			//#ifdef DLOGGING
//@			logger.severe(e.getMessage(), e);
			//#endif
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		UiUtil.delCmds(m_bookmarkList, allCmds);
		for (int ic = 0; ic < cmds.length; ic++) {
			if (cmds[ic] != null) {
				if (prompts[ic] != null) {
					m_bookmarkList.addPromptCommand( cmds[ic],
							ResourceProviderME.get(prompts[ic]) );
				} else {
					m_bookmarkList.addCommand( cmds[ic] );
				}
			}
		}
		m_bookmarkList.setTitle(ResourceProviderME.get((m_mainBmk ?
						"title.book" : "title.m.book")));
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
    }
    
    /** Show loading form */
    final public void showLoadingFormRsc(final String key,
									   Displayable disp) {
		initializeLoadingForm(ResourceProviderME.get(key), disp);
		setCurrent( m_loadForm );
	}

    /** Set title and addmessage for loading form */
    final public void setLoadingFinished(final String title, String msg) {
		if (title != null) {
			m_loadForm.setTitle(title);
		}
		if (msg != null) {
			m_loadForm.appendMsg(msg);
		}
    }
    
    /** Initialize import form */
    
    /** Initialize URL text Box */
    final private void initializeURLBox(final String url,
			CommandListener cmdl) {
		m_boxURL = new TextBox("URL", url,
								256, TextField.URL);
        m_boxURL.addCommand( m_boxOkCmd );
        m_boxURL.addCommand( m_boxCancelCmd );
        m_boxURL.setCommandListener(cmdl);
    }
    
	//#ifdef DLOGGING
//@    final public void initializeDebugForm() {
//@        m_debug.addCommand( m_backFrDebugCmd );
//@        m_debug.addCommand( m_clearDebugCmd );
//@        m_debug.setCommandListener(this);
//@	}
	//#endif

    /** Run method is used to get RSS feed with HttpConnection */
    public void run(){
        /* Use networking if necessary */
        long lngStart;
        long lngTimeTaken;
        while(m_process) {
            try {
				// Initialize bookmarks here since it does some work.
				if (m_bookmarkList == null) {
					synchronized (this) {
						if (m_bookmarkList == null) {
							initForms();
						}
					}
				}

				//#ifdef DTESTUI
//@				// If there are headers, and the header index is >= 0,
//@				// open the header so that it's items can be listed
//@				// with test UI classes.
//@				// Need to change the selection to match the m_headerIndex.
//@				if (m_headerNext && (m_headerIndex >= 0) &&
//@						(m_headerTestList != null) &&
//@				    (m_headerIndex < m_headerTestList.size()) &&
//@					(m_display.getCurrent() == m_headerTestList)) {
//@					m_headerNext = false;
//@					if (m_headerTestList.getSelectedIndex() >= 0) {
//@						m_headerTestList.setSelectedIndex(
//@								m_headerTestList.getSelectedIndex(), false);
//@					}
//@					m_headerTestList.setSelectedIndex(m_headerIndex, true);
//@					m_headerTestList.commandAction(List.SELECT_COMMAND,
//@							m_headerTestList);
//@				}
//@				// After intializing the form (which was already logged by
//@				// testui classes), simulate the back command
//@				if (m_itemNext && (m_headerIndex >= 0) &&
//@						(m_headerTestList != null) &&
//@					(m_headerIndex < m_headerTestList.size()) &&
//@					(m_display.getCurrent() == m_itemForm )) {
//@					m_itemNext = false;
//@					commandAction( m_backCommand, m_itemForm );
//@					m_headerIndex++;
//@					if (m_headerIndex >= m_headerTestList.size()) {
//@						System.out.println("Test UI Test Rss items last");
//@						m_headerIndex = -1;
//@					}
//@				}
				//#endif

				// Open existing bookmark and show headers (items).
                if( m_openPage || m_getPage || m_getModPage ) {
                    try {
						if (m_openPage) {
							final RssItunesFeed feed =
								(RssItunesFeed)m_rssFeeds.get(
									m_bookmarkList.getString(m_curBookmark));
							m_curRssParser = new RssFeedParser( feed );
						} else {
							/** Get RSS feed */
							final int maxItemCount =
								m_appSettings.getMaximumItemCountInFeed();
							m_curRssParser.parseRssFeed( m_getModPage,
									maxItemCount );
						}
						//#ifdef DTEST
//@						System.gc();
//@						long beginMem = Runtime.getRuntime().freeMemory();
						//#endif
						final HeaderList hdrList = new HeaderList(this,
								m_curRssParser.getRssFeed());
						//#ifdef DTEST
//@						System.gc();
//@						System.out.println("headerList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
						//#endif
                        hdrList.fillHeadersList();
                        setCurrent( hdrList );
						//#ifdef DTEST
//@						hdrList.testFeed();
						//#endif
                    }catch(CauseMemoryException e) {
						System.gc();
						if ((m_curRssParser != null) &&
								(m_curRssParser.getRssFeed() != null)) {
							m_curRssParser.getRssFeed().setItems(new Vector());
						}
						recordExcFormFinRsc(
                        		/* \nOut of memory loading/parsing feed on:\n */
                        		(m_openPage ? "exc.om.ld" : "exc.om.pse"),
                                m_curRssParser.getRssFeed().getUrl(), e);
                    }catch(Exception e) {
						/* Error loading/parsing  feed on:\n \1 */
						recordExcFormFinRsc(
                        		(m_openPage ? "exc.er.ld" : "exc.er.pse"),
                                m_curRssParser.getRssFeed().getUrl(), e);

                    }catch(OutOfMemoryError e) {
						System.gc();
						if ((m_curRssParser != null) &&
								(m_curRssParser.getRssFeed() != null)) {
							m_curRssParser.getRssFeed().setItems(new Vector());
						}
						recordExcFormFinRsc(
                        		/* Out of memory loading/parsing feed on:\n */
                        		(m_openPage ? "exc.om.ld" : "exc.om.pse"),
                                m_curRssParser.getRssFeed().getUrl(), e);
                    }catch(Throwable t) {
						recordExcFormFinRsc(
                        		/* Internal error loading/parsing feed on:\n */
                        		(m_openPage ? "exc.int.ld" : "exc.int.pse"),
                                m_curRssParser.getRssFeed().getUrl(), t);
					} finally {
						m_getPage = false;
						m_openPage = false;
						m_getModPage = false;
                    }
                }

				/* Handle going to settings form. */
                if( m_getSettingsForm ) {
					m_getSettingsForm = false;
                    try{
						//#ifdef DTEST
//@						System.gc();
//@						long beginMem = Runtime.getRuntime().freeMemory();
						//#endif
						final SettingsForm settingsForm = new SettingsForm(this);
						settingsForm.updateForm();
						//#ifdef DTEST
//@						System.gc();
//@						System.out.println("SettingsForm size=" +
//@								(beginMem - Runtime.getRuntime().freeMemory()));
						//#endif
						setCurrent( settingsForm );
                    } catch(OutOfMemoryError t) {
						System.gc();
						/* \nOut Of Memory Error loading settings form */
						recordExcFormFinRsc("exc.om.set", t);
                    } catch(Throwable t) {
						/* \nInternal error loading settings form */
						recordExcFormFinRsc("exc.int.set", t);
					}
				}

				/* Handle going to help form. */
                if( m_getHelpForm ) {
					m_getHelpForm = false;
                    try{
						final HelpForm helpForm = initializeHelp();
						setCurrent( helpForm );
                    } catch(OutOfMemoryError t) {
						System.gc();
						/* \nOut Of Memory Error loading help form */
						recordExcFormFinRsc("exc.om.bhlp", t);
                    } catch(Throwable t) {
						/* \nInternal error loading help form */
						recordExcFormFinRsc("exc.int.bhlp", t);
					}
				}

				/* Handle going to bookmark form. */
                if( m_getAddBMForm || m_getEditBMForm ) {
                    try{
						//#ifdef DTEST
//@						System.gc();
//@						long beginMem = Runtime.getRuntime().freeMemory();
						//#endif
						BMForm bmForm = new BMForm(m_getAddBMForm);
						//#ifdef DTEST
//@						System.gc();
//@						System.out.println("BMForm size=" +
//@								(beginMem - Runtime.getRuntime().freeMemory()));
						//#endif
						if (m_getEditBMForm) {
							final RssItunesFeed bm = (RssItunesFeed)m_rssFeeds.get(
									m_bookmarkList.getString(m_curBookmark));
							bmForm.updateBM(bm);
						}
						setCurrent( bmForm );
					} catch(OutOfMemoryError t) {
						System.gc();
						/* \nOut Of Memory Error loading bookmark form */
						recordExcFormFinRsc("exc.om.bmk", t);
					} catch(Throwable t) {
						/* \nInternal error loading bookmark form */
						recordExcFormFinRsc("exc.int.bmk", t);
					} finally {
						m_getAddBMForm = false;
						m_getEditBMForm = false;
					}
				}

                if( m_refreshAllFeeds || m_refreshUpdFeeds ) {
					/* Updating all (modified) feeds...*/
					Gauge gauge = new Gauge(ResourceProviderME.get(
								m_refreshAllFeeds ? "text.ua.feed" :
								"text.um.feed"),
							false, m_rssFeeds.size(), 0);
					int pl = m_loadForm.append(gauge);
                    try{
						boolean errFound = false;
                        final int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
                        Enumeration feedEnum = m_rssFeeds.elements();
						int ic = 1;
                        while(feedEnum.hasMoreElements()) {
                            RssItunesFeed feed = (RssItunesFeed)feedEnum.nextElement();
                            try{
                                m_loadForm.appendMsg(feed.getName() + "...");
                                RssFeedParser parser = new RssFeedParser( feed );
                                parser.parseRssFeed( m_refreshUpdFeeds,
										maxItemCount);
                                m_loadForm.appendMsg("ok\n");
                            } catch(CauseMemoryException ex) {
								throw ex;
                            } catch(Exception ex) {
								CauseException ce = new CauseException(
										"Error parsing feed " + feed.getName(),
										ex);
								m_loadForm.addExc(ce);
								//#ifdef DLOGGING
//@								logger.severe(ce.getMessage(), ex);
								//#endif
                                m_loadForm.appendMsg("Error\n");
								System.out.println(ce.getMessage());
								errFound = true;
                            }
							gauge.setValue(ic);
							ic++;
                        }
						if (errFound) {
							setLoadingFinished(
									/* Finished with one or more exceptions */
									/* or errors below. */
									ResourceProviderME.get("text.fin.errb"),
									/* Updating finished with one or more */
									/* exceptions or errors above*/
									ResourceProviderME.get("text.fin.erra"));
							setCurrent( m_loadForm );
						} else {
							setLoadingFinished("Updating finished",
									"Updating finished use back to return.");
							setCurrent( m_bookmarkList );
						}
						pl = -1;
                    } catch(CauseMemoryException ex) {
						recordExcForm("Out Of Memory Error parsing feeds", ex);
						freeFeedItems();
						recordFin();
                    } catch(Exception ex) {
						recordExcFormFin("Error parsing feeds", ex);
                    } catch(OutOfMemoryError ex) {
						System.gc();
						recordExcForm("Out Of Memory Error parsing feeds", ex);
						freeFeedItems();
						recordFin();
                    } catch(Throwable t) {
						recordExcFormFin("Internal error parsing feeds", t);
					} finally {
						if (pl >= 0) {
							m_loadForm.delete(pl);
						}
						m_refreshAllFeeds = false;
						m_refreshUpdFeeds = false;
					}
                }

				// Go to import feed form
				if( m_getImportForm ) {
					try {
						//#ifdef DTEST
//@						System.gc();
//@						long beginMem = Runtime.getRuntime().freeMemory();
						//#endif
						ImportFeedsForm importFeedsForm;
						//#ifdef DTEST
//@						if (m_getTestImportForm) {
//@							RssItunesFeed bm = (RssItunesFeed)m_rssFeeds.get(
//@									m_bookmarkList.getString(m_curBookmark));
//@							importFeedsForm = new ImportFeedsForm(bm.getUrl());
//@						} else
						//#endif
							importFeedsForm = new ImportFeedsForm(m_appSettings.getImportUrl());
						//#ifdef DTEST
//@						System.gc();
//@						System.out.println("ImportForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
						//#endif
						setCurrent( importFeedsForm );
                    } catch(Exception ex) {
						recordExcFormFin("Error loading import form\n", ex);
                    } catch(OutOfMemoryError ex) {
						System.gc();
						recordExcFormFin("Out Of Memory loading import form\n",
                                ex);
                    } catch(Throwable t) {
						recordExcFormFin("Internal loading import form\n", t);
					} finally {
						m_getImportForm = false;
						//#ifdef DTEST
//@						m_getTestImportForm = false;
						//#endif
					}
				}

				//#ifdef DTESTUI
//@				if ((m_bookmarkIndex < m_bookmarkList.size()) &&
//@				    (m_bookmarkIndex >= 0)) {
//@					if (m_bookmarkList.getSelectedIndex() >= 0) {
//@						m_bookmarkList.setSelectedIndex(
//@								m_bookmarkList.getSelectedIndex(), false);
//@					}
//@					m_bookmarkList.setSelectedIndex(m_bookmarkIndex, true);
//@					commandAction(m_editBookmark, m_bookmarkList);
//@					m_bookmarkIndex++;
//@					if (m_bookmarkIndex >= m_bookmarkList.size()) {
//@						m_bookmarkIndex = -1;
//@						System.out.println("Test UI Test Rss feeds last");
//@					}
//@				}
//@
				//#endif

				//#ifdef DJSR75
//@				/* Find files in the file system to get for bookmark or
//@				   import from. */
//@                if( m_getFile ) {
//@					if( m_getFile ) {
//@						try {
//@							final KFileSelectorMgr fileSelectorMgr =
//@								new KFileSelectorMgr();
//@							fileSelectorMgr.doLaunchSelector(this,
//@										m_fileRtnForm, m_fileURL);
//@						} catch(OutOfMemoryError ex) {
//@							System.gc();
//@							/* Out Of Memory Error getting file form. */
//@							recordExcFormFinRsc("exc.om.flf", ex);
//@						} catch (Throwable t) {
//@							/* Internal error getting file form. */
//@							recordExcFormFinRsc("exc.int.flf", t);
//@						} finally {
//@							m_getFile = false;
//@						}
//@					}
//@				}
				//#endif

				/* Handle going to link (platform request.). */
				//#ifdef DMIDP20
				if (m_platformReq) {
					try {

					if( super.platformRequest(m_platformURL) ) {
						m_exit = true;
					} else {
						setCurrent( m_itemRrnForm );
					}
				} catch (ConnectionNotFoundException e) {
					//#ifdef DLOGGING
//@					logger.severe("Error opening link " + m_platformURL, e);
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

				/* Sort the read or unread items. */
				if ( m_runNews ) {
					try {
						AllNewsList unreadHeaderList = new AllNewsList(this,
							m_bookmarkList, m_rssFeeds,
									m_appSettings.getMarkUnreadItems() ?
									m_unreadImage : null);
						//#ifdef DTESTUI
//@						m_unreadHeaderTestList = unreadHeaderList;
						//#endif
						unreadHeaderList.initializeUnreadHhdrsList();
						unreadHeaderList.sortUnreadItems( true,
								m_bookmarkList, m_rssFeeds );
						setCurrent( unreadHeaderList );
                    }catch(OutOfMemoryError t) {
						System.gc();
						recordExcFormFin("\nOut Of Memory Error sorting items", t);
                    }catch(Throwable t) {
						recordExcFormFin("\nInternal error sorting items", t);
					} finally {
						m_runNews = false;
					}
				}

				if ( m_exit || m_saveBookmarks ) {
					try {
						//#ifdef DLOGGING
//@						if (fineLoggable) {logger.fine("m_exit,m_saveBookmarks=" + m_exit + "," + m_saveBookmarks);}
						//#endif
						storeSettings(true, true, m_exit);
						if (m_loadForm.hasExc()) {
							if (m_exit) {
								m_loadForm.addQuit();
							}
							setCurrent( m_loadForm );
						} else if (m_exit) {
							try {
								destroyApp(true);
							} catch (MIDletStateChangeException e) {
								//#ifdef DLOGGING
//@								if (fineLoggable) {logger.fine("MIDletStateChangeException=" + e.getMessage());}
								//#endif
							}
							m_process = false;
							super.notifyDestroyed();
							m_exit = false;
						} else {
							setCurrent( m_bookmarkList );
						}
					} finally {
						m_exit = false;
						m_saveBookmarks = false;
					}
				}

                lngStart = System.currentTimeMillis();
                lngTimeTaken = System.currentTimeMillis()-lngStart;
                if(lngTimeTaken<100L) {
					synchronized(this) {
						if (!m_needWakeup) {
							super.wait(75L-lngTimeTaken);
						}
						m_needWakeup = false;
					}
				}
            } catch (InterruptedException e) {
                break;
            } catch (Throwable t) {
				try {
					if (m_loadForm == null) {
						synchronized(this) {
							if (m_loadForm == null) {
								/* Processing... */
								showLoadingFormRsc("text.proc", m_bookmarkList);
							}
						}
					}
					recordExcForm("Internal error while processing", t);
				} catch (Throwable e) {
					t.printStackTrace();
					final Alert internalAlert = new Alert(
							/* Internal error */
							ResourceProviderME.get("exc.int.err"),
							/* Internal error while processing */
							ResourceProviderME.get("exc.int.proc"),
							null,
							AlertType.ERROR);
					internalAlert.setTimeout(Alert.FOREVER);
					setCurrent( internalAlert );
				}
            }
        }
    }
	
	/* Free memory by getting rid of items. */
	final private void freeFeedItems() {
		try {
			m_loadForm.appendMsg("Trying to free memory.");
			Enumeration feedEnum = m_rssFeeds.elements();
			while(feedEnum.hasMoreElements()) {
				RssItunesFeed feed =
					(RssItunesFeed)feedEnum.nextElement();
				feed.setItems(null);
				feed.setItems(new Vector());
			}
		} catch(OutOfMemoryError ex2) {
			recordExcForm("Out Of Memory tyring to free memory.", ex2);
		}
	}

    /**
	 * Create help form.
	 * @author  Irving Bunton
	 * @version 1.0
	 */
	final private HelpForm initializeHelp() {
		//#ifdef DTEST
//@		System.gc();
//@		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		final HelpForm helpForm = new HelpForm(this,
				m_bookmarkList);
		/* To start, go to add feed or import feed */
		helpForm.appendRsc(m_mainBmk ? "text.m.help" : "text.bk.help");
		if (m_mainBmk) {
			/* Manage bookmarks. */
			helpForm.appendCmdHelpRsc(m_manageBkmrk, "text.mge.help");
			/* Update all feeds. */
			helpForm.appendCmdHelpRsc(m_updateAllCmd, "text.ua.help");
			/* Update modified feeds. */
			helpForm.appendCmdHelpRsc(m_updateAllModCmd, "text.um.help");
			/* Go to settings. */
			helpForm.appendCmdHelpRsc(m_settingsCmd, "text.set.help");
		} else {
			/* Go back to main bookmarks. */
			helpForm.appendCmdHelpRsc(m_backBookmark, "text.bbk.help");
		}
		/* Save without exiting. */
		helpForm.appendCmdHelpRsc(m_saveCommand, "text.save.help");
		/* Exit. */
		helpForm.appendCmdHelpRsc(m_exitCommand, "text.exit.help");
		//#ifdef DTEST
//@		System.gc();
//@		System.out.println("Main HelpForm size=" +
//@				(beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
		return helpForm;
	}

	/** Store the settings. */
	final private void storeSettings(final boolean saveHdr,
			final boolean saveItems, final boolean exitingApp) {
		m_saving = true;
		try {
			saveBkMrkSettings(saveHdr, saveItems, "label.save.d", m_exit);
		} catch (RecordStoreFullException e) {
			recordExcFormFinRsc(
					/* Unrecoverable error saving feeds again. */
					"exc.sv.ursf", e);
		} catch (Exception e) {
			recordExcFormFin(
					"Internal error saving feeds", e);
		} catch (OutOfMemoryError e) {
			recordExcFormFin(
					"Out of memory error saving feeds", e);
		} catch (Throwable e) {
			recordExcFormFin(
					"Internal error saving feeds", e);
		}
		m_stored = exitingApp;
		m_saving = false;
	}

	/* Record the exception in the loading form, log it and give std error. */
	final public void recordFin() {
		m_loadForm.setTitle("Finished with errors or esceptions below");
		m_loadForm.appendMsg("Finished with errors or esceptions above");
	}

	/* Record the exception in the loading form, log it and give std error. */
	final public void recordExcFormRsc(final String key, final Throwable e) {
		recordExcForm(ResourceProviderME.get(key), e);
	}

	/* Record the exception in the loading form, log it and give std error. */
	final public void recordExcForm(final String causeMsg, final Throwable e) {
		final CauseException ce = new CauseException(causeMsg, e);
		m_loadForm.addExc(ce);
		//#ifdef DLOGGING
//@		logger.severe(ce.getMessage(), e);
		//#endif
		/** Error while parsing RSS feed */
		System.out.println(e.getClass().getName() + " " + ce.getMessage());
		e.printStackTrace();
		m_loadForm.appendMsg(ce.getMessage());
		setCurrent( m_loadForm );
	}

	/* Record the exception in the loading form, log it and give std error. */
	final public void recordExcFormFinRsc(final String key, final Throwable e) {
		recordExcFormFin(ResourceProviderME.get(key), e);
	}

	/* Record the exception in the loading form, log it and give std error. */
	final public void recordExcFormFinRsc(final String key, final String parm,
			final Throwable e) {
		recordExcFormFin(ResourceProviderME.get(key, parm), e);
	}

	/* Record the exception in the loading form, log it and give std error. */
	final public void recordExcFormFin(final String causeMsg, final Throwable e) {
		recordExcForm(causeMsg, e);
		recordFin();
	}

	//#ifdef DMIDP20
	final public void setCurrentItem(Item item) {
		m_display.setCurrentItem(item);
		wakeUp();
	}
	//#endif

	/* Set current displayable and wake up the thread. */
	final public void setCurrent(Displayable disp) {

		//#ifdef DTESTUI
//@		String title = "";
//@		if (disp instanceof Form) {
//@			title = ((Form)disp).getTitle();
//@		} else if (disp instanceof List) {
//@			title = ((List)disp).getTitle();
//@		}
//@		System.out.println("Test UI setCurrent " + disp.getClass().getName() + "," + title);
		//#endif
		m_display.setCurrent( disp );
		wakeUp();
	}

	//#ifdef DTESTUI
//@	/* Get current displayable. */
//@	final public Displayable getCurrent() {
//@		return m_display.getCurrent();
//@	}
	//#endif

	/* Set current displayable and wake up the thread. */
	final public void setCurrent(Alert alert, Displayable disp) {
		m_display.setCurrent( alert, disp );
		wakeUp();
	}

	/* Notify us that we are finished. */
	final public void wakeUp() {
    
		synchronized(this) {
			m_needWakeup = true;
			super.notify();
		}
	}

    /** Show item form */
    final public void showItemForm() {
		setCurrent( m_itemForm );
    }
    
	//#ifdef DTESTUI
//@	/** Cause item form to go back to the prev form. */
//@    final public void backFrItemForm() {
//@		commandAction( m_backCommand, m_itemForm );
//@    }
//@    
//@    /** Show item form */
//@    final public boolean isItemForm() {
//@        return (m_display.getCurrent() == m_itemForm);
//@    }
	//#endif
    
    /** Initialize RSS item form */
    final public void initializeItemForm(final RssItunesFeed feed,
								   final RssItunesItem item,
								   List prevList) {
        System.out.println("Create new item form");
		String title = item.getTitle();
		boolean hasTitle = true;
		//#ifdef DTEST
//@		System.gc();
//@		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		if (title.length() == 0) {
			hasTitle = false;
			title = getItemDescription(item);
		}
		//#ifdef DTEST
//@		m_itemForm = new PromptForm( this, title );
		//#else
		m_itemForm = new Form( title );
		//#endif
		boolean pageEnabled = m_appSettings.getPageEnabled();
		int fontSize = pageEnabled ? getFontSize() : 0;
        m_itemForm.addCommand( m_backCommand );
		final String sienclosure = item.getEnclosure();
		String desc = item.getDescription();
		String descLabel;
		if (hasTitle) {
			if (desc.length()>0) {
				descLabel = title;
			} else {
				descLabel = "Title\n";
				desc = title;
			}
		} else {
			descLabel = "Description\n";
		}
		m_itemForm.append(getTextItem(pageEnabled, descLabel, desc, fontSize,
					m_itemForm,
					prevList));
		citem = item;
		if (m_itunesEnabled && (item.isItunes() || feed.isItunes())) {
			final String author = item.getAuthor();
			if (author.length() > 0) {
				m_itemForm.append(getTextItem(pageEnabled, "Author:", author,
							fontSize, m_itemForm, prevList));
			}
			final String subtitle = item.getSubtitle();
			if (subtitle.length() > 0) {
				m_itemForm.append(getTextItem(pageEnabled, "Subtitle:",
							subtitle, fontSize, m_itemForm, prevList));
			}
			final String summary = item.getSummary();
			if (summary.length() > 0) {
				m_itemForm.append(getTextItem(pageEnabled, "Summary:", summary,
							fontSize, m_itemForm, prevList));
			}
			final String duration = item.getDuration();
			if (duration.length() > 0) {
				m_itemForm.append(new StringItem("Duration:", duration));
			}
			String expLabel = "Explicit:";
			String explicit = item.getExplicit();
			if (explicit.equals(RssItunesItem.UNSPECIFIED)) {
				expLabel = "Feed explicit:";
				explicit = feed.getExplicit();
			}
			m_itemForm.append(new StringItem(expLabel, explicit));
		}
		String linkLabel = "Link:";
        String link = item.getLink();
		//#ifdef DITUNES
//@		if (link.length() == 0) {
//@			link = feed.getLink();
//@			linkLabel = "Feed link:";
//@		}
		//#endif
		if (link.length() > 0) {
			//#ifdef DMIDP20
			StringItem slink = new StringItem(linkLabel, link, Item.HYPERLINK);
			//#else
//@			StringItem slink = new StringItem(linkLabel, link);
			//#endif
			citemLnkNbr  = m_itemForm.append(slink);
		} else {
			citemLnkNbr  = -1;
		}
		if (sienclosure.length() > 0) {
			//#ifdef DMIDP20
			StringItem senclosure = new StringItem("Enclosure:", sienclosure,
													  Item.HYPERLINK);
			//#else
//@			StringItem senclosure = new StringItem("Enclosure:", sienclosure);
			//#endif
			citemEnclNbr = m_itemForm.append(senclosure);
		} else {
			citemEnclNbr  = -1;
		}
        
        // Add item's date if it is available
		String dateLabel = "Date:";
        Date itemDate = item.getDate();
		//#ifdef DITUNES
//@        if(itemDate==null) {
//@			itemDate = feed.getDate();
//@			dateLabel = "Feed date:";
//@		}
		//#endif
        if(itemDate!=null) {
            m_itemForm.append(new StringItem(dateLabel, itemDate.toString()));
        }

		m_itemRrnForm = prevList;
		//#ifdef DMIDP20
		if (link.length() > 0) {
			m_itemForm.addCommand( m_openLinkCmd );
		}
		if (sienclosure.length() > 0) {
			m_itemForm.addCommand( m_openEnclosureCmd );
		}
		//#endif
		if (link.length() > 0) {
			m_itemForm.addCommand( m_copyLinkCmd );
		}
		if (sienclosure.length() > 0) {
			m_itemForm.addCommand( m_copyEnclosureCmd );
		}
		m_itemForm.addCommand( m_rssItemHelpCmd );
		//#ifdef DTEST
//@		m_itemForm.addPromptCommand( m_testClearCmd,
//@					ResourceProviderME.get("text.w.q") );
		//#endif
        m_itemForm.setCommandListener( this );
		//#ifdef DTEST
//@		System.gc();
//@		System.out.println("itemForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
    }

	/* Get the font size. */
	final int getFontSize() {
		int fontSize;
		switch (m_appSettings.getFontSize()) {
			case 0:
				fontSize = Font.getDefaultFont().getSize();
				break;
			case 1:
				fontSize = Font.SIZE_SMALL;
				break;
			case 2:
				fontSize = Font.SIZE_MEDIUM;
				break;
			case 3:
				fontSize = Font.SIZE_LARGE;
				break;
			default:
				fontSize = Font.getDefaultFont().getSize();
				break;
		}
		return fontSize;
	}

	/** Get page custom item or StringItem if or not pageEnabled or
	    PageCustomItem gives an error. */
	final private Item getTextItem(boolean pageEnabled, String textLabel,
			String text, int fontSize, Form descForm, List prevList) {
		if (pageEnabled) {
			//#ifdef DMIDP20
			/* This is a custom item only present in MIDP 2.0 */
			try {
				return new PageCustomItem(textLabel,
							descForm.getWidth(), descForm.getHeight(),
							fontSize, text, prevList, this);
			} catch (Exception e) {
			//#endif
				return new StringItem(textLabel, text);
			//#ifdef DMIDP20
			}
			//#endif
		} else {
			return new StringItem(textLabel, text);
		}
	}

	/** Get the max words configured from the descritption. */
	final public String getItemDescription( final RssItunesItem item ) {
		final String [] parts = StringUtil.split(item.getDescription(), ' ');
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
    public void startApp() {
		if (!m_netThread.isAlive()) {
			m_process = true;
			try {
				//#ifdef DCLDCV11
//@				m_netThread = new Thread(this, "RssReaderMIDlet");
				//#else
				m_netThread = new Thread(this);
				//#endif
				m_netThread.start();
			} catch (Exception e) {
				System.err.println("Could not restart thread.");
				e.printStackTrace();
				//#ifdef DLOGGING
//@				logger.severe("Could not restart thread.", e);
				//#endif
			}
			//#ifdef DLOGGING
//@			logger.info("RssReaderMIDlet thread not started.  Started now.");
			//#endif
		}
    }
    
    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() {
		m_process = false;
		wakeUp();
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case we need to save the bookmarks/feeds:w
     */
    public void destroyApp(boolean unconditional)
		throws MIDletStateChangeException {
		//#ifdef DLOGGING
//@		logger.info("RecordStore.listRecordStores() != null=" + (RecordStore.listRecordStores() != null));
		//#endif
		if (!m_exit && !m_stored && !m_saving) {
			// Show that we are exiting.  Since the application is destroyed
			// this may cause an error.
			try {
				/* Exiting saving data... */
				showLoadingFormRsc("text.sav", m_bookmarkList);
			} catch (Throwable t) {
			}
			storeSettings(false, false, true);
		}
    	if (unconditional) {
			// If unconditional, we are to release all resources and stop
			// threads.
			m_process = false;
		}
    }
    
    /** Save bookmarks to record store
        releaseMemory use true if exiting as we do not need
		the rss feeds anymore, so we can save memory and avoid
		having extra memory around.  */
    final public void saveBookmarks(final boolean saveHdr,
			final boolean saveItems, final int region,
									boolean releaseMemory) {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("saveHdr,saveItems,region,releaseMemory=" + saveHdr + "," + saveItems + "," + region + "," + releaseMemory);}
		//#endif
		StringBuffer bookmarks = new StringBuffer();
		m_settings.setStringProperty("bookmarks", bookmarks.toString());
		m_settings.setBooleanProperty(m_settings.ITEMS_ENCODED, true);
		final int bsize = m_bookmarkList.size();
		if (bsize == 0) {
			return;
		}
		//#ifdef DTEST
//@		int storeTime = 0;
		//#endif
		final int bookRegion = region - 1;
		final int iparts = m_settings.MAX_REGIONS - 1;
		final int firstIx = bookRegion * bsize / iparts;
		final int endIx = (bookRegion + 1) * bsize / iparts - 1;
        try {
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("firstIx,endIx=" + firstIx + "," + endIx);}
			//#endif
			Vector vstored = new Vector();
			try {
				/** Try to save feeds including items */
				for( int i=firstIx; i<=endIx; i++) {
					final String name = m_bookmarkList.getString(i);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("i,name=" + i + "," + name);}
					//#endif
					if (!m_rssFeeds.containsKey( name )) {
						continue;
					}
					if( name.length()>0) {
						final RssItunesFeed rss =
							(RssItunesFeed)m_rssFeeds.get( name );
						//#ifdef DCOMPATIBILITY1
//@						CompatibilityRssFeed1 rss1 = new CompatibilityRssFeed1(rss);
						//#ifdef DTEST
//@						String prevStore = rss1.getStoreString( saveItems );
//@						RssItunesFeed nrss = new RssItunesFeed( false, true, true, prevStore );
//@						if (!rss1.equals(nrss)) {
							//#ifdef DLOGGING
//@							logger.severe("itunes store stings not backwards compatible i=" + i);
							//#endif
//@						}
//@						long beginStore = System.currentTimeMillis();
						//#endif
//@						bookmarks.append(rss1.getStoreString(saveItems));
						//#elifdef DCOMPATIBILITY2
//@						CompatibilityRssFeed2 rss2 = new CompatibilityRssFeed2(rss);
//@						final String prevStore = rss2.getStoreString(saveItems);
//@						bookmarks.append(prevStore);
						//#ifdef DTEST
//@						RssItunesFeed nrss = new RssItunesFeed(new RssFeed(
//@											false, true, prevStore ));
//@						if (!rss2.equals(nrss)) {
							//#ifdef DLOGGING
//@							logger.severe("itunes store stings not backwards compatible i=" + i);
							//#endif
//@						}
//@						long beginStore = System.currentTimeMillis();
						//#endif
						//#elifdef DCOMPATIBILITY3
//@						CompatibilityRssItunesFeed3 rss3 =
//@							new CompatibilityRssItunesFeed3(rss);
//@						final String prevStore = rss3.getStoreString(
//@								saveItems, true);
//@						bookmarks.append(prevStore);
						//#ifdef DTEST
//@						RssItunesFeed nrss = new RssItunesFeed(new RssFeed(
//@											false, true, prevStore ));
//@						if (!rss3.equals(nrss)) {
							//#ifdef DLOGGING
//@							logger.severe("itunes store stings not backwards compatible i=" + i);
							//#endif
//@						}
//@						long beginStore = System.currentTimeMillis();
						//#endif
						//#else
						/* This is where things are normally done when not */
						/* testing compatibility. */
						//#ifdef DTEST
//@						long beginStore = System.currentTimeMillis();
						//#endif
						bookmarks.append(rss.getStoreString(saveHdr, saveItems,
									true));
						//#endif
						//#ifdef DTEST
//@						storeTime += System.currentTimeMillis() - beginStore;
						//#endif
						//#ifdef DCOMPATIBILITY1
//@						bookmarks.append(OLD_FEED_SEPARATOR);
						//#elifdef DCOMPATIBILITY2
//@						bookmarks.append(OLD_FEED_SEPARATOR);
						//#elifdef DCOMPATIBILITY2
//@						bookmarks.append(OLD_FEED_SEPARATOR);
						//#elifdef DCOMPATIBILITY3
//@						bookmarks.append(OLD_FEED_SEPARATOR);
						//#else
						bookmarks.append(CFEED_SEPARATOR);
						//#endif
						if (releaseMemory) {
							vstored.addElement( name );
						}
					}
				}
			} catch(OutOfMemoryError error) {
				/* Don't release memory so that we can re-try. */
				releaseMemory = false;
				bookmarks.setLength(0);
				System.gc();
				recordExcForm("Out of memory error saving feeds.", error);
				throw error;
				
			} finally {
				if (releaseMemory) {
					final int vslen = vstored.size();
					for (int ic = 0; ic < vslen; ic++) {
						final RssItunesFeed rss =
							(RssItunesFeed)m_rssFeeds.get( 
						 (String)vstored.elementAt( ic ));
						rss.setItems(new Vector(0));
					}
				}
			}
			//#ifdef DTEST
//@			System.out.println("storeTime=" + storeTime);
			//#endif
            m_settings.setStringProperty("bookmarks",bookmarks.toString());
			//#ifdef DLOGGING
//@			if (fineLoggable) {logger.fine("bookmarks.length()=" + bookmarks.length());}
			//#endif
		} catch(OutOfMemoryError error) {
			throw error;
		} catch (Throwable t) {
            m_settings.setStringProperty("bookmarks", bookmarks.toString());
			//#ifdef DTEST
//@			System.out.println("storeTime=" + storeTime);
			//#endif
//#ifdef DLOGGING
//@			logger.severe("saveBookmarks could not save.", t);
//#endif
			System.out.println("saveBookmarks could not save." + t + " " +
					           t.getMessage());
			t.printStackTrace();
        }
    }

    /** Update RSS feed's headers */
    final private void updateHeaders(final boolean updMod, Displayable dispBack) {
		try {
			/* Updating feed... */
			showLoadingFormRsc("text.u.feed", dispBack);
			if(m_curRssParser.getRssFeed().getUrl().length()>0) {
				if (updMod) {
					m_getModPage = true;
				} else {
					m_getPage = true;
				}
			} else {
				m_loadForm.addExc(new Exception(
							"Feed has no URL cannot load."));
			}
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("updateHeaders could not update." + t + " " +
//@					           t.getMessage());
//#endif
			System.out.println("updateHeaders could not update." + t + " " +
					           t.getMessage());
		}
    }
    
    /** Update all RSS feeds */
    final private void updateAllHeaders(final boolean updModHdr) {
        /* Updating all or modified feeds... */
        showLoadingFormRsc((updModHdr ? "text.um.feed" : "text.ua.feed"),
				m_bookmarkList);
		if (updModHdr) {
			m_refreshUpdFeeds = true;
		} else {
			m_refreshAllFeeds = true;
		}
    }
    
	/* Set flag to show find files list.
	   fileRtnForm - Form to return to after file finished.
	   fileURL - Text field that has URL to put file URL into as well
	   			 as field to go back to if 2.0 is valid.
	*/
	final private void reqFindFiles( final Form fileRtnForm,
								     final TextField fileURL) {
		m_fileRtnForm = fileRtnForm;
		m_fileURL = fileURL;
		m_getFile = true;
	}

	/* Restore previous values. */
	final private void restorePrevValues(Item[] items, byte[] bdata) {
		DataInputStream dis = new DataInputStream(
				new ByteArrayInputStream(bdata));
		for (int ic = 0; ic < items.length; ic++) {
			try {
				final Item item = items[ic];
				if (item instanceof ChoiceGroup) {
					((ChoiceGroup)item).setSelectedIndex(dis.readInt(),
						true);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("set selected " + ((ChoiceGroup)item).getSelectedIndex());}
					//#endif
				} else if (item instanceof TextField) {
					final int len = dis.readInt();
					byte [] bvalue = new byte[len];
					dis.read(bvalue);
					String value;
					try {
						value = new String(bvalue, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						value = new String(bvalue);
						//#ifdef DLOGGING
//@						logger.severe("cannot convert value=" + value, e);
						//#endif
					}
					((TextField)item).setString(value);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("set string " + ((TextField)item).getString());}
					//#endif
				}
			} catch (IOException e) {
				//#ifdef DLOGGING
//@				logger.severe("IOException reading selected.", e);
				//#endif
			}
		}
		if (dis != null) {
			try { dis.close(); } catch (IOException e) {}
		}
	}

	/* Store current values. */
	final private byte[] storeValues(Item[] items) {
		ByteArrayOutputStream bout = new
				ByteArrayOutputStream();
		DataOutputStream dout = new
				DataOutputStream( bout );
		for (int ic = 0; ic < items.length; ic++) {
			try {
				final Item item = items[ic];
				if (item instanceof ChoiceGroup) {
					dout.writeInt(((ChoiceGroup)item).getSelectedIndex());
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("stored selected " + item.getLabel() + "," + ((ChoiceGroup)item).getSelectedIndex());}
					//#endif
				} else if (item instanceof TextField) {
					final String value = ((TextField)item).getString();
					dout.writeInt(value.length());
					byte [] bvalue;
					try {
						bvalue = value.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						bvalue = value.getBytes();
						//#ifdef DLOGGING
//@						logger.severe("cannot store value=" + value, e);
						//#endif
					}
					dout.write( bvalue, 0, bvalue.length );
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("set string " + item.getLabel() + "," + ((TextField)item).getString());}
					//#endif
				}
			} catch (IOException e) {
				//#ifdef DLOGGING
//@				logger.severe("IOException storing selected.", e);
				//#endif
			}
		}
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("bout.toByteArray().length=" + bout.toByteArray().length);}
		//#endif
		if (dout != null) {
			try { dout.close(); } catch (IOException e) {}
		}
		return bout.toByteArray();
	}

	/* Save the current bookmarks and other properties.
	   saveItems  - true if want to save items (use false if running out
	   			    of memory.
	   releaseMemory - true if memory used is to be released as the
	   				   bookmarks are saved.  Used when exitiing as true.
	*/
	final private synchronized void saveBkMrkSettings(final boolean saveHdr,
			final boolean saveItems,
											final String mkey,
											final boolean releaseMemory)
	throws RecordStoreFullException, Throwable {
		Gauge gauge = new Gauge(ResourceProviderME.get(mkey),
				false, m_settings.MAX_REGIONS, 0);
		int pl = m_loadForm.append(gauge);
		try {
			try {
				m_settings.setStringProperty("bookmarks","");
				m_settings.save(0, false);
				for (int ic = 1; ic < m_settings.MAX_REGIONS; ic++) {
					saveBookmarks(saveHdr, saveItems, ic, releaseMemory);
					m_settings.save(ic, false);
					gauge.setValue(ic);
				}
				// Set internal region back to 0.
				m_settings.setStringProperty("bookmarks","");
				m_settings.save(0, false);
				gauge.setValue(m_settings.MAX_REGIONS);
				pl = -1;
			} catch(OutOfMemoryError e) {
				/* Error during save out of memory. */
				throw new CauseMemoryException(ResourceProviderME.get("exc.sv.om"), e);
			}
		} catch(CauseRecStoreException e) {
			if ((e.getFirstCause() != null) &&
			    !(e.getFirstCause() instanceof RecordStoreFullException)) {
				/* Error saving feeds to database.  Database error. */
				recordExcFormRsc("exc.sv.dbe", e);
			} else {
				/* Error saving feeds to database.  Database full. */
				recordExcFormRsc("exc.sv.dbf", e);
				if (saveItems) {
					/* Retrying without saving items to save space in database. */
					m_loadForm.appendMsg(ResourceProviderME.get("text.sv.rwo"));
					if (pl >= 0) {
						m_loadForm.delete(pl);
						pl = -1;
					}
					saveBkMrkSettings(true, false, "label.save.d", releaseMemory);
					m_loadForm.appendMsg("Retry successful.");
				} else {
					throw e;
				}
			}
	} catch(CauseMemoryException e) {
		recordExcForm("Error saving feeds.  Out of memory error.", e);
		if (saveItems) {
			/* Retrying without saving items to save space in memory. */
			m_loadForm.appendMsg(ResourceProviderME.get("text.sv.rwo"));
			if (pl >= 0) {
				m_loadForm.delete(pl);
				pl = -1;
			}
			saveBkMrkSettings(true, false, "label.save.d", m_exit);
			m_loadForm.appendMsg("Retry successful.");
		} else {
			throw e;
		}
		} catch(Exception e) {
			recordExcForm("Internal error saving feeds.", e);
			throw e;
		} catch(Throwable t) {
			recordExcForm("Internal error saving feeds.", t);
			throw t;
		} finally {
			if (pl >= 0) {
				m_loadForm.delete(pl);
			}
		}
	}

	/** Remove the ref to this displayable so that the memory can be freed. */
	final public void removeRef(final Displayable disp) {
		m_loadForm.removeRef(disp);
	}

    /** Respond to commands */
    public void commandAction(final Command c, final Displayable s) {
		//#ifdef DLOGGING
		//#ifdef DMIDP20
//@		if (finestLoggable) {logger.finest("command,displayable=" + c.getLabel() + "," + s.getTitle());}
		//#else
//@		if (finestLoggable) {logger.finest("command,displayable=" + c.getLabel());}
		//#endif
		//#endif

        /** Manage bookmarks */
        if( c == m_manageBkmrk ){
			m_mainBmk = false;
			updBookmarkList();
        }
        
        /** Main bookmarks */
        if( c == m_backBookmark ){
			m_mainBmk = true;
			updBookmarkList();
        }
        
        /** Add new RSS feed bookmark */
        if( c == m_addNewBookmark ){
			/* Loading add bookmark... */
			showLoadingFormRsc("text.l.bmrk", m_bookmarkList);
			m_getAddBMForm = true;
			m_curBookmark = m_bookmarkList.getSelectedIndex();
        }
        
        /** Exit from MIDlet and save bookmarks */
        if( c == m_exitCommand ){
			/* Exiting saving data... */
			showLoadingFormRsc("text.exit", m_bookmarkList);
			synchronized (this) {
				if ( !m_netThread.isAlive() ) {
					m_netThread.start();
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("Thread started.");}
					//#endif
					try {
						m_netThread.sleep(1000L);
					} catch (InterruptedException e) {}
				}
			}
			m_exit = true;
        }
        
		//#ifdef DTEST
//@        /** Exit from MIDlet and save bookmarks */
//@        if( c == m_testClearCmd ){
//@			/* Trick to think that data was stored.  We want to remove the
//@			   database. */
//@			m_stored = true;
//@			try {
//@				Settings.deleteStore();
//@				destroyApp(true);
//@			} catch (MIDletStateChangeException e) {
//@			}
//@			super.notifyDestroyed();
//@		}
		//#endif

        /** Save bookmarks without exit (don't free up bookmarks)  */
        if( c == m_saveCommand ){
			/* Saving data... */
			showLoadingFormRsc("text.save", m_bookmarkList);
			m_saveBookmarks = true;
        }
        
        /** Edit currently selected RSS feed bookmark */
        if( c == m_editBookmark ){
			try {
				if( m_bookmarkList.size()>0 ){
					/* Loading edit bookmark... */
					showLoadingFormRsc("text.edit", m_bookmarkList);
					m_curBookmark = m_bookmarkList.getSelectedIndex();
					m_getEditBMForm = true;
				}
			}catch(Throwable t) {
				//#ifdef DLOGGING
//@				logger.severe("Editing feeds.", t);
				//#endif
				/** Error while parsing RSS feed */
				System.out.println("Error editing feeds: " + t.getMessage());
			}
        }
        
        /** Delete currently selected RSS feed bookmark */
        if( c == m_delBookmark ){
            if( m_bookmarkList.size()>0 ){
                m_curBookmark = m_bookmarkList.getSelectedIndex();
                String name = m_bookmarkList.getString(m_curBookmark);
                m_bookmarkList.delete( m_curBookmark );
				if (m_rssFeeds.containsKey( name )) {
					m_rssFeeds.remove( name );
				}
            }
        }
        
        /** Open RSS feed bookmark */
        if( c == m_openBookmark || (c == List.SELECT_COMMAND &&
                m_display.getCurrent()==m_bookmarkList)){
            if( m_bookmarkList.size()>0 ){
                m_curBookmark = m_bookmarkList.getSelectedIndex();
                
                RssItunesFeed feed = (RssItunesFeed)m_rssFeeds.get(
                        m_bookmarkList.getString(m_curBookmark));
                m_curRssParser = new RssFeedParser( feed );
                if( feed.getItems().size()==0 ) {
                    /** Update RSS feed headers only if this is a first time */
                    updateHeaders(false, m_bookmarkList);
                } else {
					/* Loading feed... */
					showLoadingFormRsc("text.l.feed", m_bookmarkList);
					if(feed.getUrl().length() == 0) {
						m_loadForm.addExc(new Exception(
									"Feed has no URL cannot load."));
					} else {
						/**
						 * Show currently selected RSS feed
						 * headers without updating them
						 */
						m_openPage = true;
						m_curBookmark = m_bookmarkList.getSelectedIndex();
					}
                }
            }
        }
        
        /** Read unread items date sorted */
        if( c == m_readUnreadItems ) {
			if (m_bookmarkList.size() > 0) {
				/* Sorting items... */
				showLoadingFormRsc("text.s.item", m_bookmarkList);
				m_runNews = true;
			}
        }
        
        /** Open RSS feed's selected topic */
        /** Get back to RSS feed headers */
        if( c == m_backCommand ){
            setCurrent( m_itemRrnForm );
			//#ifdef DTESTUI
//@			if (m_headerIndex >= 0) {
//@				m_headerNext = true;
//@			} else if (m_unreadHeaderTestList != null) {
//@				m_unreadHeaderTestList.gotoNews();
//@			}
			//#endif
        }
        
        /** Copy link to clipboard.  */
        if( c == m_copyLinkCmd ){
			String link = citem.getLink();
			m_itemForm.set(citemLnkNbr, new TextField("Link:", link,
					link.length(), TextField.URL));
			//#ifdef DMIDP10
//@			setCurrent(m_itemForm);
			//#else
			setCurrentItem(m_itemForm.get(citemLnkNbr));
			//#endif
        }
        
        /** Copy enclosure to clipboard.  */
        if( c == m_copyEnclosureCmd ){
			final String link = citem.getEnclosure();
			m_itemForm.set(citemEnclNbr, new TextField("Enclosure:",
                link, link.length(), TextField.URL));
			//#ifdef DMIDP10
//@			setCurrent(m_itemForm);
			//#else
			setCurrentItem(m_itemForm.get(citemEnclNbr));
			//#endif
        }

		/** Help for RSS item. */
        if( c == m_rssItemHelpCmd ){
			final HelpForm helpForm = new HelpForm(this, m_itemForm);
			helpForm.appendRsc("text.kpd.help");
		}
        
		//#ifdef DMIDP20
        /** Go to link and get back to RSS feed headers */
        if( c == m_openLinkCmd ){
			final String link = citem.getLink();
			m_platformReq = true;
			m_platformURL = link;
			wakeUp();
		}
		//#endif

		//#ifdef DMIDP20
        /** Go to link and get back to RSS feed headers */
        if( c == m_openEnclosureCmd ){
			m_platformReq = true;
			m_platformURL = citem.getEnclosure();
			wakeUp();
		}
		//#endif
        
        /** Update all RSS feeds */
        if( (c == m_updateAllCmd) || (c == m_updateAllModCmd) ) {
            updateAllHeaders(c == m_updateAllModCmd);
        }
        
        /** Show import feed list form */
        if( c == m_importFeedListCmd ) {
			// Set current bookmark so that the added feeds go after
			// the current boolmark.
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			/* Loading import form... */
			showLoadingFormRsc("text.l.imp", m_bookmarkList);
			m_getImportForm = true;
			wakeUp();
        }
        
		//#ifdef DTEST
//@		/** Show import feed list form and default file */
//@		if( c == m_importCurrFeedListCmd ) {
//@			if( m_bookmarkList.size()>0 ) {
//@                m_curBookmark = m_bookmarkList.getSelectedIndex();
				//#ifdef DTESTUI
//@				m_bookmarkLastIndex = m_curBookmark;
				//#endif
//@				/* Loading import form... */
//@				showLoadingFormRsc("text.l.imp", m_bookmarkList);
//@				m_getImportForm = true;
//@				m_getTestImportForm = true;
//@				wakeUp();
//@			}
//@        }
		//#endif

		//#ifdef DTESTUI
//@        /** Auto edit feeds/bookmarks to */
//@        if( c == m_testBMCmd ) {
//@			m_bookmarkIndex = 0;
//@			System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" + m_bookmarkIndex);
//@		}
		//#endif

		//#ifdef DTESTUI
//@        /** Go back to last position */
//@        if( c == m_testRtnCmd ) {
//@			if (m_bookmarkLastIndex != 1) {
//@				if (m_bookmarkList.getSelectedIndex() >= 0) {
//@					m_bookmarkList.setSelectedIndex(
//@							m_bookmarkList.getSelectedIndex(), false);
//@				}
//@				m_bookmarkList.setSelectedIndex( m_bookmarkLastIndex, true );
//@			}
//@		}
		//#endif

        /** Settings form */
        if( c == m_settingsCmd ) {
			/* Loading settings... */
			showLoadingFormRsc("text.l.s", m_bookmarkList);
			m_getSettingsForm = true;
			wakeUp();
        }
        
        /** Show help */
		if( c == m_helpCmd ) {
			/* Loading help... */
			showLoadingFormRsc("text.l.h", m_bookmarkList);
			m_getHelpForm = true;
		}

		//#ifdef DTESTUI
//@        /** Show encodings list */
//@		if( c == m_testEncCmd ) {
//@			/* Loading test form... */
//@			showLoadingFormRsc("text.l.t", m_bookmarkList);
//@			setCurrent( m_testingForm );
//@		}
		//#endif

	//#ifdef DLOGGING
//@        /** Show about */
//@		if( c == m_debugCmd ) {
//@			/* Loading debug form... */
//@			showLoadingFormRsc("text.l.d", m_bookmarkList);
//@			setCurrent( m_debug );
//@		}
//@
//@        /** Clear form */
//@		if( c == m_clearDebugCmd ) {
//@			while(m_debug.size()>0) {
//@				m_debug.delete(0);
//@			}
//@		}
//@
//@        /** Back to bookmarks */
//@		if( c == m_backFrDebugCmd ) {
//@			setCurrent( m_bookmarkList );
//@		}
//@
		//#endif

    }
    
	/* Form to import feeds. */
	final private class ImportFeedsForm extends Form
		implements CommandListener, Runnable {

		private boolean     m_getFeedList = false;      // The noticy flag for list parsing
		private boolean     m_getFeedTitleList = false; // The noticy flag for title/list parsing
		// The noticy flag for override existing feeds
		private boolean     m_override = false;  // The noticy flag for override
		private boolean     m_needWakeup = false;   // Flag to show need to wakeup
		private boolean     m_getHelp = false;      // The help form flag
		private boolean     m_process = true;   // Flag to continue looping
		private FeedListParser m_listParser;    // The feed list parser
		private TextField   m_feedListURL;      // The feed list URL field
		private TextField   m_feedNameFilter;   // The feed name filter string
		private TextField   m_feedURLFilter;    // The feed URL filter string
		private TextField   m_feedListUsername; // The feed list username
		private TextField   m_feedListPassword; // The feed list password
		private ChoiceGroup m_importFormatGroup;// The import type choice group
		private ChoiceGroup m_importTitleGroup; // The import title choice group
		private ChoiceGroup m_importHTMLGroup;  // The import HTML redirect choice group
		private ChoiceGroup m_importOvrGroup; // The import override choice group
		private Command     m_importInsCmd;   // The import before the current point?
		private Command     m_importAddCmd;   // The import after the current point?
		private Command     m_importAppndCmd; // The import append
		private Command     m_importCancelCmd;  // The Cancel command for importing
		private Command     m_importFileCmd;    // The find files command for importing
		private Command     m_pasteImportURLCmd;// The paste command
		private Command     m_helpCmd;          // The help command
		//#ifdef DTESTUI
//@		private Command     m_testImportCmd;    // Test UI rss opml command
		//#endif

		/* Constructor */
		ImportFeedsForm(String url) {
			super("Import feeds");
			m_getFeedList = false;
			m_getFeedTitleList = false;
			if(url.length()==0) {
				url = "http://";
			}
			m_feedListURL = new TextField("URL", url, 256, TextField.URL);
			super.append(m_feedListURL);
			
			String[] formats = {"OPML", "line by line", "HTML OPML Auto link",
								"HTML RSS Auto links", "HTML Links"};
			m_importFormatGroup = new ChoiceGroup("Format", ChoiceGroup.EXCLUSIVE, formats, null);
			super.append(m_importFormatGroup);
			
			m_feedNameFilter = new TextField("Name filter string (optional)", "", 256, TextField.ANY);
			super.append(m_feedNameFilter);
			m_feedURLFilter = new TextField("URL filter string (optional)", "", 256, TextField.ANY);
			super.append(m_feedURLFilter);
			
			final String username = m_appSettings.getImportUrlUsername();
			m_feedListUsername  = new TextField("Username (optional)", username, 64, TextField.ANY);
			super.append(m_feedListUsername);
			
			final String password = m_appSettings.getImportUrlPassword();
			m_feedListPassword  = new TextField("Password (optional)", password, 64, TextField.PASSWORD);
			super.append(m_feedListPassword);
			String[] titleInfo =
					{"Skip feed with missing title",
					 "Get missing titles from feed"};
			m_importTitleGroup  = new ChoiceGroup("Missing title (optionl)",
					ChoiceGroup.EXCLUSIVE, titleInfo, null);
			super.append(m_importTitleGroup);
			String[] HTMLInfo =
					{"Redirect if HTML (ignored for HTML link import)",
					 "Treat HTML as import"};
			m_importHTMLGroup  =
				new ChoiceGroup("Treat HTML mime type as valid import (optional)",
					ChoiceGroup.EXCLUSIVE, HTMLInfo, null);
			String[] overrideInfo =
					{"Don't override existing feeds.",
					 "Override (replace) existing feeds."};
			/* TODO allow override
			m_importOvrGroup  = new ChoiceGroup(
					"Override existing feeds (optionl)",
					ChoiceGroup.EXCLUSIVE, overrideInfo, null);
			super.append(m_importOvrGroup);
			*/
			if (m_importSave != null) { 
				Item[] items = {m_importFormatGroup, m_feedNameFilter,
					m_feedURLFilter, m_feedListUsername, m_feedListPassword,
					m_importFormatGroup, m_importTitleGroup,
					m_importHTMLGroup
					// TODO allow override ,m_importOvrGroup
					}; 
				restorePrevValues(items, m_importSave);
			}
			super.append(m_importHTMLGroup);
			/* Insert import */
			/* Insert current import */
			m_importInsCmd      = UiUtil.getCmdRsc("cmd.i.imp", "cmd.li.imp",
					Command.SCREEN, 1);
			/* Add import */
			/* Add current import */
			m_importAddCmd      = UiUtil.getCmdRsc("cmd.a.imp", "cmd.la.imp",
					Command.SCREEN, 2);
			/* Append import */
			/* Append end import */
			m_importAppndCmd    = UiUtil.getCmdRsc("cmd.ap.imp", "cmd.lap.imp",
					Command.SCREEN, 3);
			/* Cancel */
			m_importCancelCmd   = UiUtil.getCmdRsc("cmd.cancel", Command.CANCEL,
					4);
			/* Find files */
			m_importFileCmd     = UiUtil.getCmdRsc("cmd.f.fl", Command.SCREEN, 5);
			/* Allow paste */
			m_pasteImportURLCmd = UiUtil.getCmdRsc("cmd.a.pst", Command.SCREEN, 6);
			m_helpCmd         = UiUtil.getCmdRsc("cmd.help", Command.HELP, 6);
			
			super.addCommand( m_importInsCmd );
			super.addCommand( m_importAddCmd );
			super.addCommand( m_importAppndCmd );
			super.addCommand( m_importCancelCmd );
			//#ifdef DJSR75
//@			super.addCommand( m_importFileCmd );
			//#endif
			if (m_appSettings.getUseTextBox()) {
				super.addCommand(m_pasteImportURLCmd);
			}
			super.addCommand(m_helpCmd);
			//#ifdef DTESTUI
//@			/* Test bookmarks imported */
//@			m_testImportCmd     = UiUtil.getCmdRsc("cmd.t.imp", Command.SCREEN, 9);
//@			super.addCommand( m_testImportCmd );
			//#endif
			super.setCommandListener(this);

			m_process = true;
			//#ifdef DCLDCV11
//@			new Thread(this, "ImportFeedsForm").start();
			//#else
			new Thread(this).start();
			//#endif
		}

		/** Add from feed list (from import). */
		final private void addFeedLists() throws CauseException, Exception {
			// Feed list parsing is ready
			System.out.println("Feed list parsing is ready");
			if(!m_listParser.isSuccessfull()) {
				throw m_listParser.getEx();
			}
			RssItunesFeed[] feeds = m_listParser.getFeeds();
			boolean notesShown = false;
			for(int feedIndex=0; feedIndex<feeds.length; feedIndex++) {
				String name = feeds[feedIndex].getName();
				//#ifdef DTEST
//@				System.out.println("Adding: " + name);
				//#endif
				// If no title (name) and we are getting the title from the
				// feed being imported, parse the name(title) only.
				if (((name == null) || (name.length() == 0)) && m_getFeedTitleList) {
					RssItunesFeed feed = feeds[feedIndex];
					RssFeedParser fparser = new RssFeedParser( feed );
					/* Loading title for */
					m_loadForm.appendMsg(ResourceProviderME.get("text.ld.t",
								feed.getUrl()));
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("Getting title for url=" + feed.getUrl());}
					//#endif
					fparser.setGetTitleOnly(true);
					/** Get RSS feed */
					int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
					try {
						fparser.parseRssFeed( false, maxItemCount );
						name = feed.getName();
						m_loadForm.appendMsg("ok\n");
					} catch(Exception ex) {
						CauseException ce = new CauseException(
								"Error loading title for feed " + feed.getUrl(),
								ex);
						//#ifdef DLOGGING
//@						logger.severe(ce.getMessage(), ex);
						//#endif
						m_loadForm.appendMsg("Error\n");
						m_loadForm.addExc(ce);
						notesShown = true;
					}
				}
				if((name != null) && (name.length()>0)) {
					final boolean pres = m_rssFeeds.containsKey( name );
					if(m_override || !pres) {
						if(pres) {
							m_loadForm.appendMsg(
									ResourceProviderME.get("text.wr.dup",
									name));
						}
						m_rssFeeds.put( name, feeds[feedIndex] );
						m_bookmarkList.insert(m_addBkmrk++, name, null);
					} else {
						/* Error:  Feed already exists with name (name) */
						/*.  Existing feed not updated. */
						CauseException ce = new CauseException(
								ResourceProviderME.get("exc.fd.ex", name));
						m_loadForm.appendMsg(ce.getMessage());
						m_loadForm.addExc(ce);
						notesShown = true;
					}
				}
			}
			if (notesShown) {
				recordFin();
				setCurrent( m_loadForm );
			} else {
				m_process = false;
				m_loadForm.removeRef(this);
				Item[] items = {m_importFormatGroup, m_feedNameFilter,
					m_feedURLFilter, m_feedListUsername, m_feedListPassword,
					m_importFormatGroup, m_importTitleGroup, m_importHTMLGroup
					// TODO allow override ,m_importOvrGroup
					};
				m_importSave = storeValues(items);
				setCurrent( m_bookmarkList );
			}
			m_getFeedList      = false;
			m_getFeedTitleList = false;
		}

		/** Run method is used to get RSS feed with HttpConnection */
		public void run(){
			/* Use networking if necessary */
			long lngStart;
			long lngTimeTaken;
			while(m_process) {
				try {
					// Add feeds from import.
					if( m_getFeedList ) {
						try {
							try {
								if(m_listParser == null) {
									/* If we get here, it's beause the user
									   pressed the menu twice.  */
									setCurrent( m_bookmarkList );
									m_loadForm.removeRef(this);
									m_getFeedList      = false;
									m_getFeedTitleList = false;
									m_process = false;
								} else if(m_listParser.isReady()) {
									addFeedLists();

								} else {
									//#ifndef DTESTUI
									if (m_debugOutput) System.out.println("Feed list parsing isn't ready");
									//#endif
								}
							} catch(OutOfMemoryError e) {
								throw new CauseMemoryException(
										"Error importing feeds from " +
										m_listParser.getUrl() + " " +
										e.getMessage(), e);
							}
						} catch(CauseMemoryException ex) {
							recordExcFormFin(
									"Out of memory error importing feeds " +
									"from " + m_listParser.getUrl() + " " +
									ex.getMessage(), ex);
							m_getFeedList      = false;
							m_getFeedTitleList = false;
							// TODO empty list parser m_listParser = null;
						} catch(Exception ex) {
							recordExcFormFin(
									"Error importing feeds from " +
									m_listParser.getUrl(), ex);
							m_getFeedList      = false;
							m_getFeedTitleList = false;
							// TODO empty list parser m_listParser = null;
						} catch(Throwable t) {
							recordExcFormFin(
									"Internal error importing feeds from " +
									m_listParser.getUrl(), t);
							m_getFeedList      = false;
							m_getFeedTitleList = false;
							// TODO empty list parser m_listParser = null;
						}
					} else if (m_getHelp) {
						final HelpForm helpForm = new HelpForm(m_midlet, this);
						helpForm.appendRsc("text.abmc.help");
						/* TODO allow override
						helpForm.appendItemHelpRsc(m_importOvrGroup, "text.oimp.help");
						*/
						helpForm.appendItemHelpRsc(m_importFormatGroup,
								"text.fimp.help");
						helpForm.appendCmdHelpRsc(m_importAddCmd, "text.aimp.help");
						helpForm.appendCmdHelpRsc(m_importAppndCmd, "text.pimp.help");
						m_getHelp = false;
						setCurrent( helpForm );
					}

					lngStart = System.currentTimeMillis();
					lngTimeTaken = System.currentTimeMillis()-lngStart;
					if(lngTimeTaken<100L) {
						synchronized(this) {
							if (!m_needWakeup) {
								super.wait(75L-lngTimeTaken);
							}
							m_needWakeup = false;
						}
					}
				} catch (InterruptedException e) {
					break;
				}
			}
		}

		/** Respond to commands */
		public void commandAction(final Command c, final Displayable s) {
			//#ifdef DTESTUI
//@			super.outputCmdAct(c, s);
			//#endif

			/** Import list of feeds */
			if( (c == m_importInsCmd ) || (c == m_importAddCmd ) ||
					(c == m_importAppndCmd )) {
				final int blen = m_bookmarkList.size();
				m_addBkmrk = (m_curBookmark == -1) ? blen : m_curBookmark;
				if( c == m_importAddCmd ){
					if (m_addBkmrk < blen) {
						m_addBkmrk++;
					}
				}
				if (c == m_importAppndCmd ) {
					m_addBkmrk = blen;
				}
				if ((m_addBkmrk < 0) || (m_addBkmrk > blen)) {
					m_addBkmrk = blen;
				}

				final String url = m_feedListURL.getString().trim();
				try {
					/* Loading feeds from import... */
					showLoadingFormRsc("text.l.imp.f", this);
					
					// 2. Import feeds
					int selectedImportType = m_importFormatGroup.getSelectedIndex();
					RssItunesFeed[] feeds = null;
					String feedNameFilter = m_feedNameFilter.getString();
					String feedURLFilter = m_feedURLFilter.getString();
					String username = m_feedListUsername.getString();
					String password = m_feedListPassword.getString();
					m_getFeedTitleList = m_importTitleGroup.isSelected(1);
					/* TODO allow override
					m_override = m_importOvrGroup.isSelected(1);
					*/
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("m_getFeedTitleList=" + m_getFeedTitleList);}
//@					if (finestLoggable) {logger.finest("selectedImportType=" + selectedImportType);}
					//#endif
					
					// Save settings
					m_appSettings.setImportUrl(url);
					m_appSettings.setImportUrlUsername(username);
					m_appSettings.setImportUrlPassword(password);
					switch (selectedImportType) {
						case 0:
							// Use OPML parser
							m_listParser = new OpmlParser(url, username, password);
							break;
						case 1:
							// Use line by line parser
							m_listParser = new LineByLineParser(url, username, password);
							break;
						case 2:
							// Use line by HMTL OPML auto link parser
							m_listParser = new HTMLAutoLinkParser(url, username, password);
							((HTMLAutoLinkParser)m_listParser).setNeedRss(false);
							break;
						case 3:
							// Use line by HMTL RSS auto link parser
							m_listParser = new HTMLAutoLinkParser(url, username, password);
							((HTMLAutoLinkParser)m_listParser).setNeedRss(true);
							break;
						case 4:
							// Use line by HMTL link parser
							m_listParser = new HTMLLinkParser(url, username, password);
							break;
					}
					m_listParser.setFeedNameFilter(feedNameFilter);
					m_listParser.setFeedURLFilter(feedURLFilter);
					m_listParser.setRedirectHtml(m_importHTMLGroup.isSelected(0)
						&& !(m_listParser instanceof HTMLAutoLinkParser)
						&& !(m_listParser instanceof HTMLLinkParser));
					//#ifdef DLOGGING
//@					if (fineLoggable) {logger.fine("redirect html=" + m_listParser.isRedirectHtml());}
					//#endif
					
					// Start parsing
					m_listParser.startParsing();
					m_getFeedList = true;
					
					// 3. Show result screen
					// 4. Show list of feeds
					
				} catch(Exception ex) {
					recordExcFormFin("Error importing feeds from " + url, ex);
					m_getFeedList = false;
				} catch(OutOfMemoryError ex) {
					/* Out Of Memory Error importing feeds from */
					recordExcFormFinRsc("exc.om.imp", url, ex);
					m_getFeedList = false;
				} catch(Throwable t) {
					/* Internal error importing feeds from */
					recordExcFormFinRsc("exc.int.imp", url, t);
					m_getFeedList = false;
				}
			}
			
			//#ifdef DJSR75
//@			/** Find import file in file system */
//@			if( c == m_importFileCmd ) {
//@				if (!JSR75_ENABLED) {
//@					Alert invalidAlert = new Alert(
//@							"JSR-75 not enabled", 
//@							"Find files (JSR-75) not enabled on the phone.",
//@							null,
//@							AlertType.WARNING);
//@					invalidAlert.setTimeout(Alert.FOREVER);
//@					setCurrent( invalidAlert, this );
//@					return;
//@				}
//@				try {
//@					/* Loading files to import from... */
//@					showLoadingFormRsc("text.l.f.imp", this);
//@					reqFindFiles( this, m_feedListURL );
//@					wakeUp();
//@				}catch(Throwable t) {
					//#ifdef DLOGGING
//@					logger.severe("RssReaderMIDlet find files ", t);
					//#endif
//@					/** Error while executing find files */
//@					System.out.println("RssReaderMIDlet find files " + t.getMessage());
//@					t.printStackTrace();
//@				}
//@			}
			//#endif
					
			/** Cancel importing -> Show list of feeds */
			if( c == m_importCancelCmd ) {
				m_loadForm.removeRef(this);
				m_process = false;
				setCurrent( m_bookmarkList );
			}
			
			if( c == m_helpCmd ) {
				m_getHelp = true;
			}

			/** Put current import URL into URL box.  */
			if( c == m_pasteImportURLCmd ) {
				initializeURLBox(m_feedListURL.getString(),
						(CommandListener)this );
				setCurrent( m_boxURL );
			}

			/** Paste into URL field from previous form.  */
			if( c == m_boxOkCmd ) {
				m_feedListURL.setString( m_boxURL.getString() );
				//#ifdef DMIDP20
				setCurrentItem( m_feedListURL );
				//#else
//@				setCurrent( this );
				//#endif
			}
			
			/** Cancel the box go back to the return form.  */
			if( c == m_boxCancelCmd ) {
				//#ifdef DMIDP20
				setCurrentItem( m_feedListURL );
				//#else
//@				setCurrent( this );
				//#endif
			}
			
			//#ifdef DTESTUI
//@			/** Import list of feeds and auto edit bookmarks/feeds */
//@			if( c == m_testImportCmd ) {
//@				m_bookmarkIndex = m_bookmarkList.size();
//@				System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" + m_bookmarkIndex);
//@				commandAction(m_importAppndCmd, this);
//@			}
			//#endif
			wakeUp();

		}

		/* Notify us that we are finished. */
		final public void importWakeUp() {
		
			synchronized(this) {
				m_needWakeup = true;
				super.notify();
			}
		}

	}

	/* Form to add new/edit existing bookmark. */
	final private class HeaderList extends List implements CommandListener {

		private RssReaderMIDlet m_midlet;       // RssReaderMIDlet midlet
		private Command     m_openHeaderCmd;    // The open header command
		private Command     m_backHeaderCmd;    // The back to bookmark list command
		private Command     m_updateCmd;        // The update headers command
		private Command     m_updateModCmd;     // The update modified headers command
		//#ifdef DITUNES
//@		private Command     m_bookmarkDetailsCmd;   // The show feed details
		//#endif

		/* Constructor */
		private HeaderList(RssReaderMIDlet midlet, final RssItunesFeed feed) {
			super("Headers", List.IMPLICIT);
			this.m_midlet = midlet;
			final boolean open1st = m_appSettings.getFeedListOpen();
			//#ifdef DLOGGING
//@			if (fineLoggable) {logger.fine("initheader open1st=" + open1st);}
			//#endif
			if (open1st) {
				// Initialize m_backHeaderCmd in form initialization so that we can
				// change it per user request.
				/* Open item */
				m_openHeaderCmd = UiUtil.getCmdRsc("cmd.op.i", Command.SCREEN, 1);
				m_backHeaderCmd = UiUtil.getCmdRsc("cmd.back", Command.BACK, 2);
				super.addCommand(m_openHeaderCmd);
				super.addCommand(m_backHeaderCmd);
			} else {
				m_backHeaderCmd = UiUtil.getCmdRsc("cmd.back", Command.BACK, 1);
				/* Open item */
				m_openHeaderCmd = UiUtil.getCmdRsc("cmd.op.i", Command.SCREEN, 2);
				super.addCommand(m_backHeaderCmd);
				super.addCommand(m_openHeaderCmd);
			}
			/* Update feed */
			m_updateCmd         = UiUtil.getCmdRsc("cmd.u.fd", Command.SCREEN, 2);
			/* Update modified feed */
			m_updateModCmd      = UiUtil.getCmdRsc("cmd.um.fd",
											  Command.SCREEN, 2);
			super.addCommand(m_updateCmd);
			super.addCommand(m_updateModCmd);
			//#ifdef DTESTUI
//@			super.addCommand(m_testRssCmd);
			//#endif
			//#ifdef DITUNES
//@			if (m_itunesEnabled && feed.isItunes()) { 
//@				/* Show bookmark details */
//@				m_bookmarkDetailsCmd    = UiUtil.getCmdRsc("cmd.s.bdt",
//@						Command.SCREEN, 4);
//@				super.addCommand(m_bookmarkDetailsCmd);
//@			}
			//#endif
			super.setCommandListener(this);
		}
		
		/** Fill RSS header list */
		final private void fillHeadersList() {
			//#ifdef DMIDP20
			if(super.size()>0) {
				super.deleteAll();
			}
			//#else
//@			while(super.size()>0) {
//@				super.delete(0);
//@			}
			//#endif
			RssItunesFeed feed = m_curRssParser.getRssFeed();
			super.setTitle( feed.getName() );
			final boolean markUnreadItems = m_appSettings.getMarkUnreadItems();
			final Vector vitems = feed.getItems();
			final int itemLen = vitems.size();
			for(int i=0; i < itemLen; i++){
				RssItunesItem r = (RssItunesItem)vitems.elementAt(i);
				String text = r.getTitle();
				if (text.length() == 0) {
					text = getItemDescription(r);
				}
				if (markUnreadItems && r.isUnreadItem()) {
					super.append( text, m_unreadImage );
				} else {
					super.append( text, null );
				}
			}
		}
		
		//#ifdef DTEST
//@		/** Test that the feed is not ruined by being stored and restored. */
//@		final private void testFeed() {
//@			RssItunesFeed feed = m_curRssParser.getRssFeed();
//@			String store = feed.getStoreString(true, true, true);
//@			RssItunesFeed feed2 = RssItunesFeed.deserialize(
//@					true, store );
//@			boolean feedEq = feed.equals(feed2);
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("feed1,2 eq=" + feedEq);}
			//#endif
//@			if (!feedEq) {
				//#ifdef DLOGGING
//@				logger.severe("Itunes feed does not match name=" + feed.getName());
				//#endif
//@				System.out.println("feed=" + feed + "," + feed.toString());
//@				System.out.println("feed store=" + store);
//@			}
//@		}
		//#endif

		//#ifdef DITUNES
//@		/** Initialize RSS bookmark feed details form */
//@		final private Form initializeDetailsForm( final RssItunesFeed feed ) {
			//#ifdef DTEST
//@			System.gc();
//@			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
//@			Form displayDtlForm = new Form( feed.getName() );
//@			displayDtlForm.addCommand( m_backCommand );
//@			displayDtlForm.setCommandListener(this);
//@			boolean pageEnabled = m_appSettings.getPageEnabled();
//@			int fontSize = pageEnabled ? getFontSize() : 0;
//@			if (m_itunesEnabled && feed.isItunes()) {
//@				final String language = feed.getLanguage();
//@				if (language.length() > 0) {
//@					displayDtlForm.append(getTextItem(pageEnabled, "Language:",
//@								language, fontSize, displayDtlForm, this));
//@				}
//@				final String author = feed.getAuthor();
//@				if (author.length() > 0) {
//@					displayDtlForm.append(getTextItem(pageEnabled, "Author:",
//@								author, fontSize, displayDtlForm, this));
//@				}
//@				final String subtitle = feed.getSubtitle();
//@				if (subtitle.length() > 0) {
//@					displayDtlForm.append(getTextItem(pageEnabled,
//@								"Subtitle:",
//@								subtitle, fontSize, displayDtlForm, this));
//@				}
//@				final String summary = feed.getSummary();
//@				if (summary.length() > 0) {
//@					displayDtlForm.append(getTextItem(pageEnabled, "Summary:",
//@								summary, fontSize, displayDtlForm, this));
//@				}
//@				displayDtlForm.append(new StringItem("Explicit:", feed.getExplicit()));
//@				final String title = feed.getTitle();
//@				if (title.length() > 0) {
//@					displayDtlForm.append(new StringItem("title:", title));
//@				}
//@				final String description = feed.getDescription();
//@				if (description.length() > 0) {
//@					displayDtlForm.append(getTextItem(pageEnabled,
//@								"Description:",
//@								description, fontSize, displayDtlForm, this));
//@				}
//@			}
//@			final String link = feed.getLink();
//@			if (link.length() > 0) {
				//#ifdef DMIDP20
//@				StringItem slink = new StringItem("Link:", link,
//@												  Item.HYPERLINK);
				//#else
//@				StringItem slink = new StringItem("Link:", link);
				//#endif
//@				displayDtlForm.append(slink);
//@			}
//@			final Date feedDate = feed.getDate();
//@			if (feedDate != null) {
//@				displayDtlForm.append(new StringItem("Date:",
//@							feedDate.toString()));
//@			}
			//#ifdef DTEST
//@			System.gc();
//@			System.out.println("displayDtlForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
//@			return displayDtlForm;
//@		}
		//#endif
		
		/** Respond to commands */
		public void commandAction(final Command c, final Displayable s) {

			//#ifdef DTESTUI
//@			super.outputCmdAct(c, s,
//@					javax.microedition.lcdui.List.SELECT_COMMAND);
			//#endif

			/** Open RSS feed's selected topic */
			if( c == m_openHeaderCmd || (c == List.SELECT_COMMAND &&
					m_display.getCurrent()==this)) {
				if( super.size()>0 ) {
					RssItunesFeed feed = m_curRssParser.getRssFeed();
					int selIdx;
					RssItunesItem item = (RssItunesItem)feed.getItems(
							).elementAt((selIdx = super.getSelectedIndex()) );
					super.set(selIdx, super.getString(selIdx),
							null );
					item.setUnreadItem(false);
					initializeItemForm( feed, item, this );
					setCurrent( m_itemForm );
					//#ifdef DTESTUI
//@					m_itemNext = true;
					//#endif
				}
			}
			
			/** Update currently selected RSS feed's headers */
			if( (c == m_updateCmd) ||  (c == m_updateModCmd) ) {
				updateHeaders(c == m_updateModCmd, this);
			}
			
			/** Get back to RSS feed bookmarks */
			if( c == m_backHeaderCmd ){
				setCurrent( m_bookmarkList );
				m_loadForm.removeRef(this);
				//#ifdef DTESTUI
//@				m_headerTestList = null;
//@				m_headerIndex = -1;
//@				m_headerNext = false;
//@				m_itemNext = false;
				//#endif
			}
			
			//#ifdef DITUNES
//@			/** Display Itune's feed detail */
//@			if( c == m_bookmarkDetailsCmd ) {
//@				Form displayDtlForm = initializeDetailsForm(
//@						m_curRssParser.getRssFeed() );
//@				setCurrent( displayDtlForm );
//@			}
			//#endif

			/* Back from details form. */
			if( c == m_backCommand ){
				setCurrent( this );
			}
			
			//#ifdef DTESTUI
//@			/** Indicate that we want to test the headers/items.  */
//@			if( c == m_testRssCmd) {
//@				if( super.size()>0 ) {
//@					m_headerTestList = this;
//@					m_headerNext = true;
//@					m_itemNext = false;
//@					m_headerIndex = 0;
//@					System.out.println("Test UI Test Rss items start m_headerIndex=" + m_headerIndex);
//@				}
//@			}
			//#endif

		}
	}

	/* Form to add new/edit existing bookmark. */
	final private class BMForm extends Form
	implements CommandListener, Runnable {
		private boolean     m_addForm;          // Flag to indicate is add form
		private Command     m_addInsCmd;   // The add before the current point?
		private Command     m_addAddCmd;        // The add after the current point?
		private Command     m_addAppndCmd;      // The add append
		private Command     m_clearCmd;         // The clear
		private Command     m_editOkCmd;        // The edit is OK
		private Command     m_addCancelCmd;     // The Cancel command
		private Command     m_pasteURLCmd;      // The allow paste command
		private Command     m_helpCmd;          // The help command
		private Command     m_BMFileCmd;        // The find files command
		private TextField   m_bmName;           // The RSS feed name field
		private TextField   m_bmURL;            // The RSS feed URL field
		private TextField   m_bmUsername;       // The RSS feed username field
		private TextField   m_bmPassword;       // The RSS feed password field

		/* Constructor */
		private BMForm(final boolean addForm) {
			super(addForm ? "New Bookmark" : "Edit Bookmark");
			m_bmName = new TextField("Name", "", 64, TextField.ANY);
			m_bmURL  = new TextField("URL", "http://", 256, TextField.URL);
			m_bmUsername  = new TextField("Username (optional)", "", 64, TextField.ANY);
			m_bmPassword  = new TextField("Password (optional)", "", 64, TextField.PASSWORD);
			super.append( m_bmName );
			super.append( m_bmURL );
			super.append( m_bmUsername );
			super.append( m_bmPassword );
			if (addForm) {
				/* Insert bookmark */
				/* Insert current bookmark */
				m_addInsCmd      = UiUtil.getCmdRsc("cmd.i.bmk", "cmd.li.bmk",
						Command.SCREEN, 1);
				/* Add bookmark */
				/* Add current bookmark */
				m_addAddCmd      = UiUtil.getCmdRsc("cmd.a.bmk", "cmd.la.bmk",
						Command.SCREEN, 2);
				/* Append bookmark */
				/* Append end bookmark */
				m_addAppndCmd    = UiUtil.getCmdRsc("cmd.ap.bmk", "cmd.lap.bmk",
						Command.SCREEN, 3);
				/* Clear */
				/* Clear screen */
				m_clearCmd    = UiUtil.getCmdRsc("cmd.clear", "cmd.lclear",
						Command.SCREEN, 4);
				super.addCommand( m_addInsCmd );
				super.addCommand( m_addAddCmd );
				super.addCommand( m_addAppndCmd );
				super.addCommand( m_clearCmd );
			} else {
				m_editOkCmd     = UiUtil.getCmdRsc("cmd.ok", Command.OK, 1);
				super.addCommand( m_editOkCmd );
			}
			m_addCancelCmd      = UiUtil.getCmdRsc("cmd.cancel",
					Command.CANCEL, 5);
			super.addCommand( m_addCancelCmd );
			//#ifdef DJSR75
//@			/* Find files */
//@			m_BMFileCmd         = UiUtil.getCmdRsc("cmd.f.fl", Command.SCREEN, 3);
//@			super.addCommand(m_BMFileCmd);
			//#endif
			if (m_appSettings.getUseTextBox()) {
				/* Allow paste */
				m_pasteURLCmd       = UiUtil.getCmdRsc("cmd.a.pst", Command.SCREEN, 4);
				super.addCommand(m_pasteURLCmd);
			}
			m_helpCmd         = UiUtil.getCmdRsc("cmd.help", Command.HELP, 5);
			super.addCommand(m_helpCmd);
			super.setCommandListener( this );
			this.m_addForm = addForm;
			if (addForm && (m_addBMSave != null)) {
				Item[] items = {m_bmName, m_bmURL,
					m_bmUsername, m_bmPassword};
				restorePrevValues(items, m_addBMSave);
			}
		}

		/* Update bookmark info. */
		final private void updateBM(final RssItunesFeed bm) {
			m_bmName.setString( bm.getName() );
			m_bmURL.setString(  bm.getUrl() );
			m_bmUsername.setString( bm.getUsername() );
			m_bmPassword.setString( bm.getPassword() );
		}

		/** Save bookmark into record store and bookmark list */
		final private void saveBookmark(){
			final String name = m_bmName.getString();
			
			final String url  = m_bmURL.getString().trim();
			
			final String username = m_bmUsername.getString();
			
			final String password = m_bmPassword.getString();
			
			final RssItunesFeed bm = new RssItunesFeed(name, url, username, password);
			
			if (m_addForm) {
				m_bookmarkList.insert(m_addBkmrk, bm.getName(), null);
			} else {
				m_bookmarkList.set(m_curBookmark, bm.getName(), null);
			}
			m_rssFeeds.put(bm.getName(), bm);
		}
		
		/** Respond to commands */
		public void commandAction(final Command c, final Displayable s) {

			/** Save currently added RSS feed's properties */
			if( (c == m_addInsCmd ) || (c == m_addAddCmd ) ||
					(c == m_addAppndCmd )) {
				final int blen = m_bookmarkList.size();
				m_addBkmrk = (m_curBookmark == -1) ? blen : m_curBookmark;
				if( c == m_addAddCmd ){
					if (m_addBkmrk < blen) {
						m_addBkmrk++;
					}
				}
				if (c == m_addAppndCmd ) {
					m_addBkmrk = blen;
				}
				if ((m_addBkmrk < 0) || (m_addBkmrk > blen)) {
					m_addBkmrk = blen;
				}
				saveBookmark();
				m_loadForm.removeRef(this);
				if (m_addForm) { 
					Item[] items = {m_bmName, m_bmURL,
						m_bmUsername, m_bmPassword};
					m_addBMSave = storeValues(items);
				}
				setCurrent( m_bookmarkList );
			}

			/** Save currently edited (or added) RSS feed's properties */
			if( c == m_editOkCmd ){
				saveBookmark();
				m_loadForm.removeRef(this);
				if (m_addForm) { 
					Item[] items = {m_bmName, m_bmURL,
						m_bmUsername, m_bmPassword};
					m_addBMSave = storeValues(items);
				}
				setCurrent( m_bookmarkList );
			}

			/** Clear data. */
			if ( c == m_clearCmd ) {
				m_bmName.setString("");
				m_bmURL.setString("");
				m_bmUsername.setString("");
				m_bmPassword.setString("");
			}
			
			/** Cancel currently edited (or added) RSS feed's properties */
			if( c == m_addCancelCmd ){
				m_loadForm.removeRef(this);
				setCurrent( m_bookmarkList );
			}
			
			/** Put current bookmark URL into URL box.  */
			if( c == m_pasteURLCmd ) {
				initializeURLBox( m_bmURL.getString(), (CommandListener)this );
				setCurrent( m_boxURL );
			}

			/** Paste into URL field from previous form.  */
			if( c == m_boxOkCmd ) {
				m_bmURL.setString( m_boxURL.getString() );
				//#ifdef DMIDP20
				setCurrentItem( m_bmURL );
				//#else
//@				setCurrent( this );
				//#endif
			}
			
			/** Cancel the box go back to the return form.  */
			if( c == m_boxCancelCmd ) {
				//#ifdef DMIDP20
				setCurrentItem( m_bmURL );
				//#else
//@				setCurrent( this );
				//#endif
			}
			

			//#ifdef DJSR75
//@			/** Find bookmark file in file system */
//@			if( c == m_BMFileCmd ) {
//@				if (!JSR75_ENABLED) {
//@					Alert invalidAlert = new Alert(
//@							"JSR-75 not enabled", 
//@							"Find files (JSR-75) not enabled on the phone.",
//@							null,
//@							AlertType.WARNING);
//@					invalidAlert.setTimeout(Alert.FOREVER);
//@					setCurrent( invalidAlert, this );
//@					return;
//@				}
//@				try {
//@					/* Loading files to bookmark from... */
//@					showLoadingFormRsc("text.l.f.bk", this);
//@					reqFindFiles( this, m_bmURL);
//@					wakeUp();
//@				}catch(Throwable t) {
					//#ifdef DLOGGING
//@					logger.severe("RssReaderMIDlet find files ", t);
					//#endif
//@					/** Error while executing find files */
//@					System.out.println("RssReaderMIDlet find files " + t.getMessage());
//@					t.printStackTrace();
//@				}
//@			}
			//#endif
					
			if( c == m_helpCmd ) {
				new Thread(this).start();
			}
		}

		public void run() {
			final HelpForm helpForm = new HelpForm(m_midlet, this);
			helpForm.appendRsc(m_addForm ? "text.abm.help" : "text.ebm.help");
			helpForm.appendItemHelpRsc(m_bmName, "text.bbmc.help");
			helpForm.appendItemHelpRsc(m_bmURL, "text.lbm.help");
			if (m_addForm) {
				helpForm.appendCmdHelpRsc(m_addInsCmd, "text.ibmc.help");
				helpForm.appendCmdHelpRsc(m_addAddCmd, "text.abmc.help");
				helpForm.appendCmdHelpRsc(m_addAppndCmd, "text.pbmc.help");
			}
			setCurrent( helpForm );
		}

	}

	/* Form to show data being loaded.  Save messages and exceptions to
	   allow them to be viewed separately as well as diagnostics for
	   reporting errors. */
	final private class LoadingForm extends PromptForm
	implements CommandListener {
		//#ifdef DMIDP10
//@		private String      m_title;         // Store title.
		//#endif
		private Command     m_loadBackCmd;   // The load form back to prev displayable command
		private Command     m_loadStartCmd;  // The load form start to displayable command
		private Command     m_loadMsgsCmd;   // The load form messages command
		private Command     m_loadDiagCmd;   // The load form diagnostic command
		private Command     m_loadErrCmd;    // The load form error command
		private Command     m_loadQuitCmd;   // The load form quit command
		private Vector m_msgs = new Vector(); // Original messages
		private Vector m_excs = new Vector(); // Only errors
		private Displayable m_disp;

		/* Constructor */
		LoadingForm(final String title, final Displayable disp) {
			super(m_midlet, title);
			//#ifdef DMIDP10
//@			this.m_title = title;
			//#endif
			if (disp != null) {
				m_disp = disp;
			}
			m_loadBackCmd       = UiUtil.getCmdRsc("cmd.back", Command.BACK, 2);
			/* Messages */
			m_loadMsgsCmd       = UiUtil.getCmdRsc("cmd.msg", Command.SCREEN, 3);
			/* Errors */
			m_loadErrCmd        = UiUtil.getCmdRsc("cmd.err", Command.SCREEN, 4);
			/* Diagnostics */
			m_loadDiagCmd       = UiUtil.getCmdRsc("cmd.diag", Command.SCREEN, 5);
			if (disp != null) {
				super.addCommand( m_loadBackCmd);
			}
			super.addCommand( m_loadMsgsCmd );
			super.addCommand( m_loadErrCmd );
			super.addCommand( m_loadDiagCmd );
			super.setCommandListener( this );
		}

		/* Add start command and where it goes when clicked.  */
		public void addStartCmd(final Displayable disp) {
			m_disp = disp;
			if (disp != null) {
				/* Start */
				m_loadStartCmd = UiUtil.getCmdRsc("cmd.st", Command.SCREEN, 1);
				super.addCommand( m_loadStartCmd );
			}
		}

		/* Add quit command used for errors during exit. */
		public void addQuit() {
			/* Quit */
			m_loadQuitCmd = UiUtil.getCmdRsc("cmd.q", Command.EXIT, 1);
			super.addPromptCommand( m_loadQuitCmd,
					ResourceProviderME.get("text.w.q") );
		}

		/** Respond to commands */
		public void commandAction(final Command c, final Displayable s) {
			//#ifdef DTESTUI
//@			super.outputCmdAct(c, s);
			//#endif

			if(( c == m_loadBackCmd ) || ( c == m_loadStartCmd )){
				setCurrent( m_disp );
			}

			if( c == m_loadQuitCmd ){
				try {
					destroyApp(true);
				} catch (Exception e) {
				}
				notifyDestroyed();
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
		/* Show errors and diagnostics. */
		final private void showMsgs() {
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
//@				logger.severe("showMsgs", t);
				//#endif
				/** Error while executing constructor */
				System.out.println("showMsgs " + t.getMessage());
				t.printStackTrace();
			}
		}

		/* Show errors and diagnostics. */
		final private void showErrMsgs(final boolean showErrsOnly) {
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
//@				logger.severe("showErrMsgs", t);
				//#endif
				/** Error while executing constructor */
				System.out.println("showErrMsgs " + t.getMessage());
				t.printStackTrace();
			}
		}

		/* Append message to form and save in messages. */
		final public void appendMsg(final String msg) {
			if (msg != null) {
				super.append(msg);
				m_msgs.addElement(msg);
			}
		}

		/* Add exception. */
		final public void addExc(final Throwable exc) {
			m_excs.addElement(exc);
		}

		/* Check for exceptions. */
		final public boolean hasExc() {
			return (m_excs.size() > 0);
		}

		/* Remove reference to displayable to free memory. */
		final public void removeRef(final Displayable disp) {
			synchronized (this) {
				if (m_disp == disp) {
					m_disp = m_bookmarkList;
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("Ref removed " + disp);}
					//#endif
				}
			}
		}

		//#ifdef DMIDP10
//@		public String getTitle() {
//@			return m_title;
//@		}
		//#endif

	}

}
