import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AnthropometricDataDto, EvolutionNoteDto, MasterDataInterface } from '@api-rest/api-model';
import { InternacionMasterDataService } from '@api-rest/services/internacion-master-data.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EvolutionNoteService } from '@api-rest/services/evolution-note.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { getError, hasError } from '@core/utils/form.utils';

@Component({
	selector: 'app-add-anthropometric',
	templateUrl: './add-anthropometric.component.html',
	styleUrls: ['./add-anthropometric.component.scss']
})
export class AddAnthropometricComponent implements OnInit {

	getError = getError;
	hasError = hasError;

	form: FormGroup;
	loading = false;
	bloodTypes: MasterDataInterface<string>[];

	constructor(
		public dialogRef: MatDialogRef<AddAnthropometricComponent>,
		@Inject(MAT_DIALOG_DATA) public data,
		private readonly formBuilder: FormBuilder,
		private readonly internacionMasterDataService: InternacionMasterDataService,
		private readonly evolutionNoteService: EvolutionNoteService,
		private readonly snackBarService: SnackBarService,
	) {
	}

	ngOnInit(): void {
		this.form = this.formBuilder.group({
			bloodType: [null],
			height: [null, [Validators.min(0), Validators.max(1000)]],
			weight: [null, [Validators.min(0), Validators.max(1000)]]
		});

		const bloodTypes$ = this.internacionMasterDataService.getBloodTypes();
		bloodTypes$.subscribe(bloodTypes => this.bloodTypes = bloodTypes);
	}

	submit(): void {
		const evolutionNote: EvolutionNoteDto = this.buildEvolutionNote(this.form.value);
		if (evolutionNote) {
			this.loading = true;
			this.evolutionNoteService.createDocument(evolutionNote, this.data.internmentEpisodeId).subscribe(_ => {
					this.snackBarService.showSuccess('internaciones.internacion-paciente.anthropometric-summary.save.SUCCESS');
					this.dialogRef.close(true);
				}, _ => {
					this.snackBarService.showError('internaciones.internacion-paciente.anthropometric-summary.save.ERROR');
					this.loading = false;
				}
			);
		}
	}

	private buildEvolutionNote(anthropometricDataForm: any): EvolutionNoteDto {
		const anthropometricData: AnthropometricDataDto = isNull(anthropometricDataForm) ? undefined : {
			bloodType: anthropometricDataForm.bloodType ? {
				id: anthropometricDataForm.bloodType.id,
				value: anthropometricDataForm.bloodType.description
			} : undefined,
			height: getValue(anthropometricDataForm.height),
			weight: getValue(anthropometricDataForm.weight),
		};

		return anthropometricData ? { confirmed: true, anthropometricData } : undefined;

		function isNull(formGroupValues: any): boolean {
			return Object.values(formGroupValues).every(el => el === null);
		}

		function getValue(controlValue: any) {
			return controlValue ? {value: controlValue} : undefined;
		}
	}

}
