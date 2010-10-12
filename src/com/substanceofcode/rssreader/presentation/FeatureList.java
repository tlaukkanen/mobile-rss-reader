//--Need to modify--#preprocess
/*
 * FeatureList.java
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
 * IB 2010-08-14 1.11.5Dev8 Support loadingForm with FeatureMgr.
 * IB 2010-08-15 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-09-27 1.11.5Dev8 Add msg for setFont error.
 * IB 2010-09-27 1.11.5Dev8 Add exception for setFont error to exception stack.
 * IB 2010-09-27 1.11.5Dev8 Log setFont error as warning.
 * IB 2010-09-27 1.11.5Dev8 Add setFont errors that are not ArrayIndexOutOfBoundsException to stack.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 */

// Expand to define MIDP define
//#define DMIDP20
// Expand to define test define
//#define DNOTEST
// Expand to define test ui define
//#define DNOTESTUI
// Expand to define logging define
//#define DNOLOGGING

package com.substanceofcode.rssreader.presentation;

import java.util.Hashtable;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
//#ifndef DTESTUI
import javax.microedition.lcdui.List;
//#else
//@import com.substanceofcode.testlcdui.List;
//#endif

import com.substanceofcode.utils.CauseException;
import com.substanceofcode.rssreader.presentation.FeatureMgr;
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.businessentities.RssReaderSettings;

//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif

/* List with optional commands added with addPromptCommand which if
   used, will give prompt message with OK/Cancel.  */
public class FeatureList extends List {

	final       Object nullPtr = null;
	protected FeatureMgr featureMgr;
	private Font font = null;

	//#ifdef DLOGGING
//@	private Logger logger = Logger.getLogger("FeatureList");
//@	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	//#endif

	public FeatureList(String title, int listType, LoadingForm loadForm) {
		super(title, listType);
		init(loadForm);
	}

	public FeatureList(String title, int listType) {
		super(title, listType);
		init(null);
	}

	public void init(LoadingForm loadForm) {
		featureMgr = new FeatureMgr(this, loadForm);
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("Starting FeatureList "
			//#ifdef DMIDP20
//@				+ super.getTitle()
			//#endif
//@				);}
		//#endif
		//#ifdef DMIDP20
		initFont();
		//#endif
	}

	//#ifdef DMIDP20
	public void initFont() {
		font = featureMgr.getCustomFont();
        RssReaderMIDlet midlet = featureMgr.getMidlet();
        if (midlet != null) {
			final int fitPolicy = midlet.getSettings().getFitPolicy();
			if (fitPolicy != List.TEXT_WRAP_DEFAULT) {
				super.setFitPolicy(fitPolicy);
			}
		}
	}
	//#endif

	public FeatureList(String title, int listType,
			          String [] stringElements,  Image[] imageElements) {
		super(title, listType, stringElements, imageElements);
		init(null);
	}

	final public void addPromptCommand(Command cmd, String prompt) {
		super.addCommand(cmd);
		featureMgr.addPromptCommand(cmd, prompt);
	}

	final public void removeCommand(Command cmd) {
		super.removeCommand(cmd);
		featureMgr.removeCommand(cmd);
	}

	final public void removeCommandPrompt(Command cmd) {
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

	//#ifdef DMIDP20
	final public int append(String stringPart, Image imagePart) {
		int rtn = -1;
		try {
			rtn = super.append(stringPart, imagePart);
		} catch (RuntimeException e) {
			handleError("append", e);
			rtn = super.append(stringPart, imagePart);
		}
		if (font != null) {
			setFont(rtn, font);
		}
		return rtn;
	}

	final public void insert(int elementnum, String stringPart, Image imagePart) {
		try {
			super.insert(elementnum, stringPart, imagePart);
		} catch (RuntimeException e) {
			handleError("insert", e);
			super.insert(elementnum, stringPart, imagePart);
		}
		int newElement = (elementnum < 0) ? 0 : elementnum;
		if (font != null) {
			setFont(newElement, font);
		}
	}

	final public void set(int elementnum, String stringPart, Image imagePart) {
		try {
			super.set(elementnum, stringPart, imagePart);
		} catch (RuntimeException e) {
			handleError("set", e);
			super.set(elementnum, stringPart, imagePart);
		}
		if (font != null) {
			setFont(elementnum, font);
		}
	}

	private void handleError(String msg, RuntimeException e) {
		CauseException ce = new CauseException("Internal error: " + msg, e);
		//#ifdef DLOGGING
//@		logger.warning(msg + " possible error with setFont.", ce);
		//#endif
		// Using emulator, this can throw array out of bounds, but
		// this is not in the 
		//#ifdef DLOGGING
//@		if (e instanceof ArrayIndexOutOfBoundsException) {
//@			logger.warning("ArrayIndexOutOfBoundsException on setFont");
//@		}
		//#endif
		RssReaderMIDlet midlet = featureMgr.getMidlet();
		if (midlet != null) {
			LoadingForm loadForm = featureMgr.getLoadForm();
			if (loadForm != null) {
				loadForm.appendNote(
				"Font not supported by device.  Reset to default or pick another font.");
				loadForm.addExc("Error changing font.", ce);
			}
			midlet.getSettings().setFontChoice(
					RssReaderSettings.DEFAULT_FONT_CHOICE);
		}
		if (e instanceof ArrayIndexOutOfBoundsException) {
			this.font = (Font)nullPtr;
			final int last = super.size() - 1;
			if (last >= 0) {
				super.setFont(last, Font.getDefaultFont());
			}
		} else {
			//#ifdef DLOGGING
//@			logger.severe("Other exception " + e.getClass().getName(), ce);
			//#endif
			throw e;
		}
	}

    public Font getFont() {
        return (font);
    }

    public void setFont(Font font) {
        this.font = font;
    }

	//#endif

    public FeatureMgr getFeatureMgr() {
        return (featureMgr);
    }

}
