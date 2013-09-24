package krasa.build.backend.domain;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ActiveProfiles("IN_MEMORY")
@ContextConfiguration(locations = { "classpath:spring.xml" })
public class FullTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	protected SessionFactory sf;

	public void flush() {
		sf.getCurrentSession().flush();
		sf.getCurrentSession().clear();
	}
}
