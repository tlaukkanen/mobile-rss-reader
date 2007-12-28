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
import javax.microedition.lcdui.Image;

public class List extends javax.microedition.lcdui.List
implements CommandListener {

	private String title;
	private Command m_testCmd = new Command("Test selections", Command.SCREEN, 9);
	private CommandListener m_cmdListener;

	public List(String title, int listType) {
		super(title, listType);
		System.out.println("Test UI List Title: " + title);
		this.title = title;
		System.out.println("Test UI List listType: " + listType);
	}

	// TODO log stringElements
	public List(String title, int listType, String[] stringElements,
				    Image[] imageElements) {
		super(title, listType, stringElements, imageElements);
		System.out.println("Test UI List Title: " + title);
		System.out.println("Test UI List listType: " + listType);
	}

	public int append(String stringPart, Image imagePart) {
		int rtn = super.append(stringPart, imagePart);
		System.out.println("Test UI List append: " + stringPart);
		System.out.println("Test UI List append int: " + rtn);
		return rtn;
	}

	public void insert(int elementnum, String stringPart, Image imagePart) {
		super.insert(elementnum, stringPart, imagePart);
		System.out.println("Test UI List insert: " + stringPart);
		System.out.println("Test UI List insert elementnum: " + elementnum);
	}

	public void set(int elementnum, String stringPart, Image imagePart) {
		try {
			super.set(elementnum, stringPart, imagePart);
		} catch (Throwable t) {
			System.out.println("Test UI List set: " + t.getMessage());
			t.printStackTrace();
		}
		System.out.println("Test UI List set: " + stringPart);
		System.out.println("Test UI List set elementnum: " + elementnum);
	}

	public int getSelectedIndex() {
		try {
			int rtn = super.getSelectedIndex();
			System.out.println("Test UI List " + title + " getSelectedIndex: " + rtn);
			return rtn;
		} catch (Throwable t) {
			System.out.println("Test UI List getSelectedIndex: " +
					t.getMessage());
			t.printStackTrace();
			return -1;
		}
	}

	public boolean isSelected(int elementnum) {
		try {
			boolean rtn = super.isSelected(elementnum);
			return rtn;
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("Test UI List isSelected: " + t.getMessage());
			return false;
		}
	}

	public void delete(int elementnum) {
		try {
			super.delete(elementnum);
			System.out.println("Test UI List delete elementnum: " + elementnum);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	//#ifdef DMIDP20
	public void deleteAll() {
		super.deleteAll();
		System.out.println("Test UI List delete all");
	}
	//#endif

	public void commandAction(Command cmd, Displayable disp) {
		//#ifdef DMIDP20
		System.out.println("Test UI List command,displayable=" + cmd.getLabel() + "," + super.getTitle());
		//#else
		System.out.println("Test UI List command,displayable=" + cmd.getLabel() + "," + super.getTitle());
		//#endif
		m_cmdListener.commandAction(cmd, this);
	}

	public void setSelectedIndex(int elementNum,
                                 boolean selected) {
		try {
			super.setSelectedIndex(elementNum, selected);
			System.out.println("Test UI List " + title + " setSelectedIndex: " + elementNum);
		} catch (Throwable t) {
			System.out.println("Test UI List " + title +
					" getSelectedIndex: " + t.getMessage());
			t.printStackTrace();
		}
	}

    public void setCommandListener(CommandListener m_cmdListener) {
		super.setCommandListener(this);
        this.m_cmdListener = m_cmdListener;
    }

}

//#endif
