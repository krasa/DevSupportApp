package krasa.overnight;

import krasa.core.backend.domain.AbstractEntity;
import krasa.overnight.domain.Result;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OvernightFacade {

	@Autowired 
	OvernightDao overnightDao;

	public Session getSession() {
		return overnightDao.getSession();
	}

	@Autowired
	@Qualifier("overnightSessionFactory")
	public void setSf(SessionFactory sf) {
		overnightDao.setSf(sf);
	}

	public List<Result> getResults(Date from, int maxResults) {
		return overnightDao.getResults(from, maxResults);
	}

	public <T extends AbstractEntity> List<T> findBy(Class clazz, Object... propertyAndValue) {
		return overnightDao.findBy(clazz, propertyAndValue);
	}

	public void deleteAll(Class clazz) {
		overnightDao.deleteAll(clazz);
	}

	public <T extends AbstractEntity> T  findById(Class<T> clazz, Integer id) {
		return overnightDao.findById(clazz, id);
	}

	public <T extends AbstractEntity> List<T> findAll(Class clazz) {
		return overnightDao.findAll(clazz);
	}

	public <T extends AbstractEntity> T findFirst(Class<T> clazz) {
		return overnightDao.findFirst(clazz);
	}

	public <T extends AbstractEntity> T findLast(Class<T> clazz) {
		return overnightDao.findLast(clazz);
	}

	public <T extends AbstractEntity> T save(T object) {
		return overnightDao.save(object);
	}

	public <T extends AbstractEntity> List<T> findLast(int n, Class<T> clazz) {
		return overnightDao.findLast(n, clazz);
	}

	public int count(Class clazz) {
		return overnightDao.count(clazz);
	}

	public <T extends AbstractEntity> void delete(T object) {
		overnightDao.delete(object);
	}

	public Query query(String query) {
		return overnightDao.query(query);
	}

	public List list(Query query) {
		return overnightDao.list(query);
	}

	public Object uniqueResult(Criteria criteria) {
		return overnightDao.uniqueResult(criteria);
	}

	public <T extends AbstractEntity> T refresh(T object) {
		return overnightDao.refresh(object);
	}
}
