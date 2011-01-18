//--Need to modify--#preprocess
/*
 * URLHandler.java
 *
 * Copyright (C) 2005-2006 Tommi Laukkanen
 * Contributions from Irving Bunton
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
 * IB 2010-10-12 1.11.5Dev9 Add --Need to modify--#preprocess to modify to become //#preprocess for RIM preprocessor.
 * IB 2010-11-29 1.11.5Dev9 Use compatibility4 version of EncodingUtil and EncodingStreamReader.
 * IB 2010-11-29 1.11.5Dev9 Allow using fixup (using true) to stop if tagName is empty.
 */

// Expand to define MIDP define
@DMIDPVERS@
// Expand to define DJSR75 define
@DJSR75@
// Expand to define test define
@DTESTDEF@
// Expand to define logging define
@DLOGDEF@
package com.substanceofcode.rssreader.businesslogic.compatibility4;

import java.util.Date;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
//#ifdef DMIDP20
import javax.microedition.io.HttpsConnection;
import javax.microedition.pki.CertificateException;
//#endif
import javax.microedition.io.InputConnection;
//#ifdef DJSR75
import javax.microedition.io.file.FileConnection;
//#endif

import com.substanceofcode.utils.compatibility4.Base64;
import com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo;
import com.substanceofcode.utils.compatibility4.EncodingUtil;
import com.substanceofcode.utils.compatibility4.CauseException;

//#ifdef DLOGGING
import net.sf.jlogmicro.util.logging.Logger;
import net.sf.jlogmicro.util.logging.Level;
//#endif

/**
 * Base class for HTML Handlers.
 *
 * @author Irving Bunton
 */
public class URLHandler {
    
    protected int MAX_REDIRECTS = 3;  // Max times URL is redirected
    protected int m_redirects = 0;  // The URL is redirected
    protected String m_redirectUrl = "";  // The URL is redirected URL
    protected boolean m_needRedirect = false;  // The URL needs to be redirected
    protected boolean m_same = false;  // The conditional get got the same results
    protected String m_location; // The URL location
    protected String m_lastMod = "";  // Last modification
    protected String m_etag = "";  // Etag
    protected InputStream m_inputStream;  // Input stream
    protected OutputStream m_outputStream;  // Output stream
    protected HttpConnection m_hc = null;
    protected InputConnection m_ic = null;
	//#ifdef DJSR75
    protected FileConnection m_fc = null;
	//#endif
    protected String m_contentType = null;  // Content type
    
	//#ifdef DLOGGING
    private Logger logger = Logger.getLogger("compatibility4.URLHandler");
    private boolean fineLoggable = logger.isLoggable(Level.FINE);
    private boolean finerLoggable = logger.isLoggable(Level.FINER);
    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif

