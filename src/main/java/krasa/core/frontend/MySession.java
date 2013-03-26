package krasa.core.frontend;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.facade.Facade;

import org.apache.wicket.Application;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Vojtech Krasa
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("MySession")
public class MySession extends WebSession {

	public static final String PROFILE_ID = "profileId";
	@SpringBean
	private Facade facade;

	private Integer current;
	@Null
	private MultiValueMap<Integer, String> scheduledBranchBuild;

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

	public void queueComponentToEnvironmentBuild(Environment environment, BuildableComponent component) {
		getScheduledComponentBuild().add(environment.getId(), component.getName());
	}

	private MultiValueMap<Integer, String> getScheduledComponentBuild() {
		if (scheduledBranchBuild == null) {
			scheduledBranchBuild = new LinkedMultiValueMap<Integer, String>();
		}
		return scheduledBranchBuild;
	}

	public void removeComponentFromBuild(Environment environment, BuildableComponent component) {
		if (scheduledBranchBuild != null) {
			scheduledBranchBuild.get(environment.getId()).remove(component.getName());
		}
	}

	public List<String> getScheduledComponentsByEnvironmentId(Environment environment) {
		List<String> stringList;
		stringList = getScheduledComponentBuild().get(environment.getId());
		if (stringList == null) {
			stringList = Collections.emptyList();
		}
		return stringList;
	}

	public void clear(Environment environment) {
		getScheduledComponentBuild().remove(environment.getId());
	}

	public boolean isQueued(@NotNull Environment environment, @NotNull String branchName) {
		List<String> strings = getScheduledComponentBuild().get(environment.getId());
		if (strings == null) {
			return false;
		}
		return strings.contains(branchName);

	}
}
