package krasa.merge.backend.dao;

import java.util.List;

import krasa.core.backend.dao.DAO;
import krasa.merge.backend.domain.Profile;

/**
 * @author Vojtech Krasa
 */
public interface ProfileDAO extends DAO<Profile> {

	List<Profile> findAllByType(Profile.Type fromSvn);
}
