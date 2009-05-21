/*
   TODO Fix new View.  Get prev pos.
   TODO Fix removing command if change
 * PageMgr.java
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
 * This was first modified no earlier than May 27, 2008.
 *
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define CLDC define
@DCLDCVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@

//#ifdef DMIDP20
package cz.cacek.ebook;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.lang.Math;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.substanceofcode.rssreader.presentation.UiUtil;
import com.substanceofcode.rssreader.presentation.HelpForm;
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import net.eiroca.j2me.RSSReader.presentation.RenderedWord;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Main display. On this CustomItem is displayed current part of book.
 * @author Tomáš Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author Jiri Bartos
 * @author $Author$
 * @version $Revision$
 * @created $Date$
 */
final public class PageMgr implements ItemCommandListener {

	final public static int PAGE_FWD_ACTION = Canvas.RIGHT;
	final public static int PAGE_BCK_ACTION = Canvas.LEFT;
	final public static int LINE_FWD_ACTION = Canvas.DOWN;
	final public static int LINE_BCK_ACTION = Canvas.UP;
	final public static int BACK_ACTION = Canvas.GAME_A;
	final public static int SELECT_ACTION = Canvas.GAME_C;
	final public static int SCROLL_ACTION = Canvas.GAME_D;
	private RssReaderMIDlet midlet;
	protected PageImpl pageImpl;
	protected AbstractView view;
	protected AbstractView labelView = null;
	protected String label = null;
	protected String text = null;
	protected int prefViewHeight;
	protected boolean resize = false;
	protected boolean viewScrollBar = false;
	private boolean initialized = false;
	private boolean paintDone = false;
	protected Page page = null;
	protected Page labelPage = null;
	protected Displayable prev;
	private String message = null;
	private Font messageFont;
	private static Command cmdPosition = null;
	//#ifdef DTEST
	private static Command cmdInfo = null;
	//#endif
	private static Command cmdHelp = null;
	private int fontSize;
	private boolean underlinedStyle;
	private boolean useActions = false;
	private boolean hasPos = false;
	private Hashtable gameKeys = null;
	private Hashtable sgameKeys = null;
	//#ifdef DTEST
	private boolean debug = true;
	//#endif

	protected ScrollThread scrollThread  = new ScrollThread();

	private Vector listVector;
	private static int scrollDelay = Common.AUTOSCROLL_PAUSE;

	private Display display;
	int lastWidth;
	int lastHeight;
	int labelPageHeight = 0;
	int mainPageHeight;

	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("PageMgr");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

	/**
	 * Thread which provides autoscroll functionality.
	 * @author Josef Cacek
	 */
	final class ScrollThread extends Thread {
		boolean processing = true;
		boolean run = false;

		//#ifdef DCLDCV11
		public ScrollThread(String name) {
			super(name);
		}
		//#endif

		public ScrollThread() {
			super();
		}

		public void run() {
			try {
				while (processing) {
					if (canRun()) {
						if (!view.fwdLine()) {
							setRun(false);
						}
						messageOff();
					}
					synchronized(this) {
						super.wait(getScrollDelay());
					}
				}
				if (message != null) {
					messageOff();
				}
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(this + " scrollThread finished.");}
				//#endif
			} catch (Exception e) {
				//#ifdef DLOGGING
				logger.severe(this + " scrollThread run failed.", e);
				//#endif
			}
		}

		public void wakeUp() {
			synchronized(this) {
				try {
					super.notify();
				} catch (Exception e) {
				}
			}
		}

		public void setRun(final boolean aRun) {
			synchronized(this) {
				run = aRun;
				if (run) {
					if (!scrollThread.isAlive()) {
						//#ifdef DCLDCV11
						scrollThread = new ScrollThread("scrollThread");
						//#else
						scrollThread = new ScrollThread();
						//#endif
						scrollThread.start();
					}
				} else {
					messageOff();
				}
			}
		}

		public boolean canRun() {
			synchronized(this) {
				return run;
			}
		}

		public void setProcessing(boolean processing) {
			this.processing = processing;
		}

