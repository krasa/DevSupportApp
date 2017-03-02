package krasa.core.backend;

import java.net.InetAddress;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteHostUtils {
	private static final Logger log = LoggerFactory.getLogger(RemoteHostUtils.class);

	public static String getRemoteHost() {
		try {
			WebRequest req = (WebRequest) RequestCycle.get().getRequest();
			HttpServletRequest httpReq = (HttpServletRequest) req.getContainerRequest();
			Enumeration<String> headerNames = httpReq.getHeaderNames();
			String ip = null;
			while (headerNames.hasMoreElements()) {
				String next = headerNames.nextElement();
				if ("X-Forwarded-For".equalsIgnoreCase(next)) {
					String header = httpReq.getHeader(next);
					String[] split = header.split(",");
					if (split.length > 0) {
						ip = split[0];
						break;
					}
				}
			}
			if (ip == null) {
				ip = httpReq.getRemoteAddr();
			}
			InetAddress addr = InetAddress.getByName(ip);
			String host = addr.getHostName();
			host = simplifyHost(host);
			return host;
		} catch (Throwable e) {
			log.error("", e);
			return "unknown";
		}
	}

	protected static String simplifyHost(String host) {
		if (host.endsWith(".tmdev")) {
			host = host.substring(0, host.length() - ".tmdev".length());
		}
		if (host.startsWith("ntb-")) {
			host = host.substring("ntb-".length(), host.length());
		}
		return host;
	}
}
