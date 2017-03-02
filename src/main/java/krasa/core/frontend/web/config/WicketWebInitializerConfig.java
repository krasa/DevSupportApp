package krasa.core.frontend.web.config;

import javax.servlet.Filter;


/**
 * Dynamic configuration support which will be used in {@link WicketWebInitializer}.
 * 
 * The configuration {@link WicketWebInitializerAutoConfig} is responsible to detect which {@link WicketWebInitializerConfig}
 * should be configured.
 * 
 * @author Marc Giffing
 */
public interface WicketWebInitializerConfig {
	
	/**
	 * @return a filter class which will be configured in the {@link WicketWebInitializer}
	 */
	public Class<? extends Filter> filterClass();
	
}
