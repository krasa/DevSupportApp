import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class resolve {

	public static final String SOURCE = "C:\\workspace\\_projekty\\_tmobile\\9999\\PORTAL_9999";
	public static final String DESTINATION = "C:\\workspace\\_projekty\\_tmobile\\PORTAL_9999_TestMove";

	public static void main(String[] args) throws IOException {
		Iterator<File> fileIterator = FileUtils.iterateFiles(
				new File(SOURCE), TrueFileFilter.TRUE,
				TrueFileFilter.TRUE);
		Multimap<String, File> old = ArrayListMultimap.create();
		while (fileIterator.hasNext()) {
			File next = fileIterator.next();
			old.put(next.getName(), next);
		}

		Iterator<File> destinationIt = FileUtils.iterateFiles(
				new File(DESTINATION), TrueFileFilter.TRUE,
				TrueFileFilter.TRUE);
		while (destinationIt.hasNext()) {
			File next = destinationIt.next();

			List<File> files = (List<File>) old.get(next.getName());
			if (!files.isEmpty()) {
				if (files.size() == 1) {
					File file = files.get(0);
					System.err.println(toRelativePathFromRoot(file) + " " + toRelativePathFromRootNew(next));
				} else {
					System.err.println("conflict " + next);
				}
			}
		}
	}

	private static String toRelativePathFromRootNew(File next) throws IOException {
		String canonicalPath = next.getCanonicalPath();
		int portal_9999 = canonicalPath.indexOf("PORTAL_9999_TestMove");
		return canonicalPath.substring(portal_9999 + 1, canonicalPath.length());
	}

	private static String toRelativePathFromRoot(File file) throws IOException {
		String canonicalPath = file.getCanonicalPath();
		int portal_9999 = canonicalPath.indexOf("PORTAL_9999");
		return canonicalPath.substring(portal_9999 + 1, canonicalPath.length());
	}
}
