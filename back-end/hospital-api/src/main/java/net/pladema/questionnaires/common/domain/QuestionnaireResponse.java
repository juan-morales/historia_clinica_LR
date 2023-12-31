package net.pladema.questionnaires.common.domain;

import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ar.lamansys.sgx.shared.auditable.entity.SGXAuditableEntity;
import ar.lamansys.sgx.shared.auditable.listener.SGXAuditListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "minsal_lr_questionnaire_response")
@EntityListeners(SGXAuditListener.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class QuestionnaireResponse extends SGXAuditableEntity<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "questionnaire_id", nullable = false)
	private Integer questionnaireId;

	@Column(name = "patient_id", nullable = false)
	private Integer patientId;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "questionnaire_response_id", nullable = false)
	private List<Answer> answers;

	@Column(name = "status_id")
	private Integer statusId;

	public QuestionnaireResponse(Optional<QuestionnaireResponse> byId) {
		this.id = byId.get().id;
		this.questionnaireId = byId.get().questionnaireId;
		this.patientId = byId.get().patientId;
	}

}
