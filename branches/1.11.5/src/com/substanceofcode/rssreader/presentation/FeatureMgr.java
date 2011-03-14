//--Need to modify--#preprocess
/*
 * FeatureMgr.java
 *
 * Copyright (C) 2007-2010 Irving Bunton
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
 * IB 2010-03-07 1.11.4RC1 Use NULL pattern.  Fixed loop.
 * IB 2010-04-30 1.11.5RC2 Track threads used.
 * IB 2010-05-24 1.11.5RC2 Implement CmdReceiver interface to allow pause of thread.
 * IB 2010-05-24 1.11.5RC2 Pause based on loop time not being reached.
 * IB 2010-05-24 1.11.5RC2 Convience method for logging UI command.
 * IB 2010-05-24 1.11.5RC2 Convience method for logging UI item.
 * IB 2010-05-24 1.11.5RC2 Use null for nullCmd.
 * IB 2010-05-28 1.11.5RC2 Use threads and CmdReceiver for MIDP 2.0 only.
 * IB 2010-06-27 1.11.5Dev2 Have convenience methods showme with/without alert.
 * IB 2010-06-27 1.11.5Dev2 Have static initSettingsEnabled to load app and general settings to help with testing.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern using nullPtr.
 * IB 2010-08-14 1.11.5Dev8 Support loading form with FeatureMgr.
 * IB 2010-08-14 1.11.5Dev8 Support setCurrentItem in FeatureMgr.
 * IB 2010-08-18 1.11.5Dev8 Have static RssReaderMIDlet variable.
 * IB 2010-08-18 1.11.5Dev8 Have static mainDisp to keep main starting form.
 * IB 2010-08-18 1.11.5Dev8 Have static display to keep display from midlet.
 * IB 2010-08-18 1.11.5Dev8 Have getCurrent for test UI.
 * IB 2010-08-18 1.11.5Dev8 Have setCurrentMgr to setCurrent display to alert and display and wakeup the featureMgr parameter.
 * IB 2010-08-18 1.11.5Dev8 Have setCurrentItemMgr to setCurrent display item or display if MIDP 1.0 and wakeup the featureMgr parameter.
 * IB 2010-08-18 1.11.5Dev8 Have setCurrentItemFeature to setCurrent display item or display if MIDP 1.0 and wakeup the featureMgr for fdisp parameter.
 * IB 2010-08-18 1.11.5Dev8 Have wakeupDisp wakeup the featureMgr for disp parameter with loop parameter.
 * IB 2010-08-18 1.11.5Dev8 Have setCurrentFeature to setCurrent display to alert and display and wakeup the featureMgr for fdisp parameter.
 * IB 2010-08-18 1.11.5Dev8 Have setCurrentAlt to setCurrent display to alert and display and wakeup the featureMgr for fdisp parameter if not null or cmainDisp if not null or disp.
 * IB 2010-08-18 1.11.5Dev8 Have setMainCurrentAlt to setCurrent display to alert and display and wakeup the featureMgr for fdisp parameter if not null or mainDisp if not null or disp.
 * IB 2010-08-18 1.11.5Dev8 Have setCurrent to setCurrent display to display and wakeup the featureMgr for the disp parameter or the current featureMgr.
 * IB 2010-08-18 1.11.5Dev8 Have setCurrent to setCurrent display to alert and display and wakeup the featureMgr for the disp parameter and the current featureMgr.
 * IB 2010-09-26 1.11.5Dev8 Have callSerially to serialize threads.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for Settings.
 * IB 2010-09-27 1.11.5Dev8 Don't use midlet directly for initSettingsEnabled.
 * IB 2010-09-27 1.11.5Dev8 Use loadForm in FeatureMgr.
 * IB 2010-09-27 1.11.5Dev8 Need getCurrent for setCurrentItemMgr.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-10-30 1.11.5Dev12 Add getSysProperty to get system property and return error message.  This gets an error in microemulator if it causes a class to be loaded.
 * IB 2010-11-05 1.11.5Dev12 Add getSysPermission to get if permission allowed, denied or undefined.  For non-MIDP 20, can only use getSysProperty if key != null.
 * IB 2010-11-15 1.11.5Dev14 Add getSysPropStarts to get system prorperties and see if it starts with a string.
 * IB 2010-11-15 1.11.5Dev14 Use nullPtr instead of nullCmd.
 * IB 2010-11-15 1.11.5Dev14 Use null to initialize variables.
 * IB 2010-11-15 1.11.5Dev14 Use acceptCmd and optional rejectCmd for prompt form or alert.
 * IB 2010-11-15 1.11.5Dev14 Use getPromptDisp to handle a prompt with accept and reject.
 * IB 2010-11-15 1.11.5Dev14 Use form instead of alert as form is more reliable and works with micro emulator.
 * IB 2010-11-15 1.11.5Dev14 Have convience FeatureMgr constructor without loading form.
 * IB 2010-11-15 1.11.5Dev14 Add getImage to FeatureMgr to allow getting an image and adding error to LoadingForm.
 * IB 2010-11-15 1.11.5Dev14 Fix using text box to allow data to be entered and copy or paste.
 * IB 2010-11-15 1.11.5Dev14 Allow text box to be used as a prompt with accept/reject.
 * IB 2010-11-15 1.11.5Dev14 Allow StringBuffer to store data from text box in FeatureMgr.
 * IB 2010-11-15 1.11.5Dev14 More logging.
 * IB 2010-11-15 1.11.5Dev14 Use null to compare instead of nullCmd in FeatureMgr.
 * IB 2010-11-15 1.11.5Dev14 Add convience method setCurrentObjMgr to set current using objects for item and display.
 * IB 2010-11-15 1.11.5Dev14 Allow item in setCurrentItemMgr to be null for convience in FeatureMgr.
 * IB 2010-11-15 1.11.5Dev14 Use choice instead of List for getPlaceIndex because choice is a interface for List.
 * IB 2010-11-15 1.11.5Dev14 Cosmetic changes.
 * IB 2010-11-16 1.11.5Dev14 Add default value for getSysProperty, getSysPermission, and getSysPropStarts.
 * IB 2010-11-16 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
 * IB 2010-11-17 1.11.5Dev14 Change initializeURLBox to use return display and return object.
 * IB 2010-11-17 1.11.5Dev14 Change prompt to set StringItem if return object.
 * IB 2010-11-17 1.11.5Dev14 Cosmetic change.
 * IB 2010-11-17 1.11.5Dev14 More logging.
 * IB 2010-11-18 1.11.5Dev14 Move find files call functionality to FeatureMgr.
 * IB 2010-11-18 1.11.5Dev14 Allow select directory for find files load message to be passed as a parameters (if selectDir true/false) to make it more generic.
 * IB 2010-11-18 1.11.5Dev14 Create setTxtObj to set a text object of TextField, StringItem, or StringBuffer.
 * IB 2010-11-18 1.11.5Dev14 Change getSysPermission to always retrieve system key.
 * IB 2010-11-19 1.11.5Dev14 Simplify getSysPermission.
 * IB 2010-11-19 1.11.5Dev14 Move find files call functionality to FeatureMgr.
 * IB 2010-11-19 1.11.5Dev14 Allow select directory for find files load message to be passed as a parameters (if selectDir true/false) to make it more generic.
 * IB 2010-11-19 1.11.5Dev14 Move static var m_backCommand out of midlet class to FeatureMgr.
 * IB 2010-11-19 1.11.5Dev14 Simplify getSysProperty and getSysPermission.
 * IB 2010-11-22 1.11.5Dev14 Define log lcdui methods for DLOGGING, DTEST or DTESTUI.
 * IB 2010-11-22 1.11.5Dev14 Add catch Throwable, logging, and printStackTrace to some methods.
 * IB 2010-11-22 1.11.5Dev14 Use logDisp to log the display class and title(if present).
 * IB 2010-11-22 1.11.5Dev14 Don't cast setCurrentFeature parameter to setCurrentAlt.
 * IB 2010-11-22 1.11.5Dev14 Have getPromptDisp use optional rejectLabel for cancel label if present.
 * IB 2011-01-12 1.11.5Alpha15 Save midlet instead of RssReaderMIDlet to be more generic and help with testing.  Have getRssMidlet to return the RssReaderMIDlet.
 * IB 2011-01-12 1.11.5Alpha15 Allow specification of a accept label for the prompt form.
 * IB 2011-01-12 1.11.5Alpha15 Have m_exitCommand be in FeatureMgr.
 * IB 2011-01-12 1.11.5Alpha15 Have loop for pause and wait on a specific time period to avoid timing issues and spurrious awakes.
 * IB 2011-01-12 1.11.5Alpha15 Have getAddTextField to both create a text field which uses layout bottom (MIDP 2.0) and appends the field to the given form.
 * IB 2011-01-12 1.11.5Alpha15 Have getAddStringItem to both create a string item which uses layout bottom (MIDP 2.0) and appends the item to the given form.
 * IB 2011-01-12 1.11.5Alpha15 Have getCmdAdd to both create a command and add it to the displayable.  Return the command pointer.
 * IB 2011-01-12 1.11.5Alpha15 Have getCmdAddPrompt to both create a prompt command and add it to the displayable.  Return the command pointer.
 * IB 2011-01-12 1.11.5Alpha15 Don't use static vars for RssReaderSettings.
 * IB 2011-01-12 1.11.5Alpha15 More logging.
 * IB 2011-01-14 1.11.5Alpha15 Use CmdReceiver interface to allow FeatureMgr to initialize KFileSelectorMgr without directly referencing it's class.  This allows for better use of optional APIs.  It is still necessary to install the correct version on the right device, however.
 * IB 2011-01-24 1.11.5Dev16 Have m_aboutCmd in FeatureMgr since it should be common for all apps.
 * IB 2011-01-24 1.11.5Dev16 Don't compile some code for internet link version.
 * IB 2011-01-24 1.11.5Dev16 Fix println statement.
 * IB 2011-01-24 1.11.5Dev16 Have jsr75Avail to determine if JSR-75 is available for MIDlet, applet or standalone.  Also, return if exists or the version number.
 * IB 2010-11-22 1.11.5Dev14 For CLDC 1.0, use this.getClass() to get the class instance to use to load KFileSelectorMgr.
 * IB 2011-01-28 1.11.5Dev17 Change setSettingNbr to setFrGui2SettingsNbr.
 * IB 2011-01-28 1.11.5Dev17 Add setFrSettings2GuiNbr to set the text field with a numeric value from the settings with the given key.
 * IB 2011-03-06 1.11.5Dev17 Only use thread utils if not small memory.
 * IB 2011-03-06 1.11.5Dev17 Only use CmdReceiver utils if not small memory.
 * IB 2011-03-06 1.11.5Dev17 Have setFrGui2SettingsNbr to take the Items from the form and set the corresponding int RSS settings.
 * IB 2011-03-06 1.11.5Dev17 Have setFrSettings2GuiNbr to take the int settings from RSS settings and set the corresponding Item in the form.
 * IB 2011-03-06 1.11.5Dev17 Have getCmdAdd and getCmdAddPrompt allow optional long label.
 * IB 2011-03-06 1.11.5Dev17 Combine statements.
 * IB 2011-03-06 1.11.5Dev17 Make sure midlet is not null for tests and future combination with other programs.
 * IB 2011-03-06 1.11.5Dev17 Let adding prompt to be used for non FeatureForm/List.  The prompt is ignored if not for these displayables.
*/

