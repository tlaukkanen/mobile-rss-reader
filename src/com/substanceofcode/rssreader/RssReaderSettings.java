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

package com.substanceofcode.rssreader;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStoreException;

/**
 * RssFeedReaderSettings contains application's settings.
 *
 * @author Tommi Laukkanen
 */
public class RssReaderSettings {
    
    private Settings m_settings;
    private static RssReaderSettings m_singleton;    
    
    private static final String MAX_ITEM_COUNT = "max-item-count";
    private static final String IMPORT_URL = "import-url";
    private static final String IMPORT_USERNAME = "import-username";
    private static final String IMPORT_PASSWORD = "import-password";
    
    /** Creates a new instance of RssFeedReaderSettings */
    private RssReaderSettings(MIDlet midlet) {
        try {
            m_settings = Settings.getInstance(midlet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** Get instance */
    public static RssReaderSettings getInstance(MIDlet midlet) {
        if(m_singleton==null) {
            m_singleton = new RssReaderSettings(midlet);
        }
        return m_singleton;
    }
    
    /** Get maximum item count */
    public int getMaximumItemCountInFeed() {
        int maxCount = m_settings.getIntProperty(MAX_ITEM_COUNT, 10);
        return maxCount;
    }
    
    /** Set maximum item count in feed */
    public void setMaximumItemCountInFeed(int maxCount) {
        m_settings.setIntProperty(MAX_ITEM_COUNT, maxCount);
    }
    
    /** Get import URL address */
    public String getImportUrl() {
        String url = m_settings.getStringProperty(IMPORT_URL, "");
        return url;
    }
    
    /** Set import URL address */
    public void setImportUrl(String url) {
        m_settings.setStringProperty(IMPORT_URL, url);
    }
    
    /** Get import URL username */
    public String getImportUrlUsername() {
        String username = m_settings.getStringProperty(IMPORT_USERNAME, "");
        return username;
    }
    
    /** Set import URL username */
    public void setImportUrlUsername(String username) {
        m_settings.setStringProperty(IMPORT_USERNAME, username);
    }
    
    /** Get import URL password */
    public String getImportUrlPassword() {
        String password = m_settings.getStringProperty(IMPORT_PASSWORD, "");
        return password;
    }
    
    /** Set import URL password */
    public void setImportUrlPassword(String password) {
        m_settings.setStringProperty(IMPORT_PASSWORD, password);
    }
    
    
}
