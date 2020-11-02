package net.pladema.patient.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Patient implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 7559172653664261066L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "person_id")
    private Integer personId;

    @Column(name = "type_id", nullable = false)
    private Short typeId;

    @Column(name = "possible_duplicate", nullable = false)
    private Boolean possibleDuplicate = false;

    @Column(name = "national_id")
    private Integer nationalId;
    
    @Column(name = "comments", length = 255)
    private String comments;
    
    @Column(name = "identity_verification_status_id")
    private Short identityVerificationStatusId;
    
    public boolean isValidated() {
    	return getTypeId().equals(PatientType.VALIDATED);
    }
}


