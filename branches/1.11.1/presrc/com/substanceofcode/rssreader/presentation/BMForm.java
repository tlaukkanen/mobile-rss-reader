//--Need to modify--#preprocess
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
/*
 * IB 2010-05-26 1.11.5RC2 Code cleanup.
 * IB 2010-06-27 1.11.5RC2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-08-15 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-09-26 1.11.5Dev8 Support loadingForm with FeatureMgr.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-17 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 * IB 2011-01-12 1.11.5Alpha15 After modifying/updating the feed, use old feed pointer with new feed pointer to update the feed.  If the old pointer does not match the current pointer, do not update as it means that the future background processing has updated the feed already. 
  * IB 2011-01-11 1.11.5Dev15 Use super.featureMgr instead of featureMgr.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
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
import com.substanceofcode.testlcdui.TextField;
import com.substanceofcode.testlcdui.StringItem;
//#endif
import javax.microedition.lcdui.Item;

import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
import com.substanceofcode.rssreader.businesslogic.FeedListParser;
import com.substanceofcode.rssreader.businesslogic.LineByLineParser;
import com.substanceofcode.rssreader.businesslogic.OpmlParser;
import com.substanceofcode.rssreader.businesslogic.RssFeedParser;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.LoadingForm;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form to add new/edit existing bookmark. */
final public class BMForm extends URLForm
	implements CommandListener {
    static byte[]      m_addBMSave = null; // Add bookmark form save
	private RssItunesFeed m_oldFeed;
	private boolean     m_addForm;          // Flag to indicate is add form
	private TextField   m_bmName;           // The RSS feed name field
	private FeatureList m_bookmarkList;

	/* Constructor */
	public BMForm(RssFeedStore rssFeeds,
			RssReaderSettings appSettings,
			FeatureList bookmarkList,
			LoadingForm loadForm) {
		super("New Bookmark", false, rssFeeds, appSettings, loadForm);
		this.m_addForm = true;
		this.m_bookmarkList = bookmarkList;
		m_bmName = new TextField("Name", "", RssFeed.MAX_NAME_LEN, TextField.ANY);
		super.append( m_bmName );
		super.initAddUI("", "", "", false, null, 7, "Insert bookmark",
						"Insert current bookmark",
						"Add bookmark", "Add current bookmark",
						"Append bookmark", "Append end bookmark");
	}

	public BMForm(RssFeedStore rssFeeds,
			RssReaderSettings appSettings,
			FeatureList bookmarkList,
			LoadingForm loadForm,
			final RssItunesFeed bm) {
		super("Edit Bookmark", false, rssFeeds, appSettings,
				loadForm);
		this.m_addForm = false;
		this.m_bookmarkList = bookmarkList;
		this.m_oldFeed = bm;
		String oldName = bm.getName();
		m_bmName = new TextField("Name", oldName,
				Math.max(oldName.length() + 10, RssFeed.MAX_NAME_LEN),
				TextField.ANY);
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
		m_rssFeeds.put(bm.getName(), bm, m_oldFeed);
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
				super.featureMgr.getLoadForm().replaceRef(this, null);
				Item[] items = {m_bmName, m_url,
					m_UrlUsername, m_UrlPassword};
				BMForm.m_addBMSave = FeatureMgr.storeValues(items);
				FeatureMgr.getRssMidlet().showBookmarkList();
			}
		}

		/** Save currently edited (or added) RSS feed's properties */
		if( m_ok ){
			m_ok = false;
			saveBookmark();
			super.featureMgr.getLoadForm().replaceRef(this, null);
			FeatureMgr.getRssMidlet().showBookmarkList();
		}

		/** Clear data. */
		if ( c == m_clearCmd ) {
			m_bmName.setString("");
			super.commandAction(c, s);
		}
		
		if (m_last) {
			m_last = false;
			if (BMForm.m_addBMSave != null) {
				Item[] items = {m_bmName, m_url,
					m_UrlUsername, m_UrlPassword};
				FeatureMgr.restorePrevValues(items, BMForm.m_addBMSave);
			}
		}

		super.execute();
	}

}
//#endif
