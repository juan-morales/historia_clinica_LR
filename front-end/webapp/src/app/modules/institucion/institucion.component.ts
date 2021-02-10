import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { ContextService } from '@core/services/context.service';
import { MenuItem } from '@core/core-model';

import { SIDEBAR_MENU } from './constants/menu';
import { PermissionsService } from '../core/services/permissions.service';
import { MenuFooter } from '@presentation/components/main-layout/main-layout.component';
import { InstitutionService } from '@api-rest/services/institution.service';
import { InstitutionDto } from '@api-rest/api-model';
import { AccountService } from '@api-rest/services/account.service';
import { FeatureFlagService } from '@core/services/feature-flag.service';
import { mapToFullName } from '@api-rest/mapper/user-person-dto.mapper';

@Component({
	selector: 'app-institucion',
	templateUrl: './institucion.component.html',
	styleUrls: ['./institucion.component.scss']
})
export class InstitucionComponent implements OnInit {
	menuItems$: Observable<MenuItem[]>;
	menuFooterItems: MenuFooter = {user: {}, institution: null};

	constructor(
		private activatedRoute: ActivatedRoute,
		private contextService: ContextService,
		private permissionsService: PermissionsService,
		private institutionService: InstitutionService,
		private accountService: AccountService,
		private featureFlagService: FeatureFlagService
	) {

	}

	ngOnInit(): void {
		this.activatedRoute.paramMap.subscribe(params => {
			const institutionId = Number(params.get('id'));
			this.contextService.setInstitutionId(institutionId);

			this.menuItems$ = this.featureFlagService.filterItems$(SIDEBAR_MENU);
			this.menuItems$ = this.menuItems$.pipe(switchMap(menu => this.permissionsService.filterItems$(menu)));

			this.institutionService.getInstitutions(Array.of(institutionId))
				.subscribe(institutionDto => {
					this.menuFooterItems.institution = {name: institutionDto[0].name, address: this.mapToAddress(institutionDto[0])};
				});
			this.accountService.getInfo()
				.subscribe( userInfo => {
					this.menuFooterItems.user.userName = userInfo.email;
					this.menuFooterItems.user.fullName = mapToFullName(userInfo.personDto);
				}
			);
		});
	}

	private mapToAddress(institutionDto: InstitutionDto) {
		return {
			street: institutionDto.institutionAddressDto.street,
			number: institutionDto.institutionAddressDto.number,
			floor: institutionDto.institutionAddressDto.floor,
			apartment: institutionDto.institutionAddressDto.apartment,
			cityName:  institutionDto.institutionAddressDto.city.description
		};
	}

}
