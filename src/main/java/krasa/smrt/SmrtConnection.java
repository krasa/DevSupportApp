package krasa.smrt;

import java.io.Serializable;

class SmrtConnection implements Serializable {

	String environment;
	String type;
	String routingId;
	String status;
	String bindType;
	String systemId;
	String url;
	String lastBoundStartTime;
	String lastBoundEndTime;
	String reconnects;

	public SmrtConnection() {
	}

	public SmrtConnection(String env, String s) {
		environment = env;
		SmrtConnectionParser parser = new SmrtConnectionParser(s);
		type = parser.next();
		routingId = parser.next();
		status = parser.next();
		bindType = parser.next();
		systemId = parser.next();
		url = parser.next();
		lastBoundStartTime = parser.next();
		lastBoundEndTime = parser.next();
		reconnects = parser.next();
	}

	class SmrtConnectionParser {

		private String[] split;
		int i = 0;

		public SmrtConnectionParser(String s) {
			split = s.split("\\|");
		}

		public String next() {
			if (split.length <= i) {
				return "null";
			}
			return split[i++].trim();
		}
	}

}
