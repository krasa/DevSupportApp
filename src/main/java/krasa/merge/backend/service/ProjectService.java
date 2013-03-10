package krasa.merge.backend.service;

import java.util.List;

import krasa.merge.backend.domain.SvnFolder;

import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
public interface ProjectService {

	void replaceBranches(SvnFolder project, List<SVNDirEntry> branches);
}
