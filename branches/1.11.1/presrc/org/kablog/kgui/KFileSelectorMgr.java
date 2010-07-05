/* Copyright (c) 2001-2005 Todd C. Stellanova, rawthought
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. 
 * 
 * 
 * 
 * This software was originally modified no later than Sept 25, 2007.
 */
/*
 * IB 2010-03-21 1.11.5RC1 Remove KFileSelector to save memory. 
 * IB 2010-04-30 1.11.5RC2 Track threads used.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern for nulls to initialize and save memory.
*/

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define logging define
@DLOGDEF@
//#ifdef DJSR75

package org.kablog.kgui;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.ImportFeedsForm;
import com.substanceofcode.utils.MiscUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
import net.sf.jlogmicro.util.logging.FormHandler;
import net.sf.jlogmicro.util.logging.RecStoreHandler;
//#endif

/**
 *
 * @author  Todd C. Stellanova
 */
public class KFileSelectorMgr
implements KViewParent 
{

	final       Object nullPtr = null;
	protected RssReaderMIDlet midlet;
	protected Form txtFrm;
	protected TextField txtFld;
	protected KFileSelectorImpl fileSelectorView; 
    protected KViewParent viewParent;
    protected boolean ready = false;
    protected boolean bDebug;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("KFileSelectorMgr");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /**
     * When the we're is done capturing an XML or multi-media, it calls this
	   method.
     */
    final public void childFinished(KViewChild child) {   
		try {
			if (fileSelectorView.getSelectedURL() != null) {
				txtFld.setString(fileSelectorView.getSelectedURL());
			}
			fileSelectorView.doCleanup();
			// Save memory
			fileSelectorView = (KFileSelectorImpl)nullPtr;
			//#ifdef DMIDP20
			midlet.setCurrentItem( txtFld );
			//#else
			midlet.setCurrent( txtFrm );
			//#endif
		} catch (Throwable t) {
			//#ifdef DLOGGING
			logger.severe("Sort dates error.", t);
			//#endif
			System.out.println("Sort dates error." + t + " " +
							   t.getMessage());
			t.printStackTrace();
		}
	}
        
	/* Start the file selector list. */
	final public void doLaunchSelector(RssReaderMIDlet midlet,
			boolean selectDir, Form txtFrm, TextField txtFld)
	throws Throwable {

		System.out.println("doLaunchSelector...");
		this.midlet = midlet;
		this.txtFrm = txtFrm;
		this.txtFld = txtFld;

		fileSelectorView = (KFileSelectorImpl)nullPtr;

		try {
			fileSelectorView = new KFileSelectorImpl();
			fileSelectorView.init(midlet,
					selectDir,
					((txtFrm instanceof ImportFeedsForm) ? "Find import file" :
					 "Find feed file"),
					null, "/icons" );
			fileSelectorView.setCommandListener(fileSelectorView, true);
			fileSelectorView.setViewParent(this);
			Display.getDisplay(midlet).setCurrent(fileSelectorView);
		}
		catch (Throwable e)
		{
			if (bDebug) System.out.println("Go to find files fail: " + e);
			//#ifdef DLOGGING
			logger.severe("Go to find files fail: ", e);
			//#endif
			e.printStackTrace();
			throw e;
		}

	}//doLaunchSelector

	/** We've updated the child's status.
	 */
	final public void childStatusChanged(KViewChild child, int statusType, int status) {
		if (bDebug) System.out.println("Child status changed: " + status);
	} 

    /** @param newView object o make visible, if possible.
     */
    final public void reqSetVisible(Displayable newView) {
    	if (viewParent != null) {
			viewParent.reqSetVisible(newView);
		} else {
			Display.getDisplay(midlet).setCurrent(newView);
		}
    }
    
    /** @param The callback client interested in receiving finished status.
     */
    final public void setViewParent(KViewParent parent) {
        this.viewParent = parent;
    }
    
    /** 
     Display a debug message somehow
     */
    final public void displayDbgMsg(String msg, AlertType type) {
         if (bDebug) System.out.println("dbgMsg: " + msg);
	}

	/* Add a deferred action.  This is either passed on to our parent or
	   run as a thread now. */
    final public void addDeferredAction(Runnable runny)
    {
    	if (viewParent != null) {
			viewParent.addDeferredAction(runny);
		} else {
			MiscUtil.getThread(runny, "KFileSelectorMgr", this, "addDeferredAction").start();
		}
    }

}
//#endif
