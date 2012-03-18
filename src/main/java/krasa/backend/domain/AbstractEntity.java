package krasa.backend.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

@MappedSuperclass
public abstract class AbstractEntity<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    public AbstractEntity() {
    }


    /**
     * The unique ID of the image in the database
     */
    @XmlTransient
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
        if (this == o) return true;
        if (!(o instanceof AbstractEntity)) return false;

        AbstractEntity that = (AbstractEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


}
