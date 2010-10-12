//--Need to modify--#preprocess
/*
 * MiscUtil.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Copyright (C) 2010 Irving Bunton, Jr
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
 * IB 2010-04-30 1.11.5RC2 Track threads used.
 * IB 2010-04-30 1.11.5RC2 Combine classes to save space.
 * IB 2010-04-30 1.11.5RC2 Have toString classes for easier logging.
 * IB 2010-05-24 1.11.5RC2 Use convenience method for encoding/decoding.
 * IB 2010-05-24 1.11.5RC2 Replace SortUtil with LGPL code to allow adding of LGPL license.
 * IB 2010-05-28 1.11.5RC2 Use threads and CmdReceiver for MIDP 2.0 only.
 * IB 2010-05-30 1.11.5RC2 More logging.
 * IB 2010-07-04 1.11.5Dev6 Don't use toString for appending booleans and integers with StringBuffer as there is already an append method for this.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern using nullPtr.
 * IB 2010-07-04 1.11.5Dev6 Code cleanup.
 * IB 2010-08-15 1.11.5Dev8 Add runnable for thread class name to thread info.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.utils;

//TODO test </a> html. test no http (or using base?)
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

//#ifdef DMIDP20
import com.substanceofcode.utils.CmdReceiver;
//#endif
//#ifdef DTEST
import com.substanceofcode.rssreader.businessentities.RssItemInfo;
import com.substanceofcode.rssreader.businessentities.RssItunesItemInfo;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi
 */
public class MiscUtil {
    

	final static Object nullPtr = null;
    private final static String breakTags = "<p><br><td><p/><br/><td/>";

    private static final char[] legalChars =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();

	//#ifdef DMIDP20
	public final static Short SPAUSE_APP = new Short((short)0);
	static private final Hashtable cthreads = new Hashtable();
	//#endif
    static private long threadNbr = 1L;
    static final private int MAX_STR_DISP = 50;

	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("MiscUtil");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Creates a new instance of MiscUtil */
    private MiscUtil() {
    }
    
    synchronized public static String getThreadName(String name) {
		return name + threadNbr++;
	}

	static public
	//#ifdef DCLDCV10
	synchronized
	//#endif
			Thread getThread(Runnable runnable, String name, String called) {
		//#ifdef DCLDCV11
		synchronized(MiscUtil.class) {
		//#endif
			String cname = getThreadName(name);
			//#ifdef DCLDCV11
			Thread thread = new Thread(runnable, getThreadName(name));
			//#else
			Thread thread = new Thread(runnable);
			//#endif
			//#ifdef DMIDP20
			cthreads.put(thread, new Object[] {cname, runnable});
			//#endif
		//#ifdef DCLDCV11
		}
		//#endif
		return thread;
	}

	static public Thread getThread(Runnable runnable, String name, Object obj,
			String called) {
		return MiscUtil.getThread(runnable, name,
				obj.getClass().getName() + "@" + called);
	}

	static public
	//#ifdef DCLDCV10
	synchronized
	//#endif
	String getThreadInfo(Thread thread) {
		if (thread == null) {
			return "Null Thread Parameter";
		}
		//#ifdef DCLDCV11
		synchronized(MiscUtil.class) {
		//#endif
			StringBuffer tsb = new StringBuffer("Thread ");
			//#ifdef DMIDP10
			return tsb.append(thread.hashCode()).append(",").append(
					thread.toString()).toString();
			//#else
			Object[] threadObjs;
			if ((threadObjs = (Object[])cthreads.get(thread)) == null) {
				return tsb.append(thread.hashCode()).append(
						" not in thread list.").toString();
			} else {
				String threadInfo = (String)threadObjs[0];
				String runnable;
				try {
					runnable = threadObjs[1].getClass().getName() + " ";
				} catch (Throwable e) {
					runnable = " ";
				}
				return tsb.append("#").append(thread.toString()).append("@").append(
						threadInfo).append(" ").append(runnable).append(
						" ").append(thread.isAlive()).toString();
			}
			//#endif
		//#ifdef DCLDCV11
		}
		//#endif
	}

