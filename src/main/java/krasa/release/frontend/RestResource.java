package krasa.release.frontend;

import java.io.*;

import javax.ws.rs.*;

import krasa.release.service.*;
import krasa.release.tokenization.*;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
public class RestResource {

	@Autowired
	TokenizationService tokenizationService;

	/* http://localhost:1111/rest/tokenize/sdpapi/15500/9999 */
	@POST
	@Path("/tokenize/{branchName}/{toVersion}/{fromVersion}")
	public String tokenize(@PathParam("branchName") String branchName, @PathParam("toVersion") String toVersion,
			@PathParam("fromVersion") String fromVersion) {
		TokenizationResult result = tokenizationService.tokenizeSynchronously(branchName + "_" + toVersion,
				Default.generateJson(fromVersion, toVersion, toVersion, toVersion, toVersion));
		String logPath = result.getLogFile().getAbsolutePath();
		return result.getStatus().name() + "\n" + log(new File(logPath).getName());
	}

	@POST
	@Path("/tokenizeWithLog/{branchName}/{toVersion}/{fromVersion}")
	public String tokenizeWithLog(@PathParam("branchName") String branchName, @PathParam("toVersion") String toVersion,
			@PathParam("fromVersion") String fromVersion) {
		TokenizationResult result = tokenizationService.tokenizeSynchronously(branchName + "_" + toVersion,
				Default.generateJson(fromVersion, toVersion, toVersion, toVersion, toVersion));
		return result.getStatus().name() + " log: " + result.getLogFile().getAbsolutePath();
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
