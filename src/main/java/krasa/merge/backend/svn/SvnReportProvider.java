package krasa.merge.backend.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import krasa.merge.backend.domain.SvnFolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
public class SvnReportProvider {
	protected static final Logger log = LoggerFactory.getLogger(SvnReportProvider.class);
	private SVNRepository repository;

	public SvnReportProvider(SVNRepository repository) {
		this.repository = repository;
	}

	public List<SVNLogEntry> getSVNLogEntries(SvnFolder branchesByName) {
		long startRevision = 0;
		long endRevision = -1; // HEAD (the latest) revision
		List<SVNLogEntry> svnLogEntries = new ArrayList<>();
		try {
			Collection logEntries = repository.log(new String[] { branchesByName.getPath() }, null, startRevision,
					endRevision, false, true);
			for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();

				svnLogEntries.add(logEntry);
				log.info("---------------------------------------------");
				log.info("revision: " + logEntry.getRevision());
				log.info("author: " + logEntry.getAuthor());
				log.info("date: " + logEntry.getDate());
				log.info("log message: " + logEntry.getMessage());

				if (logEntry.getChangedPaths().size() > 0) {
					log.info("changed paths:");
					Set changedPathsSet = logEntry.getChangedPaths().keySet();

					for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(
								changedPaths.next());
						log.info(" "
								+ entryPath.getType()
								+ " "
								+ entryPath.getPath()
								+ ((entryPath.getCopyPath() != null) ? " (from " + entryPath.getCopyPath()
										+ " revision " + entryPath.getCopyRevision() + ")" : ""));
					}
				}
			}
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return svnLogEntries;
	}
}
