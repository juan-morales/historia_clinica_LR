import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
// deps
import { HistoriaClinicaModule } from '../../historia-clinica.module';
import { PresentationModule } from '@presentation/presentation.module';
// routing
import { InternacionesRoutingModule } from './internaciones-routing.module';
import { AnamnesisComponent } from './routes/anamnesis/anamnesis.component';
import { CambiarDiagnosticoPrincipalComponent } from './routes/cambiar-diagnostico-principal/cambiar-diagnostico-principal.component';
import { EpicrisisComponent } from './routes/epicrisis/epicrisis.component';
import { EvaluacionClinicaDiagnosticosComponent } from './routes/evaluacion-clinica-diagnosticos/evaluacion-clinica-diagnosticos.component';
import { InternacionesHomeComponent } from './routes/home/internaciones-home.component';
import { InternacionPacienteComponent } from './routes/internacion-paciente/internacion-paciente.component';
import { NewInternmentComponent } from './routes/new-internment/new-internment.component';
import { NotaEvolucionComponent } from './routes/nota-evolucion/nota-evolucion.component';
import { PatientBedRelocationComponent } from './routes/patient-bed-relocation/patient-bed-relocation.component';
import { PatientDischargeComponent } from './routes/patient-discharge/patient-discharge.component';
// components
import { AlergiasComponent } from './components/alergias/alergias.component';
import { AnamnesisFormComponent } from './components/anamnesis-form/anamnesis-form.component';
import { AntecedentesFamiliaresComponent } from './components/antecedentes-familiares/antecedentes-familiares.component';
import { AntecedentesPersonalesComponent } from './components/antecedentes-personales/antecedentes-personales.component';
import { DiagnosticoPrincipalComponent } from './components/diagnostico-principal/diagnostico-principal.component';
import { DiagnosticosComponent } from './components/diagnosticos/diagnosticos.component';
import { EpicrisisFormComponent } from './components/epicrisis-form/epicrisis-form.component';
import { InternacionesTableComponent } from './components/internaciones-table/internaciones-table.component';
import { MedicacionComponent } from './components/medicacion/medicacion.component';
import { MedicalDischargeComponent } from './components/medical-discharge/medical-discharge.component';
import { NotaEvolucionFormComponent } from './components/nota-evolucion-form/nota-evolucion-form.component';
import { VacunasComponent } from './components/vacunas/vacunas.component';

@NgModule({
	declarations: [
		// routing
		AnamnesisComponent,
		CambiarDiagnosticoPrincipalComponent,
		EpicrisisComponent,
		EvaluacionClinicaDiagnosticosComponent,
		InternacionesHomeComponent,
		InternacionPacienteComponent,
		NewInternmentComponent,
		NotaEvolucionComponent,
		PatientBedRelocationComponent,
		PatientDischargeComponent,
		// components
		AlergiasComponent,
		AnamnesisFormComponent,
		AntecedentesFamiliaresComponent,
		AntecedentesPersonalesComponent,
		DiagnosticoPrincipalComponent,
		DiagnosticosComponent,
		EpicrisisFormComponent,
		InternacionesTableComponent,
		MedicacionComponent,
		MedicalDischargeComponent,
		NotaEvolucionFormComponent,
		VacunasComponent,
	],
	imports: [
		CommonModule,
		FormsModule,
		// routing
		InternacionesRoutingModule,
		// deps
		HistoriaClinicaModule,
		PresentationModule,
	]
})
export class InternacionesModule { }
