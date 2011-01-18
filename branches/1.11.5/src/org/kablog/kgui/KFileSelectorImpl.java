//--Need to modify--#preprocess
/*
 * Copyright (c) 2001-2005 Todd C. Stellanova, rawthought
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 *
 * This software was originally modified no later than Sept 25, 2007.
 */
/*
 * IB 2010-03-14 1.11.5RC2 Fix comment to remove getInstance.
 * IB 2010-04-30 1.11.5RC2 Track threads used.
 * IB 2010-04-30 1.11.5RC2 Use thread from FeatureMgr.
 * IB 2010-05-28 1.11.5RC2 Use threads and CmdReceiver for MIDP 2.0 only.
 * IB 2010-07-04 1.11.5Dev6 Move test statement to be all in define to prevent unused code.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern for nulls to initialize and save memory.
 * IB 2010-08-15 1.11.5Dev8 Need LoadingForm for FeatureList.
 * IB 2010-08-15 1.11.5Dev8 Remove midlet which is now not used directly.
 * IB 2010-08-15 1.11.5Dev8 Use showMe for getDisplay.setCurrent.
 * IB 2010-08-15 1.11.5Dev8 Use featureMgr for super.getFeatureMgr().
 * IB 2010-09-27 1.11.5Dev8 Move title to construtor for KFileSelectorImpl.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-13 1.11.5Dev14 Use getImage from FeatureMgr to get the images.
 * IB 2010-11-15 1.11.5Dev14 Use nullPtr for fileDataBlock.
 * IB 2010-11-15 1.11.5Dev14 Do not use nullPtr to initialize variables.
 * IB 2010-11-16 1.11.5Dev14 Use getSysProperty to get file seperator.
 * IB 2010-11-16 1.11.5Dev14 Initialize FILE_SEPARATOR, openCommand, cancelCommand, and selectCommand in the constructor.
 * IB 2010-11-16 1.11.5Dev14 Have back be 1, cancel be 2, stop be 3, ok be 4, open be 5, and select be 6.
 * IB 2010-11-17 1.11.5Dev14 Move endif to right place for DTEST.
 * IB 2010-11-19 1.11.5Dev14 Have println in DTEST.
 * IB 2010-11-19 1.11.5Dev14 Have debug conditional println use displayDbgMsg.
 * IB 2010-11-19 1.11.5Dev14 Have displayDbgMsg and be in DTEST.
 * IB 2010-11-19 1.11.5Dev14 Change debug log info.
 * IB 2010-11-19 1.11.5Dev14 Have displayDbgMsg print the class name.
 * IB 2010-11-22 1.11.5Dev14 Replace Alert with loading form exception.
 * IB 2010-11-22 1.11.5Dev14 Fix stack trace to use e instead of t.
 * IB 2011-01-01 1.11.5Dev15 Use procIoExc in URLHandler to handle the IO exception and other exceptions common with IO exception.
 * IB 2010-01-01 1.11.5Dev15 If logging and necessary, write severe or other message.  If not logging, printStackTrace.
 * IB 2010-01-01 1.11.5Dev15 Use closeConnection in MiscUtil to close a connection.
 * IB 2010-01-01 1.11.5Dev15 Release memory for InputStream by closing it and making it null with closeConnection.
 * IB 2010-01-01 1.11.5Dev15 Use nullPtr to decrease memory.
 * IB 2010-01-01 1.11.5Dev15 Use featureMgr instead of getFeatureMgr().
 * IB 2011-01-11 1.11.5Dev15 Use super.featureMgr instead of featureMgr.
*/

// Expand to define MIDP define
//#define DMIDP20
// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define test define
//#define DNOTEST
// Expand to define logging define
//#define DNOLOGGING

