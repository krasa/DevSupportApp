package krasa.backend.service;

import krasa.backend.domain.SvnFolder;

/**
 * @author Vojtech Krasa
 */
public interface SvnLoaderProcessor {
    void refreshProjects();

    void refreshAllBranches();

    void refreshProjectBranches(SvnFolder project);

    void refreshBranchesByProjectName(String path);

}
