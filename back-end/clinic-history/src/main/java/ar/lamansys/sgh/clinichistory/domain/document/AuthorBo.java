package ar.lamansys.sgh.clinichistory.domain.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthorBo {

	private Integer personId;
    private String firstName;
    private String lastName;
	private String nameSelfDetermination;

	public AuthorBo(Integer personId, String firstName, String lastName, String nameSelfDetermination){
		this.personId = personId;
		this.firstName = firstName;
        this.lastName = lastName;
		this.nameSelfDetermination = nameSelfDetermination;
    }

}