//#ifdef DJSR75
//@package org.kablog.kgui;
//@
//@import java.io.IOException;
//@import java.io.InputStream;
//@import java.io.*;
//@import java.util.Vector;
//@import java.util.Enumeration;
//@import javax.microedition.io.Connector;
//@import javax.microedition.io.file.FileSystemListener;
//@import javax.microedition.io.file.FileConnection;
//@import javax.microedition.io.file.FileSystemRegistry;
//@import javax.microedition.lcdui.Alert;
//@import javax.microedition.lcdui.AlertType;
//@import javax.microedition.lcdui.List;
//@import javax.microedition.lcdui.CommandListener;
//@import javax.microedition.lcdui.Command;
//@import javax.microedition.lcdui.Displayable;
//@import javax.microedition.lcdui.Display;
//@import javax.microedition.lcdui.Image;
//@
//@import com.substanceofcode.rssreader.presentation.FeatureList;
//@import com.substanceofcode.rssreader.presentation.FeatureMgr;
//@import com.substanceofcode.rssreader.presentation.LoadingForm;
//@import com.substanceofcode.rssreader.businesslogic.URLHandler;
//@import com.substanceofcode.utils.MiscUtil;
//@
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif
//@
//@final public class KFileSelectorImpl
//@ extends FeatureList
//@  implements KFileSelector, CommandListener, FileSystemListener, Runnable {
//@
//@	final       Object nullPtr = null;
//@	protected   Image UPDIR_IMAGE;
//@	protected   Image FOLDER_IMAGE;
//@	protected   Image FILE_IMAGE;
//@
//@	protected final String FILE_SEPARATOR;
//@	protected static final String FILE_SEPARATOR_ALT = "/";
//@
//@	private final static String UP_DIR = "..";
//@
//@	private final Command openCommand;
//@
//@	private final Command cancelCommand;
//@
//@	private final Command selectCommand;
//@
//@	private Vector rootsList = new Vector();
//@
//@	// Stores the current root, if null we are showing all the roots
//@	private FileConnection currentRoot = null;
//@	protected String[] filePatterns = null;
//@	protected byte[] fileDataBlock = null;
//@	protected String selectedFile = null;
//@	protected String selectedURL = null;
//@	protected KViewParent parent = null;
//@	protected String title = null;
//@	protected String defaultDir = null;
//@	protected String iconDir = "";
	//#ifdef DTEST
//@	private static final boolean bDebug = false;
	//#endif
//@	protected boolean bCurFolderIsARoot = true;
//@	protected boolean selectDir = false;
//@	protected boolean itemSelected = false;
//@	protected boolean dirSelected = false;
//@	protected boolean cancelCmd = false;
//@
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("KFileSelectorImpl");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
//@
//@	/* Create the list and initialization. */
//@	public KFileSelectorImpl(String title, LoadingForm loadForm)
//@	{
//@		super(title, List.IMPLICIT, loadForm);
//@
//@		FILE_SEPARATOR = (String)FeatureMgr.getSysProperty(
//@				"file.separator", "/", "Unable to get file.separator", null)[0];
//@		cancelCommand = new Command("Cancel", Command.CANCEL, 2);
//@		openCommand = new Command("Open", Command.ITEM, 4);
//@		selectCommand = new Command("Select", Command.ITEM, 5);
		//#ifdef DTEST
//@		try {
//@
//@			if (bDebug)
//@			{
//@				System.out.println("--- file sep: '" + FILE_SEPARATOR + "'");
//@				System.out.println("--- file sep_alt: '" + FILE_SEPARATOR_ALT + "'");
//@			}
			//#ifdef DLOGGING
//@			if (fineLoggable) {logger.fine("--- file sep: '" + FILE_SEPARATOR + "'");}
//@			if (fineLoggable) {logger.fine("--- file sep_alt: '" + FILE_SEPARATOR_ALT + "'");}
			//#endif
//@		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("KFileSelectorImpl constructor", t);
			//#else
//@			t.printStackTrace();
			//#endif
//@		}
		//#endif
//@
//@	} //constructor
//@
//@	/* Initialize.  Get images. */
//@	public void init() {
		//#ifdef DTEST
//@		displayDbgMsg("MFS load images....", null);
		//#endif
//@		//ROOT_IMAGE = Image.createImage("root_icon.png");
//@		FOLDER_IMAGE = FeatureMgr.getImage(iconDir + "/folder_icon.png",
//@				super.featureMgr.getLoadForm());
//@		FILE_IMAGE = FeatureMgr.getImage(iconDir + "/file_icon.png",
//@				super.featureMgr.getLoadForm());
//@		UPDIR_IMAGE =  FeatureMgr.getImage(iconDir + "/up_dir_icon.png",
//@				super.featureMgr.getLoadForm());
//@
		//#ifdef DTEST
//@		displayDbgMsg("MFS building cmds....", null);
		//#endif
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("MFS building cmds....");}
		//#endif
