import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MedicationDto, SnomedECL } from '@api-rest/api-model';
import { pushTo, removeFrom } from '@core/utils/array.utils';
import { SearchSnomedConceptComponent } from '@historia-clinica/modules/ambulatoria/dialogs/search-snomed-concept/search-snomed-concept.component';
import { ComponentEvaluationManagerService } from '@historia-clinica/modules/ambulatoria/services/component-evaluation-manager.service';
import { FormMedicationComponent } from '../../dialogs/form-medication/form-medication.component';

@Component({
  selector: 'app-medication',
  templateUrl: './medication.component.html',
  styleUrls: ['./medication.component.scss']
})
export class MedicationComponent  {
	@Output() medicationsChange = new EventEmitter();
	@Input() medications: MedicationDto[] = [];
	@Input() hideSuspended: boolean;


	constructor(
		private readonly componentEvaluationManagerService: ComponentEvaluationManagerService,
		private readonly dialog: MatDialog,

	) {	}

	addToList(medicacion: MedicationDto) {
		if (medicacion) {
			this.add(medicacion);
		}
	}


	add(medicacion: MedicationDto) {
		this.medications = pushTo<MedicationDto>(this.medications, medicacion);
		this.componentEvaluationManagerService.medications = this.medications;
		this.medicationsChange.next(this.medications);
	}

	remove(index: number) {
		this.medications = removeFrom<MedicationDto>(this.medications, index);
		this.componentEvaluationManagerService.medications = this.medications;
		this.medicationsChange.next(this.medications);
	}

	addMedication() {
		const dialogConfig = new MatDialogConfig();
		dialogConfig.width = '35%';
		dialogConfig.disableClose = false;
		dialogConfig.data = {
			label:'historia-clinica.new-consultation-medication-form.CONCEPT_LABEL',
			title: 'ambulatoria.paciente.nueva-consulta.medicaciones.ADD',
			eclFilter: SnomedECL.MEDICINE
		};

		const dialogRef = this.dialog.open(SearchSnomedConceptComponent, dialogConfig);

		dialogRef.afterClosed().subscribe(snomedConcept => {
			if (snomedConcept) {
				const dialog = this.dialog.open(FormMedicationComponent, {
					data: {
						title: 'ambulatoria.paciente.nueva-consulta.medicaciones.ADD',
						hideSuspended: this.hideSuspended,
						snomedConcept: snomedConcept
					}
				});
				dialog.afterClosed().subscribe(medicacion => {
					if (snomedConcept)
						this.addToList(medicacion)
				});
			}
		});
	}

}
