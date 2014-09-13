package krasa.core.frontend.utils;

import org.apache.wicket.util.string.AppendingStringBuffer;

public class Strings {
	/**
	 * Converts a String to multiline HTML markup by replacing newlines with line break entities (&lt;br/&gt;)
	 * 
	 * @param s
	 *            String to transform
	 * @return String with all single occurrences of newline replaced with &lt;br/&gt; and all multiple occurrences of
	 *         newline replaced with &lt;p&gt;.
	 */
	public static CharSequence toMultilineMarkup(final CharSequence s) {
		if (s == null) {
			return null;
		}

		final AppendingStringBuffer buffer = new AppendingStringBuffer();
		int newlineCount = 0;

		buffer.append("<span>");
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);

			switch (c) {
			case '\n':
				newlineCount++;
				break;

			case '\r':
				break;

			default:
				for (int j = 0; j < newlineCount; j++) {
					buffer.append("<br/>");
				}

				buffer.append(c);
				newlineCount = 0;
				break;
			}
		}
		if (newlineCount == 1) {
			buffer.append("<br/>");
		} else if (newlineCount > 1) {
			buffer.append("</span><span>");
		}
		buffer.append("</span>");
		return buffer;
	}

	public static String cutExtension(String name) {
		return name.substring(0, name.indexOf("."));
	}
}
