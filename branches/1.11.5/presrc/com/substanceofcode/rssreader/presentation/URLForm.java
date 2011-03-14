//--Need to modify--#preprocess
/*
 * URLForm.java
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
 * IB 2010-06-27 1.11.5Dev2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-09-26 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-09-26 1.11.5Dev8 Repalce setCurrent with showMe.
 * IB 2010-09-26 1.11.5Dev8 Use loadingForm from FeatureMgr.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-16 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
 * IB 2010-11-18 1.11.5Dev14 Move find files call functionality to FeatureMgr.
 * IB 2010-11-18 1.11.5Dev14 Allow select directory for find files load message to be passed as a parameters (if selectDir true/false) to make it more generic.
>  * IB 2010-11-22 1.11.5Dev14 Replace Alert with loading form exception.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-12 1.11.5Alpha15 Use midlet in FeatureMgr with getRssMidlet to get the RssReaderMIDlet.
 * IB 2011-01-11 1.11.5Alpha15 Use super.featureMgr instead of featureMgr.
 * IB 2011-02-05 1.11.5Dev17 Have FeatureMgr.getCmdAdd to both create a command and add it to the displayable.  Return the command pointer.
 * IB 2011-02-05 1.11.5Dev17 Have FeatureMgr.getCmdAddPrompt to both create a prompt command and add it to the displayable.  Return the command pointer.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define itunes define
@DITUNESDEF@
// Expand to define logging define
@DLOGDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define full vers define
@DFULLVERSDEF@

//#ifdef DFULLVERS
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
// If using the test UI define the Test UI's
import com.substanceofcode.testlcdui.ChoiceGroup;
import com.substanceofcode.testlcdui.Form;
import com.substanceofcode.testlcdui.List;
import com.substanceofcode.testlcdui.TextBox;
import com.substanceofcode.testlcdui.TextField;
import com.substanceofcode.testlcdui.StringItem;
//#endif
import javax.microedition.lcdui.Item;

import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.presentation.ImportFeedsForm;

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.LoadingForm;
import com.substanceofcode.rssreader.presentation.FeatureMgr;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi Laukkanen
 */
