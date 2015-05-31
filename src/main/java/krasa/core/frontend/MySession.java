package krasa.core.frontend;

import javax.servlet.http.Cookie;

import krasa.svn.backend.domain.Profile;
import krasa.svn.backend.facade.SvnFacade;

import org.apache.wicket.*;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.*;
import org.apache.wicket.spring.injection.annot.*;

/**
 * @author Vojtech Krasa
 */
public class MySession extends WebSession {

	public static final String PROFILE_ID = "profileId";
	@SpringBean
	private SvnFacade facade;

	private Integer current;

	/**
	 * Constructor. Note that {@link org.apache.wicket.request.cycle.RequestCycle} is not available until this
	 * constructor returns.
	 * 
	 * @param request
	 *            The current request
	 */
	public MySession(Request request) {
		super(request);
		SpringComponentInjector.get().inject(this);
		Cookie cookie = ((WebRequest) request).getCookie(PROFILE_ID);
		if (cookie != null && cookie.getValue() != null && current == null) {
			current = Integer.valueOf(cookie.getValue());
		}
	}

	public Profile getCurrent() {
		Profile profile;
		if (current == null) {
			profile = facade.getDefaultProfile();
			current = profile.getId();
		} else {
			profile = facade.getProfileByIdOrDefault(current);
		}
		return profile;
	}

	public void setCurrentProfile(Integer current) {
		this.current = current;
		Cookie cookie = new Cookie(PROFILE_ID, current.toString());
		cookie.setMaxAge(Integer.MAX_VALUE);
		((WebResponse) RequestCycle.get().getResponse()).addCookie(cookie);
	}

	/**
	 * Returns session associated to current thread. Should always return a session during a request cycle, even though
	 * the session might be temporary
	 * 
	 * @return session.
	 */
	public static MySession get() {
		MySession session = (MySession) ThreadContext.getSession();
		if (session != null) {
			return session;
		} else {
			return (MySession) Application.get().fetchCreateAndSetSession(RequestCycle.get());
		}
	}

	public Integer getCurrentProfileId() {
		return getCurrent().getId();
	}

}
