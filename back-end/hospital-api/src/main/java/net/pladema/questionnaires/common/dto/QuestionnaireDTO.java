package net.pladema.questionnaires.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionnaireDTO {

	private Integer questionnaireId;

	List<QuestionnaireAnswers> answers;

	public QuestionnaireDTO(List<QuestionnaireAnswers> lstQuestionnaire){
		this.answers = lstQuestionnaire;
	}
}
