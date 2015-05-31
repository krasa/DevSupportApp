package krasa.svn.backend.service;

import java.util.List;

import krasa.core.frontend.MySession;
import krasa.svn.backend.dao.ProfileDAO;
import krasa.svn.backend.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Vojtech Krasa
 */
@Service
public class ProfileProvider {

	@Autowired
	ProfileDAO profileDAO;

	public Profile getFirstProfile() {
		Profile profile = profileDAO.findFirst();
		if (profile == null) {
			profile = new Profile();
			profileDAO.save(profile);
		}
		return profile;
	}

	public List<Branch> getSelectedBranches() {
		return getCurrentProfile().getBranches();
	}

	private Profile getCurrentProfile() {
		return MySession.get().getCurrent();
	}

	public void updateSelectionOfSvnFolder(SvnFolder object, Boolean selected) {
		Profile profile = getCurrentProfile();
		if (selected) {
			profile.addBranch(object.getName());
		} else {
			profile.removeBranch(object.getName());
		}
		save(profile);
	}

	private void save(Profile profile) {
		profileDAO.save(profile);
	}

	public void addSelectedBranch(String branchName) {
		Profile profile = getCurrentProfile();
		profile.addBranch(branchName);
		save(profile);
	}

	public List<String> getSelectedBranchesNames() {
		return getCurrentProfile().getBranchesNames();
	}

	public void addSelectedBranches(List<SvnFolder> branches) {
		Profile profile = getCurrentProfile();
		for (SvnFolder branch : branches) {
			profile.addBranch(branch.getName());
		}
		save(profile);
	}

	public void deleteAllBranchesFromProfile() {
		Profile profile = getCurrentProfile();
		profile.removeAllBranches();
		save(profile);

	}

}
