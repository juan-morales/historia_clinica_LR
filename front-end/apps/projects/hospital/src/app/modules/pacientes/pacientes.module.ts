import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GuardiaModule } from '@historia-clinica/modules/guardia/guardia.module';
// deps
import { LazyMaterialModule } from '../lazy-material/lazy-material.module';
import { PresentationModule } from '@presentation/presentation.module';
import { InternacionesModule } from "@historia-clinica/modules/ambulatoria/modules/internacion/internaciones.module";
// routing
import { PacientesRoutingModule } from './pacientes-routing.module';
import { EditPatientComponent } from './routes/edit-patient/edit-patient.component';
import { HomeComponent } from './routes/home/home.component';
import { NewPatientComponent } from './routes/new-patient/new-patient.component';
import { NewTemporaryPatientComponent } from './routes/new-temporary-patient/new-temporary-patient.component';
import { ProfileComponent } from './routes/profile/profile.component';
import { SearchComponent } from './routes/search/search.component';
// components
import { AssignedAppointmentComponent } from './component/assigned-appointment/assigned-appointment.component';
import { AssignedAppointmentsComponent } from './component/assigned-appointments/assigned-appointments.component';
import { CardLicenseComponent } from './component/card-license/card-license.component';
import { CardProfessionsComponent } from './component/card-professions/card-professions.component';
import { CardPatientComponent } from './component/card-patient/card-patient.component';
import { CardRolesComponent } from './component/card-roles/card-roles.component';
import { LicenseFormComponent } from './component/license-form/license-form.component';
import { ProfessionSpecialtiesFormComponent } from './component/profession-specialties-form/profession-specialties-form.component';
import { SearchCreateComponent } from './component/search-create/search-create.component';
import { ViewPatientDetailComponent } from './component/view-patient-detail/view-patient-detail.component';
import { ResumenDeGuardiaComponent } from './component/resumen-de-guardia/resumen-de-guardia.component';
import { EditIdentificationNumberComponent } from './dialogs/edit-identification-number/edit-identification-number.component';

// dialogs
import { ArtComponent } from './dialogs/art/art.component';
import { EditLicenseComponent } from './dialogs/edit-license/edit-license.component';
import { EditPrefessionsSpecialtiesComponent } from './dialogs/edit-prefessions-specialties/edit-prefessions-specialties.component';
import { EditRolesComponent } from './dialogs/edit-roles/edit-roles.component';
import { HealthInsuranceComponent } from './dialogs/health-insurance/health-insurance.component';
import { MedicalCoverageComponent } from "./dialogs/medical-coverage/medical-coverage.component";
import { PrivateHealthInsuranceComponent } from './dialogs/private-health-insurance/private-health-insurance.component';
import { ReportsComponent } from './dialogs/reports/reports.component';
import { ScanPatientComponent } from './dialogs/scan-patient/scan-patient.component';
import { MessageForAuditComponent } from './dialogs/message-for-audit/message-for-audit.component';
import { WarningEditIdentificationNumberComponent } from './dialogs/warning-edit-identification-number/warning-edit-identification-number.component';

// pipes
import { ViewNameProfessionAndSpecialtyPipe } from './pipe/view-name-profession-and-specialty.pipe';
import { FilesUploaderComponent } from './component/files-uploader/files-uploader.component';
import { EmergencyCareTemporaryPatientProfile } from './routes/emergency-care-temporary-patient-profile/emergency-care-temporary-patient-profile';

@NgModule({
	declarations: [
		// routing
		EditPatientComponent,
		EditPatientComponent,
		HomeComponent,
		NewPatientComponent,
		NewTemporaryPatientComponent,
		ProfileComponent,
		SearchComponent,
		// components
		AssignedAppointmentComponent,
		AssignedAppointmentsComponent,
		CardLicenseComponent,
		CardProfessionsComponent,
		CardPatientComponent,
		CardRolesComponent,
		LicenseFormComponent,
		ProfessionSpecialtiesFormComponent,
		SearchCreateComponent,
		ViewPatientDetailComponent,
		// dialogs
		ArtComponent,
		EditLicenseComponent,
		EditPrefessionsSpecialtiesComponent,
		EditRolesComponent,
		HealthInsuranceComponent,
		MedicalCoverageComponent,
		PrivateHealthInsuranceComponent,
		ReportsComponent,
		ScanPatientComponent,
		ScanPatientComponent,
		MessageForAuditComponent,
		// pipes
		ViewNameProfessionAndSpecialtyPipe,
		FilesUploaderComponent,
		ResumenDeGuardiaComponent,
		EmergencyCareTemporaryPatientProfile,
  		EditIdentificationNumberComponent,
    	WarningEditIdentificationNumberComponent,
	],
	exports: [
		CardPatientComponent,
		ResumenDeGuardiaComponent,
	],
	imports: [
		FormsModule,
		ReactiveFormsModule,
		// routing
		PacientesRoutingModule,
		// deps
		LazyMaterialModule,
		PresentationModule,
		InternacionesModule,
		GuardiaModule
	]
})
export class PacientesModule {
}
