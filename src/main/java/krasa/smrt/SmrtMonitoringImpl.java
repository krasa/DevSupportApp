package krasa.smrt;

import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SmrtMonitoringImpl {

	private static final Logger log = LoggerFactory.getLogger(SmrtMonitoringImpl.class);

	private static ForkJoinPool pool = new ForkJoinPool(10);
	@Autowired
	Environment environment;

	public SmrtMonitoringImpl() {
	}

	public List<SmrtConnection> getSmrtConnections() {
		long start = System.currentTimeMillis();
		ArrayList<Callable<List<SmrtConnection>>> callables = new ArrayList<>();
		String property = environment.getProperty("smrt.monitoring.url", "");
		String[] split = property.split("\\|");
		for (String s : split) {
			String[] split1 = s.split("=");
			if (split1.length == 2) {
				callables.add(new GetStatus(split1[0], split1[1]));
			}
		}

		List<Future<List<SmrtConnection>>> invoke = pool.invokeAll(callables);
		List<SmrtConnection> smrtConnections = new ArrayList<>();
		for (Future<List<SmrtConnection>> listFuture : invoke) {
			try {
				smrtConnections.addAll(listFuture.get());
			} catch (Throwable e) {
				log.info("", e);
			}
		}
		long end = System.currentTimeMillis();
		// System.err.println(end - start);

		normalizeUrls(smrtConnections);
		return smrtConnections;
	}

	private void normalizeUrls(List<SmrtConnection> smrtConnections) {
		for (SmrtConnection smrtConnection : smrtConnections) {
			String url = smrtConnection.url;

			String property = environment.getProperty("smrt.url.normalization", "");
			String[] split = property.split("\\|");

			for (int i = 0; i < split.length; i++) {
				String s = split[i];
				String[] split1 = s.split("=");
				if (split1.length == 2) {
					url = url.replace(split1[0], split1[1]);
				}
			}

			smrtConnection.url = url;
		}
	}

}
