//--Need to modify--#preprocess
/*
 * FeedListParser.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
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
 * IB 2010-03-07 1.11.4RC1 Use observer pattern for OPML/list parsing to prevent hangs from spotty networks and bad URLs.
 * IB 2010-04-17 1.11.5RC2 Change to put compatibility classes in compatibility packages.
 * IB 2010-05-30 1.11.5RC2 Use compatibility URLHandler.
 * IB 2010-06-29 1.11.5RC2 Use compatibility observer pattern.
 * IB 2010-09-29 1.11.5Dev8 Add //#preprocess for RIM preprocessor.
 * IB 2010-10-12 1.11.5Dev9 Change to --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
*/

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.rssreader.businesslogic.compatibility4;

import com.substanceofcode.rssreader.businessentities.compatibility4.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.rssreader.businesslogic.compatibility4.URLHandler;
import java.io.IOException;
import java.io.InputStream;

import com.substanceofcode.utils.compatibility4.CauseException;
//#ifdef DMIDP20
import net.eiroca.j2me.observable.compatibility4.Observable;
import net.eiroca.j2me.observable.compatibility4.ObserverManager;
//#endif
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.LoadingForm;
import com.substanceofcode.utils.MiscUtil;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.LogManager;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Base class for feed list parsers.
 *
 * @author Tommi Laukkanen
 */
public abstract class FeedListParser extends URLHandler
implements
//#ifdef DMIDP20
			Observable,
