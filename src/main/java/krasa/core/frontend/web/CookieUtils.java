package krasa.core.frontend.web;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import krasa.build.backend.facade.UsernameException;
import krasa.core.backend.RemoteHostUtils;

public class CookieUtils {

	private static final String VOJTITKO_USER_NAME = "vojtitko_userName";
	private static final String BUILD_COMPONENT = "vojtitko_defaultBuildComponent";

	public static String getCookie_userName() {
		String userNameX = getCookieValue(VOJTITKO_USER_NAME);
		if (userNameX == null) {
			String remoteHost = RemoteHostUtils.getRemoteHost();
			remoteHost = remoteHost.replace("0:0:0:0:0:0:0:1", "local");
			newCookie_userName(remoteHost);
			userNameX = remoteHost;
		}
		return userNameX;
	}

	public static void newCookie_userName(String userName) {
		setCookieValue(userName, VOJTITKO_USER_NAME);
	}

	public static String getBuildComponent() {
		return getCookieValue(BUILD_COMPONENT);
	}

	public static void setBuildComponent(String buildComponent) {
		setCookieValue(buildComponent, BUILD_COMPONENT);
	}

	private static void setCookieValue(String buildComponent, String buildComponent2) {
		WebResponse webResponse = (WebResponse) RequestCycle.get().getResponse();
		Cookie cookie = new Cookie(buildComponent2, buildComponent);
		cookie.setMaxAge(Integer.MAX_VALUE);
		webResponse.addCookie(cookie);
	}

	private static String getCookieValue(String vojtitkoUserName) {
		WebRequest webRequest = (WebRequest) RequestCycle.get().getRequest();
		Cookie cookie = webRequest.getCookie(vojtitkoUserName);
		String userNameX = null;
		if (cookie != null) {
			userNameX = cookie.getValue();
		}
		return userNameX;
	}

	public static String getValidUsername() throws UsernameException {
		String cookie_userName = getCookie_userName();
		if (StringUtils.isBlank(cookie_userName)) {
			throw new UsernameException("Fill your user name!!!");
		}
		if (!StringUtils.isAlpha(cookie_userName)) {
			throw new UsernameException("Only letters are allowed as username");
		}
		return cookie_userName;
	}
}
