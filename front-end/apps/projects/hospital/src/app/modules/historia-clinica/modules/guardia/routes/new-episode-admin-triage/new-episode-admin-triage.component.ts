import { Component } from '@angular/core';
import { ECAdministrativeDto, ResponseEmergencyCareDto, TriageAdministrativeDto } from '@api-rest/api-model';
import { NewEpisodeService } from '../../services/new-episode.service';
import { EmergencyCareEpisodeService } from '@api-rest/services/emergency-care-episode.service';
import { Router } from '@angular/router';
import { SnackBarService } from '@presentation/services/snack-bar.service';
import { ContextService } from '@core/services/context.service';
import { ROUTE_EMERGENCY_CARE } from '../../services/triage-definitions.service';
import { GuardiaRouterService } from '../../services/guardia-router.service';

@Component({
	selector: 'app-new-episode-admin-triage',
	templateUrl: './new-episode-admin-triage.component.html',
	styleUrls: ['./new-episode-admin-triage.component.scss']
})
export class NewEpisodeAdminTriageComponent {

	disable = false;
	private triage: TriageAdministrativeDto;
	private emergencyCareDto = {} as ECAdministrativeDto;
	private readonly routePrefix;

	constructor(
		private readonly newEpisodeService: NewEpisodeService,
		private readonly emergencyCareEpisodeService: EmergencyCareEpisodeService,
		private router: Router,
		private snackBarService: SnackBarService,
		private contextService: ContextService,
		private readonly guardiaRouterService: GuardiaRouterService,
	) {
		this.routePrefix = 'institucion/' + this.contextService.institutionId;
	}

	confirmEvent(triage: TriageAdministrativeDto): void {
		this.disable = true;
		this.triage = triage;
		this.emergencyCareDto.triage = this.triage;
		this.emergencyCareDto.administrative = this.newEpisodeService.getAdministrativeAdmissionDto();
		this.emergencyCareEpisodeService.createAdministrative(this.emergencyCareDto).subscribe(
			emergencyCareId => {
				this.disable = false;
				this.emergencyCareEpisodeService.getAdministrative(emergencyCareId).subscribe((dto: ResponseEmergencyCareDto) => {
					this.guardiaRouterService.goToEpisode(dto.emergencyCareState.id, dto.patient)
					this.snackBarService.showSuccess('guardia.new-episode.SUCCESS');
				});
			},
			error =>
				error?.text ?
					this.snackBarService.showError(error.text) : this.snackBarService.showError('guardia.new-episode.ERROR')
		);
	}

	cancelEvent(): void {
		this.router.navigate([this.routePrefix + ROUTE_EMERGENCY_CARE + '/nuevo-episodio/administrativa'], { state: { commingBack: true } });
	}

}