//#endif
	Runnable {
    
    private Thread m_parsingThread;
    private LoadingForm m_loadForm = null;
    private int m_maxItemCount = 10;
	protected String m_url;
	protected String m_username;
	protected String m_password;
	protected String m_feedNameFilter;
	protected String m_feedURLFilter;
	protected boolean m_getFeedTitleList = false;
	protected boolean m_redirectHtml = false;
	protected RssItunesFeedInfo[] m_feeds;
    private boolean m_successfull = false;
    private CauseException m_ex = null;
    private boolean m_redirect = false;  // The RSS feed is redirected
	//#ifdef DMIDP20
    private ObserverManager observerMgr = null;
	//#endif
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.FeedListParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
	//#endif

    /** Creates a new instance of FeedListParser */
    public FeedListParser(String url, String username, String password) {
		super();
        m_parsingThread = new Thread(this);
		m_url = url;
		m_username = username;
		m_password = password;
		//#ifdef DMIDP20
		observerMgr = new ObserverManager(this);
		//#endif
    }
    
    /** Start parsing the feed list */
    public void startParsing() {
        m_parsingThread.start();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Thread started=" + m_parsingThread);}
		//#endif
    }
    
    /** Get feed list */
    public RssItunesFeedInfo[] getFeeds() {
        return m_feeds;
    }
    
	//#ifdef DMIDP20
	public ObserverManager getObserverManager() {
		return observerMgr;
	}
	//#endif

    /** Parsing thread */
    public void run() {
        try {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("Thread running=" + this);}
			//#endif
            m_feeds = parseFeeds();
			if (m_getFeedTitleList) {
				for(int feedIndex=0; feedIndex<m_feeds.length; feedIndex++) {
					String name = m_feeds[feedIndex].getName();
					if ((name == null) || (name.length() == 0)) {
						RssItunesFeed feed = (RssItunesFeed)m_feeds[feedIndex];
						RssFeedParser fparser = new RssFeedParser( feed );
						if (m_loadForm != null) {
							m_loadForm.appendMsg("Loading title from " +
										feed.getUrl());
						}
						//#ifdef DLOGGING
						logger.finest("Getting title for url=" + feed.getUrl());
						//#endif
						fparser.setGetTitleOnly(true);
						/** Get RSS feed */
						try {
							fparser.parseRssFeed( false, m_maxItemCount );
							m_feeds[feedIndex] = fparser.getRssFeed();
							if (m_loadForm != null) {
								m_loadForm.appendMsg("ok\n");
							}
						} catch(Exception ex) {
							if (m_loadForm != null) {
								m_loadForm.recordExcForm("Error loading title for feed " +
										feed.getUrl(), ex);
							}
						}
					}
				}
			}
			m_successfull = true;
        } catch( IOException ex ) {
			//#ifdef DLOGGING
			logger.severe("FeedListParser.run(): Error while parsing " +
					      "feeds: " + m_url, ex);
			//#endif
            // TODO: Add exception handling
            System.err.println("FeedListParser.run(): Error while parsing feeds: " + ex.toString());
			m_ex = new CauseException("Error while parsing feed " + m_url, ex);
        } catch( Exception ex ) {
			//#ifdef DLOGGING
			logger.severe("FeedListParser.run(): Error while parsing " +
					      "feeds: " + m_url, ex);
			//#endif
            // TODO: Add exception handling
            System.err.println("FeedListParser.run(): Error while parsing feeds: " + ex.toString());
			m_ex = new CauseException("Error while parsing feed " + m_url, ex);
        } catch( OutOfMemoryError t ) {
			System.gc();
			// Save memory by releasing it.
			m_feeds = null;
			//#ifdef DLOGGING
			logger.severe("FeedListParser.run(): Out Of Memory Error while " +
					"parsing feeds: " + m_url, t);
			//#endif
            // TODO: Add exception handling
            System.err.println("FeedListParser.run(): " +
					"Out Of Memory Error while parsing feeds: " + t.toString());
			m_ex = new CauseException("Out Of Memory Error while parsing " +
					"feed " + m_url, t);
        } catch( Throwable t ) {
			//#ifdef DLOGGING
			logger.severe("FeedListParser.run(): Error while parsing " +
					      "feeds: " + m_url, t);
			//#endif
            // TODO: Add exception handling
            System.err.println("FeedListParser.run(): Error while parsing feeds: " + t.toString());
			m_ex = new CauseException("Internal error while parsing feed " +
									  m_url, t);
        } finally {
			//#ifdef DLOGGING
			if (m_ex != null) {
				logger.severe("run parse " + m_ex.getMessage(), m_ex);
			}
			//#endif
			//#ifdef DMIDP20
			if (observerMgr != null) {
				observerMgr.notifyObservers(this);
			}
			//#endif
        }        
    }  
    
    
    /** Get feeds from selected url */
    public RssItunesFeedInfo[] parseFeeds()
	throws IOException, Exception {
        
		try {
			super.handleOpen(m_url, m_username, m_password, false, false, null,
					"");
			if (m_needRedirect) {
				m_needRedirect = false;
				m_feeds = parseHeaderRedirect(m_location);
				return m_feeds;
			}

			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("m_redirectHtml=" + m_redirectHtml);}
			//#endif
			// If we find HTML, usually it is redirection
			if (m_redirectHtml && (m_contentType != null) &&
					(m_contentType.indexOf("html") >= 0)) {
				return parseHTMLRedirect();
			} else {
				return parseFeeds(m_inputStream);
			}
        } catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("Import error with " + m_url, e);
			//#endif
			if ((m_url != null) && m_url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
            throw new CauseException("Error while parsing import data: " 
                    + e.toString(), e);
        } catch(OutOfMemoryError t) {
			// Save memory by releasing it.
			m_feeds = null;
			System.gc();
			//#ifdef DLOGGING
			logger.severe("Out Of Memory Error with " + m_url, t);
			//#endif
			if ((m_url != null) && m_url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
            throw new CauseException("Out Of Memory Error while parsing RSS " +
					"data", t);
        } catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("parseFeeds error with " + m_url, t);
			//#endif
			if ((m_url != null) && m_url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
            throw new CauseException("Internal error while parsing RSS data",
									 t);
        } finally {
			super.handleClose();
		}
    }    
    
	/** If header shows redirect, handle it here. */
	private RssItunesFeedInfo[] parseHeaderRedirect(String newUrl)
    throws IOException, Exception {
		if (m_redirect) {
			//#ifdef DLOGGING
			logger.severe("Error 2nd header redirect url:  " + newUrl);
			//#endif
			System.out.println("Error 2nd header redirect url " +
					m_redirectUrl + " to 2nd redirect " + newUrl);
			throw new IOException("Error 2nd header redirect url " +
					m_redirectUrl + " to 2nd redirect " + newUrl);
		}
		m_redirect = true;
		m_redirectUrl = m_url;
		m_url = newUrl;
		try {
			return parseFeeds();
		} finally {
			m_url = newUrl;
		}
	}

	/** Read HTML and if it has links, redirect and parse the XML. */
	private RssItunesFeedInfo[] parseHTMLRedirect()
    throws IOException, Exception {
		String svUrl = m_url;
		m_url = super.parseHTMLRedirect(m_url, m_inputStream);
		try {
			return parseFeeds();
		} finally {
			m_url = svUrl;
		}
	}

    abstract RssItunesFeedInfo[] parseFeeds(InputStream is);

    public void setFeedNameFilter(String feedNameFilter) {
        if (feedNameFilter == null) {
			this.m_feedNameFilter = null;
		} else if (feedNameFilter.length() == 0) {
			this.m_feedNameFilter = null;
		} else {
			this.m_feedNameFilter = feedNameFilter.toLowerCase();
		}
    }

    public String getFeedNameFilter() {
        return (m_feedNameFilter);
    }

    public void setFeedURLFilter(String m_feedURLFilter) {
        if (m_feedURLFilter == null) {
			this.m_feedURLFilter = null;
		} else if (m_feedURLFilter.length() == 0) {
			this.m_feedURLFilter = null;
		} else {
			this.m_feedURLFilter = m_feedURLFilter.toLowerCase();
		}
    }

    public String getFeedURLFilter() {
        return (m_feedURLFilter);
    }

    public boolean isSuccessfull() {
        return (m_successfull);
    }

    public void setRedirectHtml(boolean m_redirectHtml) {
        this.m_redirectHtml = m_redirectHtml;
    }

    public boolean isRedirectHtml() {
        return (m_redirectHtml);
    }

    public CauseException getEx() {
        return (m_ex);
    }

    public String getUrl() {
        return (m_url);
    }

    public void join() throws InterruptedException {
		m_parsingThread.join();
	}

    public void setGetFeedTitleList(boolean getFeedTitleList) {
        this.m_getFeedTitleList = getFeedTitleList;
    }

    public void setLoadForm(LoadingForm loadForm) {
        this.m_loadForm = loadForm;
    }

    public void setMaxItemCount(int maxItemCount) {
        this.m_maxItemCount = maxItemCount;
    }

}
