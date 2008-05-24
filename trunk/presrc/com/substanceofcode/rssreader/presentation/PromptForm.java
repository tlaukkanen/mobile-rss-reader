/*
 * PromptForm.java
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
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Image;
//#ifndef DTESTUI
import javax.microedition.lcdui.List;
//#else
import com.substanceofcode.testlcdui.Form;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel.  */
public class PromptForm extends Form {

	private Hashtable promptCommands = new Hashtable();
	private Command origCmd = null;
	private Command cmdOK;
	private Command cmdCancel;
	protected MIDlet midlet;
	private PromptForm pform;
	private Alert promptAlert = null;
	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("PromptForm");
	private boolean fineLoggable = false;
	private boolean finestLoggable = false;
	//#endif

	public PromptForm(MIDlet midlet, String title) {
		super(title);
		this.midlet = midlet;
		init();
	}

	private void init() {
		pform = this;
		//#ifdef DLOGGING
		fineLoggable = logger.isLoggable(Level.FINE);
		logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
		finestLoggable = logger.isLoggable(Level.FINEST);
		logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
		//#endif
	}

	public PromptForm(MIDlet midlet, String title, Item[] items) {
		super(title, items);
		this.midlet = midlet;
		init();
	}

	public void addPromptCommand(Command cmd, String prompt) {
		super.addCommand(cmd);
		promptCommands.put(cmd, prompt);
	}

	public void removeCommand(Command cmd) {
		super.removeCommand(cmd);
		promptCommands.remove(cmd);
	}

    public void setCommandListener(CommandListener cmdListener) {
		super.setCommandListener(new PromptHandler(cmdListener));
    }

	private class PromptHandler implements CommandListener {
		private CommandListener cmdListener;

		private PromptHandler (CommandListener cmdListener) {
			this.cmdListener = cmdListener;
		}

		/* Prompt if command is in prompt camands.  */
		public void commandAction(Command cmd, Displayable disp) {
			//#ifdef DTESTUI
			pform.outputCmdAct(cmd, disp);
			//#endif
			try {
				if (promptCommands.containsKey(cmd)) {
					if ((promptAlert == null) || !disp.equals(promptAlert)) {
						origCmd = cmd; System.out.println("here cact 7");
					}
					promptAlert = new Alert(cmd.getLabel(),
							(String)promptCommands.get(cmd), null,
							AlertType.CONFIRMATION); System.out.println("here cact 11");
					promptAlert.setTimeout(Alert.FOREVER); System.out.println("here cact 12");
					cmdOK = UiUtil.getCmdRsc("cmd.ok", Command.OK, 0); System.out.println("here cact 13");
					promptAlert.addCommand(cmdOK); System.out.println("here cact 14");
					cmdCancel = UiUtil.getCmdRsc("cmd.cancel", Command.CANCEL, 0); System.out.println("here cact 15");
					promptAlert.addCommand(cmdCancel); System.out.println("here cact 16");
					promptAlert.setCommandListener(this); System.out.println("here cact 17");
					Display.getDisplay(midlet).setCurrent(promptAlert); System.out.println("here cact 18");
					return;
				} else if (cmd.equals(cmdOK)
					//#ifdef DMIDP20
						   || cmd.equals(Alert.DISMISS_COMMAND)
					//#endif
							) {
					//#ifdef DLOGGING
					if (fineLoggable) {
						logger.fine("origCmd,type=" + origCmd.getLabel() + "," + origCmd.getCommandType()); System.out.println("here cact 26");
					}
					//#endif
					Display.getDisplay(midlet).setCurrent(pform);
					cmdListener.commandAction(origCmd, pform);
				} else if (cmd.equals(cmdCancel)) {
					Display.getDisplay(midlet).setCurrent(pform);
					return;
				} else {
					Display.getDisplay(midlet).setCurrent(pform);
					cmdListener.commandAction(cmd, disp); System.out.println("here cact 31");
				}
			} catch (Throwable e) {
				//#ifdef DLOGGING
				logger.severe("commandAction caught ", e); System.out.println("here cact 35");
				//#endif
				System.out.println("commandAction caught " + e + " " + e.getMessage()); System.out.println("here cact 37");
			}
		}
	}

}
