package net.pladema.questionnaires.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "minsal_lr_answer")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "item_id")
	private Integer itemId;

	@Column(name = "questionnaire_response_id", insertable = false, updatable = false)
	private Integer questionnaireResponseId;

	@Column(name = "option_id")
	private Integer answerId;

	@Column(name = "value")
	private String value;

	public Answer(Integer id, Integer itemId, Integer questionnaireResponseId, Integer answerId) {
		this.id = id;
		this.itemId = itemId;
		this.questionnaireResponseId = questionnaireResponseId;
		this.answerId = answerId;
	}
}
