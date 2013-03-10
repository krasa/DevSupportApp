package krasa.merge.backend.svn;

import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
public interface SVNConnector {
	SVNRepository getBaseRepositoryConnection();

	SVNRepository connect(String connectionUrl);

}
