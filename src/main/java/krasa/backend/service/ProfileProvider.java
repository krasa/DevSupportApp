package krasa.backend.service;

import krasa.backend.domain.Profile;
import krasa.backend.domain.SvnFolder;

import java.util.Set;

/**
 * @author Vojtech Krasa
 */
public interface ProfileProvider {
    Profile getFirstProfile();

    Set<String> getSelectedBranches();

    void updateSelectionOfSvnFolder(SvnFolder object, Boolean aBoolean);

    void addSelectedBranch(String objectAsString);
}
