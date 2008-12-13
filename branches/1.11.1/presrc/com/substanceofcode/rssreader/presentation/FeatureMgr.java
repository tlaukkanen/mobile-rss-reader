/*
 * FeatureMgr.java
 *
 * Copyright (C) 2007 Irving Bunton
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

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define logging define
@DLOGDEF@

package com.substanceofcode.rssreader.presentation;

import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
//#ifndef DTESTUI
import javax.microedition.lcdui.Form;
//#else
import com.substanceofcode.testlcdui.Form;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel. */

public class FeatureMgr implements CommandListener, Runnable {

	private Hashtable promptCommands = null;
	private Displayable disp;
	private Command origCmd = null;
	protected Command exCmd = null;
	private Displayable exDisp = null;
	protected RssReaderMIDlet midlet;
    private boolean     background = false;   // Flag to continue looping
    private boolean     needWakeup = false;   // Flag to show need to wakeup
    private Thread      netThread = null;  // The thread for networking, etc

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("FeatureMgr");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

	private Runnable featureUser;

	public FeatureMgr (RssReaderMIDlet midlet, Displayable disp) {
		this.midlet = midlet;
		this.disp = disp;
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Starting FeatureMgr " + disp.getClass().getName());}
		//#endif
	}

    public void setCommandListener(CommandListener featureUser,
			boolean background) {
		synchronized(this) {
			this.featureUser = (Runnable)featureUser;
			this.background = background;
		}
		if (background) {
			if (featureUser == null) {
				throw new NullPointerException("featureUser is null.  Must have command listener");
			}
			startWakeup(false);
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("featureUser,background,calling thread,new thread=" + featureUser + "," + background + "," + Thread.currentThread() + "," + netThread);}
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
        long lngTimeTaken;
		do {
			try {
				Command ccmd = null;
				Displayable cdisp = null;
				Command corigCmd = null;
				synchronized(this) {
					if (exCmd != null) {
						ccmd = exCmd;
						cdisp = exDisp;
					}
					corigCmd = origCmd;
				}
				if ((ccmd != null) && (cdisp != null)) {
					try {
						Hashtable cpromptCommands = null;
						synchronized(this) {
							cpromptCommands = promptCommands;
						}
						//#ifdef DLOGGING
						if (fineLoggable) {logger.fine("disp,ccmd,cpromptCommands,corigCmd,thread=" + disp + "," + ccmd.getLabel() + "," + cpromptCommands + "," + "," + corigCmd + "," + Thread.currentThread());}
						//#endif
						if ((cpromptCommands != null)
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
							midlet.setCurrent(formAlert);
							Alert promptAlert = new Alert(ccmd.getLabel(),
									promptMsg, null,
									AlertType.CONFIRMATION);
							promptAlert.setTimeout(Alert.FOREVER);
							promptAlert.addCommand(new Command("OK", Command.OK, 0));
							promptAlert.addCommand(new Command("Cancel", Command.CANCEL, 1));
							promptAlert.setCommandListener(this);
							midlet.setCurrent(promptAlert, formAlert);
						} else if (cdisp.equals(disp)) {
							((CommandListener)featureUser).commandAction(ccmd, disp);
							featureUser.run();
						}
						if (!cdisp.equals(disp)) {
							//#ifdef DLOGGING
							if (fineLoggable) {logger.fine("corigCmd,cdisp,disp=" + corigCmd.getLabel() + "," + cdisp + "," + disp);}
							//#endif

							try {
								if ((ccmd.getCommandType() == Command.OK)
								//#ifdef DMIDP20
									   || ccmd.equals(Alert.DISMISS_COMMAND)
								//#endif
										) {
									//#ifdef DLOGGING
									if (fineLoggable) {
										logger.fine("corigCmd,type=" + corigCmd.getLabel() + "," + corigCmd.getCommandType());
									}
									//#endif
									midlet.setCurrent(disp);
									((CommandListener)featureUser).commandAction(corigCmd, disp);
									featureUser.run();
								} else if (ccmd.getCommandType() == Command.CANCEL) {
									midlet.setCurrent(disp);
								}
							} finally {
								synchronized(this) {
									origCmd = null;
								}
							}
						}
					} catch (Throwable e) {
						//#ifdef DLOGGING
						logger.severe("commandAction caught ", e);
						//#endif
						System.out.println("commandAction caught " + e + " " + e.getMessage());
					} finally {
						synchronized(this) {
							exCmd = null;
							exDisp = null;
						}
					}
				} else {
					featureUser.run();
				}
				lngStart = System.currentTimeMillis();
				lngTimeTaken = System.currentTimeMillis()-lngStart;
				if(lngTimeTaken<100L) {
					synchronized(this) {
						if (!needWakeup) {
							super.wait(75L-lngTimeTaken);
						}
						needWakeup = false;
					}
				}
			} catch (InterruptedException e) {
				break;
			}
		} while (background);
	}

	/* Prompt if command is in prompt camands.  */
	public void commandAction(Command cmd, Displayable cdisp) {
		synchronized(this) {
			this.exCmd = cmd;
			this.exDisp = cdisp;
		}
		startWakeup(true);
	}

	public void startWakeup(boolean wakeup) {
		if ( (netThread == null) || !netThread.isAlive() ) {
			try {
				netThread = new Thread(this);
				netThread.start();
			} catch (Exception e) {
				System.err.println("Could not restart thread.");
				e.printStackTrace();
				//#ifdef DLOGGING
				logger.severe("Could not restart thread.", e);
				//#endif
			}
			//#ifdef DLOGGING
			logger.info(this.getClass().getName() +
					" thread not started.  Started now.");
			//#endif
		} else if (wakeup) {
			wakeUp();
		}
	}

	/* Notify us that we are finished. */
	public void wakeUp() {
    
		synchronized(this) {
			needWakeup = true;
			super.notify();
		}
	}

    public void setBackground(boolean background) {
        this.background = background;
    }

    public void setMidlet(RssReaderMIDlet midlet) {
        this.midlet = midlet;
    }

    public RssReaderMIDlet getMidlet() {
        return (midlet);
    }

}
