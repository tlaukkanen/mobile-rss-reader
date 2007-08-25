/*
 * Settings.java
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

// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.utils;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * A class for storing and retrieving application settings and properties.
 * Class stores all settings into one Hashtable variable. Hashtable is loaded
 * from RecordStore at initialization and it is stored back to the RecordStore
 * with save method.
 *
 * @author  Tommi Laukkanen
 * @version 1.0
 */
public class Settings {
    
    public static final int MAX_REGIONS = 6;
    public static final String SETTINGS_NAME = "RssReader-setttings-vers";
    public static final String SETTINGS_VERS = "2";
    private static Settings m_store;
    private MIDlet          m_midlet;
    private boolean         m_valuesChanged = false;
    private boolean         m_initialized = true;
    private Hashtable       m_properties = new Hashtable();
    private int             m_region;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("Settings");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /**
     * Singleton pattern is used to return 
     * only one instance of record store
     */
    public static synchronized Settings getInstance( MIDlet midlet )
    throws IOException, RecordStoreException {
        if( m_store == null ) {
            m_store = new Settings( midlet );
        }
        return m_store;
    }

    /** Constructor */
    private Settings( MIDlet midlet )
    throws IOException, RecordStoreException {
        m_midlet = midlet;
        load(0);
    }
    
    /** Return true if value exists in record store */
    private boolean exists( String name ) {
        return getProperty( name ) != null;
    }
    
    /** Get property from Hashtable*/
    private synchronized String getProperty( String name ) {
        String value = (String) m_properties.get( name );
        if( value == null && m_midlet != null ) {
            value = m_midlet.getAppProperty( name );
            if( value != null ) {
                m_properties.put( name, value );
            }
        }
        return value;
    }
    
    /** Get boolean property */
    public boolean getBooleanProperty( String name, boolean defaultValue) {
        String value = getProperty( name );
        if( value != null ) {
            return value.equals( "true" ) || value.equals( "1" );
        }
        return defaultValue;
    }
    
    /** Get integer property */
    public int getIntProperty( String name, int defaultValue ) {
        String value = getProperty( name );
        if( value != null ) {
            try {
                return Integer.parseInt( value );
            } catch( NumberFormatException e ) {
            }
        }
        return defaultValue;
    }
    
    /** Get string property */
    public String getStringProperty(int region, String name,
			                        String defaultValue ) {
		if (region != m_region) {
			try {
				load(region);
			} catch (Exception e) {
				System.out.println("load error");
				return defaultValue;
			}
		}
        Object value = getProperty( name );
        return ( value != null ) ? value.toString() : defaultValue;
    }
    
