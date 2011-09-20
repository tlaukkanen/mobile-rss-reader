//--Need to modify--#preprocess
/*
 * KFileSelectorMgr.java
 *
 * Copyright (c) 2001-2005 Todd C. Stellanova, rawthought
 * Copyright (C) 2007-2011 Irving Bunton, Jr
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
 * IB 2010-08-15 1.11.5Dev8 Remove unused reqSetVisible.
 * IB 2010-08-15 1.11.5Dev8 Need LoadingForm for FeatureList.
 * IB 2010-08-15 1.11.5Dev8 Use FeatureForm for txtFrm.
 * IB 2010-08-15 1.11.5Dev8 Use setCurrentItemMgr for setCurrentItem/setCurrent.
 * IB 2010-08-15 1.11.5Dev8 Use setMainCurrentAlt for getDisplay.setCurrent.
 * IB 2010-08-15 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-09-26 1.11.5Dev8 Use setCurrentItemFeature instead of setCurrentItemMgr.
 * IB 2010-09-27 1.11.5Dev8 Move title to construtor for KFileSelectorImpl.
 * IB 2010-10-12 1.11.5Dev8 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-18 1.11.5Dev14 Allow an object to be written to with the find files result using FeatureMgr.setTxtObj.
 * IB 2010-11-19 1.11.5Dev14 Allow title for find files as a parameter to allow more generic doLaunchSelector in KFileSelectorMgr.
 * IB 2010-11-19 1.11.5Dev14 Have println in DTEST.
 * IB 2010-11-19 1.11.5Dev14 Have debug conditional println use displayDbgMsg.
 * IB 2010-11-19 1.11.5Dev14 Have displayDbgMsg and be in DTEST.
 * IB 2010-11-19 1.11.5Dev14 Change debug log info.
 * IB 2011-01-01 1.11.5Dev15 Need to define DTEST to use it.
 * IB 2011-01-14 1.11.5Alpha15 Use CmdReceiver interface to allow FeatureMgr to initialize KFileSelectorMgr without directly referencing it's class.  This allows for better use of optional APIs.
*/

// Expand to define MIDP define
//#define DMIDP20
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define test define
//#define DNOTEST
// Expand to define logging define
//#define DNOLOGGING
//#ifdef DJSR75
//@
//@package org.kablog.kgui;
//@
//@import javax.microedition.lcdui.*;
//@
//@import com.substanceofcode.rssreader.presentation.ImportFeedsForm;
//@import com.substanceofcode.rssreader.presentation.LoadingForm;
//@import com.substanceofcode.rssreader.presentation.FeatureForm;
//@import com.substanceofcode.utils.MiscUtil;
//@import com.substanceofcode.rssreader.presentation.FeatureMgr;
//@import com.substanceofcode.utils.CmdReceiver;
//@
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//@import net.sf.jlogmicro.util.logging.FormHandler;
//@import net.sf.jlogmicro.util.logging.RecStoreHandler;
//#endif
//@
//@/**
//@ *
//@ * @author  Todd C. Stellanova
//@ */
//@public class KFileSelectorMgr
//@implements KViewParent, CmdReceiver
//@{
//@
//@	final       Object nullPtr = null;
//@	protected Displayable txtDisp;
//@	protected Object txtObj;
//@	protected KFileSelectorImpl fileSelectorView;
//@    protected KViewParent viewParent;
//@    protected boolean ready = false;
//@    protected boolean bDebug;
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("KFileSelectorMgr");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
//@
//@	public KFileSelectorMgr() {
//@		fileSelectorView = (KFileSelectorImpl)nullPtr;
//@	}
//@
//@    /**
//@     * When the we're is done capturing an XML or multi-media, it calls this
//@	   method.
//@     */
//@    final public void childFinished(KViewChild child) {
//@		try {
//@			if (fileSelectorView.getSelectedURL() != null) {
//@				FeatureMgr.setTxtObj(txtObj, fileSelectorView.getSelectedURL());
//@			}
//@			fileSelectorView.doCleanup();
//@			FeatureMgr.setCurrentObjMgr( fileSelectorView.getFeatureMgr(),
//@					txtObj, txtDisp);
//@			// Save memory
//@			fileSelectorView = (KFileSelectorImpl)nullPtr;
//@		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("Sort dates error.", t);
			//#endif
			//#ifdef DTEST
