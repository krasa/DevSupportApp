package krasa.core.frontend.commons;

import krasa.svn.frontend.pages.svn.SvnFolderBrowsePage;

import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Vojtech Krasa
 */
public class ProjectLink extends BookmarkablePageLink<String> {

	private IModel<String> linkParameter;

	public ProjectLink(String name, IModel<String> labelModel, IModel<String> linkParameter) {
		super(name, SvnFolderBrowsePage.class);
		this.linkParameter = linkParameter;
		setDefaultModel(labelModel);
	}

	@Override
	public PageParameters getPageParameters() {
		PageParameters pageParameters = new PageParameters();
		String object = linkParameter.getObject();
		if (object == null) {
			throw new IllegalStateException("parameter is null");
		}
		pageParameters.add(SvnFolderBrowsePage.PATH_PARAMETER, object);
		return pageParameters;
	}

	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		replaceComponentTagBody(markupStream, openTag, getModelObject());

	}
}
