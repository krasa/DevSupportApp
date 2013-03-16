package krasa.merge.backend.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
@Entity
public class SvnFolder extends AbstractEntity {

	public static final Comparator<SvnFolder> NAME_COMPARATOR = new Comparator<SvnFolder>() {
		@Override
		public int compare(SvnFolder o1, SvnFolder o2) {
			return o2.getName().compareTo(o1.getName());
		}
	};
	@Column
	private String name;
	private Boolean boolVal;
	@Column
	private String searchFrom;
	@Column
	private String path;
	@Enumerated
	private Type type;
	@OneToMany(mappedBy = "parent")
	@Cascade(CascadeType.DELETE)
	private List<SvnFolder> childs;
	@ManyToOne
	private SvnFolder parent;

	public Boolean getBoolVal() {
		return boolVal;
	}

	public void setBoolVal(Boolean boolVal) {
		this.boolVal = boolVal;
	}

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

	public String getSearchFrom() {
		return searchFrom;
	}

	public void setSearchFrom(String searchFrom) {
		this.searchFrom = searchFrom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (searchFrom == null) {

		}
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

	public Set<String> getCommonSubFolders(SvnFolder from) {
		Set<String> subFoldersTo = getChildNamesAsSet();
		Set<String> subFolders = from.getChildNamesAsSet();
		Set<String> commonFolders = new HashSet<String>();
		for (String s : subFoldersTo) {
			if (subFolders.contains(s)) {
				commonFolders.add(s);
			}
		}
		return commonFolders;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public Set<String> getBranchNamesAsSet() {
		HashSet<String> strings = new HashSet<String>();
		for (SvnFolder child : childs) {
			strings.add(child.getName());
		}
		return strings;

	}
}
