package krasa.core.frontend;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import krasa.core.frontend.web.WicketWebInitializer;

/**
 * The main starter configuration class which will be called by spring. The class is configured in
 * META-INF/spring.factories
 * 
 * @author Marc Giffing
 *
 */
@Configuration
@Import({
		// ClassCandidateScanner.class,
		// WicketBootWebApplicationAutoConfiguration.class,
		WicketWebInitializer.class
})
// @EnableConfigurationProperties({ GeneralSettingsProperties.class })
// @ComponentScan(basePackageClasses = WicketExtensionLocation.class, nameGenerator =
// CustomAnnotationBeanNameGenerator.class)
public class WicketAutoConfiguration {

}