//@
//@		super.addCommand(openCommand);
//@		if (selectDir) {
//@			super.addCommand(selectCommand);
//@		}
//@		super.addCommand(cancelCommand);
//@		super.setSelectCommand(openCommand);
//@
//@	}
//@
//@	// Init fields.
//@	public void init(boolean selectDir, String defaultDir, String iconDir)
//@	{
//@
//@		this.selectDir = selectDir;
//@		this.title = title;
//@		this.defaultDir = defaultDir;
//@		this.iconDir = iconDir;
		//#ifdef DLOGGING
//@		if (finestLoggable) {logger.finest("init selectDir,title,defaultDir,iconDir=" + selectDir + "," + title + "," + defaultDir + "," + iconDir);}
		//#endif
//@		init();
//@	}
//@
//@	/* Thread run method used to execute actions.  */
//@	public void run() {
//@		try {
//@			try {
//@				if (itemSelected) {
//@					try {
//@						openSelected(dirSelected);
//@					} catch (Throwable t) {
						//#ifdef DLOGGING
//@						logger.severe("KFileSelectorImpl openSelected ", t);
						//#endif
						//#ifdef DTEST
//@						/** Error while executing constructor */
//@						System.out.println("KFileSelectorImpl run openSelected " + t.getMessage());
						//#endif
//@						super.featureMgr.getLoadForm().recordExcFormFin(
//@								"Internal error.  Unable to open.", t);
//@						t.printStackTrace();
//@					}
//@				} else if (cancelCmd) {
//@					doCleanup();
//@					parent.childFinished(this);
//@					cancelCmd = false;
//@					super.featureMgr.setBackground(false);
//@				}
//@			} catch (Exception e) {
				//#ifdef DLOGGING
//@				logger.severe("KFileSelectorImpl run ", e);
				//#else
//@				e.printStackTrace();
				//#endif
//@			} finally {
//@				itemSelected = false;
//@				dirSelected = false;
//@			}
//@
//@		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("KFileSelectorImpl run ", t);
			//#endif
			//#ifdef DTEST
//@			/** Error while executing constructor */
//@			System.out.println("KFileSelectorImpl run " + t.getMessage());
			//#endif
//@			t.printStackTrace();
//@		}
//@	}
//@
//@	/**
//@	 * @param The callback client interested in receiving finished status.
//@	 */
//@	public void setViewParent(KViewParent aParent)
//@	{
//@		if (null == this.parent) {
//@			FileSystemRegistry.addFileSystemListener(this);
//@			resetRoots(); //display the root directories
//@		}
//@		this.parent = aParent;	
//@	}//setViewParent
//@
//@
	//#ifdef DTEST
//@    /** 
//@     Display a debug message somehow
//@     */
//@    final public void displayDbgMsg(String msg, AlertType type) {
//@         if (bDebug) System.out.println("KFileSelectorImpl dbgMsg: " + msg);
//@	}
	//#endif
//@
//@	/**
//@	 * Cleanup any allocated resources immediately.
//@	 */
//@	public void doCleanup()
//@	{
//@		// Save memory.
//@		this.selectedFile = (String)nullPtr;
//@		this.selectedURL = (String)nullPtr;
//@
//@		currentRoot = (FileConnection)MiscUtil.closeConnection(currentRoot);
//@		// Save memory.
//@		fileDataBlock = (byte[])nullPtr;
//@
//@	}//doCleanup
//@
//@	public void commandAction(Command c, Displayable d)
//@	{
//@
//@		try {
			//#ifdef DTEST
//@			displayDbgMsg("cmd action: " + c, null);
			//#endif
			//#ifdef DLOGGING
//@			if (fineLoggable) {logger.fine("disp,cmd=" + d.getTitle() + "," + c.getLabel());}
			//#endif
//@
//@			if (c == openCommand)
//@			{
//@				itemSelected = true;
//@			}
//@			else if (c == selectCommand)
//@			{
//@				itemSelected = true;
//@				dirSelected = true;
//@			}
//@			else if (c == cancelCommand)
//@			{
//@				cancelCmd = true;
//@
//@			}
//@		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			logger.severe("KFileSelectorImpl commandAction ", t);
			//#endif
			//#ifdef DTEST
//@			/** Error while executing constructor */
//@			System.out.println("KFileSelectorImpl commandAction " + t.getMessage());
			//#endif
//@			t.printStackTrace();
//@
//@		}
//@
//@	}//commandAction
//@
//@
//@	/* Show current or all root directories.  */
//@	public void resetRoots()
//@	{
		//#ifdef DTEST
