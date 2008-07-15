/*
 * ViewPage.java
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
// Expand to define test define
@DTESTDEF@

//#ifdef DMIDP20
package cz.cacek.ebook;

import java.io.EOFException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import cz.cacek.ebook.Common;
import cz.cacek.ebook.Page;

/**
 * Implementation of ebook content screen. 
 * @author Tomáš Darmovzal [tomas.darmovzal (at) seznam.cz]
 * @author Josef Cacek [josef.cacek (at) atlas.cz]
 * @author $Author$
 * @version $Revision$
 * @created $Date$
 */
public class ViewPage {
	protected int width;
	protected int height;
	protected int background;
	protected int foreground;
	protected Font font;
	protected StringBuffer buffer;
	protected int borderSpace;
	protected int lineSpace;
	protected boolean wrapSpaces;
	protected Page page;
	protected int position;
	protected int scrollWidth;
	protected int scrollHeight;
	protected int[] charWidths;
	protected Image offscreen;

	private Screen screen;

    /**
     * Class Screen holds lines which are currently displayed on the screen.
     * Class is synchronized.<p/>
     * Instances of this class are created during LCD-screen font change.
     * @author Josef Cacek [josef.cacek (at) atlas.cz]
     * @author $Author$
     * @version $Revision$
     * @created $Date$
     */
	static class Screen {
		private int positions[];
		private String content[];

		/**
         * Creates new screen object
		 * @param aLines
		 */
		public Screen(int aLines) {
			positions = new int[aLines+1];
			content = new String[aLines];
		}
		
		/**
         * Returns count of rows displayed on screen.
		 * @return count of rows displayed on screen
		 */
		public int size() {
			synchronized(this) {
				return content.length;
			}
		}
		
		/**
         * Returns position (in page) of first character displayed on given line.
		 * @param aLine line for which is position returned
		 * @return position of line in page
		 */
		public int getPosition(int aLine) {
			synchronized(this) {
				return positions[aLine];
			}
		}

		/**
         * Sets position of line
		 * @param aLine
		 * @param aPos
		 */
		public void setPosition(int aLine, int aPos) {
			synchronized(this) {
				positions[aLine] = aPos;
			}
		}

		/**
         * Returns string displayed in given row.
		 * @param aLine index of row
		 * @return string displayed in given row
		 */
		public String getContent(int aLine) {
			synchronized(this) {
				return content[aLine];
			}
		}

		/**
         * Sets string to display in given line
		 * @param aLine index of row
		 * @param aStr
		 */
		public void setContent(int aLine, String aStr) {
			synchronized(this) {
				content[aLine] = aStr;
			}
		}
		
		/**
         * Rolls positions and content forward and adds new line. First displayed line
         * is deleted (rolled out).
         * to the end.
		 * @param aLine new line
		 * @param aNewPosition position of new line (position in page)
		 */
		void rollFw(String aLine, int aNewPosition) {
			synchronized(this) {
				for (int i=0; i<content.length-1; i++) {
					content[i] = content[i + 1];
					positions[i] = positions[i + 1];
				}
				positions[content.length - 1] = positions[content.length];
				content[content.length - 1] = aLine;
				positions[positions.length - 1] = aNewPosition;
			}
		}

		/**
         * Rolls backward, new line (given as parameter) is added to the beginning.
		 * @param aLine new line
		 * @param aNewPosition
         * @see #rollFw(String, int)
		 */
		void rollBw(String aLine, int aNewPosition) {
			synchronized(this) {
				positions[content.length] = positions[content.length - 1];
				for (int i = content.length - 1; i > 0; i--) {
					content[i] = content[i - 1];
					positions[i] = positions[i - 1];
				}
				content[0] = aLine;
				positions[0] = aNewPosition;
			}
		}

	}

