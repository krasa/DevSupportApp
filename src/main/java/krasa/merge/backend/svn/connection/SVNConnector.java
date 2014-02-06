package krasa.merge.backend.svn.connection;

import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.svn.SvnReportProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author Vojtech Krasa
 */
public class SVNConnector {
	protected static final Logger log = LoggerFactory.getLogger(SvnReportProvider.class);

	public SVNRepository connect(Repository repository) {
		return connect(repository.getUrl());
	}

	public SVNRepository connect(String connectionUrl) {
		try {
			DAVRepositoryFactory.setup();

			// String url = "http://svn.apache.org/repos/asf/";
			String name = "anonymous";
			String password = "anonymous";

			SVNRepository repository = null;
			repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(connectionUrl));
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
			repository.setAuthenticationManager(authManager);

			log.info("Repository Root: " + repository.getRepositoryRoot(true));
			log.info("Repository UUID: " + repository.getRepositoryUUID(true));

			SVNNodeKind nodeKind = repository.checkPath("", -1);
			if (nodeKind == SVNNodeKind.NONE) {
				log.warn("There is no entry at '" + connectionUrl + "'.");
				System.exit(1);
			} else if (nodeKind == SVNNodeKind.FILE) {
				log.warn("The entry at '" + connectionUrl + "' is a file while a directory was expected.");
				System.exit(1);
			}
			return repository;
		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
	}

}
