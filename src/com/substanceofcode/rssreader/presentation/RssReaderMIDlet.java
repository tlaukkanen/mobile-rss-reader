/*
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
// Expand to define MIDP define
//#define DMIDP20
// Expand to define logging define
//#define DNOLOGGING

package com.substanceofcode.rssreader.presentation;

import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssItem;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.rssreader.businesslogic.Controller;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.utils.Settings;
import com.substanceofcode.utils.EncodingUtil;
import com.substanceofcode.utils.SortUtil;
import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import javax.microedition.midlet.*;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.rms.*;
import javax.microedition.lcdui.*;

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
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_saveBookmarks;    // The save bookmarks flag
    private boolean     m_exit;             // The exit application flag
    private boolean     m_getModPage;       // The noticy flag for modified HTTP
    private boolean     m_refreshAllFeeds;  // The notify flag for all feeds
    private boolean     m_refreshUpdFeeds;  // The notify flag for updated feeds
    private boolean     m_getFeedList;      // The noticy flag for list parsing
    private boolean     m_getFeedTitleList; // The noticy flag for title/list parsing
    private FeedListParser m_listParser;    // The feed list parser
    private int         m_maxRssItemCount;  // The maximum item count in a feed
    private boolean     m_sortUnread = false;
	// Tells us if this is the first time program was used.  This is
	// done by seeing if max item count is set.  We also set it after
	// showing the about.
    private boolean     m_firstTime = false;
	// This is a mark (icon) next to unread items (except on unread items
	// screen).  Given that many screens are small, it is optional as 
	// we don't want to reduce space for text.
    private Image           m_unreadImage;
    private Vector          m_unreadItems = new Vector();
    
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
    private Form        m_itemForm;         // The item form
    private Form        m_addNewBMForm;     // The add new bookmark form
    private Form        m_loadForm;         // The "loading..." form
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
    private TextField   m_boxRtnItem;       // The list to return from for item
    private TextBox     m_boxURL;           // The feed list URL box
    private ChoiceGroup m_importFormatGroup;// The import type choice group
    private ChoiceGroup m_importTitleGroup; // The import title choice group
    private SettingsForm m_settingsForm;    // The settings form
    
    // Commands
    private Command     m_addOkCmd;         // The OK command
    private Command     m_addCancelCmd;     // The Cancel command
	//#ifdef DMIDP20
    private Command     m_pasteURLCmd;      // The Cancel command
	//#endif
    private Command     m_exitCommand;      // The exit command
    private Command     m_SaveCommand;      // The save without exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_readUnreadItems;  // The read unread items command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
    private Command     m_backCommand;      // The back to header list command
	//#ifdef DMIDP20
    private Command     m_pasteImportURLCmd;// The paste command
	//#endif
	//#ifdef DMIDP20
    private Command     m_openLinkCmd;      // The open link command
    private Command     m_openEnclosureCmd; // The open enclosure command
	//#endif
    private Command     m_copyLinkCmd;    // The copy link command
    private Command     m_copyEnclosureCmd; // The copy enclosure command
    private Command     m_openHeaderCmd;    // The open header command
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
	//#ifdef DLOGGING
//@    private Command     m_debugCmd; // The back to bookmark list command
//@	                                      // from debug form
//@    private Command     m_backFrDebugCmd; // The back to bookmark list command
//@	                                      // from debug form
	//#endif
    private Command     m_importOkCmd;      // The OK command for importing
    private Command     m_importCancelCmd;  // The Cancel command for importing
    private Command     m_boxOkCmd;         // The OK command for import box URL
    private Command     m_boxCancelCmd;     // The Cancel command for import box URL
    private Command     m_settingsCmd;      // The show settings command
    private Command     m_AboutCmd;      // The show About
    private Command     m_updateAllCmd;     // The update all command
    private Command     m_updateAllModCmd;  // The update all modified command
    
    // The controller of the application
    private Controller m_controller;
    private int citemLnkNbr = -1;
    private int citemEnclNbr = -1;
    private RssItem citem = null;
	//#ifdef DLOGGING
//@    private Form m_debug;
//@    private Logger logger;
//@    private boolean fineLoggable;
//@    private boolean finestLoggable;
	//#endif
    
    public RssReaderMIDlet() {
        m_display = Display.getDisplay(this);
        
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

			/** Initialize encodingUtil. */
			EncodingUtil.init();

			/** Initialize controller */
			m_controller = new Controller( this );
			
			/** Initialize commands */
			m_addOkCmd          = new Command("OK", Command.OK, 1);
			m_addCancelCmd      = new Command("Cancel", Command.CANCEL, 2);
			//#ifdef DMIDP20
			m_pasteURLCmd       = new Command("Allow paste", Command.CANCEL, 2);
			//#endif
			m_backCommand       = new Command("Back", Command.SCREEN, 1);
			m_exitCommand       = new Command("Exit", Command.SCREEN, 5);
			m_SaveCommand       = new Command("Save without exit", Command.SCREEN, 4);
			m_addNewBookmark    = new Command("Add new feed", Command.SCREEN, 2);
			m_openBookmark      = new Command("Open feed", Command.SCREEN, 1);
			m_readUnreadItems      = new Command("Read unread items", Command.SCREEN, 1);
			m_editBookmark      = new Command("Edit feed", Command.SCREEN, 2);
			m_delBookmark       = new Command("Delete feed", Command.SCREEN, 3);
			m_openHeaderCmd     = new Command("Open item", Command.SCREEN, 1);
			m_openUnreadHdrCmd  = new Command("Open item", Command.SCREEN, 1);
			m_sortUnreadItemsCmd = new Command("Date sort items",
											   Command.SCREEN, 1);
			//#ifdef DMIDP20
			m_openLinkCmd       = new Command("Open link", Command.SCREEN, 1);
			m_openEnclosureCmd  = new Command("Open enclosure", Command.SCREEN, 1);
			//#endif
			m_copyLinkCmd       = new Command("Copy link", Command.SCREEN, 1);
			m_copyEnclosureCmd  = new Command("Copy enclosure", Command.SCREEN, 1);
			m_backHeaderCmd     = new Command("Back", Command.SCREEN, 2);
			m_backUnreadHdrCmd     = new Command("Back", Command.SCREEN, 2);
			m_updateCmd         = new Command("Update feed", Command.SCREEN, 2);
			m_updateModCmd      = new Command("Update modified feed",
											  Command.SCREEN, 2);
			m_importFeedListCmd = new Command("Import feeds", Command.SCREEN, 3);
			//#ifdef DTEST
