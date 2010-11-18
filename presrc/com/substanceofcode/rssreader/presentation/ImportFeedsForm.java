//--Need to modify--#preprocess
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
/*
 * IB 2010-03-07 1.11.4RC1 Use observer pattern for OPML/list parsing to prevent hangs from spotty networks and bad URLs.  Prevent override message from causing hang on import feeds.
 * IB 2010-03-14 1.11.5RC1 Use htmlUrl which is link tag in feed for OPML.  This happens for Google reader.
 * IB 2010-03-14 1.11.5RC1 Code cleanup.
 * IB 2010-05-24 1.11.5RC2 Optionally use feed URL as feed name if not found in import file.
 * IB 2010-05-24 1.11.5RC2 Give warning if no feeds found in import.
 * IB 2010-05-24 1.11.5RC2 Change out.println to log instead.
 * IB 2010-05-24 1.11.5RC2 Only do export if signed.
 * IB 2010-05-27 1.11.5RC2 Modified code to write OPML file using OpmlParser.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser, HTMLLinkParser, and HTMLAutoLinkParser in small memory MIDP 1.0 to save space.
 * IB 2010-05-28 1.11.5RC2 Only do export if signed and MIDP 2.0.
 * IB 2010-05-30 1.11.5RC2 Do export only for signed, Itunes and JSR-75.
 * IB 2010-06-27 1.11.5Dev2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-06-27 1.11.5Dev2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-07-05 1.11.5Dev6 Use null pattern for nulls to initialize and save memory.
 * IB 2010-09-26 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-09-26 1.11.5Dev8 Repalce setCurrent with showMe.
 * IB 2010-09-26 1.11.5Dev8 Use loadingForm from FeatureMgr.
 * IB 2010-09-26 1.11.5Dev8 Use getLoadingForm instead of initializeLoadingForm.
 * IB 2010-09-26 1.11.5Dev8 Allow non-smartphone to export the OPML if signed.
 * IB 2010-09-26 1.11.5Dev8 Have loading form display Write OPML or line by line.
 * IB 2010-09-28 1.11.5Dev9 Combine statements with getItemFields into one statement.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 More logging.
 * IB 2010-11-16 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
*/
// FIX check for blank url

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define signed define
@DSIGNEDDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define test ui define
@DTESTUIDEF@

package com.substanceofcode.rssreader.presentation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.microedition.io.ConnectionNotFoundException;
//#ifdef DJSR75
import javax.microedition.io.file.FileConnection;
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
//#else
// If using the test UI define the Test UI's
import com.substanceofcode.testlcdui.ChoiceGroup;
import com.substanceofcode.testlcdui.Form;
import com.substanceofcode.testlcdui.TextField;
//#endif
import javax.microedition.lcdui.Item;

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.LoadingForm;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
//#ifdef DJSR75
import com.substanceofcode.rssreader.businesslogic.URLHandler;
//#endif
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
//#ifndef DSMALLMEM
import com.substanceofcode.rssreader.businesslogic.HTMLAutoLinkParser;
import com.substanceofcode.rssreader.businesslogic.HTMLLinkParser;
//#endif
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observer;
import net.yinlight.j2me.observable.Observable;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi Laukkanen
 */
/* Form to import feeds. */
final public class ImportFeedsForm extends URLForm
implements 
//#ifdef DMIDP20
			Observer,
