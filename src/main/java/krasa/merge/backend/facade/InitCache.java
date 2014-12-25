package krasa.merge.backend.facade;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitCache {

	@Autowired
	Facade facade;

	// init hsqldb and stuff
	@PostConstruct
	public void init() {
		facade.getProfiles();
	}
}