//@			m_importCurrFeedListCmd = new Command("Import current feeds", Command.SCREEN, 3);
			//#endif
			m_importOkCmd       = new Command("OK", Command.OK, 1);
			m_importCancelCmd   = new Command("Cancel", Command.CANCEL, 2);
			m_boxOkCmd          = new Command("OK", Command.OK, 1);
			m_boxCancelCmd      = new Command("Cancel", Command.CANCEL, 2);
			//#ifdef DMIDP20
			m_pasteImportURLCmd = new Command("Allow paste", Command.SCREEN, 3);
			//#endif
			m_settingsCmd       = new Command("Settings", Command.SCREEN, 4);
			m_AboutCmd          = new Command("About", Command.SCREEN, 4);
			m_updateAllCmd      = new Command("Update all", Command.SCREEN, 2);
			m_updateAllModCmd   = new Command("Update modified all",
											  Command.SCREEN, 2);
		//#ifdef DLOGGING
//@			m_debugCmd          = new Command("Debug Log", Command.SCREEN, 4);
//@			m_backFrDebugCmd    = new Command("Back", Command.SCREEN, 2);
		//#endif
			
			m_getPage = false;
			m_getModPage = false;
			m_refreshAllFeeds = false;
			m_refreshUpdFeeds = false;
			m_getFeedList = false;
			m_getFeedTitleList = false;
			m_curBookmark = -1;
			m_maxRssItemCount = 10;
			
			// To get proper initialization, need to 
			try {
				m_settings = Settings.getInstance(this);
				m_firstTime = !m_settings.isInitialized();
			} catch(Exception e) {
				System.err.println("Error while getting settings: " + e.toString());
			}

			m_appSettings = RssReaderSettings.getInstance(this);
			//#ifdef DLOGGING
//@			if (m_appSettings.getLogLevel().equals("")) {
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
			
			initializeLoadingForm("Loading items...");
			m_display.setCurrent( m_loadForm );

			/** Initialize thread for http connection operations */
			m_netThread = new Thread(this);
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
		//#ifdef DLOGGING
