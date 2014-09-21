package krasa.intellij;

import static krasa.intellij.IntelliJUtils.unmarshal;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.filefilter.FileFilterUtils.trueFileFilter;

import java.io.*;
import java.util.*;

import javax.xml.bind.JAXBException;

import krasa.core.frontend.WicketApplication;
import krasa.core.frontend.utils.Strings;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.*;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.*;
import org.springframework.util.Assert;

/**
 * @author Vojtech Krasa
 */
public class PluginUploadForm extends Form<Void> {
	private static final Logger log = LoggerFactory.getLogger(PluginUploadForm.class);

	FileUploadField fileUploadField;

	public PluginUploadForm(String id) {
		super(id);
		setMultiPart(true);
		add(fileUploadField = new FileUploadField("fileInput"));
		setMaxSize(Bytes.kilobytes(5000));
	}

	public FileUploadField getFileUploadField() {
		return fileUploadField;
	}

	@Override
	protected void onSubmit() {
		final List<FileUpload> uploads = fileUploadField.getFileUploads();
		if (uploads != null) {
			for (FileUpload upload : uploads) {
				File newFile = new File(getUploadFolder(), upload.getClientFileName());
				try {
					if (newFile.exists()) {
						Assert.isTrue(newFile.delete());
					}
					Assert.isTrue(newFile.createNewFile());
					upload.writeTo(newFile);
					PluginDefinition pluginDefinition = getPluginDefinition(newFile);

					if (pluginDefinition == null) {
						throw new IllegalStateException("could not find plugin.xml");
					}

					updateIndex(newFile, pluginDefinition);
					info("saved file: " + upload.getClientFileName());
				} catch (Exception e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
		}
	}

	private PluginDefinition getPluginDefinition(File newFile) throws ZipException, JAXBException {
		PluginDefinition pluginDefinition = null;
		ZipFile zipFile = new ZipFile(newFile);
		File tmpDir = new File(getUploadFolder().getAbsolutePath() + "/tmp" + Strings.cutExtension(newFile.getName()));
		try {
			FileUtils.deleteDirectory(tmpDir);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		zipFile.extractAll(tmpDir.getAbsolutePath());
		Collection<File> files = listFiles(tmpDir, new String[] { "jar" }, true);
		for (File file : files) {
			ZipFile jarFile = new ZipFile(file);
			File tmpJarDir = new File(tmpDir, "tmp" + file.getName());
			jarFile.extractAll(tmpJarDir.getAbsolutePath());
			Collection<File> pluginXml = listFiles(tmpJarDir, new NameFileFilter("plugin.xml"), trueFileFilter());
			for (File file1 : pluginXml) {
				pluginDefinition = unmarshal(file1, IntelliJUtils.JAXB_CONTEXT);
				log.info("Found {}", pluginDefinition);
			}
		}
		return pluginDefinition;
	}

	private void updateIndex(File newFile, PluginDefinition pluginDefinition) {
		PluginsIndex pluginsIndex = IntelliJUtils.getPluginsIndex();
		pluginDefinition.setFileName(newFile.getName());
		pluginsIndex.add(pluginDefinition);
		IntelliJUtils.saveIndex(pluginsIndex);
	}

	public Folder getUploadFolder() {
		return WicketApplication.getWicketApplication().getUploadFolder();
	}
}