//@			System.out.println("Sort dates error." + t + " " +
//@							   t.getMessage());
			//#endif
//@			t.printStackTrace();
//@		}
//@	}
//@
//@	/* Start the file selector list. */
//@	final public void doLaunchSelector(boolean selectDir, String findTitle,
//@			Displayable txtDisp, Object txtObj,
//@			LoadingForm loadForm)
//@	throws Throwable {
//@
		//#ifdef DTEST
//@		System.out.println("doLaunchSelector...");
		//#endif
//@		this.txtDisp = txtDisp;
//@		this.txtObj = txtObj;
//@
//@		fileSelectorView = (KFileSelectorImpl)nullPtr;
//@
//@		try {
//@			fileSelectorView = new KFileSelectorImpl(findTitle, loadForm);
//@			fileSelectorView.init(selectDir, null, "/icons" );
//@			fileSelectorView.setCommandListener(fileSelectorView, true);
//@			fileSelectorView.setViewParent(this);
//@			fileSelectorView.getFeatureMgr().setMainCurrentAlt(null, null,
//@					fileSelectorView);
//@		}
//@		catch (Throwable e)
//@		{
			//#ifdef DTEST
//@			displayDbgMsg("Go to find files fail: " + e, null);
			//#endif
			//#ifdef DLOGGING
//@			logger.severe("Go to find files fail: ", e);
			//#endif
//@			e.printStackTrace();
//@			throw e;
//@		}
//@
//@	}//doLaunchSelector
//@
//@	/** We've updated the child's status.
//@	 */
//@	final public void childStatusChanged(KViewChild child, int statusType, int status) {
		//#ifdef DTEST
//@		displayDbgMsg("Child status changed: " + status, null);
		//#endif
//@	}
//@
//@    /** @param The callback client interested in receiving finished status.
//@     */
//@    final public void setViewParent(KViewParent parent) {
//@        this.viewParent = parent;
//@    }
//@
	//#ifdef DTEST
//@    /**
//@     Display a debug message somehow
//@     */
//@    final public void displayDbgMsg(String msg, AlertType type) {
//@         if (bDebug) System.out.println("dbgMsg: " + msg);
//@	}
	//#endif
//@
//@	/* Add a deferred action.  This is either passed on to our parent or
//@	   run as a thread now. */
//@    final public void addDeferredAction(Runnable runny)
//@    {
//@    	if (viewParent != null) {
//@			viewParent.addDeferredAction(runny);
//@		} else {
//@			MiscUtil.getThread(runny, "KFileSelectorMgr", this, "addDeferredAction").start();
//@		}
//@    }
//@
//@	public Object[] action(Object[] reqs) {
//@		if (reqs[0] == MiscUtil.SINIT_OBJ) {
//@			try {
//@				this.doLaunchSelector(
//@						((Boolean)reqs[1]).booleanValue(),
//@						(String)reqs[2], (Displayable)reqs[3], reqs[4],
//@						(LoadingForm)reqs[5]);
//@				return new Object[] {null, new Integer(1), fileSelectorView};
//@			} catch (Throwable e) {
//@				return new Object[] {e, new Integer(1), fileSelectorView};
//@			}
//@		} else {
//@			return new Object[] {new IllegalArgumentException(
//@					"Illegal argument " + reqs[0] +
//@					".  Only SINIT_OBJ is a valid request."), null};
//@		}
//@	}
//@}
//#endif
