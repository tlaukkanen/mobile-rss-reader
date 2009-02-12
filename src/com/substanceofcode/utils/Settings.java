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

// Expand to define CLDC define
//#define DCLDCV10
// Expand to define logging define
//#define DNOLOGGING
package com.substanceofcode.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
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
final public class Settings {
    
    public static final int OLD_MAX_REGIONS = 1;
	//#ifdef DCOMPATIBILITY2
//@    public static final int MAX_REGIONS = 10;
	//#else
    public static final int MAX_REGIONS = 15;
	//#endif
    public static final String SETTINGS_NAME = "RssReader-setttings-vers";
	// The first settings did not have a version, so it ends up being
	// "" by default
    public static final String FIRST_SETTINGS_VERS = "";
    public static final String ITUNES_CAPABLE_VERS = "3";
    public static final String ENCODING_VERS = "4";
    public static final String ITEMS_ENCODED = "items-encoded";
    public static final String STORE_DATE = "store-date";
	//#ifdef DCOMPATIBILITY2
//@    public static final String SETTINGS_VERS = "2";
	//#elifdef DCOMPATIBILITY3
//@    public static final String SETTINGS_VERS = ITUNES_CAPABLE_VERS;
	//#else
    public static final String SETTINGS_VERS = ENCODING_VERS;
	//#endif
    private static Settings m_store;
    private MIDlet          m_midlet;
    private boolean         m_valuesChanged = false;
    private boolean         m_initialized = true;
    private Hashtable       m_properties = new Hashtable();
    private int             m_region;
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("Settings");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
    
    /**
     * Singleton pattern is used to return 
     * only one instance of record store
     */
	//#ifdef DCLDCV11
//@    public static Settings getInstance( MIDlet midlet )
	//#else
    public static synchronized Settings getInstance( MIDlet midlet )
	//#endif
    throws IOException, RecordStoreException {
		//#ifdef DCLDCV11
//@		synchronized(Settings.class) {
		//#endif
			if( m_store == null ) {
				m_store = new Settings( midlet );
			}
			return m_store;
		//#ifdef DCLDCV11
//@        }
		//#endif
    }

    /** Constructor */
    private Settings( MIDlet midlet )
    throws IOException, RecordStoreException {
        m_midlet = midlet;
        load(0);
    }
    
    /** Return true if value exists in record store */
    public boolean exists( String name ) {
        return getProperty( name ) != null;
    }
    
