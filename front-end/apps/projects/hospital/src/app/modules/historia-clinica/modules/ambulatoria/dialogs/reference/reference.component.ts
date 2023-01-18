import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AddressDto, CareLineDto, ClinicalSpecialtyDto, DateDto, DepartmentDto, HCEPersonalHistoryDto, InstitutionBasicInfoDto, ReferenceDto, ReferenceProblemDto } from '@api-rest/api-model';
import { AddressMasterDataService } from '@api-rest/services/address-master-data.service';
import { CareLineService } from '@api-rest/services/care-line.service';
import { ClinicalSpecialtyService } from '@api-rest/services/clinical-specialty.service';
import { HceGeneralStateService } from '@api-rest/services/hce-general-state.service';
import { InstitutionService } from '@api-rest/services/institution.service';
import { ContextService } from '@core/services/context.service';
import { TypeaheadOption } from '@presentation/components/typeahead/typeahead.component';
import { forkJoin, Observable, of } from 'rxjs';


const COUNTRY = 14;

@Component({
	selector: 'app-reference',
	templateUrl: './reference.component.html',
	styleUrls: ['./reference.component.scss']
})
export class ReferenceComponent implements OnInit {

	formReference: FormGroup;
	problemsList$: Observable<any[]>;
	problemsList: any[] = [];
	specialties$: Observable<ClinicalSpecialtyDto[]>;
	careLines: CareLineDto[];
	referenceProblemDto: ReferenceProblemDto[] = [];
	selectedFiles: File[] = [];
	selectedFilesShow: any[] = [];
	DEFAULT_RADIO_OPTION = '0';
	provinces: TypeaheadOption<any>[];
	departments: TypeaheadOption<any>[];
	institutions: TypeaheadOption<any>[];
	defaultProvince: TypeaheadOption<any>;
	defaultDepartment: TypeaheadOption<any>;
	defaultInstitution: TypeaheadOption<any>;
	required = true;
	departmentDisable = false;
	submitForm = false;
	originalDestinationDepartment: TypeaheadOption<any>;

	constructor(
		@Inject(MAT_DIALOG_DATA) public data: any,
		private readonly formBuilder: FormBuilder,
		private readonly hceGeneralStateService: HceGeneralStateService,
		private readonly careLineService: CareLineService,
		private readonly dialogRef: MatDialogRef<ReferenceComponent>,
		private readonly institutionService: InstitutionService,
		private readonly clinicalSpecialty: ClinicalSpecialtyService,
		private readonly adressMasterData: AddressMasterDataService,
		private readonly contextService: ContextService
	) { }

	ngOnInit(): void {
		this.createReferenceForm();

		this.setProblems();

		this.disableInputs();

		this.subscribesToChangesInForm();

		this.adressMasterData.getByCountry(COUNTRY).subscribe(provinces => {
			this.provinces = this.setFilterValues(provinces, "description");

			this.institutionService.getAddress(this.contextService.institutionId).subscribe((institutionInfo: AddressDto) => {
				this.loadDefaultInstitutionInformation(institutionInfo);
			});
		})
	}

	private getAllInstitutions() {
		this.institutionService.getAllInstitutions().subscribe((institutions: InstitutionBasicInfoDto[]) => {
			this.institutions = this.institutionsToTypeaheadOption(institutions);
		});
	}

	setFilterValues(response, attribute: string) {
		const opt: TypeaheadOption<any>[] = [];
		response.map(value => {
			opt.push({
				value: value.id,
				compareValue: value[attribute],
				viewValue: value[attribute]
			});
		})
		return opt;
	}

	setProblems() {

		const consultationProblems = this.data.consultationProblems.map(consultationProblem => {
			return {
				hcePersonalHistoryDto: this.buildPersonalHistoryDto(consultationProblem),
				chronic: consultationProblem.cronico,
			}
		});

		consultationProblems.forEach(problem => this.problemsList.push(problem));

		const activeProblems$ = this.hceGeneralStateService.getActiveProblems(this.data.patientId);

		const chronicProblems$ = this.hceGeneralStateService.getChronicConditions(this.data.patientId);

		forkJoin([activeProblems$, chronicProblems$]).subscribe(([activeProblems, chronicProblems]) => {
			const chronicProblemsHCEPersonalHistory = chronicProblems.map(chronicProblem => {
				return {
					hcePersonalHistoryDto: chronicProblem,
					chronic: true,
				}
			});

			const activeProblemsHCEPersonalHistory = activeProblems.map(activeProblem => {
				return {
					hcePersonalHistoryDto: activeProblem,
					chronic: null,
				}
			});

			const problems = [...activeProblemsHCEPersonalHistory, ...chronicProblemsHCEPersonalHistory];
			problems.forEach((problem: HCEPersonalHistory) => {
				const existProblem = this.problemsList.find(consultationProblem => consultationProblem.hcePersonalHistoryDto.snomed.sctid === problem.hcePersonalHistoryDto.snomed.sctid);
				if (!existProblem) {
					this.problemsList.push(problem);
				}
			});
			this.problemsList$ = of(this.problemsList);
		});
	}

