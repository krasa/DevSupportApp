package krasa.merge.backend.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import krasa.core.backend.domain.AbstractEntity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
@Entity
public class Profile extends AbstractEntity<Profile> implements Serializable {
	public static final String DATE_PATTERN = "yyyy.MM.dd HH:MM";

	@Column
	private String name = "NewProfile";
	@Column
	private Integer revisionFrom;
	@ManyToMany(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.DELETE, CascadeType.SAVE_UPDATE, CascadeType.MERGE })
	private List<Branch> branches = new ArrayList<>();
	@Enumerated
	private Type type = Type.USER;

	public void removeBranch(String name) {
		for (int i = 0; i < branches.size(); i++) {
			Branch branch = branches.get(i);
			if (branch.getName().equals(name)) {
				branches.remove(i);
				return;
			}
		}
	}

	public List<String> getBranchesNames() {
		ArrayList<String> strings = new ArrayList<>();
		for (Branch branch : branches) {
			strings.add(branch.getName());
		}
		return strings;
	}

	public enum Type {
		USER,
		FROM_SVN
	}

	public Profile() {
	}

	public Profile(Profile profile) {
		branches = profile.getBranches();
		SimpleDateFormat s = new SimpleDateFormat(DATE_PATTERN);
		this.setName("New Profile " + s.format(new Date()));
	}

	public Profile(SVNDirEntry dirEntry, String content) {
		type = Type.FROM_SVN;
		name = dirEntry.getName();
		branches = new ArrayList<>();
		for (String o : content.split("\n")) {
			String[] split = o.split(" ");
			Branch e = new Branch(split);
			branches.add(e);
		}
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

	public List<Branch> getBranches() {
		return branches;
	}

	public void setBranches(List<Branch> branches) {
		this.branches = branches;
	}

	public void addBranch(String name) {
		for (Branch branch : branches) {
			if (branch.getName().equals(name)) {
				return;
			}
		}
		branches.add(new Branch(name));
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
