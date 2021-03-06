package krasa.core.frontend.web;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import krasa.core.frontend.WicketApplication;
import krasa.core.frontend.web.actuator.WicketAutoConfig;
import krasa.core.frontend.web.actuator.WicketEndpointRepository;
import krasa.core.frontend.web.config.WicketWebInitializerAutoConfig;
import krasa.core.frontend.web.config.WicketWebInitializerConfig;

/**
 * Primary container configuration to configure Wicket requests.
 * 
 * @author Marc Giffing
 */
@Configuration
@Import(value = { WicketWebInitializerAutoConfig.class })
@EnableConfigurationProperties({ WicketWebInitializerProperties.class })
public class WicketWebInitializer implements ServletContextInitializer {

	public static final String WICKET_FILTERNAME = "wicket-filter";
	
	@Autowired
	private ApplicationContext applicationContext; 
	
	@Autowired
	private WicketWebInitializerConfig wicketWebInitializerConfig;

	@Autowired
	private WicketWebInitializerProperties props;
	
	@Autowired
	private WicketEndpointRepository wicketEndpointRepository;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		String[] beanNamesForType = applicationContext.getBeanNamesForType(WicketApplication.class);
		if(beanNamesForType.length != 1){
			throw new IllegalStateException("Could not find exactly one bean for type WicketApplication " + beanNamesForType.length);
		}
		
		FilterRegistration filter = servletContext.addFilter(WICKET_FILTERNAME, wicketWebInitializerConfig.filterClass());

		// Spring configuration
		filter.setInitParameter(WicketFilter.APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
		filter.setInitParameter("applicationBean", beanNamesForType[0]);

		filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, props.getFilterMappingParam());
		filter.addMappingForUrlPatterns(null, false, "/*");

		Map<String, String> initParameters = props.getInitParameters();
		for (Entry<String, String> initParam : initParameters.entrySet()) {
			filter.setInitParameter(initParam.getKey(), initParam.getValue());
		}
		
		
		wicketEndpointRepository.add(new WicketAutoConfig.Builder(this.getClass())
				.withDetail("wicketFilterName", WICKET_FILTERNAME)
				.withDetail("wicketFilterClass", wicketWebInitializerConfig.filterClass())
				.withDetail("properties", props)
				.build());
		
		
	}

}
