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

import javax.microedition.midlet.MIDlet;
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
import javax.microedition.lcdui.TextBox;
//#ifndef DTESTUI
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
//#else
//@import com.substanceofcode.testlcdui.ChoiceGroup;
//@import com.substanceofcode.testlcdui.Form;
//@import com.substanceofcode.testlcdui.TextField;
//#endif

import com.substanceofcode.utils.MiscUtil;
//#ifdef DMIDP20
import com.substanceofcode.utils.CmdReceiver;
//#endif
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
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

	private Hashtable promptCommands = null;
	private Displayable disp;
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
	protected RssReaderMIDlet midlet;
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

	public FeatureMgr (RssReaderMIDlet midlet, Displayable disp) {
		this.midlet = midlet;
		this.disp = disp;
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("Starting FeatureMgr " + disp.getClass().getName());}
		//#endif
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
								midlet.setCurrent(formAlert);
								Alert promptAlert = new Alert(ccmd.getLabel(),
										promptMsg, null,
										AlertType.CONFIRMATION);
								promptAlert.setTimeout(Alert.FOREVER);
								promptAlert.addCommand(new Command("OK", Command.OK, 0));
								promptAlert.addCommand(new Command("Cancel", Command.CANCEL, 1));
								promptAlert.setCommandListener(this);
								midlet.setCurrent(promptAlert, formAlert);
								synchronized(this) {
									promptDisp1 = formAlert;
									promptDisp2 = promptAlert;
								}
							} else if( (urlRrnForm != null) &&
									   (cdisp instanceof TextBox)) {
								/** Paste into URL field from previous form.  */
								int cmdType = ccmd.getCommandType();
								if (cmdType == Command.OK) {
									urlRrnItem.setString( ((TextBox)cdisp).getString() );
									//#ifdef DMIDP20
									midlet.setCurrentItem( urlRrnItem );
									//#else
//@									midlet.setCurrent( urlRrnForm );
									//#endif
									// Free memory
									urlRrnForm = null;
									// Free memory
									urlRrnItem = null;
								}
								
								/** Cancel the box go back to the return form.  */
								if (cmdType == Command.CANCEL) {
									//#ifdef DMIDP20
									midlet.setCurrentItem( urlRrnItem );
									//#else
//@									midlet.setCurrent( urlRrnForm );
									//#endif
									// Free memory
									urlRrnForm = null;
									urlRrnItem = null;
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
										midlet.setCurrent(disp);
										cmdFeatureUser.commandAction(corigCmd, disp);
										if (background && (runFeatureUser != null)) {
											runFeatureUser.run();
										}
									} else if (ccmd.getCommandType() == Command.CANCEL) {
										midlet.setCurrent(disp);
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
					if(lngTotalTimeTaken < LOOP_TIME) {
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
		if ( (procThread == null) || !procThread.isAlive() ) {
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

    public void setMidlet(RssReaderMIDlet midlet) {
        this.midlet = midlet;
    }

    public RssReaderMIDlet getMidlet() {
        return (midlet);
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
		if( (insCmd == null ) || (addCmd == null ) || (appndCmd == null )) {
			return -1;
		}

		if( (c == insCmd ) || (c == addCmd ) || (c == appndCmd )) {
			final int blen = plist.size();
			int cplace = getSelectedIndex(plist);
			int addElem = (cplace == -1) ? blen : cplace;
			if(( c == addCmd ) && (addElem < blen)) {
				addElem++;
			}
			if (c == appndCmd ) {
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
        form.append( choiceGroup );
		return choiceGroup;
	}

    /** Initialize URL text Box */
    public static void initializeURLBox(RssReaderMIDlet midlet,
			final String url, Form prevForm, TextField prevItem) {
		TextBox boxURL = new TextBox("URL", url, 256, TextField.URL);
		FeatureMgr featureMgr = new FeatureMgr(midlet, boxURL);
		featureMgr.urlRrnForm = prevForm;
		featureMgr.urlRrnItem = prevItem;
		boxURL.addCommand(new Command("OK", Command.OK, 1));
		boxURL.addCommand(new Command("Cancel", Command.CANCEL, 2));
        boxURL.setCommandListener(featureMgr);
		midlet.setCurrent( boxURL );
    }
    
	//#ifdef DMIDP20
	public Font getCustomFont() {
		final RssReaderSettings appSettings = midlet.getSettings();
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

	/* Store current values. */
	final static public byte[] storeValues(Item[] items) {
		//#ifdef DLOGGING
//@		Logger logger = Logger.getLogger("FeatureMgr");
//@		boolean cfinestLoggable = logger.isLoggable(Level.FINEST);
		//#endif
		ByteArrayOutputStream bout = new
				ByteArrayOutputStream();
		DataOutputStream dout = new
				DataOutputStream( bout );
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
						dout.write( bvalue, 0, bvalue.length );
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

}
