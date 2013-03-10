package krasa.merge.backend.service;

import krasa.merge.backend.domain.SvnFolder;

/**
 * @author Vojtech Krasa
 */
public interface SvnLoaderProcessor {
	void refreshProjects(boolean force);

	void refreshAllBranches();

	void refreshProjectBranches(SvnFolder project, boolean force);

	void refreshBranchesByProjectName(String path);

}