    /** Load properties from record store */
    private synchronized void load(int region)
    throws IOException, RecordStoreException {
        RecordStore rs = null;
        ByteArrayInputStream bin = null;
        DataInputStream din = null;
        
        m_valuesChanged = false;
        m_properties.clear();
		boolean currentSettings = true;
        
        try {
            rs = RecordStore.openRecordStore("Store", true );
			int numRecs = rs.getNumRecords();
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("region=" + region);}
			if (finestLoggable) {logger.finest("numRecs=" + numRecs);}
			//#endif
            if( numRecs == 0 ) {
				if (region == 0) {
					m_initialized = false;
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("m_initialized=" + m_initialized);}
					//#endif
				}
			} else {
				if ( numRecs < MAX_REGIONS ) {
					currentSettings = false;
				}
                byte[] data = rs.getRecord( region + 1 );
                if( data != null ) {
                    bin = new ByteArrayInputStream( data );
                    din = new DataInputStream( bin );
                    int num = din.readInt();
                    while( num-- > 0 ) {
                        String name = din.readUTF();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("name=" + name);}
						//#endif
                        String value;
						if (currentSettings) {
							int blen = din.readInt();
							byte [] bvalue = new byte[blen];
							din.read(bvalue);
							value = new String(bvalue);
						} else {
							value = din.readUTF();
						}
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("value=" + value);}
						//#endif
						m_properties.put( name, value );
                    }
                }
            }
			for (int ic = numRecs; ic < MAX_REGIONS; ic++) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("adding ic=" + ic);}
				//#endif
				rs.addRecord( null, 0, 0 );
			}
			if (!currentSettings && ( numRecs > 0 ) && (region == 0)) {
				// If not current settings, save them to udate to
				// current.
				save(0, true);
				// Update bookmark region too.
				save(1, true);
			}
			m_region = region;
        } finally {
            if( din != null ) {
                try { din.close(); } catch( Exception e ){}
            }
            
            if( rs != null ) {
                try { rs.closeRecordStore(); } catch( Exception e ){}
            }
        }
    }
    
    /** Save property Hashtable to record store.
        Use MAX_REGIONS records in store to help with running out of memory.  */
    public synchronized void save( int region, boolean force )
            throws IOException, RecordStoreException {
        if( !m_valuesChanged && !force ) return;
        
        RecordStore rs = null;
        ByteArrayOutputStream bout = new
                ByteArrayOutputStream();
        DataOutputStream dout = new
                DataOutputStream( bout );
        
        try {
			Object vers = null;
            if ( m_properties.containsKey(SETTINGS_NAME) ) {
				vers = m_properties.get(SETTINGS_NAME);
			}
			m_properties.put(SETTINGS_NAME, SETTINGS_VERS);
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("save region=" + region);}
			//#endif
            dout.writeInt( m_properties.size() );
            Enumeration e = m_properties.keys();
            while( e.hasMoreElements() ) {
                String name = (String) e.nextElement();
                String value = m_properties.get( name ).toString();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("name=" + name);}
				//#endif
                dout.writeUTF( name );
				byte[] bvalue = value.getBytes();
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("value=" + value);}
				//#endif
                dout.writeInt( bvalue.length );
                dout.write( bvalue, 0, bvalue.length );
            }
            
            byte[] data = bout.toByteArray();
            
            rs = RecordStore.openRecordStore( "Store", false );
            rs.setRecord( (region + 1), data, 0, data.length );
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("stored region=" + region);}
			//#endif
			if ( vers != null) {
				m_properties.put(SETTINGS_NAME, vers);
			}
        } finally {
            try { dout.close(); } catch( Exception e ){}
            
            if( rs != null ) {
                try { rs.closeRecordStore(); } catch( Exception e ){}
            }
        }
    }
    
    /** Get memory usage of the record store */
    public synchronized Hashtable getSettingMemInfo()
		throws IOException, RecordStoreException {
		try {
        
			RecordStore rs = null;
			Hashtable memInfo = null;
			
			try {
				
				rs = RecordStore.openRecordStore( "Store", false );
				memInfo = new Hashtable(2);
				memInfo.put("used", Integer.toString(rs.getSize()));
				memInfo.put("available", Integer.toString(
						rs.getSizeAvailable()));
				return memInfo;
			} finally {
				
				if( rs != null ) {
					try { rs.closeRecordStore(); } catch( Exception e ){}
				}
			}
		} catch (RecordStoreNotFoundException re) {
			return new Hashtable(0);
		} catch (Exception e) {
			System.out.println("Error in getSettingMemInfo()");
			e.printStackTrace();
			return new Hashtable(0);
		}
    }
    
    /** Set a boolean property */
    public void setBooleanProperty( String name, boolean value ) {
        setStringProperty( name, value ? "true" : "false" );
    }
    
    /** Set an integer property */
    public void setIntProperty( String name, int value ) {
        setStringProperty( name, Integer.toString( value ) );
    }
    
    /** Set a string property */
    public synchronized boolean setStringProperty( String name, String value ) {
        if( name == null && value == null ) return false;
        m_properties.put( name, value );
        m_valuesChanged = true;
        return true;
    }

	/** Get properties size to allow us to know if it was from a load or not.
	  **/
	public boolean isInitialized() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("m_initialized=" + m_initialized);}
		//#endif
		return m_initialized;
	}

}
