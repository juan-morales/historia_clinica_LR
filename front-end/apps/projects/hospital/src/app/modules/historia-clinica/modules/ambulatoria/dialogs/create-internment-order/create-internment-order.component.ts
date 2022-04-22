import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { RequestMasterDataService } from "@api-rest/services/request-masterdata.service";
import { SnomedECL } from "@api-rest/api-model";
import { TemplateOrConceptOption, TemplateOrConceptType } from "@historia-clinica/components/template-concept-typeahead-search/template-concept-typeahead-search.component";
import { InternmentStateService } from "@api-rest/services/internment-state.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { TEXT_AREA_MAX_LENGTH } from '@core/constants/validation-constants';
import { hasError } from '@core/utils/form.utils';
import { OrderStudiesService, Study } from "@historia-clinica/services/order-studies.service";

@Component({
  selector: 'app-create-order',
  templateUrl: './create-internment-order.component.html',
  styleUrls: ['./create-internment-order.component.scss']
})
export class CreateInternmentOrderComponent implements OnInit {

	readonly ecl = SnomedECL.PROCEDURE;
	public readonly TEXT_AREA_MAX_LENGTH = TEXT_AREA_MAX_LENGTH;
	hasError = hasError;

	form: FormGroup;
	firstStepCompleted = false;

	studyCategoryOptions = [];
	healthProblemOptions = [];
	selectedStudy: TemplateOrConceptOption = null;

	orderStudiesService: OrderStudiesService;

  	constructor(
		@Inject(MAT_DIALOG_DATA) public data: { internmentEpisodeId: number },
  		public dialogRef: MatDialogRef<CreateInternmentOrderComponent>,
		private readonly formBuilder: FormBuilder,
		private readonly requestMasterDataService: RequestMasterDataService,
		private readonly internmentStateService: InternmentStateService,
	) {
  		this.orderStudiesService = new OrderStudiesService();
	}

  	ngOnInit(): void {
		this.form = this.formBuilder.group({
			studyCategory: [null, Validators.required],
			studySelection: [null, Validators.required],
			healthProblem: [null, Validators.required],
			notes: [null, [Validators.maxLength(this.TEXT_AREA_MAX_LENGTH)]]
		});

		this.requestMasterDataService.categories().subscribe(categories => {
			this.studyCategoryOptions = categories;
		});

		this.internmentStateService.getDiagnosesGeneralState(this.data.internmentEpisodeId).subscribe(diagnoses => {
			this.healthProblemOptions = diagnoses.map(problem => ({
				id: problem.id,
				main: problem.main,
				description: problem.snomed.pt,
				sctid: problem.snomed.sctid
			}));
			this.setMainDiagnosisAsDefaultHealthProblem();
		})
  	}

  	private setMainDiagnosisAsDefaultHealthProblem() {
		const mainDiagnosis = this.healthProblemOptions.filter((d) => { return d.main }).pop();
		this.form.controls.healthProblem.setValue(mainDiagnosis);
	}

  	handleStudySelected(study) {
  		this.selectedStudy = study;
  		this.form.controls.studySelection.setValue(this.getStudyDisplayName());
	}

	resetStudySelector() {
  		this.selectedStudy = null;
  		this.form.controls.studySelection.setValue(null);
	}

	selectedStudyIsTemplate(): boolean {
  		return this.selectedStudy.type === TemplateOrConceptType.TEMPLATE;
	}

	getStudyDisplayName(): string {
  		return (this.selectedStudyIsTemplate()) ? this.selectedStudy?.data?.description : this.selectedStudy?.data?.pt.term;
	}

	getTemplateIncludedConceptsDisplayText(): string {
  		return this.selectedStudy.data.concepts.map(c => c.pt.term).join(', ');
	}

	goToConfirmationStep() {
  		this.firstStepCompleted = true;
  		this.loadSelectedConceptsIntoOrderStudiesService();
	}

	private loadSelectedConceptsIntoOrderStudiesService() {
		let conceptsToLoad: Study[];
		if (this.selectedStudyIsTemplate()) {
			conceptsToLoad = this.selectedStudy.data.concepts.map(concept => ({
				snomed: {
					sctid: concept.conceptId,
					pt: concept.pt.term
				}
			}));
		}
		else {
			conceptsToLoad = [
				{
					snomed: {
						sctid: this.selectedStudy.data.sctid,
						pt: this.selectedStudy.data.pt.term
					} 
				}
			];
		}
		this.orderStudiesService.addAll(conceptsToLoad);
	}

	getSelectedCategoryDisplayName() {
		return this.studyCategoryOptions.filter((c) => c.id === this.form.controls.studyCategory.value).pop()?.description;
	}

	getSelectedHealthProblemDisplayName() {
		return this.form.controls.healthProblem.value.description;
	}

	getNotesDisplayText() {
		return this.form.controls.notes.value;
	}

}
