/*
   TODO handle OutOfMemoryError
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
//@import com.substanceofcode.rssreader.businessentities.CompatibilityRssItem1;
//#elifdef DCOMPATIBILITY2
//@import com.substanceofcode.rssreader.businessentities.CompatibilityRssFeed2;
//@import com.substanceofcode.rssreader.businessentities.CompatibilityRssItem2;
//#endif
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.rssreader.businesslogic.Controller;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.HTMLAutoLinkParser;
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.utils.Settings;
import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.SortUtil;
import com.substanceofcode.utils.CauseException;
import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

//#ifdef DMIDP20
import javax.microedition.lcdui.ItemCommandListener;
//#endif
//#ifdef DJSR238
//@import javax.microedition.global.ResourceManager;
//#endif

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//@import net.sf.jlogmicro.util.logging.FormHandler;
//@import net.sf.jlogmicro.util.logging.RecStoreHandler;
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
		//#ifdef DMIDP20
        ItemCommandListener,
		//#endif
        Runnable {
    
    // Attributes
    private Display     m_display;          // The display for this MIDlet
    private Settings    m_settings;         // The settings
    private RssReaderSettings m_appSettings;// The application settings
    private Hashtable   m_rssFeeds;         // The bookmark URLs
    private Thread      m_netThread;        // The thread for networking
    private boolean     m_debugOutput = false; // Flag to write to output for test
    private boolean     m_process = true;   // Flag to continue looping
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_saveBookmarks;    // The save bookmarks flag
    private boolean     m_exit;             // The exit application flag
    private boolean     m_getModPage;       // The noticy flag for modified HTTP
    private boolean     m_refreshAllFeeds;  // The notify flag for all feeds
    private boolean     m_refreshUpdFeeds;  // The notify flag for updated feeds
    private boolean     m_getFeedList;      // The noticy flag for list parsing
    private boolean     m_getFeedTitleList; // The noticy flag for title/list parsing
    private boolean     m_getImportFile;    // The file flag for file browsing
    private boolean     m_getBMFile;       // The noticy flag for modified HTTP
    private boolean     m_sortUnread = false;
	//#ifdef DTESTUI
//@    private int         m_headerIndex = -1; // Index in headers to auto test
//@    // Index in bookmarks to auto test by opening in edit
//@	// This counts up until the bookmark size is reached.
//@    private int         m_bookmarkIndex = -1;
//@    private int         m_bookmarkLastIndex = -1; // Last place when import current was selected
	//#endif
	// Tells us if this is the first time program was used.  This is
	// done by seeing if max item count is set.  We also set it after
	// showing the about.
    private boolean     m_firstTime = false;
    private boolean     m_prevOpen1st = true;
    private boolean     m_itunesEnabled = false;
    private FeedListParser m_listParser;    // The feed list parser
	//#ifdef DLOGGING
//@    private boolean fineLoggable;
//@    private boolean finestLoggable;
	//#endif
    private int         m_maxRssItemCount;  // The maximum item count in a feed
	// This is a mark (icon) next to unread items (except on unread items
	// screen).  Given that many screens are small, it is optional as 
	// we don't want to reduce space for text.
    private Image           m_unreadImage;
    private Vector          m_unreadItems = new Vector();
    
	private int             m_addBkmrk; // Place to add (insert) imported bookmarks
    // Currently selected bookmark
    private int             m_curBookmark;  // The currently selected item
    private RssFeedParser   m_curRssParser; // The currently selected RSS
	//#ifdef DLOGGING
//@	private RecordStore     m_recStore = null; // Rec store
	//#endif
    
    // GUI items
    private PromptList  m_bookmarkList;     // The bookmark list
    private List        m_headerList;       // The header list
    private List        m_unreadHeaderList;    // The header list for unread items
    private List        m_itemRrnForm;      // The list to return from for item
	//#ifdef DJSR75
//@    private KFileSelectorMgr m_fileSelectorMgr;// The list to return from for item
	//#endif
    private Form        m_displayDtlForm;   // The display details form
    private Form        m_itemForm;         // The item form
    private Form        m_addNewBMForm;     // The add new bookmark form
    private LoadingForm m_loadForm;         // The "loading..." form
    private Form        m_importFeedsForm;  // The import feed list form
    private TextField   m_bmName;           // The RSS feed name field
    private TextField   m_bmURL;            // The RSS feed URL field
    private TextField   m_bmUsername;       // The RSS feed username field
    private TextField   m_bmPassword;       // The RSS feed password field
    private TextField   m_feedListURL;      // The feed list URL field
    private TextField   m_feedNameFilter;   // The feed name filter string
    private TextField   m_feedURLFilter;    // The feed URL filter string
    private TextField   m_feedListUsername; // The feed list username
    private TextField   m_feedListPassword; // The feed list password
    private TextField   m_boxRtnItem;       // The item to return to
    private Form        m_boxRtnForm;       // The form to return to
    private TextBox     m_boxURL;           // The feed list URL box
    private ChoiceGroup m_importFormatGroup;// The import type choice group
    private ChoiceGroup m_importTitleGroup; // The import title choice group
    private ChoiceGroup m_importHTMLGroup;  // The import HTML redirect choice group
    private SettingsForm m_settingsForm;    // The settings form
	//#ifdef DTESTUI
//@    private TestingForm m_testingForm;    // The testing form
	//#endif
    
    // Commands
    private Command     m_addOkCmd;         // The OK command
	//#ifdef DTESTUI
//@	private Command     m_testRssCmd;       // Tet UI rss headers command
//@	private Command     m_testImportCmd;      // Tet UI rss opml command
//@	private Command     m_testBMCmd;        // Tet UI bookmarks list command
//@	private Command     m_testRtnCmd;       // Tet UI return to prev command
	//#endif
    private Command     m_addCancelCmd;     // The Cancel command
    private Command     m_pasteURLCmd;      // The allow paste command
    private Command     m_BMFileCmd;        // The find files command
    private Command     m_exitCommand;      // The exit command
    private Command     m_saveCommand;      // The save without exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_readUnreadItems;  // The read unread items command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
    private Command     m_backCommand;      // The back to header list command
    private Command     m_pasteImportURLCmd;// The paste command
	//#ifdef DMIDP20
    private Command     m_openLinkCmd;      // The open link command
    private Command     m_openEnclosureCmd; // The open enclosure command
	//#endif
    private Command     m_copyLinkCmd;    // The copy link command
    private Command     m_copyEnclosureCmd; // The copy enclosure command
    private Command     m_openHeaderCmd;    // The open header command
    // The show feed/bookmark details command for Itunes feed
    private Command     m_bookmarkDetailsCmd;   // The show feed details
    private Command     m_loadBackCmd;// The load form back to prev displayable command
    private Command     m_loadDiagCmd;      // The load form diagnostic command
    private Command     m_loadErrCmd;        // The load form error command
    private Command     m_backHeaderCmd;    // The back to bookmark list command
    private Command     m_openUnreadHdrCmd;    // The open new header command
    private Command     m_sortUnreadItemsCmd; // The open new header command
    private Command     m_backUnreadHdrCmd;    // The back to bookmark list command
    private Command     m_updateCmd;        // The update headers command
    private Command     m_updateModCmd;     // The update modified headers command
    private Command     m_importFeedListCmd;// The import feed list command
	//#ifdef DTEST
//@    private Command     m_importCurrFeedListCmd;// The import feed list command and default current seleected feed
	//#endif
	//#ifdef DTESTUI
//@    private Command     m_testEncCmd;     // The test encoding
	//#endif
	//#ifdef DLOGGING
//@    private Command     m_debugCmd; // The back to bookmark list command
//@	                                      // from debug form
//@    private Command     m_backFrDebugCmd; // The back to bookmark list command
//@	                                      // from debug form
	//#endif
    private Command     m_importInsCmd;   // The import before the current point?
    private Command     m_importAddCmd;   // The import after the current point?
    private Command     m_importAppndCmd; // The import append
    private Command     m_importCancelCmd;  // The Cancel command for importing
    private Command     m_importFileCmd;    // The find files command for importing
    private Command     m_boxOkCmd;         // The OK command for import box URL
    private Command     m_boxCancelCmd;     // The Cancel command for import box URL
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
//@    private javax.microedition.lcdui.Form m_debug;
//@    private Logger logger;
	//#endif
    
    public RssReaderMIDlet() {
        m_display = Display.getDisplay(this);
        
		//#ifdef DTESTUI
//@		TestOutput.init(System.out, "UTF-8");
		//#endif
		//#ifdef DLOGGING
		//#ifdef DCLDCV10
//@        LogManager.getLogManager().readConfiguration(this);
		//#else
//@        LogManager.readConfiguration(this);
		//#endif
//@        logger = Logger.getLogger("RssReaderMIDlet");
//@		for (Enumeration eHandlers = logger.getParent().getHandlers().elements();
//@				eHandlers.hasMoreElements();) {
//@			Object ohandler = eHandlers.nextElement();
//@			if (ohandler instanceof FormHandler) {
//@				m_debug = ((FormHandler)ohandler).getForm();
//@				logger.finest("form=" + m_debug);
//@			}
//@		}
//@        logger = Logger.getLogger("RssReaderMIDlet");
//@        logger.info("RssReaderMIDlet started.");
//@        logger.info("RssReaderMIDlet has form handler=" + (m_debug != null));
		//#endif
		try {

			/** Initialize controller */
			m_controller = new Controller( this );
			
			m_appSettings = RssReaderSettings.getInstance(this);
			m_itunesEnabled = m_appSettings.getItunesEnabled();

			/** Initialize commands */
			m_addOkCmd          = new Command("OK", Command.OK, 1);
			//#ifdef DTESTUI