	private mapProblems(problems: any[]): ReferenceProblemDto[] {
		return problems.map(problem => ({
			id: problem.id,
			snomed: problem.snomed,
		}));
	}

	get associatedProblemsControls(): FormControl {
		return this.formReference.get('problems') as FormControl;
	}

	setSpecialtyCareLine(): void {
		const careLineId = this.formReference.value.careLine;
		if (careLineId) {
			this.formReference.controls.clinicalSpecialty.enable();
			this.formReference.controls.clinicalSpecialty.setValidators([Validators.required]);
			this.formReference.updateValueAndValidity();
			this.specialties$ = this.clinicalSpecialty.getAllByDestinationInstitution(careLineId, this.formReference.value.institutionDestinationId);
		}
	}


	setProblemsReference(problemsArray: string[]) {
		if (problemsArray.length) {
			this.referenceProblemDto = problemsArray.map(problem => ({
				id: this.problemsList.find(p => p.hcePersonalHistoryDto.snomed.pt === problem).hcePersonalHistoryDto.id,
				snomed: this.problemsList.find(p => p.hcePersonalHistoryDto.snomed.pt === problem).hcePersonalHistoryDto.snomed,
			}));
		}
		else {
			this.referenceProblemDto = [];
		}
		this.setInformation();
	}

	save(): void {
		this.submitForm = true;
		if (this.formReference.valid) {
			const reference = { data: this.buildReference(), files: this.selectedFiles, problems: this.getReferenceProblems() };
			this.dialogRef.close(reference);
		}
	}

	private buildReference(): Reference {
		return {
			careLine: this.formReference.controls.careLine.value,
			clinicalSpecialty: this.formReference.controls.clinicalSpecialty.value,
			consultation: true,
			note: this.formReference.value.summary,
			problems: this.mapProblems(this.referenceProblemDto),
			procedure: false,
			fileIds: [],
			destinationInstitutionId: this.formReference.value.institutionDestinationId,
			phonePrefix: this.formReference.value.phonePrefix,
			phoneNumber: this.formReference.value.phoneNumber
		}
	}

	onSelectFileFormData($event): void {
		Array.from($event.target.files).forEach((file: File) => {
			this.selectedFiles.push(file);
			this.selectedFilesShow.push(file.name);
		});
	}

	removeSelectedFile(index): void {
		this.selectedFiles.splice(index, 1);
		this.selectedFilesShow.splice(index, 1);
	}

	setInformationByInstitution(institutionId: number) {
		this.formReference.controls.institutionDestinationId.setValue(institutionId);
		this.setInformation()
	}

	setInformation() {
		if (!this.formReference.value.institutionDestinationId) {
			this.clearCareLinesAndSpecialties();
			return;
		}
		if (this.formReference.value.searchByCareLine === this.DEFAULT_RADIO_OPTION)
			this.setCareLines();
		else
			this.setSpecialties();
	}

	private getReferenceProblems(): HCEPersonalHistory[] {
		let referenceProblems: HCEPersonalHistory[] = [];
		this.referenceProblemDto.forEach(referenceProblemDto => {
			const problemToAdd = this.problemsList.find(problem => problem.hcePersonalHistoryDto.snomed.sctid === referenceProblemDto.snomed.sctid)
			referenceProblems.push(problemToAdd);

		});
		return referenceProblems;
	}

	private buildPersonalHistoryDto(problem): HCEPersonalHistoryDto {
		return {
			hasPendingReference: false,
			inactivationDate: null,
			severity: problem.codigoSeveridad,
			startDate: (problem.fechaInicio?.day) ? this.mapToString(problem.fechaInicio) : problem.fechaInicio,
			snomed: problem.snomed
		}
	}

