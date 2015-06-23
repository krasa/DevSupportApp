package krasa.build.frontend.components;

import krasa.build.backend.dto.LogFileDto;
import krasa.core.frontend.commons.SpanMultiLineLabel;

import org.apache.wicket.ajax.*;
import org.apache.wicket.markup.html.panel.*;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;

public class LogPanel extends Panel {

	private final LogModel model;
	protected LogFileDto last;

	public LogPanel(String id, LogModel model) {
		super(id);
		this.model = model;

		if (model.exists()) {
			add(createLogData());
			add(createNextLog());
		} else {
			add(new EmptyPanel("logData"));
			add(new EmptyPanel("nextLog"));
		}
	}

	private SpanMultiLineLabel createLogData() {
		return new SpanMultiLineLabel("logData", new LoadableDetachableModel<String>() {

			@Override
			protected String load() {
				last = model.getLog();
				return last.getText();
			}
		});
	}

	private SpanMultiLineLabel createNextLog() {
		final LoadableDetachableModel<String> model = new LoadableDetachableModel<String>() {

			@Override
			protected String load() {
				int offset = last.getOffset();
				last = LogPanel.this.model.getNextLog(offset);
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

				if (!LogPanel.this.model.isAlive() && model.getObject().isEmpty()) {
					stop(target);
					onUpdate(target);
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

	protected void onUpdate(AjaxRequestTarget target) {

	}
}
