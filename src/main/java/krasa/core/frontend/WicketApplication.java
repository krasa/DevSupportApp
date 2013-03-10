package krasa.core.frontend;

import krasa.build.frontend.pages.BuildPage;
import krasa.merge.frontend.pages.mergeinfo.MergeInfoPage;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.ResourceRegistrationListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Application object for your web application. If you want to run this application without building, run the Start
 * class.
 * 
 * @see krasa.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	private EventBus eventBus;

	private BeanFactory beanFactory;

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<MergeInfoPage> getHomePage() {
		return MergeInfoPage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
		super.init();
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		mountPage("build", BuildPage.class);
		// IResourceSettings settings = getResourceSettings();
		// settings.setResourcePollFrequency(Duration.ONE_SECOND);
		// // settings.getResourceFinders().add(new Path("C:\\Users\\Meo\\Desktop\\SVNMergeInfo\\src\\main\\java"));
		// getResourceSettings().setUseDefaultOnMissingResource(true);

		// settings.addResourceFolder("/opt/SVNMergeInfo/src/main/java/");

		// add your configuration here
		eventBus = new EventBus(this);
		eventBus.addRegistrationListener(new ResourceRegistrationListener() {

			@Override
			public void resourceUnregistered(String uuid) {
				System.out.println("Unregistered " + uuid);
			}

			@Override
			public void resourceRegistered(String uuid, Page page) {
				System.out.println("Registered " + uuid);
			}
		});
	}

	public ApplicationContext getSpringContext() {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
	}

	@Override
	public Session newSession(Request request, Response response) {
		return (Session) getSpringContext().getBean("MySession", request);
	}

	public EventBus getEventBus() {
		return eventBus;
	}
}
