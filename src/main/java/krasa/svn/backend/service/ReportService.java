package krasa.svn.backend.service;

import java.io.*;
import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.svn.backend.connection.SVNConnector;
import krasa.svn.backend.dao.SvnFolderDAO;
import krasa.svn.backend.domain.*;
import krasa.svn.backend.dto.ReportResult;

import org.apache.wicket.util.io.IOUtils;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
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

	@Value("${rns.directory}")
	private String rnsDirectory;
	@Value("${rns.command}")
	private String rnsCommand;
	@Value("${SvnHeadVsLastTag.command}")
	private String svnHeadVsLastTagCommand;
	@Value("${VersionsOnPrgens.command}")
	private String versionsOnPrgensCommand;

	public String runRns(String profileName) {
		ProcessBuilder pb = new ProcessBuilder(rnsCommand, "releases/" + profileName);
		pb.redirectErrorStream(true);
		pb.directory(new File(rnsDirectory));
		return execute(pb);
	}

	public String runVersionsOnPrgens() {
		ProcessBuilder pb = new ProcessBuilder(versionsOnPrgensCommand);
		pb.redirectErrorStream(true);
		pb.directory(new File(rnsDirectory));
		return execute(pb);

	}

	public String runSvnHeadVsLastTag(String profileName) {
		ProcessBuilder pb = new ProcessBuilder(svnHeadVsLastTagCommand, "releases/" + profileName);
		pb.redirectErrorStream(true);
		pb.directory(new File(rnsDirectory));
		return execute(pb);
	}

	private String execute(ProcessBuilder pb) {
		try {
			Process p = pb.start();
			String x = IOUtils.toString(p.getInputStream());
			String xs = IOUtils.toString(p.getErrorStream());
			System.err.println(xs);
			p.destroy();
			return x;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

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
