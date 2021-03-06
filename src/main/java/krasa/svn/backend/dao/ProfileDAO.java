package krasa.svn.backend.dao;

import java.util.List;

import krasa.core.backend.dao.AbstractDAO;
import krasa.svn.backend.domain.Profile;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Vojtech Krasa
 */
@Repository
public class ProfileDAO extends AbstractDAO<Profile> {
	public ProfileDAO() {
		super(Profile.class);
	}

	@Override
	protected Class<Profile> getEntityClass() {
		return Profile.class;
	}

	public List<Profile> findAllByType(Profile.Type fromSvn) {
		Query query = getSession().createQuery("from " + getEntityName() + " s where s.type = :type  order by s.name");
		query.setParameter("type", fromSvn);
		return query.list();
	}
}