//@		displayDbgMsg("resetRoots...", null);
		//#endif
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("resetRoots...");}
		//#endif
//@
//@		loadRoots();
//@
//@		if (defaultDir != null)
//@		{
			//#ifdef DTEST
//@			displayDbgMsg("default.dir: " + defaultDir, null);
			//#endif
//@			try
//@			{
//@				// Free memory before doing the open.
//@				currentRoot = (FileConnection)nullPtr;
//@				currentRoot = (FileConnection) Connector.open(  defaultDir,  Connector.READ);
//@				displayCurrentRoot();
//@			}
//@			catch (Exception e)
//@			{
				//#ifdef DTEST
//@				displayDbgMsg("### resetroot ex: " + e, null);
				//#endif
				//#ifdef DLOGGING
//@				logger.severe("KFileSelectorImpl constructor ", e);
				//#endif
//@				displayAllRoots();
//@			}
//@		}
//@		else
//@		{
//@			displayAllRoots();
//@		}
//@	}//resetRoots
//@
//@
//@
//@
//@	/* Display all roots. */
//@	protected void displayAllRoots()
//@	{
		//#ifdef DTEST
//@		displayDbgMsg("displayAllRoots...", null);
		//#endif
//@
//@		super.setTitle(title);
//@		super.deleteAll();
//@		Enumeration roots = rootsList.elements();
//@		while (roots.hasMoreElements())
//@		{
//@			String root = (String) roots.nextElement();
			//#ifdef DTEST
//@			displayDbgMsg("root: " + root, null);
			//#endif
//@			super.append(root.substring(1), FOLDER_IMAGE);
//@		}
//@		currentRoot = (FileConnection)MiscUtil.closeConnection(currentRoot);
//@	}//displayAllRoots
//@
//@	/* Load roots into rootsList array. */
//@	protected void loadRoots()
//@	{
		//#ifdef DTEST
//@		displayDbgMsg("loadRoots...", null);
		//#endif
//@
//@		bCurFolderIsARoot = true;
//@
//@		if (!rootsList.isEmpty())
//@		{
//@			rootsList.removeAllElements();
//@		}
//@
//@		try {
//@			Enumeration roots = FileSystemRegistry.listRoots();
//@			while (roots.hasMoreElements())
//@			{
//@				rootsList.addElement(FILE_SEPARATOR + (String) roots.nextElement());
//@			}
//@		} catch (Throwable e) {
			//#ifdef DTEST
//@			displayDbgMsg("### load roots: " + e, null);
			//#else
//@			e.printStackTrace();
			//#endif
//@		}
//@	}//loadRoots
//@
//@
//@	/* Open the selected directory or file. */
//@	protected void openSelected(boolean cdirSelected)
//@	{
//@
//@		int selectedIndex = super.getSelectedIndex();
		//#ifdef DTEST
//@		displayDbgMsg("openSelected....", null);
//@		displayDbgMsg("selectedIndex: " + selectedIndex, null);
		//#endif
		//#ifdef DLOGGING
//@		if (fineLoggable) {logger.fine("openSelected selectedIndex: " + selectedIndex);}
		//#endif
//@
//@		if (selectedIndex >= 0)
//@		{
//@			selectedFile = super.getString(selectedIndex);
			//#ifdef DTEST
//@			displayDbgMsg("selectedFile: " + selectedFile, null);
			//#endif
			//#ifdef DLOGGING
//@			if (fineLoggable) {logger.fine("openSelected selectedFile: " + selectedFile);}
			//#endif
//@
//@			if (null != selectedFile)
//@			{
//@				if (!cdirSelected && (selectedFile.endsWith(FILE_SEPARATOR) || selectedFile.endsWith(FILE_SEPARATOR_ALT)))
//@				{
//@					try
//@					{
//@						if (null == currentRoot)
//@						{
							//#ifdef DTEST
//@							displayDbgMsg("new currentRoot...", null);
							//#endif
//@							currentRoot = (FileConnection) Connector.open("file:///" + selectedFile, Connector.READ);
//@						}
//@						else
//@						{
							//#ifdef DTEST
//@							displayDbgMsg("set cur root conn...", null);
							//#endif
//@							currentRoot.setFileConnection(selectedFile);
//@						}
//@						displayCurrentRoot();
//@					}
//@					catch (Throwable e)
//@					{
						//#ifdef DTEST
//@						displayDbgMsg("### file Conn open: " + e, null);
						//#endif
