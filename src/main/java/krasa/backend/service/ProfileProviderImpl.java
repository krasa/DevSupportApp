package krasa.backend.service;

import krasa.backend.dao.ProfileDAO;
import krasa.backend.domain.Profile;
import krasa.backend.domain.SvnFolder;
import krasa.frontend.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Vojtech Krasa
 */
@Service
public class ProfileProviderImpl implements ProfileProvider {

    @Autowired
    ProfileDAO profileDAO;

    private Integer id;

    public Profile getFirstProfile() {
        if (id == null) {
            Profile profile = profileDAO.findFirst();
            if (profile == null) {
                profile = new Profile();
                profileDAO.save(profile);
            }
            id = profile.getId();
        }
        return profileDAO.findById(id);
    }

    public Set<String> getSelectedBranches() {
        return getCurrentProfile().getSelectedBranches();
    }

    private Profile getCurrentProfile() {
        return MySession.get().getCurrent();
    }

    public void updateSelectionOfSvnFolder(SvnFolder object, Boolean aBoolean) {
        Profile profile = getCurrentProfile();
        if (aBoolean) {
            profile.getSelectedBranches().add(object.getName());
        } else {
            profile.getSelectedBranches().remove(object.getName());
        }
        save(profile);
    }

    private void save(Profile profile) {
        profileDAO.save(profile);
    }

    public void addSelectedBranch(String objectAsString) {
        Profile profile = getCurrentProfile();
        profile.getSelectedBranches().add(objectAsString);
        save(profile);
    }

}
