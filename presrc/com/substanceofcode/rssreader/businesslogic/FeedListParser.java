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
 * IB 2011-03-06 1.11.5Dev17 Only use thread utils if not small memory.
 * IB 2011-03-09 1.11.5Dev17 More logging.
 * IB 2011-03-11 1.11.5Dev17 Set m_urlHandler to null to save memory.
 * IB 2011-03-13 1.11.5Dev17 Save old feeds and feed names (from URL) from RssFeedStore to allow future background updates.
 * IB 2011-03-13 1.11.5Dev17 Allow access to current field names from RssFeedStore to allow future background updates.
 * IB 2011-03-13 1.11.5Dev17 Allow clearing of RssFeedStore background info.
 * IB 2011-03-13 1.11.5Dev17 Have getFeedTitleList to use title for name when parsing the whole feed.
 * IB 2011-03-13 1.11.5Dev17 Allow optional keeping of user name/password.
 * IB 2011-03-13 1.11.5Dev17 Allow optional keeping of state of items (e.g. read/unread).
 * IB 2011-03-13 1.11.5Dev17 Have getTitleOnly to use title for name when parsing just to get the title.
 * IB 2011-03-13 1.11.5Dev17 Have getFeedTitleList to use title for name when parsing the whole feed.
 * IB 2011-03-13 1.11.5Dev17 Have useFeedUrlList to use the url for name.
 * IB 2011-03-13 1.11.5Dev17 Return presence boolean array to show which feeds were added.
 * IB 2011-03-13 1.11.5Dev17 Give info messages if no loading form and logging.
 * IB 2011-03-13 1.11.5Dev17 Set urlHandler to null after parsing is done to relieve memory.
 * IB 2011-03-13 1.11.5Dev17 Have optional setting of getAllFeedList to parse all feeds.
 * IB 2011-03-13 1.11.5Dev17 Have optional setting of getAllUpdFeedList to parse all updated feeds.
 * IB 2011-03-18 1.11.5Dev17 Combine statements.
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
import java.util.Vector;

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
	protected URLHandler m_urlHandler;
	RssFeedStore m_rssFeeds;
	RssFeedStore m_oldRssFeeds;
	RssFeedStore m_oldRssNames;
	Vector m_feedNames;
	protected String m_feedNameFilter = "";
	protected String m_feedURLFilter = "";
	protected boolean m_keepModGroup = false;
	protected boolean m_keepUsPwd = false;
	protected boolean m_getTitleOnly = false;
	protected boolean m_getFeedTitleList = false;
	protected boolean m_useFeedUrlList = false;
	protected boolean m_getAllFeedList = false;
	protected boolean m_getAllUpdFeedList = false;
	protected boolean m_override = false;  // The noticy flag for override
	//#ifndef DSMALLMEM
	protected boolean m_redirectHtml = false;
	//#endif
	protected RssItunesFeed[] m_feeds = null;
	protected boolean[] m_pres = null;
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
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
    volatile protected boolean m_logChar    = false; // Log characters use traceLoggable
    volatile protected boolean m_logParseChar = false; // Log characters use traceLoggable
    volatile protected boolean m_logRepeatChar = false; // Log characters use traceLoggable
    volatile protected boolean m_logReadChar = false; // Log characters use traceLoggable
	//#endif

    /** Creates a new instance of FeedListParser */
    private FeedListParser(String url, String username, String password,
			boolean needUrlHandler, String[] afeedNames,
			RssFeedStore rssFeeds) {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("Constructor url,username,password,needUrlHandler,afeedNames,rssFeeds=" + url + "," + username + "," + password + "," + needUrlHandler + "," + afeedNames + "," + rssFeeds);}
		//#endif
		m_urlHandler = needUrlHandler ? new URLHandler() : (URLHandler)nullPtr;
		m_needParse = needUrlHandler;
		if ((m_rssFeeds = rssFeeds) != null) {
			Object[] arrFeeds = rssFeeds.copyFeeds(afeedNames);
			m_oldRssFeeds = (RssFeedStore)arrFeeds[0];
			m_oldRssNames = (RssFeedStore)arrFeeds[1];
			m_feedNames = (Vector)arrFeeds[2];
		} else {
			m_oldRssFeeds = (RssFeedStore)nullPtr;
			m_oldRssNames = (RssFeedStore)nullPtr;
			m_feedNames = (Vector)nullPtr;
		}
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
    public FeedListParser(String url, String username, String password,
			RssFeedStore rssFeeds) {
    	this(url, username, password, true, null, rssFeeds);
    }
    
    /** Creates a new instance of FeedListParser */
    public FeedListParser(String[] feedNames, RssFeedStore rssFeeds) {
    	this(null, null, null, false, feedNames, rssFeeds);
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
    
    public boolean[] getPres() {
        return (m_pres);
    }

	//#ifdef DMIDP20
	public ObservableHandler getObservableHandler() {
		return observableHandler;
	}
	//#endif

    /** Parsing thread */
    public void run() {
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("run m_feedNameFilter,m_feedURLFilter,m_keepModGroup,m_keepUsPwd,m_maxItemCount,m_useFeedUrlList,m_getFeedTitleList,m_getTitleOnly=" + m_feedNameFilter + "," + m_feedURLFilter + "," + m_keepModGroup + "," + m_keepUsPwd + "," + + m_maxItemCount + "," + m_useFeedUrlList + "," + m_getFeedTitleList + "," + m_getTitleOnly);}
		//#endif
        try {
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("Thread running=" + MiscUtil.getThreadInfo(m_parsingThread));}
			//#endif
            if (m_needParse && (((m_feeds = parseFeeds()) == null) || (m_feeds.length == 0))) {
				m_urlHandler = (URLHandler)nullPtr;
				if (m_feeds != null) {
					m_pres = new boolean[0];
				}
				m_successfull = false;
				m_ex = new CauseException("Invalid or empty import file " +
						m_url);
				return;
			} else if (m_needParse || m_getFeedTitleList || m_getAllFeedList ||
					m_getAllUpdFeedList || m_useFeedUrlList || m_getTitleOnly) {
				m_urlHandler = (URLHandler)nullPtr;
				int len;
				boolean useFeeds;
				if (useFeeds = (m_feeds != null)) {
					len = m_feeds.length;
				} else {
					len = m_feedNames.size();
				}
				Vector vfeeds = new Vector();
				boolean[] apres = new boolean[len];
				int presIndex = 0;
				for (int feedIndex=0; feedIndex < len; feedIndex++) {
					String name;
					RssItunesFeed origFeed;
					if (useFeeds) {
						origFeed = m_feeds[feedIndex];
						name = origFeed.getName();
					} else {
						if ((name = (String)m_feedNames.elementAt(feedIndex)) ==
								null) {
							origFeed = (RssItunesFeed)nullPtr;
						} else {
							origFeed = m_rssFeeds.getOld(name);
						}
					}
					if ((name == null) || (origFeed == null)) {
						//#ifdef DLOGGING
						logger.warning("Skipping null name/feed feedIndex,name,origFeed=" + feedIndex + "," + name + "," + ((origFeed == null) ? "null" : origFeed.getName()));
						//#endif
						continue;
					}
					RssItunesFeed feed = (origFeed != null) ?
						(RssItunesFeed)origFeed.clone() : (RssItunesFeed)nullPtr;
					boolean modified = false;
					boolean hasNoName;
					if ((hasNoName = (name.length() == 0)) && m_useFeedUrlList
							&& (feed != null)) {
						name = feed.getUrl();
						feed.setName(name);
						modified = true;
						hasNoName = false;
					}
					String oldname;
					if (hasNoName) {
						oldname = m_oldRssNames.getFeedName(feed.getUrl());
						if (oldname == null) {
							oldname = "";
						} else if ((oldname.length() > 0)) {
							name = oldname;
							hasNoName = false;
						}
					} else {
						oldname = name;
					}
					RssItunesFeed oldfeed = ((oldname.length() > 0) &&
								(m_oldRssFeeds != null)) ?
							m_oldRssFeeds.get(oldname) : (RssItunesFeed)nullPtr;
					boolean pres = (!hasNoName &&
							oldname.equals(name) && (oldfeed != null));
					if (m_loadForm != null) {
						m_loadForm.appendMsg((hasNoName ?
								((feed != null) ? feed.getUrl() : (String)nullPtr)
								: name) + "...");
					//#ifdef DLOGGING
					} else {
						logger.info((hasNoName ?
								((feed != null) ? feed.getUrl() : (String)nullPtr)
								: name) + "...");
					//#endif
					}
					try {
						if (pres && !m_getAllFeedList && !m_override) {
							CauseException ce = new CauseException(
									"Feed already exists with name " + name +
									".  Existing feed not updated.  " +
									"Use override in place to override an existing " +
									"feed with an old feed with the same name.");
							if (m_loadForm != null) {
								m_loadForm.addExc("Error\n" + ce.getMessage(),
										ce);
							}
							//#ifdef DLOGGING
							logger.severe("FeedListParser.run(): No override", ce);
							//#endif
							continue;
						} else if (((name.length() == 0) && (m_getFeedTitleList
										|| m_getTitleOnly)) ||
								((m_getAllFeedList || m_getAllUpdFeedList) &&
								 (name.length() != 0) && (!pres ||
									 (pres && m_override)))) {
							if (pres && (m_loadForm != null)) {
								m_loadForm.appendNote(
										"Warning\nOverriding existing feed with the one from " +
										"import feed name " + name);
							//#ifdef DLOGGING
							} else if(pres) {
								logger.info(
										"Warning\nOverriding existing feed with the one from " +
										"import feed name " + name);
							//#endif
							}
							RssFeedParser fparser = new RssFeedParser( feed,
									oldfeed, m_getAllUpdFeedList && pres);
							if (m_loadForm != null) {
								if (name.length() == 0) {
									m_loadForm.appendMsg(
											"\nLoading title for name from " +
											feed.getUrl());
								} else {
									m_loadForm.appendMsg("\nLoading feed from " +
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
							if (m_getAllUpdFeedList || m_getAllFeedList) {
								fparser.setGetFeedTitleList(m_getFeedTitleList);
							} else {
								fparser.setGetTitleOnly(m_getTitleOnly);
							}
							/** Get RSS feed */
							try {
								fparser.parseRssFeed( m_getAllUpdFeedList,
										m_maxItemCount );
								feed = fparser.getRssFeed();
								fparser = (RssFeedParser)nullPtr;
								if ((name.length() == 0) &&
									(oldname.length() != 0) && 
									feed.getName().equals(oldname)) {
									if (m_loadForm != null) {
										m_loadForm.appendNote(
												"\nWarning loaded title/name matches existing name, " +
												name);
									}
									if (!m_getAllFeedList && !m_override) {
										CauseException ce = new CauseException(
												"Feed already exists with " +
												"name " + name +
												".  Existing feed not updated.  " +
												"Use override in place to override an existing " +
												"feed with an old feed with the same name.");
										if (m_loadForm != null) {
											m_loadForm.addExc("Error\n" +
													ce.getMessage(), ce);
											//#ifdef DLOGGING
										} else {
											logger.severe(
													"Error\n" +
													ce.getMessage());
											//#endif
										}
										continue;
									}
									oldfeed = ((oldname != null) &&
											(m_oldRssFeeds != null)) ?
										m_oldRssFeeds.get(oldname) :
										(RssItunesFeed)nullPtr;
									pres = true;
								}
								modified = true;
								if (pres && (m_keepUsPwd || m_keepModGroup) &&
										(oldfeed != null)) {
									feed.checkPresRead(m_keepModGroup, oldfeed);
								}
							} catch(Exception ex) {
								if (m_loadForm != null) {
									m_loadForm.recordExcForm(
											"Error\nInternal error loading " +
											"info for feed " +
											feed.getUrl(), ex);
								}
							}
						} else if ((name.length() == 0) && (!m_getFeedTitleList
										&& !m_getTitleOnly)) {
							if (m_loadForm != null) {
								m_loadForm.appendNote(
										"Error\nSkipping feed with no name url=" +
										feed.getUrl() +
										"\nUse get missing titles from feed.");
							//#ifdef DLOGGING
							} else {
								logger.info(
										"Error\nSkipping feed with no name url=" +
										feed.getUrl());
							//#endif
							}
						}
					} finally {
						vfeeds.addElement(modified ? feed : origFeed);
						apres[presIndex++] = pres;
						if (modified || !pres) {
							m_rssFeeds.put(feed);
						}
						if (m_loadForm != null) {
							m_loadForm.appendMsg("ok\n");
						//#ifdef DLOGGING
						} else {
							logger.info("ok");
						//#endif
						}
					}
				}
				m_feeds = MiscUtil.getVecrFeed(vfeeds);
				if (len == presIndex) {
					m_pres = apres;
				} else {
					m_pres= new boolean[presIndex];
					System.arraycopy(apres, 0, m_pres, 0, presIndex);
				}
			}
			m_urlHandler = (URLHandler)nullPtr;
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
			//#ifndef DSMALLMEM
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
			m_oldRssFeeds = (RssFeedStore)nullPtr;
			m_oldRssNames = (RssFeedStore)nullPtr;
			m_feedNames = (Vector)nullPtr;
			if (m_rssFeeds != null) {
				m_rssFeeds.clearFeedInfo();
			}
        }
    }  
    
    
    /** Get feeds from selected url */
    public RssItunesFeed[] parseFeeds() throws IOException, Exception {
        
		try {
			m_urlHandler.handleOpen(m_url, m_username, m_password, false, false,
					null, "",
					"Error while parsing import data ",
					"Out of memory error while parsing import data ",
					"Internal error while parsing import data ");
			if (m_urlHandler.m_needRedirect) {
				m_urlHandler.m_needRedirect = false;
				m_feeds = parseHeaderRedirect(m_urlHandler.m_location);
				return m_feeds;
			}

			//#ifndef DSMALLMEM
			//#ifdef DLOGGING
			if (fineLoggable) {logger.fine("m_redirectHtml=" + m_redirectHtml);}
			//#endif
			// If we find HTML, usually it is redirection
			if (m_redirectHtml && HTMLParser.isHtml(m_urlHandler.m_contentType)) {
				return parseHTMLRedirect();
			} else {
			//#endif
				return parseFeeds(m_urlHandler.m_inputStream);
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
			m_urlHandler.handleClose();
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
					m_urlHandler.m_redirectUrl + " to 2nd redirect " + newUrl);
			throw new IOException("Error 2nd header redirect url " +
					m_urlHandler.m_redirectUrl + " to 2nd redirect " + newUrl);
		}
		m_redirect = true;
		m_urlHandler.m_redirectUrl = m_url;
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
		m_url = m_urlHandler.parseHTMLRedirect(m_url, m_urlHandler.m_inputStream);
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

    public void setGetTitleOnly(boolean getTitleOnly) {
        this.m_getTitleOnly = getTitleOnly;
    }

    public void setGetAllFeedList(boolean getAllFeedList) {
        this.m_getAllFeedList = getAllFeedList;
    }

    public boolean isGetAllFeedList() {
        return (m_getAllFeedList);
    }

    public void setGetAllUpdFeedList(boolean getAllUpdFeedList) {
        this.m_getAllUpdFeedList = getAllUpdFeedList;
    }

    public boolean isGetAllUpdFeedList() {
        return (m_getAllUpdFeedList);
    }

    public void setKeepModGroup(boolean keepModGroup) {
        this.m_keepModGroup = keepModGroup;
    }

    public boolean isKeepModGroup() {
        return (m_keepModGroup);
    }

    public void setKeepUsPwd(boolean keepUsPwd) {
        this.m_keepUsPwd = keepUsPwd;
    }

    public boolean isKeepUsPwd() {
        return (m_keepUsPwd);
    }

    public void setOverride(boolean override) {
        this.m_override = override;
    }

    public boolean isOverride() {
        return (m_override);
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
