/*
 * RssFeedReaderSettings.java
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
// Expand to define itunes define
@DITUNESDEF@
// Expand to define test define
@DTESTDEF@
package com.substanceofcode.rssreader.businessentities;

import com.substanceofcode.utils.Settings;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Font;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.lcdui.List;

/**
 * RssFeedReaderSettings contains application's settings.
 *
 * @author Tommi Laukkanen
 */
public class RssReaderSettings {
    
    private Settings m_settings;
    private static RssReaderSettings m_singleton;    
	//#ifdef DMIDP20
    public static final int DEFAULT_FONT_CHOICE = 0;
	//#endif
    
    private static final String MAX_ITEM_COUNT = "max-item-count";
    private static final String MAX_WORD_COUNT = "max-word-count";
    private static final String IMPORT_URL = "import-url";
    private static final String IMPORT_USERNAME = "import-username";
    private static final String IMPORT_PASSWORD = "import-password";
    private static final String MARK_UNREAD_ITEMS = "mark-unread-items";
    private static final String FEED_LIST_OPEN = "feed-list-open";
    private static final String ITUNES_ENABLED = "itunes-enabled";
	//#ifdef DMIDP20
    private static final String FONT_CHOICE = "font-choice";
    private static final String FIT_POLICY = "fit-policy";
    private static final String BOOKMARK_NAME_NEWS = "bookmark-name-news";
	//#endif
    private static final String USE_TEXT_BOX = "use-text-box";
    private static final String USE_STANDARD_EXIT = "use-standard-exit";
    private static final String NOVICE = "novice";
	//#ifdef DTEST
    private static final String LOG_LEVEL = "log-level";
	//#endif
    