	private mapToString(date: DateDto): string {
		return date.year.toString() + date.month.toString() + date.day.toString();

	}

	private setCareLines() {
		const problemSnomedIds: string[] = this.referenceProblemDto.map(problem => problem.snomed.sctid);
		const institutionId = this.formReference.value.institutionDestinationId;
		if (!problemSnomedIds.length || !institutionId) {
			this.formReference.controls.careLine.disable();
		}
		if (problemSnomedIds.length && institutionId) {
			this.formReference.controls.careLine.enable();
			this.formReference.controls.careLine.updateValueAndValidity();
			this.careLineService.getByProblemSnomedIdsAndInstitutionId(institutionId, problemSnomedIds).subscribe(careLines => this.careLines = careLines);
		}
		this.formReference.controls.clinicalSpecialty.disable();
	}

	private setSpecialties() {
		const institutionId = this.formReference.value.institutionDestinationId;
		if (institutionId) {
			this.formReference.controls.clinicalSpecialty.enable();
			this.specialties$ = this.clinicalSpecialty.getClinicalSpecialtyByInstitution(institutionId);
			this.formReference.controls.clinicalSpecialty.updateValueAndValidity();
		}
	}

	private subscribesToChangesInForm() {
		this.formReference.controls.searchByCareLine.valueChanges.subscribe(option => {
			if (option === this.DEFAULT_RADIO_OPTION) {
				this.formReference.controls.careLine.setValidators([Validators.required]);
				this.formReference.controls.clinicalSpecialty.setValue(null);
				this.formReference.controls.clinicalSpecialty.disable();
				this.formReference.controls.clinicalSpecialty.updateValueAndValidity();
				this.setCareLines();
			} else {
				this.formReference.controls.careLine.removeValidators([Validators.required]);
				this.formReference.controls.careLine.setValue(null);
				this.formReference.controls.careLine.disable();
				this.formReference.controls.careLine.updateValueAndValidity();
				this.setSpecialties();
			}
		});
	}

	setInfoByProvince(province: number) {
		this.clearInformation();
		this.setDepartmentsByProvince(province);
		this.formReference.controls.provinceId.setValue(province);
	}

	private setDepartmentsByProvince(province: number) {
		if (province) {
			this.adressMasterData.getDepartmentsByProvince(province).subscribe(response => {
				if (response) {
					const options: TypeaheadOption<any>[] = this.setFilterValues(response, "description");
					this.departments = options;
					this.departmentDisable = false;
					this.updateDepartmentValue();
				};
			})
		} else {
			this.departmentDisable = true;
		}
	}

	private updateDepartmentValue() {
		if (this.originalDestinationDepartment){
			this.defaultDepartment = this.originalDestinationDepartment;
			this.originalDestinationDepartment = null;
		} else {
			this.defaultDepartment = this.clearTypeahead();
		}
	}

	setInstitutionsByProvince(province: number) {
		this.institutionService.findByProvinceId(province).subscribe((institutions: InstitutionBasicInfoDto[]) => {
			this.institutions = this.institutionsToTypeaheadOption(institutions);
		});
	}

	private clearTypeahead() {
		return { value: null, viewValue: null, compareValue: null }
	}

	filterInstitutionsByDepartment(department: number) {
		this.formReference.controls.departmentId.setValue(department);
		this.defaultInstitution = this.clearTypeahead();
		if (department) {
			this.clearCareLinesAndSpecialties();
			this.institutionService.findByDepartmentId(department).subscribe((institutions: InstitutionBasicInfoDto[]) =>{
				this.institutions = this.institutionsToTypeaheadOption(institutions);
			});
		} else {
			if (this.formReference.value.provinceId) {
				this.setInstitutionsByProvince(this.formReference.value.provinceId);
			} else {
				this.getAllInstitutions();
			}
		}
	}

	private institutionsToTypeaheadOption(institutions: InstitutionBasicInfoDto[]): TypeaheadOption<any>[]{
		if (institutions) {
			const options: TypeaheadOption<any>[] = this.setFilterValues(institutions, "name");
			return options;
		}
	}

	private clearInformation() {
		this.defaultDepartment = this.clearTypeahead();
		this.clearCareLinesAndSpecialties();
	}