//@						URLHandler.procIoExc(
//@								"Error while accessing file at ", e,
//@								(null == currentRoot),
//@								"file:///" + selectedFile,
//@								"Out of memory error while accessing file at dir",
//@								"Internal error while accessing file at dir",
//@								"openSelected",
//@								super.featureMgr.getLoadForm()
								//#ifdef DLOGGING
//@								,logger
								//#endif
//@								);
//@					}
//@				}
//@				else if (selectedFile.equals(UP_DIR))
//@				{
//@					String curRootName = currentRoot.getPath()  + currentRoot.getName();
					//#ifdef DTEST
//@					displayDbgMsg("curRootName: " + curRootName, null);
					//#endif
//@					String curShortName = null;
//@					if (curRootName.charAt(0) == '/') {
//@						curShortName = curRootName.substring(1);
//@					}
					//#ifdef DLOGGING
//@					if (finestLoggable) {logger.finest("curShortName=" + curShortName);}
//@					if (finestLoggable) {logger.finest("rootsList(0)=" + (String)rootsList.elementAt(0));}
					//#endif
//@
//@					if( rootsList.contains(curRootName) ||
//@						((curShortName != null) &&
//@					    rootsList.contains(FILE_SEPARATOR + curShortName)))
//@					{
//@						displayAllRoots();
//@					}
//@					else
//@					{
//@						try
//@						{
//@							currentRoot.setFileConnection(UP_DIR);
//@							displayCurrentRoot();
//@						}
//@						catch (Throwable e)
//@						{
							//#ifdef DTEST
//@							displayDbgMsg("### setfileConn: " + e, null);
							//#endif
//@							URLHandler.procIoExc(
//@									"Error while accessing file at dir", e,
//@									false,
//@									UP_DIR,
//@									"Out of memory error while accessing file at dir",
//@									"Internal error while accessing file at dir",
//@									"openSelected",
//@									super.featureMgr.getLoadForm()
									//#ifdef DLOGGING
//@									,logger
									//#endif
//@									);
//@							if (e instanceof IllegalArgumentException)
//@							{
//@								//there's something hosed with this path-- jump back to roots
//@								displayAllRoots();
//@							}
//@						}
//@					}
//@				}
//@				else
//@				{
					//#ifdef DTEST
//@					displayDbgMsg("user selected: " + selectedFile, null);
					//#endif
//@
//@					//the user has selected a particular file
//@
//@
//@					//parent.childFinished(this);
//@
//@					// Stop thread of FeatureMgr
//@					super.featureMgr.setBackground(false);
//@
//@					// Clean up in separate thread.  This also saves
//@					// the selectedURL and sends childFinished.
//@					parent.addDeferredAction(new KFileSelectorKicker(this));
//@
//@				}
//@			}
			//#ifdef DTEST
//@			else {
//@				displayDbgMsg("### no selected file???", null);
//@			}
			//#endif
//@		}
//@	}//openSelected
//@
//@
//@	/* Finish completion. */
//@	protected void doNotifyOpComplete() {
//@
//@		if (null != currentRoot)
//@		{
//@			selectedURL = currentRoot.getURL() + selectedFile;	
			//#ifdef DTEST
//@			displayDbgMsg("=== Selected URL: " + selectedURL, null);
			//#endif
//@
//@		}
//@
//@		currentRoot = (FileConnection)MiscUtil.closeConnection(currentRoot);
//@		//selectedFile = (String)nullPtr;
//@
//@		parent.childFinished(this);
//@	}
//@
//@
//@	/* Display the current root. */
//@	protected void displayCurrentRoot()
//@	{
		//#ifdef DTEST
//@		displayDbgMsg("displayCurrentRoot...", null);
		//#endif
