package net.pladema.person.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "person_extended")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonExtended implements Serializable {
     /*
     */
    private static final long serialVersionUID = -4236914278481347401L;

    @Id
    @Column(name = "person_id", nullable = false)
    private Integer id;

    @Column(name = "cuil", length = 10)
    private String cuil;

    @Column(name = "mothers_last_name", length = 20)
    private String mothersLastName;

    @Column(name = "addres_id", nullable = false)
    private Integer addressId;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "email", length = 100,  nullable = false)
    private String email;

    @Column(name = "ethnic", length = 25)
    private String ethnic;

    @Column(name = "religion", length = 25)
    private String religion;

    @Column(name = "name_self_determination", length = 25)
    private String nameSelfDetermination;

    @Column(name = "gender_self_determination")
    private Short genderSelfDetermination;

    @Column(name = "health_insurance_id")
    private Short healthInsuranceId;
}
