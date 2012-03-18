package krasa.backend.dao;

import krasa.backend.domain.Profile;
import org.springframework.stereotype.Repository;

/**
 * @author Vojtech Krasa
 */
@Repository
public class ProfileDAOImpl extends AbstractDAO<Profile> implements ProfileDAO {
    public ProfileDAOImpl() {
        super(Profile.class);
    }

    @Override
    protected Class<Profile> getEntityClass() {
        return Profile.class;
    }

}
