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
*/

// Expand to define MIDP define
//#define DMIDP20
// Expand to define CLDC define
//#define DCLDC10
// Expand to define test define
//#define DNOTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define logging define
//#define DNOLOGGING

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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.TextBox;
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
//#else
//@import com.substanceofcode.testlcdui.ChoiceGroup;
//@import com.substanceofcode.testlcdui.Form;
//@import com.substanceofcode.testlcdui.List;
//@import com.substanceofcode.testlcdui.TextField;
//#endif

import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.utils.Settings;
//#ifdef DMIDP20
import com.substanceofcode.utils.CmdReceiver;
//#endif
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;
import com.substanceofcode.rssreader.presentation.LoadingForm;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel. */

public class FeatureMgr implements CommandListener,
	   Runnable
		//#ifdef DMIDP20
	   ,CmdReceiver
		//#endif
{

	final       Object nullPtr = null;
	private Hashtable promptCommands = null;
	static private Displayable mainDisp = null;
	private Displayable disp;
	private LoadingForm dispLoadForm = null;
    static private Display display = null; // The display for this MIDlet
	private Displayable promptDisp1;
	private Displayable promptDisp2;
    static final private long DEFAULT_LOOP_TIME = 500L;
    static private long LOOP_TIME = DEFAULT_LOOP_TIME;  // Time to wait for loops.
	static private Command nullCmd = null;
	private Command origCmd = nullCmd;
	private boolean foundDisp = false;
	private boolean foundPrompt = false;
	protected Command exCmd = nullCmd;
	private Displayable exDisp = null;
	static protected RssReaderMIDlet midlet = null;
    private Form        urlRrnForm = null; // The form to return to for URL box
    private TextField   urlRrnItem = null; // The item to return to for URL box
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
//@		if (fineLoggable) {logger.fine("Starting FeatureMgr " + disp.getClass().getName());}
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
//@				if (finestLoggable && (loop > 0)) {logger.finest("run loop,background,cmdFeatureUser,runFeatureUser,foundDisp,foundPrompt,exCmd,exDisp,origCmd=" + this.loop + "," + background + "," + cmdFeatureUser + "," + runFeatureUser + "," + foundDisp + "," + foundPrompt + "," + logCmd(exCmd) + "," + exDisp + "," + logCmd(origCmd));}
				//#endif
				try {
					Command ccmd = nullCmd;
					Displayable cdisp = null;
					Command corigCmd = nullCmd;
					boolean cfoundDisp = false;
					boolean cfoundPrompt = false;
					synchronized(this) {
						cfoundDisp = foundDisp;
						cfoundPrompt = foundPrompt;
						if ((cfoundDisp || cfoundPrompt) && (exCmd != nullCmd)) {
							ccmd = exCmd;
							cdisp = exDisp;
							corigCmd = origCmd;
						}
					}
					if ((ccmd != nullCmd) && (cdisp != null)) {
						try {
							Hashtable cpromptCommands = null;
							synchronized(this) {
								cpromptCommands = promptCommands;
							}
							//#ifdef DLOGGING
//@							if (fineLoggable) {logger.fine("run disp,cdisp,ccmd,corigCmd,cfoundDisp,cfoundPrompt,thread,cpromptCommands="  + disp + "," + cdisp + "," + logCmd(ccmd) + "," + logCmd(corigCmd) + "," + cfoundDisp + "," + cfoundPrompt + "," + procThread + "," + cpromptCommands);}
							//#endif
							if (cfoundDisp && (cpromptCommands != null)
									&& cpromptCommands.containsKey(ccmd)) {
								synchronized(this) {
									origCmd = ccmd;
								}
								String promptMsg = (String)cpromptCommands.get(ccmd);
								// Due to a quirk on T637 (MIDP 1.0), we need to create a form
								// before the alert or the alert will not be seen.
								Form formAlert = new Form(ccmd.getLabel());
								formAlert.append(promptMsg);
								formAlert.addCommand(new Command("OK", Command.OK, 0));
								formAlert.addCommand(new Command("Cancel", Command.CANCEL, 1));
								formAlert.setCommandListener(this);
								FeatureMgr.setCurrentMgr(this, null, formAlert);
								Alert promptAlert = new Alert(ccmd.getLabel(),
										promptMsg, null,
										AlertType.CONFIRMATION);
								promptAlert.setTimeout(Alert.FOREVER);
								promptAlert.addCommand(new Command("OK", Command.OK, 0));
								promptAlert.addCommand(new Command("Cancel", Command.CANCEL, 1));
								promptAlert.setCommandListener(this);
								FeatureMgr.setCurrentMgr(this, promptAlert, formAlert);
								synchronized(this) {
									promptDisp1 = formAlert;
									promptDisp2 = promptAlert;
								}
							} else if ((urlRrnForm != null) &&
									   (cdisp instanceof TextBox)) {
								/** Paste into URL field from previous form.  */
								int cmdType = ccmd.getCommandType();
								if (cmdType == Command.OK) {
									urlRrnItem.setString(((TextBox)cdisp).getString());
									FeatureMgr.setCurrentItemMgr(this, urlRrnItem, urlRrnForm);
									// Free memory
									urlRrnForm = (Form)nullPtr;
									// Free memory
									urlRrnItem = (TextField)nullPtr;
								}
								
								/** Cancel the box go back to the return form.  */
								if (cmdType == Command.CANCEL) {
									FeatureMgr.setCurrentItemMgr(this, urlRrnItem, urlRrnForm);
									// Free memory
									urlRrnForm = (Form)nullPtr;
									urlRrnItem = (TextField)nullPtr;
								}
							} else if (cfoundDisp && cdisp.equals(disp)) {
								//#ifdef DLOGGING
//@								if (fineLoggable) {logger.fine("Equal cdisp,disp,cmdFeatureUser=" + logCmd(ccmd) + "," + cdisp + "," + disp + "," + cmdFeatureUser);}
								//#endif
								cmdFeatureUser.commandAction(ccmd, cdisp);
								if (background && (runFeatureUser != null)) {
									runFeatureUser.run();
								}
							}
							if (cfoundPrompt && !cdisp.equals(disp)) {
								//#ifdef DLOGGING
//@								if (fineLoggable) {logger.fine("run corigCmd,cdisp,disp=" + logCmd(corigCmd) + "," + cdisp + "," + disp);}
								//#endif

								try {
									if ((ccmd.getCommandType() == Command.OK)
									//#ifdef DMIDP20
										   || ccmd.equals(Alert.DISMISS_COMMAND)
									//#endif
											) {
										//#ifdef DLOGGING
//@										if (fineLoggable) {
//@											logger.fine("run corigCmd,type=" + logCmd(corigCmd));
//@										}
										//#endif
										FeatureMgr.setCurrentMgr(this, null, disp);
										cmdFeatureUser.commandAction(corigCmd, disp);
										if (background && (runFeatureUser != null)) {
											runFeatureUser.run();
										}
									} else if (ccmd.getCommandType() == Command.CANCEL) {
										FeatureMgr.setCurrentMgr(this, null, disp);
									}
								} finally {
									synchronized(this) {
										origCmd = nullCmd;
										promptDisp1 = disp;
										promptDisp2 = disp;
									}
								}
							}
						} catch (Throwable e) {
							//#ifdef DLOGGING
//@							logger.severe("run commandAction caught ", e);
							//#endif
							System.out.println("run commandAction caught " + e + " " + e.getMessage());
						} finally {
							synchronized(this) {
								foundDisp = false;
								foundPrompt = false;
								exCmd = nullCmd;
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
						if (pauseApp) {
							super.wait();
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
			//#ifdef DMIDP20
			if (procThread != null) {
				MiscUtil.removeThread(procThread);
			}
			//#endif
		}
	}

	/* Prompt if command is in prompt camands.  */
	public void commandAction(Command cmd, Displayable cdisp) {
		synchronized(this) {
			foundDisp = (cdisp == disp);
			foundPrompt = (cdisp != promptDisp1) &&
				((cdisp == promptDisp1) || (cdisp == promptDisp2));
			this.exCmd = (cmd == null) ? nullCmd : cmd;
			this.exDisp = cdisp;
		}
		startWakeup(true);
	}

	public void startWakeup(boolean wakeupThread) {
		if ( (procThread == null) || !procThread.isAlive()) {
			//#ifdef DMIDP20
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
	public void wakeup(int loop) {
    
		synchronized(this) {
			this.loop += loop;
			super.notify();
		}
	}

    public void setBackground(boolean background) {
        this.background = background;
    }

    static public void setMidlet(RssReaderMIDlet midlet) {
        FeatureMgr.midlet = midlet;
    }

    static public RssReaderMIDlet getMidlet() {
        return (FeatureMgr.midlet);
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
							javax.microedition.lcdui.List plist) {
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

    /** Initialize URL text Box */
    public static void initializeURLBox(final String url,
			Form prevForm, TextField prevItem) {
		TextBox boxURL = new TextBox("URL", url, 256, TextField.URL);
		FeatureMgr featureMgr = new FeatureMgr(boxURL, null);
		featureMgr.urlRrnForm = prevForm;
		featureMgr.urlRrnItem = prevItem;
		boxURL.addCommand(new Command("OK", Command.OK, 1));
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
//@		String title = "";
//@		if (disp instanceof Form) {
//@			title = ((Form)disp).getTitle();
//@		} else if (disp instanceof List) {
//@			title = ((List)disp).getTitle();
//@		}
//@		String alertTitle;
//@		if (alert instanceof Form) {
//@			alertTitle = "," + ((Form)alert).getTitle();
//@		} else if (alert instanceof List) {
//@			alertTitle = "," + ((List)alert).getTitle();
//@		} else {
//@			alertTitle = "";
//@		}
//@		System.out.println("Test UI setCurrent " + disp.getClass().getName() + alertTitle +  "," + title);
		//#endif
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
	}

	/* Set setCurrent display item or display if MIDP 1.0 and wakeup the featureMgr parameter.  */
	static final public void setCurrentItemMgr(FeatureMgr featureMgr,
								        Item item, Displayable disp) {

		//#ifdef DTESTUI
//@		String title = "";
//@		if (disp instanceof Form) {
//@			title = ((Form)disp).getTitle();
//@		} else if (disp instanceof List) {
//@			title = ((List)disp).getTitle();
//@		}
//@		System.out.println("Test UI setCurrentItem " + disp.getClass().getName() + item.getLabel() +  "," + title);
		//#endif
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
		display.setCurrentItem(item);
		//#else
//@		display.setCurrent(disp);
		//#endif
		FeatureMgr wfeatureMgr = wakeupDisp(disp, 2);
		if ((featureMgr != wfeatureMgr) && (featureMgr != null)) {
			featureMgr.wakeup(2);
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
				setCurrentFeature((FeatureForm)disp, alert, disp);
			} else {
				FeatureMgr.setCurrentMgr(null, alert, disp);
			}
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
		final RssReaderSettings appSettings = FeatureMgr.getMidlet().getSettings();
		if (appSettings == null) {
			return null;
		}
		if (appSettings.getFontChoice() ==
				RssReaderSettings.DEFAULT_FONT_CHOICE) {
			return null;
		} else {
			Font defFont = Font.getDefaultFont();
			return Font.getFont(Font.FACE_SYSTEM, defFont.getStyle(),
					appSettings.getFontSize());
		}
	}
	//#endif

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

	//#ifdef DMIDP20
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

	/* Store current values. */
	final static public byte[] storeValues(Item[] items) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("FeatureMgr");
//@		boolean cfinestLoggable = logger.isLoggable(Level.FINEST);
		//#endif
		ByteArrayOutputStream bout = new
				ByteArrayOutputStream();
		DataOutputStream dout = new
				DataOutputStream(bout);
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


	//#ifdef DLOGGING
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

}
