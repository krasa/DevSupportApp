package krasa.backend.svn;

import krasa.backend.domain.SvnFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class SvnMergeInfoProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private SVNRepository repository;

    public SvnMergeInfoProvider(SVNRepository repository) {
        this.repository = repository;
    }

    public List<SVNLogEntry> getMerges(SvnFolder from, SvnFolder to) throws SVNException {
        log.debug("finding merges from {} to {}", from.getName(), to.getName());

        final List<SVNLogEntry> svnLogEntries = new ArrayList<SVNLogEntry>();
        SVNDiffClient svnDiffClient = new SVNDiffClient(repository.getAuthenticationManager(), new DefaultSVNOptions());
        svnDiffClient.doGetLogEligibleMergeInfo(getUrl(from), SVNRevision.HEAD, getUrl(to), SVNRevision.HEAD, false, null, new SVNLogEntryHandler(svnLogEntries));
        return svnLogEntries;
    }


    public List<SVNLogEntry> getMerges(SvnFolder from, SvnFolder to, String commonFolder) throws SVNException {

        log.debug("finding merges from {} to {} for folder" + commonFolder, from.getName(), to.getName());

        final List<SVNLogEntry> svnLogEntries = new ArrayList<SVNLogEntry>();
        SVNDiffClient svnDiffClient = new SVNDiffClient(repository.getAuthenticationManager(), new DefaultSVNOptions());
        svnDiffClient.doGetLogEligibleMergeInfo(getUrl(from, commonFolder), SVNRevision.HEAD, getUrl(to, commonFolder), SVNRevision.HEAD, false, null, new SVNLogEntryHandler(svnLogEntries));
        return svnLogEntries;

    }

    private SVNURL getUrl(SvnFolder from) throws SVNException {
        return repository.getLocation().appendPath(from.getPath(), false);
    }

    private SVNURL getUrl(SvnFolder to, String commonFolder) throws SVNException {
        return repository.getLocation().appendPath(to.getPath(), false).appendPath(commonFolder, false);
    }


    protected class SVNLogEntryHandler implements ISVNLogEntryHandler {
        List<SVNLogEntry> svnLogEntries;

        SVNLogEntryHandler(List<SVNLogEntry> svnLogEntries) {
            this.svnLogEntries = svnLogEntries;
        }

        public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
            svnLogEntries.add(logEntry);
        }
    }

}
