package krasa.svn.backend.service;

import org.tmatesoft.svn.core.*;

public class SvnException extends RuntimeException {

	protected final SVNErrorCode errorCode;

	public SvnException(SVNException e) {
		super(e.getMessage(), e);
		errorCode = e.getErrorMessage().getErrorCode();
	}

	public SVNErrorCode getErrorCode() {
		return errorCode;
	}
}
