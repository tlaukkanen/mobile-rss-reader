/*
 * RssFeedReaderSettings.java
 *
 * Created on 8. lokakuuta 2006, 22:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
}