    /** Open file or URL.  Give error if there is a problem with the URL/file.*/
    final public void handleOpen(String url, String username, String password,
			boolean writePost, boolean saveBandwidth, String slastModified, String etag)
	throws IOException, Exception {
        
		//#ifdef DLOGGING
		if (finestLoggable) {logger.finest("handleOpen url,saveBandwidth,slastModified,etag=" + url + "," + saveBandwidth + "," + ((slastModified == null) ? "null" : slastModified) +
				"," + etag);}
		//#endif
        try {
			if (url.startsWith("file://")) {
				//#ifdef DJSR75
				/*
				 * Open an FileConnection with the file system 
				 */
				m_fc = (FileConnection) Connector.open( url,
						Connector.READ | (writePost ? Connector.WRITE : 0) );
				m_lastMod = RssFormatParser.stdDate(new Date(m_fc.lastModified()), "GMT");
				if (writePost) {
					if (!m_fc.exists()) {
						m_fc.create();
					}
					m_outputStream = m_fc.openOutputStream();
				} else {
					m_inputStream = m_fc.openInputStream();
				}
				//#else
				/*
				 * Open an InputConnection with the file system.
				 * The trick is knowing the URL.
				 */
				m_ic = (InputConnection) Connector.open( url, Connector.READ );
				m_inputStream = m_ic.openInputStream();
				//#endif
			} else if (url.startsWith("jar://")) {
				// If testing, allow opening of files in the jar.
				m_inputStream = super.getClass().getResourceAsStream( url.substring(6));
				if (m_inputStream == null) {
					throw new IOException("No file found in jar:  " + url);
				}
				int dotPos = url.lastIndexOf('.');
				if (dotPos >= 0) {
					m_contentType = url.substring(dotPos + 1);
				}
			} else {
				/**
				 * Open an HttpConnection or HttpsConnection with the Web server
				 * The default request method is GET
				 */
				if (url.startsWith("https:")) {
					//#ifdef DMIDP20
					 m_hc = (HttpsConnection) Connector.open( url );
					//#else
					 // If not supporting https, allow method to throw the
					 // error.  Some implementations do allow this to work.
					m_hc = (HttpConnection) Connector.open( url );
					//#endif
				} else {
					m_hc = (HttpConnection) Connector.open( url );
				}
				m_hc.setRequestMethod(HttpConnection.GET);
				/** Some web servers requires these properties */
				m_hc.setRequestProperty("User-Agent", 
						"Profile/MIDP-1.0 Configuration/CLDC-1.0");
				m_hc.setRequestProperty("Content-Length", "0");
				m_hc.setRequestProperty("Connection", "close");
				if (saveBandwidth && (slastModified != null) &&
					(slastModified.length() > 0)) {
					m_hc.setRequestProperty("If-Modified-Since",
							slastModified);
				}
				if (saveBandwidth && (etag.length() > 0)) {
					m_hc.setRequestProperty("If-None-Match", etag);
				}

				/** Add credentials if they are defined */
				if( username.length()>0) {
					/** 
					 * Add authentication header in HTTP request. Basic authentication
					 * should be formatted like this:
					 *     Authorization: Basic QWRtaW46Zm9vYmFy
					 */
					String userPass;
					Base64 b64 = new Base64();
					userPass = username + ":" + password;
					userPass = b64.encode(userPass.getBytes());
					m_hc.setRequestProperty("Authorization", "Basic " + userPass);
				}            
				int respCode = m_hc.getResponseCode();
				m_inputStream = m_hc.openInputStream();
				String respMsg = m_hc.getResponseMessage();
				m_lastMod = m_hc.getHeaderField("last-modified");
				if ((m_lastMod == null) || (m_lastMod.length() == 0)) {
					m_lastMod = m_hc.getHeaderField("Last-Modified");
					if (m_lastMod == null) {
						m_lastMod = "";
					}
				}
				m_etag = m_hc.getHeaderField("ETag");
				if ((m_etag == null) || (m_etag.length() == 0)) {
					m_etag = m_hc.getHeaderField("etag");
					if (m_etag == null) {
						m_etag = "";
					}
				}
				m_contentType = m_hc.getHeaderField("content-type");
				m_location = m_hc.getHeaderField("location");
				//#ifdef DLOGGING
				if (fineLoggable) {logger.fine("handleOpen response code=" + respCode);}
				if (fineLoggable) {logger.fine("handleOpen response message=" + respMsg);}
				if (fineLoggable) {logger.fine("handleOpen response m_lastMod=" + m_lastMod);}
				if (fineLoggable) {logger.fine("handleOpen response m_etag=" + m_etag);}
				if (fineLoggable) {logger.fine("handleOpen response m_location=" + m_location);}
				if (finestLoggable) {
					for (int ic = 0; ic < 20; ic++) {
						logger.finest("handleOpen hk=" + ic + "," +
								m_hc.getHeaderFieldKey(ic));
						logger.finest("handleOpen hf=" + ic + "," +
								m_hc.getHeaderField(ic));
					}
				}
				//#endif
				// Don't do HTML redirect as wa may want to process an HTML.
				if ((respCode == HttpConnection.HTTP_NOT_FOUND) ||
					 (respCode == HttpConnection.HTTP_INTERNAL_ERROR) ||
					 (respCode == HttpConnection.HTTP_FORBIDDEN)) {
					throw new IOException("HTTP error " + respCode +
							((respMsg == null) ? "" : " " + respMsg));
				}

				if ((((respCode == HttpConnection.HTTP_MOVED_TEMP) ||
					 (respCode == HttpConnection.HTTP_MOVED_PERM) ||
					 (respCode == HttpConnection.HTTP_TEMP_REDIRECT) ||
					 (respCode == HttpConnection.HTTP_SEE_OTHER)) ||
					 ((respCode == HttpConnection.HTTP_OK) &&
					  respMsg.equals("Moved Temporarily"))) && 
					 (m_location != null)) {
					m_needRedirect = true;
					return;
				}
				if (respCode == HttpConnection.HTTP_NOT_MODIFIED) {
					m_same = true;
				}
			}
			//#ifdef DLOGGING
			if (finestLoggable) {logger.finest("handleOpen m_contentType=" + m_contentType);}
			//#endif
            
        } catch(IllegalArgumentException e) {
			//#ifdef DLOGGING
			logger.severe("handleOpen possible bad url error with " + url,
						  e);
			//#endif
			if ((url != null) && url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
			if (writePost) {
				throw e;
			} else {
				throw new CauseException("Error while parsing RSS data:  " +
										  url, e);
			}
        } catch(ConnectionNotFoundException e) {
			//#ifdef DLOGGING
			logger.severe("handleOpen connection error with " + url, e);
			//#endif
			if ((url != null) && url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
			if (writePost) {
				throw e;
			} else {
				throw new CauseException("Bad URL/File or protocol error while " +
										 "opening: " + url, e);
			}
		//#ifdef DMIDP20
        } catch(CertificateException e) {
			//#ifdef DLOGGING
			logger.severe("handleOpen https security error with " + url, e);
			//#endif
			if ((url != null) && url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
            throw new CauseException("Bad URL/File or protocol error or " +
					"certifacate error while opening: " + url, e);
		//#endif
        } catch(IOException e) {
			throw e;
        } catch(SecurityException e) {
			//#ifdef DLOGGING
			logger.severe("handleOpen security error with " + url, e);
			//#endif
			if ((url != null) && url.startsWith("file://")) {
				System.err.println("Cannot process file.");
			}
			if (writePost) {
				throw e;
			} else {
				throw new CauseException("Security error while oening " +
										 ": " + url, e);
			}
        } catch(Exception e) {
			//#ifdef DLOGGING
			logger.severe("handleOpen internal error with " + url, e);
			//#endif
			if ((url != null) && (url.startsWith("file://"))) {
				System.err.println("Cannot process file.");
			}
            throw new CauseException("Internal error while parsing " +
									 ": ", e);
        } catch(Throwable t) {
			//#ifdef DLOGGING
			logger.severe("handleOpen internal error with " + url, t);
			//#endif
			t.printStackTrace();
            throw new CauseException("Internal error while parsing RSS data " +
								"contact support: ", t);
        }
    }
    
	/** Read HTML and if it has links, redirect and parse the XML. */
	protected String parseHTMLRedirect(String url, InputStream is)
    throws IOException, Exception {
		if (++m_redirects >= MAX_REDIRECTS) {
			//#ifdef DLOGGING
			logger.severe("Error redirect url:  " + url);
			//#endif
			System.out.println("Error redirect url:  " + url);
			throw new IOException("Error url " + m_redirectUrl +
					" to redirect url:  " + url);
		}
		m_redirectUrl = url;
		com.substanceofcode.rssreader.businessentities.RssItunesFeedInfo[] feeds =
				HTMLLinkParser.parseFeeds(new EncodingUtil(is),
									url,
									null,
									null,
									true
									//#ifdef DLOGGING
									,logger,
									fineLoggable,
									finerLoggable,
									finestLoggable
									//#endif
									);
		if ((feeds == null) || (feeds.length == 0)) {
			//#ifdef DLOGGING
			logger.severe("Parsing HTML redirect cannot be " +
						  "processed.");
			//#endif
			System.out.println(
					"Parsing HTML redirect cannot be " +
					"processed.");
			throw new IOException("Parsing HTML redirect cannot be " +
								  "processed.");
		}
		// Use last link as the site may have adds in the beginning.
		return feeds[feeds.length - 1].getUrl();
	}

	final public void handleClose() {
		try {
			if (m_inputStream != null) m_inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (m_hc != null) m_hc.close();
		} catch (IOException e) {
			//#ifdef DLOGGING
			logger.warning("handleOpen possible bad open url error with " +
					m_hc.getURL(), e);
			//#else
			e.printStackTrace();
			//#endif
		}
		//#ifdef DJSR75
		try {
			if (m_fc != null) m_fc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//#endif
	}

    final public void setLastMod(String m_lastMod) {
        this.m_lastMod = m_lastMod;
    }

    final public String getLastMod() {
        return (m_lastMod);
    }

    public void setEtag(String etag) {
        this.m_etag = etag;
    }

    public String getEtag() {
        return (m_etag);
    }

	//#ifdef DJSR75
    public FileConnection getFc() {
        return (m_fc);
    }

    public OutputStream getOutputStream() {
        return (m_outputStream);
    }

	//#endif

}
