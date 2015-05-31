package krasa.core.frontend;

import javax.servlet.*;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.ws.javax.JavaxWebSocketFilter;
import org.springframework.boot.context.embedded.*;
import org.springframework.context.annotation.*;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

/**
 * This class is the replacement of the web.xml. It registers the wicket filter in the spring aware configuration style.
 *
 * @author Stefan Kloe
 *
 */
@Configuration
public class WebInitializer implements ServletContextInitializer {

	public static final String WICKET_WEBSOCKET = "wicket.websocket";

	@Override
	public void onStartup(ServletContext sc) throws ServletException {
		FilterRegistration filter = sc.addFilter(WICKET_WEBSOCKET, JavaxWebSocketFilter.class);
		filter.setInitParameter("applicationClassName", WicketApplication.class.getCanonicalName());
		filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
		filter.addMappingForUrlPatterns(null, false, "/*");

		sc.getSessionCookieConfig().setMaxAge(60 * 60 * 10);
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		return new ServletRegistrationBean(new HystrixMetricsStreamServlet(), "/hystrix.stream/*");
	}
}