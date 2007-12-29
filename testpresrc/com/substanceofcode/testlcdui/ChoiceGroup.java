/*
 * ChoiceGroup.java
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
import javax.microedition.lcdui.Item;
//#ifdef DMIDP20
import javax.microedition.lcdui.ItemCommandListener;
//#endif
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

public class ChoiceGroup extends javax.microedition.lcdui.ChoiceGroup
//#ifdef DMIDP20
implements ItemCommandListener
//#endif
{

	//#ifdef DMIDP20
	private ItemCommandListener m_itemCmdListener;
	//#endif

	public ChoiceGroup(String label, int choiceType) {
		super(label, choiceType);
		System.out.println("Test UI ChoiceGroup Title: " + label);
		System.out.println("Test UI ChoiceGroup choiceType: " + choiceType);
	}

	// TODO log stringElements
	public ChoiceGroup(String label, int choiceType, String[] stringElements,
				    Image[] imageElements) {
		super(label, choiceType, stringElements, imageElements);
		System.out.println("Test UI ChoiceGroup Label: " + label);
		System.out.println("Test UI ChoiceGroup listType: " + label + "," + choiceType);
		if ((stringElements != null) && (stringElements.length > 0)) {
			System.out.println("Test UI ChoiceGroup stringElements start: " + label);
			for (int ic = 0; ic < stringElements.length; ic++) {
				System.out.println("Test UI ChoiceGroup listType: " + label + "," + stringElements[ic]);
			}
			System.out.println("Test UI ChoiceGroup stringElements end: " + label);
		}
	}

	public int append(String stringPart, Image imagePart) {
		int rtn = super.append(stringPart, imagePart);
		System.out.println("Test UI ChoiceGroup append: " + stringPart);
		System.out.println("Test UI ChoiceGroup append int: " + rtn);
		return rtn;
	}

	public void insert(int elementnum, String stringPart, Image imagePart) {
		super.insert(elementnum, stringPart, imagePart);
		System.out.println("Test UI ChoiceGroup insert: " + stringPart);
		System.out.println("Test UI ChoiceGroup insert elementnum: " + elementnum);
	}

	public void set(int elementnum, String stringPart, Image imagePart) {
		try {
			super.set(elementnum, stringPart, imagePart);
		} catch (Throwable t) {
			System.out.println("Test UI ChoiceGroup set: " + t.getMessage());
			t.printStackTrace();
		}
		System.out.println("Test UI ChoiceGroup set: " + stringPart);
		System.out.println("Test UI ChoiceGroup set elementnum: " + elementnum);
	}

	public int getSelectedIndex() {
		try {
			int rtn = super.getSelectedIndex();
			System.out.println("Test UI ChoiceGroup " + super.getLabel() + " getSelectedIndex: " + rtn);
			return rtn;
		} catch (Throwable t) {
			System.out.println("Test UI ChoiceGroup getSelectedIndex: " +
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
			System.out.println("Test UI ChoiceGroup isSelected: " + t.getMessage());
			return false;
		}
	}

	public void delete(int elementnum) {
		try {
			super.delete(elementnum);
			System.out.println("Test UI ChoiceGroup delete elementnum: " + elementnum);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	//#ifdef DMIDP20
	public void deleteAll() {
		super.deleteAll();
		System.out.println("Test UI ChoiceGroup delete all");
	}
	//#endif

	public void setSelectedIndex(int elementNum,
                                 boolean selected) {
		try {
			super.setSelectedIndex(elementNum, selected);
			System.out.println("Test UI ChoiceGroup " + super.getLabel() + " setSelectedIndex: " + elementNum);
		} catch (Throwable t) {
			System.out.println("Test UI ChoiceGroup " + super.getLabel() +
					" getSelectedIndex: " + t.getMessage());
			t.printStackTrace();
		}
	}

	//#ifdef DMIDP20
	public void commandAction(Command cmd, Item item) {
		//#ifdef DMIDP20
		System.out.println("Test UI StringItem command,item=" + cmd.getLabel() + "," + item.getLabel());
		//#else
		System.out.println("Test UI StringItem command,displayable=" + cmd.getLabel() + "," + super.getClass().getName());
		//#endif
		m_itemCmdListener.commandAction(cmd, item);
	}

    public void setItemCommandListener(ItemCommandListener itemCmdListener) {
        this.m_itemCmdListener = itemCmdListener;
		super.setItemCommandListener(this);
    }
	//#endif
}

//#endif