//@			m_testRssCmd        = new Command("Test headers/items", Command.SCREEN, 9);
//@			m_testImportCmd     = new Command("Test bookmarks imported", Command.SCREEN, 9);
//@			m_testBMCmd         = new Command("Test bookmarks shown", Command.SCREEN, 9);
//@			m_testRtnCmd        = new Command("Test go back to last", Command.SCREEN, 10);
			//#endif
			m_addCancelCmd      = new Command("Cancel", Command.CANCEL, 2);
			m_BMFileCmd         = new Command("Find files", Command.SCREEN, 3);
			m_pasteURLCmd       = new Command("Allow paste", Command.SCREEN, 4);
			m_backCommand       = new Command("Back", Command.BACK, 1);
			m_exitCommand       = new Command("Exit", Command.SCREEN, 5);
			m_saveCommand       = new Command("Save without exit", Command.SCREEN, 4);
			m_addNewBookmark    = new Command("Add new feed", Command.SCREEN, 2);
			m_openBookmark      = new Command("Open feed", Command.SCREEN, 1);
			m_readUnreadItems   = new Command("Read unread items", Command.SCREEN, 1);
			m_editBookmark      = new Command("Edit feed", Command.SCREEN, 2);
			m_delBookmark       = new Command("Delete feed", Command.SCREEN, 3);
			m_openHeaderCmd     = new Command("Open item", Command.SCREEN,
					(m_appSettings.getFeedListOpen() ? 1 : 2));
			m_bookmarkDetailsCmd    = new Command("Show bookmark details",
					Command.SCREEN, 4);
			m_openUnreadHdrCmd  = new Command("Open item", Command.SCREEN, 1);
			m_sortUnreadItemsCmd = new Command("Date sort items",
											   Command.SCREEN, 1);
			//#ifdef DMIDP20
			m_openLinkCmd       = new Command("Open link", Command.SCREEN, 2);
			m_openEnclosureCmd  = new Command("Open enclosure", Command.SCREEN, 2);
			//#endif
			m_copyLinkCmd       = new Command("Copy link", Command.SCREEN, 1);
			m_copyEnclosureCmd  = new Command("Copy enclosure", Command.SCREEN, 1);
			m_loadBackCmd       = new Command("Back", Command.BACK, 3);
			m_loadErrCmd        = new Command("Errors", Command.SCREEN, 1);
			m_loadDiagCmd       = new Command("Diagnostics", Command.SCREEN, 2);
			m_backUnreadHdrCmd  = new Command("Back", Command.BACK, 2);
			m_updateCmd         = new Command("Update feed", Command.SCREEN, 2);
			m_updateModCmd      = new Command("Update modified feed",
											  Command.SCREEN, 2);
			m_importFeedListCmd = new Command("Import feeds", Command.SCREEN, 3);
			//#ifdef DTEST
//@			m_importCurrFeedListCmd = new Command("Import current feeds", Command.SCREEN, 3);
			//#endif
			//#ifdef DMIDP20
			m_importInsCmd      = new Command("Insert import",
					"Insert current import", Command.SCREEN, 1);
			m_importAddCmd      = new Command("Add import",
					"Add current import", Command.SCREEN, 2);
			m_importAppndCmd    = new Command("Append import",
					"Append end import", Command.SCREEN, 3);
			//#else
//@			m_importInsCmd      = new Command("Insert import", Command.SCREEN, 1);
//@			m_importAddCmd      = new Command("Add import", Command.SCREEN, 2);
//@			m_importAppndCmd    = new Command("Append import", Command.SCREEN, 3);
			//#endif
			m_importCancelCmd   = new Command("Cancel", Command.CANCEL, 4);
			m_importFileCmd     = new Command("Find files", Command.SCREEN, 5);
			m_pasteImportURLCmd = new Command("Allow paste", Command.SCREEN, 6);
			m_boxOkCmd          = new Command("OK", Command.OK, 1);
			m_boxCancelCmd      = new Command("Cancel", Command.CANCEL, 2);
			m_settingsCmd       = new Command("Settings", Command.SCREEN, 4);
			m_aboutCmd          = new Command("About", Command.SCREEN, 4);
			m_updateAllCmd      = new Command("Update all", Command.SCREEN, 2);
			m_updateAllModCmd   = new Command("Update modified all",
											  Command.SCREEN, 2);
			//#ifdef DTESTUI
//@			m_testEncCmd        = new Command("Testing Form", Command.SCREEN, 4);
			//#endif

		//#ifdef DLOGGING
