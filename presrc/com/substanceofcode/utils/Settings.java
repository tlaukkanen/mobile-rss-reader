//--Need to modify--#preprocess
/*
 * Settings.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2007-2010 Irving Bunton, Jr
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
 * IB 2010-04-30 1.11.5RC2 Cosmetic change for compatibility.
 * IB 2010-05-25 1.11.5RC2 More logging.
 * IB 2010-05-26 1.11.5RC2 Use defaults for ITEMS_ENCODED and STORE_DATE of true and current date respectively.
 * IB 2010-05-31 1.11.5RC2 Give errors for load/store errors.
 * IB 2010-05-31 1.11.5RC2 Better logging including RecordStore log utility.
 * IB 2010-05-31 1.11.5RC2 If ITEMS_ENCODED or STORE_DATE not set, set it.
 * IB 2010-05-31 1.11.5RC2 Use constant for store name.
 * IB 2010-05-31 1.11.5RC2 Need to define test define for tests.
 * IB 2010-05-31 1.11.5RC2 When initializing the RMS, set record 0 and record 1 to their final initialization values instead of first setting them to null and then updating.  Don't make records with null as value.  This allows the emulator to sometimes work.
 * IB 2010-05-31 1.11.5RC2 Don't create null or empty records.
 * IB 2010-06-07 1.11.5RC2 Move synchronized to have lesser scope to prevent proguard preverfy failures.
 * IB 2010-06-27 1.11.5Dev2 Remove compatibility code from non test code.
 * IB 2010-06-27 1.11.5Dev2 If updating record and the record does not exist, add instead.
 * IB 2010-06-27 1.11.5Dev2 Use constant for bookmarks.
 * IB 2010-06-27 1.11.5Dev2 Use RECORD_STORE_NAME for store to match 1.12Alpha1.
 * IB 2010-06-27 1.11.5Dev2 Use volatile for m_store and m_properties
 * IB 2010-06-27 1.11.5Dev2 Use openRecStore to standardize openning of record store.  First open as existing if there are record stores.
 * IB 2010-06-27 1.11.5Dev2 Use closeStore to standardize closing of record store and giving error.
 * IB 2010-06-27 1.11.5Dev2 Better logging.
 * IB 2010-06-27 1.11.5Dev2 If we get a property that is not set, set it to default value in addition to returning the default value.
 * IB 2010-06-27 1.11.5Dev2 Have load return number of records in store.
 * IB 2010-06-27 1.11.5Dev2 Only read record if not null and length > 1.
 * IB 2010-06-27 1.11.5Dev2 If IOException when reading from store, make string without encoding.
 * IB 2010-06-27 1.11.5Dev2 Flush the save output buffer to make sure it's finished.
 * IB 2010-06-27 1.11.5Dev2 Change getSettingMemInfo to return int array to save memory and simplify.
 * IB 2010-06-27 1.11.5Dev2 Have deleteStore for testing deleting the store.
 * IB 2010-06-27 1.11.5Dev2 Have deleteSettings for testing deleting the settings to be used for reload.
 * IB 2010-06-27 1.11.5Dev2 Allow access to the initialization records.
 * IB 2010-06-27 1.11.5Dev2 Allow listRecordStores() for logging.
 * IB 2010-06-27 1.11.5Dev2 If loading, and the region is beyond existing records, set bookmarks to &quot;&quot;.
 * IB 2010-07-04 1.11.5Dev6 Return array from openRecStore to return record store and number of records.
 * IB 2010-07-04 1.11.5Dev6 Also return number of records from init.
 * IB 2010-07-04 1.11.5Dev6 Catch CauseRecStoreException instead of RecordStoreException since RecordStoreException is not thrown.
 * IB 2010-07-04 1.11.5Dev6 Don't return from witin a finally block.
 * IB 2010-07-05 1.11.5Dev6 Use null pattern using nullPtr.
 * IB 2010-07-29 1.11.5Dev8 If default set from get, set m_valuesChanged to true.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for Settings.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-19 1.11.5Dev14 Move static vars CFEED_SEPARATOR and OLD_FEED_SEPARATOR out of midlet class to Settings.
 * IB 2011-01-01 1.11.5Dev15 Change static vars to instance vars to reduce RMS save problems.
 * IB 2011-01-01 1.11.5Dev15 Make some globally used vars volatile.
 * IB 2011-01-01 1.11.5Dev15 Add trace logging.
 * IB 2011-01-01 1.11.5Dev15 Create loadRec to make loading more independent.
 * IB 2011-01-01 1.11.5Dev15 Future have loadRec return true if read/conversion error for value.
 * IB 2011-01-01 1.11.5Dev15 Future use return from saveRec which returns true if read/conversion error for value.
 * IB 2011-01-11 1.11.5Dev15 Make BOOKMARKS_NAME blank if record 2 (region 1) or greater.
 * IB 2011-01-11 1.11.5Dev15 Add RecordStoreException code to loadRec if cannot get a record.
 * IB 2011-01-11 1.11.5Dev15 Change logging of load value to trace.
 * IB 2011-01-11 1.11.5Dev15 Have deleteStore create settings if it does not exist since the vars are no longer static, need to use settings instance to access instance vars to delete store.
 * IB 2011-01-24 1.11.5Dev16 For internet link version, settings is used only by SettingsForm.  Don't have it be a singleton to reduce static methods.
 * IB 2011-03-06 1.11.5Dev17 Specify imports without '*'.
 */

