package krasa.frontend;

import krasa.frontend.pages.mergeinfo.MergeInfoPage;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.time.Duration;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 *
 * @see krasa.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
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
        IResourceSettings settings = getResourceSettings();
        settings.setResourcePollFrequency(Duration.ONE_SECOND);
        settings.addResourceFolder("C:\\workspace\\_T-Mobile\\SVNMergeInfo\\src\\main\\java");


        // add your configuration here
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new MySession(request);
    }
}
