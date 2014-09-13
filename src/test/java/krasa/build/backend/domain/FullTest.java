package krasa.build.backend.domain;

import krasa.core.backend.config.MainConfig;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
@ActiveProfiles("IN_MEMORY")
@ContextConfiguration(classes = { krasa.core.backend.config.MainConfig.class })
public abstract class FullTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	@Qualifier("sessionFactory")
	protected SessionFactory sf;

	public void flush() {
		sf.getCurrentSession().flush();
		sf.getCurrentSession().clear();
	}
}