//@			if (m_debug != null) {
//@				initializeDebugForm();
//@			}
		//#endif
			m_settingsForm = new SettingsForm(this);
			
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
        m_display.setCurrent( m_bookmarkList );
    }
    
    /** Load bookmarks from record store */
    private void initializeBookmarkList() {
        try {
            m_bookmarkList = new PromptList(this, "Bookmarks", List.IMPLICIT);
            m_bookmarkList.addCommand( m_exitCommand );
            m_bookmarkList.addCommand( m_SaveCommand );
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
	//#ifdef DLOGGING
//@			if (m_debug != null) {
//@				m_bookmarkList.addCommand( m_debugCmd );
//@			}
	//#endif
            m_bookmarkList.addCommand( m_AboutCmd );
            m_bookmarkList.setCommandListener( this );
            
            int i = 1;
            
            m_rssFeeds = new Hashtable();
			for (int ic = 1; ic < m_settings.MAX_REGIONS; ic++) {
				boolean stop = false;
				String bms = m_settings.getStringProperty(ic, "bookmarks", "");
				// Save memory by setting bookmarks to "" now that
				// we will convert them to objects.
				m_settings.setStringProperty("bookmarks", "");
				
				if(bms.length()>0) {
					do{
						
						String part = "";
						if(bms.indexOf("^")>0) {
							part = bms.substring(0, bms.indexOf("^"));
						}
						bms = bms.substring(bms.indexOf("^")+1);
						if(part.length()>0) {
							RssFeed bm = new RssFeed( part );
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
    private void initializeLoadingForm(String desc) {
        m_loadForm = new Form("Loading");
        m_loadForm.append( desc + "\n" );
        m_loadForm.addCommand( m_backHeaderCmd );
        m_loadForm.setCommandListener( this );
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
        m_addNewBMForm.setCommandListener(this);
    }
    
    /** Update bookmark adding/editing form */
    private void updateAddBookmarkForm(String title) {
        m_addNewBMForm.setTitle(title);
		//#ifdef DMIDP20
        RssReaderSettings settings = RssReaderSettings.getInstance(this);
        boolean useTextBox = settings.getUseTextBox();
		if (useTextBox) {
			m_bmURL.setItemCommandListener(this);
			m_bmURL.addCommand(m_pasteURLCmd);
		} else {
			m_bmURL.setItemCommandListener(null);
			m_bmURL.removeCommand(m_pasteURLCmd);
		}
		//#endif
	}

    /** Initialize import form */
    private void initializeImportForm() {
        m_importFeedsForm = new Form("Import feeds");
        RssReaderSettings settings = RssReaderSettings.getInstance(this);
        String url = settings.getImportUrl();
        if(url.length()==0) {
            url = "http://";
        }
		m_feedListURL = new TextField("URL", url, 256, TextField.URL);
		m_importFeedsForm.append(m_feedListURL);
        
        String[] formats = {"OPML", "line by line", "HTML Links"};
        m_importFormatGroup = new ChoiceGroup("Format", ChoiceGroup.EXCLUSIVE, formats, null);
        m_importFeedsForm.append(m_importFormatGroup);
        
        m_feedNameFilter = new TextField("Name filter string (optional)", "", 256, TextField.ANY);
        m_importFeedsForm.append(m_feedNameFilter);
        m_feedURLFilter = new TextField("URL filter string (optional)", "", 256, TextField.ANY);
        m_importFeedsForm.append(m_feedURLFilter);
        
        String username = settings.getImportUrlUsername();
        m_feedListUsername  = new TextField("Username (optional)", username, 64, TextField.ANY);
        m_importFeedsForm.append(m_feedListUsername);
        
        String password = settings.getImportUrlPassword();
        m_feedListPassword  = new TextField("Password (optional)", password, 64, TextField.PASSWORD);
        m_importFeedsForm.append(m_feedListPassword);
        String[] titleInfo =
				{"Skip feed with missing title",
			     "Get missing titles from feed"};
        m_importTitleGroup  = new ChoiceGroup("Missing title (optionl)",
				ChoiceGroup.EXCLUSIVE, titleInfo, null);
        m_importFeedsForm.append(m_importTitleGroup);
        
        m_importFeedsForm.addCommand( m_importOkCmd );
        m_importFeedsForm.addCommand( m_importCancelCmd );
		//#ifdef DMIDP20
        m_importFeedsForm.addCommand( m_pasteImportURLCmd );
		//#endif
        m_importFeedsForm.addCommand( m_importCancelCmd );
        m_importFeedsForm.setCommandListener(this);
    }
    
    /** Update import form */
    private void updateImportForm() {
		//#ifdef DMIDP20
        RssReaderSettings settings = RssReaderSettings.getInstance(this);
        boolean useTextBox = settings.getUseTextBox();
		if (useTextBox) {
			m_feedListURL.setItemCommandListener(this);
			m_feedListURL.addCommand(m_pasteImportURLCmd);
		} else {
			m_feedListURL.setItemCommandListener(null);
			m_feedListURL.removeCommand(m_pasteImportURLCmd);
		}
		//#endif
	}

    /** Initialize URL text Box */
    private void initializeURLBox(String url) {
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
        while(true) {
            try {
				// Initialize bookmarks here since it does some work.
				if (m_bookmarkList == null) {
					synchronized (this) {
						if (m_bookmarkList == null) {
							initForms();
						}
					}
				}
                if( m_getPage || m_getModPage ) {
                    try {
                        /** Get RSS feed */
                        int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
						//TODO
                        m_curRssParser.parseRssFeed( m_getModPage, maxItemCount );
                        fillHeadersList();
                        m_display.setCurrent( m_headerList );
                    }catch(Exception e) {
						//#ifdef DLOGGING
//@						logger.severe("Parsing feeds.", e);
						//#endif
                        /** Error while parsing RSS feed */
                        System.out.println("Error: " + e.getMessage());
                        m_loadForm.append("\nError parsing feed on:\n" +
                                m_curRssParser.getRssFeed().getUrl());
                        m_display.setCurrent( m_loadForm );
                    }catch(Throwable t) {
						//#ifdef DLOGGING
//@						logger.severe("Parsing feeds on " +
//@                                m_curRssParser.getRssFeed().getUrl(), t);
						//#endif
                        /** Error while parsing RSS feed */
                        System.out.println("Error: " + t.getMessage());
                        m_loadForm.append("\nError parsing feed on:\n" +
                                m_curRssParser.getRssFeed().getUrl());
                        m_display.setCurrent( m_loadForm );
                    }
                    m_getPage = false;
                    m_getModPage = false;
                }
                if( m_refreshAllFeeds || m_refreshUpdFeeds ) {
                    try{
                        int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
                        Enumeration feedEnum = m_rssFeeds.elements();
                        while(feedEnum.hasMoreElements()) {
                            RssFeed feed = (RssFeed)feedEnum.nextElement();
                            try{
                                m_loadForm.append(feed.getName() + "...");
                                RssFeedParser parser = new RssFeedParser( feed );
                                parser.parseRssFeed( m_refreshUpdFeeds,
										maxItemCount);
                                m_loadForm.append("ok\n");
                            } catch(Exception ex) {
								//#ifdef DLOGGING
//@								logger.severe("Error parsing feed " +
//@										      feed.getName(), ex);
								//#endif
                                m_loadForm.append("Error\n");
								System.out.println("Error feed " +
										           feed.getName());
                            }
                        }
                        m_display.setCurrent( m_bookmarkList );
                    } catch(Exception ex) {
						//#ifdef DLOGGING
//@						logger.severe("Error parsing feeds from:\n" +
//@                                m_curRssParser.getRssFeed().getUrl(), ex);
						//#endif
                        m_loadForm.append("\nError parsing feed from:\n" +
                                m_curRssParser.getRssFeed().getUrl());
                        m_display.setCurrent( m_loadForm );
                    } catch(Throwable t) {
						//#ifdef DLOGGING
//@						logger.severe("Error parsing feeds from:\n" +
//@                                m_curRssParser.getRssFeed().getUrl(), t);
						//#endif
                        m_loadForm.append("\nError parsing feed from:\n" +
                                m_curRssParser.getRssFeed().getUrl());
                        m_display.setCurrent( m_loadForm );
                    }
                    m_refreshAllFeeds = false;
                    m_refreshUpdFeeds = false;
                }
                if( m_getFeedList || m_getFeedTitleList ) {
                    try {
                        if(m_listParser.isReady()) {
                            // Feed list parsing is ready
                            System.out.println("Feed list parsing is ready");
							if(!m_listParser.isSuccessfull()) {
								throw new Exception("Feed parsing error.");
							}
                            RssFeed[] feeds = m_listParser.getFeeds();
                            for(int feedIndex=0; feedIndex<feeds.length; feedIndex++) {
                                String name = feeds[feedIndex].getName();
                                System.out.println("Adding: " + name);
                                if ((name == null) && m_getFeedTitleList) {
									m_loadForm.append("Loading title for " +
											"feed " +
											feeds[feedIndex].getUrl());
									//#ifdef DLOGGING
//@									if (finestLoggable) {logger.finest("Getting title for url=" + feeds[feedIndex].getUrl());}
									//#endif
									RssFeedParser fparser =
											new RssFeedParser(
											feeds[feedIndex] );
									fparser.setGetTitleOnly(true);
									/** Get RSS feed */
									int maxItemCount =
											m_appSettings.getMaximumItemCountInFeed();
									try {
										fparser.parseRssFeed( false, maxItemCount );
										name = feeds[feedIndex].getName();
										m_loadForm.append("ok\n");
									} catch(Exception ex) {
										//#ifdef DLOGGING
//@										logger.severe("Error parsing feed",
//@													  ex);
										//#endif
										m_loadForm.append("Error\n");
									}
								}
                                if((name != null) && (name.length()>0)) {
                                    if(!m_rssFeeds.containsKey( name )) {
                                        m_rssFeeds.put( name, feeds[feedIndex] );
                                        if( m_curBookmark>=0 ){
                                            m_bookmarkList.set(m_curBookmark, name, null);
                                        } else{
                                            m_bookmarkList.insert(m_bookmarkList.size(), name, null);
                                        }
                                    }
                                }
                            }
                            m_display.setCurrent( m_bookmarkList );
							m_getFeedList      = false;
							m_getFeedTitleList = false;
                        } else {
                            System.out.println("Feed list parsing isn't ready");
                        }
                    } catch(Exception ex) {
                        // TODO: Add exception handling
						//#ifdef DLOGGING
//@						logger.severe("Error while parsing feed list: ", ex);
						//#endif
                        System.err.println("Error while parsing feed list: " + ex.toString());
						ex.printStackTrace();
						Alert importAlert = new Alert(
								"Import errror", 
								"Error importing feeds:  " + ex.getMessage(),
								null,
								AlertType.ERROR);
						importAlert.setTimeout(Alert.FOREVER);
						m_display.setCurrent( importAlert, m_importFeedsForm );
						m_getFeedList      = false;
						m_getFeedTitleList = false;
                    } catch(Throwable t) {
                        // TODO: Add exception handling
						//#ifdef DLOGGING
//@						logger.severe("Error while parsing feed list: ", t);
						//#endif
                        System.err.println("Error while parsing feed list: " + t.toString());
						t.printStackTrace();
						Alert importAlert = new Alert(
								"Import errror", 
								"Error importing feeds:  " + t.getMessage(),
								null,
								AlertType.ERROR);
						importAlert.setTimeout(Alert.FOREVER);
						m_display.setCurrent( importAlert, m_importFeedsForm );
						m_getFeedList      = false;
						m_getFeedTitleList = false;
                    }
                }

				if ( m_sortUnread ) {
					try {
						int [] indexes = new int[m_unreadItems.size()];
						long [] ldates = new long[m_unreadItems.size()];
						Vector vsorted = new Vector(m_unreadItems.size());
						RssItem [] uitems = new RssItem[m_unreadItems.size()];
						m_unreadItems.copyInto(uitems);
						int kc = 0;
						for (int ic = 0; ic < uitems.length; ic++) {
							indexes[kc] = ic;
							if (uitems[ic].getDate() == null) {
								vsorted.addElement(uitems[ic]);
							} else {
								ldates[kc++] = uitems[ic].getDate().getTime();
								//#ifdef DLOGGING
//@								if (finestLoggable) {logger.finest("kc,date=" + ic + "," + new Date(ldates[kc - 1]));}
								//#endif
							}
						}
						uitems = null;
						SortUtil.sortLongs( indexes, ldates, 0, kc - 1);
						uitems = new RssItem[indexes.length];
						m_unreadItems.copyInto(uitems);
						for (int ic = 0; ic < kc ; ic++) {
							//#ifdef DLOGGING
//@							if (finestLoggable) {logger.finest("ic,index,date=" + ic + "," + indexes[ic] + "," + new Date(ldates[indexes[ic]]));}
							//#endif
							vsorted.addElement(uitems[indexes[ic]]);
						}
						boolean firstItem = true;
						for( int ic = vsorted.size(); ic > 0; ic-- ){
						
							RssFeed feed = (RssFeed)vsorted.elementAt(ic);
							/**
							 * Show currently selected RSS feed
							 * headers without updating them
							 */
							fillUnreadHdrsList( firstItem, feed );
							if ( firstItem ) {
								firstItem = false;
							}
						}
					} catch (Throwable t) {
						//#ifdef DLOGGING
//@						logger.severe("Sort dates error.", t);
						//#endif
						System.out.println("Sort dates error." + t + " " +
										   t.getMessage());
						t.printStackTrace();
					} finally {
						m_sortUnread = false;
						m_display.setCurrent( m_unreadHeaderList );
					}
				}

				if ( m_exit || m_saveBookmarks ) {
					synchronized (this) {
						if ( m_exit || m_saveBookmarks ) {
							saveBkMrkSettings(m_exit);
							if (m_exit) {
								destroyApp(false);
								super.notifyDestroyed();
								m_exit = false;
							} else {
								m_display.setCurrent( m_bookmarkList );
								m_saveBookmarks = false;
							}
						}
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
    private void SaveBookmark(){
        String name = "";
        name = m_bmName.getString();
        
        String url  = "";
        url  = m_bmURL.getString();
        
        String username = "";
        username = m_bmUsername.getString();
        
        String password = "";
        password = m_bmPassword.getString();
        
        RssFeed bm = new RssFeed(name, url, username, password);
        
        String key;
        if( m_curBookmark>=0 ){
            m_bookmarkList.set(m_curBookmark, bm.getName(), null);
        } else{
            m_bookmarkList.insert(m_bookmarkList.size(), bm.getName(), null);
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
        RssFeed feed = m_curRssParser.getRssFeed();
        m_headerList.setTitle( feed.getName() );
		boolean markUnreadItems = m_appSettings.getMarkUnreadItems();
        for(int i=0; i<feed.getItems().size(); i++){
            RssItem r = (RssItem)feed.getItems().elementAt(i);
			if (markUnreadItems && r.isUnreadItem()) {
				m_headerList.append( r.getTitle(), m_unreadImage );
			} else {
				m_headerList.append( r.getTitle(), null );
			}
        }
    }
    
    /** Fill RSS unread header list */
    private void fillUnreadHdrsList( boolean firstItem, RssFeed feed ) {
        if(firstItem && (m_unreadHeaderList.size()>0)) {
			initializeLoadingForm("Loading unread items...");
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
        for(int i=0; i<feed.getItems().size(); i++){
            RssItem r = (RssItem)feed.getItems().elementAt(i);
			if (r.isUnreadItem()) {
				m_unreadHeaderList.append( r.getTitle(), null );
				m_unreadItems.addElement(r);
			}
        }
    }
    
    /** Initialize RSS headers list */
    private void initializeHeadersList() {
        m_headerList = new List("Headers", List.IMPLICIT);
        m_headerList.addCommand(m_openHeaderCmd);
        m_headerList.addCommand(m_backHeaderCmd);
        m_headerList.addCommand(m_updateCmd);
        m_headerList.addCommand(m_updateModCmd);
        m_headerList.setCommandListener(this);
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
    private void initializeItemForm(RssItem item) {
        System.out.println("Create new item form");
        m_itemForm = new Form( item.getTitle() );
		//#ifdef DMIDP20
        m_itemForm.addCommand( m_openLinkCmd );
		if (!item.getEnclosure().equals("")) {
			m_itemForm.addCommand( m_openEnclosureCmd );
		}
		//#endif
        m_itemForm.addCommand( m_copyLinkCmd );
		//#ifdef DMIDP20
		if (!item.getEnclosure().equals("")) {
			m_itemForm.addCommand( m_copyEnclosureCmd );
		}
		//#endif
        m_itemForm.addCommand( m_backCommand );
        m_itemForm.setCommandListener(this);
        m_itemForm.setTitle(item.getTitle());
        m_itemForm.append(new StringItem(item.getTitle() + "\n",
                item.getDescription()));
		citem = item;
        StringItem senclosure = null;
		//#ifdef DMIDP20
        StringItem slink = new StringItem("Link:", item.getLink(),
				                          Item.HYPERLINK);
		if (!item.getEnclosure().equals("")) {
			senclosure = new StringItem("Enclosure:", item.getEnclosure(),
													  Item.HYPERLINK);
		}
		//#else
//@        StringItem slink = new StringItem("Link:", item.getLink());
//@		if (!item.getEnclosure().equals("")) {
//@			senclosure = new StringItem("Enclosure:", item.getEnclosure());
//@		}
		//#endif
        citemLnkNbr  = m_itemForm.append(slink);
		// TODO get number of this or delete all and add.
		if (senclosure != null) {
			citemEnclNbr = m_itemForm.append(senclosure);
		}
        
        // Add item's date if it is available
        Date itemDate = item.getDate();
        if(itemDate!=null) {
            m_itemForm.append(new StringItem("Date:", itemDate.toString()));
        }
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
 "GNU General Public License for more details. " +
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
     * In this case there is nothing to cleanup.
     */
    public void destroyApp(boolean unconditional) {
    }
    
    /** Save bookmarks to record store
        releaseMemory use true if exiting as we do not need
		the rss feeds anymore, so we can save memory and avoid
		having extra memory around.  */
    public void saveBookmarks(int region, boolean releaseMemory) {
        String bookmarks = "";
		m_settings.setStringProperty("bookmarks",bookmarks);
		if (m_bookmarkList.size() == 0) {
			return;
		}
        try {
			int bookRegion = region - 1;
			int iparts = m_settings.MAX_REGIONS - 1;
			int firstIx = bookRegion * m_bookmarkList.size() / iparts;
			int endIx = (bookRegion + 1) * m_bookmarkList.size() / iparts - 1;
            /** Try to save feeds including items */
            for( int i=firstIx; i<=endIx; i++) {
                String name = m_bookmarkList.getString(i);
				if (!m_rssFeeds.containsKey( name )) {
					continue;
				}
                RssFeed rss = (RssFeed)m_rssFeeds.get( name );
                if( name.length()>0) {
                    bookmarks += rss.getStoreString(true) + "^";
					if (releaseMemory) {
						m_rssFeeds.remove( name );
					}
				}
            }
            m_settings.setStringProperty("bookmarks",bookmarks);
        } catch(OutOfMemoryError error) {
//#ifdef DLOGGING
//@			logger.severe("saveBookmarks could not save.", error);
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
            bookmarks = "";
            for( int i=0; i<m_bookmarkList.size(); i++) {
                String name = m_bookmarkList.getString(i);
                RssFeed rss = (RssFeed)m_rssFeeds.get( name );
                if( name.length()>0)
                    bookmarks += rss.getStoreString(false) + "^";
            }
            m_settings.setStringProperty("bookmarks",bookmarks);
		} catch (Throwable t) {
//#ifdef DLOGGING
//@			logger.severe("saveBookmarks could not save.", t);
//#endif
			System.out.println("saveBookmarks could not save." + t + " " +
					           t.getMessage());
        }
    }
    
    /** Update RSS feed's headers */
    private void updateHeaders(boolean updMod) {
		try {
			initializeLoadingForm("updating feed...");
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
    private void updateAllHeaders(boolean updModHdr) {
        initializeLoadingForm("Updating all feeds...");
        m_display.setCurrent( m_loadForm );
		if (updModHdr) {
			m_refreshUpdFeeds = true;
		} else {
			m_refreshAllFeeds = true;
		}
    }
    
	private synchronized void saveBkMrkSettings(boolean releaseMemory) {
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
			initializeLoadingForm("Exiting saving data...");
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
        if( c == m_SaveCommand ){
			initializeLoadingForm("Saving data...");
			m_display.setCurrent( m_loadForm );
			m_saveBookmarks = true;
        }
        
        /** Save currently edited (or added) RSS feed's properties */
        if( c == m_addOkCmd ){
            SaveBookmark();
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
					RssFeed bm = (RssFeed)m_rssFeeds.get(
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
                
                RssFeed feed = (RssFeed)m_rssFeeds.get(
                        m_bookmarkList.getString(m_curBookmark));
                m_curRssParser = new RssFeedParser( feed );
                if( m_curRssParser.getRssFeed().getItems().size()==0 ) {
                    /** Update RSS feed headers only if this is a first time */
                    updateHeaders(false);
                } else {
                    /**
                     * Show currently selected RSS feed
                     * headers without updating them
                     */
                    fillHeadersList();
                    m_display.setCurrent( m_headerList );
                }
            }
        }
        
        /** Read unread items */
        if( c == m_readUnreadItems ) {
            if( m_bookmarkList.size()>0 ){
				boolean firstItem = true;
				for( int ic = 0; ic < m_bookmarkList.size(); ic++ ){
                
					RssFeed feed = (RssFeed)m_rssFeeds.get(
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
			initializeLoadingForm("Sorting items...");
			m_display.setCurrent( m_loadForm );
			m_sortUnread = true;
		}

        /** Open RSS feed's selected topic */
        if( c == m_openHeaderCmd || (c == List.SELECT_COMMAND &&
                m_display.getCurrent()==m_headerList)) {
            if( m_headerList.size()>0 ) {
                RssFeed feed = m_curRssParser.getRssFeed();
				int selIdx;
                RssItem item = (RssItem) feed.getItems().elementAt(
                        (selIdx = m_headerList.getSelectedIndex()) );
				m_headerList.set(selIdx, m_headerList.getString(selIdx),
						null );
                item.setUnreadItem(false);
                initializeItemForm( item );
				m_itemRrnForm = m_headerList;
                m_display.setCurrent( m_itemForm );
            }
        }
        
        /** Open RSS feed's selected topic */
        if( (c == m_openUnreadHdrCmd) || (c == List.SELECT_COMMAND &&
                s.equals(m_unreadHeaderList))) {
			int selIdx = m_unreadHeaderList.getSelectedIndex();
            if( m_unreadHeaderList.size()>0){
				m_unreadHeaderList.delete(selIdx);
				RssItem item = (RssItem)m_unreadItems.elementAt(selIdx);
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
			try {
				if( super.platformRequest(citem.getLink()) ) {
					saveBkMrkSettings(true);
					destroyApp(false);
					super.notifyDestroyed();
				}
				m_display.setCurrent( m_itemRrnForm );
			} catch (ConnectionNotFoundException e) {
				//#ifdef DLOGGING
//@				logger.severe("Error opening link " + citem.getLink(),
//@							  e);
				//#endif
				Alert badLink = new Alert("Could not connect to link",
								"Bad link:  " + citem.getLink(), null,
								AlertType.ERROR);
				badLink.setTimeout(Alert.FOREVER);
				m_display.setCurrent( badLink, m_itemRrnForm );
			}
        }

        /** Go to link and get back to RSS feed headers */
        if( c == m_openEnclosureCmd ){
			try {
				if( super.platformRequest(citem.getEnclosure()) ) {
					destroyApp(false);
					super.notifyDestroyed();
				}
				m_display.setCurrent( m_itemRrnForm );
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
            updateHeaders(c == m_updateModCmd);
        }
        
        /** Update all RSS feeds */
        if( (c == m_updateAllCmd) || (c == m_updateAllModCmd) ) {
            updateAllHeaders(c == m_updateAllModCmd);
        }
        
        /** Show import feed list form */
        if( c == m_importFeedListCmd ) {
			// Reset current bookmark so that the added feeds do not
			// get put into the same bookmark and overrite each other.
			m_curBookmark = -1;
			updateImportForm();
            m_display.setCurrent( m_importFeedsForm );
        }
        
		//#ifdef DTEST
//@		/** Show import feed list form and default file */
//@		if( c == m_importCurrFeedListCmd ) {
//@			if( m_bookmarkList.size()>0 ) {
//@                m_curBookmark = m_bookmarkList.getSelectedIndex();
//@                RssFeed bm = (RssFeed)m_rssFeeds.get(
//@                        m_bookmarkList.getString(m_curBookmark));
//@				updateImportForm();
//@				m_feedListURL.setString(bm.getUrl());
//@				m_curBookmark = -1;
//@				m_display.setCurrent( m_importFeedsForm );
//@			}
//@        }
		//#endif

        /** Import list of feeds */
        if( c == m_importOkCmd ) {
            try {
                // TODO: Add code for importing
				initializeLoadingForm("Loading feeds from import...");
				m_display.setCurrent( m_loadForm );
                
                // 2. Import feeds
                int selectedImportType = m_importFormatGroup.getSelectedIndex();
                RssFeed[] feeds = null;
                String url = m_feedListURL.getString();
				String feedNameFilter = m_feedNameFilter.getString();
				String feedURLFilter = m_feedURLFilter.getString();
                String username = m_feedListUsername.getString();
                String password = m_feedListPassword.getString();
				//#ifdef DLOGGING
//@				if (finestLoggable) {logger.finest("m_getFeedTitleList=" + m_getFeedTitleList);}
				//#endif
                
                // Save settings
                RssReaderSettings settings = RssReaderSettings.getInstance(this);
                settings.setImportUrl(url);
                settings.setImportUrlUsername(username);
                settings.setImportUrlPassword(password);
                if(selectedImportType==2) {
                    // Use line by line parser
                    m_listParser = new HTMLLinkParser(url, username, password);
                }
                if(selectedImportType==1) {
                    // Use line by line parser
                    m_listParser = new LineByLineParser(url, username, password);
                }
                if(selectedImportType==0) {
                    // Use OPML parser
                    m_listParser = new OpmlParser(url, username, password);
                }
				m_listParser.setFeedNameFilter(feedNameFilter);
				m_listParser.setFeedURLFilter(feedURLFilter);
                
                // Start parsing
                m_listParser.startParsing();
                m_getFeedList = !m_getFeedTitleList;
                m_getFeedTitleList = m_importTitleGroup.isSelected(1);
                
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
        
        /** Cancel importing -> Show list of feeds */
        if( c == m_importCancelCmd ) {
			// Only go to header list if something is there.
			if(m_headerList.size()>0) {
				m_display.setCurrent( m_headerList );
			} else {
				m_display.setCurrent( m_bookmarkList );
			}
        }
        
		//#ifdef DMIDP20

        /** Paste into URL field from previous form.  */
        if( c == m_boxOkCmd ) {
			if ( m_boxRtnItem == m_bmURL ) {
				m_bmURL.setString( m_boxURL.getString() );
				m_display.setCurrentItem( m_bmURL );
			} else if ( m_boxRtnItem == m_feedListURL ) {
				m_feedListURL.setString( m_boxURL.getString() );
				m_display.setCurrentItem( m_feedListURL );
			}
        }
        
        /** Cancel the box go back to the return form.  */
        if( c == m_boxCancelCmd ) {
			m_display.setCurrentItem( m_boxRtnItem );
        }
		//#endif
        
        /** Settings form */
        if( c == m_settingsCmd ) {
            m_display.setCurrent( m_settingsForm );
        }
        
        /** Show about */
		if( c == m_AboutCmd ) {
			Alert m_about = getAbout();
			m_display.setCurrent( m_about, m_bookmarkList );
		}

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

}
