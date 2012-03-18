package krasa.frontend.pages.commons;

import krasa.backend.domain.SvnFolder;
import krasa.frontend.pages.svn.SvnFolderBrowsePage;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Vojtech Krasa
 */
public class ProjectLink extends BookmarkablePageLink<String> {

    public ProjectLink(String name, IModel<SvnFolder> model) {
        super(name, SvnFolderBrowsePage.class);
        setDefaultModel(Model.of(model.getObject().getName()));
    }

    public ProjectLink(String name, String value) {
        super(name, SvnFolderBrowsePage.class);
        setDefaultModel(Model.of(value));
    }

    @Override
    public PageParameters getPageParameters() {
        PageParameters pageParameters = new PageParameters();
        pageParameters.add(SvnFolderBrowsePage.PATH_PARAMETER, getModelObject());
        return pageParameters;
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, getModelObject());

    }
}
