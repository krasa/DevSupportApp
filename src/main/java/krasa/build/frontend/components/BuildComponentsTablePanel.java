package krasa.build.frontend.components;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.ProcessAlreadyRunning;
import krasa.build.backend.facade.BuildFacade;
import krasa.build.backend.facade.ComponentChangedEvent;
import krasa.build.backend.facade.ComponentDeletedEvent;
import krasa.build.frontend.pages.LogPage;
import krasa.core.frontend.StaticImage;
import krasa.core.frontend.commons.EditablePanel;
import krasa.core.frontend.commons.LabelPanel;
import krasa.core.frontend.commons.LabeledBookmarkablePageLink;
import krasa.core.frontend.commons.LinkPanel;
import krasa.core.frontend.commons.MyFeedbackPanel;
import krasa.core.frontend.commons.table.ButtonColumn;
import krasa.core.frontend.commons.table.DateColumn;
import krasa.core.frontend.commons.table.DummyModelDataProvider;
import krasa.core.frontend.commons.table.PanelColumn;
import krasa.core.frontend.commons.table.ProjectLinkColumn;
import krasa.core.frontend.commons.table.PropertyEditableColumn;
import krasa.core.frontend.components.BaseEmptyPanel;
import krasa.core.frontend.components.BasePanel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.IItemFactory;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.azeckoski.reflectutils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vojtech Krasa
 */
public class BuildComponentsTablePanel extends BasePanel {

	public static final String MODAL = "MODAL";
	public static final String ROW_ID_PREFIX = "buildCompId";
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private final Integer environmentId;
	protected Form form;
	protected final FeedbackPanel feedback;
	@SpringBean
	private BuildFacade buildFacade;
	private AjaxFallbackDefaultDataTable<BuildableComponentDto, String> table;

	public BuildComponentsTablePanel(String id, final IModel<Environment> environmentIModel) {
		super(id);
		environmentId = environmentIModel.getObject().getId();
		add(feedback = new MyFeedbackPanel("feedback"));
		add(createTable());
		add(new BaseEmptyPanel(MODAL));
	}

	private AjaxFallbackDefaultDataTable<BuildableComponentDto, String> createTable() {
		table = new AjaxFallbackDefaultDataTable<BuildableComponentDto, String>("components", getColumns(), getModel(),
				100) {
			@Override
			protected Item<BuildableComponentDto> newRowItem(String id, int index, IModel<BuildableComponentDto> model) {
				Item<BuildableComponentDto> item = new Item<BuildableComponentDto>(id, index, model) {
					@Override
					public void onEvent(IEvent<?> event) {
						super.onEvent(event);
						if (event.getPayload() instanceof ComponentChangedEvent) {
							ComponentChangedEvent payload = (ComponentChangedEvent) event.getPayload();
							BuildableComponentDto buildableComponentDto = payload.getBuildableComponentDto();
							if (buildableComponentDto.getId().equals(getModelObject().getId())) {
								setModelObject(payload.getBuildableComponentDto());
								payload.getTarget().add(this);
								event.stop();
							}
						} else if (event.getPayload() instanceof ComponentDeletedEvent) {
							ComponentDeletedEvent payload = (ComponentDeletedEvent) event.getPayload();
							BuildableComponentDto buildableComponentDto = payload.getBuildableComponentDto();
							AjaxRequestTarget target = payload.getTarget();
							if (buildableComponentDto.getId().equals(getModelObject().getId())) {
								this.setVisible(false);
								target.add(this);
								event.stop();
							}
						}
					}
				};
				item.setMarkupId(ROW_ID_PREFIX + model.getObject().getId());
				item.setOutputMarkupId(true);
				return item;
			}
		};
		return table;

	}

