package krasa.core.frontend.pages;

import java.io.*;

import krasa.core.frontend.commons.LabelPanel;
import krasa.core.frontend.components.BaseEmptyPanel;
import krasa.merge.backend.dto.MergeJobDto;
import krasa.merge.frontend.component.merge.MergesPanel;
import krasa.release.domain.TokenizationJob;
import krasa.release.frontend.TokenizationLeftPanel;
import krasa.release.service.TokenizationFileUtils;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.*;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;

public class FileSystemLogPage extends BasePage {

	public static final String NAME = "logName";
	public static final String TYPE = "type";
	private final Type type;
	private long lastLength;
	private File logFile;

	public FileSystemLogPage(PageParameters parameters) {
		super(parameters);
		logFile = TokenizationFileUtils.getLogFileByName(parameters.get(NAME).toString());
		type = parameters.get(TYPE).toEnum(Type.class);
		lastLength = logFile.length();
		queue(new AjaxLazyLoadPanel("logPanel") {

			@Override
			public Component getLazyLoadComponent(String markupId) {
				LoadableDetachableModel<String> labelModel = new LoadableDetachableModel<String>() {

					@Override
					protected String load() {
						try {
							if (!logFile.exists()) {
								return "File does not exists: " + logFile.getAbsolutePath();
							}
							return FileUtils.readFileToString(logFile);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}

					}
				};
				LabelPanel<String> components = new LabelPanel<String>(markupId, labelModel) {

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
				components.add(new AbstractAjaxTimerBehavior(Duration.seconds(1)) {

					@Override
					protected void onTimer(AjaxRequestTarget target) {
						long length = logFile.length();
						if (length > lastLength) {
							target.add(getComponent());
							lastLength = length;
						}
					}
				});
				return components;
			}
		});
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		if (Type.TokenizationJob == type) {
			return new TokenizationLeftPanel(id);
		} else if (Type.MergeJob == type) {
			return new MergesPanel(id);
		} else {
			return new BaseEmptyPanel(id);
		}
	}

	public static PageParameters getTokenizationPageParameters(String tokenize) {
		PageParameters parameters = new PageParameters();
		parameters.add("logName", tokenize);
		parameters.add(TYPE, Type.TokenizationJob);
		return parameters;
	}

	public static PageParameters params(TokenizationJob modelObject) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(NAME, modelObject.getLogName());
		pageParameters.add(TYPE, Type.TokenizationJob);
		return pageParameters;
	}

	public static PageParameters params(MergeJobDto modelObject) {
		PageParameters pageParameters = new PageParameters();
		pageParameters.add(NAME, modelObject.getLogName());
		pageParameters.add(TYPE, Type.MergeJob);
		return pageParameters;
	}

	enum Type {
		TokenizationJob,
		MergeJob
	}
}