	private clearCareLinesAndSpecialties() {
		if (this.careLines?.length) {
			this.careLines = [];
			this.formReference.controls.careLine.setValue(null);
			this.formReference.controls.careLine.updateValueAndValidity();
		}
		this.specialties$?.subscribe(specialties => {
			if (specialties.length) {
				this.formReference.controls.clinicalSpecialty.setValue(null);
				this.formReference.controls.clinicalSpecialty.updateValueAndValidity();
			}
		});

		disableInputs(this.formReference, this.referenceProblemDto);

		function disableInputs(formReference: FormGroup, referenceProblemDto: ReferenceProblemDto[]) {
			if (!formReference.value.institutionDestinationId) {
				formReference.controls.clinicalSpecialty.disable();
			}
			if (!referenceProblemDto.length || !formReference.value.institutionDestinationId) {
				formReference.controls.careLine.disable();
			}
		}
	}

	private loadDefaultInstitutionInformation(institutionInfo: AddressDto) {
		if (institutionInfo.provinceId) {
			this.setProvince(institutionInfo.provinceId);
			this.adressMasterData.getDepartmentById(institutionInfo.departmentId).subscribe((department: DepartmentDto) => {
				this.formReference.controls.departmentOrigin.setValue(department.description);
				this.setDefaultDestinationDepartment(department);
				if (department.id) {
					this.institutionService.findByDepartmentId(department.id).subscribe((institutions: InstitutionBasicInfoDto[]) =>
						this.setInstitutionOrigin(institutions)
					);
				}
			});
		}
		else {
			if (institutionInfo.departmentId) {
				const information$: Observable<any>[] = [];
				information$.push(this.adressMasterData.getDepartmentById(institutionInfo.departmentId));
				information$.push(this.institutionService.findByDepartmentId(institutionInfo.departmentId));
				forkJoin(information$).subscribe(info => {
					if (info[0].provinceId) {
						this.setProvince(info[0].provinceId);
					}
					this.formReference.controls.departmentOrigin.setValue(info[0].description);
					this.setDefaultDestinationDepartment(info[0]);
					this.setInstitutionOrigin(info[1]);
				});
			}
		}
	}

	private setDefaultDestinationDepartment(department: DepartmentDto) {
		if (department) {
			this.formReference.controls.departmentId.setValue(department.id);
			this.originalDestinationDepartment = { value: department.id, viewValue: department.description, compareValue: department.description };
		}
	}

	private createReferenceForm() {
		this.formReference = this.formBuilder.group({
			problems: [null, [Validators.required]],
			searchByCareLine: [this.DEFAULT_RADIO_OPTION],
			provinceId: [null],
			departmentId: [null],
			consultation: [null],
			procedure: [null],
			careLine: [null, [Validators.required]],
			clinicalSpecialty: [null, [Validators.required]],
			institutionDestinationId: [null, [Validators.required]],
			summary: [null],
			provinceOrigin: [null],
			departmentOrigin: [null],
			institutionOrigin: [null],
			phoneNumber: [null, [Validators.required, Validators.maxLength(20)]],
			phonePrefix: [null, [Validators.required, Validators.maxLength(10)]]
		});
	}

	private disableInputs() {
		this.formReference.controls.clinicalSpecialtyId.disable();
		this.formReference.controls.procedure.disable();
		this.formReference.controls.careLine.disable();
		this.formReference.controls.provinceOrigin.disable();
		this.formReference.controls.departmentOrigin.disable();
		this.formReference.controls.institutionOrigin.disable();
	}

	private setProvince(provinceId: number) {
		const provinceOrigin = this.provinces.find(p => p.value === provinceId);
		this.defaultProvince = { value: provinceOrigin.value, compareValue: provinceOrigin.compareValue, viewValue: provinceOrigin.viewValue }
		this.formReference.controls.provinceOrigin.setValue(provinceOrigin.viewValue);
	}

	private setInstitutionOrigin(institutions: InstitutionBasicInfoDto[]) {
		const institution = institutions.find((i: InstitutionBasicInfoDto) => i.id === this.contextService.institutionId);
		this.formReference.controls.institutionOrigin.setValue(institution.name);
	}

}

export interface HCEPersonalHistory {
	hcePersonalHistoryDto: HCEPersonalHistoryDto;
	chronic: boolean
}

export interface Reference {
	careLine: CareLineDto,
	clinicalSpecialty: ClinicalSpecialtyDto,
	consultation: boolean;
	destinationInstitutionId: number;
	fileIds: number[];
	note?: string;
	problems: ReferenceProblemDto[];
	procedure?: boolean;
	phoneNumber: string;
	phonePrefix: string;
}
