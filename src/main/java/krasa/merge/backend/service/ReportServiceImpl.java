package krasa.merge.backend.service;

import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.Branch;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.backend.svn.SVNConnector;
import krasa.merge.backend.svn.SvnFolderProvider;
import krasa.merge.backend.svn.SvnReportProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * @author Vojtech Krasa
 */
@Service
public class ReportServiceImpl implements ReportService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProfileProvider profileProvider;

	@Autowired
	private SvnFolderDAO svnFolderDAO;

	@Autowired
	private SVNConnector svnConnection;

	@Autowired
	private GlobalSettingsProvider globalSettingsProvider;

	public ReportResult getReport() {
		ReportResult reportResult = new ReportResult();
		SvnReportProvider svnReportProvider = new SvnReportProvider(svnConnection.getBaseRepositoryConnection());
		SvnFolderProvider svnFolderProvider = new SvnFolderProvider(svnConnection.getBaseRepositoryConnection());

		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		List<SvnFolder> branchesByNames = svnFolderDAO.findBranchesByNames(selectedBranches);
		for (SvnFolder branch : branchesByNames) {

			List<SVNLogEntry> svnLogEntries = svnReportProvider.getSVNLogEntries(branch);

			List<SVNDirEntry> tags = svnFolderProvider.getTags(branch);

			reportResult.add(branch, svnLogEntries);
			reportResult.addTags(branch, tags);
		}
		return reportResult;

	}
}
