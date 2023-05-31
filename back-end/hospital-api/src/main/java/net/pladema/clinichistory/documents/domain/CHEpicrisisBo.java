package net.pladema.clinichistory.documents.domain;

import com.google.common.base.Joiner;

import net.pladema.clinichistory.documents.infrastructure.output.repository.entity.VClinicHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CHEpicrisisBo extends CHDocumentBo{

	private String problems;
	private String notes;
	private String personalRecord;
	private String familyRecord;
	private String medicines;
	private String vaccines;
	private String otherCircumstances;
	private String externalCause;
	private String obstetricEvent;

	public CHEpicrisisBo (VClinicHistory entity, ECHEncounterType encounterType, ECHDocumentType documentType){
		super(entity, encounterType, documentType);
		this.problems = entity.getHealthConditionSummary().getProblems();
		this.notes = entity.getHealthConditionSummary().getNotes();
		this.personalRecord = entity.getHealthConditionSummary().getPersonalRecord();
		this.familyRecord = entity.getHealthConditionSummary().getFamilyRecord();
		this.medicines = entity.getHealthConditionSummary().getMedicines();
		this.vaccines = entity.getHealthConditionSummary().getVaccines();
		this.otherCircumstances = entity.getHealthConditionSummary().getEpicrisisOtherCircumstances();
		this.externalCause = entity.getHealthConditionSummary().getEpicrisisExternalCause();
		this.obstetricEvent = entity.getHealthConditionSummary().getEpicrisisObstetricEvent();
	}

	@Override
	public List<ClinicalRecordBo> getClinicalRecords() {
		List<String> terms = Stream.of(problems, notes, personalRecord, familyRecord, medicines, vaccines, otherCircumstances, externalCause, obstetricEvent).filter(term -> term!=null && !term.isBlank()).collect(Collectors.toList());
		List<ClinicalRecordBo> result = new ArrayList<>();
		if(!terms.isEmpty()) {
			String evolution = Joiner.on(". <br />").join(terms);
			result.add(new ClinicalRecordBo("Epicrisis", evolution.replace("|(", " (").replace('|', ',').replace("\\n", "<br />")));
		}
		return result;
	}


}
