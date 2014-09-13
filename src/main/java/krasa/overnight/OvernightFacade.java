package krasa.overnight;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.stereotype.Service;

@Service
public class OvernightFacade {

	@SpringBean
	OvernightDao overnightDao;
}
