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

package com.substanceofcode.rssreader;

import java.util.*;
import javax.microedition.midlet.*;
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
    private Hashtable   m_rssFeeds;         // The bookmark URLs
    private Thread      m_netThread;        // The thread for networking
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_getFeedList;      // The noticy flag for list parsing
    private FeedListParser m_listParser;    // The feed list parser
    
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
    private ChoiceGroup m_importFormatGroup;// The import type choice group
    
    // Commands
    private Command     m_addOkCmd;         // The OK command
    private Command     m_addCancelCmd;     // The Cancel command
    private Command     m_exitCommand;      // The exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
    private Command     m_backCommand;      // The back to header list command
    private Command     m_openHeaderCmd;    // The open header command
    private Command     m_backHeaderCmd;    // The back to bookmark list command
    private Command     m_updateCmd;        // The update headers command
    private Command     m_importFeedListCmd;// The import feed list command
    private Command     m_importOkCmd;      // The OK command for importing
    private Command     m_importCancelCmd;  // The Cancel command for importing
    
    
    public RssReaderMIDlet() {
        m_display = Display.getDisplay(this);
        
        /** Initialize commands */
        m_addOkCmd          = new Command("OK", Command.OK, 1);
        m_addCancelCmd      = new Command("Cancel", Command.CANCEL, 2);
        m_backCommand       = new Command("Back", Command.SCREEN, 1);
        m_exitCommand       = new Command("Exit", Command.SCREEN, 4);
        m_addNewBookmark    = new Command("Add new", Command.SCREEN, 2);
        m_openBookmark      = new Command("Open", Command.SCREEN, 1);
        m_editBookmark      = new Command("Edit", Command.SCREEN, 2);
        m_delBookmark       = new Command("Delete", Command.SCREEN, 3);
        m_openHeaderCmd     = new Command("Open", Command.SCREEN, 1);
        m_backHeaderCmd     = new Command("Back", Command.SCREEN, 2);
        m_updateCmd         = new Command("Update", Command.SCREEN, 2);
        m_importFeedListCmd = new Command("Import feeds", Command.SCREEN, 3);
        m_importOkCmd       = new Command("OK", Command.OK, 1);
        m_importCancelCmd   = new Command("Cancel", Command.CANCEL, 2);
        
        m_getPage = false;
        m_getFeedList = false;
        m_curBookmark = -1;
        
        /** Initialize GUI items */
        initializeBookmarkList();
        initializeAddBookmarkForm();
        initializeHeadersList();
        initializeLoadingForm();
        initializeImportForm();
        
        m_display.setCurrent(m_bookmarkList);
        
        /** Initialize thread for http connection operations */
        m_netThread = new Thread(this);
        m_netThread.start();
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
            m_bookmarkList.setCommandListener( this );
            
            boolean stop = false;
            int i = 1;
            
            m_rssFeeds = new Hashtable();
            m_settings = Settings.getInstance(this);
            String bms = m_settings.getStringProperty("bookmarks", "");
            
            if(bms.length()>0) {
                do{
                    System.out.println("bms: " + bms);
                    String part = "";
                    if(bms.indexOf("^")>0)
                        part = bms.substring(0, bms.indexOf("^"));
                    bms = bms.substring(bms.indexOf("^")+1);
                    RssFeed bm = new RssFeed( part );
                    if(bm.getName().length()>0){
                        m_bookmarkList.append(bm.getName(),null);
                        m_rssFeeds.put(bm.getName(), bm);
                    }
                    if( part.length()==0)
                        stop = true;
                }while(!stop);
            }
        } catch(Exception e) {
            
        }
    }
    
    /** Initialize loading form */
    private void initializeLoadingForm() {
        m_loadForm = new Form("Loading");
        m_loadForm.append("Loading RSS feed...");
        m_loadForm.addCommand( m_backHeaderCmd );
        m_loadForm.setCommandListener( this );
    }
    
    /** Initialize bookmark adding form */
    private void initializeAddBookmarkForm() {
        m_addNewBMForm = new Form("New bookmark");
        m_bmName = new TextField("Name", "", 35, TextField.ANY);
        m_bmURL  = new TextField("URL", "http://", 64, TextField.URL);
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
        m_feedListURL = new TextField("URL", "http://", 64, TextField.URL);
        m_importFeedsForm.append(m_feedListURL);
        
        String[] formats = {"line by line", "OPML"};
        m_importFormatGroup = new ChoiceGroup("Format", ChoiceGroup.EXCLUSIVE, formats, null);
        m_importFeedsForm.append(m_importFormatGroup);
        
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
                        m_curRssParser.parseRssFeed();
                        fillHeadersList();
                        m_display.setCurrent( m_headerList );
                    }catch(Exception e) {
                        /** Error while parsing RSS feed */
                        System.out.println("Error: " + e.getMessage());
                        m_loadForm.append("\nError parsing RSS feed on:\n" + 
                                m_curRssParser.getRssFeed().getUrl());
                        m_display.setCurrent( m_loadForm );
                    }
                    m_getPage = false;
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
                                    m_rssFeeds.put( name, feeds[feedIndex] );
                                    if( m_curBookmark>=0 ){
                                        m_bookmarkList.set(m_curBookmark, name, null);
                                    } else{
                                        m_bookmarkList.insert(m_bookmarkList.size(), name, null);
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
        System.out.println("bm: " + bm.getStoreString());
    }
    
    /** Fill RSS header list */
    private void fillHeadersList() {
        while(m_headerList.size()>0)
            m_headerList.delete(0);
        for(int i=0; i<m_curRssParser.getRssFeed().getItems().size(); i++){
            RssFeed feed = m_curRssParser.getRssFeed();
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
        if( m_itemForm == null){
            System.out.println("Create new item form");
            m_itemForm = new Form( item.getTitle() );
            m_itemForm.addCommand( m_backCommand );
            m_itemForm.setCommandListener(this);
        }
        while(m_itemForm.size()>0) {
            System.out.println("Delete item");
            m_itemForm.delete(0);
        }
        m_itemForm.setTitle(item.getTitle());
        m_itemForm.append(new StringItem(item.getTitle() + "\n", 
                item.getDescription()));
        m_itemForm.append(new StringItem("Link:", 
                item.getLink()));
    }
    
    /**
     * Start up the Hello MIDlet by creating the TextBox and associating
     * the exit command and listener.
     */
    public void startApp() {
        m_display.setCurrent( m_bookmarkList );
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
        for( int i=0; i<m_bookmarkList.size(); i++) {
            String name = m_bookmarkList.getString(i);
            RssFeed rss = (RssFeed)m_rssFeeds.get( name );
            if( name.length()>0)
                bookmarks += rss.getStoreString() + "^";
        }
        m_settings.setStringProperty("bookmarks",bookmarks);
    }
    
    /** Update RSS feed's headers */
    private void updateHeaders() {
        initializeLoadingForm();
        m_display.setCurrent( m_loadForm );
        if(m_curRssParser.getRssFeed().getUrl().length()>0) {
            m_getPage = true;
        }
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
        
        /** Get back to RSS feed bookmarks */
        if( c == m_backHeaderCmd ){
            m_display.setCurrent( m_bookmarkList );
        }
        
        /** Update currently selected RSS feed's headers */
        if( c == m_updateCmd ) {
            updateHeaders();
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
                if(selectedImportType==0) {
                    // Use line by line parser
                    m_listParser = new LineByLineParser(url);
                }
                if(selectedImportType==1) {
                    // Use OPML parser
                    m_listParser = new OpmlParser(url);
                }
                
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
    }
}
