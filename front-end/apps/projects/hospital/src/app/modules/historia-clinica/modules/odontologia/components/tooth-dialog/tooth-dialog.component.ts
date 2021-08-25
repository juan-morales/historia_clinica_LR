import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { OdontologyConceptDto, ToothDto, ToothSurfacesDto } from '@api-rest/api-model';
import { OdontogramService as OdontogramRestService } from '../../api-rest/odontogram.service';
import { ProcedureOrder, ToothAction, ActionType } from '../../services/actions.service';
import { ConceptsFacadeService } from '../../services/concepts-facade.service';
import { ToothTreatment } from '../../services/surface-drawer.service';
import { getSurfaceShortName } from '../../utils/surfaces';
import { Actions, ToothComponent } from '../tooth/tooth.component';

@Component({
	selector: 'app-tooth-dialog',
	templateUrl: './tooth-dialog.component.html',
	styleUrls: ['./tooth-dialog.component.scss'],
})
export class ToothDialogComponent implements OnInit, AfterViewInit {

	@ViewChild('tooth') toothComponent: ToothComponent;

	constructor(
		private readonly formBuilder: FormBuilder,
		private readonly odontogramRestService: OdontogramRestService,
		@Inject(MAT_DIALOG_DATA) public data: { tooth: ToothDto, quadrantCode: number, currentActions: ToothAction[], records: ToothAction[] },
		private dialogRef: MatDialogRef<ToothDialogComponent>,
		private conceptsFacadeService: ConceptsFacadeService
	) {

	}

	readonly toothTreatment = ToothTreatment.AS_FRACTIONAL_TOOTH;

	form: FormGroup;
	newHallazgoId: number;

	selectedSurfacesText: string;

	selectedSurfaces: string[] = [];

	private surfacesDto: ToothSurfacesDto;

	private diagnostics: OdontologyConceptDto[];
	filteredDiagnostics: OdontologyConceptDto[];

	private procedures: OdontologyConceptDto[];
	filteredProcedures: OdontologyConceptDto[];

	firstProcedureId: string;
	secondProcedureId: string;
	thirdProcedureId: string;

	ngAfterViewInit(): void {
		this.toothComponent.setFindingsAndProcedures(this.data.currentActions);
		this.toothComponent.actionsSubject$.subscribe(actionsSctids => {
			this.reciveToothCurrentViewActions(actionsSctids)
		});

		if (this.data.records.length) {
			this.toothComponent.setRecords(this.data.records);
		}
	}

	ngOnInit(): void {

		this.form = this.formBuilder.group(
			{
				findingId: [undefined],
				procedures: this.formBuilder.group({
					firstProcedureId: [undefined],
					secondProcedureId: [undefined],
					thirdProcedureId: [undefined],
				})
			}
		);

		this.odontogramRestService.getToothSurfaces(this.data.tooth.snomed.sctid).subscribe(surfaces => this.surfacesDto = surfaces);

		const filterFunction = this.getFilterFuction(false);
		this.conceptsFacadeService.getDiagnostics$().subscribe(
			diagnostics => {
				this.diagnostics = diagnostics;
				this.setAppropiateFindings(filterFunction);
			}
		);
		this.conceptsFacadeService.getProcedures$().subscribe(
			procedures => {
				this.procedures = procedures;
				this.setAppropiateProcedures(filterFunction);
			}
		);
	}

	confirm() {
		this.dialogRef.close(this.toothComponent.getFindingsAndProcedures());
	}

	reciveSelectedSurfaces(surfaces: string[]) {
		this.selectedSurfaces = surfaces;
		this.concatNames();
		this.setAppropiateActions(surfaces.length != 0)
	}

	private setAppropiateActions(surfacesSelected: boolean) {

		const filterFuncion = this.getFilterFuction(surfacesSelected);

		this.setAppropiateFindings(filterFuncion);
		this.setAppropiateProcedures(filterFuncion);
	}

	private getFilterFuction(surfacesSelected: boolean): Function {
		let filterFuncion = (diagnostic: OdontologyConceptDto) => { return diagnostic.applicableToTooth }
		if (surfacesSelected) {
			filterFuncion = (diagnostic: OdontologyConceptDto) => { return diagnostic.applicableToSurface }
		}
		return filterFuncion;
	}

	private setAppropiateFindings(filterFuncion) {
		this.filteredDiagnostics = this.diagnostics?.filter(filterFuncion);
	}

	private setAppropiateProcedures(filterFuncion) {
		this.filteredProcedures = this.procedures?.filter(filterFuncion);
	}

	findingChanged(hallazgoId) {
		this.newHallazgoId = hallazgoId.value;
	}

	private reciveToothCurrentViewActions(actions: Actions) {
		if (actions?.findingId) {
			this.form.controls.findingId.patchValue(actions.findingId);
		} else {
			this.form.controls.findingId.patchValue(undefined);
			this.newHallazgoId = undefined;
		}
		const procedures = {
			firstProcedureId: actions.procedures?.firstProcedureId,
			secondProcedureId: actions.procedures?.secondProcedureId,
			thirdProcedureId: actions.procedures?.thirdProcedureId
		};
		this.form.patchValue({
			procedures
		});
	}

	private concatNames() {
		this.selectedSurfacesText = '';
		if (this.selectedSurfaces.length) {
			this.selectedSurfacesText = this.selectedSurfaces.length === 1 ? 'Cara ' : 'Caras ';
			const mappedNames = this.selectedSurfaces.map(surface => this.findSutableName(surface));
			this.selectedSurfacesText += mappedNames.filter(Boolean).join(', ');
		}
	}

	private findSutableName(surface: string): string {
		const sctid = this.surfacesDto[surface].sctid;
		return getSurfaceShortName(sctid);
	}

	firstProcedureChanged() {
		this.firstProcedureId = this.form.value.procedures.firstProcedureId;
	}

	secondProcedureChanged() {
		this.secondProcedureId = this.form.value.procedures.secondProcedureId;
	}

	thirdProcedureChanged() {
		this.thirdProcedureId = this.form.value.procedures.thirdProcedureId;
	}

}

