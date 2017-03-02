package krasa.core.backend.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class JaxbUtils {

	public static final SAXParserFactory saxParserFactory = createXmlReader();

	public static <T> T unmarshal(File newFile, Unmarshaller unmarshaller) throws SAXException,
			ParserConfigurationException, FileNotFoundException, JAXBException {
		SAXSource source = getSaxSource(newFile, saxParserFactory);
		return (T) unmarshaller.unmarshal(source);
	}

	private static SAXParserFactory createXmlReader() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			return spf;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private static SAXSource getSaxSource(File newFile, SAXParserFactory spf) throws SAXException,
			ParserConfigurationException, FileNotFoundException {
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();
		return new SAXSource(xmlReader, new InputSource(new FileInputStream(newFile)));
	}

	public static JAXBContext getJaxbContext(Class... classes) {
		try {
			return JAXBContext.newInstance(classes);
		} catch (JAXBException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
