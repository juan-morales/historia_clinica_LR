import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { CubejsClientModule } from '@cubejs-client/ngx';
// deps
import { PresentationModule } from '@presentation/presentation.module';
// routing
import { InstitutionExtensionComponent, SystemExtensionComponent } from './routes/extension/extension.component';
// components
import { CubejsChartComponent } from './components/cubejs-chart/cubejs-chart.component';
import { CubejsDashboardComponent } from './components/cubejs-dashboard/cubejs-dashboard.component';
import { DateOnlyIsoFormInputComponent } from './components/date-only-iso-form-input/date-only-iso-form-input.component';
import { JsonComponent } from './components/json/json.component';
import { PageLayoutComponent } from './components/page-layout/page-layout.component';
import { QueryRendererComponent } from './components/query-renderer/query-renderer.component';
import { TabsComponent } from './components/tabs/tabs.component';
import { UiCardComponent } from './components/ui-card/ui-card.component';
import { UiComponentComponent } from './components/ui-component/ui-component.component';
import { UiComponentListComponent } from './components/ui-component-list/ui-component-list.component';
import { UiExternalComponentComponent } from './components/ui-external-component/ui-external-component.component';
// config options
import { cubejsOptions } from './extensions-cubejs';
import { RoutedExternalComponent } from './components/routed-external/routed-external.component';
import { SpecialtyFormInputComponent } from './components/specialty-form-input/specialty-form-input.component';
import { ProfessionalFormInputComponent } from './components/professional-form-input/professional-form-input.component';
import { MultiselectCubejsDashboardComponent } from './components/multiselect-cubejs-dashboard/multiselect-cubejs-dashboard.component';
import { CubejsCardComponent } from './components/cubejs-card/cubejs-card.component';
import { AppointmentStateInputComponent } from './components/appointment-state-input/appointment-state-input.component';

@NgModule({
	imports: [
		CommonModule,
		NgChartsModule,
		ReactiveFormsModule,
		CubejsClientModule.forRoot(cubejsOptions),
		// deps
		PresentationModule,
	],
	declarations: [
		// routing
		InstitutionExtensionComponent,
		SystemExtensionComponent,
		// components
		CubejsChartComponent,
		CubejsDashboardComponent,
		DateOnlyIsoFormInputComponent,
		JsonComponent,
		PageLayoutComponent,
		QueryRendererComponent,
		TabsComponent,
		UiCardComponent,
		UiComponentComponent,
		UiComponentListComponent,
		UiExternalComponentComponent,
		RoutedExternalComponent,
		SpecialtyFormInputComponent,
		ProfessionalFormInputComponent,
  MultiselectCubejsDashboardComponent,
  CubejsCardComponent,
  AppointmentStateInputComponent,
	],
	exports: [
		// components
		PageLayoutComponent,
		UiExternalComponentComponent,
	]
})
export class ExtensionsModule { }
