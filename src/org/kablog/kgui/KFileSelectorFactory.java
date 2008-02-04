/* From kablog-j2me. */

// Expand to define DJSR75 define
//#define DNOJSR75
// Expand to define logging define
//#define DNOLOGGING

//#ifdef DJSR75
//@package org.kablog.kgui;
//@
//@import javax.microedition.midlet.MIDlet;
//@
//#ifdef DLOGGING
//@import net.sf.jlogmicro.util.logging.Logger;
//@import net.sf.jlogmicro.util.logging.LogManager;
//@import net.sf.jlogmicro.util.logging.Level;
//#endif
//@
//@public class KFileSelectorFactory {
	//#ifdef DLOGGING
//@    private Logger logger = Logger.getLogger("KFileSelectorImpl");
//@    private boolean fineLoggable = logger.isLoggable(Level.FINE);
//@    private boolean finerLoggable = logger.isLoggable(Level.FINER);
//@    private boolean finestLoggable = logger.isLoggable(Level.FINEST);
	//#endif
//@
	//#ifdef DJSR75
//@	public static KFileSelector getInstance(MIDlet midlet, String title,
//@											String defaultDir, String iconDir) {
//@		try {
//@			KFileSelector newInst = new KFileSelectorImpl();
//@			((KFileSelectorImpl)newInst).init(midlet, title, defaultDir,
//@					iconDir);
//@			return (newInst);
//@		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("KFileSelectorImpl");
//@			logger.severe("KFileSelectorFactory getInstance ", t);
			//#endif
//@			/** Error while executing constructor */
//@			System.out.println("KFileSelectorFactory getInstance " + t.getMessage());
//@			t.printStackTrace();
//@			return null;
//@		}
//@
//@	}
//@
//@	public static KFileSelector getInstance() {
//@		
//@		try {
//@			KFileSelector newInst = new KFileSelectorImpl();
//@			((KFileSelectorImpl)newInst).init();
//@			return (newInst);
//@		} catch (Throwable t) {
			//#ifdef DLOGGING
//@			Logger logger = Logger.getLogger("KFileSelectorImpl");
//@			logger.severe("KFileSelectorFactory getInstance ", t);
			//#endif
//@			/** Error while executing constructor */
//@			System.out.println("KFileSelectorFactory getInstance " + t.getMessage());
//@			t.printStackTrace();
//@			return null;
//@		}
//@
//@	}
	//#endif
//@}
//#endif
