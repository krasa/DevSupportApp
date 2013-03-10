package krasa.merge.backend.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import krasa.merge.backend.domain.SvnFolder;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
public class SvnReportProvider {
	private SVNRepository repository;

	public SvnReportProvider(SVNRepository repository) {

		this.repository = repository;
	}

	public List<SVNLogEntry> getSVNLogEntries(SvnFolder branchesByName) {
		long startRevision = 0;
		long endRevision = -1; // HEAD (the latest) revision
		List<SVNLogEntry> svnLogEntries = new ArrayList<SVNLogEntry>();
		try {
			Collection logEntries = repository.log(new String[] { branchesByName.getPath() }, null, startRevision,
					endRevision, false, true);
			for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();

				svnLogEntries.add(logEntry);
				System.out.println("---------------------------------------------");
				System.out.println("revision: " + logEntry.getRevision());
				System.out.println("author: " + logEntry.getAuthor());
				System.out.println("date: " + logEntry.getDate());
				System.out.println("log message: " + logEntry.getMessage());

				if (logEntry.getChangedPaths().size() > 0) {
					System.out.println();
					System.out.println("changed paths:");
					Set changedPathsSet = logEntry.getChangedPaths().keySet();

					for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(
								changedPaths.next());
						System.out.println(" "
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