    /** Get property from Hashtable*/
    private String getProperty( String name ) {
		synchronized(this) {
			String value = (String) m_properties.get( name );
			if( value == null && m_midlet != null ) {
				value = m_midlet.getAppProperty( name );
				if( value != null ) {
					m_properties.put( name, value );
				}
			}
			return value;
		}
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
				e.printStackTrace();
            }
        }
        return defaultValue;
    }
    
    /** Get long property */
    public long getLongProperty( String name, long defaultValue ) {
        String value = getProperty( name );
        if( value != null ) {
            try {
                return Long.parseLong( value );
            } catch( NumberFormatException e ) {
				//#ifdef DLOGGING
//@				logger.warning("Warning parsing long name,value=" + name + "," + value, e);
				//#else
				e.printStackTrace();
				//#endif
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
    private void load(int region)
    throws IOException, RecordStoreException {
		synchronized(this) {
			RecordStore rs = null;
			ByteArrayInputStream bin = null;
			DataInputStream din = null;
			
			m_valuesChanged = false;
			m_properties.clear();
			boolean currentSettings = true;
			int numRecs = 0;
			
			try {
				rs = RecordStore.openRecordStore("Store", true );
				numRecs = rs.getNumRecords();
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("region=" + region);}
//@				if (finestLoggable) {logger.finest("numRecs=" + numRecs);}
				//#endif
				//#ifdef DTEST
//@				System.out.println("region=" + region);
//@				System.out.println("numRecs=" + numRecs);
				//#endif
				if( numRecs == 0 ) {
					if (region == 0) {
						m_initialized = false;
						//#ifdef DLOGGING
//@						if (finestLoggable) {logger.finest("m_initialized=" + m_initialized);}
						//#endif
					}
				} else {
					if ( numRecs == OLD_MAX_REGIONS ) {
						currentSettings = false;
					}
					//#ifdef DLOGGING
//@					if (fineLoggable) {logger.fine("currentSettings=" + currentSettings);}
					//#endif
					byte[] data = rs.getRecord( region + 1 );
					if( data != null ) {
						bin = new ByteArrayInputStream( data );
						din = new DataInputStream( bin );
						int num = din.readInt();
						while( num-- > 0 ) {
							String name = din.readUTF();
							//#ifdef DLOGGING
//@							if (finestLoggable) {logger.finest("name=" + name);}
							//#endif
							String value;
							if (currentSettings) {
								final int blen = din.readInt();
								byte [] bvalue = new byte[blen];
								final int bvlen = din.read(bvalue);
								try {
									value = new String(bvalue, 0, bvlen, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									value = new String(bvalue, 0, bvlen);
									//#ifdef DLOGGING
//@									logger.severe("cannot convert load name=" + name, e);
									//#endif
									/** Error while executing constructor */
									System.out.println("cannot convert load name=" +
											name + e.getMessage());
									e.printStackTrace();
								} catch (IOException e) {
									value = new String(bvalue, 0, bvlen);
									//#ifdef DLOGGING
//@									logger.severe("cannot convert load name=" + name, e);
									//#endif
									/** Error while executing constructor */
									System.out.println("cannot convert load name=" +
											name + e.getMessage());
									e.printStackTrace();
								}
							} else {
								value = din.readUTF();
							}
							//#ifdef DLOGGING
//@							if (finestLoggable) {logger.finest("value=" + value);}
							//#endif
							m_properties.put( name, value );
						}
					}
				}
				for (int ic = numRecs; ic < MAX_REGIONS; ic++) {
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("adding ic=" + ic);}
					//#endif
					rs.addRecord( null, 0, 0 );
				}
				m_region = region;
			} catch (Exception e) {
				//#ifdef DLOGGING
//@				logger.severe("load ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("load " + e.getMessage());
				e.printStackTrace();
			} catch (Throwable e) {
				//#ifdef DLOGGING
//@				logger.severe("load throwable ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("load throwable " + e.getMessage());
				e.printStackTrace();
			} finally {
				if( din != null ) {
					/* Workaround for MicroEmulator. */
					try { ((InputStream)din).close();
					} catch( Exception e ){
						e.printStackTrace();
					}
				}
				
				if( rs != null ) {
					try { rs.closeRecordStore();
					} catch( Exception e ){
						e.printStackTrace();
					}
				}
				if (!currentSettings && ( numRecs > 0 ) && (region == 0)) {
					// If not current settings, save them to udate to
					// current.
					save(0, true);
					// Update bookmark region too.
					save(1, true);
				}
			}
		}
    }
    
    /** Save property Hashtable to record store.
        Use MAX_REGIONS records in store to help with running out of memory.  */
    public void save( int region, boolean force )
    throws IOException, RecordStoreException {
		synchronized(this) {
			if( !m_valuesChanged && !force ) return;
			
			RecordStore rs = null;
			ByteArrayOutputStream bout = new
					ByteArrayOutputStream();
			DataOutputStream dout = new
					DataOutputStream( bout );
			
			try {
				String vers = null;
				if ( m_properties.containsKey(SETTINGS_NAME) ) {
					vers = (String)m_properties.get(SETTINGS_NAME);
				}

				Hashtable cproperties = m_properties;
				//#ifndef DCOMPATIBILITY1
				//#ifndef DCOMPATIBILITY2
				//#ifndef DCOMPATIBILITY3
				if (region > 0) {
					cproperties = new Hashtable();
					cproperties.put("bookmarks", m_properties.get("bookmarks"));
					cproperties.put(SETTINGS_NAME,
							m_properties.get(SETTINGS_NAME));
					cproperties.put(ITEMS_ENCODED, m_properties.get(ITEMS_ENCODED));
					cproperties.put(STORE_DATE, m_properties.get(STORE_DATE));
				}
				//#endif
				//#endif
				//#endif

				// Put version only if it is not DCOMPATIBILITY1 which is
				// the settings based on the first few versions before
				// the setting store was changed as the first few
				// versions did not have settings version in properties.
				//#ifndef DCOMPATIBILITY1
				cproperties.put(SETTINGS_NAME, SETTINGS_VERS);
				//#endif
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("save region=" + region);}
				//#endif
				dout.writeInt( cproperties.size() );
				Enumeration e = cproperties.keys();
				while( e.hasMoreElements() ) {
					String name = (String) e.nextElement();
					String value = cproperties.get( name ).toString();
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("name=" + name);}
					//#endif
					dout.writeUTF( name );
					byte[] bvalue;
					try {
						bvalue = value.getBytes("UTF-8");

					} catch (UnsupportedEncodingException uee) {
						bvalue = value.getBytes();
						//#ifdef DLOGGING
//@						logger.severe("cannot convert save name=" + name, uee);
						//#endif
						/** Error while executing constructor */
						System.out.println("cannot convert save name=" +
								name + uee.getMessage());
						uee.printStackTrace();
					} catch (IOException ioe) {
						bvalue = value.getBytes();
						//#ifdef DLOGGING
//@						logger.severe("cannot convert save name=" + name, ioe);
						//#endif
						/** Error while executing constructor */
						System.out.println("cannot convert save name=" +
								name + ioe.getMessage());
						ioe.printStackTrace();
					}
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("value=" + value);}
					//#endif
					dout.writeInt( bvalue.length );
					dout.write( bvalue, 0, bvalue.length );
				}
				
				byte[] data = bout.toByteArray();
				
				rs = RecordStore.openRecordStore( "Store", true );
				rs.setRecord( (region + 1), data, 0, data.length );
				//#ifdef DLOGGING
//@				if (fineLoggable) {logger.fine("stored region=" + region);}
				//#endif
				//#ifndef DCOMPATIBILITY1
				if ( vers != null) {
					cproperties.put(SETTINGS_NAME, vers);
				}
				//#endif
			} catch (Exception e) {
				//#ifdef DLOGGING
//@				logger.severe("catch ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("catch " + e.getMessage());
				e.printStackTrace();
			} catch (Throwable e) {
				//#ifdef DLOGGING
//@				logger.severe("catch throwable ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("catch throwable " + e.getMessage());
				e.printStackTrace();
			} finally {
				try { dout.close();
				} catch( Exception e ){
					e.printStackTrace();
				}
				
				if( rs != null ) {
					try { rs.closeRecordStore();
					} catch( Exception e ){
						e.printStackTrace();
					}
				}
			}
		}
    }
    
    /** Get memory usage of the record store */
    public Hashtable getSettingMemInfo()
		throws IOException, RecordStoreException {
		synchronized(this) {
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
						try { rs.closeRecordStore();
						} catch( Exception e ){
							e.printStackTrace();
						}
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
    }
    
    /** Set a boolean property */
    public void setBooleanProperty( String name, boolean value ) {
        setStringProperty( name, value ? "true" : "false" );
    }
    
    /** Set an integer property */
    public void setIntProperty( String name, int value ) {
        setStringProperty( name, Integer.toString( value ) );
    }
    
    /** Set an long property */
    public void setLongProperty( String name, long value ) {
        setStringProperty( name, Long.toString( value ) );
    }
    
    /** Set a string property */
    public boolean setStringProperty( String name, String value ) {
		synchronized(this) {
			if( name == null && value == null ) return false;
			m_properties.put( name, value );
			m_valuesChanged = true;
			return true;
		}
    }

	/** Get properties size to allow us to know if it was from a load or not.
	  **/
	public boolean isInitialized() {
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("m_initialized=" + m_initialized);}
		//#endif
		return m_initialized;
	}

}
