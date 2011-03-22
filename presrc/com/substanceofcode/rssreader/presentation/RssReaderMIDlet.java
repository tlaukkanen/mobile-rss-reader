//--Need to modify--#preprocess
/*
 * RssReaderMIDlet.java
 *
 * Copyright (C) 2005-2007 Tommi Laukkanen
 * Copyright (C) 2007-2011 Irving Bunton, Jr
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
 * IB 2010-03-07 1.11.4RC1 Use observer pattern for feed parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-03-14 1.11.5RC2 Fix logging.
 * IB 2010-03-14 1.11.5RC2 Synchronize use of opening or updating the feed.
 * IB 2010-03-14 1.11.5RC2 Fix previous page for loading screen in procPage.
 * IB 2010-03-14 1.11.5RC2 Combine if statements.
 * IB 2010-03-14 1.11.5RC2 Fix showing diagnostics.
 * IB 2010-05-24 1.11.5RC2 Code cleanup.
 * IB 2010-05-24 1.11.5RC2 Combine classes to save space.
 * IB 2010-05-24 1.11.5RC2 Log thread info for diagnostics.
 * IB 2010-05-24 1.11.5RC2 Use one thread for novice import.
 * IB 2010-05-24 1.11.5RC2 Fix mispelling of bookmarks.
 * IB 2010-05-24 1.11.5RC2 Only do export if signed.
 * IB 2010-05-28 1.11.5RC2 Use threads and CmdReceiver for MIDP 2.0 only.
 * IB 2010-05-29 1.11.5RC2 Fix opening of feed that causes parsing error with MIDP 1.0.
 * IB 2010-05-29 1.11.5RC2 Don't use HTML in small memory MIDP 1.0 to save space.
 * IB 2010-05-30 1.11.5RC2 Do export only for signed, Itunes and JSR-75.
 * IB 2010-05-31 1.11.5RC2 Move display of loading form before loading of settings to allow reporting of settings errors.
 * IB 2010-05-31 1.11.5RC2 Keep better track of loading finished.
 * IB 2010-05-31 1.11.5RC2 Change setCurrentNotes so that it will set loading finished if need be and use the current displayable if it's a LoadingForm or use a LoadingForm paramater.
 * IB 2010-06-01 1.11.5RC2 If we are finished loading or exiting, but there is no back screen, add quit.  Also allow quit if exiting with error.
 * IB 2010-06-02 1.11.5RC2 Use settings instance from RssReaderSettings to make sure that we share the same one.
 * IB 2010-06-27 1.11.5Dev2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-06-27 1.11.5Dev2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-06-27 1.11.5Dev2 Use volatile for m_firstTime in RssReaderMIDlet.
 * IB 2010-06-27 1.11.5Dev2 Have test reload db.
 * IB 2010-06-27 1.11.5Dev2 Make sure m_loadForm is not null when using RssReaderMIDlet.
 * IB 2010-06-27 1.11.5Dev2 Have static methods to load/save settings and bookmarks for better testing.
 * IB 2010-06-27 1.11.5Dev2 Have set current allow displayable as alert to handle alert bugs in the future.
 * IB 2010-06-27 1.11.5Dev2 Have static initSettingsEnabled to load app and general settings to help with testing.
 * IB 2010-06-27 1.11.5Dev2 Have static loadBookmarkList load bookmarks from settings DB to help with testing.
 * IB 2010-06-27 1.11.5Dev2 Make sure m_appSettings is not null when using RssReaderMIDlet.
 * IB 2010-06-27 1.11.5Dev2 Change command priorities to be in the right order and have update mod ahead of udpate all.
 * IB 2010-06-27 1.11.5Dev2 Set gauge based on max value to be more flexible.
 * IB 2010-06-27 1.11.5Dev2 Have procBookmarkExc to handle exceptions for init/load of bookmarks.
 * IB 2010-07-05 1.11.5Dev6 Use null pattern using nullPtr.
 * IB 2010-07-28 1.11.5Dev8 Use static open link command to share with Item form and detail form.
 * IB 2010-07-28 1.11.5Dev8 Allow open of feed link on details screen for smartphone version if not empty.
 * IB 2010-07-28 1.11.5Dev8 More logging.
 * IB 2010-08-15 1.11.5Dev8 Don't use midlet directly for FileSelectorMgr.
 * IB 2010-08-15 1.11.5Dev8 Have setCurrent done in getLoadingForm.
 * IB 2010-09-26 1.11.5Dev8 Allow export of OPML/line by line if not the smartphone version.
 * IB 2010-09-26 1.11.5Dev8 Don't access m_display directly since it's in FeatureMgr.
 * IB 2010-09-26 1.11.5Dev8 Have setCurrentItem done in FeatureMgr.
 * IB 2010-09-26 1.11.5Dev8 Have getCurrent in FeatureMgr.
 * IB 2010-09-26 1.11.5Dev8 Have callSerially in FeatureMgr.
 * IB 2010-09-26 1.11.5Dev8 Have setCurrent directly using display in FeatureMgr.
 * IB 2010-09-26 1.11.5Dev8 Have loadForm parm for procBackPage to use the current loadForm.
 * IB 2010-09-26 1.11.5Dev8 Have main observer obsmain for procBackPage to use the main observer.
 * IB 2010-09-26 1.11.5Dev8 Have initLoad to allow initialization of loading form.
 * IB 2010-09-26 1.11.5Dev8 Don't use midlet for makeObserable.
 * IB 2010-09-26 1.11.5Dev8 Have m_fileRtnForm be a feature form.
 * IB 2010-09-26 1.11.5Dev8 Have m_openLinkCmd be static to use with detail form  and item form.
 * IB 2010-09-26 1.11.5Dev8 Have m_backCommand be static to be used for several forms.
 * IB 2010-09-26 1.11.5Dev8 Don't need midlet for BMForm because it's used indirectly.
 * IB 2010-09-26 1.11.5Dev8 Don't need midlet for ImportFeedsForm because it's used indirectly.
 * IB 2010-09-26 1.11.5Dev8 Don't need midlet for AllNewsList because it's used indirectly.
 * IB 2010-09-26 1.11.5Dev8 Don't need midlet for HeaderList because it's used indirectly.
 * IB 2010-09-26 1.11.5Dev8 Don't need midlet for ItemForm because it's used indirectly.
 * IB 2010-09-26 1.11.5Dev8 Checks to make sure that the commandAction is for the item form for back.
 * IB 2010-09-26 1.11.5Dev8 Use procPlatform in DetailForm and ItemForm.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for initSettingsEnabled.
 * IB 2010-09-27 1.11.5Dev8 Have convenience method setCurrentAlt.
 * IB 2010-09-27 1.11.5Dev8 Use setCurrentFeature to go back to list before item form.
 * IB 2010-09-27 1.11.5Dev8 Have loadForm for procUpdHeader.
 * IB 2010-09-27 1.11.5Dev8 Have loadForm for procBackHeader.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-10-30 1.11.5Dev12 Use getSysProperty to get system property and return error message.  This gets an error in microemulator if it causes a class to be loaded.
 * IB 2010-11-15 1.11.5Dev14 Use getImage from FeatureMgr to get the images.
 * IB 2010-11-15 1.11.5Dev14 If logging error, exit the constructor.
 * IB 2010-11-15 1.11.5Dev14 If no m_bookmarkList, don't add/remove commands from it.
 * IB 2010-11-15 1.11.5Dev14 If create m_bookmarkList is successful, add start command in case something goes wrong after creating the booklist.
 * IB 2010-11-15 1.11.5Dev14 Cosmetic changes.
 * IB 2010-11-15 1.11.5Dev14 Create initApp to initialize the app.
 * IB 2010-11-15 1.11.5Dev14 Create getAboutInfo to return info for about to be used as alert from getAbout or form.
 * IB 2010-11-15 1.11.5Dev14 Combine nested if statements.
 * IB 2010-11-16 1.11.5Dev14 Add default value of null for getSysProperty.
 * IB 2010-11-16 1.11.5Dev14 Add default value for getSysProperty, getSysPermission, and getSysPropStarts.
 * IB 2010-11-17 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
 * IB 2010-11-17 1.11.5Dev14 More logging.
 * IB 2010-11-19 1.11.5Dev14 Move find files call functionality to FeatureMgr.
 * IB 2010-11-19 1.11.5Dev14 Move static vars CFEED_SEPARATOR and OLD_FEED_SEPARATOR out of midlet class to Settings.
 * IB 2010-11-19 1.11.5Dev14 Move static var m_backCommand out of midlet class to FeatureMgr.
 * IB 2010-11-19 1.11.5Dev14 Make m_openLinkCmd not static to remove all static vars from midlet class.
 * IB 2010-11-19 1.11.5Dev14 Use getSetLicensePrompt for getAbout.
 * IB 2010-11-19 1.11.5Dev14 Fix open link and other static menu.
 * IB 2010-11-22 1.11.5Dev14 Replace Alert with loading form exception.
 * IB 2010-11-22 1.11.5Dev14 Fix exitApp to handle exit from open link correctly.
 * IB 2010-11-22 1.11.5Dev14 Fix exitApp to not use synchronized for the processing of saving.
 * IB 2010-11-22 1.11.5Dev14 Don't call exitApp while synchronized.
 * IB 2010-11-22 1.11.5Dev14 More logging.
 * IB 2010-11-22 1.11.5Dev14 Make exit/saving vars volatile so that they should get set correctly.
 * IB 2010-11-22 1.11.5Dev14 Use showMeNotes after saving data.
 * IB 2010-11-22 1.11.5Dev14 Have getPromptDisp pass in "Quit Program" instead of cancel to promt for About license.
 * IB 2010-11-22 1.11.5Dev14 Move setting of m_openLinkCmd to be initialized if no logging.
 * IB 2011-01-14 1.11.5Alpha15 Only compile some portions if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Set instance vars in constructor.
 * IB 2011-01-14 1.11.5Alpha15 Create forms in constructor, but add items in the start of run method.
 * IB 2011-01-14 1.11.5Alpha15 Have bookmarks list for the full version and SettingsForm for the internet link version.
 * IB 2011-01-12 1.11.5Alpha15 If logging level is changed, use initLogVars to reset the logging vars in RssReaderMIDlet.
 * IB 2011-01-12 1.11.5Alpha15 Have exit code allow either full or internet link version.
 * IB 2011-01-14 1.11.5Dev15 Change static fields to instance vars for the Settings singleton to reduce static memory used.
 * IB 2011-01-12 1.11.5Alpha15 Have getCmdAdd to both create a command and add it to the displayable.  Return the command pointer.
 * IB 2011-01-12 1.11.5Alpha15 Have getCmdAddPrompt to both create a prompt command and add it to the displayable.  Return the command pointer.
 * IB 2011-01-22 1.11.5Alpha15 Use main display from FeatureMgr.
 * IB 2011-01-22 1.11.5Alpha15 Have modUpdAllFeeds to allow future regression testing of background update/modify.
 * IB 2011-01-14 1.11.5Dev15 Have optional backlight after update/refresh all.
 * IB 2011-01-14 1.11.5Dev15 Have optional vibrate after update/refresh all.
 * IB 2011-01-14 1.11.5Dev15 Have checkActive be checkRssActive so for future use of background processing.
 * IB 2011-01-18 1.11.5Dev16 Use getCmdAdd to create and add a command.
 * IB 2011-01-24 1.11.5Dev16 More only compile some portions if it is the full version.
 * IB 2011-01-24 1.11.5Dev16 Have m_aboutCmd in FeatureMgr since it should be common for all apps.
 * IB 2011-01-24 1.11.5Dev16 Keep showing license form until the user accepts it.  Otherwise, kick them out of the program.
 * IB 2011-01-18 1.11.5Dev16 Use jsr75avail from FeatureMgr to determine availabilit of JSR-75.
 * IB 2011-01-18 1.11.5Dev16 Use initForm (singular) to initialize the settings form's vars/form.  
 * IB 2011-01-18 1.11.5Dev16 Use initForms (plural in RssReaderMIDlet) to initialize the settings form.  This will handle showing of about/license.
 * IB 2011-01-24 1.11.5Dev16 Use setSelectedIndex after initializing bookmarks to make sure selection is at the top of the list.
 * IB 2011-01-24 1.11.5Dev16 If unconditional=false is used for destroyApp, throw MIDletStateChangeException and start or continue saving.
 * IB 2010-11-22 1.11.5Dev14 Have open mobilizer and no pic mobilizer for item and bookmark details forms.
 * IB 2011-01-31 1.11.5Dev17 Change items to array to save on memory and for simplicity.
 * IB 2011-02-02 1.11.5Dev17 Allow optional saving of only the feed header name, user/pass, and link.
 * IB 2011-03-06 1.11.5Dev17 Specify imports without '*'.
 * IB 2011-03-06 1.11.5Dev17 Use RssItem instead of RssItunesItem to allow future difference in the two.
 * IB 2011-03-06 1.11.5Dev17 Standardize var names.
 * IB 2011-03-06 1.11.5Dev17 Have long command name option for some commands.
 * IB 2011-03-06 1.11.5Dev17 Synchronize access to m_platformURL.
 * IB 2011-03-13 1.11.5Dev17 Parse and store feeds from FeedListParser in ImportFeedsForm.  Put new feeds into the booklist in the form code.
 * IB 2011-03-13 1.11.5Dev17 Cosmetic changes.
 * IB 2011-03-13 1.11.5Dev17 More logging.
 * IB 2011-03-13 1.11.5Dev17 Null bookmarks StringBuffer to make sure that we save memory starting recovery code for out of memory.
*/

// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define itunes define
@DSMARTPHONEDEF@
// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define novice define
@DNOVICEDEF@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define signed define
@DSIGNEDDEF@
// Expand to define logging define
@DLOGDEF@

package com.substanceofcode.rssreader.presentation;

import java.lang.SecurityException;
//#ifdef DFULLVERS
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.rssreader.businessentities.RssItunesItem;
import com.substanceofcode.rssreader.businessentities.RssItem;
//#endif
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
//#ifdef DFULLVERS
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.RssFormatParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
//#endif
//#ifdef DFULLVERS
import com.substanceofcode.rssreader.presentation.AllNewsList;
//#endif
import com.substanceofcode.utils.Settings;
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.CauseException;
import com.substanceofcode.utils.CauseRecStoreException;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
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
import com.substanceofcode.testutil.TestOutput;
//#endif

//#ifdef DJSR238
import javax.microedition.global.ResourceManager;
//#endif

//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
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
//#ifdef DFULLVERS
implements 
	//#ifdef DMIDP20
			Observer,
	//#endif
	CommandListener,
	Runnable
//#endif
{
    
	final       Object nullPtr;
    // Attributes
    private Displayable m_prevDisp;         // The displayable to return to
    private Settings    m_settings;         // The settings
    private RssReaderSettings m_appSettings;// The application settings
	//#ifdef DFULLVERS
    private RssFeedStore m_rssFeeds; // The bookmark URLs
	//#endif
    final public boolean JSR75_ENABLED;
	//#ifdef DMIDP20
    final public String GOOGLE_MOBILITY;
    final public String GOOGLE_NO_IMAGES;
    final public String SKWEEZER_MOBILITY;
	//#endif
	//#ifdef DTEST
    private boolean     m_debugOutput = true; // Flag to write to output for test
	//#endif
    private boolean     m_getPage;          // The noticy flag for HTTP
    private boolean     m_openPage;         // Open the headers
    volatile private boolean m_saveBookmarks;    // The save bookmarks flag
    volatile private boolean m_exit;             // The exit application flag
    volatile private boolean m_saving;           // The saving settings flag
    volatile private boolean m_stored;           // The data stored flag
    private boolean     m_about;            // The about flag
    private boolean     m_getModPage;       // The noticy flag for modified HTTP
    private boolean     m_getSettingsForm;  // Flag to get settings form
    private boolean     m_getAddBMForm;     // Flag to get add bookmark form
    private boolean     m_getEditBMForm;    // Flag to get edit bookmark form
    private boolean     m_refreshAllFeeds;  // The notify flag for all feeds
    private boolean     m_refreshUpdFeeds;  // The notify flag for updated feeds
    private boolean     m_getImportForm;    // The noticy flag for going to Import Feed list
	//#ifdef DSIGNED
	//#ifdef DJSR75
    private boolean     m_getExportForm;    // The noticy flag for going to Export Feed list
	//#endif
	//#endif
    private boolean     m_runNews;  // Run AllNewsList form.
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
    volatile private boolean m_firstTime;
    volatile private boolean m_showLicense;
    volatile private boolean m_needFormInit;
    private boolean     m_novice;
    private boolean     m_itunesEnabled;
	//#ifdef DMIDP20
    private boolean     m_parseBackground;
	//#endif
	//#ifdef DLOGGING
    private boolean fineLoggable;
    private boolean finestLoggable;
	//#endif
	// This is a mark (icon) next to unread items (except on unread items
	// screen).  Given that many screens are small, it is optional as 
	// we don't want to reduce space for text.
	private Image           m_unreadImage;
    
    // Currently selected bookmark
    private int             m_curBookmark;  // The currently selected item
	//#ifdef DMIDP20
    volatile private Observable m_backGrParser; // The currently selected RSS in background
    volatile private Observable m_backGrFdlParser; // The currently selected feed list in background
	//#endif
    
    // GUI items
	//#ifdef DFULLVERS
    private FeatureList  m_bookmarkList; // The bookmark list
	//#else
    private SettingsForm  m_settingsForm; // The bookmark list
	//#endif
	//#ifdef DTESTUI
	//#ifdef DFULLVERS
    private HeaderList  m_headerTestList;       // The header list
    private AllNewsList m_allNewsTestList; // The test header list for unread items
	//#endif
	//#endif
    private Displayable m_itemRtnList;      // The list to return from for item
	//#ifdef DFULLVERS
    private ItemForm    m_itemForm;         // The item form
	//#endif
    private LoadingForm m_loadForm;  // The "loading..." form
    
    // Commands
	//#ifdef DTESTUI
	private Command     m_testBMCmd;        // Test UI bookmarks list command
	private Command     m_testRtnCmd;       // Test UI return to prev command
	//#endif
    private Command     m_saveCommand;      // The save without exit command
    private Command     m_addNewBookmark;   // The add new bookmark command
    private Command     m_openBookmark;     // The open bookmark command
    private Command     m_readUnreadItems;  // The read unread items command
    private Command     m_editBookmark;     // The edit bookmark command
    private Command     m_delBookmark;      // The delete bookmark command
	//#ifdef DMIDP20
	final public Command m_openLinkCmd;// The open link command
	//#endif
    private Command     m_importFeedListCmd;// The import feed list command
	//#ifdef DSIGNED
	//#ifdef DMIDP20
	//#ifdef DJSR75
    private Command     m_exportFeedListCmd;// The export feed list command
	//#endif
	//#endif
	//#endif
	//#ifdef DTEST
    private Command     m_importCurrFeedListCmd; // The import feed list command and default current seleected feed
    private Command     m_reloadDbCmd; // The close and reload the database.
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
    private Command     m_updateAllCmd;     // The update all command
    private Command     m_updateAllModCmd;  // The update all modified command
    
    private int m_citemLnkNbr;
    private int m_citemEnclNbr;
	//#ifdef DFULLVERS
    private RssItem m_citem;
	//#endif
	//#ifdef DLOGGING
	//#ifdef DFULLVERS
    private LoggerRptForm m_debug;
	//#endif
    private boolean m_retryLog = false;
    private Level m_retryLevel = Level.FINEST;
    private Logger logger;
	//#endif
    
    public RssReaderMIDlet()
	throws SecurityException {
		FeatureMgr.setDisplay(Display.getDisplay(this));
		FeatureMgr.setMidlet(this);

		nullPtr = null;
		m_runNews = false;
		m_firstTime = false;
		m_showLicense = false;
		m_unreadImage = null;
		//#ifdef DFULLVERS
		m_bookmarkList = (FeatureList)nullPtr;
		m_citem = (RssItem)nullPtr;
		//#else
		m_settingsForm = null;
		//#endif
		m_loadForm = null;
		m_citemLnkNbr = -1;
		m_citemEnclNbr = -1;
		m_needFormInit = true;
		//#ifdef DMIDP20
		GOOGLE_MOBILITY = "http://www.google.com/gwt/n?u=";
		GOOGLE_NO_IMAGES = "&_gwt_noimg=1";
		SKWEEZER_MOBILITY = "http://www.skweezer.com/s.aspx?q=";
		m_parseBackground = false;
		m_backGrParser = (Observable)nullPtr; // The currently selected RSS in background
		m_backGrFdlParser = (Observable)nullPtr; // The currently selected RSS in background
		//#endif

		//#ifdef DTESTUI
		TestOutput.init(System.out, "UTF-8");
		//#endif

		//#ifdef DLOGGING
		try {
			initializeLoadingForm("Loading items...", null);
			m_loadForm.addQuit();
		} catch (Throwable t) {
			if (m_loadForm != null) {
				m_loadForm.recordExcForm("Internal error loading form.",
						t);
			}
		}

		boolean loggingErr = false;
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
			initLogVars();
			//#ifdef DFULLVERS
			logger.info("RssReaderMIDlet has form handler=" + (m_debug != null));
			m_debug = new LoggerRptForm(logManager, this,
					this, "net.sf.jlogmicro.util.logging.FormHandler");
			//#endif
		} catch (Throwable t) {
			loggingErr = true;
			m_loadForm.appendMsg("Error initiating logging " +
					t.getClass().getName() + "," + t.getMessage());
			String [] msgs = LogManager.getLogManager().getStartMsgs();
			m_loadForm.addExc("msgs.length" + msgs.length, t);
			for (int ic = 0; ic < msgs.length; ic++) {
				m_loadForm.appendMsg(msgs[ic]);
			}
			System.out.println("Error initiating logging" + t);
			t.printStackTrace();
		}
		//#endif

		//#ifdef DFULLVERS
		// Initialize this after logging is initialized.
		m_rssFeeds = new RssFeedStore();
		//#endif
		//#ifdef DMIDP20
		m_openLinkCmd  = new Command("Open link", Command.SCREEN, 5);
		//#endif
		try {

			//#ifdef DLOGGING
			if (loggingErr) {
				return;
			}
			//#endif

			if (m_loadForm == null) {
				try {
					initializeLoadingForm("Loading items...", null);
					m_loadForm.addQuit();
				} catch (Throwable t) {
					if (m_loadForm != null) {
						m_loadForm.recordExcForm("Internal error loading form.",
								t);
					}
				}
			}

			Object[] arrsettings = FeatureMgr.initSettingsEnabled(m_loadForm
					//#ifdef DLOGGING
					,logger
					,fineLoggable
					//#endif
					);
			m_appSettings = (RssReaderSettings)arrsettings[0];
			m_settings = (Settings)arrsettings[1];
			m_firstTime = ((Boolean)arrsettings[2]).booleanValue();
			m_itunesEnabled = ((Boolean)arrsettings[3]).booleanValue();
			m_showLicense = m_firstTime || !m_appSettings.getAcceptLicense();

			/** Initialize commands */
			if (FeatureMgr.m_backCommand == null) {
				FeatureMgr.m_backCommand = new Command("Back", Command.BACK, 1);
			}
			//#ifdef DFULLVERS
			initExit(m_bookmarkList);
			//#else
			initExit(m_settingsForm);
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
			//#ifdef DSIGNED
			//#ifdef DJSR75
			m_getExportForm = false;
			//#endif
			//#endif
			m_curBookmark = -1;
			CauseException ce = null;

			// To get proper initialization, need to 
			//#ifdef DLOGGING
			if (m_appSettings != null) {
				if (m_appSettings.getLogLevel().length() == 0) {
					m_appSettings.setLogLevel(
							logger.getParent().getLevel().getName());
				} else {
					logger.getParent().setLevel(
							Level.parse(m_appSettings.getLogLevel()));
					//#ifdef DLOGGING
					initLogVars();
					//#endif
				}
			}
			//#endif

			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("Constructor m_appSettings,m_settings,m_firstTime,m_showLicense,m_itunesEnabled=" + m_appSettings + "," + m_settings + "," + m_firstTime + "," + m_showLicense + "," + m_itunesEnabled);}
			//#endif

			//#ifdef DFULLVERS
			if ((m_appSettings != null) && m_appSettings.getMarkUnreadItems()) {
				m_unreadImage = FeatureMgr.getImage("/icons/unread.png", m_loadForm);
			}

			if (ce != null) {
				m_loadForm.addExc(ce.getMessage(), ce);
			}
			// Need to create bookmark list to create it's thread which will
			// finish initialization of the bookmark list.
			m_bookmarkList = new FeatureList("Bookmarks", List.IMPLICIT,
											 m_loadForm);
			FeatureMgr.setMainDisp(m_bookmarkList);
			//#else
			m_unreadImage = null;
			m_settingsForm = new SettingsForm(m_loadForm);
			FeatureMgr.setMainDisp(m_settingsForm);
			//#endif

		} catch(Throwable t) {
			m_settings = null;
			m_appSettings = null;
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
		} finally {
			// Get here so that bookmarklist knows to not use some commands
			boolean hasApps;
			m_novice = (hasApps = (m_appSettings != null)) &&
				m_appSettings.getNovice();
			m_itunesEnabled = hasApps && m_appSettings.getItunesEnabled();
			//#ifdef DFULLVERS
			JSR75_ENABLED = (m_bookmarkList != null) &&
						((Boolean)m_bookmarkList.getFeatureMgr(
						).jsr75Avail()[0]).booleanValue();
			//#else
			JSR75_ENABLED = (m_settingsForm != null) &&
						((Boolean)m_settingsForm.getFeatureMgr(
						).jsr75Avail()[0]).booleanValue();
			//#endif
			//#ifdef DLOGGING
			if ((logger != null) && fineLoggable) {logger.fine("JSR75_ENABLED=" + JSR75_ENABLED);}
			//#endif
			//#ifdef DFULLVERS
			if (m_bookmarkList != null) {
				m_bookmarkList.setCommandListener( this, true );
			}
			//#else
			if (m_settingsForm != null) {
				m_settingsForm.setCommandListener( m_settingsForm, true );
			}
			//#endif
		}
	}
    
	//#ifdef DLOGGING
	public void initLogVars() {
		fineLoggable = logger.isLoggable(Level.FINE);
		logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
		finestLoggable = logger.isLoggable(Level.FINEST);
		logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
		m_retryLevel = Level.FINEST;
		m_retryLog = false; // fineLoggable
	}
	//#endif

	/* Create exit command based on if it's a standard exit. */
	final public void initExit(Displayable disp) {
		boolean prevExit = (FeatureMgr.m_exitCommand != null);
		if (prevExit && (disp != null)) {
			disp.removeCommand(FeatureMgr.m_exitCommand);
		}
		if (m_appSettings != null) {
			FeatureMgr.m_exitCommand = new Command("Exit",
					(m_appSettings.getUseStandardExit() ? Command.EXIT
					 : Command.SCREEN), 30);
		} else {
			FeatureMgr.m_exitCommand = new Command("Exit", Command.SCREEN, 30);
		}
		if (prevExit && (disp != null)) {
			if (disp instanceof FeatureList) {
			((FeatureList)disp).addPromptCommand(FeatureMgr.m_exitCommand,
					"Are you sure you want to exit?");
			} else if (disp instanceof FeatureForm) {
				((FeatureForm)disp).addPromptCommand(FeatureMgr.m_exitCommand,
						"Are you sure you want to exit?");
			} else {
				disp.addCommand(FeatureMgr.m_exitCommand);
			}
		}
	}

	/* Initialize the forms that are not dynamic. */
	//#ifdef DFULLVERS
	final private void initForms()
	//#else
	final public void initForms()
	//#endif
	{
		try {
			/** Initialize GUI items */
			//#ifdef DFULLVERS
			initializeBookmarkList();
			//#else
			m_settingsForm = initializeSettingsForm(false, m_loadForm);
			//#endif
			//initializeLoadingForm();
			//#ifdef DLOGGING
			//#ifdef DFULLVERS
			if (m_debug != null) {
				initializeDebugForm();
			}
			//#endif
			//#endif
			//#ifdef DTEST
			System.gc();
			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			//#ifdef DTEST
			System.gc();
			System.out.println("TestingForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("m_novice=" + m_novice);}
			//#endif
			try {
				if( m_firstTime ) {
					//#ifdef DFULLVERS
					// Set Max item count to default so that it is initialized.
					if (m_appSettings != null) {
						m_appSettings.setMaximumItemCountInFeed(
								m_appSettings.getMaximumItemCountInFeed());
					}
					saveBkMrkSettings("Initializing database...",
							System.currentTimeMillis(), m_firstTime, false,
							m_rssFeeds, m_bookmarkList, m_loadForm, m_settings
							//#ifdef DLOGGING
							,logger
							,fineLoggable
							,finestLoggable
							//#endif
							);
					//#endif
				}
				if (m_showLicense) {
					// If novice, show about later.
					if (!m_novice) {
						m_showLicense = false;
						getSetLicensePrompt(false);
					}
				} else {
					// If not novice, show bookmark.  If we are novice,
					// we only show novice if we have already loaded the
					// novice bookmarks.
					if (!m_novice) {
						//#ifdef DFULLVERS
						setCurrentNotes( m_bookmarkList );
						//#else
						setCurrentNotes( m_settingsForm );
						//#endif
					}
				}
			} catch(Exception e) {
				System.err.println("Error while getting/updating settings: " + e.toString());
				//#ifdef DFULLVERS
				m_loadForm.replaceRef(null, m_bookmarkList);
				//#else
				m_loadForm.replaceRef(null, m_settingsForm);
				//#endif
				m_loadForm.recordExcForm("Internal error.  Unable to initialize forms",
						e);
			}

		} catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("initForms ", t);
			//#endif
			/** Error while initializing forms */
			System.out.println("initForms " + t.getMessage());
			t.printStackTrace();
			//#ifdef DFULLVERS
			m_loadForm.replaceRef(null, m_bookmarkList);
			//#else
			m_loadForm.replaceRef(null, m_settingsForm);
			//#endif
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
    
	//#ifdef DFULLVERS
    /** Show bookmark list */
    final public void showBookmarkList() {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("before m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		m_itunesEnabled = (m_appSettings != null) &&
			m_appSettings.getItunesEnabled();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("after m_itunesEnabled=" + m_itunesEnabled);}
		//#endif
		if ( m_bookmarkList != null) {
			setCurrentAlt( null, null, m_bookmarkList );
		}
    }
	//#endif
    
	//#ifdef DFULLVERS
    /** Load bookmarks from record store */
    final private void initializeBookmarkList() {
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		int nbrRegions = m_firstTime ? (m_novice ? 2 : 1) : (m_settings.MAX_REGIONS + 1);
		Gauge gauge = new Gauge("Initializing bookmarks...",
				false, nbrRegions, 0);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("m_settings.MAX_REGIONS,gauge.getMaxValue()=" + m_settings.MAX_REGIONS + "," + gauge.getMaxValue());}
		//#endif
        try {
			if (m_bookmarkList == null) {
				m_bookmarkList = new FeatureList("Bookmarks", List.IMPLICIT,
						m_loadForm);
				//#ifdef DLOGGING
				logger.warning(
						"Bookmarks should have been created in constructor");
				//#endif
			}
			m_loadForm.replaceRef(null, m_bookmarkList);
			//#ifdef DMIDP20
			// If font is wrong, it can cause an exception for some
			// devices.  This leaves some of the data not loaded.
			// So, we'll update this later
            m_bookmarkList.setFont(null);
			//#endif
			int priority = 7;
			if (!m_novice) {
				m_addNewBookmark = FeatureMgr.getCmdAdd(m_bookmarkList,
						"Add", "Add new feed", Command.SCREEN, priority++);
			}
			m_openBookmark = FeatureMgr.getCmdAdd(m_bookmarkList,
					"Open", "Open feed", Command.SCREEN, 5);
			m_readUnreadItems = FeatureMgr.getCmdAdd(m_bookmarkList,
					"River", "River of news", Command.SCREEN, priority++);
			if (!m_novice) {
				m_editBookmark = FeatureMgr.getCmdAdd(m_bookmarkList, "Edit",
						"Edit feed", Command.SCREEN, priority++);
				m_delBookmark = FeatureMgr.getCmdAddPrompt(m_bookmarkList,
						"Delete", "Delete feed", Command.SCREEN, priority++, "Are you sure you want to delete?" );
				m_importFeedListCmd = FeatureMgr.getCmdAdd(m_bookmarkList,
						"Import", "Import feeds", Command.SCREEN, priority++);
				int spriority = priority;
				//#ifdef DSIGNED
				//#ifdef DJSR75
				m_exportFeedListCmd = FeatureMgr.getCmdAdd(m_bookmarkList,
						"Export", "Export feeds", Command.SCREEN, priority++);
				//#endif
				//#endif
				if (spriority == priority) {
					priority++;
				}
				//#ifdef DTEST
				m_importCurrFeedListCmd = FeatureMgr.getCmdAdd(m_bookmarkList,
						"Import current", "Import current feed", Command.SCREEN, priority++);
				//#else
				priority++;
				//#endif
			}
			m_updateAllModCmd = FeatureMgr.getCmdAdd(m_bookmarkList,
					"Update mod", "Update modified all", Command.SCREEN, priority++);
			m_updateAllCmd = FeatureMgr.getCmdAddPrompt(m_bookmarkList,
					"Update all", null, Command.SCREEN, priority++,
					"Are you sure that you want to upgrade all?  " +
					"Unlike update modified all, update all does not use " +
					"conditional gets.  This can use more network " +
					"resources.  Also, all read flags are reset.");
			//#ifdef DTEST
			m_reloadDbCmd = FeatureMgr.getCmdAdd(m_bookmarkList, "Reload DB",
					null, Command.SCREEN, priority++);
			//#else
			priority++;
			//#endif
			m_settingsCmd = FeatureMgr.getCmdAdd(m_bookmarkList, "Settings",
					null, Command.SCREEN, priority++);
			m_saveCommand = FeatureMgr.getCmdAdd(m_bookmarkList,
					"Save only", "Save without exit", Command.SCREEN,
					priority++);
            m_bookmarkList.addPromptCommand( FeatureMgr.m_exitCommand,
					                         "Are you sure you want to exit?" );
			FeatureMgr.m_aboutCmd = FeatureMgr.getCmdAdd(m_bookmarkList,
					"About", null, Command.SCREEN, priority++);
			//#ifdef DTESTUI
			m_testBMCmd = FeatureMgr.getCmdAdd(m_bookmarkList,
					"Test bookmarks shown", null, Command.SCREEN, 10);
			m_testRtnCmd = FeatureMgr.getCmdAdd(m_bookmarkList,
					"Test go back to last", null, Command.SCREEN, 11);
			//#endif
			//#ifdef DTESTUI
			m_testEncCmd = FeatureMgr.getCmdAdd(m_bookmarkList, "Testing Form", null, Command.SCREEN, priority++);
			//#endif
			//#ifdef DLOGGING
			if (m_debug != null) {
				m_debugCmd = FeatureMgr.getCmdAdd(m_bookmarkList, "Debug Log", null, Command.SCREEN, priority++);
			}
			//#endif
			//#ifdef DTEST
			System.gc();
			System.out.println("empty bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
            
			loadBookmarkList(gauge, m_rssFeeds, m_bookmarkList, m_loadForm,
							 m_settings,
							 m_firstTime
						//#ifdef DTEST
						,m_debugOutput
						//#endif
						//#ifdef DLOGGING
						,logger
						,fineLoggable
						//#endif
					);

			m_firstTime = false;

			//#ifdef DMIDP20
			if ((m_appSettings != null) &&
					(m_appSettings.getFontChoice() !=
					m_appSettings.DEFAULT_FONT_CHOICE)) {
				final int len = m_bookmarkList.size();
				m_bookmarkList.initFont();
				final Font font = m_bookmarkList.getFont();
				for (int ic = 0; ic < len; ic++) {
					m_bookmarkList.setFont(ic, font);
				}
			}
			//#endif
			if (m_bookmarkList.size() > 0) {
				m_bookmarkList.setSelectedIndex(0, true);
			}
			m_loadForm.addStartCmd( m_bookmarkList );
		} catch (Throwable t) {
			procBookmarkExc("Error while initializing bookmark list", t,
					m_loadForm
						//#ifdef DLOGGING
						,logger
						//#endif
					);
		}
	}
	//#endif

    /** Show settigs form.  */
    final public SettingsForm initializeSettingsForm(boolean createForm,
			LoadingForm loadForm) {
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		SettingsForm settingsForm = null;
		//#ifdef DINTLINK
		if (!createForm && (loadForm == null)) {
			loadForm = m_loadForm;
		}
		//#endif

		try {
			if (createForm) {
				settingsForm = new SettingsForm(m_loadForm);
				settingsForm.setCommandListener( settingsForm, false );
			//#ifdef DINTLINK
			} else {
				settingsForm = m_settingsForm;
			//#endif
			}
			settingsForm.initForm();
			settingsForm.updateForm();
			//#ifdef DTEST
			System.gc();
			System.out.println("SettingsForm size=" +
					(beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			// If not full vers.
			//#ifdef DINTLINK
			loadForm.addStartCmd( settingsForm );
			//#endif
		} catch (Throwable t) {
			procBookmarkExc("Error while initializing settings form", t,
					loadForm
						//#ifdef DLOGGING
						,logger
						//#endif
					);
		}
		return settingsForm;
	}

	//#ifdef DFULLVERS
    /** Load bookmarks from record store */
    static final public void loadBookmarkList(Gauge gauge,
			RssFeedStore rssFeeds, final Choice bookmarkList,
			final LoadingForm loadForm,
			final Settings settings,
			final boolean firstTime
					//#ifdef DTEST
					,boolean debugOutput
					//#endif
				//#ifdef DLOGGING
				,Logger logger
				,boolean fineLoggable
				//#endif
			) {

		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("loadBookmarkList firstTime=" + firstTime);}
		//#endif
		int pl = -1;
		try {
			if (gauge != null) {
				pl = loadForm.append(gauge);
			}
			if (!firstTime) {
				for (int ic = 1; ic < settings.MAX_REGIONS; ic++) {
					boolean stop = false;
					String bms = settings.getStringProperty(ic, settings.BOOKMARKS_NAME, "");
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("loadBookmarkList bms.length()=" + bms.length());}
					//#endif
					try {
						if(bms.length() == 0) {
							continue;
						}
						final String vers = settings.getStringProperty(ic,
								settings.SETTINGS_NAME, "");
						final boolean firstSettings =
							 vers.equals(settings.FIRST_SETTINGS_VERS);
						final boolean itunesCapable = ((vers.length() > 0) &&
							 (vers.compareTo(settings.ITUNES_CAPABLE_VERS) >= 0));
						final boolean encodingSettings = ((vers.length() > 0) &&
							 (vers.compareTo(settings.ENCODING_VERS) >= 0));
						final boolean modifiedSettings = vers.equals(
								settings.MODIFIED_VERS);
						settings.getBooleanProperty(settings.ITEMS_ENCODED,
									true);
						/* FUTURE
						final long storeDate = settings.getLongProperty(
								settings.STORE_DATE, 0L);
							*/
						final char feedSeparator =
							encodingSettings ? settings.CFEED_SEPARATOR : settings.OLD_FEED_SEPARATOR;
						//#ifdef DLOGGING
						if (fineLoggable) {logger.fine("loadBookmarkList region,vers,firstSettings,itunesCapable,encodingSettings,modifiedSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + encodingSettings + "," + modifiedSettings);}
						//#endif
						//#ifdef DTEST
						if (debugOutput) System.out.println("loadBookmarkList region,vers,firstSettings,itunesCapable,encodingSettings,modifiedSettings=" + ic + "," + vers + "," + firstSettings + "," + itunesCapable + "," + encodingSettings + "," + modifiedSettings);
						//#endif
						// Save memory by setting bookmarks to "" now that
						// we will convert them to objects.
						settings.setStringProperty(settings.BOOKMARKS_NAME, "");
						
						do {
							
							String part = "";
							int pos = bms.indexOf(feedSeparator);
							if(pos > 0) {
								part = bms.substring(0, pos);
							}
							bms = bms.substring(pos+1);
							if(part.length()>0) {
								RssItunesFeed bm = null;
								if (itunesCapable) {
									bm = RssItunesFeed.deserialize(modifiedSettings,
											true, part );
								} else {
									bm = new RssItunesFeed(new RssFeed(
												firstSettings, true, part ));
								}
								if(bm.getName().length()>0){
									bookmarkList.append(bm.getName(),null);
									rssFeeds.put(bm.getName(), bm, null);
								}
							}
							if( part.length()==0)
								stop = true;
						}while(!stop);
					} finally {
						if (gauge != null) {
							gauge.setValue(ic);
						}
					}
				}
				//#ifdef DTEST
				System.gc();
				System.out.println("full bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
				//#endif
			}
			pl = -1;
			if (gauge != null) {
				gauge.setValue(gauge.getMaxValue());
			}
			// Reset internal region to 0.
			settings.getStringProperty(0, settings.BOOKMARKS_NAME, "");
			//#ifdef DTEST
			System.gc();
			System.out.println("full bookmarkList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
		} catch(Throwable t) {
			procBookmarkExc("Error while loading bookmark list", t, loadForm
						//#ifdef DLOGGING
						,logger
						//#endif
					);
		} finally {
			if (pl >= 0) {
				loadForm.delete(pl);
			}
		}
    }
	//#endif
    
	static public void procBookmarkExc(String excMsg, Throwable t,
								  final LoadingForm loadForm
								  //#ifdef DLOGGING
								  ,Logger logger
								  //#endif
			) {
		if (t instanceof Exception) {
			loadForm.recordExcForm(excMsg, t);
		} else if (t instanceof OutOfMemoryError) {
			CauseException ce = new CauseException(excMsg, t);
			loadForm.recordExcForm(
					"Out Of Memory Error initializing/loading form", ce);
		} else {
			CauseException ce = new CauseException(excMsg, t);
			loadForm.recordExcForm(
					"Internal error initializing/loading form", ce);
		}
	}

	/** Show loading form */
	final public void showLoadingForm() {
		if ( m_loadForm != null) {
			m_loadForm.getFeatureMgr().showMe();
		}
	}
	
	/** Show loading form */
	final public void showLoadingForm(Displayable fform) {
		if ( m_loadForm != null) {
			m_loadForm.getFeatureMgr().setCurrentFeature( fform, (Displayable)null, m_loadForm );
		}
	}
	
	/** Initialize loading form */
	final public LoadingForm initializeLoadingForm(final String desc,
									   Displayable disp,
									   //#ifdef DMIDP20
									   Observable observable
									   //#else
									   Object observable
									   //#endif
			)
					{
		m_loadForm = LoadingForm.getLoadingForm(desc, disp, observable);
		return m_loadForm;
    }

    final public LoadingForm initializeLoadingForm(final String desc,
									   Displayable disp) {
		return initializeLoadingForm(desc, disp, null);
	}

	//#ifdef DFULLVERS
	//#ifdef DLOGGING
    final public void initializeDebugForm() {
		m_clearDebugCmd = FeatureMgr.getCmdAdd(m_debug, "Clear", null, Command.SCREEN, 7);
		m_backFrDebugCmd = FeatureMgr.getCmdAdd(m_debug, "Back", null, Command.BACK, 1);
        m_debug.setCommandListener(this);
	}
	//#endif

	static public boolean modUpdAllFeeds(int maxItemCount,
					boolean refreshUpdFeeds,
					RssFeedStore rssFeeds,
					LoadingForm cloadForm
					//#ifdef DLOGGING
					,boolean retryLog
					,Level retryLevel
					,Logger logger
					//#endif
					) {
		boolean errFound = false;
		Enumeration feedEnum = rssFeeds.elements();
		while(feedEnum.hasMoreElements()) {
			RssItunesFeed feed = (RssItunesFeed)feedEnum.nextElement();
			try{
				if (cloadForm != null) {
					cloadForm.appendMsg(feed.getName() + "...");
				//#ifdef DLOGGING
				} else {
					logger.info(feed.getName() + "...");
				//#endif
				}
				RssFeedParser parser = new RssFeedParser(
						feed, feed, refreshUpdFeeds );
				parser.parseModRssFeed(refreshUpdFeeds,
						maxItemCount);
				feed = parser.getRssFeed();
				RssItunesFeed oldfeed = rssFeeds.get(
						feed.getName());
				if (oldfeed != null) {
					feed.checkPresRead(true, feed);
				}
				rssFeeds.put(feed.getName(), feed, null);
				if (cloadForm != null) {
					cloadForm.appendMsg("ok\n");
				//#ifdef DLOGGING
				} else {
					logger.info("ok\n");
				//#endif
				}
			} catch(Exception ex) {
				CauseException ce = new CauseException(
						"Error parsing feed " + feed.getName(),
						ex);
				//#ifdef DLOGGING
				logger.severe(ce.getMessage(), ex);
				//#else
				System.out.println(ce.getMessage());
				ex.printStackTrace();
				//#endif
				if (cloadForm != null) {
					cloadForm.addExc("Error\n", ce);
				}
				errFound = true;
			}
		}
		return errFound;
	}
	//#endif

	public void initializeAboutForm() {
		// Because of problems with alerts on T637, need to
		// show a form before we show the alert, or it never
		// appears.
		initializeLoadingForm("Loading about...",
				FeatureMgr.getMainDisp());
		getSetLicensePrompt(true);
	}

	//#ifdef DFULLVERS
    /** Run method is used to get RSS feed with HttpConnection, etc */
    public void run() {
		try {
			if (m_needFormInit) {
				Thread.sleep(1L);
				Thread.yield();
				initApp();
				m_needFormInit = false;
			}
			//#endif
			// Start full version.  This ends far down with matching comment.
			//#ifdef DFULLVERS
			//#ifdef DTESTUI
			// If there are headers, and the header index is >= 0,
			// open the header so that it's items can be listed
			// with test UI classes.
			// Need to change the selection to match the m_headerIndex.
			if (m_headerNext && (m_headerIndex >= 0) &&
					(m_headerTestList != null) &&
				(m_headerIndex < m_headerTestList.size()) &&
				(FeatureMgr.getCurrent() == m_headerTestList)) {
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
				(FeatureMgr.getCurrent() == m_itemForm )) {
				m_itemNext = false;
				m_itemForm.commandAction( FeatureMgr.m_backCommand, m_itemForm );
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
				try {
					final SettingsForm settingsForm = initializeSettingsForm(
							true, m_loadForm);
					setCurrent( null, settingsForm );
				} catch(Throwable t) {
					// Message already given.
					t.printStackTrace();
				}
			}

			/* Handle going to bookmark form. */
			if( m_getAddBMForm || m_getEditBMForm ) {
				RssItunesFeed bm = null;
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
						bm = (RssItunesFeed)m_rssFeeds.get(
								m_bookmarkList.getString(m_curBookmark));
						bmForm = new BMForm(m_rssFeeds, m_appSettings,
								m_bookmarkList, m_loadForm, bm);
					} else {
						bmForm = new BMForm(m_rssFeeds, m_appSettings,
								m_bookmarkList, m_loadForm);
					}
					bmForm.setCommandListener( bmForm, false );
					//#ifdef DTEST
					System.gc();
					System.out.println("BMForm size=" +
							(beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( null, bmForm );
				} catch(OutOfMemoryError e) {
					m_loadForm.recordExcForm("\nOut Of Memory Error loading " +
							"bookmark form", e);
				} catch(Throwable t) {
					m_loadForm.recordExcForm("\nInternal error loading bookmark " +
							"form" + ((m_getEditBMForm ? (" " + bm.getName()) : " ")), t);
				} finally {
					m_getAddBMForm = false;
					m_getEditBMForm = false;
				}
			}

			if( m_refreshAllFeeds || m_refreshUpdFeeds ) {
				LoadingForm cloadForm = initializeLoadingForm(
						"Updating all " +
						(m_refreshUpdFeeds ? "modified " : "") +
						"feeds...", m_bookmarkList);
				try{
					final int maxItemCount = m_appSettings.getMaximumItemCountInFeed();
					boolean errFound = modUpdAllFeeds(maxItemCount,
							m_refreshUpdFeeds, m_rssFeeds, cloadForm
							//#ifdef DLOGGING
							,m_retryLog
							,m_retryLevel
							,logger
							//#endif
							);
					if (errFound) {
						cloadForm.setLoadingFinished(
								"Finished with one or more exceptions " +
								"or errors.",
								"Updating finished with one or more " +
								"exceptions or errors..");
						cloadForm.getFeatureMgr().showMe();
					} else {
						cloadForm.setLoadingFinished("Updating finished",
								"Updating finished use back to return.");
						showBookmarkList();
					}
				} catch(Exception ex) {
					cloadForm.recordExcForm("Error parsing feeds\n", ex);
				} catch(OutOfMemoryError ex) {
					cloadForm.recordExcForm("Out Of Memory Error parsing feeds \n",
							ex);
				} catch(Throwable t) {
					cloadForm.recordExcForm("Internal error parsing feeds from:\n",
							t);
				} finally {
					m_refreshAllFeeds = false;
					m_refreshUpdFeeds = false;
					//#ifdef DMIDP20
					if (m_appSettings.getBacklightFlashSecs() > 0) {
						FeatureMgr.getDisplay().flashBacklight(
								1000 * m_appSettings.getBacklightFlashSecs());
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("run update m_appSettings.getBacklightFlashSecs()=" + m_appSettings.getBacklightFlashSecs());}
						//#endif
					}

					if (m_appSettings.getVibrateSecs() > 0) {
						FeatureMgr.getDisplay().vibrate(
								1000 * m_appSettings.getVibrateSecs());
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("run update m_appSettings.getVibrateSecs()=" + m_appSettings.getVibrateSecs());}
						//#endif
					}
					//#endif
				}
			}

			// Go to import feed form
			if( m_getImportForm
				//#ifdef DSIGNED
				//#ifdef DJSR75
					|| m_getExportForm
				//#endif
				//#endif
					) {
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
						importFeedsForm = new ImportFeedsForm(
								m_bookmarkList, m_getImportForm, m_rssFeeds,
								m_appSettings, m_loadForm, bm.getUrl());
					} else
					//#endif
					{
						importFeedsForm = new ImportFeedsForm(
								m_bookmarkList, m_getImportForm, m_rssFeeds,
								m_appSettings,
								m_loadForm, m_appSettings.getImportUrl());
					}
					importFeedsForm.setCommandListener(importFeedsForm, true);
					//#ifdef DTEST
					System.gc();
					System.out.println("ImportForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
					//#endif
					setCurrent( null, importFeedsForm );
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
					//#ifdef DSIGNED
					//#ifdef DJSR75
					m_getExportForm = false;
					//#endif
					//#endif
					//#ifdef DTEST
					m_getTestImportForm = false;
					//#endif
				}
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
					AllNewsList allNewsList = new AllNewsList(
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
				initializeAboutForm();
			}

			if ( m_exit || m_saveBookmarks ) {
				initializeLoadingForm(
						(m_exit ?  "Exiting saving data..." :
						 "Saving data..."), FeatureMgr.getMainDisp());
				exitApp( m_exit, m_loadForm );
			}

		} catch (Throwable t) {
			try {
				if (m_loadForm == null) {
					synchronized(this) {
						if (m_loadForm == null) {
							initializeLoadingForm("Processing...",
									FeatureMgr.getMainDisp());
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
				setCurrent( null, m_loadForm );
			} catch (Throwable e) {
				m_loadForm.recordExcFormFin(
						"Internal error while processing", e);
				e.printStackTrace();
			}
		}
    }
	// End DFULLVERS
	//#endif
	
	//#ifdef DTESTUI
	final public void updHeaderNext() {
		if (m_headerIndex >= 0) {
			m_headerNext = true;
		}
	}
	//#endif

	//#ifdef DFULLVERS
	// Open existing bookmark and show headers (items).
	final public void setPageInfo(boolean openPage, boolean getPage,
		boolean getModPage, Displayable prevDisp) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("setPageInfo openPage,getPage,prevDisp=" + openPage  + "," + getPage + "," + getModPage + "," + prevDisp);}
		//#endif
		synchronized(this) {
			m_openPage = openPage;
			m_getPage = getPage;
			m_getModPage = getModPage;
			m_prevDisp = prevDisp;
		}
	}

	// Open existing bookmark and show headers (items).
	final public void procPage(RssItunesFeed feed) {

		Displayable cprevDisp = null;
		boolean     copenPage = false;
		synchronized(this) {
			cprevDisp = m_prevDisp;
			copenPage = m_openPage;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procPage copenPage,cprevDisp,feed=" + copenPage + "," + cprevDisp + "," + feed);}
		//#endif

		// Open existing bookmark and show headers (items).
		try {
			/* Loading feed... */
			initializeLoadingForm("Loading feed...", cprevDisp);
			if(feed.getUrl().length() == 0) {
				m_loadForm.recordExcFormFin("Unable to open feed.  No URL.",
						new Exception(
						"Feed has no URL cannot load."));
				return;
			}
			procHeader(feed);
		}catch(Throwable e) {
			procPageExc(feed, copenPage, e);
		} finally {
			synchronized(this) {
				m_openPage = false;
			}
		}
	}

	//#ifdef DMIDP20
	// Open existing bookmark and show headers (items).
	final public RssFeedParser procBackPage(RssItunesFeed feed,
										    boolean modfeed,
										    boolean updfeed,
										    Observer obsmain,
										    boolean initLoad,
											Observer obs1, LoadingForm loadForm) {

		if (obsmain == null) {
			obsmain = this;
		}
		RssFeedParser cbackGrParser = new RssFeedParser(feed,
				(modfeed ? feed : (RssItunesFeed)nullPtr), updfeed);
		Displayable cprevDisp = null;
		boolean     cgetPage = false;
		boolean     cgetModPage = false;
		boolean     cparseBackground = false;
		synchronized(this) {
			cprevDisp = m_prevDisp;
			cgetPage = m_getPage;
			cgetModPage = m_getModPage;
			cparseBackground = m_parseBackground;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procBackPage cgetPage,cgetModPage,cprevDisp,feed=" + cgetPage + "," + cgetModPage + "," + cprevDisp + "," + feed);}
		//#endif

		// Open existing bookmark and show headers (items).
		if( cgetPage || cgetModPage ) {
			try {
				/* Updating feed... */
				if (initLoad || (loadForm == null)) {
					loadForm = LoadingForm.getLoadingForm(
							cgetModPage ? "Updating modified feed..." :
							"Updating feed..." , cprevDisp, cbackGrParser);
					if (obs1 == null) {
						m_loadForm = loadForm;
						m_bookmarkList.getFeatureMgr().setLoadForm(loadForm);
					} else if (obs1 instanceof FeatureList) {
						((FeatureList)obs1).getFeatureMgr().setLoadForm(loadForm);
					} else if (obs1 instanceof FeatureForm) {
						((FeatureForm)obs1).getFeatureMgr().setLoadForm(loadForm);
					}
				}
				if(feed.getUrl().length() == 0) {
					loadForm.recordExcFormFin("Unable to open feed.  No URL.",
							new Exception(
							"Feed has no URL cannot load."));
					return null;
				}
				if (cparseBackground) {
					loadForm.appendNote("Note: feed is still parsing.  Wait for it to finish.");
					return null;
				}
				synchronized(this) {
					m_parseBackground = true;
				}
				/** Get RSS feed */
				final int maxItemCount =
					m_appSettings.getMaximumItemCountInFeed();
				cbackGrParser.makeObserable(cgetModPage, maxItemCount);
				loadForm.addPromptCommand(FeatureMgr.m_backCommand,
									"Are you sure that you want to go back? Parsing has not finished.");
				if (obs1 != null) {
					cbackGrParser.getObservableHandler().addObserver(obs1);
				}
				cbackGrParser.getObservableHandler().addObserver(obsmain);
				((RssFeedParser)cbackGrParser).getParsingThread().start();
				synchronized(this) {
					m_backGrParser = cbackGrParser;
				}
			}catch(Throwable e) {
				procPageExc(feed, false, e);
			} finally {
				synchronized(this) {
					m_getPage = false;
					m_openPage = false;
					m_getModPage = false;
				}
			}
		}
		return cbackGrParser;
	}
	//#endif

	// Open existing bookmark and show headers (items).
	final public void procHeader(RssItunesFeed feed) {

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procHeader feed=" + feed);}
		//#endif
		// Open existing bookmark and show headers (items).

		try {

			//#ifdef DTEST
			System.gc();
			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			HeaderList hdrList = new HeaderList(m_bookmarkList,
				m_curBookmark, m_rssFeeds,
				m_unreadImage, m_itunesEnabled, m_loadForm,
				feed);
			//#ifdef DTEST
			System.out.println("headerList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			hdrList.sortAllItems( false, m_bookmarkList, m_rssFeeds );
			hdrList.setCommandListener(hdrList, true);
			setCurrent( null, hdrList );
			m_loadForm.replaceRef(null, hdrList);
		}catch(Exception e) {
			m_loadForm.recordExcFormFin(
					"\nError loading feed on:\n" +
					feed.getUrl(), e);

		}catch(OutOfMemoryError e) {
			m_loadForm.recordExcFormFin(
					"\nOut of memory loading feed on:\n" +
					feed.getUrl(), e);
		}catch(Throwable t) {
			m_loadForm.recordExcFormFin(
					"\nInternal error loading " +
						"feed on:\n" +
					feed.getUrl(), t);
		} finally {
			synchronized(this) {
				m_openPage = false;
			}
		}
	}

    //#ifdef DMIDP20
	public RssFeedParser checkRssActive(Observable observable) {
		synchronized(this) {
			observable = observable.getObservableHandler().checkActive(
					m_parseBackground, m_backGrParser, observable);
		}
		if ((observable == null) || !(observable instanceof RssFeedParser)) {
			return null;
		}
		return (RssFeedParser)observable;
	}

	// Open existing bookmark and show headers (items).
	final public void procBackHeader(RssFeedParser cbackGrRssParser,
			Observable observable, LoadingForm loadForm) {

		cbackGrRssParser = checkRssActive(observable);
		if (cbackGrRssParser == null) {
			return;
		}

		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procBackHeader m_parseBackground=" + m_parseBackground);}
		//#endif
		procUpdHeader(cbackGrRssParser, loadForm);
	}
	//#endif

	final public void procUpdHeader(RssFeedParser parser,
			LoadingForm loadForm) {
		// Open existing bookmark and show headers (items).
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("procUpdHeader m_getPage,m_getModPage,parser=" + m_getPage + "," + m_getModPage + "," + parser);}
		//#endif
		// Open existing bookmark and show headers (items).

		RssItunesFeed feed = parser.getRssFeed();
		try {

			if(!parser.isSuccessfull()) {
				throw parser.getEx();
			}
			//#ifdef DTEST
			System.gc();
			long beginMem = Runtime.getRuntime().freeMemory();
			//#endif
			m_rssFeeds.put(feed.getName(), feed);
			HeaderList hdrList = null;
			if (m_prevDisp instanceof HeaderList) {
				hdrList = (HeaderList)m_prevDisp;
			} else {
				hdrList = new HeaderList(m_bookmarkList,
					m_curBookmark, m_rssFeeds,
					m_unreadImage, m_itunesEnabled, loadForm,
					feed);
			}
			//#ifdef DTEST
			System.out.println("headerList size=" + (beginMem - Runtime.getRuntime().freeMemory()));
			//#endif
			hdrList.sortAllItems( false, m_bookmarkList, m_rssFeeds );
			hdrList.setCommandListener(hdrList, true);
			setCurrent( null, hdrList );
			loadForm.replaceRef(null, hdrList);
		}catch(Exception e) {
			loadForm.recordExcFormFin(
					"\nError parsing feed on:\n" +
					parser.getRssFeed().getUrl(), e);

		}catch(OutOfMemoryError e) {
			loadForm.recordExcFormFin(
					"\nOut of memory parsing feed on:\n" +
					parser.getRssFeed().getUrl(), e);
		}catch(Throwable t) {
			loadForm.recordExcFormFin(
					"\nInternal error parsing feed on:\n" +
					parser.getRssFeed().getUrl(), t);
		} finally {
			synchronized(this) {
				if (parser != null) {
					loadForm.removeCommandPrompt(FeatureMgr.m_backCommand);
					//#ifdef DMIDP20
					m_parseBackground = false;
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("procUpdHeader m_parseBackground=" + m_parseBackground);}
					//#endif
					//#endif
				}
			}
		}
	}
	// End DFULLVERS
	//#endif

	/** Save data and exit the application. This accesses the database,
	    so it must not be called by commandAction as it may hang.  It must
	    be called by a separate thread.  */
	final public void exitApp(boolean exit, LoadingForm loadForm) {
		boolean cexit = false;
		boolean csaveBookmarks = false;
		synchronized(this) {
			m_exit = exit;
			m_appSettings.setAcceptLicense(true);
			if ( (m_exit || m_saveBookmarks) && !m_saving ) {
				if (m_exit && m_stored) {
					return;
				}
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("exitApp loadForm,m_exit,m_saveBookmarks,m_saving=" + loadForm + "," + m_exit + "," + m_saveBookmarks + "," + m_saving);}
				//#endif
				if ( !m_exit && !m_saveBookmarks ) {
					return;
				}
				cexit = m_exit;
				csaveBookmarks = m_saveBookmarks;
			} else {
				return;
			}
		}
		if (cexit || csaveBookmarks) {
			try {
				synchronized(this) {
					if (m_saving) {
						return;
					} else {
						m_saving = true;
					}
				}
				if (cexit) {
					loadForm.addQuit();
				}
				//#ifdef DFULLVERS
				saveBkMrkSettings("Saving items to database...",
						System.currentTimeMillis(), m_firstTime, cexit,
						m_rssFeeds, m_bookmarkList, loadForm, m_settings
						//#ifdef DLOGGING
						,logger
						,fineLoggable
						,fineLoggable
						//#endif
						);
				//#endif
				if (cexit) {
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
				//#ifdef DFULLVERS
				} else {
					loadForm.appendMsg(
							"Finished saving.  Use back to return.");
					showBookmarkList();
				//#endif
				}
			} finally {
				m_stored = m_exit;
				m_exit = false;
				m_saveBookmarks = false;
				m_saving = false;
			}
		}
	}

	/* Notify us that we are finished. */
	final public void wakeup(int loop) {
    
		//#ifdef DFULLVERS
		if (m_bookmarkList != null) {
			m_bookmarkList.getFeatureMgr().wakeup(loop);
		}
		//#else
		if (m_settingsForm != null) {
			m_settingsForm.getFeatureMgr().wakeup(loop);
		}
		//#endif
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentAlt(Displayable fform, Displayable alert, Displayable disp) {
		FeatureMgr.setMainCurrentAlt(fform, alert, disp);

	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Displayable disp) {
		setCurrentNotes(null, disp);
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Displayable alert, Displayable disp) {

		setCurrentNotes(alert, disp, null);
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrentNotes(Displayable alert, Displayable disp,
			LoadingForm cloadForm) {
		//#ifdef DTESTUI
		String title = "";
		if (disp instanceof Form) {
			title = ((Form)disp).getTitle();
		} else if (disp instanceof List) {
			title = ((List)disp).getTitle();
		}
		System.out.println("Test UI setCurrentNotes " + FeatureMgr.logDisp(disp));
		//#endif
		if (cloadForm == null) {
			cloadForm = (disp instanceof LoadingForm) ? (LoadingForm)disp : m_loadForm;
		}
		if (cloadForm.hasNotes() || cloadForm.hasExc()) {
			cloadForm.replaceRef(null, disp);
			if (!cloadForm.isLoadFinished()) {
				cloadForm.recordFin();
			}
			if (alert != null) {
				setCurrent( alert, cloadForm );
			} else {
				setCurrent( null, cloadForm );
			}
		} else {
			if (alert != null) {
				setCurrent( alert, disp );
			} else {
				setCurrent( null, disp );
			}
		}
	}

	/* Set current displayable and wake up the thread. */
	final public void setCurrent(Displayable alert, Displayable disp) {
		FeatureMgr.setMainCurrentAlt(null, alert, disp);
	}

	//#ifdef DFULLVERS
    /** Show item form */
    final public void showItemForm() {
        setCurrent( null, m_itemForm );
    }
    
	//#ifdef DTESTUI
	/** Cause item form to go back to the prev form. */
    final public void backFrItemForm() {
		m_itemForm.commandAction( FeatureMgr.m_backCommand, m_itemForm );
    }
    
    /** Show item form */
    final public boolean isItemForm() {
        return (FeatureMgr.getDisplay().getCurrent() == m_itemForm);
    }
	//#endif
    
    /** Initialize RSS item form */
    final public void initializeItemForm(final RssItunesFeed feed,
								   final RssItem item,
								   List prevList, LoadingForm loadForm) {
        System.out.println("Create new item form");
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		final String title = item.getTitle();
		m_itemRtnList = prevList;
		if (title.length() > 0) {
			m_itemForm = new ItemForm( title, title, feed, item, loadForm);
		} else {
			m_itemForm = new ItemForm( getItemDescription(item), title,
					feed, item, loadForm);
		}
		m_itemForm.setCommandListener(m_itemForm, false);
		//#ifdef DTEST
		System.out.println("itemForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
		setCurrent( null, m_itemForm );
    }
	//#endif

	//#ifdef DFULLVERS
	//#ifdef DSMARTPHONE
    /** Initialize RSS item form */
    final public void initializeDetailForm(final RssItunesFeed feed,
								   FeatureList prevList, LoadingForm loadForm) {
		//#ifdef DTEST
		System.gc();
		long beginMem = Runtime.getRuntime().freeMemory();
		//#endif
		DetailForm displayDtlForm = new DetailForm( feed, prevList, loadForm);
		displayDtlForm.setCommandListener(displayDtlForm, false);
		//#ifdef DTEST
		System.out.println("displayDtlForm size=" + (beginMem - Runtime.getRuntime().freeMemory()));
		//#endif
		displayDtlForm.getFeatureMgr().showMe();
    }
	//#endif

	/** Get the max words configured from the descritption. */
	final public String getItemDescription( final RssItem item ) {
		final String [] parts = MiscUtil.split(item.getDescription(), " ");
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
    
    public void initApp() {
		// Initialize bookmarks here since it does some work.
		if ((m_bookmarkList == null) || (m_bookmarkList.size() == 0)) {
			synchronized (this) {
				if ((m_bookmarkList == null) || (m_bookmarkList.size() == 0)) {
					initForms();
				}
				if (m_novice) {
					if (m_bookmarkList.size() == 0) {
						initializeLoadingForm("Loading items...",
								m_bookmarkList);
						try {
							FeedListParser listParser =
								new LineByLineParser(
									"jar:///data/novice.txt", "", "",
									m_rssFeeds);
							listParser.setGetFeedTitleList(true);
							listParser.setFeedNameFilter("");
							listParser.setFeedURLFilter("");
							listParser.setOverride(true);
							listParser.setMaxItemCount(
									m_appSettings.getMaximumItemCountInFeed());
							//#ifndef DSMALLMEM
							listParser.setRedirectHtml(false);
							//#endif
							listParser.run();
							ImportFeedsForm.addFeedLists(listParser,
									0, m_bookmarkList, m_loadForm);
						}catch(Throwable e) {
							m_loadForm.recordExcForm(
									"\nError loading intial bookmarks\n", e);
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
						if (!m_showLicense && !m_loadForm.hasExc()) {
							showBookmarkList();
						}
					}
					if (m_showLicense) {
						m_showLicense = false;
						getSetLicensePrompt(false);
					}
				}

			}
		}
    }
	//#endif

    /**
     * Start up the Hello MIDlet by creating the TextBox and associating
     * the exit command and listener.
     */
    public void startApp()
	throws MIDletStateChangeException {
    }
    
	final private String[] getAboutInfo() {
		return new String[] {
				"About RssReader",
 "RssReader v" + super.getAppProperty("MIDlet-Version") + "-" +
 super.getAppProperty("Program-Version") +
 " By using this program you agree with the license and disclaimer" +
 " THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR " +
 "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, " +
 "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE " +
 "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER " +
 "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING " +
 "FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS " +
 "IN THE SOFTWARE.  " +
 " Copyright (C) 2005-2007 Tommi Laukkanen, " +
 " Copyright (C) 2007-2010 Irving Bunton, Jr, " +
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
 "Using this software means that you accept this license and disclaimer."};
	}

    /**
	 * Create about alert.
	 * @author  Irving Bunton
	 * @version 1.0
	 */
	/* Alerts can cause problems.  Do not even reference them.
	final private Alert getAbout() {
		String[] aboutInfo = getAboutInfo();
		final Alert about = new Alert(aboutInfo[0], aboutInfo[1], null,
				AlertType.INFO);
		about.setTimeout(Alert.FOREVER);
 
		return about;
	}
	*/

    /**
	 * Create license prompt.
	 * @author  Irving Bunton
	 * @version 1.0
	 */
	final private Displayable getSetLicensePrompt(boolean isAbout) {
		String[] aboutInfo = getAboutInfo();
		if (isAbout) {
			m_loadForm.addStartCmd( FeatureMgr.getMainDisp() );
			m_loadForm.addQuit();
		}
		Displayable disp = m_loadForm.getFeatureMgr().getPromptDisp(
				"License and Disclaimer", aboutInfo[1],
				((m_loadForm.hasExc() || m_loadForm.hasNotes()) ?
											m_loadForm.m_loadMsgsCmd :
											m_loadForm.m_loadStartCmd),
				"Agree",
				(isAbout ? FeatureMgr.m_exitCommand : m_loadForm.m_loadQuitCmd),
				 "Disagree", m_loadForm, null);
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("disp=" + disp);}
		//#endif
		return disp;
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
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("destroyApp unconditional=" + unconditional);}
		//#endif
		//#ifdef DFULLVERS
    	if (m_bookmarkList != null) {
			if (unconditional) {
				m_bookmarkList.getFeatureMgr().setBackground(false);
			}
		//}
		//#else
    	if (m_settingsForm != null) {
			if (unconditional) {
				m_settingsForm.getFeatureMgr().setBackground(false);
			}
		//#endif
			else {
				if (m_saving) {
					throw new MIDletStateChangeException("Saving please wait.");
				} else if (!m_exit) {
					m_exit = true;
					//#ifdef DFULLVERS
					m_bookmarkList.getFeatureMgr().wakeup(2);
					//#else
					m_settingsForm.getFeatureMgr().wakeup(2);
					//#endif
					throw new MIDletStateChangeException("Saving please wait.");
				}
			}
		}
    }
    
	//#ifdef DFULLVERS
    /** Save bookmarks to record store
        releaseMemory use true if exiting as we do not need
		the rss feeds anymore, so we can save memory and avoid
		having extra memory around.  */
    static final public void saveBookmarks(final long storeDate,
			int region, boolean releaseMemory,
			RssFeedStore rssFeeds, final Choice bookmarkList,
			final LoadingForm loadForm,
			final Settings settings
			//#ifdef DLOGGING
			,final Logger logger
			,final boolean fineLoggable
			,final boolean finestLoggable
			//#endif
			) {
		System.gc();
		StringBuffer bookmarks = new StringBuffer();
		settings.setStringProperty(settings.BOOKMARKS_NAME,
				bookmarks.toString());
		settings.setLongProperty(settings.STORE_DATE, storeDate);
		final int bsize = bookmarkList.size();
		if (bsize == 0) {
			return;
		}
		//#ifdef DTEST
		int storeTime = 0;
		//#endif
		final int bookRegion = region - 1;
		final int iparts = settings.MAX_REGIONS - 1;
		final int firstIx = bookRegion * bsize / iparts;
		final int endIx = (bookRegion + 1) * bsize / iparts - 1;
        try {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("saveBookmarks firstIx,endIx=" + firstIx + "," + endIx);}
			//#endif
			Vector vstored = new Vector();
			try {
				/** Try to save feeds including items */
				for( int i=firstIx; i<=endIx; i++) {
					final String name = bookmarkList.getString(i);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("saveBookmarks i,name=" + i + "," + name);}
					//#endif
					if (!rssFeeds.containsKey( name )) {
						continue;
					}
					if( name.length()>0) {
						final RssItunesFeed rss =
							(RssItunesFeed)rssFeeds.get( name );
						//#ifdef DTEST
						long beginStore = System.currentTimeMillis();
						//#endif
						bookmarks.append(rss.getStoreString(true, true, true));
						//#ifdef DTEST
						storeTime += System.currentTimeMillis() - beginStore;
						//#endif
						bookmarks.append(settings.CFEED_SEPARATOR);
						if (releaseMemory) {
							vstored.addElement( name );
						}
					}
				}
			} catch(OutOfMemoryError error) {
				//#ifdef DLOGGING
				int len = bookmarks.length();
				//#endif
				// Null this to make sure tha we release the memory.
				bookmarks = null;
				//#ifdef DLOGGING
				logger.severe(
						"saveBookmarks Out of memory while Saving bookmarks length=" +
						len, error);
				//#endif
				loadForm.recordExcForm(
						"Out of memory while Saving bookmarks without " +
						"updated news items.  Reducing memory.", error);

				bookmarks = new StringBuffer();
				/** Save feeds without items */
				for( int i=firstIx; i<=endIx; i++) {
					final String name = bookmarkList.getString(i);
					if( name.length() == 0) {
						continue;
					}
					final RssItunesFeed rss = (RssItunesFeed)rssFeeds.get( name );
					bookmarks.append(rss.getStoreString(false, false, true));
					bookmarks.append(settings.CFEED_SEPARATOR);
					if (releaseMemory) {
						vstored.addElement( name );
					}
				}
			} finally {
				if (releaseMemory) {
					final int vslen = vstored.size();
					for (int ic = 0; ic < vslen; ic++) {
						rssFeeds.remove( (String)vstored.elementAt( ic ));
					}
				}
			}
			//#ifdef DTEST
			System.out.println("storeTime=" + storeTime);
			//#endif
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("saveBookmarks bookmarks.length()=" + bookmarks.length());}
			//#endif
            settings.setStringProperty(settings.BOOKMARKS_NAME, bookmarks.toString());
		} catch (Throwable t) {
            settings.setStringProperty(settings.BOOKMARKS_NAME, bookmarks.toString());
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

	/* Save the current bookmarks and other properties.
	   releaseMemory - true if memory used is to be released as the
	   				   bookmarks are saved.  Used when exitiing as true.
	*/
	static final public synchronized void saveBkMrkSettings(String guageTxt,
			final long storeDate,
			final boolean firstTime,
			final boolean releaseMemory,
			RssFeedStore rssFeeds, final Choice bookmarkList,
			final LoadingForm loadForm,
			final Settings settings
			//#ifdef DLOGGING
			,final Logger logger
			,final boolean fineLoggable
			,final boolean finestLoggable
			//#endif
			) {
		int nbrRegions = firstTime ? 1 : settings.MAX_REGIONS + 1;
		Gauge gauge = new Gauge(guageTxt, false, nbrRegions, 0);
		int pl = loadForm.append(gauge);
		loadForm.getFeatureMgr().showMe();
		try {
			if (!firstTime) {
				settings.setLongProperty(settings.STORE_DATE, storeDate);
				settings.save(0, false);
			}
			gauge.setValue(1);
			if (!firstTime) {
				for (int ic = 1; ic < settings.MAX_REGIONS; ic++) {
					saveBookmarks(storeDate, ic, releaseMemory, rssFeeds,
							bookmarkList, loadForm, settings
							//#ifdef DLOGGING
							,logger
							,fineLoggable
							,finestLoggable
							//#endif
							);
					settings.save(ic, false);
					gauge.setValue(ic + 1);
				}
				// Set internal region back to 0.
				settings.setStringProperty(settings.BOOKMARKS_NAME, "");
				settings.save(0, false);
			}
			pl = -1;
			gauge.setValue(gauge.getMaxValue());
		} catch(CauseRecStoreException e) {
			if ((e.getFirstCause() != null) &&
				!(e.getFirstCause() instanceof RecordStoreFullException)) {
				/* Error saving feeds to database.  Database error. */
				loadForm.recordExcForm(
						"Error saving feeds to database.  Database error. ", e);
			} else {
				/* Error saving feeds to database.  Database full. */
				loadForm.recordExcForm("Error saving feeds to database.  Database full. ", e);
			}
		} catch(Exception e) {
			loadForm.recordExcForm("Internal error saving feeds.", e);
		} catch(Throwable t) {
			loadForm.recordExcForm("Internal error saving feeds.", t);
		} finally {
			if (pl >= 0) {
				loadForm.delete(pl);
			}
			loadForm.showMeNotes(FeatureMgr.getMainDisp());
		}
	}
	//#endif

	/** Remove the ref to this displayable so that the memory can be freed. */
	final public void replaceRef(final Displayable disp,
			final Displayable newDisp) {
		m_loadForm.replaceRef(disp, newDisp);
	}

	//#ifdef DFULLVERS
	//#ifdef DMIDP20
	public void changed(Observable observable, Object arg) {

		RssFeedParser cbackGrRssParser = checkRssActive(observable);
		if (cbackGrRssParser == null) {
			return;
		}
		try {
			if (!cbackGrRssParser.getObservableHandler().isCanceled()) {
				RssItunesFeed feed = cbackGrRssParser.getRssFeed();
				m_rssFeeds.put(feed.getName(), feed);
				procBackHeader(cbackGrRssParser, observable, m_loadForm);
			}
		} finally {
			stopRssBackground((Observable)cbackGrRssParser);
		}
	}
	//#endif

	public void procPageExc(RssItunesFeed feed, boolean copenPage, Throwable e) {
		if (e instanceof Exception) {
			/* Error loading/parsing  feed on:\n \1 */
			m_loadForm.recordExcFormFin(
					(copenPage ? "Error loading " : "Error parsing ") +
					feed.getUrl(), e);

		} else if (e instanceof OutOfMemoryError) {
			System.gc();
			if (feed != null) {
				//#ifdef DLOGGING
				logger.severe("Out of memory for feed setting to 0 items:" + feed.getName());
				//#endif
				feed.setItems(new RssItem[0]);
			}
			m_loadForm.recordExcFormFin(
					/* Out of memory loading/parsing feed on:\n */
					(copenPage ? "Out of memory loading " : "Out of memory parsing ") +
					feed.getUrl(), e);
		} else {
			m_loadForm.recordExcForm(
					/* Internal error loading/parsing feed on:\n */
					(copenPage ? "Out of memory loading " : "Out of memory parsing ") +
					feed.getUrl(), e);
		}
	}

    /** Respond to commands */
    public void commandAction(Command c, Displayable s) {
		int ctype = c.getCommandType();
		//#ifdef DLOGGING
		//#ifdef DMIDP20
		if (finestLoggable) {logger.finest("command,ctype,displayable=" + c.getLabel() + "," + ctype + "," + s.getTitle());}
		//#else
		if (finestLoggable) {logger.finest("command,ctype,displayable=" + c.getLabel() + "," + ctype);}
		//#endif
		//#endif
        /** Add new RSS feed bookmark */
        if( c == m_addNewBookmark ){
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getAddBMForm = true;
        }
        
        /** Exit from MIDlet and save bookmarks */
        if( c == FeatureMgr.m_exitCommand ){
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
			m_curBookmark = FeatureMgr.getSelectedIndex(m_bookmarkList);
			if( m_curBookmark >= 0 ){
				boolean copenPage = false;
				boolean cgetPage = false;
				boolean cgetModPage = false;
				String parm = m_bookmarkList.getString(
						m_curBookmark);
				RssItunesFeed feed = null;
				try {
					feed = (RssItunesFeed)m_rssFeeds.get(parm);
					if (feed == null) {
						return;
					}
					copenPage = ( feed.getItems().length > 0 );
				} catch (Throwable e) {
					procPageExc(feed, copenPage, e);
					return;
				}
				cgetPage = !copenPage;
				cgetModPage = false;

				setPageInfo(copenPage, cgetPage, cgetModPage, m_bookmarkList);
				// Open existing bookmark and show headers (items).
				if (copenPage) {
					procPage(feed);
				} else {
					//#ifdef DMIDP20
					procBackPage(feed, false, false, this, true, null,
							m_loadForm);
					//#else
					try {
						RssFeedParser parser = new RssFeedParser( feed, null,
								false );
						/* Updating feed... */
						initializeLoadingForm(
								"Updating feed..." , m_bookmarkList);
						setPageInfo(false, false, false, m_bookmarkList);
						final int maxItemCount =
							m_appSettings.getMaximumItemCountInFeed();
						parser.parseRssFeed( false, maxItemCount);
						procUpdHeader(parser, m_loadForm);
					}catch(Throwable e) {
						procPageExc(feed, false, e);
					} finally {
						synchronized(this) {
							m_getPage = false;
							m_openPage = false;
							m_getModPage = false;
						}
					}
					//#endif
				}
			}
		}
        
        /** Read unread items date sorted */
        if(( c == m_readUnreadItems ) && (m_bookmarkList.size() > 0)) {
			m_runNews = true;
        }
        
        /** Open RSS feed's selected topic */
        /** Get back to RSS feed headers */
        if ((ctype == Command.BACK) && (m_itemRtnList != null) &&
            (s instanceof Form) &&
            (((Form)s) == m_itemForm)) {
			FeatureMgr.setCurrentAlt(m_itemRtnList, null, null, m_itemRtnList);
			m_itemRtnList  = (Displayable)nullPtr;
		}

		//#ifdef DTESTUI
		if( (s instanceof Form) &&
			(((Form)s) == ((Form)m_itemForm)) && (ctype == Command.BACK) ){
			if (m_headerIndex >= 0) {
				m_headerNext = true;
			} else if (m_allNewsTestList != null) {
				m_allNewsTestList.gotoNews();
			}
		}
		//#endif
        
        /** Update all RSS feeds */
        if( c == m_updateAllCmd ) {
			m_refreshAllFeeds = true;
        }
        
        /** Update all modified RSS feeds */
        if( c == m_updateAllModCmd ) {
			m_refreshUpdFeeds = true;
        }
        
        /** Show import feed list form */
        if( c == m_importFeedListCmd ) {
			// Set current bookmark so that the added feeds go after
			// the current boolmark.
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getImportForm = true;
        }
        
		//#ifdef DSIGNED
		//#ifdef DJSR75
        /** Show export feed list form */
        if( c == m_exportFeedListCmd ) {
			// Set current bookmark so that the added feeds go after
			// the current boolmark.
			m_curBookmark = m_bookmarkList.getSelectedIndex();
			m_getExportForm = true;
        }
		//#endif
		//#endif
        
		//#ifdef DTEST
		/** Show import feed list form and default file */
		if( c == m_importCurrFeedListCmd ) {
			m_curBookmark = FeatureMgr.getSelectedIndex(m_bookmarkList);
			if( m_curBookmark >= 0 ) {
				//#ifdef DTESTUI
				m_bookmarkLastIndex = m_curBookmark;
				System.out.println("TESTUI Import Current:  " + m_bookmarkList.getString(m_curBookmark));
				//#endif
				m_getTestImportForm = true;
				m_getImportForm = true;
			}
        }
		//#endif

        /** Update all modified RSS feeds */
		//#ifdef DTEST
        if( c == m_reloadDbCmd ) {
			m_appSettings.deleteSettings();
			m_appSettings = null;
			m_settings = null;
			Object[] arrsettings = FeatureMgr.initSettingsEnabled(m_loadForm
					//#ifdef DLOGGING
					,logger
					,fineLoggable
					//#endif
					);
			m_appSettings = (RssReaderSettings)arrsettings[0];
			m_settings = (Settings)arrsettings[1];
			m_firstTime = ((Boolean)arrsettings[2]).booleanValue();
			m_itunesEnabled = ((Boolean)arrsettings[3]).booleanValue();
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
        if(( c == m_testRtnCmd ) && (m_bookmarkLastIndex != 1)) {
			if (m_bookmarkList.getSelectedIndex() >= 0) {
				m_bookmarkList.setSelectedIndex(
						m_bookmarkList.getSelectedIndex(), false);
			}
			m_bookmarkList.setSelectedIndex( m_bookmarkLastIndex, true );
		}
		//#endif

        /** Settings form */
        if( c == m_settingsCmd ) {
			m_getSettingsForm = true;
        }
        
        /** Show about */
		if( c == FeatureMgr.m_aboutCmd ) {
			m_about = true;
		}

		//#ifdef DLOGGING
		//#ifdef DFULLVERS
        /** Show about */
		if( c == m_debugCmd ) {
			setCurrent( null, m_debug );
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

		// End DLOGGING
		//#endif

    }
    
	//#ifdef DMIDP20
	public void stopRssBackground(Observable cbackGrParser) {
		synchronized(this) {
			if (m_parseBackground && (cbackGrParser == m_backGrParser)) {
				m_parseBackground = false;
				setPageInfo(false, false, false, m_prevDisp);
				m_backGrParser.getObservableHandler().deleteObserver(this);
				m_loadForm.removeCommandPrompt(FeatureMgr.m_backCommand);
			}
		}
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("m_parseBackground,cbackGrParser,m_backGrParser=" + m_parseBackground + "," + cbackGrParser + "," + m_backGrParser);}
		//#endif
	}
	//#endif
	// End DFULLVERS
	//#endif

	//#ifdef DTESTUI
    public void setBookmarkIndex(int bookmarkIndex) {
        this.m_bookmarkIndex = bookmarkIndex;
    }

    public int getBookmarkIndex() {
        return (m_bookmarkIndex);
    }
	//#endif

	//#ifdef DMIDP20
	public void procPlatform(String platformURL, FeatureForm cfm,
							 Displayable rtn) {
		FeatureMgr featureMgr = cfm.getFeatureMgr();
		LoadingForm loadForm = featureMgr.getLoadForm();
		try {
			loadForm = LoadingForm.getLoadingForm(
					"Loading web page...", cfm, null);
			featureMgr.setLoadForm(loadForm);
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("procPlatform platformURL=" + platformURL);}
			//#endif
			if( super.platformRequest(platformURL) ) {
				loadForm.appendMsg("Exiting saving data...");
				synchronized(this) {
					m_exit = true;
				}
				exitApp(m_exit, loadForm);
			} else {
				setCurrent( null, rtn );
			}
		} catch (ConnectionNotFoundException e) {
			/* Show exception and set form. */
			loadForm.recordExcFormFin(
					"Could not connect to bad link:" + platformURL,
					e);
		}
	}
	//#endif

	//#ifdef DSMARTPHONE
	/* Form to look at item. */
	final public class DetailForm extends FeatureForm
		implements CommandListener {
		private boolean     m_platformReq; // Flag to get platform req open link
		private String m_platformURL;         // Platform request URL
		private Displayable        m_rtn;

		private DetailForm (final RssItunesFeed feed, Displayable rtn,
							LoadingForm loadForm) {
			super(feed.getName(), loadForm);
			this.m_rtn = rtn;
			m_platformReq = false;
			super.addCommand( FeatureMgr.m_backCommand );
			//#ifdef DMIDP20
			m_platformURL = feed.getLink();
			if (m_platformURL.length() > 0) {
				super.addCommand( m_openLinkCmd );
			}
			//#endif
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
			if (m_platformURL.length() > 0) {
				//#ifdef DMIDP20
				StringItem slink = new StringItem("Link:", m_platformURL,
												  Item.HYPERLINK);
				//#else
				StringItem slink = new StringItem("Link:", m_platformURL);
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
			if( c == FeatureMgr.m_backCommand ){
				setCurrent( null, m_rtn );
			}
			//#ifdef DMIDP20
			/** Go to link and get back to RSS feed headers */
			if( c == m_openLinkCmd ){
				synchronized(this) {
					m_platformReq = true;
				}
			}
			//#endif

			execute();

		}
			
		public void execute() {

			//#ifdef DMIDP20
			/* Handle going to link (platform request.). */
			if ( m_platformReq ) {
				try {
					procPlatform(m_platformURL, this, m_rtn);
				} finally {
					m_platformReq = false;
				}
			}
			//#endif

		}

	}
	//#endif

	//#ifdef DFULLVERS
	/* Form to look at item. */
	final private class ItemForm extends FeatureForm
		implements CommandListener {
		private boolean     m_platformReq;    // Flag to get platform req open link
		private String m_platformURL;         // Platform request URL
		//#ifdef DMIDP20
		private Command     m_openPicLinkCmd; // The open minimized link command
		private Command     m_openMobLinkCmd; // The open minimized link command
		private Command     m_openEnclosureCmd; // The open enclosure command
		//#endif
		private Command     m_nextItemCmd;      // The next item
		private Command     m_copyEnclosureCmd; // The copy enclosure command
		private Command     m_copyLinkCmd;    // The copy link command

		private ItemForm(final String title, final String actTitle,
								final RssItunesFeed feed,
								   final RssItem item,
								   LoadingForm loadForm) {
			super(title, loadForm);
			m_platformReq = false;
			//#ifdef DMIDP20
			int mc = m_appSettings.getMobilizerChoice();
			boolean hasMobilizer = (mc > 0);
			//#endif
			super.addCommand( FeatureMgr.m_backCommand );
			final String sienclosure = item.getEnclosure();
			final String desc = item.getDescription();
			if ((actTitle.length()>0) && (desc.length()>0)) {
				super.append(new StringItem(actTitle + "\n", desc));
			} else if (actTitle.length()>0) {
				super.append(new StringItem("Title\n", actTitle));
			} else {
				super.append(new StringItem("Description\n", desc));
			}
			m_citem = item;
			if (m_itunesEnabled && (item instanceof RssItunesItem) &&
				(((RssItunesItem)item).isItunes())) {
				RssItunesItem ititem = (RssItunesItem)item;
				final String author = ititem.getAuthor();
				if (author.length() > 0) {
					super.append(new StringItem("Author:", author));
				}
				final String subtitle = ititem.getSubtitle();
				if (subtitle.length() > 0) {
					super.append(new StringItem("Subtitle:", subtitle));
				}
				final String summary = ititem.getSummary();
				if (summary.length() > 0) {
					super.append(new StringItem("Summary:", summary));
				}
				final String duration = ititem.getDuration();
				if (duration.length() > 0) {
					super.append(new StringItem("Duration:", duration));
				}
				String expLabel = "Explicit:";
				String explicit = ititem.getExplicit();
				if (explicit.equals(RssItunesItem.UNSPECIFIED)) {
					expLabel = "Feed explicit:";
					explicit = feed.getExplicit();
				}
				super.append(new StringItem(expLabel, explicit));
			}
			String linkLabel = "Link:";
			String link = item.getLink();
			//#ifdef DSMARTPHONE
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
				m_citemLnkNbr  = super.append(slink);
			} else {
				m_citemLnkNbr  = -1;
			}
			if (sienclosure.length() > 0) {
				//#ifdef DMIDP20
				StringItem senclosure = new StringItem("Enclosure:", sienclosure,
						Item.HYPERLINK);
				//#else
				StringItem senclosure = new StringItem("Enclosure:", sienclosure);
				//#endif
				m_citemEnclNbr = super.append(senclosure);
			} else {
				m_citemEnclNbr  = -1;
			}
			//#ifdef DMIDP20
			if (hasMobilizer && (link.length() > 0)) {
				/* Open Mobilizer */
				m_openMobLinkCmd    = FeatureMgr.getCmdAdd(this,
						"Mobile Open", "Mobilizer Open", Command.SCREEN, 8);
				if (mc == m_appSettings.GOOGLE_MOBILIZER_CHOICE) {
					/* Open Mobilizer no pics.  */
					m_openPicLinkCmd = FeatureMgr.getCmdAdd(this,
							"No Pic Mobile", "No Pic Mobilizer Open", Command.SCREEN, 9);
				} else {
					m_openPicLinkCmd = null;
				}
			} else {
				m_openPicLinkCmd = null;
				m_openMobLinkCmd = null;
			}
			if (link.length() > 0) {
				super.addCommand( m_openLinkCmd );
			}
			//#endif

			// Add item's date if it is available
			String dateLabel = "Date:";
			Date itemDate = item.getDate();
			//#ifdef DSMARTPHONE
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

			m_nextItemCmd = FeatureMgr.getCmdAdd(this, "Next", "Next Item",
					Command.SCREEN, 7);
			if (link.length() > 0) {
				m_copyLinkCmd = FeatureMgr.getCmdAdd(this, "Copy link", null, Command.SCREEN, 9);
			}
			if (sienclosure.length() > 0) {
				m_copyEnclosureCmd = FeatureMgr.getCmdAdd(this, "Copy enclosure", null, Command.SCREEN, 10);
			}
			//#ifdef DMIDP20
			if (link.length() > 0) {
				super.addCommand( m_openLinkCmd );
			}
			if (sienclosure.length() > 0) {
				m_openEnclosureCmd = FeatureMgr.getCmdAdd(this, "Open enclosure", null, Command.SCREEN, 8);
			}
			//#endif
		}

		public void commandAction(Command c, Displayable s) {
			/** Get back to RSS feed headers */
			if( c == FeatureMgr.m_backCommand ){
				if ((m_itemRtnList != null) &&
					((m_itemRtnList instanceof HeaderList) ||
					(m_itemRtnList instanceof AllNewsList))) {
					((AllNewsList)m_itemRtnList).nextItem(false);
				}
				FeatureMgr.setCurrentFeature( m_itemRtnList, null,
						m_itemRtnList );
				//#ifdef DTESTUI
				FeatureMgr.getRssMidlet().updHeaderNext();
				//#endif
			}
			
			/** Copy link to clipboard.  */
			if( c == m_copyLinkCmd ){
				String link = m_citem.getLink();
				super.set(m_citemLnkNbr, new TextField("Link:", link,
						link.length(), TextField.URL));
				super.featureMgr.showMe(super.get(m_citemLnkNbr));
			}
			
			/** Copy enclosure to clipboard.  */
			if( c == m_copyEnclosureCmd ){
				final String link = m_citem.getEnclosure();
				super.set(m_citemEnclNbr, new TextField("Enclosure:",
					link, link.length(), TextField.URL));
				super.featureMgr.showMe(super.get(m_citemEnclNbr));
			}
			
			//#ifdef DMIDP20
			/** Go to link and get back to RSS feed headers */
			if( ( c == m_openLinkCmd ) || ( c == m_openMobLinkCmd ) ||
					( c == m_openPicLinkCmd )) {
				String link;
				synchronized(this) {
					link = m_citem.getLink();
				}
				if ((link != null) && (link.length() > 0)) {
					if( (c == m_openMobLinkCmd) || (c == m_openPicLinkCmd) ) {
						String urllink = MiscUtil.urlEncode(link);
						if (m_appSettings.getMobilizerChoice() == 1) {
							link = GOOGLE_MOBILITY + urllink;
							if( c != m_openPicLinkCmd ) {
								link += GOOGLE_NO_IMAGES;
							}
						} else {
							link = SKWEEZER_MOBILITY + urllink;
						}
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("commandAction mobility URL link,urllink=" + link + "," + urllink);}
						//#endif
					}

					synchronized(this) {
						m_platformURL = link;
						m_platformReq = true;
					}
				}
			}
			//#endif

			//#ifdef DMIDP20
			/** Go to link and get back to RSS feed headers */
			if( c == m_openEnclosureCmd ){
				synchronized(this) {
					m_platformURL = m_citem.getEnclosure();
					m_platformReq = true;
				}
			}
			//#endif
			
			if ((c == m_nextItemCmd) && (m_itemRtnList != null) &&
					((m_itemRtnList instanceof HeaderList) ||
					(m_itemRtnList instanceof AllNewsList))) {
				((AllNewsList)m_itemRtnList).nextItem(true);
			}

			execute();

		}

		public void execute() {

			/* Handle going to link (platform request.). */
			//#ifdef DMIDP20
			if ( m_platformReq ) {
				try {
					procPlatform(m_platformURL, this, m_itemRtnList);
				} finally {
					m_platformReq = false;
				}
			}
		//#endif

		}

	}
	//#endif

}
