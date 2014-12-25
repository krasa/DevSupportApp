package krasa.core.frontend;

import java.io.File;

import krasa.build.frontend.pages.*;
import krasa.intellij.IntelliJEnterprisePluginRepositoryPage;
import krasa.merge.frontend.pages.mergeinfo.MergeInfoPage;

import org.apache.wicket.*;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.core.util.file.WebApplicationPath;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.*;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.resource.FileResourceStream;
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

	public static final String INTELLIJ_PLUGIN_REPO_RESOURCES = "intellijPluginRepoResources";
	public static final String INTELLIJ_PLUGIN_REPO_PLUGINS = "intellijPluginRepo/plugins";
	private Folder uploadFolder = null;
	private BeanFactory beanFactory;
	private File pluginsFolder;

	public static WicketApplication getWicketApplication() {
		return ((WicketApplication) get());
	}

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
		getResourceSettings().getPropertiesFactory().load(this.getClass(),
				this.getClass().getCanonicalName().replace(".", "/"));
		getResourceSettings().getPropertiesFactory().load(krasa.StartVojtitko.class, "org/apache/wicket/Application_cs");
		uploadFolder = new Folder("intellijPluginRepo"); // Ensure folder exists
		uploadFolder.mkdirs();

		getApplicationSettings().setUploadProgressUpdatesEnabled(true);
		getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		mountPage("build", BuildPage.class);
		mountPage("buildLog", LogPage.class);
		mountPage("IntelliJPluginRepository", IntelliJEnterprisePluginRepositoryPage.class);

		pluginsFolder = new File(INTELLIJ_PLUGIN_REPO_PLUGINS);
		pluginsFolder.mkdir();

		getSharedResources().add("intellijPluginRepoResources",
				new FolderContentResource(pluginsFolder.getAbsoluteFile()));

		mountResource("intellijPlugin", new SharedResourceReference(WicketApplication.INTELLIJ_PLUGIN_REPO_RESOURCES));

		getResourceSettings().getResourceFinders().add(
				new WebApplicationPath(getServletContext(), "intellijPluginRepoResources"));

		// add your configuration here
		getComponentInstantiationListeners().add(new IComponentInstantiationListener() {

			public void onInstantiation(Component component) {
				if (component instanceof IComponentResolver) {
					return;
				} else if (component instanceof DataTable.Caption) {
					return;
				} else if (component instanceof Form)
					component.setOutputMarkupId(true);
				else if (component instanceof FormComponent)
					component.setOutputMarkupId(true);
				else if (component instanceof WebMarkupContainer)
					return;
				component.setOutputMarkupId(true);
			}
		});
	}

	static class FolderContentResource implements IResource {

		private final File rootFolder;

		public FolderContentResource(File rootFolder) {
			this.rootFolder = rootFolder;
		}

		public void respond(Attributes attributes) {
			PageParameters parameters = attributes.getParameters();
			String fileName = parameters.get(0).toString();
			if (fileName == null) {
				throw new IllegalArgumentException("no file name");
			}
			File file = new File(rootFolder, fileName);
			FileResourceStream fileResourceStream = new FileResourceStream(file);
			ResourceStreamResource resource = new ResourceStreamResource(fileResourceStream);
			resource.respond(attributes);
		}
	}

	public ApplicationContext getSpringContext() {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new MySession(request);
	}

	public Folder getUploadFolder() {
		return uploadFolder;
	}

	public File getPluginsFolder() {
		return pluginsFolder;
	}
}
