package krasa.release.frontend;

import java.io.File;
import java.io.IOException;

import krasa.core.frontend.commons.LabelPanel;
import krasa.core.frontend.pages.BasePage;
import krasa.merge.frontend.component.table.FixedModalWindow;
import krasa.release.service.TokenizationFileUtils;
import krasa.release.service.TokenizationService;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class TokenizationPage extends BasePage {

	protected final RequiredTextField<Object> branchNamePatternField;
	private final TextArea<Object> jsonTextArea;
	@SpringBean
	TokenizationService tokenizationService;
	private String branchNamePattern;
	private String json;
	private Integer fromVersion = 9999;
	private Integer toVersion;
	private FixedModalWindow modalWindow;

	public TokenizationPage() throws IOException {
		final Form<TokenizationPage> form = new Form<>("form", new CompoundPropertyModel<>(this));
		reset();
		add(modalWindow = new FixedModalWindow("modalWindow"));
		add(form);
		branchNamePatternField = new RequiredTextField<>("branchNamePattern");
		form.add(branchNamePatternField);
		form.add(new RequiredTextField<>("fromVersion"));
		final RequiredTextField<Object> toVersionField = new RequiredTextField<>("toVersion");
		toVersionField.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (branchNamePattern == null || branchNamePattern.startsWith(".*_")) {
					branchNamePattern = ".*_" + toVersion;
				}
				target.add(branchNamePatternField);
			}
		});
		form.add(toVersionField);
		jsonTextArea = new TextArea<>("json");
		form.add(jsonTextArea);
		form.add(resetButton());
		form.add(setAsDefaultButton());
		form.add(executeButton());
		form.add(executeSynchronouslyButton());

	}

	protected AjaxButton executeSynchronouslyButton() {
		return new AjaxButton("executeSynchronously") {
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(getFeedbackPanel());
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				modalWindow.setContent(new AjaxLazyLoadPanel(modalWindow.getContentId()) {

					private File logFile;

					@Override
					public Component getLazyLoadComponent(String markupId) {
						if (logFile == null) {
							logFile = tokenizationService.tokenizeSynchronously(branchNamePattern, fromVersion,
									toVersion, json);
						}
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
								return new MultiLineLabel(id, labelModel);
							}
						};
					}
				});
				modalWindow.show(target);
			}
		};
	}

	protected AjaxButton executeButton() {
		return new AjaxButton("execute") {
			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				super.onError(target, form);
				target.add(getFeedbackPanel());
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				File file = tokenizationService.tokenizeSynchronously(branchNamePattern.toUpperCase(), fromVersion,
						toVersion, json);
				final String tokenize = file.getName();
				final PageParameters parameters = new PageParameters();
				parameters.add("logName", tokenize);
				setResponsePage(FileSystemLogPage.class, parameters);
			}
		};
	}

	protected AjaxButton resetButton() {
		final AjaxButton components = new AjaxButton("reset") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				reset();
				jsonTextArea.modelChanged();
				target.add(jsonTextArea);
			}
		};
		components.setDefaultFormProcessing(false);
		return components;
	}

	protected AjaxButton setAsDefaultButton() {
		final AjaxButton components = new AjaxButton("setAsDefault") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					TokenizationFileUtils.rewriteTemplate(jsonTextArea.getInput());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		components.setDefaultFormProcessing(false);
		return components;
	}

	private void reset() {
		File file = TokenizationFileUtils.getTemplateFile();
		if (file.exists()) {
			try {
				json = FileUtils.readFileToString(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new TokenizationLeftPanel(id);
	}
}
