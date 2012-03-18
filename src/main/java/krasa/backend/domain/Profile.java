package krasa.backend.domain;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Vojtech Krasa
 */
@Entity
public class Profile extends AbstractEntity<Profile> {
    public static final String DATE_PATTERN = "yyyy.MM.dd HH:MM";

    @Column
    private String name = "NewProfile";
    @Column
    private Integer revisionFrom;
    @ManyToMany
    private List<SvnFolder> includedBranches;
    @ElementCollection(targetClass = String.class)
    private Set<String> selectedBranches = new HashSet<String>();

    public Profile() {
    }

    public Profile(Profile profile) {
        includedBranches = profile.getIncludedBranches();
        selectedBranches = profile.getSelectedBranches();
        SimpleDateFormat s = new SimpleDateFormat(DATE_PATTERN);
        this.setName("New Profile " + s.format(new Date()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRevisionFrom() {
        return revisionFrom;
    }

    public void setRevisionFrom(Integer revisionFrom) {
        this.revisionFrom = revisionFrom;
    }

    public List<SvnFolder> getIncludedBranches() {
        return includedBranches;
    }

    public void setIncludedBranches(List<SvnFolder> includedBranches) {
        this.includedBranches = includedBranches;
    }

    public Set<String> getSelectedBranches() {
        return selectedBranches;
    }

    public void setSelectedBranches(Set<String> selectedBranches) {
        this.selectedBranches = selectedBranches;
    }
}
