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
 * IB 2010-04-30 1.11.5RC2 Track threads used.
 * IB 2010-05-24 1.11.5RC2 Optionally use feed URL as feed name if not found in import file.
 * IB 2010-05-25 1.11.5RC2 Give error if import file is empty/invalid.
 * IB 2010-05-28 1.11.5RC2 Use threads and CmdReceiver for MIDP 2.0 only.
 * IB 2010-05-28 1.11.5RC2 Don't use HTMLParser, HTMLLinkParser, and HTMLAutoLinkParser in small memory MIDP 1.0 to save space.
 * IB 2010-05-28 1.11.5RC2 Check for html, htm, shtml, and shtm suffixes.
 * IB 2010-06-27 1.11.5Dev2 Use ObservableHandler, Observer, and Observable re-written to use observer pattern without GPL code.  This is dual licensed as GPL and LGPL.
 * IB 2010-06-27 1.11.5Dev2 Make LoadingForm an independent class to remove dependency on RssReaderMIDlet for better testing.
 * IB 2010-07-04 1.11.5Dev6 Don't use m_ prefix for parameter definitions.
 * IB 2010-07-04 1.11.5Dev6 Do not have feedNameFilter and feedUrlFilter as null.
 * IB 2010-07-04 1.11.5Dev6 Cosmetic code cleanup.
 * IB 2010-07-04 1.11.5Dev6 Use null pattern using nullPtr.
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2011-01-14 1.11.5Alpha15 Only compile this is it is the full version.
 * IB 2011-01-14 1.11.5Alpha15 Save the old rss feed names to know if updating or adding apps.
 * IB 2011-01-14 1.11.5Alpha15 Have update/refresh all feeds for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Have override flag for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Handle updating existing feeds or parsing and updating/adding feeds.
 * IB 2011-01-14 1.11.5Alpha15 Use procIoExc to process exception handling for IO and other exceptions including out of memory.
 * IB 2011-01-14 1.11.5Alpha15 More logging.
 * IB 2011-01-14 1.11.5Alpha15 Use RssFeedStore class for rssFeeds to allow synchornization for future background processing.
 * IB 2011-01-14 1.11.5Alpha15 Log change to m_feedURLFilter.
 * IB 2011-01-14 1.11.5Alpha15 Cosmetic.
*/
// Expand to define MIDP define
@DMIDPVERS@
// Expand to define memory size define
@DMEMSIZEDEF@
// Expand to define full vers define
@DFULLVERSDEF@
// Expand to define full vers define
@DINTLINKDEF@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
//#ifdef DFULLVERS
package com.substanceofcode.rssreader.businesslogic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.substanceofcode.rssreader.businessentities.RssItunesFeed;
import com.substanceofcode.rssreader.businessentities.RssFeedStore;
//#ifndef DSMALLMEM
import com.substanceofcode.utils.HTMLParser;
//#endif
import com.substanceofcode.utils.CauseException;
//#ifdef DMIDP20
import net.yinlight.j2me.observable.Observable;
import net.yinlight.j2me.observable.ObservableHandler;
//#endif
import com.substanceofcode.utils.MiscUtil;
import com.substanceofcode.rssreader.presentation.RssReaderMIDlet;
import com.substanceofcode.rssreader.presentation.LoadingForm;

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
public abstract class FeedListParser
implements
//#ifdef DMIDP20
			Observable,
