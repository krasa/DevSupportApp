package krasa.merge.frontend.pages.config;

import java.util.logging.Logger;

import krasa.core.frontend.pages.BasePage;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.ajax.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.ws.api.*;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

/**
 * @author Vojtech Krasa
 */
public class TestPage extends BasePage {

	private final static Logger logger = Logger.getLogger(ProfileEditPanel.class.getName());

	@SpringBean
	private Facade facade;
	private Label foo;

	public TestPage() {
		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
			}
		});
		AbstractAjaxTimerBehavior abstractAjaxTimerBehavior = new AbstractAjaxTimerBehavior(Duration.seconds(1)) {

			@Override
			protected void onTimer(AjaxRequestTarget ajaxRequestTarget) {
				ajaxRequestTarget.add(foo);
			}
		};
		add(abstractAjaxTimerBehavior);
		foo = new Label("foo", "foo");
		add(foo);
	}

}
