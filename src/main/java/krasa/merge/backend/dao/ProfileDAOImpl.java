package krasa.merge.backend.dao;

import java.util.List;

import krasa.core.backend.dao.AbstractDAO;
import krasa.merge.backend.domain.Profile;

import org.hibernate.Query;
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

	@Override
	public List<Profile> findAllByType(Profile.Type fromSvn) {
		Query query = getSession().createQuery("from " + getEntityName() + " s where s.type = :type  order by s.name");
		query.setParameter("type", fromSvn);
		return query.list();
	}
}