//@			m_debugCmd          = new Command("Debug Log", Command.SCREEN, 4);
//@			m_backFrDebugCmd    = new Command("Back", Command.BACK, 2);
		//#endif
			
			m_getPage = false;
			m_getModPage = false;
			m_refreshAllFeeds = false;
			m_refreshUpdFeeds = false;
			m_getFeedList = false;
			m_getFeedTitleList = false;
			m_getImportFile = false;
			m_getBMFile = false;
			m_curBookmark = -1;
			m_maxRssItemCount = 10;
			
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
			
			initializeLoadingForm("Loading items...", null);
			m_display.setCurrent( m_loadForm );

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
		}
    }
    
	private void initForms() {
		try {
			/** Initialize GUI items */
			initializeBookmarkList();
			initializeAddBookmarkForm();
			initializeHeadersList();
			initializeUnreadHhdrsList();
			//initializeLoadingForm();
			initializeImportForm();
		//#ifdef DLOGGING
//@			if (m_debug != null) {
//@				initializeDebugForm();
//@			}
		//#endif
			m_settingsForm = new SettingsForm(this);
			//#ifdef DTESTUI
//@			m_testingForm = new TestingForm(this);
			//#endif
			
			if( m_firstTime ) {
				try {
					m_firstTime = false;
					// Set Max item count to default so that it is initialized.
					m_appSettings.setMaximumItemCountInFeed(
							m_appSettings.getMaximumItemCountInFeed());
					saveBkMrkSettings(false);
					Alert m_about = getAbout();
					m_display.setCurrent( m_about, m_bookmarkList );
				} catch(Exception e) {
					System.err.println("Error while getting/updating settings: " + e.toString());
					m_display.setCurrent( m_bookmarkList );
				}
			} else {
				m_display.setCurrent( m_bookmarkList );
			}

		}catch(Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("initForms ", t);
			//#endif
			/** Error while initializing forms */
			System.out.println("initForms " + t.getMessage());
			t.printStackTrace();
		}
    }
    
    /** Get application settings */
    public RssReaderSettings getSettings() {
        return m_appSettings;
    }
    
    /** Show bookmark list */
    public void showBookmarkList() {
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("before m_prevOpen1st,m_itunesEnabled=" + m_prevOpen1st + "," + m_itunesEnabled);}
		//#endif
		if (m_prevOpen1st != m_appSettings.getFeedListOpen()) {
			initializeHeadersList();
		}
		m_itunesEnabled = m_appSettings.getItunesEnabled();
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("after m_prevOpen1st,m_itunesEnabled=" + m_prevOpen1st + "," + m_itunesEnabled);}
		//#endif
        m_display.setCurrent( m_bookmarkList );
    }
    
    /** Load bookmarks from record store */
    private void initializeBookmarkList() {
        try {
            m_bookmarkList = new PromptList(this, "Bookmarks", List.IMPLICIT);
            m_bookmarkList.addCommand( m_exitCommand );
            m_bookmarkList.addCommand( m_saveCommand );
            m_bookmarkList.addCommand( m_addNewBookmark );
            m_bookmarkList.addCommand( m_openBookmark );
            m_bookmarkList.addCommand( m_readUnreadItems );
            m_bookmarkList.addCommand( m_editBookmark );
            m_bookmarkList.addPromptCommand( m_delBookmark,
					                         "Are you sure you want to delete?" );
            m_bookmarkList.addCommand( m_importFeedListCmd );
			//#ifdef DTEST
//@            m_bookmarkList.addCommand( m_importCurrFeedListCmd );
			//#endif
            m_bookmarkList.addCommand( m_settingsCmd );
            m_bookmarkList.addCommand( m_updateAllCmd );
            m_bookmarkList.addCommand( m_updateAllModCmd );
			//#ifdef DTESTUI
//@            m_bookmarkList.addCommand( m_testBMCmd );
//@            m_bookmarkList.addCommand( m_testRtnCmd );
			//#endif
			//#ifdef DTESTUI
//@			m_bookmarkList.addCommand( m_testEncCmd );
			//#endif
	//#ifdef DLOGGING
//@			if (m_debug != null) {
//@				m_bookmarkList.addCommand( m_debugCmd );
//@			}
	//#endif
            m_bookmarkList.addCommand( m_aboutCmd );
            m_bookmarkList.setCommandListener( this );
            
            int i = 1;
            
            m_rssFeeds = new Hashtable();
			for (int ic = 1; ic < m_settings.MAX_REGIONS; ic++) {
				boolean stop = false;
				String vers = m_settings.getStringProperty(ic,
						m_settings.SETTINGS_NAME, "");
				boolean firstSettings =
					 vers.equals(m_settings.FIRST_SETTINGS_VERS);
				boolean itunesCapable =
					 vers.equals(m_settings.ITUNES_CAPABLE_VERS);
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("Settings region,vers,firstSettings,itunescapable=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable);}
				//#endif
				//#ifdef DTEST
//@				if (m_debugOutput) System.out.println("Settings region,vers,firstSettings,itunescapable=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable);
				//#endif
				String bms = m_settings.getStringProperty(ic, "bookmarks", "");
				// Save memory by setting bookmarks to "" now that
				// we will convert them to objects.
				m_settings.setStringProperty("bookmarks", "");
				
				if(bms.length()>0) {
					do{
						
						String part = "";
						if(bms.indexOf('^')>0) {
							part = bms.substring(0, bms.indexOf('^'));
						}
						bms = bms.substring(bms.indexOf('^')+1);
						if(part.length()>0) {
							// TODO change to check vers
							//#ifdef DCOMPATIBILITY1
//@							RssFeed bm1 = new CompatibilityRssFeed1( part );
//@							RssItunesFeed bm = new RssItunesFeed( bm1 );
							//#elifdef DCOMPATIBILITY2
//@							RssFeed bm2 = new CompatibilityRssFeed2( part );
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
            }
			// Reset internal region to 0.
			m_settings.getStringProperty(0, "bookmarks", "");
        } catch(Exception e) {
			//#ifdef DLOGGING
//@			logger.severe("Error while initializing bookmark list: ", e);
			//#endif
            System.err.println("Error while initializing bookmark list: " + e.toString());
        } catch(OutOfMemoryError e) {
			//#ifdef DLOGGING
//@			logger.severe("Error while initializing bookmark list: ", e);
			//#endif
            System.err.println("Error while initializing bookmark list: " + e.toString());
            Alert memoryAlert = new Alert(
                    "Out of memory", 
                    "Loading bookmarks without all news items.",
                    null,
                    AlertType.WARNING);
			memoryAlert.setTimeout(Alert.FOREVER);
            m_display.setCurrent( memoryAlert, m_loadForm );
		}catch(Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("Error while initializing bookmark list: ", t);
			//#endif
			/** Error while parsing RSS feed */
			System.out.println("Error while initializing bookmark list: " + t.getMessage());
		}
    }
    
    /** Initialize loading form */
    private void initializeLoadingForm(final String desc,
									   Displayable disp) {
        m_loadForm = new LoadingForm("Loading");
        m_loadForm.append( desc + "\n" );
		if (disp != null) {
			m_loadForm.addCommand( m_loadErrCmd );
			m_loadForm.addCommand( m_loadDiagCmd );
			m_loadForm.addBackCommand( m_loadBackCmd, disp );
		}
        m_loadForm.setCommandListener( m_loadForm );
    }
    
    /** Initialize bookmark adding form */
    private void initializeAddBookmarkForm() {
        m_addNewBMForm = new Form("New bookmark");
        m_bmName = new TextField("Name", "", 64, TextField.ANY);
        m_bmURL  = new TextField("URL", "http://", 256, TextField.URL);
        m_bmUsername  = new TextField("Username (optional)", "", 64, TextField.ANY);
        m_bmPassword  = new TextField("Password (optional)", "", 64, TextField.PASSWORD);
        m_addNewBMForm.append( m_bmName );
        m_addNewBMForm.append( m_bmURL );
        m_addNewBMForm.append( m_bmUsername );
        m_addNewBMForm.append( m_bmPassword );
        m_addNewBMForm.addCommand( m_addOkCmd );
        m_addNewBMForm.addCommand( m_addCancelCmd );
		//#ifdef DJSR75
//@        m_addNewBMForm.addCommand(m_BMFileCmd);
		//#endif
        m_addNewBMForm.setCommandListener(this);
    }
    
    /** Update bookmark adding/editing form */
    private void updateAddBookmarkForm(final String title) {
        m_addNewBMForm.setTitle(title);
        boolean useTextBox = m_appSettings.getUseTextBox();
		if (useTextBox) {
			//#ifdef DMIDP20
			m_bmURL.setItemCommandListener(this);
			m_bmURL.addCommand(m_pasteURLCmd);
			//#else
//@			m_addNewBMForm.addCommand(m_pasteURLCmd);
			//#endif
		} else {
			//#ifdef DMIDP20
			m_bmURL.setItemCommandListener(null);
			m_bmURL.removeCommand(m_pasteURLCmd);
			//#else
//@			m_addNewBMForm.removeCommand(m_pasteURLCmd);
			//#endif
		}
	}

    /** Initialize import form */
    private void initializeImportForm() {
        m_importFeedsForm = new Form("Import feeds");
        String url = m_appSettings.getImportUrl();
        if(url.length()==0) {
            url = "http://";
        }
		m_feedListURL = new TextField("URL", url, 256, TextField.URL);
		m_importFeedsForm.append(m_feedListURL);
        
        String[] formats = {"OPML", "line by line", "HTML OPML Auto link",
						    "HTML RSS Auto links", "HTML Links"};
        m_importFormatGroup = new ChoiceGroup("Format", ChoiceGroup.EXCLUSIVE, formats, null);
        m_importFeedsForm.append(m_importFormatGroup);
        
        m_feedNameFilter = new TextField("Name filter string (optional)", "", 256, TextField.ANY);
        m_importFeedsForm.append(m_feedNameFilter);
        m_feedURLFilter = new TextField("URL filter string (optional)", "", 256, TextField.ANY);
        m_importFeedsForm.append(m_feedURLFilter);
        
        String username = m_appSettings.getImportUrlUsername();
        m_feedListUsername  = new TextField("Username (optional)", username, 64, TextField.ANY);
        m_importFeedsForm.append(m_feedListUsername);
        
        String password = m_appSettings.getImportUrlPassword();
        m_feedListPassword  = new TextField("Password (optional)", password, 64, TextField.PASSWORD);
        m_importFeedsForm.append(m_feedListPassword);
        String[] titleInfo =
				{"Skip feed with missing title",
			     "Get missing titles from feed"};
        m_importTitleGroup  = new ChoiceGroup("Missing title (optionl)",
				ChoiceGroup.EXCLUSIVE, titleInfo, null);
        m_importFeedsForm.append(m_importTitleGroup);
        String[] HTMLInfo =
				{"Redirect if HTML (ignored for HTML link import)",
			     "Treat HTML as import"};
        m_importHTMLGroup  =
			new ChoiceGroup("Treat HTML mime type as valid import (optional)",
				ChoiceGroup.EXCLUSIVE, HTMLInfo, null);
        m_importFeedsForm.append(m_importHTMLGroup);
        
        m_importFeedsForm.addCommand( m_importInsCmd );
        m_importFeedsForm.addCommand( m_importAddCmd );
        m_importFeedsForm.addCommand( m_importAppndCmd );
        m_importFeedsForm.addCommand( m_importCancelCmd );
		//#ifdef DJSR75
//@        m_importFeedsForm.addCommand( m_importFileCmd );
		//#endif
		//#ifdef DTESTUI
//@        m_importFeedsForm.addCommand( m_testImportCmd );
		//#endif
        m_importFeedsForm.setCommandListener(this);
    }
    
    /** Update import form */
    private void updateImportForm() {
        boolean useTextBox = m_appSettings.getUseTextBox();
		if (useTextBox) {
			//#ifdef DMIDP20
			m_feedListURL.setItemCommandListener(this);
			m_feedListURL.addCommand(m_pasteImportURLCmd);
			//#else
//@			m_importFeedsForm.addCommand(m_pasteImportURLCmd);
			//#endif
		} else {
			//#ifdef DMIDP20
			m_feedListURL.setItemCommandListener(null);
			m_feedListURL.removeCommand(m_pasteImportURLCmd);
			//#else
//@			m_importFeedsForm.removeCommand(m_pasteImportURLCmd);
			//#endif
		}
	}

    /** Initialize URL text Box */
    private void initializeURLBox(final String url) {
		m_boxURL = new TextBox("URL", url,
								256, TextField.URL);
        m_boxURL.addCommand( m_boxOkCmd );
        m_boxURL.addCommand( m_boxCancelCmd );
        m_boxURL.setCommandListener(this);
    }
    
	//#ifdef DLOGGING
//@    public void initializeDebugForm() {
//@        m_debug.addCommand( m_backFrDebugCmd );
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
//@				if ((m_headerIndex < m_headerList.size()) &&
//@				    (m_headerIndex >= 0)) {
//@					if (m_headerList.getSelectedIndex() >= 0) {
//@						m_headerList.setSelectedIndex(
//@								m_headerList.getSelectedIndex(), false);
//@					}
//@					m_headerList.setSelectedIndex(m_headerIndex, true);
//@					m_headerIndex++;
//@					commandAction(m_openHeaderCmd, m_headerList);
//@					if (m_headerIndex >= m_headerList.size()) {
//@						System.out.println("Test UI Test Rss items last");
//@						m_headerIndex = -1;
//@					}
//@				}
				//#endif
                if( m_getPage || m_getModPage ) {
                    try {
                        /** Get RSS feed */
                        int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
                        m_curRssParser.parseRssFeed( m_getModPage,
								maxItemCount );
                        fillHeadersList();
						updateHeadersList();
                        m_display.setCurrent( m_headerList );
						//#ifdef DTEST
//@						RssItunesFeed feed = m_curRssParser.getRssFeed();
//@						String store = feed.getStoreString(true, true);
//@						RssItunesFeed feed2 = RssItunesFeed.deserialize(
//@								true, store );
//@						boolean feedEq = feed.equals(feed2);
						//#ifdef DLOGGING
//@						if (finestLoggable) {logger.finest("feed1,2 eq=" + feedEq);}
						//#endif
//@						if (!feedEq) {
//@							System.out.println("feed=" + feed + "," + feed.toString());
//@							System.out.println("feed store=" + store);
//@						}
						//#endif
                    }catch(Exception e) {
						CauseException ce = new CauseException(
                        		"\nError parsing feed on:\n" +
                                m_curRssParser.getRssFeed().getUrl(), e);
                        m_loadForm.addExc(ce);
						//#ifdef DLOGGING
//@						logger.severe(ce.getMessage(), e);
						//#endif
                        /** Error while parsing RSS feed */
                        System.out.println("Error: " + e.getMessage());
                        m_loadForm.append(ce.getMessage());
                        m_display.setCurrent( m_loadForm );
                    }catch(Throwable t) {
						CauseException ce = new CauseException(
                        		"\nError parsing feed on:\n" +
                                m_curRssParser.getRssFeed().getUrl(), t);
                        m_loadForm.addExc(ce);
						//#ifdef DLOGGING
//@						logger.severe(ce.getMessage(), t);
						//#endif
                        /** Error while parsing RSS feed */
                        System.out.println("Throwable Error: " + t.getMessage());
                        m_loadForm.append(ce.getMessage());
                        m_display.setCurrent( m_loadForm );
                    }
                    m_getPage = false;
                    m_getModPage = false;
                }

                if( m_refreshAllFeeds || m_refreshUpdFeeds ) {
                    try{
						boolean errFound = false;
                        int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
                        Enumeration feedEnum = m_rssFeeds.elements();
                        while(feedEnum.hasMoreElements()) {
                            RssItunesFeed feed = (RssItunesFeed)feedEnum.nextElement();
                            try{
                                m_loadForm.append(feed.getName() + "...");
                                RssFeedParser parser = new RssFeedParser( feed );
                                parser.parseRssFeed( m_refreshUpdFeeds,
										maxItemCount);
                                m_loadForm.append("ok\n");
                            } catch(Exception ex) {
								CauseException ce = new CauseException(
										"Error parsing feed " + feed.getName(),
										ex);
								m_loadForm.addExc(ce);
								//#ifdef DLOGGING
//@								logger.severe(ce.getMessage(), ex);
								//#endif
                                m_loadForm.append("Error\n");
								System.out.println(ce.getMessage());
								errFound = true;
                            }
                        }
						if (errFound) {
							m_display.setCurrent( m_loadForm );
						} else {
							m_display.setCurrent( m_bookmarkList );
						}
                    } catch(Exception ex) {
						//#ifdef DLOGGING
//@						logger.severe("Error parsing feeds from:\n" +
//@                                m_curRssParser.getRssFeed().getUrl(), ex);
						//#endif
						CauseException ce = new CauseException(
                        		"\nError updating feeds", ex);
                        m_loadForm.append(ce.getMessage());
                        m_loadForm.addExc(ce);
                        m_display.setCurrent( m_loadForm );
                    } catch(Throwable t) {
						CauseException ce = new CauseException(
                        		"\nError updating feeds", t);
						//#ifdef DLOGGING
//@						logger.severe(ce.getMessage(), t);
						//#endif
                        m_loadForm.append(ce.getMessage());
                        System.out.println("Throwable " + ce.getMessage());
                        m_loadForm.addExc(ce);
                        m_display.setCurrent( m_loadForm );
					} finally {
						m_refreshAllFeeds = false;
						m_refreshUpdFeeds = false;
                    }
                }

                if( m_getFeedList ) {
                    try {
                        if(m_listParser == null) {
							/* If we get here, it's beause the user
							   pressed the menu twice.  */
                            m_display.setCurrent( m_bookmarkList );
							m_getFeedList      = false;
							m_getFeedTitleList = false;
						} else if(m_listParser.isReady()) {
							//addFeedList();
	/** Add from feed list (from import). */
	//void addFeedList() throws CauseException, Exception {
		// Feed list parsing is ready
		System.out.println("Feed list parsing is ready");
		if(!m_listParser.isSuccessfull()) {
			throw m_listParser.getEx();
		}
		RssItunesFeed[] feeds = m_listParser.getFeeds();
		boolean notesShown = false;
		for(int feedIndex=0; feedIndex<feeds.length; feedIndex++) {
			String name = feeds[feedIndex].getName();
			System.out.println("Adding: " + name);
			// If no title (name) and we are getting the title from the
			// feed being imported, parse the name(title) only.
			if (((name == null) || (name.length() == 0)) && m_getFeedTitleList) {
				RssItunesFeed feed = feeds[feedIndex];
				RssFeedParser fparser = new RssFeedParser( feed );
				m_loadForm.append("Loading title for " +
						"feed " + feed.getUrl());
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("Getting title for url=" + feed.getUrl());}
				//#endif
				fparser.setGetTitleOnly(true);
				/** Get RSS feed */
				int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
				try {
					fparser.parseRssFeed( false, maxItemCount );
					name = feed.getName();
					m_loadForm.append("ok\n");
				} catch(Exception ex) {
					CauseException ce = new CauseException(
							"Error loading title for feed " + feed.getUrl(),
							ex);
					//#ifdef DLOGGING
//@					logger.severe(ce.getMessage(), ex);
					//#endif
					m_loadForm.append("Error\n");
					m_loadForm.addExc(ce);
					notesShown = true;
				}
			}
			if((name != null) && (name.length()>0)) {
				if(!m_rssFeeds.containsKey( name )) {
					m_rssFeeds.put( name, feeds[feedIndex] );
					m_bookmarkList.insert(m_addBkmrk++, name, null);
				} else {
					m_loadForm.append("Feed already exists with name " + name +
							".  Existing feed not updated." );
					notesShown = true;
				}
			}
		}
		if (notesShown) {
			m_display.setCurrent( m_loadForm );
		} else {
			m_display.setCurrent( m_bookmarkList );
		}
		m_getFeedList      = false;
		m_getFeedTitleList = false;
	//}

                        } else {
							//#ifndef DTESTUI
                            if (m_debugOutput) System.out.println("Feed list parsing isn't ready");
							//#endif
                        }
                    } catch(Exception ex) {
						CauseException ce = new CauseException(
								"Error importing feeds from " +
                                m_listParser.getUrl() + " " +
								ex.getMessage(), ex);
						m_loadForm.addExc(ce);
                        // TODO: Add exception handling
						//#ifdef DLOGGING
//@						logger.severe(ce.getMessage(), ex);
						//#endif
                        System.err.println(ce.getMessage());
						ex.printStackTrace();
						m_loadForm.append(ce.getMessage());
						m_display.setCurrent( m_loadForm );
						m_getFeedList      = false;
						m_getFeedTitleList = false;
						// TODO empty list parser m_listParser = null;
                    } catch(Throwable t) {
						CauseException ce = new CauseException(
								"Error importing feeds from " +
                                m_listParser.getUrl() + " " +
								t.getMessage(), t);
						m_loadForm.addExc(ce);
                        // TODO: Add exception handling
						//#ifdef DLOGGING
//@						logger.severe(ce.getMessage(), t);
						//#endif
                        System.err.println("Throwable " + ce.getMessage());
						t.printStackTrace();
						m_loadForm.append(ce.getMessage());
						m_display.setCurrent( m_loadForm );
						m_getFeedList      = false;
						m_getFeedTitleList = false;
						// TODO empty list parser m_listParser = null;
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
//@                if( m_getBMFile || m_getImportFile ) {
//@                    try {
//@						if (m_fileSelectorMgr == null) {
//@							m_fileSelectorMgr = new KFileSelectorMgr();
//@							m_fileSelectorMgr.doLaunchSelector(this,
//@									(m_getBMFile ? m_bmURL :
//@									 m_feedListURL) );
//@						} else if (m_fileSelectorMgr.isReady()) {
							//#ifdef DMIDP20
//@							m_display.setCurrentItem(m_getBMFile ? m_bmURL :
//@									m_feedListURL);
							//#else
//@							m_display.setCurrent(m_getBMFile ?
//@									m_addNewBookmark : m_importFeedsForm) :
							//#endif
//@							m_fileSelectorMgr = null;
//@							m_getImportFile = false;
//@							m_getBMFile = false;
//@						}
//@					} catch (Throwable t) {
						//#ifdef DLOGGING
//@						logger.severe("Findig files.", t);
						//#endif
//@						System.out.println("Throwable Error finding files" + t +
//@								" " + t.getMessage());
//@						t.printStackTrace();
//@						m_fileSelectorMgr = null;
//@						m_getImportFile = false;
//@						m_getBMFile = false;
//@					}
//@				}
				//#endif

				if ( m_sortUnread ) {
					try {
						int [] indexes = new int[m_unreadItems.size()];
						long [] ldates = new long[m_unreadItems.size()];
						Vector vsorted = new Vector(m_unreadItems.size());
						Vector vunsorted = new Vector(m_unreadItems.size());
						RssItunesItem [] uitems = new RssItunesItem[m_unreadItems.size()];
						m_unreadItems.copyInto(uitems);
						int kc = 0;
						for (int ic = 0; ic < uitems.length; ic++) {
							if (uitems[ic].getDate() == null) {
								vsorted.addElement(uitems[ic]);
							} else {
								indexes[kc] = kc;
								ldates[kc++] = uitems[ic].getDate().getTime();
								vunsorted.addElement(uitems[ic]);
								//#ifdef DLOGGING
//@								if (finestLoggable) {logger.finest("kc,date=" + ic + "," + new Date(ldates[kc - 1]));}
								//#endif
							}
						}
						uitems = null;
						SortUtil.sortLongs( indexes, ldates, 0, kc - 1);
						uitems = new RssItunesItem[kc];
						vunsorted.copyInto(uitems);
						vunsorted = null;
						for (int ic = 0; ic < kc ; ic++) {
							//#ifdef DLOGGING
//@							if (finestLoggable) {logger.finest("ic,index,date=" + ic + "," + indexes[ic] + "," + new Date(ldates[indexes[ic]]) + "," + uitems[indexes[ic]].getDate());}
							//#endif
							vsorted.addElement(uitems[indexes[ic]]);
						}
						uitems = null;
						m_unreadItems.removeAllElements();
						//#ifdef DMIDP20
						m_unreadHeaderList.deleteAll();
						//#else
//@						while(m_unreadHeaderList.size()>0) {
//@
//@							m_unreadHeaderList.delete(0);
//@
//@						}
						//#endif
						for( int ic = vsorted.size() - 1; ic > 0; ic-- ){
						
							RssItunesItem r = (RssItunesItem)vsorted.elementAt(ic);
							m_unreadHeaderList.append( r.getTitle(), null );
							m_unreadItems.addElement(r);
						}
						m_sortUnread = false;
						m_display.setCurrent( m_unreadHeaderList );
					} catch(OutOfMemoryError e) {
						//#ifdef DLOGGING
//@						logger.severe("Error while initializing bookmark list: ", e);
						//#endif
						System.err.println("Error while initializing bookmark list: " + e.toString());
						Alert memoryAlert = new Alert(
								"Out of memory", 
								"Loading unread all news items.",
								null,
								AlertType.WARNING);
						memoryAlert.setTimeout(Alert.FOREVER);
						m_display.setCurrent( memoryAlert, m_unreadHeaderList );
					} catch (Throwable t) {
						//#ifdef DLOGGING
//@						logger.severe("Sort dates error.", t);
						//#endif
						System.out.println("Throwable Sort dates error." + t +
								" " + t.getMessage());
						t.printStackTrace();
						Alert memoryAlert = new Alert(
								"Internal error", 
								"Loading unread all news items.",
								null,
								AlertType.WARNING);
						memoryAlert.setTimeout(Alert.FOREVER);
						m_display.setCurrent( memoryAlert, m_unreadHeaderList );
					}
				}

				if ( m_exit || m_saveBookmarks ) {
					try {
						//#ifdef DLOGGING
//@						if (fineLoggable) {logger.fine("m_exit,m_saveBookmarks=" + m_exit + "," + m_saveBookmarks);}
						//#endif
						saveBkMrkSettings(m_exit);
						if (m_exit) {
							try {
								destroyApp(true);
							} catch (MIDletStateChangeException e) {
								//#ifdef DLOGGING
//@								if (fineLoggable) {logger.fine("MIDletStateChangeException=" + e.getMessage());}
								//#endif
							}
							super.notifyDestroyed();
							m_exit = false;
						} else {
							m_display.setCurrent( m_bookmarkList );
						}
					} finally {
						m_exit = false;
						m_saveBookmarks = false;
					}
				}
                lngStart = System.currentTimeMillis();
                lngTimeTaken = System.currentTimeMillis()-lngStart;
                if(lngTimeTaken<100)
                    m_netThread.sleep(75-lngTimeTaken);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    /** Save bookmark into record store and bookmark list */
    private void saveBookmark(){
        String name = "";
        name = m_bmName.getString();
        
        String url  = "";
        url  = m_bmURL.getString();
        
        String username = "";
        username = m_bmUsername.getString();
        
        String password = "";
        password = m_bmPassword.getString();
        
        RssItunesFeed bm = new RssItunesFeed(name, url, username, password);
        
        String key;
        if( m_curBookmark>=0 ){
            m_bookmarkList.set(m_curBookmark, bm.getName(), null);
        } else{
            m_bookmarkList.append(bm.getName(), null);
        }
        m_rssFeeds.put(bm.getName(), bm);
    }
    
    /** Fill RSS header list */
    private void fillHeadersList() {
		//#ifdef DMIDP20
        if(m_headerList.size()>0) {
            m_headerList.deleteAll();
        }
		//#else
//@        while(m_headerList.size()>0) {
//@
//@            m_headerList.delete(0);
//@
//@		}
		//#endif
        RssItunesFeed feed = m_curRssParser.getRssFeed();
        m_headerList.setTitle( feed.getName() );
		boolean markUnreadItems = m_appSettings.getMarkUnreadItems();
        final Vector vitems = feed.getItems();
        final int itemLen = vitems.size();
        for(int i=0; i < itemLen; i++){
            RssItunesItem r = (RssItunesItem)vitems.elementAt(i);
			if (markUnreadItems && r.isUnreadItem()) {
				m_headerList.append( r.getTitle(), m_unreadImage );
			} else {
				m_headerList.append( r.getTitle(), null );
			}
        }
    }
    
    /** Fill RSS unread header list */
    private void fillUnreadHdrsList( final boolean firstItem, final RssItunesFeed feed ) {
        if(firstItem && (m_unreadHeaderList.size()>0)) {
			initializeLoadingForm("Loading unread items...", m_bookmarkList);
			m_display.setCurrent( m_loadForm );
			//#ifdef DMIDP20
            m_unreadHeaderList.deleteAll();
			//#else
//@			while(m_unreadHeaderList.size()>0) {
//@
//@				m_unreadHeaderList.delete(0);
//@
//@			}
			//#endif
			m_unreadItems.removeAllElements();
        }
        Vector vitems = feed.getItems();
        final int itemLen = vitems.size();
        for(int i=0; i < itemLen; i++){
            RssItunesItem r = (RssItunesItem)vitems.elementAt(i);
			if (r.isUnreadItem()) {
				m_unreadHeaderList.append( r.getTitle(), null );
				m_unreadItems.addElement(r);
			}
        }
    }
    
    /** Initialize RSS headers list */
    private void initializeHeadersList() {
        m_headerList = new List("Headers", List.IMPLICIT);
		m_prevOpen1st = m_appSettings.getFeedListOpen();
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("initheader m_prevOpen1st=" + m_prevOpen1st );}
		//#endif
		if (m_prevOpen1st) {
			// Initialize m_backHeaderCmd in form initialization so that we can
			// change it per user request.
			m_openHeaderCmd = new Command("Open", Command.SCREEN, 1);
			m_backHeaderCmd = new Command("Back", Command.BACK, 2);
			m_headerList.addCommand(m_openHeaderCmd);
			m_headerList.addCommand(m_backHeaderCmd);
		} else {
			m_backHeaderCmd = new Command("Back", Command.BACK, 1);
			m_openHeaderCmd = new Command("Open", Command.SCREEN, 2);
			m_headerList.addCommand(m_backHeaderCmd);
			m_headerList.addCommand(m_openHeaderCmd);
		}
		//#ifdef DMIDP20
        m_headerList.setSelectCommand(m_openHeaderCmd);
		//#endif
        m_headerList.addCommand(m_updateCmd);
        m_headerList.addCommand(m_updateModCmd);
		//#ifdef DTESTUI
//@        m_headerList.addCommand(m_testRssCmd);
		//#endif
        m_headerList.setCommandListener(this);
    }
    
    /** Update RSS headers list for presence of itunes to allow display
	 	of details
	 **/
    private void updateHeadersList() {
		m_headerList.removeCommand(m_bookmarkDetailsCmd);
		RssItunesFeed feed = m_curRssParser.getRssFeed();
		if (m_itunesEnabled && feed.isItunes()) { 
			m_headerList.addCommand(m_bookmarkDetailsCmd);
		}
	}

    /** Initialize new RSS headers list */
    private void initializeUnreadHhdrsList() {
        m_unreadHeaderList = new List("Unread Headers", List.IMPLICIT);
        m_unreadHeaderList.addCommand(m_openUnreadHdrCmd);
        m_unreadHeaderList.addCommand(m_sortUnreadItemsCmd);
        m_unreadHeaderList.addCommand(m_backUnreadHdrCmd);
        m_unreadHeaderList.setCommandListener(this);
    }
    
    /** Initialize RSS item form */
    private void initializeItemForm(final RssItunesItem item) {
        System.out.println("Create new item form");
        m_itemForm = new Form( item.getTitle() );
        m_itemForm.addCommand( m_backCommand );
		String sienclosure = item.getEnclosure();
		//#ifdef DMIDP20
        m_itemForm.addCommand( m_openLinkCmd );
		if (sienclosure.length() != 0) {
			m_itemForm.addCommand( m_openEnclosureCmd );
		}
		//#endif
        m_itemForm.addCommand( m_copyLinkCmd );
		//#ifdef DMIDP20
		if (sienclosure.length() != 0) {
			m_itemForm.addCommand( m_copyEnclosureCmd );
		}
		//#endif
        m_itemForm.setCommandListener(this);
        m_itemForm.append(new StringItem(item.getTitle() + "\n",
                item.getDescription()));
		citem = item;
		if (m_itunesEnabled && item.isItunes()) {
			String author = item.getAuthor();
			if (author.length() > 0) {
				m_itemForm.append(new StringItem("Author:", author));
			}
			String subtitle = item.getSubtitle();
			if (subtitle.length() > 0) {
				m_itemForm.append(new StringItem("Subtitle:", subtitle));
			}
			String summary = item.getSummary();
			if (summary.length() > 0) {
				m_itemForm.append(new StringItem("Summary:", summary));
			}
			String duration = item.getDuration();
			if (duration.length() > 0) {
				m_itemForm.append(new StringItem("Duration:", duration));
			}
			m_itemForm.append(new StringItem("Explicit:", item.getExplicit()));
		}
        StringItem senclosure = null;
		String linkLabel = "Link:";
        String link = item.getLink();
		//#ifdef DITUNES
//@		if (link.length() == 0) {
//@			link = m_curRssParser.getRssFeed().getLink();
//@			linkLabel = "Feed link:";
//@		}
		//#endif
		//#ifdef DMIDP20
        StringItem slink = new StringItem(linkLabel, link, Item.HYPERLINK);
		if (sienclosure.length() != 0) {
			senclosure = new StringItem("Enclosure:", sienclosure,
													  Item.HYPERLINK);
		}
		//#else
//@        StringItem slink = new StringItem(linkLabel, link);
//@		if (sienclosure.length() != 0) {
//@			senclosure = new StringItem("Enclosure:", sienclosure);
//@		}
		//#endif
		if (link.length() > 0) {
			citemLnkNbr  = m_itemForm.append(slink);
		} else {
			citemLnkNbr  = -1;
		}
		// TODO get number of this or delete all and add.
		if (senclosure != null) {
			citemEnclNbr = m_itemForm.append(senclosure);
		} else {
			citemEnclNbr  = -1;
		}
        
        // Add item's date if it is available
		String dateLabel = "Date:";
        Date itemDate = item.getDate();
		//#ifdef DITUNES
//@        if(itemDate==null) {
//@			itemDate = m_curRssParser.getRssFeed().getDate();
//@			dateLabel = "Feed date:";
//@		}
		//#endif
        if(itemDate!=null) {
            m_itemForm.append(new StringItem(dateLabel, itemDate.toString()));
        }
		//#ifdef DTESTUI
//@		// After intializing the form (which was already logged by
//@		// testui classes), simulate the back command
//@		if ((m_headerIndex < m_headerList.size()) && (m_headerIndex >= 0)) {
//@			commandAction( m_backCommand, m_itemForm );
//@		}
		//#endif
    }
    
    /** Initialize RSS bookmark feed form */
    private void initializeDisplayForm( final RssItunesFeed feed ) {
        m_displayDtlForm = new Form( feed.getName() );
        m_displayDtlForm.addCommand( m_backCommand );
        m_displayDtlForm.setCommandListener(this);
		m_itemRrnForm = m_headerList;
		if (m_itunesEnabled && feed.isItunes()) {
			String language = feed.getLanguage();
			if (language.length() > 0) {
				m_displayDtlForm.append(new StringItem("Language:", language));
			}
			String author = feed.getAuthor();
			if (author.length() > 0) {
				m_displayDtlForm.append(new StringItem("Author:", author));
			}
			String subtitle = feed.getSubtitle();
			if (subtitle.length() > 0) {
				m_displayDtlForm.append(new StringItem("Subtitle:", subtitle));
			}
			String summary = feed.getSummary();
			if (summary.length() > 0) {
				m_displayDtlForm.append(new StringItem("Summary:", summary));
			}
			m_displayDtlForm.append(new StringItem("Explicit:", feed.getExplicit()));
			String title = feed.getTitle();
			if (title.length() > 0) {
				m_displayDtlForm.append(new StringItem("title:", title));
			}
			String description = feed.getDescription();
			if (description.length() > 0) {
				m_displayDtlForm.append(new StringItem("Description:", description));
			}
		}
		//#ifdef DMIDP20
        StringItem slink = new StringItem("Link:", feed.getLink(),
				                          Item.HYPERLINK);
		//#else
//@        StringItem slink = new StringItem("Link:", feed.getLink());
		//#endif
        m_displayDtlForm.append(slink);
    }
    
    /**
     * Start up the Hello MIDlet by creating the TextBox and associating
     * the exit command and listener.
     */
    public void startApp() {
    }
    
    /**
	 * Create about alert.
	 * @author  Irving Bunton
	 * @version 1.0
	 */
	private Alert getAbout() {
		Alert about = new Alert("About RssReader",
 "RssReader v" + super.getAppProperty("MIDlet-Version") +
 " Copyright (C) 2005-2006 Tommi Laukkanen " +
 "http://code.google.com/p/mobile-rss-reader/.  " +
 "This program is distributed in the hope that it will be useful, " +
 "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
 "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
 "GNU General Public License for more details at www.gnu.org. " +
 "This program is free software; you can redistribute it and/or modify " +
 "it under the terms of the GNU General Public License as published by " +
 "the Free Software Foundation; either version 2 of the License, or " +
 "(at your option) any later version.  ", null, AlertType.INFO);
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
    public void saveBookmarks(int region, boolean releaseMemory) {
		StringBuffer bookmarks = new StringBuffer("");
		m_settings.setStringProperty("bookmarks", bookmarks.toString());
		if (m_bookmarkList.size() == 0) {
			return;
		}
		//#ifdef DTEST
//@		int storeTime = 0;
		//#endif
        try {
			int bookRegion = region - 1;
			int iparts = m_settings.MAX_REGIONS - 1;
			int firstIx = bookRegion * m_bookmarkList.size() / iparts;
			int endIx = (bookRegion + 1) * m_bookmarkList.size() / iparts - 1;
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("firstIx,endIx=" + firstIx + "," + endIx);}
			//#endif
			Vector vstored = new Vector();
			try {
				/** Try to save feeds including items */
				for( int i=firstIx; i<=endIx; i++) {
					String name = m_bookmarkList.getString(i);
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("i,name=" + i + "," + name);}
					//#endif
					if (!m_rssFeeds.containsKey( name )) {
						continue;
					}
					RssItunesFeed rss = (RssItunesFeed)m_rssFeeds.get( name );
					if( name.length()>0) {
						//#ifdef DCOMPATIBILITY1
//@						CompatibilityRssFeed1 rss1 = new CompatibilityRssFeed1(rss);
						//#ifdef DTEST
//@						String prevStore = rss1.getStoreString(true);
//@						RssItunesFeed nrss = new RssItunesFeed( false, true, true, prevStore );
//@						if (!rss1.equals(nrss)) {
							//#ifdef DLOGGING
//@							logger.severe("itunes store stings not backwards compatible i=" + i);
							//#endif
//@						}
//@						long beginStore = System.currentTimeMillis();
						//#endif
//@						bookmarks.append(rss1.getStoreString(true));
						//#elifdef DCOMPATIBILITY2
//@						CompatibilityRssFeed2 rss2 = new CompatibilityRssFeed2(rss);
//@						String prevStore = rss2.getStoreString(true);
						//#ifdef DTEST
//@						RssItunesFeed nrss = new RssItunesFeed( false, false, true, prevStore );
//@						if (!rss2.equals(nrss)) {
							//#ifdef DLOGGING
//@							logger.severe("itunes store stings not backwards compatible i=" + i);
							//#endif
//@						}
//@						long beginStore = System.currentTimeMillis();
						//#endif
						//#else
						//#ifdef DTEST
//@						long beginStore = System.currentTimeMillis();
						//#endif
						rss = new RssItunesFeed(rss);
						bookmarks.append(rss.getStoreString(true, true));
						//#endif
						//#ifdef DTEST
//@						storeTime += System.currentTimeMillis() - beginStore;
						//#endif
						bookmarks.append("^");
						if (releaseMemory) {
							vstored.addElement( name );
						}
					}
				}
			} catch(OutOfMemoryError error) {
	//#ifdef DLOGGING
//@				logger.severe("saveBookmarks could not save.", error);
	//#endif
				System.out.println("Error saveBookmarks could not save.  " + error +
								   " " + error.getMessage());
				Alert memoryAlert = new Alert(
						"Out of memory", 
						"Saving bookmarks without updated news items.",
						null,
						AlertType.WARNING);
				memoryAlert.setTimeout(Alert.FOREVER);
				m_display.setCurrent( memoryAlert, m_loadForm );
				
				/** Save feeds without items */
				bookmarks.setLength(0);
				final int bsize = m_bookmarkList.size();
				for( int i=0; i<bsize; i++) {
					String name = m_bookmarkList.getString(i);
					if( name.length() == 0) {
						continue;
					}
					RssItunesFeed rss = (RssItunesFeed)m_rssFeeds.get( name );
					bookmarks.append(rss.getStoreString(false, true));
					bookmarks.append("^");
				}
			} finally {
				if (releaseMemory) {
					int vslen = vstored.size();
					for (int ic = 0; ic < vslen; ic++) {
						m_rssFeeds.remove( (String)vstored.elementAt( ic ));
					}
				}
			}
			//#ifdef DTEST
//@			System.out.println("storeTime=" + storeTime);
			//#endif
            m_settings.setStringProperty("bookmarks",bookmarks.toString());
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
        }
    }
    
    /** Update RSS feed's headers */
    private void updateHeaders(final boolean updMod, Displayable dispBack) {
		try {
			initializeLoadingForm("updating feed...", dispBack);
			m_display.setCurrent( m_loadForm );
			if(m_curRssParser.getRssFeed().getUrl().length()>0) {
				if (updMod) {
					m_getModPage = true;
				} else {
					m_getPage = true;
				}
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
    private void updateAllHeaders(final boolean updModHdr) {
        initializeLoadingForm("Updating all feeds...", m_bookmarkList);
        m_display.setCurrent( m_loadForm );
		if (updModHdr) {
			m_refreshUpdFeeds = true;
		} else {
			m_refreshAllFeeds = true;
		}
    }
    
	private synchronized void saveBkMrkSettings(final boolean releaseMemory) {
		System.gc();
		try {
			m_settings.save(0, false);
			for (int ic = 1; ic < m_settings.MAX_REGIONS; ic++) {
				saveBookmarks(ic, releaseMemory);
				m_settings.save(ic, false);
			}
			// Set internal region back to 0.
			m_settings.setStringProperty("bookmarks","");
			m_settings.save(0, false);
		} catch(Exception e) {
			//#ifdef DLOGGING
//@			logger.severe("Saving feeds.", e);
			//#endif
			/** Error while parsing RSS feed */
			System.out.println("Error saving: " + e + e.getMessage());
		} catch(Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("Saving feeds.", t);
			//#endif
			/** Error while parsing RSS feed */
			System.out.println("Error saving: " + t + t.getMessage());
		}
	}

    /** Respond to commands */
    public void commandAction(Command c, Displayable s) {
		//#ifdef DLOGGING
		//#ifdef DMIDP20
//@		if (finestLoggable) {logger.finest("command,displayable=" + c.getLabel() + "," + s.getTitle());}
		//#else
//@		if (finestLoggable) {logger.finest("command,displayable=" + c.getLabel());}
		//#endif
		//#endif
        /** Add new RSS feed bookmark */
        if( c == m_addNewBookmark ){
			updateAddBookmarkForm("New bookmark");
            m_curBookmark = -1;
            m_bmName.setString("");
            m_bmURL.setString("http://");
            m_display.setCurrent( m_addNewBMForm );
        }
        
        /** Exit from MIDlet and save bookmarks */
        if( c == m_exitCommand ){
			initializeLoadingForm("Exiting saving data...", m_bookmarkList);
			m_display.setCurrent( m_loadForm );
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
        
        /** Save bookmarks without exit (don't free up bookmarks)  */
        if( c == m_saveCommand ){
			initializeLoadingForm("Saving data...", m_bookmarkList);
			m_display.setCurrent( m_loadForm );
			m_saveBookmarks = true;
        }
        
        /** Save currently edited (or added) RSS feed's properties */
        if( c == m_addOkCmd ){
            saveBookmark();
            m_display.setCurrent( m_bookmarkList );
        }
        
        /** Cancel currently edited (or added) RSS feed's properties */
        if( c == m_addCancelCmd ){
            m_display.setCurrent( m_bookmarkList );
        }
        
        /** Edit currently selected RSS feed bookmark */
        if( c == m_editBookmark ){
			try {
				if( m_bookmarkList.size()>0 ){
					m_curBookmark = m_bookmarkList.getSelectedIndex();
					RssItunesFeed bm = (RssItunesFeed)m_rssFeeds.get(
							m_bookmarkList.getString(m_curBookmark));
					updateAddBookmarkForm("Edit bookmark");
					m_bmName.setString( bm.getName() );
					m_bmURL.setString(  bm.getUrl() );
					m_bmUsername.setString( bm.getUsername() );
					m_bmPassword.setString( bm.getPassword() );
					m_display.setCurrent( m_addNewBMForm );
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
                    /**
                     * Show currently selected RSS feed
                     * headers without updating them
                     */
                    fillHeadersList();
					updateHeadersList();
                    m_display.setCurrent( m_headerList );
                }
            }
        }
        
        /** Read unread items */
        if( c == m_readUnreadItems ) {
            final int bsize = m_bookmarkList.size();
            if( bsize > 0 ){
				boolean firstItem = true;
				for( int ic = 0; ic < bsize; ic++ ){
				
					RssItunesFeed feed = (RssItunesFeed)m_rssFeeds.get(
							m_bookmarkList.getString(ic));
					if( feed.getItems().size()>0 ) {
						/**
						 * Show currently selected RSS feed
						 * headers without updating them
						 */
						fillUnreadHdrsList( firstItem, feed );
						if ( firstItem ) {
							firstItem = false;
						}
					}
				}
				if ( !firstItem ) {
					m_unreadHeaderList.setTitle("Unread items:  " +
											 m_unreadHeaderList.size());
					m_display.setCurrent( m_unreadHeaderList );
					m_unreadItems.trimToSize();
				} else {
					m_display.setCurrent( m_bookmarkList );
				}
            }
        }
        
        if( c == m_sortUnreadItemsCmd ) {
			initializeLoadingForm("Sorting items...", m_unreadHeaderList);
			m_display.setCurrent( m_loadForm );
			m_sortUnread = true;
		}

		//#ifdef DTESTUI
//@        /** Indicate that we want to test the headers/items.  */
//@        if( c == m_testRssCmd) {
//@            if( m_headerList.size()>0 ) {
//@				m_headerIndex = 0;
//@				System.out.println("Test UI Test Rss items start m_headerIndex=" + m_headerIndex);
//@			}
//@		}
		//#endif
        /** Open RSS feed's selected topic */
        if( c == m_openHeaderCmd || (c == List.SELECT_COMMAND &&
                m_display.getCurrent()==m_headerList)) {
            if( m_headerList.size()>0 ) {
                RssItunesFeed feed = m_curRssParser.getRssFeed();
				int selIdx;
                RssItunesItem item = (RssItunesItem)feed.getItems().elementAt(
                        (selIdx = m_headerList.getSelectedIndex()) );
				m_headerList.set(selIdx, m_headerList.getString(selIdx),
						null );
                item.setUnreadItem(false);
                initializeItemForm( item );
				m_itemRrnForm = m_headerList;
                m_display.setCurrent( m_itemForm );
            }
        }
        
        /** Display Itune's feed detail */
        if( c == m_bookmarkDetailsCmd ) {
			initializeDisplayForm( m_curRssParser.getRssFeed() );
			m_display.setCurrent( m_displayDtlForm );
		}

        /** Open RSS feed's selected topic */
        if( (c == m_openUnreadHdrCmd) || (c == List.SELECT_COMMAND &&
                s.equals(m_unreadHeaderList))) {
			int selIdx = m_unreadHeaderList.getSelectedIndex();
            if( m_unreadHeaderList.size()>0){
				m_unreadHeaderList.delete(selIdx);
				RssItunesItem item = (RssItunesItem)m_unreadItems.elementAt(selIdx);
				m_unreadItems.removeElementAt(selIdx);
				m_unreadHeaderList.setTitle("New items:  " +
										 m_unreadHeaderList.size());
				/**
				 * Show currently selected RSS item
				 * without updating it
				 */
				item.setUnreadItem(false);
				initializeItemForm( item );
				m_itemRrnForm = m_unreadHeaderList;
				m_display.setCurrent( m_itemForm );
            }
        }
        
        /** Get back to RSS feed headers */
        if( c == m_backCommand ){
            m_display.setCurrent( m_itemRrnForm );
        }
        
        /** Copy link to clipboard.  */
        if( c == m_copyLinkCmd ){
			m_itemForm.set(citemLnkNbr, new TextField("Link:",
                citem.getLink(), citem.getLink().length(), TextField.URL));
			//#ifdef DMIDP10
//@			m_display.setCurrent(m_itemForm);
			//#else
			m_display.setCurrentItem(m_itemForm.get(citemLnkNbr));
			//#endif
        }
        
        /** Copy enclosure to clipboard.  */
        if( c == m_copyEnclosureCmd ){
			m_itemForm.set(citemEnclNbr, new TextField("Enclosure:",
                citem.getEnclosure(), citem.getEnclosure().length(),
				TextField.URL));
			//#ifdef DMIDP10
//@			m_display.setCurrent(m_itemForm);
			//#else
			m_display.setCurrentItem(m_itemForm.get(citemEnclNbr));
			//#endif
        }
        
		//#ifdef DMIDP20
        /** Go to link and get back to RSS feed headers */
        if( c == m_openLinkCmd ){
			String link = citem.getLink();
			try {
				//#ifdef DITUNES
//@				if (link.length() == 0) {
//@					link = m_curRssParser.getRssFeed().getLink();
//@				}
				//#endif
				if( super.platformRequest(link) ) {
					m_exit = true;
				} else {
					m_display.setCurrent( m_itemRrnForm );
				}
			} catch (ConnectionNotFoundException e) {
				//#ifdef DLOGGING
//@				logger.severe("Error opening link " + link, e);
				//#endif
				Alert badLink = new Alert("Could not connect to link",
								"Bad link:  " + link, null, AlertType.ERROR);
				badLink.setTimeout(Alert.FOREVER);
				m_display.setCurrent( badLink, m_itemRrnForm );
			}
        }

        /** Go to link and get back to RSS feed headers */
        if( c == m_openEnclosureCmd ){
			try {
				if( super.platformRequest(citem.getEnclosure()) ) {
					m_exit = true;
				} else {
					m_display.setCurrent( m_itemRrnForm );
				}
			} catch (ConnectionNotFoundException e) {
				//#ifdef DLOGGING
//@				logger.severe("Error opening link " + citem.getEnclosure(),
//@							  e);
				//#endif
				Alert badLink = new Alert("Could not connect to link",
								"Bad link:  " + citem.getEnclosure(), null,
								AlertType.ERROR);
				badLink.setTimeout(Alert.FOREVER);
				m_display.setCurrent( badLink, m_headerList );
			}
        }

		//#endif
        
        /** Get back to RSS feed bookmarks */
        if( c == m_backHeaderCmd ){
            m_display.setCurrent( m_bookmarkList );
        }
        
        /** Get back to RSS feed bookmarks */
        if( c == m_backUnreadHdrCmd ){
			m_unreadItems.removeAllElements();
            m_display.setCurrent( m_bookmarkList );
        }
        
        /** Update currently selected RSS feed's headers */
        if( (c == m_updateCmd) ||  (c == m_updateModCmd) ) {
            updateHeaders(c == m_updateModCmd, m_headerList);
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
			updateImportForm();
            m_display.setCurrent( m_importFeedsForm );
        }
        
		//#ifdef DTEST
//@		/** Show import feed list form and default file */
//@		if( c == m_importCurrFeedListCmd ) {
//@			if( m_bookmarkList.size()>0 ) {
//@                m_curBookmark = m_bookmarkList.getSelectedIndex();
//@				m_bookmarkLastIndex = m_curBookmark;
//@                RssItunesFeed bm = (RssItunesFeed)m_rssFeeds.get(
//@                        m_bookmarkList.getString(m_curBookmark));
//@				updateImportForm();
//@				m_feedListURL.setString(bm.getUrl());
//@				m_display.setCurrent( m_importFeedsForm );
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
//@        /** Import list of feeds and auto edit bookmarks/feeds */
//@        if( c == m_testImportCmd ) {
//@			m_bookmarkIndex = m_bookmarkList.size();
//@			System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" + m_bookmarkIndex);
//@			commandAction(m_importAppndCmd, m_importFeedsForm);
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

        /** Import list of feeds */
        if( (c == m_importInsCmd ) || (c == m_importAddCmd ) ||
				(c == m_importAppndCmd )) {
			m_addBkmrk = (m_curBookmark == -1) ?
				m_bookmarkList.size() : m_curBookmark;
			if( c == m_importAddCmd ){
				if (m_addBkmrk < m_bookmarkList.size()) {
					m_addBkmrk++;
				}
			}
			if (c == m_importAppndCmd ) {
				m_addBkmrk = m_bookmarkList.size();
			}
			if ((m_addBkmrk < 0) || (m_addBkmrk > m_bookmarkList.size())) {
				m_addBkmrk = m_bookmarkList.size();
			}

            try {
                // TODO: Add code for importing
				initializeLoadingForm("Loading feeds from import...",
						m_importFeedsForm);
				m_display.setCurrent( m_loadForm );
                
                // 2. Import feeds
                int selectedImportType = m_importFormatGroup.getSelectedIndex();
                RssItunesFeed[] feeds = null;
                String url = m_feedListURL.getString();
				String feedNameFilter = m_feedNameFilter.getString();
				String feedURLFilter = m_feedURLFilter.getString();
                String username = m_feedListUsername.getString();
                String password = m_feedListPassword.getString();
                m_getFeedTitleList = m_importTitleGroup.isSelected(1);
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("m_getFeedTitleList=" + m_getFeedTitleList);}
//@				if (finestLoggable) {logger.finest("selectedImportType=" + selectedImportType);}
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
//@				if (fineLoggable) {logger.fine("redirect html=" + m_listParser.isRedirectHtml());}
				//#endif
                
                // Start parsing
                m_listParser.startParsing();
                m_getFeedList = true;
                
                // 3. Show result screen
                // 4. Show list of feeds
                
            } catch(Exception ex) {
				//#ifdef DLOGGING
//@				logger.severe("Error importing feeds ", ex);
				//#endif
                // TODO: Show alarm
				Alert importAlert = new Alert(
						"Import errror", 
						"Error importing feeds",
						null,
						AlertType.ERROR);
				importAlert.setTimeout(Alert.FOREVER);
				m_display.setCurrent( importAlert, m_importFeedsForm );
            } catch(Throwable t) {
				//#ifdef DLOGGING
//@				logger.severe("Error importing feeds ", t);
				//#endif
                // TODO: Show alarm
				Alert importAlert = new Alert(
						"Import errror", 
						"Error importing feeds",
						null,
						AlertType.ERROR);
				importAlert.setTimeout(Alert.FOREVER);
				m_display.setCurrent( importAlert, m_importFeedsForm );
				t.printStackTrace();
            }
        }
        
		//#ifdef DJSR75
//@        /** Find import file in file system */
//@        if( c == m_importFileCmd ) {
//@			if (!KFileSelectorMgr.isJsr75Enabled()) {
//@				Alert invalidAlert = new Alert(
//@						"Out of memory", 
//@						"Loading bookmarks without all news items.",
//@						null,
//@						AlertType.WARNING);
//@				invalidAlert.setTimeout(Alert.FOREVER);
//@				m_display.setCurrent( invalidAlert, m_importFeedsForm );
//@				return;
//@			}
//@            try {
//@				initializeLoadingForm("Loading files to import from...",
//@						m_importFeedsForm);
//@				m_display.setCurrent( m_loadForm );
//@				m_getImportFile = true;
//@			}catch(Throwable t) {
				//#ifdef DLOGGING
//@				logger.severe("RssReaderMIDlet find files ", t);
				//#endif
//@				/** Error while executing find files */
//@				System.out.println("RssReaderMIDlet find files " + t.getMessage());
//@				t.printStackTrace();
//@			}
//@		}
		//#endif
                
		//#ifdef DJSR75
//@        /** Find bookmark file in file system */
//@        if( c == m_BMFileCmd ) {
//@			if (!KFileSelectorMgr.isJsr75Enabled()) {
//@				Alert invalidAlert = new Alert(
//@						"Out of memory", 
//@						"Loading bookmarks without all news items.",
//@						null,
//@						AlertType.WARNING);
//@				invalidAlert.setTimeout(Alert.FOREVER);
//@				m_display.setCurrent( invalidAlert, m_addNewBMForm );
//@				return;
//@			}
//@            try {
//@                // TODO: Add code for importing
//@				initializeLoadingForm("Loading files to bookmark from...",
//@						m_addNewBMForm);
//@				m_display.setCurrent( m_loadForm );
//@				m_getBMFile = true;
//@			}catch(Throwable t) {
				//#ifdef DLOGGING
//@				logger.severe("RssReaderMIDlet find files ", t);
				//#endif
//@				/** Error while executing find files */
//@				System.out.println("RssReaderMIDlet find files " + t.getMessage());
//@				t.printStackTrace();
//@			}
//@		}
		//#endif
                
        /** Cancel importing -> Show list of feeds */
        if( c == m_importCancelCmd ) {
			// Only go to header list if something is there.
			if(m_headerList.size()>0) {
				m_display.setCurrent( m_headerList );
			} else {
				m_display.setCurrent( m_bookmarkList );
			}
        }
        
        /** Paste into URL field from previous form.  */
        if( c == m_boxOkCmd ) {
			if ( m_boxRtnItem == m_bmURL ) {
				m_bmURL.setString( m_boxURL.getString() );
				//#ifdef DMIDP20
				m_display.setCurrentItem( m_bmURL );
				//#else
//@				m_display.setCurrent( m_addNewBMForm );
				//#endif
			} else if ( m_boxRtnItem == m_feedListURL ) {
				m_feedListURL.setString( m_boxURL.getString() );
				//#ifdef DMIDP20
				m_display.setCurrentItem( m_feedListURL );
				//#else
//@				m_display.setCurrent( m_importFeedsForm );
				//#endif
			}
        }
        
        /** Cancel the box go back to the return form.  */
        if( c == m_boxCancelCmd ) {
			//#ifdef DMIDP20
			m_display.setCurrentItem( m_boxRtnItem );
			//#else
//@			m_display.setCurrent( m_boxRtnForm );
			//#endif
        }
        
        /** Settings form */
        if( c == m_settingsCmd ) {
            m_display.setCurrent( m_settingsForm );
        }
        
        /** Show about */
		if( c == m_aboutCmd ) {
			Alert m_about = getAbout();
			m_display.setCurrent( m_about, m_bookmarkList );
		}

		//#ifdef DTESTUI
//@        /** Show encodings list */
//@		if( c == m_testEncCmd ) {
//@			m_display.setCurrent( m_testingForm );
//@		}
		//#endif

	//#ifdef DLOGGING
//@        /** Show about */
//@		if( c == m_debugCmd ) {
//@			m_display.setCurrent( m_debug );
//@		}
//@
//@        /** Back to bookmarks */
//@		if( c == m_backFrDebugCmd ) {
//@			m_display.setCurrent( m_bookmarkList );
//@		}
//@
	//#endif

        /** Put current import URL into URL box.  */
		if( c == m_pasteImportURLCmd ) {
			initializeURLBox(m_feedListURL.getString() );
			m_boxRtnItem = m_feedListURL;
			m_boxRtnForm = m_importFeedsForm;
			m_display.setCurrent( m_boxURL );
		}

        /** Put current import URL into URL box.  */
		if( c == m_pasteURLCmd ) {
			initializeURLBox( m_bmURL.getString() );
			m_boxRtnItem = m_bmURL;
			m_boxRtnForm = m_addNewBMForm;
			m_display.setCurrent( m_boxURL );
		}

    }
    
	//#ifdef DMIDP20
    /** Respond to commands from items */
    public void commandAction(Command c, Item i) {
        /** Put current import URL into URL box.  */
		if( c == m_pasteImportURLCmd ) {
			initializeURLBox(m_feedListURL.getString() );
			m_boxRtnItem = m_feedListURL;
			m_display.setCurrent( m_boxURL );
		}

        /** Put current import URL into URL box.  */
		if( c == m_pasteURLCmd ) {
			initializeURLBox( m_bmURL.getString() );
			m_boxRtnItem = m_bmURL;
			m_display.setCurrent( m_boxURL );
		}

	}
	//#endif

	private class LoadingForm extends Form implements CommandListener {
		private Vector m_excs = new Vector();
		private Command m_loadBackCmd;
		private Displayable m_disp;

		/* Constructor */
		LoadingForm(String title) { super(title);}

		/* Add back command and displayable to go to.  */
		public void addBackCommand( Command loadBackCmd, Displayable disp ) {
			m_loadBackCmd = loadBackCmd;
			m_disp = disp;
			super.addCommand(m_loadBackCmd);
		}

		/** Respond to commands */
		public void commandAction(Command c, Displayable s) {
			if( c == m_loadBackCmd ){
				m_display.setCurrent( m_disp );
			}

			/** Give errors for loading */
			if( c == m_loadErrCmd ) {
				showErrMsga(true);
			}

			/** Give diagnostics for loading */
			if( c == m_loadDiagCmd ) {
				showErrMsga(false);
			}

		}

		/* Show errors and diagnostics. */
		private void showErrMsga(boolean showErrsOnly) {
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
			}catch(Throwable t) {
				//#ifdef DLOGGING
//@				logger.severe("showErrMsga", t);
				//#endif
				/** Error while executing constructor */
				System.out.println("showErrMsga " + t.getMessage());
				t.printStackTrace();
			}
		}

		public void addExc(Throwable exc) {
			m_excs.addElement(exc);
		}

	}

}