// Expand to define MIDP define
//#define DMIDP20
// Expand to define CLDC define
//#define DCLDCV10
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define full vers define
//#define DFULLVERS
// Expand to define test define
//#define DNOTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define logging define
//#define DNOLOGGING

//#ifdef DTESTUI
//#define DTESTLOGMIN
//#elifdef DTEST
//#define DTESTLOGMIN
//#elifdef DLOGGING
//#define DTESTLOGMIN
//#endif
package com.substanceofcode.rssreader.presentation;

import java.util.Hashtable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.TextBox;
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
//#else
//@import com.substanceofcode.testlcdui.ChoiceGroup;
//@import com.substanceofcode.testlcdui.Form;
//@import com.substanceofcode.testlcdui.List;
//@import com.substanceofcode.testlcdui.StringItem;
//@import com.substanceofcode.testlcdui.TextField;
//#endif

import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.Settings;
//#ifndef DSMALLMEM
import com.substanceofcode.utils.CmdReceiver;
//#endif
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.rssreader.presentation.LoadingForm;
import com.substanceofcode.utils.CauseException;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel. */

public class FeatureMgr implements CommandListener,
	   Runnable
		//#ifndef DSMALLMEM
	   ,CmdReceiver
		//#endif
{

	final       Object nullPtr = null;
	private Hashtable promptCommands = null;
	static private Displayable mainDisp = null;
	private Displayable disp;
	private LoadingForm dispLoadForm = null;
    static private Display display = null; // The display for this MIDlet
	private Displayable promptDisp1 = null;
	private Displayable promptDisp2 = null;
    static final private long DEFAULT_LOOP_TIME = 500L;
    static private long LOOP_TIME = DEFAULT_LOOP_TIME;  // Time to wait for loops.
	private Command acceptCmd = null;
	private Command rejectCmd = null;
	private boolean foundDisp = false;
	private boolean foundPrompt = false;
	protected Command exCmd = null;
	private Displayable exDisp = null;
	static protected MIDlet midlet = null;
    static public Command m_backCommand = null; // The back to header list command
    static public Command m_exitCommand = null;// The exit command
    static public Command m_aboutCmd = null;// The about command
    private Displayable rtnDisp = null; // The form to return to for text box
    private Object rtnObj = null; // The object (e.g. item or StringBuffer) to return to for text box
    volatile private boolean     background = false;  // Flag to continue looping
    volatile private boolean     pauseApp = false;  // Flag to pause the application.
    volatile private int         loop = 0;   // Number of times to loop
    volatile private Thread      procThread = null;  // The thread for networking, other processing, etc

	//#ifdef DLOGGING
//@	private Logger logger = Logger.getLogger("FeatureMgr");
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
//@    private boolean traceLoggable = logger.isLoggable(Level.TRACE);
	//#endif

	private CommandListener cmdFeatureUser = null;
	private Runnable runFeatureUser = null;

	public FeatureMgr(Displayable disp, LoadingForm loadForm) {
		this.disp = disp;
		this.dispLoadForm = loadForm;
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("Starting FeatureMgr " + logDisp(disp));}
		//#endif
	}

	public FeatureMgr(Displayable disp) {
		this(disp, null);
	}

    public void setCommandListener(CommandListener cmdFeatureUser,
			boolean background) {
		//#ifdef DLOGGING
//@		if (fineLoggable && (cmdFeatureUser != null)) {logger.fine("setCommandListener cmdFeatureUser,background=" + cmdFeatureUser.getClass().getName() + "," + cmdFeatureUser + "," + background);}
		//#endif
		synchronized(this) {
			this.cmdFeatureUser = cmdFeatureUser;
			if (background) {
				if (cmdFeatureUser != null) {
					if (!(cmdFeatureUser instanceof Runnable)) {
						throw new IllegalArgumentException(
								"Listener must implement Runnable");
					}
					this.runFeatureUser = (Runnable)cmdFeatureUser;
					//#ifdef DLOGGING
//@					if (fineLoggable) {logger.fine("setCommandListener cmdFeatureUser" + cmdFeatureUser.getClass().getName() + "," + cmdFeatureUser);}
					//#endif
				} else {
					this.runFeatureUser = (Runnable)cmdFeatureUser;
					//#ifdef DLOGGING
//@					if (fineLoggable) {logger.fine("setCommandListener cmdFeatureUser=null");}
					//#endif
				}
			}
			this.background = background;
		}
		synchronized(this) {
			if (background) {
				// If running in the background, allow thread to wake up
				// on it's own.
				startWakeup(false);
			}
		}
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("cmdFeatureUser,background,calling thread,new thread=" + cmdFeatureUser + "," + background + "," + Thread.currentThread() + "," + procThread);}
		//#endif
    }

	public void addPromptCommand(Command cmd, String prompt) {
		synchronized(this) {
			if (promptCommands == null) {
				promptCommands = new Hashtable();
			}
			promptCommands.put(cmd, prompt);
		}
	}

	public void removeCommand(Command cmd) {
		synchronized(this) {
			if (promptCommands != null) {
				promptCommands.remove(cmd);
			}
		}
	}

	public Displayable getPromptDisp(String promptTitle, String promptMsg,
									 Command acceptCmd,
									 String acceptLabel,
									 Command rejectCmd,
									 String rejectLabel,
									 Displayable rtnDisp,
									 Object rtnObj ) {
		// Due to a quirk on T637 (MIDP 1.0), we need to create a form
		// before the alert or the alert will not be seen.
		Form formAlert = new Form(promptTitle);
		formAlert.append(promptMsg);
		formAlert.addCommand(new Command(((acceptLabel == null) ? "OK":
						acceptLabel), Command.OK, 4));
		formAlert.addCommand(new Command(((rejectLabel == null) ? "Cancel":
						rejectLabel), Command.CANCEL, 2));
		formAlert.setCommandListener(this);
		/*
		   Alert promptAlert = new Alert(ccmd.getLabel(),
		   promptMsg, null,
		   AlertType.CONFIRMATION);
		   promptAlert.setTimeout(Alert.FOREVER);
		   promptAlert.addCommand(new Command("OK", Command.OK, 4));
		   promptAlert.addCommand(new Command("Cancel", Command.CANCEL, 2));
		   promptAlert.setCommandListener(this);
		 */
		synchronized(this) {
			promptDisp1 = formAlert;
			this.acceptCmd = acceptCmd;
			this.rejectCmd = rejectCmd;
			this.rtnDisp = rtnDisp;
			this.rtnObj = rtnObj;
			/*
			   promptDisp2 = promptAlert;
			 */
		}
		FeatureMgr.setCurrentMgr(this, null, formAlert);
		/*
	     FeatureMgr.setCurrentMgr(this, promptAlert, formAlert);
		 */
		return formAlert;
	}

	/* Create prompt alert. */
	public void run() {
        /* Use networking if necessary */
        long lngStart;
        long lngTaskEnd;
        long lngTotalTimeTaken;
		try {
			do {
				lngStart = System.currentTimeMillis();
				//#ifdef DLOGGING
//@				if (finestLoggable && (loop > 0)) {logger.finest("run loop,background,cmdFeatureUser,runFeatureUser,foundDisp,foundPrompt,exCmd,exDisp,acceptCmd,rejectCmd=" + this.loop + "," + background + "," + cmdFeatureUser + "," + runFeatureUser + "," + foundDisp + "," + foundPrompt + "," + logCmd(exCmd) + "," + exDisp + "," + logCmd(acceptCmd) + "," + logCmd(rejectCmd));}
				//#endif
				try {
					Command ccmd = null;
					Displayable cdisp = null;
					Displayable crtnDisp = null;
					Object crtnObj = null;
					Command cacceptCmd = null;
					Command crejectCmd = (Command)nullPtr;
					boolean cfoundDisp = false;
					boolean cfoundPrompt = false;
					synchronized(this) {
						cfoundDisp = foundDisp;
						cfoundPrompt = foundPrompt;
						if ((cfoundDisp || cfoundPrompt) && (exCmd != null)) {
							crtnDisp = rtnDisp;
							crtnObj = rtnObj;
							ccmd = exCmd;
							cdisp = exDisp;
							cacceptCmd = acceptCmd;
							crejectCmd = rejectCmd;
						}
					}
					if ((ccmd != null) && (cdisp != null)) {
						try {
							Hashtable cpromptCommands = null;
							synchronized(this) {
								cpromptCommands = promptCommands;
							}
							//#ifdef DLOGGING
//@							if (fineLoggable) {logger.fine("run disp,cdisp,ccmd,cacceptCmd,crejectCmd,cfoundDisp,cfoundPrompt,thread,cpromptCommands="  + disp + "," + cdisp + "," + logCmd(ccmd) + "," + logCmd(cacceptCmd) + "," + logCmd(crejectCmd) + "," + cfoundDisp + "," + cfoundPrompt + "," + procThread + "," + cpromptCommands);}
							//#endif
							if (cfoundPrompt) {
								int cmdType = ccmd.getCommandType();
								try {
									if (cdisp.equals(disp)) {
										if ((crtnDisp != null) &&
												(cdisp instanceof TextBox)) {
											/** Paste into URL field from previous form.  */
											TextBox tbox = (TextBox)cdisp;
											if (cmdType == Command.OK) {
												setTxtObj(crtnObj,
														tbox.getString());
											}

											/** OK or Cancel the box go back to the return object(item)/form.  */
											if ((cmdType == Command.OK) ||
												(cmdType == Command.CANCEL)) {
												FeatureMgr.setCurrentObjMgr(
														this, crtnObj,
														crtnDisp);
											}
										}
									} else {
										//#ifdef DLOGGING
//@										if (fineLoggable) {logger.fine("run cacceptCmd,crejectCmd,cdisp,disp=" + logCmd(cacceptCmd) + "," + logCmd(crejectCmd) + "," + cdisp + "," + disp);}
										//#endif

										if (cmdType == Command.OK) {
											//#ifdef DLOGGING
//@											if (fineLoggable) {
//@												logger.fine("run cacceptCmd,crejectCmd,cmdType=" + logCmd(cacceptCmd) + "," + logCmd(crejectCmd) + "," + cmdType);
//@											}
											//#endif
											FeatureMgr.setCurrentMgr(this, null, disp);
											cmdFeatureUser.commandAction(cacceptCmd, disp);
											if (background && (runFeatureUser != null)) {
												runFeatureUser.run();
											}
										} else if (cmdType == Command.CANCEL) {
											FeatureMgr.setCurrentObjMgr(this, crtnObj, crtnDisp);
											if (crejectCmd != null) {
												cmdFeatureUser.commandAction(
														crejectCmd, disp);
												if (background && (runFeatureUser != null)) {
													runFeatureUser.run();
												}
											}
										}
									}
								} finally {
									synchronized(this) {
										acceptCmd = (Command)nullPtr;
										rtnDisp = (Displayable)nullPtr;
										rtnObj = (Item)nullPtr;
										promptDisp1 = (Displayable)nullPtr;
										promptDisp2 = (Displayable)nullPtr;
									}
								}
							} else if (cfoundDisp) {
								if ((cpromptCommands != null)
									&& cpromptCommands.containsKey(ccmd)) {
									String promptMsg =
										(String)cpromptCommands.get(ccmd);
									getPromptDisp(ccmd.getLabel(), promptMsg,
											ccmd, null, null, null, disp, null);
								} else if (cdisp.equals(disp)) {
									//#ifdef DLOGGING
//@									if (fineLoggable) {logger.fine("Equal cdisp,disp,cmdFeatureUser=" + logCmd(ccmd) + "," + cdisp + "," + disp + "," + cmdFeatureUser);}
									//#endif
									cmdFeatureUser.commandAction(ccmd, cdisp);
									if (background && (runFeatureUser != null)) {
										runFeatureUser.run();
									}
								}
							}
						} catch (Throwable e) {
							//#ifdef DLOGGING
//@							logger.severe("run commandAction caught ", e);
							//#endif
							System.out.println("run commandAction caught " + e + " " + e.getMessage());
							e.printStackTrace();
						} finally {
							synchronized(this) {
								foundDisp = false;
								foundPrompt = false;
								exCmd = (Command)nullPtr;
								exDisp = disp;
							}
						}
					} else {
						if (background && (runFeatureUser != null)) {
							runFeatureUser.run();
						}
					}
					lngTotalTimeTaken = (lngTaskEnd = System.currentTimeMillis()) - lngStart;
					lngTotalTimeTaken += System.currentTimeMillis() - lngTaskEnd;
					//#ifdef DLOGGING
//@					if (traceLoggable && (loop > 0)) {logger.trace("run loop,background,lngTaskEnd,lngTotalTimeTaken=" + this.loop + "," + background + "," + lngStart + "," + lngTaskEnd + "," + lngTotalTimeTaken);}
					//#endif
					synchronized(this) {
						while(pauseApp) {
							super.wait(DEFAULT_LOOP_TIME);
						}
					}
					if (lngTotalTimeTaken < LOOP_TIME) {
						synchronized(this) {
							if (loop == 0) {
								super.wait(LOOP_TIME - lngTotalTimeTaken);
							} else {
								loop--;
							}
						}
					}
				} catch (InterruptedException e) {
					break;
				}
			} while (background);
		} finally {
			//#ifndef DSMALLMEM
			if (procThread != null) {
				MiscUtil.removeThread(procThread);
			}
			//#endif
		}
	}

	/* Prompt if command is in prompt camands.  */
	public void commandAction(Command cmd, Displayable cdisp) {
		synchronized(this) {
			if (cdisp == disp) {
				foundDisp = true;
				foundPrompt = (cdisp == promptDisp1);
			} else {
				foundDisp = false;
				foundPrompt = ((cdisp == promptDisp1) || (cdisp == promptDisp2));
			}
			this.exCmd = (cmd == null) ? (Command)nullPtr : cmd;
			this.exDisp = cdisp;
		}
		startWakeup(true);
	}

	public void startWakeup(boolean wakeupThread) {
		if ( (procThread == null) || !procThread.isAlive()) {
			//#ifndef DSMALLMEM
			if (procThread != null) {
				MiscUtil.removeThread(procThread);
			}
			//#endif
			try {
				procThread = MiscUtil.getThread(this, disp.getClass().getName(),
						this, "startWakeup");
				procThread.start();
			} catch (Exception e) {
				System.err.println("Could not restart thread.");
				e.printStackTrace();
				//#ifdef DLOGGING
//@				logger.severe("Could not restart thread.", e);
				//#endif
			}
			//#ifdef DLOGGING
//@			logger.info(this.getClass().getName() +
//@					" thread not started.  Started now.");
			//#endif
		} else if (wakeupThread) {
			synchronized(this) {
				if (pauseApp) {
					pauseApp = false;
				}
			}
			wakeup(3);
		}
	}

	/* Notify us that we are finished. */
	public void wakeup(int incr) {
    
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("wakeup loop,incr=" + this.loop + "," + incr);}
		//#endif
		synchronized(this) {
			this.loop += incr;
			super.notify();
		}
	}

    public void setBackground(boolean background) {
        this.background = background;
    }

    static public void setMidlet(MIDlet midlet) {
        FeatureMgr.midlet = midlet;
    }

    static public RssReaderMIDlet getRssMidlet() {
		if (midlet instanceof RssReaderMIDlet) {
			return ((RssReaderMIDlet)FeatureMgr.midlet);
		} else {
			return null;
		}
    }

    static public MIDlet getMidlet() {
		return FeatureMgr.midlet;
    }

  /**
   * Return the selected index of the choice.  If nothing selected (-1),
   * return 0 if size &gt; 0, or -1 if 0 size.
   *
   * Constructor
   * @param choice - Choice interface
   *
   * @author Irv Bunton
   */
	static public int getSelectedIndex(Choice choice) {
		final int selIdx = choice.getSelectedIndex();
		if (selIdx != -1) {
			return selIdx;
		} else {
			if (choice.size() > 0) {
				choice.setSelectedIndex(0, true);
				return 0;
			} else {
				return -1;
			}
		}
	}

  /**
   * Get the place (index) in a list to insert/append an element if using
   * an inert, add, or append command.
   *
   * @param c - command selected by user
   * @param cplace - current place selected in list (-1 if no selection)
   * @param insCmd
   * @param addCmd
   * @param appndCmd
   * @param plist
   * @return    final
   * @author Irv Bunton
   */
	static int getPlaceIndex(Command c, Command insCmd,
							Command addCmd,
							Command appndCmd,
							javax.microedition.lcdui.Choice plist) {
		if ((insCmd == null) || (addCmd == null) || (appndCmd == null)) {
			return -1;
		}

		if ((c == insCmd) || (c == addCmd) || (c == appndCmd)) {
			final int blen = plist.size();
			int cplace = getSelectedIndex(plist);
			int addElem = (cplace == -1) ? blen : cplace;
			if ((c == addCmd) && (addElem < blen)) {
				addElem++;
			}
			if (c == appndCmd) {
				addElem = blen;
			}
			if ((addElem < 0) || (addElem > blen)) {
				addElem = blen;
			}
			return addElem;
		} else {
			return -1;
		}
	}

  /**
   * Create a TextField, set the layout and add it to the form.
   *
   * @param label
   * @param value
   * @param maxSize
   * @param constraint
   * @return    TextField
   * @author Irv Bunton
   */
	static public TextField getAddTextField(Form form, String label,
												String value, int maxSize,
												int constraint) {
        TextField testField = new TextField(label, value, maxSize, constraint);
		//#ifdef DMIDP20
		testField.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        form.append(testField);
		return testField;
	}

  /**
   * Create a StringItem, set the layout and add it to the form.
   *
   * @param label
   * @param value
   * @return    StringItem
   * @author Irv Bunton
   */
	static public StringItem getAddStringItem(Form form, String label,
												String value) {
        StringItem itemInfo = new StringItem(label, value);
		//#ifdef DMIDP20
		itemInfo.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        form.append(itemInfo);
		return itemInfo;
	}

  /**
   * Create a ChoiceGroup, set the layout and add it to the form.
   *
   * @param label
   * @param choices
   * @return    ChoiceGroup
   * @author Irv Bunton
   */
	static public ChoiceGroup getAddChoiceGroup(Form form, String label,
												String[] choices) {
        ChoiceGroup choiceGroup = new ChoiceGroup(label,
				                            Choice.EXCLUSIVE, choices, null);
		//#ifdef DMIDP20
		choiceGroup.setLayout(Item.LAYOUT_BOTTOM);
		//#endif
        form.append(choiceGroup);
		return choiceGroup;
	}

	public void setFrGui2SettingsNbr(TextField nfield, String setKey,
			Settings settings) {
		settings.setIntProperty(setKey, Integer.parseInt(nfield.getString()));
	}

	public void setFrSettings2GuiNbr(Settings settings, String setKey,
			TextField nfield) {
		nfield.setString(String.valueOf(settings.getIntProperty(setKey, 0)));
	}

    /** Initialize URL text Box */
    public static void initializeURLBox(final String url,
			Displayable rtnDisp, Object rtnObj) {
		TextBox boxURL = new TextBox("URL", url, 256, TextField.URL);
		FeatureMgr featureMgr = new FeatureMgr(boxURL, null);
		featureMgr.promptDisp1 = boxURL;
		featureMgr.rtnDisp = rtnDisp;
		featureMgr.rtnObj = rtnObj;
		boxURL.addCommand(new Command("OK", Command.OK, 4));
		boxURL.addCommand(new Command("Cancel", Command.CANCEL, 2));
        boxURL.setCommandListener(featureMgr);
		FeatureMgr.setCurrentMgr(featureMgr, null, boxURL);
    }
    
	public void showMe() {
		FeatureMgr.setCurrentMgr(this, null, disp);
	}

	public void showMe(Displayable alert) {
		FeatureMgr.setCurrentMgr(this, alert, disp);
	}

	public void showMe(Item item) {
		FeatureMgr.setCurrentItemMgr(this, item, disp);
	}

    static public void setMainDisp(Displayable mainDisp) {
        FeatureMgr.mainDisp = mainDisp;
    }

    static public Displayable getMainDisp() {
        return FeatureMgr.mainDisp;
    }

    static public void setDisplay(Display display) {
        FeatureMgr.display = display;
    }

    static public Display getDisplay() {
        return (FeatureMgr.display);
    }

	static final public void callSerially(Runnable r) {
		FeatureMgr.display.callSerially(r);
	}

    static public Displayable getCurrent() {
        return (FeatureMgr.display.getCurrent());
    }

	/* Set setCurrent display to alert and display and wakeup the featureMgr parameter. */
	/* If alert is null, it is not used.  */
	static final public void setCurrentMgr(FeatureMgr featureMgr,
								        Displayable alert, Displayable disp) {

		//#ifdef DTESTUI
//@		System.out.println("Test UI setCurrentMgr " + logDisp(alert) + "," + logDisp(disp));
		//#endif
		try {
			if (display != null) {
				if (alert != null) {
					display.setCurrent((Alert)alert, disp);
				} else {
					display.setCurrent(disp);
				}
			}
			FeatureMgr wfeatureMgr = wakeupDisp(disp, 2);
			if ((featureMgr != wfeatureMgr) && (featureMgr != null)) {
				featureMgr.wakeup(2);
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
//@			Logger.getLogger("FeatureMgr").severe("setCurrentMgr caught ", e);
			//#endif
			e.printStackTrace();
		}
	}

	/* Set setCurrent display item or display if MIDP 1.0 and wakeup the featureMgr parameter.  */
	static final public void setCurrentItemMgr(FeatureMgr featureMgr,
								        Item item, Displayable disp) {

		//#ifdef DTESTUI
//@		System.out.println("Test UI setCurrentItemMgr " + logItem(item) + "," + logDisp(disp));
		//#endif
		try {
			//#ifdef DMIDP20
			// To prevent loading form from being displayed instead of the
			// next form when that form has no items, show the load form
			// again as a workaround.
			if (featureMgr != null) {
				LoadingForm loadForm = featureMgr.getLoadForm(); 
				if ((loadForm != null) &&
						(FeatureMgr.getCurrent() == loadForm)) {
					display.setCurrent(loadForm);
				}
			}
			if (item != null) {
				display.setCurrentItem(item);
			} else {
				display.setCurrent(disp);
			}
			//#else
//@			display.setCurrent(disp);
			//#endif
			FeatureMgr wfeatureMgr = wakeupDisp(disp, 2);
			if ((featureMgr != wfeatureMgr) && (featureMgr != null)) {
				featureMgr.wakeup(2);
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
//@			Logger.getLogger("FeatureMgr").severe("setCurrentItemMgr caught ", e);
			//#endif
			e.printStackTrace();
		}
	}

	/* Set setCurrent display item or display if MIDP 1.0 and wakeup the featureMgr parameter.  */
	static final public void setCurrentObjMgr(FeatureMgr featureMgr,
								        Object obj, Displayable disp) {
		if ((obj != null) && (obj instanceof Item)) {
			FeatureMgr.setCurrentItemMgr(featureMgr, (Item)obj, disp);
		} else {
			FeatureMgr.setCurrentMgr(featureMgr, null, disp);
		}
	}

	/* Set setCurrent display item or display if MIDP 1.0 and wakeup the featureMgr for fdisp parameter. */
	static final public void setCurrentItemFeature(Displayable fdisp,
								        Item item, Displayable disp) {
		FeatureMgr featureMgr = (fdisp instanceof FeatureForm) ? ((FeatureForm)fdisp).getFeatureMgr() : ((FeatureList)fdisp).getFeatureMgr();
		FeatureMgr.setCurrentItemMgr(featureMgr, item, disp);
	}

	/* Wakeup the featureMgr for disp parameter with loop parameter.  */
	static public FeatureMgr wakeupDisp(Displayable disp, int loop) {
		FeatureMgr featureMgr;
		if (disp instanceof FeatureForm) {
			featureMgr = ((FeatureForm)disp).getFeatureMgr();
		} else if (disp instanceof FeatureList) {
			featureMgr = ((FeatureList)disp).getFeatureMgr();
		} else {
			return null;
		}
		if (featureMgr != null) {
			featureMgr.wakeup(loop);
			return featureMgr;
		} else {
			return null;
		}
	}

	/* Set setCurrent display to alert and display and wakeup the featureMgr for fdisp parameter. */
	final static public void setCurrentFeature(Displayable fdisp, Displayable alert, Displayable disp) {
		FeatureMgr featureMgr = (fdisp instanceof FeatureForm) ? ((FeatureForm)fdisp).getFeatureMgr() : ((FeatureList)fdisp).getFeatureMgr();
		featureMgr.setCurrentMgr(featureMgr, alert, disp);
	}

	/* Set setCurrent display to alert and display and wakeup the featureMgr for fdisp parameter if not null or cmainDisp if not null or disp.  */
	final static public void setCurrentAlt(Displayable cmainDisp, Displayable fdisp, Displayable alert, Displayable disp) {

		//#ifdef DTESTUI
//@		System.out.println("Test UI setCurrentAlt " + logDisp(alert) + "," + logDisp(disp));
		//#endif
		try {
			if (fdisp != null) {
				setCurrentFeature(fdisp, alert, disp);
				if (fdisp != disp) {
					wakeupDisp(disp, 2);
				}
			} else if (cmainDisp != null) {
				setCurrentFeature(cmainDisp, alert, disp);
				if (cmainDisp != disp) {
					wakeupDisp(disp, 2);
				}
			} else {
				if ((disp instanceof FeatureForm) ||
						(disp instanceof FeatureList)) {
					setCurrentFeature(disp, alert, disp);
				} else {
					FeatureMgr.setCurrentMgr(null, alert, disp);
				}
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
//@			Logger.getLogger("FeatureMgr").severe("setCurrentAlt caught ", e);
			//#endif
			e.printStackTrace();
		}
	}

	/* Set setCurrent display to alert and display and wakeup the featureMgr for fdisp parameter if not null or mainDisp if not null or disp. */
	static final public void setMainCurrentAlt(Displayable fdisp, Displayable alert, Displayable disp) {
		setCurrentAlt(mainDisp, fdisp, alert, disp);
	}

	/* Set setCurrent display to alert and display and wakeup the featureMgr parameter. */
	static final public void setCurrentMgr(FeatureMgr featureMgr,
								        Displayable disp) {
		setCurrentMgr(featureMgr, null, disp);
	}

	//#ifdef DMIDP20
	public Font getCustomFont() {
		RssReaderMIDlet midlet;
		final RssReaderSettings appSettings = ((midlet =
				FeatureMgr.getRssMidlet()) == null) ? null :
				midlet.getSettings();
		if (appSettings == null) {
			return null;
		}
		if (appSettings.getFontChoice() ==
				appSettings.DEFAULT_FONT_CHOICE) {
			return null;
		} else {
			Font defFont = Font.getDefaultFont();
			return Font.getFont(Font.FACE_SYSTEM, defFont.getStyle(),
					appSettings.getFontSize());
		}
	}
	//#endif

	//#ifdef DFULLVERS
	/* Restore previous values. */
	final static public void restorePrevValues(Item[] items, byte[] bdata) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("FeatureMgr");
//@		boolean cfinestLoggable = logger.isLoggable(Level.FINEST);
		//#endif
		DataInputStream dis = new DataInputStream(
				new ByteArrayInputStream(bdata));
		for (int ic = 0; ic < items.length; ic++) {
			try {
				final Item item = items[ic];
				if (item == null) {
					continue;
				}
				if (item instanceof ChoiceGroup) {
					((ChoiceGroup)item).setSelectedIndex(dis.readInt(),
						true);
					//#ifdef DLOGGING
//@					if (cfinestLoggable) {logger.finest("restorePrevValues set selected " + logItem(item));}
					//#endif
				} else if (item instanceof TextField) {
					final int len = dis.readInt();
					if (len > 0) {
						byte [] bvalue = new byte[len];
						final int blen = dis.read(bvalue);
						String value;
						try {
							value = new String(bvalue, 0, blen, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							value = new String(bvalue, 0, blen);
							//#ifdef DLOGGING
//@							logger.severe("restorePrevValues cannot convert value=" + value, e);
							//#endif
						}
						((TextField)item).setString(value);
						//#ifdef DLOGGING
//@						if (cfinestLoggable) {logger.finest("restorePrevValues set string " + logItem(item));}
						//#endif
					}
				}
			} catch (IOException e) {
				//#ifdef DLOGGING
//@				logger.severe("IOException reading selected.", e);
				//#else
				e.printStackTrace();
				//#endif
			}
		}
		if (dis != null) {
			/* Workaround for MicroEmulator. */
			try { ((InputStream)dis).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//#endif

	//#ifndef DSMALLMEM
	public Object[] action(Object[] reqs) {
		if ((reqs.length == 2) && (reqs[0] instanceof Short) &&
			((Short)reqs[0] == MiscUtil.SPAUSE_APP)) {
			synchronized(this) {
				if (!(pauseApp = ((Boolean)reqs[1]).booleanValue())) {
					wakeup(3);
				}
			}
			return new Object[] {MiscUtil.SPAUSE_APP, reqs[1]};
		} else {
			return null;
		}
	}
	//#endif


    public void setLoadForm(LoadingForm dispLoadForm) {
        this.dispLoadForm = dispLoadForm;
    }

    public LoadingForm getLoadForm() {
        return (dispLoadForm);
    }

	//#ifdef DFULLVERS
	/* Store current values. */
	final static public byte[] storeValues(Item[] items) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("FeatureMgr");
//@		boolean cfinestLoggable = logger.isLoggable(Level.FINEST);
		//#endif
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);
		for (int ic = 0; ic < items.length; ic++) {
			try {
				final Item item = items[ic];
				if (item == null) {
					continue;
				}
				if (item instanceof ChoiceGroup) {
					dout.writeInt(((ChoiceGroup)item).getSelectedIndex());
					//#ifdef DLOGGING
//@					if (cfinestLoggable) {logger.finest("storeValues stored selected " + logItem(item));}
					//#endif
				} else if (item instanceof TextField) {
					final String value = ((TextField)item).getString();
					byte [] bvalue;
					try {
						bvalue = value.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						bvalue = value.getBytes();
						//#ifdef DLOGGING
//@						logger.severe("storeValues cannot store value=" + value, e);
						//#endif
					}
					dout.writeInt(bvalue.length);
					if (bvalue.length > 0) {
						dout.write(bvalue, 0, bvalue.length);
					}
					//#ifdef DLOGGING
//@					if (cfinestLoggable) {logger.finest("storeValues store string " + logItem(item));}
					//#endif
				}
			} catch (IOException e) {
				//#ifdef DLOGGING
//@				logger.severe("IOException storing selected.", e);
				//#else
				e.printStackTrace();
				//#endif
			}
		}
		//#ifdef DLOGGING
//@		if (cfinestLoggable) {logger.finest("bout.toByteArray().length=" + bout.toByteArray().length);}
		//#endif
		if (dout != null) {
			try { dout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bout.toByteArray();
	}
	//#endif

  /**
   * Create a new command using the standard parms
   *
   * @param label - command label
   * @param longLabel - long command label (ignored if not MIDP 2.0)
   * @param commandType - Command type
   * @param priority - Command priority
   * @return    Command
   * @author Irv Bunton
   */
    public static Command getCmdAdd(Displayable disp, String label,
			String longLabel, int commandType, int priority) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("FeatureMgr");
//@		logger.finest("getCmdAdd label,longLabel,commandType,priority=" + label + "," + longLabel + "," + commandType + "," + priority);
		//#endif
		Command ncmd;
		//#ifdef DMIDP20
		if (longLabel != null) {
			disp.addCommand(ncmd = new Command(label, longLabel, commandType,
						priority));
		} else {
			//#endif
			disp.addCommand(ncmd = new Command(label, commandType, priority));
			//#ifdef DMIDP20
		}
		//#endif
		return ncmd;
	}

  /**
   * Create a new command using the standard parms
   *
   * @param label - command label
   * @param longLabel - command long label
   * @param commandType - Command type
   * @param priority - Command priority
   * @param prompt - Command prompt
   * @return    Command
   * @author Irv Bunton
   */
    public static Command getCmdAddPrompt(Displayable disp, String label,
			String longLabel, int commandType, int priority, String prompt) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("FeatureMgr");
//@		logger.finest("getCmdAddPrompt label,commandType,priority=" + label + "," + commandType + "," + priority);
		//#endif
		Command ncmd;
		if (disp instanceof FeatureForm) {
			((FeatureForm)disp).addPromptCommand(ncmd =
				getCmdAdd(disp, label, longLabel, commandType, priority),
				prompt);
		} if (disp instanceof FeatureList) {
			((FeatureList)disp).addPromptCommand(ncmd =
				getCmdAdd(disp, label, longLabel, commandType, priority),
				prompt);
		} else {
			ncmd = getCmdAdd(disp, label, longLabel, commandType, priority);
		}
		return ncmd;
	}

	//#ifdef DFULLVERS
  /**
   * Get the image for the image path.
   *
   * @param imagePath - Path for image
   * @return    Image - image for path
   *
   * @author Irv Bunton
   */
	public static Image getImage(final String imagePath, LoadingForm loadForm) {
		//#ifdef DLOGGING
//@		Logger logger;
//@		(logger = Logger.getLogger("FeatureMgr")).finest("getImage imagePath,loadForm=" + imagePath + "," + loadForm);
		//#endif
		Image image = null;
		CauseException wce = null;
		CauseException ce = null;
		try {
			try {
				// createImage("/icons/unread.png") does not always work
				// with the emulator.  so, I do an alternate which is
				// effectively the same thing.
				image = Image.createImage(imagePath);
				//#ifdef DLOGGING
//@				Logger.getLogger("FeatureMgr").finest("getImage image with,height=" + image.getWidth() + "," + image.getHeight());
				//#endif
			} catch(IOException e) {
				wce = new CauseException("Error while getting createImage: " + imagePath,
					e);
				//#ifdef DMIDP20
				InputStream is =
						imagePath.getClass().getResourceAsStream(imagePath);
				if (is == null) {
					throw new IOException("Cannot open resource " + imagePath);
				}
				image = Image.createImage(is);
				is.close();
				//#endif
				//#ifdef DLOGGING
//@				logger.warning(
//@						"getImage Could not get icon, alternate worked " +
//@						"icons ex: ", e);
				//#endif
			}
		} catch(Exception e) {
			ce = new CauseException("Error while getting image: " + imagePath,
					e);
			//#ifdef DLOGGING
//@			logger.severe(ce.getMessage(), ce);
			//#endif
			System.err.println("Error while getting mark image: " + e.toString());
		}
		if (loadForm != null) {
			if (ce != null) {
				if (wce != null) {
					loadForm.addExc(wce.getMessage(), wce);
				}
				loadForm.addExc(ce.getMessage(), ce);
			}
		}
		return image;
		
	}
	//#endif

	//#ifdef DTESTLOGMIN
//@	static String logCmd(Command cmd) {
//@	  return ((cmd == null) ? "null command" : (new StringBuffer(
//@			cmd.getLabel()).append(",").append(cmd.getCommandType()).append(
//@				",").append(cmd.getPriority()).toString()));
//@	}
//@
//@	static String logItem(Item citem) {
//@		if (citem == null) {
//@			return "null item";
//@		} else {
//@			StringBuffer sb = new StringBuffer(citem.getLabel());
//@			if (citem instanceof ChoiceGroup) {
//@				sb.append(",").append(((ChoiceGroup)citem).getSelectedIndex());
//@			} else if (citem instanceof TextField) {
//@				sb.append(",").append(((TextField)citem).getString());
//@			}
//@			return sb.toString();
//@		}
//@	}
//@
//@	static String logDisp(Displayable cdisp) {
//@		if (cdisp == null) {
//@			return "null displyable";
//@		} else {
//@			StringBuffer sb = new StringBuffer(cdisp.getClass().getName());
//@			String title;
//@			if (cdisp instanceof Form) {
//@				title = ((Form)cdisp).getTitle();
//@			} else if (cdisp instanceof List) {
//@				title = ((List)cdisp).getTitle();
//@			} else {
//@				title = "";
//@			}
//@			return sb.append("," + title).toString();
//@		}
//@	}
//@
	//#endif

	static public Object[] initSettingsEnabled(LoadingForm loadForm
												//#ifdef DLOGGING
//@												,Logger logger
//@												,boolean fineLoggable
												//#endif
			) {
		Object[] arrsettings = new Object[] {null, null, null, null};
		RssReaderSettings appSettings = null;
		Settings             settings = null;
		boolean firstTime = false;
		boolean itunesEnabled = false;
		try {
			appSettings = RssReaderSettings.getInstance();
			arrsettings[0] = appSettings;
			Throwable le = appSettings.getLoadExc();
			if (le != null) {
				loadForm.recordExcForm("Error while loading settings.", le);
			}
			try {
				settings = appSettings.getSettingsInstance();
				arrsettings[1] = settings;
				firstTime = !settings.isInitialized();
				arrsettings[2] = new Boolean(firstTime);
				itunesEnabled = appSettings.getItunesEnabled();
				arrsettings[3] = new Boolean(itunesEnabled);
			} catch(Exception e) {
				loadForm.recordExcForm(
						"Internal error.  Error while getting settings/stored bookmarks",
						e);
			}
		} catch(Exception e) {
			loadForm.recordExcForm("Error while loading settings.", e);
		}
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("Constructor appSettings,settings,firstTime,itunesEnabled=" + appSettings + "," + settings + "," + firstTime + "," + itunesEnabled);}
		//#endif
		return arrsettings;
	}

	static public Object[] getSysProperty(String key, String def, String msg,
										LoadingForm loadForm) {
		Object[] res = new Object[] {null, null};
		try {
			res[0] = System.getProperty(key);
			if (res[0] == null) {
				res[0] = def;
			}
			//#ifdef DLOGGING
//@			Logger.getLogger("FeatureMgr").finest("getSysProperty key,def,res[0],[1]=" + key + "," + def + "," + res[0] + "," + res[1]);
			//#endif
		} catch (Throwable e) {
			CauseException ce = new CauseException(msg, e);
			if (loadForm == null) {
				System.out.print(ce.getMessage());
			} else {
				loadForm.recordExcForm(msg, ce);
			}
			res[1] = ce.getMessage();
		}
		return res;
	}

	static public boolean getSysPropStarts(String key, String def, String msg,
										LoadingForm loadForm, String startStr) {
		Object[] objs = getSysProperty(key, def, msg, loadForm);
		if (objs[0] != null) {
			return ((String)objs[0]).toLowerCase().startsWith(startStr);
		} else {
			return false;
		}
	}

	static public Object[] getSysPermission(String permission, String key,
										    String def,
										    String msg, LoadingForm loadForm) {
		CauseException ce = null;
		Object[] res = new Object[] {null, null, null, null};
		//#ifdef DMIDP20
		try {
			Object[] rese = (key != null) ? getSysProperty(key, def, msg, loadForm) : new Object[] {null, null};
			res[2] = rese[0];
			res[3] = rese[1];
			res[0] = new Integer(midlet.checkPermission(permission));
			//#ifdef DLOGGING
//@			Logger.getLogger("FeatureMgr").finest("getSysPermission permission,key,def,res[0],[1],[2],[3]=" + permission + "," + key + "," + def + "," + res[0]  + "," + res[1]  + "," + res[2]+ "," + res[3]);
			//#endif
			if (((Integer)res[0]).intValue() == -1) {
				ce = new CauseException("Undefined permission may require user permission " + msg);
			}
		} catch (Throwable e) {
			ce = new CauseException(msg, e);
			res[0] = new Integer(-2);
		}
		if (ce != null) {
			res[1] = ce.getMessage();
			if (loadForm == null) {
				System.out.print(ce.getMessage());
			} else {
				loadForm.recordExcForm(msg, ce);
			}
		}
		//#else
//@		res[0] = new Integer(-3);
		//#endif
		//#ifdef DLOGGING
//@		Logger.getLogger("FeatureMgr").finest("getSysPermission 2 key,def,res[0],[1],[2],[3]=" + key + "," + def + "," + res[0] + "," + res[1] + "," + res[2] + "," + res[3]);
		//#endif
		return res;
	}

	public Object[] jsr75Avail() {
		boolean hasjsr75 = false;
		Object[] ojsr75Avail = new Object[] {null, null, null};
		//#ifdef DMIDP10
//@		Object[] ojsr75 = FeatureMgr.getSysProperty(
//@				"microedition.io.file.FileConnection.version", null,
//@				"Unable to get JSR-75 FileConnection", null);
//@		ojsr75Avail[2] = ojsr75[0];
		//#else
		Object[] ojsr75 = 
				FeatureMgr.getSysPermission(
				"javax.microedition.io.Connector.file.read",
				"microedition.io.file.FileConnection.version", null,
				"Unable to get JSR-75 FileConnection", null);
		int ijsr75 = ((Integer)ojsr75[0]).intValue();
		if (ijsr75 >= 0) {
			hasjsr75 = ijsr75 == 1;
			ojsr75Avail[1] = new Boolean(ijsr75 >= 0);
		} else {
		}
		ojsr75Avail[2] = ojsr75[2];
		//#endif
		if (!hasjsr75) {
			hasjsr75 = (ojsr75Avail[2] != null);
		}
		if (!hasjsr75) {
			try {
				//#ifdef DCLDCV10
				this.getClass().
				//#else
//@				Class.
				//#endif
					forName("javax.microedition.io.file.FileConnection");
				hasjsr75 = true;
			} catch (Throwable e) {
			}
		}
		ojsr75Avail[0] = new Boolean(hasjsr75);
		return ojsr75Avail;
	}

	//#ifdef DJSR75
//@	/* Set flag to show find files list.
//@	   fileRtnForm - Form to return to after file finished.
//@	   fileURL - Text field that has URL to put file URL into as well
//@	   			 as field to go back to if 2.0 is valid.
//@	*/
//@	final public void getFindFiles( final boolean selectDir,
//@			String selectMsg, String noSelectMsg, String findTitle,
//@			final Displayable fileRtnDisp, final Object fileRtnObj) {
//@		LoadingForm cloadForm = null;
//@		try {
//@			cloadForm = LoadingForm.getLoadingForm(
//@					(selectDir ? selectMsg :
//@							((noSelectMsg == null) ? selectMsg : noSelectMsg)),
//@						fileRtnDisp, null);
//@
//@			final Class fileSelectorMgrClass =
				//#ifdef DCLDCV10
//@				this.getClass().
				//#else
//@				Class.
				//#endif
//@					forName("org.kablog.kgui.KFileSelectorMgr");
//@			final CmdReceiver fileSelectorMgr =
//@				(CmdReceiver)fileSelectorMgrClass.newInstance();
			//#ifdef DLOGGING
//@			if (finestLoggable) {logger.finest("selectDir,selectMsg,noSelectMsg,fileRtnDisp,fileRtnObj=" + selectDir + "," + selectMsg  + "," + noSelectMsg + "," + fileRtnDisp + "," + fileRtnObj);}
			//#endif
//@			Object[] rtn = fileSelectorMgr.action(new Object []
//@					{MiscUtil.SINIT_OBJ, new Boolean(selectDir), findTitle,
//@					fileRtnDisp, fileRtnObj, cloadForm});
//@			if (rtn[0] != null) {
//@				throw (Throwable)rtn[0];
//@			}
//@		} catch(ClassNotFoundException ex) {
//@			cloadForm.recordExcForm("Internal error JSR-75 not available or " +
//@					"cannot be loaded.", ex);
//@		} catch(ClassCastException ex) {
//@			cloadForm.recordExcForm("Internal error loading/initializing " +
//@					"class cannot be loaded.", ex);
//@		} catch(OutOfMemoryError ex) {
//@			cloadForm.recordExcForm("Out Of Memory Error getting " +
//@					"file form.", ex);
//@		} catch (Throwable t) {
//@			cloadForm.recordExcForm("Internal error getting file " +
//@					"form.", t);
//@		}
//@	}
	//#endif

	static public void setTxtObj(Object txtObj, String value) {
		if (txtObj instanceof TextField) {
			TextField tfld = (TextField)txtObj;
			tfld.setString(value);
		} else if (txtObj instanceof StringItem) {
			StringItem sfld = (StringItem)txtObj;
			sfld.setText(value);
		} else if (txtObj instanceof StringBuffer) {
			StringBuffer sbrtn = (StringBuffer)txtObj;
			sbrtn.setLength(0);
			sbrtn.append(value);
		}
	}

}
