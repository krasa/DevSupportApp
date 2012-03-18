package krasa.backend.service;

import krasa.backend.domain.SvnFolder;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.List;

/**
 * @author Vojtech Krasa
 */
public interface ProjectService {

    void replaceBranches(SvnFolder project, List<SVNDirEntry> branches);
}
