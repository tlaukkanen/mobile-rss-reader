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
 * 
 */

// Expand to define DJSR75 define
@DJSR75@
// Expand to define logging define
@DLOGDEF@
//#ifdef DJSR75

package org.kablog.kgui;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

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
//#ifdef DJSR75
implements KViewParent 
//#endif
{

	//#ifdef DJSR75
	protected MIDlet midlet;
	protected TextField txtFld;
	protected KFileSelector fileSelectorView; 
    protected KViewParent viewParent;
    protected boolean ready = false;
    protected boolean bDebug;
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("KFileSelectorMgr");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
	//#endif

	static public boolean isJsr75Enabled() {
		return (System.getProperty(
					"microedition.io.file.FileConnection.version") != null);
	}

	//#ifdef DJSR75
    /**
     * When the camView is done capturing an image, it calls this method.
     */
    public void childFinished(KViewChild child) {   
		try {
			if (fileSelectorView.getSelectedURL() != null) {
				txtFld.setString(fileSelectorView.getSelectedURL());
			}
			fileSelectorView.doCleanup();
			fileSelectorView = null;
			ready = true;
		} catch (Throwable t) {
			//#ifdef DLOGGING
			logger.severe("Sort dates error.", t);
			//#endif
			System.out.println("Sort dates error." + t + " " +
							   t.getMessage());
			t.printStackTrace();
		}
	}
        
	public void doLaunchSelector(MIDlet midlet, TextField txtFld) {

		System.out.println("doLaunchSelector...");
		this.midlet = midlet;
		this.txtFld = txtFld;

		fileSelectorView = null;

		if (isJsr75Enabled())
		{
			try {
				fileSelectorView = KFileSelectorFactory.getInstance(
						midlet, "Find import file", null, "/icons" );
				fileSelectorView.setViewParent(this);
				Display.getDisplay(midlet).setCurrent((List)fileSelectorView);
			}
			catch (Exception ex)
			{
				if (bDebug) System.out.println("### selector fail: " + ex);
			}
		}

	}//doLaunchSelector

	/** We've updated the child's status.
	 */
	public void childStatusChanged(KViewChild child, int statusType, int status) {
		if (bDebug) System.out.println("Child status changed: " + status);
	} 

    /** @param newView object o make visible, if possible.
     */
    public void reqSetVisible(Displayable newView) {
    	if (viewParent != null) {
			viewParent.reqSetVisible(newView);
		} else {
			Display.getDisplay(midlet).setCurrent(newView);
		}
    }
    
    /** @param The callback client interested in receiving finished status.
     */
    public void setViewParent(KViewParent parent) {
        this.viewParent = parent;
    }
    
    /** 
     Display a debug message somehow
     */
    public void displayDbgMsg(String msg, AlertType type) {
         if (bDebug) System.out.println("dbgMsg: " + msg);
	}

    public void addDeferredAction(Runnable runny)
    {
    	if (viewParent != null) {
			viewParent.addDeferredAction(runny);
		} else {
			new Thread(runny).start();
		}
    }

    public boolean isReady() {
        return (ready);
    }

	//#endif
}
//#endif
