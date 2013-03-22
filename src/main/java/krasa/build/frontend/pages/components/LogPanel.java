package krasa.build.frontend.pages.components;

import krasa.build.backend.dto.Result;
import krasa.build.backend.execution.adapter.ProcessAdapter;
import krasa.build.frontend.components.PocessKillButton;
import krasa.core.frontend.commons.SpanMultiLineLabel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;

public class LogPanel extends Panel {

	private final IModel<ProcessAdapter> model;
	protected final PocessKillButton kill;
	protected Result last;

	public LogPanel(String id, IModel<ProcessAdapter> model) {
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
		add(kill);
	}

	private ProcessAdapter getProgress() {
		return model.getObject();
	}

	private SpanMultiLineLabel createLogData() {
		return new SpanMultiLineLabel("logData", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				last = new Result();
				last = getProgress().getNextLog(last.getLength());
				return last.getText();
			}
		});
	}

	private SpanMultiLineLabel createNextLog() {
		SpanMultiLineLabel spanMultiLineLabel = new SpanMultiLineLabel("nextLog",
				new LoadableDetachableModel<String>() {
					@Override
					protected String load() {
						int length = last.getLength();
						last = getProgress().getNextLog(length);
						String text = last.getText();
						text = text.replaceAll("\n", "\n</br>");
						return text;
					}
				}) {

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
				if (!(getProgress().isAlive())) {
					stop(target);
					target.add(kill);
				}
				target.appendJavaScript("$('#logData').append('<span>' + $('#nextLog').text()   + '</span>');"
						+ "$('#nextLog').remove();" + "$(\"<span id='nextLog'>\").insertAfter($('#logData'));");
			}
		});
		nextLog.setOutputMarkupId(true);
		return nextLog;
	}
}
