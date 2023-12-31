import { Injectable } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { SnomedDto, SnomedECL } from "@api-rest/api-model";
import { SnowstormService } from "@api-rest/services/snowstorm.service";
import { SnomedSemanticSearch, SnomedService } from "@historia-clinica/services/snomed.service";
import { ActionDisplays, TableModel } from "@presentation/components/table/table.component";
import { SnackBarService } from "@presentation/services/snack-bar.service";

@Injectable({
	providedIn: 'root'
})

export class SearchSnomedConceptsParenteralPlanService {

	salineForm: UntypedFormGroup;
	pharmacoForm: UntypedFormGroup;
	salineSnomedConcept: SnomedDto;
	searching = false;
	showToSearchSnomedConcept = false;
	showPharmacoTitle = false;
	snowstormServiceNotAvailable = false;
	conceptsResultsTable: TableModel<any>;

	constructor(
		private readonly formBuilder: UntypedFormBuilder,
		private readonly snowstormService: SnowstormService,
		private readonly snomedService: SnomedService,
		private readonly snackBarService: SnackBarService
	) {
		this.salineForm = this.formBuilder.group({
			snomed: [null, [Validators.required]]
		});
		this.pharmacoForm = this.formBuilder.group({
			pharmaco: this.formBuilder.array([])
		});
	}

	resetAllForms(): void {
		delete this.salineSnomedConcept;
		this.salineForm.reset();
		this.showPharmacoTitle = false;
		this.showToSearchSnomedConcept = false;
		this.pharmacos.clear();
	}

	setSalineSnomedConcept(selectedConcept: SnomedDto): void {
		this.salineSnomedConcept = selectedConcept;
		const pt = selectedConcept ? selectedConcept.pt : '';
		this.salineForm.controls.snomed.setValue(pt);
	}

	private buildConceptsResultsTable(data: SnomedDto[]): TableModel<SnomedDto> {
		return {
			columns: [
				{
					columnDef: '1',
					header: 'Descripción SNOMED',
					text: concept => concept.pt
				},
				{
					columnDef: 'select',
					action: {
						displayType: ActionDisplays.BUTTON,
						display: 'Seleccionar',
						matColor: 'primary',
						do: concept => this.setSalineSnomedConcept(concept)
					}
				},
			],
			data,
			enablePagination: true
		};
	}

	searchSalineConcept(searchValue: string): void {
		if (searchValue) {
			this.searching = true;
			this.snowstormService.getSNOMEDConcepts({ term: searchValue, ecl: SnomedECL.MEDICINE })
				.subscribe(
					results => {
						this.conceptsResultsTable = this.buildConceptsResultsTable(results.items);
						this.searching = false;
					},
					error => {
						this.snackBarService.showError('historia-clinica.snowstorm.CONCEPTS_COULD_NOT_BE_OBTAINED');
						this.snowstormServiceNotAvailable = true;
					}
				);
		}
	}

	searchPharmacoConcept(searchValue: string): void {
		if (searchValue) {
			const search: SnomedSemanticSearch = {
				searchValue,
				eclFilter: SnomedECL.MEDICINE
			};
			this.snomedService.openConceptsSearchDialog(search)
				.subscribe((selectedConcept: SnomedDto) => this.setPharmacoForm(selectedConcept));
		}
	}

	setPharmacoForm(selectedConcept: SnomedDto): void {
		if (selectedConcept) {
			const pharmacoFormArray = this.pharmacoForm.get('pharmaco') as UntypedFormArray;
			pharmacoFormArray.push(this.addPharmaco(selectedConcept));
			this.showToSearchSnomedConcept = false;
		}
	}

	private addPharmaco(selectedConcept: SnomedDto): UntypedFormGroup {
		const pt = selectedConcept ? selectedConcept.pt : '';
		const form = this.formBuilder.group({
			snomed: this.formBuilder.group({
				pt: [pt],
				sctid: [selectedConcept.sctid]
			}),
			dose: [null, [Validators.required]]
		});
		return form;
	}

	removePharmaco(i: number) {
		this.pharmacos.removeAt(i);
		if (!this.pharmacos.controls.length)
			this.showPharmacoTitle = false;
	}

	loadTootip(i: number): string {
		const pharmaco: UntypedFormGroup = <UntypedFormGroup>this.pharmacos.at(i);
		return pharmaco.controls.snomed.value.pt
	}

	get pharmacos(): UntypedFormArray {
		return this.pharmacoForm.get('pharmaco') as UntypedFormArray
	}

	getSnomed(i: number): UntypedFormGroup {
		return <UntypedFormGroup>this.pharmacos.at(i).get('snomed')
	}

	searchPharmacoSnomedConcept() {
		this.showPharmacoTitle = true;
		this.showToSearchSnomedConcept = true;
	}

	setForm(selectedConcept: SnomedDto): void {
		if (selectedConcept) {
			this.salineSnomedConcept = selectedConcept;
			const pt = selectedConcept ? selectedConcept.pt : '';
			this.salineForm.controls.snomed.setValue(pt);
		}
	}

}
