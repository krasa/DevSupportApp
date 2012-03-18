package krasa.frontend.pages.config;

import krasa.backend.domain.Profile;
import krasa.backend.facade.Facade;
import krasa.backend.service.SvnLoaderProcessor;
import krasa.frontend.MySession;
import krasa.frontend.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.util.logging.Logger;

/**
 * @author Vojtech Krasa
 */
public class ConfigurationPage extends BasePage {
    private final static Logger logger = Logger.getLogger(ProfileEditPanel.class.getName());

    @SpringBean
    private Facade facade;
    @SpringBean
    private SvnLoaderProcessor svnLoaderProcessor;
    protected IModel<Profile> model;

    public static PageParameters createPageParameters(Profile profile) {
        PageParameters parameters = new PageParameters();
        parameters.add("id", profile.getId());
        return parameters;
    }

    public ConfigurationPage() {
        model = new LoadableDetachableModel<Profile>() {
            @Override
            protected Profile load() {
                return facade.getProfileByIdOrDefault(MySession.get().getCurrentProfileId());
            }
        };
        initComponents(model);
    }

    public ConfigurationPage(PageParameters parameters) {
        super(parameters);
        model = new LoadableDetachableModel<Profile>() {
            @Override
            protected Profile load() {
                StringValue id = getPageParameters().get("id");
                Integer integer = id.toInteger();
                return facade.getProfileByIdOrDefault(integer);
            }
        };
        initComponents(model);

    }

    private MarkupContainer initComponents(final IModel<Profile> configModel) {
        final Form<Profile> form = new Form<Profile>("form");
        form.add(new Button("newProfile") {
            @Override
            public void onSubmit() {
                Profile newProfile = facade.createNewProfile();
                MySession.get().setCurrentProfile(newProfile.getId());
                PageParameters parameters = createPageParameters(newProfile);
                setResponsePage(ConfigurationPage.class, parameters);
            }
        });
        form.add(new Button("copyProfile") {
            @Override
            public void onSubmit() {
                Profile newProfile = facade.copyProfile(configModel.getObject());
                MySession.get().setCurrentProfile(newProfile.getId());
                PageParameters parameters = createPageParameters(newProfile);
                setResponsePage(ConfigurationPage.class, parameters);
            }
        });
        form.add(new IndicatingAjaxButton("refreshProjects") {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
                svnLoaderProcessor.refreshProjects();
                ajaxRequestTarget.add(form);
                info("Processing");
//                StatusLabel status1 = new StatusLabel("status", svnRefreshCallbackIModel);
//                status.replaceWith(status1);
//                status = status1;
            }

            @Override
            protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
                error("Error");
            }
        });
        form.add(new IndicatingAjaxButton("refreshProjectsBraches") {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
                svnLoaderProcessor.refreshAllBranches();
                ajaxRequestTarget.add(form);
                info("Processing");
            }

            @Override
            protected void onError(AjaxRequestTarget ajaxRequestTarget, Form<?> components) {
                error("Error");
            }
        });
//        form.add(new TagItTextField<String>("tagit", Model.of("")) {
//
//                @Override
//                protected Iterable<String> getChoices(String input) {
//                    return facade.getSelectedBranchesName(input);
//                }
//        });
        setOutputMarkupPlaceholderTag(true);

        return add(form);
    }

    @Override
    protected Component newCurrentPanel(String id) {
        return new ProfileEditPanel(id, model);
    }

    @Override
    protected Component newLeftColumnPanel(String id) {
        return new ProfileListPanel(id);
    }
}
