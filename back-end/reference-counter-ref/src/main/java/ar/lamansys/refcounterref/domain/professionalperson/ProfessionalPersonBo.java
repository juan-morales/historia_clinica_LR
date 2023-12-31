package ar.lamansys.refcounterref.domain.professionalperson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalPersonBo {

    private Integer id;

    private String firstName;

	private String nameSelfDetermination;

    private String lastName;

	private String otherLastNames;

	private String middleNames;

    public ProfessionalPersonBo(String firstName, String nameSelfDetermination,
								String lastName,String otherLastNames,String middleNames) {
    	this.firstName = firstName;
    	this.nameSelfDetermination = nameSelfDetermination;
    	this.lastName = lastName;
		this.otherLastNames=otherLastNames;
		this.middleNames=middleNames;
	}

	public ProfessionalPersonBo(Integer id, String firstName, String nameSelfDetermination, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.nameSelfDetermination = nameSelfDetermination;
		this.lastName = lastName;
	}
}
