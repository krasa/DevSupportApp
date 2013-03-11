package krasa.merge.backend.svn;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.svn.connection.SVNConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
@Component
public class SvnReleaseProviderImpl implements SvnReleaseProvider {

	@Value("${releases.url}")
	private String url;
	@Value("${releases.url.suffix}")
	private String path;
	@Autowired
	SVNConnector svnConnector;

	public List<Profile> getReleases() {
		ArrayList<Profile> releases = new ArrayList<Profile>();
		SVNRepository repository = svnConnector.connect(url);
		try {
			Collection projects = repository.getDir(path, -1, null, (Collection) null);
			Iterator iterator = projects.iterator();
			while (iterator.hasNext()) {
				SVNDirEntry file = (SVNDirEntry) iterator.next();
				if (file.getKind() == SVNNodeKind.FILE) {
					SvnUtils.printInfo(file);

					Map fileProperties = new HashMap();
					SVNProperties properties = new SVNProperties();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					repository.getFile(path + "/" + file.getName(), -1, properties, baos);
					String mimeType = (String) fileProperties.get(SVNProperty.MIME_TYPE);
					boolean isTextType = SVNProperty.isTextMimeType(mimeType);

					if (isTextType) {
						releases.add(new Profile(file, baos.toString()));
					}
				}
			}
		} catch (SVNException e) {
			throw new RuntimeException(e);
		} finally {
			repository.closeSession();
		}
		return releases;
	}

}
