package krasa.svn.backend.service;

import java.io.ByteArrayOutputStream;
import java.util.*;

import krasa.svn.backend.connection.SVNConnector;
import krasa.svn.backend.domain.Profile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
@Component
public class SvnReleaseProvider {

	@Value("${releases.url}")
	private String url;
	@Value("${releases.url.suffix}")
	private String path;

	public List<Profile> getReleases() {
		ArrayList<Profile> releases = new ArrayList<>();
		SVNRepository repository = new SVNConnector().connect(url);

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
