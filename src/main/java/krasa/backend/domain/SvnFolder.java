package krasa.backend.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.tmatesoft.svn.core.SVNDirEntry;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Vojtech Krasa
 */
@Entity
public class SvnFolder extends AbstractEntity {
    @Column
    private String name;
    @Column
    private String path;
    @Enumerated
    private Type type;
    @OneToMany(mappedBy = "parent")
    @Cascade(CascadeType.DELETE)
    private List<SvnFolder> childs;
    @ManyToOne
    private SvnFolder parent;

    public SvnFolder() {
    }

    public SvnFolder(SVNDirEntry entry, String path) {
        name = entry.getName();
        this.path = path;

    }

    public SvnFolder(SVNDirEntry child, Type branch) {
        this(child, child.getURL().getPath());
        setType(branch);
    }

    public SvnFolder(SVNDirEntry entry, String pathToParentDir, Type type) {
        name = entry.getName();
        this.path = pathToParentDir + "/" + name;
        setType(type);

    }

    public void add(SvnFolder branch) {
        if (childs == null) {
            childs = new ArrayList<SvnFolder>();
        }
        childs.add(branch);
        branch.setParent(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<SvnFolder> getChilds() {
        if (childs == null) {
            childs = new ArrayList<SvnFolder>();
        }
        return childs;
    }

    public void setChilds(List<SvnFolder> childs) {
        this.childs = childs;
    }

    public SvnFolder getParent() {
        return parent;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setParent(SvnFolder parent) {
        this.parent = parent;
    }

    public void removeChild(SvnFolder svnFolder) {
        childs.remove(svnFolder);
    }

    public Set<String> getChildNamesAsSet() {
        Set<String> svnFolders = new HashSet<String>();

        for (SvnFolder child : childs) {
            svnFolders.add(child.getName());
        }
        return svnFolders;
    }
}
