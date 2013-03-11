package krasa.merge.backend;

import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;

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
