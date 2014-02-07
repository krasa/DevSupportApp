package krasa.release.frontend;

import java.io.File;
import java.io.IOException;

import krasa.core.frontend.commons.LabelPanel;
import krasa.core.frontend.pages.BasePage;
import krasa.release.domain.TokenizationJob;
import krasa.release.service.TokenizationFileUtils;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

public class FileSystemLogPage extends BasePage {

	public static final String NAME = "name";
	private File logFile;

	public FileSystemLogPage(final PageParameters parameters) {
		super(parameters);
		logFile = TokenizationFileUtils.getLogFileByName(parameters.get(NAME).toString());
		add(new AjaxLazyLoadPanel("logPanel") {
			@Override
			public Component getLazyLoadComponent(String markupId) {
				LoadableDetachableModel<String> labelModel = new LoadableDetachableModel<String>() {

					@Override
					protected String load() {
						try {
							if (!logFile.exists()) {
								return "File does not exists";
							}
							return FileUtils.readFileToString(logFile);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}

					}
				};
				return new LabelPanel<String>(markupId, labelModel) {

					@Override
					protected Component getComponent(String id, IModel<String> labelModel) {
						return new MultiLineLabel(id, labelModel) {
							@Override
							public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
								CharSequence body = Strings.toMultilineMarkup(getDefaultModelObjectAsString().replace(
										"\t", "&nbsp;&nbsp;&nbsp;&nbsp;"));
								replaceComponentTagBody(markupStream, openTag, body);
							}
						};
					}
				};
			}
		});
	}

	public static PageParameters params(TokenizationJob modelObject) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(NAME, modelObject.getLogName());
		return pageParameters;
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new TokenizationLeftPanel(id);
	}

}
