package krasa.build.backend.dao;

import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.core.backend.dao.CommonDAO;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CommonBuildDao extends CommonDAO {

	public Environment getEnvironmentByName(String environment) {
		Query query = query("from " + Environment.class.getSimpleName() + " where name = :name");
		query.setString("name", environment);
		return this.uniqueResult(query, Environment.class);
	}

	public void updateBuildMode(Integer id, String buildMode) {
		Query query = query("update " + BuildableComponent.class.getSimpleName()
				+ " set buildMode = :buildMode where id = :id");
		query.setString("buildMode", buildMode);
		query.setInteger("id", id);
		int i = query.executeUpdate();
		if (i != 1) {
			throw new IllegalStateException("update failed for id=" + id + " buildMode+" + buildMode);
		}
	}
}