// Expand to define CLDC define
@DCLDCVERS@
// Expand to define DFULLVERS define
@DFULLVERSDEF@
// Expand to define DINTLINK define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import com.substanceofcode.utils.CauseException;
import com.substanceofcode.utils.CauseRecStoreException;
import com.substanceofcode.rssreader.presentation.FeatureMgr;

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
final public class Settings {
    
	final Object nullPtr = null;
    public final int OLD_MAX_REGIONS = 1;
    public final int MAX_REGIONS = 15;
    public final String SETTINGS_NAME = "RssReader-setttings-vers";
    public final String BOOKMARKS_NAME = "bookmarks";
    final public char CFEED_SEPARATOR = (char)4;
    final public char OLD_FEED_SEPARATOR = '^';
	// The first settings did not have a version, so it ends up being
	// "" by default
    public final String RECORD_STORE_NAME = "Store";
    public final String FIRST_SETTINGS_VERS = "";
    public final String ITUNES_CAPABLE_VERS = "3";
    public final String ENCODING_VERS = "4";
    public final String MODIFIED_VERS = "5";
    public final String ITEMS_ENCODED = "items-encoded";
    public final String STORE_DATE = "store-date";
    public final String SETTINGS_VERS = MODIFIED_VERS;
    volatile private static Settings m_store;
    volatile private boolean         m_valuesChanged = false;
    volatile private boolean         m_initialized = true;
    volatile private int             m_initRecs = 0;
    volatile private boolean         m_firstLoad = true;
    private Hashtable       m_properties = new Hashtable();
    volatile private int             m_region;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("Settings");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif
    
    /**
     * Singleton pattern is used to return 
     * only one instance of record store
     */
	//#ifdef DCLDCV11
    public static Settings getInstance()
	//#else
	//#ifdef DFULLVERS
    public static synchronized Settings getInstance()
	//#else
	//#ifdef DINTLINK
	// For internet link version, we only call this once.  We don't need
	// synchronized which might have some problems in some KVMs.
    public Settings getInstance()
	//#endif
	// End DINTLINK
	//#endif
	// End DFULLVERS
	//#endif
	// End DCLDCV11
    throws IOException, CauseRecStoreException, CauseException {
		//#ifdef DCLDCV11
		synchronized(Settings.class) {
		//#endif
			if( m_store == null ) {
				m_store = new Settings();
			}
			return m_store;
		//#ifdef DCLDCV11
        }
		//#endif
    }

    /** Constructor */
	//#ifdef DFULLVERS
    private Settings()
	//#else
    public Settings()
	//#endif
    throws IOException, CauseRecStoreException, CauseException {
        load(0);
    }

