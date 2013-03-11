package krasa;

import java.util.Collection;
import java.util.Iterator;

import krasa.merge.backend.svn.connection.SVNConnector;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
public class DisplayRepositoryTree {
	public static void main(String[] args) throws SVNException {
		SVNRepository repository = new SVNConnector("http://svn.apache.org/repos/asf/").getBaseRepositoryConnection();

		listEntries(repository, "");

		long latestRevision = repository.getLatestRevision();
		System.out.println("Repository latest revision: " + latestRevision);

	}

	public static void listEntries(SVNRepository repository, String path) throws SVNException {
		Collection entries = repository.getDir(path, -1, null, (Collection) null);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			System.out.println(entry.getURL().getPath());
			if (entry.getKind() == SVNNodeKind.DIR) {
				listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
			}
		}
	}

}