//#endif
	Runnable {
    
	final       Object nullPtr = null;
    final private Thread m_parsingThread;
    private LoadingForm m_loadForm = null;
    private int m_maxItemCount = 10;
	protected String m_url;
	protected String m_username;
	protected String m_password;
	protected URLHandler urlHandler;
	RssFeedStore m_oldRssFeeds;
	Hashtable m_oldRssNames;
	protected String m_feedNameFilter = "";
	protected String m_feedURLFilter = "";
	protected boolean m_getFeedTitleList = false;
	protected boolean m_getAllFeedList = false;
	protected boolean m_getAllUpdFeedList = false;
	protected boolean m_useFeedUrlList = false;
	protected boolean m_override = false;  // The noticy flag for override
	//#ifndef DSMALLMEM
	protected boolean m_redirectHtml = false;
	//#endif
	protected RssItunesFeed[] m_feeds;
    final private boolean m_needParse;
    private boolean m_successfull = false;
    private Exception m_ex = null;
    private boolean m_redirect = false;  // The RSS feed is redirected
	//#ifdef DMIDP20
    private ObservableHandler observableHandler = null;
	//#endif
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("FeedListParser");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    volatile protected boolean m_logChar    = false; // Log characters use traceLoggable
    volatile protected boolean m_logParseChar = false; // Log characters use traceLoggable
    volatile protected boolean m_logRepeatChar = false; // Log characters use traceLoggable
    volatile protected boolean m_logReadChar = false; // Log characters use traceLoggable
	//#endif

    /** Creates a new instance of FeedListParser */
    public FeedListParser(String url, String username, String password,
			RssFeedStore rssFeeds) {
		urlHandler = new URLHandler();
		m_needParse = true;
		RssFeedStore[] arrFeeds = rssFeeds.copyFeeds();
		m_oldRssFeeds = (RssFeedStore)arrFeeds[0];
		m_oldRssNames = arrFeeds[1];
        m_parsingThread = MiscUtil.getThread(this, "FeedListParser", this,
				"constructor");
		m_url = url;
		m_username = username;
		m_password = password;
		//#ifdef DMIDP20
		observableHandler = new ObservableHandler();
		//#endif
    }
    
    /** Creates a new instance of FeedListParser */
    public FeedListParser(RssItunesFeed[] feeds, RssFeedStore rssFeeds) {
		urlHandler = null;
		m_needParse = false;
		RssFeedStore[] arrFeeds = rssFeeds.copyFeeds();
		m_oldRssFeeds = (RssFeedStore)arrFeeds[0];
		m_oldRssNames = arrFeeds[1];
        m_parsingThread = MiscUtil.getThread(this, "FeedListParser", this,
				"constructor");
		m_url = null;
		m_username = null;
		m_password = null;
		m_feeds = feeds;
		//#ifdef DMIDP20
		observableHandler = new ObservableHandler();
		//#endif
    }
    
    /** Start parsing the feed list */
    public void startParsing() {
        m_parsingThread.start();
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("Thread started=" + MiscUtil.getThreadInfo(m_parsingThread));}
		//#endif
    }
    
    /** Get feed list */
    public RssItunesFeed[] getFeeds() {
        return m_feeds;
    }
    
	//#ifdef DMIDP20
	public ObservableHandler getObservableHandler() {
		return observableHandler;
	}
	//#endif

    /** Parsing thread */
    public void run() {
        try {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("Thread running=" + MiscUtil.getThreadInfo(m_parsingThread));}
			//#endif
            if (m_needParse && (((m_feeds = parseFeeds()) == null) || (m_feeds.length == 0))) {
				m_successfull = false;
				m_ex = new CauseException("Invalid or empty import file " +
						m_url);
				return;
			} else if (m_getFeedTitleList || m_getAllFeedList ||
					m_getAllUpdFeedList || m_useFeedUrlList) {
				for(int feedIndex=0; feedIndex<m_feeds.length; feedIndex++) {
					RssItunesFeed feed = m_feeds[feedIndex];
					String name = feed.getName();
					String oldname;
					if ((name.length() == 0) && m_useFeedUrlList) {
						name = feed.getUrl();
						feed.setName(name);
					}
					boolean hasNoName = (name.length() == 0);
					if (hasNoName) {
						oldname = (String)m_oldRssNames.get(feed.getUrl());
						if (oldname == null) {
							oldname = "";
						} else if ((oldname.length() > 0)) {
							name = oldname;
							hasNoName = false;
						}
					} else {
						oldname = name;
					}
					RssItunesFeed oldfeed = ((oldname.length() > 0) ?
							m_oldRssFeeds.get(oldname) : null);
					boolean pres = ((name.length() != 0) &&
							oldname.equals(name) && (oldfeed != null));
					if (pres && !m_getAllFeedList && !m_override) {
						CauseException ce = new CauseException("Error:  Feed " +
								"already exists with name " + name +
								".  Existing feed not updated.  " +
								"Use override in place to override an existing " +
								"feed with an old feed with the same name.");
						if (m_loadForm != null) {
							m_loadForm.addExc(ce.getMessage(), ce);
						}
						//#ifdef DLOGGING
						logger.severe("FeedListParser.run(): No override", ce);
						//#endif
						continue;
					} else if (((name.length() == 0) && m_getFeedTitleList) ||
								((name.length() != 0) && !pres) ||
								(pres && (m_getAllFeedList || m_override))) {
						if (pres && (m_loadForm != null)) {
							m_loadForm.appendNote(
									"Overriding existing feed with the one from " +
									"import feed name " + name);
						}
						RssFeedParser fparser = new RssFeedParser( feed,
								oldfeed, m_getAllUpdFeedList && pres);
						if (m_loadForm != null) {
							if (name.length() == 0) {
								m_loadForm.appendMsg(
										"Loading title for name from " +
										feed.getUrl());
							} else {
								m_loadForm.appendMsg("Loading feed from " +
										feed.getUrl());
							}
						}
						//#ifdef DLOGGING
						logger.finest((m_getFeedTitleList ?
									"Getting title for url=" :
									(m_getAllUpdFeedList ?
									 "Updating feed for url=" :
								"Getting feed for url=")) +
								feed.getUrl());
						//#endif
						fparser.setGetTitleOnly(m_getFeedTitleList);
						/** Get RSS feed */
						try {
							fparser.parseRssFeed( m_getAllUpdFeedList,
									m_maxItemCount );
							feed = fparser.getRssFeed();
							if ((name.length() == 0) &&
								(oldname.length() != 0) && 
									feed.getName().equals(oldname)) {
								if (m_loadForm != null) {
									m_loadForm.appendNote(
										"Loaded title/name matches existing name, " +
										name);
								}
								if (!m_getAllFeedList && !m_override) {
									CauseException ce = new CauseException(
											"Error:  Feed " +
											"already exists with name " + name +
											".  Existing feed not updated.  " +
											"Use override in place to override an existing " +
											"feed with an old feed with the same name.");
									if (m_loadForm != null) {
										m_loadForm.addExc(ce.getMessage(), ce);
									}
									continue;
								}
								oldfeed = ((oldname != null) ?
										m_oldRssFeeds.get(oldname) : null);
								pres = true;
							}
							if (pres) {
								feed.checkPresRead(m_getAllUpdFeedList,
										oldfeed);
							}
							m_feeds[feedIndex] = feed;
							if (m_loadForm != null) {
								m_loadForm.appendMsg("ok\n");
							}
						} catch(Exception ex) {
							if (m_loadForm != null) {
								m_loadForm.recordExcForm(
										"Error loading title for feed " +
										feed.getUrl(), ex);
							}
						}
					}
				}
			}
			m_successfull = true;
        } catch( Throwable e ) {
			m_ex = URLHandler.procIoExc("Error while parsing import data ", e,
					false, m_url,
					"Out of memory error while parsing import data ",
					"Internal error while parsing import data ",
					"run()", m_loadForm
					//#ifdef DLOGGING
					,logger
					//#endif
					);
        } finally {
			//#ifdef DMIDP20
			MiscUtil.removeThread(m_parsingThread);
			//#endif
			//#ifdef DLOGGING
			if (m_ex != null) {
				logger.severe("run parse " + m_ex.getMessage(), m_ex);
			}
			//#endif
			//#ifdef DMIDP20
			if (observableHandler != null) {
				observableHandler.notifyObservers(this);
			}
			//#endif
        }        
    }  
    
    
    /** Get feeds from selected url */
    public RssItunesFeed[] parseFeeds() throws IOException, Exception {
        
		try {
			urlHandler.handleOpen(m_url, m_username, m_password, false, false,
					null, "",
					"Error while parsing import data ",
					"Out of memory error while parsing import data ",
					"Internal error while parsing import data ");
			if (urlHandler.m_needRedirect) {
				urlHandler.m_needRedirect = false;
				m_feeds = parseHeaderRedirect(urlHandler.m_location);
				return m_feeds;
			}

			//#ifndef DSMALLMEM
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("m_redirectHtml=" + m_redirectHtml);}
			//#endif
			// If we find HTML, usually it is redirection
			if (m_redirectHtml && HTMLParser.isHtml(urlHandler.m_contentType)) {
				return parseHTMLRedirect();
			} else {
			//#endif
				return parseFeeds(urlHandler.m_inputStream);
			//#ifndef DSMALLMEM
			}
			//#endif
        } catch(Throwable e) {
			Exception ex = URLHandler.procIoExc("Error while parsing import data ", e,
					false, m_url,
					"Out of memory error while parsing import data ",
					"Internal error while parsing import data ",
					"parseFeeds()", m_loadForm
					//#ifdef DLOGGING
					,logger
					//#endif
					);
			throw ex;
        } finally {
			urlHandler.handleClose();
		}
    }
    
	/** If header shows redirect, handle it here. */
	private RssItunesFeed[] parseHeaderRedirect(String newUrl)
    throws IOException, Exception {
		if (m_redirect) {
			//#ifdef DLOGGING
			logger.severe("Error 2nd header redirect url:  " + newUrl);
			//#endif
			System.out.println("Error 2nd header redirect url " +
					urlHandler.m_redirectUrl + " to 2nd redirect " + newUrl);
			throw new IOException("Error 2nd header redirect url " +
					urlHandler.m_redirectUrl + " to 2nd redirect " + newUrl);
		}
		m_redirect = true;
		urlHandler.m_redirectUrl = m_url;
		m_url = newUrl;
		try {
			return parseFeeds();
		} finally {
			m_url = newUrl;
		}
	}

	//#ifndef DSMALLMEM
	/** Read HTML and if it has links, redirect and parse the XML. */
	private RssItunesFeed[] parseHTMLRedirect()
    throws IOException, Exception {
		String svUrl = m_url;
		m_url = urlHandler.parseHTMLRedirect(m_url, urlHandler.m_inputStream);
		try {
			return parseFeeds();
		} finally {
			m_url = svUrl;
		}
	}
	//#endif

    abstract RssItunesFeed[] parseFeeds(InputStream is);

    public void setFeedNameFilter(String feedNameFilter) {
        if (feedNameFilter == null) {
			this.m_feedNameFilter = "";
		} else {
			this.m_feedNameFilter = feedNameFilter.toLowerCase();
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("setFeedNameFilter=" + feedNameFilter);}
		//#endif
    }

    public String getFeedNameFilter() {
        return (m_feedNameFilter);
    }

	public void setFeedURLFilter(String feedURLFilter) {
		if (feedURLFilter == null) {
			this.m_feedURLFilter = "";
		} else {
			this.m_feedURLFilter = feedURLFilter.toLowerCase();
		}
		//#ifdef DLOGGING
		if (fineLoggable) {logger.fine("setFeedURLFilter=" + feedURLFilter);}
		//#endif
	}

    public String getFeedURLFilter() {
        return (m_feedURLFilter);
    }

    public boolean isSuccessfull() {
        return (m_successfull);
    }

	//#ifndef DSMALLMEM
    public void setRedirectHtml(boolean redirectHtml) {
        this.m_redirectHtml = redirectHtml;
    }

    public boolean isRedirectHtml() {
        return (m_redirectHtml);
    }
	//#endif

    public Exception getEx() {
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

    public void setUseFeedUrlList(boolean useFeedUrlList) {
        this.m_useFeedUrlList = useFeedUrlList;
    }

    public void setLoadForm(LoadingForm loadForm) {
        this.m_loadForm = loadForm;
    }

    public void setMaxItemCount(int maxItemCount) {
        this.m_maxItemCount = maxItemCount;
    }

	//#ifdef DLOGGING
	//#ifdef DTEST
    public void setLogChar(boolean logChar) {
        this.m_logChar = logChar;
    }

    public boolean isLogChar() {
        return (m_logChar);
    }
    public void setLogReadChar(boolean logReadChar) {
        this.m_logReadChar = logReadChar;
    }

    public boolean isLogReadChar() {
        return (m_logReadChar);
    }

    public void setLogParseChar(boolean logParseChar) {
        this.m_logParseChar = logParseChar;
    }

    public boolean isLogParseChar() {
        return (m_logParseChar);
    }

    public void setLogRepeatChar(boolean logRepeatChar) {
        this.m_logRepeatChar = logRepeatChar;
    }

    public boolean isLogRepeatChar() {
        return (m_logRepeatChar);
    }

	//#endif
	//#endif

}
//#endif
