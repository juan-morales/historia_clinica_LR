import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ContextService } from '@core/services/context.service';
import { InstitutionDto } from '@api-rest/api-model';
import { InstitutionService } from '@api-rest/services/institution.service';
import { AppRoutes } from '../../../../app-routing.module';
import { mapToAddress } from '@api-presentation/mappers/institution-dto.mapper';
import { PermissionsService } from '@core/services/permissions.service';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
	institucion$: Observable<InstitutionDto>;
	roles = [];

	constructor(
		private contextService: ContextService,
		private institutionService: InstitutionService,
		private router: Router,
		private permissionsService: PermissionsService,

	) { }

	ngOnInit(): void {
		this.contextService.institutionId$.subscribe(id => {
			this.institucion$ = this.institutionService.getInstitutions([id]).pipe(
				map(list => list && list.length ? list[0] : undefined),
			);

		});
		this.permissionsService.contextRoleAssignments$.subscribe(
			roles => this.roles = roles.map(role => role.roleDescription)
		);

	}

	address(institution: InstitutionDto) {
		return mapToAddress(institution?.institutionAddressDto);
	}

	switchInstitution() {
		this.router.navigate([AppRoutes.Home]);
	}
}
