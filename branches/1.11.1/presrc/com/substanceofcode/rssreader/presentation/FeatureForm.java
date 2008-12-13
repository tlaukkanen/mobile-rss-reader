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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Image;
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
	protected FeatureMgr featureMgr;

	//#ifdef DLOGGING
	private Logger logger = Logger.getLogger("FeatureForm");
	private boolean fineLoggable = logger.isLoggable(Level.FINE);
	private boolean finestLoggable = logger.isLoggable(Level.FINEST);
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
	}

	public FeatureForm(RssReaderMIDlet midlet, String title, Item[] items) {
		super(title, items);
		init(midlet);
	}

	final public void addPromptCommand(Command cmd, String prompt) {
		super.addCommand(cmd);
		featureMgr.addPromptCommand(cmd, prompt);
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
