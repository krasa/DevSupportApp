package krasa.build.frontend.components;

import java.lang.reflect.Method;
import java.util.*;

import krasa.build.backend.domain.Environment;
import krasa.build.backend.dto.BuildableComponentDto;
import krasa.build.backend.exception.ProcessAlreadyRunning;
import krasa.build.backend.facade.*;
import krasa.build.frontend.pages.BuildLogPage;
import krasa.core.frontend.StaticImage;
import krasa.core.frontend.commons.*;
import krasa.core.frontend.commons.table.*;
import krasa.core.frontend.components.*;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.*;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.*;
import org.apache.wicket.markup.repeater.*;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.*;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.*;

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
	MyAjaxFallbackDefaultDataTable table;

	public BuildComponentsTablePanel(String id, IModel<Environment> environmentIModel) {
		super(id);
		environmentId = environmentIModel.getObject().getId();
		add(feedback = new MyFeedbackPanel("feedback"));
		add(createTable());
		add(new BaseEmptyPanel(MODAL));
		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				if (message instanceof ComponentChangedEvent) {
					ComponentChangedEvent changedEvent = (ComponentChangedEvent) message;
					if (changedEvent.getBuildableComponentDto().getEnvironmentId().equals(environmentId)) {
						log.info("ComponentChangedEvent " + changedEvent.getBuildableComponentDto());
						changedEvent.setTarget(handler);
						send(table, Broadcast.DEPTH, message);
					}
				}
			}
		});
	}

	private AjaxFallbackDefaultDataTable<BuildableComponentDto, String> createTable() {
		table = new MyAjaxFallbackDefaultDataTable();
		return table;
	}

	private void refreshRow(AjaxRequestTarget target, BuildableComponentDto buildableComponentDto) {
		ComponentChangedEvent payload = new ComponentChangedEvent(buildableComponentDto);
		payload.setTarget(target);
		send(table, Broadcast.DEPTH, payload);
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
		ArrayList<IColumn<BuildableComponentDto, String>> columns = new ArrayList<>();
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
						buildFacade.saveBuildMode(defaultModelObject.getComponentId(),
								defaultModelObject.getBuildMode());
					}
				});
			}
		});
		columns.add(new DateColumn<BuildableComponentDto>(new Model<>("build start"), "buildStartTime",
				"buildStartTime"));
		columns.add(new DateColumn<BuildableComponentDto>(new Model<>("total time"), "totalTime", "totalTime", "mm:ss"));
		columns.add(buildColumn());
		columns.add(logColumn());
		columns.add(new StyledLabelColumn(new Model<>(""), "status"));
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
						LabeledBookmarkablePageLink link = new LabeledBookmarkablePageLink(id, BuildLogPage.class,
								BuildLogPage.params(rowModel.getObject()), labelModel) {

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
					buildFacade.buildComponent(model.getObject());
					target.add(table);
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
				buildFacade.deleteComponentById(model.getObject().getComponentId());
				sendDeletedRowEvent(model, target);
			}
		};
	}

	private void sendDeletedRowEvent(IModel<BuildableComponentDto> model, AjaxRequestTarget target) {
		BuildableComponentDto buildableComponentDto = new BuildableComponentDto();
		buildableComponentDto.setComponentId(model.getObject().getComponentId());
		ComponentDeletedEvent componentDeletedEvent = new ComponentDeletedEvent(buildableComponentDto);
		componentDeletedEvent.setTarget(target);
		send(table, Broadcast.DEPTH, componentDeletedEvent);
	}

	public class MyAjaxFallbackDefaultDataTable extends
			krasa.core.frontend.commons.table.MyAjaxFallbackDefaultDataTable<BuildableComponentDto, String> {

		private DataGridView<BuildableComponentDto> dataGridView;

		public MyAjaxFallbackDefaultDataTable() {
			super("components", BuildComponentsTablePanel.this.getColumns(), BuildComponentsTablePanel.this.getModel(),
					100);
		}

		@Override
		protected DataGridView<BuildableComponentDto> newDataGridView(String id,
				List<? extends IColumn<BuildableComponentDto, String>> iColumns,
				IDataProvider<BuildableComponentDto> dataProvider) {
			dataGridView = super.newDataGridView(id, iColumns, dataProvider);
			return dataGridView;
		}

		@Override
		protected Item<BuildableComponentDto> newRowItem(String id, int index, IModel<BuildableComponentDto> model) {
			Item<BuildableComponentDto> item = new Item<BuildableComponentDto>(id, index, model) {

				@Override
				public void onEvent(IEvent<?> event) {
					super.onEvent(event);
					if (event.getPayload() instanceof ComponentChangedEvent) {
						ComponentChangedEvent payload = (ComponentChangedEvent) event.getPayload();
						BuildableComponentDto buildableComponentDto = payload.getBuildableComponentDto();
						if (buildableComponentDto.getComponentId().equals(getModelObject().getComponentId())) {
							setModelObject(payload.getBuildableComponentDto());
							payload.getTarget().add(this);
							event.stop();
						}
					} else if (event.getPayload() instanceof ComponentDeletedEvent) {
						ComponentDeletedEvent payload = (ComponentDeletedEvent) event.getPayload();
						BuildableComponentDto buildableComponentDto = payload.getBuildableComponentDto();
						AjaxRequestTarget target = payload.getTarget();
						if (buildableComponentDto.getComponentId().equals(getModelObject().getComponentId())) {
							this.setVisible(false);
							target.add(this);
							event.stop();
						}
					}
				}
			};
			item.setMarkupId(ROW_ID_PREFIX + model.getObject().getComponentId());
			item.setOutputMarkupId(true);
			if (item.getModelObject().getIndex() >= 0) {
				item.add(new AttributeAppender("class", Model.of("highlightIndex" + item.getModelObject().getIndex()),
						" "));
			}
			return item;
		}

		public void addItem(AjaxRequestTarget target, BuildableComponentDto buildableComponentDto) {
			try {
				Method method = RefreshingView.class.getDeclaredMethod("newItemFactory");
				method.setAccessible(true);

				IItemFactory factory = (IItemFactory) method.invoke(dataGridView);

				Item item = factory.newItem(buildableComponentDto.getComponentId(), new Model(buildableComponentDto));
				dataGridView.add(item);
				target.prependJavaScript(String.format("var item=document.createElement('%s');item.id='%s';"
						+ "Wicket.$('%s').getElementsByTagName(\"tbody\")[0].appendChild(item);", "tr",
						item.getMarkupId(), getMarkupId()));
				target.add(item);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

}
