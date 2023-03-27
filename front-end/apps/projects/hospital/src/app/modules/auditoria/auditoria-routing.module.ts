import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppFeature, ERole } from '@api-rest/api-model';
import { FeatureFlagGuard } from '@core/guards/FeatureFlagGuard';
import { RoleGuard } from '@core/guards/RoleGuard';
import { ControlPatientDuplicateComponent } from './routes/control-patient-duplicate/control-patient-duplicate.component';
import { HomeComponent } from './routes/home/home.component';
import { PatientFusionComponent } from './routes/patient-fusion/patient-fusion.component';


const routes: Routes = [{
	path: '',
	children: [
		{
			path: '',
			component: HomeComponent
		},
		{
			path:"control-pacientes-duplicados",
			component: ControlPatientDuplicateComponent
		},
		{
			path:"fusionar-pacientes",
			component:PatientFusionComponent
		}
	],
	canActivate: [RoleGuard,FeatureFlagGuard],
		data: { allowedRoles: [ERole.AUDITOR_MPI] , featureFlag: AppFeature.HABILITAR_MODULO_AUDITORIA }
}
];

@NgModule({
	imports: [RouterModule.forChild(routes)],
	exports: [RouterModule]
})
export class AuditoriaRoutingModule { }
