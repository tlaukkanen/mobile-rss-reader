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
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;

//#ifdef DJSR75
import org.kablog.kgui.KFileSelectorMgr;
//#endif
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;

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
	implements CommandListener, Runnable {

	protected boolean     m_cancel = false;      // The cancel flag
	protected boolean     m_clear = false;      // The clear flag
	//#ifdef DJSR75
	protected boolean     m_fileReq = false;      // The get file flag
	//#endif
	protected int         m_addBkmrk = 0;// Place to add (insert) imported bookmarks
	protected TextField   m_url;      // The feed list URL field
	protected TextField   m_UrlUsername; // The feed list username
	protected TextField   m_UrlPassword; // The feed list password
	protected Command     m_addCmd = null; // The add command 
	protected Command     m_insCmd = null;   // The import before the current point?
	protected Command     m_appndCmd = null; // The import append
	protected Command     m_clearCmd; // The clear
//#ifdef DJSR75
	protected Command     m_fileCmd;    // The find files command for importing
//#endif
	//#ifdef DMIDP20
	protected Command     m_pasteURLCmd;// The paste command
	//#endif
	protected RssReaderMIDlet m_midlet;       // The application midlet
	protected Hashtable m_rssFeeds;         // The bookmark URLs
	protected RssReaderSettings m_appSettings;// The application settings
	protected RssReaderMIDlet.LoadingForm m_loadForm; // The application settings
	protected Thread m_thread = null; // The thread

	//#ifdef DLOGGING
	private Logger m_logger = Logger.getLogger("URLForm");
	private boolean m_fineLoggable = m_logger.isLoggable(Level.FINE);
	private boolean m_finerLoggable = m_logger.isLoggable(Level.FINER);
	private boolean m_finestLoggable = m_logger.isLoggable(Level.FINEST);
	//#endif

	/** Initialize import form */
	public URLForm(RssReaderMIDlet midlet, String formName,
			Hashtable rssFeeds,
			RssReaderSettings appSettings,
			RssReaderMIDlet.LoadingForm loadForm) {
		super(midlet, formName);
		m_midlet = midlet;
		m_rssFeeds = rssFeeds;
		m_appSettings = appSettings;
		m_loadForm = loadForm;
	}

  /**
   *
   * Initialize UI elements
   *
   * @author Irv Bunton
   */
	public void initCommonUI(String url, String username, String password,
			int initPriority) {
		if(url.length()==0) {
			url = "http://";
		}
		m_url = new TextField("URL", url, 256, TextField.URL);
		super.append(m_url);
		
		m_UrlUsername  = new TextField("Username (optional)", username, 64, TextField.ANY);
		super.append(m_UrlUsername);
		
		m_UrlPassword  = new TextField("Password (optional)", password, 64, TextField.PASSWORD);
		super.append(m_UrlPassword);
		/* Cancel */
		Command cancelCmd = new Command("Cancel", Command.CANCEL,
				initPriority++);
		//#ifdef DJSR75
		/* Find files */
		m_fileCmd     = new Command("Find files", Command.SCREEN,
				initPriority++);
		//#endif
		//#ifdef DMIDP20
		/* Allow paste */
		m_pasteURLCmd = new Command("Allow paste", Command.SCREEN,
				initPriority++);
		//#endif
		
		super.addCommand( cancelCmd );
		//#ifdef DJSR75
		super.addCommand( m_fileCmd );
		//#endif
		//#ifdef DMIDP20
		if (m_appSettings.getUseTextBox()) {
			super.addCommand(m_pasteURLCmd);
		}
		//#endif
	}

	public void initAddUI(String url, String username, String password,
			int initPriority,
			String insLabel, String insLongLabel,
			String addLabel, String addLongLabel,
			String appendLabel, String appendLongLabel) {

		//#ifdef DMIDP20
		m_insCmd      = new Command(insLabel, insLongLabel,
				Command.SCREEN, initPriority++);
		m_addCmd      = new Command(addLabel, addLongLabel,
				Command.SCREEN, initPriority++);
		m_appndCmd    = new Command(appendLabel, appendLongLabel,
				Command.SCREEN, initPriority++);
		//#else
		m_insCmd      = new Command(insLabel,
				Command.SCREEN, initPriority++);
		m_addCmd      = new Command(addLabel,
				Command.SCREEN, initPriority++);
		m_appndCmd    = new Command(appendLabel,
				Command.SCREEN, initPriority++);
		//#endif

		/* Clear */ m_clearCmd   = new Command("Clear", Command.SCREEN,
					initPriority++);

		super.addCommand( m_insCmd );
		super.addCommand( m_addCmd );
		super.addCommand( m_appndCmd );
		super.addCommand( m_clearCmd );

		initCommonUI(url, username, password, initPriority);
	}

	/** Respond to commands */
	public void run() {

		if (m_clear) {
			m_clear = false;
			m_url.setString("http://");
			m_UrlUsername.setString("");
			m_UrlPassword.setString("");
		}
		if (m_cancel) {
			m_cancel = false;
			m_loadForm.replaceRef(this, null);
			m_midlet.showBookmarkList();
		}

		//#ifdef DJSR75
		if (m_fileReq) {
			m_fileReq = false;
			if (!m_midlet.JSR75_ENABLED) {
				Alert invalidAlert = new Alert(
						"JSR-75 not enabled", 
						"Find files (JSR-75) not enabled on the phone.",
						null,
						AlertType.WARNING);
				invalidAlert.setTimeout(Alert.FOREVER);
				m_midlet.setCurrent( invalidAlert, this );
				return;
			}
			try {
				m_midlet.reqFindFiles( this, m_url);
				m_midlet.getFile();
			}catch(Throwable t) {
				//#ifdef DLOGGING
				m_logger.severe("RssReaderMIDlet find files ", t);
				//#endif
				/** Error while executing find files */
				System.out.println("RssReaderMIDlet find files " + t.getMessage());
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

		/** Cancel currently edited (or added) RSS feed's properties */
		if ( commandType == Command.CANCEL ) {
			m_cancel = true;
		}
		
		//#ifdef DMIDP20
		/** Put current bookmark URL into URL box.  */
		if( c == m_pasteURLCmd ) {
			new UiUtil().initializeURLBox( m_midlet, m_url.getString(),
					this, m_url );
		}
		//#endif

		//#ifdef DJSR75
		/** Find bookmark file in file system */
		if( c == m_fileCmd ) {
			m_fileReq = true;
		}
		//#endif
				
	}

}
