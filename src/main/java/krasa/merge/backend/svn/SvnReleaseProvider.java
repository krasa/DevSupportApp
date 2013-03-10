package krasa.merge.backend.svn;

import java.util.List;

import krasa.merge.backend.domain.Profile;

/**
 * @author Vojtech Krasa
 */
public interface SvnReleaseProvider {
	List<Profile> getReleases();
}
