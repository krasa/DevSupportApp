package krasa.merge.backend.service;

import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.Branch;
import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.backend.svn.SvnFolderProvider;
import krasa.merge.backend.svn.SvnFolderProviderImpl;
import krasa.merge.backend.svn.SvnReportProvider;
import krasa.merge.backend.svn.connection.SVNConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
@Service
public class ReportService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private ProfileProvider profileProvider;

	@Autowired
	private SvnFolderDAO svnFolderDAO;

	@Autowired
	private GlobalSettingsProvider globalSettingsProvider;

	public ReportResult getReport(Repository repository) {
		ReportResult reportResult = new ReportResult();
		SVNRepository connect = new SVNConnector().connect(repository);
		SvnReportProvider svnReportProvider = new SvnReportProvider(connect);
		SvnFolderProvider svnFolderProvider = new SvnFolderProviderImpl(repository, connect);

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
