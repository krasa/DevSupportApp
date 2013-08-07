package krasa.build.frontend.components;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.Status;
import krasa.build.backend.dto.Result;
import krasa.core.frontend.commons.SpanMultiLineLabel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;

public class LogPanel extends Panel {

	private final IModel<BuildJob> model;
	protected final PocessKillButton kill;
	protected final PocessKillButton kill2;
	protected Result last;

	public LogPanel(String id, IModel<BuildJob> model) {
		super(id);
		this.model = model;

		if (getProgress() != null) {
			add(createLogData());
			add(createNextLog());
		} else {
			add(new EmptyPanel("logData"));
			add(new EmptyPanel("nextLog"));
		}
		kill = new PocessKillButton("kill", model);
		kill2 = new PocessKillButton("kill2", model);
		add(kill);
		add(kill2);
	}

	private BuildJob getProgress() {
		return model.getObject();
	}

	private SpanMultiLineLabel createLogData() {
		return new SpanMultiLineLabel("logData", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				last = getProgress().getLog();
				return last.getText();
			}
		});
	}

	private SpanMultiLineLabel createNextLog() {
		final LoadableDetachableModel<String> model = new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				int length = last.getLength();
				last = getProgress().getNextLog(length);
				String text = last.getText();
				text = text.replaceAll("\n", "\n</br>");
				return text;
			}
		};
		SpanMultiLineLabel spanMultiLineLabel = new SpanMultiLineLabel("nextLog", model) {

			// This is needed because, wicket created dynamic ids for the "nextLog" component
			@Override
			public String getMarkupId(boolean createIfDoesNotExist) {
				return "nextLog";
			}
		};
		SpanMultiLineLabel nextLog = spanMultiLineLabel;
		nextLog.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)) {

			@Override
			protected void onPostProcessTarget(AjaxRequestTarget target) {

				/*
				 * We are doing the following here: - append the content of "nextLog" to "logData" - remove "nextLog" -
				 * insert "nextLog" after "logData".
				 */

				if (getProgress().getStatus() == Status.PENDING) {
				} else if (!getProgress().isProcessAlive()) {
					stop(target);
					target.add(kill);
					target.add(kill2);
				} else if (!model.getObject().isEmpty()) {
					// @formatter:off
					target.prependJavaScript("window.shouldScroll = $(window).scrollTop() + $(window).height()  >= $(document).height();");
					target.appendJavaScript("$('#logData').append('<span>' + $('#nextLog').text()   + '</span>');"
							+ "$('#nextLog').remove();" + "$(\"<span id='nextLog'>\").insertAfter($('#logData'));"

							+ "if(window.shouldScroll) {window.scroll(0,document.body.scrollHeight);}"
					// @formatter:on

					);
				}
			}
		});
		nextLog.setOutputMarkupId(true);
		return nextLog;
	}
}
