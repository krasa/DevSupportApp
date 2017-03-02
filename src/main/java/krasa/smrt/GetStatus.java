package krasa.smrt;

import java.util.*;
import java.util.concurrent.Callable;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class GetStatus implements Callable<List<SmrtConnection>> {

	String url;
	String env;
	private List<SmrtConnection> smrtConnections = Collections.emptyList();

	public GetStatus(String url, String env) {
		this.url = url;
		this.env = env;
	}

	@Override
	public List<SmrtConnection> call() throws Exception {
		try {
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			requestFactory.setReadTimeout(50000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);

			ResponseEntity<String> forEntity = restTemplate.getForEntity(
					url, String.class);
			String[] split = forEntity.getBody().split("\n");

			smrtConnections = new ArrayList<>();
			for (int i = 0; i < split.length; i++) {
				if (i == 0) {
					continue;
				}
				smrtConnections.add(new SmrtConnection(env, split[i]));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return smrtConnections;
	}
}