	//#ifdef DMIDP20
	static public
	//#ifdef DCLDCV10
	synchronized
	//#endif
			String[] getDispThreads() {
		//#ifdef DCLDCV11
		synchronized(MiscUtil.class) {
		//#endif
			String[] sthreads = new String[cthreads.size()];
			Enumeration ethreads = cthreads.keys();
			for (int ic = 0;ethreads.hasMoreElements(); ic++) {
				Thread thread = (Thread)ethreads.nextElement();
				sthreads[ic] = getThreadInfo(thread);
			}
			return sthreads;
		//#ifdef DCLDCV11
		}
		//#endif
	}

	public static
	//#ifdef DCLDCV10
	synchronized
	//#endif
    Thread removeThread(Thread cthread) {
		if (cthread == null) {
			return null;
		}
		//#ifdef DCLDCV11
		synchronized(MiscUtil.class) {
		//#endif
			if (cthreads.containsKey(cthread)) {
				cthreads.remove(cthread);
				return cthread;
			} else {
				return null;
			}
		//#ifdef DCLDCV11
		}
		return cthread;
		//#endif
	}
	//#endif

    /** Base 64 encode the given data */
    static public String encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);

        int end = len - 3;
        int i = start;
        int n = 0;

        while (i <= end) {
            int d =
                ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 0x0ff) << 8)
                    | (((int) data[i + 2]) & 0x0ff);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append(legalChars[d & 63]);

            i += 3;

            if (n++ >= 14) {
                n = 0;
                buf.append("\r\n");
            }
        }

        if (i == start + len - 2) {
            int d =
                ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 255) << 8);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append("=");
        }
        else if (i == start + len - 1) {
            int d = (((int) data[i]) & 0x0ff) << 16;

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    static public String encodeStr(String preData) {
        String encodedSerializedData = null;
		try {
			encodedSerializedData = MiscUtil.encode( preData.getBytes("UTF-8") );
		} catch (UnsupportedEncodingException e) {
			encodedSerializedData = MiscUtil.encode( preData.getBytes() );
		}
		//#ifdef DLOGGING
		Logger.getLogger("MiscUtil").trace("encodeStr encodedSerializedData=" + encodedSerializedData);
		//#endif
		return encodedSerializedData;
	}
		
    static private int decode(char c) {
        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else
            switch (c) {
                case '+' :
                    return 62;
                case '/' :
                    return 63;
                case '=' :
                    return 0;
                default :
                    throw new RuntimeException(
                        "unexpected code: " + c);
            }
    }

    /** Decodes the given Base 64 encoded String to a new byte array. 
    The byte array holding the decoded data is returned. */

    static public byte[] decode(String s) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decode(s, bos);
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
			// Save memory
            bos = (ByteArrayOutputStream)nullPtr;
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE 64: " + ex.toString());
        }
        return decodedBytes;
    }

    static private void decode(String s, OutputStream os)
        throws IOException {
        int i = 0;

        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= ' ') {
                i++;
			}

            if (i == len)
                break;

            int tri =
                (decode(s.charAt(i)) << 18)
                    + (decode(s.charAt(i + 1)) << 12)
                    + (decode(s.charAt(i + 2)) << 6)
                    + (decode(s.charAt(i + 3)));

            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);

            i += 4;
        }
    }

	/** Deserialize the object */
	public static String decodeStr(String data) {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("MiscUtil");
		boolean traceLoggable = logger.isLoggable(Level.TRACE);
		if (traceLoggable) {logger.trace("decodeStr data=" + data);}
		//#endif
		// Base 64 decode
		byte[] decodedData = MiscUtil.decode(data);
		try {
			data = new String( decodedData, "UTF-8" );
		} catch (UnsupportedEncodingException e) {
			data = new String( decodedData );
		}
		//#ifdef DLOGGING
		if (traceLoggable) {logger.trace("decodeStr 2 data=" + data);}
		//#endif
		return data;
		
	}
			
    /**
     * Split string into multiple strings
     * @param original      Original string
     * @param separator     Separator string in original string
     * @return              Splitted string array
     */
    public static String[] split(String original, String separator) {
        Vector nodes = new Vector();
        
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while(index>=0) {
            nodes.addElement( original.substring(0, index) );
            original = original.substring(index+separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement( original );
        
        // Create splitted string array
		int nsize = nodes.size();
        String[] result = new String[ nsize ];
        if( nsize >0 ) {
			nodes.copyInto(result);
        }
        return result;
    }
    
    /**
     * Join strings into one string
     * @param originals      Original strings
     * @param joinStr        Join string
     * @param index          Index to start at
     * @return               Joined string
     */
    public static String join(String[] originals, String joinStr, int index) {
        if (originals == null)  return null;
        if (joinStr == null)  joinStr = "";
        StringBuffer sb = new StringBuffer(originals[index]);
        
        // Parse nodes into vector
        for (int ic = index + 1; ic < originals.length; ic++) {
            sb.append( joinStr + originals[ic] );
        }
        return sb.toString();
    }
    
    /* Replace all instances of a String in a String.
     *   @param  s  String to alter.
     *   @param  f  String to look for.
     *   @param  r  String to replace it with, or null to just remove it.
     */
    public static String replace( String s, String f, String r ) {
        if (s == null)  return s;
        if (f == null)  return s;
        if (r == null)  r = "";
        
        int index01 = s.indexOf( f );
        while (index01 != -1) {
            s = s.substring(0,index01) + r + s.substring(index01+f.length());
            index01 += r.length();
            index01 = s.indexOf( f, index01 );
        }
        return s;
    }
    
    /** 
     * Method removes HTML tags from given string.
     * 
     * @param text  Input parameter containing HTML tags (eg. <b>cat</b>)
     * @return      String without HTML tags (eg. cat)
     */
    public static String removeHtml(String text) {
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("MiscUtil");
		boolean finerLoggable = logger.isLoggable(Level.FINER);
		//#endif
        try{
			if (text == null) { return null; }
            String htmlText = text.trim();
            int htmlStartIndex = htmlText.indexOf('<');
			if (htmlStartIndex == -1) { return text; }
            StringBuffer plainText = new StringBuffer();
            while (htmlStartIndex>=0) {
                plainText.append(htmlText.substring(0,htmlStartIndex));
                int htmlEndIndex = htmlText.indexOf('>', htmlStartIndex);
				// If we have unmatched '<' without '>' stop or we
				// get into infinate loop.
                if (htmlEndIndex < 0) {
					//#ifdef DLOGGING
					if (finerLoggable) {logger.finer("No end > for htmlStartIndex,htmlText=" + htmlStartIndex + "," + htmlText);}
					if (finerLoggable) {logger.finer("plainText=" + plainText);}
					//#endif
					return plainText.toString().trim();
				}
                final int html1stSpaceIndex = htmlText.indexOf(' ',
						htmlStartIndex);
                int htmlTagEndIndex;
                int startTagLen;
                int tagLen;
				if ((html1stSpaceIndex > 0) &&
						(htmlEndIndex > html1stSpaceIndex)) {
					startTagLen = html1stSpaceIndex - htmlStartIndex;
					htmlTagEndIndex = htmlText.lastIndexOf(' ',
						htmlEndIndex) + 1;
					tagLen = startTagLen + htmlEndIndex - htmlTagEndIndex + 1;
				} else {
					startTagLen = htmlEndIndex - htmlStartIndex;
					htmlTagEndIndex = htmlEndIndex;
					tagLen = startTagLen + 1;
				}
				try {
					if ((3 <= tagLen) && (tagLen <= 5)) {
						final String tag = htmlText.substring(htmlStartIndex,
								htmlStartIndex + startTagLen) +
							htmlText.substring(htmlTagEndIndex,
									htmlEndIndex + 1);
						if (breakTags.indexOf(tag) >= 0) {
							plainText.append(" ");
						}
					}
				} catch(Exception e) {
					CauseException ce = new CauseException("Error while removing HTML and break: " +
									   e.getClass().getName() + " " + e.toString());
					//#ifdef DLOGGING
					logger.severe(e.getMessage(), ce);
					//#endif
					System.err.println(ce.getMessage());
					e.printStackTrace();
				}
                htmlText = htmlText.substring(htmlEndIndex+1);
                htmlStartIndex = htmlText.indexOf('<');
            }
			plainText.append(htmlText);
            return plainText.toString().trim();
        } catch(Exception e) {
			CauseException ce = new CauseException("Error while removing HTML: " +
							   e.getClass().getName() + " " + e.toString());
			//#ifdef DLOGGING
			logger.severe(e.getMessage(), ce);
			//#endif
			System.err.println(ce.getMessage());
			e.printStackTrace();
            return text;
        }
    }
    
	static public String toString(Object obj, boolean showClass, int maxStrLen) {
		if (obj == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer(
			((showClass) ? obj.getClass().getName() : ""));
		sb.append(",");
		if (maxStrLen > 0) {
			if (obj.toString().length() < maxStrLen) {
				sb.append(obj.toString());
			} else {
				sb.append(obj.toString().substring(0, maxStrLen - 1));
			}
		}
		return sb.toString();
	}

	static public String toString(Object obj, boolean showClass) {
		return toString(obj, showClass, MAX_STR_DISP);
	}

	//#ifdef DLOGGING
	static public String toString(RssItemInfo item, boolean showClass) {
		if (item == null) {
			return "null";
		}
		if (item instanceof RssItunesItemInfo) {
			return ((showClass) ? (((RssItunesItemInfo)item).getClass().getName() + ",") : "") +
				((RssItunesItemInfo)item).toString();
		} else {
			return ((showClass) ? (item.getClass().getName() + ",") : "") +
					item.toString();
		}
	}
	//#endif

/*
 * Visit url for update: http://sourceforge.net/projects/jvftp
 * 
 * JvFTP was developed by http://sourceforge.net/users/bpetrovi
 * The sources was donated to sourceforge.net under the terms 
 * of GNU Lesser General Public License (LGPL). Redistribution of any 
 * part of JvFTP or any derivative works must include this notice.
 */
/*
 * This was modified 2010-05-22.
 */
	/** This is a generic version of C.A.R Hoare's Quick Sort 
	 * algorithm.  This will handle arrays that are already
	 * Sorted, and arrays with duplicate keys.
	 * If you think of a one dimensional array as going from
	 * the lowest index on the left to the highest index on the right
	 * then the parameters to this function are lowest index or
	 * left and highest index or right.  The first time you call
	 * this function it will be with the parameters 0, a.length - 1.
	 *
	 * @param a	   an long array
	 * @param lo0	 left boundary of array partition
	 * @param hi0	 right boundary of array partition */
	static private void longQuickSort(long[] a, int[] indexes, int lo0, int hi0)
	{
		//#ifdef DLOGGING
		Logger logger = Logger.getLogger("MiscUtil");
		if (logger.isLoggable(Level.FINE)) {logger.fine("longQuickSort a.length,indexes.length,lo0,hi0=" + a.length + "," + indexes.length + "," + lo0 + "," + hi0);}
		//#endif

		long mid;
		int swap;

		Vector vlos = new Vector();
		vlos.addElement(new Integer(lo0));
		Vector vhis = new Vector();
		vhis.addElement(new Integer(hi0));

		for (int ic = 0; ic < vlos.size(); ic++) {
			lo0 = ((Integer)vlos.elementAt(ic)).intValue();
			int lo = lo0;
			hi0 = ((Integer)vhis.elementAt(ic)).intValue();
			int hi = hi0;
			if (hi0 > lo0) {

				/* Arbitrarily establishing partition element as the midpoint of
				 * the array. */
				mid = a[indexes[(lo0 + hi0) >> 1]];

				// loop through the array until indices cross
				while (lo <= hi) {
					/* find the first element that is greater than or equal to 
					 * the partition element starting from the left Index. */
					while ((lo < hi0) && (a[indexes[lo]] < mid)) {
						++lo;
					}

					/* find an element that is smaller than or equal to 
					 * the partition element starting from the right Index. */
					while ((hi > lo0) && (a[indexes[hi]] > mid)) {
						--hi;
					}

					// if the indexes have not crossed, Swap
					if (lo <= hi) {
						swap = indexes[lo];
						indexes[lo] = indexes[hi];
						indexes[hi] = swap;
						++lo;
						--hi;
					}
				}

				/* If the right index has not reached the left side of array
				 * must now Sort the left partition. */
				if (lo0 < hi) {
					vlos.addElement(new Integer(lo0));
					vhis.addElement(new Integer(hi));
				}

				/* If the left index has not reached the right side of array
				 * must now Sort the right partition. */
				if (lo < hi0) {
					vlos.addElement(new Integer(lo));
					vhis.addElement(new Integer(hi0));
				}
			}
		}

	}

  /**
   * Call quick sort if &gt; 1 element.
   * @param a
   */
	static public void indexedSort(long[] a, int[] indexes, int aend) {
		if (aend <= 0) {
			return;
		}
		longQuickSort(a, indexes, 0, aend);
	}

}