//@
//@		try
//@		{
//@			if (null != currentRoot)
//@			{
//@				String rootName = currentRoot.getName();
//@				if ((rootName == null) || (rootName.length() < 1))
//@					rootName = selectedFile;
//@
//@				setTitle("[" + rootName + "]");
//@
//@			}
//@			else
//@				setTitle(title);
//@
//@			// open the root
//@			super.deleteAll();
//@			super.append(UP_DIR, UPDIR_IMAGE);
//@
//@			// list all dirs
//@			Enumeration listOfDirs = currentRoot.list("*", false);
//@			while (listOfDirs.hasMoreElements())
//@			{
//@				String currentDir = (String) listOfDirs.nextElement();
//@
//@				if (currentDir.endsWith(FILE_SEPARATOR)  || currentDir.endsWith(FILE_SEPARATOR_ALT))
//@				{
//@					super.append(currentDir, FOLDER_IMAGE);
//@				}
//@			}
//@
//@			if (filePatterns != null) {
//@				for (int ic = 0; ic > filePatterns.length; ic++) {
//@					// list all filePatterns files and dont show hidden files
//@					Enumeration listOfFiles = currentRoot.list(filePatterns[ic], false);
//@					while (listOfFiles.hasMoreElements())
//@					{
//@						String currentFile = (String) listOfFiles.nextElement();
//@						super.append(currentFile, FILE_IMAGE);
//@					}
//@				}
//@			} else {
//@				// list all files
//@				Enumeration listOfFiles = currentRoot.list();
//@				while (listOfFiles.hasMoreElements())
//@				{
//@					String currentFile = (String) listOfFiles.nextElement();
//@					if (!currentFile.endsWith(FILE_SEPARATOR) &&
//@							!currentFile.endsWith(FILE_SEPARATOR_ALT)) {
//@						super.append(currentFile, FILE_IMAGE);
//@					}
//@				}
//@			}
//@
//@		}
//@		catch (Exception e)
//@		{
			//#ifdef DLOGGING
//@			logger.severe("KFileSelectorImpl constructor", e);
			//#else
//@			e.printStackTrace();
			//#endif
			//#ifdef DTEST
//@			displayDbgMsg("### displayRoot ex: " + e, null);
			//#endif
//@		}
//@
//@	}//displayCurrentRoot
//@
//@	/* Get the selected file name. */
//@	public String getFileName()
//@	{
//@		return selectedFile;
//@	}//getFileName
//@
	//#ifdef DTEST
//@	final public String getFileMimeType()
//@	{
//@		String szMimeType = null;
//@
//@		if (null != selectedFile)
//@		{
//@			if ((selectedFile.indexOf("jpg") > 0) || (selectedFile.indexOf("jpeg") > 0))
//@				szMimeType = "image/jpeg";
//@			else if (selectedFile.indexOf("png") > 0)
//@				szMimeType = "image/png";
//@			else if (selectedFile.indexOf("gif") > 0)
//@				szMimeType = "image/gif";
//@			else if (selectedFile.indexOf("bmp") > 0)
//@				szMimeType = "image/bmp";
//@		}
//@
//@		return szMimeType;
//@	}//getMimeType
	//#endif
//@
	//#ifdef DTEST
//@	final public Image getThumbnail(int width, int height)
//@	{
//@		Image thumbImage = FILE_IMAGE;
//@		String fileType = getFileMimeType();
//@
//@		if (null != fileType)
//@		{
//@			try {
//@				byte[] datablock = getFileData();
//@				thumbImage = Image.createImage(datablock,0,datablock.length);
//@			}
//@			catch (java.lang.OutOfMemoryError oom) {
				//#ifdef DTEST
//@				if (bDebug) System.err.println("### OOM on createImage: "  + Runtime.getRuntime().freeMemory());
				//#endif
//@			}
//@			catch (Exception ex) {
				//#ifdef DTEST
//@				if (bDebug)  {
//@					System.err.println("### Couldn't create image: " + ex);
//@					ex.printStackTrace();
//@				}
				//#endif
//@
//@			}
//@		}
//@
//@		if (thumbImage.getWidth() > (2*width)) {
//@			thumbImage = FILE_IMAGE;
//@		}
//@
		//#ifdef DTEST
//@		displayDbgMsg("...getThumbnail", null);
		//#endif
//@
//@		return thumbImage;
//@	}//getThumbnail
	//#endif
//@
	//#ifdef DTEST
//@	/* Get data from the selected file. */
//@	final public byte[] getFileData()
//@	{
//@		if (null == fileDataBlock)
//@		{
//@			if (null != selectedURL)
//@			{
//@				try {
//@
//@					long availSize = 0;
//@
					//#ifdef DTEST
//@					displayDbgMsg("selectedURL: " + selectedURL, null);
					//#endif
//@
//@					// Free memory before doing the open.
//@					currentRoot = (FileConnection)nullPtr;
//@					currentRoot = (FileConnection) Connector.open(selectedURL);
//@
//@					//currentRoot.setFileConnection(selectedFile); //relative to current directory
//@
					//#ifdef DTEST
