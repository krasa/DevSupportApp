package krasa.svn.frontend.pages.config;

import java.util.logging.Logger;

import javax.servlet.http.Cookie;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

import krasa.core.frontend.pages.BasePage;
import krasa.svn.backend.facade.SvnFacade;

/**
 * @author Vojtech Krasa
 */
public class TestPage extends BasePage {

	private final static Logger logger = Logger.getLogger(ProfileEditPanel.class.getName());

	@SpringBean
	private SvnFacade facade;
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

		queue(foo);
		Form form = new Form("form");
		queue(form);
		form.add(new AjaxButton("testButton") {

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();

				// Variant A: Get cookie with specified name
				Cookie cookie = webRequest.getCookie("vojtitko_userName");
				if (cookie != null) {
					String userName = cookie.getValue();

				}

				newCookie();

			}
		});
	}

	private void newCookie() {
		WebResponse webResponse = (WebResponse) RequestCycle.get().getResponse();

		// Create cookie and add it to the response
		Cookie cookie = new Cookie("vojtitko_userName", "cookieValue");
		cookie.setMaxAge(Integer.MAX_VALUE);
		webResponse.addCookie(cookie);
	}

}
