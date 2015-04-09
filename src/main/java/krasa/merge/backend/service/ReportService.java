package krasa.merge.backend.service;

import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.*;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.backend.svn.*;
import krasa.merge.backend.svn.connection.SVNConnector;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.*;
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
		SvnFolderProvider svnFolderProvider = new SvnFolderProvider(repository, connect);

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
