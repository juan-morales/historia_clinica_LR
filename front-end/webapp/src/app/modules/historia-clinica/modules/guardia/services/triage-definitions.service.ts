import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ContextService } from '@core/services/context.service';
import { PermissionsService } from '@core/services/permissions.service';
import { map, take } from 'rxjs/operators';
import { EmergencyCareTypes } from '../constants/masterdata';
import { ERole } from '@api-rest/api-model';
import { forkJoin, Observable } from 'rxjs';
import { AdministrativeTriageDialogComponent } from '../dialogs/administrative-triage-dialog/administrative-triage-dialog.component';

export const ROLES_TO_MEDIC_TRIAGE: ERole[] = [ERole.ESPECIALISTA_MEDICO, ERole.PROFESIONAL_DE_SALUD, ERole.ENFERMERO];

@Injectable({
	providedIn: 'root'
})
export class TriageDefinitionsService {

	private readonly routePrefix;

	constructor(private readonly router: Router,
	            private readonly contextService: ContextService,
	            private readonly permissionsService: PermissionsService) {
		this.routePrefix = `institucion/${this.contextService.institutionId}`;
	}

	getTriagePath(emergencyCareTypeId: number): Observable<{ url: string, component: any }> {
		return this.permissionsService.hasContextAssignments$(ROLES_TO_MEDIC_TRIAGE)
				.pipe(
					take(1),
					map((hasMedicRole: boolean) => this.triagePathDecision(hasMedicRole, emergencyCareTypeId))
				);
	}

	private triagePathDecision(hasMedicRole: boolean, emergencyCareTypeId: number): { url: string, component: any } {
		if (hasMedicRole) {
			switch (emergencyCareTypeId) {
				case EmergencyCareTypes.PEDIATRIA:
					return { url: this.getPediatricTriageUrl(),
							component: undefined }; // TODO PediatricTriageDialogComponent
					break;
				case EmergencyCareTypes.ADULTO:
				case EmergencyCareTypes.GINECOLOGIA:
					return { url: this.getAdultTriageUrl(),
							component: undefined }; // TODO AdultTriageDialogComponent
					break;
			}
		}
		return { url: this.getAdministrativeTriageUrl(),
				component: AdministrativeTriageDialogComponent };
	}


	private getAdministrativeTriageUrl(): string {
		return `${this.routePrefix}/guardia/nuevo-episodio/triage-administrativo`;
	}

	private getAdultTriageUrl(): string {
		return `${this.routePrefix}/guardia/nuevo-episodio/triage-medico`;
	}

	private getPediatricTriageUrl(): string {
		return `${this.routePrefix}/guardia/nuevo-episodio/triage-pediatrico`;
	}

}