	/**
	 * Constructor
	 * @param aWidth
	 * @param aHeight
	 * @throws Exception
	 */
	public ViewPage(int aWidth, int aHeight) throws Exception {
		width = aWidth;
		height = aHeight;
		buffer = new StringBuffer(256);
		borderSpace = 2;
		lineSpace = 0;
		wrapSpaces = true;
		scrollWidth = 5;
		scrollHeight = 5;
		charWidths = new int[256];
		setColors(0xFFFFFF, 0x000000);
		setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN,
				Font.SIZE_SMALL));
		offscreen = Image.createImage(width, height);
	}

	/**
	 * Sets active page for view.
	 * @param aPage
	 */
	public void setPage(Page aPage) {
		page = aPage;
        setPosition(page.getPosition());
        fillPage();
	}

	/**
	 * Sets font for view
	 * @param aFont
	 * @throws Exception
	 */
	public void setFont(Font aFont) throws Exception {
		font = aFont;
		int tmpLines = (height - (2 * borderSpace))
				/ (font.getHeight() + lineSpace);
		screen = new Screen(tmpLines);
		for (int i = 0; i < charWidths.length; i++) {
			charWidths[i] = font.charWidth((char) i);
		}
		if (page != null) {
			fillPage();
		}
	}

	/**
	 * Returns font 
	 * @return font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets FG/BG colors for view
	 * @param aBG
	 * @param aFG
	 */
	public void setColors(int aBG, int aFG) {
		background = aBG;
		foreground = aFG;
	}

	/**
	 * Returns foreground color.
	 * @return foreground color
	 */
	public int getForegroundColor() {
		return foreground;
	}

	/**
	 * Returns background color.
	 * @return background color
	 */
	public int getBackgroundColor() {
		return background;
	}

	/**
	 * Sets position in active page in characters
	 * @param aPos
	 */
	public void setPosition(int aPos) {
		position = aPos;
		page.setPosition(position);
	}

	/**
	 * Sets position in active page in percents
	 * @param aPerc
	 */
	public void setPercPosition(int aPerc) {
		setPosition((page.size - 1) * aPerc / 100);
	}

	/**
	 * Returns current position as a percentige of page.
	 * 
	 * @return current position as a percentige of page
	 */
	public int getPercPosition() {
		return position * 100 / (page.size - 1);
	}
	
	/**
	 * Moves view one page ahead
	 * @throws Exception
	 */
	public void fwdPage() throws Exception {
		position = screen.getPosition(screen.size());
		fillPage();
	}

	/**
	 * Moves view one page back
	 * @throws Exception
	 */
	public void bckPage() throws Exception {
		for (int i = 0, n=screen.size(); i < n; i++) {
			bckLine();
		}
	}

	/**
	 * Moves view one line ahead
	 * @throws Exception
	 * @return true if scrolling is succesfull
	 */
	public boolean fwdLine() throws Exception {
		synchronized(this) {
			//#ifdef DTEST
			Common.debug("fwdLine() started");
			//#endif
			page.setPosition(screen.getPosition(screen.size()));
			final String tmpLine = nextLine();
			final boolean tmpResult = tmpLine != null;
			if (tmpResult) {
				screen.rollFw(tmpLine, page.getPosition());
				position = screen.getPosition(0);
			}
			//#ifdef DTEST
			Common.debug("fwdLine() finished ("+tmpResult+")");
			//#endif
			return tmpResult;
		}
	}

	/**
	 * Moves view one line back
	 * @throws Exception
	 */
	public void bckLine() throws Exception {
		synchronized(this) {
			page.setPosition(screen.getPosition(0) - 1);
			String line = prevLine();
			if (line != null) {
				screen.rollBw(line, page.getPosition() + 1);
				position = screen.getPosition(0);
			}
		}
	}

	/**
	 * fills page from current position
	 */
	public void fillPage() {
		//#ifdef DTEST
		Common.debug("fillPage() started");
		//#endif
		page.setPosition(position);
		screen.setPosition(0, position);
		try {
			for (int i = 0, n=screen.size(); i < n; i++) {
				screen.setContent(i, nextLine());
				screen.setPosition(i + 1, page.getPosition());
			}
		} catch (Exception e) {
			Common.debugErr(e.getMessage());			
			throw new RuntimeException(e.getMessage());
		}
		//#ifdef DTEST
		Common.debug("fillPage() finished");
		//#endif
	}

	/**
	 * Reads and returns next line for view.
	 * @return next line
	 * @throws Exception
	 */
	protected String nextLine() throws Exception {
		if(page.getPosition() >= page.size - 1) { 
			return null;
		}
		int len = 0;
		int ws = -1;
		int index = 0;
		boolean eof = false;
		buffer.setLength(0);
		for(;;){
			char c;
			try {
				c = page.readNext();
			} catch (EOFException e){
				eof = true;
				break;
			}
			if(c == '\t') c = ' ';
			if(c == '\r' || (c == ' ' && index==0)) continue;
			if(c == '\n') break;
			if(c == ' ') ws = index;
			len += charWidth(c);
			if(len > width - (2 * borderSpace) - scrollWidth){
				page.readPrev();
				if((ws != -1) && wrapSpaces){
					int discard = index - ws - 1;
					for(int i = 0; i < discard; i++) { 
						page.readPrev();
					}
					index = ws;
					buffer.setLength(index);
				}
				break;
			}
			buffer.append(c);
			index++;
		}
		if (eof && index == 0) {
			return null;
		}
		return (buffer.toString()).trim();
	}

	/**
	 * Reads and returns previous line. (backward reading)
	 * @return previous line
	 * @throws Exception
	 */
	protected String prevLine() throws Exception {
		if (page.getPosition() <= 0)
			return null;
		int len = 0;
		int ws = -1;
		int index = 0;
		boolean eof = false;
		buffer.setLength(0);
		for (;;) {
			char c;
			try {
				c = page.readPrev();
			} catch (EOFException e) {
				eof = true;
				break;
			}
			if (c == '\t') c = ' ';
			if (c == '\r' || (c==' ' && index==0)) continue;
			if (c == '\n') break;
			if (c == ' ') ws = index;
			len += charWidth(c);
			if (len > width - (2 * borderSpace) - scrollWidth) {
				page.readNext();
				if ((ws != -1) && wrapSpaces) {
					int discard = index - ws - 1;
					for (int i = 0; i < discard; i++) {
						page.readNext();
					}
					index = ws;
					buffer.setLength(index);
				}
				break;
			}
			buffer.append(c);
			index++;
		}
		if (eof && index==0) {
			return null;			
		}
		buffer.reverse();
		return (buffer.toString()).trim();
	}

	/**
	 * returns width of given character for current font
	 * @param aChr character
	 * @return width of given character
	 */
	protected int charWidth(char aChr) {
		return (aChr < 256)?charWidths[aChr]:font.charWidth(aChr);
	}

	/**
	 * Draw current view to display.
	 * @param aGraphic
	 * @param aX
	 * @param aY
	 */
	public void draw(Graphics aGraphic, int aX, int aY) {
		Graphics g = offscreen.getGraphics();
		// Draw background
		g.setColor(background);
		g.fillRect(0, 0, width, height);
		// Draw text
		g.setColor(foreground);
		g.setFont(font);
		int pos = 0;
		for (int i=0, n=screen.size(); i < n; i++) {
			String line = screen.getContent(i);
			if (line != null)
				g.drawString(line, borderSpace, borderSpace + pos,
						Graphics.LEFT | Graphics.TOP);
			pos += font.getHeight() + lineSpace;
		}
		// Draw border
		g.setColor(foreground);
		g.drawRect(0, 0, width - 1, height - 1);
		// Draw scroll
		g.setColor(background);
		g.fillRect(width - scrollWidth, 0, scrollWidth - 1, height - 1);
		g.setColor(foreground);
		g.drawRect(width - scrollWidth, 0, scrollWidth - 1, height - 1);
		int scroll = (height-scrollHeight) * page.getPosition() / page.size;
		g.fillRect(width - scrollWidth, scroll,
				scrollWidth - 1, scrollHeight - 1);
		// Draw offscreen
		aGraphic.drawImage(offscreen, aX, aY, Graphics.LEFT | Graphics.TOP);
	}
    
    /**
     * Returns position of first character on screen of current view.
     * @return current position of view
     */
    public int getPosition() {
        return screen==null?0:screen.getPosition(0);
    }
}
//#endif
