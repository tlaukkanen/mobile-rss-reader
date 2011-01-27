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
 * IB 2011-01-14 1.11.5Alpha15 Use conditional preprocessed cldc11 code with modifications instead of cldc10 code.
 * IB 2011-01-14 1.11.5Alpha15 Use convience method cmpModLog from LoggingTestCase to see if feeds are unequal and change the logging level to retry using logging to make debugging equals failures easier.  Also, retry with modified previous version for bug fixes/enhancements made in the current version.
 * IB 2011-01-14 1.11.5Alpha15 Use convience methods updSvLogging and updPrevLogging from LoggingTestCase to alter/restore the logging level.
 * IB 2011-01-14 1.11.5Alpha15 For JMUnit Plus using midlets, take midlet from the single midlet vs the current test which is part of multiple midlets used as tests.
 * IB 2011-01-14 1.11.5Alpha15 Use procThrowable from LoggingTestCase.
 * IB 2011-01-24 1.11.5Dev16 Don't compile some code for internet link version.
 * IB 2011-01-24 1.11.5Dev16 Fix println statement.
*/

// Expand to define itunes define
@DFULLVERSDEF@
// Expand to define test define
@DCOMPATDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define JMUnit test define
@DJMTESTDEF@
// Expand to define JMUNITPLUS define
@DJMTESTPLUSDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DJMTEST
package com.substanceofcode.jmunit.logging;

import java.util.Vector;

import jmunit.framework.cldc11.Test;
import jmunit.framework.cldc11.TestCase;
import jmunit.framework.cldc11.AssertionFailedException;

//#ifdef DFULLVERS
import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
//#endif
import com.substanceofcode.utils.CauseException;

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
	/*
	protected Vector svLogLevel = null;
	*/
    protected Logger logger;
    protected boolean fineLoggable;
    protected boolean finerLoggable;
    protected boolean finestLoggable;
    protected boolean traceLoggable;
	protected Level svLogLevel = null;
	protected Level svCmpLogLevel = null;
    protected LoggerRptForm debugForm = null;
	protected String scompLevel = Level.INFO.getName(); // Change to more stringent
	//#endif

	public LoggingTestCase(int nbr, String name) {
		super(nbr, name);
		//#ifdef DLOGGING
		LogManager logManager = LogManager.getLogManager();
		//#ifdef DJMTESTPLUS
		logManager.readConfiguration(Test.getTestMidlet());
		//#else
		logManager.readConfiguration(this);
		//#endif
		logger = Logger.getLogger(name);
		setFlags();
		for (Enumeration eHandlers = logger.getParent().getHandlers().elements();
				eHandlers.hasMoreElements();) {
			Object ohandler = eHandlers.nextElement();
			if (ohandler instanceof FormHandler) {
				Form oform = (Form)((FormHandler)ohandler).getView();
				logger.finest("form=" + oform);
			}
		}
		debugForm = new LoggerRptForm(logManager,
				//#ifdef DJMTESTPLUS
				Test.getTestMidlet(),
				//#else
				this,
				//#endif
					null, "net.sf.jlogmicro.util.logging.FormHandler");
		logger.info("Loading test case,nbr cases=" + name + "," + nbr);
		//#endif
	}

	//#ifdef DLOGGING
	public void setFlags() {
		fineLoggable = logger.isLoggable(Level.FINE);
		logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
		finerLoggable = logger.isLoggable(Level.FINER);
		logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
		finestLoggable = logger.isLoggable(Level.FINEST);
		logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
		traceLoggable = logger.isLoggable(Level.TRACE);
		logger.fine("obj,traceLoggable=" + this + "," + traceLoggable);
	}

	public Level updSvLogging(String newLogLevel) {
		/*
		if (svLogLevel == null) {
			svLogLevel = new Vector();
		}
		svLogLevel.addElement(logger.getParent().getLevel());
		*/
		Level cLogLevel = logger.getParent().getLevel();
		String strsvLogLevel = cLogLevel.getName();
		if (!strsvLogLevel.equals(newLogLevel)) {
			Level nlogLevel;
			logger.getParent().setLevel(nlogLevel = Level.parse(newLogLevel));
			//logger = Logger.getLogger(CURRENT_CLASS);
			setFlags();
			return nlogLevel;
		} else {
			return null;
		}
	}

	public void updPrevLogging(Level svLogLevel) {
		if (svLogLevel != null) {
			Level cLogLevel = logger.getParent().getLevel();
			if (!cLogLevel.getName().equals(svLogLevel.getName())) {
				logger.getParent().setLevel(svLogLevel);
				//logger = Logger.getLogger(CURRENT_CLASS);
				setFlags();
			}
		}
	}
	//#endif

	//#ifdef DFULLVERS
	public Object[] cmpModLog(String cmpMsg,
			RssItunesFeedInfo ncmpfeed,
			RssItunesFeedInfo nfeed) {
		boolean feq = ncmpfeed.equals(nfeed);
		if (!feq) {
			//#ifdef DLOGGING
			logger.warning("Feeds not equal nfeed.getName()=" + nfeed.getName());
			svCmpLogLevel = updSvLogging(scompLevel);
			//#endif
			RssItunesFeedInfo mcmpfeed = (RssItunesFeedInfo)ncmpfeed.clone();
			RssItunesFeedInfo mnfeed = (RssItunesFeedInfo)nfeed.clone();
			feq = mcmpfeed.equals(mnfeed);
			if (!feq) {
				//#ifdef DCOMPATIBILITY
				if (((RssItunesFeed)mnfeed).setItemDatesNull(mcmpfeed)) {
				//#endif
					feq = mcmpfeed.equals(mnfeed);
				//#ifdef DCOMPATIBILITY
				}
				//#endif
			}
			//#ifdef DLOGGING
			updPrevLogging(svCmpLogLevel);
			//#endif
		}
		Throwable ae = null;
		if (!feq) {
			try {
				assertTrue(cmpMsg, ncmpfeed.equals(nfeed));
			} catch (Throwable e) {
				ae = e;
			}
		}
		return new Object[] {new Boolean(feq), ae};
	}
	//#endif

	public void procThrowable(String mname, Throwable e)
	throws Throwable {
		CauseException ce;
		if (e instanceof AssertionFailedException) {
			ce = new CauseException(mname +
					" AssertionFailedException  RuntimeException " +
					"exception.", e);
		} else if (e instanceof RuntimeException) {
			ce = new CauseException(mname +
					" Internal error, runtime exception.", e);
		} else if (e instanceof Exception) {
			ce = new CauseException(mname +
					" Internal error, miscelaneous exception.", e);
		} else if (e instanceof OutOfMemoryError) {
			ce = new CauseException(mname +
					" Internal error, OutOfMemoryError error throwable.", e);
		} else if (e instanceof Error) {
			ce = new CauseException(mname +
					" Internal error, Error throwable.", e);
		} else {
			ce = new CauseException(mname +
					" Internal error, miscelaneous throwable.", e);
		}
		//#ifdef DLOGGING
		logger.severe(ce.getMessage(), ce);
		//#else
		System.out.println(ce.getMessage());
		e.printStackTrace();
		//#endif
		throw e;
	}

}
//#endif
