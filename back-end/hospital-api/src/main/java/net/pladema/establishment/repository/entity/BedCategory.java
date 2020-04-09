package net.pladema.establishment.repository.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bed_category")
@Getter
@Setter

public class BedCategory {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BedCategory other = (BedCategory) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "BedCategory [id=" + id + ", description=" + description + "]";
	}
	
	
}
