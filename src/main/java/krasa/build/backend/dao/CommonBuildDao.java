package krasa.build.backend.dao;

import krasa.build.backend.domain.Environment;
import krasa.core.backend.dao.CommonDAO;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CommonBuildDao extends CommonDAO {
	@Override
	protected Class getEntityClass() {
		return null;
	}

	public Environment getEnvironmentByName(String environment) {
		Query query = query("from " + Environment.class.getSimpleName() + " where name = :name");
		query.setString("name", environment);
		return this.uniqueResult(query, Environment.class);
	}

}
