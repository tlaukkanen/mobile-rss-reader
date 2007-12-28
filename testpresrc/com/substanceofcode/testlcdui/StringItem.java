/*
 * StringItem.java
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

public class StringItem extends javax.microedition.lcdui.StringItem
//#ifdef DMIDP20
implements ItemCommandListener
//#endif
{

	//#ifdef DMIDP20
	private ItemCommandListener m_itemCmdListener;
	//#endif

	public StringItem(String label, String text) {
		super(label, text);
		System.out.println("Test UI String Item Label: " + label);
		System.out.println("Test UI String Item text: " + text);
	}

	//#ifdef DMIDP20
	public StringItem(String label, String text,  int appearance) {
		super(label, text, appearance);
		System.out.println("Test UI String Item Label: " + label);
		System.out.println("Test UI String Item text: " + text);
		System.out.println("Test UI String Item appearance: " + appearance);
	}
	//#endif

	public String getText() {
		String rtn = super.getText();
		System.out.println("Test UI String Item getText: " + getLabel() + "," +rtn);
		return rtn;
	}

	public void setText(String text) {
		super.setText(text);
		System.out.println("Test UI String Item setString: " + getLabel() + "," + text);
		return;
	}

	//#ifdef DMIDP20
	public void commandAction(Command cmd, Item item) {
		System.out.println("Test UI StringItem command,item=" + cmd.getLabel() + "," + item.getLabel());
		m_itemCmdListener.commandAction(cmd, item);
	}

    public void setItemCommandListener(ItemCommandListener itemCmdListener) {
        this.m_itemCmdListener = itemCmdListener;
		super.setItemCommandListener(this);
    }
	//#endif
}

//#endif
