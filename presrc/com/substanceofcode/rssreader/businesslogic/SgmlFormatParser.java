//--Need to modify--#preprocess
/*
 * SgmlFormatParser.java
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
 * IB 2010-05-24 1.11.5RC2 Use interfaces to make compatibility testing easier.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this if it is the full version.
 */
// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
//#ifdef DTEST
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businesslogic;

import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.utils.SgmlParserIntr;
import java.io.IOException;


/**
 * Interface contains methods for parsing general feed.
 *
 * @author Tommi Laukkanen
 */
public interface SgmlFormatParser {

    RssItunesFeedInfo parse(SgmlParserIntr parser, RssItunesFeedInfo feed,
			int maxItemCount, boolean getTitleOnly) throws IOException;
    
}
//#endif
//#endif
