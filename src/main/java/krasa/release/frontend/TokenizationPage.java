package krasa.release.frontend;

import java.io.*;
import java.util.*;

import krasa.core.frontend.StaticImage;
import krasa.core.frontend.commons.LabelPanel;
import krasa.core.frontend.commons.table.*;
import krasa.core.frontend.pages.*;
import krasa.release.domain.TokenizationPageModel;
import krasa.release.service.TokenizationService;
import krasa.release.tokenization.TokenizationResult;
import krasa.svn.backend.domain.*;
import krasa.svn.backend.facade.SvnFacade;
import krasa.svn.frontend.component.BranchAutocompleteFormPanel;
import krasa.svn.frontend.component.table.FixedModalWindow;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vojtech Krasa
 */
public class TokenizationPage extends BasePage {

	@SpringBean
	TokenizationService tokenizationService;
	@SpringBean
	SvnFacade facade;

	private FixedModalWindow modalWindow;

	private TextArea<Object> jsonTextArea;
	private TextField<Object> fromVersionField;
	private TextField<Object> toVersionField;
	private TextField newPortalDbField;
	private TextField newSacDbField;
	private TextField commitMessage;
	private TextField newPitDbField;

	private TokenizationPageModel tokenizationPageModel = new TokenizationPageModel();
	private AjaxFallbackDefaultDataTable<String, String> branchesTable;
	private Form<TokenizationPageModel> form;
	private AutoCompleteTextField<String> branchNamePatternField;

	public TokenizationPage() throws IOException {
		queue(modalWindow = new FixedModalWindow("modalWindow"));
		queue(form = new Form<>("form", new CompoundPropertyModel<>(tokenizationPageModel)));
		form.add(fromVersionField = new RequiredTextField<>("fromVersion"));

		toVersionField = new TextField<>("toVersion");
		toVersionField.add(new OnChangeAjaxBehavior() {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				tokenizationPageModel.updateFields();
				modelChanged();
				newPortalDbField.clearInput();
				newSacDbField.clearInput();
				newPitDbField.clearInput();
				branchNamePatternField.clearInput();
				branchNamePatternField.getModel().setObject(tokenizationPageModel.getBranchNamePatternTemplate());

				target.add(newPortalDbField);
				target.add(newSacDbField);
				target.add(newPitDbField);
				target.add(branchNamePatternField);
			}
		});
		form.add(toVersionField);
		form.add(newPortalDbField = new TextField<>("newPortalDb"));
		form.add(newSacDbField = new TextField<>("newSacDb"));
		form.add(newPitDbField = new TextField<>("newPitDb"));
		form.add(commitMessage = new TextField<>("commitMessage"));
		form.add(jsonTextArea = new TextArea<>("json"));
		form.add(resetButton());
		form.add(generateJsonButton());
		form.add(executeButton());
		form.add(executeSynchronouslyButton());
		form.add(createBranchesTable());
		form.add(branchAutoComplete());
	}

	protected AjaxButton executeSynchronouslyButton() {
		return new AjaxButton("executeSynchronously") {

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
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
							result = tokenizationService.tokenizeSynchronously(tokenizationPageModel);
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

	private AjaxFallbackDefaultDataTable<String, String> createBranchesTable() {
		ArrayList<IColumn<String, String>> iColumns = new ArrayList<IColumn<String, String>>();
		iColumns.add(nameColumn());
		iColumns.add(deleteColumn());
		SortableModelDataProvider<String> dataProvider = new SortableModelDataProvider<String>(
				new AbstractReadOnlyModel<List<String>>() {

					@Override
					public List<String> getObject() {
						return tokenizationPageModel.getBranchesPatterns();
					}
				});
		return branchesTable = new AjaxFallbackDefaultDataTable<>("branchesTable", iColumns, dataProvider,
				Integer.MAX_VALUE);
	}

	private LabelColumn<String> nameColumn() {
		return new LabelColumn<String>(Model.of("name")) {

			@Override
			protected Object getModel(IModel<String> rowModel) {
				return rowModel.getObject();
			}
		};
	}

	private IColumn<String, String> deleteColumn() {
		return new ButtonColumn<String>(new Model<>(""), null, StaticImage.DELETE) {

			@Override
			protected void onSubmit(IModel<String> model, AjaxRequestTarget target, Form<?> form) {
				tokenizationPageModel.getBranchesPatterns().remove(model.getObject());
				{
					target.add(branchesTable);
				}
			}
		};
	}

	private BranchAutocompleteFormPanel branchAutoComplete() {
		BranchAutocompleteFormPanel branchAutocompleteFormPanel = new BranchAutocompleteFormPanel("addBranchPanel") {

			@Override
			protected void deleteAllBranches(AjaxRequestTarget target) {
				tokenizationPageModel.getBranchesPatterns().clear();
			}

			@Override
			protected void addAllMatchingBranches(String fieldValue, AjaxRequestTarget target) {
				List<SvnFolder> branchesByNameLikeAsDisplayable = facade.findBranchesByNameLike(fieldValue);
				for (Displayable displayable : branchesByNameLikeAsDisplayable) {
					tokenizationPageModel.getBranchesPatterns().add(displayable.getDisplayableText());
				}
			}

			@Override
			protected void addBranch(String fieldValue, AjaxRequestTarget target) {
				tokenizationPageModel.getBranchesPatterns().add(fieldValue);
			}

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(branchesTable);
			}
		};
		branchNamePatternField = branchAutocompleteFormPanel.getField();
		return branchAutocompleteFormPanel;
	}

	protected AjaxButton executeButton() {
		AjaxButton button = new AjaxButton("execute") {

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(getFeedbackPanel());
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// setAsDefault();
				File logFile = tokenizationService.tokenizeAsync(tokenizationPageModel);
				String tokenize = logFile.getName();
				PageParameters parameters = FileSystemLogPage.getTokenizationPageParameters(tokenize);
				setResponsePage(FileSystemLogPage.class, parameters);
			}
		};
		// button.setDefaultFormProcessing(false);
		return button;
	}

	protected AjaxButton resetButton() {
		AjaxButton reset = new AjaxButton("reset") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				form.clearInput();
				tokenizationPageModel.importFrom(new TokenizationPageModel());
				target.add(TokenizationPage.this);
			}
		};
		// reset.setDefaultFormProcessing(false);
		return reset;
	}

	protected AjaxButton generateJsonButton() {
		AjaxButton generateJson = new AjaxButton("generateJson") {

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
		// TODO fixes Stackoverflow
		// generateJson.setDefaultFormProcessing(false);
		return generateJson;
	}

	private void setAsDefault() {
		Profile currentProfile = facade.getCurrentProfile();
		facade.updateProfile(currentProfile);
	}

	@Override
	protected Component newLeftColumnPanel(String id) {
		return new TokenizationLeftPanel(id);
	}
}
