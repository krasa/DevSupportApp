package krasa.intellij;

import static krasa.intellij.IntelliJUtils.unmarshal;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.moveFile;
import static org.apache.commons.io.filefilter.FileFilterUtils.trueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import krasa.core.frontend.WicketApplication;
import krasa.core.frontend.utils.Strings;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

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
		setMaxSize(Bytes.kilobytes(20000));
	}

	public FileUploadField getFileUploadField() {
		return fileUploadField;
	}

	@Override
	protected void onSubmit() {
		List<FileUpload> uploads = fileUploadField.getFileUploads();
		if (uploads.isEmpty()) {
			error("No file uploaded");
		}
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
				saveFile(newFile, pluginDefinition);
				updateIndex(pluginDefinition);
				info("saved file: " + upload.getClientFileName());
			} catch (Throwable e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	private void saveFile(File tempFile, PluginDefinition pluginDefinition) throws IOException {
		File pluginsFolder = WicketApplication.getWicketApplication().getPluginsFolder();
		String extension = tempFile.getName().substring(tempFile.getName().indexOf("."));
		File destFile = new File(pluginsFolder, pluginDefinition.getId() + "_" + pluginDefinition.getVersion()
				+ extension);
		pluginDefinition.setFileName(destFile.getName());
		destFile.delete();
		moveFile(tempFile, destFile);
	}

	private PluginDefinition getPluginDefinition(File newFile) throws ZipException, JAXBException, IOException {
		PluginDefinition pluginDefinition = null;
		File tmpDir = new File(getUploadFolder().getAbsolutePath() + "/tmp" + Strings.cutExtension(newFile.getName()));
		if (newFile.getName().endsWith("zip")) {
			ZipFile zipFile = new ZipFile(newFile);
			try {
				FileUtils.deleteDirectory(tmpDir);
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			zipFile.extractAll(tmpDir.getAbsolutePath());
		} else {
			File destFile = new File(tmpDir, newFile.getName());
			destFile.delete();
			copyFile(newFile, destFile);
		}
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

	private void updateIndex(PluginDefinition pluginDefinition) {
		PluginsIndex pluginsIndex = IntelliJUtils.getPluginsIndex();
		pluginsIndex.add(pluginDefinition);
		IntelliJUtils.saveIndex(pluginsIndex);
	}

	public Folder getUploadFolder() {
		return WicketApplication.getWicketApplication().getUploadFolder();
	}
}
