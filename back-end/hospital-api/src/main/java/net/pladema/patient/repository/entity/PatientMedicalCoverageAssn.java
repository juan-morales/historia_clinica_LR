package net.pladema.patient.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.pladema.patient.service.domain.PatientMedicalCoverageBo;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "patient_medical_coverage")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class PatientMedicalCoverageAssn implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2516704317374146771L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "medical_coverage_id", nullable = false)
    private Integer medicalCoverageId;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "vigency_date")
    private LocalDate vigencyDate;

    @Column( name = "affiliate_number", length = 25)
    private String affiliateNumber;

    @Column(name = "private_health_insurance_details_id")
    private Integer privateHealthInsuranceDetailsId;

    public PatientMedicalCoverageAssn(PatientMedicalCoverageBo coverageBo, Integer patientId){
        if (coverageBo.getId() != null)
            this.id = coverageBo.getId();
        this.patientId = patientId;
        this.medicalCoverageId = coverageBo.getMedicalCoverage().getId();
        this.vigencyDate = coverageBo.getVigencyDate();
        this.affiliateNumber = coverageBo.getAffiliateNumber();
        if (coverageBo.getPrivateHealthInsuranceDetails() != null)
            this.privateHealthInsuranceDetailsId = coverageBo.getPrivateHealthInsuranceDetails().getId();
    }

}
