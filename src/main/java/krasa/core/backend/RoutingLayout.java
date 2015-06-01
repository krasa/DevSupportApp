package krasa.core.backend;

import java.util.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.*;

public class RoutingLayout extends LayoutBase<ILoggingEvent> {

	private Map<String, Layout<ILoggingEvent>> layouts = new HashMap<>();
	private Layout<ILoggingEvent> defaultLayout;

	@Override
	public void start() {
		if (defaultLayout == null) {
			System.err.println("defaultLayout is null");
			addError("defaultLayout is null");
		}
		if (layouts.isEmpty()) {
			addWarn("layouts are empty");
		}
		super.start();
	}

	@Override
	public String doLayout(ILoggingEvent event) {
		if (!isStarted()) {
			return CoreConstants.EMPTY_STRING;
		}
		try {
			Layout layout = layouts.get(event.getLoggerName());
			if (layout != null) {
				return layout.doLayout(event);
			} else if (defaultLayout != null) {
				return defaultLayout.doLayout(event);
			} else {
				return event.getMessage();
			}
		} catch (Exception e) {
			addError(e.getMessage(), e);
			return e.toString();
		}
	}

	public void setDefaultLayout(Layout<ILoggingEvent> defaultLayout) {
		this.defaultLayout = defaultLayout;
	}

	public void addLayoutRoute(LayoutRoute layoutRoute) {
		layouts.put(layoutRoute.loggerName, layoutRoute.layout);
	}

	public static class LayoutRoute {
		String loggerName;
		Layout<ILoggingEvent> layout;

		public void setLoggerName(String loggerName) {
			this.loggerName = loggerName;
		}

		public void setLayout(Layout<ILoggingEvent> layout) {
			this.layout = layout;
		}
	}

}
