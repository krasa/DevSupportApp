package krasa.merge.backend.service;

import java.util.List;

import krasa.merge.backend.domain.Branch;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.domain.SvnFolder;

/**
 * @author Vojtech Krasa
 */
public interface ProfileProvider {
	Profile getFirstProfile();

	List<Branch> getSelectedBranches();

	void updateSelectionOfSvnFolder(SvnFolder object, Boolean aBoolean);

	void addSelectedBranch(String objectAsString);

	List<String> getSelectedBranchesNames();

	void addSelectedBranches(List<SvnFolder> branches);
}
