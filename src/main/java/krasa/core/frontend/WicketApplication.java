package krasa.core.frontend;

import krasa.build.frontend.pages.BuildPage;
import krasa.merge.frontend.pages.mergeinfo.MergeInfoPage;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
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

		// add your configuration here
		eventBus = new EventBus(this);
		getComponentInstantiationListeners().add(new IComponentInstantiationListener() {
			public void onInstantiation(Component component) {
				if (component instanceof Form)
					component.setOutputMarkupId(true);
				if (component instanceof WebMarkupContainer)
					return;
				component.setOutputMarkupId(true);
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
