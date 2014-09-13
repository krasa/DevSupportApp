package krasa.release.frontend;

import java.io.*;

import krasa.core.frontend.commons.LabelPanel;
import krasa.core.frontend.pages.*;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.facade.Facade;
import krasa.merge.frontend.component.table.FixedModalWindow;
import krasa.release.domain.TokenizationPageModel;
import krasa.release.service.TokenizationService;
import krasa.release.tokenization.TokenizationResult;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class TokenizationPage extends BasePage {

	protected final RequiredTextField<Object> branchNamePatternField;
	private final TextArea<Object> jsonTextArea;
	private final RequiredTextField<Object> fromVersionField;
	@SpringBean
	TokenizationService tokenizationService;
	@SpringBean
	Facade facade;

	private FixedModalWindow modalWindow;

	private final RequiredTextField<Object> toVersionField;
	private final RequiredTextField newPortalDbField;
	private final RequiredTextField newSacDbField;
	private final RequiredTextField newPitDbField;

	private final TokenizationPageModel tokenizationPageModel = new TokenizationPageModel();

	public TokenizationPage() throws IOException {
		final Form<TokenizationPageModel> form = new Form<>("form", new CompoundPropertyModel<>(tokenizationPageModel));
		reset();
		add(modalWindow = new FixedModalWindow("modalWindow"));
		add(form);

		fromVersionField = new RequiredTextField<>("fromVersion");
		form.add(fromVersionField);

		toVersionField = new RequiredTextField<>("toVersion");
		toVersionField.add(new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				tokenizationPageModel.updateFields();
				modelChanged();
				target.add(newPortalDbField);
				target.add(newSacDbField);
				target.add(newPitDbField);
				target.add(branchNamePatternField);
			}
		});
		form.add(toVersionField);

		newPortalDbField = new RequiredTextField<>("newPortalDb");
		form.add(newPortalDbField);

		newSacDbField = new RequiredTextField<>("newSacDb");
		form.add(newSacDbField);

		newPitDbField = new RequiredTextField<>("newPitDb");
		form.add(newPitDbField);

		branchNamePatternField = new RequiredTextField<>("branchNamePattern");
		form.add(branchNamePatternField);

		jsonTextArea = new TextArea<>("json");
		form.add(jsonTextArea);
		form.add(resetButton());
		form.add(generateJsonButton());
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

					private TokenizationResult result;

					@Override
					public Component getLazyLoadComponent(String markupId) {
						if (result == null) {
							setAsDefault();
							result = tokenizationService.tokenizeSynchronously(
									tokenizationPageModel.getBranchNamePattern(), tokenizationPageModel.getJson());
						}
						LoadableDetachableModel<String> labelModel = new LoadableDetachableModel<String>() {

							@Override
							protected String load() {
								try {
									if (!result.getLogFile().exists()) {
										return "File does not exists";
									}
									return FileUtils.readFileToString(result.getLogFile());
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
				setAsDefault();
				File logFile = tokenizationService.tokenizeAsync(
						tokenizationPageModel.getBranchNamePattern().toUpperCase(), tokenizationPageModel.getJson());
				final String tokenize = logFile.getName();
				final PageParameters parameters = FileSystemLogPage.getTokenizationPageParameters(tokenize);
				setResponsePage(FileSystemLogPage.class, parameters);
			}
		};
	}

	protected AjaxButton resetButton() {
		final AjaxButton components = new AjaxButton("reset") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				reset();
				target.add(TokenizationPage.this);
			}
		};
		components.setDefaultFormProcessing(false);
		return components;
	}

	protected AjaxButton generateJsonButton() {
		final AjaxButton components = new AjaxButton("generateJson") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				tokenizationPageModel.regenerateJson();
				jsonTextArea.modelChanged();
				target.add(jsonTextArea);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}
		};
		return components;
	}

	private void setAsDefault() {
		Profile currentProfile = facade.getCurrentProfile();
		currentProfile.setTokenizationPageModel(tokenizationPageModel);
		facade.updateProfile(currentProfile);
	}

	private void reset() {
		TokenizationPageModel model = facade.getCurrentProfile().getTokenizationPageModel();
		if (model != null) {
			tokenizationPageModel.importFrom(model);
		}
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new TokenizationLeftPanel(id);
	}
}