//@					displayDbgMsg(" getting data...", null);
					//#endif
//@					availSize = currentRoot.fileSize();
					//#ifdef DTEST
//@					displayDbgMsg("file availSize: " + availSize, null);
					//#endif
//@
					//#ifdef DTEST
//@					if (bDebug && !currentRoot.canRead()) System.out.println("### can't read???");
					//#endif
//@
//@
//@					if (availSize > 0)
//@					{
//@
//@						try {
//@							//DataInputStream is = currentRoot.openDataInputStream();
//@							InputStream is = currentRoot.openInputStream();
							//#ifdef DTEST
//@							displayDbgMsg("Creating new file block [" + availSize + "]", null);
							//#endif
//@
//@							System.gc();
//@							fileDataBlock = new byte[(int)availSize];
							//#ifdef DTEST
//@							displayDbgMsg("Allocated: " + availSize, null);
							//#endif
//@
//@							is.read(fileDataBlock);
//@							is = (InputStream)MiscUtil.closeInputStream(is);
//@
							//#ifdef DTEST
//@							displayDbgMsg("...data read.", null);
							//#endif
//@						}
//@						catch (Throwable e)
//@						{
//@							URLHandler.procIoExc(
//@									"Error while accessing file at dir", e,
//@									currentRoot == null,
//@									selectedURL,
//@									"Out of memory error while accessing file at dir",
//@									"Internal error while accessing file at dir",
//@									"openSelected",
//@									super.featureMgr.getLoadForm()
									//#ifdef DLOGGING
//@									,logger
									//#endif
//@									);
//@						}
//@					}
//@
//@					currentRoot = (FileConnection)MiscUtil.closeConnection(currentRoot);
//@				}
//@				catch (Throwable e)
//@				{
//@					URLHandler.procIoExc(
//@							"Error while reading file at dir", e,
//@							currentRoot == null,
//@							selectedURL,
//@							"Out of memory error while reading file at dir",
//@							"Internal error while reading file at dir",
//@							"openSelected",
//@							super.featureMgr.getLoadForm()
							//#ifdef DLOGGING
//@							,logger
							//#endif
//@							);
//@				}
//@			}
//@		}
//@		else
//@		{
			//#ifdef DTEST
//@			displayDbgMsg("Existing fileDataBlock [" + fileDataBlock.length + "]", null);
			//#endif
//@		}
//@
//@		return fileDataBlock;
//@
//@	}//getFileData
	//#endif
//@
//@
//@	/* Method to listen for changes in root. */
//@	public void rootChanged(int changeType, String strArg)
//@	{
		//#ifdef DTEST
//@		if (bDebug) {
//@			//that's nice...
//@			if (changeType == FileSystemListener.ROOT_ADDED) {
//@				System.out.println("=== FileSys: ROOT ADDED");		
//@			} else if (changeType == FileSystemListener.ROOT_REMOVED) {
//@				System.out.println("=== FileSys:ROOT_REMOVED");
//@			}
//@
//@			System.out.println("strArg: " + strArg);
//@		}
		//#endif
//@	}
//@
//@	/* Get selected URL. */
//@	public String getSelectedURL() {
//@		return (selectedURL);
//@	}
//@
//@	/* Set list of file patterns to add to file list.  If null, all files
//@	   are selected. */
//@    public void setFilePatterns(String[] filePatterns) {
//@        this.filePatterns = filePatterns;
//@    }
//@
//@	/* Set list of file patterns to add to file list.  */
//@    public String[] getFilePatterns() {
//@        return (filePatterns);
//@    }
//@
//@} //class KFileSelectorImpl
//@
//@/* Class to handle the completion of the OP through a thread. */
//@final class KFileSelectorKicker
//@implements Runnable
//@{
//@	
//@	/* Create.  Save target to run. */
//@	public KFileSelectorKicker(KFileSelectorImpl aTarget) {
//@		
//@		target = aTarget;
//@		
//@	}
//@
//@	
//@	/* Complete the Op when we run if we have a target.  */
//@	public void run() {
//@		
//@		try {
//@			if (null != target) {
//@				
//@				target.doNotifyOpComplete();
//@			}
		//#ifdef DMIDP20
//@		} finally {
//@			MiscUtil.removeThread(Thread.currentThread());
		//#endif
//@		}
//@	}
//@	
//@	KFileSelectorImpl target;
//@}
//#endif
