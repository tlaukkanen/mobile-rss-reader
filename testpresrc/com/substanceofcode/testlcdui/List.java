//--Need to modify--#preprocess
/*
 * List.java
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
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 */

// Expand to define MIDP define
@DMIDPVERS@
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
 * List.
 *
 * Test GUI class to log methods on the List class.
 *
 * @author  Irving Bunton
 * @version 1.0
 */
public class List extends javax.microedition.lcdui.List
implements LogActIntr {

	private String m_title;

	public List(String title, int listType) {
		super(title, listType);
		init(title, listType);
	}

	final private void init(String title, int listType) {
		TestOutput.println("Test UI List Title: " + title);
		this.m_title = title;
		TestOutput.println("Test UI List listType: " + listType);
	}

	// TODO log stringElements
	public List(String title, int listType, String[] stringElements,
				    Image[] imageElements) {
		super(title, listType, stringElements, imageElements);
		init(title, listType);
	}

	public int append(String stringPart, Image imagePart) {
		int rtn = super.append(stringPart, imagePart);
		TestOutput.println("Test UI List append: [" + m_title + "]," + stringPart);
		TestOutput.println("Test UI List append int: [" + m_title + "]," + rtn);
		return rtn;
	}

	public void insert(int elementnum, String stringPart, Image imagePart) {
		super.insert(elementnum, stringPart, imagePart);
		TestOutput.println("Test UI List insert: [" + m_title + "]," + stringPart);
		TestOutput.println("Test UI List insert elementnum: [" + m_title + "]," + elementnum);
	}

	public void set(int elementnum, String stringPart, Image imagePart) {
		try {
			super.set(elementnum, stringPart, imagePart);
		} catch (Throwable t) {
			TestOutput.println("Test UI List set: [" + m_title + "]," + t.getMessage());
			t.printStackTrace();
		}
		TestOutput.println("Test UI List set: [" + m_title + "]," + stringPart);
		TestOutput.println("Test UI List set elementnum: [" + m_title + "]," + elementnum);
	}

	//#ifdef DMIDP20
	public void setFont(int elementnum, Font font) {
		try {
			super.setFont(elementnum, font);
		} catch (Throwable t) {
			TestOutput.println("Test UI List setFont: [" + m_title + "]," + t.getMessage());
			t.printStackTrace();
		}
		TestOutput.println("Test UI List setFont,size: [" + m_title + "]," + font.getSize());
		TestOutput.println("Test UI List setFont elementnum: [" + m_title + "]," + elementnum);
	}
	//#endif

	public int getSelectedIndex() {
		try {
			int rtn = super.getSelectedIndex();
			TestOutput.println("Test UI List getSelectedIndex: [" + m_title + "]," +rtn);
			return rtn;
		} catch (Throwable t) {
			TestOutput.println("Test UI List getSelectedIndex: [" +
					m_title + "]," + t.getMessage());
			t.printStackTrace();
			return -1;
		}
	}

	public boolean isSelected(int elementnum) {
		try {
			boolean rtn = super.isSelected(elementnum);
			TestOutput.println("Test UI List isSelected: [" + m_title + "]," + elementnum);
			return rtn;
		} catch (Throwable t) {
			t.printStackTrace();
			TestOutput.println("Test UI List isSelected: [" + m_title + "]," + t.getMessage());
			return false;
		}
	}

	public void delete(int elementnum) {
		try {
			super.delete(elementnum);
			TestOutput.println("Test UI List delete elementnum: [" + m_title + "]," + elementnum);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	//#ifdef DMIDP20
	public void deleteAll() {
		super.deleteAll();
		TestOutput.println("Test UI List delete all [" + m_title + "]");
	}
	//#endif

	//#ifdef DMIDP10
	public String getTitle() {
		return m_title;
	}
	//#endif

	public void outputCmdAct(Command cmd, Displayable disp) {
		String dispTitle = "";
		if (disp instanceof Form) {
			dispTitle = ((Form)disp).getTitle();
		} else if (disp instanceof List) {
			dispTitle = ((List)disp).getTitle();
		}
		String lblCmd = cmd.getLabel();
		if (cmd.equals(javax.microedition.lcdui.List.SELECT_COMMAND)) {
			lblCmd = "Implicit select";
			final int sel = super.getSelectedIndex();
			if (sel >= 0) {
				dispTitle += "," + super.getString(sel);
			}
		}

		TestOutput.println("Test UI List command,displayable,dispsame=[" + getTitle() + "]," + lblCmd + "," + dispTitle + "," + disp.equals(this));
	}

	public void setSelectedIndex(int elementNum, boolean selected) {
		try {
			super.setSelectedIndex(elementNum, selected);
			TestOutput.println("Test UI List [" + m_title + "] setSelectedIndex: " + elementNum);
		} catch (Throwable t) {
			TestOutput.println("Test UI List [" + m_title +
					"] getSelectedIndex: " + t.getMessage());
			t.printStackTrace();
		}
	}

    public void setCommandListener(CommandListener cmdListener) {
		super.setCommandListener(new CmdHandler(this, cmdListener));
    }

}

//#endif
