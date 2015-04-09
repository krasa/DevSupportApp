package krasa.merge.backend.facade;

import java.io.File;

import org.apache.wicket.util.io.IOUtils;
import org.junit.Test;

/**
 * @author Vojtech Krasa
 */
public class FacadeTest {

	@Test
	public void testName() throws Exception {
		ProcessBuilder pb = new ProcessBuilder("ipconfig");
		pb.directory(new File("C:/"));
		Process p = pb.start();
		System.out.println(IOUtils.toString(p.getInputStream()));
		p.destroy();
	}

}
