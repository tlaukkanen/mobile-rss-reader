/*
 * ImportFeedsForm.java
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

import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.businesslogic.HTMLAutoLinkParser;
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
//#ifdef DJSR75
//@import org.kablog.kgui.KFileSelectorMgr;
//#endif

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi Laukkanen
 */
/* Form to import feeds. */
final public class ImportFeedsForm extends Form
	implements CommandListener, Runnable {

    static private byte[] m_importSave = null; // Import form save
	private boolean     m_getFeedList = false;      // The noticy flag for list parsing
	private boolean     m_getFeedTitleList = false; // The noticy flag for title/list parsing
	// The noticy flag for override existing feeds
	private boolean     m_override = false;  // The noticy flag for override
	private boolean     m_needWakeup = false;   // Flag to show need to wakeup
	private boolean     m_process = true;   // Flag to continue looping
	private int         m_addBkmrk; // Place to add (insert) imported bookmarks
	private static boolean m_notesShown;    // Flag to show notes
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
	//#ifdef DTESTUI
//@	private Command     m_testImportCmd;      // Tet UI rss opml command
	//#endif
    private RssReaderMIDlet m_midlet;       // The application midlet
    private Hashtable m_rssFeeds;         // The bookmark URLs
    private RssReaderSettings m_appSettings;// The application settings
    private PromptList  m_bookmarkList;     // The bookmark list
    private RssReaderMIDlet.LoadingForm m_loadForm; // The application settings
	//#ifdef DLOGGING
//@    private Logger m_logger = Logger.getLogger("ImportFeedsForm");
//@    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
//@    private boolean m_finerLoggable = m_logger.isLoggable(Level.FINER);
//@    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif

	/* Constructor */
    /** Initialize import form */
	public ImportFeedsForm(RssReaderMIDlet midlet,
			PromptList bookmarkList,
			Hashtable rssFeeds,
			RssReaderSettings appSettings,
			RssReaderMIDlet.LoadingForm loadForm, String url) {
		super("Import feeds");
		m_midlet = midlet;
		m_bookmarkList = bookmarkList;
		m_rssFeeds = rssFeeds;
		m_appSettings = appSettings;
		m_loadForm = loadForm;
		m_getFeedList = false;
		m_getFeedTitleList = false;
		m_listParser = null;
		m_notesShown = false;
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
		super.append(m_importHTMLGroup);
		m_importOvrGroup  = new ChoiceGroup(
				"Override existing feeds in place (optionl)",
				ChoiceGroup.EXCLUSIVE,
				new String[] {"Don't override existing feeds.",
				 "Override (replace) existing feeds."},
				null);
		super.append(m_importOvrGroup);
		if (m_importSave != null) { 
			Item[] items = {m_importFormatGroup, m_feedNameFilter,
				m_feedURLFilter, m_feedListUsername, m_feedListPassword,
				m_importFormatGroup, m_importTitleGroup, m_importHTMLGroup}; 
			m_midlet.restorePrevValues(items, m_importSave);
		}
		//#ifdef DMIDP20
		m_importInsCmd      = new Command("Insert import",
				"Insert current import", Command.SCREEN, 1);
		m_importAddCmd      = new Command("Add import",
				"Add current import", Command.SCREEN, 2);
		m_importAppndCmd    = new Command("Append import",
				"Append end import", Command.SCREEN, 3);
		//#else
//@		m_importInsCmd      = new Command("Insert import", Command.SCREEN, 1);
//@		m_importAddCmd      = new Command("Add import", Command.SCREEN, 2);
//@		m_importAppndCmd    = new Command("Append import", Command.SCREEN, 3);
		//#endif
		m_importCancelCmd   = new Command("Cancel", Command.CANCEL, 4);
		m_importFileCmd     = new Command("Find files", Command.SCREEN, 5);
		m_pasteImportURLCmd = new Command("Allow paste", Command.SCREEN, 6);
		
		super.addCommand( m_importInsCmd );
		super.addCommand( m_importAddCmd );
		super.addCommand( m_importAppndCmd );
		super.addCommand( m_importCancelCmd );
		//#ifdef DJSR75
//@		super.addCommand( m_importFileCmd );
		//#endif
		if (m_appSettings.getUseTextBox()) {
			super.addCommand(m_pasteImportURLCmd);
		}
		//#ifdef DTESTUI
//@		m_testImportCmd     = new Command("Test bookmarks imported", Command.SCREEN, 9);
//@		super.addCommand( m_testImportCmd );
		//#endif
		super.setCommandListener(this);

		m_process = true;
		//#ifdef DCLDCV11
//@		new Thread(this, "ImportFeedsForm").start();
		//#else
		new Thread(this).start();
		//#endif
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
					m_getFeedList = false;
					m_midlet.initializeLoadingForm(
							"Loading feeds from import...",
							this);
					final String url = m_feedListURL.getString().trim();
					try {
						
						// 2. Import feeds
						int selectedImportType = m_importFormatGroup.getSelectedIndex();
						RssItunesFeed[] feeds = null;
						String feedNameFilter = m_feedNameFilter.getString();
						String feedURLFilter = m_feedURLFilter.getString();
						String username = m_feedListUsername.getString();
						String password = m_feedListPassword.getString();
						m_getFeedTitleList = m_importTitleGroup.isSelected(1);
						m_override = m_importOvrGroup.isSelected(1);
						//#ifdef DLOGGING
//@						if (m_finestLoggable) {m_logger.finest("m_getFeedTitleList=" + m_getFeedTitleList);}
//@						if (m_finestLoggable) {m_logger.finest("selectedImportType=" + selectedImportType);}
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
//@						if (m_fineLoggable) {m_logger.fine("redirect html=" + m_listParser.isRedirectHtml());}
						//#endif
						
						// Start parsing
						m_listParser.startParsing();
						
						// 3. Show result screen
						// 4. Show list of feeds
						
					} catch(Exception ex) {
						m_listParser = null;
						m_midlet.recordExcForm("Error importing feeds from " + url, ex);
					} catch(OutOfMemoryError ex) {
						m_listParser = null;
						m_midlet.recordExcForm("Out Of Memory Error importing feeds from " +
								url, ex);
					} catch(Throwable t) {
						m_listParser = null;
						m_midlet.recordExcForm("Out Of Memory Error importing feeds from " +
								url, t);
					}
				}
				if(m_listParser != null) {
					try {
						if(m_listParser.isReady()) {
							addFeedLists(m_listParser, m_getFeedTitleList,
									m_addBkmrk,
									m_appSettings.getMaximumItemCountInFeed(),
									 m_override, m_rssFeeds,
									 m_bookmarkList, m_loadForm);
							if (m_notesShown || m_loadForm.hasExc()) {
								m_loadForm.setTitle(
										"One or more warnings, " +
										"exceptions or errors.");
								m_midlet.setCurrent( m_loadForm );
							} else {
								m_process = false;
								m_loadForm.removeRef(this);
								Item[] items = {m_importFormatGroup,
									m_feedNameFilter,
									m_feedURLFilter, m_feedListUsername,
									m_feedListPassword,
									m_importFormatGroup, m_importTitleGroup, m_importHTMLGroup};
								m_importSave = m_midlet.storeValues(items);
								m_midlet.setCurrent( m_bookmarkList );
							}
							m_listParser = null;
						} else {
							//#ifndef DTESTUI
							if (m_debugOutput) System.out.println("Feed list parsing isn't ready");
							//#endif
						}
					} catch(Exception ex) {
						m_midlet.recordExcForm(
								"Error importing feeds from " +
								m_listParser.getUrl() + " " +
								ex.getMessage(), ex);
						m_getFeedTitleList = false;
						m_listParser = null;
					} catch(Throwable t) {
						m_midlet.recordExcForm(
								"Error importing feeds from " +
								m_listParser.getUrl() + " " +
								t.getMessage(), t);
						m_getFeedTitleList = false;
						m_listParser = null;
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
			}
		}
	}

	/** Respond to commands */
	public void commandAction(final Command c, final Displayable s) {
		//#ifdef DTESTUI
//@		super.outputCmdAct(c, s);
		//#endif

		/** Import list of feeds if one of the insert, add, or append commands
		    is used.  */
		m_addBkmrk = UiUtil.getPlaceIndex(c, m_importInsCmd,
				m_importAddCmd, m_importAppndCmd, m_bookmarkList);
		if( m_addBkmrk >= 0 ) {
			m_getFeedList = true;
		}
		
		//#ifdef DJSR75
//@		/** Find import file in file system */
//@		if( c == m_importFileCmd ) {
//@			if (!RssReaderMIDlet.JSR75_ENABLED) {
//@				Alert invalidAlert = new Alert(
//@						"JSR-75 not enabled", 
//@						"Find files (JSR-75) not enabled on the phone.",
//@						null,
//@						AlertType.WARNING);
//@				invalidAlert.setTimeout(Alert.FOREVER);
//@				m_midlet.setCurrent( invalidAlert, this );
//@				m_midlet.wakeUp();
//@				return;
//@			}
//@			try {
//@				m_midlet.reqFindFiles( this, m_feedListURL );
//@				m_midlet.wakeUp();
//@			}catch(Throwable t) {
				//#ifdef DLOGGING
//@				m_logger.severe("RssReaderMIDlet find files ", t);
				//#endif
//@				/** Error while executing find files */
//@				System.out.println("RssReaderMIDlet find files " + t.getMessage());
//@				t.printStackTrace();
//@			}
//@		}
		//#endif
				
		/** Cancel importing -> Show list of feeds */
		if( c == m_importCancelCmd ) {
			m_loadForm.removeRef(this);
			m_process = false;
			m_midlet.setCurrent( m_bookmarkList );
		}
		
		/** Put current import URL into URL box.  */
		if( c == m_pasteImportURLCmd ) {
			new UiUtil().initializeURLBox(m_midlet, m_feedListURL.getString(),
					this, m_feedListURL);
		}

		//#ifdef DTESTUI
//@		/** Import list of feeds and auto edit bookmarks/feeds */
//@		if( c == m_testImportCmd ) {
//@			m_midlet.setBookmarkIndex(m_bookmarkList.size());
//@			System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" +
//@					m_midlet.getBookmarkIndex());
//@			commandAction(m_importAppndCmd, this);
//@		}
		//#endif
		importWakeUp();

	}

	/** Add from feed list (from import). */
	final public static void addFeedLists(FeedListParser listParser,
			boolean getFeedTitleList, int addBkmrk,
			int maxItemCount, boolean override, Hashtable rssFeeds,
			List bookmarkList, RssReaderMIDlet.LoadingForm loadForm)
	throws CauseException, Exception {
		// Feed list parsing is ready
		System.out.println("Feed list parsing is ready");
		if(!listParser.isSuccessfull()) {
			throw listParser.getEx();
		}
		RssItunesFeed[] feeds = listParser.getFeeds();
		for(int feedIndex=0; feedIndex<feeds.length; feedIndex++) {
			String name = feeds[feedIndex].getName();
			//#ifdef DTEST
//@			System.out.println("Adding: " + name);
			//#endif
			// If no title (name) and we are getting the title from the
			// feed being imported, parse the name(title) only.
			if (((name == null) || (name.length() == 0)) &&
					getFeedTitleList) {
				RssItunesFeed feed = feeds[feedIndex];
				RssFeedParser fparser = new RssFeedParser( feed );
				loadForm.appendMsg("Loading title for " +
						"feed " + feed.getUrl());
				//#ifdef DLOGGING
//@				Logger logger = Logger.getLogger("ImportFeedsForm");
//@				logger.finest("Getting title for url=" + feed.getUrl());
				//#endif
				fparser.setGetTitleOnly(true);
				/** Get RSS feed */
				try {
					fparser.parseRssFeed( false, maxItemCount );
					name = feed.getName();
					loadForm.appendMsg("ok\n");
				} catch(Exception ex) {
					CauseException ce = new CauseException(
							"Error loading title for feed " + feed.getUrl(),
							ex);
					//#ifdef DLOGGING
//@					logger.severe(ce.getMessage(), ex);
					//#endif
					loadForm.appendMsg("Error\n");
					loadForm.addExc(ce);
				}
			}
			if((name != null) && (name.length()>0)) {
				final boolean pres = rssFeeds.containsKey( name );
				if(override || !pres) {
					if(pres) {
						loadForm.appendMsg(
								"Overriding existing feed with the one from " +
								"import feed name " + name);
						m_notesShown = true;
					}
					rssFeeds.put( name, feeds[feedIndex] );
					if(!pres) {
						bookmarkList.insert(addBkmrk++, name, null);
					}
				} else {
					CauseException ce = new CauseException("Error:  Feed " +
							"already exists with name " + name +
							".  Existing feed not updated.  " +
							"Use override in place to override an existing " +
							"feed with an old feed with the same name.");
					loadForm.appendMsg(ce.getMessage());
					loadForm.addExc(ce);
				}
			}
		}
	}

	/* Notify us that we are finished. */
	final public void importWakeUp() {
	
		synchronized(this) {
			m_needWakeup = true;
			super.notify();
		}
	}

}
