import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { MenuItem } from '@core/core-model';
import { SIDEBAR_MENU } from './constants/menu';
import { PermissionsService } from '../core/services/permissions.service';
import { MenuFooter } from '@presentation/components/main-layout/main-layout.component';
import { AccountService } from '@api-rest/services/account.service';
import { mapToFullName } from '@api-rest/mapper/user-person-dto.mapper';
import { UserDto } from '@api-rest/api-model';
import { ContextService } from './../core/services/context.service';

@Component({
	selector: 'app-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
	menuItems$: Observable<MenuItem[]>;
	menuFooterItems: MenuFooter = {user: {}};

	private NO_INSTITUTION: number = -1;

	constructor(
		private contextService: ContextService,
		private permissionsService: PermissionsService,
		private accountService: AccountService
	) { }

	ngOnInit(): void {
		this.contextService.setInstitutionId(this.NO_INSTITUTION);

		this.menuItems$ = this.permissionsService.filterItems$(SIDEBAR_MENU);

		this.accountService.getInfo()
				.subscribe( userInfo => {
					this.menuFooterItems.user = {
						userName: userInfo.email,
						fullName: mapToFullName(userInfo.personDto)
					};
				}
			);
	}
}
