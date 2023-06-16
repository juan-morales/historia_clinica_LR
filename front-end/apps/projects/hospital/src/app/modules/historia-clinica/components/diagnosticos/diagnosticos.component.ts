import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DiagnosisDto, HealthConditionDto } from '@api-rest/api-model';
import { HEALTH_CLINICAL_STATUS } from '@historia-clinica/modules/ambulatoria/modules/internacion/constants/ids';
import { ComponentEvaluationManagerService } from '@historia-clinica/modules/ambulatoria/services/component-evaluation-manager.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { HEALTH_VERIFICATIONS } from '../../modules/ambulatoria/modules/internacion/constants/ids';
import { DiagnosisCreationEditionComponent } from '../../modules/ambulatoria/modules/internacion/dialogs/diagnosis-creation-edition/diagnosis-creation-edition.component';
import { SelectMainDiagnosisComponent } from '../../modules/ambulatoria/modules/internacion/dialogs/select-main-diagnosis/select-main-diagnosis.component';

@Component({
	selector: 'app-diagnosticos',
	templateUrl: './diagnosticos.component.html',
	styleUrls: ['./diagnosticos.component.scss'],
	providers: []
})
export class DiagnosticosComponent {
	@Input() showTitle = false;
	@Output() diagnosisChange = new EventEmitter();
	@Output() mainDiagnosisChange = new EventEmitter();

	@Input()
	diagnosticos: DiagnosisDto[] = [];
	_mainDiagnosis: HealthConditionDto;

	@Input()
	set mainDiagnosis(newMainDiagnosis: HealthConditionDto) {
		this._mainDiagnosis = newMainDiagnosis;
		this.mainDiagnosisChange.emit(this._mainDiagnosis)
	}
	@Input()
	type: string;

	CONFIRMED = HEALTH_VERIFICATIONS.CONFIRMADO;
	ACTIVE = HEALTH_CLINICAL_STATUS.ACTIVO;

	constructor(
		public dialog: MatDialog,
		private snackBarService: SnackBarService,
		private readonly componentEvaluationManagerService: ComponentEvaluationManagerService,

	) { }

	openCreationDialog(isMainDiagnosis: boolean) {
		const dialogRef = this.dialog.open(DiagnosisCreationEditionComponent, {
			width: '450px',
			data: {
				type: 'CREATION',
				isMainDiagnosis: isMainDiagnosis
			}
		});

		dialogRef.afterClosed().subscribe(diagnosis => {
			if (diagnosis) {
				if (!this.diagnosticos.find(currentDiagnosis => currentDiagnosis.snomed.pt === diagnosis.snomed.pt) && diagnosis.snomed.pt != this._mainDiagnosis?.snomed.pt) {

					if (isMainDiagnosis) {
						this.componentEvaluationManagerService.mainDiagnosis = diagnosis;
						diagnosis.presumptive = false;
						diagnosis.verificationId = this.CONFIRMED;
						diagnosis.statusId = this.ACTIVE;
						this.mainDiagnosis = diagnosis;
					}
					else {
						this.diagnosticos.push(diagnosis);
						this.componentEvaluationManagerService.diagnosis = this.diagnosticos;
						this.diagnosisChange.emit(this.diagnosticos);
					}
				}
				else
					this.snackBarService.showError('internaciones.anamnesis.diagnosticos.messages.ERROR');
			}
		});
	}

	openModifyMainDiagnosisDialog() {
		const dialogRef = this.dialog.open(SelectMainDiagnosisComponent, {
			width: '450px',
			data: {
				currentMainDiagnosis: this._mainDiagnosis,
				otherDiagnoses: this.diagnosticos.filter(d => d.statusId === this.ACTIVE)
			}
		});

		dialogRef.afterClosed().subscribe(potentialNewMainDiagnosis => {
			if (potentialNewMainDiagnosis) {
				if (potentialNewMainDiagnosis != this._mainDiagnosis) {
					this.componentEvaluationManagerService.mainDiagnosis = potentialNewMainDiagnosis;
					let oldMainDiagnosis = this._mainDiagnosis;
					this.diagnosticos.push(oldMainDiagnosis);
					this.diagnosticos.splice(this.diagnosticos.indexOf(potentialNewMainDiagnosis), 1);
					this.mainDiagnosis = potentialNewMainDiagnosis;
					this._mainDiagnosis.isAdded = true;
					this._mainDiagnosis.verificationId = this.CONFIRMED;
					this._mainDiagnosis.statusId = this.ACTIVE;
					(<DiagnosisDto>this._mainDiagnosis).presumptive = false;
				}
			}
		});
	}

	removeDiagnosis(event: any) {
		const index = this.diagnosticos.indexOf(event);
		if (index !== -1) {
			this.diagnosticos.splice(index, 1);
			this.componentEvaluationManagerService.diagnosis = this.diagnosticos;
		}
	}
}
