package krasa;

import org.junit.Test;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoTest {
    public static void main(String[] args) throws SVNException {
        DAVRepositoryFactory.setup();

        String url = "http://svn.apache.org/repos/asf/";
        String name = "anonymous";
        String password = "anonymous";


        SVNRepository repository = null;
        repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);


        System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
        System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));


        SVNNodeKind nodeKind = repository.checkPath("", -1);
        if (nodeKind == SVNNodeKind.NONE) {
            System.err.println("There is no entry at '" + url + "'.");
            System.exit(1);
        } else if (nodeKind == SVNNodeKind.FILE) {
            System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
            System.exit(1);
        }

        System.err.println(repository.getLocation().getPath());
//        List<SVNDirEntry> svnDirEntries = new BranchesList(repository).inWholeRepository();
//        List<SVNDirEntry> svnDirEntries1 = new SvnFolderProvider(repository).getBranches("stdcxx");
//        SVNDiffClient svnDiffClient = new SVNDiffClient(authManager, new DefaultSVNOptions());
//        SVNDirEntry svnDirEntry = svnDirEntries1.get(0);
//        SVNDirEntry svnDirEntry1 = svnDirEntries1.get(2);
//        svnDiffClient.doGetLogEligibleMergeInfo(svnDirEntry.getURL(), SVNRevision.HEAD, svnDirEntry1.getURL(), SVNRevision.HEAD, true, null, new ISVNLogEntryHandler() {
//            public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
//                System.err.println(logEntry);
//            }
//        });

//        SVNDiffClient svnDiffClient = new SVNDiffClient(authManager, new DefaultSVNOptions());
//        svnDiffClient.doGetLogEligibleMergeInfo();
    }

    @Test
    public void testName() throws Exception {
        String s = "http://svn/sdp/spieeuk/branches/SPIEEUK_1100";
        String s1 = "http://svn/sdp/spieeuk/branches/SPIEEUK_1000";

        DAVRepositoryFactory.setup();

        String url = "http://svn/sdp/";
        String name = "anonymous";
        String password = "anonymous";


        SVNRepository repository = null;
        repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
        repository.setAuthenticationManager(authManager);


        System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
        System.out.println("Repository UUID: " + repository.getRepositoryUUID(true));


        SVNNodeKind nodeKind = repository.checkPath("", -1);
        if (nodeKind == SVNNodeKind.NONE) {
            System.err.println("There is no entry at '" + url + "'.");
            System.exit(1);
        } else if (nodeKind == SVNNodeKind.FILE) {
            System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
            System.exit(1);
        }


        //        List<SVNDirEntry> svnDirEntries = new BranchesList(repository).inWholeRepository();
//                List<SVNDirEntry> svnDirEntries1 = new SvnFolderProvider(repository, new SVNRefreshCallback()).forProject("stdcxx");
        SVNDiffClient svnDiffClient = new SVNDiffClient(authManager, new DefaultSVNOptions());
//                SVNDirEntry svnDirEntry = svnDirEntries1.get(0);
//                SVNDirEntry svnDirEntry1 = svnDirEntries1.get(2);
        svnDiffClient.doGetLogEligibleMergeInfo(SVNURL.parseURIDecoded(s), SVNRevision.HEAD, SVNURL.parseURIDecoded(s1), SVNRevision.HEAD, true, null, new ISVNLogEntryHandler() {
            public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
                System.err.println(logEntry.getRevision());
            }
        });
    }
}