    /** Creates a new instance of RssFeedReaderSettings */
    private RssReaderSettings(MIDlet midlet) {
        try {
            m_settings = Settings.getInstance(midlet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** Get instance */
    final public static RssReaderSettings getInstance(MIDlet midlet) {
        if(m_singleton==null) {
            m_singleton = new RssReaderSettings(midlet);
        }
        return m_singleton;
    }
    
    /** Get maximum item count */
    final public int getMaximumItemCountInFeed() {
        int maxCount = m_settings.getIntProperty(MAX_ITEM_COUNT, 10);
        return maxCount;
    }
    
    /** Set maximum item count in feed */
    final public void setMaximumItemCountInFeed(int maxCount) {
        m_settings.setIntProperty(MAX_ITEM_COUNT, maxCount);
    }
    
    /** Get maximum word count in description */
    final public int getMaxWordCountInDesc() {
        int maxCount = m_settings.getIntProperty(MAX_WORD_COUNT, 10);
        return maxCount;
    }
    
    /** Set maximum word count in description */
    final public void setMaxWordCountInDesc(int maxCount) {
        m_settings.setIntProperty(MAX_WORD_COUNT, maxCount);
    }
    
    /** Get import URL address */
    final public String getImportUrl() {
        String url = m_settings.getStringProperty(0, IMPORT_URL, "");
        return url;
    }
    
    /** Set import URL address */
    final public void setImportUrl(String url) {
        m_settings.setStringProperty( IMPORT_URL, url);
    }
    
    /** Get import URL username */
    final public String getImportUrlUsername() {
        String username = m_settings.getStringProperty(0, IMPORT_USERNAME, "");
        return username;
    }
    
    /** Set import URL username */
    final public void setImportUrlUsername(String username) {
        m_settings.setStringProperty( IMPORT_USERNAME, username);
    }
    
    /** Get import URL password */
    final public String getImportUrlPassword() {
        String password = m_settings.getStringProperty(0, IMPORT_PASSWORD, "");
        return password;
    }
    
    /** Set import URL password */
    final public void setImportUrlPassword(String password) {
        m_settings.setStringProperty( IMPORT_PASSWORD, password);
    }
    
    /** Get mark unread items */
    final public boolean getMarkUnreadItems() {
        return m_settings.getBooleanProperty( MARK_UNREAD_ITEMS, false);
    }
    
    /** Set import URL password */
    final public void setMarkUnreadItems(boolean markUnreadItems) {
        m_settings.setBooleanProperty( MARK_UNREAD_ITEMS, markUnreadItems);
    }
    
    /** Get feed list back is first command */
    final public boolean getFeedListOpen() {
        return m_settings.getBooleanProperty( FEED_LIST_OPEN, true);
    }
    
    /** Set feed list back is first command */
    final public void setFeedListOpen(boolean feedListOpen) {
        m_settings.setBooleanProperty( FEED_LIST_OPEN, feedListOpen);
    }
    
    /** Get itunes enabled */
    final public boolean getItunesEnabled() {
		//#ifdef DITUNES
        return m_settings.getBooleanProperty( ITUNES_ENABLED, true);
		//#else
        return m_settings.getBooleanProperty( ITUNES_ENABLED, false);
		//#endif
    }
    
    /** Set feed list back is first command */
    final public void setItunesEnabled(boolean itunesEnabled) {
        m_settings.setBooleanProperty( ITUNES_ENABLED, itunesEnabled);
    }
    
	//#ifdef DMIDP20
    /** Get font choice */
    final public int getFontChoice() {
        return m_settings.getIntProperty( FONT_CHOICE, DEFAULT_FONT_CHOICE);
    }
    
	/* Get the font size. This is the actual size of the font */
	final public int getFontSize() {
		int fontSize;
		switch (getFontChoice()) {
			case 1:
				fontSize = Font.SIZE_SMALL;
				break;
			case 2:
				fontSize = Font.SIZE_MEDIUM;
				break;
			case 3:
				fontSize = Font.SIZE_LARGE;
				break;
			case DEFAULT_FONT_CHOICE:
			default:
				fontSize = Font.getDefaultFont().getSize();
				break;
		}
		return fontSize;
	}

    /** Set font size */
    final public void setFontChoice(int fontChoice) {
        m_settings.setIntProperty( FONT_CHOICE, fontChoice);
    }
    
    /** Get fit policy */
    final public int getFitPolicy() {
        return m_settings.getIntProperty( FIT_POLICY, List.TEXT_WRAP_DEFAULT);
    }
    
    /** Set fit policy */
    final public void setFitPolicy(int fitPolicy) {
        m_settings.setIntProperty( FIT_POLICY, fitPolicy);
    }
    
    /** Get put bookmark name in news item list.*/
    final public boolean getBookmarkNameNews() {
        return m_settings.getBooleanProperty( BOOKMARK_NAME_NEWS, false);
    }
    
    /** Set put bookmark name in news item list. */
    final public void setBookmarkNameNews(boolean bookmarkNameNews) {
        m_settings.setBooleanProperty( BOOKMARK_NAME_NEWS, bookmarkNameNews);
    }
    
    /** Get use text box */
    final public boolean getUseTextBox() {
        return m_settings.getBooleanProperty( USE_TEXT_BOX, false);
    }
    
    /** Set use text box */
    final public void setUseTextBox(boolean useTextBox) {
        m_settings.setBooleanProperty( USE_TEXT_BOX, useTextBox);
    }
	//#endif
    
    /** Get use standard exit */
    final public boolean getUseStandardExit() {
        return m_settings.getBooleanProperty( USE_STANDARD_EXIT, false);
    }
    
    /** Set standard exit */
    final public void setUseStandardExit(boolean useStandardExit) {
        m_settings.setBooleanProperty( USE_STANDARD_EXIT, useStandardExit);
    }
    
    /** Get novice user */
    final public boolean getNovice() {
        return m_settings.getBooleanProperty( NOVICE, false);
    }
    
    /** Set novice user */
    final public void setNovice(boolean novice) {
        m_settings.setBooleanProperty( NOVICE, novice);
    }
    
	//#ifdef DTEST
    /** Get log level */
    final public String getLogLevel() {
        String logLevel = m_settings.getStringProperty(0, LOG_LEVEL, "");
        return logLevel;
    }
    
    /** Set import URL password */
    final public void setLogLevel(String logLevel) {
        m_settings.setStringProperty( LOG_LEVEL, logLevel);
    }
	//#endif
    
    /** Get settings version */
    final public String getSettingsVers() {
        return m_settings.getStringProperty( 0, Settings.SETTINGS_NAME, "");
    }
    
}
