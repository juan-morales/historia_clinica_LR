import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { InternacionesRoutingModule } from './internaciones-routing.module';
import { AppMaterialModule } from 'src/app/app.material.module';
import { CoreModule } from '@core/core.module';
import { PresentationModule } from '../presentation/presentation.module';
import { AnamnesisFormComponent } from './components/anamnesis-form/anamnesis-form.component';
import { InternacionesHomeComponent } from './routes/home/internaciones-home.component';
import { InternacionesTableComponent } from './components/internaciones-table/internaciones-table.component';
import { InternacionPacienteComponent } from './routes/internacion-paciente/internacion-paciente.component';
import { AnamnesisComponent } from './routes/anamnesis/anamnesis.component';
import { ConceptsSearchDialogComponent } from './dialogs/concepts-search-dialog/concepts-search-dialog.component';
import { ConceptsSearchComponent } from './components/concepts-search/concepts-search.component';
import { FormsModule } from '@angular/forms';
import { DiagnosticosComponent } from './components/diagnosticos/diagnosticos.component';
import { AlergiasComponent } from './components/alergias/alergias.component';
import { VacunasComponent } from './components/vacunas/vacunas.component';
import { MedicacionComponent } from './components/medicacion/medicacion.component';
import { AntecedentesFamiliaresComponent } from './components/antecedentes-familiares/antecedentes-familiares.component';
import { AntecedentesPersonalesComponent } from './components/antecedentes-personales/antecedentes-personales.component';

@NgModule({
	declarations: [
		AnamnesisFormComponent,
		InternacionesTableComponent,
		InternacionesHomeComponent,
		InternacionPacienteComponent,
		AnamnesisComponent,
		AntecedentesPersonalesComponent,
		ConceptsSearchDialogComponent,
		ConceptsSearchComponent,
		DiagnosticosComponent,
		AlergiasComponent,
		VacunasComponent,
		MedicacionComponent,
		AntecedentesFamiliaresComponent,
	],
	imports: [
		AppMaterialModule,
		CoreModule,
		CommonModule,
		FormsModule,
		PresentationModule,
		InternacionesRoutingModule,
	]
})
export class InternacionesModule { }
