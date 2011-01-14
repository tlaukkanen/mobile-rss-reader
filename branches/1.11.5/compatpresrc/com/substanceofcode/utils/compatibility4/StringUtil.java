//--Need to modify--#preprocess
/*
 * StringUtil.java
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
 * IB 2010-05-24 1.11.5RC2 Use StringUtil in compatibility package for testing.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.utils.compatibility4;
//TODO test </a> html. test no http (or using base?)
import java.util.Vector;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 *
 * @author Tommi
 */
public class StringUtil {

    private final static String breakTags = "<p><br><td><p/><br/><td/>";

	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.StringUtil");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Creates a new instance of StringUtil */
    private StringUtil() {
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
		Logger logger = Logger.getLogger("StringUtil");
		boolean finerLoggable = logger.isLoggable(Level.FINER);
		//#endif
        try{
			if (text == null) { return null; }
            StringBuffer plainText = new StringBuffer();
            String htmlText = text.trim();
            int htmlStartIndex = htmlText.indexOf('<');
			if (htmlStartIndex == -1) { return text; }
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
    
}
