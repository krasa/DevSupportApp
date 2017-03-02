package krasa.core.frontend.web.servlet.standard;

import javax.servlet.Filter;

import org.apache.wicket.protocol.http.WicketFilter;

import krasa.core.frontend.web.config.WicketWebInitializerConfig;

/**
 * The {@link StandardWicketWebInitializer} will be configured when no other 
 * {@link WicketWebInitializerConfig} is present. It uses the standard {@link WicketFilter}.
 *  
 * @author Marc Giffing
 */
public class StandardWicketWebInitializer implements WicketWebInitializerConfig {

	@Override
	public Class<? extends Filter> filterClass() {
		return WicketFilter.class;
	}

}