		public boolean isProcessing() {
			return (processing);
		}

	}

	/**
	 * Constructor
	 * Set the page to show
	 * @param aLabel - albel name
	 * @param aFrmWidth - form width
	 * @param aMidlet - midlet
	 * @throws Exception
	 */
	public PageMgr(String aLabel, int aFrmWidth, int aFrmHeight,
						  final int aFontSize, final boolean aUnderlinedStyle,
						  final boolean isHtml,
						  final String aText,
						  final PageImpl aPageImpl,
						  final Displayable aPrev,
						  final RssReaderMIDlet aMidlet)
	throws Exception {
		try {
			midlet = aMidlet;
			fontSize = aFontSize;
			underlinedStyle = aUnderlinedStyle;
			lastWidth = aFrmWidth;
			pageImpl = aPageImpl;
			prev = aPrev;
			display = Display.getDisplay(midlet);
			if (gameKeys == null) {
				initGameKeys();
			}
			messageFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD,
				aFontSize);
			if (isHtml) {
				if (aLabel != null) {
					label = "<b>" + aLabel + "</b>";
				}
				if (aUnderlinedStyle) {
					text = "<u>" + aText + "</u>";
				} else {
					text = aText;
				}
				// Create label (if needed) and main view.  Need to use
				// a temp height for it to work.  These are not used
				// by html view.
				setCustomData(aFrmWidth, (label != null) ? aFrmHeight : 0, aFrmHeight, aFontSize,
						aUnderlinedStyle);
				if (labelView != null) {
					labelPageHeight = ((HtmlView)labelView).getPageHeight();
				}
				mainPageHeight = ((HtmlView)view).getPageHeight();
			} else {
				if (aLabel != null) {
					labelPage = new Page(aLabel);
					labelPageHeight = View.estimateHeight(aFontSize,
							true, false, aFrmWidth, aFrmHeight,
							labelPage);
				}
				page = new Page(aText);
				mainPageHeight = View.estimateHeight(aFontSize, false,
						aUnderlinedStyle, aFrmWidth, aFrmHeight, page);
				setCustomData(aFrmWidth, labelPageHeight, mainPageHeight,
						aFontSize, aUnderlinedStyle);
			}
			lastHeight = labelPageHeight + mainPageHeight;
			prefViewHeight = Math.min(lastHeight,
					aFrmHeight - AbstractView.getTotalHeightBorderSpace());
			if (isHtml) {
				updCustomData(aFrmWidth, prefViewHeight, aFontSize,
						aUnderlinedStyle);
			}
			if (cmdHelp == null) {
				cmdHelp = UiUtil.getCmdRsc(Common.CMD_I_HELP, Command.ITEM, 3);
			}
			pageImpl.addCommand(cmdHelp);
		} catch (Exception e) {
			//#ifdef DLOGGING
			logger.severe(this + " Constructor failed with exception", e);
			//#endif
		} catch (Throwable e) {
			//#ifdef DLOGGING
			logger.severe(this + " Constructor failed with throwable", e);
			//#endif
		} finally {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(this + " aLabel,aFrmWidth,aFrmHeight,labelPageHeight,mainPageHeight,prefViewHeight=" + aLabel + "," + aFrmWidth + "," + aFrmHeight + "," + labelPageHeight + "," + mainPageHeight + "," + prefViewHeight);}
			//#endif
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(this + " lastHeight=" + lastHeight);}
			//#endif
			synchronized(this) {
				initialized = true;
				if (!paintDone) {
					pageImpl.svcRepaint();
				}
			}
			//#ifdef DTEST
			if (cmdInfo == null) {
				cmdInfo = UiUtil.getCmdRsc(Common.CMD_INFO,
						Command.ITEM, 9);
			}
			pageImpl.addCommand(cmdInfo);
			//#endif
		}
	}

  /**
   * Init game keys.
   *
   * @return    Hashtable - Convert
   * @author Irv Bunton
   */
	final private void initGameKeys() {
		Hashtable hact = new Hashtable();
		int [] keys = {Canvas.KEY_NUM0,
					   Canvas.KEY_NUM1,
					   Canvas.KEY_NUM2,
					   Canvas.KEY_NUM3,
					   Canvas.KEY_NUM4,
					   Canvas.KEY_NUM5,
					   Canvas.KEY_NUM6,
					   Canvas.KEY_NUM7,
					   Canvas.KEY_NUM8,
					   Canvas.KEY_NUM9,
					   Canvas.KEY_POUND,
					   Canvas.KEY_STAR};
		String [] skeys = {
					   "0",
					   "1",
					   "2",
					   "3",
					   "4",
					   "5",
					   "6",
					   "7",
					   "8",
					   "9",
					   "pound",
					   "star"};
		int [] acts = {LINE_BCK_ACTION,
					   LINE_FWD_ACTION,
		               PAGE_BCK_ACTION,
					   PAGE_FWD_ACTION,
					   Canvas.FIRE,
					   BACK_ACTION,
					   Canvas.GAME_B,
					   SELECT_ACTION,
					   SCROLL_ACTION};
		String [] sacts = {"UP",
					   "DOWN",
					   "LEFT",
					   "RIGHT",
					   "FIRE",
					   "GAME_A",
					   "GAME_B",
					   "GAME_C",
					   "GAME_D"};
		for (int ic = 0; ic < sacts.length; ic++) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(sacts[ic] + "=" + acts[ic]);}
			//#endif
			hact.put(new Integer(acts[ic]), sacts[ic]);
		}
		Hashtable ckeys = new Hashtable();
		Hashtable sckeys = new Hashtable();
		for (int ic = 0; ic < keys.length; ic++) {
			int cact = pageImpl.getGameAction(keys[ic]);
			Integer icact = new Integer(cact);
			if (hact.containsKey(icact)) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(skeys[ic] + "=" + keys[ic] + "," + hact.get(icact));}
				//#endif
				hact.remove(icact);
				ckeys.put(icact, new Integer(keys[ic]));
				sckeys.put(icact, skeys[ic]);
			};
		}
		final int hsize = hact.size();
		useActions = (hsize == 0);
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("useActions,hact.size()=" + useActions + "," + hsize);}
		//#endif
		if (!useActions) {
			int[] altKeys = {Canvas.KEY_NUM2, Canvas.KEY_NUM8,
				Canvas.KEY_NUM4, Canvas.KEY_NUM6, Canvas.KEY_NUM5,
				Canvas.KEY_NUM1, Canvas.KEY_NUM7, Canvas.KEY_NUM3,
				Canvas.KEY_NUM9};
			String[] saltKeys = {"2", "8", "4", "6", "5", "1", "7", "3", "9"};
			gameKeys = new Hashtable();
			sgameKeys = new Hashtable();
			for (int ic = 0; ic < acts.length; ic++) {
				Integer act = new Integer(acts[ic]);

				gameKeys.put(act, new Integer(altKeys[ic]));
				sgameKeys.put(act, saltKeys[ic]);
			}
		} else {
			gameKeys = ckeys;
			sgameKeys = sckeys;
		}
	}

  /**
   * Create a new view with the width and height and font.  Also, set
   * page position to 0.  Also, account for message if page height is <
   * given height.
   *
   * @param aWidth
   * @param aHeight
   * @param aFontSize
   * @param aUnderlinedStyle
   * @return    final
   * @author Irv Bunton
   */
	final private void setCustomData(int aWidth, int aLabelHeight,
			int aMainHeight, int aFontSize,
			boolean aUnderlinedStyle)
	throws Exception {
		//#ifdef DLOGGING
		//#ifdef DTEST
		if (debug) {
			if (finestLoggable) {logger.finest(this + " setCustomData aWidth,aLabelHeight,aMainHeight,aFontSize,aUnderlinedStyle=" + aWidth + "," + aLabelHeight + "," + aMainHeight + "," + aFontSize + "," + aUnderlinedStyle);}
		}
		//#endif
		//#endif
		if (labelPage != null) {
			labelView = new View(aWidth, aLabelHeight, aFontSize, true,
					false);
			labelPage.setPosition(0);
			((View)labelView).setPage(labelPage);
		} else if (aLabelHeight > 0) {
			labelView = new HtmlView(aWidth, aLabelHeight, aFontSize, label);
		}
		if (page != null) {
			view = new View(aWidth, aMainHeight, aFontSize,
					false, aUnderlinedStyle);
			page.setPosition(0);
			((View)view).setPage(page);
			viewScrollBar = !((View)view).isLastPage();
		} else {
			view = new HtmlView(aWidth, aMainHeight, aFontSize, text);
		}
	}


  /**
   * Create a new view with the width and height and font.  Also, set
   * page position to 0.  Also, account for message if page height is <
   * given height.
   *
   * @param aWidth
   * @param aHeight
   * @param aFontSize
   * @param aUnderlinedStyle
   * @return    final
   * @author Irv Bunton
   */
	final private void updCustomData(int aWidth, int aHeight, int aFontSize,
			boolean aUnderlinedStyle)
	throws Exception {
		//#ifdef DLOGGING
		//#ifdef DTEST
		if (debug) {
			if (finestLoggable) {logger.finest(this + " updCustomData aWidth,aHeight,labelPageHeight,aFontSize,aUnderlinedStyle=" + aWidth + "," + aHeight + "," + labelPageHeight + "," + aFontSize + "," + aUnderlinedStyle);}
		}
		//#endif
		//#endif
		int labelHeight = Math.min(labelPageHeight, aHeight);
		int mainHeight = aHeight - labelHeight;
		if (mainHeight >= (messageFont.getHeight() +
					AbstractView.getTotalHeightBorderSpace())) {
			setCustomData(aWidth,
					labelHeight, mainHeight, aFontSize, aUnderlinedStyle);
		//#ifdef DLOGGING
		//#ifdef DTEST
		} else {
			if (debug) {
				if (finestLoggable) {logger.finest(this + " updCustomData labelHeight,mainHeight,messageFont.getHeight(),AbstractView.getTotalHeightBorderSpace()=" + labelHeight + "," + mainHeight + "," + messageFont.getHeight() + "," + AbstractView.getTotalHeightBorderSpace());}
			}
		//#endif
		//#endif
		}
		viewScrollBar = (aHeight < (labelPageHeight + mainPageHeight));
		if ((aHeight < prefViewHeight) && !hasPos) {
			hasPos = true;
			if (cmdPosition == null) {
				cmdPosition = UiUtil.getCmdRsc(Common.PAGE_CMD_POSITION,
						Command.ITEM, 2);
			}
			pageImpl.addCommand(cmdPosition);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.microedition.lcdui.Displayable#paint(javax.microedition.lcdui.Graphics)
	 */
	final public void paint(Graphics g, int pwidth, int pheight) {
		if (!initialized) {
			synchronized(this) {
				if (!initialized) {
					paintDone = true;
				}
			}
		}
		//#ifdef DTEST
		Common.debug("PageMgr.paint(Graphics g)");
		//#endif
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest(this + " paint pwidth,pheight=" + pwidth + "," + pheight);}
		//#endif
		if ((pheight > prefViewHeight) && !resize &&
				(pageImpl instanceof PageCustomItem)) {
			midlet.callSerially((Runnable)pageImpl);
			resize = true;
		}
		if ((lastWidth != pwidth) || (lastHeight != pheight)) {
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(this + " paint changed pwidth,lastWidth,pheight,lastHeight=" + pwidth + "," + lastWidth + "," + pheight + "," + lastHeight);}
			//#endif
			lastWidth = pwidth;
			lastHeight = pheight;
			try {
				updCustomData(lastWidth, lastHeight, fontSize,
						underlinedStyle);
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(this + " viewScrollBar=" + viewScrollBar);}
				//#endif
			} catch (Exception e) {
				//#ifdef DTEST
				Common.debugErr("PageMgr.paint view failed");
				//#endif
				//#ifdef DLOGGING
				logger.severe(this + " paint updCustomData " + e.getMessage(), e);
				//#endif
			}
		}
		// Write the item only if there is space for the label and at least
		// one line.
		//#ifdef DLOGGING
		//#ifdef DTEST
		if (debug) {
			if (finestLoggable) {logger.finest(this + " labelPageHeight,pheight,viewScrollBar=" + labelPageHeight + "," + pheight + "," + viewScrollBar);}
		}
		//#endif
		//#endif
		if (((labelPageHeight + messageFont.getHeight()) < pheight) ||
		    (prefViewHeight <= pheight)) {
			if (labelView != null) {
				labelView.draw(g, 0, 0, false, false);
			}
			view.draw(g, 0, labelPageHeight, true, viewScrollBar);
			if (message != null) {
				int mx = 2;
				int my = 2;
				g.setFont(messageFont);
				int w = messageFont.stringWidth(message);
				int h = messageFont.getHeight();
				g.setColor(0xFFFFFF);
				g.fillRect(mx, my, w + 3, h + 3);
				g.setColor(0x000000);
				g.drawString(message, mx + 2, my + 2, Graphics.LEFT | Graphics.TOP);
				g.drawRect(mx, my, w + 3, h + 3);
			}
		}
	}

	/**
	 * @return Returns width of last paint.
	 */
	final public int getWidth() {
		return lastWidth;
	}

	/**
	 * @return Returns height of last paint.
	 */
	final public int getHeight() {
		return lastHeight;
	}

	/**
	 * @return Returns min content width of last paint.
	 */
	final public int getMinContentWidth() {
		return lastWidth;
	}

	/**
	 * @return Returns min content height of last paint.
	 */
	final public int getMinContentHeight() {
		return lastHeight;
	}

	/**
	 * @return Returns preferred width of last paint.
	 */
	final public int getPrefContentWidth(int width) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest(this + " getPrefContentWidth,width=" + width);}
		//#endif
		if ((width > 0) && (lastWidth < width)) {
			lastWidth = width;
		}
		return lastWidth;
	}

	/**
	 * @return Returns preferred height of last paint.
	 */
	final public int getPrefContentHeight(int height) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest(this + " getPrefContentHeight,height,labelPageHeight,mainPageHeight=" + height + "," + labelPageHeight + "," + mainPageHeight);}
		//#endif
		return prefViewHeight;
	}

	/**
	 * Size changed
	 */
	final public void sizeChanged(int width, int height) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest(this + " sizeChanged,width,height=" + width + "," + height);}
		//#endif
		if ((lastHeight != width) || (lastHeight != height)) {
			lastWidth = width;
			lastHeight = height;
			try {
				updCustomData(lastWidth, lastHeight, fontSize,
						underlinedStyle);
			} catch (Exception e) {
				//#ifdef DTEST
				Common.debugErr("PageMgr.sizeChanged view failed");
				//#endif
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#keyPressed(int)
	 */
	final public void keyPressed(int aKey) {
		//#ifdef DTEST
		Common.debug("Key pressed " + aKey);
		//#endif
		key(aKey);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#keyRepeated(int)
	 */
	final public void keyRepeated(int aKey) {
		//#ifdef DTEST
		Common.debug("Key repeated " + aKey);
		//#endif
		key(aKey);
	}

	/**
	 * Key actions handler
	 * @param aKey
	 */
	final public void key(final int aKey) {
		synchronized(this) {
			final int action = pageImpl.getGameAction(aKey);
			//#ifdef DTEST
			Common.debug("Key: " + aKey + ", Action: " + action);
			//#endif
			if (!scrollThread.canRun()) {
				keyNormal(aKey, action);
			} else {
				keyAutoRun(aKey, action);
			}
			//#ifdef DTEST
			Common.debug("Key action finished.");
			//#endif
		}
	}


	/**
	 * Key actions handler.  If no scroll bar, don't do movement commands.
	 * @param aKey
	 */
	final public void keyNormal(final int aKey, final int action) {
		if (useActions) {
			switch (action) {
				case PAGE_BCK_ACTION:
					if (viewScrollBar) {
						prevPage();
					}
					break;
				case PAGE_FWD_ACTION:
					if (viewScrollBar) {
						nextPage();
					}
					break;
				case LINE_FWD_ACTION:
					if (viewScrollBar) {
						nextLine();
					}
					break;
				case LINE_BCK_ACTION:
					if (viewScrollBar) {
						prevLine();
					}
					break;
				case BACK_ACTION:
					display.setCurrent(prev);
					break;
				case SELECT_ACTION:
					pageImpl.selectKey();
					break;
				case SCROLL_ACTION:
					if (viewScrollBar && (lastHeight < mainPageHeight)) {
						scrollThread.setRun(true);
					}
					break;
				default:
					break;
			}
		} else {
			Integer ikey = new Integer(aKey);
			if (ikey.equals((Integer)gameKeys.get(new Integer(PAGE_BCK_ACTION)))) {
				if (viewScrollBar) {
					prevPage();
				}
			} else if (ikey.equals(
					(Integer)gameKeys.get(new Integer(PAGE_FWD_ACTION)))) {
				if (viewScrollBar) {
					nextPage();
				}
			} else if (ikey.equals(
					(Integer)gameKeys.get(new Integer(LINE_FWD_ACTION)))) {
				if (viewScrollBar) {
					nextLine();
				}
			} else if (ikey.equals(
					(Integer)gameKeys.get(new Integer(LINE_BCK_ACTION)))) {
				if (viewScrollBar) {
					prevLine();
				}
			} else if (ikey.equals(
					(Integer)gameKeys.get(new Integer(BACK_ACTION)))) {
					display.setCurrent(prev);
			} else if (ikey.equals(
					(Integer)gameKeys.get(new Integer(SCROLL_ACTION)))) {
				if (viewScrollBar && (lastHeight < mainPageHeight)) {
					scrollThread.setRun(true);
				}
			} else if (ikey.equals(
					(Integer)gameKeys.get(new Integer(SELECT_ACTION)))) {
				if (viewScrollBar) {
					pageImpl.selectKey();
				}
			}
		}
	}


	/**
	 * Key handlers for Autorun book reading.
	 * @param aKey
	 */
	final public void keyAutoRun(final int aKey, final int action) {
		Integer ikey = new Integer(aKey);
		if ((useActions &&
		    (((action == PAGE_BCK_ACTION) || (action == LINE_BCK_ACTION) ||
				(action == Canvas.GAME_B)))) ||
		   (!useActions &&
			(ikey.equals(
					(Integer)gameKeys.get(new Integer(PAGE_BCK_ACTION))) ||
			 ikey.equals(
					(Integer)gameKeys.get(new Integer(LINE_BCK_ACTION))) ||
			 ikey.equals(
					(Integer)gameKeys.get(new Integer(Canvas.GAME_B)))))) {
			addScrollDelay(Common.AUTOSCROLL_STEP);
		} else if ((useActions &&
		    (((action == PAGE_FWD_ACTION) || (action == LINE_FWD_ACTION)))) ||
		   (!useActions &&
			(ikey.equals(
					(Integer)gameKeys.get(new Integer(PAGE_FWD_ACTION))) ||
			 ikey.equals(
					(Integer)gameKeys.get(new Integer(LINE_FWD_ACTION)))))) {
			addScrollDelay(- Common.AUTOSCROLL_STEP);
		}
		if ((useActions && (action == SCROLL_ACTION)) ||
		    (!useActions &&
			 ikey.equals(
					(Integer)gameKeys.get(new Integer(SCROLL_ACTION))))) {
			messageOn(RssReaderMIDlet.get(Common.I_STOP));
			scrollThread.setRun(false);
		} else {
			messageOn(RssReaderMIDlet.get(Common.I_DELAY)+getScrollDelay());
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#pointerPressed(int, int)
	 */
	final public void pointerPressed(int aX, int aY) {
		//#ifdef DTEST
		Common.debug("Pointer pressed (" + aX + "," + aY + ")");
		//#endif
		int seg = (aY * 4) / getHeight();
		if (scrollThread != null) {
			return;
		}
		synchronized (this) {
			switch (seg) {
				case 0:
					prevPage();
					break;
				case 1:
					prevLine();
					break;
				case 2:
					nextLine();
					break;
				case 3:
					nextPage();
					break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Item)
	 */
	final public void commandAction(Command aCmd, Item aItem) {
		//#ifdef DTEST
		Common.debug("commandAction() for page started");
		//#endif
		if (aCmd == cmdPosition) {
			PositionForm pform = new PositionForm(Common.PAGE_POS_HEAD, view,
					(Item)pageImpl,
					midlet);
			pform.createGauge(Common.PAGE_POS_ACTUAL, view.getPercPosition());
		//#ifdef DTEST
		} else if (aCmd == cmdInfo) {
			final HelpForm helpForm = new HelpForm(midlet, (Item)pageImpl);
			helpForm.append("lastWidth=" + lastWidth + "\n");
			helpForm.append("lastHeight=" + lastHeight + "\n");
			helpForm.append("labelPageHeight=" + labelPageHeight + "\n");
			helpForm.append("mainPageHeight=" + mainPageHeight + "\n");
			helpForm.append("prefViewHeight=" + prefViewHeight + "\n");
			helpForm.append("viewScrollBar=" + viewScrollBar + "\n");
			helpForm.append("initialized=" + initialized + "\n");
			helpForm.append("resize=" + resize + "\n");
			helpForm.append("paintDone=" + paintDone + "\n");
			helpForm.append("RenderedWord.heightFont=" + RenderedWord.heightFont + "\n");
			helpForm.append("label=" + label + "\n");
			helpForm.append("page=" + (page != null) + "\n");
			helpForm.append("text=" + (text != null) + "\n");
			helpForm.append("view=" + view.getClass().getName() + "\n");
			if (view instanceof HtmlView) {
				HtmlView hview = (HtmlView)view;
				int hvlen = hview.getRenderedWordsLen();
				helpForm.append("hview.getRenderedWordsLen()=" + hvlen + "\n");
			} else if (view instanceof View) {
				View vview = (View)view;
				int vvlen = vview.getScreenSize();
				helpForm.append("vview.getScreenSize()=" + vvlen + "\n");
			}
			helpForm.append("messageFont.getHeight()=" + messageFont.getHeight() + "\n");
			display.setCurrent(helpForm);
		//#endif
		} else if (aCmd == cmdHelp) {
			final HelpForm helpForm = new HelpForm(midlet, (Item)pageImpl);
			/*Press \1 to go back to the previous screen. */
			appendKeyHelp(helpForm, BACK_ACTION, Common.TEXT_B_KEY);
			/*Press \1 to go to open the link. */
			appendKeyHelp(helpForm, SELECT_ACTION, Common.TEXT_SEL_KEY);
			if (!scrollThread.canRun()) {
				/* Page forward. */
				appendKeyHelp(helpForm, PAGE_FWD_ACTION, Common.TEXT_PF_KEY);
				/* Page backward. */
				appendKeyHelp(helpForm, PAGE_BCK_ACTION, Common.TEXT_PB_KEY);
				/* Line forward. */
				appendKeyHelp(helpForm, LINE_FWD_ACTION, Common.TEXT_LF_KEY);
				/* Line backward. */
				appendKeyHelp(helpForm, LINE_BCK_ACTION, Common.TEXT_LB_KEY);
			} else {
				/* Scroll faster. */
				appendKeyHelp(helpForm, PAGE_FWD_ACTION, Common.TEXT_AUF_KEY);
				/* Scroll slower. */
				appendKeyHelp(helpForm, PAGE_BCK_ACTION, Common.TEXT_AUS_KEY);
				appendKeyHelp(helpForm, LINE_FWD_ACTION, Common.TEXT_AUF_KEY);
				appendKeyHelp(helpForm, LINE_BCK_ACTION, Common.TEXT_AUS_KEY);
			}
			if (lastHeight < mainPageHeight) {
				appendKeyHelp(helpForm, SCROLL_ACTION, Common.TEXT_S_KEY);
			}
			display.setCurrent(helpForm);
		}
	}

  /**
   * Get the ascii key represented by the game action.  Get the resource to
   * allow localization.  Substitute this in key help. Append to help form.
   *
   * @param HelpForm - help form to append to.
   * @param aKey - Game action.
   * @param aResKey - Resource for key help
   * @author Irv Bunton
   */
	final private int appendKeyHelp(final HelpForm helpForm, int aKey,
			int aResKey) {
		String pkey = RssReaderMIDlet.get(Common.TEXT_K_0 +
				((Integer)gameKeys.get(
					new Integer(aKey))).intValue() - Canvas.KEY_NUM0);
		return helpForm.append(RssReaderMIDlet.get(aResKey, pkey));
	}

	/**
	 * Set the text to show
	 */
	final public void setText(final String aText) {
		if (view instanceof View) {
			page = new Page(aText);
			((View)view).setPage(page);
		} else {
			text = aText;
			((HtmlView)view).setText(aText);
		}
	}

	/**
	 * Scrolls one line ahead in current book
	 */
	final protected void nextLine() {
		try {
			pauseOn();
			view.fwdLine();
			if (view.emptyPage()) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(this + " Forward line too far.  Going back.");}
				//#endif
				view.bckLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			messageOff();
		}
	}

	/**
	 * Scrolls one page ahead in current book
	 */
	final protected void nextPage() {
		try {
			pauseOn();
			view.fwdPage();
			if (view.emptyPage()) {
				//#ifdef DLOGGING
				if (finestLoggable) {logger.finest(this + " Forward page too far.  Going back.");}
				//#endif
				view.bckPage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			messageOff();
		}
	}

	/**
	 * Scrolls one line back in current book
	 */
	final protected void prevLine() {
		try {
			pauseOn();
			view.bckLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			messageOff();
		}
	}

	/**
	 * Scrolls one page back in current book
	 */
	final protected void prevPage() {
		try {
			pauseOn();
			view.bckPage();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			messageOff();
		}
	}

	/**
	 * Displays "Wait" message
	 */
	final protected void pauseOn() {
		messageOn(RssReaderMIDlet.get(Common.I_WAIT));
	}

	/**
	 * Displays system message (e.g. Wait) display
	 */
	final protected void messageOn(final String aMsg) {
		synchronized(this) {
			message = aMsg;
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest(this + " message=" + message);}
			//#endif
			pageImpl.svcRepaint();
		}
	}

	/**
	 * Disable system message (e.g. Wait) display
	 */
	final protected void messageOff() {
		synchronized(this) {
			message = null;
			pageImpl.svcRepaint();
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#hideNotify()
	 */
	final public void showNotify() {
		//#ifdef DTEST
		Common.debug("PageCanvas.showNotify()");
		//#endif
		if (!scrollThread.isProcessing() || !scrollThread.isAlive()) {
			if (!scrollThread.isAlive()) {
				try {
					boolean run = scrollThread.canRun();
					//#ifdef DCLDCV11
					scrollThread = new ScrollThread("scrollThread");
					//#else
					scrollThread = new ScrollThread();
					//#endif
					scrollThread.setRun(run);
					scrollThread.start();
					//#ifdef DLOGGING
					if (finestLoggable) {logger.finest(this + " scrollThread re-started from showNotify after recreate.");}
					//#endif
				} catch (Exception e) {
					//#ifdef DLOGGING
					logger.severe(this + " Unable to start thread.", e);
					//#endif
					e.printStackTrace();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.Displayable#hideNotify()
	 */
	final public void hideNotify() {
		//#ifdef DTEST
		Common.debug("PageCanvas.hideNotify()");
		//#endif
		/* Don't change run so that help can know if it's on.  Also, it allows
		   us to keep it's value. */
		scrollThread.setProcessing(false);
		scrollThread.wakeUp();
	}

	/**
	 * Sets pause between scrolling.
	 * @param aDelay
	 */
	final public synchronized static void setScrollDelay(int aDelay) {
		if (aDelay < Common.AUTOSCROLL_STEP) {
			aDelay = Common.AUTOSCROLL_STEP;
		}
		scrollDelay = aDelay;
	}

	final public synchronized static void addScrollDelay(int aDelay) {
		setScrollDelay(getScrollDelay() + aDelay);
	}

	/**
	 * @return Returns the scrollDelay.
	 */
	final public synchronized static int getScrollDelay() {
		return scrollDelay;
	}

}
//#endif
