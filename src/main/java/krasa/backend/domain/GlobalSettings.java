package krasa.backend.domain;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Vojtech Krasa
 */
@Entity
public class GlobalSettings extends AbstractEntity {

    @ElementCollection(targetClass = String.class)
    private Set<String> projectsWithSubfoldersMergeSearching = new HashSet<String>();

    public Set<String> getProjectsWithSubfoldersMergeSearching() {
        return projectsWithSubfoldersMergeSearching;
    }

    public void setProjectsWithSubfoldersMergeSearching(Set<String> projectsWithSubfoldersMergeSearching) {
        this.projectsWithSubfoldersMergeSearching = projectsWithSubfoldersMergeSearching;
    }

    public Boolean isMergeOnSubFoldersForProject(String path) {
        return projectsWithSubfoldersMergeSearching.contains(path);
    }

    public void addMergeOnSubFoldersForProject(String path) {
        projectsWithSubfoldersMergeSearching.add(path.toLowerCase());
    }

    public void setProjectsWithSubfoldersMergeSearching(String path, Boolean modelObject) {
        if (modelObject) {
            projectsWithSubfoldersMergeSearching.add(path.toLowerCase());
        } else {
            projectsWithSubfoldersMergeSearching.remove(path.toLowerCase());
        }
    }
}
