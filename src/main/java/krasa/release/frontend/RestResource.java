package krasa.release.frontend;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import krasa.release.service.TokenizationFileUtils;
import krasa.release.service.TokenizationService;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/tokenize")
public class RestResource {

	@Autowired
	TokenizationService tokenizationService;

	/* http://localhost:1111/rest/tokenize/sdpapi/15500/9999 */
	@GET
	@Path("/{branchName}/{toVersion}/{fromVersion}")
	public String sayHello(@PathParam("branchName") String branchName, @PathParam("toVersion") Integer toVersion,
			@PathParam("fromVersion") Integer fromVersion) {
		File logFile = tokenizationService.tokenizeSynchronously(branchName + "_" + toVersion, fromVersion, toVersion,
				TokenizationFileUtils.readTemplate());
		String logPath = logFile.getAbsolutePath();
		return log(new File(logPath).getName());
	}

	@GET
	@Path("/log/{logName}")
	public String log(@PathParam("logName") String logName) {

		try {
			File file = TokenizationFileUtils.getLogFileByName(logName);
			String fileToString = FileUtils.readFileToString(file);
			String s = "LogFile: " + file.getAbsolutePath() + " <br/><br/>" + fileToString;
			return s.replace("\n", "<br/>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		} catch (IOException e) {
			return e.getMessage();
		}
	}

}
