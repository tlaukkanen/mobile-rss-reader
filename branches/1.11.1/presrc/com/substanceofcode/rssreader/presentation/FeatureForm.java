/*
 * FeatureForm.java
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
/*
 * IB 2010-06-27 1.11.5Dev2 Make midlet and LoadingForm optional in FeatureForm and FeatureList.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern using nullPtr.
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

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.StringItem;
//#ifndef DTESTUI
import javax.microedition.lcdui.Form;
//#else
import com.substanceofcode.testlcdui.Form;
//#endif

import com.substanceofcode.rssreader.presentation.FeatureMgr;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/* List with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel.  */
public class FeatureForm extends Form {

	final       Object nullPtr = null;
	protected FeatureMgr featureMgr;
	private Font font = null;

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("FeatureForm");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	//#endif

	public FeatureForm(RssReaderMIDlet midlet, String title) {
		super(title);
		init(midlet);
	}

	private void init(RssReaderMIDlet midlet) {
		featureMgr = new FeatureMgr(midlet, this);
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Starting FeatureForm "
			//#ifdef DMIDP20
				+ super.getTitle()
			//#endif
				);}
		//#endif
		//#ifdef DMIDP20
		font = featureMgr.getCustomFont();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("init font=" + font);}
		//#endif
		//#endif
	}

	//#ifdef DMIDP20
	final public void insert(int itemNum, Item item) {
		try {
			if (font != null) {
				setItemFont(item);
			}
			super.insert(itemNum, item);
		} catch (RuntimeException e) {
			handleError("insert", e);
			super.insert(itemNum, item);
		}
	}

	final public void set(int itemNum, Item item) {
		try {
			if (font != null) {
				setItemFont(item);
			}
			super.set(itemNum, item);
		} catch (RuntimeException e) {
			handleError("set", e);
			super.set(itemNum, item);
		}
	}

	final public int append(Item item) {
		try {
			if (font != null) {
				setItemFont(item);
			}
			return super.append(item);
		} catch (RuntimeException e) {
			handleError("append", e);
			return super.append(item);
		}
	}

	final public int append(String stringPart) {
		try {
			int rtn = super.append(stringPart);
			if (font != null) {
				Item item = super.get(rtn);
				setItemFont(item);
			}
			return rtn;
		} catch (RuntimeException e) {
			handleError("append", e);
			return super.append(stringPart);
		}
	}

	private void setItemFont(Item item) {
		try {
			if (item instanceof StringItem) {
				((StringItem)item).setFont(font);
			} else if (item instanceof ChoiceGroup) {
				final int clen = ((ChoiceGroup)item).size();
				for (int i = 0; i < clen; i++) {
					((ChoiceGroup)item).setFont(i, font);
				}
			}
		} catch (RuntimeException e) {
			if (font != null) {
				font = (Font)nullPtr;
				setItemFont(item);
			}
		}
	}

	private void handleError(String msg, Throwable e) {
		//#ifdef DLOGGING
		logger.warning(msg + " possible error with setFont.", e);
		//#endif
		e.printStackTrace();
		RssReaderMIDlet midlet = featureMgr.getMidlet();
		if (midlet != null) {
			LoadingForm loadForm = midlet.getLoadForm();
			if (loadForm != null) {
				loadForm.appendNote(
					"Font not supported by device.  Reset to default or pick another font.");
				loadForm.addExc("Error changing font.", e);
			}
			midlet.getSettings().setFontChoice(
				RssReaderSettings.DEFAULT_FONT_CHOICE);
		}
		this.font = (Font)nullPtr;
		final int flen = super.size();
		for (int i = 0; i < flen; i++) {
			Item item = super.get(i);
			setItemFont(item);
		}
	}

	//#endif

	public FeatureForm(RssReaderMIDlet midlet, String title, Item[] items) {
		super(title, items);
		init(midlet);
	}

	final public void addPromptCommand(Command cmd, String prompt) {
		super.addCommand(cmd);
		featureMgr.addPromptCommand(cmd, prompt);
	}

	final public void removeCommandPrompt(Command cmd) {
		featureMgr.removeCommand(cmd);
	}

	final public void removeCommand(Command cmd) {
		super.removeCommand(cmd);
		featureMgr.removeCommand(cmd);
	}

	// Annotations not supported for WTK as it uses Java 1.3 target
    final public void setCommandListener(CommandListener cmdListener) {
		throw new RuntimeException("Not supported");
	}

    final public void setCommandListener(CommandListener cmdListener,
			boolean background) {
		super.setCommandListener(featureMgr);
		featureMgr.setCommandListener(cmdListener, background);
    }

    public FeatureMgr getFeatureMgr() {
        return (featureMgr);
    }

}
