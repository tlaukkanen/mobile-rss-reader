/*
 * PromptMgr.java
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

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* Form with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel.  */
final public class PromptMgr implements CommandListener {

	private Hashtable promptCommands = new Hashtable();
	private Displayable disp;
	private Command origCmd = null;
	private Command cmdOK;
	private Command cmdCancel;
	protected MIDlet midlet;
	private Alert promptAlert = null;
	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("PromptMgr");
	private boolean fineLoggable = false;
	private boolean finestLoggable = false;
	//#endif

	private CommandListener cmdListener;

	public PromptMgr (MIDlet midlet, Displayable disp) {
		this.midlet = midlet;
		this.disp = disp;
	}

    final public void setCommandListener(CommandListener cmdListener) {
		this.cmdListener = cmdListener;
    }

	final public void addPromptCommand(Command cmd, String prompt) {
		promptCommands.put(cmd, prompt);
	}

	final public void removeCommand(Command cmd) {
		promptCommands.remove(cmd);
	}

	/* Prompt if command is in prompt camands.  */
	public void commandAction(Command cmd, Displayable cdisp) {
		try {
			if (promptCommands.containsKey(cmd)) {
				if ((promptAlert == null) || !cdisp.equals(promptAlert)) {
					origCmd = cmd;
				}
				promptAlert = new Alert(cmd.getLabel(),
						(String)promptCommands.get(cmd), null,
						AlertType.INFO);
				promptAlert.setTimeout(Alert.FOREVER);
				cmdOK = new Command("OK", Command.OK, 0);
				promptAlert.addCommand(cmdOK);
				cmdCancel = new Command("Cancel", Command.CANCEL, 0);
				promptAlert.addCommand(cmdCancel);
				promptAlert.setCommandListener(this);
				Display.getDisplay(midlet).setCurrent(promptAlert);
				return;
			} else if (cmd.equals(cmdOK)
				//#ifdef DMIDP20
					   || cmd.equals(Alert.DISMISS_COMMAND)
				//#endif
						) {
				//#ifdef DLOGGING
				if (fineLoggable) {
					logger.fine("origCmd,type=" + origCmd.getLabel() + "," + origCmd.getCommandType());
				}
				//#endif
				Display.getDisplay(midlet).setCurrent(disp);
				cmdListener.commandAction(origCmd, disp);
			} else if (cmd.equals(cmdCancel)) {
				Display.getDisplay(midlet).setCurrent(disp);
				return;
			} else {
				Display.getDisplay(midlet).setCurrent(disp);
				cmdListener.commandAction(cmd, disp);
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("commandAction caught ", e);
			//#endif
			System.out.println("commandAction caught " + e + " " + e.getMessage());
		}
	}

}
