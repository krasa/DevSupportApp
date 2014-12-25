package krasa.smrt;

import java.util.List;

import javax.annotation.Nullable;

import krasa.core.frontend.commons.table.IFilter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

public class SmrtFilter implements IFilterStateLocator<SmrtConnection>, IFilter<SmrtConnection> {

	private SmrtConnection smrtConnection = new SmrtConnection();

	@Override
	public SmrtConnection getFilterState() {
		return smrtConnection;
	}

	@Override
	public void setFilterState(SmrtConnection state) {
		smrtConnection = state;
	}

	@Override
	public List<SmrtConnection> filter(List<SmrtConnection> smrtConnections) {
		return Lists.newArrayList(Iterables.filter(smrtConnections, new Predicate<SmrtConnection>() {

			@Override
			public boolean apply(@Nullable SmrtConnection input) {
				if (envNotMatching(input)) {
					return false;
				}
				if (smrtConnection.routingId != null && !smrtConnection.routingId.equals(input.routingId)) {
					return false;
				}
				if (smrtConnection.type != null && !smrtConnection.type.equals(input.type)) {
					return false;
				}
				if (smrtConnection.status != null && !smrtConnection.status.equals(input.status)) {
					return false;
				}
				if (smrtConnection.bindType != null && !smrtConnection.bindType.equals(input.bindType)) {
					return false;
				}
				if (smrtConnection.systemId != null && !smrtConnection.systemId.equals(input.systemId)) {
					return false;
				}
				if (smrtConnection.url != null && !smrtConnection.url.equals(input.url)) {
					return false;
				}
				return true;
			}
		}));
	}

	private boolean envNotMatching(SmrtConnection input) {
		return smrtConnection.environment != null
				&& !(isUat(input.environment) || smrtConnection.environment.equals(input.environment));
	}

	private boolean isUat(String environment) {
		if ("uat".equals(smrtConnection.environment)) {
			return environment.startsWith("uat");
		}
		return false;
	}

	public List<SmrtConnection> filterByEnvironment(List<SmrtConnection> smrtConnections) {
		return Lists.newArrayList(Iterables.filter(smrtConnections, new Predicate<SmrtConnection>() {

			@Override
			public boolean apply(@Nullable SmrtConnection input) {
				if (envNotMatching(input)) {
					return false;
				}
				return true;
			}
		}));
	}
}
