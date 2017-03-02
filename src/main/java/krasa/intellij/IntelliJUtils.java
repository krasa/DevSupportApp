package krasa.intellij;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import krasa.core.backend.utils.JaxbUtils;
import krasa.core.frontend.WicketApplication;

/**
 * @author Vojtech Krasa
 */
public class IntelliJUtils {

	private static final Logger log = LoggerFactory.getLogger(IntelliJUtils.class);

	public static final JAXBContext JAXB_CONTEXT = JaxbUtils.getJaxbContext(PluginDefinition.class, PluginsIndex.class);

	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(File newFile, JAXBContext jaxbContext) {
		try {
			return JaxbUtils.unmarshal(newFile, jaxbContext.createUnmarshaller());
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void saveIndex(PluginsIndex index) {
		File indexFile = indexFile();
		try {
			getMarshaller().marshal(index, indexFile);
		} catch (JAXBException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static PluginsIndex getPluginsIndex() {
		PluginsIndex pluginsIndex;
		File indexFile = indexFile();
		if (indexFile.exists()) {
			try {
				pluginsIndex = (PluginsIndex) getUnmarshaller().unmarshal(indexFile);
			} catch (Throwable e) {
				log.error("Index corrupted", e);
				pluginsIndex = new PluginsIndex();
			}
		} else {
			pluginsIndex = new PluginsIndex();
		}
		return pluginsIndex;

	}

	private static File indexFile() {
		return new File(WicketApplication.getWicketApplication().getUploadFolder().getAbsoluteFile(), "index.xml");
	}

	static Unmarshaller getUnmarshaller() {
		try {
			return JAXB_CONTEXT.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	static javax.xml.bind.Marshaller getMarshaller() {
		try {
			return JAXB_CONTEXT.createMarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
