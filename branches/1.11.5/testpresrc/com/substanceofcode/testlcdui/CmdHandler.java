//--Need to modify--#preprocess
/*
 * CmdHandler.java
 *
 * Copyright (C) 2007 Irving Bunton Jr
 * http://www.substanceofcode.com
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
 * IB 2010-09-29 1.11.5Dev8 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define test ui define
@DTESTUIDEF@

//#ifdef DTESTUI

package com.substanceofcode.testlcdui;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import com.substanceofcode.testutil.TestOutput;

/**
 * CmdHandler.
 *
 * Test GUI class to log methods on the CommandListener interface.
 *
 * @author  Irving Bunton
 * @version 1.0
 */

final public class CmdHandler implements CommandListener {
	private LogActIntr m_disp;
	private CommandListener m_cmdListener;

	public CmdHandler (LogActIntr disp, CommandListener cmdListener) {
		m_disp = disp;
		m_cmdListener = cmdListener;
	}

	/* Prompt if command is in prompt camands.  */
	public void commandAction(Command cmd, Displayable disp) {
		((LogActIntr)m_disp).outputCmdAct(cmd, disp);
		m_cmdListener.commandAction(cmd, disp);
	}
}

//#endif
