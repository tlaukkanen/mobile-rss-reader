//--Need to modify--#preprocess
/*
 * LoggingTestCase.java
 *
 * Copyright (C) 2009 Irving Bunton
 * http://code.google.com/p/mobile-rss-reader/
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
 * IB 2010-04-05 1.11.5RC1 Log loading of test case.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
*/

// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.logging;

import javax.microedition.midlet.MIDlet;

import jmunit.framework.cldc10.TestCase;

import com.substanceofcode.rssreader.businessentities.RssItunesItem;

import java.util.Enumeration;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.presentation.LoggerRptForm;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
//#endif

abstract public class LoggingTestCase extends TestCase {

	//#ifdef DLOGGING
    protected Logger logger;
    protected boolean fineLoggable;
    protected boolean finerLoggable;
    protected boolean finestLoggable;
    protected boolean traceLoggable;
    protected LoggerRptForm debugForm = null;
	//#endif

	public LoggingTestCase(int nbr, String name) {
		super(nbr, name);
		//#ifdef DLOGGING
		LogManager logManager = LogManager.getLogManager();
		logManager.readConfiguration(this);
		logger = Logger.getLogger(name);
		fineLoggable = logger.isLoggable(Level.FINE);
		logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
		finerLoggable = logger.isLoggable(Level.FINER);
		logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
		finestLoggable = logger.isLoggable(Level.FINEST);
		logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
		traceLoggable = logger.isLoggable(Level.TRACE);
		logger.fine("obj,traceLoggable=" + this + "," + traceLoggable);
		for (Enumeration eHandlers = logger.getParent().getHandlers().elements();
				eHandlers.hasMoreElements();) {
			Object ohandler = eHandlers.nextElement();
			if (ohandler instanceof FormHandler) {
				Form oform = (Form)((FormHandler)ohandler).getView();
				logger.finest("form=" + oform);
			}
		}
		debugForm = new LoggerRptForm(logManager, this,
					null, "net.sf.jlogmicro.util.logging.FormHandler");
		logger.info("Loading test case,nbr cases=" + name + "," + nbr);
		//#endif
	}

}
//#endif
