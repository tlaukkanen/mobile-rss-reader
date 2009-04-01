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
// FIX check for blank url

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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
//#ifdef DJSR75
//@import javax.microedition.io.file.FileConnection;
//#endif
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
//@import com.substanceofcode.testlcdui.TextField;
//@import com.substanceofcode.testlcdui.StringItem;
//#endif
import javax.microedition.lcdui.Item;

import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
//#ifdef DJSR75
//@import com.substanceofcode.rssreader.businesslogic.URLHandler;
//#endif
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
final public class ImportFeedsForm extends URLForm
	implements CommandListener, Runnable {

    static private byte[] m_importSave = null; // Import form save
    static private byte[] m_exportSave = null; // Export form save
	//#ifndef DTESTUI
    private boolean     m_debugOutput = false; // Flag to write to output for test
	//#endif
	private boolean     m_getFeedList = false;      // The noticy flag for list parsing
	private boolean     m_getFeedTitleList = false; // The noticy flag for title/list parsing
	// The noticy flag for override existing feeds
	private boolean     m_override = false;  // The noticy flag for override
    final private boolean m_importFeeds;
	private FeedListParser m_listParser = null;    // The feed list parser
	private TextField   m_feedNameFilter;   // The feed name filter string
	private TextField   m_feedURLFilter;    // The feed URL filter string
	private ChoiceGroup m_importFormatGroup;// The import type choice group
	private ChoiceGroup m_importTitleGroup; // The import title choice group
	private ChoiceGroup m_importHTMLGroup;  // The import HTML redirect choice group
	private ChoiceGroup m_importOvrGroup; // The import override choice group
	//#ifdef DTESTUI
//@	private Command     m_testImportCmd;      // Tet UI rss opml command
	//#endif
    private FeatureList  m_bookmarkList;     // The bookmark list
	//#ifdef DLOGGING
//@    private Logger m_logger = Logger.getLogger("ImportFeedsForm");
//@    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
//@    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif

	/* Constructor */
    /** Initialize import form */
	public ImportFeedsForm(RssReaderMIDlet midlet,
			FeatureList bookmarkList, boolean importFeeds,
			Hashtable rssFeeds,
			RssReaderSettings appSettings,
			RssReaderMIDlet.LoadingForm loadForm, String url) {
		super(midlet, (importFeeds ? "Import" : "Export") + " feeds",
				!importFeeds, rssFeeds, appSettings, loadForm);
		m_bookmarkList = bookmarkList;
		m_importFeeds = importFeeds;
		if(url.length()==0) {
			url = "http://";
		}
		String[] formats = null;

		if (m_importFeeds) {
			super.initAddUI(url, m_appSettings.getImportUrlUsername(),
					m_appSettings.getImportUrlPassword(), false, null, 1,
						"Insert import", "Insert current import",
						"Add import", "Add current import",
						"Append import", "Append end import");
			formats = new String[] {"OPML", "line by line", "HTML OPML Auto link",
								"HTML RSS Auto links", "HTML Links"};
		} else {
			super.initUrlUI(url, true,
					"Are you sure you want to export?  \r\n" +
					"This can cause endless prompts on some phones.", 1);
			formats = new String[] {"OPML", "line by line"};
		}
		m_importFormatGroup = new ChoiceGroup("Format", ChoiceGroup.EXCLUSIVE, formats, null);

		super.append(m_importFormatGroup);
		if (m_importFeeds) {
		
			m_feedNameFilter = new TextField("Name filter string (optional)", "", 256, TextField.ANY);
			super.append(m_feedNameFilter);
			m_feedURLFilter = new TextField("URL filter string (optional)", "", 256, TextField.ANY);
			super.append(m_feedURLFilter);
			
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
		
			//#ifdef DTESTUI
//@			m_testImportCmd     = new Command("Test bookmarks imported", Command.SCREEN, 9);
//@			super.addCommand( m_testImportCmd );
			//#endif
		}

	}

	/** Run method is used to get RSS feed with HttpConnection */
	public void run() {

		super.execute();

		//This (OK) happens only for export.  Import has insert/add/append
		//#ifdef DJSR75
//@		if (m_ok) {
//@			m_ok = false;
//@			final String url = m_url.getString().trim();
			//#ifdef DLOGGING
//@			if (m_finestLoggable) {m_logger.finest("Writing to url=" + url);}
			//#endif
//@			URLHandler uhandler = new URLHandler();
//@			OutputStreamWriter osw = null;
//@			try {
//@				uhandler.handleOpen(url, null, null, true, false, null, "");
//@				OutputStream os = uhandler.getOutputStream();
//@				// On many devices, writing to a file gives a propmt for
//@				// each write to the file which is very annoying, so
//@				// we put data into a StringBuffer and then to the file
//@				// all at once.
//@				StringBuffer sb = new StringBuffer();
//@				try {
//@					osw = new OutputStreamWriter(os, "UTF-8");
//@				} catch (UnsupportedEncodingException e) {
//@					osw = new OutputStreamWriter(os);
					//#ifdef DLOGGING
//@					m_logger.severe("run Unable to use UTF-8 for export", e);
					//#endif
//@				}
//@				int selectedImportType = m_importFormatGroup.getSelectedIndex();
//@				if (selectedImportType == 0) {
//@					sb.append("<opml version=\"1.0\">\n<head>\n" +
//@							"<title>Rss Reader subscriptions</title>\n" +
//@							"</head>\n<body>\n");
//@				}
//@				// Line by line is URL followed by name
//@				final int blen = m_bookmarkList.size();
//@				for (int i = 0; i < blen; i++) {
//@					final RssItunesFeed feed = (RssItunesFeed)m_rssFeeds.get(
//@							m_bookmarkList.getString(i));
//@					if (selectedImportType == 0) {
//@						sb.append("<outline title=" + feed.getName() +
//@							" text=" + feed.getName() + ">\n" +
//@						"    <outline text=\"" + feed.getName() +
//@							"\" title=" + feed.getName() + "\" type=\"rss\"\n" +
//@						"xmlUrl=\"" + feed.getUrl() + "\" htmlUrl='\"" + feed.getUrl() +
//@						"\"/>\n</outline>\n");
//@					} else {
//@						sb.append(feed.getUrl() + " " + feed.getName());
//@					}
//@				}
//@				if (selectedImportType == 0) {
//@					sb.append("<body>\n</opml>\n");
//@				}
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest("Export sb.length()=" + sb.length());}
				//#endif
//@				osw.write(sb.toString());
//@				Item[] items = getItemFields();
//@				ImportFeedsForm.m_exportSave = FeatureMgr.storeValues(items);
//@				m_midlet.showBookmarkList();
//@			} catch(IllegalArgumentException ex) {
//@				m_loadForm.recordExcForm("Invalid url:  " + url, ex);
//@			} catch(ConnectionNotFoundException ex) {
//@				m_loadForm.recordExcForm("Invalid connection or url:  " + url, ex);
//@			} catch(IOException ex) {
//@				m_loadForm.recordExcForm("Error exporting feeds to " + url, ex);
//@			} catch(SecurityException ex) {
//@				m_loadForm.recordExcForm("Security error exporting feeds to " + url, ex);
//@			} catch(Throwable t) {
//@				m_loadForm.recordExcForm("Internal error exporting feeds to " +
//@						url, t);
//@			} finally {
//@				uhandler.handleClose();
//@				if (osw != null) {
//@					try {
//@						osw.close();
//@					} catch (IOException e) {
						//#ifdef DLOGGING
//@						m_logger.severe("Can't close output file.", e);
						//#endif
//@						e.printStackTrace();
//@					}
//@				}
//@			}
//@		}
		//#endif

		// Add feeds from import.

		if( m_getFeedList ) {
			m_getFeedList = false;
			final String url = m_url.getString().trim();
			try {
				m_midlet.initializeLoadingForm(
						"Loading feeds from import...",
						this);
					
				// 2. Import feeds
				int selectedImportType = m_importFormatGroup.getSelectedIndex();
				RssItunesFeed[] feeds = null;
				String feedNameFilter = m_feedNameFilter.getString();
				String feedURLFilter = m_feedURLFilter.getString();
				String username = m_UrlUsername.getString();
				String password = m_UrlPassword.getString();
				m_getFeedTitleList = m_importTitleGroup.isSelected(1);
				m_override = m_importOvrGroup.isSelected(1);
				//#ifdef DLOGGING
//@				if (m_finestLoggable) {m_logger.finest("m_getFeedTitleList=" + m_getFeedTitleList);}
//@				if (m_finestLoggable) {m_logger.finest("selectedImportType=" + selectedImportType);}
				//#endif
				
				// Save settings
				m_appSettings.setImportUrl(url);
				m_appSettings.setImportUrlUsername(username);
				m_appSettings.setImportUrlPassword(password);
				FeedListParser clistParser = null;
				switch (selectedImportType) {
					case 1:
						// Use line by line parser
						clistParser = new LineByLineParser(url, username, password);
						break;
					case 2:
						// Use line by HMTL OPML auto link parser
						clistParser = new HTMLAutoLinkParser(url, username, password);
						((HTMLAutoLinkParser)clistParser).setNeedRss(false);
						break;
					case 3:
						// Use line by HMTL RSS auto link parser
						clistParser = new HTMLAutoLinkParser(url, username, password);
						((HTMLAutoLinkParser)clistParser).setNeedRss(true);
						break;
					case 4:
						// Use line by HMTL link parser
						clistParser = new HTMLLinkParser(url, username, password);
						break;
					case 0:
					default:
						// Use OPML parser
						clistParser = new OpmlParser(url, username, password);
						break;
				}
				clistParser.setFeedNameFilter(feedNameFilter);
				clistParser.setFeedURLFilter(feedURLFilter);
				clistParser.setRedirectHtml(m_importHTMLGroup.isSelected(0)
					&& !(clistParser instanceof HTMLAutoLinkParser)
					&& !(clistParser instanceof HTMLLinkParser));
				//#ifdef DLOGGING
//@				if (m_fineLoggable) {m_logger.fine("redirect html=" + clistParser.isRedirectHtml());}
				//#endif
				
				// Start parsing
				clistParser.startParsing();
				m_listParser = clistParser;
				
				// 3. Show result screen
				// 4. Show list of feeds
				
			} catch(Exception ex) {
				m_loadForm.recordExcForm("Error importing feeds from " + url, ex);
			} catch(OutOfMemoryError ex) {
				m_loadForm.recordExcForm("Out Of Memory Error importing feeds from " +
						url, ex);
			} catch(Throwable t) {
				m_loadForm.recordExcForm("Internal error importing feeds from " +
						url, t);
			}
		}

		if((m_listParser != null) && m_listParser.isReady()) {
			try {
				addFeedLists(m_listParser, m_getFeedTitleList,
						m_addBkmrk,
						m_appSettings.getMaximumItemCountInFeed(),
						 m_override, m_rssFeeds,
						 m_bookmarkList, m_loadForm);
				if (m_loadForm.hasNotes() || m_loadForm.hasExc()) {
					m_loadForm.setTitle(
							"One or more warnings, " +
							"exceptions or errors.");
					m_midlet.setCurrent( m_loadForm );
				} else {
					m_loadForm.replaceRef(this, null);
					Item[] items = getItemFields();
					ImportFeedsForm.m_importSave = FeatureMgr.storeValues(items);
					m_midlet.showBookmarkList();
				}
				super.getFeatureMgr().setBackground(false);
			} catch(Exception ex) {
				m_loadForm.recordExcForm(
						"Error importing feeds from " +
						m_listParser.getUrl() + " " +
						ex.getMessage(), ex);
				m_getFeedTitleList = false;
			} catch(Throwable t) {
				m_loadForm.recordExcForm(
						"Error importing feeds from " +
						m_listParser.getUrl() + " " +
						t.getMessage(), t);
				m_getFeedTitleList = false;
			} finally {
				// Free memory.
				m_listParser = null;
			}
			//#ifndef DTESTUI
		} else {
			if (m_debugOutput) System.out.println("Feed list parsing isn't ready");
			//#endif
		}
	}

	private Item[] getItemFields() {
		Item[] items = {m_importFormatGroup,
			m_feedNameFilter,
			m_feedURLFilter, m_UrlUsername,
			m_UrlPassword,
			m_importFormatGroup, m_importTitleGroup, m_importHTMLGroup};
		return items;
	}

	/** Respond to commands */
	public void commandAction(Command c, Displayable s) {

		super.commandAction(c, s);

		/** Import list of feeds if one of the insert, add, or append commands
		    is used.  */
		m_addBkmrk = FeatureMgr.getPlaceIndex(c, m_insCmd,
				m_addCmd, m_appndCmd, m_bookmarkList);
		if( m_addBkmrk >= 0 ) {
			m_getFeedList = true;
		}
		
		if (m_clear) {
			m_feedNameFilter.setString("");
			m_feedURLFilter.setString("");
		}

		if (m_last) {
			m_last = false;
			if ((ImportFeedsForm.m_importSave != null) ||
				  (ImportFeedsForm.m_exportSave != null)) {
				Item[] items = getItemFields();
				FeatureMgr.restorePrevValues(items,
						m_importFeeds ? ImportFeedsForm.m_importSave :
						 ImportFeedsForm.m_exportSave);
			}
		}

		//#ifdef DTESTUI
//@		/** Import list of feeds and auto edit bookmarks/feeds */
//@		if( c == m_testImportCmd ) {
//@			m_midlet.setBookmarkIndex(m_bookmarkList.size());
//@			System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" +
//@					m_midlet.getBookmarkIndex());
//@			commandAction(m_appndCmd, this);
//@		}
		//#endif

	}

	/** Add from feed list (from import). */
	public static void addFeedLists(FeedListParser listParser,
			boolean getFeedTitleList, int addBkmrk,
			int maxItemCount, boolean override, Hashtable rssFeeds,
			FeatureList bookmarkList, RssReaderMIDlet.LoadingForm loadForm)
	throws CauseException, Exception {
		// Feed list parsing is ready
		System.out.println("Feed list parsing is ready");
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("ImportFeedsForm");
//@		logger.finest("addFeedLists rssFeeds=" + rssFeeds);
		//#endif
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
					loadForm.addExc("Error\n", ce);
				}
			}
			if((name != null) && (name.length()>0)) {
				final boolean pres = rssFeeds.containsKey( name );
				if(override || !pres) {
					if(pres) {
						loadForm.appendNote(
								"Overriding existing feed with the one from " +
								"import feed name " + name);
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
					loadForm.addExc(ce.getMessage(), ce);
				}
			}
		}
	}

}
