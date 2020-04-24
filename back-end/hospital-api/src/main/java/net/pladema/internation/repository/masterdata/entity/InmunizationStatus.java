package net.pladema.internation.repository.masterdata.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "inmunization_status")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class InmunizationStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3053291021636483828L;

	public static final String COMPLETE = "255594003";
	public static final String ERROR = "723510000";
	public static final String NOT_DONE = "385660001";

	@Id
	@Column(name = "id", length = 20)
	private String id;

	@Column(name = "description", nullable = false, length = 100)
	private String description;

}
