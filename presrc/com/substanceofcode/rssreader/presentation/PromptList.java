/*
 * PromptList.java
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
// Expand to define test ui define
@DTESTUIDEF@
// Expand to define logging define
@DLOGDEF@

package com.substanceofcode.rssreader.presentation;

import java.util.Hashtable;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
//#ifdef DTESTUI
import com.substanceofcode.testlcdui.Form;
import com.substanceofcode.testlcdui.List;
//#else
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
//#endif

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* List with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel.  */
public class PromptList extends List implements CommandListener {
	private CommandListener cmdListener;

	private Hashtable promptCommands = new Hashtable();
	private Command origCmd = null;
	private Command cmdOK;
	private Command cmdCancel;
	protected MIDlet midlet;
	private Alert promptAlert = null;
	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("PromptList");
	private boolean fineLoggable = false;
	private boolean finestLoggable = false;
	//#endif

	public PromptList(MIDlet midlet, String title, int listType) {
		super(title, listType);
		this.midlet = midlet;
		init();
	}

	private void init() {
		//#ifdef DLOGGING
		fineLoggable = logger.isLoggable(Level.FINE);
		logger.fine("obj,fineLoggable=" + this + "," + fineLoggable);
		finestLoggable = logger.isLoggable(Level.FINEST);
		logger.fine("obj,finestLoggable=" + this + "," + finestLoggable);
		//#endif
	}

	public PromptList(MIDlet midlet, String title, int listType,
			          String [] stringElements,  Image[] imageElements) {
		super(title, listType, stringElements, imageElements);
		this.midlet = midlet;
		init();
	}

	/* Prompt if command is in prompt camands.  */
	public void commandAction(Command cmd, Displayable disp) {
        try {
			if (promptCommands.containsKey(cmd)) {
				if ((promptAlert == null) || !disp.equals(promptAlert)) {
					origCmd = cmd;
				}
				promptAlert = new Alert(cmd.getLabel(),
						(String)promptCommands.get(cmd), null,
						AlertType.CONFIRMATION);
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
				Display.getDisplay(midlet).setCurrent(this);
				cmdListener.commandAction(origCmd, this);
			} else if (cmd.equals(cmdCancel)) {
				Display.getDisplay(midlet).setCurrent(this);
				return;
			} else {
				Display.getDisplay(midlet).setCurrent(this);
				cmdListener.commandAction(cmd, disp);
			}
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe("commandAction caught ", e);
			//#endif
			System.out.println("commandAction caught " + e + " " + e.getMessage());
		}
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
		super.setCommandListener(this);
        this.cmdListener = cmdListener;
    }

}
