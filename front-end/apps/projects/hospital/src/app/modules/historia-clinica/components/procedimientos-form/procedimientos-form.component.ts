import { Component, forwardRef } from '@angular/core';
import { FormBuilder, NG_VALUE_ACCESSOR } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { AppFeature } from '@api-rest/api-model';
import { FeatureFlagService } from '@core/services/feature-flag.service';
import { NewConsultationProcedureFormComponent } from '@historia-clinica/dialogs/new-consultation-procedure-form/new-consultation-procedure-form.component';
import { ProcedimientosService } from '@historia-clinica/services/procedimientos.service';
import { SnomedService } from '@historia-clinica/services/snomed.service';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { Subscription } from 'rxjs';

@Component({
	selector: 'app-procedimientos-form',
	templateUrl: './procedimientos-form.component.html',
	styleUrls: ['./procedimientos-form.component.scss'],
	providers: [
		{
			provide: NG_VALUE_ACCESSOR,
			useExisting: forwardRef(() => ProcedimientosFormComponent),
			multi: true,
		}
	]
})
export class ProcedimientosFormComponent{

	procedimientos = this.formBuilder.group({data: []});
	onChangeSub: Subscription;
	searchConceptsLocallyFFIsOn = false;

	procedimientoNuevaConsultaService = new ProcedimientosService(this.formBuilder, this.snomedService, this.snackBarService);
	constructor(
		private readonly dialog: MatDialog,
		private readonly formBuilder: FormBuilder,
		private readonly snomedService: SnomedService,
		private readonly snackBarService: SnackBarService,
		private readonly featureFlagService: FeatureFlagService,
	) {
		this.featureFlagService.isActive(AppFeature.HABILITAR_BUSQUEDA_LOCAL_CONCEPTOS).subscribe(isOn => {
			this.searchConceptsLocallyFFIsOn = isOn;
		});

		this.procedimientoNuevaConsultaService.procedimientos$.subscribe(r => this.writeValue({data: r}))
	}

	addProcedure(): void {
		this.dialog.open(NewConsultationProcedureFormComponent, {
			data: {
				procedureService: this.procedimientoNuevaConsultaService,
				searchConceptsLocallyFF: this.searchConceptsLocallyFFIsOn,
			},
			autoFocus: false,
			width: '35%',
			disableClose: true,
		});
	}


	onTouched = () => { };

	writeValue(obj: any): void {
		if (obj)
			this.procedimientos.setValue(obj);
	}

	registerOnChange(fn: any): void {
		this.onChangeSub = this.procedimientos.valueChanges
			.subscribe(value => {
				const toEmit = this.procedimientos.valid ? value : null;
				fn(toEmit);
			})
	}

	registerOnTouched(fn: any): void {
		this.onTouched = fn;
	}

	setDisabledState?(isDisabled: boolean): void {
		isDisabled ? this.procedimientos.disable() : this.procedimientos.enable();
	}

	ngOnDestroy(): void {
		this.onChangeSub.unsubscribe();
	}
}
