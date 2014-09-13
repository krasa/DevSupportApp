package krasa.build.backend;

import java.util.Date;

public class DateUtils {
	public static int compareDates(Date start, Date start1) {
		if (start == null && start1 == null) {
			return 0;
		}
		if (start == null) {
			return 1;
		}
		if (start1 == null) {
			return -1;
		}
		return start.compareTo(start1);
	}

	public static int compareDatesNullOnEnd(Date start, Date start1) {
		if (start == null && start1 == null) {
			return 0;
		}
		if (start == null) {
			return 1;
		}
		if (start1 == null) {
			return -1;
		}
		return start1.compareTo(start);
	}
}
