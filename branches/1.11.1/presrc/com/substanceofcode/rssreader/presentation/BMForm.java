/*
 * BMForm.java
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
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form to add new/edit existing bookmark. */
final public class BMForm extends URLForm
	implements CommandListener {
    static byte[]      m_addBMSave = null; // Add bookmark form save
	private boolean     m_addForm;          // Flag to indicate is add form
	private TextField   m_bmName;           // The RSS feed name field
	private FeatureList m_bookmarkList;

	/* Constructor */
	/* Constructor */
	public BMForm(RssReaderMIDlet midlet,
			Hashtable rssFeeds,
			RssReaderSettings appSettings,
			FeatureList bookmarkList,
			RssReaderMIDlet.LoadingForm loadForm) {
		super(midlet, "New Bookmark", false, rssFeeds, appSettings, loadForm);
		this.m_addForm = true;
		this.m_bookmarkList = bookmarkList;
		m_bmName = new TextField("Name", "", 64, TextField.ANY);
		super.append( m_bmName );
		super.initAddUI("", "", "", false, null, 1, "Insert bookmark",
						"Insert current bookmark",
						"Add bookmark", "Add current bookmark",
						"Append bookmark", "Append end bookmark");
		if (m_addBMSave != null) { 
			Item[] items = {m_bmName, m_url,
				m_UrlUsername, m_UrlPassword};
			m_midlet.restorePrevValues(items, m_addBMSave);
		}
	}

	public BMForm(RssReaderMIDlet midlet,
			Hashtable rssFeeds,
			RssReaderSettings appSettings,
			FeatureList bookmarkList,
			RssReaderMIDlet.LoadingForm loadForm,
			final RssItunesFeed bm) {
		super(midlet, "Edit Bookmark", false, rssFeeds, appSettings,
				loadForm);
		this.m_addForm = false;
		this.m_bookmarkList = bookmarkList;
		m_bmName = new TextField("Name", bm.getName(), 64, TextField.ANY);
		super.append( m_bmName );
		int initPriority = 1;
		super.initCommonInputUI(bm.getUrl(), bm.getUsername(),
				bm.getPassword(), true, null, initPriority);

	}

	/** Save bookmark into record store and bookmark list */
	private void saveBookmark(){
		final String name = m_bmName.getString();
		
		final String url  = m_url.getString().trim();
		
		final String username = m_UrlUsername.getString();
		
		final String password = m_UrlPassword.getString();
		
		final RssItunesFeed bm = new RssItunesFeed(name, url, username, password);
		
		if (m_addForm) {
			m_bookmarkList.insert(m_addBkmrk, bm.getName(), null);
		} else {
			m_bookmarkList.set(m_bookmarkList.getSelectedIndex(),
					bm.getName(), null);
		}
		m_rssFeeds.put(bm.getName(), bm);
	}
	
	/** Respond to commands */
	public void commandAction(Command c, Displayable s) {

		super.commandAction(c, s);

		/** Save currently added RSS feed's properties */

		if (m_addForm) {
			/** If add commands used, do add. */
			m_addBkmrk = FeatureMgr.getPlaceIndex(c, m_insCmd,
					m_addCmd, m_appndCmd, m_bookmarkList);

			if( m_addBkmrk >= 0 ) {
				saveBookmark();
				m_loadForm.replaceRef(this, null);
				Item[] items = {m_bmName, m_url,
					m_UrlUsername, m_UrlPassword};
				m_addBMSave = m_midlet.storeValues(items);
				m_midlet.showBookmarkList();
			}
		}

		/** Save currently edited (or added) RSS feed's properties */
		if( m_ok ){
			m_ok = false;
			saveBookmark();
			m_loadForm.replaceRef(this, null);
			m_midlet.showBookmarkList();
		}

		/** Clear data. */
		if ( c == m_clearCmd ) {
			m_bmName.setString("");
			super.commandAction(c, s);
		}
		
		super.execute();
	}

}
