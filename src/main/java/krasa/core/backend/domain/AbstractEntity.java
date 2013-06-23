package krasa.core.backend.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.common.base.Objects;

@MappedSuperclass
public abstract class AbstractEntity<T> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Integer id;

	public AbstractEntity() {
	}

	/**
	 * The unique ID of the image in the database
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * The unique ID of the image in the database
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof AbstractEntity))
			return false;

		AbstractEntity that = (AbstractEntity) o;

		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).toString();
	}
}
