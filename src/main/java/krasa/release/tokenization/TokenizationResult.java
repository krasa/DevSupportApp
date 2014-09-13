package krasa.release.tokenization;

import java.io.*;

import krasa.build.backend.domain.Status;

public class TokenizationResult implements Serializable {

	private final File logFile;
	private final Status status;

	public TokenizationResult(File logFile, Status status) {

		this.logFile = logFile;
		this.status = status;
	}

	public File getLogFile() {
		return logFile;
	}

	public Status getStatus() {
		return status;
	}
}