public class URLForm extends FeatureForm
	implements CommandListener {

	protected boolean     m_ok = false;      // The ok flag
	protected boolean     m_cancel = false;      // The cancel flag
	protected boolean     m_clear = false;      // The clear flag
	protected boolean     m_last = false;      // The last flag
	//#ifdef DJSR75
	protected boolean     m_selectDir = false;  // The select directory flag
	protected boolean     m_fileReq = false;      // The get file flag
	//#endif
	protected int         m_addBkmrk = 0;// Place to add (insert) imported bookmarks
	protected TextField   m_url;      // The feed list URL field
	protected TextField   m_UrlUsername; // The feed list username
	protected TextField   m_UrlPassword; // The feed list password
	protected Command     m_addCmd = null; // The add command 
	protected Command     m_insCmd = null;   // The import before the current point?
	protected Command     m_appndCmd = null; // The import append
	protected Command     m_lastCmd = null; // The last data used
	protected Command     m_clearCmd; // The clear
	//#ifdef DJSR75
	protected Command     m_fileCmd;    // The find files command for importing
	//#endif
	//#ifdef DMIDP20
	protected Command     m_pasteURLCmd;// The paste command
	//#endif
	protected RssFeedStore m_rssFeeds;         // The bookmark URLs
	protected RssReaderSettings m_appSettings;// The application settings
	// The loading form
	protected Thread m_thread = null; // The thread

	//#ifdef DLOGGING
	private Logger m_logger = Logger.getLogger("URLForm");
	//#endif

	/** Initialize import form */
	public URLForm(String formName, boolean selectDir,
			RssFeedStore rssFeeds,
			RssReaderSettings appSettings,
			LoadingForm loadForm) {
		super(formName, loadForm);
		//#ifdef DJSR75
		m_selectDir = selectDir;
		//#endif
		m_rssFeeds = rssFeeds;
		m_appSettings = appSettings;
	}

  /**
   *
   * Initialize UI elements
   *
   * @author Irv Bunton
   */
	public void initUrlUI(String url, boolean hasOK, String okPrompt,
			int initPriority) {
		if(url.length()==0) {
			url = "http://";
		}
		m_url = new TextField("URL", url, 256, TextField.URL);
		super.append(m_url);
		
		FeatureMgr.getCmdAdd(this, "Cancel", null, Command.CANCEL, 2);
		if (hasOK) {
			if (okPrompt != null) {
				FeatureMgr.getCmdAddPrompt(this, "OK", null, Command.OK, 4,
						okPrompt);
			} else {
				FeatureMgr.getCmdAdd(this, "OK", null, Command.OK, 4);
			}
		}

		//#ifdef DJSR75
		/* Find files */
		m_fileCmd     = FeatureMgr.getCmdAdd(this, "Find", "Find files",
				Command.SCREEN, initPriority++);
		//#endif
		/* Allow paste */
		//#ifdef DMIDP20
		if (m_appSettings.getUseTextBox()) {
			m_pasteURLCmd = FeatureMgr.getCmdAdd(this, "Paste", "Allow paste", Command.SCREEN,
					initPriority++);
		}
		//#endif
		// Show last data entered
		m_lastCmd = FeatureMgr.getCmdAdd(this, "Last", "Last Entry", Command.SCREEN, initPriority++);
		
	}

  /**
   *
   * Initialize UI elements
   *
   * @author Irv Bunton
   */
	public void initCommonInputUI(String url, String username, String password,
			boolean hasOK, String okPrompt, int initPriority) {
		initUrlUI(url, hasOK, okPrompt, initPriority);
		
		m_UrlUsername  = new TextField("Username (optional)", username, 64, TextField.ANY);
		super.append(m_UrlUsername);
		
		m_UrlPassword  = new TextField("Password (optional)", password, 64, TextField.PASSWORD);
		super.append(m_UrlPassword);
		
	}

	public void initAddUI(String url, String username, String password,
			boolean hasOK, String okPrompt, int initPriority,
			String insLabel, String insLongLabel,
			String addLabel, String addLongLabel,
			String appendLabel, String appendLongLabel) {

		m_insCmd      = FeatureMgr.getCmdAdd(this, insLabel, insLongLabel,
				Command.SCREEN, initPriority++);
		m_addCmd      = FeatureMgr.getCmdAdd(this, addLabel, addLongLabel,
				Command.SCREEN, initPriority++);
		m_appndCmd    = FeatureMgr.getCmdAdd(this, appendLabel, appendLongLabel,
				Command.SCREEN, initPriority++);

		/* Clear */
		m_clearCmd   = FeatureMgr.getCmdAdd(this, "Clear", null, Command.SCREEN,
				initPriority++);

		initCommonInputUI(url, username, password, hasOK, okPrompt, initPriority);
	}

	/** Respond to commands */
	public void execute() {

		if (m_clear) {
			m_clear = false;
			m_url.setString("http://");
			m_UrlUsername.setString("");
			m_UrlPassword.setString("");
		}
		if (m_cancel) {
			m_cancel = false;
			super.featureMgr.getLoadForm().replaceRef(this, null);
			super.featureMgr.getRssMidlet().showBookmarkList();
		}

		//#ifdef DJSR75
		if (m_fileReq) {
			m_fileReq = false;
			if (!super.featureMgr.getRssMidlet().JSR75_ENABLED) {
				CauseException ce = new CauseException(
						"Find files (JSR-75) not enabled on the phone.");
				LoadingForm loadForm = super.getFeatureMgr().getLoadForm();
				if (loadForm != null) {
					loadForm.recordExcFormFin(
							"Find files (JSR-75) not enabled on the phone.", ce);
				}
				return;
			}
			try {
				RssReaderMIDlet midlet = featureMgr.getRssMidlet();
				if (midlet != null) {
					if (this instanceof ImportFeedsForm) {
						super.featureMgr.getFindFiles(m_selectDir,
								"Loading files to export from...",
								"Loading files to import from...", 
						(m_selectDir ? "Find export file" : "Find import file"),
						 this, m_url);
					} else {
						super.featureMgr.getFindFiles(false,
								"Loading files to read from...", null,
								"Find feed file", this, m_url);
					}
				}
			}catch(Throwable t) {
				//#ifdef DLOGGING
				m_logger.severe("URLForm find files ", t);
				//#endif
				/** Error while executing find files */
				System.out.println("URLForm find files " + t.getMessage());
				t.printStackTrace();
			}
		}
		//#endif
				
	}

	/** Respond to commands */
	public void commandAction(Command c, Displayable s) {
		int commandType = c.getCommandType();

		/** Clear data. */
		if ( c == m_clearCmd ) {
			m_clear = true;
		}

		/** OK accept input */
		if ( commandType == Command.OK ) {
			m_ok = true;
		}
		
		/** Cancel currently edited (or added) RSS feed's properties */
		if ( commandType == Command.CANCEL ) {
			m_cancel = true;
		}
		
		//#ifdef DMIDP20
		/** Put current bookmark URL into URL box.  */
		if( c == m_pasteURLCmd ) {
			FeatureMgr.initializeURLBox( m_url.getString(), this, m_url );
		}
		//#endif

		//#ifdef DJSR75
		/** Find bookmark file in file system */
		if( c == m_fileCmd ) {
			m_fileReq = true;
		}
		//#endif
				
		if( c == m_lastCmd ) {
			m_last = true;
		}

	}

}
//#endif
