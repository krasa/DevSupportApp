package krasa.backend.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
public interface SVNConnector {
    SVNRepository connect() throws SVNException;

    SVNRepository getConnection();
}
