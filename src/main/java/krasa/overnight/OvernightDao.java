package krasa.overnight;

import java.util.*;

import krasa.core.backend.config.OvernightConfig;
import krasa.core.backend.dao.UniversalDao;
import krasa.core.backend.domain.AbstractEntity;
import krasa.overnight.domain.Result;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = OvernightConfig.TX_MANAGER)
@Repository
public class OvernightDao extends UniversalDao {

	public static final int OFFSET = 6;

	@Autowired
	@Qualifier("overnightSessionFactory")
	@Override
	public void setSf(SessionFactory sf) {
		super.setSf(sf);
	}

	public List<Result> getResults(Date from, int maxResults) {
		Query query = getSession().createQuery(
				"from krasa.overnight.domain.Result r where  :dateFrom < r.timeStamp and  :dateTo > timeStamp  and r.result.id != 1");

		query.setMaxResults(maxResults);
		query.setParameter("dateFrom", DateUtils.addHours(from, -4));
		query.setParameter("dateTo", DateUtils.addHours(from, OFFSET));
		List<Result> list = query.list();
		return list;
	}

	@Override
	public <T extends AbstractEntity> List<T> findBy(Class clazz, Object... propertyAndValue) {
		return super.findBy(clazz, propertyAndValue);
	}

	@Override
	public void deleteAll(Class clazz) {
		super.deleteAll(clazz);
	}

	@Override
	public <T extends AbstractEntity> T findById(Class<T> clazz, Integer id) {
		return super.findById(clazz, id);
	}

	@Override
	public <T extends AbstractEntity> List<T> findAll(Class clazz) {
		return super.findAll(clazz);
	}

	@Override
	public <T extends AbstractEntity> T findFirst(Class<T> clazz) {
		return super.findFirst(clazz);
	}

	@Override
	public <T extends AbstractEntity> T findLast(Class<T> clazz) {
		return super.findLast(clazz);
	}

	@Override
	public <T extends AbstractEntity> T save(T object) {
		return super.save(object);
	}

	@Override
	public int count(Class clazz) {
		return super.count(clazz);
	}

	@Override
	public <T extends AbstractEntity> void delete(T object) {
		super.delete(object);
	}

	@Override
	protected Query query(String query) {
		return super.query(query);
	}

	@Override
	protected List list(Query query) {
		return super.list(query);
	}

	@Override
	protected Object uniqueResult(Criteria criteria) {
		return super.uniqueResult(criteria);
	}

	@Override
	public <T extends AbstractEntity> T refresh(T object) {
		return super.refresh(object);
	}
}
