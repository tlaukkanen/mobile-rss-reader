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

//#define MIDP20

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
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.*;

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
        implements CommandListener, Runnable {
    
    // Attributes
    private Display     m_display;          // The display for this MIDlet
    private Settings    m_settings;         // The settings
    private RssReaderSettings m_appSettings;// The application settings
    private Hashtable   m_rssFeeds;         // The bookmark URLs
    private Thread      m_netThread;        // The thread for networking
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_refreshAllFeeds;  // The notify flag for all feeds
    private boolean     m_getFeedList;      // The noticy flag for list parsing
    private FeedListParser m_listParser;    // The feed list parser
    private int         m_maxRssItemCount;  // The maximum item count in a feed
	// Tells us if this is the first time program was used.  This is
	// done by seeing if max item count is set.  We also set it after
	// showing the about.
    private boolean firstTime = false;
    
    // Currently selected bookmark
    private int             m_curBookmark;  // The currently selected item
    private RssFeedParser   m_curRssParser; // The currently selected RSS
    
    // GUI items
    private List        m_bookmarkList;     // The bookmark list
    private List        m_headerList;       // The header list
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
    private ChoiceGroup m_importFormatGroup;// The import type choice group
    private SettingsForm m_settingsForm;    // The settings form
    
    // Commands
    private Command     m_addOkCmd;         // The OK command
    private Command     m_addCancelCmd;     // The Cancel command
    private Command     m_exitCommand;      // The exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
    private Command     m_backCommand;      // The back to header list command
	//#ifdef MIDP20
    private Command     m_openLinkCmd;    // The open link command
	//#endif
    private Command     m_copyLinkCmd;    // The copy link command
    private Command     m_openHeaderCmd;    // The open header command
    private Command     m_backHeaderCmd;    // The back to bookmark list command
    private Command     m_updateCmd;        // The update headers command
    private Command     m_importFeedListCmd;// The import feed list command
    private Command     m_importOkCmd;      // The OK command for importing
    private Command     m_importCancelCmd;  // The Cancel command for importing
    private Command     m_settingsCmd;      // The show settings command
    private Command     m_AboutCmd;      // The show About
    private Command     m_updateAllCmd;     // The update all command
    
    // The controller of the application
    private Controller m_controller;
    private int citemNbr = -1;
    private RssItem citem = null;
    
    public RssReaderMIDlet() {
        m_display = Display.getDisplay(this);
        
        /** Initialize controller */
        m_controller = new Controller( this );
        
        /** Initialize commands */
        m_addOkCmd          = new Command("OK", Command.OK, 1);
        m_addCancelCmd      = new Command("Cancel", Command.CANCEL, 2);
        m_backCommand       = new Command("Back", Command.SCREEN, 1);
        m_exitCommand       = new Command("Exit", Command.SCREEN, 5);
        m_addNewBookmark    = new Command("Add new feed", Command.SCREEN, 2);
        m_openBookmark      = new Command("Open feed", Command.SCREEN, 1);
        m_editBookmark      = new Command("Edit feed", Command.SCREEN, 2);
        m_delBookmark       = new Command("Delete feed", Command.SCREEN, 3);
        m_openHeaderCmd     = new Command("Open item", Command.SCREEN, 1);
		//#ifdef MIDP20
        m_openLinkCmd       = new Command("Open link", Command.SCREEN, 1);
		//#endif
        m_copyLinkCmd       = new Command("Copy link", Command.SCREEN, 1);
        m_backHeaderCmd     = new Command("Back", Command.SCREEN, 2);
        m_updateCmd         = new Command("Update feed", Command.SCREEN, 2);
        m_importFeedListCmd = new Command("Import feeds", Command.SCREEN, 3);
        m_importOkCmd       = new Command("OK", Command.OK, 1);
        m_importCancelCmd   = new Command("Cancel", Command.CANCEL, 2);
        m_settingsCmd       = new Command("Settings", Command.SCREEN, 4);
        m_AboutCmd          = new Command("About", Command.SCREEN, 4);
        m_updateAllCmd      = new Command("Update all", Command.SCREEN, 2);
        
        m_getPage = false;
        m_refreshAllFeeds = false;
        m_getFeedList = false;
        m_curBookmark = -1;
        m_maxRssItemCount = 10;
        
        m_appSettings = RssReaderSettings.getInstance(this);
		try {
			m_settings = Settings.getInstance(this);
			firstTime = !m_settings.isInitialized();
        } catch(Exception e) {
            System.err.println("Error while getting settings: " + e.toString());
        }
        
        /** Initialize GUI items */
        initializeBookmarkList();
        initializeAddBookmarkForm();
        initializeHeadersList();
        //initializeLoadingForm();
        initializeImportForm();
        m_settingsForm = new SettingsForm(this);
        
        m_display.setCurrent(m_bookmarkList);
        
        /** Initialize thread for http connection operations */
        m_netThread = new Thread(this);
        m_netThread.start();
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
            m_bookmarkList = new List("Bookmarks", List.IMPLICIT);
            m_bookmarkList.addCommand( m_exitCommand );
            m_bookmarkList.addCommand( m_addNewBookmark );
            m_bookmarkList.addCommand( m_openBookmark );
            m_bookmarkList.addCommand( m_editBookmark );
            m_bookmarkList.addCommand( m_delBookmark );
            m_bookmarkList.addCommand( m_importFeedListCmd );
            m_bookmarkList.addCommand( m_settingsCmd );
            m_bookmarkList.addCommand( m_updateAllCmd );
            m_bookmarkList.addCommand( m_AboutCmd );
            m_bookmarkList.setCommandListener( this );
            
            boolean stop = false;
            int i = 1;
            
            m_rssFeeds = new Hashtable();
            m_settings = Settings.getInstance(this);
            String bms = m_settings.getStringProperty("bookmarks", "");
            
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
        } catch(Exception e) {
            System.err.println("Error while initializing bookmark list: " + e.toString());
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
        m_bmName = new TextField("Name", "", 35, TextField.ANY);
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
        
        m_importFeedsForm.addCommand( m_importOkCmd );
        m_importFeedsForm.addCommand( m_importCancelCmd );
        m_importFeedsForm.setCommandListener(this);
    }
    
    /** Run method is used to get RSS feed with HttpConnection */
    public void run(){
        /* Use networking if necessary */
        long lngStart;
        long lngTimeTaken;
        while(true) {
            try {
                if( m_getPage ) {
                    try {
                        /** Get RSS feed */
                        int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
                        m_curRssParser.parseRssFeed( maxItemCount );
                        fillHeadersList();
                        m_display.setCurrent( m_headerList );
                    }catch(Exception e) {
                        /** Error while parsing RSS feed */
                        System.out.println("Error: " + e.getMessage());
                        m_loadForm.append("\nError parsing feed on:\n" +
                                m_curRssParser.getRssFeed().getUrl());
                        m_display.setCurrent( m_loadForm );
                    }
                    m_getPage = false;
                }
                if( m_refreshAllFeeds ) {
                    try{
                        int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
                        Enumeration feedEnum = m_rssFeeds.elements();
                        while(feedEnum.hasMoreElements()) {
                            RssFeed feed = (RssFeed)feedEnum.nextElement();
                            try{
                                m_loadForm.append(feed.getName() + "...");
                                RssFeedParser parser = new RssFeedParser( feed );
                                parser.parseRssFeed(maxItemCount);
                                m_loadForm.append("ok\n");
                            } catch(Exception ex) {
                                m_loadForm.append("Error\n");
                            }
                        }
                        m_display.setCurrent( m_bookmarkList );
                    } catch(Exception ex) {
                        m_loadForm.append("\nError parsing feed from:\n" +
                                m_curRssParser.getRssFeed().getUrl());
                        m_display.setCurrent( m_loadForm );
                    }
                    m_refreshAllFeeds = false;
                }
                if( m_getFeedList ) {
                    try {
                        if(m_listParser.isReady()==true) {
                            // Feed list parsing is ready
                            System.out.println("Feed list parsing is ready");
                            RssFeed[] feeds = m_listParser.getFeeds();
                            for(int feedIndex=0; feedIndex<feeds.length; feedIndex++) {
                                String name = feeds[feedIndex].getName();
                                System.out.println("Adding: " + name);
                                if(name.length()>0) {
                                    if(m_rssFeeds.containsKey( name )==false) {
                                        m_rssFeeds.put( name, feeds[feedIndex] );
                                        if( m_curBookmark>=0 ){
                                            m_bookmarkList.set(m_curBookmark, name, null);
                                        } else{
                                            m_bookmarkList.insert(m_bookmarkList.size(), name, null);
                                        }
                                    }
                                }
                            }
                            m_getFeedList = false;
                            m_display.setCurrent( m_bookmarkList );
                        } else {
                            System.out.println("Feed list parsing isn't ready");
                        }
                    } catch(Exception ex) {
                        // TODO: Add exception handling
                        System.err.println("Error while parsing feed list: " + ex.toString());
                        m_getFeedList = false;
                        m_display.setCurrent( m_bookmarkList );
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
        while(m_headerList.size()>0) {
            m_headerList.delete(0);
        }
        RssFeed feed = m_curRssParser.getRssFeed();
        m_headerList.setTitle( feed.getName() );
        for(int i=0; i<m_curRssParser.getRssFeed().getItems().size(); i++){
            RssItem r = (RssItem)feed.getItems().elementAt(i);
            m_headerList.append( r.getTitle(), null );
        }
    }
    
    /** Initialize RSS headers list */
    private void initializeHeadersList() {
        m_headerList = new List("Headers", List.IMPLICIT);
        m_headerList.addCommand(m_openHeaderCmd);
        m_headerList.addCommand(m_backHeaderCmd);
        m_headerList.addCommand(m_updateCmd);
        m_headerList.setCommandListener(this);
    }
    
    /** Initialize RSS item form */
    private void initializeItemForm(RssItem item) {
        System.out.println("Create new item form");
        m_itemForm = new Form( item.getTitle() );
		//#ifdef MIDP20
        m_itemForm.addCommand( m_openLinkCmd );
		//#endif
        m_itemForm.addCommand( m_copyLinkCmd );
        m_itemForm.addCommand( m_backCommand );
        m_itemForm.setCommandListener(this);
        m_itemForm.setTitle(item.getTitle());
        m_itemForm.append(new StringItem(item.getTitle() + "\n",
                item.getDescription()));
		citem = item;
        citemNbr  = m_itemForm.append(new StringItem("Link:",
                item.getLink()));
        
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
		if( firstTime ) {
			try {
				firstTime = false;
				m_settings = Settings.getInstance(this);
				// Set Max item count to default so that it is initialized.
				m_appSettings.setMaximumItemCountInFeed(
						m_appSettings.getMaximumItemCountInFeed());
				m_settings.save(false);
				Alert m_about = getAbout();
				m_display.setCurrent( m_about, m_bookmarkList );
			} catch(Exception e) {
				System.err.println("Error while getting/updating settings: " + e.toString());
				m_display.setCurrent( m_bookmarkList );
			}
		} else {
            m_display.setCurrent( m_bookmarkList );
		}
    }
    
    /**
	 * Create about alert.
	 * @author  Irving Bunton
	 * @version 1.0
	 */
	private Alert getAbout() {
		Alert about = new Alert("About RssReader",
"RssReader v1.7 " +
 "Copyright (C) 2005-2006 Tommi Laukkanen " +
 "http://www.substanceofcode.com.  " +
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
    
    /** Save bookmarks to record store */
    public void saveBookmarks() {
        String bookmarks = "";
        try {
            /** Try to save feeds including items */
            for( int i=0; i<m_bookmarkList.size(); i++) {
                String name = m_bookmarkList.getString(i);
                RssFeed rss = (RssFeed)m_rssFeeds.get( name );
                if( name.length()>0)
                    bookmarks += rss.getStoreString(true) + "^";
            }
            m_settings.setStringProperty("bookmarks",bookmarks);
        } catch(OutOfMemoryError error) {
            Alert memoryAlert = new Alert(
                    "Out of memory", 
                    "Saving bookmarks without updated news items.",
                    null,
                    AlertType.WARNING);
            m_display.setCurrent( memoryAlert );
            
            /** Save feeds without items */
            bookmarks = "";
            for( int i=0; i<m_bookmarkList.size(); i++) {
                String name = m_bookmarkList.getString(i);
                RssFeed rss = (RssFeed)m_rssFeeds.get( name );
                if( name.length()>0)
                    bookmarks += rss.getStoreString(false) + "^";
            }
            m_settings.setStringProperty("bookmarks",bookmarks);
        }
    }
    
    /** Update RSS feed's headers */
    private void updateHeaders() {
        initializeLoadingForm("updating feed...");
        m_display.setCurrent( m_loadForm );
        if(m_curRssParser.getRssFeed().getUrl().length()>0) {
            m_getPage = true;
        }
    }
    
    /** Update all RSS feeds */
    private void updateAllHeaders() {
        initializeLoadingForm("Updating all feeds...");
        m_display.setCurrent( m_loadForm );
        m_refreshAllFeeds = true;
    }
    
    /** Respond to commands */
    public void commandAction(Command c, Displayable s) {
        /** Add new RSS feed bookmark */
        if( c == m_addNewBookmark ){
            m_curBookmark = -1;
            m_bmName.setString("");
            m_bmURL.setString("http://");
            m_display.setCurrent( m_addNewBMForm );
        }
        
        /** Exit from MIDlet and save bookmarks */
        if( c == m_exitCommand ){
            try {
                saveBookmarks();
                m_settings.save(false);
            } catch(Exception e) {}
            destroyApp(false);
            notifyDestroyed();
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
            if( m_bookmarkList.size()>0 ){
                m_curBookmark = m_bookmarkList.getSelectedIndex();
                RssFeed bm = (RssFeed)m_rssFeeds.get(
                        m_bookmarkList.getString(m_curBookmark));
                m_bmName.setString( bm.getName() );
                m_bmURL.setString(  bm.getUrl() );
                m_bmUsername.setString( bm.getUsername() );
                m_bmPassword.setString( bm.getPassword() );
                m_display.setCurrent( m_addNewBMForm );
            }
        }
        
        /** Delete currently selected RSS feed bookmark */
        if( c == m_delBookmark ){
            if( m_bookmarkList.size()>0 ){
                m_curBookmark = m_bookmarkList.getSelectedIndex();
                m_bookmarkList.delete( m_curBookmark );
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
                    updateHeaders();
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
        
        /** Open RSS feed's selected topic */
        if( c == m_openHeaderCmd || (c == List.SELECT_COMMAND &&
                m_display.getCurrent()==m_headerList)) {
            if( m_headerList.size()>0 ) {
                RssFeed feed = m_curRssParser.getRssFeed();
                RssItem item = (RssItem) feed.getItems().elementAt(
                        m_headerList.getSelectedIndex() );
                initializeItemForm( item );
                m_display.setCurrent( m_itemForm );
            }
        }
        
        /** Get back to RSS feed headers */
        if( c == m_backCommand ){
            m_display.setCurrent( m_headerList );
        }
        
        /** Copy link to clipboard.  */
        if( c == m_copyLinkCmd ){
			m_itemForm.set(citemNbr, new TextField("Link:",
                citem.getLink(), citem.getLink().length(), TextField.URL));
        }
        
		//#ifdef MIDP20
        /** Go to link and get back to RSS feed headers */
        if( c == m_openLinkCmd ){
			try {
				if( super.platformRequest(citem.getLink()) ) {
					destroyApp(false);
					super.notifyDestroyed();
				}
				m_display.setCurrent( m_headerList );
			} catch (ConnectionNotFoundException e) {
				Alert badLink = new Alert("Could not connect to link",
								"Bad link:  " + citem.getLink(), null,
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
        
        /** Update currently selected RSS feed's headers */
        if( c == m_updateCmd ) {
            updateHeaders();
        }
        
        /** Update all RSS feeds */
        if( c == m_updateAllCmd ) {
            updateAllHeaders();
        }
        
        /** Show import feed list form */
        if( c == m_importFeedListCmd ) {
            m_display.setCurrent( m_importFeedsForm );
        }
        
        /** Import list of feeds */
        if( c == m_importOkCmd ) {
            try {
                // TODO: Add code for importing
                // 1. Show wait screen
                
                // 2. Import feeds
                int selectedImportType = m_importFormatGroup.getSelectedIndex();
                RssFeed[] feeds = null;
                String url = m_feedListURL.getString();
				String feedNameFilter = m_feedNameFilter.getString();
				String feedURLFilter = m_feedURLFilter.getString();
                String username = m_feedListUsername.getString();
                String password = m_feedListPassword.getString();
                
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
                m_getFeedList = true;
                
                // 3. Show result screen
                // 4. Show list of feeds
                
            } catch(Exception ex) {
                // TODO: Show alarm
            }
        }
        
        /** Cancel importing -> Show list of feeds */
        if( c == m_importCancelCmd ) {
            m_display.setCurrent( m_headerList );
        }
        
        /** Settings form */
        if( c == m_settingsCmd ) {
            m_display.setCurrent( m_settingsForm );
        }
        
        /** Show about */
		if( c == m_AboutCmd ) {
			Alert m_about = getAbout();
			m_display.setCurrent( m_about, m_bookmarkList );
		}
    }
    
}