	public void addItem(AjaxRequestTarget target, BuildableComponentDto buildableComponentDto) {
		try {
			RefreshingView datagrid = (RefreshingView) ReflectUtils.getInstance().getFieldValue(table, "datagrid");
			Method method = RefreshingView.class.getDeclaredMethod("newItemFactory");
			method.setAccessible(true);

			IItemFactory factory = (IItemFactory) method.invoke(datagrid);

			Item item = factory.newItem(buildableComponentDto.getId(), new Model(buildableComponentDto));
			datagrid.add(item);
			target.prependJavaScript(String.format("var item=document.createElement('%s');item.id='%s';"
					+ "Wicket.$('%s').getElementsByTagName(\"tbody\")[0].appendChild(item);", "tr", item.getMarkupId(),
					table.getMarkupId()));
			target.add(item);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Subscribe
	public void refreshRow(AjaxRequestTarget target, ComponentChangedEvent message) {
		if (message.getBuildableComponentDto().getEnvironmentId().equals(environmentId)) {
			message.setTarget(target);
			send(table, Broadcast.DEPTH, message);
		}
	}

	private void refreshRow(AjaxRequestTarget target, BuildableComponentDto buildableComponentDto) {
		refreshRow(target, new ComponentChangedEvent(buildableComponentDto));
	}

	private DummyModelDataProvider<BuildableComponentDto> getModel() {
		return new DummyModelDataProvider<>(new LoadableDetachableModel<List<BuildableComponentDto>>() {
			@Override
			protected List<BuildableComponentDto> load() {
				return BuildableComponentDto.transform(buildFacade.getComponentsByEnvironment(environmentId));
			}
		});
	}

	private List<IColumn<BuildableComponentDto, String>> getColumns() {
		final ArrayList<IColumn<BuildableComponentDto, String>> columns = new ArrayList<>();
		columns.add(new ProjectLinkColumn<BuildableComponentDto, String>(new Model<>("name"), "name", "name"));
		columns.add(new PropertyEditableColumn<BuildableComponentDto, String>(new Model<>("buildMode"), "buildMode",
				"buildMode", 60) {
			@Override
			protected void decoratePanel(final EditablePanel<BuildableComponentDto> panel) {
				super.decoratePanel(panel);
				panel.getTextfield().add(new OnChangeAjaxBehavior() {
					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						BuildableComponentDto defaultModelObject = (BuildableComponentDto) panel.getDefaultModelObject();
						buildFacade.saveBuildMode(defaultModelObject.getId(), defaultModelObject.getBuildMode());
					}
				});
			}
		});
		columns.add(new DateColumn<BuildableComponentDto>(new Model<>("build start"), "buildStartTime",
				"buildStartTime"));
		columns.add(new DateColumn<BuildableComponentDto>(new Model<>("total time"), "totalTime", "totalTime", "mm:ss"));
		columns.add(buildColumn());
		columns.add(logColumn());
		columns.add(new PanelColumn<BuildableComponentDto>(new Model<>(""), "status") {
			@Override
			protected Panel getPanel(String componentId, IModel<BuildableComponentDto> rowModel) {
				return new LabelPanel(componentId, new PropertyModel<>(rowModel, "status")) {
					@Override
					protected Component getComponent(String id, IModel labelModel) {
						Label label = new Label(id, labelModel);
						label.add(new AttributeModifier("id", labelModel));
						return label;
					}
				};
			}
		});
		columns.add(editColumn());
		columns.add(deleteColumn());
		return columns;
	}

	private PanelColumn<BuildableComponentDto> logColumn() {
		return new PanelColumn<BuildableComponentDto>(new Model<>("")) {
			@Override
			protected Panel getPanel(String componentId, IModel<BuildableComponentDto> rowModel) {
				return new LinkPanel<BuildableComponentDto>(componentId, getDisplayModel(), rowModel) {
					@Override
					protected Link getComponent(String id, IModel<String> labelModel,
							final IModel<BuildableComponentDto> rowModel) {
						LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink(id, LogPage.class,
								LogPage.params(rowModel.getObject()), labelModel) {

							@Override
							public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
								replaceComponentTagBody(markupStream, openTag,
										"<img border=\"0\" src=\"/img/5_content_copy.png\" alt=\"Go to log\"/>");
							}

							@Override
							protected void onConfigure() {
								super.onConfigure();
								setVisible(rowModel.getObject().getBuildJobId() != null);
							}
						};
						return link;
					}
				};
			}
		};
	}

	private ButtonColumn<BuildableComponentDto> buildColumn() {
		return new ButtonColumn<BuildableComponentDto>(new Model<>(""), null, StaticImage.BUILD) {
			@Override
			protected void onSubmit(IModel<BuildableComponentDto> model, AjaxRequestTarget target, Form<?> form) {
				try {
					BuildableComponentDto buildableComponentDto = buildFacade.buildComponent(model.getObject());
					refreshRow(target, buildableComponentDto);
				} catch (ProcessAlreadyRunning e) {
					info("already building");
					target.add(feedback);
				}
			}
		};
	}

	private IColumn<BuildableComponentDto, String> editColumn() {
		return new ButtonColumn<BuildableComponentDto>(new Model<>(""), null, StaticImage.EDIT) {
			@Override
			protected void onSubmit(IModel<BuildableComponentDto> model, AjaxRequestTarget target, Form<?> form) {
				final ModalWindow modalWindow = new ModalWindow(MODAL);
				modalWindow.setContent(new ComponentEditPanel(modalWindow.getContentId(), model) {
					@Override
					public void onSubmit(AjaxRequestTarget target, BuildableComponentDto buildableComponentDto) {
						modalWindow.close(target);
						refreshRow(target, buildableComponentDto);
					}
				});
				modalWindow.setAutoSize(true);
				BuildComponentsTablePanel.this.replace(modalWindow);
				target.add(modalWindow);
				modalWindow.show(target);
			}
		};
	}

	private IColumn<BuildableComponentDto, String> deleteColumn() {
		return new ButtonColumn<BuildableComponentDto>(new Model<>(""), null, StaticImage.DELETE) {
			@Override
			protected void onSubmit(IModel<BuildableComponentDto> model, AjaxRequestTarget target, Form<?> form) {
				buildFacade.deleteComponentById(model.getObject().getId());
				sendDeletedRowEvent(model, target);
			}
		};
	}

	private void sendDeletedRowEvent(IModel<BuildableComponentDto> model, AjaxRequestTarget target) {
		BuildableComponentDto buildableComponentDto = new BuildableComponentDto();
		buildableComponentDto.setId(model.getObject().getId());
		ComponentDeletedEvent componentDeletedEvent = new ComponentDeletedEvent(buildableComponentDto);
		componentDeletedEvent.setTarget(target);
		send(table, Broadcast.DEPTH, componentDeletedEvent);
	}
}
