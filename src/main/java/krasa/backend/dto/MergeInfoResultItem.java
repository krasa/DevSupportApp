package krasa.backend.dto;

import krasa.backend.domain.SvnFolder;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoResultItem implements Serializable {
    private String from;
    private String to;
    private List<SVNLogEntry> merges;

    public MergeInfoResultItem(SvnFolder to, SvnFolder from, List<SVNLogEntry> merges) {
        this.from = from.getName();
        this.to = to.getName();
        this.merges = merges;
    }

    public MergeInfoResultItem(SvnFolder to, SvnFolder from, String commonFolder, List<SVNLogEntry> merges) {
        this.from = from.getName() + " " + commonFolder;
        this.to = to.getName() + " " + commonFolder;
        this.merges = merges;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<SVNLogEntry> getMerges() {
        return merges;
    }

    public void setMerges(List<SVNLogEntry> merges) {
        this.merges = merges;
    }
}