	private Object[] openRecStore(boolean createIfNecessary)
	throws CauseRecStoreException {
		int numRecs = 0;
		String[] listStores = RecordStore.listRecordStores();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("openRecStore listStores=" + listStores);}
		if (fineLoggable) {listRecordStores();}
		//#endif
		RecordStore	rs = null;
		if (createIfNecessary && (listStores != null) &&
				(listStores.length > 0)) {
			try {
				Object[] openObjs = openRecStore(false);
				if (openObjs != null) {
					return openObjs;
				}
			} catch (CauseRecStoreException e) {
				return null;
			}
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("openRecStore createIfNecessary,rs,rs.getNumRecords()=" + createIfNecessary + "," + rs + "," + getStoreInfo(rs));}
			//#endif
		}
		try {
		rs = RecordStore.openRecordStore(RECORD_STORE_NAME, createIfNecessary);
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("openRecStore createIfNecessary,rs,rs.getNumRecords()=" + createIfNecessary + "," + rs + "," + getStoreInfo(rs));}
		//#endif
		numRecs = rs.getNumRecords();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("openRecStore numRecs=" + numRecs);}
		//#endif
		//#ifdef DTEST
		System.out.println("numRecs=" + numRecs);
		//#endif
		} catch (RecordStoreException e) {
			CauseRecStoreException ce = new CauseRecStoreException(
					"Open/access record store error for " + RECORD_STORE_NAME,
					e);

			//#ifdef DLOGGING
			logger.severe(ce.getMessage(), ce);
			//#endif
			throw ce;
		}
		return new Object[] {rs, new Integer(numRecs)};
	}
    
	private Object[] init()
    throws IOException, CauseRecStoreException, CauseException {
		boolean currentSettings = true;
		RecordStore rs = null;
		int numRecs = 0;
		int recix = 0;
		
		try {
			Object[] openObjs = openRecStore(true);
			rs = (RecordStore)openObjs[0];
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("init openRecStore rs info=" + rs + "," + getStoreInfo(rs));}
			//#endif
			numRecs = ((Integer)openObjs[1]).intValue();
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("init numRecs=" + numRecs);}
			//#endif
			if( numRecs == 0 ) {
				synchronized(this) {
					m_initialized = false;
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("init m_initialized=" + m_initialized);}
					//#endif
				}
			} else {
				if ( numRecs == OLD_MAX_REGIONS ) {
					currentSettings = false;
				}
			}
			if (numRecs <= 1) {
				if (numRecs == 0) {
					saveRec(rs, 0, true);
					++numRecs;
				}
				if (numRecs == 1) {
					saveRec(rs, 1, true);
					++numRecs;
				}
				if (!m_initialized) {
					m_initRecs = numRecs;
				}
			}
		} catch (CauseRecStoreException e) {
			//#ifdef DLOGGING
			logger.severe("init ", e);
			//#endif
			/** Error while executing constructor */
			System.out.println("init " + e.getMessage());
			e.printStackTrace();
			throw new CauseRecStoreException(
					"RecordStoreException while loading record index=" +
					recix, e);
		} catch (Exception e) {
			//#ifdef DLOGGING
			logger.severe("init ", e);
			//#endif
			/** Error while executing constructor */
			System.out.println("init " + e.getMessage());
			e.printStackTrace();
			throw new CauseException("Internal error while initializing recix=" +
					recix, e);
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("init throwable ", e);
			//#endif
			/** Error while executing constructor */
			System.out.println("init throwable " + e.getMessage());
			e.printStackTrace();
			throw new CauseException("Internal error while loading " +
					"recix=" + recix, e);
		}
		return new Object[] {rs, new Integer(numRecs), new Boolean(currentSettings)};
	}

	private RecordStore closeStore(RecordStore rs) {
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("closeStore rs,rsinfo=" + rs + "," + getStoreInfo(rs));}
		//#endif
		if( rs == null ) {
			return null;
		} else {
			try {
				rs.closeRecordStore();
			} catch( Throwable e ) {
				//#ifdef DLOGGING
				logger.severe("closeStore cannot close store=" +
						RECORD_STORE_NAME, e);
				//#endif
				e.printStackTrace();
			}
			return null;
		}
	}

    /** Return true if value exists in record store */
    public boolean exists( String name ) {
        return getProperty( name ) != null;
    }
    
    /** Get property from Hashtable*/
    private String getProperty( String name ) {
		synchronized(this) {
			String value = (String) m_properties.get( name );
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("getProperty 1 m_region,m_properties.size(),name,value=" + m_region + "," + m_properties.size() + "," + name + "," + value);}
			//#endif
			if (value == null) {
				MIDlet midlet = FeatureMgr.getMidlet();
				if (midlet != null) {
					value = midlet.getAppProperty( name );
					if( value != null ) {
						m_properties.put( name, value );
						m_valuesChanged = true;
					}
				}
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("getProperty 2 m_region,name,value=" + m_region + "," + name + "," + value);}
			//#endif
			return value;
		}
    }
    
    /** Get boolean property */
    public boolean getBooleanProperty( String name, boolean defaultValue) {
        String value = getProperty( name );
        if( value != null ) {
            return value.equals( "true" ) || value.equals( "1" );
        }
		setBooleanProperty( name, defaultValue );
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
		setIntProperty( name, defaultValue );
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
				logger.warning("Warning parsing long name,value=" + name + "," + value, e);
				//#else
				e.printStackTrace();
				//#endif
            }
        }
		setLongProperty( name, defaultValue );
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
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("getStringProperty region,m_region,name,value=" + region + "," + m_region + "," + name + "," + value);}
		//#endif
		String rtnValue = ( value != null ) ? value.toString() : defaultValue;
		if (value == null) {
			setStringProperty( name, rtnValue );
		}
        return rtnValue;
    }
    
    /** Load properties from record store */
    private int load(int region)
    throws IOException, CauseRecStoreException, CauseException {
		//#ifdef DLOGGING
		logger.info("load region=" + region);
		//#endif
		int numRecs = 0;
		RecordStore rs = null;
		synchronized(this) {
			
			m_valuesChanged = false;
			m_properties.clear();
			boolean currentSettings;
			if (m_firstLoad) {
				m_firstLoad = false;
				Object[] res = init();
				rs = (RecordStore)res[0];
				numRecs = ((Integer)res[1]).intValue();
				currentSettings = ((Boolean)res[2]).booleanValue();
			} else {
				currentSettings = true;
			}
			
			try {
				if (rs == null) {
					Object[] openObjs = openRecStore(true);
					rs = (RecordStore)openObjs[0];
					numRecs = ((Integer)openObjs[1]).intValue();
				}

				//#ifdef DLOGGING
				logger.info("load region=" + region);
				logger.info("load rs=" + rs);
				//#endif
				//#ifdef DLOGGING
				logger.info("load numRecs=" + numRecs);
				//#endif
				//#ifdef DTEST
				System.out.println("load rs=" + rs);
				System.out.println("load region=" + region);
				System.out.println("load numRecs=" + numRecs);
				//#endif
				if( numRecs != 0 ) {
					//#ifdef DLOGGING
					if (fineLoggable) {logger.fine("load currentSettings=" + currentSettings);}
					//#endif
					if (region >= numRecs) {
						m_properties.put(BOOKMARKS_NAME, "");
					} else {
						loadRec( rs, region, currentSettings, m_properties );
					}
				}
				
			} finally {
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("load rs,rs.info=" + rs + "," + getStoreInfo(rs));}
				//#endif
				if( rs != null ) {
					try {
						numRecs = rs.getNumRecords();
					} catch (RecordStoreNotOpenException rsnoe) {
						//#ifdef DLOGGING
						logger.severe("load cannot getNumRecords", rsnoe);
						//#endif
						rsnoe.printStackTrace();
					}
				}
				if ((!currentSettings || !m_initialized) && ( numRecs > 0 ) &&
						(region == 0)) {
					// If not current settings, save them to udate to
					// current.
					saveRec(rs, 0, true);
					if (numRecs == 0) {
						++numRecs;
					}
					// Update bookmark region too.
					saveRec(rs, 1, true);
					if (numRecs == 1) {
						++numRecs;
					}
				}
				if (rs != null) {
					closeStore(rs);
				}
			}
			return numRecs;
		}
    }
    
	// todo change "load"
    public boolean loadRec(RecordStore rs, int region, boolean currentSettings,
			Hashtable properties  )
    throws IOException, CauseRecStoreException, CauseException {
		synchronized(this) {
			byte[] data = null;
			try {
				data = rs.getRecord( region + 1 );
			} catch (InvalidRecordIDException irie) {
				//#ifdef DLOGGING
				logger.warning("load cannot get record region=" + region, irie);
				//#endif
				if (region >= 1) {
					properties.put(BOOKMARKS_NAME, "");
				}
				irie.printStackTrace();
			} catch (RecordStoreException e) {
				//#ifdef DLOGGING
				logger.severe("load ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("load " + e.getMessage());
				e.printStackTrace();
				throw new CauseRecStoreException(
						"RecordStoreException while loading region=" +
						region, e);
			}
			ByteArrayInputStream bin = null;
			DataInputStream din = null;
			boolean readError = false;
			boolean convError = false;
			try {
				if( (data != null) && (data.length > 1) ) {
					bin = new ByteArrayInputStream( data );
					din = new DataInputStream( bin );
					int num = din.readInt();
					while( num-- > 0 ) {
						String name = din.readUTF();
						//#ifdef DLOGGING
						if (finestLoggable) {logger.finest("load name=" + name);}
						//#endif
						String value;
						if (currentSettings) {
							final int blen = din.readInt();
							//#ifdef DLOGGING
							if (finestLoggable) {logger.finest("load blen=" + blen);}
							//#endif
							if (blen == 0) {
								value = "";
							} else {
								byte [] bvalue = new byte[blen];
								final int bvlen = din.read(bvalue);
								try {
									value = new String(bvalue, 0, bvlen, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									//#ifdef DLOGGING
									logger.severe("load cannot convert load name=" + name, e);
									//#endif
									/** Error while executing constructor */
									System.out.println("load cannot convert load name=" +
											name + e.getMessage());
									e.printStackTrace();
									value = new String(bvalue, 0, bvlen);
								} catch (IOException e) {
									//#ifdef DLOGGING
									logger.severe("load cannot convert load name=" + name, e);
									//#endif
									/** Error while executing constructor */
									System.out.println("load cannot convert load name=" +
											name + e.getMessage());
									e.printStackTrace();
									value = new String(bvalue, 0, bvlen);
								}
							}
						} else {
							value = din.readUTF();
						}
						//#ifdef DLOGGING
						if (traceLoggable) {logger.trace("load value=" + value);}
						//#endif
						properties.put( name, value );
					}
				}
				m_region = region;
			} catch (Exception e) {
				//#ifdef DLOGGING
				logger.severe("load ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("load " + e.getMessage());
				e.printStackTrace();
				throw new CauseException("Internal error while loading region=" +
						region, e);
			} catch (Throwable e) {
				//#ifdef DLOGGING
				logger.severe("load throwable ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("load throwable " + e.getMessage());
				e.printStackTrace();
				throw new CauseException("Internal error while loading " +
						"region=" + region, e);
			} finally {
				/* Workaround for MicroEmulator. */
				din = (DataInputStream)MiscUtil.closeInputStream(din);
				/* Workaround for MicroEmulator. */
				bin = (ByteArrayInputStream)MiscUtil.closeInputStream(bin);
			}
			return readError || convError;
		}
	}

    /** Save property Hashtable to record store.
        Use MAX_REGIONS records in store to help with running out of memory.  */
    public void save( int region, boolean force )
	throws IOException, CauseRecStoreException, CauseException {
		synchronized(this) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("save region,force,m_valuesChanged=" + region + "," + force + "," + m_valuesChanged);}
			//#endif
			RecordStore rs = null;
			try {
				Object[] openObjs = openRecStore(true);
				rs = (RecordStore)openObjs[0];
				saveRec( rs, region, force );
			} finally {
				
				if( rs != null ) {
					rs = closeStore(rs);
				}
			}
		}
	}

    public boolean saveRec(RecordStore rs, int region, boolean force )
	throws IOException, CauseRecStoreException, CauseException {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("saveRec rs,rs.info,region,force,m_valuesChanged=" + rs + "," + getStoreInfo(rs) + "," + region + "," + force + "," + m_valuesChanged);}
		//#endif

		synchronized(this) {
			if( !m_valuesChanged && !force ) return false;
			
			boolean convError = false;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream( bout );
			
			try {
				String vers = (String)m_properties.get(SETTINGS_NAME);
				if ( vers == null ) {
					vers = SETTINGS_VERS;
					m_properties.put(SETTINGS_NAME, vers);
				}

				String value;
				if ((value = (String)m_properties.get(BOOKMARKS_NAME)) == null) {
					value = "";
					m_properties.put(BOOKMARKS_NAME, value);
				}
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest("save put bookmarks=" + value);}
				//#endif
				Hashtable cproperties = m_properties;
				if (region > 0) {
					cproperties = new Hashtable();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("save put bookmarks=" + m_properties.get(BOOKMARKS_NAME));}
					//#endif
					cproperties.put(BOOKMARKS_NAME, value);
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("save put settings=" + vers);}
					//#endif
					cproperties.put(SETTINGS_NAME, vers);
					if ((value = (String)m_properties.get(ITEMS_ENCODED)) == null) {
						value = "true";
						m_properties.put(ITEMS_ENCODED, value);
					}
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("save put items_encoded=" + value);}
					//#endif
					cproperties.put(ITEMS_ENCODED, value);
					if ((value = (String)m_properties.get(STORE_DATE)) == null) {
						value = Long.toString(System.currentTimeMillis());
						m_properties.put(STORE_DATE, value);
					}
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("save put store_date=" + value);}
					//#endif
					cproperties.put(STORE_DATE, value);
				}

				// Put version.
				cproperties.put(SETTINGS_NAME, SETTINGS_VERS);
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("save region,cproperties.size()=" + region + "," + cproperties.size());}
				//#endif
				dout.writeInt( cproperties.size() );
				Enumeration e = cproperties.keys();
				while( e.hasMoreElements() ) {
					String name = (String) e.nextElement();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("save name=" + name);}
					//#endif
					dout.writeUTF( name );
					value = cproperties.get(name).toString();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("save value=" + value);}
					//#endif
					byte[] bvalue;
					try {
						bvalue = value.getBytes("UTF-8");

					} catch (UnsupportedEncodingException uee) {
						//#ifdef DLOGGING
						logger.severe("save cannot convert save name=" + name, uee);
						//#endif
						/** Error while executing constructor */
						System.out.println("save cannot convert save name=" +
								name + uee.getMessage());
						uee.printStackTrace();
						bvalue = value.getBytes();
					} catch (IOException ioe) {
						//#ifdef DLOGGING
						logger.severe("save cannot convert save name=" + name, ioe);
						//#endif
						if (!convError) {
							convError = true;
						}
						/** Error while executing constructor */
						System.out.println("save cannot convert save name=" +
								name + ioe.getMessage());
						ioe.printStackTrace();
						bvalue = value.getBytes();
					}
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest("save bvalue=" + bvalue);}
					//#endif
					dout.writeInt( bvalue.length );
					dout.write( bvalue, 0, bvalue.length );
				}
				
				try {
					dout.flush();
				} catch( Exception de ){
					de.printStackTrace();
				}
				byte[] data = bout.toByteArray();
				
				try {
					rs.setRecord( (region + 1), data, 0, data.length );
				} catch (InvalidRecordIDException irie) {
					rs.addRecord( data, 0, data.length );
				}
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("save stored region=" + region);}
				//#endif
				if ( vers != null) {
					cproperties.put(SETTINGS_NAME, vers);
				}
			} catch (RecordStoreFullException e) {
				//#ifdef DLOGGING
				logger.severe("catch RecordStoreFullException ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("catch RecordStoreFullException " +
								   e.getMessage());
				e.printStackTrace();
				throw new CauseRecStoreException(
						"RecordStoreFullException while saving.", e);
			} catch (RecordStoreException e) {
				//#ifdef DLOGGING
				logger.severe("catch RecordStoreException ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("catch RecordStoreException " +
								   e.getMessage());
				e.printStackTrace();
				throw new CauseRecStoreException(
						"RecordStoreException while saving.", e);
			} catch (Exception e) {
				//#ifdef DLOGGING
				logger.severe("catch ", e);
				//#endif
				/** Error while executing constructor */
				System.out.println("catch " + e.getMessage());
				e.printStackTrace();
				throw new CauseException("Internal error during save.", e);
			} catch (Throwable e) {
				CauseException ce = new CauseException("Internal error while " +
						"processing save.", e);
				//#ifdef DLOGGING
				logger.severe(ce.getMessage(), ce);
				//#endif
				/** Error while executing constructor */
				System.out.println(ce.getMessage() + e.getMessage());
				e.printStackTrace();
				throw ce;
			} finally {
				/* Workaround for MicroEmulator. */
				dout = (DataOutputStream)MiscUtil.closeOutputStream(dout);
				/* Workaround for MicroEmulator. */
				bout = (ByteArrayOutputStream)MiscUtil.closeOutputStream(bout);
			}
			return convError;
		}
    }
    
    /** Get memory usage of the record store */
    public int[] getSettingMemInfo()
		throws IOException, RecordStoreException {
	try {
			
				RecordStore rs = null;
				int[] memInfo = null;
				
					try {
						
						synchronized(this) {
							Object[] openObjs = openRecStore(false);
							if (openObjs == null) {
								return new int[0];
							} else {
								rs = (RecordStore)openObjs[0];
							}
							memInfo = new int[2];
							memInfo[0] = rs.getSize();
							memInfo[1] = rs.getSizeAvailable();
						}
						return memInfo;
					} finally {
						rs = closeStore(rs);
					}
			} catch (CauseException re) {
				return new int[0];
			} catch (Exception e) {
				System.out.println("Error in getSettingMemInfo()");
				e.printStackTrace();
				return new int[0];
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
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("setStringProperty name,value=" + name + "," + value);}
		//#endif
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
		if (finestLoggable) {logger.finest("isInitialized m_initialized=" + m_initialized);}
		//#endif
		return m_initialized;
	}

	//#ifdef DTEST
	final static public void deleteStore() {
		Settings csettings;
		if (m_store != null) {
			csettings = m_store;
		} else {
			try {
				csettings = new Settings();
			} catch (Throwable e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			//#ifdef DLOGGING
			Logger.getLogger("Settings").trace("deleteStore deleting " +
					csettings.RECORD_STORE_NAME);
			//#endif
			RecordStore.deleteRecordStore( csettings.RECORD_STORE_NAME );
		} catch (RecordStoreNotFoundException e) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("Settings");
			logger.warning("Cannot deleteRecordStore " +
					"RecordStoreNotFound " + csettings.RECORD_STORE_NAME);
			//#endif
		} catch (RecordStoreException e) {
			//#ifdef DLOGGING
			Logger logger = Logger.getLogger("Settings");
			logger.severe("Cannot deleteRecordStore RecordStoreException " +
					csettings.RECORD_STORE_NAME, e);
			//#endif
		}
	}

	final static public void deleteSettings() {
		if (m_store != null) {
			m_store.m_valuesChanged = false;
			m_store.m_initialized = true;
			m_store.m_initRecs = 0;
			m_store.m_firstLoad = true;
			m_store.m_properties = new Hashtable();
			m_store = null;
		}
	}
	//#endif
    
	//#ifdef DLOGGING
	public String getStoreInfo(RecordStore rs) {
		if (rs == null) {
			return "null";
		} else {
			try {
				return ((rs == null) ? "null" : (rs.getNumRecords() + "," + rs.getSize() + "," + rs.getSizeAvailable()));
			} catch (RecordStoreNotOpenException e) {
				return "RecordStoreNotOpenException";
			}
		}
	}

	final static public void listRecordStores() {
		String[] rss = RecordStore.listRecordStores();
		Logger logger = Logger.getLogger("Settings");
		if (rss == null) {
			logger.fine("listRecordStores rss=" + rss);
			return;
		}
		logger.fine("listRecordStores rss.length=" + rss.length);
		for (int i = 0; i < rss.length; i++) {
			logger.fine("listRecordStores rss[i]=" + i + "," + rss[i]);
		}
	}
	//#endif

    public int getInitRecs() {
        return (m_initRecs);
    }

}