//#endif
	CommandListener,
	Runnable {

	final       Object nullPtr = null;
    static private byte[] m_importSave = null; // Import form save
    static private byte[] m_exportSave = null; // Export form save
	private boolean     m_getFeedList = false;      // The noticy flag for list parsing
	// The noticy flag for override existing feeds
	private boolean     m_override = false;  // The noticy flag for override
	//#ifdef DMIDP20
    private boolean     m_parseBackground = false;
    private Observable  m_backGrListParser = null; // The currently selected RSS in background
	//#endif
    final private boolean m_importFeeds;
	private TextField   m_feedNameFilter;   // The feed name filter string
	private TextField   m_feedURLFilter;    // The feed URL filter string
	private ChoiceGroup m_importFormatGroup;// The import type choice group
	private ChoiceGroup m_importTitleGroup; // The import title choice group
	//#ifndef DSMALLMEM
	private ChoiceGroup m_importHTMLGroup;  // The import HTML redirect choice group
	//#endif
	private ChoiceGroup m_importOvrGroup; // The import override choice group
	//#ifdef DTESTUI
	private Command     m_testImportCmd;      // Tet UI rss opml command
	//#endif
    private FeatureList  m_bookmarkList;     // The bookmark list
	//#ifdef DLOGGING
    private Logger m_logger = Logger.getLogger("ImportFeedsForm");
    private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
    private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif

	/* Constructor */
    /** Initialize import form */
	public ImportFeedsForm(
			FeatureList bookmarkList, boolean importFeeds,
			Hashtable rssFeeds,
			RssReaderSettings appSettings,
			LoadingForm loadForm, String url) {
		super((importFeeds ? "Import" : "Export") + " feeds",
				!importFeeds, rssFeeds, appSettings, loadForm);
		m_bookmarkList = bookmarkList;
		m_importFeeds = importFeeds;
		if(url.length()==0) {
			url = "http://";
		}
		String[] formats = null;

		if (m_importFeeds) {
			super.initAddUI(url, m_appSettings.getImportUrlUsername(),
					m_appSettings.getImportUrlPassword(), false, null, 7,
						"Insert import", "Insert current import",
						"Add import", "Add current import",
						"Append import", "Append end import");
			formats = new String[] {"OPML", "line by line"
								//#ifndef DSMALLMEM
								, "HTML OPML Auto link",
								"HTML RSS Auto links", "HTML Links"
								//#endif
								};
			//#ifdef DSIGNED
			//#ifdef DJSR75
		} else {
			super.initUrlUI(url, true,
					"Are you sure you want to export?  \r\n" +
					"This can cause endless prompts on some phones.", 7);
			formats = new String[] {"OPML", "line by line"};
			//#endif
			//#endif
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
					 "Get missing titles from feed",
					 "Use link for missing title"};
			m_importTitleGroup  = new ChoiceGroup("Missing title (optionl)",
					ChoiceGroup.EXCLUSIVE, titleInfo, null);
			super.append(m_importTitleGroup);
			//#ifndef DSMALLMEM
			String[] HTMLInfo =
					{"Redirect if HTML (ignored for HTML link import)",
					 "Treat HTML as import"};
			m_importHTMLGroup  =
				new ChoiceGroup("Treat HTML mime type as valid import (optional)",
					ChoiceGroup.EXCLUSIVE, HTMLInfo, null);
			super.append(m_importHTMLGroup);
			//#endif
			m_importOvrGroup  = new ChoiceGroup(
					"Override existing feeds in place (optionl)",
					ChoiceGroup.EXCLUSIVE,
					new String[] {"Don't override existing feeds.",
					 "Override (replace) existing feeds."},
					null);
			super.append(m_importOvrGroup);
		
			//#ifdef DTESTUI
			m_testImportCmd     = new Command("Test bookmarks imported", Command.SCREEN, 90);
			super.addCommand( m_testImportCmd );
			//#endif
		}

	}

	//#ifdef DMIDP20
  /**
   * If the observer (list parser) has changed, add the feeds (or give error
   * message).  If observable is null, remove the observer as this means that
   * we are not going to wait for the feeds as it is probably hung.
   *
   * @param observable
   * @author Irv Bunton
   */
	public void changed(Observable observable, Object arg) {

		FeedListParser cbackGrListParser = null;
		synchronized(this) {
			cbackGrListParser = (FeedListParser)observable.getObservableHandler(
						).checkActive(m_parseBackground,
						m_backGrListParser, observable);
		}
		if (cbackGrListParser == null) {
			return;
		}

		if (!cbackGrListParser.getObservableHandler().isCanceled()) {
			addFeedLists(cbackGrListParser);
		}
	}
	//#endif

	public void addFeedLists(FeedListParser cfeedListParser) {

		LoadingForm loadForm = featureMgr.getLoadForm();
		try {
			addFeedLists(cfeedListParser,
					m_addBkmrk,
					m_appSettings.getMaximumItemCountInFeed(),
					 m_override, m_rssFeeds,
					 m_bookmarkList, loadForm);
			if (loadForm.hasNotes() || loadForm.hasExc()) {
				loadForm.recordFin();
				loadForm.getFeatureMgr().showMe();
			} else {
				loadForm.replaceRef(this, null);
				ImportFeedsForm.m_importSave = FeatureMgr.storeValues(
						getItemFields());
				featureMgr.getMidlet().showBookmarkList();
				featureMgr.setBackground(false);
			}
		} catch(Exception ex) {
			loadForm.recordExcFormFin(
					"Error importing feeds from " +
					cfeedListParser.getUrl(), ex);
		} catch(OutOfMemoryError e) {
			loadForm.recordExcForm(e.getMessage(), e);
			loadForm.recordFin();
		} catch(Throwable t) {
			loadForm.recordExcFormFin(
					"Internal error importing feeds from " +
					cfeedListParser.getUrl(), t);
		} finally {
			//#ifdef DMIDP20
			if (cfeedListParser.getObservableHandler() != null) {
				cfeedListParser.getObservableHandler().deleteObserver(this);
			}
			//#endif
			loadForm.removeCommandPrompt(RssReaderMIDlet.m_backCommand);
			// Free memory.
			//#ifdef DMIDP20
			synchronized(this) {
				m_backGrListParser = (Observable)nullPtr;
				m_parseBackground = false;
			}
			//#endif
		}
	}

	/** Run method is used to get RSS feed with HttpConnection */
	public void run() {

		super.execute();

		//This (OK) happens only for export.  Import has insert/add/append
		//#ifdef DSIGNED
		//#ifdef DJSR75
		if (m_ok) {
			m_ok = false;
			int selectedImportType = m_importFormatGroup.getSelectedIndex();
			LoadingForm loadForm = LoadingForm.getLoadingForm(
					((selectedImportType == 0) ? "Writing OPML." : "Writing line by line."),
					this, null);
			featureMgr.setLoadForm(loadForm);
			final String url = m_url.getString().trim();
			//#ifdef DLOGGING
			if (m_finestLoggable) {m_logger.finest("Writing to url=" + url);}
			//#endif
			URLHandler uhandler = new URLHandler();
			OutputStreamWriter osw = null;
			try {
				uhandler.handleOpen(url, null, null, true, false, null, "");
				OutputStream os = uhandler.getOutputStream();
				// On many devices, writing to a file gives a propmt for
				// each write to the file which is very annoying, so
				// we put data into a StringBuffer and then to the file
				// all at once.
				StringBuffer sb = new StringBuffer();
				try {
					osw = new OutputStreamWriter(os, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					osw = new OutputStreamWriter(os);
					//#ifdef DLOGGING
					m_logger.severe("run Unable to use UTF-8 for export", e);
					//#endif
				}
				if (selectedImportType == 0) {
					sb.append(OpmlParser.getOpmlBegin());
				}
				// Line by line is URL followed by name
				final int blen = m_bookmarkList.size();
				//#ifdef DLOGGING
				if (m_finestLoggable) {m_logger.finest("run Writing blen=" + blen);}
				//#endif
				for (int i = 0; i < blen; i++) {
					final RssItunesFeed feed = (RssItunesFeed)m_rssFeeds.get(
							m_bookmarkList.getString(i));
					if (selectedImportType == 0) {
						sb.append(OpmlParser.getOpmlLine(feed));
					} else {
						sb.append(feed.getUrl() + " " + feed.getName());
					}
				}
				if (selectedImportType == 0) {
					sb.append(OpmlParser.getOpmlEnd());
				}
				//#ifdef DLOGGING
				if (m_finestLoggable) {m_logger.finest("Export sb.length()=" + sb.length());}
				if (m_finestLoggable) {m_logger.finest("Export sb=" + sb.toString());}
				//#endif
				osw.write(sb.toString());
				ImportFeedsForm.m_exportSave = FeatureMgr.storeValues(
						getItemFields());
				featureMgr.getMidlet().showBookmarkList();
				loadForm.recordFin();
			} catch(IllegalArgumentException ex) {
				loadForm.recordExcForm("Invalid url:  " + url, ex);
			} catch(ConnectionNotFoundException ex) {
				loadForm.recordExcForm("Invalid connection or url:  " + url, ex);
			} catch(IOException ex) {
				loadForm.recordExcForm("Error exporting feeds to " + url, ex);
			} catch(SecurityException ex) {
				loadForm.recordExcForm("Security error exporting feeds to " + url, ex);
			} catch(Throwable t) {
				loadForm.recordExcForm("Internal error exporting feeds to " +
						url, t);
			} finally {
				uhandler.handleClose();
				if (osw != null) {
					try {
						osw.close();
					} catch (IOException e) {
						//#ifdef DLOGGING
						m_logger.severe("Can't close output file.", e);
						//#endif
						e.printStackTrace();
					}
				}
			}
		}
		//#endif
		//#endif

		// Add feeds from import.

		if( m_getFeedList ) {
			m_getFeedList = false;
			final String url = m_url.getString().trim();
			LoadingForm loadForm = LoadingForm.getLoadingForm(
						"Loading feeds from import...",
						this, null);
			try {
					
				// 2. Import feeds
				int selectedImportType = m_importFormatGroup.getSelectedIndex();
				RssItunesFeed[] feeds = null;
				String feedNameFilter = m_feedNameFilter.getString();
				String feedURLFilter = m_feedURLFilter.getString();
				String username = m_UrlUsername.getString();
				String password = m_UrlPassword.getString();
				boolean getFeedTitleList = m_importTitleGroup.isSelected(1);
				boolean useFeedUrlList = m_importTitleGroup.isSelected(2);
				m_override = m_importOvrGroup.isSelected(1);
				//#ifdef DLOGGING
				if (m_finestLoggable) {m_logger.finest("getFeedTitleList,useFeedUrlList=" + getFeedTitleList + "," + useFeedUrlList);}
				if (m_finestLoggable) {m_logger.finest("selectedImportType=" + selectedImportType);}
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
					//#ifndef DSMALLMEM
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
					//#endif
					case 0:
					default:
						// Use OPML parser
						clistParser = new OpmlParser(url, username, password);
						break;
				}
				clistParser.setGetFeedTitleList(getFeedTitleList);
				clistParser.setUseFeedUrlList(useFeedUrlList);
				clistParser.setLoadForm(loadForm);
				clistParser.setMaxItemCount(
						m_appSettings.getMaximumItemCountInFeed());
				clistParser.setFeedNameFilter(feedNameFilter);
				clistParser.setFeedURLFilter(feedURLFilter);
				//#ifndef DSMALLMEM
				clistParser.setRedirectHtml(m_importHTMLGroup.isSelected(0)
					&& !(clistParser instanceof HTMLAutoLinkParser)
					&& !(clistParser instanceof HTMLLinkParser));
				//#ifdef DLOGGING
				if (m_fineLoggable) {m_logger.fine("redirect html=" + clistParser.isRedirectHtml());}
				//#endif
				//#endif
				
				// Start parsing
				//#ifdef DMIDP20
				synchronized(this) {
					m_backGrListParser = clistParser;
					m_backGrListParser.getObservableHandler().addObserver(this);
					m_parseBackground = true;
					loadForm.setObservable(m_backGrListParser);
				}
				loadForm.addPromptCommand(RssReaderMIDlet.m_backCommand,
									"Are you sure that you want to go back? Reading the list has not finished.");
				//#endif
				clistParser.startParsing();
				//#ifdef DMIDP10
				clistParser.join();
				addFeedLists(clistParser);
				//#endif
				
				// 3. Show result screen
				// 4. Show list of feeds
				
			} catch(Exception ex) {
				loadForm.recordExcForm("Error importing feeds from " + url, ex);
			} catch(OutOfMemoryError ex) {
				loadForm.recordExcForm("Out Of Memory Error importing feeds from " +
						url, ex);
			} catch(Throwable t) {
				loadForm.recordExcForm("Internal error importing feeds from " +
						url, t);
			}
		}

	}

	private Item[] getItemFields() {
		Item[] items = {m_importFormatGroup,
			m_feedNameFilter,
			m_feedURLFilter, m_UrlUsername,
			m_UrlPassword,
			m_importFormatGroup, m_importTitleGroup
		//#ifndef DSMALLMEM
				, m_importHTMLGroup
		//#endif
		};
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
			//#ifdef DMIDP20
			boolean cparseBackground = false;
			synchronized(this) {
				cparseBackground = m_parseBackground;
			}
			if (cparseBackground) {
				featureMgr.getMidlet().getLoadForm().appendNote(
						"NOTE:  Import parsing has already started.  Please wait for it to finish.");
			} else {
				m_getFeedList = true;
			}
			//#else
			m_getFeedList = true;
			//#endif
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
		/** Import list of feeds and auto edit bookmarks/feeds */
		if( c == m_testImportCmd ) {
			RssReaderMIDlet midlet = featureMgr.getMidlet();
			midlet.setBookmarkIndex(m_bookmarkList.size());
			System.out.println("Test UI Test Rss feeds m_bookmarkIndex=" +
					midlet.getBookmarkIndex());
			commandAction(m_appndCmd, this);
		}
		//#endif

	}

	/** Add from feed list (from import). */
	public static void addFeedLists(FeedListParser listParser,
			int addBkmrk,
			int maxItemCount, boolean override, Hashtable rssFeeds,
			FeatureList bookmarkList, LoadingForm loadForm)
	throws CauseException, Exception {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("ImportFeedsForm");
		// Feed list parsing is ready
		logger.info("Feed list parsing is ready");
		logger.finest("addFeedLists rssFeeds=" + rssFeeds);
		//#endif
		if(!listParser.isSuccessfull()) {
			throw listParser.getEx();
		}
		RssItunesFeed[] feeds = listParser.getFeeds();
		if (feeds.length == 0) {
			loadForm.appendNote(
					"No feeds found in file or file does not match requested " +
					"format URL:" + listParser.getUrl());
		}
		for(int feedIndex=0; feedIndex<feeds.length; feedIndex++) {
			String name = feeds[feedIndex].getName();
			//#ifdef DTEST
			//#ifdef DLOGGING
			logger.info("Adding: " + name);
			//#endif
			//#endif
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
